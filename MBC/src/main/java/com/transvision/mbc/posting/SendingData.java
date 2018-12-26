package com.transvision.mbc.posting;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.transvision.mbc.R;
import com.transvision.mbc.adapters.ApproveAdapter;
import com.transvision.mbc.adapters.MRAdapter;
import com.transvision.mbc.adapters.RoleAdapter;
import com.transvision.mbc.adapters.SpinnerAdapter;
import com.transvision.mbc.fragments.SummaryDetailsFragment;
import com.transvision.mbc.values.FunctionsCall;
import com.transvision.mbc.values.GetSetValues;


import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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

import static android.content.Context.MODE_PRIVATE;
import static com.transvision.mbc.values.Constants.BILLING_SERVICE;
import static com.transvision.mbc.values.Constants.COLLECTION_SERVICE;
import static com.transvision.mbc.values.Constants.REAL_TRM_URL;
import static com.transvision.mbc.values.Constants.REAL_TRM_URL2;
import static com.transvision.mbc.values.Constants.SERVICE;
import static com.transvision.mbc.values.Constants.SERVICE2;
import static com.transvision.mbc.values.Constants.TEST_TRM_URL;
import static com.transvision.mbc.values.Constants.TRM_TEST_URL2;
import static com.transvision.mbc.values.Constants.TRM_URL;

public class SendingData {

    ReceivingData receivingData = new ReceivingData();
    private FunctionsCall functionsCall = new FunctionsCall();
    private String BASEURL,BASE_BILLING_URL;
    private String BASE_TICKETING_LOGIN,BASE_COLLECTION_URL;
    public SendingData(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        String test_real = sharedPreferences.getString("TEST_REAL_SERVER", "");
        if (StringUtils.equalsIgnoreCase(test_real,"TEST"))
            server_link(0);
            //flag = "test";
        else
            server_link(1);
        //flag = "real";
    }

    private void server_link(int val)
    {
        if (val == 0)
        {
            BASE_COLLECTION_URL = TRM_TEST_URL2 + COLLECTION_SERVICE;
            BASE_BILLING_URL = TRM_TEST_URL2 + BILLING_SERVICE;
            BASEURL = TEST_TRM_URL + SERVICE;
            BASE_TICKETING_LOGIN = TEST_TRM_URL + SERVICE2;
        }else {
            BASE_COLLECTION_URL = REAL_TRM_URL2 + COLLECTION_SERVICE;
            BASE_BILLING_URL = TRM_URL + BILLING_SERVICE;
            BASEURL = REAL_TRM_URL + SERVICE;
            BASE_TICKETING_LOGIN = REAL_TRM_URL + SERVICE2;
        }
    }

    private String UrlPostConnection(String Post_Url, HashMap<String, String> datamap) throws IOException {
        try {
            StringBuilder response = new StringBuilder();
            URL url = new URL(Post_Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60000);
            conn.setConnectTimeout(60000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(datamap));
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            } else {
                response = new StringBuilder();
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Debug", "SERVER TIME OUT");

        }
        return null;
    }


    private String UrlPostConnection(String Post_Url) throws IOException {
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
        }

