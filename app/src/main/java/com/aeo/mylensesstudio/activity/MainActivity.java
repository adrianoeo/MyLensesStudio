package com.aeo.mylensesstudio.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.aeo.mylensesstudio.R;
import com.aeo.mylensesstudio.util.Utility;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // Object for intrinsic lock
    public static final Object sDataLock = new Object();

    public Toolbar toolbar;

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

        if (savedInstanceState == null) {
            Utility.setScreen(R.id.nav_status, toolbar, getSupportFragmentManager());
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuHelp) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.msg_units);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.btn_ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Utility.setScreen(id, toolbar, getSupportFragmentManager());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
/*
    private void setScreen(int id) {
        if (id == R.id.nav_status) {
            replaceFragment(new StatusFragment());
            toolbar.setTitle(R.string.nav_status);
        } else if (id == R.id.nav_periodo) {
            replaceFragment(new ListReplaceLensFragment());
            toolbar.setTitle(R.string.nav_periodo);
        } else if (id == R.id.nav_dados) {
            toolbar.setTitle(R.string.nav_dados);
        } else if (id == R.id.nav_notificacao) {
            toolbar.setTitle(R.string.nav_notificacao);
        } else if (id == R.id.nav_historico) {
            toolbar.setTitle(R.string.nav_historico);
        } else if (id == R.id.nav_compra) {
            toolbar.setTitle(R.string.nav_compra);
        }
    }
*/
    /*
    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();

        trans.replace(R.id.fragment_container, fragment);

//		 * IMPORTANT: The following lines allow us to add the fragment to the
//		 * stack and return to it later, by pressing back
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        // remove back stack
        // getFragmentManager().popBackStack(null,
        // FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // trans.addToBackStack(null);

        trans.commit();
    }
*/

}
