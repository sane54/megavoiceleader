/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package megavoiceleader;

import java.util.ArrayList;
import org.jfugue.Pattern;


/**
 *
 * @author witzg
 */
public class Chord {
    public ArrayList<Integer> chord_pitches = new ArrayList();
    public Integer dictionary_index;
    public Integer voice_leading_score;
    public String dictionary_string;
    public Pattern chord_tokens;
    public Pattern chord ;
    
    public Chord (Integer dic_index) {
        dictionary_index = dic_index; 
        
    }

    public Chord (Integer dic_index, String dic_string) {
        dictionary_index = dic_index; 
        dictionary_string = dic_string; 
    }
    
    public Chord () { 
        
    }
    
    public Chord(Chord input) {
        dictionary_index = input.dictionary_index;
        dictionary_string = input.dictionary_string;
        chord = input.chord;
        chord_tokens = input.chord_tokens;
    }
    
    
}
