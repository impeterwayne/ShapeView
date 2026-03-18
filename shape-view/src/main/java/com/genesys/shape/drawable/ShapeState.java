package com.genesys.shape.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import java.util.ArrayList;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeDrawable
 *    time   : 2021/08/15
 *    desc   : ShapeDrawable parameter construction
 */
public class ShapeState extends Drawable.ConstantState {

    public int changingConfigurations;
    @ShapeTypeLimit
    public int shapeType = ShapeType.RECTANGLE;
    @ShapeGradientTypeLimit
    public int solidGradientType = ShapeGradientType.LINEAR_GRADIENT;
    public ShapeGradientOrientation solidGradientOrientation = ShapeGradientOrientation.TOP_TO_BOTTOM;
    public int[] solidColors;
    public int[] strokeColors;
    public int[] tempSolidColors; // no need to copy
    public float[] tempSolidPositions; // no need to copy
    public float[] positions;
    public boolean hasSolidColor;
    public boolean hasStrokeColor;
    public int solidColor;
    public int strokeSize = -1;   // if >= 0 use stroking.
    public ShapeGradientOrientation strokeGradientOrientation = ShapeGradientOrientation.TOP_TO_BOTTOM;
    public int strokeColor;
    public ColorStateList solidColorStateList;
    public ColorStateList strokeColorStateList;

    public ColorStateList solidGradientStartColorStateList;
    public ColorStateList solidGradientCenterColorStateList;
    public ColorStateList solidGradientEndColorStateList;

    public ColorStateList strokeGradientStartColorStateList;
    public ColorStateList strokeGradientCenterColorStateList;
    public ColorStateList strokeGradientEndColorStateList;

    public ColorStateList outerShadowColorStateList;
    public float strokeDashSize;
    public float strokeDashGap;
    public float radius;    // use this if mRadiusArray is null
    public float[] radiusArray;
    public Rect padding;
    public int width = -1;
    public int height = -1;
    public float ringInnerRadiusRatio;
    public float ringThicknessRatio;
    public int ringInnerRadiusSize = -1;
    public int ringThicknessSize = -1;
    public float solidCenterX = 0.5f;
    public float solidCenterY = 0.5f;
    public float gradientRadius = 0.5f;

    // ===== Radial Gradient Transformation =====
    /** Rotation angle for radial gradient (in degrees). 0 = no rotation. */
    public float radialGradientAngle = 0f;
    /** Horizontal radius for elliptical gradients. -1 means use gradientRadius. */
    public float gradientRadiusX = -1f;
    /** Vertical radius for elliptical gradients. -1 means use gradientRadius. */
    public float gradientRadiusY = -1f;
    /** Start X position (focal point) relative to view (0-1). NaN = same as center. */
    public float radialStartX = Float.NaN;
    /** Start Y position (focal point) relative to view (0-1). NaN = same as center. */
    public float radialStartY = Float.NaN;

    // ===== Linear Gradient Extent Positions =====
    /** Start X position for linear gradient (0-1). NaN = use orientation. */
    public float linearGradientStartX = Float.NaN;
    /** Start Y position for linear gradient (0-1). NaN = use orientation. */
    public float linearGradientStartY = Float.NaN;
    /** End X position for linear gradient (0-1). NaN = use orientation. */
    public float linearGradientEndX = Float.NaN;
    /** End Y position for linear gradient (0-1). NaN = use orientation. */
    public float linearGradientEndY = Float.NaN;

    public boolean useLevel;
    public boolean useLevelForShape;
    public boolean opaque;

    /** Shadow size */
    public int outerShadowSize;
    /** Shadow color */
    public int outerShadowColor;
    /** Shadow horizontal offset */
    public int outerShadowOffsetX;
    /** Shadow vertical offset */
    public int outerShadowOffsetY;

    public int lineGravity = Gravity.CENTER;

    // ===== Inner Shadow Support =====
    /** List of inner shadow effects (rendered in order from first to last) */
    public ArrayList<InnerShadow> innerShadows;

    public ShapeState() {}

