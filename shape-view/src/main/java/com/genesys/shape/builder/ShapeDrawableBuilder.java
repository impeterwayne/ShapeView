package com.genesys.shape.builder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.genesys.shape.config.IShapeDrawableStyleable;
import com.genesys.shape.drawable.InnerShadow;
import com.genesys.shape.drawable.ShapeDrawable;
import com.genesys.shape.drawable.ShapeGradientOrientation;
import com.genesys.shape.drawable.ShapeGradientType;
import com.genesys.shape.drawable.ShapeGradientTypeLimit;
import com.genesys.shape.drawable.ShapeType;
import com.genesys.shape.drawable.ShapeTypeLimit;
import com.genesys.shape.other.ExtendStateListDrawable;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeView
 *    time   : 2021/08/28
 *    desc   : ShapeDrawable Builder class
 */
public final class ShapeDrawableBuilder {

    private static final int NO_COLOR = Color.TRANSPARENT;

    private final View mView;

    @ShapeTypeLimit
    private int mType;
    private int mWidth;
    private int mHeight;

    private int mSolidColor;
    private ColorStateList mSolidColorStateList;
    private Integer mSolidPressedColor;
    private Integer mSolidCheckedColor;
    private Integer mSolidDisabledColor;
    private Integer mSolidFocusedColor;
    private Integer mSolidSelectedColor;

    private float mTopLeftRadius;
    private float mTopRightRadius;
    private float mBottomLeftRadius;
    private float mBottomRightRadius;

    private int[] mSolidGradientColors;
    private ColorStateList mSolidGradientStartColorStateList;
    private ColorStateList mSolidGradientCenterColorStateList;
    private ColorStateList mSolidGradientEndColorStateList;
    private ShapeGradientOrientation mSolidGradientOrientation;
    @ShapeGradientTypeLimit
    private int mSolidGradientType;
    private float mSolidGradientCenterX;
    private float mSolidGradientCenterY;
    private float mSolidGradientRadius;

    // Radial gradient transformations
    private float mRadialGradientAngle = 0f;
    private float mGradientRadiusX = -1f;
    private float mGradientRadiusY = -1f;
    private float mRadialStartX = Float.NaN;
    private float mRadialStartY = Float.NaN;

    // Gradient color stop positions (Figma compatibility)
    private float mGradientStartPercent = -1f;
    private float mGradientCenterPercent = -1f;
    private float mGradientEndPercent = -1f;
    private boolean mHasCustomGradientPositions = false;

    // Linear gradient extent positions (physical position in view)
    private float mLinearGradientStartX = Float.NaN;
    private float mLinearGradientStartY = Float.NaN;
    private float mLinearGradientEndX = Float.NaN;
    private float mLinearGradientEndY = Float.NaN;
    private boolean mHasCustomLinearExtent = false;

    private int mStrokeColor;
    private ColorStateList mStrokeColorStateList;
    private Integer mStrokePressedColor;
    private Integer mStrokeCheckedColor;
    private Integer mStrokeDisabledColor;
    private Integer mStrokeFocusedColor;
    private Integer mStrokeSelectedColor;

    private int[] mStrokeGradientColors;
    private ColorStateList mStrokeGradientStartColorStateList;
    private ColorStateList mStrokeGradientCenterColorStateList;
    private ColorStateList mStrokeGradientEndColorStateList;
    private ShapeGradientOrientation mStrokeGradientOrientation;

    private int mStrokeSize;
    private int mStrokeDashSize;
    private int mStrokeDashGap;

    private int mOuterShadowSize;
    private int mOuterShadowColor;
    private ColorStateList mOuterShadowColorStateList;
    private int mOuterShadowOffsetX;
    private int mOuterShadowOffsetY;

    private int mRingInnerRadiusSize;
    private float mRingInnerRadiusRatio;
    private int mRingThicknessSize;
    private float mRingThicknessRatio;

    private int mLineGravity;

    // Primary Inner Shadow
    private int mInnerShadowSize;
    private int mInnerShadowColor;
    private ColorStateList mInnerShadowColorStateList;
    private int mInnerShadowOffsetX;
    private int mInnerShadowOffsetY;

    // Secondary Inner Shadow (for bevel effects)
    private int mInnerShadow2Size;
    private int mInnerShadow2Color;
    private ColorStateList mInnerShadow2ColorStateList;
    private int mInnerShadow2OffsetX;
    private int mInnerShadow2OffsetY;

