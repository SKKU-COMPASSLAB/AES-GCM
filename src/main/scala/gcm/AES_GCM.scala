package gcm

import chisel3._
import chisel3.util._
import aes._

/*
        |   Mode = 0: IDLE
        |       DontCare
        |   
        |   Mode = 1: AES-128
        |       key     = Encryption key
        |       dataIn1 = IV + CTR
        |       dataIn2 = PT
        |   
        |   Mode = 2: GHASH - GMult
        |       key     = Hash Subkey
        |       dataIn1 = Previous GHASH
        |       dataIn2 = Cipher Text
*/

class AES_GCM_IO extends Bundle{
    val key = Input(Vec(4, Vec(4, UInt(8.W))))

    val start = Input(Bool())
    val mode = Input(UInt(2.W))

    val dataIn1 = Input(Vec(4, Vec(4, UInt(8.W))))
    val dataIn2 = Input(Vec(4, Vec(4, UInt(8.W))))

    val dataOut = (Vec(4, Vec(4, UInt(8.W))))
    val valid = Output(Bool())
}

class AES_GCM extends Module{
    val io = IO(new AES_GCM_IO)

    val ghash = Module(new GHASH)
    val aes = Module(new AES)

    aes.io.key := io.key

    aes.io.stateIn := DontCare
    aes.io.start := DontCare
    ghash.io.xIn := DontCare
    ghash.io.cypherIn := DontCare
    ghash.io.hashKey := DontCare
    ghash.io.start := DontCare

    io.dataOut := VecInit(Seq.fill(4)(VecInit(Seq.fill(4)(0.U(8.W)))))
    io.valid := false.B

    val sIDLE :: sAES :: sGHASH :: Nil = Enum(3)

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

    switch(state){
        is(sIDLE){
            aes.io.start := false.B
            ghash.io.start := false.B
            io.valid := false.B
            io.dataOut := VecInit(Seq.fill(4)(VecInit(Seq.fill(4)(0.U(8.W)))))

            when (io.start){state := MuxCase(sIDLE, Seq(
                            (io.mode === 0.U) -> sIDLE,
                            (io.mode === 1.U) -> sAES,
                            (io.mode === 2.U) -> sGHASH))}
        }
        is(sAES){
            ghash.io.start := false.B
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
        is(sGHASH){
            aes.io.start := false.B
            ghash.io.xIn := flatten(io.dataIn1)
            ghash.io.cypherIn := flatten(io.dataIn2)
            ghash.io.hashKey := flatten(io.key)
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
    }
}







// class AES_GCM extends Module{
//     val io = IO(new AES_GCM_IO)

//     val ghash = Module(new GHASH)
//     val aes = Module(new AES)

//     aes.io.key := io.key

//     aes.io.stateIn := DontCare
//     aes.io.start := DontCare
//     ghash.io.xIn := DontCare
//     ghash.io.cypherIn := DontCare
//     ghash.io.hashKey := DontCare
//     ghash.io.start := DontCare

//     io.dataOut := DontCare
//     io.valid := false.B

//     val sIDLE :: sAAD :: sDATA :: sHASHKEY_sTAG :: someThing :: Nil = Enum(5)

//     val state = RegInit(sIDLE)

//     def flatten(data: Vec[Vec[UInt]]): UInt = {
//         val flattenData = Wire(Vec(16, UInt(8.W)))
//         for (i <- 0 until 4){
//             for (j <- 0 until 4){
//                 flattenData(i*4+j) := data(j)(i)
//             }
//         }
//         flattenData reduce {Cat(_, _)}
//     }

//     val CT = Wire(Vec(4, Vec(4, UInt(8.W))))
//     CT := DontCare

//     switch(state){
//         is(sIDLE){
//             aes.io.start := false.B
//             ghash.io.start := false.B
//             io.valid := false.B
//             io.dataOut := DontCare
//             CT := DontCare

//             when (io.start){state := MuxCase(sIDLE, Seq(
//                             (io.mode === 1.U) -> sAAD,
//                             (io.mode === 2.U) -> sDATA,
//                             (io.mode === 3.U) -> sHASHKEY_sTAG))}
//         }
//         is(sAAD){
//             ghash.io.xIn := flatten(io.dataIn1)
//             ghash.io.cypherIn := flatten(io.dataIn2)
//             ghash.io.hashKey := flatten(io.dataIn3)
//             ghash.io.start := true.B
//             io.valid := ghash.io.valid
//             when(ghash.io.valid){
//                 for (i <- 0 until 4){
//                     for (j <- 0 until 4){
//                         io.dataOut(j)(i) := ghash.io.ghash((15-(i * 4 + j))*8+7, (15-(i*4+j))*8)
//                     }
//                 }
//                 state := sIDLE
//             }

//         }
//         is(sDATA){
//             aes.io.stateIn := io.dataIn1
//             aes.io.start := true.B
//             when(aes.io.valid){
//                 for (i <- 0 until 4){
//                     for (j <- 0 until 4){
//                         CT(i)(j) := aes.io.stateOut(i)(j) ^ io.dataIn2(i)(j)
//                     }
//                 }
//                 state := someThing
//             }
//         }
//         is(someThing){
//             aes.io.start := false.B
//             ghash.io.xIn := flatten(io.dataIn3)
//             ghash.io.cypherIn := flatten(CT)
//             ghash.io.hashKey := flatten(io.dataIn4)
//             ghash.io.start := true.B
//             io.valid := ghash.io.valid
//             when(ghash.io.valid){
//                 for (i <- 0 until 4){
//                     for (j <- 0 until 4){
//                         io.dataOut(j)(i) := ghash.io.ghash((15-(i * 4 + j))*8+7, (15-(i*4+j))*8)//ghash.io.ghash(i*4+j)
//                     }
//                 }
//                 state := sIDLE
//             }
//         }
//         is(sHASHKEY_sTAG){
//             aes.io.stateIn := io.dataIn1
//             aes.io.start := true.B
//             io.valid := aes.io.valid
//             when(aes.io.valid){
//                 for (i <- 0 until 4){
//                     for (j <- 0 until 4){
//                         //io.dataOut(i)(j) := aes.io.stateOut(i)(j) ^ io.dataIn2(i)(j)
//                         CT(i)(j) := aes.io.stateOut(i)(j) ^ io.dataIn2(i)(j)
//                     }
//                 }
//                 state := Mux()
//                 state := sIDLE
//             }
//         }
//     }
// }