package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import org.nlogo.api.Context;

/**
 * Set scale/tonic for a voice.
 */
public class SetDrum implements Command {

    // set-drum ID INST-STRING
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[]{
                Syntax.NumberType(),
                Syntax.StringType()}
        );
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {
        int drumid;
        int instrument;

        try {
            drumid = args[0].getIntValue();
            instrument = SoundExtension.getDrum(args[1].getString());
        } catch (LogoException e) {
            throw new ExtensionException("Error in SetDrum: " + e.getMessage());
        }


        if (drumid < 0 || drumid >= P.NDRUMS)
            throw new ExtensionException("Bad voice ID: " + drumid);

        P.drums[drumid].instrument = instrument;
        P.drums[drumid].isMidi = true;

    }
}
