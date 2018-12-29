package com.transvision.mbc.fragments;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.transvision.mbc.MainActivity;
import com.transvision.mbc.R;
import com.transvision.mbc.adapters.ApproveAdapter;
import com.transvision.mbc.posting.SendingData;
import com.transvision.mbc.values.FunctionsCall;
import com.transvision.mbc.values.GetSetValues;

import java.util.ArrayList;
import java.util.Objects;

import static com.transvision.mbc.values.Constants.DOWNLOAD_UPLOAD_APPROVAL_FAILURE;
import static com.transvision.mbc.values.Constants.DOWNLOAD_UPLOAD_APPROVAL_GRANT_FAILURE;
import static com.transvision.mbc.values.Constants.DOWNLOAD_UPLOAD_APPROVAL_GRANT_SUCCESS;
import static com.transvision.mbc.values.Constants.DOWNLOAD_UPLOAD_APPROVAL_SUCCESS;
import static com.transvision.mbc.values.Constants.sPref_SUBDIVISION;

public class Upload_Approval extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView;
    FunctionsCall functionsCall;
    GetSetValues getSetValues;
    private ApproveAdapter approveAdapter;
    ArrayList<GetSetValues> arrayList;
    SendingData sendingData;
    private Handler handler = null;
    Button approve;
    StringBuilder stringBuilder;
    String mr_list = "", subdivision = "";
    ProgressDialog progressDialog;
    SharedPreferences sPref;
    SharedPreferences.Editor editor;
    boolean checkbox_selected = false;
    int selectedCount = 0;

    {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DOWNLOAD_UPLOAD_APPROVAL_SUCCESS:
                        progressDialog.dismiss();
                        approve.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        //recyclerView.smoothScrollToPosition(approveAdapter.getItemCount()-1);
                        break;
                    case DOWNLOAD_UPLOAD_APPROVAL_FAILURE:
                        progressDialog.dismiss();
                        approve.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Upload Approval List Not Found for this Subdivision!!", Toast.LENGTH_SHORT).show();
                        //((MainActivity) Objects.requireNonNull(getActivity())).switchContent(MainActivity.Steps.FORM3, getResources().getString(R.string.app_name));
                        SendSubdivCode sendSubdivCode = new SendSubdivCode();
                        FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                        //For clearing top stack
                        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentTransaction.replace(R.id.container_main, sendSubdivCode).commit();
                        break;
                    case DOWNLOAD_UPLOAD_APPROVAL_GRANT_SUCCESS:
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Approval Success", Toast.LENGTH_SHORT).show();
                        checkbox_selected = false;
                        Fragment currentfragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container_main);
                        FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                        fragmenttransaction.detach(currentfragment);
                        fragmenttransaction.attach(currentfragment);
                        fragmenttransaction.commit();
                        break;
                    case DOWNLOAD_UPLOAD_APPROVAL_GRANT_FAILURE:
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Approval Failure!!", Toast.LENGTH_SHORT).show();
                        checkbox_selected = false;
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    public Upload_Approval() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload__approval, container, false);
        checkbox_selected = false;
        sPref = ((MainActivity) Objects.requireNonNull(getActivity())).getsharedPref();
        editor = sPref.edit();
        editor.apply();
        subdivision = (sPref.getString(sPref_SUBDIVISION, ""));
        Log.d("debug", "Subdivision in Upload Approval " + subdivision);
        functionsCall = new FunctionsCall();
        getSetValues = new GetSetValues();
        arrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.download_approve_recycler_view);
        approve = view.findViewById(R.id.btn_submit);
        approve.setOnClickListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        approveAdapter = new ApproveAdapter(getActivity(), arrayList, getSetValues);
        recyclerView.setAdapter(approveAdapter);
        sendingData = new SendingData(Objects.requireNonNull(getContext()));
        setHasOptionsMenu(true);
        progressDialog = new ProgressDialog(getActivity(), R.style.MyProgressDialogstyle);
        progressDialog.setTitle("Fetching Data");
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        SendingData.Approval_Details approval_details = sendingData.new Approval_Details(handler, arrayList, getSetValues, approveAdapter);
        approval_details.execute(subdivision, "1");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuInflater mi = Objects.requireNonNull(getActivity()).getMenuInflater();
        mi.inflate(R.menu.download_upload_approval, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_select_all:
                if (item.isChecked())
                    item.setChecked(false);
                else item.setChecked(true);
                if (item.isChecked())
                    approveAdapter.selectAll();
                else approveAdapter.de_selectAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                postdata();
                break;
        }
    }

    private void postdata() {
        ArrayList<GetSetValues> approvedlist = approveAdapter.getApprovedList();
        for (int j = 0; j < approvedlist.size(); j++) {
            GetSetValues getSetValues1 = approvedlist.get(j);
            if (getSetValues1.isSelected()) {
                checkbox_selected = true;
            }
        }
        if (checkbox_selected) {
            stringBuilder = new StringBuilder();
            for (int i = 0; i < approvedlist.size(); i++) {
                GetSetValues getSetValues = approvedlist.get(i);
                if (getSetValues.isSelected()) {
                    stringBuilder.append(getSetValues.getMrcode()).append("-").append(getSetValues.getDate()).append(",");
                }
            }
            mr_list = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
            progressDialog = new ProgressDialog(getActivity(), R.style.MyProgressDialogstyle);
            progressDialog.setTitle("Updating");
            progressDialog.setMessage("Please Wait..");
            progressDialog.show();
            SendingData.MR_Approved mr_approved = sendingData.new MR_Approved(handler);
            mr_approved.execute(mr_list, "1");
        } else
            Toast.makeText(getContext(), "Please select atleast one MR for approval!!", Toast.LENGTH_SHORT).show();
    }
}

