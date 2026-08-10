package com.genesys.shape.styleable;

import com.genesys.shape.R;
import com.genesys.shape.config.IShapeDrawableStyleable;
import com.genesys.shape.config.ITextColorStyleable;

/**
 * EditText's Shape attribute values
 */
public final class ShapeEditTextStyleable implements IShapeDrawableStyleable, ITextColorStyleable {

    /** {@link IShapeDrawableStyleable#getEffectStyleables()} */
    private static final int[][] EFFECT_STYLEABLES = {
        {
            R.styleable.ShapeEditText_shape_effect1Type,
            R.styleable.ShapeEditText_shape_effect1Color,
            R.styleable.ShapeEditText_shape_effect1Blur,
            R.styleable.ShapeEditText_shape_effect1Spread,
            R.styleable.ShapeEditText_shape_effect1OffsetX,
            R.styleable.ShapeEditText_shape_effect1OffsetY,
            R.styleable.ShapeEditText_shape_effect1Edges,
        },
        {
            R.styleable.ShapeEditText_shape_effect2Type,
            R.styleable.ShapeEditText_shape_effect2Color,
            R.styleable.ShapeEditText_shape_effect2Blur,
            R.styleable.ShapeEditText_shape_effect2Spread,
            R.styleable.ShapeEditText_shape_effect2OffsetX,
            R.styleable.ShapeEditText_shape_effect2OffsetY,
            R.styleable.ShapeEditText_shape_effect2Edges,
        },
        {
            R.styleable.ShapeEditText_shape_effect3Type,
            R.styleable.ShapeEditText_shape_effect3Color,
            R.styleable.ShapeEditText_shape_effect3Blur,
            R.styleable.ShapeEditText_shape_effect3Spread,
            R.styleable.ShapeEditText_shape_effect3OffsetX,
            R.styleable.ShapeEditText_shape_effect3OffsetY,
            R.styleable.ShapeEditText_shape_effect3Edges,
        },
        {
            R.styleable.ShapeEditText_shape_effect4Type,
            R.styleable.ShapeEditText_shape_effect4Color,
            R.styleable.ShapeEditText_shape_effect4Blur,
            R.styleable.ShapeEditText_shape_effect4Spread,
            R.styleable.ShapeEditText_shape_effect4OffsetX,
            R.styleable.ShapeEditText_shape_effect4OffsetY,
            R.styleable.ShapeEditText_shape_effect4Edges,
        },
    };

    /**
     * {@link IShapeDrawableStyleable}
     */

    @Override
    public int getShapeTypeStyleable() {
        return R.styleable.ShapeEditText_shape_type;
    }

    @Override
    public int getShapeWidthStyleable() {
        return R.styleable.ShapeEditText_shape_width;
    }

    @Override
    public int getShapeHeightStyleable() {
        return R.styleable.ShapeEditText_shape_height;
    }

    @Override
    public int getRadiusStyleable() {
        return R.styleable.ShapeEditText_shape_radius;
    }

    @Override
    public int getRadiusInTopLeftStyleable() {
        return R.styleable.ShapeEditText_shape_radiusInTopLeft;
    }

    @Override
    public int getRadiusInTopStartStyleable() {
        return R.styleable.ShapeEditText_shape_radiusInTopStart;
    }

    @Override
    public int getRadiusInTopRightStyleable() {
        return R.styleable.ShapeEditText_shape_radiusInTopRight;
    }

    @Override
    public int getRadiusInTopEndStyleable() {
        return R.styleable.ShapeEditText_shape_radiusInTopEnd;
    }

    @Override
    public int getRadiusInBottomLeftStyleable() {
        return R.styleable.ShapeEditText_shape_radiusInBottomLeft;
    }

    @Override
    public int getRadiusInBottomStartStyleable() {
        return R.styleable.ShapeEditText_shape_radiusInBottomStart;
    }

    @Override
    public int getRadiusInBottomRightStyleable() {
        return R.styleable.ShapeEditText_shape_radiusInBottomRight;
    }

