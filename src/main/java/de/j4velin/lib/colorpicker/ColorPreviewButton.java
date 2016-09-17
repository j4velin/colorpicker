package de.j4velin.lib.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ColorPreviewButton extends View {

    private Paint paint = new Paint();
    private int color;
    private final int borderColor;

    public ColorPreviewButton(final Context context) {
        this(context, null);
    }

    public ColorPreviewButton(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPreviewButton(final Context context, final AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ColorPreviewButton, 0, 0);
        try {
            borderColor = a.getColor(R.styleable.ColorPreviewButton_borderColor, Color.LTGRAY);
            color = a.getColor(R.styleable.ColorPreviewButton_initColor, Color.BLACK);
        } finally {
            a.recycle();
        }
    }

    public void setColor(final int c) {
        color = c;
        invalidate();
    }

    public int getColor() {
        return color;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, paint);
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        int borderwidth = (int) Util.dpToPx(getContext(), 1f);
        paint.setStrokeWidth(borderwidth);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2 - 1, paint);
    }

    @Override
    public void setBackgroundColor(int color) {
        setColor(color);
    }
}
