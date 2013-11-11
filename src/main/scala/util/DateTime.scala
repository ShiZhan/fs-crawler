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
  private val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
  private val formatDate = new SimpleDateFormat("yyyy-MM-dd")
  private val formatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
  def get = format.format(Calendar.getInstance.getTime)
  def get(d: Date) = format.format(d)
  def get(i: Long) = format.format(i)
  def getDate = formatDate.format(Calendar.getInstance.getTime)
  def getUTC = formatUTC.format(Calendar.getInstance.getTime)
}