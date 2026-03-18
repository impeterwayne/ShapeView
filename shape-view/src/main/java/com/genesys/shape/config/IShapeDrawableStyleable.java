package com.genesys.shape.config;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeView
 *    time   : 2021/08/28
 *    desc   : ShapeDrawable View attribute collection interface
 */
public interface IShapeDrawableStyleable {

    int getShapeTypeStyleable();

    int getShapeWidthStyleable();

    int getShapeHeightStyleable();

    int getRadiusStyleable();

    int getRadiusInTopLeftStyleable();

    int getRadiusInTopStartStyleable();

    int getRadiusInTopRightStyleable();

    int getRadiusInTopEndStyleable();

    int getRadiusInBottomLeftStyleable();

    int getRadiusInBottomStartStyleable();

    int getRadiusInBottomRightStyleable();

    int getRadiusInBottomEndStyleable();

    int getSolidColorStyleable();

    int getSolidPressedColorStyleable();

    default int getSolidCheckedColorStyleable() {
        return 0;
    }

    int getSolidDisabledColorStyleable();

    int getSolidFocusedColorStyleable();

    int getSolidSelectedColorStyleable();

    int getSolidGradientStartColorStyleable();

    int getSolidGradientCenterColorStyleable();

    int getSolidGradientEndColorStyleable();

    int getSolidGradientOrientationStyleable();

    int getSolidGradientTypeStyleable();

    int getSolidGradientCenterXStyleable();

    int getSolidGradientCenterYStyleable();

    int getSolidGradientRadiusStyleable();

    // Radial gradient ellipse scale factors (for elliptical gradients)
    default int getSolidGradientRadiusXStyleable() {
        return 0;
    }

    default int getSolidGradientRadiusYStyleable() {
        return 0;
    }

    // Radial gradient rotation angle
    default int getSolidRadialAngleStyleable() {
        return 0;
    }

    // Gradient color stop positions (0.0 to 1.0)
    default int getSolidGradientStartPercentStyleable() {
        return 0;
    }

    default int getSolidGradientCenterPercentStyleable() {
        return 0;
    }

    default int getSolidGradientEndPercentStyleable() {
        return 0;
    }

    // Gradient extent positions (0.0 to 1.0 as percentage of view dimensions)
    default int getSolidGradientStartXStyleable() {
        return 0;
    }

    default int getSolidGradientStartYStyleable() {
        return 0;
    }

    default int getSolidGradientEndXStyleable() {
        return 0;
    }

    default int getSolidGradientEndYStyleable() {
        return 0;
    }

    int getStrokeColorStyleable();

    int getStrokePressedColorStyleable();

    default int getStrokeCheckedColorStyleable() {
        return 0;
    }

    int getStrokeDisabledColorStyleable();

    int getStrokeFocusedColorStyleable();

    int getStrokeSelectedColorStyleable();

    int getStrokeGradientStartColorStyleable();

    int getStrokeGradientCenterColorStyleable();

    int getStrokeGradientEndColorStyleable();

    int getStrokeGradientOrientationStyleable();

    int getStrokeSizeStyleable();

    int getStrokeDashSizeStyleable();

    int getStrokeDashGapStyleable();

    int getOuterShadowSizeStyleable();

    int getOuterShadowColorStyleable();

    int getOuterShadowOffsetXStyleable();

    int getOuterShadowOffsetYStyleable();

    int getRingInnerRadiusSizeStyleable();

    int getRingInnerRadiusRatioStyleable();

    int getRingThicknessSizeStyleable();

    int getRingThicknessRatioStyleable();

    int getLineGravityStyleable();

    // Primary Inner Shadow styleable methods
    default int getInnerShadowSizeStyleable() {
        return 0;
    }

    default int getInnerShadowColorStyleable() {
        return 0;
    }

    default int getInnerShadowOffsetXStyleable() {
        return 0;
    }

    default int getInnerShadowOffsetYStyleable() {
        return 0;
    }

    // Secondary Inner Shadow styleable methods (for bevel effects)
    default int getInnerShadow2SizeStyleable() {
        return 0;
    }

    default int getInnerShadow2ColorStyleable() {
        return 0;
    }

    default int getInnerShadow2OffsetXStyleable() {
        return 0;
    }

    default int getInnerShadow2OffsetYStyleable() {
        return 0;
    }
}