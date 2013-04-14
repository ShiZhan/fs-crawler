/**
 * XML Schema dateTime
 */
package util

import java.util.Calendar
import java.text.SimpleDateFormat

/**
 * @author ShiZhan
 * according to http://www.w3.org/TR/xmlschema-2/#dateTime
 */
object DateTime {

  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

  def get = dateFormat.format(Calendar.getInstance.getTime)

}