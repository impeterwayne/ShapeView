package com.genesys.shape.builder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.genesys.shape.config.ITextColorStyleable;
import com.genesys.shape.config.ITextViewAttribute;
import com.genesys.shape.other.TextViewAttribute;
import com.genesys.shape.span.LinearGradientFontSpan;
import com.genesys.shape.span.StrokeFontSpan;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeView
 *    time   : 2021/08/28
 *    desc   : TextColor Builder class
 */
public final class TextColorBuilder {

    /** Horizontal gradient orientation */
    public static final int GRADIENT_ORIENTATION_HORIZONTAL = LinearLayout.HORIZONTAL;
    /** Vertical gradient orientation */
    public static final int GRADIENT_ORIENTATION_VERTICAL = LinearLayout.VERTICAL;

    private final TextView mTextView;
    private final ITextViewAttribute mTextViewAttribute;

    private int mTextColor;
    private Integer mTextPressedColor;
    private Integer mTextCheckedColor;
    private Integer mTextDisabledColor;
    private Integer mTextFocusedColor;
    private Integer mTextSelectedColor;

    private int[] mTextGradientColors;
    private int mTextGradientOrientation;

    private int mTextStrokeColor;
    private int mTextStrokeSize;

    private ColorStateList mTextColorStateList;
    private ColorStateList mTextGradientStartColorStateList;
    private ColorStateList mTextGradientCenterColorStateList;
    private ColorStateList mTextGradientEndColorStateList;
    private ColorStateList mTextStrokeColorStateList;

    // Reentrancy guard to prevent StackOverflow from
    // drawableStateChanged() → onStateChanged() → setText() → drawableStateChanged() loop
    private boolean mIsInStateChange;

    // Cached LinearGradient to avoid per-frame allocation
    private LinearGradient mCachedTextGradient;
    private int mCachedGradientCanvasWidth;
    private int mCachedGradientCanvasHeight;
    private int mCachedGradientPaddingLeft;
    private int mCachedGradientPaddingTop;
    private int mCachedGradientPaddingEnd;
    private int mCachedGradientPaddingBottom;
    private int mCachedGradientOrientation = -1;
    private int mCachedGradientColorsHash;

