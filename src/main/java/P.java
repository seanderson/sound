package org.nlogo.extensions.sound;


// Params for all of music

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
	"PICCOLO"};

    static AudMixer mixer; // plays all wavs on single line
    
    public P() {
	setBPM(BEATSPERMINUTE);
	int tmp = 122; // purply colors
	dcolor = new Double[NDRUMS];
	mixer = new AudMixer();
	mixer.start();
	for (int i = 0; i < NDRUMS; i++) {
	    if (tmp == 129) tmp = 132;
	    dcolor[i] = new Double( tmp++ );
	}
	vcolor = new Double[NVOICES];
	for (int i = 0; i < NVOICES; i++) {
	    vcolor[i] = new Double(BASE_VOICE_COLOR + 20 * i);
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
}
