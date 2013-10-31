object xylinechart {
  import scalax.chart.Charting._
  import org.jfree.ui.RectangleEdge
  import java.awt.Color

  def main(args: Array[String]) = {
    val data = Seq((1, 2), (2, 4), (3, 6), (4, 8), (5, 10))
    val dataset = data.toXYSeriesCollection("some points")
    val lChart = XYLineChart(
      dataset,
      title = "Chart",
      domainAxisLabel = "X",
      rangeAxisLabel = "Y")
    val jfc = lChart.peer
    jfc.getLegend.setPosition(RectangleEdge.RIGHT)
    jfc.backgroundPaint = Color.white
    val plot = lChart.plot
    plot.setBackgroundPaint(Color.white)
    plot.setDomainGridlinePaint(Color.blue)
    plot.setRangeGridlinePaint(Color.blue)
    lChart.show
    lChart.saveAsPDF("r:/chart.pdf", (800, 600))
  }
}

object barchart {
  import scalax.chart.Charting._
  import org.jfree.ui.RectangleEdge
  import java.awt.{ Color, GradientPaint }

  def main(args: Array[String]) = {
    val data = Seq((1, 4), (2, 4), (3, 6), (4, 8), (5, 10))
    val dataset = data.toCategoryDataset
    val bChart = BarChart(
      dataset,
      title = "My Chart of Some Points",
      domainAxisLabel = "X",
      rangeAxisLabel = "Y")
    val jfc = bChart.peer
    jfc.getLegend.setPosition(RectangleEdge.RIGHT)
    jfc.backgroundPaint = Color.white
    val plot = bChart.plot
    plot.setBackgroundPaint(Color.white)
    plot.setRangeGridlinePaint(Color.blue)
    val gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
      0.0f, 0.0f, new Color(0, 0, 64))
    val render = plot.getRenderer
    render.setSeriesPaint(0, gp0)
    render.setSeriesPaint(1, gp0)
    render.setSeriesPaint(2, gp0)
    render.setSeriesPaint(3, gp0)
    render.setSeriesPaint(4, gp0)
    bChart.show
    bChart.saveAsPDF("r:/chart.pdf", (800, 600))
  }
}