    public TextColorBuilder(TextView textView, TypedArray typedArray, ITextColorStyleable styleable) {
        mTextView = textView;
        
        if (typedArray.hasValue(styleable.getTextColorStyleable())) {
            mTextColorStateList = typedArray.getColorStateList(styleable.getTextColorStyleable());
        }
        
        if (mTextColorStateList != null) {
            mTextColor = mTextColorStateList.getDefaultColor();
        } else {
            mTextColor = typedArray.getColor(styleable.getTextColorStyleable(), textView.getTextColors().getDefaultColor());
        }
        if (typedArray.hasValue(styleable.getTextPressedColorStyleable())) {
            mTextPressedColor = typedArray.getColor(styleable.getTextPressedColorStyleable(), mTextColor);
        }
        if (styleable.getTextCheckedColorStyleable() > 0 && typedArray.hasValue(styleable.getTextCheckedColorStyleable())) {
            mTextCheckedColor = typedArray.getColor(styleable.getTextCheckedColorStyleable(), mTextColor);
        }
        if (typedArray.hasValue(styleable.getTextDisabledColorStyleable())) {
            mTextDisabledColor = typedArray.getColor(styleable.getTextDisabledColorStyleable(), mTextColor);
        }
        if (typedArray.hasValue(styleable.getTextFocusedColorStyleable())) {
            mTextFocusedColor = typedArray.getColor(styleable.getTextFocusedColorStyleable(), mTextColor);
        }
        if (typedArray.hasValue(styleable.getTextSelectedColorStyleable())) {
            mTextSelectedColor = typedArray.getColor(styleable.getTextSelectedColorStyleable(), mTextColor);
        }

        if (typedArray.hasValue(styleable.getTextStartColorStyleable()) || 
            typedArray.hasValue(styleable.getTextEndColorStyleable())) {
            
            if (typedArray.hasValue(styleable.getTextStartColorStyleable())) {
                 mTextGradientStartColorStateList = typedArray.getColorStateList(styleable.getTextStartColorStyleable());
            }
            if (typedArray.hasValue(styleable.getTextCenterColorStyleable())) {
                 mTextGradientCenterColorStateList = typedArray.getColorStateList(styleable.getTextCenterColorStyleable());
            }
            if (typedArray.hasValue(styleable.getTextEndColorStyleable())) {
                 mTextGradientEndColorStateList = typedArray.getColorStateList(styleable.getTextEndColorStyleable());
            }

            int startColor = mTextGradientStartColorStateList != null ? 
                    mTextGradientStartColorStateList.getDefaultColor() : 
                    typedArray.getColor(styleable.getTextStartColorStyleable(), mTextColor);
            
            int endColor = mTextGradientEndColorStateList != null ? 
                    mTextGradientEndColorStateList.getDefaultColor() : 
                    typedArray.getColor(styleable.getTextEndColorStyleable(), mTextColor);

            if (typedArray.hasValue(styleable.getTextCenterColorStyleable())) {
                int centerColor = mTextGradientCenterColorStateList != null ? 
                        mTextGradientCenterColorStateList.getDefaultColor() : 
                        typedArray.getColor(styleable.getTextCenterColorStyleable(), mTextColor);
                mTextGradientColors = new int[] {startColor, centerColor, endColor};
            } else {
                mTextGradientColors = new int[] {startColor, endColor};
            }
        }

        mTextGradientOrientation = typedArray.getColor(styleable.getTextGradientOrientationStyleable(),
                LinearGradientFontSpan.GRADIENT_ORIENTATION_HORIZONTAL);

        if (typedArray.hasValue(styleable.getTextStrokeColorStyleable())) {
            mTextStrokeColorStateList = typedArray.getColorStateList(styleable.getTextStrokeColorStyleable());
            if (mTextStrokeColorStateList != null) {
                mTextStrokeColor = mTextStrokeColorStateList.getDefaultColor();
            } else {
                mTextStrokeColor = typedArray.getColor(styleable.getTextStrokeColorStyleable(), Color.TRANSPARENT);
            }
        }

        if (typedArray.hasValue(styleable.getTextStrokeSizeStyleable())) {
            mTextStrokeSize = typedArray.getDimensionPixelSize(styleable.getTextStrokeSizeStyleable(), 0);
        }

        mTextViewAttribute = new TextViewAttribute(mTextView);
    }

    public TextColorBuilder setTextColor(int color) {
        mTextColor = color;
        return this;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public TextColorBuilder setTextPressedColor(Integer color) {
        mTextPressedColor = color;
        return this;
    }

    @Nullable
    public Integer getTextPressedColor() {
        return mTextPressedColor;
    }

    public TextColorBuilder setTextCheckedColor(Integer color) {
        mTextCheckedColor = color;
        return this;
    }

    @Nullable
    public Integer getTextCheckedColor() {
        return mTextCheckedColor;
    }

    public TextColorBuilder setTextDisabledColor(Integer color) {
        mTextDisabledColor = color;
        return this;
    }

    @Nullable
    public Integer getTextDisabledColor() {
        return mTextDisabledColor;
    }

    public TextColorBuilder setTextFocusedColor(Integer color) {
        mTextFocusedColor = color;
        return this;
    }

    @Nullable
    public Integer getTextFocusedColor() {
        return mTextFocusedColor;
    }

    public TextColorBuilder setTextSelectedColor(Integer color) {
        mTextSelectedColor = color;
        return this;
    }

    @Nullable
    public Integer getTextSelectedColor() {
        return mTextSelectedColor;
    }

    public TextColorBuilder setTextGradientColors(int startColor, int endColor) {
        return setTextGradientColors(new int[]{startColor, endColor});
    }

    public TextColorBuilder setTextGradientColors(int startColor, int centerColor, int endColor) {
        return setTextGradientColors(new int[]{startColor, centerColor, endColor});
    }

    public TextColorBuilder setTextGradientColors(int[] colors) {
        mTextGradientColors = colors;
        return this;
    }

    @Nullable
    public int[] getTextGradientColors() {
        return mTextGradientColors;
    }

    public boolean isTextGradientColorsEnable() {
        return mTextGradientColors != null && mTextGradientColors.length > 0;
    }

    public TextColorBuilder setTextGradientOrientation(int orientation) {
        mTextGradientOrientation = orientation;
        return this;
    }

    public int getTextGradientOrientation() {
        return mTextGradientOrientation;
    }

    public TextColorBuilder setTextStrokeColor(int color) {
        mTextStrokeColor = color;
        return this;
    }

    public TextColorBuilder setTextStrokeSize(int size) {
        mTextStrokeSize = size;
        return this;
    }

    public int getTextStrokeColor() {
        return mTextStrokeColor;
    }

    public int getTextStrokeSize() {
        return mTextStrokeSize;
    }

    public boolean isTextStrokeColorEnable() {
        return mTextStrokeColor != Color.TRANSPARENT && mTextStrokeSize > 0;
    }

     /**
     * Clear text gradient color
     */
    public void clearTextGradientColor() {
        if (!isTextGradientColorsEnable()) {
            mTextView.setTextColor(mTextColor);
        }
        mTextGradientColors = null;
        mTextView.postInvalidate();
    }

     /**
     * Clear text stroke color
     */
    public void clearTextStrokeColor() {
        mTextStrokeColor = Color.TRANSPARENT;
        mTextView.setText(mTextView.getText().toString(), BufferType.NORMAL);
    }

    public SpannableStringBuilder buildStrokeFontSpannable(CharSequence text) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);

