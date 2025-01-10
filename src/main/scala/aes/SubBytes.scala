package aes

import chisel3._
import chisel3.util._

class SubBytes extends Module{
    val io = IO(new Bundle{
        val stateIn = Input(Vec(4, Vec(4, UInt(8.W))))
        val stateOut = Output(Vec(4, Vec(4, UInt(8.W))))
    })

    val substitutedBytes = Wire(Vec(4, Vec(4, UInt(8.W))))
    val sBox = Module(new SBox)

    for (i <- 0 until 4) {
        for (j <- 0 until 4) {
            sBox.io.stateIn(i)(j) := io.stateIn(i)(j)
            substitutedBytes(i)(j) := sBox.io.stateOut(i)(j) // sBox(io.stateIn(i)(j)) // sBox.io.byteOut(i)(j)
        }
    }

    io.stateOut := substitutedBytes
}