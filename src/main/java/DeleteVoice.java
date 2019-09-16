package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
// SEA
import org.nlogo.api.Context;

import org.nlogo.nvm.Workspace;
import org.nlogo.nvm.ExtensionContext;

import org.nlogo.agent.World;
import org.nlogo.agent.Patch;

/**
 * Copy notes from voice1 to voice2.
 * Offset by midi number.
 */
public class DeleteVoice implements Command {
    /*
      Delete voice from begin to end measures inclusive.
    */
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[]{
                Syntax.NumberType(),
                Syntax.NumberType(),
                Syntax.NumberType()}
        );
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {
        int vid;
        int beginmeasure;
        int endmeasure; // amount to add to dest voice

        try {
            vid = args[0].getIntValue();
            beginmeasure = args[1].getIntValue();
            endmeasure = args[2].getIntValue();
        } catch (LogoException e) {
            throw new ExtensionException("Error in DeleteVoice: " + e.getMessage());
        }

        if (vid < 0 || vid >= P.NVOICES)
            throw new ExtensionException("Bad voice id: " + vid + " in DeleteVoice");

        if (beginmeasure > endmeasure ||
                beginmeasure < 0 || beginmeasure >= P.NMEASURES ||
                endmeasure < 0 || endmeasure >= P.NMEASURES)
            throw new ExtensionException("Bad measures specified: " +
                    beginmeasure + " to " + endmeasure);

        delete((ExtensionContext) context, vid, beginmeasure, endmeasure);


    }


    /*
      Delete all voices sounds from measure a to b inclusive.
      Assumes a<=b.
    */
    public static void delete(ExtensionContext ec, int vid, int a, int b)
            throws ExtensionException {
        Workspace ws = ec.workspace();
        World w = ws.world();
        int pcoloridx = w.patchesOwnIndexOf("PCOLOR");
        Patch p = null;

        try {
            int ymin = P.PATCHESPERVOICE * (1 + vid); // 1 is rhythm offset
            int ymax = ymin + P.PATCHESPERVOICE;
            int xmin = a * P.MAXNOTESPERMEASURE;
            int xmax = (b + 1) * P.MAXNOTESPERMEASURE;

            for (int x = xmin; x < xmax; x++) {
                for (int y = ymin; y < ymax; y++) {
                    p = w.getPatchAt(x, y);
                    p.setPatchVariable(pcoloridx, P.DBLACK);
                }
            }
        } catch (org.nlogo.api.AgentException ex) {
            throw new org.nlogo.api.ExtensionException("Bad patch in music.");
        }


    }

}
