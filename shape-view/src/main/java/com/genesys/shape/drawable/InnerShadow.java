package com.genesys.shape.drawable;

import android.content.res.ColorStateList;
import android.graphics.BlurMaskFilter;
import androidx.annotation.ColorInt;

/**
 * Represents a single inner shadow effect.
 * Inner shadows are drawn inside the shape bounds, creating inset/beveled effects.
 * <p>
 * Multiple inner shadows can be stacked to create complex effects like
 * highlight and shadow pairs for 3D button appearances.
 *
 * @see ShapeDrawable#setInnerShadow
 * @see ShapeDrawable#addInnerShadow
 */
public class InnerShadow {

    /** Shadow color (ARGB format) */
    @ColorInt
    public int color;
    public ColorStateList colorStateList;

    /** Blur radius in pixels. Higher values create softer shadows. */
    public float blurRadius;

    /** Horizontal offset in pixels. Positive = right, negative = left. */
    public float offsetX;

    /** Vertical offset in pixels. Positive = down, negative = up. */
    public float offsetY;

    /** Spread radius in pixels. Expands/contracts the shadow before blur. */
    public float spread;

    // Cached BlurMaskFilter to avoid per-frame allocation
    transient BlurMaskFilter cachedMaskFilter;
    transient float cachedBlurRadius = -1f;

    /**
     * Create an inner shadow with default spread (0).
     *
     * @param color      Shadow color (ARGB)
     * @param blurRadius Blur radius in pixels
     * @param offsetX    Horizontal offset in pixels
     * @param offsetY    Vertical offset in pixels
     */
    public InnerShadow(@ColorInt int color, float blurRadius, float offsetX, float offsetY) {
        this(color, blurRadius, offsetX, offsetY, 0f);
    }

    /**
     * Create an inner shadow with all parameters.
     *
     * @param color      Shadow color (ARGB)
     * @param blurRadius Blur radius in pixels
     * @param offsetX    Horizontal offset in pixels
     * @param offsetY    Vertical offset in pixels
     * @param spread     Spread radius in pixels
     */
    public InnerShadow(@ColorInt int color, float blurRadius, float offsetX, float offsetY, float spread) {
        this.color = color;
        this.blurRadius = blurRadius;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.spread = spread;
        this.spread = spread;
    }

    public InnerShadow(ColorStateList colorStateList, float blurRadius, float offsetX, float offsetY, float spread) {
        this.colorStateList = colorStateList;
        this.color = colorStateList.getDefaultColor();
        this.blurRadius = blurRadius;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.spread = spread;
    }

    /**
     * Create a deep copy of this inner shadow.
     *
     * @return A new InnerShadow with the same parameters
     */
    public InnerShadow copy() {
        if (colorStateList != null) {
            return new InnerShadow(colorStateList, blurRadius, offsetX, offsetY, spread);
        }
        return new InnerShadow(color, blurRadius, offsetX, offsetY, spread);
    }

    /**
     * Get or create a cached BlurMaskFilter for this shadow.
     * Returns null if blurRadius <= 0.
     */
    public BlurMaskFilter getOrCreateMaskFilter() {
        if (blurRadius <= 0) {
            cachedMaskFilter = null;
            cachedBlurRadius = -1f;
            return null;
        }
        float effectiveRadius = blurRadius * 1.5f;
        if (cachedMaskFilter == null || cachedBlurRadius != effectiveRadius) {
            cachedMaskFilter = new BlurMaskFilter(effectiveRadius, BlurMaskFilter.Blur.NORMAL);
            cachedBlurRadius = effectiveRadius;
        }
        return cachedMaskFilter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InnerShadow that = (InnerShadow) o;
        return color == that.color &&
                Float.compare(that.blurRadius, blurRadius) == 0 &&
                Float.compare(that.offsetX, offsetX) == 0 &&
                Float.compare(that.offsetY, offsetY) == 0 &&
                Float.compare(that.spread, spread) == 0;
    }

    @Override
    public int hashCode() {
        int result = color;
        result = 31 * result + Float.floatToIntBits(blurRadius);
        result = 31 * result + Float.floatToIntBits(offsetX);
        result = 31 * result + Float.floatToIntBits(offsetY);
        result = 31 * result + Float.floatToIntBits(spread);
        return result;
    }
}
