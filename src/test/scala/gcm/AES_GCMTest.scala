package gcm

import chisel3._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec
import svsim._
import chisel3.simulator._
import java.nio.file.Files
import java.nio.file.Paths
import java.io.File
import scala.reflect.io.Directory

object Simulator extends PeekPokeAPI {
  def simulate[T <: RawModule](
      module: => T
  )(body: (T) => Unit): Unit = {
    makeSimulator
      .simulate(module)({ module =>
        module.controller.setTraceEnabled(true)
        body(module.wrapped)
      })
      .result
  }
  import Simulator._

  private class DefaultSimulator(val workspacePath: String) extends SingleBackendSimulator[verilator.Backend] {
    val backend = verilator.Backend.initializeFromProcessEnvironment()
    val tag = "default"
    val commonCompilationSettings = CommonCompilationSettings()
    val backendSpecificCompilationSettings = verilator.Backend.CompilationSettings(
      traceStyle = Some(verilator.Backend.CompilationSettings.TraceStyle.Vcd(traceUnderscore = true))
    )
  }
  private def makeSimulator: DefaultSimulator = {
    val id = ProcessHandle.current().pid().toString()
    val className = getClass().getName().stripSuffix("$")
    new DefaultSimulator(Files.createDirectories(Paths.get(s"test_run_dir/${className}_${id}")).toString)
  }
}

import Simulator._

class AES_GCMTest extends AnyFlatSpec {
  behavior of "GCM"

  // it should "test GHASH" in {
  //   simulate(new GHASH) { dut =>
  //     dut.clock.step(1)
  //   }
  // }

