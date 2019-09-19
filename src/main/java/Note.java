package org.nlogo.extensions.sound;

// Represent all info for a single note.
public class Note {
    private int pitch; // midi note pitch index
    private int vol; // volume of note (0 to 9, limited by integer values of color)

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
/*    public Note(int midipitch, int pitchcolor) {
        pitch = midipitch;
        vol = color2volume(pitchcolor);
    }
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
        return pitchcolor.intValue() % P.VELOCITY_MAX;

    }
    public static int color2volume(int pitchcolor) {
        return pitchcolor % P.VELOCITY_MAX;

    }

    /**
     * Converts voice's default velocity by including this note's scale (0-9).
     * @param v this voice
     * @return NetLogo velocity for sound (0 to 127)
     */
    public int vel(Voice v) {
        int vdiff = (int) (vol - 10 * P.DEFAULT_VOLUME); // -5 to +5 change relative to default.
        return (int) ( (v.vel + (P.VELOCITY_MAX - v.vel) * (vdiff/ (P.NUMVOLUMES/2.0))) % P.VELOCITY_MAX);
    }


}
