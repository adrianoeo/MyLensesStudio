package com.aeo.mylenses.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aeo.mylenses.R;
import com.aeo.mylenses.adapter.ListReplaceLensBaseAdapter;
import com.aeo.mylenses.dao.TimeLensesDAO;
import com.aeo.mylenses.slidetab.SlidingTabLayout;
import com.aeo.mylenses.util.AnalyticsApplication;
import com.aeo.mylenses.util.Utility;
import com.aeo.mylenses.vo.TimeLensesVO;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

public class ListReplaceLensFragment extends ListFragment {

    public static List<TimeLensesVO> listLenses;
    ListReplaceLensBaseAdapter mListAdapter;
    private Tracker mTracker;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<TimeLensesVO> listLens = TimeLensesDAO.getInstance(getContext()).getListLens();

        if (listLens != null && listLens.size() == 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    inflater.getContext(),
                    android.R.layout.simple_list_item_1,
                    new String[]{getString(R.string.msg_insert_time_replace)});
            setListAdapter(adapter);
        }

        //Retira Tab referente ao Fragment do Periodo das lentes
        View viewMain = getActivity().findViewById(R.id.drawer_layout);
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) viewMain.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(null);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_action_new);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.replaceFragmentWithBackStack(new TimeLensesFragment(), getFragmentManager());
            }
        });

        Toolbar toolbar = (Toolbar) viewMain.findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        TextView idLens = (TextView) v.findViewById(R.id.textViewIdReplaceLens);

        if (idLens != null) {
            TimeLensesFragment fragment
                    = TimeLensesFragment.newInstance(Integer.parseInt(idLens.getText().toString()));
            Utility.replaceFragmentWithBackStack(fragment, getFragmentManager());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        List<TimeLensesVO> listLens = TimeLensesDAO.getInstance(getContext()).getListLens();

        if (listLens != null && listLens.size() > 0) {
            mListAdapter = new ListReplaceLensBaseAdapter(getContext(), listLens, getFragmentManager());
            setListAdapter(mListAdapter);
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    inflater.getContext(),
                    android.R.layout.simple_list_item_1,
                    new String[]{getString(R.string.msg_insert_time_replace)});
            setListAdapter(adapter);
        }

        CoordinatorLayout.LayoutParams params
                = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        int margin = (int) getResources().getDimension(R.dimen.fab_margin);
        params.setMargins(margin, margin, margin, margin);
        fab.setLayoutParams(params);

        fab.show();

        mTracker.setScreenName("ListReplaceLensFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
