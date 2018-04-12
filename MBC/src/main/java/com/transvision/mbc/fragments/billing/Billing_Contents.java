package com.transvision.mbc.fragments.billing;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.transvision.mbc.Location;
import com.transvision.mbc.R;
import com.transvision.mbc.fragments.SendSubdivCode;

import org.apache.commons.lang.StringUtils;
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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
/**
 * Created by Sourav
 */
public class Billing_Contents extends Fragment {
    private static final int SUB_DIV_LOGIN_SUCCESS = 1;
    private static final int SUB_DIV_LOGIN_FAILURE = 2;
    private static final int SUB_DIV_LOGIN_DIALOG = 3;
    private static final int DLG_LOGIN = 4;

    View view;
    Button subdivlogin, gps;
    EditText subdivcode, subdivpass;
    FragmentTransaction fragmentTransaction;
    String code, password;
    String requestUrl = "";
    ProgressDialog progressDialog;

    public Billing_Contents() {

    }

    private final Handler mhandler;

    {
        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SUB_DIV_LOGIN_SUCCESS:
                        progressDialog.dismiss();
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_billing_contents, container, false);
        //Code for Disabling back button for a particular fragment
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        return true;
                    }
                }
                return false;
            }
        });
        //End of back button button disabling code

        subdivlogin = (Button) view.findViewById(R.id.subdiv_login_btn);
        gps = (Button) view.findViewById(R.id.gps_btn);
        subdivcode = (EditText) view.findViewById(R.id.edit_subdiv_code);
        subdivpass = (EditText) view.findViewById(R.id.edit_password);

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Location.class);
                getActivity().startActivity(intent);
            }
        });

       /* subdivlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkvalidation();
            }
        });*/

        subdivlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog(DLG_LOGIN);
            }
        });
        return view;
    }

    public void showdialog(int id) {
        LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.subdiv_login_screen, null);
        final EditText et_login_id = (EditText) linearLayout.findViewById(R.id.et_admin_id);
        final EditText et_pass = (EditText) linearLayout.findViewById(R.id.et_admin_password);
        switch (id) {
            case SUB_DIV_LOGIN_SUCCESS:
                progressDialog = ProgressDialog.show(getActivity(), "Fetching details..", "Wait..", true);
                break;
            case SUB_DIV_LOGIN_FAILURE:
                progressDialog = progressDialog.show(getActivity(), "Fetching details..", "wait", true);
                break;

            case DLG_LOGIN:
                AlertDialog.Builder login_dlg = new AlertDialog.Builder(getActivity());
                login_dlg.setTitle(getResources().getString(R.string.dialog_login));
                login_dlg.setCancelable(false);
                LinearLayout dlg_linear = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.login_layout, null);
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
                                        login_dialog.dismiss();

                                        ConnectURL connectURL = new ConnectURL();
                                        connectURL.execute(code, password);
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
    public void checkvalidation() {
        code = subdivcode.getText().toString();
        password = subdivpass.getText().toString();

        if (code.equals("") || password.equals("")) {
            Toast.makeText(getActivity(), "Please Enter all credentials!!!", Toast.LENGTH_SHORT).show();
        } else {
            code = subdivcode.getText().toString();
            password = subdivpass.getText().toString();
            ConnectURL connectURL = new ConnectURL();
            connectURL.execute();
        }
    }

    public class ConnectURL extends AsyncTask<String, String, String> {
        HashMap<String, String> datamap = new HashMap<>();

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();

            datamap.put("username", params[0]);
            datamap.put("userpassword", params[1]);

            /*datamap.put("username", code);
            datamap.put("userpassword", password);*/
            try {
                requestUrl = UrlPostConnection("http://www.bc_service.hescomtrm.com/Service.asmx/CheckValidUser", datamap);

            } catch (IOException e) {
                e.printStackTrace();
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
                    showdialog(SUB_DIV_LOGIN_SUCCESS);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 3000);

                    Toast.makeText(getActivity(), "Success..", Toast.LENGTH_SHORT).show();
                    SendSubdivCode sendsubdivcode = new SendSubdivCode();
                    Bundle bundle = new Bundle();
                    bundle.putString("subdivcode", code);
                    sendsubdivcode.setArguments(bundle);
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container_main, sendsubdivcode).commit();

                } else {
                    showdialog(SUB_DIV_LOGIN_FAILURE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 3000);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showdialog(DLG_LOGIN);
                        }
                    }, 3000);

                    Toast.makeText(getActivity(), "Invalid Credentials!!!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "No data found in this date..", Toast.LENGTH_SHORT).show();
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


}
