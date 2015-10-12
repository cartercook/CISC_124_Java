import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * TODO: implement drag function
 * @author Carter
 * @author Alan McLeod
 *
 */
class MandelbrotPanel extends JPanel {
			
			private final double ESCAPE_MODULUS = 2.0;
			private final int MAX_ITERATIONS = 32;
			private final double xyOrigin = -2.24;
			private final double absLength = 2.26 - xyOrigin; //2.26 = x&yMax
			private double xMin, yMin;
			private double xyLength;
			private double zoom;
			
			class ClickListener extends MouseAdapter {
				public void mouseReleased(MouseEvent e) {
					Boolean zoomOut = false;
				    if(SwingUtilities.isRightMouseButton(e))
				        zoomOut = true;
				    MandelbrotPanel.this.zoom(e.getX(), e.getY(), zoomOut);
				}
			}
			
			public MandelbrotPanel() {
				super();
				xMin = yMin = xyOrigin;
				xyLength = absLength;
				zoom = 1;
				setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				this.setToolTipText("click to zoom");
				addMouseListener(new ClickListener());
			}
			
			public void paintComponent(Graphics g) {
				//super.paintComponent(g) is not necessary.
				Graphics2D g2D = (Graphics2D) g;
				drawMandelbrot(g2D);
			}

			/**
			 * Draws the Mandelbrot figure according to the current draw panel size
			 * and x and y axis limits
			 */
			private void drawMandelbrot(Graphics2D g2D) {
				double xPos, yPos;
				ComplexNumber c, z;
				double modulus = 0;
				boolean escaped = false;
				int iterations = 0;
				int desiredColour;
				// Calculate the scale factor to go from pixels to actual units
				int height = this.getHeight(); // the JPanel drawing area
				int width = this.getWidth();
				double xScale = xyLength / width;  // These are min and max values
				double yScale = xyLength / height; // in actual coordinates.
				
				BufferedImage pretty = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

				// Iterate through the entire panel, pixel by pixel
				for (int row = 0; row < height; row++) {
					// Calculate the actual y position
					yPos = yMin + row * yScale;
					for (int col = 0; col < width; col++) {
						// Calculate the actual x position
						xPos = xMin + col * xScale;
						// Create the complex number for this position
						c = new ComplexNumber(xPos, yPos);
						z = new ComplexNumber(0, 0);
						iterations = 0;
						// Iterate the fractal equation z = z*z + c
						// until z either escapes or the maximum number of iterations
						// is reached
						do {
							z = (z.multiply(z)).add(c);
							modulus = z.abs();
							escaped = modulus > ESCAPE_MODULUS;
							iterations++;
						} while (iterations < MAX_ITERATIONS && !escaped);
						// Set the colour according to what stopped the above loop
						if (escaped) {
							desiredColour = setEscapeColour(iterations);
						} else {
							desiredColour = setColour(modulus);
						}
						pretty.setRGB(col, row, desiredColour);

					} // end for
				} // end for
				g2D.drawImage(pretty, null, 0, 0);

			} // end drawMandelbrot

			/**
			 * Sets gray level for escape situation
			 */
			private int setEscapeColour(int numIterations) {
				float grayLevel = 1.0F - (float) numIterations / MAX_ITERATIONS;
				grayLevel = Math.max(grayLevel, 0.1F);
				return new Color(grayLevel, grayLevel, grayLevel).getRGB();
			} // end setEscapeColour

			/**
			 * Sets colour level for interior situation
			 * The algorithm used here is *totally* empirical!
			 */
			private int setColour(double modulus) {
				float factor = (float) (modulus / ESCAPE_MODULUS);
				float incr = (float) Math.log10(factor * 3.5);
				float b = Math.min(Math.abs(0.5F * factor + incr), 1.0F);
				float r = Math.min(Math.abs(8.0F * incr) * factor, 1.0F);
				float g = Math.min(Math.abs(6.0F * incr) * factor, 1.0F);
				return new Color(r, g, b).getRGB();
			} // end setColour
			
			/**
			 * convert on-window x value to fractal value between xMin and xMin+xyLength
			 * @param panelX value from between 0 and this.getWidth();
			 * @return the fractal x value used to calculate the pixel colour at that spot.
			 */
			public double getFractalX(int panelX) {	
				return xMin + ((double)panelX/this.getWidth()) * xyLength;
			}
			
			/**
			 * convert on-window y value to fractal value between yMin and yMin+xyLength
			 * @param panelY value from between 0 and this.getHeight();
			 * @return the fractal y value used to calculate the pixel colour at that spot.
			 */
			public double getFractalY(int panelY) {
				return yMin + ((double)panelY/this.getHeight()) * xyLength;
			}
			
			/**
			 * zoom away from/towards an area on the MandelbrotPanel.
			 * @param x the x position, in panel coordinates, to zoom in to.
			 * @param y the y position, in panel coordinates, to zoom in to.
			 * @param out zooms out if true, in if false.
			 */
			public void zoom(int x, int y, boolean out) {
				xMin = getFractalX(x);
				yMin = getFractalY(y);
				if (out) {
					this.setToolTipText(null); //the user knows how it's done now, erase help message.
					zoom /= 2; //zooms by powers of 2
				} else {
					if (zoom == 1)
						this.setToolTipText("right click to zoom out"); //now that they know how to zoom in, teach them how to zoom out
					zoom *= 2;
				}
				xyLength = absLength/zoom;
				xMin -= xyLength / 2; //zoom towards the center of the click, rather than
				yMin -= xyLength / 2; //setting the click to be the top-left of the new draw area.
				this.repaint(); //redraw or else the onscreen pixels will not change.
			}
			
			/**
			 * center the fractal and reset instructional ToolTip messages.
			 */
			public void resetZoom() {
				xMin = yMin = xyOrigin;
				zoom = 1;
				xyLength = absLength;
				this.repaint();
				
				this.setToolTipText("click to zoom");
			}
			
			public double getZoom() {
				return zoom;
			}
			
		}