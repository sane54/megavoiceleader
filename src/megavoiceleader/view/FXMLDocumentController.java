/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package megavoiceleader.view;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/**
 *
 * @author Owner
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private Button fileChooserButton;
    @FXML
    private TextField lengthField;
    @FXML
    private TextField loopField;
    @FXML
    private TextField tempoField;
    @FXML
    private Button runButton;
    @FXML
    private CheckBox midi_out_chk;
    @FXML
    private CheckBox q_mode_chk;
    @FXML
    private CheckBox broken_chords;
    
    Button PlayButton = new Button();
    Button PauseButton = new Button();
    Button StopButton = new Button();
    
    Stage ctrlStage = new Stage();

    
    public void handlerunButton (ActionEvent event) {
        boolean proceed = true;

        if (megavoiceleader.InputParameters.getFilePath() != null && megavoiceleader.InputParameters.getFilePath().isFile()) {
            CancelBox.show("File will be overwritten. OK?", "Overwrite existing file");
            proceed = CancelBox.getProceed();
        }
        
        if (proceed == false) return;
		
        //Set input and decrement parameters
        megavoiceleader.InputParameters.setTempo(Integer.parseInt(tempoField.getText()));
        megavoiceleader.InputParameters.setPieceLength(Integer.parseInt(lengthField.getText()));

        if(midi_out_chk.isSelected()) {
            megavoiceleader.InputParameters.set_out_to_midi_yoke(true);
        }
        else megavoiceleader.InputParameters.set_out_to_midi_yoke(false);
		
        if(q_mode_chk.isSelected()) megavoiceleader.InputParameters.set_q_mode(true);
        else megavoiceleader.InputParameters.set_q_mode(false);
        
        if (broken_chords.isSelected()) megavoiceleader.InputParameters.set_broken(true);
        else megavoiceleader.InputParameters.set_broken(false);
              
        runButton.setDisable(true);
        fileChooserButton.setDisable(true);
        
        //Starting the counterpoint
        megavoiceleader.VoiceLeader2 model = new megavoiceleader.VoiceLeader2();
        Thread generatorThread = new Thread((Runnable) model.worker);
        generatorThread.start();
        
        model.worker.runningProperty().addListener(
            (ObservableValue<? extends Boolean> observable, Boolean oldvalue, Boolean newvalue) -> {
         
                if(newvalue.equals(false)) {
                    //Buttons disabled when thread is running - re-enabled when thread stops
                    runButton.setDisable(false);
                    fileChooserButton.setDisable(false);
                    if (model.worker.getState().equals(model.worker.getState().CANCELLED))return;
                        playSaveDialog();
                } 
        });
        
        //build the cancel screen
        Stage stage = new Stage();
        stage.setMinWidth(250);
        stage.setTitle("Generating Harmonic Texture");
        
        ProgressBar pb = new ProgressBar();
        pb.progressProperty().bind(model.worker.progressProperty());
        pb.progressProperty().addListener(
            (observable, oldvalue, newvalue) -> {
                if((double)newvalue == 1.0) {
                    stage.close();
                }    
        });  
        
        Label mylabel = new Label("");
        mylabel.textProperty().bind(model.worker.messageProperty());
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(c -> {
            mylabel.textProperty().unbind();
            mylabel.setText("Cancelling... please wait");
            model.worker.cancel();
            stage.close();
        });
        
        VBox pane = new VBox(20);
        pane.getChildren().addAll(pb, mylabel, cancelButton);
        pane.setAlignment(Pos.CENTER);
        pane.setMinWidth(400);
        pane.setStyle("-fx-font: 11px \"Lucida Sans Unicode\"; -fx-padding: 10; -fx-background-color: beige;");
        
        Group mystuff = new Group();
        mystuff.getChildren().addAll(pane);
        stage.setScene(new Scene(mystuff));
        stage.show();  
        
            
    }// end of handlerunButtonCode
    
    @FXML
    public void handleFileChooserButton () {
        if (fileChooserButton.isDisabled()) {
            MessageBox.show( "You have to wait until the current run is over", "Be Patient");

        }
        else {
            File recordsDir = new File(System.getProperty("user.home"), "Chord_Dictionary");
            if (! recordsDir.exists()) {
                recordsDir.mkdirs();
            }
            final FileChooser chordDictChooser = new FileChooser();
            chordDictChooser.setInitialDirectory(recordsDir);
            File chordDict = chordDictChooser.showOpenDialog(ctrlStage);
            if (chordDict != null) {
                megavoiceleader.InputParameters.setChordDict(chordDict);  
            }    
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tempoField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                String oldValue, String newValue) {
                int tempo= 120;
                try {
                    tempo = Integer.parseInt(newValue);
                }//end try
                catch (NumberFormatException e) {
                    tempoField.setText("120");
                }//end catch
                    
                if (tempo < 1 || tempo > 2000) {
                Platform.runLater(() -> { tempoField.clear();});
                }//end if block
            }//ends changed method
        });
        tempoField.setTooltip(new Tooltip 
                        ("has to be between 1 and 2000"));
        
        lengthField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                String oldValue, String newValue) {
                int pclen= 4;
                try {
                    pclen = Integer.parseInt(newValue);
                }//end try
                catch (NumberFormatException e) {
                    lengthField.setText("4");
                }//end catch
                    
                if (pclen < 1 || pclen > 2000) {
                Platform.runLater(() -> { lengthField.clear();});
                }//end if block
            }//ends changed method
        });
        lengthField.setTooltip(new Tooltip 
                        ("has to be between 1 and 2000"));

        loopField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                String oldValue, String newValue) {
                int pclen= 4;
                try {
                    pclen = Integer.parseInt(newValue);
                }//end try
                catch (NumberFormatException e) {
                    loopField.setText("4");
                }//end catch
                    
                if (pclen < 1 || pclen > 2000) {
                Platform.runLater(() -> { loopField.clear();});
                }//end if block
            }//ends changed method
        });
        loopField.setTooltip(new Tooltip 
                        ("has to be between 1 and 2000"));
    }    

    public void playSaveDialog() {
        boolean proceed;
        PlayButton.setVisible(true);
        PauseButton.setVisible(true);
        StopButton.setVisible(true);
        String pattern_or_queue = "Pattern?";
        if (megavoiceleader.InputParameters.get_q_mode() == true) {
            pattern_or_queue = "Queue?";
        }
        else megavoiceleader.PatternQueueStorerSaver.clear_queue();
        
        CancelBox.show("Play " + pattern_or_queue, " ");
        proceed = CancelBox.getProceed();
        if (proceed) {
            PlayerBox myPlayerBox = new PlayerBox();
        }
        CancelBox.show("Save " + pattern_or_queue, " ");
        proceed = CancelBox.getProceed();
        if (proceed) {
            if (megavoiceleader.InputParameters.get_q_mode() == true) {
                boolean newQDir = true;
                if(megavoiceleader.InputParameters.getQueueDir() != null) {
                    CancelBox.show("Change Queue Directory", megavoiceleader.InputParameters.getQueueDir().getAbsolutePath());
                    newQDir = CancelBox.getProceed();
                }
                if (newQDir) {
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    File selectedDirectory = directoryChooser.showDialog(ctrlStage);
                    if (selectedDirectory != null) {
                        megavoiceleader.InputParameters.setQueueDirectory(selectedDirectory.getAbsolutePath());
                        megavoiceleader.InputParameters.setQueueDir(selectedDirectory);
                    }                    
                }

                megavoiceleader.PatternQueueStorerSaver.save_queue();
            }
            else {
                if (megavoiceleader.InputParameters.getFilePath() == null) fileSaver();
                megavoiceleader.PatternStorerSaver1.save_pattern();
                megavoiceleader.PatternStorerSaver1.clear_pattern();
            }
        }
        if (megavoiceleader.InputParameters.get_q_mode() == false) {
            megavoiceleader.PatternStorerSaver1.clear_pattern();
        }
        
    }
    
    public void fileSaver() {
        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy-HH-mm");
        String dateString = DATE_FORMAT.format(today);
        int tempo_bpm = megavoiceleader.InputParameters.getTempo();
        final FileChooser fileChooser = new FileChooser();
        if (megavoiceleader.InputParameters.getFileDir() != null) {
            File dirLoc = megavoiceleader.InputParameters.getFileDir();
            System.out.println(dirLoc);
            fileChooser.setInitialDirectory(dirLoc);
        }
        fileChooser.setInitialFileName(tempo_bpm + "-" + dateString);
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("MIDI", "*.mid"), 
            new FileChooser.ExtensionFilter("ALL", "*.*")    
        );
        File file = fileChooser.showSaveDialog(ctrlStage);
        if (file != null) {
            File midi_file = new File(file.getAbsolutePath() + ".mid");
            megavoiceleader.InputParameters.setFilePath(midi_file);
        }  
    }
    
}
