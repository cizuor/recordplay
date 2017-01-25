package com.example.pierre.eucalyptus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.suitebuilder.TestMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class record extends AppCompatActivity {


    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;

    //private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    //private PlayButton   mPlayButton = null;
    private MediaPlayer mPlayer = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    private TextView energi;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }


    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }


    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }



    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();



    }


    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        //FileInputStream str = null;
        DataInputStream str =null;

        int taille=200;
        int[] tab=new int[taille] ;
        List<Integer> values = new ArrayList<>();
        int i=0;
        int j=0;



        try {
            //str = new FileInputStream(mFileName);
            str = new DataInputStream(new FileInputStream(mFileName));
            //str = new MediaRecorder.AudioSource(mFileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            while(true){
                values.add(str.readUnsignedShort());
            }


            /*while(true) {
                int tmp;
                tmp=str.readUnsignedShort();
                if(tmp==-1) {
                    break;
                }
                tab[i%taille]=tmp;
                i++;
            }*/
            /*for (i=0;i<taille;i++){
                tab[i]=str.read();
            }*/

            /*for (i=0;i<taille;i++){
                j=tab[i]+j;
            }*/

            //a = a + Integer.toString(str.read());

        } catch (IOException e) {
            e.printStackTrace();
            for (i=values.size();i>values.size()-50;i--){
                j=j+(values.get(values.size()-i)*values.get(values.size()-i));
            }
            j=j/50;
            energi.setText(Integer.toString(j));
            //energi.setText(values.toString());
        } finally {
            try {
                str.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        energi = (TextView)findViewById(R.id.textView);

        final Button mRecordButton = (Button) findViewById(R.id.record);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    mRecordButton.setText("Stop recording");
                } else {
                    mRecordButton.setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        });

        final Button mPlayButton = (Button) findViewById(R.id.play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    mPlayButton.setText("Stop playing");
                } else {
                    mPlayButton.setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        });







    }


    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }



}
