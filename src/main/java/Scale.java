package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
// SEA
import org.nlogo.api.Color;
import org.nlogo.nvm.Workspace;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.api.WorldResizer;

import org.nlogo.agent.World;
import org.nlogo.agent.Patch;

/**
   Set scale/tonic for a voice.
*/
public class Scale implements Command {
    
  // voice, take beats, base note time, string of 16 chars "9-------4-------"
  public Syntax getSyntax() {
      // set-scale VOICEID TONIC SCALENAME
      // Assumes TONIC is lowest midi value.
      return SyntaxJ.commandSyntax( new int[] { Syntax.NumberType(),
						Syntax.NumberType(),
						Syntax.StringType() }
	  );
  }

  public void perform(Argument args[], Context context)
      throws ExtensionException {

      int vid, tonic;
      String scalename;
      try {
	  vid = args[0].getIntValue();
	  tonic = args[1].getIntValue();
	  scalename = args[2].getString();
      } catch (LogoException e) {
	  throw new ExtensionException(e.getMessage());
      }

      if (vid < 0 || vid >= P.NVOICES)
	  throw new ExtensionException("Bad voice " + vid);

      P.voices[vid].setScale(tonic,scalename);
  }
}
