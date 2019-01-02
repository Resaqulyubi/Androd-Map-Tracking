package travel.maptracking.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.HttpUrl;
import okhttp3.Response;
import travel.maptracking.R;
import travel.maptracking.model.schedule;
import travel.maptracking.model.user;
import travel.maptracking.network.Api;
import travel.maptracking.util.BaseAppCompatActivity;
import travel.maptracking.util.Constant;

public class ScheduleDetailActivity extends BaseAppCompatActivity {
    private android.support.v7.widget.Toolbar toolbar;
    private ScheduleDetailActivity obj;
    TextView tv_driver_id,tv_driver_name,tv_driver_phone,tv_driver_email,tv_driver_car,tv_driver_police_number;
    user.Data dataDriver =new user().getDataItem();
    schedule.Data dataSchedule;

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
        setContentView(R.layout.activity_schedule_detail);


        if (hasPermissions(this, RUNTIME_PERMISSIONS)) {
            setupMapFragmentView();
        } else {
            ActivityCompat
                    .requestPermissions(this, RUNTIME_PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
        }

        TextView tv_jalan =findViewById(R.id.tv_jalan);
        TextView tv_id_shedule =findViewById(R.id.tv_id_shedule);
        TextView tv_id =findViewById(R.id.tv_id);
        TextView tv_nama =findViewById(R.id.tv_nama);
        TextView tv_email =findViewById(R.id.tv_email);
        TextView tv_phone =findViewById(R.id.tv_phone);
        TextView tv_address =findViewById(R.id.tv_address);
        TextView tv_paket =findViewById(R.id.tv_paket);
        TextView tv_payment =findViewById(R.id.tv_payment);

        TextView tv_pickup_time =findViewById(R.id.tv_pickup_time);
        TextView tv_pickup_point =findViewById(R.id.tv_pickup_point);
        TextView tv_arrival =findViewById(R.id.tv_arrival);
        TextView tv_destination_point =findViewById(R.id.tv_destination_point);

         tv_driver_id =findViewById(R.id.tv_driver_id);
        tv_driver_name =findViewById(R.id.tv_driver_name);
        tv_driver_phone =findViewById(R.id.tv_driver_phone);
        tv_driver_email =findViewById(R.id.tv_driver_email);
        tv_driver_car =findViewById(R.id.tv_driver_car);
        tv_driver_police_number =findViewById(R.id.tv_driver_police_number);

        RadioButton rdb_waiting =findViewById(R.id.rdb_waiting);
        RadioButton rdb_otw =findViewById(R.id.rdb_otw);
        RadioButton rdb_arrive =findViewById(R.id.rdb_arrive);
        RadioButton rdb_problem =findViewById(R.id.rdb_problem);
        RadioButton rdb_back_to_base =findViewById(R.id.rdb_back_to_base);

        Button btn_tracking =findViewById(R.id.btn_tracking);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        obj = this;


        String extra = getIntent().getStringExtra("json");

        Gson gson = new Gson();
        TypeToken<schedule.Data> token = new TypeToken<schedule.Data>() {};
        dataSchedule = gson.fromJson(extra, token.getType());

        tv_id_shedule.append(dataSchedule.getId());
        tv_jalan.setText(dataSchedule.getPickup_point()+"-"+ dataSchedule.getDestination());
        tv_nama.setText(dataSchedule.getCustomer_name());
        tv_id.setText(dataSchedule.getId_customer());
        tv_email.setText(dataSchedule.getEmail());
        tv_phone.setText(dataSchedule.getPhone());
        tv_address.setText(dataSchedule.getAddress());
        tv_paket.setText(dataSchedule.getPaket());
        tv_payment.setText(dataSchedule.getPayment_code());

        tv_pickup_time.setText(dataSchedule.getPickup_time());
        tv_pickup_point.setText(dataSchedule.getPickup_point());
        tv_arrival.setText(dataSchedule.getArrival());
        tv_destination_point.setText(dataSchedule.getDestination());

        tv_driver_id.setText(dataSchedule.getId_driver());


        switch (dataSchedule.getStatus()){
            case "waiting":
                rdb_waiting.setChecked(true);
                break;
            case "otw":
                rdb_otw.setChecked(true);
                break;
            case "arrive":
                rdb_arrive.setChecked(true);
                break;
            case "problem":
                rdb_problem.setChecked(true);
                break;
            case "backtobase":
                rdb_back_to_base.setChecked(true);
                break;


        }

        btn_tracking.setOnClickListener(view -> {
            Gson gson1 = new Gson();
            String jsonDriver = gson1.toJson(dataDriver);
            Intent i = new Intent(this, TrackingDriverActivity.class)
                    .putExtra("jsonSchedule",getIntent().getStringExtra("json"))
                    .putExtra("jsonDriver",jsonDriver);
            startActivity(i);
        });

        getRecordUser(dataSchedule.getId_driver());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            getRecordUser(dataSchedule.getId_driver());

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean getRecordUser(String id) {
        boolean[] a = {false};
        new AsyncTask<Void, Void, Boolean>() {
            Date dStart = null;
            Date dEnd = null;
            ProgressDialog dialog =new ProgressDialog(ScheduleDetailActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("Loading get dataRoute Schedule...");
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

                httpUrlBuilder.addQueryParameter("id",id);

                SimpleDateFormat ft = new SimpleDateFormat (Constant.default_simpledate2);


                try (Response response = new Api(ScheduleDetailActivity.this).
                        get(getString(R.string.api_user),httpUrlBuilder)) {
                    if (response == null || !response.isSuccessful())
                        throw new IOException("Unexpected code = " + response);

                    String responseBodyString = response.body().string();
                    Gson gson = new Gson();
                    user user = gson.fromJson(responseBodyString, user.class);

                    if (user.isStatus()) {
                        dataDriver=user.getData().get(0);
                        obj.runOnUiThread(new Runnable() {

                            public void run() {
                                tv_driver_name.setText(user.getData().get(0).getNama());
                                tv_driver_phone.setText(user.getData().get(0).getPhone());
                                tv_driver_email.setText(user.getData().get(0).getEmail());
                                tv_driver_car.setText(user.getData().get(0).getCar());
                                tv_driver_police_number.setText(user.getData().get(0).getPolice_number());
                            }
                        });
                    }else {
                        obj.runOnUiThread(new Runnable() {
                            public void run() {

                                Toast.makeText(obj, "Tidak Ada dataSchedule", Toast.LENGTH_SHORT).show();
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
        m_mapFragmentView = new MapFragmentView(this,"scheduledetail");
    }
}
