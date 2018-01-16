package com.wix.mysql.io

import java.io.{InputStream, OutputStream, StringReader}
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

import org.specs2.mutable.SpecWithJUnit

class TimingOutProcessExecutorTest extends SpecWithJUnit {

  "TimingOutProcessExecutor" should {

    "throw an exception if command does not complete within provided timeout" in {
      new TimingOutProcessExecutor("cmd").waitFor(new FakeProcess(Integer.MAX_VALUE), TimeUnit.MILLISECONDS.toNanos(1000)) must
        throwA[InterruptedException].like { case e => e.getMessage must contain("Timeout of 1 sec exceeded while waiting for 'cmd'")}
    }

    "return process exit code if command does complete within execution bounds" in {
      new TimingOutProcessExecutor("").waitFor(new FakeProcess(3), TimeUnit.MILLISECONDS.toNanos(2000)) mustEqual 0
    }
  }
}

class FakeProcess(val completeAfterNumberOfCalls: Int) extends Process {
  val exitValueInvoctionCounter = new AtomicInteger(completeAfterNumberOfCalls)

  override def exitValue(): Int = {
    exitValueInvoctionCounter.decrementAndGet() match {
      case 0 => 0
      case _ => throw new IllegalThreadStateException()
    }
  }

  override def destroy(): Unit = {}

  override def waitFor(): Int = ???

  override def getOutputStream: OutputStream = ???

  override def getErrorStream: InputStream = new FakeInputStream("err")

  override def getInputStream: InputStream = new FakeInputStream("err")
}

class FakeInputStream(collectedOutput: String) extends InputStream {
  val output = new StringReader(collectedOutput)
  override def read(): Int = output.read()
}
