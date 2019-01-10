package travel.maptracking.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.LocationDataSourceHERE;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapState;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.Location;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeRequest2;
import com.here.android.positioning.StatusListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Response;
import travel.maptracking.R;
import travel.maptracking.model.db.OutboxDB;
import travel.maptracking.model.schedule;
import travel.maptracking.model.user;
import travel.maptracking.network.Api;
import travel.maptracking.util.BaseAppCompatActivity;
import travel.maptracking.util.Constant;
import travel.maptracking.util.Util;

public class ScheduleDetailActivityDriver extends BaseAppCompatActivity implements Map.OnTransformListener, PositioningManager.OnPositionChangedListener {
    private android.support.v7.widget.Toolbar toolbar;
    private ScheduleDetailActivityDriver obj;
    TextView tv_driver_id,tv_driver_name,tv_driver_phone,tv_driver_email,tv_driver_car,tv_driver_police_number;
    TextView tv_admin_id,tv_admin_name,tv_admin_email,tv_admin_phone,tv_admin_office;
    Spinner sp_status;
    user.Data dataDriver =new user().getDataItem();
    schedule.Data dataSchedule;
    String hakAkses="";
    Double latitudeOld = 0d;
    Double longitudeOld = 0d;
    Double altitudeOld = 0d;

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

    //gps


    // map embedded in the map fragment
    private Map map;

    // map fragment embedded in this activity
    private SupportMapFragment mapFragment;


    // positioning manager instance
    private PositioningManager mPositioningManager;

    // HERE location dataSchedule source instance
    private LocationDataSourceHERE mHereLocation;

    // flag that indicates whether maps is being transformed
    private boolean mTransforming;

    // callback that is called when transforming ends
    private Runnable mPendingUpdate;

    // text view instance for showing location information
    private TextView mLocationInfo;



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

        hakAkses=  Util.getSharedPreferenceString(this, Constant.PREFS_IS_USER_AKSES,"");

