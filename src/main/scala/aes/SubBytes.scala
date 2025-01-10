package aes

import chisel3._
import chisel3.util._

class SubBytes extends Module{
    val io = IO(new Bundle{
        val stateIn = Input(Vec(4, Vec(4, UInt(8.W))))
        val stateOut = Output(Vec(4, Vec(4, UInt(8.W))))
    })

    val sBox = VecInit(Seq(
    "h63".U(8.W), "h7C".U(8.W), "h77".U(8.W), "h7B".U(8.W), "hF2".U(8.W), "h6B".U(8.W), "h6F".U(8.W), "hC5".U(8.W), "h30".U(8.W), "h01".U(8.W), "h67".U(8.W), "h2B".U(8.W), "hFE".U(8.W), "hD7".U(8.W), "hAB".U(8.W), "h76".U(8.W),
    "hCA".U(8.W), "h82".U(8.W), "hC9".U(8.W), "h7D".U(8.W), "hFA".U(8.W), "h59".U(8.W), "h47".U(8.W), "hF0".U(8.W), "hAD".U(8.W), "hD4".U(8.W), "hA2".U(8.W), "hAF".U(8.W), "h9C".U(8.W), "hA4".U(8.W), "h72".U(8.W), "hC0".U(8.W),
    "hB7".U(8.W), "hFD".U(8.W), "h93".U(8.W), "h26".U(8.W), "h36".U(8.W), "h3F".U(8.W), "hF7".U(8.W), "hCC".U(8.W), "h34".U(8.W), "hA5".U(8.W), "hE5".U(8.W), "hF1".U(8.W), "h71".U(8.W), "hD8".U(8.W), "h31".U(8.W), "h15".U(8.W),
    "h04".U(8.W), "hC7".U(8.W), "h23".U(8.W), "hC3".U(8.W), "h18".U(8.W), "h96".U(8.W), "h05".U(8.W), "h9A".U(8.W), "h07".U(8.W), "h12".U(8.W), "h80".U(8.W), "hE2".U(8.W), "hEB".U(8.W), "h27".U(8.W), "hB2".U(8.W), "h75".U(8.W),
    "h09".U(8.W), "h83".U(8.W), "h2C".U(8.W), "h1A".U(8.W), "h1B".U(8.W), "h6E".U(8.W), "h5A".U(8.W), "hA0".U(8.W), "h52".U(8.W), "h3B".U(8.W), "hD6".U(8.W), "hB3".U(8.W), "h29".U(8.W), "hE3".U(8.W), "h2F".U(8.W), "h84".U(8.W),
    "h53".U(8.W), "hD1".U(8.W), "h00".U(8.W), "hED".U(8.W), "h20".U(8.W), "hFC".U(8.W), "hB1".U(8.W), "h5B".U(8.W), "h6A".U(8.W), "hCB".U(8.W), "hBE".U(8.W), "h39".U(8.W), "h4A".U(8.W), "h4C".U(8.W), "h58".U(8.W), "hCF".U(8.W),
    "hD0".U(8.W), "hEF".U(8.W), "hAA".U(8.W), "hFB".U(8.W), "h43".U(8.W), "h4D".U(8.W), "h33".U(8.W), "h85".U(8.W), "h45".U(8.W), "hF9".U(8.W), "h02".U(8.W), "h7F".U(8.W), "h50".U(8.W), "h3C".U(8.W), "h9F".U(8.W), "hA8".U(8.W),
    "h51".U(8.W), "hA3".U(8.W), "h40".U(8.W), "h8F".U(8.W), "h92".U(8.W), "h9D".U(8.W), "h38".U(8.W), "hF5".U(8.W), "hBC".U(8.W), "hB6".U(8.W), "hDA".U(8.W), "h21".U(8.W), "h10".U(8.W), "hFF".U(8.W), "hF3".U(8.W), "hD2".U(8.W),
    "hCD".U(8.W), "h0C".U(8.W), "h13".U(8.W), "hEC".U(8.W), "h5F".U(8.W), "h97".U(8.W), "h44".U(8.W), "h17".U(8.W), "hC4".U(8.W), "hA7".U(8.W), "h7E".U(8.W), "h3D".U(8.W), "h64".U(8.W), "h5D".U(8.W), "h19".U(8.W), "h73".U(8.W),
    "h60".U(8.W), "h81".U(8.W), "h4F".U(8.W), "hDC".U(8.W), "h22".U(8.W), "h2A".U(8.W), "h90".U(8.W), "h88".U(8.W), "h46".U(8.W), "hEE".U(8.W), "hB8".U(8.W), "h14".U(8.W), "hDE".U(8.W), "h5E".U(8.W), "h0B".U(8.W), "hDB".U(8.W),
    "hE0".U(8.W), "h32".U(8.W), "h3A".U(8.W), "h0A".U(8.W), "h49".U(8.W), "h06".U(8.W), "h24".U(8.W), "h5C".U(8.W), "hC2".U(8.W), "hD3".U(8.W), "hAC".U(8.W), "h62".U(8.W), "h91".U(8.W), "h95".U(8.W), "hE4".U(8.W), "h79".U(8.W),
    "hE7".U(8.W), "hC8".U(8.W), "h37".U(8.W), "h6D".U(8.W), "h8D".U(8.W), "hD5".U(8.W), "h4E".U(8.W), "hA9".U(8.W), "h6C".U(8.W), "h56".U(8.W), "hF4".U(8.W), "hEA".U(8.W), "h65".U(8.W), "h7A".U(8.W), "hAE".U(8.W), "h08".U(8.W),
    "hBA".U(8.W), "h78".U(8.W), "h25".U(8.W), "h2E".U(8.W), "h1C".U(8.W), "hA6".U(8.W), "hB4".U(8.W), "hC6".U(8.W), "hE8".U(8.W), "hDD".U(8.W), "h74".U(8.W), "h1F".U(8.W), "h4B".U(8.W), "hBD".U(8.W), "h8B".U(8.W), "h8A".U(8.W),
    "h70".U(8.W), "h3E".U(8.W), "hB5".U(8.W), "h66".U(8.W), "h48".U(8.W), "h03".U(8.W), "hF6".U(8.W), "h0E".U(8.W), "h61".U(8.W), "h35".U(8.W), "h57".U(8.W), "hB9".U(8.W), "h86".U(8.W), "hC1".U(8.W), "h1D".U(8.W), "h9E".U(8.W),
    "hE1".U(8.W), "hF8".U(8.W), "h98".U(8.W), "h11".U(8.W), "h69".U(8.W), "hD9".U(8.W), "h8E".U(8.W), "h94".U(8.W), "h9B".U(8.W), "h1E".U(8.W), "h87".U(8.W), "hE9".U(8.W), "hCE".U(8.W), "h55".U(8.W), "h28".U(8.W), "hDF".U(8.W),
    "h8C".U(8.W), "hA1".U(8.W), "h89".U(8.W), "h0D".U(8.W), "hBF".U(8.W), "hE6".U(8.W), "h42".U(8.W), "h68".U(8.W), "h41".U(8.W), "h99".U(8.W), "h2D".U(8.W), "h0F".U(8.W), "hB0".U(8.W), "h54".U(8.W), "hBB".U(8.W), "h16".U(8.W)
    ))


    val substitutedBytes = Wire(Vec(4, Vec(4, UInt(8.W))))
    // val sBox = Module(new SBox)

    for (i <- 0 until 4) {
        for (j <- 0 until 4) {
            // sBox.io.byteIn(i)(j) := io.stateIn(i)(j)
            substitutedBytes(i)(j) := sBox(io.stateIn(i)(j)) // sBox.io.byteOut(i)(j)
        }
    }

    io.stateOut := substitutedBytes
}