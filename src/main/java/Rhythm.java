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
 * Create one measure of rhythm for one drum voice.
 */
public class Rhythm implements Command {

    // USAGE: rhythm voiceID  STRING STARTMEASURE NUMMEASURES
    // string of 16 chars "9-------4-------" defines 16 beat rhythm
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[]{
                Syntax.NumberType(),
                Syntax.StringType(),
                Syntax.NumberType(),
                Syntax.NumberType()
                }
        );
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {
        // create a NetLogo list for the result
        LogoListBuilder list = new LogoListBuilder();
        int voice, startmeas, nummeas;
        String pttn;
        try {
            voice = args[0].getIntValue();
            pttn = args[1].getString();
            startmeas = args[2].getIntValue();
            nummeas = args[3].getIntValue();
        } catch (LogoException e) {
            throw new ExtensionException(e.getMessage());
        }

        ExtensionContext ec = (ExtensionContext) context;
        Workspace ws = ec.workspace();

        World w = ws.world();

        // Validate input
        if (voice < 0 || voice >= org.nlogo.extensions.sound.P.NDRUMS)
            throw new org.nlogo.api.ExtensionException("Voice id out of range: " + voice);
        if (pttn.length() != P.MAXNOTESPERMEASURE)
            throw new org.nlogo.api.ExtensionException("Pttn string length incorrect: " + pttn.length());
        if (startmeas < 0 || startmeas >= org.nlogo.extensions.sound.P.NMEASURES)
            throw new org.nlogo.api.ExtensionException("Invalid start measure: " + startmeas);
        if (nummeas < 0 || startmeas + nummeas > org.nlogo.extensions.sound.P.NMEASURES)
            throw new org.nlogo.api.ExtensionException("Invalid measure count: " + nummeas);



        try {
            Patch p = w.getPatchAt(1, 1);
            int pcoloridx = w.patchesOwnIndexOf("PCOLOR");
            if (pcoloridx < 0) {
                throw new ExtensionException("patch does not own pcolor");
            }
            // add measures of rhythm at voice
            for (int x = startmeas * P.MAXNOTESPERMEASURE, pos = 0; x < (startmeas + nummeas) * P.MAXNOTESPERMEASURE;
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

