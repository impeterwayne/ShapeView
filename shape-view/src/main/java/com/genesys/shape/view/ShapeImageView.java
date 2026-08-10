package com.genesys.shape.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
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
 * ImageView that supports direct definition of Shape background
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

        // Clip to the shape the background actually painted, which sits inside whatever room
        // the shadow claimed on each edge.
        Rect shadowInsets = builder.getShadowInsets();
        float strokeSize = builder.getStrokeSize();

        float left = shadowInsets.left + strokeSize;
        float top = shadowInsets.top + strokeSize;
        float right = getWidth() - shadowInsets.right - strokeSize;
        float bottom = getHeight() - shadowInsets.bottom - strokeSize;

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