        return result.toString();
    }

    @SuppressLint("StaticFieldLeak")
    public class MR_Login extends AsyncTask<String, String, String> {
        String response="", mrcode="";
        Handler handler;
        GetSetValues getSetValues;
        public MR_Login(Handler handler, GetSetValues getSetValues) {
            this.handler = handler;
            this.getSetValues = getSetValues;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("MRCode", params[0]);
            datamap.put("DeviceId", params[1]);
            datamap.put("PASSWORD", params[2]);
            datamap.put("Date","");
            functionsCall.logStatus("MRCode: "+mrcode + "\n" + "DeviceID: "+params[1] + "\n" + "Password: "+params[2]);
            try {
                response = UrlPostConnection(BASE_COLLECTION_URL+"MRDetails", datamap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.getMR_Login_status(result, handler, getSetValues);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class Login extends AsyncTask<String, String, String> {
        String response = "";
        GetSetValues getSetValues;
        Handler handler;
        public Login(GetSetValues getSetValues, Handler handler) {
            this.getSetValues = getSetValues;
            this.handler = handler;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("username", params[0]);
            datamap.put("password", params[1]);
            try {
                response = UrlPostConnection(BASE_TICKETING_LOGIN + "loginDetails", datamap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.get_Details(result, getSetValues, handler);
        }
    }

    //Send subdivcode request
    @SuppressLint("StaticFieldLeak")
    public class SendSubdivCodeRequest extends AsyncTask<String, String, String> {
        String response = "";
        Handler handler;
        GetSetValues getSetValues;
        ArrayList<GetSetValues>arrayList;
        SpinnerAdapter roleAdapter;
        public SendSubdivCodeRequest(Handler handler, ArrayList<GetSetValues>arrayList, GetSetValues getSetValues, SpinnerAdapter roleAdapter) {
            this.handler = handler;
            this.arrayList = arrayList;
            this.getSetValues = getSetValues;
            this.roleAdapter = roleAdapter;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                //response = UrlPostConnection("http://bc_service.hescomtrm.com/Service.asmx/Subdivision_Details");
                response = UrlPostConnection(BASEURL + "Subdivision_Details");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.getSubdivdetails(result,handler,arrayList,getSetValues, roleAdapter);
        }
    }

    //Summary Details sending
    @SuppressLint("StaticFieldLeak")
    public class BillingFileSummary extends AsyncTask<String, String, String> {
        String response = "";
        Handler handler;
        GetSetValues getSetValues;

        public BillingFileSummary(Handler handler, GetSetValues getSetValues) {
            this.handler = handler;
            this.getSetValues = getSetValues;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String,String> datamap = new HashMap<>();
            datamap.put("subdivcode", params[0]);
            datamap.put("fromdate",params[1]);
            datamap.put("todate",params[2]);
            try {
                //response = urlPostConnection("http://bc_service.hescomtrm.com/Service.asmx/FilesCount",datamap);
                response = UrlPostConnection(BASEURL + "FilesCount",datamap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.getBillingSummary(result,handler,getSetValues);
        }
    }

    //MR Tracking
    public class MRTracking extends AsyncTask<String, String, String> {
        String response = "";
        Handler handler;
        GetSetValues getSetValues;
        ArrayList<GetSetValues>arrayList;
        MRAdapter mrAdapter;

        public MRTracking(Handler handler, ArrayList<GetSetValues>arrayList,GetSetValues getSetValues,MRAdapter mrAdapter)
        {
            this.handler = handler;
            this.getSetValues = getSetValues;
            this.arrayList = arrayList;
            this.mrAdapter = mrAdapter;
        }
        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("SubDivCode", params[0]);
            try {
                response = UrlPostConnection(BASEURL + "LGLTMRDETAILS", datamap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.getMrTracking_Summary(result, handler,arrayList,getSetValues,mrAdapter);
        }
    }
    //Download Approval
 /*   public class Download_Approval extends AsyncTask<String, String, String> {
        String response = "";
        Handler handler;
        GetSetValues getSetValues;
        ArrayList<GetSetValues>arrayList;
        ApproveAdapter approveAdapter;

        public Download_Approval(Handler handler, ArrayList<GetSetValues>arrayList,GetSetValues getSetValues,ApproveAdapter approveAdapter)
        {
            this.handler = handler;
            this.getSetValues = getSetValues;
            this.arrayList = arrayList;
            this.approveAdapter = approveAdapter;
        }
        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("SubDivCode", params[0]);
            try {
                response = UrlPostConnection("http://test_bc_service.hescomtrm.com/ReadFile.asmx/MR_FETCH", datamap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.getDownload_Approve_Summary(result, handler,arrayList,getSetValues,approveAdapter);
        }
    }*/

    //MR_Approval
    public class Approval_Details extends AsyncTask<String, String, String> {
        String response = "";
        Handler handler;
        GetSetValues getSetValues;
        ArrayList<GetSetValues>arrayList;
        ApproveAdapter approveAdapter;
        public Approval_Details(Handler handler,ArrayList<GetSetValues>arrayList,GetSetValues getSetValues,ApproveAdapter approveAdapter) {
            this.handler = handler;
            this.arrayList = arrayList;
            this.getSetValues = getSetValues;
            this.approveAdapter = approveAdapter;
        }
        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("Subdivision", params[0]);
            datamap.put("Flag",params[1]);
            try {
                response = UrlPostConnection(BASE_BILLING_URL + "Approval_Details", datamap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.get_Approve_Summary(result, handler,arrayList,getSetValues,approveAdapter);
        }
    }

    //MR_Approved
    public class MR_Approved extends AsyncTask<String, String, String> {
        String response = "";
        Handler handler;

        public MR_Approved(Handler handler) {
            this.handler = handler;
        }
        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("MRcode", params[0]);
            datamap.put("Flag",params[1]);
            try {
                response = UrlPostConnection(BASE_BILLING_URL + "MR_Approved", datamap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.get_Approve_Details(result,handler);
        }
    }
}
