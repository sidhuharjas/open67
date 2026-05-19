package com.open67.capture;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public final class SyntheticFrameSource implements FrameSource {

    private long frameIndex;

    @Override
    public Mat capture() {
        int width = 960;
        int height = 540;
        Mat frame = Mat.zeros(height, width, CvType.CV_8UC3);

        double wave = Math.sin(frameIndex * 0.12);
        int offsetX = (int) Math.round(40 * wave);
        int offsetY = (int) Math.round(18 * Math.cos(frameIndex * 0.08));
        Scalar skin = new Scalar(102, 148, 202);

        Point palmTopLeft = new Point(360 + offsetX, 220 + offsetY);
        Point palmBottomRight = new Point(570 + offsetX, 405 + offsetY);
        Imgproc.rectangle(frame, palmTopLeft, palmBottomRight, skin, -1);

        int fingerWidth = 34;
        int fingerHeight = 120;
        int fingerGap = 10;
        int fingerBaseX = 375 + offsetX;
        int fingerTopY = 120 + offsetY;

        for (int finger = 0; finger < 5; finger++) {
            int x = fingerBaseX + finger * (fingerWidth + fingerGap);
            int y = fingerTopY + (finger == 0 ? 16 : 0) + (finger == 4 ? 8 : 0);
            int heightAdjustment = finger == 1 ? 12 : finger == 3 ? 10 : 0;
            Imgproc.rectangle(frame, new Point(x, y + heightAdjustment),
                    new Point(x + fingerWidth, y + fingerHeight), skin, -1);
        }

        Imgproc.circle(frame, new Point(420 + offsetX, 430 + offsetY), 28, skin, -1);
        Imgproc.circle(frame, new Point(525 + offsetX, 430 + offsetY), 28, skin, -1);

        Imgproc.putText(frame, "Synthetic fallback feed", new Point(24, 48),
                Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(240, 240, 240), 2);
        Imgproc.putText(frame, "Hold your webcam up to the camera for live input", new Point(24, 84),
                Imgproc.FONT_HERSHEY_SIMPLEX, 0.65, new Scalar(220, 220, 220), 1);

        frameIndex++;
        return frame;
    }

    @Override
    public String description() {
        return "Synthetic fallback";
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() {
    }
}