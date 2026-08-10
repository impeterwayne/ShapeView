package com.genesys.shape.styleable;

import com.genesys.shape.R;
import com.genesys.shape.config.IShapeDrawableStyleable;

/**
 * View's Shape attribute values
 */
public final class ShapeViewStyleable implements IShapeDrawableStyleable {

    /** {@link IShapeDrawableStyleable#getEffectStyleables()} */
    private static final int[][] EFFECT_STYLEABLES = {
        {
            R.styleable.ShapeView_shape_effect1Type,
            R.styleable.ShapeView_shape_effect1Color,
            R.styleable.ShapeView_shape_effect1Blur,
            R.styleable.ShapeView_shape_effect1Spread,
            R.styleable.ShapeView_shape_effect1OffsetX,
            R.styleable.ShapeView_shape_effect1OffsetY,
            R.styleable.ShapeView_shape_effect1Edges,
        },
        {
            R.styleable.ShapeView_shape_effect2Type,
            R.styleable.ShapeView_shape_effect2Color,
            R.styleable.ShapeView_shape_effect2Blur,
            R.styleable.ShapeView_shape_effect2Spread,
            R.styleable.ShapeView_shape_effect2OffsetX,
            R.styleable.ShapeView_shape_effect2OffsetY,
            R.styleable.ShapeView_shape_effect2Edges,
        },
        {
            R.styleable.ShapeView_shape_effect3Type,
            R.styleable.ShapeView_shape_effect3Color,
            R.styleable.ShapeView_shape_effect3Blur,
            R.styleable.ShapeView_shape_effect3Spread,
            R.styleable.ShapeView_shape_effect3OffsetX,
            R.styleable.ShapeView_shape_effect3OffsetY,
            R.styleable.ShapeView_shape_effect3Edges,
        },
        {
            R.styleable.ShapeView_shape_effect4Type,
            R.styleable.ShapeView_shape_effect4Color,
            R.styleable.ShapeView_shape_effect4Blur,
            R.styleable.ShapeView_shape_effect4Spread,
            R.styleable.ShapeView_shape_effect4OffsetX,
            R.styleable.ShapeView_shape_effect4OffsetY,
            R.styleable.ShapeView_shape_effect4Edges,
        },
    };

    /**
     * {@link IShapeDrawableStyleable}
     */

    @Override
    public int getShapeTypeStyleable() {
        return R.styleable.ShapeView_shape_type;
    }

    @Override
    public int getShapeWidthStyleable() {
        return R.styleable.ShapeView_shape_width;
    }

    @Override
    public int getShapeHeightStyleable() {
        return R.styleable.ShapeView_shape_height;
    }

    @Override
    public int getRadiusStyleable() {
        return R.styleable.ShapeView_shape_radius;
    }

    @Override
    public int getRadiusInTopLeftStyleable() {
        return R.styleable.ShapeView_shape_radiusInTopLeft;
    }

    @Override
    public int getRadiusInTopStartStyleable() {
        return R.styleable.ShapeView_shape_radiusInTopStart;
    }

    @Override
    public int getRadiusInTopRightStyleable() {
        return R.styleable.ShapeView_shape_radiusInTopRight;
    }

    @Override
    public int getRadiusInTopEndStyleable() {
        return R.styleable.ShapeView_shape_radiusInTopEnd;
    }

    @Override
    public int getRadiusInBottomLeftStyleable() {
        return R.styleable.ShapeView_shape_radiusInBottomLeft;
    }

    @Override
    public int getRadiusInBottomStartStyleable() {
        return R.styleable.ShapeView_shape_radiusInBottomStart;
    }

    @Override
    public int getRadiusInBottomRightStyleable() {
        return R.styleable.ShapeView_shape_radiusInBottomRight;
    }

    @Override
    public int getRadiusInBottomEndStyleable() {
        return R.styleable.ShapeView_shape_radiusInBottomEnd;
    }

    @Override
    public int getSolidColorStyleable() {
        return R.styleable.ShapeView_shape_solidColor;
    }

    @Override
    public int getSolidPressedColorStyleable() {
        return R.styleable.ShapeView_shape_solidPressedColor;
    }

    @Override
    public int getSolidDisabledColorStyleable() {
        return R.styleable.ShapeView_shape_solidDisabledColor;
    }

    @Override
    public int getSolidFocusedColorStyleable() {
        return R.styleable.ShapeView_shape_solidFocusedColor;
    }

    @Override
    public int getSolidSelectedColorStyleable() {
        return R.styleable.ShapeView_shape_solidSelectedColor;
    }

    @Override
    public int getSolidGradientStartColorStyleable() {
        return R.styleable.ShapeView_shape_solidGradientStartColor;
    }

    @Override
    public int getSolidGradientCenterColorStyleable() {
        return R.styleable.ShapeView_shape_solidGradientCenterColor;
    }

    @Override
    public int getSolidGradientEndColorStyleable() {
        return R.styleable.ShapeView_shape_solidGradientEndColor;
    }

    @Override
    public int getSolidGradientOrientationStyleable() {
        return R.styleable.ShapeView_shape_solidGradientOrientation;
    }

    @Override
    public int getSolidGradientTypeStyleable() {
        return R.styleable.ShapeView_shape_solidGradientType;
    }

    @Override
    public int getSolidGradientCenterXStyleable() {
        return R.styleable.ShapeView_shape_solidGradientCenterX;
    }

    @Override
    public int getSolidGradientCenterYStyleable() {
        return R.styleable.ShapeView_shape_solidGradientCenterY;
    }

    @Override
    public int getSolidGradientRadiusStyleable() {
        return R.styleable.ShapeView_shape_solidGradientRadius;
    }

