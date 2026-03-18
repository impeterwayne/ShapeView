package com.genesys.shape.config;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeView
 *    time   : 2024/09/15
 *    desc   : TextView attribute interface class
 */
public interface ITextViewAttribute {

     /**
     * Get current layout direction
     */
    int getLayoutDirection();

     /**
     * Get current text gravity
     */
    int getTextGravity();

     /**
     * Get TextView left padding
     */
    int getPaddingLeft();

     /**
     * Get TextView right padding
     */
    int getPaddingRight();
}