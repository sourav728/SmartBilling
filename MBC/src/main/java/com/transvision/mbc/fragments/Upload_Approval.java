package com.transvision.mbc.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.transvision.mbc.MainActivity;
import com.transvision.mbc.R;
import com.transvision.mbc.adapters.ApproveAdapter;
import com.transvision.mbc.posting.SendingData;
import com.transvision.mbc.values.FunctionsCall;
import com.transvision.mbc.values.GetSetValues;

import java.util.ArrayList;
import java.util.Objects;

import static com.transvision.mbc.values.Constants.DOWNLOAD_APPROVAL_FAILURE;
import static com.transvision.mbc.values.Constants.DOWNLOAD_APPROVAL_GRANT_FAILURE;
import static com.transvision.mbc.values.Constants.DOWNLOAD_APPROVAL_GRANT_SUCCESS;
import static com.transvision.mbc.values.Constants.DOWNLOAD_APPROVAL_SUCCESS;
import static com.transvision.mbc.values.Constants.UPLOAD_APPROVAL_FAILURE;
import static com.transvision.mbc.values.Constants.UPLOAD_APPROVAL_GRANT_FAILURE;
import static com.transvision.mbc.values.Constants.UPLOAD_APPROVAL_GRANT_SUCCESS;
import static com.transvision.mbc.values.Constants.UPLOAD_APPROVAL_SUCCESS;

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
    String mr_list = "";
    int selectedCount = 0;

    {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPLOAD_APPROVAL_SUCCESS:
                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                        //recyclerView.smoothScrollToPosition(approveAdapter.getItemCount()-1);
                        break;
                    case UPLOAD_APPROVAL_FAILURE:
                        Toast.makeText(getActivity(), "Approval List Not Found for this Subdivision!!", Toast.LENGTH_SHORT).show();
                        break;
                    case UPLOAD_APPROVAL_GRANT_SUCCESS:
                        Toast.makeText(getActivity(), "Approval Success", Toast.LENGTH_SHORT).show();
                    case UPLOAD_APPROVAL_GRANT_FAILURE:
                        Toast.makeText(getActivity(), "Approval Failure!!", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
    }

    public Upload_Approval() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload__approval, container, false);

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
        sendingData = new SendingData(getContext());
        setHasOptionsMenu(true);
        SendingData.Approval_Details approval_details = sendingData.new Approval_Details(handler, arrayList, getSetValues, approveAdapter);
        approval_details.execute("540037", "1");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuInflater mi = getActivity().getMenuInflater();
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
                Toast.makeText(getActivity(), "Clicked...", Toast.LENGTH_SHORT).show();
                postdata();
                break;
        }
    }

    private void postdata() {
        ArrayList<GetSetValues> approvedlist = approveAdapter.getApprovedList();
        stringBuilder = new StringBuilder();
        for (int i = 0; i < approvedlist.size(); i++) {
            GetSetValues getSetValues = approvedlist.get(i);
            if (getSetValues.isSelected()) {
                stringBuilder.append(getSetValues.getMrcode()).append("-").append(getSetValues.getDate()).append(",");
            }
        }
        Log.d("debug", stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1));
        mr_list = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
        SendingData.MR_Approved mr_approved = sendingData.new MR_Approved(handler);
        mr_approved.execute(mr_list, "1");
    }

}

