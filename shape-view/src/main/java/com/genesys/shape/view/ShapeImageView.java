package com.genesys.shape.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.genesys.shape.R;
import com.genesys.shape.builder.RippleBuilder;
import com.genesys.shape.builder.ShapeDrawableBuilder;
import com.genesys.shape.config.IGetShapeDrawableBuilder;
import com.genesys.shape.styleable.ShapeImageViewStyleable;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeView
 *    time   : 2021/07/17
 *    desc   : ImageView that supports direct definition of Shape background
 */
public class ShapeImageView extends AppCompatImageView implements IGetShapeDrawableBuilder {

    private static final ShapeImageViewStyleable STYLEABLE = new ShapeImageViewStyleable();

    private final ShapeDrawableBuilder mShapeDrawableBuilder;
    private final RippleBuilder mRippleBuilder;

    public ShapeImageView(Context context) {
        this(context, null);
    }

    public ShapeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShapeImageView);
        mShapeDrawableBuilder = new ShapeDrawableBuilder(this, typedArray, STYLEABLE);
        mRippleBuilder = new RippleBuilder(this, mShapeDrawableBuilder, typedArray,
                R.styleable.ShapeImageView_shape_ripple_enabled,
                R.styleable.ShapeImageView_shape_ripple_color,
                R.styleable.ShapeImageView_shape_ripple_radius);
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