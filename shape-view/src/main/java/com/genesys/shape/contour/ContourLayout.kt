/*
 * Copyright 2019 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.genesys.shape.contour

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.genesys.shape.contour.constraints.SizeConfig
import com.genesys.shape.contour.constraints.SizeConfigSmartLambdas.CoordinateAxis.HORIZONTAL
import com.genesys.shape.contour.constraints.SizeConfigSmartLambdas.CoordinateAxis.VERTICAL
import com.genesys.shape.contour.constraints.SizeConfigSmartLambdas.matchParent
import com.genesys.shape.contour.constraints.SizeConfigSmartLambdas.wrapContent
import com.genesys.shape.contour.errors.CircularReferenceDetected
import com.genesys.shape.contour.solvers.AxisSolver
import com.genesys.shape.contour.solvers.ComparisonResolver
import com.genesys.shape.contour.solvers.ComparisonResolver.CompareBy.MaxOf
import com.genesys.shape.contour.solvers.ComparisonResolver.CompareBy.MinOf
import com.genesys.shape.contour.solvers.SimpleAxisSolver
import com.genesys.shape.contour.solvers.SimpleAxisSolver.Point.Baseline
import com.genesys.shape.contour.solvers.SimpleAxisSolver.Point.Max
import com.genesys.shape.contour.solvers.SimpleAxisSolver.Point.Mid
import com.genesys.shape.contour.solvers.SimpleAxisSolver.Point.Min
import com.genesys.shape.contour.solvers.XAxisSolver
import com.genesys.shape.contour.solvers.YAxisSolver
import com.genesys.shape.contour.utils.toXInt
import com.genesys.shape.contour.utils.toYInt
import com.genesys.shape.contour.utils.unwrapXIntLambda
import com.genesys.shape.contour.utils.unwrapXIntToXIntLambda
import com.genesys.shape.contour.utils.unwrapYIntLambda
import com.genesys.shape.contour.utils.unwrapYIntToYIntLambda
import com.genesys.shape.contour.wrappers.HasDimensions
import com.genesys.shape.contour.wrappers.ParentGeometry
import com.genesys.shape.contour.wrappers.ViewDimensions
import kotlin.math.max
import kotlin.math.min

private const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

/**
 * The central class to use when interacting with Contour.
 *
 * To build a custom layout inherit from [ContourLayout] (or [com.genesys.shape.layout.ShapeContourLayout]).
 */
