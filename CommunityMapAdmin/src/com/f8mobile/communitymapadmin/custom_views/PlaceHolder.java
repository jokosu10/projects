package com.f8mobile.communitymapadmin.custom_views;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import com.f8mobile.communitymapadmin.asynctask.CommunityAppGooglePlacesAsyncTask;

public class PlaceHolder extends ArrayAdapter<String> implements Filterable {

	private ArrayList<String> resultList;
	private int mode;
	private ArrayList<String> filterableString;
	
	public PlaceHolder(Context context, int resource, int mode) {
		super(context, resource);
		this.mode = mode;
	}


	public PlaceHolder(Context context, int resource, int mode, ArrayList<String> toBeFiltered) {
		super(context, resource);
		this.mode = mode;
		this.filterableString = toBeFiltered;
	}	
	
	@Override

	public int getCount() {
		return resultList.size();
	}

	@Override
	public String getItem(int position) {
		return resultList.get(position);
	}
	
	@Override
	public Filter getFilter() {
		
		Filter filter = new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					// Retrieve the auto complete results.
					resultList = initiateAutoComplete(constraint.toString());
					// Assign the data to filter results
					filterResults.values = resultList;
					filterResults.count = resultList.size();
				}
				return filterResults;
		}

	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
			
		if(results != null && results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	};

	return filter;
	}
	
	public ArrayList<String> initiateAutoComplete(String input) {
		
		String[] params = new String[1];
		params[0] = input;
		
		ArrayList<String> result = null; 
		ArrayList<String> temp = new ArrayList<String>();
		try {
			
			if(mode == 1) {
				result = (ArrayList<String>) new CommunityAppGooglePlacesAsyncTask().execute(params).get();
			} else {
				for(String a : filterableString) {
					if(a.contains(input)) {
						temp.add(a);
					}
				}
				
				result = temp;
			}
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}

