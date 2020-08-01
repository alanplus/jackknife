package com.lwh.jackknife.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;

public class StarShape extends Shape {

    private float mXA = 400;
    private float mYA = 200;
    private int mSide = 100; //五角星边长
    private int mColor = Color.YELLOW;
    private int mRotateAngle;

    @Override
    public void draw(Canvas canvas, Paint paint) {
        Path path = new Path();
        float[] points = getPoints(mXA, mYA, mSide);
        for (int i = 0; i < points.length - 1; i++) {
            path.lineTo(points[i], points[i += 1]);
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(mRotateAngle);
        path.transform(matrix);
        paint.setColor(mColor);
        canvas.drawPath(path, paint);
    }

    public void setXA(float x) {
        this.mXA = x;
    }

    public void setYA(float y) {
        this.mYA = y;
    }

    public void setRotateAngle(int angle) {
        this.mRotateAngle = angle;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    /**
     * @param xA   起始点位置A的x轴绝对位置
     * @param yA   起始点位置A的y轴绝对位置
     * @param side 五角星边的边长
     */
    public float[] getPoints(float xA, float yA, int side) {
        float xB;
        float xC;
        float xD;
        float xE;
        float yB;
        float yC;
        float yD;
        float yE;
        xD = (float) (xA - side * Math.sin(Math.toRadians(18)));
        xC = (float) (xA + side * Math.sin(Math.toRadians(18)));
        yD = yC = (float) (yA + Math.cos(Math.toRadians(18)) * side);
        yB = yE = (float) (yA + Math.sqrt(Math.pow((xC - xD), 2) - Math.pow((side / 2), 2)));
        xB = xA + (side / 2);
        xE = xA - (side / 2);
        return new float[]{yA, yA, xD, yD, xB, yB, xE, yE, xC, yC, xA, yA};
    }
}
