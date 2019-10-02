package org.nlogo.extensions.sound;

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

import org.nlogo.api.ExtensionException;

/**
 * Initialize world for voices, one lick.
 */
public class Init implements Command {
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
        World w = ws.world();

        ws.setDimensions(w.getDimensions(), P.PATCHSIZE);
      /*  ws.setDimensions(new WorldDimensions3D(0, P.XMAX - 1, 0,
                        P.YMAX, 0, 1,
                        P.PATCHSIZE,
                        P.WRAP, P.WRAP, P.WRAP),
                P.PATCHSIZE);*/
        w = ws.world();
        int pcoloridx = getPcolorID(w);
        try {
            for (int x = 0; x < P.XMAX; x++)
                for (int y = 0; y < P.YMAX; y++)
                    w.getPatchAt(x, y).setPatchVariable(pcoloridx, P.DBLACK);
        } catch (org.nlogo.api.AgentException ex) {
            throw new org.nlogo.api.ExtensionException("Bad patch in Init."
                    + ex.getMessage());

        }
        initDrawing(w, ec);
        addAgents(w);
    }

    /**
     * Initialize various parts of blank music "score".
     * drawing sits above world, below turtles
     */

    static void initDrawing(World w, ExtensionContext context) {
        int alpha = 100;
        java.awt.Color white_trans = new java.awt.Color(255, 255, 255, alpha);
        java.awt.Color gray_trans = new java.awt.Color(100, 100, 100, alpha);
        java.awt.Color gray_verytrans = new java.awt.Color(100, 100, 100, alpha / 2);


        w.clearDrawing();
        java.awt.image.BufferedImage drawing = context.getDrawing();
        Graphics2D g = (Graphics2D) drawing.getGraphics();
        int xmax = (int) (P.XMAX * P.PATCHSIZE);
        int ymax = (int) (P.YMAX * P.PATCHSIZE);
        // clearing here fails
        // g.setColor(java.awt.Color.BLACK);
        // g.drawRect(0,0,xmax,ymax);

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
    private static void addAgents(World w) throws ExtensionException {
        int tonic = 36;
        P.drums = new Voice[P.NDRUMS];
        P.voices = new Voice[P.NVOICES];
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
       If agents exist already, probably after an importWorld, just
       attach agents to already created voices.  Reload waves if necessary.

     */
    public static void attachAgents(World w) throws ExtensionException {


        // assign drums
        for (int i = 0; i < P.NDRUMS; i++) {
            P.drums[i].agent = w.getTurtle(P.drums[i].agentID);
            if (!P.drums[i].isMidi()) {
                P.drums[i].setWaveform(P.drums[i].dir, P.drums[i].wavfile);
            }
        }
        // assign voices

        for (int i = 0; i < P.NVOICES; i++) {
            P.voices[i].agent = w.getTurtle(P.voices[i].agentID);
            if (!P.voices[i].isMidi()) {
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
     * Resize world using a changed PATCHSIZE.
     *
     * @param context
     * @throws ExtensionException
     */

    public static void resizeWorld(Context context)
            throws ExtensionException {

        ExtensionContext ec = (ExtensionContext) context;
        Workspace ws = ec.workspace();
        ws.world().clearDrawing();
        System.out.println("gets to 0");
        ws.setDimensions(new

                        WorldDimensions3D(0, P.XMAX - 1, 0,
                        P.YMAX, 0, 1,
                        P.PATCHSIZE,
                        P.WRAP, P.WRAP, P.WRAP),

                P.PATCHSIZE);
        World w = ws.world();
        w.clearDrawing();
        System.out.println("gets to 1");
        // w.patchSize(P.PATCHSIZE);
        //ws.resizeView();  // not in event thread
        //ws.setDimensions(w.getDimensions(), P.PATCHSIZE);
        System.out.println("gets to 2");
        int pcoloridx = getPcolorID(w);

        initDrawing(w, ec);
        System.out.println("gets to 3");
        updateAgentsPositions(w);
        System.out.println("gets to 4");
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

} // end Init class

