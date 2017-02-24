package com.example.chaiche.note;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class UserSetActivity extends AppCompatActivity {

    private static final String sharedpreference_userset_data = "USERSET_DATE";
    private SharedPreferences settings;

    String password ="",password_again="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_set);

        initView();
    }

    @Override
    public void onResume(){
        super.onResume();
        readData();
    }

    Switch switch_enablepassword;
    LinearLayout lin_enablepassword;

    TextView txv_enablepassword;

    ImageView igv_enablepassword_1,igv_enablepassword_2,igv_enablepassword_3,igv_enablepassword_4;

    Toolbar toolbar;

    public void initView(){

        toolbar = (Toolbar)findViewById(R.id.user_set_activity_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("使用者設定");

        switch_enablepassword = (Switch)findViewById(R.id.user_set_activity_switch_enablepassword);
        switch_enablepassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                init_enablepassword();
                if(isChecked){
                    lin_enablepassword.setVisibility(View.VISIBLE);

                }
                else{
                    saveData();
                    lin_enablepassword.setVisibility(View.GONE);
                }

            }
        });

        lin_enablepassword = (LinearLayout)findViewById(R.id.user_set_lin_enablepassword);
        lin_enablepassword.setVisibility(View.GONE);

        txv_enablepassword = (TextView)findViewById(R.id.user_set_activity_txv_enablepassword);

        igv_enablepassword_1 = (ImageView)findViewById(R.id.user_set_activity_igv_enablepassword_1);
        igv_enablepassword_2 = (ImageView)findViewById(R.id.user_set_activity_igv_enablepassword_2);
        igv_enablepassword_3 = (ImageView)findViewById(R.id.user_set_activity_igv_enablepassword_3);
        igv_enablepassword_4 = (ImageView)findViewById(R.id.user_set_activity_igv_enablepassword_4);
    }


    public void readData(){
        settings = getSharedPreferences(sharedpreference_userset_data,0);
        String password = settings.getString("enablepassword","");
        if(!password.equals(""))
            switch_enablepassword.setChecked(true);
    }
    public void saveData(){
        settings = getSharedPreferences(sharedpreference_userset_data,0);
        settings.edit()
                .putString("enablepassword", password)
                .commit();
    }



    public void init_enablepassword(){
        password ="";
        password_again="";
        txv_enablepassword.setText("輸入新密碼");
        igv_enablepassword_1.setBackground(null);
        igv_enablepassword_2.setBackground(null);
        igv_enablepassword_3.setBackground(null);
        igv_enablepassword_4.setBackground(null);
    }

    public void txv_password_click(View v){

        int password_length = password.length();
        if(password_length<4){
            password+=((TextView)v).getText().toString();
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
                            UserSetActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txv_enablepassword.setText("再次確認密碼");
                                    igv_enablepassword_1.setBackground(null);
                                    igv_enablepassword_2.setBackground(null);
                                    igv_enablepassword_3.setBackground(null);
                                    igv_enablepassword_4.setBackground(null);
                                }
                            });
                        }
                    }).start();
                    break;
            }
        }
        else if(password.length() == 4){
            

            int password_again_length = password_again.length();

            password_again+=((TextView)v).getText().toString();
            Drawable drawable = getDrawable(R.drawable.cast_expanded_controller_seekbar_thumb);
            switch (password_again_length){
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
                            UserSetActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(password.equals(password_again)){
                                        saveData();
                                        Toast.makeText(UserSetActivity.this,"成功更改密碼",Toast.LENGTH_SHORT).show();
                                        init_enablepassword();
                                    }
                                    else{
                                        init_enablepassword();
                                        txv_enablepassword.setText("輸入錯誤 重新輸入新密碼");
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

