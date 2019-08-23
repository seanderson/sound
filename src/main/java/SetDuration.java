package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.api.Context;

/**
   Set duration of all notes for this voice.
   Duration is in integer terms of 1/MAXNOTESPERMEASURE
   and must be >= 1 and < P.XMAX.
*/
public class SetDuration implements Command {
    
  public Syntax getSyntax() {
      return SyntaxJ.commandSyntax( new int[] {
	      Syntax.NumberType(),
	      Syntax.NumberType() }
	  );
  }

  public void perform(Argument args[], Context context)
      throws ExtensionException {
      int vid;
      int dur;
      
      try {
	  vid = args[0].getIntValue();
	  dur = args[1].getIntValue();
      } catch (LogoException e) {
	  throw new ExtensionException("Error in SetDuration: " + e.getMessage());
      } 


      if (vid < 0 || vid >= P.NVOICES)
	  throw new ExtensionException("Bad voice ID: " + vid + " in SetDuration");
      if (dur <= 0 || dur > P.XMAX)
	  throw new ExtensionException("Bad note duration: " + dur + " in SetDuration");

      P.voices[vid].dur = dur;


  }
}
