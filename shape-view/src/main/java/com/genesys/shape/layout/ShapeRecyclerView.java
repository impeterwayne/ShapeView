package com.genesys.shape.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

import com.genesys.shape.R;
import com.genesys.shape.builder.RippleBuilder;
import com.genesys.shape.builder.ShapeDrawableBuilder;
import com.genesys.shape.config.IGetShapeDrawableBuilder;
import com.genesys.shape.styleable.ShapeRecyclerViewStyleable;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeView
 *    time   : 2021/07/17
 *    desc   : RecyclerView that supports direct definition of Shape background
 */
public class ShapeRecyclerView extends RecyclerView implements IGetShapeDrawableBuilder {

    private static final ShapeRecyclerViewStyleable STYLEABLE = new ShapeRecyclerViewStyleable();

    private final ShapeDrawableBuilder mShapeDrawableBuilder;
    private final RippleBuilder mRippleBuilder;

    public ShapeRecyclerView(Context context) {
        this(context, null);
    }

    public ShapeRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShapeRecyclerView);
        mShapeDrawableBuilder = new ShapeDrawableBuilder(this, typedArray, STYLEABLE);
        mRippleBuilder = new RippleBuilder(this, mShapeDrawableBuilder, typedArray,
                R.styleable.ShapeRecyclerView_shape_ripple_enabled,
                R.styleable.ShapeRecyclerView_shape_ripple_color,
                R.styleable.ShapeRecyclerView_shape_ripple_radius);
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