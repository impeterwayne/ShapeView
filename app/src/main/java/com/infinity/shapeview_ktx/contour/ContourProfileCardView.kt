package com.infinity.shapeview_ktx.contour

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.genesys.shape.contour.SizeMode
import com.genesys.shape.drawable.ShapeEffect
import com.genesys.shape.drawable.ShapeType
import com.genesys.shape.layout.ShapeContourLayout
import com.genesys.shape.view.ShapeButton
import com.genesys.shape.view.ShapeImageView
import com.genesys.shape.view.ShapeTextView
import com.infinity.shapeview_ktx.R

/**
 * 1. Profile card demonstrating sibling constraints, SizeMode.AtMost, and automatic shadow padding.
 */
class ContourProfileCardView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : ShapeContourLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var isFollowing = false

    private val avatar = ShapeImageView(context).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        setImageResource(R.drawable.img_thanks)
        shapeDrawableBuilder
            .setType(ShapeType.OVAL)
            .setStrokeSize(2.dip)
            .setStrokeColor(0xFF6366F1.toInt())
            .intoBackground()
    }

    private val nameView = TextView(context).apply {
        setText(R.string.contour_profile_name)
        setTextColor(0xFF0F172A.toInt())
        textSize = 16f
        paint.isFakeBoldText = true
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
    }

    private val badgeView = ShapeTextView(context).apply {
        setText(R.string.contour_profile_badge)
        setTextColor(0xFF4338CA.toInt())
        textSize = 10f
        paint.isFakeBoldText = true
        setPadding(8.dip, 2.dip, 8.dip, 2.dip)
        shapeDrawableBuilder
            .setRadius(10f.dip)
            .setSolidColor(0xFFEEF2FF.toInt())
            .intoBackground()
    }

    private val roleView = TextView(context).apply {
        setText(R.string.contour_profile_role)
        setTextColor(0xFF64748B.toInt())
        textSize = 13f
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
    }

    private val followButton = ShapeButton(context).apply {
        setText(R.string.contour_btn_follow)
        textSize = 13f
        isAllCaps = false
        gravity = Gravity.CENTER
        setPadding(14.dip, 0, 14.dip, 0)
        applyFollowStyle(following = false)
        setOnClickListener {
            isFollowing = !isFollowing
            applyFollowStyle(isFollowing)
            Toast.makeText(context, context.getString(R.string.toast_follow_toggled), Toast.LENGTH_SHORT).show()
        }
    }

    private val bioView = TextView(context).apply {
        setText(R.string.contour_profile_bio)
        setTextColor(0xFF475569.toInt())
        textSize = 13f
        setLineSpacing(0f, 1.2f)
    }

    init {
        setPadding(16.dip, 16.dip, 16.dip, 16.dip)
        contourHeightWrapContent()

        shapeDrawableBuilder
            .setRadius(16f.dip)
            .setSolidColor(0xFFFFFFFF.toInt())
            .setStrokeSize(1.dip)
            .setStrokeColor(0xFFE2E8F0.toInt())
            .addEffect(ShapeEffect.dropShadow(0x1F000000, 12.dip, 0, 0, 4.dip))
            .intoBackground()

        avatar.layoutBy(
            x = leftTo { parent.left() }.widthOf { 52.xdip },
            y = topTo { parent.top() }.heightOf { 52.ydip }
        )

        followButton.layoutBy(
            x = rightTo { parent.right() },
            y = centerVerticallyTo { avatar.centerY() }.heightOf { 36.ydip }
        )

        nameView.layoutBy(
            x = leftTo { avatar.right() + 12.xdip }
                .rightTo(SizeMode.AtMost) { followButton.left() - 56.xdip },
            y = topTo { avatar.top() + 4.ydip }
        )

        badgeView.layoutBy(
            x = leftTo { nameView.right() + 6.xdip },
            y = centerVerticallyTo { nameView.centerY() }
        )

        roleView.layoutBy(
            x = leftTo { nameView.left() }
                .rightTo(SizeMode.AtMost) { followButton.left() - 8.xdip },
            y = topTo { nameView.bottom() + 2.ydip }
        )

        bioView.layoutBy(
            x = matchParentX(),
            y = topTo { avatar.bottom() + 14.ydip }
        )
    }

    private fun ShapeButton.applyFollowStyle(following: Boolean) {
        if (following) {
            setText(R.string.contour_btn_following)
            textColorBuilder.setTextColor(0xFF4F46E5.toInt()).intoTextColor()
            shapeDrawableBuilder
                .setRadius(18f.dip)
                .setSolidColor(0xFFEEF2FF.toInt())
                .setSolidPressedColor(0xFFE0E7FF.toInt())
                .setStrokeSize(1.dip)
                .setStrokeColor(0xFFC7D2FE.toInt())
                .intoBackground()
        } else {
            setText(R.string.contour_btn_follow)
            textColorBuilder.setTextColor(0xFFFFFFFF.toInt()).intoTextColor()
            shapeDrawableBuilder
                .setRadius(18f.dip)
                .setSolidColor(0xFF6366F1.toInt())
                .setSolidPressedColor(0xFF4F46E5.toInt())
                .setStrokeSize(0)
                .intoBackground()
        }
    }
}
