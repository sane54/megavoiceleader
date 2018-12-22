package megavoiceleader;

import org.jfugue.Note;
import org.jfugue.PatternTransformer;
import org.jfugue.Tempo;


public class CleanerPatternTransformer extends PatternTransformer {

	public CleanerPatternTransformer()	{
		super();
	}

	public void tempoEvent(Tempo tempo) {
		
}
	
	public void noteEvent(Note note) {
			getReturnPattern().addElement(note);
	}


}
