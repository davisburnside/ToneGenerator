
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
 
public class ToneGenerator extends JFrame implements ActionListener, ChangeListener {
 
    // determines how many samples per second will be written to the SourceDataLine.
    protected final int audioSampleRate = 43200;
 
    // When true, this program will continuously write to the SourceDataLine 
    boolean playTone = true;
 
    static double currentVolume = 0.8;
    static double currentFrequency = 347;
 
    // These values are determined by the selection of the JComboBoxes
    static String lowestTone = "C3";
    static String highestTone = "C5";
 
    //This variable is used to allow each NEW wave buffer to start where the PREVIOUS buffer left off (Prevents pops and clicks in the audio)
    double angleOffsetToApply = 0;
 
    // The maximum value for both JSliders. 
    // The frequency slider should be more sensitive to change than the volume slider.
    int freqSliderRange = 512;
    int volumeSliderRange = 100;
 
    // This is the object that receives audio data and forwards it to the speakers. It has an audio buffer
    // which is being continuously written to inside the threadTask
    SourceDataLine sourceDataLine;
 
    // Allows the user to choose the highest and lowest possible tones
    JLabel label_highestTone = new JLabel("Highest Tone");
    JLabel label_lowestTone = new JLabel("Lowest Tone");
    JComboBox comboBox_highestTone = new JComboBox();
    JComboBox comboBox_lowestTone = new JComboBox();
 
    // Allows the user to change the frequency played and Volume
    JLabel label_currentFrequency;
    JSlider slider_currentFrequency;
    JLabel label_currentVolume;
    JSlider slider_currentVolume;
 
    // Displays the frequency range and currentFrequency
    ToneDisplay toneDisplay = new ToneDisplay();
 
    //========================================================================
    // First methods executed on startup, placed in call order
    //========================================================================
    
    public static void main(String[] args) throws LineUnavailableException {
 
	new ToneGenerator();
    }
 
    public ToneGenerator() {
 
	super();
 
	intitializeValues();
 
	setupGUI();
 
	setupSinWaveGenerator();
 
	addShutDownHook();
    }
 
    private void intitializeValues() {
 
	currentFrequency = getFrequencyOfTone(lowestTone);
	currentVolume = 0.25;
 
	for (String toneName : ToneBean.toneMap.keySet()) {
 
	    comboBox_highestTone.addItem(toneName);
	    comboBox_lowestTone.addItem(toneName);
	}
    }
 
