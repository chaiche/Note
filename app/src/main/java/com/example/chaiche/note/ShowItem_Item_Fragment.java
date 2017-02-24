package com.example.chaiche.note;


import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowItem_Item_Fragment extends Fragment {

    OnItemSelectedListener callback;

    ArrayList<Info> arrayList = null;

    ArrayList<Info> arrayList_now = null;



    public interface OnItemSelectedListener {
        public Cursor getData();
        public void deleteData(int id);

        public void loadtext(String text);
        public void loadurl(String url);
        public void loadposition(String name,String pos);
    }

    public ShowItem_Item_Fragment() {
        // Required empty public constructor
        arrayList = new ArrayList<Info>();
        arrayList_now = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OnItemSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnItemSelectedListener");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_item__item_, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getData();

        initView();
    }

    ListView liv;
    MyLivAdapter myLivAdapter;

    EditText edt_search;

    public void initView(){
        liv = (ListView)getActivity().findViewById(R.id.show_item_frg_liv);
        myLivAdapter = new MyLivAdapter(getContext(),arrayList_now);
        liv.setAdapter(myLivAdapter);

        liv.setOnItemClickListener(liv_adapter_click);
        liv.setOnItemLongClickListener(liv_adapter_longclick);

        edt_search = (EditText)getActivity().findViewById(R.id.show_item_frg_edt_search);

        edt_search.addTextChangedListener(edt_textWatcher);
    }

    public void getData(){

        arrayList.clear();
        Cursor cs = callback.getData();
        int count = cs.getCount();
        if(count != 0) {
            cs.moveToFirst();
            for(int i=0; i<count; i++) {
                int id = cs.getInt(0);
                String name = cs.getString(1);
                int type = cs.getInt(2);
                String text = cs.getString(3);
                String url = cs.getString(4);
                String pos = cs.getString(5);
                String date = cs.getString(6);
                Info info = new Info(id,name,type,text,url,pos,date);
                arrayList.add(0,info);
                cs.moveToNext();
            }
        }
        arrayList_now.clear();
        arrayList_now = (ArrayList<Info>) arrayList.clone();

        Log.d("count",arrayList_now.size()+"");

    }

    public class MyLivAdapter extends BaseAdapter{

        LayoutInflater lif = null;
        ArrayList<Info> arrayList = null;
        MyLivAdapter(Context context,ArrayList<Info> arrayList){
            lif = LayoutInflater.from(context);
            this.arrayList = arrayList;
        }
        public void setarrayList(ArrayList<Info> arrayList){
            this.arrayList = arrayList;
        }
        @Override
        public int getCount() {
            return this.arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return this.arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = lif.inflate(R.layout.show_item_frg_liv_layout,null);

            TextView txv_name,txv_type,txv_date;
            txv_name = (TextView)convertView.findViewById(R.id.show_item_frg_liv_layout_txv_name);
            txv_name.setText(this.arrayList.get(position).name);

            txv_type = (TextView)convertView.findViewById(R.id.show_item_frg_liv_layout_txv_type);

            switch (this.arrayList.get(position).type){
                case 0:
                    txv_type.setText("類型："+"文字");
                    break;
                case 1:
                    txv_type.setText("類型："+"網址");
                    break;
                case 2:
                    txv_type.setText("類型："+"位置");
                    break;
                default:
            }

            txv_date = (TextView)convertView.findViewById(R.id.show_item_frg_liv_layout_txv_date);
            txv_date.setText(this.arrayList.get(position).date);
            return convertView;
        }
    }

    int now_item_id = -1;

    private AdapterView.OnItemClickListener liv_adapter_click = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            now_item_id = arrayList_now.get(position).id;
            switch (arrayList_now.get(position).type){
                case 0:
                    callback.loadtext(arrayList_now.get(position).text);
                    break;
                case 1:
                    callback.loadurl(arrayList_now.get(position).url);
                    break;
                case 2:
                    callback.loadposition(arrayList_now.get(position).name,arrayList_now.get(position).pos);
                    break;
                default:
            }

        }
    };
    private AdapterView.OnItemLongClickListener liv_adapter_longclick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

            final View dialog_item = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_check_delete_layout, null);
            TextView txv = (TextView)dialog_item.findViewById(R.id.dialog_check_delete_layout_txv);
            txv.setText("確認刪除"+arrayList_now.get(position).name+"?");
            new AlertDialog.Builder(getActivity())
                    .setTitle("刪除訊息")
                    .setView(dialog_item)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.deleteData(arrayList_now.get(position).id);
                            liv_adapter_notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("no",null)
                    .show();

            return true;
        }
    };

    public void liv_adapter_notifyDataSetChanged(){
        getData();
        myLivAdapter.setarrayList(arrayList_now);
        myLivAdapter.notifyDataSetChanged();
        edt_search.setText("");
        liv.requestFocus();
    }

    class Info{
        int id;
        String name;
        int type;
        String text;
        String url;
        String pos;
        String date;
        Info(int id,String name,int type,String text,String url,String position,String date){
            this.id = id;
            this.name = name;
            this.type = type;
            this.text = text;
            this.url = url;
            this.pos = position;
            this.date = date;
        }
    }

    private TextWatcher edt_textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            arrayList_now.clear();
            for(int i =0;i<arrayList.size();i++){
                String name = arrayList.get(i).name;
                if(name.contains(s)){
                    arrayList_now.add(arrayList.get(i));
                }
            }
            myLivAdapter.setarrayList(arrayList_now);
            myLivAdapter.notifyDataSetChanged();
        }
    };
}
