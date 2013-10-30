object chart {
  import org.jfree.chart.ChartFactory
  import org.jfree.chart.ChartUtilities
  import org.jfree.chart.JFreeChart
  import org.jfree.data.general._
  import org.jfree.data._
  import org.jfree.chart.ChartPanel
  import org.jfree.chart.plot.PiePlot

  import javax.swing.JFrame
  import javax.swing.JPanel

  def main(args: Array[String]) = {
    val pieDataset = new DefaultPieDataset
    pieDataset.setValue("A", 75)
    pieDataset.setValue("B", 10)
    pieDataset.setValue("C", 10)
    pieDataset.setValue("D", 5)
    val chart = ChartFactory.createPieChart(
      "Hello World",
      pieDataset,
      true,
      true,
      false)

    println("Hello world")

    val frame = new JFrame("Hello Pie World")
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE )
    frame.setSize(640, 420)
    frame.add( new ChartPanel(chart) )
    frame.pack()
    frame.setVisible(true)
  }
}
