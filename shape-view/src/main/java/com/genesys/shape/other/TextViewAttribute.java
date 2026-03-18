package com.genesys.shape.other;

import android.widget.TextView;
import com.genesys.shape.config.ITextViewAttribute;

/**
 *    author : Android Wheel
 *    github : https://github.com/getActivity/ShapeView
 *    time   : 2024/09/15
 *    desc   : Get text control attributes
 */
public class TextViewAttribute implements ITextViewAttribute {

    private final TextView mTextView;

    public TextViewAttribute(TextView textView) {
        mTextView = textView;
    }

    @Override
    public int getLayoutDirection() {
        return mTextView.getLayoutDirection();
    }

    @Override
    public int getTextGravity() {
        return mTextView.getGravity();
    }

    @Override
    public int getPaddingLeft() {
        return mTextView.getPaddingLeft();
    }

    @Override
    public int getPaddingRight() {
        return mTextView.getPaddingRight();
    }
}
