import chisel3._
import chisel3.util._

trait AxiParameters {
  val AxiAddrWidth = 4
  val AxiDataWidth = 32
}

object AxiParameters extends AxiParameters { }

class Axi4LiteA extends Bundle with AxiParameters {
  val addr = Output(UInt(AxiAddrWidth.W))
}

class Axi4LiteW extends Bundle with AxiParameters {
  val data = Output(UInt(AxiDataWidth.W))
  val strb = Output(UInt((AxiDataWidth/8).W)) // 写掩码
}

// 读写传输事务（Transaction）都存在 2bit 位宽的回复信号 RRESP/BRESP，分别存在 4 种回复情况，分别为
// OKAY ，常规访问成功
// EXOKAY，独占访问成功
// SLVERR，从机错误，尽管从机接收到了访问请求，但因为种种原因向主机返回了一个错误状态，AXI 传输事务的回复由应用具体决定，可能包括以下错误场景：
//   -- FIFO 或者缓冲区溢出
//   -- 主机发起了不支持的传输位宽
//   -- 尝试向读保护的地址写入数据
//   -- 超时
// DECERR，解码错误，一般由 interconnect 组件产生，表示主机发送的传输事务地址无效，无法将传输事务发送给某个从机。

class Axi4LiteB extends Bundle with AxiParameters {
  val resp = Output(UInt(2.W))
}

class Axi4LiteR extends Bundle with AxiParameters {
  val resp = Output(UInt(2.W))
  val data = Output(UInt(AxiDataWidth.W))
}

// Decoupled: 为接口包装一层valid 和 ready
// Flipped: 翻转端口列表的方向
class Axi4LiteIO extends Bundle {
  val aw = Decoupled(new Axi4LiteA)         // AXI4-Lite Write Address Channel Signals
  val w = Decoupled(new Axi4LiteW)          // AXI4-Lite Write Data Channel Signals
  val b = Flipped(Decoupled(new Axi4LiteB)) // AXI4-Lite Write Response Channel Signals
  val ar = Decoupled(new Axi4LiteA)         // AXI4-Lite Read Address Channel Signals
  val r = Flipped(Decoupled(new Axi4LiteR)) // AXI4-Lite Read Data Channel Signals
}