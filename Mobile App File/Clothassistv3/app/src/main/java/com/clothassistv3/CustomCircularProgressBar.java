package com.clothassistv3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

public class CustomCircularProgressBar extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private Typeface fjallaOneTypeface;
    private float progress;
    private String progressText = ""; // Initialize progressText with an empty string

    public CustomCircularProgressBar(Context context) {
        super(context);
        init();
    }
    public CustomCircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public CustomCircularProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setStyle(Paint.Style.STROKE);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(Color.CYAN); // Changed color to cyan
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(20);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(80);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Context context = getContext();
        fjallaOneTypeface = ResourcesCompat.getFont(context, R.font.fjalla_one_regular);
    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 40;

        // Draw background circle
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);
        // Draw progress arc
        @SuppressLint("DrawAllocation") RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawArc(rectF, -90, 360 * progress / 100, false, progressPaint);

        // Draw progress text with Fjalla One Regular font
        if (progressText != null && fjallaOneTypeface != null) {
            textPaint.setTypeface(fjallaOneTypeface);
            canvas.drawText(progressText, centerX, centerY + textPaint.getTextSize() / 2, textPaint);
        }
    }
    @SuppressLint("DefaultLocale")
    public void setProgress(float progress) {
        this.progress = progress;
        this.progressText = String.format("%.0f%%", progress);
        postInvalidate(); // Redraw the view
    }
}