    @Override
    public int getSolidGradientRadiusSizeStyleable() {
        return R.styleable.ShapeView_shape_solidGradientRadiusSize;
    }

    @Override
    public int getSolidGradientRadiusRatioStyleable() {
        return R.styleable.ShapeView_shape_solidGradientRadiusRatio;
    }

    @Override
    public int getSolidGradientRadiusXStyleable() {
        return R.styleable.ShapeView_shape_solidGradientRadiusX;
    }

    @Override
    public int getSolidGradientRadiusYStyleable() {
        return R.styleable.ShapeView_shape_solidGradientRadiusY;
    }

    @Override
    public int getSolidRadialAngleStyleable() {
        return R.styleable.ShapeView_shape_solidRadialAngle;
    }

    @Override
    public int getSolidGradientStartPercentStyleable() {
        return R.styleable.ShapeView_shape_solidGradientStartPercent;
    }

    @Override
    public int getSolidGradientCenterPercentStyleable() {
        return R.styleable.ShapeView_shape_solidGradientCenterPercent;
    }

    @Override
    public int getSolidGradientEndPercentStyleable() {
        return R.styleable.ShapeView_shape_solidGradientEndPercent;
    }

    @Override
    public int getSolidGradientStartXStyleable() {
        return R.styleable.ShapeView_shape_solidGradientStartX;
    }

    @Override
    public int getSolidGradientStartYStyleable() {
        return R.styleable.ShapeView_shape_solidGradientStartY;
    }

    @Override
    public int getSolidGradientEndXStyleable() {
        return R.styleable.ShapeView_shape_solidGradientEndX;
    }

    @Override
    public int getSolidGradientEndYStyleable() {
        return R.styleable.ShapeView_shape_solidGradientEndY;
    }

    @Override
    public int getStrokeColorStyleable() {
        return R.styleable.ShapeView_shape_strokeColor;
    }

    @Override
    public int getStrokePressedColorStyleable() {
        return R.styleable.ShapeView_shape_strokePressedColor;
    }

    @Override
    public int getStrokeDisabledColorStyleable() {
        return R.styleable.ShapeView_shape_strokeDisabledColor;
    }

    @Override
    public int getStrokeFocusedColorStyleable() {
        return R.styleable.ShapeView_shape_strokeFocusedColor;
    }

    @Override
    public int getStrokeSelectedColorStyleable() {
        return R.styleable.ShapeView_shape_strokeSelectedColor;
    }

    @Override
    public int getStrokeGradientStartColorStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientStartColor;
    }

    @Override
    public int getStrokeGradientCenterColorStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientCenterColor;
    }

    @Override
    public int getStrokeGradientEndColorStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientEndColor;
    }

    @Override
    public int getStrokeGradientOrientationStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientOrientation;
    }

    @Override
    public int getStrokeGradientTypeStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientType;
    }

    @Override
    public int getStrokeGradientCenterXStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientCenterX;
    }

    @Override
    public int getStrokeGradientCenterYStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientCenterY;
    }

    @Override
    public int getStrokeGradientRadiusSizeStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientRadiusSize;
    }

    @Override
    public int getStrokeGradientRadiusRatioStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientRadiusRatio;
    }

    @Override
    public int getStrokeGradientRadiusXStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientRadiusX;
    }

    @Override
    public int getStrokeGradientRadiusYStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientRadiusY;
    }

    @Override
    public int getStrokeRadialAngleStyleable() {
        return R.styleable.ShapeView_shape_strokeRadialAngle;
    }

    @Override
    public int getStrokeGradientStartPercentStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientStartPercent;
    }

    @Override
    public int getStrokeGradientCenterPercentStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientCenterPercent;
    }

    @Override
    public int getStrokeGradientEndPercentStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientEndPercent;
    }

    @Override
    public int getStrokeGradientStartXStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientStartX;
    }

    @Override
    public int getStrokeGradientStartYStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientStartY;
    }

    @Override
    public int getStrokeGradientEndXStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientEndX;
    }

    @Override
    public int getStrokeGradientEndYStyleable() {
        return R.styleable.ShapeView_shape_strokeGradientEndY;
    }

    @Override
    public int getStrokeSizeStyleable() {
        return R.styleable.ShapeView_shape_strokeSize;
    }

    @Override
    public int getStrokeDashSizeStyleable() {
        return R.styleable.ShapeView_shape_strokeDashSize;
    }

    @Override
    public int getStrokeDashGapStyleable() {
        return R.styleable.ShapeView_shape_strokeDashGap;
    }

    @Override
    public int[][] getEffectStyleables() {
        return EFFECT_STYLEABLES;
    }

    @Override
    public int getEffectPadContentStyleable() {
        return R.styleable.ShapeView_shape_effectPadContent;
    }

    @Override
    public int getRingInnerRadiusSizeStyleable() {
        return R.styleable.ShapeView_shape_ringInnerRadiusSize;
    }

    @Override
    public int getRingInnerRadiusRatioStyleable() {
        return R.styleable.ShapeView_shape_ringInnerRadiusRatio;
    }

    @Override
    public int getRingThicknessSizeStyleable() {
        return R.styleable.ShapeView_shape_ringThicknessSize;
    }

    @Override
    public int getRingThicknessRatioStyleable() {
        return R.styleable.ShapeView_shape_ringThicknessRatio;
    }

    @Override
    public int getLineGravityStyleable() {
        return R.styleable.ShapeView_shape_lineGravity;
    }

}