package com.genesys.shape.drawable;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ShapeEffectType.DROP_SHADOW, ShapeEffectType.INNER_SHADOW})
@Retention(RetentionPolicy.SOURCE)
public @interface ShapeEffectTypeLimit {}
