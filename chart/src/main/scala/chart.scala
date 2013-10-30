object chart {
  import scalax.chart.Charting._
  import org.jfree.chart.StandardChartTheme

  def main(args: Array[String]) = {
    implicit val theme = StandardChartTheme.createLegacyTheme
    val data = Seq((1, 2), (2, 4), (3, 6), (4, 8), (5, 10))
    val dataset = data.toXYSeriesCollection("some points")
    val lineChart = XYLineChart(
      dataset,
      title = "My Chart of Some Points",
      domainAxisLabel = "X",
      rangeAxisLabel = "Y")
    lineChart.backgroundPaint = new Color(255, 255, 255)
    lineChart.show
    lineChart.saveAsPDF("r:/chart.pdf", (1024,768))
  }
}
