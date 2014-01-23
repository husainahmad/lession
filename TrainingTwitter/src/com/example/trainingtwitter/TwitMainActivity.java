package com.example.trainingtwitter;

import java.lang.reflect.Field;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import com.example.trainingtwitter.fragments.TwitDetailFragment;
import com.example.trainingtwitter.fragments.TwitListFragment;
import com.example.trainingtwitter.utils.Properties;


public class TwitMainActivity extends Activity {

	private TwitListFragment twitListFragment =  new TwitListFragment();
	private TwitDetailFragment twitDetailFragment = new TwitDetailFragment();
	private ActionBar actionBar;
	private MenuItem menuItem;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initView();
		
		actionBar = getActionBar();
		actionBar.setSubtitle("Training");
		actionBar.setTitle("My Twitter Client"); 
		
		getOverflowMenu();
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(Properties.APP_ID);
	}
	
	private void getOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			sendBroadcast(new Intent("TwitList_Alarm").putExtra("status", true));
			
			// change the icon
			
			menuItem = item;
			menuItem.setActionView(R.layout.progressbar);
			menuItem.expandActionView();
			
			break;
		case R.id.action_settings:
			startActivity(new Intent(this, TwitSettingActivity.class));
			break;
		case R.id.action_about:
			startActivity(new Intent(this, TwitterAboutUsActivity.class));
			break;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initView() {
		setContentView(R.layout.activity_main);
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		if (findViewById(R.id.detail_container) != null) {
			fragmentTransaction.replace(R.id.list_container, twitListFragment);
			fragmentTransaction.replace(R.id.detail_container, twitDetailFragment);
			fragmentTransaction.commit();
		} else {
			if (findViewById(R.id.list_container) != null) {
				twitListFragment = new TwitListFragment();
				fragmentTransaction.replace(R.id.list_container, twitListFragment);
			} 
			fragmentTransaction.commit();
		}
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle.getBoolean("success", true)) {
				if (menuItem!=null) {
					menuItem.collapseActionView();
					menuItem.setActionView(null);
				}
				twitListFragment.loadsTweets();
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, new IntentFilter("TwitList"));
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

}