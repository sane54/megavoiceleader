/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package megavoiceleader;

import org.jfugue.Pattern;
import org.jfugue.Tempo;
import org.jfugue.extras.IntervalPatternTransformer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author witzg
 */
public class VoiceLeader2_refactorTest {
    
    public VoiceLeader2_refactor james;
    megavoiceleader.Chord testChord1, testChord2;
    
    public VoiceLeader2_refactorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        james = new VoiceLeader2_refactor();
        james.octaveTransposer = new IntervalPatternTransformer(12);
        james.master_pattern.addElement(new Tempo(140));
        testChord1 = new Chord();
        testChord1.dictionary_string = "C4w E4w G4w";
        testChord1.chord_tokens = new Pattern(testChord1.dictionary_string);
        System.out.println("testChord1 - chord tokens before transpose: "
                            + testChord1.chord_tokens.getMusicString() );
        testChord1.chord = new Pattern(testChord1.dictionary_string.replaceAll("w ", "w+"));
        testChord1.chord_tokens = 
                james.cleanme.transform(
                james.octaveTransposer.transform(testChord1.chord_tokens));
        testChord1.chord = 
                james.cleanme.transform(
                james.octaveTransposer.transform(testChord1.chord)); 

        System.out.println("testChord 1 dictionary string: " + testChord1.dictionary_string);
        System.out.println("testChord 1 chord: " + testChord1.chord.getMusicString());
        System.out.println("testChord 1 chord tokens " 
                            + testChord1.chord_tokens.getMusicString());
        testChord2 = new Chord();
        testChord2.dictionary_string = "C4w A4w D5w";
        testChord2.chord_tokens = new Pattern(testChord2.dictionary_string);
         testChord2.chord = new Pattern(testChord2.dictionary_string.replaceAll("w ", "w+"));
        testChord2.chord_tokens = 
                james.cleanme.transform(
                james.octaveTransposer.transform(testChord2.chord_tokens));
        testChord2.chord = 
                james.cleanme.transform(
                james.octaveTransposer.transform(testChord2.chord));
        System.out.println("testChord 2 dictionary string: " + testChord2.dictionary_string);
        System.out.println("testChord 2 chord: " + testChord2.chord.getMusicString());
        System.out.println("testChord 2 chord tokens " 
                            + testChord2.chord_tokens.getMusicString());
        
    }
    
    @After
    public void tearDown() {
    }

//    /**
//     * Test of scoreChord method, of class VoiceLeader2_refactor.
//     */
//    @Test
//    public void testScoreChord() {
//        System.out.println("scoreChord");
//        //Chord expResult = null;
//        Chord result = james.scoreChord(testChord2, testChord1);
//        System.out.println("testChord2 score: " + result.voice_leading_score);
//    }

//    @Test
//    public void testScoreTranspositions(){
//        Integer result = james.scoreTranspositions(testChord2, testChord1, 20);
//        System.out.println("New Score to Beat: " + result);
//        System.out.println("nextChordCandidates");
//        for(Chord my_chord: james.next_chord_candidates)
//            System.out.print(my_chord.chord_tokens.getMusicString() + " ");
//        System.out.println("");
//    }


    
    
//    /**
//     * Test of initializeChordDictionary method, of class VoiceLeader2_refactor.
//     */
//    @Test
//    public void testInitializeChordDictionary() {
//        System.out.println("initializeChordDictionary");
//        String expResult = "";
//        String result = james.initializeChordDictionary();
//        for (String line: james.lines)
//            System.out.println(line);
//        System.out.println("initialized dictionary successfully");
//        
//    }

    
      @Test
      public void testFindNextChord(){
          james.initializeChordDictionary();
          Chord firstChord = new Chord(0);
          firstChord = james.initializeChord(firstChord);
          
          Chord nextChord = james.findNextChord(firstChord);
          System.out.println("from first chord: " + firstChord.chord_tokens.getMusicString());
          System.out.println("next chord is: " + nextChord.chord_tokens.getMusicString());
      }
    
//    /**
//     * Test of initializeChord method, of class VoiceLeader2_refactor.
//     */
//    @Test
//    public void testInitializeChord() {
//        System.out.println("initializeChord");
//        String result = james.initializeChordDictionary();
//        System.out.println(result + james.lines.get(3));
//        Chord myChord = new Chord(3);
//        myChord = james.initializeChord(myChord);
//        System.out.println(myChord.dictionary_string);
//        System.out.println(myChord.chord.getMusicString());
//        System.out.println(myChord.chord_tokens.getMusicString());
//        
//
//    }

//    /**
//     * Test of addChordToMaster method, of class VoiceLeader2_refactor.
//     */
//    @Test
//    public void testAddChordToMaster() {
//        System.out.println("addChordToMaster");
//        
//        james.addChordToMaster(testChord1, false);
//        james.addChordToMaster(testChord2, true);
//        System.out.println(james.master_pattern.getMusicString());
//    }

//    /**
//     * Test of loop_detector method, of class VoiceLeader2_refactor.
//     */
//    @Test
//    public void testLoop_detector() {
//        //System.out.println("loop_detector");
//        Integer new_index = 28;
//        VoiceLeader2_refactor instance = new VoiceLeader2_refactor();
//        //instance.recently_used = new ArrayList();
//        instance.recently_used.add(45);
//        instance.recently_used.add(27);
//        instance.recently_used.add(28);
//        instance.recently_used.add(30);
//        instance.recently_used.add(27);
//        instance.recently_used.add(23);
//        instance.recently_used.add(45);
//        instance.recently_used.add(29);
//        instance.recently_used.add(45);
//        instance.recently_used.add(27);
//        instance.recently_used.add(28);
//        instance.recently_used.add(23);
//        instance.recently_used.add(45);
//        instance.recently_used.add(29);
//        instance.recently_used.add(45);
//        instance.recently_used.add(27);
//        instance.max_loops = 1;
//        Boolean expResult = true;
//        Boolean result = instance.loop_detector(new_index);
//        System.out.println("result: " + result);
//        assertEquals(expResult, result);
//    }
    
}
