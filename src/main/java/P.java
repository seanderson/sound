package org.nlogo.extensions.sound;

// Params for all of music

public class P {
    static int NMEASURES = 4; // measures in one lick (pattern)
    static int MAXNOTESPERMEASURE = 16;
    static int NVOICES = 5; // bottom "voice" is rhythm, up to 10 drums
    static int PATCHESPERVOICE = 16; // possible notes 
    static int XMAX = NMEASURES * MAXNOTESPERMEASURE; // 4 measures
    static int YMAX = NVOICES * PATCHESPERVOICE;
    static Double PATCHSIZE = new Double(4.0);
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
    static Voice[] voices;
    
    public P() {
	vcolor = new Double[NVOICES];
	vcolor[0] = new Double(RCOLOR);
	for (int i = 0; i < NVOICES; i++) {
	    vcolor[i] = new Double(BASE_VOICE_COLOR + 10 * i);
	}
    }
}
