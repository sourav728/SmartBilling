package com.transvision.mbc.values;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.transvision.mbc.other.CheckInternetConnection;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class FunctionsCall {
    public void logStatus(String message) {
        Log.d("debug", message);
    }
    public String Parse_Date2(String time) {
        String input = "yyyy-MM-d";
        String output = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(input);
        SimpleDateFormat outputFormat = new SimpleDateFormat(output);

        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String Parse_Date4(String time) {
        String input = "yyyy-MM-d";
        String output = "MM-dd-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(input);
        SimpleDateFormat outputFormat = new SimpleDateFormat(output);

        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    public String Parse_date3(String time)
    {
        String inputPattern = "dd-MM-yyyy HH:mm:ss";
        String outputPattern = "HH:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    public boolean checkInternetConnection(Context context) {
        CheckInternetConnection cd = new CheckInternetConnection(context.getApplicationContext());
        return cd.isConnectingToInternet();
    }
    /***********CHECKING INTERNET IS ON OR NOT****************/
    public final boolean isInternetOn(Activity activity) {
        ConnectivityManager connect = (ConnectivityManager) activity.getSystemService(activity.getBaseContext().CONNECTIVITY_SERVICE);
        if (connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else if (connect.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                connect.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    public String datepickerdlgformat(String dateformat) {
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateformat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String format = sdf.format(c.getTime());
        return format;
    }

    public String Parse_Date3(String time) {
        String input = "yyyy-MM-dd";
        String output = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(input);
        SimpleDateFormat outputFormat = new SimpleDateFormat(output);

        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String Parse_Date5(String time) {
        String input = "MM-dd-yyyy";
        String output = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(input);
        SimpleDateFormat outputFormat = new SimpleDateFormat(output);

        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    public void showtoast(Context context, String Message) {
        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
    }
    public String filepath(String value) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), Appfoldername()
                + File.separator + value);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String pathname = dir.toString();
        return pathname;
    }
    public String Appfoldername() {
        String name = "MBC_Insight";
        return name;
    }
    public String currentRecpttime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);
        return sdf.format(new Date());
    }
    public boolean compare(String v1, String v2) {
        String s1 = normalisedVersion(v1);
        String s2 = normalisedVersion(v2);
        int cmp = s1.compareTo(s2);
        String cmpStr = cmp < 0 ? "<" : cmp > 0 ? ">" : "==";
        return cmpStr.equals("<");
    }
    public String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }
    private String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }
    public void showprogress(String Message, ProgressDialog dialog, String Title) {
        dialog.setTitle(Title);
        dialog.setMessage(Message);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();
    }
    public void updateApp(Context context, File Apkfile) {
        Uri path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            path = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", Apkfile);
        } else path = Uri.fromFile(Apkfile);
        Intent objIntent = new Intent(Intent.ACTION_VIEW);
        objIntent.setDataAndType(path, "application/vnd.android.package-archive");
        objIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(objIntent);
    }
    public double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }
    public void rotateMarker(final Marker marker, final float toRotation) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 2000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    float bearing =  -rot > 180 ? rot / 2 : rot;

                    marker.setRotation(bearing);

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });

    }
}