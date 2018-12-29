package com.transvision.mbc;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.transvision.mbc.fragments.MR_Approval;
import com.transvision.mbc.fragments.SendSubdivCode;
import com.transvision.mbc.service.MyService;
import com.transvision.mbc.values.FunctionsCall;
import com.transvision.mbc.values.GetSetValues;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import static com.transvision.mbc.values.Constants.PREF_NAME;
import static com.transvision.mbc.values.Constants.sPref_ROLE;
import static com.transvision.mbc.values.Constants.sPref_SUBDIVISION;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    GetSetValues getSetValues;
    private Fragment fragment;
    private Toolbar toolbar;
    private ArrayList<String> main_tabs_list;
    String subdiv;
    String current_version = "";
    FunctionsCall fcall;
    SharedPreferences sPref;
    SharedPreferences.Editor editor;
    private boolean isFirstBackPressed = false;
    public enum Steps {
        FORM2(SendSubdivCode.class),
        FORM3(MR_Approval.class);
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
        sPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = sPref.edit();
        editor.apply();
        fcall = new FunctionsCall();
        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            current_version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
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
        Menu menu = logout_navigationView.getMenu();
        MenuItem nav_version = menu.findItem(R.id.nav_versioncode);
        //below line is for disableing the nav_versioncode click
        nav_version.setEnabled(false);
        nav_version.setTitle("Version: " + current_version);
        logout_navigationView.setItemTextColor(ColorStateList.valueOf(Color.parseColor("#039BE5")));
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
    public ArrayList<String> gettabs_list() {
        return this.main_tabs_list;
    }
    public SharedPreferences getsharedPref() {
        return this.sPref;
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0){
            super.onBackPressed();
        }else{
            if (isFirstBackPressed) {
                super.onBackPressed();
            } else {
                isFirstBackPressed = true;
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isFirstBackPressed = false;
                    }
                }, 2000);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_main:
                switchContent(Steps.FORM2, getResources().getString(R.string.login));
                break;
            case R.id.nav_logout:
                Intent intent = new Intent(MainActivity.this, ActivityLogin2.class);
                startActivity(intent);
                editor.putString(sPref_ROLE,"");
                editor.putString(sPref_SUBDIVISION,"");
                editor.clear();
                editor.commit();
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    /*    if (StringUtils.startsWithIgnoreCase(sPref.getString(sPref_ROLE, ""), "AAO")) {
            if (!isMyServiceRunning(MyService.class)) {
                fcall.logStatus("MyService not running");
                Intent service = new Intent(MainActivity.this, MyService.class);
                startService(service);
            } else fcall.logStatus("MyService Running in background");
        }*/
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
