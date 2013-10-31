object xylinechart {
  import scalax.chart.Charting._
  import org.jfree.chart.StandardChartTheme
  import org.jfree.ui.RectangleEdge
  import java.awt.{ Font, Color }

  def main(args: Array[String]) = {
    implicit val theme = new StandardChartTheme("generic")
    theme.setExtraLargeFont(new Font("Courier New", Font.PLAIN, 20))
    theme.setLargeFont(new Font("Courier New", Font.PLAIN, 14))
    theme.setRegularFont(new Font("Courier New", Font.PLAIN, 12))
    theme.setSmallFont(new Font("Courier New", Font.PLAIN, 10))
    theme.setPlotBackgroundPaint(Color.white)
    theme.setDomainGridlinePaint(Color.blue)
    theme.setRangeGridlinePaint(Color.blue)

    val data = Seq((1, 2), (2, 4), (3, 6), (4, 8), (5, 10))
    val dataset = data.toXYSeriesCollection("some points")
    val lineChart = XYLineChart(
      dataset,
      title = "Chart",
      domainAxisLabel = "X",
      rangeAxisLabel = "Y")
    lineChart.backgroundPaint = Color.white
    lineChart.peer.getLegend.setPosition(RectangleEdge.RIGHT)
    lineChart.show
    lineChart.saveAsPDF("r:/chart.pdf", (800, 600))
  }
}

object barchart {
  import scalax.chart.Charting._
  import org.jfree.chart.StandardChartTheme

  def main(args: Array[String]) = {
    implicit val theme = StandardChartTheme.createLegacyTheme
    val data = Seq((1, 2), (2, 4), (3, 6), (4, 8), (5, 10))
    val dataset = data.toCategoryDataset
    val lineChart = BarChart(
      dataset,
      title = "My Chart of Some Points",
      domainAxisLabel = "X",
      rangeAxisLabel = "Y")
    lineChart.backgroundPaint = new Color(255, 255, 255)
    lineChart.show
    lineChart.saveAsPDF("r:/chart.pdf", (1024, 768))
  }
}