    public ShapeDrawableBuilder(View view, TypedArray typedArray, IShapeDrawableStyleable styleable) {
        mView = view;
        mType = typedArray.getInt(styleable.getShapeTypeStyleable(), ShapeType.RECTANGLE);
        mWidth = typedArray.getDimensionPixelSize(styleable.getShapeWidthStyleable(), -1);
        mHeight = typedArray.getDimensionPixelSize(styleable.getShapeHeightStyleable(), -1);

        if (typedArray.hasValue(styleable.getSolidColorStyleable())) {
            mSolidColorStateList = typedArray.getColorStateList(styleable.getSolidColorStyleable());
            if (mSolidColorStateList != null) {
                mSolidColor = mSolidColorStateList.getDefaultColor();
            } else {
                mSolidColor = typedArray.getColor(styleable.getSolidColorStyleable(), NO_COLOR);
            }
        } else {
            mSolidColor = NO_COLOR;
        }

        if (typedArray.hasValue(styleable.getSolidPressedColorStyleable())) {
            mSolidPressedColor = typedArray.getColor(styleable.getSolidPressedColorStyleable(), NO_COLOR);
        }
        if (styleable.getSolidCheckedColorStyleable() > 0 && typedArray.hasValue(styleable.getSolidCheckedColorStyleable())) {
            mSolidCheckedColor = typedArray.getColor(styleable.getSolidCheckedColorStyleable(), NO_COLOR);
        }
        if (typedArray.hasValue(styleable.getSolidDisabledColorStyleable())) {
            mSolidDisabledColor = typedArray.getColor(styleable.getSolidDisabledColorStyleable(), NO_COLOR);
        }
        if (typedArray.hasValue(styleable.getSolidFocusedColorStyleable())) {
            mSolidFocusedColor = typedArray.getColor(styleable.getSolidFocusedColorStyleable(), NO_COLOR);
        }
        if (typedArray.hasValue(styleable.getSolidSelectedColorStyleable())) {
            mSolidSelectedColor = typedArray.getColor(styleable.getSolidSelectedColorStyleable(), NO_COLOR);
        }

        int layoutDirection = getLayoutDirection(view);

        int radius = typedArray.getDimensionPixelSize(styleable.getRadiusStyleable(), 0);
        mTopLeftRadius = mTopRightRadius = mBottomLeftRadius = mBottomRightRadius = radius;

        if (typedArray.hasValue(styleable.getRadiusInTopStartStyleable())) {
            switch (layoutDirection) {
                case View.LAYOUT_DIRECTION_RTL:
                    mTopRightRadius = typedArray.getDimensionPixelSize(styleable.getRadiusInTopStartStyleable(), radius);
                    break;
                case View.LAYOUT_DIRECTION_LTR:
                default:
                    mTopLeftRadius = typedArray.getDimensionPixelSize(styleable.getRadiusInTopStartStyleable(), radius);
                    break;
            }
        }
        if (typedArray.hasValue(styleable.getRadiusInTopEndStyleable())) {
            switch (layoutDirection) {
                case View.LAYOUT_DIRECTION_RTL:
                    mTopLeftRadius = typedArray.getDimensionPixelSize(styleable.getRadiusInTopEndStyleable(), radius);
                    break;
                case View.LAYOUT_DIRECTION_LTR:
                default:
                    mTopRightRadius = typedArray.getDimensionPixelSize(styleable.getRadiusInTopEndStyleable(), radius);
                    break;
            }
        }
        if (typedArray.hasValue(styleable.getRadiusInBottomStartStyleable())) {
            switch (layoutDirection) {
                case View.LAYOUT_DIRECTION_RTL:
                    mBottomRightRadius = typedArray.getDimensionPixelSize(styleable.getRadiusInBottomStartStyleable(), radius);
                    break;
                case View.LAYOUT_DIRECTION_LTR:
                default:
                    mBottomLeftRadius = typedArray.getDimensionPixelSize(styleable.getRadiusInBottomStartStyleable(), radius);
                    break;
            }
        }
        if (typedArray.hasValue(styleable.getRadiusInBottomEndStyleable())) {
            switch (layoutDirection) {
                case View.LAYOUT_DIRECTION_RTL:
                    mBottomLeftRadius = typedArray.getDimensionPixelSize(styleable.getRadiusInBottomEndStyleable(), radius);
                    break;
                case View.LAYOUT_DIRECTION_LTR:
                default:
                    mBottomRightRadius = typedArray.getDimensionPixelSize(styleable.getRadiusInBottomEndStyleable(), radius);
                    break;
            }
        }

        if (typedArray.hasValue(styleable.getRadiusInTopLeftStyleable())) {
            mTopLeftRadius = typedArray.getDimensionPixelSize(styleable.getRadiusInTopLeftStyleable(), radius);
        }
        if (typedArray.hasValue(styleable.getRadiusInTopRightStyleable())) {
            mTopRightRadius = typedArray.getDimensionPixelSize(styleable.getRadiusInTopRightStyleable(), radius);
        }
        if (typedArray.hasValue(styleable.getRadiusInBottomLeftStyleable())) {
            mBottomLeftRadius = typedArray.getDimensionPixelSize(styleable.getRadiusInBottomLeftStyleable(), radius);
        }
        if (typedArray.hasValue(styleable.getRadiusInBottomRightStyleable())) {
            mBottomRightRadius = typedArray.getDimensionPixelSize(styleable.getRadiusInBottomRightStyleable(), radius);
        }

        if (typedArray.hasValue(styleable.getSolidGradientStartColorStyleable()) ||
            typedArray.hasValue(styleable.getSolidGradientEndColorStyleable())) {

            if (typedArray.hasValue(styleable.getSolidGradientStartColorStyleable())) {
                mSolidGradientStartColorStateList = typedArray.getColorStateList(styleable.getSolidGradientStartColorStyleable());
            }

            if (typedArray.hasValue(styleable.getSolidGradientCenterColorStyleable())) {
                mSolidGradientCenterColorStateList = typedArray.getColorStateList(styleable.getSolidGradientCenterColorStyleable());
            }

            if (typedArray.hasValue(styleable.getSolidGradientEndColorStyleable())) {
                mSolidGradientEndColorStateList = typedArray.getColorStateList(styleable.getSolidGradientEndColorStyleable());
            }

            int startColor = typedArray.getColor(styleable.getSolidGradientStartColorStyleable(), NO_COLOR);
            int centerColor = NO_COLOR;
            int endColor = typedArray.getColor(styleable.getSolidGradientEndColorStyleable(), NO_COLOR);
            boolean hasCenter = false;

            if (typedArray.hasValue(styleable.getSolidGradientCenterColorStyleable())) {
                centerColor = typedArray.getColor(styleable.getSolidGradientCenterColorStyleable(), NO_COLOR);
                hasCenter = true;
            }

            if (hasCenter) {
                mSolidGradientColors = new int[] {startColor, centerColor, endColor};
            } else {
                mSolidGradientColors = new int[] {startColor, endColor};
            }
        }

        mSolidGradientOrientation = transformGradientOrientation(typedArray.getInt(styleable.getSolidGradientOrientationStyleable(),
                                                                    getDefaultGradientOrientation()));
        mSolidGradientType = typedArray.getInt(styleable.getSolidGradientTypeStyleable(), ShapeGradientType.LINEAR_GRADIENT);
        mSolidGradientCenterX = typedArray.getFloat(styleable.getSolidGradientCenterXStyleable(), 0.5f);
        mSolidGradientCenterY = typedArray.getFloat(styleable.getSolidGradientCenterYStyleable(), 0.5f);

        if (typedArray.hasValue(styleable.getSolidGradientRadiusStyleable())) {
            TypedValue value = new TypedValue();
            typedArray.getValue(styleable.getSolidGradientRadiusStyleable(), value);
            if (value.type == TypedValue.TYPE_DIMENSION) {
                mSolidGradientRadius = typedArray.getDimensionPixelSize(styleable.getSolidGradientRadiusStyleable(), radius);
            } else if (value.type == TypedValue.TYPE_FRACTION) {
                mSolidGradientRadius = typedArray.getFraction(styleable.getSolidGradientRadiusStyleable(), 1, 1, 0f);
            } else {
                mSolidGradientRadius = typedArray.getFloat(styleable.getSolidGradientRadiusStyleable(), 0f);
            }
        } else {
            mSolidGradientRadius = radius;
        }

        // Parse radial gradient ellipse and angle attributes
        if (styleable.getSolidGradientRadiusXStyleable() > 0 &&
            typedArray.hasValue(styleable.getSolidGradientRadiusXStyleable())) {
            mGradientRadiusX = typedArray.getFloat(styleable.getSolidGradientRadiusXStyleable(), -1f);
        }
        if (styleable.getSolidGradientRadiusYStyleable() > 0 &&
            typedArray.hasValue(styleable.getSolidGradientRadiusYStyleable())) {
            mGradientRadiusY = typedArray.getFloat(styleable.getSolidGradientRadiusYStyleable(), -1f);
        }
        if (styleable.getSolidRadialAngleStyleable() > 0 &&
            typedArray.hasValue(styleable.getSolidRadialAngleStyleable())) {
            mRadialGradientAngle = typedArray.getFloat(styleable.getSolidRadialAngleStyleable(), 0f);
        }

        // Parse gradient color stop position attributes (Figma compatibility)
        if (styleable.getSolidGradientStartPercentStyleable() > 0 &&
            typedArray.hasValue(styleable.getSolidGradientStartPercentStyleable())) {
            mGradientStartPercent = typedArray.getFloat(styleable.getSolidGradientStartPercentStyleable(), 0f);
            mHasCustomGradientPositions = true;
        }
        if (styleable.getSolidGradientCenterPercentStyleable() > 0 &&
            typedArray.hasValue(styleable.getSolidGradientCenterPercentStyleable())) {
            mGradientCenterPercent = typedArray.getFloat(styleable.getSolidGradientCenterPercentStyleable(), 0.5f);
            mHasCustomGradientPositions = true;
        }
        if (styleable.getSolidGradientEndPercentStyleable() > 0 &&
            typedArray.hasValue(styleable.getSolidGradientEndPercentStyleable())) {
            mGradientEndPercent = typedArray.getFloat(styleable.getSolidGradientEndPercentStyleable(), 1f);
            mHasCustomGradientPositions = true;
        }

        // Parse linear gradient extent positions (Figma compatibility)
        if (styleable.getSolidGradientStartXStyleable() > 0 &&
            typedArray.hasValue(styleable.getSolidGradientStartXStyleable())) {
            mLinearGradientStartX = typedArray.getFloat(styleable.getSolidGradientStartXStyleable(), 0.5f);
            mHasCustomLinearExtent = true;
        }
        if (styleable.getSolidGradientStartYStyleable() > 0 &&
            typedArray.hasValue(styleable.getSolidGradientStartYStyleable())) {
            mLinearGradientStartY = typedArray.getFloat(styleable.getSolidGradientStartYStyleable(), 0f);
            mHasCustomLinearExtent = true;
        }
        if (styleable.getSolidGradientEndXStyleable() > 0 &&
            typedArray.hasValue(styleable.getSolidGradientEndXStyleable())) {
            mLinearGradientEndX = typedArray.getFloat(styleable.getSolidGradientEndXStyleable(), 0.5f);
            mHasCustomLinearExtent = true;
        }
        if (styleable.getSolidGradientEndYStyleable() > 0 &&
            typedArray.hasValue(styleable.getSolidGradientEndYStyleable())) {
            mLinearGradientEndY = typedArray.getFloat(styleable.getSolidGradientEndYStyleable(), 1f);
            mHasCustomLinearExtent = true;
        }

        if (typedArray.hasValue(styleable.getStrokeColorStyleable())) {
            mStrokeColorStateList = typedArray.getColorStateList(styleable.getStrokeColorStyleable());
            if (mStrokeColorStateList != null) {
                mStrokeColor = mStrokeColorStateList.getDefaultColor();
            } else {
                mStrokeColor = typedArray.getColor(styleable.getStrokeColorStyleable(), NO_COLOR);
            }
        } else {
            mStrokeColor = NO_COLOR;
        }
        if (typedArray.hasValue(styleable.getStrokePressedColorStyleable())) {
            mStrokePressedColor = typedArray.getColor(styleable.getStrokePressedColorStyleable(), NO_COLOR);
        }
        if (styleable.getStrokeCheckedColorStyleable() > 0 && typedArray.hasValue(styleable.getStrokeCheckedColorStyleable())) {
            mStrokeCheckedColor = typedArray.getColor(styleable.getStrokeCheckedColorStyleable(), NO_COLOR);
        }
        if (typedArray.hasValue(styleable.getStrokeDisabledColorStyleable())) {
            mStrokeDisabledColor = typedArray.getColor(styleable.getStrokeDisabledColorStyleable(), NO_COLOR);
        }
        if (typedArray.hasValue(styleable.getStrokeFocusedColorStyleable())) {
            mStrokeFocusedColor = typedArray.getColor(styleable.getStrokeFocusedColorStyleable(), NO_COLOR);
        }
        if (typedArray.hasValue(styleable.getStrokeSelectedColorStyleable())) {
            mStrokeSelectedColor = typedArray.getColor(styleable.getStrokeSelectedColorStyleable(), NO_COLOR);
        }

        if (typedArray.hasValue(styleable.getStrokeGradientStartColorStyleable()) ||
            typedArray.hasValue(styleable.getStrokeGradientEndColorStyleable())) {

            if (typedArray.hasValue(styleable.getStrokeGradientStartColorStyleable())) {
                mStrokeGradientStartColorStateList = typedArray.getColorStateList(styleable.getStrokeGradientStartColorStyleable());
            }
            if (typedArray.hasValue(styleable.getStrokeGradientCenterColorStyleable())) {
                mStrokeGradientCenterColorStateList = typedArray.getColorStateList(styleable.getStrokeGradientCenterColorStyleable());
            }
            if (typedArray.hasValue(styleable.getStrokeGradientEndColorStyleable())) {
                mStrokeGradientEndColorStateList = typedArray.getColorStateList(styleable.getStrokeGradientEndColorStyleable());
            }

            int startColor = typedArray.getColor(styleable.getStrokeGradientStartColorStyleable(), NO_COLOR);
            int centerColor = NO_COLOR;
            int endColor = typedArray.getColor(styleable.getStrokeGradientEndColorStyleable(), NO_COLOR);
            boolean hasCenter = false;

            if (typedArray.hasValue(styleable.getStrokeGradientCenterColorStyleable())) {
                 centerColor = typedArray.getColor(styleable.getStrokeGradientCenterColorStyleable(), NO_COLOR);
                 hasCenter = true;
            }

            if (hasCenter) {
                 mStrokeGradientColors = new int[] {startColor, centerColor, endColor};
            } else {
                 mStrokeGradientColors = new int[] {startColor, endColor};
            }
        }

        mStrokeGradientOrientation = transformGradientOrientation(typedArray.getInt(styleable.getStrokeGradientOrientationStyleable(),
                                                                    getDefaultGradientOrientation()));

        mStrokeSize = typedArray.getDimensionPixelSize(styleable.getStrokeSizeStyleable(), 0);
        mStrokeDashSize = typedArray.getDimensionPixelSize(styleable.getStrokeDashSizeStyleable(), 0);
        mStrokeDashGap = typedArray.getDimensionPixelSize(styleable.getStrokeDashGapStyleable(), 0);

        mOuterShadowSize = typedArray.getDimensionPixelSize(styleable.getOuterShadowSizeStyleable(), 0);
        
        if (typedArray.hasValue(styleable.getOuterShadowColorStyleable())) {
            mOuterShadowColorStateList = typedArray.getColorStateList(styleable.getOuterShadowColorStyleable());
            if (mOuterShadowColorStateList != null) {
                mOuterShadowColor = mOuterShadowColorStateList.getDefaultColor();
            } else {
                mOuterShadowColor = typedArray.getColor(styleable.getOuterShadowColorStyleable(), 0x10000000);
            }
        } else {
            mOuterShadowColor = 0x10000000;
        }
        mOuterShadowOffsetX = typedArray.getDimensionPixelOffset(styleable.getOuterShadowOffsetXStyleable(), 0);
        mOuterShadowOffsetY = typedArray.getDimensionPixelOffset(styleable.getOuterShadowOffsetYStyleable(), 0);

        mRingInnerRadiusSize = typedArray.getDimensionPixelOffset(styleable.getRingInnerRadiusSizeStyleable(), -1);
        mRingInnerRadiusRatio = typedArray.getFloat(styleable.getRingInnerRadiusRatioStyleable(), 3.0f);
        mRingThicknessSize = typedArray.getDimensionPixelOffset(styleable.getRingThicknessSizeStyleable(), -1);
        mRingThicknessRatio = typedArray.getFloat(styleable.getRingThicknessRatioStyleable(), 9.0f);

        mLineGravity = typedArray.getInt(styleable.getLineGravityStyleable(), Gravity.CENTER);

        // Primary Inner Shadow attributes
        if (styleable.getInnerShadowSizeStyleable() > 0 || styleable.getInnerShadowColorStyleable() > 0) {
             if (typedArray.hasValue(styleable.getInnerShadowSizeStyleable())) {
                mInnerShadowSize = typedArray.getDimensionPixelSize(styleable.getInnerShadowSizeStyleable(), 0);
             }
             if (typedArray.hasValue(styleable.getInnerShadowColorStyleable())) {
                 mInnerShadowColorStateList = typedArray.getColorStateList(styleable.getInnerShadowColorStyleable());
                 if (mInnerShadowColorStateList != null) {
                     mInnerShadowColor = mInnerShadowColorStateList.getDefaultColor();
                 } else {
                     mInnerShadowColor = typedArray.getColor(styleable.getInnerShadowColorStyleable(), 0);
                 }
             }
             if (typedArray.hasValue(styleable.getInnerShadowOffsetXStyleable())) {
                mInnerShadowOffsetX = typedArray.getDimensionPixelOffset(styleable.getInnerShadowOffsetXStyleable(), 0);
             }
             if (typedArray.hasValue(styleable.getInnerShadowOffsetYStyleable())) {
                mInnerShadowOffsetY = typedArray.getDimensionPixelOffset(styleable.getInnerShadowOffsetYStyleable(), 0);
             }
        }

        // Secondary Inner Shadow attributes
        if (styleable.getInnerShadow2SizeStyleable() > 0 || styleable.getInnerShadow2ColorStyleable() > 0) {
             if (typedArray.hasValue(styleable.getInnerShadow2SizeStyleable())) {
                mInnerShadow2Size = typedArray.getDimensionPixelSize(styleable.getInnerShadow2SizeStyleable(), 0);
             }
             if (typedArray.hasValue(styleable.getInnerShadow2ColorStyleable())) {
                 mInnerShadow2ColorStateList = typedArray.getColorStateList(styleable.getInnerShadow2ColorStyleable());
                 if (mInnerShadow2ColorStateList != null) {
                     mInnerShadow2Color = mInnerShadow2ColorStateList.getDefaultColor();
                 } else {
                     mInnerShadow2Color = typedArray.getColor(styleable.getInnerShadow2ColorStyleable(), 0);
                 }
             }
             if (typedArray.hasValue(styleable.getInnerShadow2OffsetXStyleable())) {
                mInnerShadow2OffsetX = typedArray.getDimensionPixelOffset(styleable.getInnerShadow2OffsetXStyleable(), 0);
             }
             if (typedArray.hasValue(styleable.getInnerShadow2OffsetYStyleable())) {
                mInnerShadow2OffsetY = typedArray.getDimensionPixelOffset(styleable.getInnerShadow2OffsetYStyleable(), 0);
             }
        }
    }

