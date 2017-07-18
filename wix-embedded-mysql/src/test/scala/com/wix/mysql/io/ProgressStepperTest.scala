package com.wix.mysql.io

import org.specs2.mutable.SpecWithJUnit

class ProgressStepperTest extends SpecWithJUnit {

  "Progress Stepper setAndGet" should {

    "return 0 initially" in {
      new ProgressStepper().setAndGet(0) mustEqual 0
    }

    "return 5 for 5" in {
      new ProgressStepper().setAndGet(5) mustEqual 5
    }

    "return 5 for 9" in {
      new ProgressStepper().setAndGet(9) mustEqual 5
    }

    "return 10 for 10" in {
      new ProgressStepper().setAndGet(10) mustEqual 10
    }
  }

  "Progress Stepper hasNext" should {

    "is true for initial 0" in {
      new ProgressStepper().hasNext(0) must beTrue
    }

    "is false for already set value" in {
      val stepper = new ProgressStepper()
      stepper.setAndGet(0)
      stepper.hasNext(0) must beFalse
    }

    "is false for less than +5" in {
      val stepper = new ProgressStepper()
      stepper.setAndGet(0)
      stepper.hasNext(4) must beFalse
    }

    "is true for +5" in {
      val stepper = new ProgressStepper()
      stepper.setAndGet(0)
      stepper.hasNext(5) must beTrue
    }

  }
}