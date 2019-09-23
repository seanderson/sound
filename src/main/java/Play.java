package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.SyntaxJ;
import org.nlogo.core.Syntax;
import org.nlogo.nvm.Workspace;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.agent.World;

public class Play implements Command {


    //
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax();
    }

    /*
      Take one step. Play drums/voices on new patch.
    */
    public void perform(Argument args[], Context context)
            throws ExtensionException {

        Note note = null;
        int vel = 100;

        ExtensionContext ec = (ExtensionContext) context;
        Workspace ws = ec.workspace();
        World w = ws.world();
        Voice voc = null;
        Patch p = null;
        try {
            // Each drum on one patch only.
            for (int i = 0; i < P.NDRUMS; i++) {
                voc = P.drums[i];
                voc.fd(1);
                p = w.getPatchAt(voc.agent.xcor(),
                        voc.agent.ycor());
                // brightness codes drum velocity!
                if (!p.pcolor().equals(P.DBLACK)) {
                    playdrumnote(voc,(double) (p.pcolor()));

                }
            }
            // Voices occupy many patches at one xcor value, but are
            // univocal: only one patch can have a note.
            for (int i = 0; i < P.NVOICES; i++) {
                voc = P.voices[i];
                voc.fd(1);
                note = getNote(w, i); // get index of voice note to play
                playnote(i, note);
            }

        } catch (org.nlogo.api.AgentException ex) {
        }
    }

    // Play note for voice id.
    // SoundExtension.playNote is in terms of msec,
    // but v.dur is in minimal note unit.
    // playWav ignores v.dur and plays out the entire wav.
    public static void playnote(int vid, Note note) //int noteidx)
            throws ExtensionException {
        Voice v = P.voices[vid];
        if (note != null) {
            if (v.isMidi())
                SoundExtension.playNote(v.instrument,
                        note.getPitch(),
                        note.vel(v), // convert voice's velocity for this note
                        v.dur * P.MIN_NOTE_DUR);
            else
                SoundExtension.playWav(v, note.getScalepos(),
                        v.dur);
        }
    }


    public static void playdrumnote(Voice voc, double pcolor)
            throws ExtensionException {
        // brightness encodes velocity
            if (voc.isMidi()) {
                int vel = (int) ((127 * (((int)pcolor) % 10)) / 9.0);
                SoundExtension.playDrum(voc.instrument, vel);
            }
            else {
                int vel = (int)pcolor % 10;
                SoundExtension.playDrumWav(voc, vel);
            }
        }




    /* 
       Return note for colored patch governed by this voice; -1 if
       none found.
     */
    public static Note getNote(World w, int vid) throws AgentException {
        Patch p = null;
        // Find first patch in range that is not black.
        double x = P.voices[vid].agent.xcor();
        double y = P.voices[vid].agent.ycor() - (P.PATCHESPERVOICE / 2);
        for (int i = 0; i < P.PATCHESPERVOICE; i++) {
            p = w.getPatchAt(x, y + i);
            if (!p.pcolor().equals(P.DBLACK)) {
                return new Note(P.voices[vid].note(i),(Double) p.pcolor(),i);
            }
        }
        return null; // no colored patch found
    }


} // end Play
