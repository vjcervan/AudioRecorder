import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.*;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.sound.sampled.*;
import javafx.scene.Node;

public class AudioRecorder extends Application
{
	private AudioFormat audioFormat;
  	private TargetDataLine targetDataLine;
  	
	@Override
    public void start(Stage primaryStage)
    {
    	Button startButton = new Button("Start");
    	Button stopButton = new Button("Stop");
    	startButton.setLayoutX(40);
    	startButton.setLayoutY(40);
    	startButton.setDisable(false);
    	stopButton.setLayoutX(100);
    	stopButton.setLayoutY(40);
    	stopButton.setDisable(true);
    	
    	startButton.setOnAction((ActionEvent e) -> {
    		startButton.setDisable(true);
            stopButton.setDisable(false);
            try
            {
                recordAudio();
            } catch (LineUnavailableException ex) {
                ex.printStackTrace();
                System.exit(0);
            }
    	});
    	
    	stopButton.setOnAction((ActionEvent e) -> {
    		startButton.setDisable(false);
            stopButton.setDisable(true);
            this.targetDataLine.stop();
            this.targetDataLine.close();
    	});
    	
    	Pane pane = new Pane();
    	Text text = new Text(20, 20, "Microphone recorder");
    	pane.getChildren().addAll(text, startButton, stopButton);
    	
    	Scene scene = new Scene(pane, 300, 100);
    	primaryStage.setTitle("Audio Recorder");
    	primaryStage.setScene(scene);
    	primaryStage.show();
    	
    	primaryStage.setOnCloseRequest((WindowEvent e) -> {
        	Platform.exit();
		});	
    }
    
    private void recordAudio() throws LineUnavailableException
    {
    	this.audioFormat = new AudioFormat(8000.0F, 16, 1, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        try
        {
            this.targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        } catch (SecurityException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        RecordingThread recThread = new RecordingThread();
        recThread.start();
    }
  	
  	protected class RecordingThread extends Thread 
  	{
  		@Override
  		public void run()
  		{
    		AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
            File audioFile = new File("MyRecording.wav");
            try
            {
                AudioRecorder.this.targetDataLine.open(AudioRecorder.this.audioFormat);
		    } catch (LineUnavailableException e) {
                e.printStackTrace();
                System.exit(0);
            }
            AudioRecorder.this.targetDataLine.start();
            AudioInputStream audioInput = new AudioInputStream(AudioRecorder.this.targetDataLine);
            try
            {
                AudioSystem.write(audioInput, fileType, audioFile);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
	}
	
	public static void main(String[] args) 
	{
		launch(args);
	}
}