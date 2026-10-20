package com.infinity.shapeview_ktx.contour

import android.content.Context
import android.transition.TransitionManager
import android.util.AttributeSet
import android.widget.TextView
import com.genesys.shape.contour.SizeMode
import com.genesys.shape.drawable.ShapeEffect
import com.genesys.shape.drawable.ShapeGradientOrientation
import com.genesys.shape.layout.ShapeContourLayout
import com.genesys.shape.view.ShapeTextView
import com.infinity.shapeview_ktx.R

/**
 * 2. Interactive card demonstrating runtime constraint updates via [updateLayoutBy] with gradient & ripple.
 */
class ContourDynamicRelayoutCardView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : ShapeContourLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var expanded = false

    private val titleView = TextView(context).apply {
        setText(R.string.contour_dynamic_title)
        setTextColor(0xFFFFFFFF.toInt())
        textSize = 16f
        paint.isFakeBoldText = true
    }

    private val pillView = ShapeTextView(context).apply {
        setText(R.string.contour_dynamic_pill_compact)
        setTextColor(0xFFFFFFFF.toInt())
        textSize = 11f
        paint.isFakeBoldText = true
        setPadding(10.dip, 4.dip, 10.dip, 4.dip)
        shapeDrawableBuilder
            .setRadius(12f.dip)
            .setSolidColor(0x33FFFFFF)
            .setStrokeSize(1.dip)
            .setStrokeColor(0x55FFFFFF)
            .intoBackground()
    }

    private val descriptionView = TextView(context).apply {
        setText(R.string.contour_dynamic_collapsed)
        setTextColor(0xE6FFFFFF.toInt())
        textSize = 13f
        setLineSpacing(0f, 1.2f)
    }

    init {
        setPadding(18.dip, 18.dip, 18.dip, 18.dip)
        contourHeightWrapContent()

        shapeDrawableBuilder
            .setRadius(16f.dip)
            .setSolidGradientColors(0xFF4F46E5.toInt(), 0xFF9333EA.toInt())
            .setSolidGradientOrientation(ShapeGradientOrientation.TOP_LEFT_TO_BOTTOM_RIGHT)
            .addEffect(ShapeEffect.dropShadow(0x334F46E5, 14.dip, 0, 0, 6.dip))
            .intoBackground()

        rippleBuilder.isRippleEnabled = true
        rippleBuilder.rippleColor = 0x30FFFFFF

        titleView.layoutBy(
            x = leftTo { parent.left() }.rightTo(SizeMode.AtMost) { pillView.left() - 8.xdip },
            y = topTo { parent.top() }
        )

        pillView.layoutBy(
            x = rightTo { parent.right() },
            y = centerVerticallyTo { titleView.centerY() }
        )

        descriptionView.layoutBy(
            x = matchParentX(),
            y = topTo { titleView.bottom() + 10.ydip }
        )

        setOnClickListener {
            expanded = !expanded
            TransitionManager.beginDelayedTransition(this)
            if (expanded) {
                pillView.setText(R.string.contour_dynamic_pill_expanded)
                descriptionView.setText(R.string.contour_dynamic_expanded)
                pillView.updateLayoutBy(
                    x = rightTo { parent.right() },
                    y = topTo { descriptionView.bottom() + 14.ydip }
                )
            } else {
                pillView.setText(R.string.contour_dynamic_pill_compact)
                descriptionView.setText(R.string.contour_dynamic_collapsed)
                pillView.updateLayoutBy(
                    x = rightTo { parent.right() },
                    y = centerVerticallyTo { titleView.centerY() }
                )
            }
            requestLayout()
        }
    }
}
