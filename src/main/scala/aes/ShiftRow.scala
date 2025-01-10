package aes

import chisel3._
import chisel3.util._

class ShiftRow extends Module{
    val io = IO(new Bundle {
        val stateIn = Input(Vec(4, Vec(4, UInt(8.W))))
        val stateOut = Output(Vec(4, Vec(4, UInt(8.W))))
    })

    io.stateOut(0) := io.stateIn(0)

    io.stateOut(1)(0) := io.stateIn(1)(1)
    io.stateOut(1)(1) := io.stateIn(1)(2)
    io.stateOut(1)(2) := io.stateIn(1)(3)
    io.stateOut(1)(3) := io.stateIn(1)(0)

    io.stateOut(2)(0) := io.stateIn(2)(2)
    io.stateOut(2)(1) := io.stateIn(2)(3)
    io.stateOut(2)(2) := io.stateIn(2)(0)
    io.stateOut(2)(3) := io.stateIn(2)(1)

    io.stateOut(3)(0) := io.stateIn(3)(3)
    io.stateOut(3)(1) := io.stateIn(3)(0)
    io.stateOut(3)(2) := io.stateIn(3)(1)
    io.stateOut(3)(3) := io.stateIn(3)(2)
}
