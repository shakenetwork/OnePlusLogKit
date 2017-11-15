package com.oem.oemlogkit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.v4.internal.view.SupportMenu;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.util.LinkedList;

public class WaveView extends SurfaceView implements Runnable, Callback {
    private int mBackgroundColor;
    private Context mContext;
    private Object mLock;
    private volatile boolean mLoop;
    private Paint mPaint;
    Path mPath;
    private LinkedList<DataWrap> mPoints;
    private SurfaceHolder mSurfaceHolder;

    public class DataWrap {
        public int data1;

        public DataWrap(int data1) {
            this.data1 = data1;
        }
    }

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLock = new Object();
        init(context);
    }

    private void init(Context context) {
        this.mPath = new Path();
        this.mPaint = new Paint();
        this.mPoints = new LinkedList();
        this.mSurfaceHolder = getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mContext = context;
        this.mBackgroundColor = this.mContext.getResources().getColor(R.color.oem_listview_background_color);
    }

    public void addPoint(int x) {
        if (this.mPoints.size() > 0 && this.mPoints.size() >= getWidth()) {
            this.mPoints.removeFirst();
        }
        this.mPoints.add(new DataWrap(processData(x)));
    }

    public void clearPoint() {
        this.mPoints.clear();
    }

    private int processData(int data) {
        if (data > (getHeight() / 2) - 1) {
            data = (getHeight() / 2) - 1;
        } else if (data < ((-getHeight()) / 2) + 1) {
            data = ((-getHeight()) / 2) + 1;
        }
        return (getHeight() / 2) - data;
    }

    public void draw() {
        if (this.mLoop) {
            Canvas canvas = this.mSurfaceHolder.lockCanvas();
            if (canvas != null) {
                this.mPaint.setAntiAlias(true);
                canvas.drawColor(this.mBackgroundColor);
                this.mPaint.setColor(-1);
                this.mPaint.setStrokeWidth(2.0f);
                canvas.drawLine(2.0f, 0.0f, 2.0f, (float) getHeight(), this.mPaint);
                canvas.drawLine(2.0f, (float) (getHeight() / 2), (float) getWidth(), (float) (getHeight() / 2), this.mPaint);
                this.mPaint.setStrokeWidth(1.0f);
                this.mPaint.setStrokeCap(Cap.ROUND);
                this.mPaint.setStyle(Style.STROKE);
                int i = 0;
                this.mPath.rewind();
                if (this.mPoints.size() > 0) {
                    this.mPath.moveTo(0.0f, (float) ((DataWrap) this.mPoints.get(0)).data1);
                    i = 1;
                }
                while (i < this.mPoints.size()) {
                    DataWrap preData = (DataWrap) this.mPoints.get(i - 1);
                    this.mPath.quadTo((float) (i - 1), (float) preData.data1, (float) i, (float) ((DataWrap) this.mPoints.get(i)).data1);
                    i++;
                }
                this.mPaint.setColor(SupportMenu.CATEGORY_MASK);
                this.mPaint.setStrokeWidth(5.0f);
                canvas.drawPath(this.mPath, this.mPaint);
                try {
                    this.mSurfaceHolder.unlockCanvasAndPost(canvas);
                } catch (IllegalArgumentException e) {
                }
            }
        }
    }

    public void run() {
        while (this.mLoop) {
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (this.mLock) {
                if (this.mLoop) {
                    draw();
                }
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        synchronized (this.mLock) {
            this.mLoop = true;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (this.mLock) {
            this.mLoop = false;
        }
    }
}
