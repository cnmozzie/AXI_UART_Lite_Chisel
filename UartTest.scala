import chisel3._
import chisel3.util._

/**
 * The UART Test component.
 */

class UartTest extends Module {
  val io = IO(new Bundle {
    val rx = Input(UInt(1.W))
    val tx = Output(UInt(1.W))
    val data_led = Output(UInt(8.W))
    val resp_led = Output(UInt(2.W))
  })
  
  val read_ctrl :: read :: write :: write_resp :: Nil = Enum(4)

  // The state register
  val stateReg = RegInit(read_ctrl)

  // The data register
  val data = RegInit(0.U(8.W))
  val resp = RegInit(0.U(2.W))

  io.data_led := data
  io.resp_led := resp

  val uart = Module(new AxiUartLite)
  uart.io.rx := io.rx
  io.tx := uart.io.tx

  uart.io.axi.aw.bits.addr := "h04".U        // Tx FIFO
  uart.io.axi.aw.valid := false.B

  uart.io.axi.ar.bits.addr := "h00".U        //  Rx FIFO
  uart.io.axi.ar.valid := false.B
  
  uart.io.axi.w.bits.data := data
  uart.io.axi.w.bits.strb := "b0001".U       // don't care
  uart.io.axi.w.valid := false.B         

  uart.io.axi.r.ready := false.B         
  
  uart.io.axi.b.ready := false.B         

  switch (stateReg) {
    is (read_ctrl) {
      uart.io.axi.ar.valid := true.B
      when (uart.io.axi.ar.valid && uart.io.axi.ar.ready) {
        stateReg := read
      }
    }

    is (read) {
      uart.io.axi.r.ready := true.B
      when (uart.io.axi.r.valid && uart.io.axi.r.ready) {
        resp := uart.io.axi.r.bits.resp
        when (uart.io.axi.r.bits.resp === 0.U) {
          data := uart.io.axi.r.bits.data(7, 0)    // 数据成功进行一次传输，存入data寄存器中
          stateReg := write
        } otherwise {
          stateReg := read_ctrl
        }
        
      }
    }

    is (write) {
      uart.io.axi.w.valid := true.B
      uart.io.axi.aw.valid := true.B
      when (uart.io.axi.w.valid & uart.io.axi.w.ready) {
        stateReg := write_resp    // 准备读取从机响应信号
      }
    }

    is (write_resp) {
      uart.io.axi.b.ready := true.B
      when (uart.io.axi.b.valid & uart.io.axi.b.ready) {
        resp := uart.io.axi.b.bits.resp
        stateReg := read_ctrl    // 传输完成
      }
    }
  }

}


/**
 * An object extending App to generate the Verilog code.
 */
object UartTest extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new UartTest())
}
