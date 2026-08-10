package com.genesys.shape.drawable;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Shape Gradient Type Assignment Limitation
 */
@IntDef({
    ShapeGradientType.LINEAR_GRADIENT,
    ShapeGradientType.RADIAL_GRADIENT,
    ShapeGradientType.SWEEP_GRADIENT
})
@Retention(RetentionPolicy.SOURCE)
public @interface ShapeGradientTypeLimit {}