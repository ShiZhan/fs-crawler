object XSDdateTime {
  import java.util.{ Calendar, Date, TimeZone }
  import java.text.SimpleDateFormat
  import com.hp.hpl.jena.datatypes.xsd.XSDDateTime

  private val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
  private val formatDate = new SimpleDateFormat("yyyy-MM-dd")

  private val calendar = Calendar.getInstance

  private def _xsd = new XSDDateTime(calendar) toString

  def now = _xsd

  def xsd(d: Date) = { calendar.setTime(d); _xsd }
  def xsd(i: Long) = { calendar.setTimeInMillis(i); _xsd }

  private def toXSD11(utc: String) =
    "%s:%s".format(utc.substring(0, 22), utc.substring(22))

  def get = toXSD11(format.format(calendar.getTime))
  def get(d: Date) = toXSD11(format.format(d))
  def get(i: Long) = toXSD11(format.format(i))

  def getDate = formatDate.format(calendar.getTime)

  def main(args: Array[String]) = {
    println("XSDdateTime:      " + get)
    println("date:             " + getDate)
    println("Jena XSDDateTime: " + now)
    println("Jena XSDDateTime: " + xsd(1366126807301L))
  }
}