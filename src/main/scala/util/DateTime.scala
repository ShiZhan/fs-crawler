/**
 * XML Schema dateTime
 */
package util

import java.util.{ Calendar, Date, TimeZone }
import java.text.SimpleDateFormat

/**
 * @author ShiZhan
 * according to:
 * http://www.w3.org/TR/xmlschema-2/#dateTime
 * http://www.w3.org/TR/xmlschema11-2/#dateTimeStamp
 */
object DateTime {
  private val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
  private val formatDate = new SimpleDateFormat("yyyy-MM-dd")
  private val calendar = Calendar.getInstance

  private def toXSD11(utc: String) =
    "%s:%s".format(utc.substring(0, 22), utc.substring(22))

  def get = toXSD11(format.format(calendar.getTime))
  def get(d: Date) = toXSD11(format.format(d))
  def get(i: Long) = toXSD11(format.format(i))
  
  def getDate = formatDate.format(calendar.getTime)
}