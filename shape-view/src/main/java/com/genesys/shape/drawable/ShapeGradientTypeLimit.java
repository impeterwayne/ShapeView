package com.genesys.shape.drawable;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeDrawable
 *    time   : 2023/07/16
 *    desc   : Shape Gradient Type Assignment Limitation
 */
@IntDef({
    ShapeGradientType.LINEAR_GRADIENT,
    ShapeGradientType.RADIAL_GRADIENT,
    ShapeGradientType.SWEEP_GRADIENT
})
@Retention(RetentionPolicy.SOURCE)
public @interface ShapeGradientTypeLimit {}