    private void setupGUI() {
 
	// Setup the JFrame
	setVisible(true);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
	// Create a top-level JPanel to add all other components to
	JPanel masterPanel = new JPanel();
	BoxLayout masterLayout = new BoxLayout(masterPanel, BoxLayout.X_AXIS);
	masterPanel.setLayout(masterLayout);
	masterPanel.setBackground(Color.decode("#CADBC0"));
	this.add(masterPanel);
 
	// The JPanel that all control elements (Buttons, sliders, combo boxes) will be inside
	JPanel controlPanel = new JPanel();
	controlPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
	BoxLayout controlPanelLayout = new BoxLayout(controlPanel, BoxLayout.Y_AXIS);
	controlPanel.setLayout(controlPanelLayout);
	masterPanel.add(controlPanel);
 
	// Create the combo boxes used to select the tone range
	JPanel toneRangePanel = new JPanel();
	GridLayout tonePanelLayout = new GridLayout(2, 2, 10, 10);
	toneRangePanel.setLayout(tonePanelLayout);
 
	int indexOfHighestTone = getIndexOfTone(highestTone);
	int indexOfLowestTone = getIndexOfTone(lowestTone);
 
	comboBox_highestTone.setSelectedIndex(indexOfHighestTone);
	comboBox_lowestTone.setSelectedIndex(indexOfLowestTone);
	comboBox_highestTone.addActionListener(this);
	comboBox_lowestTone.addActionListener(this);
 
	toneRangePanel.add(label_lowestTone);
	toneRangePanel.add(label_highestTone);
	toneRangePanel.add(comboBox_lowestTone);
	toneRangePanel.add(comboBox_highestTone);
 
	controlPanel.add(toneRangePanel);
 
	// The Panel that hold the sliders for volume and frequency
	JPanel freqAndVolumePanel = new JPanel();
	BoxLayout toneBoxLayout = new BoxLayout(freqAndVolumePanel, BoxLayout.X_AXIS);
	freqAndVolumePanel.setLayout(toneBoxLayout);
 
	// Add the Panel, Slider, and Label for the frequency
	JPanel freqSelectionPanel = new JPanel();
	BoxLayout freqBoxLayout = new BoxLayout(freqSelectionPanel, BoxLayout.Y_AXIS);
	freqSelectionPanel.setLayout(freqBoxLayout);
 
	slider_currentFrequency = new JSlider(JSlider.VERTICAL, 0, freqSliderRange, 0);
	slider_currentFrequency.setAlignmentX(SwingConstants.CENTER);
	slider_currentFrequency.addChangeListener(this);
	freqSelectionPanel.add(slider_currentFrequency);
 
	label_currentFrequency = new JLabel();
	label_currentFrequency.setAlignmentX(SwingConstants.CENTER);
	freqSelectionPanel.add(label_currentFrequency);
 
	freqAndVolumePanel.add(freqSelectionPanel);
 
	// Add the Panel, Slider, and Label for the frequency
	JPanel volumeSelectionPanel = new JPanel();
	BoxLayout volumeBoxLayout = new BoxLayout(volumeSelectionPanel, BoxLayout.Y_AXIS);
	volumeSelectionPanel.setLayout(volumeBoxLayout);
 
	slider_currentVolume = new JSlider(JSlider.VERTICAL, 0, volumeSliderRange, (int) (currentVolume * 100f));
	slider_currentVolume.setAlignmentX(SwingConstants.CENTER);
	slider_currentVolume.addChangeListener(this);
	volumeSelectionPanel.add(slider_currentVolume);
 
	label_currentVolume = new JLabel();
	label_currentVolume.setAlignmentX(SwingConstants.CENTER);
	volumeSelectionPanel.add(label_currentVolume);
 
	freqAndVolumePanel.add(volumeSelectionPanel);
 
	controlPanel.add(freqAndVolumePanel);
 
	// Add the component that draws the tones to screen.
	toneDisplay.addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent e) {
		toneDisplay.repaint();
	    }
	});
	toneDisplay.setPreferredSize(new Dimension(300, 600));
	masterPanel.add(toneDisplay);
 
	updateVisuals();
 
	// Fit everything nicely inside the JFrame
	pack();
    }
 
    private void setupSinWaveGenerator() {
 
	Thread thread = new Thread(audioGeneratorThread());
	thread.start();
    }
 
    private void addShutDownHook() {
 
	Runtime.getRuntime().addShutdownHook(new Thread() {
 
	    public void run() {
 
		// Allows the audioGeneratorThread Thread to end when the program closes
		playTone = false;
 
		if (sourceDataLine != null) {
 
		    System.out.println("start drain");
		    sourceDataLine.drain();
		    System.out.println("end drain");
 
		    sourceDataLine.close();
		    System.out.println("line closed");
 
		} else {
 
		    System.out.println("SourceDataLine never initialized, nothing to close / drain");
		}
	    }
	});
    }
 
    //========================================================================
    // Main helper methods
    //========================================================================
 
    public static double getFrequencyOfTone(String toneName) {
 
	return ToneBean.toneMap.get(toneName);
    }
 
    public int getDistanceBetweenHighestAndLowestTones() {
 
	int numberOfPlayableTones = 0;
	int indexA = 0;
	int indexB = 0;
	for (String toneName : ToneBean.toneMap.keySet()) {
 
	    if (toneName.equals(lowestTone)) {
		break;
	    }
	    indexA++;
	}
	for (String toneName : ToneBean.toneMap.keySet()) {
 
	    if (toneName.equals(highestTone)) {
		break;
	    }
	    indexB++;
	}
	int numberOfTones = indexB - indexA;
	return numberOfTones;
    }
 
    double getFrequencyFromSliderValue(double sliderValue) {
 
	// This method takes advantage of the fact that each note's frequency 
	// is (2^(1/12)) times, (or about 1.059) higher than the previous note.
 
	double lowestFreq = getFrequencyOfTone(lowestTone);
	double numberOfTonesPerMeasure = 12;
	double numberOfPlayableTones = (double) getDistanceBetweenHighestAndLowestTones();
 
	// These variable changes LINEARLY as the slider moves.
	double numberOfPlayableMeasures = (numberOfPlayableTones) / numberOfTonesPerMeasure;
	double normalizedSliderValue = sliderValue / freqSliderRange;
	double power = numberOfPlayableMeasures * normalizedSliderValue;
 
	// This variables changes NONLINEARLY as the slider moves.	
	double freq = lowestFreq * Math.pow(2, power);
 
	return freq;
    }
 
    int getIndexOfTone(String inputToneName) {
 
	int index = 0;
	for (String toneName : ToneBean.toneMap.keySet()) {
 
	    if (toneName.equals(inputToneName)) {
 
		return index;
	    }
	    index++;
	}
 
	return -1;
    }
 
    void updateVisuals() {
 
	// Get the slider value in String form and truncate it if it's too long
	String StringValue = Double.toString(currentFrequency);
	if (StringValue.length() > 5) {
	    String truncatedValue = StringValue.substring(0, 5);
	    StringValue = truncatedValue;
	}
 
	// By using HTML tags inside the Button's label, you can show multiple lines of text.
	String textValue = "<html>Freq<br>" + StringValue + "</html>";
	label_currentFrequency.setText(textValue);
 
	textValue = "<html>Vol<br>" + Integer.toString((int) ((currentVolume) * 100f)) + "</html>";
	label_currentVolume.setText(textValue);
 
	toneDisplay.repaint();
    }
 
    //========================================================================
    // The Thread that generates audio data, and its helper method
    //========================================================================
   
    private Runnable audioGeneratorThread() {
 
	return () -> {
 
	    try {
 
		// how long (in milliseconds) the audio buffer will last before being filled again
		int lengthOfWaveBufferInMillis = 200;
 
		// How many individual audio samples will be inside the audio buffer
		int numberOfSamplesInWaveBuffer = (int) ((lengthOfWaveBufferInMillis * audioSampleRate) / 1000);
 
		// Create an AudioFormat object, which will tell the sourceDataLine how to interpet the Stream of bytes
		AudioFormat af = new AudioFormat(audioSampleRate, 16, 1, true, true);
 
		// Initialize the sourceDataLine and open a data Stream to it.
		sourceDataLine = AudioSystem.getSourceDataLine(af);
		sourceDataLine.open(af, numberOfSamplesInWaveBuffer * 2);
 
		// Reduces the master volume of the program 
		FloatControl volume = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
		volume.setValue(-12.0F);
 
		System.out.println("begin writing to buffer");
		sourceDataLine.start();
 
		// Create the audio buffer
		byte[] audioDataBuffer = new byte[numberOfSamplesInWaveBuffer];
 
		while (playTone) {
 
		    // Generate the new waveform to fill the buffer
		    audioDataBuffer = createAndPopulateNextAudioDataBuffer(currentFrequency, numberOfSamplesInWaveBuffer);
 
		    // Stream the contents of the buffer into the sourceDataLine. This method is blocking,
		    // and will only return when the sourceDataLine's internal buffer has "drained" enough
		    // to allow another write() command to top it off again.
		    int count = sourceDataLine.write(audioDataBuffer, 0, audioDataBuffer.length);
//		    System.out.println(count + " many bytes written");
 
		}
 
		sourceDataLine.flush();
		sourceDataLine.close();
 
		System.out.println("closing sourceDataLine");
 
 
	    } catch (LineUnavailableException e) {
		System.out.println(e);
	    }
	};
    }
 
    public byte[] createAndPopulateNextAudioDataBuffer(double freq, int samples) {
 
	byte[] audioDataBuffer = new byte[samples];
 
	double period = 1f / freq;
	double angleInRads = 0;
	double additionalAmpReduction = 0.7;
	double distanceInRadsBetweenSamples = 0;
 
	for (int loop = 0; loop < audioDataBuffer.length; loop++) {
 
	    angleInRads = (2.0 * Math.PI * loop) / (audioSampleRate * period) + angleOffsetToApply;
 
	    audioDataBuffer[loop] = (byte) (currentVolume * (Math.sin(angleInRads) * 127f) * (additionalAmpReduction));
 
	    distanceInRadsBetweenSamples = (2.0 * Math.PI) / (audioSampleRate * period);
	}
 
	angleOffsetToApply = (angleInRads % (2 * Math.PI)) + distanceInRadsBetweenSamples;
 
	return audioDataBuffer;
    }
 
    //========================================================================
    // Triggered by Listener events
    //========================================================================
 
    // This is triggered by selecting a new JComboBox element
    @Override
    public void actionPerformed(ActionEvent e) {
 
	JComboBox comboBox = (JComboBox) e.getSource();
	String selection = (String) comboBox.getSelectedItem();
 
	if (comboBox == comboBox_lowestTone) {
 
	    int indexOfSelection = getIndexOfTone(selection);
	    int indexOfHighestTone = getIndexOfTone(highestTone);
 
	    if (indexOfSelection < indexOfHighestTone) {
 
		lowestTone = selection;
 
	    } else {
 
		int indexOfLowestTone = getIndexOfTone(lowestTone);
		comboBox.setSelectedIndex(indexOfLowestTone);
	    }
 
	} else if (comboBox == comboBox_highestTone) {
 
	    int indexOfSelection = getIndexOfTone(selection);
	    int indexOfLowestTone = getIndexOfTone(lowestTone);
 
	    if (indexOfSelection > indexOfLowestTone) {
 
		highestTone = selection;
 
	    } else {
 
		int indexOfHighestTone = getIndexOfTone(highestTone);
		comboBox.setSelectedIndex(indexOfHighestTone);
	    }
 
	}
	toneDisplay.repaint();
    }
 
    // This is triggered by moving a new JSlider
    @Override
    public void stateChanged(ChangeEvent e) {
 
	JSlider eventSource = (JSlider) e.getSource();
 
	if (eventSource == slider_currentFrequency) {
 
	    double sliderValue = slider_currentFrequency.getValue();
	    currentFrequency = getFrequencyFromSliderValue(sliderValue);
	    updateVisuals();
	}
	if (eventSource == slider_currentVolume) {
 
	    double sliderValue = slider_currentVolume.getValue() / 100f;
	    currentVolume = sliderValue;
	    updateVisuals();
	}
    }
}