import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * the JFrame containing all essential components, including the main() function.
 * @author Carter
 */
public class Assn4_13cjcc extends JFrame {
	
	public Assn4_13cjcc() {
		this.setLayout(new BorderLayout());
		this.setSize(500, 500); //set shrunken size
		this.setExtendedState(MAXIMIZED_BOTH); //maximize window
		this.setDefaultCloseOperation(EXIT_ON_CLOSE); //or else the program won't stop running
		this.setTitle("Fractal Got Back");
		setIconImage(new ImageIcon("fractalIcon.png").getImage());
		this.setVisible(true);
		
		MandelbrotPanel drawPanel = new MandelbrotPanel();
	    this.add(BorderLayout.CENTER, drawPanel);
		
	    /**
	     * displays the x and y values used to draw the fractal over which the mouse is hovering.
	     * @author Carter
	     */
		class XYLabel extends JLabel {
			public XYLabel() {
				this.setText("z = z*z + c    c = 0.000000 + 0.000000i");
			    drawPanel.addMouseMotionListener(new MouseListener());
			}
			
			/**
			 * update the JLabel's text whenever the mouse is moved. This class must be added to the MandelbrotPanel.
			 * @author Carter
			 */
			class MouseListener extends MouseAdapter {
				public void mouseMoved(MouseEvent e) {
					//requires access to an instance of MandelbrotPanel.
			        XYLabel.this.setText(String.format("z = z*z + c    c = %.6f + %.6fi", drawPanel.getFractalX(e.getX()), drawPanel.getFractalY(e.getY())));
			    }
			}
		}
		
		/**
		 * calls MandelBrotPanel's resetZoom function on click.
		 * @author Carter
		 */
		class ResetButton extends JButton {
			public ResetButton() {
				this.setText("Reset Zoom");
			    addMouseListener(new ClickListener());
			}
			
			class ClickListener extends MouseAdapter {
				public void mouseReleased(MouseEvent e) {
					drawPanel.resetZoom();
				}
			}
		}
	    
		JPanel toolBar = new JPanel();
		toolBar.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16)); //a nice uniform border between this panel and its contents
		toolBar.setLayout(new BorderLayout());
		this.add(BorderLayout.SOUTH, toolBar);
		
		toolBar.add(BorderLayout.WEST, new XYLabel());
		toolBar.add(BorderLayout.EAST, new ResetButton());
	}
	
	public static void main(String[] args) {
		new Assn4_13cjcc(); //instantiates the actual window
	}
	
}