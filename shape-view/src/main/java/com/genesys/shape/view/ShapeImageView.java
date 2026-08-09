package com.genesys.shape.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.genesys.shape.R;
import com.genesys.shape.builder.RippleBuilder;
import com.genesys.shape.builder.ShapeDrawableBuilder;
import com.genesys.shape.config.IGetShapeDrawableBuilder;
import com.genesys.shape.drawable.ShapeType;
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

    private final Path mClipPath = new Path();
    private final RectF mClipRect = new RectF();
    private boolean mNeedsUpdateClipPath = true;

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mNeedsUpdateClipPath = true;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        mNeedsUpdateClipPath = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mNeedsUpdateClipPath) {
            updateClipPath();
            mNeedsUpdateClipPath = false;
        }

        int saveCount = canvas.save();
        try {
            canvas.clipPath(mClipPath);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    private void updateClipPath() {
        ShapeDrawableBuilder builder = getShapeDrawableBuilder();

        float shadowSize = builder.getOuterShadowSize();
        float shadowOffsetX = builder.getOuterShadowOffsetX();
        float shadowOffsetY = builder.getOuterShadowOffsetY();
        float strokeSize = builder.getStrokeSize();

        float left = shadowSize - shadowOffsetX + strokeSize;
        float top = shadowSize - shadowOffsetY + strokeSize;
        float right = getWidth() - shadowSize - shadowOffsetX - strokeSize;
        float bottom = getHeight() - shadowSize - shadowOffsetY - strokeSize;

        mClipRect.set(left, top, right, bottom);

        mClipPath.reset();
        if (builder.getType() == ShapeType.OVAL) {
            mClipPath.addOval(mClipRect, Path.Direction.CW);
        } else {
            float topLeftRadius = Math.max(builder.getTopLeftRadius() - strokeSize, 0f);
            float topRightRadius = Math.max(builder.getTopRightRadius() - strokeSize, 0f);
            float bottomRightRadius = Math.max(builder.getBottomRightRadius() - strokeSize, 0f);
            float bottomLeftRadius = Math.max(builder.getBottomLeftRadius() - strokeSize, 0f);

            float[] radii = new float[] {
                topLeftRadius, topLeftRadius,
                topRightRadius, topRightRadius,
                bottomRightRadius, bottomRightRadius,
                bottomLeftRadius, bottomLeftRadius
            };
            mClipPath.addRoundRect(mClipRect, radii, Path.Direction.CW);
        }
    }
}