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
 * Initialize world for voices, one lick.
 */
public class Rhythm implements Command {

    // USAGE: rhythm voiceID  STRING
    // string of 16 chars "9-------4-------" defines 16 beat rhythm
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[]{
                //Syntax.NumberType(),
                //Syntax.NumberType(),
                Syntax.NumberType(),
                Syntax.StringType()}
        );
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {
        // create a NetLogo list for the result
        LogoListBuilder list = new LogoListBuilder();
        int voice, nbeats, basebeat;
        String pttn;
        try {
            voice = args[0].getIntValue();
            //nbeats = args[1].getIntValue();
            //basebeat = args[2].getIntValue();
            pttn = args[1].getString();
        } catch (LogoException e) {
            throw new ExtensionException(e.getMessage());
        }

        ExtensionContext ec = (ExtensionContext) context;
        Workspace ws = ec.workspace();

        World w = ws.world();

        try {
            Patch p = w.getPatchAt(1, 1);
            int pcoloridx = w.patchesOwnIndexOf("PCOLOR");
            if (pcoloridx < 0) {
                throw new ExtensionException("patch does not own pcolor");
            }
            // add measures of rhythm at voice
            for (int x = 0, pos = 0; x < P.XMAX;
                 x++, pos = (pos + 1) % P.MAXNOTESPERMEASURE) {
                p = w.getPatchAt(x, voice);
                if (pttn.charAt(pos) == P.SIL)
                    p.setPatchVariable(pcoloridx, new Double(P.BLACK));
                else if (Character.isDigit(pttn.charAt(pos))) {
                    p.setPatchVariable(pcoloridx,
                            new Double(P.dcolor[voice] + Character.getNumericValue(pttn.charAt(pos))));
                } else { //ignore
                    p.setPatchVariable(pcoloridx, new Double(P.BLACK));
                }
            }
        } catch (org.nlogo.api.AgentException ex) {
            throw new org.nlogo.api.ExtensionException("Bad patch in music.");
        }

    }
}

