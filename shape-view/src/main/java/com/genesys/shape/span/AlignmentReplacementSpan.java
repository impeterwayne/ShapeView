package com.genesys.shape.span;

import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.style.AlignmentSpan;
import android.text.style.ReplacementSpan;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;

import com.genesys.shape.config.ITextViewAttribute;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeView
 *    time   : 2024/09/15
 *    desc   : ReplacementSpan with custom drawing gravity
 */
public abstract class AlignmentReplacementSpan extends ReplacementSpan implements AlignmentSpan {

    @NonNull
    private final ITextViewAttribute mTextAttribute;

    public AlignmentReplacementSpan(@NonNull ITextViewAttribute textViewAttribute) {
        mTextAttribute = textViewAttribute;
    }

    @NonNull
    public ITextViewAttribute getTextAttribute() {
        return mTextAttribute;
    }

    @Override
    public Alignment getAlignment() {
        // Get TextView text gravity
        int gravity = mTextAttribute.getTextGravity();

        // Set AlignmentSpan based on gravity
        Layout.Alignment alignment;

        // Get current layout direction (LTR or RTL)
        boolean isRtl = (mTextAttribute.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL);

        // The order of judgment must be: left and right, start and end, center and center_horizontal
        if (hasFlag(gravity, Gravity.LEFT)) {
            // Gravity.LEFT is always left aligned, need to judge based on layout direction
            alignment = isRtl ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_NORMAL;
        } else if (hasFlag(gravity, Gravity.RIGHT)) {
            // Gravity.RIGHT is always right aligned, need to judge based on layout direction
            alignment = isRtl ? Layout.Alignment.ALIGN_NORMAL : Layout.Alignment.ALIGN_OPPOSITE;
        } else if (hasFlag(gravity, Gravity.START)) {
            // Gravity.START equals ALIGN_NORMAL, automatically adjusts based on layout direction
            alignment = Layout.Alignment.ALIGN_NORMAL;
        } else if (hasFlag(gravity, Gravity.END)) {
            // Gravity.END equals ALIGN_OPPOSITE, automatically adjusts based on layout direction
            alignment = Layout.Alignment.ALIGN_OPPOSITE;
        } else if (hasFlag(gravity, Gravity.CENTER) ||
            hasFlag(gravity, Gravity.CENTER_HORIZONTAL)) {
            // Center aligned, here alignment only supports horizontal center, not vertical center
            alignment = Layout.Alignment.ALIGN_CENTER;
        } else {
            // Default left aligned
            alignment = Layout.Alignment.ALIGN_NORMAL;
        }

        // Bad news: this method only supports horizontal text gravity setting, not vertical
        // Good news: when setting a custom Span, TextView text gravity setting only fails horizontally, vertically it still works
        return alignment;
    }

    /**
     * Check if the integer contains a flag
     */
    protected boolean hasFlag(int i, int flag) {
        return (i & flag) == flag;
    }
}