package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
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
    
    static class Type {
	static int[] tones;
	static String name;
	static String[] names = {"PENTATONIC","PENTATONIC MINOR",
				 "BLUES","MAJOR 7","MINOR 7",
				 "WHOLE TONE"};
	static final int[][] SCALES = {
	    {0,2,5,7,9},    // pentatonic
	    {0,3,5,7,10},   // pent. minor
	    {0,3,4,5,7,10}, // blues
	    {0,4,7,11},     // major 7
	    {0,3,7,10},     // minor 7
	    {0,2,4,6,8,10} // whole tone
	};

    }
    public static String PENTATONIC = Type.names[0];
    public static String PENTATONIC_MINOR = Type.names[1];
    public static String BLUES = Type.names[2];
    public static String MAJOR_7 = Type.names[3];
    public static String MINOR_7 = Type.names[4];
    public static String WHOLE_TONE = Type.names[5];

    public static int[] getScale(String name)
	throws ExtensionException {

	for (int i = 0; i < Type.names.length; i++) {
	    if (Type.names[i].equals(name)) return Type.SCALES[i];
	}
	throw new ExtensionException("Scale undefined: " + name);
    }
    
  public Syntax getSyntax() {
      // set-scale VOICEID TONIC SCALENAME
      // Assumes TONIC is lowest midi value.
      return SyntaxJ.commandSyntax( new int[] {
			  Syntax.NumberType(),
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