    @Override
    public int getRadiusInBottomEndStyleable() {
        return R.styleable.ShapeEditText_shape_radiusInBottomEnd;
    }

    @Override
    public int getSolidColorStyleable() {
        return R.styleable.ShapeEditText_shape_solidColor;
    }

    @Override
    public int getSolidPressedColorStyleable() {
        return R.styleable.ShapeEditText_shape_solidPressedColor;
    }

    @Override
    public int getSolidDisabledColorStyleable() {
        return R.styleable.ShapeEditText_shape_solidDisabledColor;
    }

    @Override
    public int getSolidFocusedColorStyleable() {
        return R.styleable.ShapeEditText_shape_solidFocusedColor;
    }

    @Override
    public int getSolidSelectedColorStyleable() {
        return R.styleable.ShapeEditText_shape_solidSelectedColor;
    }

    @Override
    public int getSolidGradientStartColorStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientStartColor;
    }

    @Override
    public int getSolidGradientCenterColorStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientCenterColor;
    }

    @Override
    public int getSolidGradientEndColorStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientEndColor;
    }

    @Override
    public int getSolidGradientOrientationStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientOrientation;
    }

    @Override
    public int getSolidGradientTypeStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientType;
    }

    @Override
    public int getSolidGradientCenterXStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientCenterX;
    }

    @Override
    public int getSolidGradientCenterYStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientCenterY;
    }

    @Override
    public int getSolidGradientRadiusStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientRadius;
    }

    @Override
    public int getSolidGradientRadiusSizeStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientRadiusSize;
    }

    @Override
    public int getSolidGradientRadiusRatioStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientRadiusRatio;
    }

    @Override
    public int getSolidGradientRadiusXStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientRadiusX;
    }

    @Override
    public int getSolidGradientRadiusYStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientRadiusY;
    }

    @Override
    public int getSolidRadialAngleStyleable() {
        return R.styleable.ShapeEditText_shape_solidRadialAngle;
    }

    @Override
    public int getSolidGradientStartPercentStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientStartPercent;
    }

    @Override
    public int getSolidGradientCenterPercentStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientCenterPercent;
    }

    @Override
    public int getSolidGradientEndPercentStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientEndPercent;
    }

    @Override
    public int getSolidGradientStartXStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientStartX;
    }

    @Override
    public int getSolidGradientStartYStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientStartY;
    }

    @Override
    public int getSolidGradientEndXStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientEndX;
    }

    @Override
    public int getSolidGradientEndYStyleable() {
        return R.styleable.ShapeEditText_shape_solidGradientEndY;
    }

    @Override
    public int getStrokeColorStyleable() {
        return R.styleable.ShapeEditText_shape_strokeColor;
    }

    @Override
    public int getStrokePressedColorStyleable() {
        return R.styleable.ShapeEditText_shape_strokePressedColor;
    }

    @Override
    public int getStrokeDisabledColorStyleable() {
        return R.styleable.ShapeEditText_shape_strokeDisabledColor;
    }

    @Override
    public int getStrokeFocusedColorStyleable() {
        return R.styleable.ShapeEditText_shape_strokeFocusedColor;
    }

    @Override
    public int getStrokeSelectedColorStyleable() {
        return R.styleable.ShapeEditText_shape_strokeSelectedColor;
    }

    @Override
    public int getStrokeGradientStartColorStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientStartColor;
    }

    @Override
    public int getStrokeGradientCenterColorStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientCenterColor;
    }

    @Override
    public int getStrokeGradientEndColorStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientEndColor;
    }

    @Override
    public int getStrokeGradientOrientationStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientOrientation;
    }

    @Override
    public int getStrokeGradientTypeStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientType;
    }

    @Override
    public int getStrokeGradientCenterXStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientCenterX;
    }

    @Override
    public int getStrokeGradientCenterYStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientCenterY;
    }

    @Override
    public int getStrokeGradientRadiusSizeStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientRadiusSize;
    }

    @Override
    public int getStrokeGradientRadiusRatioStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientRadiusRatio;
    }

    @Override
    public int getStrokeGradientRadiusXStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientRadiusX;
    }

    @Override
    public int getStrokeGradientRadiusYStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientRadiusY;
    }

    @Override
    public int getStrokeRadialAngleStyleable() {
        return R.styleable.ShapeEditText_shape_strokeRadialAngle;
    }

    @Override
    public int getStrokeGradientStartPercentStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientStartPercent;
    }

    @Override
    public int getStrokeGradientCenterPercentStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientCenterPercent;
    }

    @Override
    public int getStrokeGradientEndPercentStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientEndPercent;
    }

    @Override
    public int getStrokeGradientStartXStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientStartX;
    }

    @Override
    public int getStrokeGradientStartYStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientStartY;
    }

    @Override
    public int getStrokeGradientEndXStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientEndX;
    }

    @Override
    public int getStrokeGradientEndYStyleable() {
        return R.styleable.ShapeEditText_shape_strokeGradientEndY;
    }

    @Override
    public int getStrokeSizeStyleable() {
        return R.styleable.ShapeEditText_shape_strokeSize;
    }

    @Override
    public int getStrokeDashSizeStyleable() {
        return R.styleable.ShapeEditText_shape_strokeDashSize;
    }

    @Override
    public int getStrokeDashGapStyleable() {
        return R.styleable.ShapeEditText_shape_strokeDashGap;
    }

    @Override
    public int[][] getEffectStyleables() {
        return EFFECT_STYLEABLES;
    }

    @Override
    public int getEffectPadContentStyleable() {
        return R.styleable.ShapeEditText_shape_effectPadContent;
    }

    @Override
    public int getRingInnerRadiusSizeStyleable() {
        return R.styleable.ShapeEditText_shape_ringInnerRadiusSize;
    }

    @Override
    public int getRingInnerRadiusRatioStyleable() {
        return R.styleable.ShapeEditText_shape_ringInnerRadiusRatio;
    }

    @Override
    public int getRingThicknessSizeStyleable() {
        return R.styleable.ShapeEditText_shape_ringThicknessSize;
    }

    @Override
    public int getRingThicknessRatioStyleable() {
        return R.styleable.ShapeEditText_shape_ringThicknessRatio;
    }

    @Override
    public int getLineGravityStyleable() {
        return R.styleable.ShapeEditText_shape_lineGravity;
    }

    /**
     * {@link ITextColorStyleable}
     */

    @Override
    public int getTextColorStyleable() {
        return R.styleable.ShapeEditText_shape_textColor;
    }

    @Override
    public int getTextPressedColorStyleable() {
        return R.styleable.ShapeEditText_shape_textPressedColor;
    }

    @Override
    public int getTextDisabledColorStyleable() {
        return R.styleable.ShapeEditText_shape_textDisabledColor;
    }

    @Override
    public int getTextFocusedColorStyleable() {
        return R.styleable.ShapeEditText_shape_textFocusedColor;
    }

    @Override
    public int getTextSelectedColorStyleable() {
        return R.styleable.ShapeEditText_shape_textSelectedColor;
    }

    @Override
    public int getTextStartColorStyleable() {
        return R.styleable.ShapeEditText_shape_textStartColor;
    }

    @Override
    public int getTextCenterColorStyleable() {
        return R.styleable.ShapeEditText_shape_textCenterColor;
    }

    @Override
    public int getTextEndColorStyleable() {
        return R.styleable.ShapeEditText_shape_textEndColor;
    }

    @Override
    public int getTextGradientOrientationStyleable() {
        return R.styleable.ShapeEditText_shape_textGradientOrientation;
    }

    @Override
    public int getTextStrokeColorStyleable() {
        return R.styleable.ShapeEditText_shape_textStrokeColor;
    }

    @Override
    public int getTextStrokeSizeStyleable() {
        return R.styleable.ShapeEditText_shape_textStrokeSize;
    }
}