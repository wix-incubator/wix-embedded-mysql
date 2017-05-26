package com.wix.mysql.io

import com.wix.mysql.io.NotifyingStreamProcessor.ResultMatchingListener
import org.specs2.mutable.SpecWithJUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ResultMatchingListenerTest extends SpecWithJUnit {

  "ResultMatchingListenerTest" should {

    "should return success given output matched provided pattern" in {
      val listener = new ResultMatchingListener("SUCCESS")

      Future {
        listener.waitForResult(4000)
      }

      listener.onMessage("SUCCESS: completed")

      listener.isInitWithSuccess must beTrue
      listener.getFailureFound must beNull
    }

    "throw a timeout exception if command does not complete within provided timeout" in {
      new ResultMatchingListener("SUCCESS").waitForResult(1000) must
        throwA[RuntimeException].like { case e => e.getMessage must contain("Timeout of 1 sec exceeded") }
    }

    "should return error output given error expression matched" in {
      val listener = new ResultMatchingListener("SUCCESS")

      Future {
        Thread.sleep(500)
        listener.onMessage("[ERROR] woops")
      }

      listener.waitForResult(5000)

      listener.isInitWithSuccess must beFalse
      listener.getFailureFound mustEqual "[ERROR] woops"
    }
  }
}
