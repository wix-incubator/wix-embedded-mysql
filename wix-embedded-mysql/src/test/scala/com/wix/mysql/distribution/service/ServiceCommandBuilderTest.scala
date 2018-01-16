package com.wix.mysql.distribution.service

import scala.collection.JavaConverters._
import scala.collection.convert.wrapAll._


import org.specs2.mutable.SpecWithJUnit

class ServiceCommandBuilderTest extends SpecWithJUnit {
  "ServiceCommandBuilder" should {

    "build a command" in {
      new ServiceCommandBuilder("v1")
        .addAll(Seq("one", "two"))
        .emit().asScala mustEqual Seq("one", "two")
    }

    "throw an exception if emitting empty command" in {
      new ServiceCommandBuilder("v1").emit() must
        throwA[RuntimeException].like { case e => e.getMessage must contain("was not populated for version: v1") }
    }

    "throw an exception if adding duplicate command" in {
      new ServiceCommandBuilder("v1")
          .addAll(Seq("--some", "--another=12"))
          .addAll(Seq("--some")) must
        throwA[RuntimeException].like { case e => e.getMessage must contain("argument with name '--some' is already present") }
    }
  }
}
