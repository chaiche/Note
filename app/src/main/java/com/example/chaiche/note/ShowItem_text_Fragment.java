package com.example.chaiche.note;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Scroller;
import android.widget.TextView;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowItem_text_Fragment extends Fragment {

    OnItemTextSelectedListener callback;
    Boolean canEdit = false;
    Boolean isEdit = false;

    Handler hd = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.arg1){
                case 0:
                    txv.setText(msg.obj.toString());
                    canEdit = true;
                    break;
                default:
            }
        }
    };

    public interface OnItemTextSelectedListener {
        public void updateData(String text);

    }
    public ShowItem_text_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OnItemTextSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnItemSelectedListener");
        }

    }
    Drawable drawable_edit,drawable_ok;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_item_text_, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        Bitmap bitmap_edit = BitmapFactory.decodeResource(getResources(), R.drawable.edit);
        bitmap_edit = zoomImage(bitmap_edit,60,60);
        drawable_edit = new BitmapDrawable(getResources(), bitmap_edit);
        Bitmap bitmap_ok = BitmapFactory.decodeResource(getResources(), R.drawable.ok);
        bitmap_ok = zoomImage(bitmap_ok,60,60);
        drawable_ok = new BitmapDrawable(getResources(), bitmap_ok);

        initView();

    }

    TextView txv;
    EditText edt;

    FrameLayout frameLayout;

    ImageButton igb;
    public void initView(){

        txv = (TextView)getActivity().findViewById(R.id.show_item_text_frg_txv);

        frameLayout = (FrameLayout)getActivity().findViewById(R.id.show_item_text_frg_framelayout);
        edt = new LinedEditText(getContext());
        frameLayout.addView(edt);

        igb = (ImageButton)getActivity().findViewById(R.id.show_item_text_frg_igb);

        igb.setOnClickListener(igb_click);
        igb.setImageDrawable(drawable_edit);


        change_visibility(0);
        canLoad = true;


    }


    Boolean canLoad = false;

    public void setText(final String text){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!canLoad);
                Message message = new Message();
                message.arg1 = 0;
                message.obj = text;
                hd.sendMessage(message);
            }
        }).start();

    }
    public void change_visibility(int i){
        if(i == 0){
            txv.setVisibility(View.VISIBLE);
            edt.setVisibility(View.GONE);
        }
        else{
            txv.setVisibility(View.GONE);
            edt.setVisibility(View.VISIBLE);
        }
    }



    private View.OnClickListener igb_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!isEdit){
                change_visibility(1);
                edt.setText(txv.getText().toString());
                igb.setSelected(true);
                igb.setImageDrawable(drawable_ok);

                isEdit = true;
            }
            else{
                change_visibility(0);
                String text = edt.getText().toString();
                txv.setText(text);
                igb.setSelected(false);
                igb.setImageDrawable(drawable_edit);
                isEdit = false;
                callback.updateData(text);
                txv.requestFocus();
                hidekeyboard();
            }

        }
    };
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



    public class LinedEditText extends EditText {
        private Paint mPaint = new Paint();

        public LinedEditText(Context context) {
            super(context);
            initPaint();
        }

        public LinedEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            initPaint();
        }

        public LinedEditText(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            initPaint();
        }

        private void initPaint() {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(0x80000000);
        }

        @Override protected void onDraw(Canvas canvas) {
            int left = getLeft();
            int right = getRight();
            int paddingTop = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            int height = getHeight();
            int lineHeight = getLineHeight();
            int count = (height-paddingTop-paddingBottom) / lineHeight;

            for (int i = 0; i < count; i++) {
                int baseline = lineHeight * (i+1) + paddingTop;
                canvas.drawLine(left+paddingLeft, baseline, right-paddingRight, baseline, mPaint);
            }

            super.onDraw(canvas);
        }
    }

    public void hidekeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0); //強制隱藏鍵盤
    }

}
