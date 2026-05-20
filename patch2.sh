sed -i '/private Point prevRightHand = null;/a\    private boolean calibrating = false;\n    private long calibrationStart = 0;' src/main/java/com/open67/vision/Gesture67Detector.java
