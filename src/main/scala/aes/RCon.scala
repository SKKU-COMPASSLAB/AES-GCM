package aes

import chisel3._
import chisel3.util._

object RCon {
    def apply(idx: Int): UInt = {
      val sBox = Wire(Vec(10, UInt(8.W)))

      sBox(0) := "h01".U
      sBox(1) := "h02".U
      sBox(2) := "h04".U
      sBox(3) := "h08".U
      sBox(4) := "h10".U
      sBox(5) := "h20".U
      sBox(6) := "h40".U
      sBox(7) := "h80".U
      sBox(8) := "h1B".U
      sBox(9) := "h36".U

      sBox(idx)
    }
}

