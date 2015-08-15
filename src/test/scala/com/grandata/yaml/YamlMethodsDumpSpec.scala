package com.grandata.yaml

import org.specs2.mutable.Specification

/**
 * Created by gustavo on 15/08/15.
 */
class YamlMethodsDumpSpec extends Specification {
  import org.json4s._
  implicit val formats = DefaultFormats
  "YamlMethods compact" should {
    "Give a compact json" in {
      import org.json4s.native.JsonMethods
      val json = JArray(
        List(
          JObject(
            "an_int" -> JInt(1),
            "a_double" -> JDouble(1.2),
            "a_string" -> JString("str"),
            "another_null" -> JNothing),
          JInt(2),
          JObject(Nil),
          JNull)
      )

      println(YamlMethods.compact(json))
      YamlMethods.compact(json) mustEqual JsonMethods.compact(JsonMethods.render(json))
    }
  }

  "YamlMethods jvalue2java" should {
    "convert JInt to int" in {
      YamlMethods.jvalue2java(JInt(1)) mustEqual 1
    }

    "convert JInt with BigInt to java BigInt" in {
      YamlMethods
        .jvalue2java(JInt(BigInt("4242424242424242424242424242424242424242424242424242424242424242"))) mustEqual
          new java.math.BigInteger("4242424242424242424242424242424242424242424242424242424242424242")
    }

    "convert JDouble to float" in {
      YamlMethods.jvalue2java(JDouble(1.2)) mustEqual 1.2
    }

    "convert JDecimal to float" in {
      YamlMethods.jvalue2java(JDecimal(1.2)) mustEqual 1.2
    }

    "convert JBool to boolean" in {
      YamlMethods.jvalue2java(JBool(true)) mustEqual true
    }

    "convert JNothing to null" in {
      YamlMethods.jvalue2java(JNothing) must beNull
    }

    "convert JNull to null" in {
      YamlMethods.jvalue2java(JNull) must beNull
    }

    "convert JString to string " in {
      YamlMethods.jvalue2java(JString("a string")) mustEqual "a string"
    }

    "convert JArray to java array" in {
      val arr = YamlMethods.jvalue2java(JArray(List(JInt(1), JInt(2), JNull)))
      arr must beAnInstanceOf[java.util.List[_]]

      val list = arr.asInstanceOf[java.util.List[_]]
      list must have size(3)
      list.get(0) mustEqual 1
      list.get(1) mustEqual 2
      list.get(2) must beNull
    }

    "convert JArray with a map inside to java array with a map inside" in {
      val arr = YamlMethods.jvalue2java(JArray(
        List(
          JObject(
            "an_int" -> JInt(1),
            "a_double" -> JDouble(1.2),
            "a_string" -> JString("str")),
          JInt(2),
          JObject(Nil),
          JNull)
        )
      )
      arr must beAnInstanceOf[java.util.List[_]]

      val list = arr.asInstanceOf[java.util.List[_]]
      list must have size(4)

      list.get(0).isInstanceOf[java.util.Map[_, _]] mustEqual true
      val map1 = list.get(0).asInstanceOf[java.util.Map[String, _]]
      map1.get("an_int") mustEqual 1
      map1.get("a_double") mustEqual 1.2
      map1.get("a_string") mustEqual "str"

      list.get(1) mustEqual 2

      list.get(2).isInstanceOf[java.util.Map[_, _]] mustEqual true
      list.get(2).asInstanceOf[java.util.Map[String, _]].size mustEqual 0

      list.get(3) must beNull
    }

    "convert JObject to java map" in {
      val obj = YamlMethods.jvalue2java(JObject(
        "int1" -> JInt(1),
        "int2" -> JInt(2),
        "null_value" -> JNull)
      )

      obj must beAnInstanceOf[java.util.Map[String, _]]
      val map = obj.asInstanceOf[java.util.Map[String, _]]
      map.size mustEqual 3
      map.get("int1") mustEqual 1
      map.get("int2") mustEqual 2
      map.get("null_value") must beNull

    }

  }

}
