package org.planetgammu.android.transautha;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;

public class TestHTTP implements Runnable {
	private Context context;
	
	TestHTTP(Context context) {
		this.context = context; 
	}

	public void run2() {
		String line = null;
		KeyStore ks = null;
		try {
			InputStream ink = context.getResources().openRawResource(R.raw.equifax);
			ks = KeyStore.getInstance("BKS");
			ks.load(ink, "equifax".toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
			tmf.init(ks);
			
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, tmf.getTrustManagers(), null); 

			URL url = new URL("https://transauth.appspot.com/");
			HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			urlConnection.setSSLSocketFactory(sslcontext.getSocketFactory());
			
			try {
				//InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				InputStream is = urlConnection.getInputStream();
				BufferedReader in  = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				while ((line = in.readLine()) != null) {
					sb.append(line + '\n');
				}

				System.out.println(sb.toString());
			} finally {
				urlConnection.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			HttpClient client = new DefaultHttpClient();
			KeyStore ks = getTrustStore();
			SSLSocketFactory socketFactory = new SSLSocketFactory(ks);
			Scheme sch = new Scheme("https", (SocketFactory) socketFactory, 443);  
			client.getConnectionManager().getSchemeRegistry().register(sch);
			
//			HttpGet get = new HttpGet("https://transauth.appspot.com/");
//			HttpResponse response = client.execute(get);

			HttpPost post = new HttpPost("https://transauth.appspot.com/aregister");
			List<NameValuePair> parms = new ArrayList<NameValuePair>(1);
			parms.add(new BasicNameValuePair("registrationid", "hello world"));
			post.setEntity(new UrlEncodedFormEntity(parms));
			HttpResponse response = client.execute(post);

			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() != 200) {
				//something
			}
			String result = EntityUtils.toString(response.getEntity());
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private KeyStore getTrustStore() {
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
	}
}