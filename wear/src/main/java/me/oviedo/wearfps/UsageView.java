package me.oviedo.wearfps;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class UsageView extends View {
    private int mBarColor = getResources().getColor(R.color.white);
    private float mPercent = 100;
    private int mDir = 1;
    private float thick = 40;
    //private Drawable mExampleDrawable;

    private Path mBarPath;
    //private Path mDiffPath;
    //private Path mFinalPath;

    //private TextPaint mTextPaint;
    private Paint mBarPaint;
    //private float mTextWidth;
    //private float mTextHeight;

    private RectF extRect, intRect;

    private Matrix mirrorMatrix;

    private float h, w, startAngle, sweepAngle;
    //private int vertices = 0;
    //private float thick = 40;

    //private double bottomPoint = 0, topPoint = 0, percentPoint = 0;

    public UsageView(Context context) {
        super(context);
        init(null, 0);
    }

    public UsageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public UsageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.UsageView, defStyle, 0);

        mBarColor = a.getColor(R.styleable.UsageView_barColor, mBarColor);

        mPercent = a.getFloat(R.styleable.UsageView_percent, mPercent);

        mDir = a.getInt(R.styleable.UsageView_dir, mDir);

        thick = a.getFloat(R.styleable.UsageView_thick, thick);

        a.recycle();

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setColor(mBarColor);
        mBarPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mBarPath = new Path();
        extRect = new RectF();
        intRect = new RectF();
        mirrorMatrix = new Matrix();

        // Set up a default TextPaint object
        /*mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(getResources().getColor(R.color.white));*/

        // Update TextPaint and text measurements from attributes
        //invalidateTextPaintAndMeasurements();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float xpad = (float)(getPaddingLeft() + getPaddingRight());
        float ypad = (float)(getPaddingTop() + getPaddingBottom());

        this.w = (float)w - xpad;
        this.h = (float)h - ypad;

        extRect.set(0, 0, w*2, h);
        intRect.set(thick, thick, w*2 - thick, h - thick);

        if (mDir == 0) {
            mirrorMatrix.preScale(-1, 1);
            mirrorMatrix.preTranslate(-w, 0);
        }

        invalidatePercentage();

    }

    private void invalidatePercentage() {
        startAngle = 90;
        sweepAngle = (1.8f*mPercent);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mBarPath.rewind();

        mBarPath.arcTo(extRect, startAngle, sweepAngle);
        mBarPath.arcTo(intRect, startAngle + sweepAngle, -sweepAngle);
        //mBarPath.close();

        //canvas.drawPath(mDiffPath, mBarPaint);

        if (mDir == 0) mBarPath.transform(mirrorMatrix);


        //p.cubicTo(0, h, 0, 0, w, 0);
        //p.lineTo(0, w);

        canvas.drawPath(mBarPath, mBarPaint);

        //canvas.drawPath();
    }

    public float getPercent() {
        return mPercent;
    }

    public void setPercent(float percentage) {
        mPercent = percentage;
        invalidatePercentage();
        invalidate();
        requestLayout();
    }


}