        StrokeFontSpan strokeFontSpan = null;

        if (isTextStrokeColorEnable()) {
            strokeFontSpan = new StrokeFontSpan(mTextViewAttribute)
                    .setTextStrokeColor(mTextStrokeColor)
                    .setTextStrokeSize(mTextStrokeSize);
        }

        if (strokeFontSpan != null) {
            strokeFontSpan.setTextSolidColor(mTextColor);
            builder.setSpan(strokeFontSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return builder;
    }

    public ColorStateList buildColorState() {
        if (mTextPressedColor == null &&
                mTextCheckedColor == null &&
                mTextDisabledColor == null &&
                mTextFocusedColor == null &&
                mTextSelectedColor == null) {
            return ColorStateList.valueOf(mTextColor);
        }

        int maxSize = 6;
        int arraySize = 0;
        int[][] statesTemp = new int[maxSize][];
        int[] colorsTemp = new int[maxSize];

        if (mTextPressedColor != null) {
            statesTemp[arraySize] = new int[]{android.R.attr.state_pressed};
            colorsTemp[arraySize] = mTextPressedColor;
            arraySize++;
        }
        if (mTextCheckedColor != null) {
            statesTemp[arraySize] = new int[]{android.R.attr.state_checked};
            colorsTemp[arraySize] = mTextCheckedColor;
            arraySize++;
        }
        if (mTextDisabledColor != null) {
            statesTemp[arraySize] = new int[]{-android.R.attr.state_enabled};
            colorsTemp[arraySize] = mTextDisabledColor;
            arraySize++;
        }
        if (mTextFocusedColor != null) {
            statesTemp[arraySize] = new int[]{android.R.attr.state_focused};
            colorsTemp[arraySize] = mTextFocusedColor;
            arraySize++;
        }
        if (mTextSelectedColor != null) {
            statesTemp[arraySize] = new int[]{android.R.attr.state_selected};
            colorsTemp[arraySize] = mTextSelectedColor;
            arraySize++;
        }

        statesTemp[arraySize] = new int[]{};
        colorsTemp[arraySize] = mTextColor;
        arraySize++;

        int[][] states;
        int[] colors;
        if (arraySize == maxSize) {
            states = statesTemp;
            colors = colorsTemp;
        } else {
            states = new int[arraySize][];
            colors = new int[arraySize];
            // Copy the array
            System.arraycopy(statesTemp, 0, states, 0, arraySize);
            System.arraycopy(colorsTemp, 0, colors, 0, arraySize);
        }
        return new ColorStateList(states, colors);
    }

    public void intoTextColor() {
        if (isTextGradientColorsEnable()) {
            // If the TextView is set to opaque, then force it to be opaque
            mTextColor = mTextColor | 0xFF000000;
        }

        mTextView.setTextColor(buildColorState());

        if (isTextStrokeColorEnable()) {
            mTextView.setText(buildStrokeFontSpannable(mTextView.getText().toString()), BufferType.SPANNABLE);
        }

        mTextView.postInvalidate();
    }

    public void onDraw(@NonNull View view, @NonNull Canvas canvas, Paint paint) {
        if (isTextGradientColorsEnable()) {
            int[] textGradientColors;
            if (mTextGradientOrientation == GRADIENT_ORIENTATION_HORIZONTAL &&
                getLayoutDirectionByContext(view.getContext()) == View.LAYOUT_DIRECTION_RTL) {
                textGradientColors = reverseArray(mTextGradientColors);
            } else {
                textGradientColors = mTextGradientColors;
            }
            LinearGradient linearGradient = getOrCreateLinearGradient(view, canvas, mTextGradientOrientation, textGradientColors);
            paint.setShader(linearGradient);
        } else {
            Shader shader = paint.getShader();
            if (shader instanceof LinearGradient) {
                paint.setShader(null);
            }
        }
    }

     /**
     * Get or create a cached LinearGradient object.
     * Only allocates a new LinearGradient when the inputs change.
     */
    private LinearGradient getOrCreateLinearGradient(@NonNull View view, @NonNull Canvas canvas,
                                                    int textGradientOrientation,
                                                    @Nullable int[] textGradientColors) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int paddingLeft = view.getPaddingLeft();
        int paddingTop = view.getPaddingTop();
        int paddingEnd = view.getPaddingEnd();
        int paddingBottom = view.getPaddingBottom();
        int colorsHash = java.util.Arrays.hashCode(textGradientColors);

        if (mCachedTextGradient != null
                && mCachedGradientCanvasWidth == canvasWidth
                && mCachedGradientCanvasHeight == canvasHeight
                && mCachedGradientPaddingLeft == paddingLeft
                && mCachedGradientPaddingTop == paddingTop
                && mCachedGradientPaddingEnd == paddingEnd
                && mCachedGradientPaddingBottom == paddingBottom
                && mCachedGradientOrientation == textGradientOrientation
                && mCachedGradientColorsHash == colorsHash) {
            return mCachedTextGradient;
        }

        LinearGradient linearGradient;
        if (textGradientOrientation == GRADIENT_ORIENTATION_VERTICAL) {
            linearGradient = new LinearGradient(
                paddingLeft, paddingTop, 0,
                (float) canvasHeight - paddingBottom,
                textGradientColors, null, Shader.TileMode.CLAMP);
        } else {
            linearGradient = new LinearGradient(
                paddingLeft, paddingTop,
                (float) canvasWidth - paddingEnd,
                (float) canvasHeight - paddingBottom,
                textGradientColors, null, Shader.TileMode.CLAMP);
        }

        // Cache the gradient and its key
        mCachedTextGradient = linearGradient;
        mCachedGradientCanvasWidth = canvasWidth;
        mCachedGradientCanvasHeight = canvasHeight;
        mCachedGradientPaddingLeft = paddingLeft;
        mCachedGradientPaddingTop = paddingTop;
        mCachedGradientPaddingEnd = paddingEnd;
        mCachedGradientPaddingBottom = paddingBottom;
        mCachedGradientOrientation = textGradientOrientation;
        mCachedGradientColorsHash = colorsHash;
        return linearGradient;
    }

