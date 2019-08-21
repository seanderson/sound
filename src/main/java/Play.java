package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.SyntaxJ;
import org.nlogo.core.Syntax;
import org.nlogo.nvm.Workspace;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.agent.World;

public class Play implements Command {


  // 
    public Syntax getSyntax() {
	return SyntaxJ.commandSyntax( 	  );
    }

    /*
      Take one step. Play drums/voices on new patch.
    */
    public void perform(Argument args[], Context context)
	throws ExtensionException {

	int note = 60;
	int vel = 100;
	int dur = 1000 / 16; // 16th note
	dur = 100;
	ExtensionContext ec = (ExtensionContext) context;
	Workspace ws = ec.workspace();
	World w = ws.world();

	Patch p = null;
	try {
	    // Each drum on one patch only.
	    for (int i = 0; i < P.NDRUMS; i++) {
		P.drums[i].fd(1);
		p = w.getPatchAt(P.drums[i].agent.xcor(),
				 P.drums[i].agent.ycor());
		if (!p.pcolor().equals(P.DBLACK)) {
		    vel = (int)(127 * (((double)p.pcolor() - P.RCOLOR) / 9.0));
		    SoundExtension.playDrum(P.drums[i].instrument,vel);
		}
	    }
	    // Voices occupy many patches at one xcor value.
	    // Only one patch can have a note.
	    vel = 100;
	    for (int i = 0; i < P.NVOICES; i++) {
		P.voices[i].fd(1);
		note = getNote(w,i);
		if (note > -1) {
		    if (P.voices[i].isMidi()) 
			SoundExtension.playNote(P.voices[i].instrument,P.voices[i].note(note),vel,dur);
		    else
			SoundExtension.playWav(P.voices[i],note,dur);
		}
	    }
		    
	} catch (org.nlogo.api.AgentException ex) {}
    }

    /* 
       Return note for colored patch governed by this voice; -1 if none found.
     */
    private int getNote(World w, int vid) throws AgentException {
	Patch p = null;
	// Find first patch in range that is not black.
	double x = P.voices[vid].agent.xcor();
	double y = P.voices[vid].agent.ycor() - (P.PATCHESPERVOICE / 2);
	for (int i = 0; i < P.PATCHESPERVOICE; i++) {
	    p = w.getPatchAt(x,y+i);
	    if (!p.pcolor().equals(P.DBLACK)) {
		return i;// P.voices[vid].note(i);
	    }
	}
	return -1; // no colored patch found
    }

    
} // end Play
