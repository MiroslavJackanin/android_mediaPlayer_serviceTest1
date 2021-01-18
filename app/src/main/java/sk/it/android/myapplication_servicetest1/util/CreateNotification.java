package sk.it.android.myapplication_servicetest1.util;

import android.app.Notification;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import sk.it.android.myapplication_servicetest1.R;

public class CreateNotification {
    public static final String CHANNEL_ID = "channel";

    public static final String ACTION_PLAY = "actionPlay";

    public static Notification notification;

    public static void createNotification(Context context, Song song, int playButton) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

        notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_music_note_black_24dp)
                .setContentTitle(song.getTitle())
                .setContentText(song.getArtist())
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.image))
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        notificationManagerCompat.notify(1, notification);
    }
}
