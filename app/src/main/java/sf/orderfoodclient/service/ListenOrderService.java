package sf.orderfoodclient.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sf.orderfoodclient.R;
import sf.orderfoodclient.activity.OrderStatusActivity;
import sf.orderfoodclient.common.Common;
import sf.orderfoodclient.model.Request;

public class ListenOrderService extends Service implements ChildEventListener {
    FirebaseDatabase db;
    DatabaseReference requests;


    public ListenOrderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requests.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification(String key, Request request) {
        Intent intent = new Intent(getBaseContext(), OrderStatusActivity.class);
        intent.putExtra("userPhone", request.getPhone());
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setTicker("FOOD")
                .setContentInfo("Your order was updated")
                .setContentText("Order #" + key + " was update status to " + Common.convertCodeToStatus(request.getStatus()))
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        // trigger
        Request request = dataSnapshot.getValue(Request.class);
        showNotification(dataSnapshot.getKey(), request);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
