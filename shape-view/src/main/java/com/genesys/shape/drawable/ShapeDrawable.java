package com.genesys.shape.drawable;

import android.annotation.SuppressLint;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import android.content.res.ColorStateList;
import java.util.ArrayList;

/**
 * author : Android Wheel
 * github : https://github.com/getActivity/ShapeDrawable
 * time   : 2021/08/14
 * desc   : Reconstructed based on {@link android.graphics.drawable.GradientDrawable}
 */
public class ShapeDrawable extends Drawable {

    private ShapeState mShapeState;

    private final Paint mSolidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect mPadding;
    private final Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);   // optional, set by the caller
    private Paint mShadowPaint;
    private Paint mInnerShadowPaint;  // Paint used for drawing inner shadows
    private ColorFilter mColorFilter;   // optional, set by the caller
    private int mAlpha = 0xFF;  // modified by the caller
    private boolean mDither;

    private final Path mPath = new Path();
    private final RectF mRect = new RectF();

    private final RectF mShadowRect = new RectF();
    private final Path mShadowPath = new Path();

    private Paint mLayerPaint;    // internal, used if we use saveLayer()
    private boolean mRectDirty;   // internal state
    private boolean mMutated;
    private Path mRingPath;
    private boolean mPathDirty = true;

    // Cached radial gradient objects to avoid allocation during draw
    private RadialGradient mCachedRadialGradient;
    private Matrix mCachedGradientMatrix;

    // Cached BlurMaskFilter for outer shadow to avoid per-frame allocation
    private BlurMaskFilter mCachedOuterShadowFilter;
    private float mCachedOuterShadowRadius = -1f;

    // Cached clip path for inner shadow rendering to avoid per-frame allocation
    private final Path mInnerShadowClipPath = new Path();

    /**
     * Current layout direction
     */
    private int mLayoutDirection;

    public ShapeDrawable() {
        this(new ShapeState());
    }

    public ShapeDrawable(ShapeState state) {
        mShapeState = state;
        initializeWithState(state);
        mRectDirty = true;
        mMutated = false;

        mStrokePaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Get Shape state object
     */
    public ShapeState getShapeState() {
        return mShapeState;
    }

    @Override
    public boolean getPadding(@NonNull Rect padding) {
        if (mPadding != null) {
            padding.set(mPadding);
            return true;
        }
        return super.getPadding(padding);
    }

    public ShapeDrawable setPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        return setPadding(new Rect(paddingLeft, paddingTop, paddingRight, paddingBottom));
    }

    public ShapeDrawable setPadding(Rect padding) {
        mPadding = padding;
        mPathDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set Shape type
     *
     * @param shape Shape type
     */
    public ShapeDrawable setType(@ShapeTypeLimit int shape) {
        mRingPath = null;
        mShapeState.setType(shape);
        mPathDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set Shape width
     */
    public ShapeDrawable setWidth(int width) {
        mShapeState.width = width;
        mPathDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set Shape height
     */
    public ShapeDrawable setHeight(int height) {
        mShapeState.height = height;
        mPathDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Sets the corner radius of the rectangle
     */
    public ShapeDrawable setRadius(float radius) {
        mShapeState.setCornerRadius(radius);
        mPathDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the corner radius of the rectangle
     *
     * @param topLeftRadius     Top-left corner radius
     * @param topRightRadius    Top-right corner radius
     * @param bottomLeftRadius  Bottom-left corner radius
     * @param bottomRightRadius Bottom-right corner radius
     */
    public ShapeDrawable setRadius(float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
        if (topLeftRadius == topRightRadius && topLeftRadius == bottomLeftRadius && topLeftRadius == bottomRightRadius) {
            return setRadius(topLeftRadius);
        }
        // Specify the radius for each of the 4 corners. For each corner, the array contains 2 values [X_radius, Y_radius]. The order of corners is top-left, top-right, bottom-right, bottom-left.
        mShapeState.setCornerRadii(new float[]{
                topLeftRadius, topLeftRadius, topRightRadius, topRightRadius,
                bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});
        mPathDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set fill color
     */

    public ShapeDrawable setSolidColor(@ColorInt int startColor, @ColorInt int endColor) {
        return setSolidColor(new int[]{startColor, endColor});
    }

    public ShapeDrawable setSolidColor(@ColorInt int startColor, @ColorInt int centerColor, @ColorInt int endColor) {
        return setSolidColor(new int[]{startColor, centerColor, endColor});
    }

    public ShapeDrawable setSolidColor(@ColorInt int... colors) {
        mShapeState.setSolidColor(colors);
        if (colors == null) {
            mSolidPaint.setColor(0);
        } else if (colors.length == 1) {
            mSolidPaint.setColor(colors[0]);
            mSolidPaint.clearShadowLayer();
        }
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set fill color gradient type
     */
    public ShapeDrawable setSolidGradientType(@ShapeGradientTypeLimit int type) {
        mShapeState.setSolidGradientType(type);
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set fill color gradient orientation
     */
    public ShapeDrawable setSolidGradientOrientation(ShapeGradientOrientation orientation) {
        mShapeState.solidGradientOrientation = orientation;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the relative position of the fill color gradient center X coordinate (default is 0.5)
     */
    public ShapeDrawable setSolidGradientCenterX(float centerX) {
        mShapeState.solidCenterX = centerX;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the relative position of the fill color gradient center Y coordinate (default is 0.5)
     */
    public ShapeDrawable setSolidGradientCenterY(float centerY) {
        mShapeState.solidCenterY = centerY;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set fill color gradient radius size
     */
    public ShapeDrawable setSolidGradientRadius(float radius) {
        mShapeState.gradientRadius = radius;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set rotation angle for radial gradient.
     * Creates a rotated elliptical effect.
     *
     * @param angleDegrees Rotation angle (0-360), positive = clockwise
     */
    public ShapeDrawable setSolidRadialAngle(float angleDegrees) {
        mShapeState.radialGradientAngle = angleDegrees;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    public float getSolidRadialAngle() {
        return mShapeState.radialGradientAngle;
    }

    /**
     * Set elliptical scale factors for radial gradient.
     * When scaleX != scaleY, creates an elliptical gradient.
     *
     * @param scaleX Horizontal scale factor (1.0 = no scale, 2.0 = 2x stretch)
     * @param scaleY Vertical scale factor (1.0 = no scale, 2.0 = 2x stretch)
     */
    public ShapeDrawable setSolidGradientRadii(float scaleX, float scaleY) {
        mShapeState.gradientRadiusX = scaleX;
        mShapeState.gradientRadiusY = scaleY;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the start position (focal point) for radial gradient.
     * Creates an off-center radial effect when different from center.
     *
     * @param startX X position relative to view (0.0 - 1.0)
     * @param startY Y position relative to view (0.0 - 1.0)
     */
    public ShapeDrawable setSolidRadialStartPosition(float startX, float startY) {
        mShapeState.radialStartX = startX;
        mShapeState.radialStartY = startY;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set custom color stop positions for gradient colors.
     * Positions define where each color appears along the gradient (0.0 to 1.0).
     * 
     * <p>For Figma compatibility, this allows specifying non-uniform color distributions:
     * <ul>
     *   <li>0.0 = Start of gradient</li>
     *   <li>1.0 = End of gradient</li>
     *   <li>Intermediate values place colors at that percentage along the gradient</li>
     * </ul>
     * 
     * <p>Example: For a 3-color gradient with start at 0%, middle at 30%, end at 100%:
     * {@code setSolidGradientPositions(0.0f, 0.3f, 1.0f)}
     *
     * @param positions Color stop positions (must match number of gradient colors)
     * @return This ShapeDrawable for chaining
     */
    public ShapeDrawable setSolidGradientPositions(float... positions) {
        mShapeState.positions = positions;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Get the current gradient color stop positions.
     * 
     * @return float array of positions, or null if using default (evenly distributed)
     */
    public float[] getSolidGradientPositions() {
        return mShapeState.positions;
    }

    /**
     * Set the start position for linear gradient as percentage of view dimensions.
     * 
     * <p>This allows precise control over where the gradient starts in the view:
     * <ul>
     *   <li>(0.0, 0.0) = Top-left corner</li>
     *   <li>(1.0, 1.0) = Bottom-right corner</li>
     *   <li>(0.5, 0.0) = Top center</li>
     * </ul>
     * 
     * <p>When set, this overrides the orientation-based gradient positioning.
     *
     * @param x X position (0.0 to 1.0, left to right)
     * @param y Y position (0.0 to 1.0, top to bottom)
     * @return This ShapeDrawable for chaining
     */
    public ShapeDrawable setSolidGradientStartPosition(float x, float y) {
        mShapeState.linearGradientStartX = x;
        mShapeState.linearGradientStartY = y;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the end position for linear gradient as percentage of view dimensions.
     * 
     * <p>This allows precise control over where the gradient ends in the view:
     * <ul>
     *   <li>(0.0, 0.0) = Top-left corner</li>
     *   <li>(1.0, 1.0) = Bottom-right corner</li>
     *   <li>(0.5, 1.0) = Bottom center</li>
     * </ul>
     * 
     * <p>When set, this overrides the orientation-based gradient positioning.
     *
     * @param x X position (0.0 to 1.0, left to right)
     * @param y Y position (0.0 to 1.0, top to bottom)
     * @return This ShapeDrawable for chaining
     */
    public ShapeDrawable setSolidGradientEndPosition(float x, float y) {
        mShapeState.linearGradientEndX = x;
        mShapeState.linearGradientEndY = y;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Get the linear gradient start X position.
     * @return Start X position (0.0 to 1.0), or NaN if using orientation
     */
    public float getLinearGradientStartX() {
        return mShapeState.linearGradientStartX;
    }

    /**
     * Get the linear gradient start Y position.
     * @return Start Y position (0.0 to 1.0), or NaN if using orientation
     */
    public float getLinearGradientStartY() {
        return mShapeState.linearGradientStartY;
    }

    /**
     * Get the linear gradient end X position.
     * @return End X position (0.0 to 1.0), or NaN if using orientation
     */
    public float getLinearGradientEndX() {
        return mShapeState.linearGradientEndX;
    }

    /**
     * Get the linear gradient end Y position.
     * @return End Y position (0.0 to 1.0), or NaN if using orientation
     */
    public float getLinearGradientEndY() {
        return mShapeState.linearGradientEndY;
    }

    /**
     * Check if custom linear gradient positions are set.
     * @return true if at least one custom position is set
     */
    public boolean hasCustomLinearGradientPositions() {
        return !Float.isNaN(mShapeState.linearGradientStartX) ||
               !Float.isNaN(mShapeState.linearGradientStartY) ||
               !Float.isNaN(mShapeState.linearGradientEndX) ||
               !Float.isNaN(mShapeState.linearGradientEndY);
    }

    /**
     * Set stroke color
     */

    public ShapeDrawable setStrokeColor(@ColorInt int startColor, @ColorInt int endColor) {
        return setStrokeColor(new int[]{startColor, endColor});
    }

    public ShapeDrawable setStrokeColor(@ColorInt int startColor, @ColorInt int centerColor, @ColorInt int endColor) {
        return setStrokeColor(new int[]{startColor, centerColor, endColor});
    }

    public ShapeDrawable setStrokeColor(@ColorInt int... colors) {
        mShapeState.setStrokeColor(colors);
        if (colors == null) {
            mStrokePaint.setColor(0);
        } else if (colors.length == 1) {
            mStrokePaint.setColor(colors[0]);
            mStrokePaint.clearShadowLayer();
        }
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set stroke color gradient orientation
     */
    public ShapeDrawable setStrokeGradientOrientation(ShapeGradientOrientation orientation) {
        mShapeState.strokeGradientOrientation = orientation;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set stroke size
     */
    public ShapeDrawable setStrokeSize(int size) {
        mShapeState.setStrokeSize(size);
        mStrokePaint.setStrokeWidth(size);
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the width of each segment of the dashed stroke
     */
    public ShapeDrawable setStrokeDashSize(float dashSize) {
        mShapeState.strokeDashSize = dashSize;
        mStrokePaint.setPathEffect(dashSize > 0 ?
                new DashPathEffect(new float[]{dashSize, mShapeState.strokeDashGap}, 0) : null);
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the gap between each segment of the dashed stroke
     */
    public ShapeDrawable setStrokeDashGap(float dashGap) {
        mShapeState.strokeDashGap = dashGap;
        mStrokePaint.setPathEffect(mShapeState.strokeDashSize > 0 ?
                new DashPathEffect(new float[]{mShapeState.strokeDashSize, dashGap}, 0) : null);
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * <p>Sets whether or not this drawable will honor its <code>level</code>
     * property.</p>
     * <p><strong>Note</strong>: changing this property will affect all instances
     * of a drawable loaded from a resource. It is recommended to invoke
     * {@link #mutate()} before changing this property.</p>
     *
     * @param useLevel True if this drawable should honor its level, false otherwise
     * @see #mutate()
     * @see #setLevel(int)
     * @see #getLevel()
     */
    public ShapeDrawable setUseLevel(boolean useLevel) {
        mShapeState.useLevel = useLevel;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    public ShapeDrawable setOuterShadowColor(@ColorInt int color) {
        mShapeState.outerShadowColor = color;
        mPathDirty = true;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    public ShapeDrawable setOuterShadowSize(int size) {
        mShapeState.outerShadowSize = size;
        mPathDirty = true;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    public ShapeDrawable setOuterShadowOffsetX(int offsetX) {
        mShapeState.outerShadowOffsetX = offsetX;
        mPathDirty = true;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    public ShapeDrawable setOuterShadowOffsetY(int offsetY) {
        mShapeState.outerShadowOffsetY = offsetY;
        mPathDirty = true;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the inner radius size of the ring
     */
    public ShapeDrawable setRingInnerRadiusSize(int size) {
        mShapeState.ringInnerRadiusSize = size;
        mShapeState.ringInnerRadiusRatio = 0;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the inner radius ratio of the ring
     */
    public ShapeDrawable setRingInnerRadiusRatio(float ratio) {
        mShapeState.ringInnerRadiusRatio = ratio;
        mShapeState.ringInnerRadiusSize = -1;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the thickness of the ring
     */
    public ShapeDrawable setRingThicknessSize(int size) {
        mShapeState.ringThicknessSize = size;
        mShapeState.ringThicknessRatio = 0;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the thickness ratio of the ring
     */
    public ShapeDrawable setRingThicknessRatio(float ratio) {
        mShapeState.ringThicknessRatio = ratio;
        mShapeState.ringThicknessSize = -1;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set line gravity
     */
    public ShapeDrawable setLineGravity(int lineGravity) {
        mShapeState.lineGravity = lineGravity;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    // ===== Inner Shadow API =====

    /**
     * Set a single inner shadow effect.
     * This clears any existing inner shadows and adds the specified shadow.
     * <p>
     * Inner shadows create an inset/beveled appearance by drawing
     * blurred shadows inside the shape bounds.
     *
     * @param color      Shadow color (ARGB)
     * @param blurRadius Blur radius in pixels (0 = sharp edge)
     * @param offsetX    Horizontal offset (positive = right)
     * @param offsetY    Vertical offset (positive = down)
     * @return This ShapeDrawable for chaining
     */
    public ShapeDrawable setInnerShadow(@ColorInt int color, float blurRadius,
                                         float offsetX, float offsetY) {
        if (mShapeState.innerShadows == null) {
            mShapeState.innerShadows = new ArrayList<>();
        } else {
            mShapeState.innerShadows.clear();
        }
        mShapeState.innerShadows.add(new InnerShadow(color, blurRadius, offsetX, offsetY));
        invalidateSelf();
        return this;
    }

    /**
     * Add an inner shadow effect to the shadow stack.
     * Multiple inner shadows are rendered in order (first added = bottom layer).
     * <p>
     * Use this to create complex beveled effects like highlight and shadow pairs.
     *
     * @param shadow InnerShadow configuration
     * @return This ShapeDrawable for chaining
     */
    public ShapeDrawable addInnerShadow(InnerShadow shadow) {
        if (shadow == null) return this;
        if (mShapeState.innerShadows == null) {
            mShapeState.innerShadows = new ArrayList<>();
        }
        mShapeState.innerShadows.add(shadow.copy());
        invalidateSelf();
        return this;
    }

    /**
     * Clear all inner shadow effects.
     *
     * @return This ShapeDrawable for chaining
     */
    public ShapeDrawable clearInnerShadows() {
        if (mShapeState.innerShadows != null) {
            mShapeState.innerShadows.clear();
        }
        invalidateSelf();
        return this;
    }

    /**
     * Check if this drawable has any inner shadows configured.
     *
     * @return true if at least one inner shadow is configured
     */
    public boolean hasInnerShadows() {
        return mShapeState.innerShadows != null && !mShapeState.innerShadows.isEmpty();
    }

    /**
     * Apply the current Drawable object to the View background
     */
    public void intoBackground(View view) {
        boolean needsSoftwareLayer = mShapeState.strokeDashGap > 0
                || mShapeState.outerShadowSize > 0
                || hasInnerShadows();

        if (needsSoftwareLayer) {
            // Hardware acceleration needs to be disabled, otherwise dashed lines or shadows may not take effect on some phones
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        view.setBackground(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Layout direction
            int layoutDirection = view.getLayoutDirection();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setLayoutDirection(layoutDirection);
            }
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void draw(@NonNull Canvas canvas) {
        if (!ensureValidRect()) {
            // nothing to draw
            return;
        }

        // remember the alpha values, in case we temporarily overwrite them
        // when we modulate them with mAlpha
        final int prevFillAlpha = mSolidPaint.getAlpha();
        final int prevStrokeAlpha = mStrokePaint.getAlpha();
        // compute the modulate alpha values
        final int currFillAlpha = modulateAlpha(prevFillAlpha);
        final int currStrokeAlpha = modulateAlpha(prevStrokeAlpha);

        final boolean haveShadow = mShapeState.outerShadowSize > 0;
        final boolean haveStroke = currStrokeAlpha > 0 && mStrokePaint.getStrokeWidth() > 0;
        final boolean haveFill = currFillAlpha > 0;
        final ShapeState st = mShapeState;
        /*  we need a layer iff we're drawing both a fill and stroke, and the
            stroke is non-opaque, and our shape type actually supports
            fill+stroke. Otherwise we can just draw the stroke (if any) on top
            of the fill (if any) without worrying about blending artifacts.
         */
        final boolean useLayer = haveStroke && haveFill && st.shapeType != ShapeType.LINE &&
                currStrokeAlpha < 255 && (mAlpha < 255 || mColorFilter != null);

        /*  Drawing with a layer is slower than direct drawing, but it
            allows us to apply paint effects like alpha and color filter to
            the result of multiple separate draws. In our case, if the user
            asks for a non-opaque alpha value (via setAlpha), and we're
            stroking, then we need to apply the alpha AFTER we've drawn
            both the fill and the stroke.
        */

        if (useLayer) {
            if (mLayerPaint == null) {
                mLayerPaint = new Paint();
            }
            mLayerPaint.setDither(mDither);
            mLayerPaint.setAlpha(mAlpha);
            mLayerPaint.setColorFilter(mColorFilter);

            float rad = mStrokePaint.getStrokeWidth();
            ShapeDrawableUtils.saveCanvasLayer(canvas, mRect.left - rad, mRect.top - rad,
                    mRect.right + rad, mRect.bottom + rad, mLayerPaint);

            // don't perform the filter in our individual paints
            // since the layer will do it for us
            mSolidPaint.setColorFilter(null);
            mStrokePaint.setColorFilter(null);
        } else {
            /*  if we're not using a layer, apply the dither/filter to our
                individual paints
            */
            mSolidPaint.setAlpha(currFillAlpha);
            mSolidPaint.setDither(mDither);
            mSolidPaint.setColorFilter(mColorFilter);
            if (mColorFilter != null && !mShapeState.hasSolidColor) {
                mSolidPaint.setColor(mAlpha << 24);
            }
            if (haveStroke) {
                mStrokePaint.setAlpha(currStrokeAlpha);
                mStrokePaint.setDither(mDither);
                mStrokePaint.setColorFilter(mColorFilter);
            }
        }

        if (haveShadow) {
            if (mShadowPaint == null) {
                mShadowPaint = new Paint();
                mShadowPaint.setColor(Color.TRANSPARENT);
                mShadowPaint.setStyle(Paint.Style.STROKE);
            }

            if (haveStroke) {
                mShadowPaint.setStrokeWidth(mStrokePaint.getStrokeWidth());
            } else {
                mShadowPaint.setStrokeWidth(mShapeState.outerShadowSize / 4f);
            }

            int shadowColor = mShapeState.outerShadowColor;
            // If the shadow color is opaque, a little transparency needs to be set, otherwise it will not be displayed
            if (ShapeDrawableUtils.setColorAlphaComponent(mShapeState.outerShadowColor, 255) == mShapeState.outerShadowColor) {
                shadowColor = ShapeDrawableUtils.setColorAlphaComponent(mShapeState.outerShadowColor, 254);
            }

            mShadowPaint.setColor(shadowColor);

            float shadowRadius;
            // Explain why the shadow size is divided by a multiple: if not done, the shadow display will exceed the View boundary, resulting in the shadow being clipped
            if (Build.VERSION.SDK_INT >= 28) {
                shadowRadius = mShapeState.outerShadowSize / 2f;
            } else {
                shadowRadius = mShapeState.outerShadowSize / 3f;
            }
            if (mCachedOuterShadowFilter == null || mCachedOuterShadowRadius != shadowRadius) {
                mCachedOuterShadowFilter = new BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.NORMAL);
                mCachedOuterShadowRadius = shadowRadius;
            }
            mShadowPaint.setMaskFilter(mCachedOuterShadowFilter);

        } else {
            if (mShadowPaint != null) {
                mShadowPaint.clearShadowLayer();
            }
        }

        switch (st.shapeType) {
            case ShapeType.RECTANGLE:
                if (st.radiusArray != null) {
                    if (mPathDirty || mRectDirty) {
                        mPath.reset();
                        mPath.addRoundRect(mRect, st.radiusArray, Path.Direction.CW);
                        mPathDirty = mRectDirty = false;
                    }
                    if (haveShadow) {
                        mShadowPath.reset();
                        mShadowPath.addRoundRect(mShadowRect, st.radiusArray, Path.Direction.CW);
                        canvas.drawPath(mShadowPath, mShadowPaint);
                    }
                    canvas.drawPath(mPath, mSolidPaint);
                    if (haveStroke) {
                        canvas.drawPath(mPath, mStrokePaint);
                    }
                } else if (st.radius > 0.0f) {
                    // since the caller is only giving us 1 value, we will force
                    // it to be square if the rect is too small in one dimension
                    // to show it. If we did nothing, Skia would clamp the rad
                    // independently along each axis, giving us a thin ellipse
                    // if the rect were very wide but not very tall
                    float rad = st.radius;
                    float r = Math.min(mRect.width(), mRect.height()) * 0.5f;
                    if (rad > r) {
                        rad = r;
                    }
                    if (haveShadow) {
                        canvas.drawRoundRect(mShadowRect, rad, rad, mShadowPaint);
                    }
                    canvas.drawRoundRect(mRect, rad, rad, mSolidPaint);
                    if (haveStroke) {
                        canvas.drawRoundRect(mRect, rad, rad, mStrokePaint);
                    }
                } else {
                    if (haveShadow) {
                        canvas.drawRect(mShadowRect, mShadowPaint);
                    }
                    if (mSolidPaint.getColor() != 0 || mColorFilter != null ||
                            mSolidPaint.getShader() != null) {
                        canvas.drawRect(mRect, mSolidPaint);
                    }
                    if (haveStroke) {
                        canvas.drawRect(mRect, mStrokePaint);
                    }
                }
                break;
            case ShapeType.OVAL:
                if (haveShadow) {
                    canvas.drawOval(mShadowRect, mShadowPaint);
                }
                canvas.drawOval(mRect, mSolidPaint);
                if (haveStroke) {
                    canvas.drawOval(mRect, mStrokePaint);
                }
                break;
            case ShapeType.LINE: {
                RectF r = mRect;
                float startX;
                float startY;
                float stopX;
                float stopY;
                int lineGravity;
                Callback callback = getCallback();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && callback instanceof View) {
                    int layoutDirection = ((View) callback).getContext().getResources().getConfiguration().getLayoutDirection();
                    lineGravity = Gravity.getAbsoluteGravity(st.lineGravity, layoutDirection);
                } else {
                    lineGravity = st.lineGravity;
                }

                switch (lineGravity) {
                    case Gravity.LEFT:
                        startX = 0;
                        startY = 0;
                        stopX = 0;
                        stopY = r.bottom;
                        break;
                    case Gravity.RIGHT:
                        startX = r.right;
                        startY = 0;
                        stopX = r.right;
                        stopY = r.bottom;
                        break;
                    case Gravity.TOP:
                        startX = 0;
                        startY = 0;
                        stopX = r.right;
                        stopY = 0;
                        break;
                    case Gravity.BOTTOM:
                        startX = 0;
                        startY = r.bottom;
                        stopX = r.right;
                        stopY = r.bottom;
                        break;
                    case Gravity.CENTER:
                    default:
                        float y = r.centerY();
                        startX = r.left;
                        startY = y;
                        stopX = r.right;
                        stopY = y;
                        break;
                }

                if (haveShadow) {
                    canvas.drawLine(startX, startY, stopX, stopY, mShadowPaint);
                }
                canvas.drawLine(startX, startY, stopX, stopY, mStrokePaint);
                break;
            }
            case ShapeType.RING:
                Path path = buildRing(st);
                if (haveShadow) {
                    canvas.drawPath(path, mShadowPaint);
                }
                canvas.drawPath(path, mSolidPaint);
                if (haveStroke) {
                    canvas.drawPath(path, mStrokePaint);
                }
                break;
            default:
                break;
        }

        // Draw inner shadows after fill/stroke but before final restore
        if (hasInnerShadows()) {
            drawInnerShadows(canvas, st);
        }

        if (useLayer) {
            canvas.restore();
        } else {
            mSolidPaint.setAlpha(prevFillAlpha);
            if (haveStroke) {
                mStrokePaint.setAlpha(prevStrokeAlpha);
            }
        }
    }

    /**
     * Draw inner shadows within the shape bounds.
     * Uses clip path to restrict drawing to shape interior, then draws
     * blurred shapes at the edges to create the inset shadow effect.
     *
     * @param canvas Canvas to draw on
     * @param st     Current shape state
     */
    private void drawInnerShadows(Canvas canvas, ShapeState st) {
        if (st.innerShadows == null || st.innerShadows.isEmpty()) {
            return;
        }

        // Initialize inner shadow paint if needed
        if (mInnerShadowPaint == null) {
            mInnerShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mInnerShadowPaint.setStyle(Paint.Style.STROKE);
        }

        canvas.save();

        // Clip to shape bounds - shadows only visible inside
        switch (st.shapeType) {
            case ShapeType.RECTANGLE:
                if (st.radiusArray != null) {
                    // Use the existing path for rounded corners
                    if (mPathDirty) {
                        mPath.reset();
                        mPath.addRoundRect(mRect, st.radiusArray, Path.Direction.CW);
                    }
                    canvas.clipPath(mPath);
                } else if (st.radius > 0) {
                    float rad = Math.min(st.radius,
                            Math.min(mRect.width(), mRect.height()) * 0.5f);
                    mInnerShadowClipPath.reset();
                    mInnerShadowClipPath.addRoundRect(mRect, rad, rad, Path.Direction.CW);
                    canvas.clipPath(mInnerShadowClipPath);
                } else {
                    canvas.clipRect(mRect);
                }
                break;
            case ShapeType.OVAL:
                mInnerShadowClipPath.reset();
                mInnerShadowClipPath.addOval(mRect, Path.Direction.CW);
                canvas.clipPath(mInnerShadowClipPath);
                break;
            default:
                // Inner shadows not supported for LINE or RING shapes
                canvas.restore();
                return;
        }

        // Draw each inner shadow from bottom to top
        for (InnerShadow shadow : st.innerShadows) {
            // Skip invisible shadows
            if (shadow.blurRadius <= 0 && shadow.spread <= 0) {
                continue;
            }

            // Skip fully transparent shadows
            if ((shadow.color >>> 24) == 0) {
                continue;
            }

            mInnerShadowPaint.setColor(shadow.color);

            // Calculate stroke width based on blur radius
            // Larger stroke width extends the shadow further into the shape
            float strokeWidth = Math.max(shadow.blurRadius * 2f, 4f) + shadow.spread;
            mInnerShadowPaint.setStrokeWidth(strokeWidth);

            // Apply blur filter (cached per InnerShadow)
            mInnerShadowPaint.setMaskFilter(shadow.getOrCreateMaskFilter());

            canvas.save();

            // Apply offset
            canvas.translate(shadow.offsetX, shadow.offsetY);

            // Draw the shape outline with thick stroke
            // The clip path will cut off the outer portion, leaving only
            // the inner part visible - creating the inner shadow effect
            switch (st.shapeType) {
                case ShapeType.RECTANGLE:
                    if (st.radiusArray != null) {
                        canvas.drawPath(mPath, mInnerShadowPaint);
                    } else if (st.radius > 0) {
                        float rad = Math.min(st.radius,
                                Math.min(mRect.width(), mRect.height()) * 0.5f);
                        canvas.drawRoundRect(mRect, rad, rad, mInnerShadowPaint);
                    } else {
                        canvas.drawRect(mRect, mInnerShadowPaint);
                    }
                    break;
                case ShapeType.OVAL:
                    canvas.drawOval(mRect, mInnerShadowPaint);
                    break;
            }

            canvas.restore();
        }

        canvas.restore();
    }

    @Override
    public boolean onLayoutDirectionChanged(int layoutDirection) {
        mLayoutDirection = layoutDirection;
        return mShapeState.shapeType == ShapeType.LINE;
    }

    private int modulateAlpha(int alpha) {
        int scale = mAlpha + (mAlpha >> 7);
        return alpha * scale >> 8;
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | mShapeState.changingConfigurations;
    }

    @Override
    public void setAlpha(int alpha) {
        if (alpha != mAlpha) {
            mAlpha = alpha;
            invalidateSelf();
        }
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setDither(boolean dither) {
        if (dither != mDither) {
            mDither = dither;
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf != mColorFilter) {
            mColorFilter = cf;
            invalidateSelf();
        }
    }

    @Override
    public int getOpacity() {
        return mShapeState.opaque ? PixelFormat.OPAQUE : PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(Rect r) {
        super.onBoundsChange(r);
        mRingPath = null;
        mPathDirty = true;
        mRectDirty = true;
    }

    @Override
    protected boolean onLevelChange(int level) {
        super.onLevelChange(level);
        mRectDirty = true;
        mPathDirty = true;
        invalidateSelf();
        return true;
    }

    private Path buildRing(ShapeState shapeState) {
        if (mRingPath != null && (!shapeState.useLevelForShape || !mPathDirty)) {
            return mRingPath;
        }
        mPathDirty = false;

        float sweep = shapeState.useLevelForShape ? (360.0f * getLevel() / 10000f) : 360f;

        RectF bounds = new RectF(mRect);

        float x = bounds.width() / 2.0f;
        float y = bounds.height() / 2.0f;

        float thickness = shapeState.ringThicknessSize != -1 ?
                shapeState.ringThicknessSize : bounds.width() / shapeState.ringThicknessRatio;
        // inner radius
        float radius = shapeState.ringInnerRadiusSize != -1 ?
                shapeState.ringInnerRadiusSize : bounds.width() / shapeState.ringInnerRadiusRatio;

        RectF innerBounds = new RectF(bounds);
        innerBounds.inset(x - radius, y - radius);

        bounds = new RectF(innerBounds);
        bounds.inset(-thickness, -thickness);

        if (mRingPath == null) {
            mRingPath = new Path();
        } else {
            mRingPath.reset();
        }

        final Path ringPath = mRingPath;
        // arcTo treats the sweep angle mod 360, so check for that, since we
        // think 360 means draw the entire oval
        if (sweep < 360 && sweep > -360) {
            ringPath.setFillType(Path.FillType.EVEN_ODD);
            // inner top
            ringPath.moveTo(x + radius, y);
            // outer top
            ringPath.lineTo(x + radius + thickness, y);
            // outer arc
            ringPath.arcTo(bounds, 0.0f, sweep, false);
            // inner arc
            ringPath.arcTo(innerBounds, sweep, -sweep, false);
            ringPath.close();
        } else {
            // add the entire ovals
            ringPath.addOval(bounds, Path.Direction.CW);
            ringPath.addOval(innerBounds, Path.Direction.CCW);
        }

        return ringPath;
    }

    /**
     * This checks mRectIsDirty, and if it is true, recomputes both our drawing
     * rectangle (mRect) and the gradient itself, since it depends on our
     * rectangle too.
     *
     * @return true if the resulting rectangle is not empty, false otherwise
     */
    private boolean ensureValidRect() {
        if (!mRectDirty) {
            return !mRect.isEmpty();
        }

        mRectDirty = false;

        Rect bounds = getBounds();
        float inset = mStrokePaint.getStrokeWidth() * 0.5f;

        final ShapeState st = mShapeState;

        float let = bounds.left + inset + mShapeState.outerShadowSize;
        float top = bounds.top + inset + mShapeState.outerShadowSize;
        float right = bounds.right - inset - mShapeState.outerShadowSize;
        float bottom = bounds.bottom - inset - mShapeState.outerShadowSize;

        mRect.set(let, top, right, bottom);

        float shadowLet;
        float shadowTop;
        float shadowRight;
        float shadowBottom;

        if (mShapeState.outerShadowOffsetX > 0) {
            shadowLet = let + mShapeState.outerShadowOffsetX;
            shadowRight = right;
        } else {
            shadowLet = let;
            shadowRight = right + mShapeState.outerShadowOffsetX;
        }

        if (mShapeState.outerShadowOffsetY > 0) {
            shadowTop = top + mShapeState.outerShadowOffsetY;
            shadowBottom = bottom;
        } else {
            shadowTop = top;
            shadowBottom = bottom + mShapeState.outerShadowOffsetY;
        }

        mShadowRect.set(shadowLet, shadowTop, shadowRight, shadowBottom);

        if (st.solidColors == null) {
            mSolidPaint.setShader(null);
        }

        if (st.strokeColors == null) {
            mStrokePaint.setShader(null);
        }

        if (st.solidColors != null) {
            RectF rect = mRect;

            switch (st.solidGradientType) {
                case ShapeGradientType.LINEAR_GRADIENT: {
                    final float level = st.useLevel ? getLevel() / 10000f : 1f;
                    float[] coordinate;
                    
                    // Check for custom linear gradient positions
                    boolean hasCustomPositions = !Float.isNaN(st.linearGradientStartX) ||
                                                  !Float.isNaN(st.linearGradientStartY) ||
                                                  !Float.isNaN(st.linearGradientEndX) ||
                                                  !Float.isNaN(st.linearGradientEndY);
                    
                    if (hasCustomPositions) {
                        // Use custom positions (with fallback to defaults)
                        float startX = Float.isNaN(st.linearGradientStartX) ? 0.5f : st.linearGradientStartX;
                        float startY = Float.isNaN(st.linearGradientStartY) ? 0f : st.linearGradientStartY;
                        float endX = Float.isNaN(st.linearGradientEndX) ? 0.5f : st.linearGradientEndX;
                        float endY = Float.isNaN(st.linearGradientEndY) ? 1f : st.linearGradientEndY;
                        
                        // Convert from percentage (0-1) to actual coordinates
                        coordinate = new float[] {
                            rect.left + rect.width() * startX,
                            rect.top + rect.height() * startY,
                            rect.left + rect.width() * (startX + (endX - startX) * level),
                            rect.top + rect.height() * (startY + (endY - startY) * level)
                        };
                    } else {
                        // Use orientation-based coordinates
                        coordinate = ShapeDrawableUtils.computeLinearGradientCoordinate(
                            mLayoutDirection, mRect, level, st.solidGradientOrientation);
                    }
                    
                    mSolidPaint.setShader(new LinearGradient(coordinate[0], coordinate[1], coordinate[2], coordinate[3],
                            st.solidColors, st.positions, Shader.TileMode.CLAMP));
                    break;
                }
                case ShapeGradientType.RADIAL_GRADIENT: {
                    float centerX, centerY, radius;
                    float angle = 0;
                    float scaleX = 1f, scaleY = 1f;

                    final float level = st.useLevel ? getLevel() / 10000f : 1f;

                    // Check if we have Start/End positions (Figma style)
                    // These attributes (linearGradientStartX etc.) are generic position attributes now
                    boolean hasCustomPositions = !Float.isNaN(st.linearGradientStartX) &&
                                                  !Float.isNaN(st.linearGradientStartY) &&
                                                  !Float.isNaN(st.linearGradientEndX) &&
                                                  !Float.isNaN(st.linearGradientEndY);

                    if (hasCustomPositions) {
                        // Figma Style: Gradient defined by a vector from Start(Center) to End(Radius edge)
                        
                        // 1. Calculate Center (Start Point)
                        centerX = rect.left + rect.width() * st.linearGradientStartX;
                        centerY = rect.top + rect.height() * st.linearGradientStartY;

                        // 2. Calculate End Point
                        float endX = rect.left + rect.width() * st.linearGradientEndX;
                        float endY = rect.top + rect.height() * st.linearGradientEndY;

                        // 3. Calculate Radius (Distance between Start and End)
                        // This defines the radius of the gradient circle/ellipse at 100%
                        double dx = endX - centerX;
                        double dy = endY - centerY;
                        radius = (float) Math.sqrt(dx * dx + dy * dy);

                        // 4. Calculate Angle (Rotation)
                        // Warning: Math.atan2 returns radians. We need degrees.
                        // Standard radial gradient 0 degrees is usually 3 o'clock.
                        // We calculate the angle of the vector (dx, dy).
                        angle = (float) Math.toDegrees(Math.atan2(dy, dx));
                        
                        // Since standard RadialGradient draws concentric circles, 'angle' usually rotates the *context* (matrix).
                        
                        // Apply level if needed (though less common for this style)
                        radius *= level;

                        // Scale X/Y from attributes if present (elliptical)
                        if (st.gradientRadiusX > 0 && st.gradientRadiusY > 0) {
                            scaleX = st.gradientRadiusX;
                            scaleY = st.gradientRadiusY;
                        }

                    } else {
                        // Standard Style: Center + Radius + Angle attributes
                        
                        // Center position (relative 0-1)
                        centerX = rect.left + rect.width() * st.solidCenterX;
                        centerY = rect.top + rect.height() * st.solidCenterY;

                        // Calculate base radius from view dimensions
                        // gradientRadius is a multiplier (1.0 = half of min dimension)
                        float baseRadius = Math.min(rect.width(), rect.height()) / 2f;
                        radius = level * baseRadius;
                        if (st.gradientRadius > 0 && st.gradientRadius != 0.5f) {
                            // gradientRadius is now a multiplier of base radius
                            radius = level * baseRadius * (st.gradientRadius * 2f);
                        }
                        
                        // Get explicit angle and scales
                        angle = st.radialGradientAngle;
                        if (st.gradientRadiusX > 0 && st.gradientRadiusY > 0) {
                            scaleX = st.gradientRadiusX;
                            scaleY = st.gradientRadiusY;
                        }
                    }

                    // Create or update cached radial gradient
                    // Note: We use the calculated radius directly.
                    // If radius is 0, Shader might crash or do nothing, so clamp to small value.
                    if (radius <= 0) radius = 1f;

                    mCachedRadialGradient = new RadialGradient(
                            centerX, centerY, radius,
                            st.solidColors, st.positions, Shader.TileMode.CLAMP);

                    // Apply Matrix transformations (Scaling, Rotation, Focal Offset)
                    boolean hasTransform = (scaleX != 1f || scaleY != 1f) || (angle != 0f) ||
                                           (hasCustomPositions ? false : (!Float.isNaN(st.radialStartX) || !Float.isNaN(st.radialStartY)));

                    if (hasTransform) {
                        if (mCachedGradientMatrix == null) {
                            mCachedGradientMatrix = new Matrix();
                        } else {
                            mCachedGradientMatrix.reset();
                        }

                        // Apply elliptical scaling
                        if (scaleX != 1f || scaleY != 1f) {
                            mCachedGradientMatrix.postScale(scaleX, scaleY, centerX, centerY);
                        }

                        // Apply rotation
                        if (angle != 0f) {
                            mCachedGradientMatrix.postRotate(angle, centerX, centerY);
                        }

                        // Apply focal point offset (Only for Standard Style, generic start/end handles this via center calculation)
                        if (!hasCustomPositions && (!Float.isNaN(st.radialStartX) || !Float.isNaN(st.radialStartY))) {
                            float focalX = Float.isNaN(st.radialStartX) ? centerX
                                    : rect.left + rect.width() * st.radialStartX;
                            float focalY = Float.isNaN(st.radialStartY) ? centerY
                                    : rect.top + rect.height() * st.radialStartY;
                            mCachedGradientMatrix.setTranslate(focalX - centerX, focalY - centerY);
                        }

                        mCachedRadialGradient.setLocalMatrix(mCachedGradientMatrix);
                    }

                    mSolidPaint.setShader(mCachedRadialGradient);
                    break;
                }
                case ShapeGradientType.SWEEP_GRADIENT: {
                    float x0;
                    float y0;

                    x0 = rect.left + (rect.right - rect.left) * st.solidCenterX;
                    y0 = rect.top + (rect.bottom - rect.top) * st.solidCenterY;

                    int[] tempSolidColors = st.solidColors;
                    float[] tempSolidPositions = null;

                    if (st.useLevel) {
                        tempSolidColors = st.tempSolidColors;
                        final int length = st.solidColors.length;
                        if (tempSolidColors == null || tempSolidColors.length != length + 1) {
                            tempSolidColors = st.tempSolidColors = new int[length + 1];
                        }
                        System.arraycopy(st.solidColors, 0, tempSolidColors, 0, length);
                        tempSolidColors[length] = st.solidColors[length - 1];


                        tempSolidPositions = st.tempSolidPositions;
                        final float fraction = 1f / (length - 1);
                        if (tempSolidPositions == null || tempSolidPositions.length != length + 1) {
                            tempSolidPositions = st.tempSolidPositions = new float[length + 1];
                        }

                        final float level = getLevel() / 10000f;
                        for (int i = 0; i < length; i++) {
                            tempSolidPositions[i] = i * fraction * level;
                        }
                        tempSolidPositions[length] = 1f;
                    }

                    mSolidPaint.setShader(new SweepGradient(x0, y0, tempSolidColors, tempSolidPositions));
                    break;
                }
                default:
                    break;
            }

            // If we don't have a solid color, the alpha channel must be
            // maxed out so that alpha modulation works correctly.
            if (!st.hasSolidColor) {
                mSolidPaint.setColor(Color.BLACK);
            }
        }

        if (st.strokeColors != null) {
            final float level = st.useLevel ? getLevel() / 10000f : 1f;
            float[] coordinate = ShapeDrawableUtils.computeLinearGradientCoordinate(
                    mLayoutDirection, mRect, level, st.strokeGradientOrientation);
            mStrokePaint.setShader(new LinearGradient(coordinate[0], coordinate[1], coordinate[2], coordinate[3],
                    st.strokeColors, st.positions, Shader.TileMode.CLAMP));

            if (!st.hasStrokeColor) {
                mStrokePaint.setColor(Color.BLACK);
            }
        }
        return !mRect.isEmpty();
    }

    @Override
    public int getIntrinsicWidth() {
        return mShapeState.width;
    }

    @Override
    public int getIntrinsicHeight() {
        return mShapeState.height;
    }

    @Override
    public ConstantState getConstantState() {
        mShapeState.changingConfigurations = getChangingConfigurations();
        return mShapeState;
    }

    @NonNull
    @Override
    public Drawable mutate() {
        if (!mMutated && super.mutate() == this) {
            mShapeState = new ShapeState(mShapeState);
            initializeWithState(mShapeState);
            mMutated = true;
        }
        return this;
    }

    private void initializeWithState(ShapeState state) {
        if (state.hasSolidColor) {
            mSolidPaint.setColor(state.solidColor);
        } else if (state.solidColors == null) {
            // If we don't have a solid color and we don't have a gradient,
            // the app is stroking the shape, set the color to the default
            // value of state.solidColor
            mSolidPaint.setColor(0);
        } else if (state.solidColorStateList != null) {
            setSolidColor(state.solidColorStateList.getColorForState(getState(), 0));
        } else {
            // Otherwise, make sure the fill alpha is maxed out.
            mSolidPaint.setColor(Color.BLACK);
        }
        mPadding = state.padding;
        if (state.strokeSize >= 0) {
            if (state.hasStrokeColor) {
                setStrokeColor(state.strokeColor);
            } else if (state.strokeColorStateList != null) {
                setStrokeColor(state.strokeColorStateList.getColorForState(getState(), 0));
            } else {
                setStrokeColor(state.strokeColors);
            }
            setStrokeSize(state.strokeSize);
            setStrokeDashSize(state.strokeDashSize);
            setStrokeDashGap(state.strokeDashGap);
        }

        // Initialize outer shadow color from state
        if (state.outerShadowColorStateList != null) {
            setOuterShadowColor(state.outerShadowColorStateList.getColorForState(getState(), state.outerShadowColor));
        } else {
            setOuterShadowColor(state.outerShadowColor);
        }

        // Initialize inner shadow colors from state
        if (state.innerShadows != null) {
            for (InnerShadow shadow : state.innerShadows) {
                if (shadow.colorStateList != null) {
                    shadow.color = shadow.colorStateList.getColorForState(getState(), shadow.color);
                }
            }
        }

        // Initialize gradient colors from state if present
        if (state.solidGradientStartColorStateList != null) {
            setSolidGradientStartColor(state.solidGradientStartColorStateList);
        }
        if (state.solidGradientCenterColorStateList != null) {
            setSolidGradientCenterColor(state.solidGradientCenterColorStateList);
        }
        if (state.solidGradientEndColorStateList != null) {
            setSolidGradientEndColor(state.solidGradientEndColorStateList);
        }

        if (state.strokeGradientStartColorStateList != null) {
            setStrokeGradientStartColor(state.strokeGradientStartColorStateList);
        }
        if (state.strokeGradientCenterColorStateList != null) {
            setStrokeGradientCenterColor(state.strokeGradientCenterColorStateList);
        }
        if (state.strokeGradientEndColorStateList != null) {
            setStrokeGradientEndColor(state.strokeGradientEndColorStateList);
        }
    }
    public void setSolidColor(ColorStateList colorStateList) {
        mShapeState.solidColorStateList = colorStateList;
        if (colorStateList != null) {
            setSolidColor(colorStateList.getColorForState(getState(), 0));
        } else {
            setSolidColor(0);
        }
    }

    public void setStrokeColor(ColorStateList colorStateList) {
        mShapeState.strokeColorStateList = colorStateList;
        if (colorStateList != null) {
            setStrokeColor(colorStateList.getColorForState(getState(), 0));
        } else {
            setStrokeColor(0);
        }
    }

    @Override
    public boolean isStateful() {
        if (super.isStateful()) {
            return true;
        }
        if (mShapeState.solidColorStateList != null && mShapeState.solidColorStateList.isStateful()) {
            return true;
        }
        if (mShapeState.strokeColorStateList != null && mShapeState.strokeColorStateList.isStateful()) {
            return true;
        }
        if (mShapeState.outerShadowColorStateList != null && mShapeState.outerShadowColorStateList.isStateful()) {
            return true;
        }
        if (mShapeState.solidGradientStartColorStateList != null && mShapeState.solidGradientStartColorStateList.isStateful()) {
            return true;
        }
        if (mShapeState.solidGradientCenterColorStateList != null && mShapeState.solidGradientCenterColorStateList.isStateful()) {
            return true;
        }
        if (mShapeState.solidGradientEndColorStateList != null && mShapeState.solidGradientEndColorStateList.isStateful()) {
            return true;
        }
        if (mShapeState.strokeGradientStartColorStateList != null && mShapeState.strokeGradientStartColorStateList.isStateful()) {
            return true;
        }
        if (mShapeState.strokeGradientCenterColorStateList != null && mShapeState.strokeGradientCenterColorStateList.isStateful()) {
            return true;
        }
        if (mShapeState.strokeGradientEndColorStateList != null && mShapeState.strokeGradientEndColorStateList.isStateful()) {
            return true;
        }
        if (mShapeState.innerShadows != null) {
            for (InnerShadow shadow : mShapeState.innerShadows) {
                if (shadow.colorStateList != null && shadow.colorStateList.isStateful()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        boolean changed = super.onStateChange(state);
        
        if (mShapeState.solidColorStateList != null) {
            int newColor = mShapeState.solidColorStateList.getColorForState(state, mShapeState.solidColor);
            if (newColor != mShapeState.solidColor) {
                setSolidColor(newColor);
                changed = true;
            }
        }
        
        if (mShapeState.strokeColorStateList != null) {
            int newColor = mShapeState.strokeColorStateList.getColorForState(state, mShapeState.strokeColor);
            if (newColor != mShapeState.strokeColor) {
                setStrokeColor(newColor);
                changed = true;
            }
        }

        if (mShapeState.outerShadowColorStateList != null) {
            int newColor = mShapeState.outerShadowColorStateList.getColorForState(state, mShapeState.outerShadowColor);
            if (newColor != mShapeState.outerShadowColor) {
                setOuterShadowColor(newColor);
                changed = true;
            }
        }

        boolean gradientChanged = false;
        if (mShapeState.solidGradientStartColorStateList != null || mShapeState.solidGradientCenterColorStateList != null || mShapeState.solidGradientEndColorStateList != null) {
            // Re-resolve gradient colors
            int startColor = mShapeState.solidGradientStartColorStateList != null ? mShapeState.solidGradientStartColorStateList.getColorForState(state, 0) : 0;
            int centerColor = mShapeState.solidGradientCenterColorStateList != null ? mShapeState.solidGradientCenterColorStateList.getColorForState(state, 0) : 0;
            int endColor = mShapeState.solidGradientEndColorStateList != null ? mShapeState.solidGradientEndColorStateList.getColorForState(state, 0) : 0;
            
             // This is a simplified check/updater. Ideally we should check if values changed.
            // But since setSolidGradientColors handles reconstruction, we just need to ensure correct colors are passed.
            // We need to know if center color is used.
             if (mShapeState.solidGradientCenterColorStateList != null) {
                 setSolidColor(startColor, centerColor, endColor);
             } else {
                 setSolidColor(startColor, endColor);
             }
             gradientChanged = true;
        }
        
        if (mShapeState.strokeGradientStartColorStateList != null || mShapeState.strokeGradientCenterColorStateList != null || mShapeState.strokeGradientEndColorStateList != null) {
             int startColor = mShapeState.strokeGradientStartColorStateList != null ? mShapeState.strokeGradientStartColorStateList.getColorForState(state, 0) : 0;
             int centerColor = mShapeState.strokeGradientCenterColorStateList != null ? mShapeState.strokeGradientCenterColorStateList.getColorForState(state, 0) : 0;
             int endColor = mShapeState.strokeGradientEndColorStateList != null ? mShapeState.strokeGradientEndColorStateList.getColorForState(state, 0) : 0;
             
             if (mShapeState.strokeGradientCenterColorStateList != null) {
                 setStrokeColor(startColor, centerColor, endColor);
             } else {
                 setStrokeColor(startColor, endColor);
             }
             gradientChanged = true;
        }

        if (gradientChanged) {
             changed = true;
        }

        if (mShapeState.innerShadows != null) {
            for (InnerShadow shadow : mShapeState.innerShadows) {
                if (shadow.colorStateList != null) {
                    int newColor = shadow.colorStateList.getColorForState(state, shadow.color);
                    if (newColor != shadow.color) {
                        shadow.color = newColor;
                        changed = true;
                    }
                }
            }
        }

        return changed;
    }

    public ShapeDrawable setSolidGradientStartColor(ColorStateList colorStateList) {
        mShapeState.solidGradientStartColorStateList = colorStateList;
        if (colorStateList != null) {
            // Trigger update
            onStateChange(getState());
        }
        return this;
    }

    public ShapeDrawable setSolidGradientCenterColor(ColorStateList colorStateList) {
        mShapeState.solidGradientCenterColorStateList = colorStateList;
        if (colorStateList != null) {
            onStateChange(getState());
        }
        return this;
    }

    public ShapeDrawable setSolidGradientEndColor(ColorStateList colorStateList) {
        mShapeState.solidGradientEndColorStateList = colorStateList;
        if (colorStateList != null) {
            onStateChange(getState());
        }
        return this;
    }

    public ShapeDrawable setStrokeGradientStartColor(ColorStateList colorStateList) {
        mShapeState.strokeGradientStartColorStateList = colorStateList;
        if (colorStateList != null) {
            onStateChange(getState());
        }
        return this;
    }

    public ShapeDrawable setStrokeGradientCenterColor(ColorStateList colorStateList) {
        mShapeState.strokeGradientCenterColorStateList = colorStateList;
        if (colorStateList != null) {
            onStateChange(getState());
        }
        return this;
    }

    public ShapeDrawable setStrokeGradientEndColor(ColorStateList colorStateList) {
        mShapeState.strokeGradientEndColorStateList = colorStateList;
        if (colorStateList != null) {
            onStateChange(getState());
        }
        return this;
    }

    public ShapeDrawable setOuterShadowColor(ColorStateList colorStateList) {
        mShapeState.outerShadowColorStateList = colorStateList;
        if (colorStateList != null) {
            setOuterShadowColor(colorStateList.getColorForState(getState(), 0));
        } else {
            setOuterShadowColor(0);
        }
        return this;
    }
}