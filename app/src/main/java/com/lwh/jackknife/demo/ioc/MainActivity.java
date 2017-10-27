/*
 * Copyright (C) 2017 The JackKnife Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lwh.jackknife.demo.ioc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lwh.jackknife.app.Activity;
import com.lwh.jackknife.demo.R;
import com.lwh.jackknife.ioc.ViewId;

public class MainActivity extends Activity {

    TextView textview_main_appname;

    @ViewId(R.id.button_main_getstarted)
    Button button_main_getstarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_scene);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getLightVibrantSwatch();
                if (swatch != null) {
                    textview_main_appname.setTextColor(swatch.getRgb());
                    button_main_getstarted.setTextColor(swatch.getTitleTextColor());
                }
            }
        });
    }

    @OnClick(R.id.button_main_getstarted)
    public void getStarted(View view) {
        Toast.makeText(this, "Button named \'Get started!\' has been clicked.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
}
