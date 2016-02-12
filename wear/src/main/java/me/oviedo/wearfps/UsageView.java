package me.oviedo.wearfps;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class UsageView extends View {
    //private String mExampleString = "Hola!"; // TODO: use a default from R.string...
    //private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    //private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private int mBarColor = getResources().getColor(R.color.white);
    private float mPercent = 100;
    private int mDir = 1;
    private float thick = 40;
    //private Drawable mExampleDrawable;

    private Path mBarPath;
    private Path mDiffPath;
    private Path mFinalPath;

    //private TextPaint mTextPaint;
    private Paint mBarPaint;
    //private float mTextWidth;
    //private float mTextHeight;

    private Matrix mirrorMatrix;

    private float h, w, x;
    private int vertices = 0;
    //private float thick = 40;

    private double bottomPoint = 0, topPoint = 0, percentPoint = 0;

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

        /*mExampleString = a.getString(
                R.styleable.UsageView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.UsageView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.UsageView_exampleDimension,
                mExampleDimension);*/

        mBarColor = a.getColor(R.styleable.UsageView_barColor, mBarColor);

        mPercent = a.getFloat(R.styleable.UsageView_percent, mPercent);

        mDir = a.getInt(R.styleable.UsageView_dir, mDir);

        thick = a.getFloat(R.styleable.UsageView_thick, thick);

        /*if (a.hasValue(R.styleable.UsageView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.UsageView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }*/

        a.recycle();

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setColor(mBarColor);
        mBarPaint.setStyle(Paint.Style.FILL);

        mBarPath = new Path();
        mDiffPath = new Path();
        mFinalPath = new Path();
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

        bottomPoint = Math.acos(h/(2*Math.sqrt((Math.pow(h,2)/4)+Math.pow(w,2))));

        topPoint = Math.PI - bottomPoint;
        bottomPoint = bottomPoint/Math.PI * 100;
        topPoint = topPoint/Math.PI * 100;

        Log.d("UsageView", "bottomPoint: " + bottomPoint + ", topPoint: " + topPoint);

        if (mDir == 0) {
            mirrorMatrix.preScale(-1, 1);
            mirrorMatrix.preTranslate(-w, 0);
        }

        mBarPath.reset();
        mBarPath.moveTo(w, h);
        mBarPath.cubicTo(0, h, 0, h / 2, 0, h / 2);
        //mBarPath.quadTo((1 - rat) * w, h * rat, 0, h / 2);
        mBarPath.cubicTo(0, h / 2, 0, 0, w, 0);
        //mBarPath.quadTo((1-rat)*w, (1-rat)*h, w, 0);

        mBarPath.lineTo(w, thick);
        mBarPath.cubicTo(thick, thick, thick, h / 2, thick, h / 2);
        //mBarPath.quadTo(thick + (1-rat)*w, thick + h*rat, thick, h/2);
        mBarPath.cubicTo(thick, h / 2, thick, h - thick, w, h - thick);
        mBarPath.close();

        //mBarPath.quadTo(thick + (1-rat)*w, (1-rat)*h-thick, w, h-thick);

        invalidatePercentage();

        // Figure out how big we can make the pie.
        //float diameter = Math.min(ww, hh);
    }

    private void invalidateTextPaintAndMeasurements() {
        //mTextPaint.setTextSize(mExampleDimension);
        //mTextPaint.setColor(mExampleColor);
        //mTextWidth = mTextPaint.measureText(mExampleString);

        //Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        //mTextHeight = fontMetrics.bottom;
    }

    double d;

    private void invalidatePercentage() {
        if (mPercent < bottomPoint) {
            d = (mPercent/100)*Math.PI;
            this.x = (float) ( w - (this.h/2) * Math.tan(d) );
            vertices = 3;
            //Log.d("UsageView", "x: " + this.x);
        } else if (mPercent < topPoint) {

            d = mPercent / 100 * Math.PI;
            this.x = (float)((h/2) + (w / Math.tan(d)));
            vertices = 2;

        } else {
            d = Math.PI - (mPercent/100)*Math.PI;
            this.x = (float) ( w - (this.h/2) * Math.tan(d) );
            vertices = 1;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        /*int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;*/

        // Draw the example drawable on top of the text.
        //canvas.drawArc(0, 0, 320, 60, 180, 360, false, mBarPaint);
        //canvas.drawArc(50, 50, 80, 140, 180, 360, true, mBarPaint);

        mDiffPath.rewind();
        mDiffPath.moveTo(w, h / 2);
        if (vertices == 3) {
            mDiffPath.lineTo(x, h);
            mDiffPath.lineTo(0, h);
            mDiffPath.lineTo(0, 0);
            mDiffPath.lineTo(w, 0);
        } else if (vertices == 2) {
            mDiffPath.lineTo(0, x);
            mDiffPath.lineTo(0, 0);
            mDiffPath.lineTo(w, 0);
        } else if (vertices == 1) {
            mDiffPath.lineTo(x, 0);
            mDiffPath.lineTo(w, 0);
        }

        mDiffPath.close();

        //canvas.drawPath(mDiffPath, mBarPaint);

        mFinalPath.op(mBarPath, mDiffPath, Path.Op.DIFFERENCE);

        if (mDir == 0) mFinalPath.transform(mirrorMatrix);


        //p.cubicTo(0, h, 0, 0, w, 0);
        //p.lineTo(0, w);

        canvas.drawPath(mFinalPath, mBarPaint);
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
