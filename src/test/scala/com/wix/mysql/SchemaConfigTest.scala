package com.wix.mysql

import java.io.File

import com.google.common.base.Optional
import com.wix.mysql.config.Charset.aCharset
import com.wix.mysql.config.SchemaConfig.aSchemaConfig
import com.wix.mysql.config.{Charset, SchemaConfig}
import org.specs2.mutable.SpecWithJUnit

import scala.collection.convert.wrapAll._

/**
 * @author viliusl
 * @since 06/06/15
 */
class SchemaConfigTest extends SpecWithJUnit {

  "SchemaConfig" should {
    "build with defaults" in {
      val schemaConfig = aSchemaConfig("aschema").build

      schemaConfig.getName mustEqual "aschema"
      schemaConfig.getCharset mustEqual Optional.absent()
      schemaConfig.getScripts must beEmpty
    }

    "build with custom charset" in {
      val charset = aCharset("charset", "collate")

      val schemaConfig = aSchemaConfig("aschema")
        .withCharset(charset)
        .build

      schemaConfig.getCharset mustEqual Optional.of(charset)
    }

    "build with Files" in {
      val files = Seq(new File("/some"), new File("/some/other"))

      val schemaConfig = aSchemaConfig("aschema")
        .withScripts(files)
        .build

      schemaConfig.getScripts.toSeq mustEqual files
    }
  }
}