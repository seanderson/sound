package org.nlogo.extensions.sound;


import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import org.nlogo.nvm.Workspace;
import org.nlogo.nvm.ExtensionContext;

import org.nlogo.agent.World;
import org.nlogo.agent.Patch;
import java.util.ArrayList;


public class AddMeasures implements Command {


    public Syntax getSyntax() {
        /** Add N measures to existing world. */
        return SyntaxJ.commandSyntax(new int[]{Syntax.NumberType()

                }
        );
    }

    class PatchInfo {
        Double pcolor;
        int x;
        int y;
        public PatchInfo(int thex, int they, Double thepcolor) {
            x = thex;
            y = they;
            pcolor = thepcolor;
        }
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {

        int num;

        try {
            num = args[0].getIntValue();
        } catch (LogoException e) {
            throw new ExtensionException(e.getMessage());
        }

        if (num <= 0 || num > 32)
            throw new ExtensionException("Number of measures added must be between 1 and 32");

        ExtensionContext ec = (ExtensionContext) context;
        Workspace ws = ec.workspace();
        ArrayList<PatchInfo> patchlist = savePatches(ws.world());

        P.NMEASURES = P.NMEASURES + num;
        P.XMAX = P.NMEASURES * P.MAXNOTESPERMEASURE;

        // Must re-dimension the world to include new patches.
        ws.setDimensions(new WorldDimensions3D(0, P.XMAX - 1, 0,
                        P.YMAX, 0, 1,
                        P.PATCHSIZE,
                        P.WRAP, P.WRAP, P.WRAP),
                P.PATCHSIZE);
        World w = ws.world();

        Init.initDrawing(ec);
        fixAgents(w);
        fixPatches(w,patchlist);
    }

    /* Save list of colorized patches. */
    static ArrayList<PatchInfo> savePatches(World w)
                throws ExtensionException {
        int pcoloridx = w.patchesOwnIndexOf("PCOLOR");
        ArrayList<PatchInfo> patches = new ArrayList<PatchInfo>();
        try {
            for (int i = 0; i < P.XMAX; i++) {
                for (int j = 0; j < P.YMAX; j++) {
                    Patch p = w.getPatchAt(i,j);
                    if (!p.pcolor().equals(P.DBLACK)) {
                        patches.add(new PatchInfo(i,j,(Double)p.pcolor()));
                    }
                }
            }
        }
        catch (AgentException e) {
            throw new ExtensionException(e.getMessage());
        }
        return patches;
    }

    // Use ArrayList to restore patch colors to previous values.
    static void fixPatches(World w,ArrayList<PatchInfo> patches)
            throws ExtensionException {

        int pcoloridx = w.patchesOwnIndexOf("PCOLOR");
        try {
            for (PatchInfo pi : patches) {
                Patch p = w.getPatchAt(pi.x, pi.y);
                p.setPatchVariable(pcoloridx, pi.pcolor);
                //System.out.println("xx " + (Double) (pi.pcolor()));
            }
        } catch (AgentException e) {
            throw new ExtensionException(e.getMessage());
        }
    }

    private static void fixAgents(World w)
            throws ExtensionException {
        double x = -1;
        double y = 0.0; //patch center at 0.0

        // create drums
        for (int i = 0; i < P.NDRUMS; i++, y += 1.0) {
            P.drums[i].resetAgent(w,x,y);
        }
        // assign voices
        y += (P.PATCHESPERVOICE / 2);

        for (int i = 0; i < P.NVOICES; i++, y += P.PATCHESPERVOICE) {

            P.voices[i].resetAgent(w,x,y);
        }

    }

}
