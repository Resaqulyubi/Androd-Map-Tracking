package travel.maptracking.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Response;
import travel.maptracking.R;
import travel.maptracking.model.schedule;
import travel.maptracking.model.scheduleRoute;
import travel.maptracking.model.user;
import travel.maptracking.network.Api;
import travel.maptracking.util.BaseAppCompatActivity;
import travel.maptracking.util.Constant;

public class TrackingDriverActivity extends BaseAppCompatActivity {
    private android.support.v7.widget.Toolbar toolbar;
    private TrackingDriverActivity obj;
    schedule.Data  schedule=null;

    public List<scheduleRoute.Data> getDataScheduleRoute() {
        return dataScheduleRoute;
    }

    public void setDataScheduleRoute(List<scheduleRoute.Data> dataScheduleRoute) {
        this.dataScheduleRoute = dataScheduleRoute;
    }

    List<scheduleRoute.Data> dataScheduleRoute=new ArrayList<>();
    //map
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] RUNTIME_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    private MapFragmentView m_mapFragmentView;

    @Override
    protected int getLayoutResource() {
        return 0;
    }

    @Override
    protected boolean isTitleEnabled() {
        return false;
    }

    @Override
    protected String getTag() {
        return null;
    }

    @Override
    protected void onCreateActivity(Bundle pSavedInstanceState) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_driver);



        TextView tv_id_driver =findViewById(R.id.tv_id_driver);
        TextView tv_driver_name =findViewById(R.id.tv_driver_name);
        TextView tv_car =findViewById(R.id.tv_car);
        TextView tv_jalan =findViewById(R.id.tv_jalan);
        TextView tv_status =findViewById(R.id.tv_status);
        TextView tv_id_shedule =findViewById(R.id.tv_id_shedule);
        Button btn_call =findViewById(R.id.btn_call);
        Button btn_message =findViewById(R.id.btn_message);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        obj = this;

        String extraSchedule = getIntent().getStringExtra("jsonSchedule");
        String extraDriver = getIntent().getStringExtra("jsonDriver");

        Gson gson = new Gson();
        TypeToken<schedule.Data> token = new TypeToken<schedule.Data>() {};
        TypeToken<user.Data> token1 = new TypeToken<user.Data>() {};
        schedule = gson.fromJson(extraSchedule, token.getType());
        user.Data  driver = gson.fromJson(extraDriver, token1.getType());


        tv_id_driver.append(driver .getId());
        tv_id_shedule.append(schedule .getId());

        tv_driver_name.setText(driver .getNama());
        tv_car.setText(driver .getCar() +" "+driver .getPolice_number());


        tv_status.setText(schedule .getStatus());
        tv_jalan.setText(schedule.getPickup_point() + "-" + schedule.getDestination());

        getRecordRouting(schedule.getId());

        btn_call.setOnClickListener(view -> {
            String phone =driver!=null? driver.getPhone():"";
            Intent call = new Intent(Intent.ACTION_DIAL);
            call.setData( Uri.parse("tel:" + phone));
            startActivity(call);

//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
        });
        btn_message.setOnClickListener(view -> {
            String phone =driver!=null? driver.getPhone():"";
            Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", phone);
            smsIntent.putExtra("sms_body", "");
            this.startActivity(Intent.createChooser(smsIntent, "SMS:"));
        });


        SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRecordRouting(schedule.getId());
                pullToRefresh.setRefreshing(false);
            }
        });

    }

    public boolean getRecordRouting(String id) {
        boolean[] a = {false};
        new AsyncTask<Void, Void, Boolean>() {
            Date dStart = null;
            Date dEnd = null;
            ProgressDialog dialog =new ProgressDialog(TrackingDriverActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("Loading get tracking...");
                dialog.setCancelable(false);
                obj.runOnUiThread(new Runnable() {
                    public void run() {
                        dialog.show();
                    }
                });
                dStart = new Date();
                dEnd = new Date();

            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean b=false;


                HttpUrl.Builder httpUrlBuilder = new HttpUrl.Builder();

                httpUrlBuilder.addQueryParameter("id_schedule",id);

                SimpleDateFormat ft = new SimpleDateFormat (Constant.default_simpledate2);


                try (Response response = new Api(TrackingDriverActivity.this).
                        get(getString(R.string.api_schedule_route),httpUrlBuilder)) {
                    if (response == null || !response.isSuccessful())
                        throw new IOException("Unexpected code = " + response);

                    String responseBodyString = response.body().string();
                    Gson gson = new Gson();
                    scheduleRoute scheduleRoute = gson.fromJson(responseBodyString, scheduleRoute.class);

                    if (scheduleRoute.isStatus() && scheduleRoute.getData().size() > 0) {
                        dataScheduleRoute = scheduleRoute.getData();

                        if (hasPermissions(TrackingDriverActivity.this, RUNTIME_PERMISSIONS)) {
                            setupMapFragmentView();
                        } else {
                            ActivityCompat
                                    .requestPermissions(TrackingDriverActivity.this, RUNTIME_PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
                        }
                    }else {
                        obj.runOnUiThread(new Runnable() {
                            public void run() {

                                Toast.makeText(obj, "Tidak Ada dataRoute Routing", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    obj.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(obj, "Tidak mendapat balasan dari server..", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
                return  b;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                obj.runOnUiThread(new Runnable() {
                    public void run() {
                        if (dialog!=null&dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                });


            }
        }.execute();


        return  a[0];

    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                for (int index = 0; index < permissions.length; index++) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {

                        /*
                         * If the user turned down the permission request in the past and chose the
                         * Don't ask again option in the permission request system dialog.
                         */
                        if (!ActivityCompat
                                .shouldShowRequestPermissionRationale(this, permissions[index])) {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                            + " not granted. "
                                            + "Please go to settings and turn on for sample app",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                    + " not granted", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                setupMapFragmentView();
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setupMapFragmentView() {
        // All permission requests are being handled. Create map fragment view. Please note
        // the HERE SDK requires all permissions defined above to operate properly.
        m_mapFragmentView = new MapFragmentView(this,"scheduletracking");
    }
}