    public ShapeDrawableBuilder setType(@ShapeTypeLimit int type) {
        mType = type;
        return this;
    }

    @ShapeTypeLimit
    public int getType() {
        return mType;
    }

    public ShapeDrawableBuilder setWidth(int width) {
        mWidth = width;
        return this;
    }

    public int getWidth() {
        return mWidth;
    }

    public ShapeDrawableBuilder setHeight(int height) {
        mHeight = height;
        return this;
    }

    public int getHeight() {
        return mHeight;
    }

    public ShapeDrawableBuilder setRadius(float radius) {
        return setRadius(radius, radius, radius, radius);
    }

    public ShapeDrawableBuilder setRadius(float topLeftRadius, float topRightRadius,
                                          float bottomLeftRadius, float bottomRightRadius) {
        mTopLeftRadius = topLeftRadius;
        mTopRightRadius = topRightRadius;
        mBottomLeftRadius = bottomLeftRadius;
        mBottomRightRadius = bottomRightRadius;
        return this;
    }

    public ShapeDrawableBuilder setRadiusRelative(float topStartRadius, float topEndRadius,
                                                    float bottomStartRadius, float bottomEndRadius) {
        int layoutDirection = mView.getLayoutDirection();
        switch (layoutDirection) {
            case View.LAYOUT_DIRECTION_RTL:
                mTopLeftRadius = topEndRadius;
                mTopRightRadius = topStartRadius;
                mBottomLeftRadius = bottomEndRadius;
                mBottomRightRadius = bottomStartRadius;
                break;
            case View.LAYOUT_DIRECTION_LTR:
            default:
                mTopLeftRadius = topStartRadius;
                mTopRightRadius = topEndRadius;
                mBottomLeftRadius = bottomStartRadius;
                mBottomRightRadius = bottomEndRadius;
                break;
        }
        return this;
    }

