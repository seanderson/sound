# Sound-Music Wiki

This documents the changes made to NetLogo's Sound Extension to support music composition.

Command descriptions are organized around tasks you are likely to perform.  You must begin your NetLogo program with 
```NetLogo
extensions [ sound ] 
```
in order to take advantage of these extensions.  Each of the following commands is called by preceding it by the extension name.  For example, call `init` by using `sound:init`

## Contents

* [Initialization, Saving, and Reloading](#init)
* [Adding and deleting voice notes](#adding-and-deleting-voice-notes)
* [Adding and deleting drum voices](#adding-and-deleting-drum-voices)
* [Playing and looping](#playing-and-looping)
* [Changing features of a voice](#changing-features)
* [Copying Voices and Adding Measures](#adding-measures)
* [Change Beats-Per-Minute or Patch size](#changing-bpm)
* [Drum Names](#drum-names)
* [Instrument Names](#instrument-names)
* [Things you can do](https://github.com/seanderson/sound/wiki/Things-you-can-do)



<a name="init"></a>
### Initialization, Saving and Reloading 


**init _num-voices_**

Initializes a music program to have four measures of  `num-voices` voices and a maximum of 16 rhythm tracks.


**reinit**

To save a model, you should use `export-world`, which you can also call from the `File` pull-down menu.  To reload this same model, first run `import-world` and then you *must* also run `sound:reinit`.

## Adding and deleting voice notes

Any colored (non-black) patches in the world will be interpreted as some sort of sound.  Patch colors in NetLogo range from 0 to 140.  The ones digit of colors is interpreted as volume, ranging from 0 (no sound) to 9 (loudest).  The strength of this loudness is relative to a voice's default volume, set using `set-voice-loudness`.

**paint-melody**

Turns on the ability to add notes to one of the voices.  Each voice has its own hue.  `paint-melody` should be run in a forever loop.  You can add notes by clicking, or clicking and dragging on patches in the world.  If a particular note has already been assigned to a voice at a particular time, the note can be moved to a different pitch, but it cannot be deleted.  Use `unpaint-melody` to delete notes.

**unpaint-melody**

Turns on the ability to select notes for deletion.  Nodes are selected using clicks of the mouse.  You can simultaneously turn-on `paint-melody` and `unpaint-melody`, however the simultaneous creation and deletion of notes is fast and can be rather hard to control.

## Adding and deleting drum voices

**rhythm _voiceID_ _rhythm-string_**

Configures the rhythm for one trace.  The _voiceID_ must be an integer from 0-15.  The _rhythm-string_ is a string of 16 characters that determines the sound at each of 16 possible subdivisions of a measure.  The dash symbol represents no sound.  The numbers, from 0-9, yield a sound at different intensity.  For example, 
`sound:rhythm "9-------4-------"` would yield a loud sound on the first beat and a sound about half as loud on the third beat.  You must specify a string of exactly 16 characters.

**set-drum _drum-voice_ _drum-name_**

Set the specified drum ID to the drum-name, a string.  Possible values of `drum-name` are specified in the [reproduced here](#drum-names)


**delete-drum _drum-voice_ _begin-measure_ _end-measure_**

Deletes all notes for a drum voice for `begin-measure` to `end-measure`, inclusive.


## Playing and looping

**play**

Advances all voices and drums by the smallest time increment, currently corresponding to one sixteenth note. 
 If a note occurs for any voice, it is played immediately.  This is the primary to play the written score and rhythm voices.

**loop-sound _file-name_**

Play the named file, usually a WAV-format file, in a loop.  You can still play other drums and instruments while this plays.  Use `sound-stop` to halt the loop.

**play-sound _file-name_**

Play the named file, usually a WAV-format file, one time. You can still play other drums and instruments while this plays. 


**stop-sound**

Stop playback of a file.  This will not stop output of the `play` command.


<a name="changing-features"></a>
## Changing features of a voice

**set-scale _voiceID_ _tonic_ _scale-name_**
Set the scale used for the specified voice.  The tonic specifies the lowest note of the voice, which is also assumed to be the tonic. Use midi numbers to specify the tonic (e.g., A-440Hz is midi note number 69).
Possible _scale-name_ values:
* "PENTATONIC": semitones 0, 2, 5, 7, and 9
* "PENTATONIC MINOR": semitones 0, 3, 5, 7 and 10
* "BLUES": semitones 0, 3, 4, 5, 7, and 10
* "MAJOR 7": semitones 0, 4, 7, and 11
* "MINOR 7": semitones 0, 3, 7, and 10 
* "WHOLE TONE": semitones 0, 2, 4, 6, 8, and 10
* "MAJOR": semitones 0, 2, 4, 5, 7, 9, and 11
* "MINOR": semitones 0, 2, 3, 5, 7, 8, 10


**set-voice-waveform** _voice-id_ _directory_ _wave-file_**

Set the voice to use `directory/wave-file` as the sound every time a note for this voice is played.



**set-voice-instrument _voice-id_ _instrument_**

Set the voice to use a specific Midi instrument. Values of `instrument` are strings as specified in the NetLogo sound documentation [reproduced here](#instrument-names)

**set-voice-duration _voice-id_ _duration_**

Set the duration of a voice in units of the smallest note type, a sixteenth note.

**set-voice-loudness _voice-id_ _loudness_**

Set loudness of voice.  The `loudness` can be any value 0 to 127 inclusive.

**set-drum-waveform _voice-id_ _directory_ _wave-file_**

Set the drum to use `directory/wave-file` as the sound for this drum's voice.

**delete-voice _voice-id_ _begin-measure_ _end-measure_**

Delete a voices notes from begin to end measures, inclusive.


<a name="adding-measures"></a>
## Copying Voices and Adding Measures

**copy-voice _source-voice_ _source_measure_ _destination-voice_  _destination_measure_ _number-of-measures_ _semitone-change_**

Copy notes from one voice to another.  Copies `number-of-measures` starting from `source-measure` to `destination-measure`.  Offsets copies notes by `semitone-change`, which can be positive, negative, or zero.  Notes that exceed a voice's range are simply omitted.

**add-measures _num-measures_**

Adds the specified number of measures to the composition.  Does not change any existing notes or drums.

<a name="changing-bpm"></a>
## Change Beats-Per-Minute or Patch size

**set-time `note-position`**

Set the time for all voices and drums to `note-position`, an integer that specifies a particular time value counting minimum note values from 0 upward to the number of measures multiplied by the number of notes per measure.

**set-parameter _parameter-name_ _parameter-value_**

Set the specified parameter's value.

Possible `parameter-name` values:
* `PATCHSIZE`: Governs the visual size of each note, and, consequently, the size of the world.
* `BPM`: Beats per minute determines the rate of playback



## Drum Names

```
35. Acoustic Bass Drum             59. Ride Cymbal 2
36. Bass Drum 1                    60. Hi Bongo
37. Side Stick                     61. Low Bongo
38. Acoustic Snare                 62. Mute Hi Conga
39. Hand Clap                      63. Open Hi Conga
40. Electric Snare                 64. Low Conga
41. Low Floor Tom                  65. Hi Timbale
42. Closed Hi Hat                  66. Low Timbale
43. Hi Floor Tom                   67. Hi Agogo
44. Pedal Hi Hat                   68. Low Agogo
45. Low Tom                        69. Cabasa
47. Open Hi Hat                    70. Maracas
47. Low Mid Tom                    71. Short Whistle
48. Hi Mid Tom                     72. Long Whistle
49. Crash Cymbal 1                 73. Short Guiro
50. Hi Tom                         74. Long Guiro
51. Ride Cymbal 1                  75. Claves
52. Chinese Cymbal                 76. Hi Wood Block
53. Ride Bell                      77. Low Wood Block
54. Tambourine                     78. Mute Cuica
55. Splash Cymbal                  79. Open Cuica
56. Cowbell                        80. Mute Triangle
57. Crash Cymbal 2                 81. Open Triangle
58. Vibraslap
```

## Instrument Names

```
*Piano*                              *Reed*
1. Acoustic Grand Piano            65. Soprano Sax
2. Bright Acoustic Piano           66. Alto Sax
3. Electric Grand Piano            67. Tenor Sax
4. Honky-tonk Piano                68. Baritone Sax
5. Electric Piano 1                69. Oboe
6. Electric Piano 2                70. English Horn
7. Harpsichord                     71. Bassoon
8. Clavi                           72. Clarinet

*Chromatic Percussion*               *Pipe*
9. Celesta                         73. Piccolo
10. Glockenspiel                   74. Flute
11. Music Box                      75. Recorder
12. Vibraphone                     76. Pan Flute
13. Marimba                        77. Blown Bottle
14. Xylophone                      78. Shakuhachi
15. Tubular Bells                  79. Whistle
16. Dulcimer                       80. Ocarina

*Organ*                              *Synth Lead*
17. Drawbar Organ                  81. Square Wave
18. Percussive Organ               82. Sawtooth Wave
19. Rock Organ                     83. Calliope
20. Church Organ                   84. Chiff
21. Reed Organ                     85. Charang
22. Accordion                      86. Voice
23. Harmonica                      87. Fifths
24. Tango Accordion                88. Bass and Lead

*Guitar*                             *Synth Pad*
25. Nylon String Guitar            89. New Age
26. Steel Acoustic Guitar          90. Warm
27. Jazz Electric Guitar           91. Polysynth
28. Clean Electric Guitar          92. Choir
29. Muted Electric Guitar          93. Bowed
30. Overdriven Guitar              94. Metal
31. Distortion Guitar              95. Halo
32. Guitar harmonics               96. Sweep

*Bass*                               *Synth Effects*
33. Acoustic Bass                  97. Rain
34. Fingered Electric Bass         98. Soundtrack
35. Picked Electric Bass           99. Crystal
36. Fretless Bass                  100. Atmosphere
37. Slap Bass 1                    101. Brightness
38. Slap Bass 2                    102. Goblins
39. Synth Bass 1                   103. Echoes
40. Synth Bass 2                   104. Sci-fi

*Strings*                            *Ethnic*
41. Violin                         105. Sitar
42. Viola                          106. Banjo
43. Cello                          107. Shamisen
44. Contrabass                     108. Koto
45. Tremolo Strings                109. Kalimba
47. Pizzicato Strings              110. Bag pipe
47. Orchestral Harp                111. Fiddle
48. Timpani                        112. Shanai

*Ensemble*                           *Percussive*
49. String Ensemble 1              113. Tinkle Bell
50. String Ensemble 2              114. Agogo
51. Synth Strings 1                115. Steel Drums
52. Synth Strings 2                116. Woodblock
53. Choir Aahs                     117. Taiko Drum
54. Voice Oohs                     118. Melodic Tom
55. Synth Voice                    119. Synth Drum
56. Orchestra Hit                  120. Reverse Cymbal

*Brass*                              *Sound Effects*
57. Trumpet                        121. Guitar Fret Noise
58. Trombone                       122. Breath Noise
59. Tuba                           123. Seashore
60. Muted Trumpet                  124. Bird Tweet
61. French Horn                    125. Telephone Ring
62. Brass Section                  126. Helicopter
63. Synth Brass 1                  127. Applause
64. Synth Brass 2                  128. Gunshot
```


