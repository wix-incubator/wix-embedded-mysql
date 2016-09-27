package com.wix.mysql.io

import java.io.{InputStream, OutputStream, StringReader}
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.ExecutionContext.Implicits.global

import org.specs2.mutable.SpecWithJUnit

import scala.concurrent.Future

class TimingOutProcessExecutorTest extends SpecWithJUnit {

  "TimingOutProcessExecutor" should {

    "return -9 if command does not complete within provided timeout" in {
      TimingOutProcessExecutor.waitFor(new FakeProcess(4000), 2000, TimeUnit.MILLISECONDS) mustEqual -9
    }

    "return process exit code if command does complete within execution bounds" in {
      TimingOutProcessExecutor.waitFor(new FakeProcess(200), 2000, TimeUnit.SECONDS) mustEqual 0
    }
  }
}

class FakeProcess(executionDurationMs: Int) extends Process {
  var completed = new AtomicBoolean(false)

  override def exitValue(): Int = {
    Future {
      Thread.sleep(executionDurationMs)
      completed.set(true)
    }

    if (completed.get()) {
      0
    } else {
      throw new IllegalThreadStateException()
    }
  }

  override def destroy(): Unit = ???

  override def waitFor(): Int = ???

  override def getOutputStream: OutputStream = ???

  override def getErrorStream: InputStream = new FakeInputStream("err")

  override def getInputStream: InputStream = new FakeInputStream("err")
}

class FakeInputStream(collectedOutput: String) extends InputStream {
  val output = new StringReader(collectedOutput)
  override def read(): Int = output.read()
}
