/**
 * pattern filling for rendering in gray scale graphics
 */
package util

/**
 * @author ShiZhan
 * available patterns:
 * 0: '\'
 * 1: '-'
 * 2: '/'
 * 3: '|'
 */
object pattern {
  import java.awt.{Color, Rectangle, TexturePaint}
  import java.awt.image.BufferedImage
  import org.jfree.chart.renderer.category.{ BarRenderer, StandardBarPainter }

  val Fill = List((0, 0, 5, 5), (0, 0, 5, 0), (0, 5, 5, 0), (0, 0, 0, 5)) map {
    case (x1, y1, x2, y2) => {
      val bufferedImage = new BufferedImage(5, 5, BufferedImage.TYPE_BYTE_GRAY)
      val big = bufferedImage.createGraphics
      big.setColor(Color.white)
      big.fillRect(0, 0, 5, 5)
      big.setColor(Color.black)
      big.drawLine(x1, y1, x2, y2)
      val imageRect = new Rectangle(0, 0, 5, 5 )
      new TexturePaint(bufferedImage, imageRect)
    }
  } toArray

  val Renderer = new BarRenderer
  Renderer.setBarPainter(new StandardBarPainter)
  Renderer.setShadowVisible(false)
  Renderer.setDrawBarOutline(true)
  (0 to 2) foreach (i => Renderer.setSeriesPaint(i, Fill(i)))

}