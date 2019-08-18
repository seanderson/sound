package org.nlogo.extensions.sound;

import org.nlogo.agent.AgentSet;
import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.api.ExtensionException;

public class Voice {
    Double color; // color of voice's trail
    Turtle agent; // a turtle
    int instrument; // index in SoundExtension.INSTRUMENT_NAMES
    static final int[] PENTATONIC = {0,2,6,8,10}; // semtones rel. to tonic
    //static final int[] DFLT_SCALE;
    int[] scale; 
    
    /**
       Create a new voice (really a turtle).
    */
    public Voice (World w,int instr,Double color, double x, double y, int size )
	throws ExtensionException {
	scale = new int[P.PATCHESPERVOICE];
	int tonic = 45; // lowest and tonic midi semitone number
	setScale(tonic,"PENTATONIC");
	AgentSet breed = w.turtles();
	agent = w.createTurtle(breed,1,0); //color.intValue(),0);
	this.instrument = instr;
	try {
	    agent.xandycor(x,y);
	    agent.heading(0.0);
	    //agent.shape("line");
	    agent.size(2);//size);//P.PATCHESPERVOICE);
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

}
