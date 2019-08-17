package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
// SEA
import org.nlogo.api.Color;
//import java.awt.Color;
import org.nlogo.nvm.Workspace;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.api.WorldResizer;
//import org.nlogo.api.Patch;
import org.nlogo.agent.World;
import org.nlogo.agent.Patch;
//import java.awt.image.BufferedImage;
//import java.awt.Graphics2D;
//import java.awt.BasicStroke;


/**
   Allow user to paint patch colors in various voices.
   Patches updated to that voice's color.
*/
public class PaintMelody implements Command {
    
  // voice, take beats, base note time, string of 16 chars "9-------4-------"
  public Syntax getSyntax() {
      return SyntaxJ.commandSyntax( 	  );
  }

  public void perform(Argument args[], Context context)
      throws ExtensionException {

      int voice, nbeats, basebeat;
      String pttn;
      try {
      } catch (LogoException e) {
	  throw new ExtensionException(e.getMessage());
      }

      ExtensionContext ec = (ExtensionContext) context;
      Workspace ws = ec.workspace();

    World w = ws.world();
    int pcoloridx = w.patchesOwnIndexOf("PCOLOR");
    Patch p = null;
    try {

	if (ws.mouseDown()) {
	    int x = (int)(ws.mouseXCor()); // patch coords.
	    int y = (int)(ws.mouseYCor());
	    int voc = y / P.PATCHESPERVOICE;
	    if (voc > 0) {  // zero is for rhythm
		p = w.getPatchAt(x,y);
		if (!p.pcolor().equals(P.BLACK)) // unset sound
		    p.setPatchVariable(pcoloridx,P.DBLACK);
		else {
		    // clear other patches for this voice.
		    for (int iy = voc * P.PATCHESPERVOICE; iy < (voc + 1) * P.PATCHESPERVOICE; iy++) {
			Patch tmp = w.getPatchAt(x,iy);
			tmp.setPatchVariable(pcoloridx,P.DBLACK);
		    }
		    p.setPatchVariable(pcoloridx,P.vcolor[voc]); // color this patch
		}
	    }
	}

    } catch (org.nlogo.api.AgentException ex) {
	throw new org.nlogo.api.ExtensionException("Bad patch in music.");
    }
    
  }
}
