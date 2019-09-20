package org.nlogo.extensions.sound;

// Represent all info for a single note.
public class Note {
    private int pitch; // midi note pitch index
    private int vol; // volume of note (0 to 99, limited by integer values of color)

    // e.g., 40 to 49.9 is shades of yellow.

    /**
     * Create new Note using midipitch value and
     * interpretation of color brightness as volume.
     * @param midipitch
     * @param pitchcolor
     */
    public Note(int midipitch, Double pitchcolor) {
        pitch = midipitch;
        vol = color2volume(pitchcolor);

    }
    /**
     * Create new Note using midipitch value and
     * int value of color.
     * @param midipitch
     * @param pitchcolor
     */


    public int getPitch() {
        return pitch;
    }

    public void setPitch(int p) {
        this.pitch = p;
    }


    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }

    public static int color2volume(Double pitchcolor) {
        return ((int) (10 * pitchcolor)) % P.NUMVOLUMES;
    }
    // ones and tenths to yield integer from 0 to 99
/*    public static int color2volume(int pitchcolor) {
        return pitchcolor % P.VELOCITY_MAX;

    }*/

    /**
     * Converts voice's default velocity by including this note's scale (0-9).
     * @param v this voice
     * @return NetLogo velocity for sound (0 to 127)
     */
    public int vel(Voice v) {
        double vdiff = (vol - (P.NUMVOLUMES / 2));
        if (vdiff > 0) { //make louder
            return (int) (v.vel + vdiff * (P.VELOCITY_MAX - v.vel) / (P.NUMVOLUMES / 2));
        } else {
            return (int) (v.vel + vdiff * (v.vel) / (P.NUMVOLUMES / 2));
        }

    }


}
