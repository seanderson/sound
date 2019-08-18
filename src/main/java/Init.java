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
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import org.nlogo.api.ExtensionException;

/**
   Initialize world for voices, one lick.
*/
public class Init implements Command {

    // No args for init
    public Syntax getSyntax() {
	return SyntaxJ.commandSyntax( );
    }
    // Command execution starts here.
    public void perform(Argument args[], Context context) 
	throws ExtensionException {
	new P(); // init all global params

	ExtensionContext ec = (ExtensionContext) context;
	Workspace ws = ec.workspace();

	ws.setDimensions(new WorldDimensions3D(0,P.XMAX,0,
					       P.YMAX,0,1,
					       P.PATCHSIZE,
					       P.WRAP,P.WRAP,P.WRAP),
			 P.PATCHSIZE);
	World w = ws.world();
	int pcoloridx = getPcolorID(w);
	try {
	    for (int x = 0; x < P.XMAX; x++)
		for (int y = 0; y < P.YMAX; y++)
		    w.getPatchAt(x,y).setPatchVariable(pcoloridx,P.DBLACK);
	}  catch (org.nlogo.api.AgentException ex) {
	    throw new org.nlogo.api.ExtensionException("Bad patch in Init."
						       + ex.getMessage());
	    
	}
	initDrawing(w,ec);
	addAgents(w);
    }

    /** 
	Initialize various parts of blank music "score".
	drawing sits above world, below turtles
    */
    
    private void initDrawing(World w, ExtensionContext context) {
	java.awt.image.BufferedImage drawing = context.getDrawing();
	Graphics2D g = drawing.createGraphics();
	g.setColor(java.awt.Color.WHITE);
	g.setStroke(new BasicStroke(P.LINE_THICKNESS));
	int xmax = (int) (P.XMAX * P.PATCHSIZE);
	int ymax = (int) (P.YMAX * P.PATCHSIZE);
	// draw voice delimiters
	for (int y = 0; y < ymax;
	     y += P.PATCHESPERVOICE * P.PATCHSIZE) {
	    g.drawLine(0,y,xmax,y);	
	}
	// draw measure delimiters
	for (int x = 0; x < xmax;
	     x += P.MAXNOTESPERMEASURE * P.PATCHSIZE) {
	    g.drawLine(x,0,x,ymax);	
	}
    }

    /**
       Add agents: one for each drummer type and one for each voice.
    */
    private void addAgents(World w) throws ExtensionException {
	P.drums = new Voice[P.NDRUMS];
	P.voices = new Voice[P.NVOICES];
	double x = 0;
	double y = 0.5;
	int size = 1;
	// assign drums
	for (int i = 0; i < P.NDRUMS; i++, y+=1.0) {
	    P.drums[i] = new Voice(w,P.dlist[i],P.dcolor[i],x,y,size);
	}
	// assign voices
	y += (P.PATCHESPERVOICE / 2) + 0.5;
	size = P.PATCHESPERVOICE;
	for (int i = 0; i < P.NVOICES; i++, y+=P.PATCHESPERVOICE) {
	    P.voices[i] = new Voice(w,P.vlist[i],P.vcolor[i],x,y,size);
	}
    }

    
    /**
       Get var id of PCOLOR
    */
    private int getPcolorID(World w) throws ExtensionException {
	int pcoloridx = 0;
	pcoloridx = w.patchesOwnIndexOf("PCOLOR");
	if (pcoloridx < 0) {
	    throw new ExtensionException("patch does not own pcolor");
	}
	return pcoloridx;
    }



} // end Init class

