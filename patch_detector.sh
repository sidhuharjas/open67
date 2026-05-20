cat << 'INNER_EOF' > /tmp/detect_patch.txt
    public void startCalibration() {
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
            if (calibrating) {
                long elapsed = System.currentTimeMillis() - calibrationStart;
                if (elapsed < 3000) {
                    return new GestureDetectionResult(false, null, 0, 0.0, 0.0, "Put both hands up! Calibrating in " + (3 - elapsed / 1000) + "s");
                } else {
                    Imgproc.cvtColor(frame, yCrCb, Imgproc.COLOR_BGR2YCrCb);
                    double[] centerLeft = yCrCb.get(frame.rows() / 2, frame.cols() / 4);
                    double[] centerRight = yCrCb.get(frame.rows() / 2, 3 * frame.cols() / 4);
                    
                    if (centerLeft != null && centerRight != null) {
                        double minY = Math.min(centerLeft[0], centerRight[0]) - 40;
                        double maxY = Math.max(centerLeft[0], centerRight[0]) + 40;
                        double minCr = Math.min(centerLeft[1], centerRight[1]) - 30;
                        double maxCr = Math.max(centerLeft[1], centerRight[1]) + 30;
                        double minCb = Math.min(centerLeft[2], centerRight[2]) - 30;
                        double maxCb = Math.max(centerLeft[2], centerRight[2]) + 30;
                        
                        lowerSkin = new Scalar(Math.max(0, minY), Math.max(0, minCr), Math.max(0, minCb));
                        upperSkin = new Scalar(Math.min(255, maxY), Math.min(255, maxCr), Math.min(255, maxCb));
                    }
                    
                    leftMoveCount = 0;
                    rightMoveCount = 0;
                    prevLeftHand = null;
                    prevRightHand = null;
                    calibrating = false;
                    
                    return new GestureDetectionResult(false, null, 0, 0.0, 0.0, "Calibration Complete! Range adapted.");
                }
            }

            Imgproc.cvtColor(frame, yCrCb, Imgproc.COLOR_BGR2YCrCb);
INNER_EOF

# Replace from 'public GestureDetectionResult detect(Mat frame) {' down to 'Imgproc.cvtColor(frame, yCrCb, Imgproc.COLOR_BGR2YCrCb);' using python
python3 -c '
import sys
content = open("src/main/java/com/open67/vision/Gesture67Detector.java").read()
start_idx = content.find("public GestureDetectionResult detect(Mat frame) {")
end_idx = content.find("Imgproc.cvtColor(frame, yCrCb, Imgproc.COLOR_BGR2YCrCb);") + len("Imgproc.cvtColor(frame, yCrCb, Imgproc.COLOR_BGR2YCrCb);")

patch = open("/tmp/detect_patch.txt").read()
new_content = content[:start_idx] + patch + content[end_idx:]
open("src/main/java/com/open67/vision/Gesture67Detector.java", "w").write(new_content)
'
