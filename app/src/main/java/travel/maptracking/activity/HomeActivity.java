package travel.maptracking.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.GridLayout;

import travel.maptracking.R;
import travel.maptracking.services.OutBoxService;
import travel.maptracking.util.Constant;
import travel.maptracking.util.Util;

public class HomeActivity extends AppCompatActivity {

    GridLayout grid_admin,grid_driver;
    CardView card_admin_schedule,card_admin_tracking,card_driver_schedule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        grid_admin=findViewById(R.id.grid_admin);
        grid_driver=findViewById(R.id.grid_driver);
        card_admin_tracking=findViewById(R.id.card_admin_tracking);
        card_admin_schedule=findViewById(R.id.card_admin_schedule);
        card_driver_schedule=findViewById(R.id.card_driver_schedule);

       if (Util.getSharedPreferenceString(this, Constant.PREFS_IS_USER_AKSES,"").equals("driver")){
           grid_admin.setVisibility(View.GONE);
           grid_driver.setVisibility(View.VISIBLE);

       }else {
           grid_admin.setVisibility(View.VISIBLE);
           grid_driver.setVisibility(View.GONE);
       }



        card_admin_schedule.setOnClickListener(View ->{
            Intent i = new Intent(HomeActivity.this, ScheduleActivity.class)
                    .putExtra("akses","admin");
            startActivity(i);
        });

        card_driver_schedule.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, ScheduleActivity.class)
                    .putExtra("akses","driver");
            startActivity(i);
        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        showdialogKeluar();
    }
    public void showdialogKeluar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Apakah anda ingin Keluar dari aplikasi ?");
        builder.setPositiveButton("YA", (dialogInterface, i) -> {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                this.startService(new Intent(this, OutBoxService.class).setAction(Constant.ACTION_STOP_OUTBOX));
                Util.setSharedPreference(this,Constant.PREFS_IS_LOGIN,false);
                System.exit(0);
                finish();
            }, 1000);

        });
        builder.setNegativeButton("TIDAK", (dialogInterface, i) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
