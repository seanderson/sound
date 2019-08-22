package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
// SEA
import org.nlogo.api.Context;

import org.nlogo.nvm.Workspace;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.api.WorldResizer;

import org.nlogo.agent.World;
import org.nlogo.agent.Patch;

/**
   Set duration of all notes for this voice.
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
	  throw new ExtensionException("Bad voice ID: " + vid);
      if (dur < 0 || dur > 1000)
	  throw new ExtensionException("Bad note duration: " + dur);

      P.voices[vid].dur = dur;


  }
}
