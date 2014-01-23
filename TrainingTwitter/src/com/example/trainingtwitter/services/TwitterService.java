package com.example.trainingtwitter.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import com.example.trainingtwitter.R;
import com.example.trainingtwitter.TwitMainActivity;
import com.example.trainingtwitter.database.TwitterContentProvider;
import com.example.trainingtwitter.fragments.Authenticated;
import com.example.trainingtwitter.fragments.Tweet;
import com.example.trainingtwitter.fragments.Twitter;
import com.example.trainingtwitter.utils.Properties;
import com.google.gson.Gson;

public class TwitterService extends Service {
	
	private TwitterAlarmReceiver twitterAlarmReceiver = new TwitterAlarmReceiver();
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		twitterAlarmReceiver.setAlarm(getApplicationContext());
		registerReceiver(receiver, new IntentFilter("TwitList_Alarm"));
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle.getBoolean("success", true)) {
				downloadTweets();
			}
		}
	};
	
	public void downloadTweets() {
		
		ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			new DownloadTwitterTask().execute("TMCPoldaMetro");
		} else {
			Log.i(TwitterService.class.toString(), "No network connection available.");
		}
	}

	// Uses an AsyncTask to download a Twitter user's timeline
	private class DownloadTwitterTask extends AsyncTask<String, Void, String> {
		final static String CONSUMER_KEY = "ubgimP1QUhYAOYORxQMw4Q";
		final static String CONSUMER_SECRET = "QlG1vZPbJCcRrargCJo1fa9VULwtc3EQPHOt50qc8";
		final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
		final static String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";
//		final static String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/mentions_timeline.json?screen_name=";
//		final static String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/home_timeline.json?screen_name=";
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
		}
		@Override
		protected String doInBackground(String... screenNames) {
			String result = null;

			if (screenNames.length > 0) {
				result = getTwitterStream(screenNames[0]);
			}
			return result;
		}

		// onPostExecute convert the JSON results into a Twitter object (which
		// is an Array list of tweets
		@Override
		protected void onPostExecute(String result) {
			Twitter twits = jsonToTwitter(result);

			if (twits==null) return;
			
			ContentValues[] values = new ContentValues[twits.size()];
			int i = 0;
			// lets write the results to the console as well
			for (Tweet tweet : twits) {
				Log.i(TwitterService.class.toString(), tweet.getText());
				Log.i(TwitterService.class.toString(), tweet.getUser().getProfileImageUrl());
				ContentValues value = new ContentValues();
				value = TwitterContentProvider.generateTweet(tweet);
				
				values[i] = value;
				
				i++;
			}
			getContentResolver().bulkInsert(TwitterContentProvider.INFO_URI, values);
			
			if (!isActivityRunning()) sendNotification();
			
			sendBroadcast(new Intent("TwitList").putExtra("status", true));
			
		}

		// converts a string of JSON data into a Twitter object
		private Twitter jsonToTwitter(String result) {
			Twitter twits = null;
			if (result != null && result.length() > 0) {
				try {
					Gson gson = new Gson();
					twits = gson.fromJson(result, Twitter.class);
				} catch (IllegalStateException ex) {
					// just eat the exception
				}
			}
			return twits;
		}

		// convert a JSON authentication object into an Authenticated object
		private Authenticated jsonToAuthenticated(String rawAuthorization) {
			Authenticated auth = null;
			if (rawAuthorization != null && rawAuthorization.length() > 0) {
				try {
					Gson gson = new Gson();
					auth = gson.fromJson(rawAuthorization, Authenticated.class);
				} catch (IllegalStateException ex) {
					// just eat the exception
				}
			}
			return auth;
		}

		private String getResponseBody(HttpRequestBase request) {
			StringBuilder sb = new StringBuilder();
			try {

				DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
				HttpResponse response = httpClient.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				String reason = response.getStatusLine().getReasonPhrase();

				if (statusCode == 200) {

					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();

					BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
					String line = null;
					while ((line = bReader.readLine()) != null) {
						sb.append(line);
					}
				} else {
					sb.append(reason);
				}
			} catch (UnsupportedEncodingException ex) {
			} catch (ClientProtocolException ex1) {
			} catch (IOException ex2) {
			}
			return sb.toString();
		}

		private String getTwitterStream(String screenName) {
			String results = null;

			// Step 1: Encode consumer key and secret
			try {
				// URL encode the consumer key and secret
				String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
				String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET,"UTF-8");

				// Concatenate the encoded consumer key, a colon character, and
				// the
				// encoded consumer secret
				String combined = urlApiKey + ":" + urlApiSecret;

				// Base64 encode the string
				String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

				// Step 2: Obtain a bearer token
				HttpPost httpPost = new HttpPost(TwitterTokenURL);
				httpPost.setHeader("Authorization", "Basic " + base64Encoded);
				httpPost.setHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
				httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
				String rawAuthorization = getResponseBody(httpPost);
				Authenticated auth = jsonToAuthenticated(rawAuthorization);

				// Applications should verify that the value associated with the
				// token_type key of the returned object is bearer
				if (auth != null && auth.getToken_type().equals("bearer")) {

					// Step 3: Authenticate API requests with bearer token
					HttpGet httpGet = new HttpGet(TwitterStreamURL + screenName);

					// construct a normal HTTPS request and include an
					// Authorization
					// header with the value of Bearer <>
					httpGet.setHeader("Authorization", "Bearer "+ auth.getAccess_token());
					httpGet.setHeader("Content-Type", "application/json");
					// update the results with the body of the response
					results = getResponseBody(httpGet);
				}
			} catch (UnsupportedEncodingException ex) {
			} catch (IllegalStateException ex1) {
			}
			return results;
		}
	}

	@SuppressWarnings("deprecation")
	public void sendNotification() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Context context = getApplicationContext();
		Notification notification = new Notification(R.drawable.ic_launcher, "My Twitter notification", System.currentTimeMillis());
		Intent intent = new Intent(context, TwitMainActivity.class);
		
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);				
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(context, "My Twitter Notification", "My Twitter Notification Has Finish Loading..", pendingIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notificationManager.notify(Properties.APP_ID, notification);
	}
	
	public boolean isActivityRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = manager.getRunningTasks(1);

		if (taskInfo.get(0).topActivity.getClassName().equals(TwitMainActivity.class.getName())) {
			return true;
		} else {
			return false;
		}

	}
}
