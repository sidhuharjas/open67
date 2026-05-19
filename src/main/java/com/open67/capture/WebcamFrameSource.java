package com.open67.capture;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public final class WebcamFrameSource implements FrameSource {

    private final VideoCapture capture;
    private final String description;

    public WebcamFrameSource(int cameraIndex) {
        this.capture = new VideoCapture();
        this.capture.open(cameraIndex);
        this.description = "Webcam " + cameraIndex;
    }

    @Override
    public Mat capture() {
        Mat frame = new Mat();
        if (!capture.isOpened()) {
            return frame;
        }

        boolean read = capture.read(frame);
        if (!read) {
            frame.release();
            return new Mat();
        }

        return frame;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public boolean isOpen() {
        return capture.isOpened();
    }

    @Override
    public void close() {
        if (capture.isOpened()) {
            capture.release();
        }
    }
}