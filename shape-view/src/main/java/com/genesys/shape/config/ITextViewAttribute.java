package com.genesys.shape.config;

/**
 * TextView attribute interface class
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