package com.wix.mysql.io

import com.wix.mysql.io.NotifyingStreamProcessor.ResultMatchingListener
import org.specs2.mutable.SpecWithJUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ResultMatchingListenerTest extends SpecWithJUnit {

  "ResultMatchingListenerTest" should {

    "should return given output pattern matched expected expression" in {
      val listener = new ResultMatchingListener("SUCCESS")
      Future {
        Thread.sleep(1000)
        listener.onMessage("SUCCESS")
      }

      listener.waitForResult(4000)

      listener.isInitWithSuccess must beTrue
    }

    "throw an exception if command does not complete within provided timeout" in {
      new ResultMatchingListener("SUCCESS").waitForResult(1000) must
        throwA[RuntimeException].like { case e => e.getMessage must contain("Timeout of 1 sec exceeded") }
    }
  }

}