open class ContourLayout(
  context: Context,
  attrs: AttributeSet?,
  defStyleAttr: Int
) : ViewGroup(context, attrs, defStyleAttr) {

  // Using secondary constructors instead of @JvmOverloads ensures that
  // they show up in autocomplete suggestions when extending ContourLayout.
  constructor(context: Context) : this(context, null, 0)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

  /**
   * Whether Contour should respect padding set on this layout as part of laying out its subviews.
   * When set to `true`, padding will influence the alignment of subviews to the layout's inner edges
   * When set to `false`, padding will be ignored in all layout calculations.
   *
   * @see ViewGroup.setPadding
   */
  @Deprecated("Migrate implementation to properly take padding into account or explicitly null it out")
  var respectPadding: Boolean = true

  private val density: Float = context.resources.displayMetrics.density
  private val widthConfig = SizeConfig(lambda = matchParent())
  private val heightConfig = SizeConfig(lambda = matchParent())
  private val geometry = ParentGeometry(
      widthConfig = widthConfig,
      heightConfig = heightConfig,
      paddingConfig = {
        @Suppress("DEPRECATION")
        if (respectPadding) {
          Rect(paddingLeft, paddingTop, paddingRight, paddingBottom)
        } else {
          Rect(0, 0, 0, 0)
        }
      }
  )
  private var constructed: Boolean = true
  private var lastWidthSpec: Int = 0
  private var lastHeightSpec: Int = 0

  override fun requestLayout() {
    if (constructed) {
      invalidateAll()
    }
    super.requestLayout()
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    if (lastWidthSpec != widthMeasureSpec || lastHeightSpec != heightMeasureSpec) {
      invalidateAll()
    }

    widthConfig.available = MeasureSpec.getSize(widthMeasureSpec)
    heightConfig.available = MeasureSpec.getSize(heightMeasureSpec)
    setMeasuredDimension(widthConfig.resolve(), heightConfig.resolve())

    lastWidthSpec = widthMeasureSpec
    lastHeightSpec = heightMeasureSpec
  }

  override fun onLayout(
    changed: Boolean,
    l: Int,
    t: Int,
    r: Int,
    b: Int
  ) {
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      if (child.visibility == GONE) {
        continue
      }

      val params = child.spec()
      params.measureSelf()
      child.layout(
          params.left().value, params.top().value,
          params.right().value, params.bottom().value
      )
    }
  }

  private fun invalidateAll() {
    widthConfig.clear()
    heightConfig.clear()
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      (child.layoutParams as? LayoutSpec)?.clear()
    }
  }

  private inline fun View.spec(): LayoutSpec {
    if (parent !== this@ContourLayout) {
      throw IllegalArgumentException("Referencing view outside of ViewGroup.")
    }
    return layoutParams as LayoutSpec
  }

  private inline fun <T> View.handleCrd(block: () -> T): T {
    return try {
      block()
    } catch (e: CircularReferenceDetected) {
      val trace = Throwable().stackTrace
      val current = trace.getOrNull(0)
      val calledBy = trace.getOrNull(1)
      e.add(CircularReferenceDetected.TraceElement(this, current, calledBy))
      throw e
    }
  }

  // API

  val Int.dip: Int get() = (density * this).toInt()
  val Int.xdip: XInt get() = XInt((density * this).toInt())
  val Int.ydip: YInt get() = YInt((density * this).toInt())

  val Float.dip: Float get() = density * this
  val Float.xdip: XFloat get() = XFloat(density * this)
  val Float.ydip: YFloat get() = YFloat(density * this)

  inline fun Int.toXInt(): XInt = XInt(this)
  inline fun Int.toYInt(): YInt = YInt(this)

  @Deprecated(
      message = "Views should be configured using layoutBy{} instead.",
      replaceWith = ReplaceWith("layoutBy(addToViewGroup, config)")
  )
  fun <T : View> T.contourOf(
    addToViewGroup: Boolean = true,
    config: T.() -> LayoutSpec
  ): T {
    val viewGroup = this@ContourLayout
    val spec = config()
    spec.dimen = ViewDimensions(this)
    spec.parent = viewGroup.geometry
    layoutParams = spec
    if (addToViewGroup) {
      viewGroup.addViewInternal(this)
    }
    return this
  }

  /**
   * Overrides how the [ContourLayout] should size its width by reverting to the default behaviour, which
   * is to take all the available space it is given
   */
  fun contourWidthMatchParent() {
    widthConfig.lambda = matchParent()
  }

  /**
   * Overrides how the [ContourLayout] should size its width. By default [ContourLayout] will take all the available
   * space it is given. This override allows fine grained control over the resulting final measure.
   * @param config a function that takes a [XInt] - which is the available space supplied by the [ContourLayout]'s
   * parent - and returns a [XInt] describing how wide the [ContourLayout] should be.
   *
   * Note: It is acceptable to reference the children views in the [config] so long as circular references are not
   * introduced!
   */
  fun contourWidthOf(config: (available: XInt) -> XInt) {
    widthConfig.lambda = unwrapXIntToXIntLambda(config)
  }

  /**
   * Overrides how the [ContourLayout] should size its width. This override instructs the view to take up
   * as much space as necessary to wrap its subviews, including its own padding.
   */
  fun contourWidthWrapContent() {
    widthConfig.lambda = wrapContent(this, HORIZONTAL)
  }

  /**
   * Overrides how the [ContourLayout] should size its height by reverting to the default behaviour, which
   * is to take all the available space it is given
   */
  fun contourHeightMatchParent() {
    heightConfig.lambda = matchParent()
  }

  /**
   * Overrides how the [ContourLayout] should size its height. By default [ContourLayout] will take all the available
   * space it is given. This override allows fine grained control over the resulting final measure.
   * @param config a function that takes a [YInt] - which is the available space supplied by the [ContourLayout]'s
   * parent - and returns a [YInt] describing how tall the [ContourLayout] should be.
   *
   * Note: It is acceptable to reference the children views in the [config] so long as circular references are not
   * introduced!
   */
  fun contourHeightOf(config: (available: YInt) -> YInt) {
    heightConfig.lambda = unwrapYIntToYIntLambda(config)
  }

  /**
   * Overrides how the [ContourLayout] should size its height. This override instructs the view to take up
   * as much space as necessary to wrap its subviews, including its own padding.
   */
  fun contourHeightWrapContent() {
    heightConfig.lambda = wrapContent(this, VERTICAL)
  }

  /**
   * Describes a View's position and dimensions according to the [x] and [y] layout specs.
   * If needed, the layout spec can later be modified using [updateLayoutBy].
   *
   * @param addToViewGroup if true, this View will be added to the [ContourLayout] if it
   * is not already added.
   */
  fun <T : View> T.layoutBy(
    x: XAxisSolver,
    y: YAxisSolver,
    addToViewGroup: Boolean = true
  ): T {
    val viewGroup = this@ContourLayout
    layoutParams = LayoutSpec(x, y).also {
      it.dimen = ViewDimensions(this)
      it.parent = viewGroup.geometry
      it.view = this
    }
    if (addToViewGroup && parent == null) {
      viewGroup.addViewInternal(this)
    }
    return this
  }

  @Deprecated(
      message = "Views should be configured using layoutBy() instead.",
      replaceWith = ReplaceWith("layoutBy(x, y, addToViewGroup)")
  )
  fun <T : View> T.applyLayout(
    x: XAxisSolver,
    y: YAxisSolver,
    addToViewGroup: Boolean = true
  ) {
    layoutBy(x, y, addToViewGroup)
  }

  @Deprecated(message = "Use layoutBy(x = emptyX(), y = emptyY()) instead.")
  @Suppress("DeprecatedCallableAddReplaceWith")
  fun View.applyEmptyLayout() =
    layoutBy(x = emptyX(), y = emptyY())

  /**
   * Updates the layout configuration of receiver view with new optional [XAxisSolver] and/or [YAxisSolver]
   * @receiver the view to configure
   * @param x configures how the [View] will be positioned and sized on the x-axis.
   * @param y configures how the [View] will be positioned and sized on the y-axis.
   */
  fun View.updateLayoutBy(
    x: XAxisSolver = spec().x,
    y: YAxisSolver = spec().y
  ) {
    val viewGroup = this@ContourLayout
    val spec = LayoutSpec(x, y)
    spec.dimen = ViewDimensions(this)
    spec.parent = viewGroup.geometry
    spec.view = this
    layoutParams = spec
  }

  @Deprecated("Use updateLayoutBy", ReplaceWith("updateLayoutBy(x, y)"))
  fun View.updateLayout(
    x: XAxisSolver = spec().x,
    y: YAxisSolver = spec().y
  ) {
    updateLayoutBy(x, y)
  }

  @Deprecated("Use updateLayoutBy", ReplaceWith("updateLayoutBy(x, y)"))
  fun View.updateLayoutSpec(
    x: XAxisSolver = spec().x,
    y: YAxisSolver = spec().y
  ) {
    updateLayoutBy(x, y)
  }

  /**
   * The left position of the receiver [View]. Guaranteed to return the resolved value or throw.
   * @return the laid-out left position of the [View]
   */
  fun View.left(): XInt = handleCrd { spec().left() }

  /**
   * The top position of the receiver [View]. Guaranteed to return the resolved value or throw.
   * @return the laid-out top position of the [View]
   */
  fun View.top(): YInt = handleCrd { spec().top() }

  /**
   * The right position of the receiver [View]. Guaranteed to return the resolved value or throw.
   * @return the laid-out right position of the [View]
   */
  fun View.right(): XInt = handleCrd { spec().right() }

  /**
   * The bottom position of the receiver [View]. Guaranteed to return the resolved value or throw.
   * @return the laid-out bottom position of the [View]
   */
  fun View.bottom(): YInt = handleCrd { spec().bottom() }

  /**
   * The center-x position of the receiver [View]. Guaranteed to return the resolved value or throw.
   * @return the laid-out left center-x of the [View]
   */
  fun View.centerX(): XInt = handleCrd { spec().centerX() }

  /**
   * The center-y position of the receiver [View]. Guaranteed to return the resolved value or throw.
   * @return the laid-out center-y position of the [View]
   */
  fun View.centerY(): YInt = handleCrd { spec().centerY() }

  /**
   * The baseline position of the receiver [View]. Guaranteed to return the resolved value or throw.
   * @return the laid-out baseline position of the [View]
   */
  fun View.baseline(): YInt = handleCrd { spec().baseline() }

  /**
   * The width of the receiver [View]. Guaranteed to return the resolved value or throw.
   * @return the laid-out width of the [View]
   */
  fun View.width(): XInt = handleCrd { spec().width() }

  /**
   * The height of the receiver [View]. Guaranteed to return the resolved value or throw.
   * @return the laid-out height of the [View]
   */
  fun View.height(): YInt = handleCrd { spec().height() }

  /**
   * The preferred width of the receiver [View] when no constraints are applied to the view.
   * @return the preferred width of the [View]
   */
  fun View.preferredWidth(): XInt = handleCrd { spec().preferredWidth() }

  /**
   * The preferred height of the receiver [View] when no constraints are applied to the view.
   * @return the preferred height of the [View]
   */
  fun View.preferredHeight(): YInt = handleCrd { spec().preferredHeight() }

  fun baselineTo(provider: LayoutContainer.() -> YInt): HeightOfOnlyContext =
    SimpleAxisSolver(
        point = Baseline,
        lambda = unwrapYIntLambda(provider)
    )

  fun topTo(provider: LayoutContainer.() -> YInt): HasTop =
    SimpleAxisSolver(
        point = Min,
        lambda = unwrapYIntLambda(provider)
    )

  fun bottomTo(provider: LayoutContainer.() -> YInt): HasBottom =
    SimpleAxisSolver(
        point = Max,
        lambda = unwrapYIntLambda(provider)
    )

  fun centerVerticallyTo(provider: LayoutContainer.() -> YInt): HeightOfOnlyContext =
    SimpleAxisSolver(
        point = Mid,
        lambda = unwrapYIntLambda(provider)
    )

  fun leftTo(provider: LayoutContainer.() -> XInt): HasLeft =
    SimpleAxisSolver(
        point = Min,
        lambda = unwrapXIntLambda(provider)
    )

  fun rightTo(provider: LayoutContainer.() -> XInt): HasRight =
    SimpleAxisSolver(
        point = Max,
        lambda = unwrapXIntLambda(provider)
    )

  fun centerHorizontallyTo(provider: LayoutContainer.() -> XInt): WidthOfOnlyContext =
    SimpleAxisSolver(
        point = Mid,
        lambda = unwrapXIntLambda(provider)
    )

  /**
   * Matches the position and width to those of [View] on the x-axis.
   */
  fun matchXTo(
    view: View,
    marginLeft: Int = 0,
    marginRight: Int = 0
  ): XAxisSolver {
    return leftTo { view.left() + marginLeft }.rightTo { view.right() - marginRight }
  }

  /**
   * Matches the position and width to those of the parent view on the x-axis.
   */
  fun matchParentX(
    marginLeft: Int = 0,
    marginRight: Int = 0
  ): XAxisSolver {
    return leftTo { parent.left() + marginLeft }.rightTo { parent.right() - marginRight }
  }

  /**
   * Matches the position and height to those of [View] on the y-axis.
   */
  fun matchYTo(
    view: View,
    marginTop: Int = 0,
    marginBottom: Int = 0
  ): YAxisSolver {
    return topTo { view.top() + marginTop }.bottomTo { view.bottom() - marginBottom }
  }

  /**
   * Matches the position and height to those of the parent view on the y-axis.
   */
  fun matchParentY(
    marginTop: Int = 0,
    marginBottom: Int = 0
  ): YAxisSolver {
    return topTo { parent.top() + marginTop }.bottomTo { parent.bottom() - marginBottom }
  }

  /**
   * Assigns zero-width to the view.
   */
  fun emptyX() = leftTo { parent.left() }.widthOf { 0.toXInt() }

  /**
   * Assigns zero-height to the view.
   */
  fun emptyY() = topTo { parent.top() }.heightOf { 0.toYInt() }

  fun minOf(
    a: XInt,
    b: XInt
  ): XInt = min(a.value, b.value).toXInt()

  fun minOf(
    a: YInt,
    b: YInt
  ): YInt = min(a.value, b.value).toYInt()

  fun maxOf(
    a: XInt,
    b: XInt
  ): XInt = max(a.value, b.value).toXInt()

  fun maxOf(
    a: YInt,
    b: YInt
  ): YInt = max(a.value, b.value).toYInt()

  fun minOf(
    p0: HasYPositionWithoutHeight,
    p1: HasYPositionWithoutHeight
  ): YAxisSolver {
    p0 as AxisSolver
    p1 as AxisSolver
    return ComparisonResolver(p0, p1, MinOf)
  }

  fun maxOf(
    p0: HasYPositionWithoutHeight,
    p1: HasYPositionWithoutHeight
  ): YAxisSolver {
    p0 as AxisSolver
    p1 as AxisSolver
    return ComparisonResolver(p0, p1, MaxOf)
  }

  fun minOf(
    p0: HasXPositionWithoutWidth,
    p1: HasXPositionWithoutWidth
  ): XAxisSolver {
    p0 as AxisSolver
    p1 as AxisSolver
    return ComparisonResolver(p0, p1, MinOf)
  }

  fun maxOf(
    p0: HasXPositionWithoutWidth,
    p1: HasXPositionWithoutWidth
  ): XAxisSolver {
    p0 as AxisSolver
    p1 as AxisSolver
    return ComparisonResolver(p0, p1, MaxOf)
  }

  private fun addViewInternal(child: View?) = super.addView(child)

  @Deprecated(ADDVIEW_DEPRECATION_MESSAGE, ReplaceWith(ADDVIEW_DEPRECATION_QUICKFIX))
  override fun addView(child: View) = super.addView(child)

  @Deprecated(ADDVIEW_DEPRECATION_MESSAGE, ReplaceWith(ADDVIEW_DEPRECATION_QUICKFIX))
  override fun addView(child: View, index: Int) = super.addView(child, index)

  @Deprecated(ADDVIEW_DEPRECATION_MESSAGE, ReplaceWith(ADDVIEW_DEPRECATION_QUICKFIX))
  override fun addView(child: View, params: LayoutParams) = super.addView(child, params)

  @Deprecated(ADDVIEW_DEPRECATION_MESSAGE, ReplaceWith(ADDVIEW_DEPRECATION_QUICKFIX))
  override fun addView(child: View, index: Int, params: LayoutParams) =
    super.addView(child, index, params)

  @Deprecated(ADDVIEW_DEPRECATION_MESSAGE, ReplaceWith(ADDVIEW_DEPRECATION_QUICKFIX))
  override fun addView(child: View, width: Int, height: Int) =
    super.addView(child, width, height)

  @Deprecated(ADDVIEW_DEPRECATION_MESSAGE, ReplaceWith(ADDVIEW_DEPRECATION_QUICKFIX))
  override fun addViewInLayout(child: View, index: Int, params: LayoutParams) =
    super.addViewInLayout(child, index, params)

  @Deprecated(ADDVIEW_DEPRECATION_MESSAGE, ReplaceWith(ADDVIEW_DEPRECATION_QUICKFIX))
  override fun addViewInLayout(
    child: View, index: Int, params: LayoutParams, preventRequestLayout: Boolean
  ) = if (child.layoutParams is LayoutSpec)
    super.addViewInLayout(child, index, params, preventRequestLayout)
  else
    throw UnsupportedOperationException(ADDVIEW_DEPRECATION_MESSAGE)

  class LayoutSpec(
    internal val x: XAxisSolver,
    internal val y: YAxisSolver
  ) : ViewGroup.LayoutParams(WRAP, WRAP), LayoutContainer {

    override lateinit var parent: Geometry
    internal lateinit var dimen: HasDimensions
    internal lateinit var view: View

    init {
      x.onAttach(this)
      y.onAttach(this)
    }

    internal fun left(): XInt = x.min().toXInt()
    internal fun right(): XInt = x.max().toXInt()
    internal fun centerX(): XInt = x.mid().toXInt()
    internal fun top(): YInt = y.min().toYInt()
    internal fun bottom(): YInt = y.max().toYInt()
    internal fun centerY(): YInt = y.mid().toYInt()
    internal fun baseline(): YInt = y.baseline().toYInt()
    internal fun width(): XInt = x.range().toXInt()
    internal fun height(): YInt = y.range().toYInt()

    internal fun preferredWidth(): XInt {
      return if (view.visibility == GONE) {
        XInt.ZERO
      } else {
        dimen.measure(0, y.measureSpec())
        dimen.width.toXInt()
      }
    }

    internal fun preferredHeight(): YInt {
      return if (view.visibility == GONE) {
        YInt.ZERO
      } else {
        dimen.measure(x.measureSpec(), 0)
        dimen.height.toYInt()
      }
    }

    internal fun measureSelf() {
      if (view.visibility == GONE) {
        x.onRangeResolved(0, 0)
        y.onRangeResolved(0, 0)
      } else {
        dimen.measure(x.measureSpec(), y.measureSpec())
        x.onRangeResolved(dimen.width, 0)
        y.onRangeResolved(dimen.height, dimen.baseline)
      }
    }

    internal fun clear() {
      x.clear()
      y.clear()
    }
  }

  companion object {
    const val ADDVIEW_DEPRECATION_MESSAGE = "Incorrectly adding view to ContourLayout"
    const val ADDVIEW_DEPRECATION_QUICKFIX = "child.layoutBy(x = matchParentX(), y = matchParentY())"
  }
}
