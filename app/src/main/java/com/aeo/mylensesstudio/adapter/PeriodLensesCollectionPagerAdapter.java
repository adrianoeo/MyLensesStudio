package com.aeo.mylensesstudio.adapter;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.aeo.mylensesstudio.fragment.LeftPeriodFragment;
import com.aeo.mylensesstudio.fragment.RightPeriodFragment;

import java.util.HashMap;
import java.util.Map;

public class PeriodLensesCollectionPagerAdapter extends FragmentPagerAdapter {

	private static final int NUMBER_FRAGMENTS = 2;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Fragment> mPageReferenceMap = new HashMap<Integer, Fragment>();

	public PeriodLensesCollectionPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		if (position == 0) {
			fragment = new LeftPeriodFragment();
			mPageReferenceMap.put(position, fragment);
		} else {
			fragment = new RightPeriodFragment();
			mPageReferenceMap.put(position, fragment);
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return NUMBER_FRAGMENTS;
	}

	public Fragment getFragment(int key) {
		return mPageReferenceMap.get(key);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		mPageReferenceMap.remove(position);
	}

}
