package aes

import chisel3._
import chisel3.util._

class MixColumns extends Module{
    val io = IO(new Bundle{
        val stateIn = Input(Vec(4, Vec(4, UInt(8.W))))
        val stateOut = Output(Vec(4, Vec(4, UInt(8.W))))
    })

    def gfMultiplyBy2(byte: UInt): UInt = {
        val shifted = byte << 1
        val reduced = Mux(byte(7), shifted ^ 0x1B.U, shifted)
        reduced(7, 0)
    }

    def gfMultiplyBy3(byte: UInt): UInt = {
        gfMultiplyBy2(byte) ^ byte
    }

    def getMultiply(check:Int, byte:UInt): UInt = {
        check match {
            case 2 => gfMultiplyBy2(byte)
            case 3 => gfMultiplyBy3(byte)
            case _ => byte
        }
    }

    def mixColumn(col: Vec[UInt]): Vec[UInt] = {
        val result = Wire(Vec(4, UInt(8.W)))
        val m = List(List(2, 3, 1, 1), List(1, 2, 3, 1), List(1, 1, 2, 3), List(3, 1, 1, 2))
        for (i <- 0 until 4) {
            result(i) := getMultiply(m(i)(0), col(0)) ^ getMultiply(m(i)(0), col(1)) ^ getMultiply(m(i)(0), col(2)) ^ getMultiply(m(i)(0), col(3))
        }
        result
    }

    // def mixColumn(col: Vec[UInt]): Vec[UInt] = {
    //     val result = Wire(Vec(4, UInt(8.W)))
    //     result(0) := gfMultiplyBy2(col(0)) ^ gfMultiplyBy3(col(1)) ^ col(2) ^ col(3)
    //     result(1) := col(0) ^ gfMultiplyBy2(col(1)) ^ gfMultiplyBy3(col(2)) ^ col(3)
    //     result(2) := col(0) ^ col(1) ^ gfMultiplyBy2(col(2)) ^ gfMultiplyBy3(col(3))
    //     result(3) := gfMultiplyBy3(col(0)) ^ col(1) ^ col(2) ^ gfMultiplyBy2(col(3))
    //     result
    // }

    for (col <- 0 until 4){
        val inputCol = Wire(Vec(4, UInt(8.W)))
        for (row <- 0 until 4){
            inputCol(row) := io.stateIn(row)(col)
        }

        val outputCol = mixColumn(inputCol)
        for (row <- 0 until 4){
            io.stateOut(row)(col) := outputCol(row)
        }
    }
}