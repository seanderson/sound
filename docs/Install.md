
# Installing custom NetLogo Sound

My directions assume you have uncompressed this archive (soundInstall)
on your Desktop.  You should also have installed NetLogo 6.1.0 on your
Desktop.

1. Install sound.jar

To install the new sound extension that includes music capabilities,
you need to copy sound.jar into the appropriate location in your
NetLogo installation.  If you have install NetLogo on your Desktop, it
is probably like this from my computer:

```console
cp sound.jar ~/Desktop/NetLogo\ 6.1.0/extensions/.bundled/sound/sound.jar
```


2. Run NetLogo (be sure it's the one in your Desktop)

In a terminal:

```console
open /Users/sven/Desktop/NetLogo\ 6.1.0/NetLogo\ 6.1.0.app
```

Change 'sven' to your userid.

3. Within NetLogo you can open the music1.nlogo program found in soundInstall.

NOTE: Documentation on the program is found at https://github.com/seanderson/sound/wiki

4. Your midi sounds may not be so great.  Get a file of better sounds at http://cognlp.org/tmp/

It's called soundbank-emg.sf2 and is about 140MB.

Simply copy this to /Users/YOURID/.gervill/soundbank-emg.sf2.
You will probably want this command, assuming the new sounds file is in your Downloads directory

```console
/bin/cp -f /Users/YOURID/Downloads/soundbank-emg.sf2  /Users/YOURID/.gervill/soundbank-emg.sf2
```

When you run NetLogo next time, this set of sounds should be used for midi.

5. If you create your own wav file (it must be at 44100 sample rate,
mono, and 16-bits) you can then create copies of the wav file at many
different pitches using the script soundInstall/runmake.sh

You need to provide 5 arguments:
runmake.sh wavfile output-root midi-num-of-wavfile lowest-midi-num-of-output highest-midi-num-of-output

This will make many files, which you should locate in a subdirectory (e.g. "wav").
This directory, containing this file must be in the same directory as your netlogo mode file (e.g., model1.nlogo).







