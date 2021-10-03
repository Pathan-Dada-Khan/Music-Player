package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private double startTime=0;
    private double endTime=0;

    private MediaPlayer music;
    private AudioManager mAudioManager;

    private Handler myHandler = new Handler();
    private SeekBar seekBar;
    private TextView start;
    private TextView end;

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            ImageView play=(ImageView)findViewById(R.id.play);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                if(music.isPlaying()) {
                    play.setImageResource(R.drawable.ic_play);
                    music.pause();
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if(!music.isPlaying()){
                    play.setImageResource(R.drawable.ic_pause);
                    music.start();
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                releaseMediaPlayer();
            }
            else if( focusChange== AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK){
                if(music.isPlaying())music.setVolume(0.1f,0.1f);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView play=findViewById(R.id.play);
        ImageView left=findViewById(R.id.left);
        ImageView right=findViewById(R.id.right);

        music =MediaPlayer.create(this,R.raw.meri_aashiqui);

        start =(TextView)findViewById(R.id.songStart);
        end =(TextView)findViewById(R.id.songEnd);
        TextView name =(TextView)findViewById(R.id.songName);
        ImageView image =(ImageView)findViewById(R.id.songImage);
        seekBar =(SeekBar)findViewById(R.id.seekbar);
        seekBar.setMax(music.getDuration());
        setTime();

        mAudioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
                seekBar.setProgress((int)startTime);
                myHandler.postDelayed(UpdateSongTime,100);
                int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    if (!music.isPlaying()) {
                        play.setImageResource(R.drawable.ic_pause);
                        music.start();
                    } else {
                        play.setImageResource(R.drawable.ic_play);
                        music.pause();
                    }
                }
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music.seekTo(0);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music.seekTo(music.getCurrentPosition()+10000);
                music.start();
                play.setImageResource(R.drawable.ic_pause);
                setTime();
                myHandler.postDelayed(UpdateSongTime,100);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    music.seekTo(progress);
                    setTime();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            startTime = music.getCurrentPosition();
            setTime();
            seekBar.setProgress((int)startTime);
            myHandler.postDelayed(this,100);
        }
    };
    public void setTime(){
        startTime=music.getCurrentPosition();
        endTime=music.getDuration();
        start.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime)-
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));

        end.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) endTime),
                TimeUnit.MILLISECONDS.toSeconds((long) endTime)-
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) endTime))));
    }
    public void releaseMediaPlayer(){
        if(music!=null){
            music.release();
            music=null;
        }
    }
}