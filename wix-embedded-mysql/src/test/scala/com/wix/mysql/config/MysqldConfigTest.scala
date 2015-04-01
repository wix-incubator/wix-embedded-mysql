package com.wix.mysql.config

import com.wix.mysql.config.MysqldConfig.Defaults._
import com.wix.mysql.distribution.Version._
import de.flapdoodle.embed.process.distribution.IVersion
import org.specs2.mutable.SpecificationWithJUnit

/**
 * @author viliusl
 * @since 27/03/15
 */
class MysqldConfigTest extends SpecificationWithJUnit {

  "Creating an instance of MysqldConfig" should {

    "fail on null username" in {
      aConfig(username = null) must thowAnIllegalArgumentWith("Username")
    }

    "fail on empty username" in {
      aConfig(username = "") must thowAnIllegalArgumentWith("Username")
    }

    "fail on null schema" in {
      aConfig(schema = null) must thowAnIllegalArgumentWith("Schema")
    }

    "fail on empty schema" in {
      aConfig(schema = "") must thowAnIllegalArgumentWith("Schema")
    }

    "forbid to use 'information_schema'" in {
      aConfig(schema = "information_schema") must thowAnIllegalArgumentWith("information_schema")
    }

    "forbid to use custom password with 'root' user" in {
      aConfig(username = "root", password = "some") must thowAnIllegalArgumentWith("custom password for 'root'")
    }

    "fail on negative port" in {
      aConfig(port = -1) must thowAnIllegalArgumentWith("positive")
    }

    "accept null password" in {
      aConfig(password = null) must not(throwA[IllegalArgumentException])
    }

    "accept multiple schemas" in {
      aConfigWithMultipleSchemas(schemas = Seq("aaa", "bbb")).getSchemas.toSeq must contain("aaa", "bbb")
    }
  }

  def aConfig(
    version: IVersion = v5_6_21,
    username: String = USERNAME,
    password: String = PASSWORD,
    schema: String = SCHEMA,
    port: Int = PORT) = new MysqldConfig(version, username, password, schema, port)

  def aConfigWithMultipleSchemas(
    version: IVersion = v5_6_21,
    username: String = USERNAME,
    password: String = PASSWORD,
    schemas: Seq[String] = Seq(SCHEMA),
    port: Int = PORT) = new MysqldConfig(version, username, password, schemas.toArray, port)

  def thowAnIllegalArgumentWith(fragment: String) = throwA[IllegalArgumentException].like {
    case e => e.getMessage must contain(fragment)
  }

}
