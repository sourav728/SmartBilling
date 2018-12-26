package com.transvision.mbc.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.transvision.mbc.R;
import com.transvision.mbc.values.GetSetValues;

import java.util.ArrayList;

public class ApproveAdapter extends RecyclerView.Adapter<ApproveAdapter.ApproveHolder> {
    private ArrayList<GetSetValues> arrayList;
    private Context context;
    private GetSetValues getSetValues;
    public boolean isSelectedAll = false;

    public ApproveAdapter(Context context, ArrayList<GetSetValues> arrayList, GetSetValues getSetValues) {
        this.arrayList = arrayList;
        this.context = context;
        this.getSetValues = getSetValues;
    }

    @NonNull
    @Override
    public ApproveAdapter.ApproveHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.download_approve_layout, null);
        return new ApproveAdapter.ApproveHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApproveAdapter.ApproveHolder approveHolder, int i) {
        final GetSetValues getSetValues = arrayList.get(i);
        approveHolder.mrcode.setText(getSetValues.getMrcode());
        approveHolder.date.setText(getSetValues.getDate());

        if (!isSelectedAll) approveHolder.chk_approve.setChecked(false);
        else approveHolder.chk_approve.setChecked(true);

        approveHolder.chk_approve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getSetValues.setSelected(b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ApproveHolder extends RecyclerView.ViewHolder {
        TextView mrcode,date;
        CheckBox chk_approve;

        public ApproveHolder(@NonNull View itemView) {
            super(itemView);
            mrcode = itemView.findViewById(R.id.txt_mr_code);
            date = itemView.findViewById(R.id.txt_fromdate);
            chk_approve = itemView.findViewById(R.id.chk_mr_approve);
        }
    }

    public void selectAll() {
        Log.e("onClickSelectAll", "yes");
        isSelectedAll = true;
        notifyDataSetChanged();
    }

    public void de_selectAll() {
        Log.e("onClickDeSelectAll", "yes");
        isSelectedAll = false;
        notifyDataSetChanged();
    }

    public ArrayList<GetSetValues> getApprovedList() {
        return arrayList;
    }
}
