/*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package megavoiceleader;

import java.io.File;

/**
 * Stores and controls access to application input parameters.
 * Default values aren't really used as they are set to whatever is 
 * specified in the input GUI. 
 * @author Trick's Music Boxes
 */
public class InputParameters {
static Boolean out_to_midi_yoke = false;
static Boolean q_mode = false;
static Boolean broken = false;
static int tempo_bpm = 120;
static int piece_length = 4;
static int loop_length = 4;
static File filePath = null;
static File queueDir = null;
static File fileDir = null;
static String queue_directory = null;
static File chordDict = null;

public static Boolean get_out_to_midi_yoke () {
    return out_to_midi_yoke;
}
 public static void setQueueDirectory(String direct) {
     queue_directory = direct;
 }
 
 public static String getQueueDirectory() {
     return queue_directory;
 }
 
 public static void setQueueDir(File direct) {
     queueDir = direct;
 }
 
 public static File getQueueDir() {
     return queueDir;
 }
public static void set_out_to_midi_yoke (Boolean out2yoke) {
    out_to_midi_yoke = out2yoke;
}

public static void set_q_mode (Boolean queue_mode) {
    q_mode = queue_mode;
}

public static Boolean get_q_mode() {
    return q_mode;
}

public static void set_broken (Boolean broke) {
    broken = broke;
}

public static Boolean get_broken() {
    return broken;
}

public static void setPieceLength(int my_piece_length) {
    piece_length = my_piece_length;    
}
public static int getLength() {
    return piece_length;
}

public static void setLoopLength(int my_loop_length) {
    loop_length = my_loop_length;    
}
public static int getLoopLength() {
    return loop_length;
}
public static void setTempo(int my_tempo) {
    tempo_bpm = my_tempo;    
    }
public static int getTempo() {
    return tempo_bpm;
}

public static void setFilePath(File file) {
    filePath = file;
    if (file != null) fileDir = file.getParentFile();
    }

public static File getFilePath() {
    if(filePath != null) return filePath;
    else {
        //System.out.println("file path is null");
        return null;
        }
}

public static File getFileDir() {
    if(fileDir!= null) return fileDir;
    else {
        //System.out.println("file path is null");
        return null;
        }
}

public static void setChordDict(File myfile) {
    chordDict = myfile;
}
public static File getChordDict() {
    return chordDict;
}

}
