package org.planetgammu.android.transautha;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class C2DMReceiver extends BroadcastReceiver {
	
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
			handleRegistration(context, intent);
		} else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
			handleMessage(context, intent);
		}
	}
	private void handleRegistration(Context context, Intent intent) {
		String registration = intent.getStringExtra("registration_id");
		String error = intent.getStringExtra("error");
		if ( error != null) {
			// Registration failed, should try again later.
		} else if (intent.getStringExtra("unregistered") != null) {
			// unregistration done, new messages from the authorized sender will be rejected
		} else if (registration != null) {
			// Send the registration ID to the 3rd party site that is sending the messages.
			// This should be done in a separate thread.
			// When done, remember that all registration is done. 
			new Thread(new PostRegistration(context, registration, true)).start();
		}
	}
	private void handleMessage(Context context, Intent intent) {
		String account = intent.getStringExtra("account");
		String amount = intent.getExtras().getString("amount");
		String pin = intent.getExtras().getString("pin");

		String message = context.getString(R.string.ta_msg);
		String message_merge = String.format(message, account, amount, pin);

		Intent i = new Intent(context, TransAuthActivity.class);
		i.putExtra("message", message_merge);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//context.startActivity(i);

		PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(),
				0,	i, PendingIntent.FLAG_CANCEL_CURRENT);

		Notification notification = new Notification(R.drawable.ic_launcher_ta,
				"Transaction Available", System.currentTimeMillis());
//		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.ledOnMS = 100;
		notification.ledOffMS = 800;
		notification.ledARGB = 0x018000ff;
		notification.setLatestEventInfo(context, "TransAuth MK2", 
				"New transaction available", pendingIntent);

		NotificationManager mNotificationManager = (NotificationManager)
				context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, notification);
	}
}