package com.genesys.shape.config;

/**
 * ShapeDrawable View attribute collection interface
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
        return -1;
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

    /**
     * @deprecated superseded by {@link #getSolidGradientRadiusSizeStyleable()} and
     *             {@link #getSolidGradientRadiusRatioStyleable()}
     */
    @Deprecated
    int getSolidGradientRadiusStyleable();

    // Radial gradient radius, as an absolute size or as a ratio of half the shortest side
    default int getSolidGradientRadiusSizeStyleable() {
        return -1;
    }

    default int getSolidGradientRadiusRatioStyleable() {
        return -1;
    }

    // Radial gradient ellipse scale factors (for elliptical gradients)
    default int getSolidGradientRadiusXStyleable() {
        return -1;
    }

    default int getSolidGradientRadiusYStyleable() {
        return -1;
    }

    // Radial gradient rotation angle
    default int getSolidRadialAngleStyleable() {
        return -1;
    }

    // Gradient color stop positions (0.0 to 1.0)
    default int getSolidGradientStartPercentStyleable() {
        return -1;
    }

    default int getSolidGradientCenterPercentStyleable() {
        return -1;
    }

    default int getSolidGradientEndPercentStyleable() {
        return -1;
    }

    // Gradient extent positions (0.0 to 1.0 as percentage of view dimensions)
    default int getSolidGradientStartXStyleable() {
        return -1;
    }

    default int getSolidGradientStartYStyleable() {
        return -1;
    }

    default int getSolidGradientEndXStyleable() {
        return -1;
    }

    default int getSolidGradientEndYStyleable() {
        return -1;
    }

    int getStrokeColorStyleable();

    int getStrokePressedColorStyleable();

    default int getStrokeCheckedColorStyleable() {
        return -1;
    }

    int getStrokeDisabledColorStyleable();

    int getStrokeFocusedColorStyleable();

    int getStrokeSelectedColorStyleable();

    int getStrokeGradientStartColorStyleable();

    int getStrokeGradientCenterColorStyleable();

    int getStrokeGradientEndColorStyleable();

    int getStrokeGradientOrientationStyleable();

    default int getStrokeGradientTypeStyleable() {
        return -1;
    }

    default int getStrokeGradientCenterXStyleable() {
        return -1;
    }

    default int getStrokeGradientCenterYStyleable() {
        return -1;
    }

    default int getStrokeGradientRadiusSizeStyleable() {
        return -1;
    }

    default int getStrokeGradientRadiusRatioStyleable() {
        return -1;
    }

    // Stroke radial gradient ellipse scale factors (for elliptical gradients)
    default int getStrokeGradientRadiusXStyleable() {
        return -1;
    }

    default int getStrokeGradientRadiusYStyleable() {
        return -1;
    }

    // Stroke radial gradient rotation angle
    default int getStrokeRadialAngleStyleable() {
        return -1;
    }

    // Stroke gradient color stop positions (0.0 to 1.0)
    default int getStrokeGradientStartPercentStyleable() {
        return -1;
    }

    default int getStrokeGradientCenterPercentStyleable() {
        return -1;
    }

    default int getStrokeGradientEndPercentStyleable() {
        return -1;
    }

    // Stroke gradient extent positions (0.0 to 1.0 as percentage of view dimensions)
    default int getStrokeGradientStartXStyleable() {
        return -1;
    }

    default int getStrokeGradientStartYStyleable() {
        return -1;
    }

    default int getStrokeGradientEndXStyleable() {
        return -1;
    }

    default int getStrokeGradientEndYStyleable() {
        return -1;
    }

    int getStrokeSizeStyleable();

    int getStrokeDashSizeStyleable();

    int getStrokeDashGapStyleable();

    // ===== Effects =====

    /** Index of the type attribute within an effect slot, see {@link #getEffectStyleables()}. */
    int EFFECT_TYPE = 0;
    int EFFECT_COLOR = 1;
    int EFFECT_BLUR = 2;
    int EFFECT_SPREAD = 3;
    int EFFECT_OFFSET_X = 4;
    int EFFECT_OFFSET_Y = 5;
    int EFFECT_EDGES = 6;
    /** Number of attributes making up one effect slot. */
    int EFFECT_ATTR_COUNT = 7;

    /**
     * The numbered effect slots this view declares — {@code shape_effect1*},
     * {@code shape_effect2*} and so on — one row per slot, read in order.
     *
     * Each row holds the styleable index of that slot's attributes, in the order given by the
     * {@code EFFECT_*} constants above; an attribute the view does not declare is left at
     * {@code -1} and simply falls back to its default. Returning {@code null} means the view
     * carries no effect attributes at all.
     */
    default int[][] getEffectStyleables() {
        return null;
    }

    /**
     * Whether the room the drop shadows take is added to the view's padding as well.
     */
    default int getEffectPadContentStyleable() {
        return -1;
    }

    int getRingInnerRadiusSizeStyleable();

    int getRingInnerRadiusRatioStyleable();

    int getRingThicknessSizeStyleable();

    int getRingThicknessRatioStyleable();

    int getLineGravityStyleable();
}