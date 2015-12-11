package com.codemolly.drawing.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mrand on 9/29/15.
 */
public class DrawingView extends View {
    private Path drawingPath;
    private Paint drawPaint;
    private Paint canvasPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private int color = 0xffffffff;

    public DrawingView(Context context) {
        super(context);
        setup();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawingPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawingPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawingPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawingPath, drawPaint);
                drawingPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public Bitmap getBitmap() {
        return canvasBitmap;
    }

    public void setDrawingColor(int newColor) {
        color = newColor;
        drawPaint.setColor(color);
    }


    private void setup(){
        drawingPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(color);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20.f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.BEVEL);
        drawPaint.setStrokeCap(Paint.Cap.BUTT);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void erase() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}
