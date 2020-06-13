/*
 * Copyright (C) 2010 Daniel Nilsson
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

package de.j4velin.lib.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class ColorPickerViewTV extends GridView implements ColorView {

    private final int[] colors;
    private int color = Color.WHITE;
    private OnColorChangedListener callback;

    public ColorPickerViewTV(Context context) {
        this(context, null);
    }

    public ColorPickerViewTV(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerViewTV(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        int colorStep = 5;
        int blackStep = 32;
        colors = new int[360 / colorStep + 256 / blackStep];
        int pos = 0;
        for (int i = 0; i < 256; i = i + blackStep) {
            colors[pos++] = Color.rgb(i, i, i);
        }
        for (int i = 0; i < 360; i = i + colorStep) {
            colors[pos++] = Color.HSVToColor(new float[]{i, 1f, 1f});
        }
        setAdapter(new ColorAdapter(context));
    }

    @Override
    public void setColor(int color, boolean callback) {
        this.color = color;
        if (callback && this.callback != null) {
            this.callback.onColorChanged(color);
        }
    }

    @Override
    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.callback = listener;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public void setAlphaSliderVisible(boolean visible) {
    }

    @Override
    public boolean getAlphaSliderVisible() {
        return false;
    }

    @Override
    public float getDrawingOffset() {
        return 0;
    }

    private class ColorAdapter extends BaseAdapter {

        private final View[] views = new View[colors.length];

        private ColorAdapter(final Context context) {
            OnClickListener ocl = new OnClickListener() {
                @Override
                public void onClick(View view) {
                    setColor((int) view.getTag(), true);
                }
            };
            OnFocusChangeListener ofl = new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean focus) {
                    if (focus) {
                        setBackgroundColor((int) view.getTag());
                    }
                }
            };
            int size = (int) Util.dpToPx(getContext(), 20);
            for (int i = 0; i < colors.length; i++) {
                View v = new View(context);
                v.setBackgroundColor(colors[i]);
                v.setMinimumHeight(size);
                v.setOnClickListener(ocl);
                v.setOnFocusChangeListener(ofl);
                v.setTag(colors[i]);
                v.setFocusable(true);
                views[i] = v;
                if (i == 0) v.requestFocus();
            }
        }

        @Override
        public int getCount() {
            return views.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            return views[position];
        }
    }
}