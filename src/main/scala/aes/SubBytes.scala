package aes

import chisel3._
import chisel3.util._

import aes.SBox

class SubBytes extends Module{
    val io = IO(new Bundle{
        val stateIn = Input(Vec(4, Vec(4, UInt(8.W))))
        val stateOut = Output(Vec(4, Vec(4, UInt(8.W))))
    })

    val substitutedBytes = Wire(Vec(4, Vec(4, UInt(8.W))))

    for (i <- 0 until 4) {
        for (j <- 0 until 4) {
            substitutedBytes(i)(j) := SBox(io.stateIn(i)(j))
        }
    }

    io.stateOut := substitutedBytes
}