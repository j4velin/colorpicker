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

import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class ColorPickerDialog extends Dialog implements ColorPickerView.OnColorChangedListener,
        View.OnClickListener {

    private final Context context;
    private ColorView mColorPicker;

    private ColorPickerPanelView mOldColor;
    private ColorPickerPanelView mNewColor;

    private EditText mHexVal;
    private boolean mHexValueEnabled = true;
    private ColorStateList mHexDefaultTextColor;

    public static OnColorChangedListener mListener;

    public interface OnColorChangedListener {
        void onColorChanged(int color);
    }

    public ColorPickerDialog(final Context context, int initialColor) {
        super(context, R.style.LibTheme_Dialog);
        this.context = context;
        init(initialColor);
    }

    private void init(int color) {
        // To fight color banding.
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setUp(color);
    }

    private void setUp(int color) {

        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.dialog_color_picker, null);

        setContentView(layout);

        mColorPicker = layout.findViewById(R.id.color_picker_view);
        mOldColor = layout.findViewById(R.id.old_color_panel);
        mNewColor = layout.findViewById(R.id.new_color_panel);

        mHexVal = layout.findViewById(R.id.hex_val);
        mHexVal.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mHexDefaultTextColor = mHexVal.getTextColors();

        mHexVal.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    String s = mHexVal.getText().toString();
                    if (s.length() > 5 || s.length() < 10) {
                        try {
                            int c = Util.convertToColorInt(s);
                            mColorPicker.setColor(c, true);
                            mHexVal.setTextColor(mHexDefaultTextColor);
                        } catch (IllegalArgumentException e) {
                            mHexVal.setTextColor(Color.RED);
                        }
                    } else {
                        mHexVal.setTextColor(Color.RED);
                    }
                    return true;
                }
                return false;
            }
        });

        ((LinearLayout) mOldColor.getParent())
                .setPadding(Math.round(mColorPicker.getDrawingOffset()), 0,
                        Math.round(mColorPicker.getDrawingOffset()), 0);

        mOldColor.setOnClickListener(this);
        mNewColor.setOnClickListener(this);
        mColorPicker.setOnColorChangedListener(this);
        mOldColor.setColor(color);
        mColorPicker.setColor(color, true);

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            layout.findViewById(R.id.fromphoto).setOnClickListener(this);
        } else {
            layout.findViewById(R.id.fromphoto).setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= 11) {
            findViewById(R.id.copy).setOnClickListener(this);
            findViewById(R.id.paste).setOnClickListener(this);
        }

    }

    @Override
    public void onColorChanged(int color) {

        mNewColor.setColor(color);

        if (mHexValueEnabled) updateHexValue(color);

		/*
        if (mListener != null) {
			mListener.onColorChanged(color);
		}
		*/

        if (((UiModeManager) getContext().getSystemService(Context.UI_MODE_SERVICE))
                .getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            if (mListener != null) {
                mListener.onColorChanged(mNewColor.getColor());
            }
            dismiss();
        }
    }

    public void setHexValueEnabled(boolean enable) {
        mHexValueEnabled = enable;
        if (enable) {
            mHexVal.setVisibility(View.VISIBLE);
            updateHexLengthFilter();
            updateHexValue(getColor());
        } else mHexVal.setVisibility(View.GONE);
        findViewById(R.id.copy)
                .setVisibility(enable & Build.VERSION.SDK_INT >= 11 ? View.VISIBLE : View.GONE);
        findViewById(R.id.paste)
                .setVisibility(enable & Build.VERSION.SDK_INT >= 11 ? View.VISIBLE : View.GONE);
    }

    public boolean getHexValueEnabled() {
        return mHexValueEnabled;
    }

    private void updateHexLengthFilter() {
        if (getAlphaSliderVisible())
            mHexVal.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
        else mHexVal.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
    }

    private void updateHexValue(int color) {
        if (getAlphaSliderVisible()) {
            mHexVal.setText(Util.convertToARGB(color).toUpperCase(Locale.getDefault()));
        } else {
            mHexVal.setText(Util.convertToRGB(color).toUpperCase(Locale.getDefault()));
        }
        mHexVal.setTextColor(mHexDefaultTextColor);
    }

    public void setAlphaSliderVisible(boolean visible) {
        mColorPicker.setAlphaSliderVisible(visible);
        if (mHexValueEnabled) {
            updateHexLengthFilter();
            updateHexValue(getColor());
        }
    }

    public boolean getAlphaSliderVisible() {
        return mColorPicker.getAlphaSliderVisible();
    }

    /**
     * Set a OnColorChangedListener to get notified when the color
     * selected by the user has changed.
     *
     * @param listener the listener
     */
    public void setOnColorChangedListener(final OnColorChangedListener listener) {
        mListener = listener;
    }

    public int getColor() {
        return mColorPicker.getColor();
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.new_color_panel) {
            if (mListener != null) {
                mListener.onColorChanged(mNewColor.getColor());
            }
            dismiss();
        } else if (v.getId() == R.id.fromphoto) {
            context.startActivity(new Intent(context, ExtractFromPhoto.class));
            dismiss();
        } else if (v.getId() == R.id.copy) {
            API11Wrapper.copy(context, mHexVal.getText().toString());
            Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.paste) {
            String s = API11Wrapper.paste(context);
            if (s != null) {
                try {
                    int c = Util.convertToColorInt(s);
                    mColorPicker.setColor(c, true);
                    mHexVal.setTextColor(mHexDefaultTextColor);
                } catch (IllegalArgumentException e) {
                    mHexVal.setTextColor(Color.RED);
                }
            }
        }
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt("old_color", mOldColor.getColor());
        state.putInt("new_color", mNewColor.getColor());
        return state;
    }

    @Override
    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mOldColor.setColor(savedInstanceState.getInt("old_color"));
        mColorPicker.setColor(savedInstanceState.getInt("new_color"), true);
    }
}