  // it should "test GFMult" in {
  //   simulate(new GFMult) { dut =>
  //     val test_vectors = List(
  //     // List("h80000000000000000000000000000000".U, "h80000000000000000000000000000000".U, "h80000000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h40000000000000000000000000000000".U, "hc0000000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h20000000000000000000000000000000".U, "he0000000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h10000000000000000000000000000000".U, "hf0000000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h08000000000000000000000000000000".U, "hf8000000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h04000000000000000000000000000000".U, "hfc000000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h02000000000000000000000000000000".U, "hfe000000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h01000000000000000000000000000000".U, "hff000000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00800000000000000000000000000000".U, "hff800000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00400000000000000000000000000000".U, "hffc00000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00200000000000000000000000000000".U, "hffe00000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00100000000000000000000000000000".U, "hfff00000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00080000000000000000000000000000".U, "hfff80000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00040000000000000000000000000000".U, "hfffc0000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00020000000000000000000000000000".U, "hfffe0000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00010000000000000000000000000000".U, "hffff0000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00008000000000000000000000000000".U, "hffff8000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00004000000000000000000000000000".U, "hffffc000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00002000000000000000000000000000".U, "hffffe000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00001000000000000000000000000000".U, "hfffff000000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000800000000000000000000000000".U, "hfffff800000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000400000000000000000000000000".U, "hfffffc00000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000200000000000000000000000000".U, "hfffffe00000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000100000000000000000000000000".U, "hffffff00000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000080000000000000000000000000".U, "hffffff80000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000040000000000000000000000000".U, "hffffffc0000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000020000000000000000000000000".U, "hffffffe0000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000010000000000000000000000000".U, "hfffffff0000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000008000000000000000000000000".U, "hfffffff8000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000004000000000000000000000000".U, "hfffffffc000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000002000000000000000000000000".U, "hfffffffe000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000001000000000000000000000000".U, "hffffffff000000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000800000000000000000000000".U, "hffffffff800000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000400000000000000000000000".U, "hffffffffc00000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000200000000000000000000000".U, "hffffffffe00000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000100000000000000000000000".U, "hfffffffff00000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000080000000000000000000000".U, "hfffffffff80000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000040000000000000000000000".U, "hfffffffffc0000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000020000000000000000000000".U, "hfffffffffe0000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000010000000000000000000000".U, "hffffffffff0000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000008000000000000000000000".U, "hffffffffff8000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000004000000000000000000000".U, "hffffffffffc000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000002000000000000000000000".U, "hffffffffffe000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000001000000000000000000000".U, "hfffffffffff000000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000800000000000000000000".U, "hfffffffffff800000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000400000000000000000000".U, "hfffffffffffc00000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000200000000000000000000".U, "hfffffffffffe00000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000100000000000000000000".U, "hffffffffffff00000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000080000000000000000000".U, "hffffffffffff80000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000040000000000000000000".U, "hffffffffffffc0000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000020000000000000000000".U, "hffffffffffffe0000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000010000000000000000000".U, "hfffffffffffff0000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000008000000000000000000".U, "hfffffffffffff8000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000004000000000000000000".U, "hfffffffffffffc000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000002000000000000000000".U, "hfffffffffffffe000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000001000000000000000000".U, "hffffffffffffff000000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000800000000000000000".U, "hffffffffffffff800000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000400000000000000000".U, "hffffffffffffffc00000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000200000000000000000".U, "hffffffffffffffe00000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000100000000000000000".U, "hfffffffffffffff00000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000080000000000000000".U, "hfffffffffffffff80000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000040000000000000000".U, "hfffffffffffffffc0000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000020000000000000000".U, "hfffffffffffffffe0000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000010000000000000000".U, "hffffffffffffffff0000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000008000000000000000".U, "hffffffffffffffff8000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000004000000000000000".U, "hffffffffffffffffc000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000002000000000000000".U, "hffffffffffffffffe000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000001000000000000000".U, "hfffffffffffffffff000000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000800000000000000".U, "hfffffffffffffffff800000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000400000000000000".U, "hfffffffffffffffffc00000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000200000000000000".U, "hfffffffffffffffffe00000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000100000000000000".U, "hffffffffffffffffff00000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000080000000000000".U, "hffffffffffffffffff80000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000040000000000000".U, "hffffffffffffffffffc0000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000020000000000000".U, "hffffffffffffffffffe0000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000010000000000000".U, "hfffffffffffffffffff0000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000008000000000000".U, "hfffffffffffffffffff8000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000004000000000000".U, "hfffffffffffffffffffc000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000002000000000000".U, "hfffffffffffffffffffe000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000001000000000000".U, "hffffffffffffffffffff000000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000800000000000".U, "hffffffffffffffffffff800000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000400000000000".U, "hffffffffffffffffffffc00000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000200000000000".U, "hffffffffffffffffffffe00000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000100000000000".U, "hfffffffffffffffffffff00000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000080000000000".U, "hfffffffffffffffffffff80000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000040000000000".U, "hfffffffffffffffffffffc0000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000020000000000".U, "hfffffffffffffffffffffe0000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000010000000000".U, "hffffffffffffffffffffff0000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000008000000000".U, "hffffffffffffffffffffff8000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000004000000000".U, "hffffffffffffffffffffffc000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000002000000000".U, "hffffffffffffffffffffffe000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000001000000000".U, "hfffffffffffffffffffffff000000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000800000000".U, "hfffffffffffffffffffffff800000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000400000000".U, "hfffffffffffffffffffffffc00000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000200000000".U, "hfffffffffffffffffffffffe00000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000100000000".U, "hffffffffffffffffffffffff00000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000080000000".U, "hffffffffffffffffffffffff80000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000040000000".U, "hffffffffffffffffffffffffc0000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000020000000".U, "hffffffffffffffffffffffffe0000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000010000000".U, "hfffffffffffffffffffffffff0000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000008000000".U, "hfffffffffffffffffffffffff8000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000004000000".U, "hfffffffffffffffffffffffffc000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000002000000".U, "hfffffffffffffffffffffffffe000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000001000000".U, "hffffffffffffffffffffffffff000000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000800000".U, "hffffffffffffffffffffffffff800000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000400000".U, "hffffffffffffffffffffffffffc00000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000200000".U, "hffffffffffffffffffffffffffe00000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000100000".U, "hfffffffffffffffffffffffffff00000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000080000".U, "hfffffffffffffffffffffffffff80000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000040000".U, "hfffffffffffffffffffffffffffc0000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000020000".U, "hfffffffffffffffffffffffffffe0000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000010000".U, "hffffffffffffffffffffffffffff0000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000008000".U, "hffffffffffffffffffffffffffff8000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000004000".U, "hffffffffffffffffffffffffffffc000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000002000".U, "hffffffffffffffffffffffffffffe000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000001000".U, "hfffffffffffffffffffffffffffff000".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000000800".U, "hfffffffffffffffffffffffffffff800".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000000400".U, "hfffffffffffffffffffffffffffffc00".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000000200".U, "hfffffffffffffffffffffffffffffe00".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000000100".U, "hffffffffffffffffffffffffffffff00".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000000080".U, "hffffffffffffffffffffffffffffff80".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000000040".U, "hffffffffffffffffffffffffffffffc0".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000000020".U, "hffffffffffffffffffffffffffffffe0".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000000010".U, "hfffffffffffffffffffffffffffffff0".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000000008".U, "hfffffffffffffffffffffffffffffff8".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000000004".U, "hfffffffffffffffffffffffffffffffc".U),
  //     // List("h80000000000000000000000000000000".U, "h00000000000000000000000000000002".U, "hfffffffffffffffffffffffffffffffe".U),
  //     List("h80000000000000000000000000000000".U, "h00000000000000000000000000000001".U, "hffffffffffffffffffffffffffffffff".U))

