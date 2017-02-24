package com.example.chaiche.note;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



/**
 * A simple {@link Fragment} subclass.
 */
public class ShowItem_map_Fragment extends Fragment {

    OnItemMapSelectedListener callback;

    private GoogleMap map;

    private LatLng nowLatLng;

    Handler hd = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.arg1){
                case 0:
                    MarkerOptions tmp = (MarkerOptions)msg.obj;
                    LatLng latLng = tmp.getPosition();
                    map.addMarker(tmp);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    nowLatLng = latLng;
                    break;
                default:
            }
        }
    };

    public interface OnItemMapSelectedListener {
        public String getCurrentLocation();
    }

    public ShowItem_map_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OnItemMapSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnItemSelectedListener");
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_item_map_, container, false);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SupportMapFragment mapFragment =
                ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.show_item_map_frg_map));
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                canLoad = true;
            }
        });

        initView();

    }


    public void initView(){
//        btn_navigation = (ImageButton)getActivity().findViewById(R.id.show_item_map_igb_navigation);
//        btn_navigation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String end = "saddr=" + nowLatLng.latitude + "," + nowLatLng.longitude;
//                String tmp = callback.getCurrentLocation();
//                String lat = tmp.substring(0,tmp.indexOf(":")-1);
//                String lng = tmp.substring(tmp.indexOf(":")+1);
//                String start = "daddr=" + lat + "," + lng;
//                String uriString = "http://maps.google.com/maps?" + start + "&" + end;
//
//                enableGoogleMapNavigation(uriString);
//            }
//        });
    }

    Boolean canLoad = false;

    public void drawmark(final String name, String pos){
        Log.d("test",pos);
        String lat = pos.substring(0,pos.indexOf(":")-1);
        String lng = pos.substring(pos.indexOf(":")+1);
        final LatLng gps = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!canLoad);
                Message message = new Message();
                message.arg1 = 0;
                message.obj = new MarkerOptions().position(gps).title(name);
                hd.sendMessage(message);
            }
        }).start();

    }

    public void enableGoogleMapNavigation(String path){

        Uri uri = Uri.parse(path);

        Intent map_it = new Intent(Intent.ACTION_VIEW, uri);

        map_it.setPackage("com.google.android.apps.maps");

        startActivity(map_it);
    }


}