    public ShapeDrawableBuilder setTopLeftRadius(float radius) {
        mTopLeftRadius = radius;
        return this;
    }

    public float getTopLeftRadius() {
        return mTopLeftRadius;
    }

    public ShapeDrawableBuilder setTopRightRadius(float radius) {
        mTopRightRadius = radius;
        return this;
    }

    public float getTopRightRadius() {
        return mTopRightRadius;
    }

    public ShapeDrawableBuilder setBottomLeftRadius(float radius) {
        mBottomLeftRadius = radius;
        return this;
    }

    public float getBottomLeftRadius() {
        return mBottomLeftRadius;
    }

    public ShapeDrawableBuilder setBottomRightRadius(float radius) {
        mBottomRightRadius = radius;
        return this;
    }

    public float getBottomRightRadius() {
        return mBottomRightRadius;
    }

    public ShapeDrawableBuilder setSolidColor(int color) {
        mSolidColor = color;
        mSolidColorStateList = ColorStateList.valueOf(color);
        clearSolidGradientColors();
        return this;
    }

    public int getSolidColor() {
        return mSolidColor;
    }

    public ShapeDrawableBuilder setSolidColor(ColorStateList colorStateList) {
        mSolidColorStateList = colorStateList;
        if (colorStateList != null) {
            mSolidColor = colorStateList.getDefaultColor();
        } else {
            mSolidColor = NO_COLOR;
        }
        clearSolidGradientColors();
        return this;
    }

    public ColorStateList getSolidColorStateList() {
        return mSolidColorStateList;
    }

    public ShapeDrawableBuilder setSolidPressedColor(Integer color) {
        mSolidPressedColor = color;
        return this;
    }

    @Nullable
    public Integer getSolidPressedColor() {
        return mSolidPressedColor;
    }

