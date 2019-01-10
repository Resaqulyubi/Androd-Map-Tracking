package travel.maptracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Response;
import travel.maptracking.R;
import travel.maptracking.model.schedule;
import travel.maptracking.model.user;
import travel.maptracking.network.Api;
import travel.maptracking.util.BaseAppCompatActivity;
import travel.maptracking.util.Constant;
import travel.maptracking.util.Util;

import static travel.maptracking.util.Constant.default_simpledateFormat;

public class ScheduleAddActivity extends BaseAppCompatActivity {
    private android.support.v7.widget.Toolbar toolbar;
    private ScheduleAddActivity obj;
    schedule.Data dataSchedule;
    user.Data dataDriver =new user().getDataItem();

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
        setContentView(R.layout.activity_schedule_add);


       MaterialEditText tv_id_pemesanan = findViewById(R.id.tv_id_pemesanan);
       MaterialEditText tv_customer_id = findViewById(R.id.tv_customer_id);
       MaterialEditText tv_customer_name = findViewById(R.id.tv_customer_name);
       MaterialEditText tv_customer_email = findViewById(R.id.tv_customer_email);
       MaterialEditText tv_customer_phone = findViewById(R.id.tv_customer_phone);
       MaterialEditText tv_customer_address = findViewById(R.id.tv_customer_address);
       MaterialEditText tv_customer_payment_code = findViewById(R.id.tv_customer_payment_code);
       MaterialEditText tv_customer_paket = findViewById(R.id.tv_customer_paket);
       MaterialEditText tv_schedule_pick_poin = findViewById(R.id.tv_schedule_pick_poin);
       MaterialEditText tv_schedule_pick_time = findViewById(R.id.tv_schedule_pick_time);
       MaterialEditText tv_schedule_destination = findViewById(R.id.tv_schedule_destination);
       MaterialEditText tv_schedule_rooute = findViewById(R.id.tv_schedule_rooute);
       MaterialEditText tv_schedule_arrival = findViewById(R.id.tv_schedule_arrival);
       Button btnContinue = findViewById(R.id.btnContinue);

       toolbar=findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
       getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       obj = this;

        btnContinue.setOnClickListener(view -> {
             String id =tv_id_pemesanan.getText().toString();
             String id_customer =tv_customer_id.getText().toString();
             String customer_name =tv_customer_name.getText().toString();
             String email=tv_customer_email.getText().toString();
             String phone=tv_customer_phone.getText().toString();
             String address =tv_customer_address.getText().toString();
             String payment_code=tv_customer_payment_code.getText().toString();
             String paket=tv_customer_paket.getText().toString();
             String pickup_point=tv_schedule_pick_poin.getText().toString();
             String pickup_time=tv_schedule_pick_time.getText().toString();
             String destination=tv_schedule_destination.getText().toString();
             String route=tv_schedule_rooute.getText().toString();
             String arrival=tv_schedule_arrival.getText().toString();
             String createby=Util.getSharedPreferenceString(this, Constant.PREFS_IS_USER_ID,"");
             String createdate=Util.getCurrentDate("yyyy-MM-dd HH:mm:ss");
             String status="waiting";
           if (!id.isEmpty()||
                   !id_customer.isEmpty()||
                   !customer_name.isEmpty()||
                   !email.isEmpty()||
                   !phone.isEmpty()||
                   !address.isEmpty()||
                   !payment_code.isEmpty()||
                   !paket.isEmpty()||
                   !pickup_point.isEmpty()||
                   !pickup_time.isEmpty()||
                   !destination.isEmpty()||
                   !route.isEmpty()||
                   !arrival.isEmpty()){


               schedule.Data dataSchedule = new schedule.Data();
               dataSchedule.setId(id);
               dataSchedule.setId_customer(id_customer);
               dataSchedule.setEmail(email);
               dataSchedule.setPhone(phone);
               dataSchedule.setCustomer_name(customer_name);
               dataSchedule.setAddress(address);
               dataSchedule.setPayment_code(payment_code);
               dataSchedule.setPaket(paket);
               dataSchedule.setPickup_point(pickup_point);
               dataSchedule.setPickup_time(pickup_time);
               dataSchedule.setDestination(destination);
               dataSchedule.setRoute(route);
               dataSchedule.setArrival(arrival);
               dataSchedule.setCreateby(createby);
               dataSchedule.setCreatedate(createdate);
               dataSchedule.setStatus(status);

               Gson gson1 = new Gson();
               String jsonSchedule = gson1.toJson(dataSchedule);
               Intent i = new Intent(this, ScheduleAddDriverActivity.class)
                       .putExtra("jsonSchedule",jsonSchedule);
               startActivityForResult(i,999);

           }else {
               Toast.makeText(obj, "Data masih ada yang belum terisi!", Toast.LENGTH_SHORT).show();
           }

        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==999 && resultCode==RESULT_OK){
            if (resultCode==RESULT_OK){
                finish();
            }

        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
