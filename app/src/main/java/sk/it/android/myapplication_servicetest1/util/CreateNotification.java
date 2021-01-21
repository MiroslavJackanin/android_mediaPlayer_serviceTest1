package sk.it.android.myapplication_servicetest1.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import sk.it.android.myapplication_servicetest1.MainActivity;
import sk.it.android.myapplication_servicetest1.R;
import sk.it.android.myapplication_servicetest1.service.NotificationReceiver;

public class CreateNotification {

    public static void createNotification(Context context, Song song, int drawable) {
        Bitmap myImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("song", song);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Intent actionIntent = new Intent(context.getApplicationContext(), NotificationReceiver.class);
        actionIntent.setAction("PLAY");
        actionIntent.putExtra("song", song);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 3, actionIntent, 0);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(drawable, "Play", actionPendingIntent).build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel1")
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(song.getTitle())
                .setContentText(song.getAlbum())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .addAction(action)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0))
                .setLargeIcon(myImg);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(1, builder.build());
    }
}