     /**
     * Get the current layout direction from the Context
     */
    private static int getLayoutDirectionByContext(@Nullable Context context) {
        int layoutDirection;
        Resources resources = null;
        Configuration configuration = null;
        if (context != null) {
            resources = context.getResources();
        }
        if (resources != null) {
            configuration = resources.getConfiguration();
        }
        if (configuration != null) {
            layoutDirection = configuration.getLayoutDirection();
        } else {
            layoutDirection = View.LAYOUT_DIRECTION_LTR;
        }
        return layoutDirection;
    }

     /**
     * Reverse int array
     */
    public static int[] reverseArray(@NonNull int[] originalArray) {
        int length = originalArray.length;
        int[] newArray = new int[length];
        for (int i = 0; i < length; i++) {
            newArray[i] = originalArray[length - 1 - i];
        }
        return newArray;
    }

    public void onStateChanged(int[] state) {
        // Reentrancy guard: setText() inside this method can re-trigger drawableStateChanged()
        // which calls onStateChanged() again, causing StackOverflowError on some devices.
        if (mIsInStateChange) {
            return;
        }
        mIsInStateChange = true;
        try {
        boolean changed = false;

        if (mTextColorStateList != null) {
            int newColor = mTextColorStateList.getColorForState(state, mTextColor);
            if (newColor != mTextColor) {
                mTextColor = newColor;
                mTextView.setTextColor(mTextColor);
                changed = true;
            }
        }

        boolean gradientChanged = false;
        if (mTextGradientStartColorStateList != null || mTextGradientCenterColorStateList != null || mTextGradientEndColorStateList != null) {
            int startColor = mTextGradientStartColorStateList != null ? mTextGradientStartColorStateList.getColorForState(state, 0) : (mTextGradientColors != null && mTextGradientColors.length > 0 ? mTextGradientColors[0] : mTextColor);
            int endColor = mTextGradientEndColorStateList != null ? mTextGradientEndColorStateList.getColorForState(state, 0) : (mTextGradientColors != null && mTextGradientColors.length > 0 ? mTextGradientColors[mTextGradientColors.length - 1] : mTextColor);
            
            if (mTextGradientCenterColorStateList != null) {
                int centerColor = mTextGradientCenterColorStateList.getColorForState(state, 0);
                 mTextGradientColors = new int[] {startColor, centerColor, endColor};
            } else if (mTextGradientColors != null && mTextGradientColors.length == 3 && mTextGradientCenterColorStateList == null) {
                // Keep existing center color if it was set via parsed array but not CSL? 
                // Actually if we are rebuilding, we should be careful. 
                // Simplified: If CSLs are present, use them. If mixed, it's tricky. 
                // For now, assuming if one CSL is set, we rebuild.
                
                // If we have a center CSL, we definitely use 3 colors.
                // If we don't, but we had 3 colors before, we should preserve the concept of 3 colors?
                // Let's rely on the existence of mTextGradientCenterColorStateList to decide 3 vs 2 for state updates.
                // Or check mTextGradientColors length
                 if (mTextGradientColors != null && mTextGradientColors.length == 3) {
                     // We had 3 colors, reuse the middle one if no CSL
                     int centerColor = mTextGradientColors[1];
                     mTextGradientColors = new int[] {startColor, centerColor, endColor};
                 } else {
                     mTextGradientColors = new int[] {startColor, endColor};
                 }
            } else {
                mTextGradientColors = new int[] {startColor, endColor};
            }
            gradientChanged = true;
            changed = true;
            // Invalidate cached gradient when colors change
            mCachedTextGradient = null;
        }

        if (mTextStrokeColorStateList != null) {
             int newColor = mTextStrokeColorStateList.getColorForState(state, mTextStrokeColor);
             if (newColor != mTextStrokeColor) {
                 mTextStrokeColor = newColor;
                 boolean spanUpdated = false;
                 CharSequence text = mTextView.getText();
                 if (text instanceof Spanned) {
                     StrokeFontSpan[] spans = ((Spanned) text).getSpans(0, text.length(), StrokeFontSpan.class);
                     for (StrokeFontSpan span : spans) {
                         span.setTextStrokeColor(newColor);
                         spanUpdated = true;
                     }
                 }
                 
                 if (!spanUpdated) {
                     // If no span found (e.g. first time or text replaced), re-apply
                     mTextView.setText(mTextView.getText());
                 }
                 changed = true;
             }
        }

        if (changed) {
            mTextView.invalidate();
        }
        } finally {
            mIsInStateChange = false;
        }
    }
}