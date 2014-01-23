package com.example.trainingtwitter.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trainingtwitter.R;
import com.example.trainingtwitter.fragments.Tweet;
import com.squareup.picasso.Picasso;

public class TwitListAdapter extends BaseAdapter {
	private Context context;
	private List<Tweet> tweets;
	
	public TwitListAdapter(Context context, List<Tweet> tweets) {
		this.context = context;
		this.tweets = tweets;
	}
	@Override
	public int getCount() {
		return tweets.size();
	}

	@Override
	public Tweet getItem(int position) {
		return tweets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return tweets.indexOf(getItem(position));
	}

	public void setTweetList(List<Tweet> tweets) {
		this.tweets = tweets;
	}
	
	public List<Tweet> getTweetList() {
		return this.tweets;
	}
	
	
	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		ViewHolder holder = null;
		
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		if (view==null) {
			view = mInflater.inflate(R.layout.row, null);
			holder = new ViewHolder();
			holder.txtTitle = (TextView) view.findViewById(R.id.textTitle);
			holder.txtDesc = (TextView) view.findViewById(R.id.textDescription);
			holder.imageView = (ImageView) view.findViewById(R.id.imageProfile);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		Tweet tweet = (Tweet) getItem(position);
		if (tweet.getText()!=null)
			holder.txtDesc.setText(tweet.getText());
		if (tweet.getDateCreated()!=null)
			holder.txtTitle.setText(tweet.getDateCreated());
		
		Picasso.with(context).load(tweet.getUser().getProfileImageUrl()).into(holder.imageView);
		
		return view;
	}
	
	private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
    }

}
