package travel.maptracking.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import travel.maptracking.R;
import travel.maptracking.util.BaseAppCompatActivity;


public class FlashActivity extends BaseAppCompatActivity {
    private static final int REQUEST_STORAGE = 123;
    private final int SPLASH_DISPLAY_LENGTH = 3000;

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
        setContentView(R.layout.activity_flash);

        TextView tv_version=findViewById(R.id.tv_version);



        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            tv_version.setText("v"+version+"-"+verCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(FlashActivity.this ,LoginActivity.class);
                FlashActivity.this.startActivity(mainIntent);
                FlashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }




}

