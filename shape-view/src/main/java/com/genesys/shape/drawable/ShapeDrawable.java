package com.genesys.shape.drawable;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
import java.util.List;

/**
 * Reconstructed based on {@link android.graphics.drawable.GradientDrawable}
 */
public class ShapeDrawable extends Drawable {

    private ShapeState mShapeState;

    private final Paint mSolidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect mPadding;
    private final Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);   // optional, set by the caller
    private Paint mShadowPaint;
    private Paint mInnerShadowPaint;  // Paint used for drawing inner shadows
    private Paint mInnerShadowMaskPaint;  // Trims the inner shadows back to the shape
    private ColorFilter mColorFilter;   // optional, set by the caller
    private int mAlpha = 0xFF;  // modified by the caller
    private boolean mDither;

    private final Path mPath = new Path();
    private final RectF mRect = new RectF();

    private final RectF mShadowRect = new RectF();
    private final Path mShadowPath = new Path();
    /** Room the shadow takes out of the bounds, per edge. Recomputed with {@link #mRect}. */
    private final Rect mShadowInsets = new Rect();

    /**
     * The blurred shadow, rasterized once per bounds/attribute change instead of every frame.
     * BlurMaskFilter has no hardware-accelerated path, so blurring straight onto the view's
     * canvas forces the whole view (and its children) into a software layer. Rasterizing here
     * and blitting the result keeps the view on the GPU.
     */
    private Bitmap mShadowBitmap;
    private final Rect mShadowBitmapSrc = new Rect();
    private final Rect mShadowBitmapDst = new Rect();
    private Paint mShadowBitmapPaint;
    private boolean mShadowDirty = true;
    /** Per-corner radii of the spread shadow, reused so drawing allocates nothing. */
    private float[] mShadowRadii;
    /** Scratch {startX, startY, stopX, stopY} for {@link ShapeType#LINE}. */
    private final float[] mLinePoints = new float[4];

    /**
     * The blurred inner shadows, rasterized together on the same terms as {@link #mShadowBitmap}
     * and for the same reason: BlurMaskFilter has no hardware-accelerated path, and painting one
     * straight onto the view's canvas used to drag the whole view into a software layer.
     */
    private Bitmap mInnerShadowBitmap;
    private final Rect mInnerShadowBitmapSrc = new Rect();
    private final Rect mInnerShadowBitmapDst = new Rect();
    private Paint mInnerShadowBitmapPaint;
    private boolean mInnerShadowDirty = true;
    /** Per-corner radii of an inner shadow's opening, reused so drawing allocates nothing. */
    private float[] mInnerShadowRadii;

    private Paint mLayerPaint;    // internal, used if we use saveLayer()
    private boolean mRectDirty;   // internal state
    private boolean mMutated;
    private Path mRingPath;
    private boolean mPathDirty = true;

    // Cached matrix reused when building gradients, to avoid allocation during draw
    private Matrix mCachedGradientMatrix;

    /** Scratch geometry for the inner shadows, reused across the rasterize pass. */
    private final Path mInnerShadowPath = new Path();
    private final RectF mInnerShadowRect = new RectF();

    /**
     * Ceiling on a shadow bitmap's pixel count. A large view (a bottom sheet, say) renders
     * its shadow downscaled and is blitted back up — a blur has no detail to lose, so the
     * only visible effect is the memory saved.
     */
    private static final int MAX_SHADOW_PIXELS = 512 * 512;

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
        invalidateShapeOutline();
        return this;
    }

    /**
     * The silhouette the shadows are traced from just changed, so both rasterized copies of
     * it are stale.
     */
    private void invalidateShapeOutline() {
        mShadowDirty = true;
        mInnerShadowDirty = true;
        invalidateSelf();
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
        invalidateShapeOutline();
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
        invalidateShapeOutline();
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
     * Set the fill gradient radius as a ratio of half the shortest side
     * (0.5 = half the shortest side, 1.0 = the whole shortest side)
     */
    public ShapeDrawable setSolidGradientRadiusRatio(float ratio) {
        mShapeState.gradientRadius = ratio;
        mShapeState.gradientRadiusInPx = false;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the fill gradient radius as an absolute size in pixels
     */
    public ShapeDrawable setSolidGradientRadiusSize(float size) {
        mShapeState.gradientRadius = size;
        mShapeState.gradientRadiusInPx = true;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * @deprecated use {@link #setSolidGradientRadiusRatio(float)} or
     *             {@link #setSolidGradientRadiusSize(float)}
     */
    @Deprecated
    public ShapeDrawable setSolidGradientRadius(float radius) {
        return setSolidGradientRadiusRatio(radius);
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
     * Set stroke color gradient type
     */
    public ShapeDrawable setStrokeGradientType(@ShapeGradientTypeLimit int type) {
        mShapeState.setStrokeGradientType(type);
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the relative position of the stroke color gradient center X coordinate (default is 0.5)
     */
    public ShapeDrawable setStrokeGradientCenterX(float centerX) {
        mShapeState.strokeCenterX = centerX;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the relative position of the stroke color gradient center Y coordinate (default is 0.5)
     */
    public ShapeDrawable setStrokeGradientCenterY(float centerY) {
        mShapeState.strokeCenterY = centerY;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the stroke gradient radius as a ratio of half the shortest side
     * (0.5 = half the shortest side, 1.0 = the whole shortest side)
     */
    public ShapeDrawable setStrokeGradientRadiusRatio(float ratio) {
        mShapeState.strokeGradientRadius = ratio;
        mShapeState.strokeGradientRadiusInPx = false;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the stroke gradient radius as an absolute size in pixels
     */
    public ShapeDrawable setStrokeGradientRadiusSize(float size) {
        mShapeState.strokeGradientRadius = size;
        mShapeState.strokeGradientRadiusInPx = true;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set rotation angle for the stroke radial gradient.
     * Creates a rotated elliptical effect.
     *
     * @param angleDegrees Rotation angle (0-360), positive = clockwise
     */
    public ShapeDrawable setStrokeRadialAngle(float angleDegrees) {
        mShapeState.strokeRadialGradientAngle = angleDegrees;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    public float getStrokeRadialAngle() {
        return mShapeState.strokeRadialGradientAngle;
    }

    /**
     * Set elliptical scale factors for the stroke radial gradient.
     * When scaleX != scaleY, creates an elliptical gradient.
     *
     * @param scaleX Horizontal scale factor (1.0 = no scale, 2.0 = 2x stretch)
     * @param scaleY Vertical scale factor (1.0 = no scale, 2.0 = 2x stretch)
     */
    public ShapeDrawable setStrokeGradientRadii(float scaleX, float scaleY) {
        mShapeState.strokeGradientRadiusX = scaleX;
        mShapeState.strokeGradientRadiusY = scaleY;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the start position (focal point) for the stroke radial gradient.
     * Creates an off-center radial effect when different from center.
     *
     * @param startX X position relative to view (0.0 - 1.0)
     * @param startY Y position relative to view (0.0 - 1.0)
     */
    public ShapeDrawable setStrokeRadialStartPosition(float startX, float startY) {
        mShapeState.strokeRadialStartX = startX;
        mShapeState.strokeRadialStartY = startY;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set custom color stop positions for the stroke gradient colors.
     * Works exactly like {@link #setSolidGradientPositions(float...)} but for the stroke.
     *
     * @param positions Color stop positions (must match number of gradient colors)
     * @return This ShapeDrawable for chaining
     */
    public ShapeDrawable setStrokeGradientPositions(float... positions) {
        mShapeState.strokePositions = positions;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Get the current stroke gradient color stop positions.
     *
     * @return float array of positions, or null if using default (evenly distributed)
     */
    public float[] getStrokeGradientPositions() {
        return mShapeState.strokePositions;
    }

    /**
     * Set the start position for the stroke linear gradient as percentage of view dimensions.
     * When set, this overrides the orientation-based gradient positioning.
     *
     * @param x X position (0.0 to 1.0, left to right)
     * @param y Y position (0.0 to 1.0, top to bottom)
     */
    public ShapeDrawable setStrokeGradientStartPosition(float x, float y) {
        mShapeState.strokeLinearGradientStartX = x;
        mShapeState.strokeLinearGradientStartY = y;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Set the end position for the stroke linear gradient as percentage of view dimensions.
     * When set, this overrides the orientation-based gradient positioning.
     *
     * @param x X position (0.0 to 1.0, left to right)
     * @param y Y position (0.0 to 1.0, top to bottom)
     */
    public ShapeDrawable setStrokeGradientEndPosition(float x, float y) {
        mShapeState.strokeLinearGradientEndX = x;
        mShapeState.strokeLinearGradientEndY = y;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    public float getStrokeLinearGradientStartX() {
        return mShapeState.strokeLinearGradientStartX;
    }

    public float getStrokeLinearGradientStartY() {
        return mShapeState.strokeLinearGradientStartY;
    }

    public float getStrokeLinearGradientEndX() {
        return mShapeState.strokeLinearGradientEndX;
    }

    public float getStrokeLinearGradientEndY() {
        return mShapeState.strokeLinearGradientEndY;
    }

    /**
     * Check if custom stroke linear gradient positions are set.
     *
     * @return true if at least one custom position is set
     */
    public boolean hasCustomStrokeLinearGradientPositions() {
        return !Float.isNaN(mShapeState.strokeLinearGradientStartX) ||
               !Float.isNaN(mShapeState.strokeLinearGradientStartY) ||
               !Float.isNaN(mShapeState.strokeLinearGradientEndX) ||
               !Float.isNaN(mShapeState.strokeLinearGradientEndY);
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

    // ===== Effect API =====

    /**
     * Replace the effect stack. Effects are painted in list order — drop shadows behind the
     * shape, inner shadows over it. Passing {@code null} or an empty list clears them.
     *
     * @param effects Effects to apply, each copied on the way in
     * @return This ShapeDrawable for chaining
     */
    public ShapeDrawable setEffects(List<ShapeEffect> effects) {
        if (mShapeState.effects != null) {
            mShapeState.effects.clear();
        }
        if (effects == null || effects.isEmpty()) {
            return invalidateEffects();
        }
        for (int i = 0; i < effects.size(); i++) {
            addEffectInternal(effects.get(i));
        }
        return invalidateEffects();
    }

    /**
     * Replace the effect stack with a single effect. Passing {@code null} clears it.
     */
    public ShapeDrawable setEffect(ShapeEffect effect) {
        if (mShapeState.effects != null) {
            mShapeState.effects.clear();
        }
        addEffectInternal(effect);
        return invalidateEffects();
    }

    /**
     * Add an effect on top of the ones already set. They are painted in the order they were
     * added, which is what makes a bevel — a dark inner shadow under a light one — possible.
     *
     * @param effect Effect configuration, copied on the way in
     * @return This ShapeDrawable for chaining
     */
    public ShapeDrawable addEffect(ShapeEffect effect) {
        addEffectInternal(effect);
        return invalidateEffects();
    }

    private void addEffectInternal(ShapeEffect effect) {
        if (effect == null) {
            return;
        }
        if (mShapeState.effects == null) {
            mShapeState.effects = new ArrayList<>();
        }
        mShapeState.effects.add(effect.copy());
    }

    /**
     * The effects currently applied, in paint order. Mutating the returned list does not
     * affect the drawable — hand it back through {@link #setEffects(List)} to apply changes.
     */
    @NonNull
    public List<ShapeEffect> getEffects() {
        if (mShapeState.effects == null) {
            return new ArrayList<>();
        }
        List<ShapeEffect> copy = new ArrayList<>(mShapeState.effects.size());
        for (int i = 0; i < mShapeState.effects.size(); i++) {
            copy.add(mShapeState.effects.get(i).copy());
        }
        return copy;
    }

    /**
     * Clear all effects.
     *
     * @return This ShapeDrawable for chaining
     */
    public ShapeDrawable clearEffects() {
        if (mShapeState.effects != null) {
            mShapeState.effects.clear();
        }
        return invalidateEffects();
    }

    /**
     * True when at least one drop shadow has a visible color and geometry of its own.
     */
    public boolean hasDropShadow() {
        return mShapeState.hasDropShadow();
    }

    /**
     * True when at least one inner shadow has a visible color and geometry of its own.
     */
    public boolean hasInnerShadow() {
        return mShapeState.hasInnerShadow();
    }

    /**
     * Room the drop shadows take out of the drawable's bounds, per edge: the most any one of
     * them asks for.
     *
     * The shape is drawn inside these insets, so anything laying out content over this
     * drawable has to inset by the same amount to stay within the visible shape.
     */
    public void getShadowInsets(@NonNull Rect out) {
        ShapeEffect.computeInsets(out, mShapeState.effects);
    }

    private ShapeDrawable invalidateEffects() {
        mShapeState.computeOpacity();
        mShadowDirty = true;
        mInnerShadowDirty = true;
        mPathDirty = true;
        mRectDirty = true;
        invalidateSelf();
        return this;
    }

    /**
     * Apply the current Drawable object to the View background
     */
    public void intoBackground(View view) {
        // Neither shadow is in this list: both rasterize their blur into a bitmap of their
        // own, so neither needs the view to drop off the GPU.
        boolean needsSoftwareLayer = mShapeState.strokeDashGap > 0;

        if (needsSoftwareLayer) {
            // Hardware acceleration needs to be disabled, otherwise dashed lines may not take effect on some phones
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

        final boolean haveShadow = mShapeState.hasDropShadow();
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

        // Painted before any saveLayer() below, whose layer is only as big as the shape plus
        // its stroke and would clip the shadow away.
        if (haveShadow) {
            if (mShadowDirty) {
                buildShadowBitmap();
                mShadowDirty = false;
            }
            if (mShadowBitmap != null) {
                if (mShadowBitmapPaint == null) {
                    mShadowBitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
                }
                // The shadow's own alpha is already baked into the bitmap; this applies the
                // drawable-level alpha on top of it.
                mShadowBitmapPaint.setAlpha(mAlpha);
                canvas.drawBitmap(mShadowBitmap, mShadowBitmapSrc, mShadowBitmapDst, mShadowBitmapPaint);
            }
        } else {
            mShadowBitmap = null;
        }

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

        switch (st.shapeType) {
            case ShapeType.RECTANGLE:
                if (st.radiusArray != null) {
                    if (mPathDirty || mRectDirty) {
                        mPath.reset();
                        mPath.addRoundRect(mRect, st.radiusArray, Path.Direction.CW);
                        mPathDirty = mRectDirty = false;
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
                    canvas.drawRoundRect(mRect, rad, rad, mSolidPaint);
                    if (haveStroke) {
                        canvas.drawRoundRect(mRect, rad, rad, mStrokePaint);
                    }
                } else {
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
                canvas.drawOval(mRect, mSolidPaint);
                if (haveStroke) {
                    canvas.drawOval(mRect, mStrokePaint);
                }
                break;
            case ShapeType.LINE: {
                computeLinePoints(st, mLinePoints);
                canvas.drawLine(mLinePoints[0], mLinePoints[1], mLinePoints[2], mLinePoints[3], mStrokePaint);
                break;
            }
            case ShapeType.RING:
                Path path = buildRing(st);
                canvas.drawPath(path, mSolidPaint);
                if (haveStroke) {
                    canvas.drawPath(path, mStrokePaint);
                }
                break;
            default:
                break;
        }

        // Drawn over the fill and stroke, but still inside any layer above, so the
        // drawable-level alpha and color filter apply to it along with everything else.
        if (st.hasInnerShadow()) {
            if (mInnerShadowDirty) {
                buildInnerShadowBitmap();
                mInnerShadowDirty = false;
            }
            if (mInnerShadowBitmap != null) {
                if (mInnerShadowBitmapPaint == null) {
                    mInnerShadowBitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
                }
                // Each shadow's own alpha is already baked into the bitmap; this applies the
                // drawable-level alpha on top of it.
                mInnerShadowBitmapPaint.setAlpha(useLayer ? 255 : mAlpha);
                canvas.drawBitmap(mInnerShadowBitmap, mInnerShadowBitmapSrc,
                        mInnerShadowBitmapDst, mInnerShadowBitmapPaint);
            }
        } else {
            mInnerShadowBitmap = null;
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
     * Work out where a {@link ShapeType#LINE} starts and stops, into
     * {@code out = {startX, startY, stopX, stopY}}.
     */
    private void computeLinePoints(ShapeState st, float[] out) {
        RectF r = mRect;
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
                out[0] = 0;
                out[1] = 0;
                out[2] = 0;
                out[3] = r.bottom;
                break;
            case Gravity.RIGHT:
                out[0] = r.right;
                out[1] = 0;
                out[2] = r.right;
                out[3] = r.bottom;
                break;
            case Gravity.TOP:
                out[0] = 0;
                out[1] = 0;
                out[2] = r.right;
                out[3] = 0;
                break;
            case Gravity.BOTTOM:
                out[0] = 0;
                out[1] = r.bottom;
                out[2] = r.right;
                out[3] = r.bottom;
                break;
            case Gravity.CENTER:
            default:
                float y = r.centerY();
                out[0] = r.left;
                out[1] = y;
                out[2] = r.right;
                out[3] = y;
                break;
        }
    }

    /**
     * Rasterize the blurred drop shadows into {@link #mShadowBitmap}, painted in list order
     * so a stack of them layers up the way it does in Figma.
     *
     * Called only when the geometry or the effects change, never per frame. Views big enough
     * to make a full-resolution bitmap wasteful are rendered downscaled and blitted back up:
     * a blur is smooth by definition, so there is no detail for the resample to lose.
     */
    private void buildShadowBitmap() {
        final ShapeState st = mShapeState;
        final Rect bounds = getBounds();
        final int width = bounds.width();
        final int height = bounds.height();
        if (width <= 0 || height <= 0 || mRect.isEmpty() || !st.hasDropShadow()) {
            mShadowBitmap = null;
            return;
        }

        float scale = 1f;
        final long pixels = (long) width * height;
        if (pixels > MAX_SHADOW_PIXELS) {
            scale = (float) Math.sqrt((double) MAX_SHADOW_PIXELS / pixels);
        }
        final int bitmapWidth = Math.max(1, Math.round(width * scale));
        final int bitmapHeight = Math.max(1, Math.round(height * scale));

        if (mShadowBitmap == null || mShadowBitmap.getWidth() != bitmapWidth
                || mShadowBitmap.getHeight() != bitmapHeight) {
            mShadowBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        } else {
            mShadowBitmap.eraseColor(Color.TRANSPARENT);
        }

        if (mShadowPaint == null) {
            mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        // A line has no interior to fill, so it is traced with the stroke it is drawn with;
        // every other shape casts a solid shadow.
        final boolean isLine = st.shapeType == ShapeType.LINE;
        mShadowPaint.setStyle(isLine ? Paint.Style.STROKE : Paint.Style.FILL);
        mShadowPaint.setStrokeWidth(isLine ? mStrokePaint.getStrokeWidth() : 0f);

        final Canvas canvas = new Canvas(mShadowBitmap);
        // Draw in the drawable's own coordinate space and let the matrix map it into the
        // bitmap. The blur radius rides along with the matrix, so it scales with the shape.
        canvas.scale(scale, scale);
        canvas.translate(-bounds.left, -bounds.top);

        for (int i = 0; i < st.effects.size(); i++) {
            final ShapeEffect effect = st.effects.get(i);
            if (!effect.isDropShadow() || !effect.isEnabled()) {
                continue;
            }
            mShadowPaint.setColor(effect.color);
            mShadowPaint.setMaskFilter(effect.blur > 0
                    ? new BlurMaskFilter(effect.blur, BlurMaskFilter.Blur.NORMAL)
                    : null);
            drawShadowShape(canvas, st, effect, mShadowPaint);
        }

        mShadowBitmapSrc.set(0, 0, bitmapWidth, bitmapHeight);
        mShadowBitmapDst.set(bounds);
    }

    /**
     * Trace one drop shadow's silhouette: the shape, grown by the spread and moved by the
     * offset — not a squashed copy of it, so its corners stay concentric with the shape's.
     *
     * Spread reaches rectangles and ovals. Rings and lines are only translated — there is no
     * single sensible way to grow a ring outward — so a spread has no effect on those two.
     */
    private void drawShadowShape(Canvas canvas, ShapeState st, ShapeEffect effect, Paint paint) {
        mShadowRect.set(mRect);
        mShadowRect.inset(-effect.spread, -effect.spread);
        mShadowRect.offset(effect.offsetX, effect.offsetY);

        switch (st.shapeType) {
            case ShapeType.RECTANGLE:
                if (st.radiusArray != null) {
                    if (mShadowRadii == null) {
                        mShadowRadii = new float[8];
                    }
                    // Grow every corner along with the shape so the shadow stays concentric.
                    for (int i = 0; i < mShadowRadii.length; i++) {
                        mShadowRadii[i] = Math.max(0f, st.radiusArray[i] + effect.spread);
                    }
                    mShadowPath.reset();
                    mShadowPath.addRoundRect(mShadowRect, mShadowRadii, Path.Direction.CW);
                    canvas.drawPath(mShadowPath, paint);
                } else if (st.radius > 0.0f) {
                    float rad = Math.max(0f, st.radius + effect.spread);
                    float max = Math.min(mShadowRect.width(), mShadowRect.height()) * 0.5f;
                    if (rad > max) {
                        rad = max;
                    }
                    canvas.drawRoundRect(mShadowRect, rad, rad, paint);
                } else {
                    canvas.drawRect(mShadowRect, paint);
                }
                break;
            case ShapeType.OVAL:
                canvas.drawOval(mShadowRect, paint);
                break;
            case ShapeType.RING: {
                int saveCount = canvas.save();
                canvas.translate(effect.offsetX, effect.offsetY);
                canvas.drawPath(buildRing(st), paint);
                canvas.restoreToCount(saveCount);
                break;
            }
            case ShapeType.LINE: {
                computeLinePoints(st, mLinePoints);
                int saveCount = canvas.save();
                canvas.translate(effect.offsetX, effect.offsetY);
                canvas.drawLine(mLinePoints[0], mLinePoints[1], mLinePoints[2], mLinePoints[3], paint);
                canvas.restoreToCount(saveCount);
                break;
            }
            default:
                break;
        }
    }

    /**
     * Rasterize the stacked inner shadows into {@link #mInnerShadowBitmap}.
     *
     * Each shadow is drawn as the room left over between a wall well outside the shape and
     * the shape's own silhouette shrunk by the spread and moved by the offset — blur that
     * and the ink piles up along the edge the offset came from, which is what an inset
     * shadow looks like. The stack is then trimmed back to the shape in one pass, so the
     * shadows share a single anti-aliased edge instead of each fighting the clip.
     *
     * Called only when the geometry or the shadow attributes change, never per frame.
     * Line and ring shapes have no interior to inset into, so they get nothing.
     */
    private void buildInnerShadowBitmap() {
        final ShapeState st = mShapeState;
        final Rect bounds = getBounds();
        final int width = bounds.width();
        final int height = bounds.height();
        if (width <= 0 || height <= 0 || mRect.isEmpty() || !st.hasInnerShadow()
                || (st.shapeType != ShapeType.RECTANGLE && st.shapeType != ShapeType.OVAL)) {
            mInnerShadowBitmap = null;
            return;
        }

        float scale = 1f;
        final long pixels = (long) width * height;
        if (pixels > MAX_SHADOW_PIXELS) {
            scale = (float) Math.sqrt((double) MAX_SHADOW_PIXELS / pixels);
        }
        final int bitmapWidth = Math.max(1, Math.round(width * scale));
        final int bitmapHeight = Math.max(1, Math.round(height * scale));

        if (mInnerShadowBitmap == null || mInnerShadowBitmap.getWidth() != bitmapWidth
                || mInnerShadowBitmap.getHeight() != bitmapHeight) {
            mInnerShadowBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        } else {
            mInnerShadowBitmap.eraseColor(Color.TRANSPARENT);
        }

        if (mInnerShadowPaint == null) {
            mInnerShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mInnerShadowPaint.setStyle(Paint.Style.FILL);
        }

        final Canvas canvas = new Canvas(mInnerShadowBitmap);
        // Draw in the drawable's own coordinate space and let the matrix map it into the
        // bitmap. The blur radius rides along with the matrix, so it scales with the shape.
        canvas.scale(scale, scale);
        canvas.translate(-bounds.left, -bounds.top);

        for (int i = 0; i < st.effects.size(); i++) {
            final ShapeEffect effect = st.effects.get(i);
            if (!effect.isInnerShadow() || !effect.isEnabled()) {
                continue;
            }
            mInnerShadowPaint.setColor(effect.color);
            mInnerShadowPaint.setMaskFilter(effect.blur > 0
                    ? new BlurMaskFilter(effect.blur, BlurMaskFilter.Blur.NORMAL)
                    : null);
            buildInnerShadowPath(st, effect, mInnerShadowPath);
            canvas.drawPath(mInnerShadowPath, mInnerShadowPaint);
        }

        // Clear whatever fell outside the shape, so the shadow honors the corner radii.
        // The cut has to be drawn as the region *outside* the silhouette — an inverse-filled
        // path, which Skia expands to the whole canvas — because a Porter-Duff draw only
        // touches the pixels its own geometry covers: painting the silhouette itself with
        // DST_IN leaves the corners it never reached untouched, and the shadow spills out
        // past them as if the shape were square. Masking still beats clipping here: a clip
        // is aliased, and this edge sits right where the shadow is at its most opaque.
        if (mInnerShadowMaskPaint == null) {
            mInnerShadowMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mInnerShadowMaskPaint.setStyle(Paint.Style.FILL);
            mInnerShadowMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            mInnerShadowMaskPaint.setColor(Color.BLACK);
        }
        buildShapeOutlinePath(st, mInnerShadowPath);
        mInnerShadowPath.setFillType(Path.FillType.INVERSE_WINDING);
        canvas.drawPath(mInnerShadowPath, mInnerShadowMaskPaint);

        mInnerShadowBitmapSrc.set(0, 0, bitmapWidth, bitmapHeight);
        mInnerShadowBitmapDst.set(bounds);
    }

    /**
     * Trace one inner shadow: a wall far outside the shape with the shadow's opening punched
     * out of it, so filling the path leaves ink everywhere the opening doesn't reach.
     *
     * The opening is the shape shrunk by the spread and moved by the offset, corner radii
     * shrinking with it so the two stay concentric. Shrink it past nothing and the opening
     * closes, which correctly floods the whole shape with shadow.
     */
    private void buildInnerShadowPath(ShapeState st, ShapeEffect effect, Path out) {
        mInnerShadowRect.set(mRect);
        mInnerShadowRect.inset(effect.spread, effect.spread);
        mInnerShadowRect.offset(effect.offsetX, effect.offsetY);

        out.reset();
        out.setFillType(Path.FillType.EVEN_ODD);
        // Far enough out that the wall's own blurred edge never reaches back into the shape.
        final float slack = effect.blur * 2f + Math.abs(effect.spread)
                + Math.max(Math.abs(effect.offsetX), Math.abs(effect.offsetY)) + 1f;
        out.addRect(mRect.left - slack, mRect.top - slack,
                mRect.right + slack, mRect.bottom + slack, Path.Direction.CW);

        if (mInnerShadowRect.width() <= 0 || mInnerShadowRect.height() <= 0) {
            // The opening has closed: the shadow covers the shape outright.
            return;
        }

        if (st.shapeType == ShapeType.OVAL) {
            out.addOval(mInnerShadowRect, Path.Direction.CW);
            return;
        }

        if (st.radiusArray != null) {
            if (mInnerShadowRadii == null) {
                mInnerShadowRadii = new float[8];
            }
            for (int i = 0; i < mInnerShadowRadii.length; i++) {
                mInnerShadowRadii[i] = Math.max(0f, st.radiusArray[i] - effect.spread);
            }
            out.addRoundRect(mInnerShadowRect, mInnerShadowRadii, Path.Direction.CW);
        } else if (st.radius > 0f) {
            float rad = Math.max(0f, st.radius - effect.spread);
            float max = Math.min(mInnerShadowRect.width(), mInnerShadowRect.height()) * 0.5f;
            if (rad > max) {
                rad = max;
            }
            out.addRoundRect(mInnerShadowRect, rad, rad, Path.Direction.CW);
        } else {
            out.addRect(mInnerShadowRect, Path.Direction.CW);
        }
    }

    /**
     * Trace the shape's silhouette — the same outline the fill occupies — into {@code out}.
     */
    private void buildShapeOutlinePath(ShapeState st, Path out) {
        out.reset();
        out.setFillType(Path.FillType.WINDING);
        if (st.shapeType == ShapeType.OVAL) {
            out.addOval(mRect, Path.Direction.CW);
        } else if (st.radiusArray != null) {
            out.addRoundRect(mRect, st.radiusArray, Path.Direction.CW);
        } else if (st.radius > 0f) {
            float rad = Math.min(st.radius, Math.min(mRect.width(), mRect.height()) * 0.5f);
            out.addRoundRect(mRect, rad, rad, Path.Direction.CW);
        } else {
            out.addRect(mRect, Path.Direction.CW);
        }
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

        getShadowInsets(mShadowInsets);

        float let = bounds.left + inset + mShadowInsets.left;
        float top = bounds.top + inset + mShadowInsets.top;
        float right = bounds.right - inset - mShadowInsets.right;
        float bottom = bounds.bottom - inset - mShadowInsets.bottom;

        mRect.set(let, top, right, bottom);

        // The geometry just moved, so whatever we rasterized last time is stale.
        mShadowDirty = true;
        mInnerShadowDirty = true;

        if (st.solidColors == null) {
            mSolidPaint.setShader(null);
        }

        if (st.strokeColors == null) {
            mStrokePaint.setShader(null);
        }

        if (st.solidColors != null) {
            mSolidPaint.setShader(buildGradientShader(mRect, false));

            // If we don't have a solid color, the alpha channel must be
            // maxed out so that alpha modulation works correctly.
            if (!st.hasSolidColor) {
                mSolidPaint.setColor(Color.BLACK);
            }
        }

        if (st.strokeColors != null) {
            mStrokePaint.setShader(buildGradientShader(mRect, true));

            if (!st.hasStrokeColor) {
                mStrokePaint.setColor(Color.BLACK);
            }
        }
        return !mRect.isEmpty();
    }

    /**
     * Build the gradient shader for either the fill or the stroke paint.
     * <p>
     * Both sides support the same set of options (gradient type, orientation, center,
     * radius, elliptical scale, rotation, focal point, color stops and explicit
     * start/end extents); they only differ in which {@link ShapeState} fields they read.
     *
     * @param rect   Rectangle the gradient is mapped onto
     * @param stroke true to build the stroke gradient, false to build the fill gradient
     */
    private Shader buildGradientShader(RectF rect, boolean stroke) {
        final ShapeState st = mShapeState;

        final int gradientType = stroke ? st.strokeGradientType : st.solidGradientType;
        final int[] colors = stroke ? st.strokeColors : st.solidColors;

        // The platform shaders throw when the stop count doesn't match the color count,
        // so fall back to evenly distributed stops instead of crashing
        float[] stops = stroke ? st.strokePositions : st.positions;
        if (stops != null && stops.length != colors.length) {
            stops = null;
        }
        final float[] colorPositions = stops;

        final ShapeGradientOrientation orientation = stroke ?
                st.strokeGradientOrientation : st.solidGradientOrientation;
        final float relativeCenterX = stroke ? st.strokeCenterX : st.solidCenterX;
        final float relativeCenterY = stroke ? st.strokeCenterY : st.solidCenterY;
        final float gradientRadius = stroke ? st.strokeGradientRadius : st.gradientRadius;
        final boolean gradientRadiusInPx = stroke ? st.strokeGradientRadiusInPx : st.gradientRadiusInPx;
        final float radiusX = stroke ? st.strokeGradientRadiusX : st.gradientRadiusX;
        final float radiusY = stroke ? st.strokeGradientRadiusY : st.gradientRadiusY;
        final float radialAngle = stroke ? st.strokeRadialGradientAngle : st.radialGradientAngle;
        final float focalStartX = stroke ? st.strokeRadialStartX : st.radialStartX;
        final float focalStartY = stroke ? st.strokeRadialStartY : st.radialStartY;
        final float extentStartX = stroke ? st.strokeLinearGradientStartX : st.linearGradientStartX;
        final float extentStartY = stroke ? st.strokeLinearGradientStartY : st.linearGradientStartY;
        final float extentEndX = stroke ? st.strokeLinearGradientEndX : st.linearGradientEndX;
        final float extentEndY = stroke ? st.strokeLinearGradientEndY : st.linearGradientEndY;

        final float level = st.useLevel ? getLevel() / 10000f : 1f;

        switch (gradientType) {
            case ShapeGradientType.RADIAL_GRADIENT: {
                float centerX, centerY, radius;
                float angle;
                float scaleX = 1f, scaleY = 1f;

                // Figma style needs all four extents to describe the center-to-edge vector
                boolean hasCustomExtent = !Float.isNaN(extentStartX) && !Float.isNaN(extentStartY) &&
                        !Float.isNaN(extentEndX) && !Float.isNaN(extentEndY);

                if (hasCustomExtent) {
                    // Figma Style: Gradient defined by a vector from Start(Center) to End(Radius edge)

                    // 1. Calculate Center (Start Point)
                    centerX = rect.left + rect.width() * extentStartX;
                    centerY = rect.top + rect.height() * extentStartY;

                    // 2. Calculate End Point
                    float endX = rect.left + rect.width() * extentEndX;
                    float endY = rect.top + rect.height() * extentEndY;

                    // 3. Calculate Radius (Distance between Start and End)
                    // This defines the radius of the gradient circle/ellipse at 100%
                    double dx = endX - centerX;
                    double dy = endY - centerY;
                    radius = (float) Math.sqrt(dx * dx + dy * dy);

                    // 4. Calculate Angle (Rotation)
                    // Math.atan2 returns radians, the matrix wants degrees.
                    // Standard radial gradient 0 degrees is at 3 o'clock.
                    angle = (float) Math.toDegrees(Math.atan2(dy, dx));

                    // Apply level if needed (though less common for this style)
                    radius *= level;
                } else {
                    // Standard Style: Center + Radius + Angle attributes

                    // Center position (relative 0-1)
                    centerX = rect.left + rect.width() * relativeCenterX;
                    centerY = rect.top + rect.height() * relativeCenterY;

                    if (gradientRadiusInPx) {
                        // Radius was declared as a dimension, use it as-is
                        radius = level * gradientRadius;
                    } else {
                        // Calculate base radius from view dimensions
                        // gradientRadius is a multiplier (0.5 = half of min dimension)
                        float baseRadius = Math.min(rect.width(), rect.height()) / 2f;
                        radius = level * baseRadius;
                        if (gradientRadius > 0 && gradientRadius != 0.5f) {
                            radius = level * baseRadius * (gradientRadius * 2f);
                        }
                    }

                    angle = radialAngle;
                }

                // Scale X/Y from attributes if present (elliptical)
                if (radiusX > 0 && radiusY > 0) {
                    scaleX = radiusX;
                    scaleY = radiusY;
                }

                // If radius is 0, the shader would draw nothing, so clamp to a small value.
                if (radius <= 0) {
                    radius = 1f;
                }

                RadialGradient radialGradient = new RadialGradient(centerX, centerY, radius,
                        colors, colorPositions, Shader.TileMode.CLAMP);

                // Apply Matrix transformations (Scaling, Rotation, Focal Offset)
                boolean hasFocalOffset = !hasCustomExtent &&
                        (!Float.isNaN(focalStartX) || !Float.isNaN(focalStartY));
                boolean hasTransform = scaleX != 1f || scaleY != 1f || angle != 0f || hasFocalOffset;

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

                    // Apply focal point offset (only for Standard Style, the Figma style
                    // already encodes it in the center calculation above)
                    if (hasFocalOffset) {
                        float focalX = Float.isNaN(focalStartX) ? centerX
                                : rect.left + rect.width() * focalStartX;
                        float focalY = Float.isNaN(focalStartY) ? centerY
                                : rect.top + rect.height() * focalStartY;
                        mCachedGradientMatrix.postTranslate(focalX - centerX, focalY - centerY);
                    }

                    // setLocalMatrix() copies the values, so the shared matrix can be reused
                    radialGradient.setLocalMatrix(mCachedGradientMatrix);
                }

                return radialGradient;
            }
            case ShapeGradientType.SWEEP_GRADIENT: {
                float x0 = rect.left + rect.width() * relativeCenterX;
                float y0 = rect.top + rect.height() * relativeCenterY;

                int[] sweepColors = colors;
                float[] sweepPositions = colorPositions;

                if (st.useLevel) {
                    final int length = colors.length;

                    sweepColors = stroke ? st.tempStrokeColors : st.tempSolidColors;
                    if (sweepColors == null || sweepColors.length != length + 1) {
                        sweepColors = new int[length + 1];
                        if (stroke) {
                            st.tempStrokeColors = sweepColors;
                        } else {
                            st.tempSolidColors = sweepColors;
                        }
                    }
                    System.arraycopy(colors, 0, sweepColors, 0, length);
                    sweepColors[length] = colors[length - 1];

                    sweepPositions = stroke ? st.tempStrokePositions : st.tempSolidPositions;
                    if (sweepPositions == null || sweepPositions.length != length + 1) {
                        sweepPositions = new float[length + 1];
                        if (stroke) {
                            st.tempStrokePositions = sweepPositions;
                        } else {
                            st.tempSolidPositions = sweepPositions;
                        }
                    }

                    final float fraction = 1f / (length - 1);
                    for (int i = 0; i < length; i++) {
                        sweepPositions[i] = i * fraction * level;
                    }
                    sweepPositions[length] = 1f;
                }

                return new SweepGradient(x0, y0, sweepColors, sweepPositions);
            }
            case ShapeGradientType.LINEAR_GRADIENT:
            default: {
                float[] coordinate;

                // Any one of the four extents opts into explicit positioning
                boolean hasCustomExtent = !Float.isNaN(extentStartX) || !Float.isNaN(extentStartY) ||
                        !Float.isNaN(extentEndX) || !Float.isNaN(extentEndY);

                if (hasCustomExtent) {
                    // Use custom positions (with fallback to defaults)
                    float startX = Float.isNaN(extentStartX) ? 0.5f : extentStartX;
                    float startY = Float.isNaN(extentStartY) ? 0f : extentStartY;
                    float endX = Float.isNaN(extentEndX) ? 0.5f : extentEndX;
                    float endY = Float.isNaN(extentEndY) ? 1f : extentEndY;

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
                            mLayoutDirection, rect, level, orientation);
                }

                return new LinearGradient(coordinate[0], coordinate[1], coordinate[2], coordinate[3],
                        colors, colorPositions, Shader.TileMode.CLAMP);
            }
        }
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

        // Initialize effect colors from state
        if (state.effects != null) {
            for (ShapeEffect effect : state.effects) {
                if (effect.colorStateList != null) {
                    effect.color = effect.colorStateList.getColorForState(getState(), effect.color);
                }
            }
            mShadowDirty = true;
            mInnerShadowDirty = true;
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
        if (mShapeState.effects != null) {
            for (ShapeEffect effect : mShapeState.effects) {
                if (effect.colorStateList != null && effect.colorStateList.isStateful()) {
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

        if (mShapeState.effects != null) {
            boolean effectChanged = false;
            for (ShapeEffect effect : mShapeState.effects) {
                if (effect.colorStateList == null) {
                    continue;
                }
                int newColor = effect.colorStateList.getColorForState(state, effect.color);
                if (newColor != effect.color) {
                    effect.color = newColor;
                    effectChanged = true;
                }
            }
            if (effectChanged) {
                invalidateEffects();
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

}