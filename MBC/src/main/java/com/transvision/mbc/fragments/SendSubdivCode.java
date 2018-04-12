package com.transvision.mbc.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.transvision.mbc.Location;
import com.transvision.mbc.R;
import com.transvision.mbc.fragments.billing.DL_MNR_Report;
import com.transvision.mbc.values.FunctionsCall;

import java.util.Calendar;

/**
 * Created by Sourav on 1/24/2018.
 */
public class SendSubdivCode extends Fragment {

    private static final int DLG_SUCCESS = 1;
    private static final int DLG_FAILURE = 2;

    FunctionsCall functionsCall;
    Boolean flag = false;
    int count = 0;
    ProgressDialog progressDialog;
    Button btn_sendsubdiv_code, btnDatePicker, btn_subdivwise_summary,dl_mnr, collectiondetails, mrtracking, location;
    String subdiv_code, date, dd, unbilled;
    String subdivisioncode, dum ;
    private int mYear, mMonth, mDay;
    FragmentTransaction fragmentTransaction;
    int daycount;
    Button signal_battery;
    public SendSubdivCode() {
        // Required empty public constructor
    }

    private final Handler mHandler;

    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DLG_SUCCESS:
                        progressDialog.dismiss();
                        break;
                    case DLG_FAILURE:
                        progressDialog.dismiss();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_send_subdiv_code, container, false);
        btnDatePicker = (Button) view.findViewById(R.id.btn_date);
        dl_mnr = (Button) view.findViewById(R.id.dl_mnr_btn);
        collectiondetails = (Button) view.findViewById(R.id.collection_report_btn);
        btn_subdivwise_summary = (Button) view.findViewById(R.id.subdiv_summary);
        signal_battery = (Button) view.findViewById(R.id.mr_signal_battery_info);

        location = (Button) view.findViewById(R.id.mr_location_demo);

        mrtracking = (Button) view.findViewById(R.id.mr_tracking_btn);

        functionsCall = new FunctionsCall();
        Bundle bundle = getArguments();
        if (bundle!= null)
        {
            subdivisioncode = bundle.getString("subdivcode");
        }

        btn_sendsubdiv_code = (Button) view.findViewById(R.id.subdiv_code_btn);
        btn_sendsubdiv_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                daycount = mDay;
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                dd =(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                               /* if (mDay<9)
                                {
                                    String val = "0";
                                    dd =(year + "-" + (monthOfYear + 1) + "-" + val+dayOfMonth);

                                }
                                else
                                {
                                    dd =(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                                }*/

                                dum = functionsCall.Parse_Date2(dd);

                                SelectSubdivision selectSubdivision = new SelectSubdivision();
                                Bundle bundle = new Bundle();
                                bundle.putString("flag","One");
                                bundle.putString("date", dum);
                                bundle.putString("dd",dd);
                                bundle.putString("daycount", daycount+"");

                                selectSubdivision.setArguments(bundle);
                                fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.container_main,selectSubdivision).addToBackStack(null).commit();

                                // Toast.makeText(getActivity(), "Moveing to next fragment!!", Toast.LENGTH_SHORT).show();
                                //Clicking on ok button will do the following actions

                               /* ShowMrDetails showMrDetails = new ShowMrDetails();
                                Bundle bundle = new Bundle();

                                bundle.putString("subdivcode", subdivisioncode);
                                bundle.putString("date", dum);
                                bundle.putString("dd",dd);
                                bundle.putString("daycount", daycount+"");

                                showMrDetails.setArguments(bundle);
                                fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.container_main,showMrDetails).addToBackStack(null).commit();
                                showdialog(DLG_SUCCESS);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                },3000);*/

                                //show_date.setText(":"+dum);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                datePickerDialog.show();


            }
        });

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                daycount = mDay;
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                if (mDay<9)
                                {
                                    String val = "0";
                                    dd =(year + "-" + (monthOfYear + 1) + "-" + val+dayOfMonth);

                                }
                                else
                                {
                                    dd =(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                                }

                                dum = functionsCall.Parse_Date2(dd);
                                Toast.makeText(getActivity(), "Moveing to next fragment!!", Toast.LENGTH_SHORT).show();
                                //show_date.setText(":"+dum);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                datePickerDialog.show();
            }
        });

        btn_subdivwise_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  SummaryFragment summaryFragment = new SummaryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("subdivcode",subdivisioncode);
                summaryFragment.setArguments(bundle);
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_main,summaryFragment).addToBackStack(null).commit();
                // Toast.makeText(getActivity(), "Will be implemented later...", Toast.LENGTH_SHORT).show();*/


                SelectSubdivision selectSubdivision = new SelectSubdivision();
                Bundle bundle = new Bundle();
                bundle.putString("flag","Two");

                selectSubdivision.setArguments(bundle);

                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_main,selectSubdivision).addToBackStack(null).commit();
            }
        });

        dl_mnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DL_MNR_Report dl_mnr_report = new DL_MNR_Report();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_main, dl_mnr_report).addToBackStack(null).commit();
            }
        });

        collectiondetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Collection collection = new Collection();
                Bundle bundle = new Bundle();
                bundle.putString("subdivcode",subdivisioncode);
                collection.setArguments(bundle);
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_main, collection).addToBackStack(null).commit();*/
                SelectSubdivision selectSubdivision = new SelectSubdivision();
                Bundle bundle = new Bundle();
                bundle.putString("flag","Three");

                selectSubdivision.setArguments(bundle);

                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_main,selectSubdivision).addToBackStack(null).commit();

            }
        });

        mrtracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* MRTrackingFragment mrTrackingFragment = new MRTrackingFragment();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_main,mrTrackingFragment).addToBackStack(null).commit();*/
                SelectSubdivision selectSubdivision = new SelectSubdivision();
                Bundle bundle = new Bundle();
                bundle.putString("flag","Four");
                selectSubdivision.setArguments(bundle);
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_main,selectSubdivision).addToBackStack(null).commit();

            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Location.class);
                startActivity(intent);
            }
        });
        signal_battery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Battery_Signal_Info battery_signal_info = new Battery_Signal_Info();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_main,battery_signal_info).addToBackStack(null).commit();
            }
        });

        return  view;
    }

    private void showdialog(int id)
    {
        switch (id)
        {
            case DLG_SUCCESS:
                progressDialog = ProgressDialog.show(getActivity(),"Wait for a second...","Success",true);
                break;
            case DLG_FAILURE:
                progressDialog = ProgressDialog.show(getActivity(),"Wiat for a second...", "Failure", true);
                break;
        }
    }
}
