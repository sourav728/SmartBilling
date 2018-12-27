package com.transvision.mbc;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.transvision.mbc.adapters.RoleAdapter;
import com.transvision.mbc.ftp.FTPAPI;
import com.transvision.mbc.other.Apk_Notification;
import com.transvision.mbc.posting.SendingData;
import com.transvision.mbc.values.FunctionsCall;
import com.transvision.mbc.values.GetSetValues;
import org.apache.commons.lang.StringUtils;
import java.io.File;
import java.util.ArrayList;
import static com.transvision.mbc.values.Constants.APK_FILE_DOWNLOADED;
import static com.transvision.mbc.values.Constants.APK_FILE_NOT_FOUND;
import static com.transvision.mbc.values.Constants.DLG_APK_NOT_FOUND;
import static com.transvision.mbc.values.Constants.DLG_APK_UPDATE_FAILURE;
import static com.transvision.mbc.values.Constants.DLG_APK_UPDATE_SUCCESS;
import static com.transvision.mbc.values.Constants.DLG_LOGIN;
import static com.transvision.mbc.values.Constants.LOGIN_FAILURE;
import static com.transvision.mbc.values.Constants.LOGIN_SUCCESS;
import static com.transvision.mbc.values.Constants.PREF_NAME;
import static com.transvision.mbc.values.Constants.sPref_ROLE;
import static com.transvision.mbc.values.Constants.sPref_SUBDIVISION;

public class ActivityLogin2 extends AppCompatActivity {

    String code, password;
    GetSetValues getSetValues;
    FunctionsCall fcall;
    Button login_btn;
    SharedPreferences sPref;
    SharedPreferences.Editor editor;
    String group = "", current_version = "", DeviceID = "", login_role = "", device_id="";
    TextView version_code;
    ProgressDialog progressdialog;
    SendingData sendingData;
    AlertDialog login_dialog;
    CheckBox test_server;
    FTPAPI ftpapi;
    ProgressDialog mProgressDialog = null;
    ArrayList<GetSetValues> roles_list;
    RoleAdapter roleAdapter;
    Spinner role_spinner;

    private Handler handler = null;

    {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LOGIN_SUCCESS:
                        SavePreferences("Username", code);
                        SavePreferences("Password", password);
                        progressdialog.dismiss();
                        login_dialog.dismiss();
                        //Below code is for custom toast message
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast,
                                (ViewGroup) findViewById(R.id.toast_layout));
                        ImageView imageView = (ImageView) layout.findViewById(R.id.image);
                        imageView.setImageResource(R.drawable.tick);
                        TextView textView = (TextView) layout.findViewById(R.id.text);
                        textView.setText("Success");
                        textView.setTextSize(20);
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                        editor.putString(sPref_ROLE, login_role);
                        editor.putString(sPref_SUBDIVISION, getSetValues.getSubdivision());
                        editor.commit();
                        start_version_check();
                        if (fcall.compare(current_version, getSetValues.getMbc_version()))
                            showdialog(DLG_APK_UPDATE_SUCCESS);
                        else showdialog(DLG_APK_UPDATE_FAILURE);
                        //finish();
                        break;
                    case LOGIN_FAILURE:
                        login_dialog.dismiss();
                        Toast.makeText(ActivityLogin2.this, "Invalid Credentials!!", Toast.LENGTH_SHORT).show();
                        progressdialog.dismiss();
                        break;

                    case APK_FILE_DOWNLOADED:
                        mProgressDialog.dismiss();
                        fcall.updateApp(ActivityLogin2.this, new File(fcall.filepath("ApkFolder") +
                                File.separator + "MBC_" + getSetValues.getMbc_version() + ".apk"));
                        break;
                    case APK_FILE_NOT_FOUND:
                        mProgressDialog.dismiss();
                        showDialog(DLG_APK_NOT_FOUND);
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
        initialize();

