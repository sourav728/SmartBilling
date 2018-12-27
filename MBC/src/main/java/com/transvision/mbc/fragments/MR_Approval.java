package com.transvision.mbc.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.transvision.mbc.R;

import java.util.Objects;

public class MR_Approval extends Fragment implements View.OnClickListener {
    Button download_approve, upload_approve;
    FragmentTransaction fragmentTransaction;

    public MR_Approval() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mr_approval, container, false);
        download_approve = view.findViewById(R.id.btn_download_approval);
        download_approve.setOnClickListener(this);
        upload_approve = view.findViewById(R.id.btn_upload_approval);
        upload_approve.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download_approval:
                Download_Approval download_approval = new Download_Approval();
                fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                fragmentTransaction.replace(R.id.container_main, download_approval).commit();
                break;
            case R.id.btn_upload_approval:
                Upload_Approval upload_approval = new Upload_Approval();
                fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                fragmentTransaction.replace(R.id.container_main, upload_approval).commit();
                break;
        }
    }
}
