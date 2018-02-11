import java.util.LinkedHashMap;
 
public class ToneBean {
 
    public static String getClosestTone(double sliderFreq) {
 
	// Arbitrarily set to 100. It will change when the loop starts
	double shortestDistance = 100;
	String closestTone = "";
 
	// Loop through all notes in order, from A0 to C8 on the keyboard
	for (String toneName : toneMap.keySet()) {
 
	    // Find the tone who's frequency is closest to this method's argument (sliderFreq)
	    double freqForTone = toneMap.get(toneName);
	    if (shortestDistance > Math.abs(freqForTone - sliderFreq)) {
 
		shortestDistance = Math.abs(freqForTone - sliderFreq);
		closestTone = toneName;
	    }
	}
 
	return closestTone;
    }
 
    public static LinkedHashMap<String, Double> toneMap = new LinkedHashMap<String, Double>() {
 
	{
	    put("A0", 27.5000);
	    put("A#0/Bb0", 29.1353);
	    put("B0", 30.8677);
	    put("C1", 32.7032);
	    put("C#1/Db1", 34.6479);
	    put("D1", 36.7081);
	    put("D#1/Eb1", 38.8909);
	    put("E1", 41.2035);
	    put("F1", 43.6536);
	    put("F#1/Gb1", 46.2493);
	    put("G1", 48.9995);
	    put("G#1/Ab1", 51.9130);
	    put("A1", 55.0000);
	    put("A#1/Bb1", 58.2705);
	    put("B1", 61.7354);
	    put("C2", 65.4064);
	    put("C#2/Db2", 69.2957);
	    put("D2", 73.4162);
	    put("D#2/Eb2", 77.7817);
	    put("E2", 82.4069);
	    put("F2", 87.3071);
	    put("F#2/Gb2", 92.4986);
	    put("G2", 97.9989);
	    put("G#2/Ab2", 103.826);
	    put("A2", 110.000);
	    put("A#2/Bb2", 116.541);
	    put("B2", 123.471);
	    put("C3", 130.813);
	    put("C#3/Db3", 138.591);
	    put("D3", 146.832);
	    put("D#3/Eb3", 155.563);
	    put("E3", 164.814);
	    put("F3", 174.614);
	    put("F#3/Gb3", 184.997);
	    put("G3", 195.998);
	    put("G#3/Ab3", 207.652);
	    put("A3", 220.000);
	    put("A#3/Bb3", 233.082);
	    put("B3", 246.942);
	    put("C4", 261.626);
	    put("C#4/Db4", 277.183);
	    put("D4", 293.665);
	    put("D#4/Eb4", 311.127);
	    put("E4", 329.628);
	    put("F4", 349.228);
	    put("F#4/Gb4", 369.994);
	    put("G4", 391.995);
	    put("G#4/Ab4", 415.305);
	    put("A4", 440.000);
	    put("A#4/Bb4", 466.164);
	    put("B4", 493.883);
	    put("C5", 523.251);
	    put("C#5/Db5", 554.365);
	    put("D5", 587.330);
	    put("D#5/Eb5", 622.254);
	    put("E5", 659.255);
	    put("F5", 698.456);
	    put("F#5/Gb5", 739.989);
	    put("G5", 783.991);
	    put("G#5/Ab5", 830.609);
	    put("A5", 880.000);
	    put("A#5/Bb5", 932.328);
	    put("B5", 987.767);
	    put("C6", 1046.50);
	    put("C#6/Db6", 1108.73);
	    put("D6", 1174.66);
	    put("D#6/Eb6", 1244.51);
	    put("E6", 1318.51);
	    put("F6", 1396.91);
	    put("F#6/Gb6", 1479.98);
	    put("G6", 1567.98);
	    put("G#6/Ab6", 1661.22);
	    put("A6", 1760.00);
	    put("A#6/Bb6", 1864.66);
	    put("B6", 1975.53);
	    put("C7", 2093.00);
	    put("C#7/Db7", 2217.46);
	    put("D7", 2349.32);
	    put("D#7/Eb7", 2489.02);
	    put("E7", 2637.02);
	    put("F7", 2793.83);
	    put("F#7/Gb7", 2959.96);
	    put("G7", 3135.96);
	    put("G#7/Ab7", 3322.44);
	    put("A7", 3520.00);
	    put("A#7/Bb7", 3729.31);
	    put("B7", 3951.07);
	    put("C8", 4186.01);
	}
    ;
 
};
 
}