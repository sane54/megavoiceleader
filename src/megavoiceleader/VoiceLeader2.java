package megavoiceleader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import org.jfugue.Pattern;
import org.jfugue.extras.IntervalPatternTransformer;
import org.jfugue.Tempo;

public class VoiceLeader2 {
    public Worker worker;
    public  VoiceLeader2() {
        worker = new Task<String>() {
            @Override
            protected String call() throws Exception {

                int piece_length = InputParameters.getLength();
                int loop_length = InputParameters.getLoopLength();
                Tempo tempo_bpm = new Tempo(InputParameters.getTempo());
                
                double completedWorkSteps = 0;
                double totalWorkSteps = piece_length;
                System.out.println("starting program");
	
                 //Declare and Initialize Variables
                List<String>lines = new ArrayList<>();
                String line;
                int lineIndexer=0;
                Random choose_me = new Random();
                Random transposeInterval = new Random();
                Pattern masterPattern = new Pattern();
                Pattern chord = new Pattern();
                Pattern workingPattern = new Pattern();
                Parallel_To_Separate_Voice_v2 P2SV = new Parallel_To_Separate_Voice_v2();
                BufferedReader br;
        
                //Initialize Tempo
                masterPattern.addElement(tempo_bpm);
                
                //Open Chord Dictionary
                File diction = InputParameters.getChordDict();
                try {
                        br = new BufferedReader(new FileReader(diction));
                        //br = new BufferedReader(new FileReader("C:\\BasicTriads"));
                } catch (FileNotFoundException e) {
                    System.out.println("File NOT FOUND");
                    return "File NOT FOUND";
                }
        
                //Read each line of chord dictionary file into "lines" array
                try {
                    while ((line = br.readLine()) != null) {
			lines.add(line);
			lineIndexer++;
                    }
                } catch (IOException e) {
                    System.out.println("Unable to read line");
                    return "Unable to read line";
                }

                //Now that we've read the dictionary into our array, we close the file
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("File NOT Closable");
                }
	
                System.out.println("lines size " + lines.size());
                Integer linesize = lines.size();
        
                //Create a chord dictionary index
                ArrayList<Integer> dictionaryIndex = new ArrayList<>();
                //System.out.println(dictionaryIndex.size());
                for (Integer dicCounter = 0; dicCounter < linesize; dicCounter++) {
                    dictionaryIndex.add(dicCounter, dicCounter);
                }
	
                //Pick a Seed chord from dictionary at Random
                int chosenIndex = choose_me.nextInt(lineIndexer);
                String musicString = lines.get(chosenIndex);
	
                //Make it a chord
                String chordString = musicString.replaceAll("w ", "w+");
	
                //DEBUG
                System.out.println("First Chord " + chordString);
	
                //Add it to chord pattern
                chord.add(chordString);

                
	
                //DEBUG
                //System.out.println(chord.getMusicString());
	
                //Randomly transpose chord
                int transInt = transposeInterval.nextInt(12);
                System.out.println("transInt: " + transInt);
                IntervalPatternTransformer Transposer = new IntervalPatternTransformer(transInt);
                chord = Transposer.transform(chord);
                System.out.println("transposed chord: " + chord.getMusicString());
                Pattern tempPattern = new Pattern(musicString);
                CleanerPatternTransformer myCleaner = new CleanerPatternTransformer();
                chord = myCleaner.transform(chord);
                tempPattern = myCleaner.transform(tempPattern);
                String first_pattern_broken_chord = myCleaner.transform(Transposer.transform(tempPattern)).getMusicString();
                
                //append to master pattern   this is the first chord
                if (InputParameters.get_broken()) {
                    masterPattern.add(first_pattern_broken_chord);
                }
                else {
                    masterPattern.add(chord);
                }
		
                //DEBUG
                //Player player = new Player();
                //player.play(chord);
	
	
                //transpose the old un-plussed string with same interval.
                workingPattern.add(musicString);
                workingPattern = Transposer.transform(workingPattern);
	
                //Read it into array1 
                String[] array1 = workingPattern.getTokens();
                
                //Get number of tokens per line in dictionary
                int numtokens = array1.length;
	
                //DEBUG
                //System.out.println(array1[1]);
                //Note testNote = array1[1];		
                IntervalPatternTransformer TransposeDown12 = new IntervalPatternTransformer(-12);
                IntervalPatternTransformer TransposeUp1 = new IntervalPatternTransformer(1);
	
                // Remove initial chosen index from dictionaryIndex. 
                dictionaryIndex.remove(chosenIndex);
	
                int[] recentlyUsed = new int[piece_length];
                recentlyUsed[0] = chosenIndex;
                Integer dicIndex = 0;
                int toleranceCounter = 0;
                for (int counter = 1; counter < piece_length; counter++) {
                    //DEBUG
                    //System.out.println("pick next chord");
                    //Recalculate size of index array.
                    int[] ChordSumArray = new int[lines.size()];
                    int[] ChordIntArray = new int[lines.size()];
                    for (int sumcounter = 0; sumcounter < lines.size(); sumcounter ++){
                        ChordSumArray[sumcounter] = 1000;
                    }
                    for (int linecounter = 0; linecounter < dictionaryIndex.size(); linecounter++) {
                        int[] transposeSumArray = new int[24];
                        int[] transposeIntArray = new int[24];
                        if (linecounter != chosenIndex && linecounter != recentlyUsed[counter - 1]) {
                            System.out.println("linecounter " + linecounter + " ChosenIndex " + chosenIndex + " Recent " + recentlyUsed[counter-1]);
                            //Select Next Chord Pattern from the Dictionary
                            //if loop avoidance is on Use only those dictionary lines that haven't already been used. 
                            //The indexes of these lines are stored in dicIndex
                            dicIndex = dictionaryIndex.get(linecounter);
                            String musicString2 = lines.get(dicIndex);
                            System.out.println("pattern " + linecounter + " " + musicString2);
                            Pattern workingPattern21 = new Pattern (musicString2);
				
                            //Transpose selected chord pattern down 1 octave
                            workingPattern21 = TransposeDown12.transform(workingPattern21);
                            int transposeInt = -12;
				
                            // Then find transposition of selected chord that has the smallest difference from preceding chord
                            // You will start from -12, then transpose up 1 half step each iteration stopping at +12
                            for (int k = 1; k < 24; k++) {
                                //Read transposition of selected chord pattern into an array of tokens called array 2
                                String[] array2 = workingPattern21.getTokens(); 
                                //Initialize Difference Array
                                int[] diffarray = new int[array2.length]; 
                                diffarray[0] = 0;
                                //Calculate difference between pitch from previous chord and pitch from selected chord
                                for (int i = 1; i < (array2.length); i++){
                                    //System.out.println(i + " " + array2.length + " " + array1[i] + " " + array2[i]);
                                    //convert the tokens in array1 and array2 to integers
                                    int note_1 = noteTokenToInt(array1[i]);
                                    int note_2 = noteTokenToInt(array2[i]);
                                    //Take the absolute value of the difference between array1 and array2 integers
                                    int interval = Math.abs(note_1 - note_2);
                                    //Read this into difference array
                                    diffarray[i]  = interval;
                                }
                                 //Sum the elements in difference array
                                int sum = 0;
                                for (int j = 1; j < diffarray.length; j++) {
                                    sum = sum + diffarray[j];
                                }
					
                                // Store this sum in transpose sum array
                                // transposeSumArray has 24 spots, 1 for each transposition of selected chord
                                transposeSumArray[k] = sum;
                                transposeIntArray[k] = transposeInt;
                                //Transpose selected chord pattern up 1 semitone
                                workingPattern21= TransposeUp1.transform(workingPattern21);
                                transposeInt++;
                            } // now find the sum of differences for the next transposition
			
				
				
                        // In transpose sum array, I need to track both interval and sum - I can create a pairList like the one below
                        // java.util.List<java.util.Map.Entry<String,Integer>> pairList= new java.util.ArrayList<>()
                        //java.util.Map.Entry<String,Integer> pair1=new java.util.AbstractMap.SimpleEntry<>("Not Unique key1",1);
                        //java.util.Map.Entry<String,Integer> pair2=new java.util.AbstractMap.SimpleEntry<>("Not Unique key2",2);
                        //pairList.add(pair1);
                        //pairList.add(pair2);
                        // oR I can just create another array of Transpose Intervals. 
				
                        //THe chord sum array will need the same thing. the chord index (linecounter) plus a transpose interval. 
                        //possibly create two chord arrays as well... 
				
                            System.out.println("Transpose Sum Array completed");
				
				
                        //DEBUG - Print out Transpose Sum Array
//                        for (int i = 0; i < transposeSumArray.length; i++) {
//                            System.out.println("Tranpose Sum Array Index " + i +"= " + transposeSumArray[i]);
//                            }
				
                            //Find smallest non-zero element in transpose sum array
                            int smallest = 1000;
                            int smallest_ind = 0;
                            for (int i = 1; i < transposeSumArray.length; i++) {
                                if (transposeSumArray[i] < smallest) {
                                    smallest = transposeSumArray[i];
                                    smallest_ind = i;
                                }	    
                            }
                            //OR......
                            //Find elements in transpose sum array with sum less than elements in chord
//                            int smallest;
//                            int smallest_ind;
//                            ArrayList<Integer> tempTransSumArray = new ArrayList<>();
//                            ArrayList<Integer> tempTransIndArray = new ArrayList<>();
//                            for (int i = 1; i < transposeSumArray.length; i++) {
//                                //System.out.println("loop");
//                                if (transposeSumArray[i] <= numtokens) {
//                                    tempTransSumArray.add(transposeSumArray[i]);
//                                    tempTransIndArray.add(i);
//                                }
//                            }
//                            int chooser_int = choose_me.nextInt(tempTransIndArray.size());
//                            System.out.println(chooser_int);
//                            smallest_ind = tempTransIndArray.get(chooser_int);
//                                                        
//                            smallest = tempTransSumArray.get(smallest_ind);
                            
                            System.out.println("index of chosen transpose sum array = " + smallest_ind);
                            //Store this in chord sum array
                            ChordSumArray[dicIndex] = smallest;
                            ChordIntArray[dicIndex] = transposeIntArray[smallest_ind];
                        }
                    }      
                    //Find smallest non-zero element in chord sum array
                    System.out.println("Chord Sum Array completed");
                    for (int i = 0; i < ChordSumArray.length; i++) {
                        System.out.println("Chord Sum Array Index " + i +"= " + ChordSumArray[i]);
                    }
//                    
//                    int smallest = 1000;
//                    int smallest_ind = 0;
//                    for (int i = 0; i < ChordSumArray.length; i++) {
//                        if ((ChordSumArray[i] < smallest) && ChordSumArray[i] != 0) {
//                            smallest = ChordSumArray[i];
//                            smallest_ind = i;
//                        }
//                    }
                    //OR.....
                    //Find elements in transpose sum array with sum less than elements in chord
                    int smallest;
                    int smallest_ind;
                    ArrayList<Integer> tempChordSumArray = new ArrayList<>();
                    ArrayList<Integer> tempChordIndArray = new ArrayList<>();
                    for (int i = 1; i < ChordSumArray.length; i++) {
                        if (ChordSumArray[i] <= numtokens) {
                            tempChordSumArray.add(ChordSumArray[i]);
                            tempChordIndArray.add(i);
                            System.out.println("temporary Chord index array size" + tempChordIndArray.size());
                        }
                    }
                    if (tempChordIndArray.isEmpty()) {
                        smallest = 1000;
                        smallest_ind = 0;
                        for (int i = 0; i < ChordSumArray.length; i++) {
                            if ((ChordSumArray[i] < smallest) && ChordSumArray[i] != 0) {
                                smallest = ChordSumArray[i];
                                smallest_ind = i;
                               }
                        }                           
                    }
                    else {
                        int chooser_int = choose_me.nextInt(tempChordIndArray.size());
                        System.out.println("chooser int " + chooser_int);
                        smallest_ind = tempChordIndArray.get(chooser_int);
                        System.out.println("smaller " + smallest_ind);
                        smallest = tempChordSumArray.get(chooser_int); 
                    }
                   
                    
                    
                    //Get pattern corresponding to index in chord sum
                    //Create a chord musicString from it (subroutine)
                    recentlyUsed[counter] = chosenIndex;
                    chosenIndex = smallest_ind;
                    String musicString3 = lines.get(smallest_ind);
                    String musicString4 = musicString3.replaceAll("w ", "w+");
                    System.out.println("Chosen Chord " + musicString4);
                    Pattern chord2 = new Pattern();
                    if (InputParameters.get_broken()) {
                        chord2 = new Pattern(musicString3);
                    }
                    else {
                        chord2 = new Pattern(musicString4);
                    }
                    System.out.println("Transposition Index " + smallest_ind);
                    //Find the correct Transposition of the chord
                    IntervalPatternTransformer Transposer2 = new IntervalPatternTransformer(ChordIntArray[smallest_ind]);
                    chord2 = Transposer2.transform(chord2);
                    
                    chord2 = myCleaner.transform(chord2);
                    System.out.println("Chosen Chord after transpose " + chord2.getMusicString());
                    //append to master pattern
                    masterPattern.add(chord2.getMusicString());
                    //Read it into array1 - with the following command
                    System.out.println("Master Pattern " + masterPattern.getMusicString());
                    
                    Pattern arrayReset = new Pattern(musicString3);
                    arrayReset = Transposer2.transform(arrayReset);	
                    array1  = arrayReset.getTokens();
//                    for (int i = 0; i < array1.length; i++) {
//                        System.out.println("array1 Index " + i +"= " + array1[i]);
//                    }
                    for (int linecounter = 0; linecounter < dictionaryIndex.size(); linecounter++) {
                        if (dictionaryIndex.get(linecounter) == chosenIndex) dictionaryIndex.remove(linecounter);
                        //break;
                    }
                    
                    toleranceCounter++;
                    if (toleranceCounter == loop_length) {
                        recentlyUsed = new int[piece_length];
                        toleranceCounter = 0;
                        for (Integer dicCounter = 0; dicCounter < linesize; dicCounter++) {
                            dictionaryIndex.add(dicCounter, dicCounter);
                        }
                    }

                    
                    if (isCancelled()) {
                        return "cancelled";
                    }
                    completedWorkSteps++;
                    updateProgress(completedWorkSteps, totalWorkSteps);
                }	

                //Break Chords Into Voices
                if (InputParameters.get_broken()) masterPattern = P2SV.transform(masterPattern);
	
                if (isCancelled()) {
                return "cancelled";
                }
                completedWorkSteps++;
                updateProgress(completedWorkSteps, totalWorkSteps);
                
                if (InputParameters.get_q_mode()) PatternQueueStorerSaver.add_pattern_to_queue(masterPattern);
                else PatternStorerSaver1.add_pattern(masterPattern);
                //all done
                return "done";
            }
        };
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
        //System.out.println("intvalue of note" + intvalue);
        //System.out.println("intvalue of flatsharp" + flatsharp);
        //System.out.println("intvalue of octave" + 12*octave);
        intvalue = intvalue + flatsharp + 12*octave;
        //System.out.println("intvalue of note" + intvalue);
        return intvalue; 
    }
    public static int getPieceLength () {
        boolean IntError = false;
        Scanner user_input = new Scanner( System.in );	   
        String lengthString;
        int pLength = 0;
        do{
            System.out.print(":");
            lengthString = user_input.next( );
            try {
                IntError = false;
                pLength = Integer.parseInt(lengthString);
            } catch (NumberFormatException n) {
                System.out.println("Please enter an integer");
                //n.printStackTrace();
                IntError = true;
            }
				
        }while(IntError);
    return pLength;
    }
    
    public static int getLoopTolerance () {
        boolean IntError = false;
        Scanner user_input = new Scanner( System.in );	   
        String InputString;
        int tolerance = 0;
        do{
            System.out.print(":");
            InputString = user_input.next( );
            try {
                IntError = false;
                tolerance = Integer.parseInt(InputString);
            } catch (NumberFormatException n) {
            System.out.println("Please enter an integer");
            //n.printStackTrace();
            IntError = true;
            }
				
        }while(IntError);
        return tolerance;
    }
}