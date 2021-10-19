import chisel3._

class AxiUartLite extends Module {
  val io = IO(new Bundle {
    val axi = Flipped(new Axi4LiteIO)
    val rx = Input(UInt(1.W))
    val tx = Output(UInt(1.W))
    val interrupt = Output(Bool())
  })

  val uartlite_inst = Module(new axi_uartlite_0)
  uartlite_inst.io.s_axi_aclk := clock;
  uartlite_inst.io.s_axi_aresetn := ~reset.asBool();

  uartlite_inst.io.s_axi_awaddr := io.axi.aw.bits.addr;
  uartlite_inst.io.s_axi_awvalid := io.axi.aw.valid;
  io.axi.aw.ready := uartlite_inst.io.s_axi_awready;

  uartlite_inst.io.s_axi_wdata := io.axi.w.bits.data;
  uartlite_inst.io.s_axi_wstrb := io.axi.w.bits.strb;
  uartlite_inst.io.s_axi_wvalid := io.axi.w.valid;
  io.axi.w.ready := uartlite_inst.io.s_axi_wready;

  io.axi.b.bits.resp := uartlite_inst.io.s_axi_bresp;
  io.axi.b.valid := uartlite_inst.io.s_axi_bvalid;
  uartlite_inst.io.s_axi_bready := io.axi.b.ready;
  
  uartlite_inst.io.s_axi_araddr := io.axi.ar.bits.addr;
  uartlite_inst.io.s_axi_arvalid := io.axi.ar.valid;
  io.axi.ar.ready := uartlite_inst.io.s_axi_arready;

  io.axi.r.bits.data := uartlite_inst.io.s_axi_rdata;
  io.axi.r.bits.resp := uartlite_inst.io.s_axi_rresp;
  io.axi.r.valid := uartlite_inst.io.s_axi_rvalid;
  uartlite_inst.io.s_axi_rready := io.axi.r.ready;

  uartlite_inst.io.rx := io.rx;
  io.tx := uartlite_inst.io.tx;
  io.interrupt := uartlite_inst.io.interrupt;

}

class axi_uartlite_0 extends BlackBox {
  val io = IO(new Bundle with AxiParameters {
    val s_axi_aclk = Input(Clock())
    val s_axi_aresetn = Input(Reset())
    val s_axi_awaddr = Input(UInt(AxiAddrWidth.W))
    val s_axi_awvalid = Input(Bool())
    val s_axi_awready = Output(Bool())
    val s_axi_wdata = Input(UInt(AxiDataWidth.W))
    val s_axi_wstrb = Input(UInt((AxiDataWidth/8).W))
    val s_axi_wvalid = Input(Bool())
    val s_axi_wready = Output(Bool())
    val s_axi_bresp = Output(UInt(2.W))
    val s_axi_bvalid = Output(Bool())
    val s_axi_bready = Input(Bool())
    val s_axi_araddr = Input(UInt(AxiAddrWidth.W))
    val s_axi_arvalid = Input(Bool())
    val s_axi_arready = Output(Bool())
    val s_axi_rdata = Output(UInt(AxiDataWidth.W))
    val s_axi_rresp = Output(UInt(2.W))
    val s_axi_rvalid = Output(Bool())
    val s_axi_rready = Input(Bool())
    val rx = Input(UInt(1.W))
    val tx = Output(UInt(1.W))
    val interrupt = Output(Bool())
  })
}