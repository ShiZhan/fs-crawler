object chart {
  import scalax.chart._
  import scalax.chart.Charting._

  def main(args: Array[String]) = {
    val data    = Seq((1,2),(2,4),(3,6),(4,8))
    val dataset = data.toXYSeriesCollection("some points")
    val chart = XYLineChart(dataset, title = "My Chart of Some Points")
    chart.show
    chart.saveAsPDF("r:/chart.pdf", (1024,768))
  }
}
