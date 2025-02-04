package aes

import chisel3._
import chisel3.util._

class AES_IO extends Bundle{
    val stateIn = Input(Vec(4, Vec(4, UInt(8.W))))
    val stateOut = Output(Vec(4, Vec(4, UInt(8.W))))
    val key = Input(Vec(4, Vec(4, UInt(8.W))))
    val valid = Output(Bool())
    val start = Input(Bool())
}

class AES extends Module{
    val io = IO(new AES_IO)

    val keyExpansion = Module(new KeyExpansion)

    val addRound = Module(new AddRound)
    val subBytes = Module(new SubBytes)
    val shiftRows = Module(new ShiftRow)
    val mixColumns = Module(new MixColumns)

    val roundKeys = Wire(Vec(44*4, UInt(8.W)))
    val state = Reg(Vec(4, Vec(4, UInt(8.W))))

    subBytes.io.stateIn := DontCare
    shiftRows.io.stateIn := DontCare
    mixColumns.io.stateIn := DontCare

    keyExpansion.io.keyIn := io.key
    roundKeys := keyExpansion.io.keyOut

    val counter = RegInit(0.U(4.W))
    counter := Mux(counter === 11.U, 0.U, Mux(io.start, counter + 1.U, 0.U))

    io.valid := counter === 11.U

    when(counter === 0.U){
        addRound.io.stateIn := io.stateIn
        for (i <- 0 until 4){
            for (j <- 0 until 4){
                addRound.io.roundKey(j)(i) := roundKeys(i*4+j)
            }
        }
        state := addRound.io.stateOut
    }
    .elsewhen(counter >= 1.U && counter <= 9.U){
        subBytes.io.stateIn := state
        shiftRows.io.stateIn := subBytes.io.stateOut
        mixColumns.io.stateIn := shiftRows.io.stateOut
        addRound.io.stateIn := mixColumns.io.stateOut
        for (i <- 0 until 4){
            for (j <- 0 until 4){
                addRound.io.roundKey(j)(i) := roundKeys(counter*16.U+i.U*4.U+j.U)
            }
        }
        state := addRound.io.stateOut
    }
    .otherwise{
        subBytes.io.stateIn := state
        shiftRows.io.stateIn := subBytes.io.stateOut
        addRound.io.stateIn := shiftRows.io.stateOut
        for (i <- 0 until 4){
            for (j <- 0 until 4){
                addRound.io.roundKey(j)(i) := roundKeys(10*16+i*4+j)
            }
        }
        state := addRound.io.stateOut
    }

    io.stateOut := Mux(counter === 11.U, state, DontCare)

}