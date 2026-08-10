package com.genesys.shape.config;

/**
 * TextColor View attribute collection interface
 */
public interface ITextColorStyleable {

    int getTextColorStyleable();

    int getTextPressedColorStyleable();

    default int getTextCheckedColorStyleable() {
        return -1;
    }

    int getTextDisabledColorStyleable();

    int getTextFocusedColorStyleable();

    int getTextSelectedColorStyleable();

    int getTextStartColorStyleable();

    int getTextCenterColorStyleable();

    int getTextEndColorStyleable();

    int getTextGradientOrientationStyleable();

    int getTextStrokeColorStyleable();

    int getTextStrokeSizeStyleable();
}