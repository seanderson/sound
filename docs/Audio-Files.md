# Overview

Any audio files that you record and use as voice notes or drum notes __must__ be in this format:
* 16-bit resolution
* WAVE audio, Microsoft PCM (.wav suffix)
* 44100 Herz sampling rate

It's probably best if all audio files you use in NetLogo are in this format, though when using `sound:loop-play` and `sound:play-sound` you can probably use other formats and sample rates.

# Use Audacity to convert to the correct file format.

To convert your file to the correct format, I recommend that you use Audacity, which is available on all RKC 100 computers.  In addition, you can download it freely for your own computer.


### Step 1: Open the file

Within Audacity, open your file.  It will appear as a waveform.

### Step 2: Select the entire waveform

The easiest way to do this is to hit Ctrl, then A (i.e., Ctrl-A).

### Step 3: Stereo to Mono

[[images/audacity-main.png]]

If your recording was in stereo, select the small triangle on the waveform panel (to the right of "IPhoneMsgA" in the figure and select *Split Stereo to Mono*.  If this results in two frames, remove one of them by selecting the X in the upper left of the waveform panel to remove that track.

###  Step 4: Fix the output sample rate.

Go to *Project Rate* at the bottom of the Audacity main screen on the left.  Choose 44100 from the pull-down menu.

### Step 5: Export the WAV

Choose *File > Export as WAV*.  In the window that opens you will see various choice in a pull-down menu just below the list of files.  Be sure to select *WAV (Microsoft) signed 16-bit PCM*.  You may want to save the output in a special folder, so be sure to pay attention to *Save in folder* and navigate to where you want the file saved.

### Step 6: Use in NetLogo

Keep your WAV files organized by collecting them in a folder just beneath where you have your NetLogo models.

