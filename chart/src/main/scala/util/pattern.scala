/**
 * pattern filling for rendering in gray scale graphics
 */
package util

/**
 * @author ShiZhan
 * available patterns:
 * 0: '\', 1: '_', 2: '/', 3: '|', 4: '.', 5: '-'
 */
object pattern {
  import java.awt.{ Color, Rectangle, TexturePaint }
  import java.awt.image.BufferedImage
  import org.jfree.chart.renderer.category.{ BarRenderer, StandardBarPainter }

  val Fill = Array(
    (0, 0, 5, 5), (0, 0, 5, 0), (0, 5, 5, 0),
    (0, 0, 0, 5), (0, 0, 0, 0), (1, 1, 3, 3)) map {
      case (x1, y1, x2, y2) => {
        val bi = new BufferedImage(5, 5, BufferedImage.TYPE_BYTE_GRAY)
        val big = bi.createGraphics
        big.setColor(Color.white)
        big.fillRect(0, 0, 5, 5)
        big.setColor(Color.black)
        big.drawLine(x1, y1, x2, y2)
        val ir = new Rectangle(0, 0, 5, 5)
        new TexturePaint(bi, ir)
      }
    }

  class Renderer extends BarRenderer {
    setBarPainter(new StandardBarPainter)
    setShadowVisible(false)
    setDrawBarOutline(true)
    (0 to Fill.length - 1) foreach (i => setSeriesPaint(i, Fill(i)))
  }
}