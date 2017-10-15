package com.lwh.demo;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lwh.jackknife.app.Activity;
import com.lwh.jackknife.ioc.ViewId;
import com.lwh.jackknife.ioc.ViewIgnore;

public class MainActivity extends Activity {

    @ViewId(R.id.textview_main_lineone)
    private TextView textview_main_lineone;

    private TextView textview_main_linetwo;

    @ViewIgnore
    private TextView textview_main_linethree;

    private boolean mButtonClicked;

    @OnClick(R.id.button_main_share)
    public void share(View view){
        if (mButtonClicked){
            Toast.makeText(this, "Don't click me again.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Please share it with your friends.", Toast.LENGTH_SHORT).show();
            textview_main_lineone.setText("https://github.com/JackWHLiu/alwh-library");
            textview_main_linetwo.setText("JackKnife");
            mButtonClicked = true;
        }
    }
}
