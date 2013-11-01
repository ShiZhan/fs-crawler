object linechart {
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

object multilinechart {
  import scalax.chart.Charting._
  import org.jfree.ui.RectangleEdge
  import java.awt.Color

  def main(args: Array[String]) = {
    val dataA = Seq((1, 2), (2, 4), (3, 6), (4, 8), (5, 10))
    val dsA = dataA.toXYSeries("A")
    val dataB = Seq((1, 10), (2, 8), (3, 6), (4, 4), (5, 2))
    val dsB = dataB.toXYSeries("B")
    val dataset = List(dsA, dsB).toXYSeriesCollection

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
      0.0f, 0.0f, new Color(0, 0, 196))
    val render = plot.getRenderer
    (0 to 4) foreach (i => render.setSeriesPaint(i, gp0))
    bChart.show
    bChart.saveAsPDF("r:/chart.pdf", (800, 600))
  }
}

object multibarchart {
  import scalax.chart.Charting._
  import org.jfree.ui.RectangleEdge
  import java.awt.{ Color, GradientPaint }

  def main(args: Array[String]) = {
    val data = List(("C1", "A", 3), ("C1", "B", 4), ("C1", "C", 3),
      ("C2", "A", 7), ("C2", "B", 8), ("C2", "C", 8))
    val dataset = data.toCategoryDataset
    val bChart = BarChart(
      dataset,
      title = "The chart of bar comparison",
      domainAxisLabel = "X",
      rangeAxisLabel = "Y")
    val jfc = bChart.peer
    jfc.getLegend.setPosition(RectangleEdge.RIGHT)
    jfc.backgroundPaint = Color.white
    val plot = bChart.plot
    plot.setBackgroundPaint(Color.white)
    plot.setRangeGridlinePaint(Color.blue)
    val render = plot.getRenderer
    List((0, Color.blue), (1, Color.green), (2, Color.yellow)) foreach {
      case (i, c) => render.setSeriesPaint(i,
        new GradientPaint(0.0f, 0.0f, c, 0.0f, 0.0f, Color.white))
    }
    bChart.show
    bChart.saveAsPDF("r:/chart.pdf", (800, 600))
  }
}
