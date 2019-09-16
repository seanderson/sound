package org.nlogo.extensions.sound;

/**
 * To allow multiple events to add to the output sound, we create a
 * single SourceDataLine and feed it from this class.
 * <p>
 * This class allows client classes to add to its data blocks, assuming that
 * all waveforms are of the same WAV/etc. format stored in bytes.
 * Addapted from https://stackoverflow.com/questions/26265575/playing-multiple-byte-arrays-simultaneously-in-java
 */

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

public class AudMixer {
    private AudioFormat format;
    private static final int CHANNELS = 1; // must be mono so far
    public static final int BUFFER_SIZE_FRAMES = 2400; //4800;
    public static final int SAMPLE_RATE = 44100;
    public static final int NBITS = 16; // Assume 16 bit input (little endian)

    // Queue hold blocks of data to send to line and use for mixing.
    private static class QBlock {
        final long when;
        final short[] data; // All data dealt with as 16-bit

        public QBlock(long when, short[] dat) {
            this.when = when; // time to play
            this.data = dat;  // what to play
        }
    }

    private final List<QBlock> finished = new ArrayList<>(); // completed
    private final short[] mixBuffer = new short[BUFFER_SIZE_FRAMES * CHANNELS];
    private final byte[] audioBuffer = new byte[2 * BUFFER_SIZE_FRAMES * CHANNELS];

    private final Thread thread;
    private final AtomicLong position = new AtomicLong();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ConcurrentLinkedQueue<QBlock>
            scheduledBlocks = new ConcurrentLinkedQueue<>();


    public AudMixer() {
        thread = new Thread(() -> run());
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        running.set(false);
    }

    /**
     gets the play cursor. note - this is not accurate and
     must only be used to schedule blocks relative to other blocks
     (e.g., for splitting up continuous sounds into multiple blocks)
     */
    public long position() {
        return position.get();
    }

    /**
     Put copy of audio block into queue so we don't
     have to worry about caller messing with it afterwards
     */
    public void mix(long when, short[] block) {
        scheduledBlocks.add(new QBlock(when, Arrays.copyOf(block, block.length)));
    }

    /**
     Assumes  mixer 0, line 0 is output
     */
    private void run() {
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        try (Mixer mixer = AudioSystem.getMixer(mixerInfo[0])) {
            Line.Info[] lineInfo = mixer.getSourceLineInfo();
            try (SourceDataLine line = (SourceDataLine) mixer.getLine(lineInfo[0])) {
                line.open(new AudioFormat(SAMPLE_RATE,
                        NBITS, CHANNELS,
                        true, false), BUFFER_SIZE_FRAMES);
                line.start();
                while (running.get())
                    sendSingleBuffer(line);
                line.stop();
            }
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            t.printStackTrace();
        }
    }

    /**
     Consider all scheduledBlocks.  Add them to block
     that will be written to the line.
     */
    private void sendSingleBuffer(SourceDataLine line) {

        Arrays.fill(mixBuffer, (short) 0);
        long bufferStartAt = position.get();

        // mixdown audio blocks
        for (QBlock block : scheduledBlocks) {

            int blockFrames = block.data.length / CHANNELS;

            // block fully played - mark for deletion
            if (block.when + blockFrames <= bufferStartAt) {
                finished.add(block);
                continue;
            }

            // block starts after end of current buffer
            if (bufferStartAt + BUFFER_SIZE_FRAMES <= block.when)
                continue;

            // mix in part of the block which overlaps current buffer
            // note that block may have already started in the past
            // but extends into the current buffer, or that it starts
            // in the future but before the end of the current buffer
            int blockOffset = Math.max(0, (int) (bufferStartAt - block.when));
            int blockMaxFrames = blockFrames - blockOffset;
            int bufferOffset = Math.max(0, (int) (block.when - bufferStartAt));
            int bufferMaxFrames = BUFFER_SIZE_FRAMES - bufferOffset;
            for (int f = 0; f < blockMaxFrames && f < bufferMaxFrames; f++)
                for (int c = 0; c < CHANNELS; c++) {
                    int bufferIndex = (bufferOffset + f) * CHANNELS + c;
                    int blockIndex = (blockOffset + f) * CHANNELS + c;
                    mixBuffer[bufferIndex] += block.data[blockIndex];
                }
        }

        scheduledBlocks.removeAll(finished);
        finished.clear();
        // Interpret bytes of mixBuffer as little endian shorts
        // which are then placed into the audiobuffer.
        ByteBuffer.wrap(audioBuffer).
                order(ByteOrder.LITTLE_ENDIAN).
                asShortBuffer().
                put(mixBuffer);
        line.write(audioBuffer, 0, audioBuffer.length);
        position.addAndGet(BUFFER_SIZE_FRAMES);
    }


} // end of AudMixer
