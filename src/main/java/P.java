package org.nlogo.extensions.sound;


// Params for all of music

import org.nlogo.api.Dump;
import org.nlogo.api.ExtensionException;

import java.util.List;

public class P {
    static int BEATSPERMINUTE = 80;
    static int BEATSPERMEASURE = 4;
    final static int SAMPLERATE = 44100; // for wavs
    final static int SAMPLESIZE = 16; // 16-bit precision for wavs
    static int NMEASURES = 4; // measures in one lick (pattern)
    static int MAXNOTESPERMEASURE = 16;
    static int NDRUMS = MAXNOTESPERMEASURE; // same a num notes for simplicity
    static int NVOICES = 4; // bottom is rhythm (not a voice)
    static int PATCHESPERVOICE = 16; // possible notes
    static int XMAX = NMEASURES * MAXNOTESPERMEASURE; // 4 measures
    static int YMAX = (NVOICES + 1) * PATCHESPERVOICE;
    final static int VELOCITY_MAX = 127;
    static Double PATCHSIZE = new Double(7.0);
    static boolean WRAP = true;
    static int LINE_THICKNESS = 1; // for drawing over patches
    static double BLACK = 0;
    static Double DBLACK = new Double(BLACK);
    static double WHITE = 9.9;
    static Double DWHITE = new Double(WHITE);
    static int RCOLOR = 20; // base rhythm voice color
    static char SIL = '-'; // silence
    static int BASE_VOICE_COLOR = 15; // red
    static Double[] vcolor; // voice colors
    static Double[] dcolor; // drum colors
    static Voice[] voices;
    static Voice[] drums;
    static int MIN_NOTE_DUR = 0; // duration, in msec, of smallest note.
    static final String DELIM = " ";

    // For initial drums, low to hi
    static final String[] DEFAULT_DRUMS = {
            "ACOUSTIC BASS DRUM",
            "ACOUSTIC SNARE",
            "ELECTRIC SNARE",
            "LOW TOM",
            "LOW MID TOM",
            "HI TOM",
            "LOW BONGO",
            "HI BONGO",
            "LOW WOOD BLOCK",
            "HI WOOD BLOCK",
            "HAND CLAP",
            "CABASA",
            "OPEN HI HAT",
            "CLOSED HI HAT",
            "RIDE CYMBAL 1",
            "SHORT WHISTLE"
    };
    // For initial voices, low to hi
    static final String[] DEFAULT_INSTRUMENTS = {
            "CELLO",
            "ACOUSTIC GRAND PIANO",
            "CLARINET",
            "FLUTE",
            "PICCOLO",
            "ACOUSTIC GRAND PIANO",
            "ACOUSTIC GRAND PIANO",
            "ACOUSTIC GRAND PIANO",
            "ACOUSTIC GRAND PIANO",
            "ACOUSTIC GRAND PIANO"
    };

    static AudMixer mixer; // plays all wavs on single line

    public P(int nvoices) {
        NVOICES = nvoices;
        PATCHESPERVOICE = 16; // possible notes
        XMAX = NMEASURES * MAXNOTESPERMEASURE; // 4 measures
        YMAX = (NVOICES + 1) * PATCHESPERVOICE;

        setBPM(BEATSPERMINUTE);
        int tmp = 120; // purply colors
        dcolor = new Double[NDRUMS];
        mixer = new AudMixer();
        mixer.start();
        for (int i = 0; i < NDRUMS; i++) {
            tmp = Math.min(tmp, 129);
            dcolor[i] = new Double(tmp++);
        }
        vcolor = new Double[NVOICES];
        for (int i = 0; i < NVOICES; i++) {
            vcolor[i] = new Double((BASE_VOICE_COLOR + 40 * i) % 140);
        }

    }

    /*
      set global beats per minute.  Must also reset duration of minimum
      note which is in msec.
    */
    public static void setBPM(int bpm) {
        if (bpm > 0 && bpm < 1000) {
            BEATSPERMINUTE = bpm;
            MIN_NOTE_DUR = (int) (1000 * (60.0 / (MAXNOTESPERMEASURE / BEATSPERMEASURE)) / BEATSPERMINUTE);
        }
    }

    /*
      set global beats per minute.  Must also reset duration of minimum
      note which is in msec.
    */
    public static void setPatchSize(int size)
            throws ExtensionException {
        if (size > 0 && size < 100) {
            PATCHSIZE = new Double(size);
        } else {
            throw new ExtensionException("Bad patch size: " + size);
        }
    }

    private static void addBoolean(StringBuilder buff, String id, boolean val) {
        buff.append(
                Dump.csv().encode(id + DELIM + val) + "\n");
        /*buff.append(id);
        buff.append(DELIM);
        buff.append(val);
        buff.append("\n");*/
    }

