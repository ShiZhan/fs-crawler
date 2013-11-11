/**
 * XML Schema dateTime
 */
package util

import java.util.{ Calendar, Date }
import java.text.SimpleDateFormat

/**
 * @author ShiZhan
 * according to http://www.w3.org/TR/xmlschema-2/#dateTime
 */
object DateTime {

  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
  private val dateFormatFull = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
  def get = dateFormat.format(Calendar.getInstance.getTime)
  def get(d: Date) = dateFormat.format(d)
  def get(i: Long) = dateFormat.format(i)
  def getFull = dateFormatFull.format(Calendar.getInstance.getTime)

}