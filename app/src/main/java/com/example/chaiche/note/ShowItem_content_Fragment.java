package com.example.chaiche.note;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowItem_content_Fragment extends Fragment {



    public ShowItem_content_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_item_content_, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        now_which = -1;
        initView();
    }

    int now_which;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    ShowItem_text_Fragment frg_text;
    Showitem_web_Fragment frg_web;
    ShowItem_map_Fragment frg_map;

    public void initView(){
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.show_item_content_frg_framelayout, new Showitem_First_Fragment());
        fragmentTransaction.commit();

        frg_text = new ShowItem_text_Fragment();
        frg_web = new Showitem_web_Fragment();
        frg_map = new ShowItem_map_Fragment();

    }

    public void changeFragment(int which){

        if(now_which == which){
            return;
        }
        switch (which){
            case 0:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.show_item_content_frg_framelayout, frg_text);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.show_item_content_frg_framelayout, frg_web);
                fragmentTransaction.commit();
                break;
            case 2:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.show_item_content_frg_framelayout, frg_map);
                fragmentTransaction.commit();
                break;
            default:

        }
        now_which = which;
    }

    public int getWhich_frg(){
        return now_which;
    }

    public void text_loadtext(String text){
        frg_text.setText(text);
    }
    public void web_loadurl(String path){
        frg_web.loadurl(path);
    }

    public void map_loadpos(String name,String pos){
        frg_map.drawmark(name,pos);
    }


}
