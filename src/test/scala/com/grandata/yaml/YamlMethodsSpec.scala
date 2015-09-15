package com.grandata.yaml

import org.specs2.mutable.Specification

/**
 * Created by gustavo on 15/08/15.
 */
class YamlMethodsSpec extends Specification {
  import org.json4s._

  "YamlMethods parse" should {
    "parse int" in {
      YamlMethods.parse("1") mustEqual JInt(1)
    }

    "parse really big int" in {
      YamlMethods.parse("424242424242424242424242424242424242424242424242424242424242424242424242") mustEqual
        JInt(BigInt("424242424242424242424242424242424242424242424242424242424242424242424242"))
    }


    "parse null" in {
      YamlMethods.parse("null") mustEqual JNull
    }

    "parse nothing as null" in {
      // WARNING: Right now I cannot distinguish between null and nothing. Should change in the future.
      (YamlMethods.parse("an_empty_key:\n") \ "an_empty_key") mustEqual JNull
    }

    "parse 0.0 big decimal" in {
      YamlMethods.parse("0.0", true) mustEqual JDecimal(0.0)
    }

    "parse 1.0 big decimal" in {
      YamlMethods.parse("1.0", true) mustEqual JDecimal(1.0)
    }

    "parse some decimal" in {
      YamlMethods.parse("-1.2", true) mustEqual JDecimal(-1.2)
    }

    "parse really big decimal" in {
      YamlMethods.parse("-1.40737488355328E+15", true) mustEqual JDecimal(-1.40737488355328E+15)
    }

    "parse 0.0 float" in {
      YamlMethods.parse("0.0") mustEqual JDouble(0.0)
    }

    "parse 1.0 float" in {
      YamlMethods.parse("1.0") mustEqual JDouble(1.0)
    }

    "parse some float" in {
      YamlMethods.parse("-1.2") mustEqual JDouble(-1.2)
    }

    "parse really big float" in {
      YamlMethods.parse("-1.40737488355328E+15") mustEqual JDouble(-1.40737488355328E+15)
    }

    "parse true as boolean" in {
      YamlMethods.parse("true") mustEqual JBool(true)
    }

    "parse yes as boolean" in {
      YamlMethods.parse("yes") mustEqual JBool(true)
    }

    "parse no as boolean" in {
      YamlMethods.parse("no") mustEqual JBool(false)
    }

    "parse false as boolean" in {
      YamlMethods.parse("false") mustEqual JBool(false)
    }

    "parse a simple int array" in {
      val source =
        """
          |- 1
          |- 2
          |-
        """.stripMargin

      val parsed = YamlMethods.parse(source)
      parsed mustEqual JArray(List(JInt(1), JInt(2), JNull))
    }

    "parse a string" in {
      YamlMethods.parse("hello") mustEqual JString("hello")
    }

    "parse empty array" in {
      YamlMethods.parse("[]") mustEqual JArray(Nil)
    }

    "parse an empty dictionary" in {
      YamlMethods.parse("{}") mustEqual JObject(Nil)
    }

    "parse a simple dictionary" in {
      val source =
        """
          |encoding: UTF-8
          |key: 1
          |another_key: 2
          |null_key: null
          |a_string: I'm a string
        """.stripMargin

      val parsed = YamlMethods.parse(source)

      println(s"Hola: ${(parsed \ "encoding") }")
      (parsed \ "encoding") mustEqual JString("UTF-8")
      (parsed \ "key") mustEqual JInt(1)
      (parsed \ "another_key") mustEqual JInt(2)
      (parsed \ "null_key") mustEqual JNull
      (parsed \ "a_string") mustEqual JString("I'm a string")
    }
  }
}
