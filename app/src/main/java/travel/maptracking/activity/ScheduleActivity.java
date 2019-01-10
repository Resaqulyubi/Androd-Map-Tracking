package travel.maptracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.common.ArrayListAccumulator;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Response;
import travel.maptracking.R;
import travel.maptracking.model.schedule;
import travel.maptracking.model.user;
import travel.maptracking.network.Api;
import travel.maptracking.util.BaseAppCompatActivity;
import travel.maptracking.util.Constant;
import travel.maptracking.util.Util;

public class ScheduleActivity extends BaseAppCompatActivity {
    private android.support.v7.widget.Toolbar toolbar;
    private ScheduleActivity obj;
    private AdapterSchedule adapter;
    private String hakAkses="";
    private String menu="";
    List<user.Data > dataDriver =new ArrayList<>();

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
        setContentView(R.layout.activity_schedule);

        toolbar = findViewById(R.id.toolbar);
        ListView lsvw_data = findViewById(R.id.lsvw_data);
        TextView tv_subtitle = findViewById(R.id.tv_subtitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//      getSupportActionBar().setTitle(R.string.halaman_notifikasi);
        obj = ScheduleActivity.this;

        hakAkses= getIntent().getStringExtra("akses");
        menu= getIntent().getStringExtra("menu");

        if (menu.equals("admin-trackingdriver")){
            tv_subtitle.setText("Driver");
        }

        adapter=new AdapterSchedule(this, position -> {
            Gson gson = new Gson();
            String json = gson.toJson(adapter.getItem(position));
            if (menu.equals("admin-trackingdriver")){
                if (dataDriver.size()>0){
                    user.Data data =null;

                    for (int i = 0; i < dataDriver.size(); i++) {
                        if (adapter.getItem(position).getId_driver().equals(dataDriver.get(i).getId())){
                            data =dataDriver.get(i);
                            break;
                        }
                    }

                    if (data!=null){
                        Gson gson1 = new Gson();
                        String jsonDriver = gson1.toJson(data);
                        Intent i = new Intent(this, TrackingDriverActivity.class)
                                .putExtra("jsonSchedule",json)
                                .putExtra("jsonDriver",jsonDriver);
                        startActivity(i);
                    }else {
                        Toast.makeText(obj, "Data Driver gagal di unduh silahkan refresh ulang", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(obj, "Data Driver gagal di unduh silahkan refresh ulang", Toast.LENGTH_SHORT).show();
                }
            }else {

                if (hakAkses.equals("driver")){
                    Intent i = new Intent(this, ScheduleDetailActivityDriver.class)
                            .putExtra("json",json);
                    startActivity(i);
                }else {

                    Intent i = new Intent(this, ScheduleDetailActivity.class)
                            .putExtra("json",json);
                    startActivity(i);
                }

            }


        });
        lsvw_data.setAdapter(adapter);
       getRecordSchedule();


        SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRecordSchedule();


                pullToRefresh.setRefreshing(false);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (hakAkses.equals("admin") && !this.menu.equals("admin-trackingdriver")) {
            inflater.inflate(R.menu.add, menu);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_add) {
            //process your onClick here

            Intent i = new Intent(this, ScheduleAddActivity.class);
            startActivityForResult(i,999);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean getRecordSchedule() {
        final boolean[] a = {false};
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        new AsyncTask<Void, Void, Boolean>() {
            Date dStart = null;
            Date dEnd = null;

//            https://www.studytutorial.in/android-line-chart-or-line-graph-using-mpandroid-library-tutorial
//            List<TransactionDB> transactions = new ArrayList<>();

            ProgressDialog dialog =new ProgressDialog(ScheduleActivity.this);

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

                if (hakAkses.equals("driver")){
                    httpUrlBuilder.addQueryParameter("id_driver",Util.getSharedPreferenceString(ScheduleActivity.this, Constant.PREFS_IS_USER_ID,""));
                }

                SimpleDateFormat ft = new SimpleDateFormat (Constant.default_simpledate2);


                try (Response response = new Api(ScheduleActivity.this).
                        get(getString(R.string.api_schedule),httpUrlBuilder)) {
                    if (response == null || !response.isSuccessful())
                        throw new IOException("Unexpected code = " + response);

                    String responseBodyString = response.body().string();
                    JSONObject responseBodyObject = new JSONObject(responseBodyString);

                    List<schedule.Data> data =new ArrayList<>();
                    Gson gson = new Gson();
                    schedule schedule = gson.fromJson(responseBodyString, schedule.class);

                    if (schedule.isStatus()) {

                        for (int x = 0; x < schedule.getData().size(); x++) {

                            data.add(schedule.getData().get(x));
                            b =true;
                        }


                        obj.runOnUiThread(new Runnable() {

                            public void run() {
                                adapter.setList(data);


                            }
                        });
                    }else {
                        obj.runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.setList(data);
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

                if (aVoid){
                    if (hakAkses.equals("admin") && menu.equals("admin-trackingdriver")) {
                        getRecordUser();
                    }else {
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        return a[0];

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==999 && resultCode ==RESULT_OK){
            getRecordSchedule();

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean getRecordUser() {
        boolean[] a = {false};
        new AsyncTask<Void, Void, Boolean>() {
            Date dStart = null;
            Date dEnd = null;
            ProgressDialog dialog =new ProgressDialog(ScheduleActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("Loading get data driver...");
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

                SimpleDateFormat ft = new SimpleDateFormat (Constant.default_simpledate2);


                try (Response response = new Api(ScheduleActivity.this).
                        get(getString(R.string.api_user),httpUrlBuilder)) {
                    if (response == null || !response.isSuccessful())
                        throw new IOException("Unexpected code = " + response);

                    String responseBodyString = response.body().string();
                    Gson gson = new Gson();
                    user user = gson.fromJson(responseBodyString, user.class);

                    if (user.isStatus()&&user.getData().size()>0) {
                        dataDriver=user.getData();
                        adapter.setDataDriver(dataDriver);

                    }else {
                        obj.runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.setDataDriver(dataDriver);
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

                adapter. notifyDataSetChanged();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        return  a[0];

    }


}
