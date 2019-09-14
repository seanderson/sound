package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import org.nlogo.nvm.Workspace;
import org.nlogo.nvm.ExtensionContext;

import org.nlogo.agent.World;
import org.nlogo.agent.Patch;



/**
 * Allow user to unpaint patch colors in various voices using the mouse.
 * Patches updated to that voice's color.
 */
public class UnPaintMelody implements Command {


    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax();
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {
        // create a NetLogo list for the result
        LogoListBuilder list = new LogoListBuilder();

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
                double x =  (ws.mouseXCor()); // patch coords.
                double y = (ws.mouseYCor());
                int voc = (int)(y / P.PATCHESPERVOICE) - 1;
                if (voc >= 0 && voc < P.vcolor.length) {  // zero is for rhythm
                    p = w.getPatchAt(x , y);
                    if (!p.pcolor().equals(P.BLACK)) { // unset sound
                        p.setPatchVariable(pcoloridx, P.DBLACK);

                    }
                }
            }

        } catch (org.nlogo.api.AgentException ex) {
            throw new org.nlogo.api.ExtensionException("Bad patch in music.");
        }

    }
}

