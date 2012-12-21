package org.planetgammu.android.transautha;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.Intent;

public class PostRegistration implements Runnable {
	private String registration;
	private Context context;
	private boolean sendIntent;

	PostRegistration(Context context, String registration, boolean sendIntent) {
		this.registration = registration;
		this.context = context;
		this.sendIntent = sendIntent;
	}
	public void run() {
		try {
			HttpClient client = new DefaultHttpClient();
			/* KeyStore ks = getTrustStore();
			SSLSocketFactory socketFactory = new SSLSocketFactory(ks);

			Scheme sch = new Scheme("https", (SocketFactory) socketFactory, 443);  
			client.getConnectionManager().getSchemeRegistry().register(sch); */

			HttpPost post = new HttpPost("https://transauth.appspot.com/aregister");
			List<NameValuePair> parms = new ArrayList<NameValuePair>(1);
			parms.add(new BasicNameValuePair("registrationid", registration));
			post.setEntity(new UrlEncodedFormEntity(parms));
			HttpResponse response = client.execute(post);

			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() != 200) {
				//something
			}
			String result = EntityUtils.toString(response.getEntity());

			JSONObject object = (JSONObject) new JSONTokener(result).nextValue();
			int error = object.getInt("error");
			if (error != 0 ) {
				//something
			}
			String refid = object.getString("refid");
			if (sendIntent) {
				Intent i = new Intent(context, TransAuthActivity.class);
				i.putExtra("refid", refid);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(i);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*	private KeyStore getTrustStore() {
		KeyStore ks;
		InputStream ink = context.getResources().openRawResource(R.raw.equifax);
		try {
			ks = KeyStore.getInstance("BKS");
			try {
				ks.load(ink, "equifax".toCharArray());
			} finally {
				ink.close();
			}
		} catch (Exception e) {
			ks = null;
		}
		return(ks);
	} */
}