package com.genesys.shape.builder;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.view.View;

import com.genesys.shape.drawable.ShapeType;

/**
 * Builder that handles ripple foreground for any Shape view.
 * Used internally by all Shape* views to support shape_ripple_enabled,
 * shape_ripple_color, and shape_ripple_radius attributes.
 */
public class RippleBuilder {

    /** Default semi-transparent white ripple */
    private static final int DEFAULT_RIPPLE_COLOR = 0x20FFFFFF;
    private static final float DEFAULT_FIXED_OFFSET = 1.1f;

    private final View mView;
    private final ShapeDrawableBuilder mShapeDrawableBuilder;

    private boolean mRippleEnabled = false;
    private int mRippleColor = DEFAULT_RIPPLE_COLOR;
    private float mRippleRadius = -1f;

    public RippleBuilder(View view, ShapeDrawableBuilder shapeDrawableBuilder,
                         TypedArray typedArray, int rippleEnabledIndex,
                         int rippleColorIndex, int rippleRadiusIndex) {
        mView = view;
        mShapeDrawableBuilder = shapeDrawableBuilder;
        mShapeDrawableBuilder.setRippleBuilder(this);

        mRippleEnabled = typedArray.getBoolean(rippleEnabledIndex, false);
        if (typedArray.hasValue(rippleColorIndex)) {
            mRippleColor = typedArray.getColor(rippleColorIndex, DEFAULT_RIPPLE_COLOR);
        }
        if (typedArray.hasValue(rippleRadiusIndex)) {
            mRippleRadius = typedArray.getDimension(rippleRadiusIndex, 0f);
        }
    }

    /**
     * Apply the ripple effect if enabled. Call after the TypedArray is recycled
     * and after intoBackground().
     */
    public void apply() {
        if (mRippleEnabled) {
            applyRipple();
        }
    }

    private void applyRipple() {
        mView.setClickable(true);

        Shape shape;
        if (mShapeDrawableBuilder.getType() == ShapeType.OVAL) {
            shape = new OvalShape();
        } else {
            float topLeft, topRight, bottomLeft, bottomRight;
            if (mRippleRadius >= 0f) {
                topLeft = mRippleRadius;
                topRight = mRippleRadius;
                bottomLeft = mRippleRadius;
                bottomRight = mRippleRadius;
            } else {
                topLeft = mShapeDrawableBuilder.getTopLeftRadius();
                topRight = mShapeDrawableBuilder.getTopRightRadius();
                bottomLeft = mShapeDrawableBuilder.getBottomLeftRadius();
                bottomRight = mShapeDrawableBuilder.getBottomRightRadius();
            }

            float tl = topLeft + DEFAULT_FIXED_OFFSET;
            float tr = topRight + DEFAULT_FIXED_OFFSET;
            float bl = bottomLeft + DEFAULT_FIXED_OFFSET;
            float br = bottomRight + DEFAULT_FIXED_OFFSET;

            float[] radii = new float[]{
                    tl, tl, tr, tr, br, br, bl, bl
            };
            shape = new RoundRectShape(radii, null, null);
        }
        ShapeDrawable mask = new ShapeDrawable(shape);

        RippleDrawable rippleDrawable = new RippleDrawable(
                ColorStateList.valueOf(mRippleColor),
                null,
                mask
        );

        Drawable foregroundDrawable = rippleDrawable;
        Rect insets = mShapeDrawableBuilder.getShadowInsets();
        if (insets != null && (insets.left > 0 || insets.top > 0 || insets.right > 0 || insets.bottom > 0)) {
            foregroundDrawable = new InsetDrawable(rippleDrawable, insets.left, insets.top, insets.right, insets.bottom);
        }

        mView.setForeground(foregroundDrawable);
    }

    public void setRippleEnabled(boolean enabled) {
        mRippleEnabled = enabled;
        if (enabled) {
            applyRipple();
        } else {
            mView.setForeground(null);
        }
    }

    public boolean isRippleEnabled() {
        return mRippleEnabled;
    }

    public void setRippleColor(int color) {
        mRippleColor = color;
        if (mRippleEnabled) {
            applyRipple();
        }
    }

    public void setRippleRadius(float radius) {
        mRippleRadius = radius;
        if (mRippleEnabled) {
            applyRipple();
        }
    }

    public int getRippleColor() {
        return mRippleColor;
    }
}
