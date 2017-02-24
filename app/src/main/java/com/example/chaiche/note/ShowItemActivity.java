package com.example.chaiche.note;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Visibility;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.chaiche.note.ShowActivity.zoomImage;

public class ShowItemActivity extends AppCompatActivity implements ShowItem_Item_Fragment.OnItemSelectedListener,
        Showitem_web_Fragment.OnItemWebSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        ShowItem_map_Fragment.OnItemMapSelectedListener,
        ShowItem_text_Fragment.OnItemTextSelectedListener ,
        Add_DialogFragment.OnItem_Add_SelectedListener {

    String tb_name = "Note";

    DBHelper db = null;


    ShowItem_Item_Fragment item_frg = null;
    ShowItem_content_Fragment content_frg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_item);

        //Log.d("test",getIntent().getIntExtra("which_id",0)+"");
        tb_name += getIntent().getIntExtra("which_id", 0);
        //Log.d("test",getIntent().getStringExtra("which_name"));
        db = new DBHelper(this, tb_name);
        db.open_tb("(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "type INTEGER(2)," +
                "text TEXT," +
                "url TEXT," +
                "position VARCHAR(32)," +
                "date VARCHAR(64))");

        initView();


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);



    }

    @Override
    public void onResume(){
        super.onResume();

        checkPermissions();
    }

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    Toolbar toolbar;

    TabWidget tabWidget;
    View item_info, item_content;

    public void initView() {
        mViewPager = (ViewPager) findViewById(R.id.show_item_viewPager);
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);

        toolbar = (Toolbar) findViewById(R.id.show_item_toolbar);
        Bitmap bitmap_logo = BitmapFactory.decodeResource(getResources(), R.drawable.note);
        bitmap_logo = zoomImage(bitmap_logo, 300, 150);
        Drawable d = new BitmapDrawable(getResources(), bitmap_logo);
        toolbar.setLogo(d);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle(" -" + getIntent().getStringExtra("which_name"));
        toolbar.inflateMenu(R.menu.menu_layout);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_add) {
                    showAddDialog();
                } else if (item.getItemId() == R.id.menu_left) {
                    //finish();
                    finishAffinity();
                }
                return false;
            }
        });

        tabWidget = (TabWidget) findViewById(R.id.show_item_tabwidget);
        tabWidget.setStripEnabled(false);

        item_info = createView(R.drawable.item);
        item_info.setFocusable(true);
        tabWidget.addView(item_info);
        item_info.setOnClickListener(mTabClickListener);

        item_content = createView(R.drawable.content);
        item_content.setFocusable(true);
        tabWidget.addView(item_content);
        item_content.setOnClickListener(mTabClickListener);

        setalpha(0);

    }

    private View createView(int drawable) {
        View v = LayoutInflater.from(this).inflate(R.layout.tabwidget_item_layout, null);
        ImageView img = (ImageView) v.findViewById(R.id.tablewidget_item_layout_igv);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),drawable);
        bitmap = zoomImage(bitmap,60,60);
        img.setImageBitmap(bitmap);
        return v;
    }
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int)width, (int)height,matrix, true);

        return bitmap;
    }

    private View.OnClickListener mTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == item_info) {
                mViewPager.setCurrentItem(0);
            } else if (v == item_content) {
                mViewPager.setCurrentItem(1);
            }
        }
    };

    private void setalpha(int a) {
        switch (a) {
            case 0:
                item_info.setAlpha((float) 1);
                item_content.setAlpha((float) 0.3);
                break;
            case 1:
                item_info.setAlpha((float) 0.3);
                item_content.setAlpha((float) 1);
                break;
        }
    }


    public void setToolbarVisibility(Boolean bool) {
        if (bool) {
            toolbar.setVisibility(View.VISIBLE);
            tabWidget.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
            tabWidget.setVisibility(View.GONE);
        }
    }

    public void addData(String name, int type, String text, String url, String position, String time) {
        ContentValues cv = new ContentValues(4);
        cv.put("name", name);
        cv.put("url", url);
        cv.put("text", text);
        cv.put("type", type);
        cv.put("position", position);
        cv.put("date", time);
        db.addData(cv);

        item_frg.liv_adapter_notifyDataSetChanged();
    }

    public Cursor getData() {

        Cursor cs = db.getData();
        return cs;
    }

    public void updateData(String text) {
        ContentValues cv = new ContentValues(1);
        cv.put("text", text);
        db.update(item_frg.now_item_id, cv);

        item_frg.liv_adapter_notifyDataSetChanged();
    }

    public void deleteData(int id) {
        db.deleteData(id);
    }


    public void loadtext(String text) {
        mViewPager.setCurrentItem(1);
        content_frg.changeFragment(0);
        content_frg.text_loadtext(text);

    }

    public void loadurl(String url) {
        mViewPager.setCurrentItem(1);
        content_frg.changeFragment(1);
        content_frg.web_loadurl(url);
    }

    public void loadposition(String name,String pos) {
        mViewPager.setCurrentItem(1);
        content_frg.changeFragment(2);
        content_frg.map_loadpos(name,pos);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (item_frg == null) {
                        item_frg = new ShowItem_Item_Fragment();
                    }
                    return item_frg;
                case 1:
                    if (content_frg == null) {
                        content_frg = new ShowItem_content_Fragment();
                    }
                    return content_frg;
                default:
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            tabWidget.setCurrentTab(arg0);
            setalpha(arg0);

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public void showAddDialog(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        Add_DialogFragment newFragment = new Add_DialogFragment();
        newFragment.show(ft, "dialog");
    }


    @Override
    public void onPause(){
        super.onPause();

        if(mLocationManager!=null) {
            if (ActivityCompat.checkSelfPermission(ShowItemActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(ShowItemActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLocationManager.removeUpdates(mLocationListener);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (db != null)
            db.closedb();
    }

    //permission
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] permissionsArray = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public Boolean checkPermissions() {

        List<String> permissionsList = new ArrayList<String>();
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_PERMISSIONS);
            return false;
        } else {
            getCurrentLocation();
            return true;
        }

    }

    LocationManager mLocationManager = null;
    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;


    public String getCurrentLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;
        if (!(isGPSEnabled || isNetworkEnabled))
            Toast.makeText(this, "getCurrentLocation no gps", Toast.LENGTH_LONG).show();
        else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (location != null) {
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            Dialog_add_MapFragment.position = gps;
            return location.getLatitude() + ":" + location.getLongitude();
        }
        return "";
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("test", location.getLatitude() + ":" + location.getLongitude());
                LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
                Dialog_add_MapFragment.position = gps;
            } else {
                Log.d("null","Location");
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            getCurrentLocation();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){


            if(mViewPager.getCurrentItem() == 1){
                if(content_frg.getWhich_frg() == 1){
                    if(!content_frg.frg_web.goBack()){
                        mViewPager.setCurrentItem(0);
                    }
                }
                else{
                    mViewPager.setCurrentItem(0);
                }
            }
            else{
                finish();
            }

            return true;
        }


        return super.onKeyDown(keyCode, event);
    }
}
