package com.aeo.mylenses.activity;

import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.aeo.mylenses.R;
import com.aeo.mylenses.dao.AlarmDAO;
import com.aeo.mylenses.dao.LensesDataDAO;
import com.aeo.mylenses.dao.TimeLensesDAO;
import com.aeo.mylenses.fragment.StatusFragment;
import com.aeo.mylenses.util.AnalyticsApplication;
import com.aeo.mylenses.util.Utility;
import com.aeo.mylenses.vo.DataLensesVO;
import com.google.android.gms.analytics.Tracker;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // Object for intrinsic lock
    public static final Object sDataLock = new Object();

    public Toolbar toolbar;
    private boolean doubleBackToExitPressedOnce;

    BackupManager mBackupManager;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        if (savedInstanceState == null) {
            Utility.replaceFragment(new StatusFragment(),
                    getSupportFragmentManager());
        }

        mBackupManager = new BackupManager(this);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.msg_press_once_again_to_exit,
                Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_compra) {
            shop();
            toolbar.setTitle(R.string.nav_compra);
        } else if (id == R.id.nav_exit) {
            this.finish();
        } else {
            Utility.setScreen(id, toolbar, getSupportFragmentManager());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shop() {
        LensesDataDAO lensesDataDAO = LensesDataDAO.getInstance(this);
        DataLensesVO lensesVO = lensesDataDAO
                .getById(lensesDataDAO.getLastIdLens());

        if (lensesVO != null) {
            String urlLeft = lensesVO.getBuy_site_left();
            String urlRight = lensesVO.getBuy_site_right();

            if ((urlLeft == null || "".equals(urlLeft))
                    && (urlRight == null || "".equals(urlRight))) {
                showAlertDialog();
            } else if (urlLeft.equals(urlRight)) {
                if (!urlLeft.startsWith("http://")
                        && !urlLeft.startsWith("https://")) {
                    urlLeft = "http://" + urlLeft;
                }

                Uri uri = Uri.parse(urlLeft);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } else {
                if (urlLeft != null && !"".equals(urlLeft)) {
                    if (!urlLeft.startsWith("http://")
                            && !urlLeft.startsWith("https://")) {
                        urlLeft = "http://" + urlLeft;
                    }
                    Uri uriLeft = Uri.parse(urlLeft);
                    startActivity(new Intent(Intent.ACTION_VIEW, uriLeft));
                }
                if (urlRight != null && !"".equals(urlRight)) {
                    if (!urlRight.startsWith("http://")
                            && !urlRight.startsWith("https://")) {
                        urlRight = "http://" + urlRight;
                    }

                    Uri uriRight = Uri.parse(urlRight);
                    startActivity(new Intent(Intent.ACTION_VIEW, uriRight));
                }
            }
        } else {
            showAlertDialog();
        }
    }

    // Opening browser
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_no_buy_site);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.btn_yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse("http://www.google.com")));
                    }
                });
        builder.setNegativeButton(R.string.btn_no, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Seta Alarme para notificações
        TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(getApplicationContext());
        AlarmDAO alarmDAO = AlarmDAO.getInstance(getApplicationContext());
        alarmDAO.setAlarm(timeLensesDAO.getLastIdLens());
    }

}
