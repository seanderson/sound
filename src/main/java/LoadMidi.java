package org.nlogo.extensions.sound;

import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import java.io.File;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import org.nlogo.api.*;

/**
 * NetLogo command loops a sound file
 */
public class LoadMidi implements org.nlogo.api.Reporter {
    public String getAgentClassString() {
	return "OTP";
    }

    public org.nlogo.core.Syntax getSyntax() {
	return SyntaxJ.reporterSyntax(
				      new int[] {Syntax.StringType()},
				      Syntax.ListType()
				      );
    }

    public boolean getSwitchesBoolean() {
	return false;
    }

    public org.nlogo.api.Command newInstance(String name) {
	return new StopSound();
    }


    public Object report(org.nlogo.api.Argument args[], org.nlogo.api.Context context)
	throws org.nlogo.api.ExtensionException, org.nlogo.api.LogoException {

	// create a NetLogo list for the result
	LogoListBuilder list = new LogoListBuilder();
	String midifile;
	try {
	    midifile = args[0].getString();
	}
	catch(LogoException e) {
	    throw new ExtensionException( e.getMessage() ) ;
	}
	try {
	    Sequence sequence = MidiSystem.getSequence(new File(midifile));

	    // Create a sequencer for the sequence
	    Sequencer sequencer = MidiSystem.getSequencer();
	    sequencer.open();
	    sequencer.setSequence(sequence);

	    // Start playing
	    sequencer.start();
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
		sequencer.stop();
	    }
	    sequencer.stop();

	  
	}
	catch (InvalidMidiDataException invalidex) {
	    throw new ExtensionException ("Invalid Midi data");
	}
	catch(MidiUnavailableException unavailex) {
		throw new ExtensionException ("Midi not available");
	}
	catch(IOException ioex) {
	    throw new ExtensionException ("IO error");
	}
      
	return list.toLogoList();
      
    }
}
