package org.nlogo.extensions.sound;

import org.nlogo.agent.AgentSet;
import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.api.ExtensionException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
//import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Voice {
    Turtle agent; // a turtle
    long agentID;
    int instrument; // index in SoundExtension.INSTRUMENT_NAMES
    boolean isMidi = true;
    int[] notes; // set of notes for this instrument.  Lowest is tonic.
    short wav[][]; // wavs to play for each note
    AudioFormat format; // format for all audio wavs
    int dur = 1; // duration of note (in terms of smallest note dur.)
    int vel = 100; // velocity
    int tonic = 48;
    String type = Scale.PENTATONIC;
    String dir; // wavfile's directory relative to model
    String wavfile;
    public static final int NOT_INITIALIZED = -1;

    /*
      Default constructor used during reinit.  Voice fields MUST be
      filled in later.
     */
    public Voice() {
        this.agentID = NOT_INITIALIZED; // signals unset
        wav = new short[P.PATCHESPERVOICE][];
        notes = new int[P.PATCHESPERVOICE];

    }
    /**
     * Create a new voice corresponding to one turtle.
     *
     */
    public Voice(World w, int instr, Double color,
                 double x, double y, int size,
                 int tonic, String type)
            throws ExtensionException {
        wav = new short[P.PATCHESPERVOICE][];
        notes = new int[P.PATCHESPERVOICE];
        this.tonic = tonic;
        this.type = type;
        setScale(tonic, this.type);
        AgentSet breed = w.turtles();
        agent = w.createTurtle(breed, 1, 0); //color.intValue(),0);
        agentID = agent.id();

        this.instrument = instr;
        try {
            agent.xandycor(x, y);
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
            agent.xcor(step + agent.xcor());
        } catch (org.nlogo.api.AgentException ex) {
            throw new org.nlogo.api.ExtensionException("Bad agent in fd."
                    + ex.getMessage());
        }
    }

    public boolean isMidi() {
        return this.isMidi;
    }

    /**
     * Return index of note at position i for this voice.
     */
    public int note(int i) {
        if (i < 0 || i >= P.PATCHESPERVOICE) return 0;
        return notes[i];
    }

    /**
     * Set up all notes for voice, based on tonic and scale name.
     * If based on wav, will force reload of wav files.
     */
    public void setScale(int thetonic, String name)
            throws ExtensionException {

        if (thetonic < 20 || thetonic > 108) return; // hard limits
        this.tonic = thetonic;
        this.type = name;
        int[] master_scale = Scale.getScale(name);
        for (int i = 0; i < P.PATCHESPERVOICE; i++) {
            // Shift scale up one octave
            if (i % master_scale.length == 0 && i != 0) {
                thetonic += 12;
            }
            this.notes[i] = thetonic + master_scale[(i % master_scale.length)];
        }
        if (!isMidi()) {
            setWaveform(this.dir,this.wavfile);
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
        bb.order(ByteOrder.LITTLE_ENDIAN);
        short[] out = new short[arr.length / 2];
        for (int i = 0; i < arr.length; i += 2) {
            out[i / 2] = bb.getShort(i);
        }
        return out;
    }

    /**
     * Set all wavefiles for this voice.
     * Assumes dir/wavfile-N.wav where N=0...P.PATCHESPERVOICE
     */
    public void setWaveform(String dir, String wavfile)
            throws ExtensionException {
        this.isMidi = false;
        this.dir = dir;
        this.wavfile = wavfile;
        wav = new short[P.PATCHESPERVOICE][];

        for (int i = 0; i < P.PATCHESPERVOICE; i++) {
            try {
                // Files names with midi number (notes[i])
                File file = new File(dir + "/" + wavfile + "-" + notes[i] + ".wav");

                AudioInputStream stream = AudioSystem.getAudioInputStream(file);
                format = stream.getFormat();
                if (!checkFormat(format))
                    throw new UnsupportedAudioFileException();
                int bytesavailable = stream.available();
                if (bytesavailable > 0) {
                    byte[] tmp = new byte[bytesavailable];
                    stream.read(tmp, 0, bytesavailable);
                    // keep bytes in array of shorts
                    wav[i] = toShort(tmp);
                } else wav[i] = null;
                stream.close();

            } catch (UnsupportedAudioFileException ex1) {
                wav[i] = null;
                throw new ExtensionException("Audio exception: " + ex1.getMessage());
            } catch (IOException ex2) {
                wav[i] = null;
                throw new ExtensionException("Wav file not found: " + ex2.getMessage());

            }

        }
    } // setWaveform


    /**
     * Set wavefiles for this drum voice.
     * Assumes dir/wavfile.wav
     */
    public void setDrumWaveform(String dir, String wavfile)
            throws ExtensionException {
        int PATCHESPERDRUM = 10;  // We create 10 different volumes for a drum waveform!
        isMidi = false;
        wav = new short[PATCHESPERDRUM][];

        int i = PATCHESPERDRUM / 2;
        try {
            // Files names with midi number (notes[i])
            File file = new File(dir + "/" + wavfile + ".wav");

            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            format = stream.getFormat();
            if (!checkFormat(format))
                throw new UnsupportedAudioFileException();
            int bytesavailable = stream.available();
            if (bytesavailable > 0) {
                byte[] tmp = new byte[bytesavailable];
                stream.read(tmp, 0, bytesavailable);
                // keep bytes in array of shorts
                wav[i] = toShort(tmp);
            } else wav[i] = null;
            stream.close();

        } catch (UnsupportedAudioFileException ex1) {
            for (i = 0; i < PATCHESPERDRUM; i++) wav[i] = null;
            throw new ExtensionException("Audio exception: " + ex1.getMessage());
        } catch (IOException ex2) {
            for (i = 0; i < PATCHESPERDRUM; i++) wav[i] = null;
            throw new ExtensionException("Wav file not found: " + ex2.getMessage());
        }
        // Create drums at all volumes above/below wav[i]
        addVolumes(wav, i);

    } // setDrumWaveform

    /*
       Fill wav, except wav[pos], with copies of
       wav[pos].  Rescale orignal to fill loudest position with
       a max sample of MAX_VALUE.  Rescale downward by factor subtracting
       about 2-3dB.  See https://www.dr-lex.be/info-stuff/volumecontrols.html

     */

    private void addVolumes(short[][] wav, int pos) {
        int len = wav[pos].length;

        double maxval = wav[pos][Utils.findAbsMax(wav[pos])];
        int last = wav.length - 1;
        wav[last] = new short[len];
        // Make loudest in wav[last]
        double factor = Short.MAX_VALUE / maxval;
        for (int j = 0; j < len; j++) {
            wav[last][j] = (short) (factor * wav[pos][j]);
        }
        factor = 0.85;
        for (int i = last - 1; i > -1; i--) {
            wav[i] = new short[len];
            for (int j = 0; j < len; j++) {
                wav[i][j] = (short) (factor * wav[i + 1][j]);
            }

        }
    }

    /** Use agent info to create and place new agent */
    public void resetAgent(World w, double x, double y)
            throws ExtensionException {
        AgentSet breed = w.turtles();
        agent = w.createTurtle(breed, 1, 0); //color.intValue(),0);
        agentID = agent.id();
        int size = P.PATCHESPERVOICE;
        try {
            agent.xandycor(x, y);
            agent.heading(0.0);
            agent.shape("line");
            agent.size(size);
        } catch (org.nlogo.api.AgentException ex) {
            throw new org.nlogo.api.ExtensionException("Bad agent in Voice."
                    + ex.getMessage());
        }
    }


}
