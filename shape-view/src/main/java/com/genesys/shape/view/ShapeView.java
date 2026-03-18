package com.genesys.shape.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.genesys.shape.R;
import com.genesys.shape.builder.RippleBuilder;
import com.genesys.shape.builder.ShapeDrawableBuilder;
import com.genesys.shape.config.IGetShapeDrawableBuilder;
import com.genesys.shape.styleable.ShapeViewStyleable;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeView
 *    time   : 2021/07/17
 *    desc   : View that supports direct definition of Shape background
 */
public class ShapeView extends View implements IGetShapeDrawableBuilder {

    private static final ShapeViewStyleable STYLEABLE = new ShapeViewStyleable();

    private final ShapeDrawableBuilder mShapeDrawableBuilder;
    private final RippleBuilder mRippleBuilder;

    public ShapeView(Context context) {
        this(context, null);
    }

    public ShapeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShapeView);
        mShapeDrawableBuilder = new ShapeDrawableBuilder(this, typedArray, STYLEABLE);
        mRippleBuilder = new RippleBuilder(this, mShapeDrawableBuilder, typedArray,
                R.styleable.ShapeView_shape_ripple_enabled,
                R.styleable.ShapeView_shape_ripple_color,
                R.styleable.ShapeView_shape_ripple_radius);
        typedArray.recycle();

        mShapeDrawableBuilder.intoBackground();
        mRippleBuilder.apply();
    }

    @Override
    public ShapeDrawableBuilder getShapeDrawableBuilder() {
        return mShapeDrawableBuilder;
    }

    public RippleBuilder getRippleBuilder() {
        return mRippleBuilder;
    }
}