  //     var count = 1
  //     for (test_vector <- test_vectors) {
  //       dut.io.a.poke(test_vector(0))
  //       dut.io.b.poke(test_vector(1))
  //       dut.io.start.poke(true.B)
  //       dut.clock.step(128+1)
  //       // dut.io.out.expect(test_vector(2))
  //       dut.io.out.expect("h00000000000000000000000000000001".U)
  //       dut.io.valid.expect(true.B)
  //       dut.io.start.poke(false.B)
  //       dut.clock.step(130)
  //       println(s"Test $count passed\n")
  //       count += 1
  //     }
  //   }
  // }

  // it should "test GCM Mode 3" in {
  //   simulate(new AES_GCM) { dut =>
  //       val key = Seq(
  //         Seq(0x2b.U, 0x7e.U, 0x15.U, 0x16.U),
  //         Seq(0x28.U, 0xae.U, 0xd2.U, 0xa6.U),
  //         Seq(0xab.U, 0xf7.U, 0x15.U, 0x88.U),
  //         Seq(0x09.U, 0xcf.U, 0x4f.U, 0x3c.U)
  //       )
  //       val plaintext = Seq(
  //         Seq(0x6b.U, 0xc1.U, 0xbe.U, 0xe2.U),
  //         Seq(0x2e.U, 0x40.U, 0x9f.U, 0x96.U),
  //         Seq(0xe9.U, 0x3d.U, 0x7e.U, 0x11.U),
  //         Seq(0x73.U, 0x93.U, 0x17.U, 0x2a.U)
  //       )
  //       val expectedCiphertext = Seq(
  //         Seq(0x18.U, 0xfa.U, 0x20.U, 0x95.U),
  //         Seq(0x96.U, 0xc3.U, 0xb0.U, 0xa6.U),
  //         Seq(0x53.U, 0x57.U, 0x31.U, 0x89.U),
  //         Seq(0x86.U, 0x3f.U, 0x28.U, 0xdc.U)
  //       )

  //       for (i <- 0 until 4){
  //           for (j <- 0 until 4){
  //               dut.io.key(i)(j).poke(key(i)(j))
  //               dut.io.dataIn1(i)(j).poke(plaintext(i)(j))
  //           }
  //       }

