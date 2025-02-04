package aes

import chisel3._
import chisel3.util._
import aes.SBox 
import aes.RCon

class KeyExpansion extends Module{
    val io = IO(new Bundle{
        val keyIn = Input(Vec(4, Vec(4, UInt(8.W))))
        val keyOut = Output(Vec(44*4, UInt(8.W)))
    })

    val roundKeys = Wire(Vec(44*4, UInt(8.W)))

    val key = Wire

    def subWord(word: Vec[UInt]): Vec[UInt] = {
        val result = Wire(Vec(4, UInt(8.W)))
        for (i <- 0 until 4){
            result(i) := SBox(word(i))
        }
        result
    }

    def rotWord(word: Vec[UInt]): Vec[UInt] = {
        val result = Wire(Vec(4, UInt(8.W)))
        result(0) := word(1)
        result(1) := word(2)
        result(2) := word(3)
        result(3) := word(0)
        result
    }

    for (i <- 0 until 4){
        for (j <- 0 until 4){
            roundKeys(i*4+j) := io.keyIn(j)(i)
        }
    }

    for (i <- 4 until 44){
        if (i % 4 == 0){
            val temp = Wire(Vec(4, UInt(8.W)))
            for (j <- 0 until 4){
                temp(j) := roundKeys((i-1)*4+j)
            }
            val sub = subWord(rotWord(temp))
            for (j <- 0 until 4){
                if (j == 0){
                    roundKeys(i*4+j) := roundKeys((i-4)*4+j) ^ sub(j) ^ RCon(i/4-1)
                } else {
                    roundKeys(i*4+j) := roundKeys((i-4)*4+j) ^ sub(j)
                }
            }
        } else {
            for (j <- 0 until 4){
                roundKeys(i*4+j) := roundKeys((i-4)*4+j) ^ roundKeys((i-1)*4+j)
            }
        }
    }

     io.keyOut <> roundKeys

}