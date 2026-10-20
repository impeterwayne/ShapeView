package com.genesys.shape.layout

import android.content.Context
import android.util.AttributeSet
import com.genesys.shape.R
import com.genesys.shape.builder.RippleBuilder
import com.genesys.shape.builder.ShapeDrawableBuilder
import com.genesys.shape.config.IGetShapeDrawableBuilder
import com.genesys.shape.contour.ContourLayout
import com.genesys.shape.styleable.ShapeContourLayoutStyleable

/**
 * ContourLayout that supports direct definition of Shape background
 */
open class ShapeContourLayout(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : ContourLayout(context, attrs, defStyleAttr), IGetShapeDrawableBuilder {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val mShapeDrawableBuilder: ShapeDrawableBuilder
    val rippleBuilder: RippleBuilder

    init {
        if (attrs == null && defStyleAttr == 0) {
            mShapeDrawableBuilder = ShapeDrawableBuilder(this)
            rippleBuilder = RippleBuilder(this, mShapeDrawableBuilder)
        } else {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShapeContourLayout, defStyleAttr, 0)
            mShapeDrawableBuilder = ShapeDrawableBuilder(this, typedArray, STYLEABLE)
            rippleBuilder = RippleBuilder(
                this,
                mShapeDrawableBuilder,
                typedArray,
                R.styleable.ShapeContourLayout_shape_ripple_enabled,
                R.styleable.ShapeContourLayout_shape_ripple_color,
                R.styleable.ShapeContourLayout_shape_ripple_radius
            )
            typedArray.recycle()

            mShapeDrawableBuilder.intoBackground()
            rippleBuilder.apply()
        }
    }

    override fun getShapeDrawableBuilder(): ShapeDrawableBuilder {
        return mShapeDrawableBuilder
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        @Suppress("SENSELESS_COMPARISON")
        if (mShapeDrawableBuilder != null && !mShapeDrawableBuilder.isApplyingShadowPadding) {
            mShapeDrawableBuilder.onUserPaddingChanged()
        }
    }

    companion object {
        private val STYLEABLE = ShapeContourLayoutStyleable()
    }
}