    private static void addInt(StringBuilder buff, String id, int num) {
        buff.append(
                Dump.csv().encode(id + DELIM + num) + "\n");

        /*buff.append(id);
        buff.append(DELIM);
        buff.append(num);
        buff.append("\n");*/
    }

    private static void addDouble(StringBuilder buff, String id, double num) {
        buff.append(
                Dump.csv().encode(id + DELIM + num) + "\n");

        /*buff.append(id);
        buff.append(DELIM);
        buff.append(num);
        buff.append("\n");*/
    }

    private static void addString(StringBuilder buff, String id, String val) {
        buff.append(
                Dump.csv().encode(id + DELIM + val) + "\n");
        /*buff.append(id);
        buff.append(DELIM);
        buff.append(val);
        buff.append("\n");*/
    }

    private static void addArray(StringBuilder buff, String id, int[] num) {

        if (num == null) return;
        buff.append(
                Dump.csv().encode(id) + "\n");
        for (int n : num) {
            buff.append(
                    Dump.csv().encode("" + n) + "\n");
        }

    }


    /*
      Append values to sb that permit reconstruction
      of music world.
     */
    public static void appendValues(StringBuilder buff) {
        addInt(buff, "BEATSPERMINUTE", BEATSPERMINUTE);
        addInt(buff, "NMEASURES", NMEASURES);
        addInt(buff, "NDRUMS", NDRUMS);
        addInt(buff, "NVOICES", NVOICES);
        addDouble(buff, "PATCHSIZE", PATCHSIZE);

        for (int i = 0; i < NVOICES; i++) {
            addInt(buff, "VOICE", i);
            addInt(buff, "instrument", voices[i].instrument);
            addBoolean(buff, "isMidi", voices[i].isMidi);
            addArray(buff, "notes", voices[i].notes);
            addInt(buff, "duration", voices[i].dur);
            addInt(buff, "velocity", voices[i].vel);
            addInt(buff, "tonic", voices[i].tonic);
            addString(buff, "dir", voices[i].dir);
            addString(buff, "wavfile", voices[i].wavfile);
        }
        for (int i = 0; i < NDRUMS; i++) {
            addInt(buff, "DRUM", i);
            addInt(buff, "instrument", drums[i].instrument);
            addBoolean(buff, "isMidi", drums[i].isMidi);
            addArray(buff, "notes", drums[i].notes);
            addInt(buff, "duration", drums[i].dur);
            addInt(buff, "velocity", drums[i].vel);
            addInt(buff, "tonic", drums[i].tonic);
            addString(buff, "dir", drums[i].dir);
            addString(buff, "wavfile", drums[i].wavfile);
        }

    }

    /*
      Append values to sb that permit reconstruction
      of music world.
     */
    public static void extractValues(List<String[]> lines)
            throws ExtensionException {
        int id = -1;
        org.nlogo.extensions.sound.Voice v = null;

        for (String[] line : lines) {
            String[] s = line[0].split(DELIM);
            if (s[0].equals("BEATSPERMINUTE")){
            }
            else if (s[0].equals("NMEASURES")) {
                P.NMEASURES = Integer.parseInt(s[1]);
            }
            else if (s[0].equals("NDRUMS")) {
                P.NDRUMS = Integer.parseInt(s[1]);
            }
            else if (s[0].equals("NVOICES")) {
                P.NVOICES = Integer.parseInt(s[1]);
            }
            else if (s[0].equals("PATCHSIZE")) {
                P.PATCHSIZE = Double.parseDouble(s[1]);
            }
            else if (s[0].equals("DRUM")) {
                id = Integer.parseInt(s[1]);
                v = P.drums[id];
            }
            else if (s[0].equals("VOICE")) {
                id = Integer.parseInt(s[1]);
                v = P.voices[id];
            }
            else if (s[0].equals("instrument")) {
                v.instrument = Integer.parseInt(s[1]);
            }
            else if (s[0].equals("isMidi")) {
                v.isMidi = Boolean.parseBoolean(s[1]);
            }
            else if (s[0].equals("notes")) {
                for (int j = 1; j < s.length; j++) {
                    v.notes[j - 1] = Integer.parseInt(s[j]);
                }
            }
            else if (s[0].equals("duration")) {
                v.dur = Integer.parseInt(s[1]);
            }
            else if (s[0].equals("velocity")) {
                v.vel = Integer.parseInt(s[1]);
            }
            else if (s[0].equals("tonic")) {
                v.tonic = Integer.parseInt(s[1]);
            }
            else if (s[0].equals("dir")) {
                v.dir = s[1].equals("null") ? null : s[1];
            }
            else if (s[0].equals("wavfile")) {
                v.wavfile = s[1].equals("null") ? null : s[1];
            }
            else{
                throw new ExtensionException("Import field not recognized: " + s[0]);
            }


        }

    }
}
