package com.example.actionbar1;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class LocationFound extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
	}
}
