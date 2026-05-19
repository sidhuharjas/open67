package com.open67.capture;

import org.opencv.core.Mat;

public interface FrameSource extends AutoCloseable {

    Mat capture();

    String description();

    boolean isOpen();

    @Override
    void close();
}