    public ShapeState(ShapeState state) {
        changingConfigurations = state.changingConfigurations;
        shapeType = state.shapeType;
        solidGradientType = state.solidGradientType;
        solidGradientOrientation = state.solidGradientOrientation;
        if (state.solidColors != null) {
            solidColors = state.solidColors.clone();
        }
        if (state.strokeColors != null) {
            strokeColors = state.strokeColors.clone();
        }
        if (state.positions != null) {
            positions = state.positions.clone();
        }
        hasSolidColor = state.hasSolidColor;
        hasStrokeColor = state.hasStrokeColor;
        solidColor = state.solidColor;
        strokeSize = state.strokeSize;
        strokeGradientOrientation = state.strokeGradientOrientation;
        strokeColor = state.strokeColor;
        strokeDashSize = state.strokeDashSize;
        strokeDashGap = state.strokeDashGap;
        radius = state.radius;
        if (state.radiusArray != null) {
            radiusArray = state.radiusArray.clone();
        }
        if (state.padding != null) {
            padding = new Rect(state.padding);
        }
        width = state.width;
        height = state.height;
        ringInnerRadiusRatio = state.ringInnerRadiusRatio;
        ringThicknessRatio = state.ringThicknessRatio;
        ringInnerRadiusSize = state.ringInnerRadiusSize;
        ringThicknessSize = state.ringThicknessSize;
        solidCenterX = state.solidCenterX;
        solidCenterY = state.solidCenterY;
        gradientRadius = state.gradientRadius;
        radialGradientAngle = state.radialGradientAngle;
        gradientRadiusX = state.gradientRadiusX;
        gradientRadiusY = state.gradientRadiusY;
        radialStartX = state.radialStartX;
        radialStartY = state.radialStartY;
        linearGradientStartX = state.linearGradientStartX;
        linearGradientStartY = state.linearGradientStartY;
        linearGradientEndX = state.linearGradientEndX;
        linearGradientEndY = state.linearGradientEndY;
        useLevel = state.useLevel;
        useLevelForShape = state.useLevelForShape;
        opaque = state.opaque;

        outerShadowSize = state.outerShadowSize;
        outerShadowColor = state.outerShadowColor;
        outerShadowOffsetX = state.outerShadowOffsetX;
        outerShadowOffsetY = state.outerShadowOffsetY;

        outerShadowOffsetX = state.outerShadowOffsetX;
        outerShadowOffsetY = state.outerShadowOffsetY;
        solidColorStateList = state.solidColorStateList;
        strokeColorStateList = state.strokeColorStateList;

        solidGradientStartColorStateList = state.solidGradientStartColorStateList;
        solidGradientCenterColorStateList = state.solidGradientCenterColorStateList;
        solidGradientEndColorStateList = state.solidGradientEndColorStateList;

        strokeGradientStartColorStateList = state.strokeGradientStartColorStateList;
        strokeGradientCenterColorStateList = state.strokeGradientCenterColorStateList;
        strokeGradientEndColorStateList = state.strokeGradientEndColorStateList;

        outerShadowColorStateList = state.outerShadowColorStateList;

        lineGravity = state.lineGravity;

        // Copy inner shadows
        if (state.innerShadows != null) {
            innerShadows = new ArrayList<>(state.innerShadows.size());
            for (InnerShadow shadow : state.innerShadows) {
                innerShadows.add(shadow.copy());
            }
        }
    }

    @Override
    public Drawable newDrawable() {
        return new ShapeDrawable(this);
    }

    @Override
    public Drawable newDrawable(Resources res) {
        return new ShapeDrawable(this);
    }

    @Override
    public int getChangingConfigurations() {
        return changingConfigurations;
    }

    public void setType(int shape) {
        shapeType = shape;
        computeOpacity();
    }

    public void setSolidGradientType(int gradientType) {
        this.solidGradientType = gradientType;
    }

    public void setSolidColor(int... colors) {
        if (colors == null) {
            solidColor = 0;
            hasSolidColor = true;
            computeOpacity();
            return;
        }

        if (colors.length == 1) {
            hasSolidColor = true;
            solidColor = colors[0];
            solidColors = null;
        } else {
            hasSolidColor = false;
            solidColor = 0;
            solidColors = colors;
        }
        computeOpacity();
    }

    public void setSolidColor(int argb) {
        hasSolidColor = true;
        solidColor = argb;
        solidColors = null;
        computeOpacity();
    }

    private void computeOpacity() {
        if (shapeType != ShapeType.RECTANGLE) {
            opaque = false;
            return;
        }

        if (radius > 0 || radiusArray != null) {
            opaque = false;
            return;
        }

        if (outerShadowSize > 0) {
            opaque = false;
            return;
        }

        if (innerShadows != null && !innerShadows.isEmpty()) {
            opaque = false;
            return;
        }

        if (strokeSize > 0 && !isOpaque(strokeColor)) {
            opaque = false;
            return;
        }

        if (hasSolidColor) {
            opaque = isOpaque(solidColor);
            return;
        }

        if (solidColors != null) {
            for (int color : solidColors) {
                if (!isOpaque(color)) {
                    opaque = false;
                    return;
                }
            }
        }

        if (hasStrokeColor) {
            opaque = isOpaque(strokeColor);
            return;
        }

        if (strokeColors != null) {
            for (int color : strokeColors) {
                if (!isOpaque(color)) {
                    opaque = false;
                    return;
                }
            }
        }

        opaque = true;
    }

    private static boolean isOpaque(int color) {
        return ((color >> 24) & 0xff) == 0xff;
    }

    public void setStrokeSize(int size) {
        strokeSize = size;
        computeOpacity();
    }

    public void setStrokeColor(int... colors) {
        if (colors == null) {
            strokeColor = 0;
            hasStrokeColor = true;
            computeOpacity();
            return;
        }

        if (colors.length == 1) {
            hasStrokeColor = true;
            strokeColor = colors[0];
            strokeColors = null;
        } else {
            hasStrokeColor = false;
            strokeColor = 0;
            strokeColors = colors;
        }
        computeOpacity();
    }

    public void setCornerRadius(float radius) {
        if (radius < 0) {
            radius = 0;
        }
        this.radius = radius;
        radiusArray = null;
    }

    public void setCornerRadii(float[] radii) {
        radiusArray = radii;
        if (radii == null) {
            radius = 0;
        }
    }
}