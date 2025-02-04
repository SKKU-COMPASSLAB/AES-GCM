package gcm

import chisel3._
import chisel3.util._

class GFMult_IO extends Bundle{
    val a = Input(UInt(128.W))
    val b = Input(UInt(128.W))
    val out = Output(UInt(128.W))
    val valid = Output(Bool())
}

class GFMult extends Module{
    val io = IO(new GFMult_IO)

    val a = WireInit(Reverse(io.a))
    val b = WireInit(Reverse(io.b))
    val r = Cat("b11100001".U, 0.U(120.W))

    val z = Reg(UInt(128.W))
    val v = Reg(UInt(128.W))

    val counter = Reg(UInt(7.W))

    val sIDLE :: sCOMPUTE :: sFINISH :: Nil = Enum(3)
    val state = RegInit(sIDLE)

    io.out := DontCare
    io.valid := Mux(state === sFINISH, true.B, false.B)

    switch(state){
        is(sIDLE){
            z := 0.U
            v := b
            counter := 0.U
            state := sCOMPUTE
        }
        is(sCOMPUTE){
            when(a(counter)){
                z := z ^ v
            }
            when(v(0)){
                v := (v >> 1) ^ r
            }
            .otherwise{
                v := v >> 1
            }
            counter := counter + 1.U
            when(counter.andR){
                state := sFINISH
            }
        }
        is(sFINISH){
            io.out := Reverse(z)
            state := sIDLE
        }
    }

}