package aes

import chisel3._
import chisel3.util._

class ShiftRow extends Module{
    val io = IO(new Bundle {
        val stateIn = Input(Vec(4, Vec(4, UInt(8.W)))) // Example 4x4 byte matrix input
        val stateOut = Output(Vec(4, Vec(4, UInt(8.W)))) // Example 4x4 byte matrix output
    })

      // Row 0: No shift
    io.stateOut(0) := io.stateIn(0)

    // Row 1: 1-byte left shift
    io.stateOut(1)(0) := io.stateIn(1)(1)
    io.stateOut(1)(1) := io.stateIn(1)(2)
    io.stateOut(1)(2) := io.stateIn(1)(3)
    io.stateOut(1)(3) := io.stateIn(1)(0)

    // Row 2: 2-byte left shift
    io.stateOut(2)(0) := io.stateIn(2)(2)
    io.stateOut(2)(1) := io.stateIn(2)(3)
    io.stateOut(2)(2) := io.stateIn(2)(0)
    io.stateOut(2)(3) := io.stateIn(2)(1)

    // Row 3: 3-byte left shift (equivalent to 1-byte right shift)
    io.stateOut(3)(0) := io.stateIn(3)(3)
    io.stateOut(3)(1) := io.stateIn(3)(0)
    io.stateOut(3)(2) := io.stateIn(3)(1)
    io.stateOut(3)(3) := io.stateIn(3)(2)
}
