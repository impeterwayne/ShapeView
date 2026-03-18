package com.genesys.shape.drawable;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeDrawable
 *    time   : 2021/08/15
 *    desc   : Shape Gradient Orientation
 */
public enum ShapeGradientOrientation {

    /** Draw gradient from left to right (0 degrees) */
    LEFT_TO_RIGHT,
    START_TO_END,

    /** Draw gradient from right to left (180 degrees) */
    RIGHT_TO_LEFT,
    END_TO_START,

    /** Draw gradient from bottom to top (90 degrees) */
    BOTTOM_TO_TOP,

    /** Draw gradient from top to bottom (270 degrees) */
    TOP_TO_BOTTOM,

    // ------------------------------ //

    /** Draw gradient from top-left to bottom-right (315 degrees) */
    TOP_LEFT_TO_BOTTOM_RIGHT,
    TOP_START_TO_BOTTOM_END,

    /** Draw gradient from top-right to bottom-left (225 degrees) */
    TOP_RIGHT_TO_BOTTOM_LEFT,
    TOP_END_TO_BOTTOM_START,

    /** Draw gradient from bottom-left to top-right (45 degrees) */
    BOTTOM_LEFT_TO_TOP_RIGHT,
    BOTTOM_START_TO_TOP_END,

    /** Draw gradient from bottom-right to top-left (135 degrees) */
    BOTTOM_RIGHT_TO_TOP_LEFT,
    BOTTOM_END_TO_TOP_START
}