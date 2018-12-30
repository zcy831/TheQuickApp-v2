package group12.thequickapp.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import group12.thequickapp.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AudioRecordActivity extends Activity {
	private boolean isRecording = false ;
    private Object tmp = new Object() ;
    boolean isSecond = false;

   
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        setRecordStartButton(true);
        setRecordStopButton(false);
       
        Button start = (Button)findViewById(R.id.start_bt) ;
        start.setOnClickListener(new OnClickListener()
        {
 
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Log.d("chengyuan", "record start");
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                      record();
                    }    
                  });
                  thread.start();
                  // findViewById(R.id.start_bt).setEnabled(false) ;
                  // findViewById(R.id.end_bt).setEnabled(true) ;
                  setRecordStartButton(false);
                  setRecordStopButton(true);
            }
         
        }) ;
       
        Button play = (Button)findViewById(R.id.play_bt) ;
        play.setOnClickListener(new OnClickListener(){
 
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                play();
            }
         
        }) ;
       
        Button stop = (Button)findViewById(R.id.end_bt) ;
        stop.setEnabled(false) ;
        stop.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                isRecording = false ;
                // findViewById(R.id.start_bt).setEnabled(true) ;
                // findViewById(R.id.end_bt).setEnabled(false) ;
                setRecordStartButton(true);
                setRecordStopButton(false);
            }
         
        }) ;
        
        Button save = (Button) findViewById(R.id.saveSound);
        save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					if(isSecond){
						int threshold = 1;
						Intent openSelectApp = new Intent(AudioRecordActivity.this, GetAppActivity.class);
						openSelectApp.putExtra("threshold", threshold);
						openSelectApp.putExtra("FromWhich", "AudioRecordActivity");
						startActivity(openSelectApp);
					}else{
						Toast.makeText(AudioRecordActivity.this, "Please record your sound again", Toast.LENGTH_SHORT).show();
						isSecond = true;
					}
					
				}catch(Exception e){
					Toast.makeText(AudioRecordActivity.this, "Audio Not Recorded", Toast.LENGTH_SHORT).show();
				}
			}
		});
       
    }

    private void setRecordStartButton(boolean isEnabled) {
        findViewById(R.id.start_bt).setEnabled(isEnabled);
        ((Button)findViewById(R.id.start_bt)).setTextColor(isEnabled ? Color.WHITE : Color.GRAY);
    }

    private void setRecordStopButton(boolean isEnabled) {
        findViewById(R.id.end_bt).setEnabled(isEnabled) ;
        ((Button)findViewById(R.id.end_bt)).setTextColor(isEnabled ? Color.WHITE : Color.GRAY);
    }
 
    public void play() {
      // Get the file we want to playback.
      File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/re.pcm");
      // Get the length of the audio stored in the file (16 bit so 2 bytes per short)
      // and create a short array to store the recorded audio.
      int musicLength = (int)(file.length()/2);
      short[] music = new short[musicLength];
 
 
      try {
        // Create a DataInputStream to read the audio data back from the saved file.
        InputStream is = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dis = new DataInputStream(bis);
         
        // Read the file into the music array.
        int i = 0;
        while (dis.available() > 0) {
          music[i] = dis.readShort();
          i++;
        }
 
 
        // Close the input streams.
        dis.close();    
 
 
        // Create a new AudioTrack object using the same parameters as the AudioRecord
        // object used to create the file.
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                               11025,
                                               AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                               AudioFormat.ENCODING_PCM_16BIT,
                                               musicLength*2,
                                               AudioTrack.MODE_STREAM);
        // Start playback
        audioTrack.play();
     
        // Write the music buffer to the AudioTrack object
        audioTrack.write(music, 0, musicLength);
 
        audioTrack.stop() ;
 
      } catch (Throwable t) {
        Log.e("AudioTrack","Playback Failed");
      }
    }
 
    public void record() {
      int frequency = 11025;
      int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
      int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
      File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/re.pcm");
      
      // Delete any previous recording.
      if (file.exists())
        file.delete();
 
      Log.d("chengyuan", "create file");
      // Create the new file.
      try {
        file.createNewFile();
      } catch (IOException e) {
        throw new IllegalStateException("Failed to create " + file.toString());
      }
      
      try {
        // Create a DataOuputStream to write the audio data into the saved file.
        OutputStream os = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        DataOutputStream dos = new DataOutputStream(bos);
        
        // Create a new AudioRecord object to record the audio.
        int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration,  audioEncoding);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                                  frequency, channelConfiguration,
                                                  audioEncoding, bufferSize);
      
        short[] buffer = new short[bufferSize];  
        audioRecord.startRecording();

        Log.d("chengyuan", "start record loop");
        isRecording = true;
        while (isRecording) {
          int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
          for (int i = 0; i < bufferReadResult; i++)
            dos.writeShort(buffer[i]);
        }
 
        Log.d("chengyuan", "stop record");
        audioRecord.stop();
        dos.close();
      
      } catch (Throwable t) {
        Log.e("AudioRecord","Recording Failed");
      }
    }
}
