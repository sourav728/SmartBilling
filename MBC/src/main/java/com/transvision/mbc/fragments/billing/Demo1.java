package com.transvision.mbc.fragments.billing;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.transvision.mbc.R;
/**
 * Created by Sourav
 */
public class Demo1 extends Fragment {

    public Demo1() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo1, container, false);
    }

}
