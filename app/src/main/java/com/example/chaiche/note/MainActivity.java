package com.example.chaiche.note;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String sharedpreference_userset_data = "USERSET_DATE";
    private SharedPreferences settings;

    String password="",enterpassword="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
    }

    TextView txv_enablepassword;
    ImageView igv_enablepassword_1,igv_enablepassword_2,igv_enablepassword_3,igv_enablepassword_4;

    public void initView(){

        txv_enablepassword = (TextView)findViewById(R.id.main_activity_txv_enablepassword);
        igv_enablepassword_1 = (ImageView)findViewById(R.id.main_activity_igv_enablepassword_1);
        igv_enablepassword_2 = (ImageView)findViewById(R.id.main_activity_igv_enablepassword_2);
        igv_enablepassword_3 = (ImageView)findViewById(R.id.main_activity_igv_enablepassword_3);
        igv_enablepassword_4 = (ImageView)findViewById(R.id.main_activity_igv_enablepassword_4);
    }

    @Override
    public void onResume(){
        super.onResume();

        readData();
    }

    public void readData() {
        settings = getSharedPreferences(sharedpreference_userset_data, 0);
        password = settings.getString("enablepassword", "");
        if (!password.equals("")){
        }
        else{
            goShowActivity();
        }
    }
    public void goShowActivity(){
        Intent it = new Intent(this,ShowActivity.class);
        startActivity(it);
        finish();
    }

    public void reset_enablepassword(){
        enterpassword = "";
        txv_enablepassword.setText("輸入錯誤 再次輸入");
        igv_enablepassword_1.setBackground(null);
        igv_enablepassword_2.setBackground(null);
        igv_enablepassword_3.setBackground(null);
        igv_enablepassword_4.setBackground(null);

    }
    public void txv_password_click(View v){

        int password_length = enterpassword.length();
        if(password_length<4){
            enterpassword+=((TextView)v).getText().toString();
            Drawable drawable = getDrawable(R.drawable.cast_expanded_controller_seekbar_thumb);
            switch (password_length){
                case 0:
                    igv_enablepassword_1.setBackground(drawable);
                    break;
                case 1:
                    igv_enablepassword_2.setBackground(drawable);
                    break;
                case 2:
                    igv_enablepassword_3.setBackground(drawable);
                    break;
                case 3:
                    igv_enablepassword_4.setBackground(drawable);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(enterpassword.equals(password)){
                                        goShowActivity();
                                    }
                                    else{
                                        reset_enablepassword();
                                    }
                                }
                            });
                        }
                    }).start();
                    break;
            }
        }

    }

}
