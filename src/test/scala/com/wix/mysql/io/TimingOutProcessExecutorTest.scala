package com.wix.mysql.io

import java.io.{InputStream, OutputStream, StringReader}
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

import scala.concurrent.ExecutionContext.Implicits.global
import org.specs2.mutable.SpecWithJUnit

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class TimingOutProcessExecutorTest extends SpecWithJUnit {

  "TimingOutProcessExecutor" should {

    "throw an exception if command does not complete within provided timeout" in {
      TimingOutProcessExecutor.waitFor(new FakeProcess(4000), TimeUnit.MILLISECONDS.toNanos(2000)) must
        throwA[InterruptedException].like { case e => e.getMessage must contain("Timeout of 2 sec exceeded")}
    }

    "return process exit code if command does complete within execution bounds" in {
      TimingOutProcessExecutor.waitFor(new FakeProcess(500), TimeUnit.MILLISECONDS.toNanos(2000)) mustEqual 0
    }
  }
}

class FakeProcess(executionDurationMs: Int) extends Process {
  @volatile
  var completed: Try[Int] = Failure(new IllegalThreadStateException())
  Future {
    Thread.sleep(executionDurationMs)
    System.out.println("complete")
    completed = Success(0)
  }

  override def exitValue(): Int = {
    completed.get
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
