package com.open67.vision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Gesture67Detector {

    private static final Scalar LOWER_SKIN = new Scalar(133, 77, 0);
    private static final Scalar UPPER_SKIN = new Scalar(173, 127, 255);
    private static final double MIN_CONTOUR_AREA = 3500.0;
    private static final double MIN_DEFECT_DEPTH = 12.0;
    private static final double MAX_SOLIDITY = 0.88;

    public GestureDetectionResult detect(Mat frame) {
        if (frame == null || frame.empty()) {
            return new GestureDetectionResult(false, null, 0, 0.0, 0.0, "No frame");
        }

        Mat yCrCb = new Mat();
        Mat skinMask = new Mat();
        Mat blurred = new Mat();
        Mat hierarchy = new Mat();
        Mat morphKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7, 7));

        try {
            Imgproc.cvtColor(frame, yCrCb, Imgproc.COLOR_BGR2YCrCb);
            Core.inRange(yCrCb, LOWER_SKIN, UPPER_SKIN, skinMask);
            Imgproc.GaussianBlur(skinMask, blurred, new Size(7, 7), 0);
            Imgproc.morphologyEx(blurred, blurred, Imgproc.MORPH_OPEN, morphKernel);
            Imgproc.morphologyEx(blurred, blurred, Imgproc.MORPH_CLOSE, morphKernel);

            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(blurred, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            if (contours.isEmpty()) {
                return new GestureDetectionResult(false, null, 0, 0.0, 0.0, "No hand contour");
            }

            MatOfPoint contour = contours.stream()
                    .max(Comparator.comparingDouble(Imgproc::contourArea))
                    .orElse(contours.get(0));

            double area = Imgproc.contourArea(contour);
            if (area < MIN_CONTOUR_AREA) {
                return new GestureDetectionResult(false, Imgproc.boundingRect(contour), 0, 0.0, area, "Hand too small");
            }

            Rect bounds = Imgproc.boundingRect(contour);
            double solidity = computeSolidity(contour);
            int defectCount = countDefects(contour);

                boolean detected = defectCount >= 2 && defectCount <= 4 && solidity <= MAX_SOLIDITY;
                String message = detected
                    ? "Gesture 67 detected"
                    : String.format("Contour ready | defects=%d | solidity=%.2f", defectCount, solidity);

                return new GestureDetectionResult(detected, bounds, defectCount, solidity, area, message);
        } finally {
            yCrCb.release();
            skinMask.release();
            blurred.release();
            hierarchy.release();
            morphKernel.release();
        }
    }

    private double computeSolidity(MatOfPoint contour) {
        MatOfInt hullIndices = new MatOfInt();
        MatOfPoint hullPoints = new MatOfPoint();
        try {
            Imgproc.convexHull(contour, hullIndices);
            int[] indices = hullIndices.toArray();
            Point[] contourPoints = contour.toArray();
            Point[] hull = new Point[indices.length];
            for (int i = 0; i < indices.length; i++) {
                hull[i] = contourPoints[indices[i]];
            }
            hullPoints.fromArray(hull);

            double area = Imgproc.contourArea(contour);
            double hullArea = Imgproc.contourArea(hullPoints);
            if (hullArea <= 0.0) {
                return 1.0;
            }
            return area / hullArea;
        } finally {
            hullIndices.release();
            hullPoints.release();
        }
    }

    private int countDefects(MatOfPoint contour) {
        MatOfInt hull = new MatOfInt();
        MatOfInt4 defects = new MatOfInt4();
        try {
            Imgproc.convexHull(contour, hull, false);
            if (hull.total() < 4) {
                return 0;
            }

            Imgproc.convexityDefects(contour, hull, defects);
            int[] defectData = defects.toArray();
            if (defectData.length == 0) {
                return 0;
            }

            Point[] contourPoints = contour.toArray();
            int count = 0;
            for (int index = 0; index + 3 < defectData.length; index += 4) {
                int startIndex = defectData[index];
                int endIndex = defectData[index + 1];
                int farIndex = defectData[index + 2];
                double depth = defectData[index + 3] / 256.0;

                if (depth < MIN_DEFECT_DEPTH) {
                    continue;
                }

                Point start = contourPoints[startIndex];
                Point end = contourPoints[endIndex];
                Point far = contourPoints[farIndex];
                double angle = angleBetween(start, far, end);
                if (angle < 100.0) {
                    count++;
                }
            }

            return count;
        } finally {
            hull.release();
            defects.release();
        }
    }

    private double angleBetween(Point start, Point far, Point end) {
        double a = distance(far, end);
        double b = distance(start, end);
        double c = distance(start, far);

        double denominator = 2.0 * a * c;
        if (denominator == 0.0) {
            return 180.0;
        }

        double cosValue = (a * a + c * c - b * b) / denominator;
        cosValue = Math.max(-1.0, Math.min(1.0, cosValue));
        return Math.toDegrees(Math.acos(cosValue));
    }

    private double distance(Point first, Point second) {
        double deltaX = first.x - second.x;
        double deltaY = first.y - second.y;
        return Math.hypot(deltaX, deltaY);
    }
}