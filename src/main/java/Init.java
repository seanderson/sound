package org.nlogo.extensions.sound;

import org.nlogo.agent.AgentSet;
import org.nlogo.agent.Patch;
import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.core.WorldDimensions;

import org.nlogo.nvm.Workspace;
import org.nlogo.nvm.ExtensionContext;

import org.nlogo.agent.World;

import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.nlogo.api.ExtensionException;

/**
 * Initialize world for voices, one lick.
 */
public class Init implements Command {


    private static ArrayList<PatchInfo> patchlist = null; // for stashing patch info

    private static class PatchInfo {
        Double pcolor;
        int x;
        int y;
        public PatchInfo(int thex, int they, Double thepcolor) {
            x = thex;
            y = they;
            pcolor = thepcolor;
        }
    }

    // Init NUM_VOICES
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[]{
                //Syntax.NumberType(),
                Syntax.NumberType()}
        );
    }


    // Command execution starts here.
    public void perform(Argument args[], Context context)
            throws ExtensionException {
        int nvoices = 4;

        try {
            nvoices = args[0].getIntValue();
            if (nvoices < 0 || nvoices > 10)
                throw new ExtensionException("Bad init num voices: " + nvoices);
        } catch (LogoException e) {
            throw new ExtensionException("Error in Init: " + e.getMessage());
        }


        new P(nvoices); // init all global params

        ExtensionContext ec = (ExtensionContext) context;
        Workspace ws = ec.workspace();

        resizeWorld(ec);

        World w = ws.world();

        int pcoloridx = getPcolorID(w);
        try {
            for (int x = 0; x < P.XMAX; x++)
                for (int y = 0; y < P.YMAX; y++)
                    w.getPatchAt(x, y).setPatchVariable(pcoloridx, P.DBLACK);
        } catch (AgentException ex) {
            throw new ExtensionException("Bad patch in Init."
                    + ex.getMessage());

        }
        initDrawing(ec);
        addAgents(w);
    }

    /**
     * Initialize various parts of blank music "score".
     * drawing sits above world, below turtles
     */

    static void initDrawing(Context context) {
        int alpha = 100;
        java.awt.Color white_trans = new java.awt.Color(255, 255, 255, alpha);
        java.awt.Color gray_trans = new java.awt.Color(100, 100, 100, alpha);
        java.awt.Color gray_verytrans = new java.awt.Color(100, 100, 100, alpha / 2);

        context.workspace().clearDrawing();

        java.awt.image.BufferedImage drawing = context.getDrawing();
        Graphics2D g = (Graphics2D) drawing.getGraphics();
        int xmax = (int) (P.XMAX * P.PATCHSIZE);
        int ymax = (int) (P.YMAX * P.PATCHSIZE);

        // draw voice delimiters
        g.setColor(white_trans);
        g.setStroke(new BasicStroke(1 * P.LINE_THICKNESS));
        for (int y = (int) (1 * P.PATCHSIZE); y <= ymax;
             y += P.PATCHESPERVOICE * P.PATCHSIZE) {
            g.drawLine(0, y, xmax, y);
        }
        // Draw lines at each beat
        g.setColor(gray_trans);
        g.setStroke(new BasicStroke(P.LINE_THICKNESS));
        int y0 = P.PATCHSIZE.intValue();
        ymax += y0;


        for (int x = 0; x < xmax;
             x += 4 * P.PATCHSIZE) {
            if ((x / (4 * P.PATCHSIZE)) % 2 == 1) // upbeats 2 and 4
                g.setColor(gray_verytrans);
            else
                g.setColor(gray_trans);
            g.drawLine(x, y0, x, ymax);
        }
        // draw measure delimiters
        g.setColor(white_trans);
        g.setStroke(new BasicStroke(2 * P.LINE_THICKNESS));
        for (int x = 0; x < xmax;
             x += P.MAXNOTESPERMEASURE * P.PATCHSIZE) {
            g.drawLine(x, y0, x, ymax);
        }


    }

    /**
     * Add agents: one for each drummer type and one for each voice.
     */
    static void addAgents(World w) throws ExtensionException {
        int tonic = 36;
        P.drums = new Voice[P.NDRUMS];
        P.voices = null;
        if (P.NVOICES > 0) P.voices = new Voice[P.NVOICES];
        double x = -1;
        double y = 0.0; //patch center at 0.0
        int size = 1;

        // assign drums
        for (int i = 0; i < P.NDRUMS; i++, y += 1.0) {

            P.drums[i] = new Voice(w, SoundExtension.getDrum(P.DEFAULT_DRUMS[i]),
                    P.dcolor[i], x, y, size,
                    tonic, Scale.PENTATONIC);
        }
        // assign voices
        y += (P.PATCHESPERVOICE / 2);
        size = P.PATCHESPERVOICE;
        for (int i = 0; i < P.NVOICES; i++, y += P.PATCHESPERVOICE) {

            P.voices[i] = new Voice(w,
                    SoundExtension.getInstrument(P.DEFAULT_INSTRUMENTS[i]),
                    P.vcolor[i], x, y, size,
                    tonic, Scale.PENTATONIC);
            tonic += 12;
        }
    }

    /*
       Import may save absolute path.  Check for existence of path, if it fails
       modify dir to be final directory in P.drums[i].dir or P.voices.[i].dir.
     */
    private static void fixFileLocations(Context context) throws ExtensionException {


        for (int i = 0; i < P.NDRUMS; i++) {
            if (P.drums[i].isMidi()) continue;
            //System.out.println("drum file " + P.drums[i].dir);
            File f = new File(P.drums[i].dir);
            //System.out.println("pathsep " + File.pathSeparator);
            if (!f.exists() || !f.isDirectory()) {
                String[] tokens = P.drums[i].dir.split(File.separator+"++");
                //for (String t: tokens) System.out.println("token " + t);
                try {
                    P.drums[i].dir = context.attachCurrentDirectory(tokens[tokens.length - 1]);
                }
                catch (MalformedURLException ex) {
                    throw new ExtensionException(ex.getMessage());
                }

            }

        }
        for (int i = 0; i < P.NVOICES; i++) {
            if (P.voices[i].isMidi()) continue;
            //System.out.println("file " + P.voices[i].dir);
            File f = new File(P.voices[i].dir);

            if (!f.exists() || !f.isDirectory()) {
                String[] tokens = P.voices[i].dir.split(File.separator + "++");
                //for (String t: tokens) System.out.println("token " + t);
                try {
                    P.voices[i].dir = context.attachCurrentDirectory(tokens[tokens.length - 1]);
                }
                catch (MalformedURLException ex) {
                    throw new ExtensionException(ex.getMessage());
                }

            }
        }


    }

    /**
       If agents exist already, probably after an importWorld, just
       attach agents to already created voices.  Reload waves if necessary.

     */
    public static void attachAgents(Context context, World w) throws ExtensionException {

        fixFileLocations(context);

        // assign drums
        for (int i = 0; i < P.NDRUMS; i++) {
            P.drums[i].agent = w.getTurtle(P.drums[i].agentID);
            if (P.drums[i].agent == null)  {
                System.out.println("No drum agent " + i);
            }
            if (!P.drums[i].isMidi()) {
                System.out.println("drum not midi " + i + " " + P.drums[i].wavfile);
                P.drums[i].setDrumWaveform(P.drums[i].dir, P.drums[i].wavfile);
            }
        }
        // assign voices

        for (int i = 0; i < P.NVOICES; i++) {
            P.voices[i].agent = w.getTurtle(P.voices[i].agentID);
            if (P.voices[i].agent == null) {
                System.out.println("No voice agent " + i);
            }
            if (!P.voices[i].isMidi()) {
                System.out.println("voice not midi " + i);
                P.voices[i].setWaveform(P.voices[i].dir, P.voices[i].wavfile);
            }
        }

    }


    /**
     * Get var id of PCOLOR
     */
    private static int getPcolorID(World w) throws ExtensionException {
        int pcoloridx = 0;
        pcoloridx = w.patchesOwnIndexOf("PCOLOR");
        if (pcoloridx < 0) {
            throw new ExtensionException("patch does not own pcolor");
        }
        return pcoloridx;
    }


    /**
     * Add agents: one for each drummer type and one for each voice.
     */
    private static void updateAgentsPositions(World w)
            throws ExtensionException {
        int tonic = 36;

        double x = -1;
        double y = 0.0; //patch center at 0.0
        int size = 1;
        try {
            // assign drums
            for (int i = 0; i < P.NDRUMS; i++, y += 1.0) {
                P.drums[i].agent.xandycor(x, y);
            }
            // assign voices
            y += (P.PATCHESPERVOICE / 2);
            size = P.PATCHESPERVOICE;
            for (int i = 0; i < P.NVOICES; i++, y += P.PATCHESPERVOICE) {
                P.voices[i].agent.xandycor(x, y);
            }
        } catch (org.nlogo.api.AgentException ex) {
            throw new org.nlogo.api.ExtensionException("Bad patch in updateAgentsPositions."
                    + ex.getMessage());

        }
    }




    /**
     * Resize world using current parameters in P (PATCHSIZE, XMAX, YMAX).
     *
     * @param context
     * @throws ExtensionException
     */
    public static void resizeWorld(Context context)
            throws ExtensionException {
        ExtensionContext ec = (ExtensionContext) context;
        Workspace ws = ec.workspace();
        ws.clearDrawing();

        int newMinX = 0;
        int newMaxX = P.XMAX;
        int newMinY = 0;
        int newMaxY =  P.YMAX;
        ws.waitFor // in event loop
                (new org.nlogo.api.CommandRunnable() {
                    public void run() {
                        ws.setDimensions
                                (new org.nlogo.core.WorldDimensions(newMinX, newMaxX,
                                        newMinY, newMaxY), P.PATCHSIZE);
                    }
                });


    }

    /**
     * Stash copy of patches for later recovery.
     * @param w the world
     */
    public static void stashPatches(World w) throws ExtensionException {
        Init.patchlist = savePatches(w);
    }

    /**
     * restore last copy of stashed patches saved using stashPatches.
     * @param w the world
     */
    public static void restorePatches(World w) throws ExtensionException {
        if (Init.patchlist != null) fixPatches(w,Init.patchlist);
    }



    /* Save list of colorized patches. */
    private static ArrayList<PatchInfo> savePatches(World w)
            throws ExtensionException {
        int pcoloridx = w.patchesOwnIndexOf("PCOLOR");
        ArrayList<PatchInfo> patches = new ArrayList<PatchInfo>();
        try {
            for (int i = 0; i < P.XMAX; i++) {
                for (int j = 0; j < P.YMAX; j++) {
                    org.nlogo.agent.Patch p = w.getPatchAt(i,j);
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
    private static void fixPatches(World w,ArrayList<PatchInfo> patches)
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

    public static void fixAgents(World w)
            throws ExtensionException {
        double x = -1;
        double y = 0.0; //patch center at 0.0

        // create drums
        for (int i = 0; i < P.NDRUMS; i++, y += 1.0) {
            P.drums[i].resetAgent(w, x, y);
        }
        // assign voices
        y += (P.PATCHESPERVOICE / 2);

        for (int i = 0; i < P.NVOICES; i++, y += P.PATCHESPERVOICE) {

            P.voices[i].resetAgent(w, x, y);
        }

    }







    } // end Init class