  //       dut.io.start.poke(true.B)
  //       dut.io.mode.poke(1.U)

  //       dut.clock.step(11+1)
  //       for (i <- 0 until 4){
  //           for (j <- 0 until 4){
  //               dut.io.dataOut(i)(j).expect(expectedCiphertext(i)(j))
  //           }
  //       }
  //       dut.io.valid.expect(true.B)
  //       dut.io.start.poke(false.B)
  //       dut.clock.step(100)

  //     }
  //   }

    // it should "test GCM Mode 1" in {
    //   simulate(new AES_GCM) { dut => 
    //     val in_1 = Seq(
    //                    Seq("h80".U(8.W), "h00".U(8.W), "h00".U(8.W), "h00".U(8.W)),
    //                    Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h00".U(8.W)),
    //                    Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h00".U(8.W)),
    //                    Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h00".U(8.W))
    //                    )
    //     val in_2 = Seq(
    //                    Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h00".U(8.W)),
    //                    Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h00".U(8.W)),
    //                    Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h00".U(8.W)),
    //                    Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h01".U(8.W))
    //                    )

    //     for (i <- 0 until 4){
    //       for (j <- 0 until 4){
    //           dut.io.dataIn1(i)(j).poke(in_1(i)(j))
    //           dut.io.dataIn3(i)(j).poke(in_2(i)(j))
    //       }
    //     }

    //     dut.io.start.poke(true.B)
    //     dut.io.mode.poke(2.U)

    //     dut.clock.step(128+2)
    //     for (i <- 0 until 4){
    //         for (j <- 0 until 4){
    //             dut.io.dataOut(i)(j).expect(in_2(i)(j))
    //         }
    //     }
    //     dut.io.valid.expect(true.B)
    //     dut.io.start.poke(false.B)
    //     dut.clock.step(100)
    //   }
    // }

    // it should "test GCM Mode 1" in {
    //   simulate(new AES_GCM) { dut => 
    //     val hashSubKey = Seq(
    //                     Seq("h66".U(8.W), "hef".U(8.W), "h88".U(8.W), "hca".U(8.W)),
    //                     Seq("he9".U(8.W), "h8a".U(8.W), "h4c".U(8.W), "h34".U(8.W)),
    //                     Seq("h4b".U(8.W), "h2c".U(8.W), "hfa".U(8.W), "h2b".U(8.W)),
    //                     Seq("hd4".U(8.W), "h3b".U(8.W), "h59".U(8.W), "h2e".U(8.W)))

    //     val E_Y0 = Seq(
    //                     Seq("h58".U(8.W), "hfa".U(8.W), "h36".U(8.W), "ha4".U(8.W)),
    //                     Seq("he2".U(8.W), "h7e".U(8.W), "h7f".U(8.W), "he7".U(8.W)),
    //                     Seq("hfc".U(8.W), "h30".U(8.W), "h1d".U(8.W), "h45".U(8.W)),
    //                     Seq("hce".U(8.W), "h61".U(8.W), "h57".U(8.W), "h5a".U(8.W)) 
    //                     )

    //     val ghash = Seq(
    //                     Seq("hA0".U(8.W), "h63".U(8.W), "h9E".U(8.W), "h60".U(8.W)),
    //                     Seq("hBB".U(8.W), "hAF".U(8.W), "h35".U(8.W), "hA4".U(8.W)),
    //                     Seq("h28".U(8.W), "hDB".U(8.W), "hFD".U(8.W), "h2B".U(8.W)),
    //                     Seq("h9B".U(8.W), "hD3".U(8.W), "h60".U(8.W), "hC3".U(8.W)) 
    //                   )

