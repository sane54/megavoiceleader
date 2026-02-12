package megavoiceleader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import org.jfugue.Pattern;
import org.jfugue.extras.IntervalPatternTransformer;
import org.jfugue.Tempo;

public class VoiceLeader2_refactor {
    public List<String>lines = new ArrayList();
    public static Random roll = new Random();
    public IntervalPatternTransformer octaveTransposer;
    public CleanerPatternTransformer cleanme;
    public IntervalPatternTransformer Transposer;
    public Pattern master_pattern = new Pattern();
    public static ArrayList<Integer> recently_used = new ArrayList();
    public List<Chord> next_chord_candidates = new ArrayList();
    public int max_loops;
    public Worker worker;
    
    
    public  VoiceLeader2_refactor() {
        final double totalWorkSteps = InputParameters.getLength();
        octaveTransposer = 
                new IntervalPatternTransformer(12*(roll.nextInt(3)));
        cleanme = new CleanerPatternTransformer();
        max_loops = InputParameters.getLoopLength();
        Boolean broken = InputParameters.get_broken();

        
        worker = new Task<String>() {
            @Override
            protected String call() throws Exception {
                initializeChordDictionary();
                master_pattern.addElement(new Tempo(InputParameters.getTempo()));
                int dic_index = roll.nextInt(lines.size());
                Chord firstChord = new Chord(dic_index);
                firstChord = initializeChord(firstChord);
                addChordToMaster(firstChord, broken);
                for (double counter = 0; counter < totalWorkSteps; counter++) {
                    Chord nextChord = new Chord(findNextChord(firstChord));
                    addChordToMaster(nextChord, broken);
                    firstChord = nextChord;
                    updateProgress(counter, totalWorkSteps);                     
                    if (isCancelled()) return "cancelled";
                }
                if (InputParameters.get_q_mode()) 
                   PatternQueueStorerSaver.add_pattern_to_queue(master_pattern);
                else PatternStorerSaver1.add_pattern(master_pattern);
                return "done bitch";    
            }
        };
    }
    

    
    public Chord findNextChord(Chord firstChord){
        Integer score_to_beat = -1;
        Chord nextChord;
        for (int linecounter = 0; 
                linecounter < lines.size(); linecounter++) {
            if (linecounter == firstChord.dictionary_index) continue;
            if(!loop_detector(linecounter)) continue;
            nextChord = new Chord(linecounter, lines.get(linecounter));
            nextChord = initializeChord(nextChord);
            System.out.println("looking at chord with index " + linecounter);
            System.out.println("we have: "+ nextChord.chord_tokens.getMusicString());
            score_to_beat = 
                    scoreTranspositions(nextChord, firstChord, score_to_beat);
        }
        System.out.println();
        
        if (next_chord_candidates.size() == 0) {
            System.out.println("NEXTCHORD CANDIDATES EMPTY");
            return firstChord;
        }
        else {
        nextChord = next_chord_candidates.get
                (roll.nextInt(next_chord_candidates.size()));
        }
        return nextChord;
    }


