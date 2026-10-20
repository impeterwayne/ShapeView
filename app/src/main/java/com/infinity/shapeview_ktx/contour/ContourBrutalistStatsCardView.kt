package com.infinity.shapeview_ktx.contour

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import com.genesys.shape.drawable.ShapeEffect
import com.genesys.shape.layout.ShapeContourLayout
import com.infinity.shapeview_ktx.R

/**
 * 3. Neo-brutalist 3-column stats banner using fractional width solvers and a 0dp hard drop shadow.
 */
class ContourBrutalistStatsCardView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : ShapeContourLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val col1Val = createStatValue(R.string.contour_stat_inflate_val)
    private val col1Label = createStatLabel(R.string.contour_stat_inflate_label)

    private val col2Val = createStatValue(R.string.contour_stat_safety_val)
    private val col2Label = createStatLabel(R.string.contour_stat_safety_label)

    private val col3Val = createStatValue(R.string.contour_stat_effects_val)
    private val col3Label = createStatLabel(R.string.contour_stat_effects_label)

    init {
        setPadding(16.dip, 18.dip, 16.dip, 18.dip)
        contourHeightWrapContent()

        shapeDrawableBuilder
            .setRadius(14f.dip)
            .setSolidColor(0xFFFEF08A.toInt())
            .setStrokeSize(2.dip)
            .setStrokeColor(0xFF000000.toInt())
            .addEffect(ShapeEffect.dropShadow(0xFF000000.toInt(), 0, 0, 5.dip, 5.dip))
            .intoBackground()

        col1Val.layoutBy(
            x = leftTo { parent.left() }.widthOf { (parent.right() - parent.left()) / 3 },
            y = topTo { parent.top() }
        )
        col1Label.layoutBy(
            x = matchXTo(col1Val),
            y = topTo { col1Val.bottom() + 4.ydip }
        )

        col2Val.layoutBy(
            x = leftTo { col1Val.right() }.widthOf { (parent.right() - parent.left()) / 3 },
            y = topTo { parent.top() }
        )
        col2Label.layoutBy(
            x = matchXTo(col2Val),
            y = topTo { col2Val.bottom() + 4.ydip }
        )

        col3Val.layoutBy(
            x = leftTo { col2Val.right() }.rightTo { parent.right() },
            y = topTo { parent.top() }
        )
        col3Label.layoutBy(
            x = matchXTo(col3Val),
            y = topTo { col3Val.bottom() + 4.ydip }
        )
    }

    private fun createStatValue(textRes: Int): TextView {
        return TextView(context).apply {
            setText(textRes)
            setTextColor(0xFF0F172A.toInt())
            textSize = 18f
            paint.isFakeBoldText = true
            gravity = Gravity.CENTER
        }
    }

    private fun createStatLabel(textRes: Int): TextView {
        return TextView(context).apply {
            setText(textRes)
            setTextColor(0xFF334155.toInt())
            textSize = 12f
            gravity = Gravity.CENTER
        }
    }
}
