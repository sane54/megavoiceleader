package megavoiceleader;

import org.jfugue.Note;
import org.jfugue.PatternTransformer;
import org.jfugue.Tempo;

/* This class exists just to clean up the Tempo event that 
IntervalPattern Transformer adds */

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
