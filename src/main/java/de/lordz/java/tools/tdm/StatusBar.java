package de.lordz.java.tools.tdm;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * StatusBar component based on JLabel.
 * 
 * @author lordz
 *
 */
public class StatusBar extends JPanel {

	private static final long serialVersionUID = -8756103519005749873L;
	private JLabel statusLabel;
	
    /** Creates a new instance of StatusBar */
    private StatusBar(int width, int height) {
        super();
        super.setPreferredSize(new Dimension(width, height));
//        setBorder(new BevelBorder(BevelBorder.RAISED));
//        setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }

    public void setMessage(String message) {
    	this.statusLabel.setText(message);
    }
        
    public static StatusBar addStatusbar(JFrame frame) {
    	var statusBar = new StatusBar(frame.getWidth(), 16);
    	statusBar.statusLabel = new JLabel("status");
    	statusBar.statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
    	statusBar.add(statusBar.statusLabel);
    	frame.getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);
    	return statusBar;
    }
    
}