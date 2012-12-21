package org.planetgammu.android.transautha;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class TransAuthActivity extends Activity {

	private TextView textStatus;
	private TextView textMessage;
	static final String REG = "registration";
	static final String PREFS_NAME = "PrefsFile";
	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Possible work around for market launches. See http://code.google.com/p/android/issues/detail?id=2373
		// for more details. Essentially, the market launches the main activity on top of other activities.
		// we never want this to happen. Instead, we check if we are the root and if not, we finish.
		if (!isTaskRoot()) {
		    final Intent intent = getIntent();
		    final String intentAction = intent.getAction(); 
		    if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
		        Log.w("TransAuthA", "Main Activity is not the root.  Finishing Main Activity instead of launching.");
		        finish();
		        return;       
		    }
		}
		setContentView(R.layout.main);
		textStatus = (TextView)findViewById(R.id.textStatus);
		textMessage = (TextView)findViewById(R.id.textMessage);
		settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		
		((Button)findViewById(R.id.title_reg)).setClickable(false);
		((Button)findViewById(R.id.title_auth)).setClickable(false);	//.setOnClickListener(new OnClickListener() { 
/*			public void onClick(View view) {
//				new Thread(new TestHTTP(TransAuthActivity.this)).start();
				new Thread(new PostRegistration(TransAuthActivity.this, "new message", false)).start();
			}});*/
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String message = extras.getString("message");
			String refid = extras.getString("refid");
			if (message != null) {
				textMessage.setText(message);
			} else if (refid != null) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(REG, refid);
				editor.commit();
			}
		}
		if (!settings.contains(REG)) {
			register();
		}
		String reg_id = settings.getString(REG, getString(R.string.register));
		textStatus.setText(reg_id);
	}
/*	@Override
	protected void onResume() {
		// don't forget to call this, it seems to be rather important
		super.onResume();
		String reg_id = settings.getString(REG, getString(R.string.register));
		textStatus.setText(reg_id);
	}*/
	private void register() {
		Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
		registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0)); // boilerplate
		//registrationIntent.putExtra("sender", "transauth@planetgammu.org");
		registrationIntent.putExtra("sender", "200113783145");
		startService(registrationIntent);
	}
}
