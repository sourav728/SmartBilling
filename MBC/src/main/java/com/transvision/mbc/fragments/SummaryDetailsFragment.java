package com.transvision.mbc.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.transvision.mbc.R;

public class SummaryDetailsFragment extends Fragment {

    TextView subdivision, totalvalidfile, totaldownload, notdownload, upload, notupload;
    String division, valid, download, up, from_date, to_date;
    TextView from, to;

    public SummaryDetailsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_summary_details, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            division = bundle.getString("subdivcode");
            download = bundle.getString("DWNRECORD");
            up = bundle.getString("UPLOADRECORD");
            valid = bundle.getString("INFOSYSRECORD");
            from_date = bundle.getString("FROM");
            to_date = bundle.getString("TO");
        }
        subdivision = view.findViewById(R.id.txtsubdivision);
        totalvalidfile = view.findViewById(R.id.txt_totalvalidfile);
        totaldownload = view.findViewById(R.id.txt_totaldownloaded);
        notdownload = view.findViewById(R.id.txt_notdownloaded);
        upload = view.findViewById(R.id.txt_uploaded);
        notupload = view.findViewById(R.id.txt_notuploaded);
        from = view.findViewById(R.id.txt_from);
        to = view.findViewById(R.id.txt_to);

        int totalvalid = Integer.parseInt(valid);
        int totaldown = Integer.parseInt(download);
        int notdown = (totalvalid - totaldown);
        int uplo = Integer.parseInt(up);
        int notuplo = (totaldown - uplo);

        from.setText(from_date);
        to.setText(to_date);

        subdivision.setText(division);
        totalvalidfile.setText(valid);
        totaldownload.setText(download);
        notdownload.setText(notdown + "");
        upload.setText(up);
        notupload.setText(notuplo + "");
        return view;
    }

}
