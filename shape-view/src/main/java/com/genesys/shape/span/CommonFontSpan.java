package com.genesys.shape.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.genesys.shape.config.ITextViewAttribute;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeView
 *    time   : 2022/05/04
 *    desc   : Common Span class
 */
public abstract class CommonFontSpan extends AlignmentReplacementSpan {

    /** Measured text width */
    private float mMeasureTextWidth;

    public CommonFontSpan(ITextViewAttribute textViewAttribute) {
        super(textViewAttribute);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fontMetricsInt) {
        mMeasureTextWidth = onMeasure(paint, fontMetricsInt, text, start, end);
        // This section cannot be removed. If the font height is not set, the draw method may not be called.
        // For details, see: https://stackoverflow.com/questions/20069537/replacementspans-draw-method-isnt-called
        Paint.FontMetricsInt metrics = paint.getFontMetricsInt();
        if (fontMetricsInt != null) {
            fontMetricsInt.top = metrics.top;
            fontMetricsInt.ascent = metrics.ascent;
            fontMetricsInt.descent = metrics.descent;
            fontMetricsInt.bottom = metrics.bottom;
        }
        return (int) mMeasureTextWidth;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        int alpha = paint.getAlpha();
        // Determine if transparency is set for the paint
        if (alpha != 255) {
            // If so, set it to opaque
            paint.setAlpha(255);
        }

        // Get text and canvas width
        float textWidth = paint.measureText(text, start, end);
        // Adjust x coordinate based on alignment
        float drawX;

        ITextViewAttribute textAttribute = getTextAttribute();
        float canvasWidth = canvas.getWidth() - textAttribute.getPaddingLeft() - textAttribute.getPaddingRight();
        // Get TextView text gravity
        int gravity = textAttribute.getTextGravity();
        // Get current layout direction (LTR or RTL)
        boolean isRtl = (textAttribute.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL);

        // The order of judgment must be: left and right, start and end, center and center_horizontal
        if (hasFlag(gravity, Gravity.LEFT)) {
            // Left aligned
            drawX = x;
        } else if (hasFlag(gravity, Gravity.RIGHT)) {
            // Right aligned
            drawX = canvasWidth - textWidth;
        } else if ((isRtl && hasFlag(gravity, Gravity.END)) || (!isRtl && hasFlag(gravity, Gravity.START))) {
            // Left aligned or START aligned (adapting to layout direction)
            drawX = x;
        } else if ((isRtl && hasFlag(gravity, Gravity.START)) || (!isRtl && hasFlag(gravity, Gravity.END))) {
            // Right aligned or END aligned (adapting to layout direction)
            drawX = Math.max(canvasWidth - textWidth, 0);
        } else if (hasFlag(gravity, Gravity.CENTER) || hasFlag(gravity, Gravity.CENTER_HORIZONTAL)) {
            // Center aligned
            drawX = Math.max((canvasWidth - textWidth) / 2, 0);
        } else {
            // Default left aligned
            drawX = x;
        }

        // Draw text
        onDraw(canvas, paint, text, textWidth, start, end, drawX, top, y, bottom);

        // Restore the paint's transparency after drawing
        paint.setAlpha(alpha);
    }

    public float onMeasure(@NonNull Paint paint, @Nullable Paint.FontMetricsInt fontMetricsInt, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end) {
        return paint.measureText(text, start, end);
    }

    public abstract void onDraw(@NonNull Canvas canvas, @NonNull Paint paint, CharSequence text, float textWidth, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom);
}