    //     for (i <- 0 until 4){
    //       for (j <- 0 until 4){
    //           dut.io.dataIn1(i)(j).poke(0.U(8.W))
    //           dut.io.dataIn2(i)(j).poke(E_Y0(i)(j))
    //           dut.io.key(i)(j).poke(hashSubKey(i)(j))
    //       }
    //     }

    //     dut.io.start.poke(true.B)
    //     dut.io.mode.poke(2.U)

    //     while (dut.io.valid.peek().litToBoolean == false){
    //       dut.clock.step(1)
    //     }
    //     // dut.clock.step(128+1)
    //     for (i <- 0 until 4){
    //         for (j <- 0 until 4){
    //             dut.io.dataOut(i)(j).expect(ghash(i)(j))
    //         }
    //     }
    //     dut.io.valid.expect(true.B)
    //     dut.io.start.poke(false.B)
    //     dut.clock.step(100)
    //   }
    // }

    // it should "test GCM Mode 1" in {
    //   simulate(new AES_GCM) { dut => 
    //     val hashSubKey = Seq(
    //                     Seq("h66".U(8.W), "hef".U(8.W), "h88".U(8.W), "hca".U(8.W)),
    //                     Seq("he9".U(8.W), "h8a".U(8.W), "h4c".U(8.W), "h34".U(8.W)),
    //                     Seq("h4b".U(8.W), "h2c".U(8.W), "hfa".U(8.W), "h2b".U(8.W)),
    //                     Seq("hd4".U(8.W), "h3b".U(8.W), "h59".U(8.W), "h2e".U(8.W)))

    //     val E_Y0 = Seq(
    //                     Seq("h58".U(8.W), "hfa".U(8.W), "h36".U(8.W), "ha4".U(8.W)),
    //                     Seq("he2".U(8.W), "h7e".U(8.W), "h7f".U(8.W), "he7".U(8.W)),
    //                     Seq("hfc".U(8.W), "h30".U(8.W), "h1d".U(8.W), "h45".U(8.W)),
    //                     Seq("hce".U(8.W), "h61".U(8.W), "h57".U(8.W), "h5a".U(8.W)) 
    //                     )

    //     val xIn = Seq(
    //                     Seq("hA0".U(8.W), "h63".U(8.W), "h9E".U(8.W), "h60".U(8.W)),
    //                     Seq("hBB".U(8.W), "hAF".U(8.W), "h35".U(8.W), "hA4".U(8.W)),
    //                     Seq("h28".U(8.W), "hDB".U(8.W), "hFD".U(8.W), "h2B".U(8.W)),
    //                     Seq("h9B".U(8.W), "hD3".U(8.W), "h60".U(8.W), "hC3".U(8.W)) 
    //                   )

    //     for (i <- 0 until 4){
    //       for (j <- 0 until 4){
    //           dut.io.dataIn1(i)(j).poke(0.U(8.W))
    //           dut.io.dataIn2(i)(j).poke(0.U(8.W))
    //           dut.io.key(i)(j).poke(hashSubKey(i)(j))
    //       }
    //     }

    //     dut.io.start.poke(true.B)
    //     dut.io.mode.poke(2.U)

