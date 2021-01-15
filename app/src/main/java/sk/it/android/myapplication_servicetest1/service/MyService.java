package sk.it.android.myapplication_servicetest1.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.widget.Toast;

import sk.it.android.myapplication_servicetest1.util.Song;

public class MyService extends Service {

    MediaPlayer mediaPlayer;
    Song song;
    Uri uri;

    private final IBinder iBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case "PLAY":
                actionPlay(intent);
                break;
            case "PAUSE":
                actionPause();
                break;
            case "RESUME":
                actionResume();
                break;
            case "SEEK_TIME_TO":
                actionSeekTimeTo(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void actionPlay(Intent intent) {
        if (song != null) {
            Song newSong = intent.getParcelableExtra("song");
            if (song.getId() == newSong.getId())
                return;
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPlayer.reset();
        }
        song = intent.getParcelableExtra("song");
        uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.getId());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
    }
    private void actionPause() {
        mediaPlayer.pause();
    }
    private void actionResume() {
        mediaPlayer.start();
    }
    private void actionSeekTimeTo(Intent intent) {
        int progress = intent.getIntExtra("progress", 0);
        mediaPlayer.seekTo(progress);
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
    public String getCurrentPositionReadable() {
        return song.convertToReadable(String.valueOf(getCurrentPosition()));
    }
    public Song getSong() {
        return song;
    }
}