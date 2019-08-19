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
   Set scale/tonic for a voice.
*/
public class Waveform implements Command {
    
    // set-waveform VOICEID DIR WAV.  Use waveform to generate scale.
    // DIR is relative to current directory.
  public Syntax getSyntax() {
      return SyntaxJ.commandSyntax( new int[] {
	      Syntax.NumberType(),
	      Syntax.StringType(),
	      Syntax.StringType() }
	  );
  }

  public void perform(Argument args[], Context context)
      throws ExtensionException {
      int vid;
      String dir, wavfile;
      
      try {
	  vid = args[0].getIntValue();
	  dir = context.attachCurrentDirectory(args[1].getString());
	  wavfile = args[2].getString();
      } catch (LogoException e) {
	  throw new ExtensionException(e.getMessage());
      } catch (java.net.MalformedURLException ex) {
	  return;
      }

      if (vid < 0 || vid >= P.NVOICES)
	  throw new ExtensionException("Bad voice " + vid);

      P.voices[vid].setWaveform(dir,wavfile);
  }
}
