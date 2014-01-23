package com.example.trainingtwitter.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.trainingtwitter.R;
import com.example.trainingtwitter.adapters.TwitListAdapter;
import com.example.trainingtwitter.database.TwitterContentProvider;
import com.example.trainingtwitter.services.TwitterService;

public class TwitListFragment extends Fragment implements OnItemClickListener{
	private String TAG = TwitListFragment.class.toString();
	private ListView listView;
	private List<Tweet> tweets = new ArrayList<Tweet>();
	private TwitListAdapter twitAdapter;
	private boolean isprocessing = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent serviceIntent = new Intent(getActivity(), TwitterService.class);
		Log.i(TAG, "onCreate : " + savedInstanceState);
		getActivity().startService(serviceIntent);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_fragment, container, false);
		
		listView = (ListView) view.findViewById(R.id.twitList);
		listView.setOnItemClickListener(this);
		
		twitAdapter = new TwitListAdapter(getActivity(), tweets);
		listView.setAdapter(twitAdapter);
		
		Log.i(TAG, "onCreateView : " + savedInstanceState);
		
		return view;
	}
	
	public void loadsTweets() {
		if (!isprocessing)
			new LoadTweetTask().execute();
	}
	
	private class LoadTweetTask extends AsyncTask<Void, Void, List<Tweet>> {
		private ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(getActivity());
			pd.setMessage("Mohon tunggu...");
			pd.show();
			
			isprocessing = true;
		}
		@Override
		protected List<Tweet> doInBackground(Void... params) {
			try {
				Cursor cursor = getActivity().getContentResolver().query(TwitterContentProvider.INFO_URI, null, null, null, "tweet_created_at");
				
				tweets = new ArrayList<Tweet>();
				while (cursor.moveToNext()) {
					Tweet tweet = new Tweet();
					tweet.setText(cursor.getString(cursor.getColumnIndex("tweet_title")));
					tweet.setDateCreated(cursor.getString(cursor.getColumnIndex("tweet_created_at")));
					
					TwitterUser twitterUser = new TwitterUser();
					twitterUser.setScreenName(cursor.getString(cursor.getColumnIndex("tweet_from")));
					twitterUser.setProfileImageUrl(cursor.getString(cursor.getColumnIndex("tweet_from_picture")));
					tweet.setUser(twitterUser);
					tweets.add(tweet);
					
				}
				return tweets;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
			
		}
		
		@Override
		protected void onPostExecute(List<Tweet> result) {
			try {
				if ((getActivity()!=null) && (result!=null)) {
					super.onPostExecute(result);
					twitAdapter.setTweetList(tweets);
					twitAdapter.notifyDataSetChanged();
				}
				
				if (pd!=null) {
					if (pd.isShowing())
						pd.dismiss();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			isprocessing = false;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		Tweet tweet = (Tweet) twitAdapter.getItem(position);
		
		TwitDetailFragment twitDetailFragment = new TwitDetailFragment();
		Bundle args = new Bundle();
        args.putString("text", tweet.getText());
        args.putString("user", tweet.getUser().getScreenName());
        args.putString("date", tweet.getDateCreated());
        
        twitDetailFragment.setArguments(args);
        
		if (getActivity().findViewById(R.id.detail_container)!=null) {
			fragmentTransaction.replace(R.id.detail_container, twitDetailFragment);
			fragmentTransaction.commit();
		} else {
			fragmentTransaction.addToBackStack(null);	        
			fragmentTransaction.replace(R.id.list_container, twitDetailFragment);
			fragmentTransaction.commit();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (twitAdapter.getTweetList().size()<=0)
			loadsTweets();
	}
	
}
