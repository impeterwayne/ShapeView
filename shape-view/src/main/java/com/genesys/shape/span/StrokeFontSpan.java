package com.genesys.shape.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.genesys.shape.config.ITextViewAttribute;
import com.genesys.shape.other.TextViewAttribute;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeView
 *    time   : 2022/05/04
 *    desc   : Stroke Font Span
 */
public class StrokeFontSpan extends CommonFontSpan {

    /** Stroke Paint */
    private final Paint mStrokePaint = new Paint();

    private int mTextStrokeColor;
    private int mTextStrokeSize;

    /** Text Color */
    private int mTextSolidColor;

    /**
     * Build a Spannable object with text stroke
     */
    public static SpannableStringBuilder buildStrokeFontSpannable(TextView textView, CharSequence text, int textStrokeColor, int textStrokeSize) {
        return buildStrokeFontSpannable(new TextViewAttribute(textView), text, textStrokeColor, textStrokeSize);
    }

    public static SpannableStringBuilder buildStrokeFontSpannable(ITextViewAttribute textViewAttribute, CharSequence text, int textStrokeColor, int textStrokeSize) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        StrokeFontSpan span = new StrokeFontSpan(textViewAttribute)
            .setTextStrokeColor(textStrokeColor)
            .setTextStrokeSize(textStrokeSize);
        builder.setSpan(span, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public StrokeFontSpan(ITextViewAttribute textViewAttribute) {
        super(textViewAttribute);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull Paint paint, CharSequence text, float textWidth,
                       int start, int end, float x, int top, int y, int bottom) {
        mStrokePaint.set(paint);
        // Clear the Shader here to avoid bringing in the text gradient color
        mStrokePaint.setShader(null);
        // Set anti-aliasing
        mStrokePaint.setAntiAlias(true);
        // Set dithering
        mStrokePaint.setDither(true);
        mStrokePaint.setTextSize(paint.getTextSize());
        // Stroke width
        mStrokePaint.setStrokeWidth(mTextStrokeSize);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        // Set bold
        //mStrokePaint.setFakeBoldText(true);
        mStrokePaint.setColor(mTextStrokeColor);
        // Draw text stroke
        canvas.drawText(text, start, end, x, y, mStrokePaint);

        // Draw original text content
        if (mTextSolidColor != Color.TRANSPARENT) {
            paint.setColor(mTextSolidColor);
            canvas.drawText(text, start, end, x, y, paint);
        }
    }

    public StrokeFontSpan setTextSolidColor(@ColorInt int color) {
        mTextSolidColor = color;
        return this;
    }

    public StrokeFontSpan setTextStrokeColor(@ColorInt int color) {
        mTextStrokeColor = color;
        return this;
    }

    public StrokeFontSpan setTextStrokeSize(int size) {
        mTextStrokeSize = size;
        return this;
    }
}
