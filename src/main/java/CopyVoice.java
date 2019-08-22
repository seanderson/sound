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
   Copy notes from voice1 to voice2.
   Offset by midi number.
*/
public class CopyVoice implements Command {
    
  public Syntax getSyntax() {
      return SyntaxJ.commandSyntax( new int[] {
	      Syntax.NumberType(),
	      Syntax.NumberType(),
	      Syntax.NumberType() }
	  );
  }

  public void perform(Argument args[], Context context)
      throws ExtensionException {
      int src;
      int dest;
      int dmidi; // amount to add to dest voice
      
      try {
	  src = args[0].getIntValue();
	  dest = args[1].getIntValue();
	  dmidi = args[2].getIntValue();
      } catch (LogoException e) {
	  throw new ExtensionException("Error in CopyVoice: " + e.getMessage());
      } 

      if (src == dest) return;
      if (src < 0 || src >= P.NVOICES)
	  throw new ExtensionException("Bad voice ID: " + src);
      if (dest < 0 || dest >= P.NVOICES)
	  throw new ExtensionException("Bad voice ID: " + dest);

      copy((ExtensionContext) context,src,dest,dmidi);


  }


    /*
      Copy melody from src to dest, offset by dmidi semitones (pos or neg).
      Will elide any tones out of range.
    */
    public static void copy(ExtensionContext ec, int src, int dest, int dmidi)
    throws ExtensionException {
	Workspace ws = ec.workspace();
	World w = ws.world();
	int pcoloridx = w.patchesOwnIndexOf("PCOLOR");
	Patch p = null;
	Patch pdest = null;
	try {
	    int y0_src = P.PATCHESPERVOICE * (1 + src); // 1 is rhythm offset
	    int y0_dest = P.PATCHESPERVOICE * (1 + dest);

	    for (int x = 0; x < P.XMAX; x++) {
		for (int y = y0_src; y < y0_src + P.PATCHESPERVOICE; y++) {
		    p = w.getPatchAt(x,y);
		    if (p.pcolor().equals(P.BLACK)) continue; // ignore empties
		    
		    // clear all destination patches at this x
		    for (int yd = y0_dest; yd < y0_dest + P.PATCHESPERVOICE; yd++) {
			pdest = w.getPatchAt(x,yd);
			pdest.setPatchVariable(pcoloridx,P.DBLACK);
		    }
		    // validate new y patch for destination voice
		    int newy = y0_dest + (y - y0_src) + dmidi;
		    if (newy >= y0_dest && newy <= y0_dest + P.PATCHESPERVOICE) {
			pdest = w.getPatchAt(x,newy);
			pdest.setPatchVariable(pcoloridx,P.vcolor[dest]);
		    }
		}
	    }
	} catch (org.nlogo.api.AgentException ex) {
	    throw new org.nlogo.api.ExtensionException("Bad patch in music.");
	}



    }

}
