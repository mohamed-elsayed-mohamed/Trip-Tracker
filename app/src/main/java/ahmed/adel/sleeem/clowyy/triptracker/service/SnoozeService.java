package ahmed.adel.sleeem.clowyy.triptracker.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.io.IOException;

import ahmed.adel.sleeem.clowyy.triptracker.MainActivity;
import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.managers.DoneTripReceiver;
import ahmed.adel.sleeem.clowyy.triptracker.managers.SnoozeCanclerBroadCast;

public class SnoozeService extends Service {
    public SnoozeService() {
    }


    private MediaPlayer player = new MediaPlayer();

    @Override
    public void onCreate() {
        super.onCreate();

        AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.amahmed);
        try {
            player.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//notesetDataSource(FileDescriptor, offset, length)Requiredoffset，If // is not filled in, there may be a problem of not being able to play, because we get it from the file called out in the raw folder //file.getStartOffset()Not 0,setDataSourceMethod ofoffsetThe default value is 0

//        try {
////            mp.setDataSource("https://www.sm3na.com/audio/cf1dd6ba6b0f");
////            mp.prepareAsync();
////            mp.start();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mp.setLooping(true);
//        mp.start();
        // Toast.makeText(getApplicationContext(), "wake up ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) player.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RemoteViews customView = new RemoteViews(getPackageName(), R.layout.dialog_notifier_snooze);

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        Intent hungupIntent = new Intent(getApplicationContext(), SnoozeCanclerBroadCast.class);

        String title = intent.getStringExtra("Title");
        String source = intent.getStringExtra("Source");
        String destination = intent.getStringExtra("Destination");
        String date = intent.getStringExtra("Date");
        int notificationId = intent.getIntExtra("notificationId",0);






        hungupIntent.putExtra("destination",destination);
        hungupIntent.putExtra("date",date);
        hungupIntent.putExtra("Title",title);
        hungupIntent.putExtra("Source",source);
        hungupIntent.putExtra("notificationId",notificationId);




        Intent answerIntent = new Intent(getApplication(), DoneTripReceiver.class);

        answerIntent.putExtra("destination",destination);
        answerIntent.putExtra("date",date);
        answerIntent.putExtra("notificationId",notificationId);



        // answerIntent.putExtra("Destination", destination);

       // if (intent.hasExtra("caller_text")) {
            answerIntent.putExtra("caller_text", intent.getStringExtra("caller_text"));
            customView.setTextViewText(R.id.txtDestination, destination);
            customView.setTextViewText(R.id.name, title);
      //  }

        //customView.setImageViewBitmap(R.id.photo, NotificationImageManager().getImageBitmap(intent.getStringExtra("user_thumbnail_image")))
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent hungupPendingIntent = PendingIntent.getBroadcast(this, 0, hungupIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent answerPendingIntent = PendingIntent.getActivity(this, 0, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        customView.setOnClickPendingIntent(R.id.btnAnswer, answerPendingIntent);
        customView.setOnClickPendingIntent(R.id.btnDecline, hungupPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("IncomingCall", "IncomingCall", NotificationManager.IMPORTANCE_HIGH);
            // notificationChannel.setSound(Uri.parse("https://www.sm3na.com/audio/cf1dd6ba6b0f"), null);
            notificationManager.createNotificationChannel(notificationChannel);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "IncomingCall");
            notification.setContentTitle(title);
            notification.setTicker("Call_STATUS");
            notification.setContentText(destination.equals("")?"no no ":destination);
            notification.setSmallIcon(R.drawable.ic_timer);
            notification.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
            notification.setCategory(NotificationCompat.CATEGORY_CALL);
            notification.setVibrate(null);
            notification.setOngoing(true);
            notification.setFullScreenIntent(pendingIntent, true);
            notification.setPriority(NotificationCompat.PRIORITY_HIGH);
            notification.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
            notification.setCustomContentView(customView);
            notification.setCustomBigContentView(customView);
            startForeground(notificationId, notification.build());
        } else {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this);

            notification.setContentTitle(title==null?"no title":title);
            notification.setTicker("Call_STATUS");
            notification.setContentText(destination.equals("")?"no no ":destination);
            notification.setSmallIcon(R.drawable.ic_delete);
            notification.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_details));
            notification.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
            notification.setVibrate(null);
            notification.setContentIntent(pendingIntent);
            notification.setOngoing(true);
            notification.setCategory(NotificationCompat.CATEGORY_CALL);
            notification.setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationCompat.Action hangupAction = new NotificationCompat.Action.Builder(android.R.drawable.sym_action_chat, "HANG UP", hungupPendingIntent).build();
            notification.addAction(hangupAction);
            notification.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
            startForeground(notificationId, notification.build());
        }


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}