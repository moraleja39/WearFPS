package me.oviedo.wearfps;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class LoadView extends View {
    private int mPercentage = 0;
    private int mBackgroundColor = Color.BLACK;

    private TextPaint mTextPaint;
    private Paint mBarPaint;

    private String mPercentText = "0%";
    private Rect mPercentBar;

    private float mTextWidth;
    private float mTextHeight;

    private int width, height;

    public LoadView(Context context) {
        super(context);
        init(null, 0);
    }

    public LoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LoadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.LoadView, defStyle, 0);

        mPercentage = a.getInt(R.styleable.LoadView_percentage, mPercentage);

        mBackgroundColor = a.getColor(R.styleable.LoadView_backgroundColor, mBackgroundColor);

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(40);

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mPercentBar = new Rect();

        // Update TextPaint and text measurements from attributes
        invalidatePercentage();
        invalidateColor();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        invalidateBar();
    }

    private void invalidatePercentage() {
        mPercentText = mPercentage + "%";
        mTextWidth = mTextPaint.measureText(mPercentText);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;

        invalidateBar();
    }

    private void invalidateBar() {
        mPercentBar.set(0, 0, (int)(width*(mPercentage/100f)), height);
    }

    private void invalidateColor() {
        mBarPaint.setColor(mBackgroundColor);
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


        // Draw the bar
        canvas.drawRect(mPercentBar, mBarPaint);

        // Draw the text.
        canvas.drawText(mPercentText, (width - mTextWidth) / 2, (height + mTextHeight) / 2, mTextPaint);

    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        invalidateColor();
    }

    public int getPercentage() { return mPercentage; }

    public void setPercentage(int percentage) {
        mPercentage = percentage;
        invalidatePercentage();
    }
}
