package sk.it.android.myapplication_servicetest1;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import sk.it.android.myapplication_servicetest1.service.MyService;
import sk.it.android.myapplication_servicetest1.util.CreateNotification;
import sk.it.android.myapplication_servicetest1.util.MyAdapter;
import sk.it.android.myapplication_servicetest1.util.Song;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnItemListener {

    private static final int PERMISSION_CODE = 1;
    private static final String PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;

    RecyclerView recyclerView;
    ArrayList<Song> arrayList;
    MyAdapter adapter;
    MediaMetadataRetriever metadataRetriever;

    ConstraintLayout currentPlayingLayout;
    TextView title, artist, duration;

    MyService myService;
    boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        if (checkMyPermission()) {
            renderList();
        } else {
            requestMyPermissions();
        }
    }

    private void requestMyPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{PERMISSION}, PERMISSION_CODE);
    }
    private boolean checkMyPermission() {
        return ActivityCompat.checkSelfPermission(this, PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                renderList();
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Permission denied", Snackbar.LENGTH_INDEFINITE).setAction("Go to settings", v -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }).show();
            }
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder localBinder = (MyService.LocalBinder) service;
            myService = localBinder.getService();
            isBound = true;

            currentPlayingLayout = findViewById(R.id.currentPlayingLayout);
            currentPlayingLayout.setVisibility(View.INVISIBLE);
            if (myService.isPlaying()) {
                initCurrentPlayingLayout(myService.getSong());
                currentPlayingLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    public void renderList() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getMusic();
        adapter = new MyAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);
    }

    public void getMusic() {
        arrayList = new ArrayList<>();
        metadataRetriever = new MediaMetadataRetriever();

        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            long id;
            String data, title, album, artist, duration;
            Song song;
            do {
                id = songCursor.getLong(songCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                data = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                metadataRetriever.setDataSource(data);
                title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                album = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                song = new Song(id, title, album, artist, duration);
                arrayList.add(song);
            } while (songCursor.moveToNext());
            songCursor.close();
        }
    }

    @Override
    public void onItemClick(int position) {
        Song song = arrayList.get(position);

        Intent intent = new Intent(this, MyService.class);
        intent.setAction("PLAY");
        intent.putExtra("song", song);
        startService(intent);

        intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("song", song);
        startActivity(intent);

        initCurrentPlayingLayout(song);
        currentPlayingLayout.setVisibility(View.VISIBLE);

        CreateNotification.createNotification(this, song, R.drawable.ic_stop);
    }

    private void initCurrentPlayingLayout(Song song) {
        title = findViewById(R.id.currTextViewTitle);
        artist = findViewById(R.id.currTextViewArtist);
        duration = findViewById(R.id.currTextViewDuration);

        title.setSelected(true);

        title.setText(song.getTitle());
        artist.setText(song.getArtist());
        duration.setText(song.getDurationReadable());
    }

    public void goToCurrentlyPlaying(View view) {
        Song song = myService.getSong();
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("song", song);
        startActivity(intent);
    }
}