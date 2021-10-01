package megavoiceleader;


import org.jfugue.Note;
import org.jfugue.PatternTransformer;


public class Parallel_To_Separate_Voice_v2 extends PatternTransformer{

	/**
	 * @param args
	 */

	int currentVoice = 0;

	
	public Parallel_To_Separate_Voice_v2() {
	}

	public void noteEvent(Note note) {
		currentVoice = 0;
		getReturnPattern().add("V0");
		getReturnPattern().addElement(note);
	
	}

	
	public void sequentialNoteEvent(Note note)	{
		note.setType((byte) 0);
		getReturnPattern().addElement(note);
	}
	
	public void parallelNoteEvent(Note note)
	{
		
		currentVoice ++;
		String voiceString = "V" + currentVoice;
		getReturnPattern().add(voiceString);
		note.setType((byte) 0);
		getReturnPattern().addElement(note);
	}
}
