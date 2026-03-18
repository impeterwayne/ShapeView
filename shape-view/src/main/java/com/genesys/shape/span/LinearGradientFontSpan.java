package com.genesys.shape.span;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Shader;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.genesys.shape.config.ITextViewAttribute;
import com.genesys.shape.other.TextViewAttribute;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeView
 *    time   : 2021/08/17
 *    desc   : Span that supports direct definition of text gradient color
 */
public class LinearGradientFontSpan extends CommonFontSpan {

    /** Horizontal gradient orientation */
    public static final int GRADIENT_ORIENTATION_HORIZONTAL = LinearLayout.HORIZONTAL;
    /** Vertical gradient orientation */
    public static final int GRADIENT_ORIENTATION_VERTICAL = LinearLayout.VERTICAL;

     /**
     * Build a Spannable object with text gradient color
     */
    public static SpannableStringBuilder buildLinearGradientFontSpannable(TextView textView, CharSequence text, int[] colors, float[] positions, int orientation) {
        return buildLinearGradientFontSpannable(new TextViewAttribute(textView), text, colors, positions, orientation);
    }

    public static SpannableStringBuilder buildLinearGradientFontSpannable(ITextViewAttribute textViewAttribute, CharSequence text, int[] colors, float[] positions, int orientation) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        LinearGradientFontSpan span = new LinearGradientFontSpan(textViewAttribute)
                .setTextGradientColor(colors)
                .setTextGradientOrientation(orientation)
                .setTextGradientPositions(positions);
        builder.setSpan(span, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /** Text gradient orientation */
    private int mTextGradientOrientation;
    /** Text gradient color group */
    private int[] mTextGradientColor;
    /** Text gradient position group */
    private float[] mTextGradientPositions;

    // Cached LinearGradient to avoid per-frame allocation
    private LinearGradient mCachedGradient;
    private float mCachedX;
    private float mCachedTextWidth;
    private int mCachedFontHeight;
    private int mCachedOrientation = -1;
    private int mCachedColorsHash;
    private int mCachedPositionsHash;

    public LinearGradientFontSpan(ITextViewAttribute textViewAttribute) {
        super(textViewAttribute);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull Paint paint, CharSequence text, float textWidth,
                       int start, int end, float x, int top, int y, int bottom) {
        final Shader oldShader = paint.getShader();
        try {
            if (mTextGradientColor == null || mTextGradientColor.length < 2) {
                paint.setShader(null);
                canvas.drawText(text, start, end, x, y, paint);
                return;
            }

            final float[] safePositions = sanitizeGradientPositions(mTextGradientColor, mTextGradientPositions);
            final int colorsHash = java.util.Arrays.hashCode(mTextGradientColor);
            final int positionsHash = java.util.Arrays.hashCode(safePositions);
            final LinearGradient linearGradient;

            if (mTextGradientOrientation == GRADIENT_ORIENTATION_VERTICAL) {
                FontMetricsInt fontMetrics = paint.getFontMetricsInt();
                int fontHeight = Math.max(fontMetrics.bottom - fontMetrics.top, 0);
                if (fontHeight <= 0) {
                    paint.setShader(null);
                    canvas.drawText(text, start, end, x, y, paint);
                    return;
                }
                if (mCachedGradient != null
                        && mCachedOrientation == mTextGradientOrientation
                        && mCachedFontHeight == fontHeight
                        && mCachedColorsHash == colorsHash
                        && mCachedPositionsHash == positionsHash) {
                    linearGradient = mCachedGradient;
                } else {
                    linearGradient = new LinearGradient(0, 0, 0, fontHeight,
                        mTextGradientColor, safePositions, Shader.TileMode.CLAMP);
                    mCachedGradient = linearGradient;
                    mCachedFontHeight = fontHeight;
                    mCachedOrientation = mTextGradientOrientation;
                    mCachedColorsHash = colorsHash;
                    mCachedPositionsHash = positionsHash;
                }
            } else {
                if (!Float.isFinite(x) || !Float.isFinite(textWidth) || textWidth <= 0f) {
                    paint.setShader(null);
                    canvas.drawText(text, start, end, x, y, paint);
                    return;
                }
                if (mCachedGradient != null
                        && mCachedOrientation == mTextGradientOrientation
                        && mCachedX == x
                        && mCachedTextWidth == textWidth
                        && mCachedColorsHash == colorsHash
                        && mCachedPositionsHash == positionsHash) {
                    linearGradient = mCachedGradient;
                } else {
                    linearGradient = new LinearGradient(x, 0, x + textWidth, 0,
                            mTextGradientColor, safePositions, Shader.TileMode.CLAMP);
                    mCachedGradient = linearGradient;
                    mCachedX = x;
                    mCachedTextWidth = textWidth;
                    mCachedOrientation = mTextGradientOrientation;
                    mCachedColorsHash = colorsHash;
                    mCachedPositionsHash = positionsHash;
                }
            }
            paint.setShader(linearGradient);
            canvas.drawText(text, start, end, x, y, paint);
        } finally {
            paint.setShader(oldShader);
        }
    }

    public LinearGradientFontSpan setTextGradientOrientation(int orientation) {
        if (orientation != GRADIENT_ORIENTATION_HORIZONTAL &&
                orientation != GRADIENT_ORIENTATION_VERTICAL) {
            orientation = GRADIENT_ORIENTATION_HORIZONTAL;
        }
        if (mTextGradientOrientation != orientation) {
            mTextGradientOrientation = orientation;
            invalidateGradientCache();
        }
        return this;
    }

    public LinearGradientFontSpan setTextGradientColor(int[] colors) {
        mTextGradientColor = colors != null ? colors.clone() : null;
        invalidateGradientCache();
        return this;
    }

    public LinearGradientFontSpan setTextGradientPositions(float[] positions) {
        mTextGradientPositions = positions != null ? positions.clone() : null;
        invalidateGradientCache();
        return this;
    }

    private void invalidateGradientCache() {
        mCachedGradient = null;
        mCachedX = 0f;
        mCachedTextWidth = 0f;
        mCachedFontHeight = 0;
        mCachedOrientation = -1;
        mCachedColorsHash = 0;
        mCachedPositionsHash = 0;
    }

    @Nullable
    private static float[] sanitizeGradientPositions(@NonNull int[] colors, @Nullable float[] positions) {
        if (positions == null) {
            return null;
        }
        if (positions.length != colors.length || positions.length == 0) {
            return null;
        }
        float lastValue = Float.NEGATIVE_INFINITY;
        for (float position : positions) {
            if (!Float.isFinite(position) || position < 0f || position > 1f || position < lastValue) {
                return null;
            }
            lastValue = position;
        }
        return positions;
    }
}
