package com.smsgt.mobile.application.supertsuper_v3.custom_ui;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;


public class TrafficProfilePagerAdapter extends FragmentStatePagerAdapter {

	private List<Fragment> fragments;

	public TrafficProfilePagerAdapter(android.support.v4.app.FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}
	
	@Override
	public Fragment getItem(int pos) {
		return this.fragments.get(pos);
	}

	public int getCount() {
		return this.fragments.size();
	}
}