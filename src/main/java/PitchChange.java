/*
*  Use TarsosDSP to change pitch of waveform
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/

//package be.tarsos.dsp.example;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.MultichannelToMono;
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd;
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd.Parameters;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.io.jvm.WaveformWriter;
import be.tarsos.dsp.resample.RateTransposer;

public class PitchChange  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3830419374132803358L;


	private AudioDispatcher dispatcher;
	private WaveformSimilarityBasedOverlapAdd wsola;
	private GainProcessor gain;
	private AudioPlayer audioPlayer;
	private RateTransposer rateTransposer;
	private double currentFactor;// pitch shift factor
	private double sampleRate;
	private boolean loop;
	
	
	public PitchChange(){
		
		this.loop = false;
		
		currentFactor = 1;
		
		
	}
	
	public static double centToFactor(double cents){
		return 1 / Math.pow(Math.E,cents*Math.log(2)/1200/Math.log(Math.E)); 
	}
	private static double factorToCents(double factor){
		return 1200 * Math.log(1/factor) / Math.log(2); 
	}
	
	
	public static void main(String[] argv) {
	    if (argv.length == 3) {
		try {
		    startCli(argv[0],argv[1],Double.parseDouble(argv[2]));
		} catch (NumberFormatException e) {
		    printHelp("Bad time stretch number.");
		} catch (UnsupportedAudioFileException e) {
		    printHelp("Unsupported audio file. Must be 16bit 44.1kHz MONO PCM wav.");
		} catch (IOException e) {
		    printHelp("IO error, could not read/write to audio file.");
		}
	    } else {
		printHelp("Please provide exactly 3 arguments");
	    }
	}

    
	private static void startCli(String source,String target,double cents) throws UnsupportedAudioFileException, IOException{
		File inputFile = new File(source);
		AudioFormat format = AudioSystem.getAudioFileFormat(inputFile).getFormat();	
		double sampleRate = format.getSampleRate();
		double factor = PitchChange.centToFactor(cents);
		RateTransposer rateTransposer = new RateTransposer(factor);
		WaveformSimilarityBasedOverlapAdd wsola = new WaveformSimilarityBasedOverlapAdd(Parameters.musicDefaults(factor, sampleRate));
		WaveformWriter writer = new WaveformWriter(format,target);
		AudioDispatcher dispatcher;
		if(format.getChannels() != 1){
			dispatcher = AudioDispatcherFactory.fromFile(inputFile,wsola.getInputBufferSize() * format.getChannels(),wsola.getOverlap() * format.getChannels());
			dispatcher.addAudioProcessor(new MultichannelToMono(format.getChannels(),true));
		}else{
			dispatcher = AudioDispatcherFactory.fromFile(inputFile,wsola.getInputBufferSize(),wsola.getOverlap());
		}
		wsola.setDispatcher(dispatcher);
		dispatcher.addAudioProcessor(wsola);
		dispatcher.addAudioProcessor(rateTransposer);
		dispatcher.addAudioProcessor(writer);
		dispatcher.run();
	}

	
	private static final void printHelp(String error){
		SharedCommandLineUtilities.printPrefix();
		System.err.println("Name:");
		System.err.println("\tTarsosDSP Pitch shifting utility.");
		SharedCommandLineUtilities.printLine();
		System.err.println("Synopsis:");
		System.err.println("\tjava -jar PitchShift.jar source.wav target.wav cents");
		SharedCommandLineUtilities.printLine();
		System.err.println("Description:");
		System.err.println("\tChange the play back speed of audio without changing the pitch.\n");
		System.err.println("\t\tsource.wav\tA readable, mono wav file.");
		System.err.println("\t\ttarget.wav\tTarget location for the pitch shifted file.");
		System.err.println("\t\tcents\t\tPitch shifting in cents: 100 means one semitone up, -100 one down, 0 is no change. 1200 is one octave up.");
		if(!error.isEmpty()){
			SharedCommandLineUtilities.printLine();
			System.err.println("Error:");
			System.err.println("\t" + error);
		}
    }
}
