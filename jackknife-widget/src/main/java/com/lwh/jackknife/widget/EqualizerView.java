package com.lwh.jackknife.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class EqualizerView extends View {

    private Context mContext;
    private Paint mPaint;
    private Paint mNodePaint;
    private Paint mNodeConnectPaint;
    private TextPaint mFreqPaint;
    private int mWidth, mHeight;
    private PointF[] mPoints;
    private final int STATE_NONE = 0;
    private final int STATE_TOUCH_DOWN = 1;
    private final int STATE_TOUCH_MOVE = 2;
    private final int STATE_TOUCH_UP = 3;
    private int STATE_NOW = STATE_NONE;

    private int[] mDecibels;
    private int[] mFreqs;
    private float mRadius;
    private float mStep;
    private int mBandsNum = 5;
    private OnUpdateDecibelListener mOnUpdateDecibelListener;

    public EqualizerView(Context context) {
        this(context, null);
    }

    public EqualizerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EqualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EqualizerView);
        mBandsNum = a.getInt(R.styleable.EqualizerView_ev_bandsNum, 5);
        mDecibels = new int[mBandsNum];
        mFreqs = new int[mBandsNum];
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

    public int getBandsNum() {
        return mBandsNum;
    }

    public void setBandsNum(int bandsNum) {
        this.mBandsNum = bandsNum;
        invalidate();
    }

    public int[] getFreqs() {
        return mFreqs;
    }

    public void setFreqs(int[] freqs) {
        this.mFreqs = freqs;
        invalidate();
    }

    public void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mNodePaint = new Paint();
        mNodePaint.setAntiAlias(true);
        mNodePaint.setColor(mContext.getResources().getColor(R.color.forest_green)); //圆圈的颜色
        mNodePaint.setStrokeWidth(6);
        mNodePaint.setStyle(Paint.Style.STROKE);
        mNodeConnectPaint = new Paint();
        mNodeConnectPaint.setAntiAlias(true);
        mNodeConnectPaint.setStrokeWidth(50);
        mNodeConnectPaint.setStyle(Paint.Style.FILL);
        mNodeConnectPaint.setColor(mContext.getResources().getColor(R.color.forest_green));  //圆圈填充的颜色和连线的颜色
        mFreqPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mFreqPaint.setColor(mContext.getResources().getColor(R.color.light_gray));
        mPoints = new PointF[12];
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
        mStep = mHeight - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                mContext.getResources().getDisplayMetrics()) / 26;    //-12到12共26份
        canvas.drawColor(mContext.getResources().getColor(R.color.white));  //背景颜色
        int stepSize = mWidth / mBandsNum;
        mPoints[0] = new PointF(-50, mStep * 13);
        mPoints[11] = new PointF(mWidth + 50, mStep * 13);
        if ((STATE_NOW == STATE_NONE)) {
            for (int i = 1; i <= mBandsNum; i++) {
                float cx = stepSize * i, cy = mStep * (mDecibels[i - 1] + 13);
                mPoints[i] = new PointF(cx, cy);
            }
            refreshView(canvas, stepSize);
        } else {
            refreshView(canvas, stepSize);
        }
    }

    private void refreshView(Canvas canvas, int stepSize) {
        for (int i = 1; i <= mBandsNum; i++) {
            float cx = stepSize * i, cy = mPoints[i].y;
            if (i == mIndex && STATE_NOW != STATE_TOUCH_UP) {
                mRadius = 50;
            } else {
                mRadius = 40;
            }
            canvas.drawCircle(cx, cy, mRadius, mNodePaint);
            canvas.drawCircle(cx, cy, mRadius - 6, mNodeConnectPaint);
            mPaint.setColor(mContext.getResources().getColor(R.color.forest_green));    //下面的线的颜色
            mPaint.setStrokeWidth(6);
            canvas.drawLine(cx, cy + mRadius + 3, stepSize * i, mHeight, mPaint);
            mPaint.setColor(mContext.getResources().getColor(R.color.light_gray));  //上面的线的颜色
            canvas.drawLine(cx, cy - mRadius - 3, stepSize * i, 0, mPaint);
        }
        Paint.FontMetrics fontMetrics = mFreqPaint.getFontMetrics();
        for (int i=0;i<mFreqs.length;i++) {
            String text = formatHz(mFreqs[i]);
            float textWidth = mFreqPaint.measureText(text);
            float x = mWidth / mFreqs.length * i + (mWidth / mFreqs.length - textWidth) / 2;
            float centerY = mHeight - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    10, mContext.getResources().getDisplayMetrics());
            float baselineY = centerY + (fontMetrics.bottom-fontMetrics.top)/2-fontMetrics.bottom;
            canvas.drawText(text, x, baselineY, mFreqPaint);
        }
    }

    private String formatHz(int freq) {
        if (freq > 1000) {
            return (freq / 1000) + "kHz";
        } else {
            return String.valueOf(freq) + "Hz";
        }
    }

    private int mLastY = 0;
    private int mIndex = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX(), y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIndex = findNodeIndex(x, y);
                if (mIndex != 0) {
                    STATE_NOW = STATE_TOUCH_DOWN;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = y - mLastY;
                if (mIndex != 0) {
                    STATE_NOW = STATE_TOUCH_MOVE;
                    mPoints[mIndex].y += deltaY;
                    if (y <= 40)
                        mPoints[mIndex].y = 40;
                    if (y >= mHeight - 40)
                        mPoints[mIndex].y = mHeight - 40;
                    mDecibels[mIndex - 1] = getDecibel(mPoints[mIndex].y);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIndex != 0) {
                    STATE_NOW = STATE_TOUCH_UP;
                    if (mDecibels[mIndex - 1] != 0 && mDecibels[mIndex - 1] != -12 &&
                            mDecibels[mIndex - 1] != 12) {
                        float lastY = mStep * (-mDecibels[mIndex - 1] + 13);
                        mPoints[mIndex].y = lastY;
                    } else if (mDecibels[mIndex - 1] == 0)
                        mPoints[mIndex].y = mStep * 13;
                    invalidate();
                    mOnUpdateDecibelListener.onUpdateDecibel(mDecibels);
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
        if (y == getHeight() - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                20, mContext.getResources().getDisplayMetrics()) - 40)
            return -12;
        else if (y == 40f)
            return 12;
        else
            return 13 - Math.round(y / mStep);
    }
}
