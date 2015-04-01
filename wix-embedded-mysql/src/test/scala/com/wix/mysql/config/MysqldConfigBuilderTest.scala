package com.wix.mysql.config

import com.wix.mysql.config.MysqldConfig.Defaults._
import com.wix.mysql.distribution.Version
import de.flapdoodle.embed.process.distribution.IVersion
import org.specs2.mutable.SpecificationWithJUnit

/**
 * @author viliusl
 * @since 26/03/15
 */
class MysqldConfigBuilderTest extends SpecificationWithJUnit {

  def template = new MysqldConfigBuilder(Version.v5_6_21)

  "MysqldConfigBuilder should build a config with" >> {

    "defaults" in {
        template.build must matchVersion(Version.v5_6_21) and
        matchUser(USERNAME) and
        matchPassword(PASSWORD) and
        matchSchemas(SCHEMA) and
        matchPort(PORT)
    }

    "custom values" in {
      template.withUsername(USERNAME + "a").withPassword(PASSWORD + "a").withSchema(SCHEMA + "a").withPort(PORT + 1).build must
        matchUser(USERNAME + "a") and matchPassword(PASSWORD + "a") and matchSchemas(SCHEMA + "a") and matchPort(PORT + 1)
    }

    "custom schemas" in {
      val schemas = Array("schema1", "schema2")
      template.withSchemas(schemas).build must matchSchemas(schemas:_*)
    }

    "null password" in {
      template.withPassword(null).build must matchPassword(null)
    }
  }

  "MysqldConfigBuilder should fail on" >> {

    "null username" in {
      template.withUsername(null).build must throwA[IllegalArgumentException]
    }
  }

  def matchVersion(version: IVersion) = be_===(version) ^^ { (_: MysqldConfig).version aka "version" }
  def matchUser(user: String) = be_===(user) ^^ { (_: MysqldConfig).getUsername aka "username" }
  def matchPassword(password: String) = be_===(password) ^^ { (_: MysqldConfig).getPassword aka "password" }
  def matchSchemas(schemas: String*) = containTheSameElementsAs(schemas) ^^ { (_: MysqldConfig).getSchemas.toSeq aka "schemas" }
  def matchPort(port: Int) = be_===(port) ^^ { (_: MysqldConfig).getPort aka "port" }
}
