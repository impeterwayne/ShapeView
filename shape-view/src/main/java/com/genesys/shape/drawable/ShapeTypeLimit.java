package com.genesys.shape.drawable;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeDrawable
 *    time   : 2023/07/16
 *    desc   : Shape Type Assignment Limitation
 */
@IntDef({ShapeType.RECTANGLE, ShapeType.OVAL,
        ShapeType.LINE, ShapeType.RING})
@Retention(RetentionPolicy.SOURCE)
public @interface ShapeTypeLimit {}