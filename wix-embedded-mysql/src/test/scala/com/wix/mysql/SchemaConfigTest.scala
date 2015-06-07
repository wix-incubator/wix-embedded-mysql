package com.wix.mysql

import java.io.File

import com.wix.mysql.config.{Charset, SchemaConfig}
import org.specs2.mutable.SpecWithJUnit

import scala.collection.convert.decorateAsScala._

/**
 * @author viliusl
 * @since 06/06/15
 */
class SchemaConfigTest extends SpecWithJUnit {

  "SchemaConfig" should {
    "provide defaults" in {
      val schemaConfig = SchemaConfig.defaults("aschema")

      schemaConfig.getName mustEqual "aschema"
      schemaConfig.getCharset mustEqual Charset.defaults
      schemaConfig.getScripts.asScala must beEmpty
    }

    "build with defaults" in {
      val schemaConfig = SchemaConfig.Builder("aschema").build

      schemaConfig.getName mustEqual "aschema"
      schemaConfig.getCharset mustEqual Charset.defaults
      schemaConfig.getScripts.asScala must beEmpty
    }

    "build with custom charset" in {
      val charset = Charset.of("charset", "collate")

      val schemaConfig = SchemaConfig.Builder("aschema")
        .withCharset(charset)
        .build

      schemaConfig.getCharset mustEqual charset
    }

    "build with Files" in {
      val files = Seq(new File("/some"), new File("/some/other"))

      val schemaConfig = SchemaConfig.Builder("aschema")
        .withScripts(files:_*)
        .build()

      schemaConfig.getScripts.asScala mustEqual files
    }
  }
}