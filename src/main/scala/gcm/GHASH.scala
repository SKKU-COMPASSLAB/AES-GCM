package gcm

import chisel3._
import chisel3.util._

class GHASH extends Module{
    val io = IO(new Bundle{
        val xIn = Input(UInt(128.W))
        val cypherIn = Input(UInt(128.W))
        val hashKey = Input(UInt(128.W))
        val ghash = Output(UInt(128.W))
    })

    val gfmult = Module(new GFMult)

    val xor_res = io.xIn ^ io.cypherIn

    gfmult.io.a := xor_res
    gfmult.io.b := io.hashKey

    io.ghash := gfmult.io.out
}