import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
 
public class ToneDisplay extends JComponent {
 
    BufferedImage clefImage;
 
    @Override
    public void paintComponent(Graphics g) {
 
	// Cast Graphics2D object from method argument
	Graphics2D g2 = (Graphics2D) g;
	
	// Allow G2 to render with AntiAliasing (makes things smoother)
	RenderingHints rh = new RenderingHints(
             RenderingHints.KEY_TEXT_ANTIALIASING,
             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	g2.setRenderingHints(rh);
	
	// get the needed information from ToneGenerator
	double lowestFreq = ToneGenerator.getFrequencyOfTone(ToneGenerator.lowestTone);
	double highestFreq = ToneGenerator.getFrequencyOfTone(ToneGenerator.highestTone);
	double frequency = ToneGenerator.currentFrequency;
	double volume = ToneGenerator.currentVolume;
 
	int heightOfDrawArea = this.getHeight() - 50;
 
	// Paint  background
	GradientPaint blackToGray = new GradientPaint(0, 0, Color.WHITE, 0, 500, Color.LIGHT_GRAY);
	g2.setPaint(blackToGray);
	g2.fillRect(0, 0, this.getWidth(), this.getHeight());
 
	// Paint the Tone lines
	int lineWidth = this.getWidth() - 60;
	Stroke stroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
	g2.setStroke(stroke);
	g2.setColor(Color.DARK_GRAY);
 
	// Get all 88 freqs...
	for (String note : ToneBean.toneMap.keySet()) {
 
	    double freqForNote = ToneBean.toneMap.get(note);
 
	    // ...But only draw the freqs that are between the highest / lowest tones
	    if (freqForNote + 1 > lowestFreq && freqForNote - 1 < highestFreq) {
 
		// Determine the correct Y position to draw each line
		double percentageOfTotalHeight = ((freqForNote - lowestFreq) / (highestFreq - lowestFreq));
		double yOffset = (1 - percentageOfTotalHeight);
		double yPos = yOffset * heightOfDrawArea + 30;
 
		g2.drawString(Double.toString(freqForNote), 10, (int) yPos - 8);
		g2.drawLine(70, (int) yPos - 8, lineWidth, (int) yPos - 8);
		g2.drawString(note, lineWidth, (int) yPos - 8);
	    }
	}
 
	// Draw slider frequency. Line thickness is determines by volume
 
	g2.setColor(Color.decode("#2FB576"));
 
	double thickness = 2 + volume * 10;
	stroke = new BasicStroke((float) thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
	g2.setStroke(stroke);
 
	int locationOfBaseNote = heightOfDrawArea + 23;
	double offsetMultiplier = ((frequency - lowestFreq) / (highestFreq - lowestFreq));
 
	int yPos = locationOfBaseNote - (int) (offsetMultiplier * heightOfDrawArea);
 
	g2.drawLine(60, yPos, lineWidth, yPos);
 
    }
 
}