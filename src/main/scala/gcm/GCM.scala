package gcm

import chisel3._
import chisel3.util._
import aes._

/*
        |   Mode = 0: GHASH - {AAD and Length(AD, PT)}
        |       dataIn1 = GHASH.Prev
        |       dataIn2 = Cipher
        |       dataIn3 = HashSubKey
        |       dataIn4 = DontCare
        |   
        |   Mode = 1: GHASH - {PT}
        |       dataIn1 = IV + CTR
        |       dataIn2 = PT
        |       dataIn3 = GHASH.Prev
        |       dataIn4 = HashSubKey
        |   
        |   Mode = 2: Hash SubKey and Tag
        |       dataIn1 = IV + CTR
        |       dataIn2 = GHASH.Prev (DontCare in case of Hash SubKey)
        |       dataIn3 = DontCare
        |       dataIn4 = DontCare
*/

class GCM_IO extends Bundle{
    // AES IOs
    val key = Input(Vec(4, Vec(4, UInt(8.W))))

    val start = Input(Bool())
    val mode = Input(UInt(2.W))

    val dataIn1 = Input(Vec(4, Vec(4, UInt(8.W))))
    val dataIn2 = Input(Vec(4, Vec(4, UInt(8.W))))
    val dataIn3 = Input(Vec(4, Vec(4, UInt(8.W))))
    val dataIn4 = Input(Vec(4, Vec(4, UInt(8.W))))

    val dataOut = (Vec(4, Vec(4, UInt(8.W))))
    val valid = Output(Bool())
}

class GCM extends Module{
    val io = IO(new GCM_IO)

    val ghash = Module(new GHASH)
    val aes = Module(new AES)

    aes.io.key := io.key

    aes.io.stateIn := DontCare
    aes.io.start := DontCare
    ghash.io.xIn := DontCare
    ghash.io.cypherIn := DontCare
    ghash.io.hashKey := DontCare
    ghash.io.start := DontCare

    io.dataOut := DontCare
    io.valid := false.B

    val sIDLE :: sAAD :: sDATA :: sHASHKEY_sTAG :: Nil = Enum(4)

    val state = RegInit(sIDLE)

    def flatten(data: Vec[Vec[UInt]]): UInt = {
        val flattenData = Wire(Vec(16, UInt(8.W)))
        for (i <- 0 until 4){
            for (j <- 0 until 4){
                flattenData(i*4+j) := data(j)(i)
            }
        }
        flattenData reduce {Cat(_, _)}
    }

    val CT = Wire(Vec(4, Vec(4, UInt(8.W))))
    CT := DontCare

    switch(state){
        is(sIDLE){
            aes.io.start := false.B
            ghash.io.start := false.B
            io.valid := false.B
            io.dataOut := DontCare
            CT := DontCare

            when (io.start){state := MuxCase(sIDLE, Seq(
                            (io.mode === 0.U) -> sAAD,
                            (io.mode === 1.U) -> sDATA,
                            (io.mode === 2.U) -> sHASHKEY_sTAG))}
        }
        is(sAAD){
            ghash.io.xIn := flatten(io.dataIn1)
            ghash.io.cypherIn := flatten(io.dataIn2)
            ghash.io.hashKey := flatten(io.dataIn3)
            ghash.io.start := true.B
            io.valid := ghash.io.valid
            when(ghash.io.valid){
                for (i <- 0 until 4){
                    for (j <- 0 until 4){
                        io.dataOut(j)(i) := ghash.io.ghash((15-(i * 4 + j))*8+7, (15-(i*4+j))*8)
                    }
                }
                state := sIDLE
            }

        }
        is(sDATA){
            aes.io.stateIn := io.dataIn1
            aes.io.start := Mux(ghash.io.start, false.B, true.B)
            when(aes.io.valid){
                for (i <- 0 until 4){
                    for (j <- 0 until 4){
                        CT(i)(j) := aes.io.stateOut(i)(j) ^ io.dataIn2(i)(j)
                    }
                }
                ghash.io.xIn := flatten(io.dataIn3)
                ghash.io.cypherIn := flatten(CT)
                ghash.io.hashKey := flatten(io.dataIn4)
                ghash.io.start := true.B
                io.valid := ghash.io.valid
                when(ghash.io.valid){
                    for (i <- 0 until 4){
                        for (j <- 0 until 4){
                            io.dataOut(j)(i) := ghash.io.ghash(i*4+j)
                        }
                    }
                    state := sIDLE
                }
            }
        }
        is(sHASHKEY_sTAG){
            aes.io.stateIn := io.dataIn1
            aes.io.start := true.B
            io.valid := aes.io.valid
            when(aes.io.valid){
                for (i <- 0 until 4){
                    for (j <- 0 until 4){
                        io.dataOut(i)(j) := aes.io.stateOut(i)(j) ^ io.dataIn2(i)(j)
                    }
                }
                state := sIDLE
            }
        }
    }
}