    public ShapeDrawableBuilder setSolidCheckedColor(Integer color) {
        mSolidCheckedColor = color;
        return this;
    }

    @Nullable
    public Integer getSolidCheckedColor() {
        return mSolidCheckedColor;
    }

    public ShapeDrawableBuilder setSolidDisabledColor(Integer color) {
        mSolidDisabledColor = color;
        return this;
    }

    @Nullable
    public Integer getSolidDisabledColor() {
        return mSolidDisabledColor;
    }

    public ShapeDrawableBuilder setSolidFocusedColor(Integer color) {
        mSolidFocusedColor = color;
        return this;
    }

    @Nullable
    public Integer getSolidFocusedColor() {
        return mSolidFocusedColor;
    }

    public ShapeDrawableBuilder setSolidSelectedColor(Integer color) {
        mSolidSelectedColor = color;
        return this;
    }

    @Nullable
    public Integer getSolidSelectedColor() {
        return mSolidSelectedColor;
    }

    public ShapeDrawableBuilder setSolidGradientColors(int startColor, int endColor) {
        return setSolidGradientColors(new int[]{startColor, endColor});
    }

    public ShapeDrawableBuilder setSolidGradientColors(int startColor, int centerColor, int endColor) {
        return setSolidGradientColors(new int[]{startColor, centerColor, endColor});
    }

    public ShapeDrawableBuilder setSolidGradientColors(int[] colors) {
        mSolidGradientColors = colors;
        return this;
    }

    @Nullable
    public int[] getSolidGradientColors() {
        return mSolidGradientColors;
    }

    public boolean isSolidGradientColorsEnable() {
        return mSolidGradientColors != null &&
                mSolidGradientColors.length > 0;
    }

    public void clearSolidGradientColors() {
        mSolidGradientColors = null;
    }

    public ShapeDrawableBuilder setSolidGradientOrientation(ShapeGradientOrientation orientation) {
        mSolidGradientOrientation = orientation;
        return this;
    }

    public ShapeGradientOrientation getSolidGradientOrientation() {
        return mSolidGradientOrientation;
    }

    public ShapeDrawableBuilder setSolidGradientType(@ShapeGradientTypeLimit int type) {
        mSolidGradientType = type;
        return this;
    }

    @ShapeGradientTypeLimit
    public int getSolidGradientType() {
        return mSolidGradientType;
    }

    public ShapeDrawableBuilder setSolidGradientCenterX(float centerX) {
        mSolidGradientCenterX = centerX;
        return this;
    }

    public float getSolidGradientCenterX() {
        return mSolidGradientCenterX;
    }

    public ShapeDrawableBuilder setSolidGradientCenterY(float centerY) {
        mSolidGradientCenterY = centerY;
        return this;
    }

    public float getSolidGradientCenterY() {
        return mSolidGradientCenterY;
    }

    public ShapeDrawableBuilder setSolidGradientRadius(float radius) {
        mSolidGradientRadius = radius;
        return this;
    }

    public float getSolidGradientRadius() {
        return mSolidGradientRadius;
    }

    public ShapeDrawableBuilder setSolidRadialAngle(float angleDegrees) {
        mRadialGradientAngle = angleDegrees;
        return this;
    }

    public float getSolidRadialAngle() {
        return mRadialGradientAngle;
    }

    public ShapeDrawableBuilder setSolidGradientRadii(float radiusX, float radiusY) {
        mGradientRadiusX = radiusX;
        mGradientRadiusY = radiusY;
        return this;
    }

    public float getGradientRadiusX() {
        return mGradientRadiusX;
    }

    public float getGradientRadiusY() {
        return mGradientRadiusY;
    }

    public ShapeDrawableBuilder setSolidRadialStartPosition(float startX, float startY) {
        mRadialStartX = startX;
        mRadialStartY = startY;
        return this;
    }

    public float getRadialStartX() {
        return mRadialStartX;
    }

    public float getRadialStartY() {
        return mRadialStartY;
    }

    // Gradient color stop position methods (Figma compatibility)
    public ShapeDrawableBuilder setSolidGradientPositions(float startPercent, float centerPercent, float endPercent) {
        mGradientStartPercent = startPercent;
        mGradientCenterPercent = centerPercent;
        mGradientEndPercent = endPercent;
        mHasCustomGradientPositions = true;
        return this;
    }

    public ShapeDrawableBuilder setSolidGradientPositions(float startPercent, float endPercent) {
        mGradientStartPercent = startPercent;
        mGradientCenterPercent = -1f;
        mGradientEndPercent = endPercent;
        mHasCustomGradientPositions = true;
        return this;
    }

    public float getGradientStartPercent() {
        return mGradientStartPercent;
    }

    public float getGradientCenterPercent() {
        return mGradientCenterPercent;
    }

    public float getGradientEndPercent() {
        return mGradientEndPercent;
    }

    public boolean hasCustomGradientPositions() {
        return mHasCustomGradientPositions;
    }

    /**
     * Set the linear gradient start and end positions as percentage of view dimensions.
     * 
     * @param startX Starting X position (0.0 = left, 1.0 = right)
     * @param startY Starting Y position (0.0 = top, 1.0 = bottom)
     * @param endX Ending X position (0.0 = left, 1.0 = right)
     * @param endY Ending Y position (0.0 = top, 1.0 = bottom)
     * @return This builder for chaining
     */
    public ShapeDrawableBuilder setLinearGradientPositions(float startX, float startY, float endX, float endY) {
        mLinearGradientStartX = startX;
        mLinearGradientStartY = startY;
        mLinearGradientEndX = endX;
        mLinearGradientEndY = endY;
        mHasCustomLinearExtent = true;
        return this;
    }

    public float getLinearGradientStartX() {
        return mLinearGradientStartX;
    }

    public float getLinearGradientStartY() {
        return mLinearGradientStartY;
    }

    public float getLinearGradientEndX() {
        return mLinearGradientEndX;
    }

    public float getLinearGradientEndY() {
        return mLinearGradientEndY;
    }

    public boolean hasCustomLinearExtent() {
        return mHasCustomLinearExtent;
    }

    public ShapeDrawableBuilder setStrokeColor(int color) {
        mStrokeColor = color;
        mStrokeColorStateList = ColorStateList.valueOf(color);
        clearStrokeGradientColors();
        return this;
    }

    public int getStrokeColor() {
        return mStrokeColor;
    }

    public ShapeDrawableBuilder setStrokeColor(ColorStateList colorStateList) {
        mStrokeColorStateList = colorStateList;
        if (colorStateList != null) {
            mStrokeColor = colorStateList.getDefaultColor();
        } else {
            mStrokeColor = NO_COLOR;
        }
        clearStrokeGradientColors();
        return this;
    }

    public ColorStateList getStrokeColorStateList() {
        return mStrokeColorStateList;
    }

    public ShapeDrawableBuilder setStrokePressedColor(Integer color) {
        mStrokePressedColor = color;
        return this;
    }

    @Nullable
    public Integer getStrokePressedColor() {
        return mStrokePressedColor;
    }

