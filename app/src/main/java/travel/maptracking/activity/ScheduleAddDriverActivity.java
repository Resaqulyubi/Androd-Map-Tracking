package travel.maptracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Response;
import travel.maptracking.R;
import travel.maptracking.model.schedule;
import travel.maptracking.model.user;
import travel.maptracking.network.Api;
import travel.maptracking.util.BaseAppCompatActivity;
import travel.maptracking.util.Constant;
import travel.maptracking.util.Util;

public class ScheduleAddDriverActivity extends BaseAppCompatActivity {
    private android.support.v7.widget.Toolbar toolbar;
    private ScheduleAddDriverActivity obj;
    private List<user.Data> dataDriver =new ArrayList<>();
    private schedule.Data dataSchedule;
    private Spinner sp_driver_id;
    private MaterialEditText tv_driver_name, tv_driver_phone, tv_driver_email,tv_driver_car,tv_driver_police_number;

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
        setContentView(R.layout.activity_schedule_add_driver);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp_driver_id = findViewById(R.id.sp_driver_id);
        tv_driver_name = findViewById(R.id.tv_driver_name);
        tv_driver_phone = findViewById(R.id.tv_driver_phone);
        tv_driver_email = findViewById(R.id.tv_driver_email);
        tv_driver_car = findViewById(R.id.tv_driver_car);
        tv_driver_police_number = findViewById(R.id.tv_driver_police_number);
       Button btnSave = findViewById(R.id.btnSave);

        obj = this;
        String extra = getIntent().getStringExtra("jsonSchedule");

        Gson gson = new Gson();
        TypeToken<schedule.Data> token = new TypeToken<schedule.Data>() {};
        dataSchedule = gson.fromJson(extra, token.getType());

        btnSave.setOnClickListener(view -> {

            if (!tv_driver_name.getText().toString().isEmpty()){
                dataSchedule.setId_driver(sp_driver_id.getSelectedItem().toString());
                addSchedule(dataSchedule.getId(),dataSchedule.getStatus(),dataSchedule.getId_driver(),dataSchedule.getId_customer(),dataSchedule.getCustomer_name(),dataSchedule.getEmail(),
                        dataSchedule.getPhone(),dataSchedule.getAddress(),dataSchedule.getPayment_code(),dataSchedule.getPaket(),dataSchedule.getPickup_point(),dataSchedule.getPickup_time(),dataSchedule.getDestination(),dataSchedule.getRoute(),dataSchedule.getArrival(),dataSchedule.getCreateby(),dataSchedule.getCreatedate());
            }else {
                Toast.makeText(obj, "Data belum terisi harap refresh", Toast.LENGTH_SHORT).show();
            }


        });

        getDriver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_refresh, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            //process your onClick here

            getDriver();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean getDriver() {
        boolean[] a = {false};

        new AsyncTask<Void, Void, Boolean>() {
            ProgressDialog dialog =new ProgressDialog(ScheduleAddDriverActivity.this);
            String iduser="";
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("checking user...");

                obj.runOnUiThread(new Runnable() {
                    public void run() {
                        dialog.show();
                    }
                });
            }
            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean b=false;
                HttpUrl.Builder httpUrlBuilder = new HttpUrl.Builder();
                httpUrlBuilder.addQueryParameter("hak_akses","driver");

                try (Response response = new Api(ScheduleAddDriverActivity.this).
                        get(getString(R.string.api_user),httpUrlBuilder)) {
                    if (response == null || !response.isSuccessful())
                        throw new IOException("Unexpected code = " + response);

                    String responseBodyString = response.body().string();
                    Gson gson = new Gson();
                    user user = gson.fromJson(responseBodyString, user.class);

                    if (user.isStatus() && user.getData().size() > 0) {
                        obj.runOnUiThread(new Runnable() {
                            public void run() {

                                List<String> data=new ArrayList<>();
                                dataDriver = user.getData();
                                for (int i = 0; i < dataDriver.size() ; i++) {
                                    data.add(dataDriver.get(i).getId());
                                }

                                tv_driver_name.setText(dataDriver.get(0).getNama());
                                tv_driver_phone.setText(dataDriver.get(0).getPhone());
                                tv_driver_email.setText(dataDriver.get(0).getEmail());
                                tv_driver_car.setText(dataDriver.get(0).getCar());
                                tv_driver_police_number.setText(dataDriver.get(0).getPolice_number());

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ScheduleAddDriverActivity.this,
                                        android.R.layout.simple_spinner_item, data);
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_driver_id.setAdapter(arrayAdapter);

                                // Set an item selection listener for spinner widget
                                sp_driver_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        // Set the value for selected index variable

                                        for (int j = 0; j < dataDriver.size(); j++) {
                                            if (adapterView.getAdapter().getItem(i).toString().equals(dataDriver.get(j).getId())) {

                                                tv_driver_name.setText(dataDriver.get(j).getNama());
                                                tv_driver_phone.setText(dataDriver.get(j).getPhone());
                                                tv_driver_email.setText(dataDriver.get(j).getEmail());
                                                tv_driver_car.setText(dataDriver.get(j).getCar());
                                                tv_driver_police_number.setText(dataDriver.get(j).getPolice_number());

                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                        });
                    }else {
                        obj.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(obj, "Tidak ada User yang ditemukan", Toast.LENGTH_SHORT).show();
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

    public boolean addSchedule(String id, String status, String id_driver, String id_customer, String customer_name, String email,
                               String phone, String address, String payment_code, String paket, String pickup_point, String pickup_time, String destination, String route, String arrival, String createby, String createdate) {

        boolean[] a = {false};
        new AsyncTask<Void, Void, Boolean>() {

            ProgressDialog dialog =new ProgressDialog(ScheduleAddDriverActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("Loading insert data...");

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
                        .add("id_driver", id_driver)
                        .add("id_customer", id_customer)
                        .add("customer_name", customer_name)
                        .add("email", email)
                        .add("phone", phone)
                        .add("address", address)
                        .add("payment_code", payment_code)
                        .add("paket", paket)
                        .add("pickup_point", pickup_point)
                        .add("pickup_time", pickup_time)
                        .add("destination", destination)
                        .add("route", route)
                        .add("arrival", arrival)
                        .add("createby", createby)
                        .add("createdate", createdate)
                        .add("status", status)
                        .add("action", "POST");
                try (Response response = new Api(ScheduleAddDriverActivity.this).
                        post(getString(R.string.api_schedule),formBody)) {

                    if (response == null || !response.isSuccessful())
                        throw new IOException("Unexpected code = " + response);

                    String responseBodyString = response.body().string();
                    JSONObject responseBodyObject = new JSONObject(responseBodyString);
                    if (responseBodyObject.getBoolean("status")) {

                        obj.runOnUiThread(new Runnable() {

                            public void run() {
                                Toast.makeText(obj, "Berhasil ditambahkan", Toast.LENGTH_SHORT).show();
//                                getRecordSchedule();
                                if (dialog!=null&dialog.isShowing()){
                                    dialog.dismiss();
                                }

                                Intent returnIntent = new Intent();
                                setResult(RESULT_OK, returnIntent);
                                finish();

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

}
