package com.grandata

import org.json4s.{Formats, DefaultFormats, JValue, JsonInput}

/**
 * Created by gustavo on 15/08/15.
 */
package object yaml {
  def parseJson(in: JsonInput, useBigDecimalForDouble: Boolean = false): JValue =
    YamlMethods.parse(in, useBigDecimalForDouble)

  def parseJsonOpt(in: JsonInput, useBigDecimalForDouble: Boolean = false): Option[JValue] =
    YamlMethods.parseOpt(in, useBigDecimalForDouble)

  def renderJValue(value: JValue)(implicit formats: Formats = DefaultFormats): JValue =
    YamlMethods.render(value)(formats)

  def compactJson(d: JValue): String = YamlMethods.compact(d)

  def prettyJson(d: JValue): String = YamlMethods.pretty(d)
}
