package com.codemolly.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by mrand on 9/29/15.
 */
public class WatchDrawingView extends SurfaceView {
    private Path drawingPath;
    private Paint drawPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private int color = 0xff000000;
    private float lastX = -1;
    private float lastY = -1;
    private SurfaceHolder mHolder;
    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            initOffScreenBuffer();
            flushBuffer();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    public WatchDrawingView(Context context) {
        super(context);
        setup(null, 0);
    }

    public WatchDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs, 0);
    }

    public WatchDrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mHolder.getSurface().isValid()) {
            float touchX = event.getX();
            float touchY = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawingPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawingPath.quadTo(lastX, lastY, touchX, touchY);
//                    drawingPath.lineTo(touchX, touchY);
                    drawCanvas.drawPath(drawingPath, drawPaint);
                    break;
                case MotionEvent.ACTION_UP:
                    drawCanvas.drawPath(drawingPath, drawPaint);
                    drawingPath.reset();
                    break;
                default:
                    return false;
            }
            lastX = touchX;
            lastY = touchY;
            flushBuffer();
            return true;
        }
        return false;
    }

    public Bitmap getBitmap() {
        return canvasBitmap;
    }

    public void setDrawingColor(int newColor) {
        color = newColor;
        drawPaint.setColor(color);
    }


    private void setup(AttributeSet attrs, int defStyle) {
        mHolder = getHolder();
        mHolder.addCallback(mCallback);
        drawingPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(color);
        drawPaint.setStrokeWidth(10.f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.BEVEL);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void initOffScreenBuffer() {
        canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawColor(Color.WHITE);
    }


    public void erase() {
        drawCanvas.drawColor(Color.WHITE);
        flushBuffer();
    }

    private void flushBuffer() {
        Canvas canvas = mHolder.lockCanvas();
        if (canvas != null) {
            canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }
}
