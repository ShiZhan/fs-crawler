/**
 * XML Schema dateTime
 */
package util

import java.util.{ Calendar, Date }
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime

/**
 * @author ShiZhan
 * according to:
 * http://www.w3.org/TR/xmlschema-2/#dateTime
 * http://www.w3.org/TR/xmlschema11-2/#dateTimeStamp
 */
object DateTime {
  private val calendar = Calendar.getInstance
  private def _xsdDT = new XSDDateTime(calendar) toString

  def get = _xsdDT
  def get(d: Date) = { calendar.setTime(d); _xsdDT }
  def get(i: Long) = { calendar.setTimeInMillis(i); _xsdDT }
}