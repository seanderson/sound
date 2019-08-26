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
 * Copy notes from voice1 to voice2.
 * USAGE: copy-voice v1 v1measA v2 v2measA nmeas offset.
 */
public class CopyVoice implements Command {

    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[]{
                Syntax.NumberType(),
                Syntax.NumberType(),
                Syntax.NumberType(),
                Syntax.NumberType(),
                Syntax.NumberType(),
                Syntax.NumberType()}
        );
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {
        int src;
        int dest;
        int srcmeasA, destmeasA;
        int nmeas;
        int dmidi; // amount to add to dest voice

        try {
            src = args[0].getIntValue();
            srcmeasA = args[1].getIntValue();

            dest = args[2].getIntValue();
            destmeasA = args[3].getIntValue();
            nmeas = args[4].getIntValue();
            dmidi = args[5].getIntValue();
        } catch (LogoException e) {
            throw new ExtensionException("Error in CopyVoice: " + e.getMessage());
        }

        if (src < 0 || src >= P.NVOICES)
            throw new ExtensionException("Bad voice ID: " + src);
        if (dest < 0 || dest >= P.NVOICES)
            throw new ExtensionException("Bad voice ID: " + dest);
        if (srcmeasA < 0 || destmeasA < 0  || srcmeasA + nmeas  > P.NMEASURES || destmeasA + nmeas > P.NMEASURES)
            throw new ExtensionException("Measure specification outside range in CopyVoice");
        if ( nmeas < 0  )
            throw new ExtensionException("Range of measure copy is negative in CopyVoice");


        copy((ExtensionContext) context, src, srcmeasA, dest, destmeasA, nmeas, dmidi);


    }


    /*
      Copy melody from src to dest, offset by dmidi semitones (pos or neg).
      Copies nmeas of srcmeasA to destmeasA.
      Will elide any tones out of range.
    */
    public static void copy(ExtensionContext ec,
                            int src, int srcmeasA,
                            int dest, int destmeasA,
                            int nmeas, int dmidi)
            throws ExtensionException {
        Workspace ws = ec.workspace();
        World w = ws.world();
        int pcoloridx = w.patchesOwnIndexOf("PCOLOR");
        Patch p = null;
        Patch pdest = null;
        try {
            int y0_src = P.PATCHESPERVOICE * (1 + src); // 1 is rhythm offset
            int y0_dest = P.PATCHESPERVOICE * (1 + dest);

            int offset = (destmeasA - srcmeasA) * P.MAXNOTESPERMEASURE;
            int xmax = P.MAXNOTESPERMEASURE * (srcmeasA + nmeas);
            for (int x = srcmeasA * P.MAXNOTESPERMEASURE; x < xmax; x++) {
                int destx = x + offset;
                for (int y = y0_src; y < y0_src + P.PATCHESPERVOICE; y++) {
                    p = w.getPatchAt(x, y);
                    if (p.pcolor().equals(P.BLACK)) continue; // ignore empties

                    // clear all destination patches at this x
                    for (int yd = y0_dest; yd < y0_dest + P.PATCHESPERVOICE; yd++) {
                        pdest = w.getPatchAt(destx, yd);
                        pdest.setPatchVariable(pcoloridx, P.DBLACK);
                    }
                    // validate new y patch for destination voice
                    int newy = y0_dest + (y - y0_src) + dmidi;
                    if (newy >= y0_dest && newy < y0_dest + P.PATCHESPERVOICE) {
                        pdest = w.getPatchAt(destx, newy);
                        pdest.setPatchVariable(pcoloridx, P.vcolor[dest]);
                    }
                }
            }
        } catch (org.nlogo.api.AgentException ex) {
            throw new org.nlogo.api.ExtensionException("Bad patch in music.");
        }


    }

}
