package com.example.trainingtwitter;

import java.lang.reflect.Field;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewConfiguration;

public class TwitSettingActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initView();
		
		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle("Training");
		actionBar.setTitle("My Twitter Client"); 
		actionBar.setHomeButtonEnabled(true);
		
		getOverflowMenu();
		
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

	private void initView() {
		setContentView(R.layout.setting);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.setting_main, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
