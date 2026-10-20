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

package com.genesys.shape.contour.solvers

import com.genesys.shape.contour.ContourLayout.LayoutSpec

/**
 * [AxisSolver] represents a strategy for solving points on an axis.
 */
interface AxisSolver {

  /**
   * Represents the left or top point of a component in pixels relative to the parent layout's top / left
   * Calling this method may trigger work.
   */
  fun min(): Int

  /**
   * Represents the center x or y point of a component in pixels relative to the parent layout's top / left
   * Calling this method may trigger work.
   */
  fun mid(): Int

  /**
   * Represents the text baseline of a component with text on the y axis relative parent layout's top.
   * Calling this method may trigger work.
   */
  fun baseline(): Int

  /**
   * Represents the right or bottom point of a component in pixels relative to the parent layout's top / left
   * Calling this method may trigger work.
   */
  fun max(): Int

  /**
   * Represents the width or height of a component in pixels. This may be solved without any of the axis point being
   * solved or vice-versa
   * Calling this method may trigger work.
   */
  fun range(): Int

  fun onAttach(parent: LayoutSpec)
  fun onRangeResolved(range: Int, baselineRange: Int)

  fun measureSpec(): Int
  fun clear()
}

interface XAxisSolver : AxisSolver
interface YAxisSolver : AxisSolver
