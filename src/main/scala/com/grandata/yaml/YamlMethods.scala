package com.grandata.yaml

/**
 * Created by gustavo on 15/08/15.
 */

import java.io.{FileReader, InputStreamReader, StringReader}
import java.util

import com.sun.corba.se.impl.orbutil.ObjectWriter
import org.json4s._
import org.yaml.snakeyaml.{ Yaml }
import org.yaml.snakeyaml.nodes.{ScalarNode, Node}
import scala.util.DynamicVariable
import scala.util.control.Exception.allCatch

// Warning: SnakeYaml is not thread safe, so this object is neither.
trait YamlMethods extends org.json4s.JsonMethods[JValue] {

  protected[this] def yaml: Yaml

  override def pretty(node: JValue): String = {
    val javaObj = jvalue2java(node)
    yaml.dump(javaObj)
  }

  private[yaml] def jvalue2java(node: JValue): AnyRef = {
    import scala.collection.JavaConversions._
    node match {
      case JNull => null
      case JArray(l) => seqAsJavaList(l.map(jvalue2java))
      case JInt(i) =>
        if (i.isValidInt) Int.box(i.toInt)
        else i.bigInteger
      case JBool(b) => Boolean.box(b)
      case JDecimal(d) => d
      case JDouble(d) => Double.box(d)
      case JNothing => null
      case JString(s) => s
      case JObject(l) => mapAsJavaMap(l.toMap.mapValues(jvalue2java))
    }
  }

  override def render(value: JValue)(implicit formats: Formats = DefaultFormats): JValue =
      formats.emptyValueStrategy.replaceEmpty(value)

  override def compact(d: JValue): String = {
    import org.json4s.native.{ renderJValue, compactJson }
    compactJson(renderJValue(d))
  }

  override def parseOpt(in: JsonInput, useBigDecimalForDouble: Boolean): Option[JValue] = allCatch opt {
    parse(in, useBigDecimalForDouble)
  }

  def parse(in: JsonInput, useBigDecimalForDouble: Boolean = false): JValue = {
    import scala.collection.JavaConversions._

    // TODO: implement using parser to avoid traversing the tree thre times.
//    val parseEvents = in match {
//      case StringInput(s) =>  yaml.parse(new StringReader(s))
//      case ReaderInput(rdr) => yaml.parse(rdr)
//      case StreamInput(stream) => yaml.parse(new InputStreamReader(stream))
//      case FileInput(file) => yaml.parse(new FileReader(file))
//    }
//
//    parseEvents.iterator.toStream.map { event =>
//      event match {
//        StreamStartEvent
//      }
//    }

    // TODO: at least implement using composer to avoid traversing the twice.
//    val tree = in match {
//      case StringInput(s) =>  yaml.compose(new StringReader(s))
//      case ReaderInput(rdr) => yaml.compose(rdr)
//      case StreamInput(stream) => yaml.compose(new InputStreamReader(stream))
//      case FileInput(file) => yaml.compose(new FileReader(file))
//    }
//

    // WARNING: Yaml.load() accepts a String or an InputStream object. Yaml.load(InputStream stream) detects the encoding by checking the BOM (byte order mark) sequence at the beginning of the stream. If no BOM is present, the utf-8 encoding is assumed.
    // WARNING: Right now I cannot distinguish between null and nothing.

    val tree = in match {
        case StringInput(s) =>  yaml.load(new StringReader(s))
        case ReaderInput(rdr) => yaml.load(rdr)
        case StreamInput(stream) => yaml.load(new InputStreamReader(stream))
        case FileInput(file) => yaml.load(new FileReader(file))
      }

    getJValue(tree)(useBigDecimalForDouble)
  }

  private def getJValue(node: Object)(implicit useBigDecimalForDouble: Boolean): JValue = {
    import scala.collection.JavaConversions._
    node match {
      case null => JNull

      case l: java.util.List[_] =>
        JArray(l.toList.map { v =>
          getJValue(v.asInstanceOf[AnyRef])
        })

      case m: java.util.Map[_, _] =>
        val pairs = m.map { case (k, v) =>
          (k.asInstanceOf[String], getJValue(v.asInstanceOf[AnyRef]))
        }.toList
        JObject(pairs)

      case i: Integer =>
        JInt(BigInt(i))

      case f: java.lang.Double =>
        if (useBigDecimalForDouble) JDecimal(BigDecimal(f))
        else JDouble(f)

      case f: java.lang.Float =>
        if (useBigDecimalForDouble) JDecimal(BigDecimal.decimal(f))
        else JDouble(f.toDouble)

      case b: java.lang.Boolean =>
        JBool(b)

      case i: java.math.BigInteger =>
        JInt(BigInt(i))
    }
  }

//
//
//  def compact(d: JValue): String = mapper.writeValueAsString(d)
//
//  def pretty(d: JValue): String = {
//    val writer = mapper.writerWithDefaultPrettyPrinter[ObjectWriter]()
//    writer.writeValueAsString(d)
//  }
//
//
//  def asJValue[T](obj: T)(implicit writer: Writer[T]): JValue = writer.write(obj)
//  def fromJValue[T](json: JValue)(implicit reader: Reader[T]): T = reader.read(json)
//
//  def asJsonNode(jv: JValue): JsonNode = mapper.valueToTree[JsonNode](jv)
//  def fromJsonNode(jn: JsonNode): JValue = mapper.treeToValue[JValue](jn, classOf[JValue])

}

object YamlMethods extends YamlMethods {

  // This is because yaml is not thread safe
  private val threadYaml: java.lang.ThreadLocal[Yaml] = new java.lang.ThreadLocal[Yaml] {
    override protected def initialValue() = new Yaml()
  }

  override protected def yaml = threadYaml.get
}