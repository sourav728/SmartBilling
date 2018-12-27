package com.transvision.mbc.fragments;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.transvision.mbc.R;
import com.transvision.mbc.posting.SendingData;
import com.transvision.mbc.values.FunctionsCall;
import com.transvision.mbc.values.GetSetValues;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import static com.transvision.mbc.values.Constants.BILLING_FILE_SUMMARY_FAILURE;
import static com.transvision.mbc.values.Constants.BILLING_FILE_SUMMARY_SUCCESS;

public class SummaryFragment extends Fragment {
    Button report;

    TextView fromdate, todate;
    ImageView imagefrom, imageto;
    private int day, month, year;
    private Calendar mcalender;
    int daycount;
    String dd, date1, date2, subdivisioncode;
    String DWNRECORD, UPLOADRECORD, INFOSYSRECORD;
    FunctionsCall functioncall;
    FragmentTransaction fragmenttransaction;
    SendingData sendingData;
    GetSetValues getSetValues;
    ProgressDialog progressDialog;
    private static Handler handler = null;

    {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BILLING_FILE_SUMMARY_SUCCESS:
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Success..", Toast.LENGTH_SHORT).show();
                        SummaryDetailsFragment summarydetailsfragment = new SummaryDetailsFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("subdivcode", subdivisioncode);
                        bundle.putString("DWNRECORD", getSetValues.getDownload_record());
                        bundle.putString("UPLOADRECORD", getSetValues.getUpload_record());
                        bundle.putString("INFOSYSRECORD", getSetValues.getInfosys_record());
                        bundle.putString("FROM", date1);
                        bundle.putString("TO", date2);
                        summarydetailsfragment.setArguments(bundle);

                        fragmenttransaction = getFragmentManager().beginTransaction();
                        fragmenttransaction.replace(R.id.container_main, summarydetailsfragment).addToBackStack(null).commit();
                        break;
                    case BILLING_FILE_SUMMARY_FAILURE:
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Data Not Found..", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        };
    }

    public SummaryFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        sendingData = new SendingData(Objects.requireNonNull(getContext()));
        fromdate = view.findViewById(R.id.txt_fromdate);
        todate = view.findViewById(R.id.txt_todate);
        imagefrom = view.findViewById(R.id.img_fromdate);
        imageto = view.findViewById(R.id.img_todate);
        report = view.findViewById(R.id.btn_report);
        getSetValues = new GetSetValues();

        mcalender = Calendar.getInstance();
        day = mcalender.get(Calendar.DAY_OF_MONTH);
        daycount = day;
        year = mcalender.get(Calendar.YEAR);
        month = mcalender.get(Calendar.MONTH);
        functioncall = new FunctionsCall();
        Bundle bundle = getArguments();
        if (bundle != null) {
            subdivisioncode = bundle.getString("subdivcode");
        }

        imagefrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog1();
            }
        });

        imageto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateDialog2();
            }
        });


        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!fromdate.getText().toString().equals("")) {
                    if (!todate.getText().toString().equals("")) {

                        progressDialog = new ProgressDialog(getActivity(), R.style.MyProgressDialogstyle);
                        progressDialog.setTitle("Fetching Details");
                        progressDialog.setMessage("Please Wait..");
                        progressDialog.show();

                        SendingData.BillingFileSummary billingFileSummary = sendingData.new BillingFileSummary(handler, getSetValues);
                        billingFileSummary.execute(subdivisioncode, functioncall.Parse_Date5(date1), functioncall.Parse_Date5(date2));

                    } else
                        Toast.makeText(getContext(), "Please Select To date!!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Please select from date!!", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    public void DateDialog1() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                dd = (year + "-" + (month + 1) + "-" + dayOfMonth);
                date1 = functioncall.Parse_Date4(dd);
                fromdate.setText(date1);
            }
        };
        DatePickerDialog dpdialog = new DatePickerDialog(Objects.requireNonNull(getContext()), listener, year, month, day);
        dpdialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpdialog.show();
    }

    public void DateDialog2() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dd = (year + "-" + (month + 1) + "-" + dayOfMonth);
                date2 = functioncall.Parse_Date4(dd);
                todate.setText(date2);
            }
        };
        DatePickerDialog dpdialog = new DatePickerDialog(Objects.requireNonNull(getContext()), listener, year, month, day);
        dpdialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpdialog.show();
    }

}
