package com.transvision.mbc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.transvision.mbc.fragments.MainFragment;
import com.transvision.mbc.fragments.SendSubdivCode;
import com.transvision.mbc.fragments.billing.Billing_Contents;
import com.transvision.mbc.values.GetSetValues;

import java.util.ArrayList;
/**
 * Created by Sourav
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    GetSetValues getSetValues;
    private Fragment fragment;
    private Toolbar toolbar;
    private ArrayList<String> main_tabs_list;
    String subdiv;

    public enum Steps {
        FORM0(MainFragment.class),
        FORM1(Billing_Contents.class),
        FORM2(SendSubdivCode.class);

        private Class clazz;

        Steps(Class clazz) {
            this.clazz = clazz;
        }

        public Class getFragClass() {
            return clazz;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        //getSetValues = (GetSetValues) intent.getSerializableExtra(GETSET);
        getSetValues = new GetSetValues();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        main_tabs_list = new ArrayList<>();
        for (int i = 0; i < getResources().getStringArray(R.array.tabs_list).length; i++) {
            main_tabs_list.add(getResources().getStringArray(R.array.tabs_list)[i]);
        }

        subdiv = intent.getExtras().getString("subdivcode");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView logout_navigationView = (NavigationView) findViewById(R.id.nav_drawer_bottom);
        logout_navigationView.setNavigationItemSelectedListener(this);

        switchContent(Steps.FORM2, getResources().getString(R.string.app_name));

    }

    public void switchContent(Steps currentForm, String title) {
        try {
            fragment = (Fragment) currentForm.getFragClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("subdivcode", subdiv);
        fragment.setArguments(bundle);
        toolbar.setTitle(title);
        ft.replace(R.id.container_main, fragment, currentForm.name());
        ft.commit();
    }

    public Fragment getFragment(Steps currentForm) {
        try {
            fragment = (Fragment) currentForm.getFragClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragment;
    }

    public GetSetValues getSetValues() {
        return this.getSetValues;
    }

    public void switchAddonContent(Steps currentForm, String title) {
        try {
            fragment = (Fragment) currentForm.getFragClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        toolbar.setTitle(title);
        ft.replace(R.id.container_main, fragment, currentForm.name());
        ft.addToBackStack(null);
        ft.commit();
    }

    public ArrayList<String> gettabs_list() {
        return this.main_tabs_list;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_main:
               /* Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(GETSET, getSetValues);
                startActivity(intent);*/
                switchContent(Steps.FORM2, getResources().getString(R.string.login));
                break;
            /*case R.id.nav_login:
                //switchContent(Steps.FORM1, getResources().getString(R.string.login));
                break;
            case R.id.nav_demo1:
                //switchContent(Steps.FORM3, getResources().getString(R.string.demo1));
                //Toast.makeText(this, "To be implemented later..", Toast.LENGTH_SHORT).show();
                //switchContent(Steps.FORM2, "Demo1");
                break;*/
            case R.id.nav_logout:
                Intent intent = new Intent(MainActivity.this, ActivityLogin2.class);
                startActivity(intent);
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
}