    public int scoreTranspositions(Chord nextChord, 
                                   Chord firstChord, int score_to_beat) {      
        for (int transpose_counter = -6; 
                transpose_counter < 6; transpose_counter=transpose_counter + 1) {
            Transposer = new IntervalPatternTransformer(transpose_counter);
            System.out.println("transpose_counter: " + transpose_counter);
            Chord transposeChord = new Chord();
            transposeChord.chord = cleanme.transform(Transposer.transform(nextChord.chord));
            transposeChord.chord_tokens =cleanme.transform(
                    Transposer.transform(nextChord.chord_tokens));
            transposeChord.dictionary_index = nextChord.dictionary_index;
            transposeChord.dictionary_string = nextChord.dictionary_string;
            System.out.println("nextChord Transposition "+transposeChord.chord_tokens.getMusicString());            
            System.out.println("firstChord "+firstChord.chord_tokens.getMusicString());
            System.out.println("origNext "+nextChord.chord_tokens.getMusicString());
            transposeChord = scoreChord(transposeChord, firstChord);
            if (transposeChord.voice_leading_score < score_to_beat || score_to_beat == -1) {
               System.out.println("found new score to beat: " + transposeChord.voice_leading_score);
               score_to_beat = transposeChord.voice_leading_score;
               next_chord_candidates.clear();
               next_chord_candidates.add(transposeChord);
            } 
            else if (transposeChord.voice_leading_score == score_to_beat) {
                next_chord_candidates.add(transposeChord);   
                System.out.println(nextChord.voice_leading_score+ " chord score equalled score to beat");
            }
        }
        System.out.print("next_chord_candidates: ");
        for (Chord m : next_chord_candidates) System.out.print(m.chord_tokens.getMusicString() + " | ");
        System.out.println();
        System.out.println("Score to Beat so far: " + score_to_beat);
        return score_to_beat;
    }

    
    public Chord scoreChord(Chord nextChord, Chord firstChord) {
        int pitchcount = firstChord.chord_tokens.getTokens().length;
        System.out.println("pitchcount: " + pitchcount);
        int[] diffarray = new int[pitchcount]; 
        for (int i = 0; i < (pitchcount); i++){
            int note_1 = noteTokenToInt(firstChord.chord_tokens.getTokens()[i]);
            System.out.println("note_1: " + note_1);
            int note_2 = noteTokenToInt(nextChord.chord_tokens.getTokens()[i]);
            System.out.println("note_2: " + note_2);
            System.out.println("difference: " + (Math.abs(note_1 - note_2)));
            diffarray[i]  = Math.abs(note_1 - note_2);
        }
       int sum = 0;
       for (int j = 0; j < diffarray.length; j++) {
           sum = sum + diffarray[j];
       }
       nextChord.voice_leading_score = sum;
       return nextChord;
    }
    
    
    public static int noteTokenToInt (String note) {
        //System.out.println("noteTokenToInt Called for note " + note);
        String s1 = note.substring(0, 1);
        //System.out.println("s1 " + s1);
        String s2 = note.substring(1, 2);
        //System.out.println("s2 " + s2);
        String s3 = note.substring(2, 3);
        //System.out.println("s3 " + s3);
        int intvalue = 0;
        int octave = 0; 
        int flatsharp = 0;
        if (s2.contains("#")) { 
            flatsharp = 1;
            //System.out.println("s2 sharp");
            octave = Integer.parseInt(s3);
            //System.out.println("1octave " + octave);
        }
        else if (s2.contains("b")) { 
            flatsharp = -1;
            //System.out.println("s2 flat");
            octave = Integer.parseInt(s3);
            //System.out.println("2ocatave " + octave);
	}	
        //else {
        if ( !(s2.contains("#")) && !(s2.contains("b")) ) { 
            flatsharp = 0;
            //System.out.println("s2 neither sharp nor flat");
            octave = Integer.parseInt(s2);
            //System.out.println("3ocatave " + octave);
        }
        if (s1.contains("C")) intvalue = 0;
        if (s1.contains("D")) intvalue = 2;
        if (s1.contains("E")) intvalue = 4;
        if (s1.contains("F")) intvalue = 5;
        if (s1.contains("G")) intvalue = 7;
        if (s1.contains("A")) intvalue = 9;
        if (s1.contains("B")) intvalue = 11;
//        System.out.println("intvalue of note" + intvalue);
//        System.out.println("intvalue of flatsharp" + flatsharp);
//        System.out.println("intvalue of octave" + 12*octave);
        intvalue = intvalue + flatsharp + 12*octave;
//        System.out.println("intvalue of note" + intvalue);
        return intvalue; 
    }
    
    
    
    public String initializeChordDictionary() {
        String line;
        BufferedReader br;
        try {
                br = new BufferedReader(
                     new FileReader(InputParameters.getChordDict()));
        } catch (FileNotFoundException e) {
            //LOG
            System.out.println("File NOT FOUND");
            return "File NOT FOUND";
        }
        try {
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            //LOG
            System.out.println("Unable to read line");
            return "Unable to read line";
        }
        try {
            br.close();
        } catch (IOException e) {
            //LOG
            System.out.println("File NOT Closable");
        }
        return null;
    }

    
    public Chord initializeChord(Chord myChord){
        myChord.dictionary_string = lines.get(myChord.dictionary_index);
        myChord.chord_tokens = new Pattern(myChord.dictionary_string);
        myChord.chord = 
                new Pattern(myChord.dictionary_string.replaceAll("w ", "w+"));
        myChord.chord_tokens = cleanme.transform(
                octaveTransposer.transform(myChord.chord_tokens));
        myChord.chord = cleanme.transform(octaveTransposer.transform(myChord.chord));
        return myChord;
    }
    
    public void addChordToMaster(Chord myChord, Boolean broken) {
        if (broken) {
            master_pattern.add(myChord.chord_tokens);
        }
        else {
            master_pattern.add(myChord.chord);
        }
        recently_used.add(myChord.dictionary_index) ;        
    }
    
    public Boolean loop_detector (Integer new_index) {
        Boolean accept = true;
        List<Integer> latest_sequence;
        List<Integer> previous_sequence;
        for (int seq_length = 2; 
             seq_length < recently_used.size()/2; 
             seq_length++) {
            int loopcount = 0;
            latest_sequence = new ArrayList();
            System.out.println("checking for loops of length " + seq_length);
            for (Integer dic_index : recently_used.subList(
                  recently_used.size() - seq_length + 1, recently_used.size()))
                latest_sequence.add(dic_index);
            latest_sequence.add(new_index);
            for (int start_pos = 0;
                    start_pos < recently_used.size() - seq_length; start_pos++) {
                previous_sequence = recently_used.subList(
                  start_pos, start_pos + seq_length );
                System.out.println("latest sequence: " + latest_sequence +  
                                    " previous sequence: " + previous_sequence);
                if(previous_sequence.equals(latest_sequence)){
                    System.out.println("I found a loop of length " + seq_length);
                    if (loopcount >= max_loops) {
                        accept = false;
                        break;                    
                    }
                    else {
                        int loops_left = max_loops - loopcount;
                        System.out.println("but I still have " + loops_left + " to go");
                        loopcount++;
                    }   
                }                
            }
            if (accept == false) break;
        }     
        return accept;
    }

}