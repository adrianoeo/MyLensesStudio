package com.aeo.mylensesstudio.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.aeo.mylensesstudio.R;
import com.aeo.mylensesstudio.adapter.PeriodLensesCollectionPagerAdapter;
import com.aeo.mylensesstudio.util.Utility;

public class TimeLensActivity2DEL extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG_LENS = "TAG_LENS";

    PeriodLensesCollectionPagerAdapter periodLensesCollectionPagerAdapter;
    ViewPager viewPager;

    private MenuItem menuItemEdit;
    private MenuItem menuItemSave;
    private MenuItem menuItemCancel;
    private MenuItem menuItemDelete;

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_time_lens_del);


        toolbar = (Toolbar) findViewById(R.id.toolbarTimeLenses);
        toolbar.setTitle(R.string.nav_periodo);
//        setSupportActionBar(toolbar);]
//        getSupportActionBar().setTitle(R.string.nav_periodo);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_time_lenses);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_time_lenses);
        navigationView.setNavigationItemSelectedListener(this);

        periodLensesCollectionPagerAdapter
                = new PeriodLensesCollectionPagerAdapter(getSupportFragmentManager(), getApplicationContext());

        viewPager = (ViewPager) findViewById(R.id.pagerPeriodLenses);
        viewPager.setAdapter(periodLensesCollectionPagerAdapter);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }

                });

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }
        };

        actionBar.addTab(actionBar.newTab().setText(R.string.tabLeftLens)
                .setTabListener(tabListener));

        actionBar.addTab(actionBar.newTab().setText(R.string.tabRightLens)
                .setTabListener(tabListener));
    }

//	private void replaceFragment(Fragment fragment) {
//		FragmentManager fm = getSupportFragmentManager();
//		FragmentTransaction trans = fm.beginTransaction();
//
//		trans.replace(R.id.fragment_time_lens_container, fragment, TAG_LENS);
//		trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//		trans.commit();
//	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.period_lenses, menu);
        menuItemEdit = menu.findItem(R.id.menuEditLenses);
        menuItemSave = menu.findItem(R.id.menuSaveLenses);
        menuItemCancel = menu.findItem(R.id.menuCancelLenses);
        menuItemDelete = menu.findItem(R.id.menuDeleteLenses);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveLens();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_time_lenses);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        saveLens();
    }

    @SuppressLint("SimpleDateFormat")
    private void saveLens() {
//		LensStatusVO lensVO = LensFragment.setLensVO();
//
//		LensDAO lensDAO = LensDAO.getInstance(this);
//
//		// Save lenses
//		lensDAO.save(lensVO);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        Utility.setScreen(id, toolbar, getSupportFragmentManager());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_time_lenses);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
