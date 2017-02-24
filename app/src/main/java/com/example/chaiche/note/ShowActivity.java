package com.example.chaiche.note;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Matrix;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowActivity extends AppCompatActivity {

    DBHelper db = null;

    static final String tb_name = "Note";

    ArrayList<Info> arrayList;
    ListAdapter myAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        initView();

        arrayList = new ArrayList<>();

        db = new DBHelper(this,tb_name);
        db.open_tb("(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(32) not null)");
        getData();

        myAdapter = new ListAdapter(this,arrayList);
        liv.setAdapter(myAdapter);


    }


    ListView liv;
    Toolbar toolbar;

    public void initView(){
        liv = (ListView)findViewById(R.id.show_liv);
        liv.setOnItemClickListener(liv_adapter_click);
        liv.setOnItemLongClickListener(liv_adapter_longclick);


        toolbar = (Toolbar) findViewById(R.id.show_toolbar);
        Bitmap bitmap_logo = BitmapFactory.decodeResource(getResources(), R.drawable.note);
        bitmap_logo = zoomImage(bitmap_logo,300,150);
        Drawable d = new BitmapDrawable(getResources(), bitmap_logo);
        toolbar.setLogo(d);
        toolbar.inflateMenu(R.menu.menu_layout);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.menu_add){
                    showAddDialog();
                }
                else if(item.getItemId()==R.id.menu_userset){
                    Intent it = new Intent(ShowActivity.this,UserSetActivity.class);
                    startActivity(it);
                }
                else if(item.getItemId()==R.id.menu_left){
                    finish();
                }
                return false;
            }
        });
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

    public void addData(String name){
        ContentValues cv = new ContentValues(1);
        cv.put("name",name);
        db.addData(cv);
    }
    public void getData(){

        arrayList.clear();

        Cursor cs = db.getData();
        int count = cs.getCount();
        if(count != 0) {
            cs.moveToFirst();
            for(int i=0; i<count; i++) {
                int id = cs.getInt(0);
                String name = cs.getString(1);
                Info info = new Info(id,name);
                arrayList.add(info);
                cs.moveToNext();
            }
        }
    }



    public class ListAdapter extends BaseAdapter{

        LayoutInflater lif;
        ArrayList<Info> arrayList;

        public ListAdapter(Context context,ArrayList<Info> arrayList){
            lif = LayoutInflater.from(context);
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = lif.inflate(R.layout.showlist_layout,null);
            TextView txv = (TextView)convertView.findViewById(R.id.showlist_layout_txv);
            txv.setText(arrayList.get(position).name);
            return convertView;
        }
    }

    private AdapterView.OnItemClickListener liv_adapter_click = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent it = new Intent();
            it.setClass(ShowActivity.this,ShowItemActivity.class);
            it.putExtra("which_id",arrayList.get(position).id);
            it.putExtra("which_name",arrayList.get(position).name);
            startActivity(it);
        }
    };
    private AdapterView.OnItemLongClickListener liv_adapter_longclick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

            final View dialog_item = LayoutInflater.from(ShowActivity.this).inflate(R.layout.dialog_check_delete_layout, null);
            TextView txv = (TextView)dialog_item.findViewById(R.id.dialog_check_delete_layout_txv);
            txv.setText("確認刪除"+arrayList.get(position).name+"?");
            new AlertDialog.Builder(ShowActivity.this)
                    .setTitle("刪除訊息")
                    .setView(dialog_item)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteTable("Note"+arrayList.get(position).id);
                            db.deleteData(arrayList.get(position).id);
                            getData();
                            myAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("no",null)
                    .show();

            return true;
        }
    };

    public void showAddDialog(){
        final View dialog_item = LayoutInflater.from(ShowActivity.this).inflate(R.layout.dialog_add_layout, null);
        new AlertDialog.Builder(ShowActivity.this)
                .setTitle("新增")
                .setView(dialog_item)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) dialog_item.findViewById(R.id.dialog_add_layout_edt);
                        String name = editText.getText().toString();
                        if(!name.equals("")) {
                            addData(name);
                            getData();
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .show();
    }
    class Info{
        int id;
        String name;
        Info(int id,String name){
            this.id = id;
            this.name = name;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(db!=null)
            db.closedb();
    }
}
