package aes

import chisel3._
import chisel3.util._

class AES extends Module{
    val io = IO(new Bundle{
        val stateIn = Input(Vec(4, Vec(4, UInt(8.W))))
        val stateOut = Output(Vec(4, Vec(4, UInt(8.W))))
        val key = Input(Vec(4, Vec(4, UInt(8.W))))
    })

    val keyExpansion = Module(new KeyExpansion)

    val addRound = Module(new AddRound)
    val subBytes = Module(new SubBytes)
    val shiftRows = Module(new ShiftRow)
    val mixColumns = Module(new MixColumns)

    val roundKeys = Wire(Vec(44*4, UInt(8.W)))
    val state = Reg(Vec(4, Vec(4, UInt(8.W))))

    keyExpansion.io.keyIn := io.key
    roundKeys := keyExpansion.io.keyOut

    addRound.io.stateIn := io.stateIn
    for (i <- 0 until 4){
        for (j <- 0 until 4){
            addRound.io.roundKey(i)(j) := roundKeys(i*4+j)
        }
    }
    state := addRound.io.stateOut

    for (i <- 1 until 10){
        subBytes.io.stateIn := state
        shiftRows.io.stateIn := subBytes.io.stateOut
        mixColumns.io.stateIn := shiftRows.io.stateOut
        addRound.io.stateIn := mixColumns.io.stateOut
        for (i <- 0 until 4){
            for (j <- 0 until 4){
                addRound.io.roundKey(i)(j) := roundKeys(i*4+j)
            }
        }
        state := addRound.io.stateOut
    }

    subBytes.io.stateIn := state
    shiftRows.io.stateIn := subBytes.io.stateOut
    addRound.io.stateIn := shiftRows.io.stateOut
    for (i <- 0 until 4){
        for (j <- 0 until 4){
            addRound.io.roundKey(i)(j) := roundKeys(i*4+j)
        }
    }
    state := addRound.io.stateOut

    io.stateOut := state

}