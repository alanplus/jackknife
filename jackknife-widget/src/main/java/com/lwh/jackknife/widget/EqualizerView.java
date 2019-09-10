package com.lwh.jackknife.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class EqualizerView extends View {

    private Context mContext;
    private Paint mPaint;
    private Paint mNodePaint;
    private Paint mNodeConnectPaint;
    private int mWidth, mHeight;
    private PointF[] mPoints;
    private final int STATE_NONE = 0;
    private final int STATE_TOUCH_DOWN = 1;
    private final int STATE_TOUCH_MOVE = 2;
    private final int STATE_TOUCH_UP = 3;
    private int STATE_NOW = STATE_NONE;

    private int[] mDecibels;
    private float mRadius;
    private float mStep;
    private int mBandsNum;
    private OnUpdateDecibelListener mOnUpdateDecibelListener;

    public EqualizerView(Context context) {
        this(context, null);
    }

    public EqualizerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EqualizerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EqualizerView);
        mBandsNum = a.getInt(R.styleable.EqualizerView_ev_bandsNum, 5);
        a.recycle();
    }

    public interface OnUpdateDecibelListener {
        void onUpdateDecibel(int[] decibels);
    }

    public void setOnUpdateDecibelListener(OnUpdateDecibelListener l) {
        this.mOnUpdateDecibelListener = l;
    }

    public int[] getDecibels() {
        return mDecibels;
    }

    public void setDecibels(int[] decibels) {
        this.mDecibels = decibels;
        invalidate();
    }

    public void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mNodePaint = new Paint();
        mNodePaint.setAntiAlias(true);
        mNodePaint.setColor(ContextCompat.getColor(mContext, R.color.forest_green)); //圆圈的颜色
        mNodePaint.setStrokeWidth(6);
        mNodePaint.setStyle(Paint.Style.STROKE);
        mNodeConnectPaint = new Paint();
        mNodeConnectPaint.setAntiAlias(true);
        mNodeConnectPaint.setStrokeWidth(50);
        mNodeConnectPaint.setStyle(Paint.Style.FILL);
        mNodeConnectPaint.setColor(ContextCompat.getColor(mContext, R.color.forest_green));  //圆圈填充的颜色和连线的颜色
        mPoints = new PointF[12];
        mDecibels = new int[10];
    }

    private int measureView(int measureSpec, int defaultSize) {
        int measureSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            measureSize = size;
        } else {
            measureSize = defaultSize;
            if (mode == MeasureSpec.AT_MOST) {
                measureSize = Math.min(measureSize, defaultSize);
            }
        }
        return measureSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureView(widthMeasureSpec, 400),
                measureView(heightMeasureSpec, 200));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        mStep = mHeight / 26;    //-12到12共26份
        canvas.drawColor(ContextCompat.getColor(mContext, R.color.white));  //背景颜色
        int stepSize = mWidth / 11;
        mPoints[0] = new PointF(-50, mStep * 13);
        mPoints[11] = new PointF(mWidth + 50, mStep * 13);
        if ((STATE_NOW == STATE_NONE)) {
            for (int i = 1; i <= 10; i++) {
                float cx = stepSize * i, cy = mStep * (mDecibels[i - 1] + 13);
                mPoints[i] = new PointF(cx, cy);
            }
            refreshView(canvas, stepSize);
        } else {
            refreshView(canvas, stepSize);
        }
    }

    private void refreshView(Canvas canvas, int stepSize) {
//        float[] points = new float[]{mPoints[0].x, mPoints[0].y, mPoints[1].x, mPoints[1].y,
//                mPoints[1].x, mPoints[1].y, mPoints[2].x, mPoints[2].y,
//                mPoints[2].x, mPoints[2].y, mPoints[3].x, mPoints[3].y,
//                mPoints[3].x, mPoints[3].y, mPoints[4].x, mPoints[4].y,
//                mPoints[4].x, mPoints[4].y, mPoints[5].x, mPoints[5].y,
//                mPoints[5].x, mPoints[5].y, mPoints[6].x, mPoints[6].y,
//                mPoints[6].x, mPoints[6].y, mPoints[7].x, mPoints[7].y,
//                mPoints[7].x, mPoints[7].y, mPoints[8].x, mPoints[8].y,
//                mPoints[8].x, mPoints[8].y, mPoints[9].x, mPoints[9].y,
//                mPoints[9].x, mPoints[9].y, mPoints[10].x, mPoints[10].y,
//                mPoints[10].x, mPoints[10].y, mPoints[11].x, mPoints[11].y};
//        canvas.drawLines(points, mNodeConnectPaint);
        for (int i = 1; i <= mBandsNum; i++) {
            float cx = stepSize * i, cy = mPoints[i].y;
            if (i == index && STATE_NOW != STATE_TOUCH_UP) {
                mRadius = 50;
            } else {
                mRadius = 40;
            }
            canvas.drawCircle(cx, cy, mRadius, mNodePaint);
            canvas.drawCircle(cx, cy, mRadius - 6, mNodeConnectPaint);
            mPaint.setColor(ContextCompat.getColor(mContext, R.color.forest_green));    //下面的线的颜色
            mPaint.setStrokeWidth(6);
            canvas.drawLine(cx, cy + mRadius + 3, stepSize * i, mHeight, mPaint);
            mPaint.setColor(ContextCompat.getColor(mContext, R.color.light_gray));  //上面的线的颜色
            canvas.drawLine(cx, cy - mRadius - 3, stepSize * i, 0, mPaint);
        }
    }

    private int mLastY = 0;
    private int index = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX(), y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                index = findNodeIndex(x, y);
                if (index != 0) {
                    STATE_NOW = STATE_TOUCH_DOWN;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = y - mLastY;
                if (index != 0) {
                    STATE_NOW = STATE_TOUCH_MOVE;
                    mPoints[index].y += deltaY;
                    if (y <= 40)
                        mPoints[index].y = 40;
                    if (y >= mHeight - 40)
                        mPoints[index].y = mHeight - 40;
                    mDecibels[index - 1] = getDecibel(mPoints[index].y);
                    invalidate();
                    mOnUpdateDecibelListener.onUpdateDecibel(mDecibels);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (index != 0) {
                    STATE_NOW = STATE_TOUCH_UP;
                    if (mDecibels[index - 1] != 0 && mDecibels[index - 1] != -12 &&
                            mDecibels[index - 1] != 12) {
                        float lastY = mStep * (-mDecibels[index - 1] + 13);
                        mPoints[index].y = lastY;
                    } else if (mDecibels[index - 1] == 0)
                        mPoints[index].y = mStep * 13;
                    invalidate();
                }
                break;
            default:
                break;
        }
        mLastY = y;
        return true;
    }

    /**
     * 查出当前正在操作的是哪个结点
     *
     * @param x
     * @param y
     * @return
     */
    private int findNodeIndex(float x, float y) {
        int result = 0;
        for (int i = 1; i < mPoints.length; i++) {
            if (mPoints[i].x - mRadius * 1.5 < x && mPoints[i].x + mRadius * 1.5 > x &&
                    mPoints[i].y - mRadius * 1.5 < y && mPoints[i].y + mRadius * 1.5 > y) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * 将坐标转换为-12到12之间的数字
     *
     * @param y
     * @return
     */
    private int getDecibel(float y) {
        if (y == getHeight() - 40)
            return -12;
        else if (y == 40f)
            return 12;
        else
            return 13 - Math.round(y / mStep);
    }
}
