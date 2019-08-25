package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.api.Context;


/**
 * Set waveform for a drum voice.
 */
public class DrumWaveform implements Command {

    // set-waveform VOICEID DIR WAV.  Use waveform for this drum.
    // DIR is relative to current directory.
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[]{
                Syntax.NumberType(),
                Syntax.StringType(),
                Syntax.StringType()}
        );
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {
        int vid;
        String dir, wavfile;

        try {
            vid = args[0].getIntValue();
            dir = context.attachCurrentDirectory(args[1].getString());
            wavfile = args[2].getString();
        } catch (LogoException e) {
            throw new ExtensionException(e.getMessage());
        } catch (java.net.MalformedURLException ex) {
            return;
        }

        if (vid < 0 || vid >=
                P.NDRUMS)
            throw new ExtensionException("Bad drum voice " + vid);

        P.drums[vid].setDrumWaveform(dir, wavfile);
    }
}


