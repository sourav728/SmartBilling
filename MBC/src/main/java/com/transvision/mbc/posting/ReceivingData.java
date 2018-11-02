package com.transvision.mbc.posting;

import android.util.Log;

import com.transvision.mbc.adapters.MRAdapter;
import com.transvision.mbc.adapters.SpinnerAdapter;
import com.transvision.mbc.values.FunctionsCall;
import com.transvision.mbc.values.GetSetValues;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import static com.transvision.mbc.values.Constants.BILLING_FILE_SUMMARY_FAILURE;
import static com.transvision.mbc.values.Constants.BILLING_FILE_SUMMARY_SUCCESS;
import static com.transvision.mbc.values.Constants.LOGIN_FAILURE;
import static com.transvision.mbc.values.Constants.LOGIN_SUCCESS;
import static com.transvision.mbc.values.Constants.MRTRACKING_FAILURE;
import static com.transvision.mbc.values.Constants.MRTRACKING_SUCCESS;
import static com.transvision.mbc.values.Constants.SUBDIV_DETAILS_FAILURE;
import static com.transvision.mbc.values.Constants.SUBDIV_DETAILS_SUCCESS;

public class ReceivingData {
    private FunctionsCall functionsCall = new FunctionsCall();

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

    //For getting result based on MR LOGIN
   /* public void getMR_Details(String result, android.os.Handler handler, GetSetValues getSetValues) {
        String res = parseServerXML(result);
        Log.d("debug", "Result is" + res);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(res);
            String message = jsonObject.getString("message");
            if (StringUtils.startsWithIgnoreCase(message, "Success"))
                handler.sendEmptyMessage(LOGIN_SUCCESS);
            else handler.sendEmptyMessage(LOGIN_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            functionsCall.logStatus("JSON Exception Failure!!");
            handler.sendEmptyMessage(LOGIN_FAILURE);
        }
    }*/

    public void get_Details(String result, GetSetValues getSetValues, android.os.Handler handler) {
        result = parseServerXML(result);
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String message = jsonObject.getString("message");
            if (StringUtils.startsWithIgnoreCase(message, "Success")) {
                getSetValues.setMbc_version(jsonObject.getString("MBC_VERSION"));
                handler.sendEmptyMessage(LOGIN_SUCCESS);
            } else handler.sendEmptyMessage(LOGIN_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(LOGIN_FAILURE);
        }
    }

    //For getting Subdivision details
    public void getSubdivdetails(String result, android.os.Handler handler, ArrayList<GetSetValues> arrayList, GetSetValues getSetValues, SpinnerAdapter roleAdapter) {
        result = parseServerXML(result);
        functionsCall.logStatus("SubDiv_Details" + result);
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    getSetValues = new GetSetValues();
                    String subdivisionname = jsonObject.getString("subdivisionname");
                    getSetValues.setLogin_role(subdivisionname);
                    arrayList.add(getSetValues);
                    roleAdapter.notifyDataSetChanged();
                }
                handler.sendEmptyMessage(SUBDIV_DETAILS_SUCCESS);
            } else handler.sendEmptyMessage(SUBDIV_DETAILS_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(SUBDIV_DETAILS_FAILURE);
            functionsCall.logStatus("JSON Exception Failure!!");
        }
    }

    //Billing File Summary
    public void getBillingSummary(String result, android.os.Handler handler, GetSetValues getSetValues) {
        result = parseServerXML(result);
        functionsCall.logStatus("SubDiv_Details" + result);
        try {
            JSONArray jsonarry = new JSONArray(result);
            JSONObject jsonobject = jsonarry.getJSONObject(0);
            String DWNRECORD = jsonobject.getString("DWNRECORD");
            String UPLOADRECORD = jsonobject.getString("UPLOADRECORD");
            String INFOSYSRECORD = jsonobject.getString("INFOSYSRECORD");

            getSetValues.setDownload_record(DWNRECORD);
            getSetValues.setUpload_record(UPLOADRECORD);
            getSetValues.setInfosys_record(INFOSYSRECORD);
            handler.sendEmptyMessage(BILLING_FILE_SUMMARY_SUCCESS);

        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(BILLING_FILE_SUMMARY_FAILURE);
            functionsCall.logStatus("JSON Exception Failure!!");
        }
    }

    //MR Tracking Summary
    public void getMrTracking_Summary(String result, android.os.Handler handler, ArrayList<GetSetValues> arrayList, GetSetValues getSetValues, MRAdapter mrAdapter) {
         result = parseServerXML(result);
        JSONArray jsonarray;
        try {
            jsonarray = new JSONArray(result);
            if (jsonarray.length() > 0) {
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonObject = jsonarray.getJSONObject(i);
                    getSetValues = new GetSetValues();
                    String MRCODE = jsonObject.getString("MRCODE");
                    String MRNAME = jsonObject.getString("MRNAME");
                    String MOBILE_NO = jsonObject.getString("MOBILE_NO");
                    String DEVICE_ID = jsonObject.getString("DEVICE_ID");
                    String LONGITUDE = jsonObject.getString("LONGITUDE");
                    String LATITUDE = jsonObject.getString("LATITUDE");

                    Log.d("Debugg", "Mrcode" + MRCODE);
                    Log.d("Debugg", "Mrname" + MRNAME);
                    Log.d("Debugg", "Phone" + MOBILE_NO);
                    Log.d("Debugg", "IEMI" + DEVICE_ID);
                    Log.d("Debugg", "LONGITUDE" + LONGITUDE);
                    Log.d("Debugg", "LATITUDE" + LATITUDE);

                    if (!MRCODE.equals(""))
                        getSetValues.setMrcode(MRCODE);
                    else getSetValues.setMrcode("NA");
                    if (!MRNAME.equals(""))
                        getSetValues.setMrname(MRNAME);
                    else getSetValues.setMrname("NA");
                    if (!MOBILE_NO.equals(""))
                        getSetValues.setMobileno(MOBILE_NO);
                    else getSetValues.setMobileno("NA");
                    if (!DEVICE_ID.equals(""))
                        getSetValues.setDeviceid(DEVICE_ID);
                    else getSetValues.setDeviceid("NA");
                    if (!LONGITUDE.equals(""))
                        getSetValues.setLongitude(LONGITUDE);
                    else getSetValues.setLongitude("NA");
                    if (!LATITUDE.equals(""))
                        getSetValues.setLatitude(LATITUDE);
                    else getSetValues.setLatitude("NA");

                    arrayList.add(getSetValues);
                    mrAdapter.notifyDataSetChanged();
                }
                handler.sendEmptyMessage(MRTRACKING_SUCCESS);
            } else handler.sendEmptyMessage(MRTRACKING_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}