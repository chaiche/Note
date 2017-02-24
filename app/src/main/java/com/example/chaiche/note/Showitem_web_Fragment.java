package com.example.chaiche.note;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class Showitem_web_Fragment extends Fragment {

    OnItemWebSelectedListener callback;


    Handler hd = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.arg1){
                case 0:
                    webview.loadUrl(msg.obj.toString());
                    break;
                default:
            }
        }
    };

    public interface OnItemWebSelectedListener {
        public void setToolbarVisibility(Boolean bool);
    }

    public Showitem_web_Fragment() {
        // Required empty public constructor

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OnItemWebSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnItemWebSelectedListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_showitem_web_, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    WebView webview;

    FrameLayout video_fullView;

    private View xCustomView;
    private WebChromeClient.CustomViewCallback xCustomViewCallback;

    ProgressBar progressBar;

    public void initView(){

        progressBar = (ProgressBar) getActivity().findViewById(R.id.show_item_web_progressbar);
        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);

        video_fullView = (FrameLayout) getActivity().findViewById(R.id.show_item_web_framelayout);

        webview = (WebView)getActivity().findViewById(R.id.show_item_web_frg_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);// 隐藏缩放按钮

//设置可在大视野范围内上下左右拖动，并且可以任意比例缩放
        webview.getSettings().setUseWideViewPort(true);
//设置默认加载的可视范围是大视野范围
        webview.getSettings().setLoadWithOverviewMode(true);
//自适应屏幕
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadsImagesAutomatically(true);

        webview.setWebViewClient(new MyCustomWebViewClient());
        webview.setWebChromeClient(new MyWebChromeClient());
        canLoad = true;


    }

    Boolean canLoad = false;

    public void loadurl(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!canLoad);
                Message message = new Message();
                message.arg1 = 0;
                message.obj = url;
                hd.sendMessage(message);
            }
        }).start();
    }

    private class MyCustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("webview onPagefinish", " url:" + url);

            //设置默认加载的可视范围是大视野范围
            webview.getSettings().setLoadWithOverviewMode(true);
//自适应屏幕
            webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            Log.d("onReceivedSslError", "onReceivedSslError");
        }

    }


    public class MyWebChromeClient extends WebChromeClient {

        // 播放网络视频时全屏会被调用的方法
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            webview.setVisibility(View.INVISIBLE);
            // 如果一个视图已经存在，那么立刻终止并新建一个
            if (xCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            video_fullView.addView(view);
            xCustomView = view;
            xCustomViewCallback = callback;
            video_fullView.setVisibility(View.VISIBLE);
            full();

            settoolbarVisibility(false);
        }

        // 视频播放退出全屏会被调用的
        @Override
        public void onHideCustomView() {


            if (xCustomView == null)// 不是全屏播放状态
                return;

            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            xCustomView.setVisibility(View.GONE);
            video_fullView.removeView(xCustomView);
            xCustomView = null;
            video_fullView.setVisibility(View.GONE);
            xCustomViewCallback.onCustomViewHidden();
            webview.setVisibility(View.VISIBLE);

            unfull();

            settoolbarVisibility(true);
        }
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);
            } else {
                if (progressBar.getVisibility() == View.GONE)
                    progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        /**
         * 设置为横屏
         */
        if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        webview.onResume();
        webview.resumeTimers();
    }
    @Override
    public  void onPause() {
        super.onPause();
        webview.onPause();
        webview.pauseTimers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        super.onDestroy();
        video_fullView.removeAllViews();
        webview.loadUrl("about:blank");
        webview.stopLoading();
        webview.setWebChromeClient(null);
        webview.setWebViewClient(null);
        webview.destroy();
        webview = null;


    }
    public void settoolbarVisibility(Boolean bool) {
        callback.setToolbarVisibility(bool);
    }

    public Boolean goBack(){
        if(webview.canGoBack()){
            webview.goBack();
            return true;
        }
        return false;
    }

    public void full(){
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void unfull(){
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(0);
    }

}