        if (hasPermissions(this, RUNTIME_PERMISSIONS)) {
            initializeMapsAndPositioning();
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

        sp_status =findViewById(R.id.sp_status);

        String[] arraySpinner = new String[]{"Waiting", "On The Way","Problem", "Arrive","Back to Base"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_status.setAdapter(arrayAdapter);


        Button btn_call =findViewById(R.id.btn_call);
        Button btn_message =findViewById(R.id.btn_message);
        Button btn_driver_status_submit =findViewById(R.id.btn_driver_status_submit);

        LinearLayout lnly_driver_detail =findViewById(R.id.lnly_driver_detail);
        LinearLayout lnly_admin_detail =findViewById(R.id.lnly_admin_detail);
        LinearLayout lnly_admin_status =findViewById(R.id.lnly_admin_status);
        LinearLayout lnly_driver_status =findViewById(R.id.lnly_driver_status);

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

        tv_admin_id =findViewById(R.id.tv_admin_id);
        tv_admin_name =findViewById(R.id.tv_admin_name);
        tv_admin_email =findViewById(R.id.tv_admin_email);
        tv_admin_office =findViewById(R.id.tv_admin_office);
        tv_admin_phone =findViewById(R.id.tv_admin_phone);


        if (hakAkses.equals("admin")){
            lnly_driver_detail.setVisibility(View.VISIBLE);
            lnly_admin_status.setVisibility(View.VISIBLE);
            lnly_admin_detail.setVisibility(View.GONE);
            lnly_driver_status.setVisibility(View.GONE);
        }else {
            lnly_driver_detail.setVisibility(View.GONE);
            lnly_admin_status.setVisibility(View.GONE);
            lnly_admin_detail.setVisibility(View.VISIBLE);
            lnly_driver_status.setVisibility(View.VISIBLE);
        }


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
            case "on the way":
                rdb_otw.setChecked(true);
                break;
            case "arrive":
                rdb_arrive.setChecked(true);
                break;
            case "problem":
                rdb_problem.setChecked(true);
                break;
            case "back to base":
                rdb_back_to_base.setChecked(true);
                break;


        }

        btn_tracking.setOnClickListener(view -> {
            if (!dataDriver.getNama().isEmpty()){
                Gson gson1 = new Gson();
                String jsonDriver = gson1.toJson(dataDriver);
                Intent i = new Intent(this, TrackingDriverActivity.class)
                        .putExtra("jsonSchedule",getIntent().getStringExtra("json"))
                        .putExtra("jsonDriver",jsonDriver);
                startActivity(i);
            }else {
                Toast.makeText(obj, "Data Driver gagal di unduh silahkan refresh ulang", Toast.LENGTH_SHORT).show();
            }
        });

        btn_message.setOnClickListener(view -> {
            if (!dataDriver.getNama().isEmpty()){
                String phone =dataDriver.getPhone();
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", phone);
                smsIntent.putExtra("sms_body", "");
                this.startActivity(Intent.createChooser(smsIntent, "SMS:"));
            }else {
                Toast.makeText(obj, "Data Admin gagal di unduh silahkan refresh ulang", Toast.LENGTH_SHORT).show();
            }
        });

        btn_call.setOnClickListener(view -> {
            if (!dataDriver.getNama().isEmpty()){
                String phone =dataDriver.getPhone();
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData( Uri.parse("tel:" + phone));
                startActivity(call);
            }else {
                Toast.makeText(obj, "Data Admin gagal di unduh silahkan refresh ulang", Toast.LENGTH_SHORT).show();
            }
        });

        btn_driver_status_submit.setOnClickListener(view -> {

//         "Waiting", "On The Way","Problem", "Arrive","Back to Base"
            String status="";

            switch (sp_status.getSelectedItemPosition()) {
                case 0:
                    status = "waiting";
                break;
                case 1:
                    status ="on the way";
                    break;
                case 2 :
                    status ="problem";
                    break;
                case 3:
                    status="arrive";
                break;
                case 4:
                    status="back to base";
                    break;
            }


            updateStatus(dataSchedule.getId(),status);
        });


        if (hakAkses.equals("admin")){
            getRecordUser(dataSchedule.getId_driver());
        }else {
            getRecordUser(dataSchedule.getCreateby());
        }


        super.executeOutbox();
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
            if (hakAkses.equals("admin")){
                getRecordUser(dataSchedule.getId_driver());
            }else {
                getRecordUser(dataSchedule.getCreateby());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean getRecordUser(String id) {
        boolean[] a = {false};
        new AsyncTask<Void, Void, Boolean>() {
            Date dStart = null;
            Date dEnd = null;
            ProgressDialog dialog =new ProgressDialog(ScheduleDetailActivityDriver.this);

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


                try (Response response = new Api(ScheduleDetailActivityDriver.this).
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
                                if (hakAkses.equals("admin")){
                                    tv_driver_name.setText(user.getData().get(0).getNama());
                                    tv_driver_phone.setText(user.getData().get(0).getPhone());
                                    tv_driver_email.setText(user.getData().get(0).getEmail());
                                    tv_driver_car.setText(user.getData().get(0).getCar());
                                    tv_driver_police_number.setText(user.getData().get(0).getPolice_number());
                                }else {
                                    tv_admin_id.setText(user.getData().get(0).getId());
                                    tv_admin_name .setText(user.getData().get(0).getNama());
                                    tv_admin_email.setText(user.getData().get(0).getEmail());
                                    tv_admin_office.setText(user.getData().get(0).getOffice());
                                    tv_admin_phone.setText(user.getData().get(0).getPhone());

                                }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
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
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                    + " not granted", Toast.LENGTH_SHORT).show();
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



    public boolean updateStatus(String id,String status) {

        boolean[] a = {false};
        new AsyncTask<Void, Void, Boolean>() {

            ProgressDialog dialog =new ProgressDialog(ScheduleDetailActivityDriver.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("Loading Update data...");

                obj.runOnUiThread(new Runnable() {
                    public void run() {
                        dialog.show();
                    }
                });
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean b=false;

                FormBody.Builder formBody = new FormBody.Builder()
                        .add("id", id.trim())
                        .add("status", status)
                        .add("action", "PUTSTATUS");
                try (Response response = new Api(ScheduleDetailActivityDriver.this).
                        post(getString(R.string.api_schedule),formBody)) {

                    if (response == null || !response.isSuccessful())
                        throw new IOException("Unexpected code = " + response);

                    String responseBodyString = response.body().string();
                    JSONObject responseBodyObject = new JSONObject(responseBodyString);
                    if (responseBodyObject.getBoolean("status")) {

                        obj.runOnUiThread(new Runnable() {

                            public void run() {

                                dataSchedule.setStatus(status);

                                Toast.makeText(obj, "Berhasil diupdate", Toast.LENGTH_SHORT).show();
//                                getRecordSchedule();
                                if (dialog!=null&dialog.isShowing()){
                                    dialog.dismiss();
                                }
                            }
                        });
                    }else {
                        obj.runOnUiThread(new Runnable() {
                            public void run() {
                                if (responseBodyObject.has("message")){
                                    String message="";
                                    try {
                                        message=responseBodyObject.getString("message");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(obj, message, Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(obj, "Error Respon false", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    obj.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(obj, "Terjadi Respon error server", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                } catch (JSONException e) {
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

    private SupportMapFragment getMapFragment() {
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    /**
     * Initializes HERE Maps and HERE Positioning. Called after permission check.
     */
    private void initializeMapsAndPositioning() {
        RelativeLayout rlly_driver_positioning= findViewById(R.id.rlly_driver_positioning);
        rlly_driver_positioning.setVisibility(View.VISIBLE);
        mLocationInfo = (TextView) findViewById(R.id.textViewLocationInfo);
        mapFragment = getMapFragment();
        mapFragment.setRetainInstance(false);

        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(getApplicationContext().getExternalFilesDir(null) + File.separator + ".here-maps", "map_intent"); /* ATTENTION! Do not forget to update {YOUR_INTENT_NAME} */
        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
        } else {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE) {
                        map = mapFragment.getMap();
                        map.setZoomLevel(map.getMaxZoomLevel() - 1.7);
                        map.addTransformListener(ScheduleDetailActivityDriver.this);
                        mPositioningManager = PositioningManager.getInstance();
                        mHereLocation = LocationDataSourceHERE.getInstance(
                                new StatusListener() {
                                    @Override
                                    public void onOfflineModeChanged(boolean offline) {
                                        // called when offline mode changes
                                    }

                                    @Override
                                    public void onAirplaneModeEnabled() {
                                        // called when airplane mode is enabled
                                    }

                                    @Override
                                    public void onWifiScansDisabled() {
                                        // called when Wi-Fi scans are disabled
                                    }

                                    @Override
                                    public void onBluetoothDisabled() {
                                        // called when Bluetooth is disabled
                                    }

                                    @Override
                                    public void onCellDisabled() {
                                        // called when Cell radios are switch off
                                    }

                                    @Override
                                    public void onGnssLocationDisabled() {
                                        // called when GPS positioning is disabled
                                    }

                                    @Override
                                    public void onNetworkLocationDisabled() {
                                        // called when network positioning is disabled
                                    }

                                    @Override
                                    public void onServiceError(ServiceError serviceError) {
                                        // called on HERE service error
                                    }

                                    @Override
                                    public void onPositioningError(PositioningError positioningError) {
                                        // called when positioning fails
                                    }

                                    @Override
                                    public void onWifiIndoorPositioningNotAvailable() {
                                        // called when running on Android 9.0 (Pie) or newer
                                    }
                                });
                        if (mHereLocation == null) {
                            Toast.makeText(ScheduleDetailActivityDriver.this, "LocationDataSourceHERE.getInstance(): failed, exiting", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        mPositioningManager.setDataSource(mHereLocation);
                        mPositioningManager.addListener(new WeakReference<PositioningManager.OnPositionChangedListener>(
                                ScheduleDetailActivityDriver.this));
                        // start position updates, accepting GPS, network or indoor positions
                        if (mPositioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR)) {
                            mapFragment.getPositionIndicator().setVisible(true);
                        } else {
                            Toast.makeText(ScheduleDetailActivityDriver.this, "PositioningManager.start: failed, exiting", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(ScheduleDetailActivityDriver.this, "onEngineInitializationCompleted: error: " + error + ", exiting", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }


    @Override
    public void onPositionUpdated(final PositioningManager.LocationMethod locationMethod, final GeoPosition geoPosition, final boolean mapMatched) {
        final GeoCoordinate coordinate = geoPosition.getCoordinate();
        if (mTransforming) {
            mPendingUpdate = new Runnable() {
                @Override
                public void run() {
                    onPositionUpdated(locationMethod, geoPosition, mapMatched);
                }
            };
        } else {
            map.setCenter(coordinate, Map.Animation.BOW);
            updateLocationInfo(locationMethod, geoPosition);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPositioningManager != null) {
            mPositioningManager.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPositioningManager != null) {
            mPositioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
        }
    }

    @Override
    public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {
        // ignored
    }

    @Override
    public void onMapTransformStart() {
        mTransforming = true;
    }

    @Override
    public void onMapTransformEnd(MapState mapState) {
        mTransforming = false;
        if (mPendingUpdate != null) {
            mPendingUpdate.run();
            mPendingUpdate = null;
        }
    }

    private void updateLocationInfo(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition) {
        if (mLocationInfo == null) {
            return;
        }
        final StringBuffer sb = new StringBuffer();
        final GeoCoordinate coord = geoPosition.getCoordinate();
        sb.append("Type: ").append(String.format(Locale.US, "%s\n", locationMethod.name()));
        sb.append("Coordinate:").append(String.format(Locale.US, "%.6f, %.6f\n", coord.getLatitude(), coord.getLongitude()));


        if (latitudeOld !=0d&&longitudeOld!=0d){
            double meter = Util.distance(latitudeOld, coord.getLatitude(), longitudeOld, coord.getLongitude(),altitudeOld,coord.getAltitude());

            if (meter>15 &&
                    !dataSchedule.getStatus().equals("waiting") &&
                    !dataSchedule.getStatus().equals("arrive") &&
                    !dataSchedule.getStatus().equals("problem")
                    ){
                FormBody.Builder formBody = new FormBody.Builder()
                        .add("action", "POST")
                        .add("id_schedule", dataSchedule.getId())
                        .add("lat", String.valueOf(coord.getLatitude()))
                        .add("longi", String.valueOf(coord.getLongitude()))
                        .add("createdate", Util.getCurrentDate("yyyy-MM-dd HH:mm:ss"))
                        .add("createby", Util.getSharedPreferenceString(this, Constant.PREFS_IS_USER_ID, "-1"));

                OutboxDB outbox = new OutboxDB("gps post", getString(R.string.api_schedule_route), Api.requestBodyToString(formBody.build()), OutboxDB.FLAG_METHOD_POST, Util.getCurrentDate("yyyy-MM-dd HH:mm:ss"), "",
                        OutboxDB.FLAG_INIT, Util.getSharedPreferenceString(this, Constant.PREFS_IS_USER_ID, "-1"));
                outbox.save();
            }

            super.executeOutbox();
        }

        latitudeOld = coord.getLatitude();
        longitudeOld = coord.getLongitude();
        altitudeOld = coord.getAltitude();
        if (coord.getAltitude() != GeoCoordinate.UNKNOWN_ALTITUDE) {
            sb.append("Altitude:").append(String.format(Locale.US, "%.2fm\n", coord.getAltitude()));
        }
        if (geoPosition.getHeading() != GeoPosition.UNKNOWN) {
            sb.append("Heading:").append(String.format(Locale.US, "%.2f\n", geoPosition.getHeading()));
        }
        if (geoPosition.getSpeed() != GeoPosition.UNKNOWN) {
            sb.append("Speed:").append(String.format(Locale.US, "%.2fm/s\n", geoPosition.getSpeed()));
        }
        if (geoPosition.getBuildingName() != null) {
            sb.append("Building: ").append(geoPosition.getBuildingName());
            if (geoPosition.getBuildingId() != null) {
                sb.append(" (").append(geoPosition.getBuildingId()).append(")\n");
            } else {
                sb.append("\n");
            }
        }
        if (geoPosition.getFloorId() != null) {
            sb.append("Floor: ").append(geoPosition.getFloorId()).append("\n");
        }




        sb.deleteCharAt(sb.length() - 1);
        mLocationInfo.setText(sb.toString());


        //

        triggerRevGeocodeRequest(coord);

    }


    private void triggerRevGeocodeRequest(GeoCoordinate coordinate ) {
//        m_resultTextView.setText("");
        /* Create a ReverseGeocodeRequest object with a GeoCoordinate. */
//        GeoCoordinate coordinate = new GeoCoordinate(49.25914, -123.00777);
        ReverseGeocodeRequest2 revGecodeRequest = new ReverseGeocodeRequest2(coordinate);
        revGecodeRequest.execute(new ResultListener<Location>() {
            @Override
            public void onCompleted(Location location, ErrorCode errorCode) {
                if (errorCode == ErrorCode.NONE) {
                    /*
                     * From the location object, we retrieve the address and display to the screen.
                     * Please refer to HERE Android SDK doc for other supported APIs.
                     */

                    Toast.makeText(ScheduleDetailActivityDriver.this, location.getAddress().getText(), Toast.LENGTH_SHORT).show();

//                    updateTextView(location.getAddress().toString());
                } else {
                    Toast.makeText(ScheduleDetailActivityDriver.this, "not get location info", Toast.LENGTH_SHORT).show();

//                    updateTextView("ERROR:RevGeocode Request returned error code:" + errorCode);
                }
            }
        });
    }
}