    public ShapeDrawableBuilder setStrokeCheckedColor(Integer color) {
        mStrokeCheckedColor = color;
        return this;
    }

    @Nullable
    public Integer getStrokeCheckedColor() {
        return mStrokeCheckedColor;
    }

    public ShapeDrawableBuilder setStrokeDisabledColor(Integer color) {
        mStrokeDisabledColor = color;
        return this;
    }

    @Nullable
    public Integer getStrokeDisabledColor() {
        return mStrokeDisabledColor;
    }

    public ShapeDrawableBuilder setStrokeFocusedColor(Integer color) {
        mStrokeFocusedColor = color;
        return this;
    }

    @Nullable
    public Integer getStrokeFocusedColor() {
        return mStrokeFocusedColor;
    }

    public ShapeDrawableBuilder setStrokeSelectedColor(Integer color) {
        mStrokeSelectedColor = color;
        return this;
    }

    @Nullable
    public Integer getStrokeSelectedColor() {
        return mStrokeSelectedColor;
    }

    public ShapeDrawableBuilder setStrokeGradientColors(int startColor, int endColor) {
        return setStrokeGradientColors(new int[]{startColor, endColor});
    }

    public ShapeDrawableBuilder setStrokeGradientColors(int startColor, int centerColor, int endColor) {
        return setStrokeGradientColors(new int[]{startColor, centerColor, endColor});
    }

    public ShapeDrawableBuilder setStrokeGradientColors(int[] colors) {
        mStrokeGradientColors = colors;
        return this;
    }

    @Nullable
    public int[] getStrokeGradientColors() {
        return mStrokeGradientColors;
    }

    public boolean isStrokeGradientColorsEnable() {
        return mStrokeGradientColors != null &&
                mStrokeGradientColors.length > 0;
    }

    public void clearStrokeGradientColors() {
        mStrokeGradientColors = null;
    }

    public ShapeDrawableBuilder setStrokeGradientOrientation(ShapeGradientOrientation orientation) {
        mStrokeGradientOrientation = orientation;
        return this;
    }

    public ShapeGradientOrientation getStrokeGradientOrientation() {
        return mStrokeGradientOrientation;
    }

    public ShapeDrawableBuilder setStrokeSize(int size) {
        mStrokeSize = size;
        return this;
    }

    public int getStrokeSize() {
        return mStrokeSize;
    }

    public ShapeDrawableBuilder setStrokeDashSize(int size) {
        mStrokeDashSize = size;
        return this;
    }

    public int getStrokeDashSize() {
        return mStrokeDashSize;
    }

    public ShapeDrawableBuilder setStrokeDashGap(int gap) {
        mStrokeDashGap = gap;
        return this;
    }

    public int getStrokeDashGap() {
        return mStrokeDashGap;
    }

    public boolean isStrokeDashLineEnable() {
        return mStrokeDashGap > 0;
    }

    public ShapeDrawableBuilder setRingInnerRadiusSize(int size) {
        mRingInnerRadiusSize = size;
        return this;
    }

    public int getRingInnerRadiusSize() {
        return mRingInnerRadiusSize;
    }

    public ShapeDrawableBuilder setRingInnerRadiusRatio(float ratio) {
        mRingInnerRadiusRatio = ratio;
        return this;
    }

    public float getRingInnerRadiusRatio() {
        return mRingInnerRadiusRatio;
    }

    public ShapeDrawableBuilder setRingThicknessSize(int size) {
        mRingThicknessSize = size;
        return this;
    }

    public int getRingThicknessSize() {
        return mRingThicknessSize;
    }

    public ShapeDrawableBuilder setRingThicknessRatio(float ratio) {
        mRingThicknessRatio = ratio;
        return this;
    }

    public float getRingThicknessRatio() {
        return mRingThicknessRatio;
    }

    public boolean isOuterShadowEnable() {
        return mOuterShadowSize > 0;
    }

    public ShapeDrawableBuilder setOuterShadowSize(int size) {
        mOuterShadowSize = size;
        return this;
    }

    public int getOuterShadowSize() {
        return mOuterShadowSize;
    }

    public ShapeDrawableBuilder setOuterShadowColor(int color) {
        mOuterShadowColor = color;
        return this;
    }

    public int getOuterShadowColor() {
        return mOuterShadowColor;
    }

    public ShapeDrawableBuilder setOuterShadowOffsetX(int offsetX) {
        mOuterShadowOffsetX = offsetX;
        return this;
    }

    public int getOuterShadowOffsetX() {
        return mOuterShadowOffsetX;
    }

    public ShapeDrawableBuilder setOuterShadowOffsetY(int offsetY) {
        mOuterShadowOffsetY = offsetY;
        return this;
    }

    public int getOuterShadowOffsetY() {
        return mOuterShadowOffsetY;
    }

    public int getLineGravity() {
        return mLineGravity;
    }

    public ShapeDrawableBuilder setLineGravity(int gravity) {
        mLineGravity = gravity;
        return this;
    }

    // Primary Inner Shadow methods
    public boolean hasInnerShadow() {
        return mInnerShadowSize > 0;
    }

    public int getInnerShadowSize() {
        return mInnerShadowSize;
    }

    public ShapeDrawableBuilder setInnerShadowSize(int size) {
        mInnerShadowSize = size;
        return this;
    }

    public int getInnerShadowColor() {
        return mInnerShadowColor;
    }

    public ShapeDrawableBuilder setInnerShadowColor(int color) {
        mInnerShadowColor = color;
        return this;
    }

    public int getInnerShadowOffsetX() {
        return mInnerShadowOffsetX;
    }

    public ShapeDrawableBuilder setInnerShadowOffsetX(int offsetX) {
        mInnerShadowOffsetX = offsetX;
        return this;
    }

    public int getInnerShadowOffsetY() {
        return mInnerShadowOffsetY;
    }

    public ShapeDrawableBuilder setInnerShadowOffsetY(int offsetY) {
        mInnerShadowOffsetY = offsetY;
        return this;
    }

    // Secondary Inner Shadow methods
    public boolean hasInnerShadow2() {
        return mInnerShadow2Size > 0;
    }

    public int getInnerShadow2Size() {
        return mInnerShadow2Size;
    }

    public ShapeDrawableBuilder setInnerShadow2Size(int size) {
        mInnerShadow2Size = size;
        return this;
    }

    public int getInnerShadow2Color() {
        return mInnerShadow2Color;
    }

    public ShapeDrawableBuilder setInnerShadow2Color(int color) {
        mInnerShadow2Color = color;
        return this;
    }

    public int getInnerShadow2OffsetX() {
        return mInnerShadow2OffsetX;
    }

    public ShapeDrawableBuilder setInnerShadow2OffsetX(int offsetX) {
        mInnerShadow2OffsetX = offsetX;
        return this;
    }

    public int getInnerShadow2OffsetY() {
        return mInnerShadow2OffsetY;
    }

    public ShapeDrawableBuilder setInnerShadow2OffsetY(int offsetY) {
        mInnerShadow2OffsetY = offsetY;
        return this;
    }