    //     while (dut.io.valid.peek().litToBoolean == false){
    //       dut.clock.step(1)
    //     }
    //     // dut.clock.step(128+1)
    //     for (i <- 0 until 4){
    //         for (j <- 0 until 4){
    //             dut.io.dataOut(i)(j).expect(hashSubKey(i)(j))
    //         }
    //     }
    //     dut.io.valid.expect(true.B)
    //     dut.io.start.poke(false.B)
    //     dut.clock.step(100)
    //   }
    // }
// --------------------------------------------
  it should "test GCM Mode 3" in {
    simulate(new AES_GCM) { dut =>
        val tag = Seq(
                        Seq("h58".U(8.W), "hfa".U(8.W), "h36".U(8.W), "ha4".U(8.W)),
                        Seq("he2".U(8.W), "h7e".U(8.W), "h7f".U(8.W), "he7".U(8.W)),
                        Seq("hfc".U(8.W), "h30".U(8.W), "h1d".U(8.W), "h45".U(8.W)),
                        Seq("hce".U(8.W), "h61".U(8.W), "h57".U(8.W), "h5a".U(8.W)) 
                        )

        val iv_ctr = Seq(
                        Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h00".U(8.W)),
                        Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h00".U(8.W)),
                        Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h00".U(8.W)),
                        Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h01".U(8.W))
                        )

        for (i <- 0 until 4){
            for (j <- 0 until 4){
                dut.io.key(i)(j).poke(0.U(8.W))
                dut.io.dataIn1(i)(j).poke(iv_ctr(i)(j))
                dut.io.dataIn2(i)(j).poke(0.U(8.W))
            }
        }

        dut.io.start.poke(true.B)
        dut.io.mode.poke(1.U)

        // dut.clock.step(11+1)
        while (dut.io.valid.peek().litToBoolean == false){
          dut.clock.step(1)
        }

        for (i <- 0 until 4){
            for (j <- 0 until 4){
                dut.io.dataOut(i)(j).expect(tag(i)(j))
            }
        }
        dut.io.valid.expect(true.B)
        dut.io.start.poke(false.B)
        dut.clock.step(100)

      }
    }

    // it should "test GCM Mode 2" in {
    //   simulate(new AES_GCM) { dut => 
    //     val hashSubKey = Seq(
    //                     Seq("h66".U(8.W), "hef".U(8.W), "h88".U(8.W), "hca".U(8.W)),
    //                     Seq("he9".U(8.W), "h8a".U(8.W), "h4c".U(8.W), "h34".U(8.W)),
    //                     Seq("h4b".U(8.W), "h2c".U(8.W), "hfa".U(8.W), "h2b".U(8.W)),
    //                     Seq("hd4".U(8.W), "h3b".U(8.W), "h59".U(8.W), "h2e".U(8.W)))
    //     val iv_ctr = Seq(
    //                     Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h00".U(8.W)),
    //                     Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h00".U(8.W)),
    //                     Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h00".U(8.W)),
    //                     Seq("h00".U(8.W), "h00".U(8.W), "h00".U(8.W), "h01".U(8.W))
    //                     )

    //     for (i <- 0 until 4){
    //       for (j <- 0 until 4){
    //           dut.io.dataIn1(i)(j).poke(iv_ctr(i)(j))
    //           dut.io.dataIn2(i)(j).poke(0.U(8.W))
    //           dut.io.key(i)(j).poke("h00".U(8.W))
    //           // dut.io.dataIn3(i)(j).poke(hashSubKey(i)(j))
    //           // dut.io.dataIn4(i)(j).poke(0.U(8.W))
    //       }
    //     }

    //     dut.io.start.poke(true.B)
    //     dut.io.mode.poke(1.U)

    //     dut.clock.step(11+1)

    //     dut.io.start.poke(false.B)
    //     dut.clock.step(1)
    //     dut.io.mode.poke(0.U)
    //     // dut.clock.step(1)

    //     // for (i <- 0 until 4){
    //     //     for (j <- 0 until 4){
    //     //         println(dut.io.dataOut(j)(i).peek().litValue.toString(16))
    //     //     }
    //     // }

    //     for (i <- 0 until 4){
    //       for (j <- 0 until 4){
    //           dut.io.dataIn1(i)(j).poke(0.U(8.W))
    //           dut.io.dataIn2(i)(j).poke(dut.io.dataOut(i)(j).peek())
    //           dut.io.key(i)(j).poke(hashSubKey(i)(j))
    //       }
    //     }

    //     dut.io.start.poke(true.B)
    //     dut.io.mode.poke(2.U)

    //     dut.clock.step(128+2)

    //     dut.io.start.poke(false.B)
    //     dut.io.mode.poke(0.U)
    //     dut.clock.step(100)
    //   }
    // }
}