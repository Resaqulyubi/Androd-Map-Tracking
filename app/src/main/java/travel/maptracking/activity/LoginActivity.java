package travel.maptracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Response;
import travel.maptracking.R;
import travel.maptracking.model.user;
import travel.maptracking.network.Api;
import travel.maptracking.util.Constant;
import travel.maptracking.util.Util;

public class LoginActivity extends AppCompatActivity {
    private LoginActivity obj;
    Button btn_login;
    MaterialEditText met_email,met_password;
    Spinner sp_login;

    @Override
    protected void onResume() {

        if (Util.getSharedPreferenceBoolean(LoginActivity.this, Constant.PREFS_IS_LOGIN,false)){
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }


        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        obj = this;


        btn_login=findViewById(R.id.btn_login);
        met_email=findViewById(R.id.met_email);
        met_password=findViewById(R.id.met_password);
        sp_login=findViewById(R.id.sp_login);


        String[] arraySpinner = new String[]{"Admin", "Driver"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_login.setAdapter(arrayAdapter);



        btn_login.setOnClickListener(View->{

            if (met_email.getText().toString().isEmpty()||
                    met_password.getText().toString().isEmpty()){
                Toast.makeText(this, "Data Anda masih ada yang kosong!!", Toast.LENGTH_SHORT).show();
            }else {

                if (sp_login.getSelectedItemPosition()==0){
                    login(met_email.getText().toString(),met_password.getText().toString(),"admin");
                }else {
                    login(met_email.getText().toString(),met_password.getText().toString(),"driver");
                }


            }

        });

    }


    public boolean login(String username,String password, String hakakses) {
        boolean[] a = {false};

        new AsyncTask<Void, Void, Boolean>() {
            ProgressDialog dialog =new ProgressDialog(LoginActivity.this);
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
                httpUrlBuilder.addQueryParameter("hak_akses",hakakses);

                try (Response response = new Api(LoginActivity.this).
                        get(getString(R.string.api_user),httpUrlBuilder)) {
                    if (response == null || !response.isSuccessful())
                        throw new IOException("Unexpected code = " + response);

                    String responseBodyString = response.body().string();
                    Gson gson = new Gson();
                    user user = gson.fromJson(responseBodyString, user.class);

                    if (user.isStatus()) {

                        boolean ispass =false;
                        for (int i = 0; i < user.getData().size(); i++) {

                           if ((user.getData().get(i).getEmail().equals(username)||
                                   user.getData().get(i).getPhone().equals(username)||
                                   user.getData().get(i).getUsername().equals(username)) &&
                                           user.getData().get(i).getPassword().equals(password)){
                               iduser=user.getData().get(i).getId();
                               ispass=true;
                               break;

                           }

                        }

                        if (ispass){

                            obj.runOnUiThread(new Runnable() {
                                public void run() {
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class)
                                            .putExtra("kategori",sp_login.getSelectedItemPosition()));
                                    Util.setSharedPreference(LoginActivity.this,Constant.PREFS_IS_LOGIN,true);
                                    Util.setSharedPreference(LoginActivity.this,Constant.PREFS_IS_USER_ID,iduser);
                                    Util.setSharedPreference(LoginActivity.this,Constant.PREFS_IS_USER_AKSES,sp_login.getSelectedItemPosition()==0?"admin":"driver");
                                    finish();
                                }
                            });
                        }else {
                            obj.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(obj, "Tidak ada User yang ditemukan", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
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
}