    @Nullable
    public Drawable buildBackgroundDrawable() {
        boolean hasSolidColorState = mSolidPressedColor != null || mSolidCheckedColor != null ||
                mSolidDisabledColor != null || mSolidFocusedColor != null || mSolidSelectedColor != null;

        boolean hasStrokeColorState = mStrokePressedColor != null || mStrokeCheckedColor != null ||
                mStrokeDisabledColor != null || mStrokeFocusedColor != null || mStrokeSelectedColor != null;

        if (!isSolidGradientColorsEnable() && !isStrokeGradientColorsEnable() &&
                mSolidColor == NO_COLOR && !hasSolidColorState && mStrokeColor == NO_COLOR && !hasStrokeColorState) {
            return null;
        }

        ShapeDrawable defaultDrawable;
        Drawable viewBackground = mView.getBackground();
        if (viewBackground instanceof ExtendStateListDrawable) {
            defaultDrawable = convertShapeDrawable(((ExtendStateListDrawable) viewBackground).getDefaultDrawable());
        } else {
            defaultDrawable = convertShapeDrawable(viewBackground);
        }

        refreshShapeDrawable(defaultDrawable, null, null);

        if (!hasSolidColorState && !hasStrokeColorState) {
            return defaultDrawable;
        }

        ExtendStateListDrawable stateListDrawable = new ExtendStateListDrawable();
        if (mSolidPressedColor != null || mStrokePressedColor != null) {
            ShapeDrawable drawable = convertShapeDrawable(stateListDrawable.getPressedDrawable());
            refreshShapeDrawable(drawable, mSolidPressedColor, mStrokePressedColor);
            stateListDrawable.setPressedDrawable(drawable);
        }

        if (mSolidCheckedColor != null || mStrokeCheckedColor != null) {
            ShapeDrawable drawable = convertShapeDrawable(stateListDrawable.getCheckDrawable());
            refreshShapeDrawable(drawable, mSolidCheckedColor, mStrokeCheckedColor);
            stateListDrawable.setCheckDrawable(drawable);
        }

        if (mSolidDisabledColor != null || mStrokeDisabledColor != null) {
            ShapeDrawable drawable = convertShapeDrawable(stateListDrawable.getDisabledDrawable());
            refreshShapeDrawable(drawable, mSolidDisabledColor, mStrokeDisabledColor);
            stateListDrawable.setDisabledDrawable(drawable);
        }

        if (mSolidFocusedColor != null || mStrokeFocusedColor != null) {
            ShapeDrawable drawable = convertShapeDrawable(stateListDrawable.getFocusedDrawable());
            refreshShapeDrawable(drawable, mSolidFocusedColor, mStrokeFocusedColor);
            stateListDrawable.setFocusedDrawable(drawable);
        }

        if (mSolidSelectedColor != null || mStrokeSelectedColor != null) {
            ShapeDrawable drawable = convertShapeDrawable(stateListDrawable.getSelectDrawable());
            refreshShapeDrawable(drawable, mSolidSelectedColor, mStrokeSelectedColor);
            stateListDrawable.setSelectDrawable(drawable);
        }

        stateListDrawable.setDefaultDrawable(defaultDrawable);
        return stateListDrawable;
    }

    public void refreshShapeDrawable(ShapeDrawable drawable,
                                     @Nullable Integer solidStateColor,
                                     @Nullable Integer strokeStateColor) {
        drawable.setType(mType)
                .setWidth(mWidth)
                .setHeight(mHeight)
                .setRadius(mTopLeftRadius, mTopRightRadius,
                        mBottomLeftRadius, mBottomRightRadius);

        drawable.setSolidGradientType(mSolidGradientType)
                .setSolidGradientOrientation(mSolidGradientOrientation)
                .setSolidGradientRadius(mSolidGradientRadius)
                .setSolidGradientCenterX(mSolidGradientCenterX)
                .setSolidGradientCenterY(mSolidGradientCenterY);

        if (mSolidGradientColors != null && mSolidGradientColors.length > 0) {
             drawable.setSolidGradientStartColor(mSolidGradientStartColorStateList)
                     .setSolidGradientCenterColor(mSolidGradientCenterColorStateList)
                     .setSolidGradientEndColor(mSolidGradientEndColorStateList);
        }

        // Apply radial gradient transformations if set
        if (mRadialGradientAngle != 0f) {
            drawable.setSolidRadialAngle(mRadialGradientAngle);
        }
        if (mGradientRadiusX > 0 && mGradientRadiusY > 0) {
            drawable.setSolidGradientRadii(mGradientRadiusX, mGradientRadiusY);
        }
        if (!Float.isNaN(mRadialStartX) || !Float.isNaN(mRadialStartY)) {
            drawable.setSolidRadialStartPosition(mRadialStartX, mRadialStartY);
        }

        // Apply custom gradient color stop positions (Figma compatibility)
        if (mHasCustomGradientPositions) {
            if (mGradientCenterPercent >= 0) {
                // 3-color gradient with custom positions
                float start = mGradientStartPercent >= 0 ? mGradientStartPercent : 0f;
                float end = mGradientEndPercent >= 0 ? mGradientEndPercent : 1f;
                drawable.setSolidGradientPositions(start, mGradientCenterPercent, end);
            } else if (mGradientStartPercent >= 0 || mGradientEndPercent >= 0) {
                // 2-color gradient with custom positions
                float start = mGradientStartPercent >= 0 ? mGradientStartPercent : 0f;
                float end = mGradientEndPercent >= 0 ? mGradientEndPercent : 1f;
                drawable.setSolidGradientPositions(start, end);
            }
        }

        // Apply custom linear gradient extent positions (physical position in view)
        if (mHasCustomLinearExtent) {
            if (!Float.isNaN(mLinearGradientStartX) || !Float.isNaN(mLinearGradientStartY)) {
                float startX = Float.isNaN(mLinearGradientStartX) ? 0.5f : mLinearGradientStartX;
                float startY = Float.isNaN(mLinearGradientStartY) ? 0f : mLinearGradientStartY;
                drawable.setSolidGradientStartPosition(startX, startY);
            }
            if (!Float.isNaN(mLinearGradientEndX) || !Float.isNaN(mLinearGradientEndY)) {
                float endX = Float.isNaN(mLinearGradientEndX) ? 0.5f : mLinearGradientEndX;
                float endY = Float.isNaN(mLinearGradientEndY) ? 1f : mLinearGradientEndY;
                drawable.setSolidGradientEndPosition(endX, endY);
            }
        }

        drawable.setStrokeGradientOrientation(mStrokeGradientOrientation)
                .setStrokeSize(mStrokeSize)
                .setStrokeDashSize(mStrokeDashSize)
                .setStrokeDashGap(mStrokeDashGap);
        
        if (mStrokeGradientColors != null && mStrokeGradientColors.length > 0) {
             drawable.setStrokeGradientStartColor(mStrokeGradientStartColorStateList)
                     .setStrokeGradientCenterColor(mStrokeGradientCenterColorStateList)
                     .setStrokeGradientEndColor(mStrokeGradientEndColorStateList);
        }

        drawable.setOuterShadowSize(mOuterShadowSize)
                .setOuterShadowColor(mOuterShadowColor)
                .setOuterShadowColor(mOuterShadowColorStateList) // Set CSL
                .setOuterShadowOffsetX(mOuterShadowOffsetX)
                .setOuterShadowOffsetY(mOuterShadowOffsetY);

        // Apply primary inner shadow if configured
        if (hasInnerShadow()) {
            if (mInnerShadowColorStateList != null) {
                drawable.addInnerShadow(new InnerShadow(
                    mInnerShadowColorStateList, mInnerShadowSize,
                    mInnerShadowOffsetX, mInnerShadowOffsetY, 0f)); // Assuming spread is 0 or needed
            } else {
                drawable.addInnerShadow(new InnerShadow(
                    mInnerShadowColor, mInnerShadowSize,
                    mInnerShadowOffsetX, mInnerShadowOffsetY));
            }
        }

        // Apply secondary inner shadow if configured
        if (hasInnerShadow2()) {
             if (mInnerShadow2ColorStateList != null) {
                drawable.addInnerShadow(new InnerShadow(
                    mInnerShadow2ColorStateList, mInnerShadow2Size,
                    mInnerShadow2OffsetX, mInnerShadow2OffsetY, 0f));
             } else {
                drawable.addInnerShadow(new InnerShadow(
                    mInnerShadow2Color, mInnerShadow2Size,
                    mInnerShadow2OffsetX, mInnerShadow2OffsetY));
             }
        }

        if (mRingInnerRadiusRatio > 0) {
            drawable.setRingInnerRadiusRatio(mRingInnerRadiusRatio);
        } else if (mRingInnerRadiusSize > -1) {
            drawable.setRingInnerRadiusSize(mRingInnerRadiusSize);
        }

        if (mRingThicknessRatio > 0) {
            drawable.setRingThicknessRatio(mRingThicknessRatio);
        } else if (mRingThicknessSize > -1) {
            drawable.setRingThicknessSize(mRingThicknessSize);
        }

        drawable.setLineGravity(mLineGravity);

        // Fill color setting
        if (solidStateColor != null) {
            drawable.setSolidColor(solidStateColor);
        } else if (isSolidGradientColorsEnable()){
            drawable.setSolidColor(mSolidGradientColors);
        } else if (mSolidColorStateList != null) {
            drawable.setSolidColor(mSolidColorStateList);
        } else {
            drawable.setSolidColor(mSolidColor);
        }

        // Stroke color setting
        if (strokeStateColor != null) {
            drawable.setStrokeColor(strokeStateColor);
        } else if (isStrokeGradientColorsEnable()) {
            drawable.setStrokeColor(mStrokeGradientColors);
        } else if (mStrokeColorStateList != null) {
            drawable.setStrokeColor(mStrokeColorStateList);
        } else {
            drawable.setStrokeColor(mStrokeColor);
        }
    }

