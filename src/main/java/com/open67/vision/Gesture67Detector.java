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

    private Scalar lowerSkin = new Scalar(133, 77, 0);
    private Scalar upperSkin = new Scalar(173, 127, 255);
    private static final double MIN_CONTOUR_AREA = 3500.0;
    private static final double MIN_SOL_CONFIDENCE = 0.40; // 40 percent sure

    private Point prevLeftHand = null;
    private Point prevRightHand = null;
    private boolean calibrating = false;
    private long calibrationStart = 0;

    private int leftMoveCount = 0;
    private int rightMoveCount = 0;

    public synchronized void startCalibration() {
        calibrating = true;
        calibrationStart = System.currentTimeMillis();
    }

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
            Core.inRange(yCrCb, lowerSkin, upperSkin, skinMask);
            Imgproc.GaussianBlur(skinMask, blurred, new Size(7, 7), 0);
            Imgproc.morphologyEx(blurred, blurred, Imgproc.MORPH_OPEN, morphKernel);
            Imgproc.morphologyEx(blurred, blurred, Imgproc.MORPH_CLOSE, morphKernel);

            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(blurred, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            if (contours.isEmpty()) {
                return new GestureDetectionResult(false, null, 0, 0.0, 0.0, "No hand contour");
            }

            List<MatOfPoint> validContours = new ArrayList<>();
            for (MatOfPoint c : contours) {
                if (Imgproc.contourArea(c) >= MIN_CONTOUR_AREA && computeSolidity(c) >= MIN_SOL_CONFIDENCE) {
                    validContours.add(c);
                }
            }

            validContours.sort((c1, c2) -> Double.compare(Imgproc.contourArea(c2), Imgproc.contourArea(c1)));

            if (validContours.isEmpty()) {
                return new GestureDetectionResult(false, null, 0, 0.0, 0.0, "Waiting for hands (solidity > 40%)");
            }

            double moveThreshold = 20.0;
            double totalArea = 0;
            Rect combinedBounds = null;

            Point currentLeft = null;
            Point currentRight = null;

            if (validContours.size() >= 2) {
                MatOfPoint hand1 = validContours.get(0);
                MatOfPoint hand2 = validContours.get(1);

                Rect b1 = Imgproc.boundingRect(hand1);
                Rect b2 = Imgproc.boundingRect(hand2);

                Rect leftBounds = b1.x < b2.x ? b1 : b2;
                Rect rightBounds = b1.x < b2.x ? b2 : b1;

                currentLeft = new Point(leftBounds.x + leftBounds.width / 2.0, leftBounds.y + leftBounds.height / 2.0);
                currentRight = new Point(rightBounds.x + rightBounds.width / 2.0, rightBounds.y + rightBounds.height / 2.0);

                totalArea = Imgproc.contourArea(hand1) + Imgproc.contourArea(hand2);

                combinedBounds = new Rect(
                    Math.min(leftBounds.x, rightBounds.x),
                    Math.min(leftBounds.y, rightBounds.y),
                    Math.max(leftBounds.x + leftBounds.width, rightBounds.x + rightBounds.width) - Math.min(leftBounds.x, rightBounds.x),
                    Math.max(leftBounds.y + leftBounds.height, rightBounds.y + rightBounds.height) - Math.min(leftBounds.y, rightBounds.y)
                );
            } else {
                MatOfPoint hand = validContours.get(0);
                Rect b = Imgproc.boundingRect(hand);
                Point center = new Point(b.x + b.width / 2.0, b.y + b.height / 2.0);
                combinedBounds = b.clone();
                totalArea = Imgproc.contourArea(hand);

                // Assign to left or right based on frame half
                if (center.x < frame.cols() / 2.0) {
                    currentLeft = center;
                } else {
                    currentRight = center;
                }
            }

            // check movement left
            if (currentLeft != null) {
                if (prevLeftHand != null) {
                    if (Math.abs(currentLeft.y - prevLeftHand.y) > moveThreshold) {
                        leftMoveCount++;
                        prevLeftHand = currentLeft;
                    }
                } else {
                    prevLeftHand = currentLeft;
                }
            }

            // check movement right
            if (currentRight != null) {
                if (prevRightHand != null) {
                    if (Math.abs(currentRight.y - prevRightHand.y) > moveThreshold) {
                        rightMoveCount++;
                        prevRightHand = currentRight;
                    }
                } else {
                    prevRightHand = currentRight;
                }
            }

            int totalMoves = leftMoveCount + rightMoveCount;
            // Both hands don't have to be detected, just start counting!
            boolean detected = totalMoves > 0;
            String message = String.format("Hands Moving | L: %d | R: %d | Total: %d", leftMoveCount, rightMoveCount, totalMoves);

            return new GestureDetectionResult(detected, combinedBounds, totalMoves, 1.0, totalArea, message);

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

    private double distance(Point first, Point second) {
        double deltaX = first.x - second.x;
        double deltaY = first.y - second.y;
        return Math.hypot(deltaX, deltaY);
    }
}
