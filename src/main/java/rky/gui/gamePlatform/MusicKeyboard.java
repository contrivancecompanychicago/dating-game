
package rky.gui.gamePlatform;

public class MusicKeyboard
{
	/**
      Constructor for a music keyboard, takes an array of note values.
	 */

	public MusicKeyboard(int[] allNotes) {
		this.allNotes = new int[allNotes.length];
		System.arraycopy(allNotes, 0, this.allNotes, 0, allNotes.length);
		noteTime = new int[allNotes.length];
	}

	/**
      Stop playing all notes.
	 */

	public void stop() {
		for (int note = 0 ; note < allNotes.length ; note++)
			synth.noteOff(0, 64 + allNotes[note],  0);
	}

	/**
      Play all the notes that have been specified for this update cycle.
	 */

	public void playNotes() {
		int currentTime = (int)System.currentTimeMillis();

		// PLAY, AND TIMESTAMP, ALL NOTES IN THE NOTE LIST

		for (int n = 0 ; n < numNotesPlaying ; n++) {
			int note = notesPlaying[n];
			if (note >= 0 && note < allNotes.length) {
				synth.noteOn(0, 64 + allNotes[note], 63);
				noteTime[note] = currentTime;
			}
		}

		// TURN OFF ALL NOTES THAT HAVE BEEN PLAYING TOO LONG

		for (int note = 0 ; note < allNotes.length ; note++)
			if (noteTime[note] < currentTime - 500)
				synth.noteOff(0, 64 + allNotes[note],  0);

		numNotesPlaying = 0;
	}

	/**
      Add a note to the notes to be played during the next update cycle.
	 */

	public void addNote(int note) {
		notesPlaying[numNotesPlaying++] = note;
	}

	/**
   		Removes All notes added.
	 */

	public void removeAllNotes() {
		if(numNotesPlaying > 0){
			numNotesPlaying = 0;
			notesPlaying = new int[100];
		}
	}

	MidiSynth synth = new MidiSynth();
	int allNotes[], noteTime[], numNotesPlaying = 0, notesPlaying[] = new int[100];
}

