package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import org.nlogo.api.Context;


/**
   Set scale/tonic for a voice.
*/
public class SetInstrument implements Command {
    
    // set-voice-instrument VOICEID NST-STRING
  public Syntax getSyntax() {
      return SyntaxJ.commandSyntax( new int[] {
	      Syntax.NumberType(),
	      Syntax.StringType() }
	  );
  }

  public void perform(Argument args[], Context context)
      throws ExtensionException {
      int vid;
      int instrument;
      
      try {
      vid = args[0].getIntValue();
      instrument = SoundExtension.getInstrument(args[1].getString());
      } catch (LogoException e) {
	  throw new ExtensionException("Error in SetInstrument: " + e.getMessage());
      } 


      if (vid < 0 || vid >= P.NVOICES)
	  throw new ExtensionException("Bad voice ID: " + vid);

      P.voices[vid].instrument = instrument;
      P.voices[vid].isMidi = true;

  }
}
