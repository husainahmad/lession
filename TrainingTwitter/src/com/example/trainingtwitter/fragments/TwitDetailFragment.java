package com.example.trainingtwitter.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trainingtwitter.R;

public class TwitDetailFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.detail_fragment, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		Bundle args = getArguments();
		if (args != null) {
			updateArticleView(args.getString("text"), args.getString("user"), args.getString("date"));
		} else {
			updateArticleView("","","");
		}
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		ActionBar actionBar = getActivity().getActionBar();
		
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		} else {
			actionBar.setDisplayHomeAsUpEnabled(false);
		}
	}
	
	
	public void updateArticleView(String text, String user, String date) {
		TextView textView = (TextView) getActivity().findViewById(R.id.textView1);
		if (textView!=null)
			textView.setText(text);
		
		TextView textView2 = (TextView) getActivity().findViewById(R.id.textView2);
		if (textView2!=null) 
			textView2.setText(user);
		
		TextView textView3 = (TextView) getActivity().findViewById(R.id.textView3);
		if (textView3!=null) 
			textView3.setText(date);
		
		
	}
	
}
