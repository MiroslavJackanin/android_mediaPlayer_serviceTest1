package sk.it.android.myapplication_servicetest1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import sk.it.android.myapplication_servicetest1.service.MyService;
import sk.it.android.myapplication_servicetest1.util.Song;

public class PlayerActivity extends AppCompatActivity {

    MyService myService;
    boolean isBound = false;
    AudioManager audioManager;
    Handler handler;

    Song song;

    Button playBtn;
    SeekBar timeSeekBar;
    SeekBar volumeSeekBar;

    TextView titleTextView;
    TextView albumTextView;
    TextView artistTextView;

    TextView elapsedTimeLabel;
    TextView remainingTimeLabel;

    Intent intent;

    // TODO save images with intentJobService to folder
    // TODO sorting/grouping/filtering main activity recyclerView
    // TODO when sorting
    // TODO playLists cards on top -> move to playlist and back to main (swipe left or right) menu button in top left actionBar opens side pane for some settings, long click on item in recycler view adds item to playlist (prompt confirm)
    // TODO next and previous song buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder localBinder = (MyService.LocalBinder) service;
            myService = localBinder.getService();
            isBound = true;
            handler = new Handler();

            playBtnInit();
            intentInit();

            timeSeekBarInit();
            volumeSeekBarInit();

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    timeSeekBar.setProgress(myService.getCurrentPosition());
                }
            }, 0, 100);

            scoutProgress.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    Thread scoutProgress = new Thread(new Runnable() {
        public void run() {
            while (true) {
                handler.post(() -> elapsedTimeLabel.setText(myService.getCurrentPositionReadable()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private void intentInit() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        song = intent.getParcelableExtra("song");

        titleTextView = findViewById(R.id.title);
        albumTextView = findViewById(R.id.album);
        artistTextView = findViewById(R.id.artist);

        titleTextView.setSelected(true);

        titleTextView.setText(song.getTitle());
        albumTextView.setText(song.getAlbum());
        artistTextView.setText(song.getArtist());

        elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.remainingTimeLabel);
        remainingTimeLabel.setText(song.getDurationReadable());
    }

    private void playBtnInit() {
        playBtn = findViewById(R.id.play_btn);
        if (myService.isPlaying()) {
            playBtn.setBackgroundResource(R.drawable.stop);
        } else {
            playBtn.setBackgroundResource(R.drawable.play);
        }
    }
    public void playBtnClick(View view) {
        if (myService.isPlaying()) {
            playBtn.setBackgroundResource(R.drawable.play);
            actionPause();
        } else {
            playBtn.setBackgroundResource(R.drawable.stop);
            actionResume();
        }
    }

    private void timeSeekBarInit() {
        timeSeekBar = findViewById(R.id.seek_bar);
        timeSeekBar.setMax(Integer.parseInt(song.getDuration()));
        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) actionSeekTimeTo(progress);
                timeSeekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                actionPause();
                playBtn.setBackgroundResource(R.drawable.play);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                actionResume();
                playBtn.setBackgroundResource(R.drawable.stop);
            }
        });
    }

    private void volumeSeekBarInit() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeSeekBar = findViewById(R.id.volume_seek_bar);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void actionPause() {
        intent = new Intent(this, MyService.class);
        intent.setAction("PAUSE");
        startService(intent);
    }
    private void actionResume() {
        intent = new Intent(this, MyService.class);
        intent.setAction("RESUME");
        startService(intent);
    }
    private void actionSeekTimeTo(int progress) {
        intent = new Intent(this, MyService.class);
        intent.setAction("SEEK_TIME_TO");
        intent.putExtra("progress", progress);
        startService(intent);
    }
}