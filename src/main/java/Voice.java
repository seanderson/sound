package org.nlogo.extensions.sound;

import org.nlogo.agent.AgentSet;
import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.api.ExtensionException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.SourceDataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Voice {
    Double color; // color of voice's trail
    Turtle agent; // a turtle
    int instrument; // index in SoundExtension.INSTRUMENT_NAMES
    static final int[] PENTATONIC = {0,2,6,8,10}; // semtones rel. to tonic
    int[] scale; // set of notes for this instrument.  Lowest is tonic.
    boolean isMidi = true;
    short wav[][]; // wavs to play for each note
    AudioFormat format; // format for all audio wavs
    int dur; // duration of note for midi
    //SourceDataLine srcline = null; // each voice can have its own dataline
    /**
       Create a new voice (really a turtle).
    */
    public Voice (World w,int instr,Double color, double x, double y, int size )
	throws ExtensionException {
	dur = 100;
	wav = new short[P.PATCHESPERVOICE][];
	scale = new int[P.PATCHESPERVOICE];
	int tonic = 45; // lowest and tonic midi semitone number
	setScale(tonic,"PENTATONIC");
	AgentSet breed = w.turtles();
	agent = w.createTurtle(breed,1,0); //color.intValue(),0);
	this.instrument = instr;
	try {
	    agent.xandycor(x,y);
	    agent.heading(0.0);
	    agent.shape("line");
	    agent.size(size);//P.PATCHESPERVOICE);
	} catch (org.nlogo.api.AgentException ex) {
	    throw new org.nlogo.api.ExtensionException("Bad agent in Voice."
						       + ex.getMessage());
	}
    }
    /*
      Move voice some patches to the east.
      @param step num patches to move.
    */
    public void fd(int step) throws ExtensionException {
	try {
	    agent.xcor( step + agent.xcor() );
	} catch (org.nlogo.api.AgentException ex) {
	    throw new org.nlogo.api.ExtensionException("Bad agent in fd."
						       + ex.getMessage());
	}
    }

    public boolean isMidi() { return this.isMidi; }
    
    /**
       Return index of note at position i for this voice.
    */
    public int note(int i) {
	if (i < 0 || i >= P.PATCHESPERVOICE) return 0;
	return scale[i];
    }

    /**
       Return index of note at position i for this voice.
    */
    public void setScale(int tonic, String name) {
	if ( tonic < 20 || tonic > 100) return;
	if (name.equals("PENTATONIC") ) {
	    for (int i = 0; i < P.PATCHESPERVOICE; i++) {
		if (i == PENTATONIC.length) {
		    tonic += 12; // next octave
		}
		scale[i] = tonic + PENTATONIC[ (i % PENTATONIC.length) ];
	    }
	}
    }


    /*
      Currently all waveforms must pass these tests.
    */
    private boolean checkFormat(AudioFormat f) {
	if (f.getChannels() != 1 ||
	    f.getSampleRate() != P.SAMPLERATE ||
	    f.isBigEndian() == true ||
	    f.getSampleSizeInBits() != P.SAMPLESIZE) {
	    return false;
	}
	
	return true;  // all ok
    }

    /*
      Convert bytes to little-endian shorts.
    */
    private short[] toShort(byte[] arr) {
	ByteBuffer bb = ByteBuffer.wrap(arr);
	bb.order( ByteOrder.LITTLE_ENDIAN);
	short[] out = new short[arr.length/2];
	for (int i = 0; i < arr.length; i+=2) {
	    out[i/2] = bb.getShort(i);
	}
	return out;
    }
    
    /**
       Set all wavefiles for this voice.
       Assumes dir/wavfile-N.wav where N=0...P.PATCHESPERVOICE
    */
    public void setWaveform(String dir, String wavfile)
	throws ExtensionException {
	isMidi = false;
	wav = new short[P.PATCHESPERVOICE][];
	
	for (int i = 0; i < P.PATCHESPERVOICE; i++) {
	    try {
		File file = new File(dir + "/" + wavfile + "-" + scale[i] + ".wav");

		AudioInputStream stream = AudioSystem.getAudioInputStream(file);
		format = stream.getFormat();
		/*		if (srcline == null)  { // init the SourceDataLine
		    srcline = AudioSystem.getSourceDataLine(format);
		    srcline.open(format,1024*1024);
		    //srcline.start();
		    }*/
		if (!checkFormat(format))
		    throw new UnsupportedAudioFileException();
		int bytesavailable = stream.available();
		if (bytesavailable > 0) {
		    byte[] tmp = new byte[bytesavailable];
		    stream.read(tmp,0,bytesavailable);
		    // place bytes into array of shorts
		    wav[i] = toShort(tmp);
		}
		else wav[i] = null;
		stream.close();

	    }/* catch (LineUnavailableException ex0) {
		throw new ExtensionException("Audio exception: " + ex0.getMessage());
		}*/

	    catch (UnsupportedAudioFileException ex1) {
		wav[i] = null;
		throw new ExtensionException("Audio exception: " + ex1.getMessage());
	    } catch (IOException ex2) {
		wav[i] = null;
		throw new ExtensionException("Wav file not found: " + ex2.getMessage());

	    }

	}



    }

}
