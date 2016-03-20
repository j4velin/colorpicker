package de.j4velin.lib.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

@SuppressWarnings("unused")
public class ColorPreviewButton extends View {

    private Paint paint = new Paint();
    private int color;

    public ColorPreviewButton(final Context context) {
        super(context);
    }

    public ColorPreviewButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPreviewButton(final Context context, final AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
        paint.setColor(Color.WHITE);
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
