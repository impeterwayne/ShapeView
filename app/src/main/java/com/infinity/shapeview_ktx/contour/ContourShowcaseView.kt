package com.infinity.shapeview_ktx.contour

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.genesys.shape.layout.ShapeContourLayout
import com.infinity.shapeview_ktx.R

/**
 * Root showcase container built 100% with [ShapeContourLayout] — zero XML inflation.
 */
class ContourShowcaseView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : ShapeContourLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val titleView = TextView(context).apply {
        setText(R.string.title_contour)
        setTextColor(0xFF0F172A.toInt())
        textSize = 22f
        paint.isFakeBoldText = true
    }

    private val subtitleView = TextView(context).apply {
        setText(R.string.contour_subtitle)
        setTextColor(0xFF64748B.toInt())
        textSize = 14f
    }

    private val section1Header = createSectionHeader(R.string.section_contour_profile)
    private val profileCard = ContourProfileCardView(context)

    private val section2Header = createSectionHeader(R.string.section_contour_dynamic)
    private val dynamicCard = ContourDynamicRelayoutCardView(context)

    private val section3Header = createSectionHeader(R.string.section_contour_brutalist)
    private val brutalistCard = ContourBrutalistStatsCardView(context)

    init {
        setPadding(16.dip, 20.dip, 16.dip, 32.dip)
        contourHeightWrapContent()

        titleView.layoutBy(
            x = matchParentX(),
            y = topTo { parent.top() }
        )

        subtitleView.layoutBy(
            x = matchParentX(),
            y = topTo { titleView.bottom() + 4.ydip }
        )

        section1Header.layoutBy(
            x = matchParentX(),
            y = topTo { subtitleView.bottom() + 20.ydip }
        )

        profileCard.layoutBy(
            x = matchParentX(),
            y = topTo { section1Header.bottom() + 10.ydip }
        )

        section2Header.layoutBy(
            x = matchParentX(),
            y = topTo { profileCard.bottom() + 20.ydip }
        )

        dynamicCard.layoutBy(
            x = matchParentX(),
            y = topTo { section2Header.bottom() + 10.ydip }
        )

        section3Header.layoutBy(
            x = matchParentX(),
            y = topTo { dynamicCard.bottom() + 20.ydip }
        )

        brutalistCard.layoutBy(
            x = matchParentX(),
            y = topTo { section3Header.bottom() + 10.ydip }
        )
    }

    private fun createSectionHeader(titleRes: Int): TextView {
        return TextView(context).apply {
            setText(titleRes)
            setTextColor(0xFF334155.toInt())
            textSize = 15f
            paint.isFakeBoldText = true
        }
    }
}
