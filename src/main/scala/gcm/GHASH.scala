package gcm

import chisel3._
import chisel3.util._

class GHASH extends Module{
    val io = IO(new Bundle{
        val xIn = Input(UInt(128.W))
        val cypherIn = Input(UInt(128.W))
        val hashKey = Input(UInt(128.W))
        val start = Input(Bool())
        
        val ghash = Output(UInt(128.W))
        val valid = Output(Bool())
    })

    val gfmult = Module(new GFMult)

    val xor_res = io.xIn ^ io.cypherIn

    gfmult.io.a := xor_res
    gfmult.io.b := io.hashKey
    gfmult.io.start := io.start

    io.valid := gfmult.io.valid
    io.ghash := gfmult.io.out
}