package com.transvision.mbc;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.transvision.mbc.adapters.RoleAdapter;
import com.transvision.mbc.values.FunctionsCall;
import com.transvision.mbc.values.GetSetValues;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Sourav
 */
public class ActivityLogin2 extends AppCompatActivity {
    private static final int DLG_LOGIN = 4;
    private static final int SUB_DIV_LOGIN_SUCCESS = 1;
    private static final int SUB_DIV_LOGIN_FAILURE = 2;
    private static final int SERVER_TIME_OUT = 3;
    private static final int LOGIN_SUCCESS = 4;
    private static final int LOGIN_FAILURE = 5;
    Spinner role_spinner;
    ProgressDialog progressDialog;
    String code, password;
    ArrayList<GetSetValues> roles_list;
    RoleAdapter roleAdapter;
    GetSetValues getSetValues;
    FunctionsCall fcall;
    Button login_btn;
    String main_role = "";
    String requestUrl = "";
    String group = "", current_version = "";
    TextView version_code;
    static ProgressDialog progressdialog;
    private Handler handler = null;

    {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LOGIN_SUCCESS:
                        progressDialog.dismiss();
                        Intent intent = new Intent(ActivityLogin2.this, MainActivity.class);
                        intent.putExtra("subdivcode", code);
                        startActivity(intent);
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
                        //end of custom toast coding
                        finish();
                        break;
                    case LOGIN_FAILURE:
                        //Toast.makeText(ActivityLogin2.this, "Login Failure!!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initialize();

        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            current_version = packageInfo.versionName;
            //version_code.setText(current_version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < getResources().getStringArray(R.array.login_role2).length; i++) {
            getSetValues = new GetSetValues();
            getSetValues.setLogin_role(getResources().getStringArray(R.array.login_role2)[i]);
            roles_list.add(getSetValues);
            roleAdapter.notifyDataSetChanged();

        }
        role_spinner.setSelection(0);


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fcall.isInternetOn(ActivityLogin2.this)) {
                    TextView tvrole = (TextView) findViewById(R.id.spinner_txt);
                    String role = tvrole.getText().toString();
                    if (!role.equals("--SELECT--")) {
                        main_role = role;
                    }
                    if (role.equals("AEE")) {
                        group = "AAO";
                        showdialog(DLG_LOGIN);
                    }
                    if (role.equals("AAO")) {
                        group = "AAO";
                        showdialog(DLG_LOGIN);
                    } else {
                        // Toast.makeText(ActivityLogin2.this, "Please select user role!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityLogin2.this, "Please connect to internet..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showdialog(int id) {
        LinearLayout linearLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.subdiv_login_screen, null);
        final EditText et_login_id = (EditText) linearLayout.findViewById(R.id.et_admin_id);
        final EditText et_pass = (EditText) linearLayout.findViewById(R.id.et_admin_password);
        switch (id) {
            case SUB_DIV_LOGIN_SUCCESS:
                progressDialog = ProgressDialog.show(this, "Fetching details..", "Wait..", true);
                break;
            case SUB_DIV_LOGIN_FAILURE:
                progressDialog = progressDialog.show(this, "Fetching details..", "wait", true);
                break;
            case SERVER_TIME_OUT:
                Toast.makeText(this, "Server Time out..", Toast.LENGTH_SHORT).show();
                break;
            case DLG_LOGIN:
                AlertDialog.Builder login_dlg = new AlertDialog.Builder(this);
                login_dlg.setTitle(getResources().getString(R.string.dialog_login));
                login_dlg.setCancelable(false);
                LinearLayout dlg_linear = (LinearLayout) this.getLayoutInflater().inflate(R.layout.login_layout, null);
                login_dlg.setView(dlg_linear);
                final EditText et_loginid = (EditText) dlg_linear.findViewById(R.id.et_login_id);
                final EditText et_password = (EditText) dlg_linear.findViewById(R.id.et_login_password);
                login_dlg.setPositiveButton(getResources().getString(R.string.dialog_login), null);
                login_dlg.setNegativeButton(getResources().getString(android.R.string.cancel), null);
                final AlertDialog login_dialog = login_dlg.create();
                login_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button positive = login_dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button negative = login_dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        positive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                code = et_loginid.getText().toString();
                                if (!TextUtils.isEmpty(code)) {
                                    password = et_password.getText().toString();
                                    if (!TextUtils.isEmpty(password)) {
                                        progressDialog = ProgressDialog.show(ActivityLogin2.this, "Login", "Login Please Wait..");
                                        login_dialog.dismiss();
                                        ConnectURL connectURL = new ConnectURL();
                                        connectURL.execute(code, password, group);
                                    } else
                                        et_password.setError(getResources().getString(R.string.dialog_login_password_error));
                                } else
                                    et_loginid.setError(getResources().getString(R.string.dialog_login_id_error));
                            }
                        });
                        negative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                login_dialog.dismiss();
                            }
                        });
                    }
                });
                login_dialog.show();
                ((AlertDialog) login_dialog).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.MAGENTA);
                ((AlertDialog) login_dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                break;
        }
    }

    public class ConnectURL extends AsyncTask<String, String, String> {
        HashMap<String, String> datamap = new HashMap<>();

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();

            datamap.put("username", params[0]);
            datamap.put("userpassword", params[1]);
            datamap.put("Group", params[2]);
            try {
                requestUrl = UrlPostConnection("http://bc_service.hescomtrm.com/Service.asmx/AndroidUser", datamap);
            } catch (Exception e) {
                e.printStackTrace();
                /**************The below code is for checking server time out time*************/
               // showdialog(SERVER_TIME_OUT);
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            return requestUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            String res = parseServerXML(s);
            Log.d("debug", "Result is" + res);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(res);
                String message = jsonObject.getString("message");
                if (StringUtils.startsWithIgnoreCase(message, "Success")) {
                    handler.sendEmptyMessage(LOGIN_SUCCESS);
                } else {
                    //below code is for custom toast
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast,
                            (ViewGroup) findViewById(R.id.toast_layout));
                    ImageView imageView = (ImageView) layout.findViewById(R.id.image);
                    imageView.setImageResource(R.drawable.invalid);
                    TextView textView = (TextView) layout.findViewById(R.id.text);
                    textView.setText("Invalid Credentials!!");
                    textView.setTextSize(20);
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    //end of custom toast code
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    showdialog(DLG_LOGIN);

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ActivityLogin2.this, "No data found..", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    private String UrlPostConnection(String Post_Url, HashMap<String, String> datamap) throws IOException {
        String response = "";
        URL url = new URL(Post_Url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        OutputStream outputStream = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        writer.write(getPostDataString(datamap));
        writer.flush();
        writer.close();
        outputStream.close();
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                response += line;
            }
        } else {
            response = "";
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            Log.d("debug", result.toString());
        }
        return result.toString();
    }

    public String parseServerXML(String result) {
        String value = "";
        XmlPullParserFactory pullParserFactory;
        InputStream res;
        try {
            res = new ByteArrayInputStream(result.getBytes());
            pullParserFactory = XmlPullParserFactory.newInstance();
            pullParserFactory.setNamespaceAware(true);
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(res, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        switch (name) {
                            case "string":
                                value = parser.nextText();
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    private void initialize() {
        role_spinner = (Spinner) findViewById(R.id.login_users_spin);
        roles_list = new ArrayList<>();
        roleAdapter = new RoleAdapter(roles_list, this);
        role_spinner.setAdapter(roleAdapter);
        login_btn = (Button) findViewById(R.id.login_btn);
        fcall = new FunctionsCall();
        //version_code = (TextView) findViewById(R.id.txt_version_code);
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        super.onDestroy();
    }
}
