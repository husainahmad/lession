package com.example.trainingtwitter;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class TwitterAboutUsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle("Training");
		actionBar.setTitle("My Twitter Client"); 
		actionBar.setHomeButtonEnabled(true);
		
		initView();
	}
	
	private void initView() {
		setContentView(R.layout.about);
	}
}
