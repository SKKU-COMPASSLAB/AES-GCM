package aes

import chisel3._
import chisel3.util._

class AddRound extends Module{
    val io = IO(new Bundle{
        val stateIn = Input(Vec(4, Vec(4, UInt(8.W))))
        val roundKey = Input(Vec(4, Vec(4, UInt(8.W))))
        val stateOut = Output(Vec(4, Vec(4, UInt(8.W))))
    })

    for (i <- 0 until 4){
        for (j <- 0 until 4){
            io.stateOut(i)(j) := io.stateIn(i)(j) ^ io.roundKey(i)(j)
        }
    }
}