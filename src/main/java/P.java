package org.nlogo.extensions.sound;


// Params for all of music

public class P {
    static int SAMPLERATE = 44100; // for wavs
    static int SAMPLESIZE = 16; // 16-bit precision for wavs
    static int NMEASURES = 4; // measures in one lick (pattern)
    static int MAXNOTESPERMEASURE = 16;
    static int NDRUMS = MAXNOTESPERMEASURE; // same a num notes for simplicity
    static int NVOICES = 6; // bottom is rhythm (not a voice)
    static int PATCHESPERVOICE = 16; // possible notes 
    static int XMAX = NMEASURES * MAXNOTESPERMEASURE; // 4 measures
    static int YMAX = (NVOICES + 1) * PATCHESPERVOICE;
    static Double PATCHSIZE = new Double(5.0);
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
    // list of drums
    static int[] dlist = {35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50};
    // list of instruments
    static int[] vlist = {0,25,41,74,92,0,0,0,0,0,0,0,0,0,0};
    static AudMixer mixer; // plays all wavs on single line
    
    public P() {
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
}
