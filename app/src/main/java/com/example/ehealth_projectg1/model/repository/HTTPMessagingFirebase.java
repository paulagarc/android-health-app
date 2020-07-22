package com.example.ehealth_projectg1.model.repository;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.ehealth_projectg1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class HTTPMessagingFirebase extends FirebaseMessagingService {

    private FirebaseRepository firebaseRepository;

   @Override
    public void onNewToken(String token) {
       //Save token in firebase for the user when it is new
        this.firebaseRepository = new FirebaseRepository();
        firebaseRepository.saveTokenHTTPMessaging(token, this);
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        //Show message as a notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(message.getNotification().getTitle())
                .setContentText(message.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }

   public void setHTTPMessagingToken(final Context context) {
       //Save the token in firebase when the user enters the application
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            //TODO inform the user that getting the token failed - posar observable?
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        //Save token in firebase
                        firebaseRepository = new FirebaseRepository();
                        firebaseRepository.saveTokenHTTPMessaging(token, context);

                    }
                });
    }
}
