package org.nlogo.extensions.sound;

import org.nlogo.agent.Patch;
import org.nlogo.agent.World;
import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.nvm.Workspace;


/**
 * Copy notes from voice1 to voice2.
 * Offset by midi number.
 */
public class DeleteDrum implements Command {
    /*
      Delete drum voice from begin to end measures inclusive.
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

        if (vid < 0 || vid >= P.NDRUMS)
            throw new ExtensionException("Bad drum voice id: " + vid + " in DeleteDrum");

        if (beginmeasure > endmeasure ||
                beginmeasure < 0 || beginmeasure >= P.NMEASURES ||
                endmeasure < 0 || endmeasure >= P.NMEASURES)
            throw new ExtensionException("Bad measures specified: " +
                    beginmeasure + " to " + endmeasure);

        delete((ExtensionContext) context, vid, beginmeasure, endmeasure);


    }


    /*
      Delete all drum sounds from measure a to b inclusive.
      Assumes a<=b.
    */
    public static void delete(ExtensionContext ec, int vid, int a, int b)
            throws ExtensionException {
        Workspace ws = ec.workspace();
        World w = ws.world();
        int pcoloridx = w.patchesOwnIndexOf("PCOLOR");
        Patch p = null;

        try {
            int ymin = vid; // 1 is rhythm offset
            int ymax = vid+1;
            int xmin = a * P.MAXNOTESPERMEASURE;
            int xmax = (b + 1) * P.MAXNOTESPERMEASURE;

            for (int x = xmin; x < xmax; x++) {
                for (int y = ymin; y < ymax; y++) {
                    p = w.getPatchAt(x, y);
                    p.setPatchVariable(pcoloridx, P.DBLACK);
                }
            }
        } catch (AgentException ex) {
            throw new ExtensionException("Bad patch in DeleteDrum.");
        }


    }

}
