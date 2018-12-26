package com.transvision.mbc.fragments;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.transvision.mbc.R;
import com.transvision.mbc.ViewAllLocation;
import com.transvision.mbc.adapters.MRAdapter;
import com.transvision.mbc.posting.SendingData;
import com.transvision.mbc.receiver.NetworkChangeReceiver;
import com.transvision.mbc.values.FunctionsCall;
import com.transvision.mbc.values.GetSetValues;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.transvision.mbc.values.Constants.MRTRACKING_FAILURE;
import static com.transvision.mbc.values.Constants.MRTRACKING_SUCCESS;
import static com.transvision.mbc.values.Constants.SUBDIV_DETAILS_FAILURE;
import static com.transvision.mbc.values.Constants.SUBDIV_DETAILS_SUCCESS;

/**
 * Created by Sourav
 */
public class MRTrackingFragment extends Fragment {
    RecyclerView recyclerView;
    private BroadcastReceiver mNetworkReceiver;
    GetSetValues getSetValues, getSet;
    FunctionsCall functionsCall;
    public static final String GETSET = "getset";
    ArrayList<GetSetValues> arrayList;
    private MRAdapter mrAdapter;
    String subdivisioncode="";
    //static TextView tv_check_connection;
    SendingData sendingData;
    private Handler handler = null;

    {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MRTRACKING_SUCCESS:
                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                        break;
                    case MRTRACKING_FAILURE:
                        Toast.makeText(getActivity(), "Failure!!", Toast.LENGTH_SHORT).show();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    public MRTrackingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mrtracking, container, false);
        mNetworkReceiver = new NetworkChangeReceiver();
        //tv_check_connection = (TextView) view.findViewById(R.id.tv_check_connection);
        Bundle bundle = getArguments();
        if (bundle!= null)
            subdivisioncode = bundle.getString("subdivcode");
        arrayList = new ArrayList<>();
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) view.findViewById(R.id.mrtrack_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mrAdapter = new MRAdapter(getActivity(), arrayList, getSetValues);
        recyclerView.setAdapter(mrAdapter);
        functionsCall = new FunctionsCall();
        sendingData = new SendingData(getContext());
        SendingData.MRTracking mrTracking = sendingData.new MRTracking(handler, arrayList, getSetValues, mrAdapter);
        mrTracking.execute(subdivisioncode);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuInflater mi = getActivity().getMenuInflater();
        mi.inflate(R.menu.location,menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint(Html.fromHtml("<font color = #212121>" + "Search by Mrcode.." + "</font>"));
        search(searchView);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_location:
                Intent intent = new Intent(getActivity(), ViewAllLocation.class);
                intent.putExtra("list",arrayList);
                intent.putExtra(GETSET, getSetValues);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView)
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mrAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
