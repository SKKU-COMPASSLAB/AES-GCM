package aes

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

class AESTest extends AnyFlatSpec {
  behavior of "AES"

  // it should "test Key Expansion" in {
  //   simulate(new KeyExpansion){dut =>
  //     val key = Seq(
  //       Seq(0x2b.U, 0x28.U, 0xab.U, 0x09.U),
  //       Seq(0x7e.U, 0xae.U, 0xf7.U, 0xcf.U),
  //       Seq(0x15.U, 0xd2.U, 0x15.U, 0x4f.U),
  //       Seq(0x16.U, 0xa6.U, 0x88.U, 0x3c.U)
  //     )
  //     for (i <- 0 until 4){
  //       for (j <- 0 until 4){
  //         dut.io.keyIn(i)(j).poke(key(i)(j))
  //       }
  //     }
  //     dut.clock.step(50)
  //   }
  // }

    it should "test AES" in {
      simulate(new AES) { dut =>
        val key = Seq(
          Seq(0x2b.U, 0x7e.U, 0x15.U, 0x16.U),
          Seq(0x28.U, 0xae.U, 0xd2.U, 0xa6.U),
          Seq(0xab.U, 0xf7.U, 0x15.U, 0x88.U),
          Seq(0x09.U, 0xcf.U, 0x4f.U, 0x3c.U)
        )
        val plaintext = Seq(
          Seq(0x6b.U, 0xc1.U, 0xbe.U, 0xe2.U),
          Seq(0x2e.U, 0x40.U, 0x9f.U, 0x96.U),
          Seq(0xe9.U, 0x3d.U, 0x7e.U, 0x11.U),
          Seq(0x73.U, 0x93.U, 0x17.U, 0x2a.U)
        )
        val expectedCiphertext = Seq(
          Seq(0x18.U, 0xfa.U, 0x20.U, 0x95.U),
          Seq(0x96.U, 0xc3.U, 0xb0.U, 0xa6.U),
          Seq(0x53.U, 0x57.U, 0x31.U, 0x89.U),
          Seq(0x86.U, 0x3f.U, 0x28.U, 0xdc.U)
        )
        for (i <- 0 until 4) {
          for (j <- 0 until 4) {
            dut.io.key(i)(j).poke(key(i)(j))
            dut.io.stateIn(i)(j).poke(plaintext(i)(j))
          }
        }

        // Apply the plaintext
        // for (i <- 0 until 4) {
        //   for (j <- 0 until 4) {
        //     dut.io.stateIn(i)(j).poke(plaintext(i)(j))
        //   }
        // }
        dut.io.start.poke(true.B)
        dut.clock.step(11)
        for (i <- 0 until 4) {
          for (j <- 0 until 4) {
            dut.io.stateOut(i)(j).expect(expectedCiphertext(i)(j))
          }
        }
        dut.io.valid.expect(true.B)
        dut.io.start.poke(false.B)
        dut.clock.step(10)
        dut.io.valid.expect(false.B)
      }
    }

    // it should "test Mix Columns" in {
    //   simulate(new MixColumns) { dut =>
    //     val key = Seq(
    //       Seq(0xd4.U, 0xe0.U, 0xb8.U, 0x1e.U),
    //       Seq(0xbf.U, 0xb4.U, 0x41.U, 0x27.U),
    //       Seq(0x5d.U, 0x52.U, 0x11.U, 0x98.U),
    //       Seq(0x30.U, 0xae.U, 0xf1.U, 0xe5.U)
    //     )
    //     for (i <- 0 until 4) {
    //       for (j <- 0 until 4) {
    //         dut.io.stateIn(i)(j).poke(key(i)(j))
    //       }
    //     }
    //     dut.clock.step(50)
    //   }
    // }

    // it should "test Key Expansion" in {
    //   simulate(new KeyExpansion) { dut =>
    //     val key = Seq(
    //       Seq(0x2b.U, 0x7e.U, 0x15.U, 0x16.U),
    //       Seq(0x28.U, 0xae.U, 0xd2.U, 0xa6.U),
    //       Seq(0xab.U, 0xf7.U, 0x15.U, 0x88.U),
    //       Seq(0x09.U, 0xcf.U, 0x4f.U, 0x3c.U)
    //     )
    //     for (i <- 0 until 4) {
    //       for (j <- 0 until 4) {
    //         dut.io.keyIn(i)(j).poke(key(i)(j))
    //       }
    //     }
    //     dut.clock.step(50)
    //   }
    // }

  //   it should "test Shift Rows" in {
  //     simulate(new ShiftRow) { dut =>
  //     val inputState = Seq(
  //       Seq(0x63.U, 0x09.U, 0xcd.U, 0xba.U),
  //       Seq(0x53.U, 0xd0.U, 0x51.U, 0x60.U),
  //       Seq(0xe0.U, 0x8c.U, 0x7c.U, 0x9c.U),
  //       Seq(0x20.U, 0x0a.U, 0x93.U, 0x7d.U)
  //     )

  //     // Expected output state after ShiftRows
  //     val expectedOutputState = Seq(
  //       Seq(0x63.U, 0x09.U, 0xcd.U, 0xba.U), // No shift
  //       Seq(0xd0.U, 0x51.U, 0x60.U, 0x53.U), // Left shift by 1
  //       Seq(0x7c.U, 0x9c.U, 0xe0.U, 0x8c.U), // Left shift by 2
  //       Seq(0x7d.U, 0x20.U, 0x0a.U, 0x93.U)  // Left shift by 3
  //     )

  //     // Apply the input state
  //     for (i <- 0 until 4) {
  //       for (j <- 0 until 4) {
  //         dut.io.stateIn(i)(j).poke(inputState(i)(j))
  //       }
  //     }

  //     // Step the clock to process the input
  //     dut.clock.step(1)

  //     // Check the output state
  //     for (i <- 0 until 4) {
  //       for (j <- 0 until 4) {
  //         dut.io.stateOut(i)(j).expect(expectedOutputState(i)(j))
  //       }
  //     }
  //   }
  // }
}