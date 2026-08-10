package com.genesys.shape.drawable;

import android.content.res.ColorStateList;
import android.graphics.Rect;

import androidx.annotation.ColorInt;

import java.util.List;


public class ShapeEffect {

    public static final int EDGE_NONE = 0;
    public static final int EDGE_LEFT = 1;
    public static final int EDGE_TOP = 1 << 1;
    public static final int EDGE_RIGHT = 1 << 2;
    public static final int EDGE_BOTTOM = 1 << 3;
    public static final int EDGE_ALL = EDGE_LEFT | EDGE_TOP | EDGE_RIGHT | EDGE_BOTTOM;


    public static final int DEFAULT_COLOR = 0x33000000;

    @ShapeEffectTypeLimit
    public int type = ShapeEffectType.DROP_SHADOW;


    @ColorInt
    public int color = DEFAULT_COLOR;

    public ColorStateList colorStateList;

    /** Blur radius in pixels. Zero leaves a hard edge. */
    public int blur;

    /**
     * Grows the shadow on every side before it is blurred, the way the spread radius of a
     * CSS {@code box-shadow} does. Negative values shrink it. An inner shadow reads it the
     * other way round, thickening the shadow as the shape's opening shrinks.
     */
    public int spread;

    /** Horizontal offset in pixels. Positive moves the shadow right. */
    public int offsetX;

    /** Vertical offset in pixels. Positive moves the shadow down. */
    public int offsetY;

    /**
     * Which edges give up room for this drop shadow, as a mask of the {@code EDGE_*} flags.
     * An edge left out reserves nothing, so the shadow runs off that side of the drawable
     * and is clipped — that is what lets a bottom sheet cast a shadow upwards while staying
     * flush to the screen on its other three sides.
     *
     * Ignored by {@link ShapeEffectType#INNER_SHADOW}, which is painted within the shape and
     * so costs no room at all.
     */
    public int edges = EDGE_ALL;

    public ShapeEffect() {}

    public ShapeEffect(@ShapeEffectTypeLimit int type, @ColorInt int color, int blur,
                       int spread, int offsetX, int offsetY) {
        this.type = type;
        this.color = color;
        this.blur = blur;
        this.spread = spread;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /** A drop shadow reaching every edge. */
    public static ShapeEffect dropShadow(@ColorInt int color, int blur, int spread,
                                         int offsetX, int offsetY) {
        return new ShapeEffect(ShapeEffectType.DROP_SHADOW, color, blur, spread, offsetX, offsetY);
    }

    /** An inner shadow. */
    public static ShapeEffect innerShadow(@ColorInt int color, int blur, int spread,
                                          int offsetX, int offsetY) {
        return new ShapeEffect(ShapeEffectType.INNER_SHADOW, color, blur, spread, offsetX, offsetY);
    }

    public ShapeEffect setType(@ShapeEffectTypeLimit int type) {
        this.type = type;
        return this;
    }

    public ShapeEffect setColor(@ColorInt int color) {
        this.color = color;
        return this;
    }

    /** Drive the color from the view's state. The list's default color is used up front. */
    public ShapeEffect setColor(ColorStateList colorStateList) {
        this.colorStateList = colorStateList;
        if (colorStateList != null) {
            this.color = colorStateList.getDefaultColor();
        }
        return this;
    }

    public ShapeEffect setBlur(int blur) {
        this.blur = Math.max(0, blur);
        return this;
    }

    public ShapeEffect setSpread(int spread) {
        this.spread = spread;
        return this;
    }

    public ShapeEffect setOffset(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        return this;
    }

    /** @see #edges */
    public ShapeEffect setEdges(int edges) {
        this.edges = edges;
        return this;
    }

    public boolean isDropShadow() {
        return type == ShapeEffectType.DROP_SHADOW;
    }

    public boolean isInnerShadow() {
        return type == ShapeEffectType.INNER_SHADOW;
    }

    /**
     * An effect only shows up if it has a visible color and some geometry of its own — a
     * zero blur, zero spread, unoffset shadow sits exactly under the shape's edge.
     */
    public boolean isEnabled() {
        if ((color >>> 24) == 0) {
            return false;
        }
        return blur > 0 || spread != 0 || offsetX != 0 || offsetY != 0;
    }

    /**
     * Room this effect needs on each edge, in pixels.
     *
     * A drop shadow reaches {@code blur + spread} beyond the shape in every direction, then
     * the offset shifts it: moving it right takes space off the right edge and gives it back
     * on the left. Edges left out of {@link #edges} reserve nothing. An inner shadow needs
     * no room at all.
     */
    public void computeInsets(Rect out) {
        if (!isEnabled() || type != ShapeEffectType.DROP_SHADOW) {
            out.setEmpty();
            return;
        }
        final int extent = blur + spread;
        out.left = (edges & EDGE_LEFT) != 0 ? Math.max(0, extent - offsetX) : 0;
        out.top = (edges & EDGE_TOP) != 0 ? Math.max(0, extent - offsetY) : 0;
        out.right = (edges & EDGE_RIGHT) != 0 ? Math.max(0, extent + offsetX) : 0;
        out.bottom = (edges & EDGE_BOTTOM) != 0 ? Math.max(0, extent + offsetY) : 0;
    }

    /**
     * Room a whole stack of effects needs on each edge: the most any one of them asks for,
     * since they all share the same shape.
     */
    public static void computeInsets(Rect out, List<ShapeEffect> effects) {
        out.setEmpty();
        if (effects == null) {
            return;
        }
        final Rect insets = new Rect();
        for (int i = 0; i < effects.size(); i++) {
            effects.get(i).computeInsets(insets);
            out.left = Math.max(out.left, insets.left);
            out.top = Math.max(out.top, insets.top);
            out.right = Math.max(out.right, insets.right);
            out.bottom = Math.max(out.bottom, insets.bottom);
        }
    }

    /**
     * Create a deep copy of this effect.
     */
    public ShapeEffect copy() {
        ShapeEffect copy = new ShapeEffect(type, color, blur, spread, offsetX, offsetY);
        copy.colorStateList = colorStateList;
        copy.edges = edges;
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShapeEffect that = (ShapeEffect) o;
        return type == that.type &&
                color == that.color &&
                blur == that.blur &&
                spread == that.spread &&
                offsetX == that.offsetX &&
                offsetY == that.offsetY &&
                edges == that.edges;
    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + color;
        result = 31 * result + blur;
        result = 31 * result + spread;
        result = 31 * result + offsetX;
        result = 31 * result + offsetY;
        result = 31 * result + edges;
        return result;
    }
}
