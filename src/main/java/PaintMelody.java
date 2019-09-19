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
 * Allow user to paint patch colors in various voices using Mouse.
 * Patches updated to that voice's color.
 */
public class PaintMelody implements Command {


    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax();
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {


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
                double x = (ws.mouseXCor()); // patch coords.
                double y = (ws.mouseYCor());
                if (y > P.YMAX - 1) return;
                int voc = (int) (y / P.PATCHESPERVOICE); // voice
                if (voc > 0) {  // zero is for drum voices
                    voc--; // usual zero-based voice
                    p = w.getPatchAt(x, y);
                    if (p.pcolor().equals(P.BLACK)) {  // only paint empty patch
                        // clear other patches for this voice.
                        for (int iy = (voc + 1) * P.PATCHESPERVOICE; iy < (voc + 2) * P.PATCHESPERVOICE; iy++) {
                            Patch tmp = w.getPatchAt(x, iy);
                            tmp.setPatchVariable(pcoloridx, P.DBLACK);
                        }
                        // color this patch
                        p.setPatchVariable(pcoloridx, P.vcolor[voc]);

                        Play.playnote(voc,
                                new Note (P.voices[voc].note( (int) y % P.PATCHESPERVOICE),
                                        P.vcolor[voc]) );
                    }
                }
            }

        } catch (org.nlogo.api.AgentException ex) {
            throw new org.nlogo.api.ExtensionException("Bad patch in music.");
        }

    }
}

