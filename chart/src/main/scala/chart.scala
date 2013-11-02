object linechart {
  import scalax.chart.Charting._
  import org.jfree.ui.RectangleEdge
  import java.awt.Color

  def main(args: Array[String]) = {
    val data = Seq((1, 2), (2, 4), (3, 6), (4, 8), (5, 10))
    val dataset = data.toXYSeriesCollection("some points")
    val lChart = XYLineChart(
      dataset,
      title = "Example Chart",
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
    lChart.saveAsPDF("r:/linechart.pdf", (500, 375))
  }
}

object multilinechart {
  import scalax.chart.Charting._
  import org.jfree.ui.RectangleEdge
  import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
  import java.awt.Color

  def main(args: Array[String]) = {
    val dataA = Seq((1, 2), (2, 4), (3, 6), (4, 8), (5, 10))
    val dsA = dataA.toXYSeries("Line A")
    val dataB = Seq((1, 10), (2, 8), (3, 6), (4, 4), (5, 2))
    val dsB = dataB.toXYSeries("Line B")
    val dataset = List(dsA, dsB).toXYSeriesCollection
    val lChart = XYLineChart(
      dataset,
      domainAxisLabel = "X",
      rangeAxisLabel = "Y")
    val jfc = lChart.peer
    jfc.getLegend.setPosition(RectangleEdge.RIGHT)
    jfc.backgroundPaint = Color.white
    val plot = lChart.plot
    plot.setBackgroundPaint(Color.white)
    plot.setDomainGridlinePaint(Color.lightGray)
    plot.setRangeGridlinePaint(Color.lightGray)
    plot.setRenderer(new XYLineAndShapeRenderer)
    lChart.show
    lChart.saveAsPDF("r:/multilinechart.pdf", (500, 375))
  }
}

object barchart {
  import scalax.chart.Charting._
  import org.jfree.ui.RectangleEdge
  import java.awt.Color

  def main(args: Array[String]) = {
    val data = Seq(
      ("A", "s0", 1), ("B", "s0", 3), ("C", "s0", 5),
      ("D", "s0", 7), ("E", "s0", 9))
    val dataset = data.toCategoryDataset
    val bChart = BarChart(
      dataset,
      title = "Example Chart of Some Bars",
      domainAxisLabel = "X",
      rangeAxisLabel = "Y")
    val jfc = bChart.peer
    jfc.getLegend.setPosition(RectangleEdge.RIGHT)
    jfc.backgroundPaint = Color.white
    val plot = bChart.plot
    plot.setBackgroundPaint(Color.white)
    plot.setRangeGridlinePaint(Color.blue)
    plot.setRenderer(util.pattern.Renderer)
    bChart.show
    bChart.saveAsPDF("r:/barchart.pdf", (500, 375))
  }
}

object multibarchart {
  import scalax.chart.Charting._
  import org.jfree.ui.RectangleEdge
  import java.awt.Color

  def main(args: Array[String]) = {
    val data = List(
      ("C1", "A", 3), ("C1", "B", 4), ("C1", "C", 3),
      ("C2", "A", 7), ("C2", "B", 8), ("C2", "C", 8))
    val dataset = data.toCategoryDataset
    val bChart = BarChart(
      dataset,
      domainAxisLabel = "X",
      rangeAxisLabel = "Y")
    val jfc = bChart.peer
    jfc.getLegend.setPosition(RectangleEdge.RIGHT)
    jfc.backgroundPaint = Color.white
    val plot = bChart.plot
    plot.setBackgroundPaint(Color.white)
    plot.setRangeGridlinePaint(Color.lightGray)
    plot.setRenderer(util.pattern.Renderer)
    bChart.show
    bChart.saveAsPDF("r:/multibarchart.pdf", (500, 375))
  }
}