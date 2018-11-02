package com.transvision.mbc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.transvision.mbc.R;
import com.transvision.mbc.values.GetSetValues;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {
    private ArrayList<GetSetValues> arrayList = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private GetSetValues getSetValues;
    public SpinnerAdapter(ArrayList<GetSetValues>arrayList,GetSetValues getSetValues, Context context)
    {
        this.arrayList = arrayList;
        this.getSetValues = getSetValues;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.spinner_items, null);
        TextView tv_role = (TextView) convertView.findViewById(R.id.spinner_txt);
        tv_role.setText(arrayList.get(position).getLogin_role());
        return convertView;
    }
}
