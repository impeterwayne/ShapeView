package com.genesys.shape.builder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.genesys.shape.config.IShapeDrawableStyleable;
import com.genesys.shape.drawable.ShapeDrawable;
import com.genesys.shape.drawable.ShapeEffect;
import com.genesys.shape.drawable.ShapeEffectType;
import com.genesys.shape.drawable.ShapeGradientOrientation;
import com.genesys.shape.drawable.ShapeGradientType;
import com.genesys.shape.drawable.ShapeGradientTypeLimit;
import com.genesys.shape.drawable.ShapeType;
import com.genesys.shape.drawable.ShapeTypeLimit;
import com.genesys.shape.other.ExtendStateListDrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ShapeDrawable Builder class
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
    private boolean mSolidGradientRadiusInPx;

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
    @ShapeGradientTypeLimit
    private int mStrokeGradientType;
    private float mStrokeGradientCenterX;
    private float mStrokeGradientCenterY;
    private float mStrokeGradientRadius;
    private boolean mStrokeGradientRadiusInPx;

    // Stroke radial gradient transformations
    private float mStrokeRadialAngle = 0f;
    private float mStrokeGradientRadiusX = -1f;
    private float mStrokeGradientRadiusY = -1f;
    private float mStrokeRadialStartX = Float.NaN;
    private float mStrokeRadialStartY = Float.NaN;

    // Stroke gradient color stop positions (Figma compatibility)
    private float mStrokeGradientStartPercent = -1f;
    private float mStrokeGradientCenterPercent = -1f;
    private float mStrokeGradientEndPercent = -1f;
    private boolean mHasCustomStrokeGradientPositions = false;

    // Stroke linear gradient extent positions (physical position in view)
    private float mStrokeLinearGradientStartX = Float.NaN;
    private float mStrokeLinearGradientStartY = Float.NaN;
    private float mStrokeLinearGradientEndX = Float.NaN;
    private float mStrokeLinearGradientEndY = Float.NaN;
    private boolean mHasCustomStrokeLinearExtent = false;

    private int mStrokeSize;
    private int mStrokeDashSize;
    private int mStrokeDashGap;

    /** Drop and inner shadows, in the order they are painted. */
    private final List<ShapeEffect> mEffects = new ArrayList<>();
    private boolean mEffectPadContent = true;
    /**
     * Padding this builder has already added on the drop shadows' behalf, so that re-applying
     * is idempotent instead of stacking a second inset on top of the first.
     */
    private final Rect mAppliedShadowPadding = new Rect();

    private int mRingInnerRadiusSize;
    private float mRingInnerRadiusRatio;
    private int mRingThicknessSize;
    private float mRingThicknessRatio;

    private int mLineGravity;

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
        if (styleable.getSolidCheckedColorStyleable() >= 0 && typedArray.hasValue(styleable.getSolidCheckedColorStyleable())) {
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

        // Deprecated combined attribute: the unit decides how the value is read
        mSolidGradientRadius = 0.5f;
        if (typedArray.hasValue(styleable.getSolidGradientRadiusStyleable())) {
            TypedValue value = new TypedValue();
            typedArray.getValue(styleable.getSolidGradientRadiusStyleable(), value);
            if (value.type == TypedValue.TYPE_DIMENSION) {
                mSolidGradientRadius = typedArray.getDimensionPixelSize(styleable.getSolidGradientRadiusStyleable(), 0);
                mSolidGradientRadiusInPx = true;
            } else {
                mSolidGradientRadius = getFloatOrFraction(typedArray, styleable.getSolidGradientRadiusStyleable(), 0.5f);
            }
        }
        // The explicit size/ratio attributes take precedence over the deprecated one
        if (styleable.getSolidGradientRadiusSizeStyleable() >= 0 &&
            typedArray.hasValue(styleable.getSolidGradientRadiusSizeStyleable())) {
            mSolidGradientRadius = typedArray.getDimensionPixelSize(styleable.getSolidGradientRadiusSizeStyleable(), 0);
            mSolidGradientRadiusInPx = true;
        }
        if (styleable.getSolidGradientRadiusRatioStyleable() >= 0 &&
            typedArray.hasValue(styleable.getSolidGradientRadiusRatioStyleable())) {
            mSolidGradientRadius = getFloatOrFraction(typedArray, styleable.getSolidGradientRadiusRatioStyleable(), 0.5f);
            mSolidGradientRadiusInPx = false;
        }

        // Parse radial gradient ellipse and angle attributes
        if (styleable.getSolidGradientRadiusXStyleable() >= 0 &&
            typedArray.hasValue(styleable.getSolidGradientRadiusXStyleable())) {
            mGradientRadiusX = typedArray.getFloat(styleable.getSolidGradientRadiusXStyleable(), -1f);
        }
        if (styleable.getSolidGradientRadiusYStyleable() >= 0 &&
            typedArray.hasValue(styleable.getSolidGradientRadiusYStyleable())) {
            mGradientRadiusY = typedArray.getFloat(styleable.getSolidGradientRadiusYStyleable(), -1f);
        }
        if (styleable.getSolidRadialAngleStyleable() >= 0 &&
            typedArray.hasValue(styleable.getSolidRadialAngleStyleable())) {
            mRadialGradientAngle = typedArray.getFloat(styleable.getSolidRadialAngleStyleable(), 0f);
        }

        // Parse gradient color stop position attributes (Figma compatibility)
        if (styleable.getSolidGradientStartPercentStyleable() >= 0 &&
            typedArray.hasValue(styleable.getSolidGradientStartPercentStyleable())) {
            mGradientStartPercent = typedArray.getFloat(styleable.getSolidGradientStartPercentStyleable(), 0f);
            mHasCustomGradientPositions = true;
        }
        if (styleable.getSolidGradientCenterPercentStyleable() >= 0 &&
            typedArray.hasValue(styleable.getSolidGradientCenterPercentStyleable())) {
            mGradientCenterPercent = typedArray.getFloat(styleable.getSolidGradientCenterPercentStyleable(), 0.5f);
            mHasCustomGradientPositions = true;
        }
        if (styleable.getSolidGradientEndPercentStyleable() >= 0 &&
            typedArray.hasValue(styleable.getSolidGradientEndPercentStyleable())) {
            mGradientEndPercent = typedArray.getFloat(styleable.getSolidGradientEndPercentStyleable(), 1f);
            mHasCustomGradientPositions = true;
        }

        // Parse linear gradient extent positions (Figma compatibility)
        if (styleable.getSolidGradientStartXStyleable() >= 0 &&
            typedArray.hasValue(styleable.getSolidGradientStartXStyleable())) {
            mLinearGradientStartX = typedArray.getFloat(styleable.getSolidGradientStartXStyleable(), 0.5f);
            mHasCustomLinearExtent = true;
        }
        if (styleable.getSolidGradientStartYStyleable() >= 0 &&
            typedArray.hasValue(styleable.getSolidGradientStartYStyleable())) {
            mLinearGradientStartY = typedArray.getFloat(styleable.getSolidGradientStartYStyleable(), 0f);
            mHasCustomLinearExtent = true;
        }
        if (styleable.getSolidGradientEndXStyleable() >= 0 &&
            typedArray.hasValue(styleable.getSolidGradientEndXStyleable())) {
            mLinearGradientEndX = typedArray.getFloat(styleable.getSolidGradientEndXStyleable(), 0.5f);
            mHasCustomLinearExtent = true;
        }
        if (styleable.getSolidGradientEndYStyleable() >= 0 &&
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
        if (styleable.getStrokeCheckedColorStyleable() >= 0 && typedArray.hasValue(styleable.getStrokeCheckedColorStyleable())) {
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

        mStrokeGradientType = getInt(typedArray, styleable.getStrokeGradientTypeStyleable(), ShapeGradientType.LINEAR_GRADIENT);
        mStrokeGradientCenterX = getFloat(typedArray, styleable.getStrokeGradientCenterXStyleable(), 0.5f);
        mStrokeGradientCenterY = getFloat(typedArray, styleable.getStrokeGradientCenterYStyleable(), 0.5f);

        mStrokeGradientRadius = 0.5f;
        if (styleable.getStrokeGradientRadiusSizeStyleable() >= 0 &&
            typedArray.hasValue(styleable.getStrokeGradientRadiusSizeStyleable())) {
            mStrokeGradientRadius = typedArray.getDimensionPixelSize(styleable.getStrokeGradientRadiusSizeStyleable(), 0);
            mStrokeGradientRadiusInPx = true;
        }
        if (styleable.getStrokeGradientRadiusRatioStyleable() >= 0 &&
            typedArray.hasValue(styleable.getStrokeGradientRadiusRatioStyleable())) {
            mStrokeGradientRadius = getFloatOrFraction(typedArray, styleable.getStrokeGradientRadiusRatioStyleable(), 0.5f);
            mStrokeGradientRadiusInPx = false;
        }

        // Parse stroke radial gradient ellipse and angle attributes
        if (styleable.getStrokeGradientRadiusXStyleable() >= 0 &&
            typedArray.hasValue(styleable.getStrokeGradientRadiusXStyleable())) {
            mStrokeGradientRadiusX = typedArray.getFloat(styleable.getStrokeGradientRadiusXStyleable(), -1f);
        }
        if (styleable.getStrokeGradientRadiusYStyleable() >= 0 &&
            typedArray.hasValue(styleable.getStrokeGradientRadiusYStyleable())) {
            mStrokeGradientRadiusY = typedArray.getFloat(styleable.getStrokeGradientRadiusYStyleable(), -1f);
        }
        if (styleable.getStrokeRadialAngleStyleable() >= 0 &&
            typedArray.hasValue(styleable.getStrokeRadialAngleStyleable())) {
            mStrokeRadialAngle = typedArray.getFloat(styleable.getStrokeRadialAngleStyleable(), 0f);
        }

        // Parse stroke gradient color stop position attributes (Figma compatibility)
        if (styleable.getStrokeGradientStartPercentStyleable() >= 0 &&
            typedArray.hasValue(styleable.getStrokeGradientStartPercentStyleable())) {
            mStrokeGradientStartPercent = typedArray.getFloat(styleable.getStrokeGradientStartPercentStyleable(), 0f);
            mHasCustomStrokeGradientPositions = true;
        }
        if (styleable.getStrokeGradientCenterPercentStyleable() >= 0 &&
            typedArray.hasValue(styleable.getStrokeGradientCenterPercentStyleable())) {
            mStrokeGradientCenterPercent = typedArray.getFloat(styleable.getStrokeGradientCenterPercentStyleable(), 0.5f);
            mHasCustomStrokeGradientPositions = true;
        }
        if (styleable.getStrokeGradientEndPercentStyleable() >= 0 &&
            typedArray.hasValue(styleable.getStrokeGradientEndPercentStyleable())) {
            mStrokeGradientEndPercent = typedArray.getFloat(styleable.getStrokeGradientEndPercentStyleable(), 1f);
            mHasCustomStrokeGradientPositions = true;
        }

        // Parse stroke linear gradient extent positions (Figma compatibility)
        if (styleable.getStrokeGradientStartXStyleable() >= 0 &&
            typedArray.hasValue(styleable.getStrokeGradientStartXStyleable())) {
            mStrokeLinearGradientStartX = typedArray.getFloat(styleable.getStrokeGradientStartXStyleable(), 0.5f);
            mHasCustomStrokeLinearExtent = true;
        }
        if (styleable.getStrokeGradientStartYStyleable() >= 0 &&
            typedArray.hasValue(styleable.getStrokeGradientStartYStyleable())) {
            mStrokeLinearGradientStartY = typedArray.getFloat(styleable.getStrokeGradientStartYStyleable(), 0f);
            mHasCustomStrokeLinearExtent = true;
        }
        if (styleable.getStrokeGradientEndXStyleable() >= 0 &&
            typedArray.hasValue(styleable.getStrokeGradientEndXStyleable())) {
            mStrokeLinearGradientEndX = typedArray.getFloat(styleable.getStrokeGradientEndXStyleable(), 0.5f);
            mHasCustomStrokeLinearExtent = true;
        }
        if (styleable.getStrokeGradientEndYStyleable() >= 0 &&
            typedArray.hasValue(styleable.getStrokeGradientEndYStyleable())) {
            mStrokeLinearGradientEndY = typedArray.getFloat(styleable.getStrokeGradientEndYStyleable(), 1f);
            mHasCustomStrokeLinearExtent = true;
        }

        mStrokeSize = typedArray.getDimensionPixelSize(styleable.getStrokeSizeStyleable(), 0);
        mStrokeDashSize = typedArray.getDimensionPixelSize(styleable.getStrokeDashSizeStyleable(), 0);
        mStrokeDashGap = typedArray.getDimensionPixelSize(styleable.getStrokeDashGapStyleable(), 0);

        mEffectPadContent = styleable.getEffectPadContentStyleable() < 0
                || typedArray.getBoolean(styleable.getEffectPadContentStyleable(), true);
        parseEffects(typedArray, styleable);

        mRingInnerRadiusSize = typedArray.getDimensionPixelOffset(styleable.getRingInnerRadiusSizeStyleable(), -1);
        mRingInnerRadiusRatio = typedArray.getFloat(styleable.getRingInnerRadiusRatioStyleable(), 3.0f);
        mRingThicknessSize = typedArray.getDimensionPixelOffset(styleable.getRingThicknessSizeStyleable(), -1);
        mRingThicknessRatio = typedArray.getFloat(styleable.getRingThicknessRatioStyleable(), 9.0f);

        mLineGravity = typedArray.getInt(styleable.getLineGravityStyleable(), Gravity.CENTER);
    }

    /**
     * Read the numbered effect slots — {@code shape_effect1*}, {@code shape_effect2*} and so
     * on — into the effect list, keeping the slot order the layout wrote them in.
     *
     * A slot that declares no geometry contributes nothing, so a layout only pays for the
     * effects it actually asks for.
     */
    private void parseEffects(TypedArray typedArray, IShapeDrawableStyleable styleable) {
        final int[][] slots = styleable.getEffectStyleables();
        if (slots == null) {
            return;
        }
        for (int[] slot : slots) {
            if (slot == null || slot.length < IShapeDrawableStyleable.EFFECT_ATTR_COUNT) {
                continue;
            }
            ShapeEffect effect = new ShapeEffect();
            effect.type = getInt(typedArray, slot[IShapeDrawableStyleable.EFFECT_TYPE],
                    ShapeEffectType.DROP_SHADOW);
            effect.blur = getDimensionPixelSize(typedArray, slot[IShapeDrawableStyleable.EFFECT_BLUR], 0);
            // getDimensionPixelOffset truncates rather than rounding, which keeps a negative
            // spread (a shadow tucked in behind the shape) on the value that was written.
            effect.spread = getDimensionPixelOffset(typedArray, slot[IShapeDrawableStyleable.EFFECT_SPREAD], 0);
            effect.offsetX = getDimensionPixelOffset(typedArray, slot[IShapeDrawableStyleable.EFFECT_OFFSET_X], 0);
            effect.offsetY = getDimensionPixelOffset(typedArray, slot[IShapeDrawableStyleable.EFFECT_OFFSET_Y], 0);
            effect.edges = getInt(typedArray, slot[IShapeDrawableStyleable.EFFECT_EDGES], ShapeEffect.EDGE_ALL);

            final int colorStyleable = slot[IShapeDrawableStyleable.EFFECT_COLOR];
            if (colorStyleable >= 0 && typedArray.hasValue(colorStyleable)) {
                ColorStateList colorStateList = typedArray.getColorStateList(colorStyleable);
                if (colorStateList != null) {
                    effect.setColor(colorStateList);
                } else {
                    effect.color = typedArray.getColor(colorStyleable, ShapeEffect.DEFAULT_COLOR);
                }
            }

            if (effect.isEnabled()) {
                mEffects.add(effect);
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

    /**
     * Set the fill gradient radius as a ratio of half the shortest side
     */
    public ShapeDrawableBuilder setSolidGradientRadiusRatio(float ratio) {
        mSolidGradientRadius = ratio;
        mSolidGradientRadiusInPx = false;
        return this;
    }

    /**
     * Set the fill gradient radius as an absolute size in pixels
     */
    public ShapeDrawableBuilder setSolidGradientRadiusSize(float size) {
        mSolidGradientRadius = size;
        mSolidGradientRadiusInPx = true;
        return this;
    }

    /**
     * @deprecated use {@link #setSolidGradientRadiusRatio(float)} or
     *             {@link #setSolidGradientRadiusSize(float)}
     */
    @Deprecated
    public ShapeDrawableBuilder setSolidGradientRadius(float radius) {
        return setSolidGradientRadiusRatio(radius);
    }

    public float getSolidGradientRadius() {
        return mSolidGradientRadius;
    }

    public boolean isSolidGradientRadiusInPx() {
        return mSolidGradientRadiusInPx;
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

    public ShapeDrawableBuilder setStrokeGradientType(@ShapeGradientTypeLimit int type) {
        mStrokeGradientType = type;
        return this;
    }

    @ShapeGradientTypeLimit
    public int getStrokeGradientType() {
        return mStrokeGradientType;
    }

    public ShapeDrawableBuilder setStrokeGradientCenterX(float centerX) {
        mStrokeGradientCenterX = centerX;
        return this;
    }

    public float getStrokeGradientCenterX() {
        return mStrokeGradientCenterX;
    }

    public ShapeDrawableBuilder setStrokeGradientCenterY(float centerY) {
        mStrokeGradientCenterY = centerY;
        return this;
    }

    public float getStrokeGradientCenterY() {
        return mStrokeGradientCenterY;
    }

    /**
     * Set the stroke gradient radius as a ratio of half the shortest side
     */
    public ShapeDrawableBuilder setStrokeGradientRadiusRatio(float ratio) {
        mStrokeGradientRadius = ratio;
        mStrokeGradientRadiusInPx = false;
        return this;
    }

    /**
     * Set the stroke gradient radius as an absolute size in pixels
     */
    public ShapeDrawableBuilder setStrokeGradientRadiusSize(float size) {
        mStrokeGradientRadius = size;
        mStrokeGradientRadiusInPx = true;
        return this;
    }

    public float getStrokeGradientRadius() {
        return mStrokeGradientRadius;
    }

    public boolean isStrokeGradientRadiusInPx() {
        return mStrokeGradientRadiusInPx;
    }

    public ShapeDrawableBuilder setStrokeRadialAngle(float angleDegrees) {
        mStrokeRadialAngle = angleDegrees;
        return this;
    }

    public float getStrokeRadialAngle() {
        return mStrokeRadialAngle;
    }

    public ShapeDrawableBuilder setStrokeGradientRadii(float radiusX, float radiusY) {
        mStrokeGradientRadiusX = radiusX;
        mStrokeGradientRadiusY = radiusY;
        return this;
    }

    public float getStrokeGradientRadiusX() {
        return mStrokeGradientRadiusX;
    }

    public float getStrokeGradientRadiusY() {
        return mStrokeGradientRadiusY;
    }

    public ShapeDrawableBuilder setStrokeRadialStartPosition(float startX, float startY) {
        mStrokeRadialStartX = startX;
        mStrokeRadialStartY = startY;
        return this;
    }

    public float getStrokeRadialStartX() {
        return mStrokeRadialStartX;
    }

    public float getStrokeRadialStartY() {
        return mStrokeRadialStartY;
    }

    public ShapeDrawableBuilder setStrokeGradientPositions(float startPercent, float centerPercent, float endPercent) {
        mStrokeGradientStartPercent = startPercent;
        mStrokeGradientCenterPercent = centerPercent;
        mStrokeGradientEndPercent = endPercent;
        mHasCustomStrokeGradientPositions = true;
        return this;
    }

    public ShapeDrawableBuilder setStrokeGradientPositions(float startPercent, float endPercent) {
        mStrokeGradientStartPercent = startPercent;
        mStrokeGradientCenterPercent = -1f;
        mStrokeGradientEndPercent = endPercent;
        mHasCustomStrokeGradientPositions = true;
        return this;
    }

    public float getStrokeGradientStartPercent() {
        return mStrokeGradientStartPercent;
    }

    public float getStrokeGradientCenterPercent() {
        return mStrokeGradientCenterPercent;
    }

    public float getStrokeGradientEndPercent() {
        return mStrokeGradientEndPercent;
    }

    public boolean hasCustomStrokeGradientPositions() {
        return mHasCustomStrokeGradientPositions;
    }

    /**
     * Set the stroke linear gradient start and end positions as percentage of view dimensions.
     *
     * @param startX Starting X position (0.0 = left, 1.0 = right)
     * @param startY Starting Y position (0.0 = top, 1.0 = bottom)
     * @param endX Ending X position (0.0 = left, 1.0 = right)
     * @param endY Ending Y position (0.0 = top, 1.0 = bottom)
     * @return This builder for chaining
     */
    public ShapeDrawableBuilder setStrokeLinearGradientPositions(float startX, float startY, float endX, float endY) {
        mStrokeLinearGradientStartX = startX;
        mStrokeLinearGradientStartY = startY;
        mStrokeLinearGradientEndX = endX;
        mStrokeLinearGradientEndY = endY;
        mHasCustomStrokeLinearExtent = true;
        return this;
    }

    public float getStrokeLinearGradientStartX() {
        return mStrokeLinearGradientStartX;
    }

    public float getStrokeLinearGradientStartY() {
        return mStrokeLinearGradientStartY;
    }

    public float getStrokeLinearGradientEndX() {
        return mStrokeLinearGradientEndX;
    }

    public float getStrokeLinearGradientEndY() {
        return mStrokeLinearGradientEndY;
    }

    public boolean hasCustomStrokeLinearExtent() {
        return mHasCustomStrokeLinearExtent;
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

    // ===== Effects =====

    /**
     * True when at least one effect would actually paint something.
     */
    public boolean isEffectEnable() {
        for (int i = 0; i < mEffects.size(); i++) {
            if (mEffects.get(i).isEnabled()) {
                return true;
            }
        }
        return false;
    }

    /**
     * The effects this builder will apply, in paint order. The list is read-only; reach for
     * {@link #addEffect}, {@link #setEffects} or {@link #clearEffects} to change it.
     */
    @NonNull
    public List<ShapeEffect> getEffects() {
        return Collections.unmodifiableList(mEffects);
    }

    /**
     * Replace the effect stack. Effects are painted in list order — drop shadows behind the
     * shape, inner shadows over it.
     *
     * @param effects Effects to apply, each copied on the way in
     */
    public ShapeDrawableBuilder setEffects(List<ShapeEffect> effects) {
        mEffects.clear();
        if (effects != null) {
            for (int i = 0; i < effects.size(); i++) {
                ShapeEffect effect = effects.get(i);
                if (effect != null) {
                    mEffects.add(effect.copy());
                }
            }
        }
        return this;
    }

    /**
     * Replace the effect stack with a single effect.
     */
    public ShapeDrawableBuilder setEffect(ShapeEffect effect) {
        mEffects.clear();
        return addEffect(effect);
    }

    /**
     * Add an effect on top of the ones already set — a second drop shadow for an elevation
     * ramp, or a light inner shadow over a dark one for a bevel.
     *
     * @param effect Effect configuration, copied on the way in
     */
    public ShapeDrawableBuilder addEffect(ShapeEffect effect) {
        if (effect != null) {
            mEffects.add(effect.copy());
        }
        return this;
    }

    public ShapeDrawableBuilder clearEffects() {
        mEffects.clear();
        return this;
    }

    /**
     * Whether the room the drop shadows take is also added to the view's padding, so content
     * stays inside the visible shape.
     */
    public ShapeDrawableBuilder setEffectPadContent(boolean padContent) {
        mEffectPadContent = padContent;
        return this;
    }

    public boolean isEffectPadContent() {
        return mEffectPadContent;
    }

    /**
     * Room the drop shadows take out of the view on each edge: the most any one of them asks
     * for. The shape is drawn inside these insets, so anything laying content over this
     * drawable has to inset by the same amount to stay within the visible shape.
     */
    public Rect getShadowInsets() {
        Rect insets = new Rect();
        ShapeEffect.computeInsets(insets, mEffects);
        return insets;
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
                .setSolidGradientCenterX(mSolidGradientCenterX)
                .setSolidGradientCenterY(mSolidGradientCenterY);

        if (mSolidGradientRadiusInPx) {
            drawable.setSolidGradientRadiusSize(mSolidGradientRadius);
        } else {
            drawable.setSolidGradientRadiusRatio(mSolidGradientRadius);
        }

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

        drawable.setStrokeGradientType(mStrokeGradientType)
                .setStrokeGradientOrientation(mStrokeGradientOrientation)
                .setStrokeGradientCenterX(mStrokeGradientCenterX)
                .setStrokeGradientCenterY(mStrokeGradientCenterY)
                .setStrokeSize(mStrokeSize)
                .setStrokeDashSize(mStrokeDashSize)
                .setStrokeDashGap(mStrokeDashGap);

        if (mStrokeGradientRadiusInPx) {
            drawable.setStrokeGradientRadiusSize(mStrokeGradientRadius);
        } else {
            drawable.setStrokeGradientRadiusRatio(mStrokeGradientRadius);
        }

        if (mStrokeGradientColors != null && mStrokeGradientColors.length > 0) {
             drawable.setStrokeGradientStartColor(mStrokeGradientStartColorStateList)
                     .setStrokeGradientCenterColor(mStrokeGradientCenterColorStateList)
                     .setStrokeGradientEndColor(mStrokeGradientEndColorStateList);
        }

        // Apply stroke radial gradient transformations if set
        if (mStrokeRadialAngle != 0f) {
            drawable.setStrokeRadialAngle(mStrokeRadialAngle);
        }
        if (mStrokeGradientRadiusX > 0 && mStrokeGradientRadiusY > 0) {
            drawable.setStrokeGradientRadii(mStrokeGradientRadiusX, mStrokeGradientRadiusY);
        }
        if (!Float.isNaN(mStrokeRadialStartX) || !Float.isNaN(mStrokeRadialStartY)) {
            drawable.setStrokeRadialStartPosition(mStrokeRadialStartX, mStrokeRadialStartY);
        }

        // Apply custom stroke gradient color stop positions (Figma compatibility)
        if (mHasCustomStrokeGradientPositions) {
            float start = mStrokeGradientStartPercent >= 0 ? mStrokeGradientStartPercent : 0f;
            float end = mStrokeGradientEndPercent >= 0 ? mStrokeGradientEndPercent : 1f;
            if (mStrokeGradientCenterPercent >= 0) {
                // 3-color gradient with custom positions
                drawable.setStrokeGradientPositions(start, mStrokeGradientCenterPercent, end);
            } else if (mStrokeGradientStartPercent >= 0 || mStrokeGradientEndPercent >= 0) {
                // 2-color gradient with custom positions
                drawable.setStrokeGradientPositions(start, end);
            }
        }

        // Apply custom stroke linear gradient extent positions (physical position in view)
        if (mHasCustomStrokeLinearExtent) {
            if (!Float.isNaN(mStrokeLinearGradientStartX) || !Float.isNaN(mStrokeLinearGradientStartY)) {
                float startX = Float.isNaN(mStrokeLinearGradientStartX) ? 0.5f : mStrokeLinearGradientStartX;
                float startY = Float.isNaN(mStrokeLinearGradientStartY) ? 0f : mStrokeLinearGradientStartY;
                drawable.setStrokeGradientStartPosition(startX, startY);
            }
            if (!Float.isNaN(mStrokeLinearGradientEndX) || !Float.isNaN(mStrokeLinearGradientEndY)) {
                float endX = Float.isNaN(mStrokeLinearGradientEndX) ? 0.5f : mStrokeLinearGradientEndX;
                float endY = Float.isNaN(mStrokeLinearGradientEndY) ? 1f : mStrokeLinearGradientEndY;
                drawable.setStrokeGradientEndPosition(endX, endY);
            }
        }

        // Painted in the order they were declared, so a later effect layers over an earlier one.
        drawable.setEffects(mEffects);

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
        if (isStrokeDashLineEnable()) {
            // Hardware acceleration needs to be disabled, otherwise dashed lines may not
            // take effect on some phones and can cause native SIGSEGV crashes in libhwui's
            // RenderThread.
            // https://developer.android.com/guide/topics/graphics/hardware-accel?hl=zh-cn
            // Neither shadow is in this list on purpose: both rasterize their blur into a
            // bitmap of their own, so they can stay on the GPU.
            mView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (drawable != null) {
            mView.setBackground(drawable);
        }
        applyShadowPadding();
    }

    /**
     * Push the view's content inside the room the shadow claimed.
     *
     * The shadow is painted within the view's bounds, which means the shape is drawn smaller
     * than the view. Without this the view's content would still be laid out to the full
     * bounds and would spill over the visible shape's edge — most obviously at a large corner
     * radius. Runs after {@code setBackground} because setting a background can itself rewrite
     * padding.
     */
    private void applyShadowPadding() {
        Rect insets = mEffectPadContent ? getShadowInsets() : new Rect();
        if (insets.equals(mAppliedShadowPadding)) {
            return;
        }
        // Subtract what was added last time, so calling this repeatedly settles on one inset.
        mView.setPadding(
                mView.getPaddingLeft() - mAppliedShadowPadding.left + insets.left,
                mView.getPaddingTop() - mAppliedShadowPadding.top + insets.top,
                mView.getPaddingRight() - mAppliedShadowPadding.right + insets.right,
                mView.getPaddingBottom() - mAppliedShadowPadding.bottom + insets.bottom);
        mAppliedShadowPadding.set(insets);
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

        mEffects.clear();

        mView.setBackground(null);
        // Hand back the padding the drop shadows were holding.
        applyShadowPadding();
    }

    /**
     * Read a dimension attribute (rounded, never negative), tolerating styleables that
     * don't declare it (index -1)
     */
    private static int getDimensionPixelSize(TypedArray typedArray, int styleableIndex, int defValue) {
        if (styleableIndex < 0 || !typedArray.hasValue(styleableIndex)) {
            return defValue;
        }
        return typedArray.getDimensionPixelSize(styleableIndex, defValue);
    }

    /**
     * Read a dimension attribute that may be negative — an offset, or a spread that tucks the
     * shadow in. Truncates rather than rounding, which keeps the sign the author wrote.
     */
    private static int getDimensionPixelOffset(TypedArray typedArray, int styleableIndex, int defValue) {
        if (styleableIndex < 0 || !typedArray.hasValue(styleableIndex)) {
            return defValue;
        }
        return typedArray.getDimensionPixelOffset(styleableIndex, defValue);
    }

    /**
     * Read an int attribute, tolerating styleables that don't declare it (index -1)
     */
    private static int getInt(TypedArray typedArray, int styleableIndex, int defValue) {
        if (styleableIndex < 0 || !typedArray.hasValue(styleableIndex)) {
            return defValue;
        }
        return typedArray.getInt(styleableIndex, defValue);
    }

    /**
     * Read an attribute declared as {@code float|fraction}, accepting either form
     */
    private static float getFloatOrFraction(TypedArray typedArray, int styleableIndex, float defValue) {
        TypedValue value = new TypedValue();
        typedArray.getValue(styleableIndex, value);
        if (value.type == TypedValue.TYPE_FRACTION) {
            return typedArray.getFraction(styleableIndex, 1, 1, defValue);
        }
        return typedArray.getFloat(styleableIndex, defValue);
    }

    /**
     * Read a float attribute, tolerating styleables that don't declare it (index -1)
     */
    private static float getFloat(TypedArray typedArray, int styleableIndex, float defValue) {
        if (styleableIndex < 0 || !typedArray.hasValue(styleableIndex)) {
            return defValue;
        }
        return typedArray.getFloat(styleableIndex, defValue);
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