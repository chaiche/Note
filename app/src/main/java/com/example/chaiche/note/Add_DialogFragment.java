package com.example.chaiche.note;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Add_DialogFragment extends DialogFragment {

    OnItem_Add_SelectedListener callback;

    View dialog_item;
    EditText edt_name ;
    EditText edt_text;
    EditText edt_url;

    LinearLayout lin_web ;

    FrameLayout frameLayout,frameLayout_qrcode;
    TextView txv_position;

    Spinner spinner;

    ImageView btn_ok;

    private ZXingScannerView zXingScannerView = null;

    public interface OnItem_Add_SelectedListener {
        public void addData(String name, int type, String text, String url, String position, String time);
    }
    public Add_DialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OnItem_Add_SelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnItem_Add_SelectedListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialog_item = inflater.inflate(R.layout.dialog_frg_add_layout, container, false);

        edt_name = (EditText) dialog_item.findViewById(R.id.dialog_frg_add_edt_name);
        edt_text = (EditText) dialog_item.findViewById(R.id.dialog_frg_add_edt_text);
        edt_url = (EditText) dialog_item.findViewById(R.id.dialog_frg_add_edt_url);

        lin_web = (LinearLayout) dialog_item.findViewById(R.id.dialog_frg_add_lin_web);
        frameLayout_qrcode = (FrameLayout)dialog_item.findViewById(R.id.dialog_frg_add_framelauout_qrcode);

        frameLayout = (FrameLayout) dialog_item.findViewById(R.id.dialog_frg_add_framelayout_position);
        txv_position = (TextView) dialog_item.findViewById(R.id.dialog_frg_add_txv_position);

        spinner = (Spinner) dialog_item.findViewById(R.id.dialog_frg_add_spinner);

        ArrayAdapter<CharSequence> item_type_List = ArrayAdapter.createFromResource(getActivity(),
                R.array.item_type,
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(item_type_List);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    edt_text.setVisibility(View.VISIBLE);
                    lin_web.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.GONE);
                    txv_position.setText("");
                    if(zXingScannerView!=null)
                        zXingScannerView.stopCamera();
                    frameLayout_qrcode.removeAllViews();
                } else if (position == 1) {
                    edt_text.setVisibility(View.GONE);
                    lin_web.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.GONE);
                    txv_position.setText("");
                    edt_url.setText("");
                    zXingScannerView = new ZXingScannerView(getContext());

                    zXingScannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
                        @Override
                        public void handleResult(Result result) {
                            Log.d("test", result.getText());
                            edt_url.setText(result.getText());

                            zXingScannerView.stopCamera();
                            frameLayout_qrcode.removeAllViews();
                        }
                    });
                    frameLayout_qrcode.addView(zXingScannerView);
                    zXingScannerView.startCamera();
                } else if (position == 2) {
                    edt_text.setVisibility(View.GONE);
                    lin_web.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.VISIBLE);
                    if(zXingScannerView!=null)
                        zXingScannerView.stopCamera();
                    frameLayout_qrcode.removeAllViews();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_ok = (ImageView)dialog_item.findViewById(R.id.dialog_frg_add_btn_ok);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_name.getText().toString();
                int type = spinner.getSelectedItemPosition();
                String text = edt_text.getText().toString();
                String url = edt_url.getText().toString();

                Double lat = Dialog_add_MapFragment.position.latitude;
                Double lng = Dialog_add_MapFragment.position.longitude;
                String pos = lat+":"+lng;

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");

                Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間

                String date = formatter.format(curDate);

                if (!name.equals("")) {
                    callback.addData(name, type, text, url, pos, date);
                }

                dismiss();
            }
        });

        return  dialog_item;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }




    @Override
    public void onPause(){
        super.onPause();

        if(zXingScannerView!=null)
            zXingScannerView.stopCamera();
    }
}
