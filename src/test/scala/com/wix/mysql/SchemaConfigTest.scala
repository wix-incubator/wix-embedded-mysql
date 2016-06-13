package com.wix.mysql

import java.io.File

import com.wix.mysql.config.Charset.aCharset
import com.wix.mysql.config.SchemaConfig.aSchemaConfig
import org.specs2.mutable.SpecWithJUnit

import scala.collection.convert.wrapAll._

class SchemaConfigTest extends SpecWithJUnit {

  "SchemaConfig" should {
    "build with defaults" in {
      val schemaConfig = aSchemaConfig("aschema").build

      schemaConfig.getName mustEqual "aschema"
      schemaConfig.getCharset must beNull
      schemaConfig.getScripts must beEmpty
    }

    "build with custom charset" in {
      val charset = aCharset("charset", "collate")

      val schemaConfig = aSchemaConfig("aschema")
        .withCharset(charset)
        .build

      schemaConfig.getCharset mustEqual charset
    }

    "build with sources" in {
      val sources = Seq(Sources.fromFile(new File("/some")), Sources.fromFile(new File("/some/other")))

      val schemaConfig = aSchemaConfig("aschema")
        .withScripts(sources)
        .build

      schemaConfig.getScripts.toSeq mustEqual sources
    }
  }
}