        for (int i = 0; i < getResources().getStringArray(R.array.login_role3).length; i++) {
            getSetValues = new GetSetValues();
            getSetValues.setLogin_role(getResources().getStringArray(R.array.login_role3)[i]);
            roles_list.add(getSetValues);
            roleAdapter.notifyDataSetChanged();
        }

        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            current_version = packageInfo.versionName;
            version_code.setText(current_version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        SavePreferences("TEST_REAL_SERVER", "REAL");
        sendingData = new SendingData(ActivityLogin2.this);

        test_server.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (test_server.isChecked()) {
                    SavePreferences("TEST_REAL_SERVER", "TEST");
                    sendingData = new SendingData(ActivityLogin2.this);
                } else {
                    SavePreferences("TEST_REAL_SERVER", "REAL");
                    sendingData = new SendingData(ActivityLogin2.this);
                }
            }
        });

        role_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                GetSetValues roledetails = roles_list.get(position);
                login_role = roledetails.getLogin_role();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(ActivityLogin2.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (tm != null)
                 device_id = tm.getDeviceId();
               // device_id = "357869083548989";
               // device_id = "352514086619271";
                if (fcall.isInternetOn(ActivityLogin2.this)) {
                    if (!StringUtils.startsWithIgnoreCase(login_role, "--SELECT--"))
                        showdialog(DLG_LOGIN);
                    else
                        Toast.makeText(ActivityLogin2.this, "Please Select Login Role!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ActivityLogin2.this, "Please connect to internet..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showdialog(int id) {
        Dialog dialog;
        LinearLayout linearLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.subdiv_login_screen, null);
        final EditText et_login_id = linearLayout.findViewById(R.id.et_admin_id);
        final EditText et_pass = linearLayout.findViewById(R.id.et_admin_password);
        switch (id) {

            case DLG_LOGIN:
                AlertDialog.Builder login_dlg = new AlertDialog.Builder(this);
                login_dlg.setTitle(getResources().getString(R.string.dialog_login));
                login_dlg.setCancelable(false);
                RelativeLayout dlg_linear = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.login_layout, null);
                login_dlg.setView(dlg_linear);
                final EditText et_loginid = dlg_linear.findViewById(R.id.et_login_id);
                final EditText et_password = dlg_linear.findViewById(R.id.et_login_password);
                final Button login_btn = dlg_linear.findViewById(R.id.dialog_positive_btn);
                final Button cancel_btn = dlg_linear.findViewById(R.id.dialog_negative_btn);

                login_dialog = login_dlg.create();
                login_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                        login_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                code = et_loginid.getText().toString();
                                if (fcall.isInternetOn(ActivityLogin2.this)) {
                                    if (!TextUtils.isEmpty(code)) {
                                        password = et_password.getText().toString();
                                        if (!TextUtils.isEmpty(password)) {
                                            progressdialog = new ProgressDialog(ActivityLogin2.this, R.style.MyProgressDialogstyle);
                                            progressdialog.setTitle("Checking Credentials");
                                            progressdialog.setMessage("Please Wait..");
                                            progressdialog.show();

                                            if (!StringUtils.startsWithIgnoreCase(login_role, "AAO")) {
                                                SendingData.Login login = sendingData.new Login(getSetValues, handler);
                                                login.execute(code, password);
                                            } else {
                                                SendingData.MR_Login login = sendingData.new MR_Login(handler,getSetValues);
                                                login.execute(code,device_id, password);
                                            }
                                        } else
                                            et_password.setError(getResources().getString(R.string.dialog_login_password_error));
                                    } else
                                        et_loginid.setError(getResources().getString(R.string.dialog_login_id_error));
                                } else
                                    Toast.makeText(ActivityLogin2.this, "Please Connect to Internet!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        cancel_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                login_dialog.dismiss();
                            }
                        });
                    }
                });
                login_dialog.show();
                (login_dialog).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.MAGENTA);
                (login_dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                break;

            case DLG_APK_UPDATE_SUCCESS:
                android.app.AlertDialog.Builder appupdate = new android.app.AlertDialog.Builder(this);
                appupdate.setTitle("App Updates");
                appupdate.setCancelable(false);
                appupdate.setMessage("Your current version number : " + current_version +
                        "\n" + "\n" +
                        "New version is available : " + getSetValues.getMbc_version() + "\n");
                appupdate.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mProgressDialog = new ProgressDialog(ActivityLogin2.this);
                        mProgressDialog.setMessage("Downloading file..");
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();
                        FTPAPI.Download_apk downloadApk = ftpapi.new Download_apk(handler, mProgressDialog, getSetValues.getMbc_version());
                        downloadApk.execute();
                    }
                });
                appupdate.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        move_to_next_activity();
                    }
                });
                dialog = appupdate.create();
                dialog.show();
                break;
            case DLG_APK_UPDATE_FAILURE:
                move_to_next_activity();
                break;
            case DLG_APK_NOT_FOUND:
                android.app.AlertDialog.Builder apknotfound = new android.app.AlertDialog.Builder(this);
                apknotfound.setTitle("App Update");
                apknotfound.setCancelable(false);
                apknotfound.setMessage("Apk not found to download from server..");
                apknotfound.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog = apknotfound.create();
                dialog.show();
                break;
        }
    }

    private void move_to_next_activity() {
        Intent intent = new Intent(ActivityLogin2.this, MainActivity.class);
        intent.putExtra("subdivcode", code);
        startActivity(intent);
        finish();
    }

    private void initialize() {
        ftpapi = new FTPAPI();
        login_btn = findViewById(R.id.login_btn);
        fcall = new FunctionsCall();
        version_code = findViewById(R.id.txt_version_code);
        sendingData = new SendingData(this);
        test_server = findViewById(R.id.checkbox);
        getSetValues = new GetSetValues();
        sPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        role_spinner = findViewById(R.id.login_users_spin);
        roles_list = new ArrayList<>();
        roleAdapter = new RoleAdapter(roles_list, this);
        role_spinner.setAdapter(roleAdapter);
        editor = sPref.edit();
        editor.apply();
    }

    private void SavePreferences(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void start_version_check() {
        fcall.logStatus("Version_receiver Checking..");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), Apk_Notification.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE) != null);
        if (!alarmRunning) {
            fcall.logStatus("Version_receiver Started..");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (10000), pendingIntent);
        } else fcall.logStatus("Version_receiver Already running..");
    }

}