    @NonNull
    public ShapeDrawable convertShapeDrawable(Drawable drawable) {
        if (drawable instanceof ShapeDrawable) {
            return (ShapeDrawable) drawable;
        }
        return new ShapeDrawable();
    }

    public void intoBackground() {
        // The obtained Drawable may be null
        Drawable drawable = buildBackgroundDrawable();
        if (isStrokeDashLineEnable() || isOuterShadowEnable() || hasInnerShadow() || hasInnerShadow2()) {
            // Hardware acceleration needs to be disabled, otherwise dashed lines, shadows,
            // or BlurMaskFilter (used by inner shadows) may not take effect on some phones
            // and can cause native SIGSEGV crashes in libhwui's RenderThread.
            // https://developer.android.com/guide/topics/graphics/hardware-accel?hl=zh-cn
            mView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (drawable != null) {
            mView.setBackground(drawable);
        }
    }

    public Drawable getDrawable() {
        return buildBackgroundDrawable();
    }

    public void clearBackground() {
        mSolidColor = NO_COLOR;
        mSolidColorStateList = null;
        mSolidGradientColors = null;
        mSolidPressedColor = null;
        mSolidCheckedColor = null;
        mSolidDisabledColor = null;
        mSolidFocusedColor = null;
        mSolidSelectedColor = null;

        mStrokeColor = NO_COLOR;
        mStrokeColorStateList = null;
        mStrokeGradientColors = null;
        mStrokePressedColor = null;
        mStrokeCheckedColor = null;
        mStrokeDisabledColor = null;
        mStrokeFocusedColor = null;
        mStrokeSelectedColor = null;

        mView.setBackground(null);
    }

     /**
     * Get the current layout direction from the context
     */
    private static int getLayoutDirection(View view) {
        int layoutDirection;
        Context context = view.getContext();
        Resources resources = null;
        Configuration configuration = null;
        if (context != null) {
            resources = context.getResources();
        }
        if (resources != null) {
            configuration = resources.getConfiguration();
        }
        if (configuration != null) {
            layoutDirection = configuration.getLayoutDirection();
        } else {
            layoutDirection = View.LAYOUT_DIRECTION_LTR;
        }
        return layoutDirection;
    }

     /**
     * Get the default gradient direction
     */
    private int getDefaultGradientOrientation() {
        // Github issue address: https://github.com/getActivity/ShapeView/issues/109
        return 10;
    }

     /**
     * Convert the xml attribute value of the gradient color in the ShapeView framework into the enum value in ShapeDrawable
     */
    private ShapeGradientOrientation transformGradientOrientation(int value) {
        switch (value) {
            case 0:
                return ShapeGradientOrientation.LEFT_TO_RIGHT;
            case 180:
                return ShapeGradientOrientation.RIGHT_TO_LEFT;
            case 1800:
                return ShapeGradientOrientation.END_TO_START;
            case 90:
                return ShapeGradientOrientation.BOTTOM_TO_TOP;
            case 270:
                return ShapeGradientOrientation.TOP_TO_BOTTOM;
            case 315:
                return ShapeGradientOrientation.TOP_LEFT_TO_BOTTOM_RIGHT;
            case 3150:
                return ShapeGradientOrientation.TOP_START_TO_BOTTOM_END;
            case 45:
                return ShapeGradientOrientation.BOTTOM_LEFT_TO_TOP_RIGHT;
            case 450:
                return ShapeGradientOrientation.BOTTOM_START_TO_TOP_END;
            case 225:
                return ShapeGradientOrientation.TOP_RIGHT_TO_BOTTOM_LEFT;
            case 2250:
                return ShapeGradientOrientation.TOP_END_TO_BOTTOM_START;
            case 135:
                return ShapeGradientOrientation.BOTTOM_RIGHT_TO_TOP_LEFT;
            case 1350:
                return ShapeGradientOrientation.BOTTOM_END_TO_TOP_START;
            case 10:
            default:
                return ShapeGradientOrientation.START_TO_END;
        }
    }
}