package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.api.Context;

/**
 * Set velocity (volume) for this voice: range 0 to 127
 */
public class SetLoudness implements Command {

    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[]{
                Syntax.NumberType(),
                Syntax.NumberType()}
        );
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {
        int vid;
        int vel;

        try {
            vid = args[0].getIntValue();
            vel = args[1].getIntValue();
        } catch (LogoException e) {
            throw new ExtensionException("Error in SetLoudness: " + e.getMessage());
        }


        if (vid < 0 || vid >= P.NVOICES)
            throw new ExtensionException("Bad voice ID: " + vid + " in SetLoudness");
        if (vel < 0 || vel > P.VELOCITY_MAX)
            throw new ExtensionException("Bad note velocity: " + vel + " in SetLoudness");

        P.voices[vid].vel = vel;


    }
}

