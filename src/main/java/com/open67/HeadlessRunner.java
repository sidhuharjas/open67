package com.open67;

import com.open67.capture.FrameSource;
import com.open67.capture.SyntheticFrameSource;
import com.open67.capture.WebcamFrameSource;
import com.open67.vision.Gesture67Detector;
import com.open67.vision.GestureDetectionResult;
import org.opencv.core.Mat;

public final class HeadlessRunner {

    private static final int TARGET_COUNT = 3;
    private static final int TIMER_SECONDS = 10;
    private static final int MAX_FRAMES = 300;
    private static final long FRAME_DELAY_MS = 33L;
    private static final long COUNT_DEBOUNCE_MS = 850L;

    private final Gesture67Detector detector = new Gesture67Detector();

    public void run() {
        FrameSource frameSource = openFrameSource();
        System.out.println("Open67 running in headless mode");
        System.out.println("Source: " + frameSource.description());
        System.out.println("Target count: " + TARGET_COUNT + ", timer seconds: " + TIMER_SECONDS);

        int gestureCount = 0;
        boolean previousGestureDetected = false;
        long lastCountedAtMs = 0L;
        long timerEndAtMs = -1L;

        try {
            for (int frameIndex = 0; frameIndex < MAX_FRAMES; frameIndex++) {
                Mat frame = frameSource.capture();
                if (frame.empty()) {
                    System.out.println("Frame " + frameIndex + ": no frame available");
                    sleepQuietly(FRAME_DELAY_MS);
                    continue;
                }

                GestureDetectionResult detectionResult = detector.detect(frame);
                long now = System.currentTimeMillis();
                boolean gestureDetected = detectionResult.detected();

                if (gestureDetected && !previousGestureDetected && now - lastCountedAtMs >= COUNT_DEBOUNCE_MS) {
                    gestureCount++;
                    lastCountedAtMs = now;
                    System.out.println("Gesture count: " + gestureCount + " | " + detectionResult.message());

                    if (gestureCount >= TARGET_COUNT && timerEndAtMs < 0L) {
                        timerEndAtMs = now + (TIMER_SECONDS * 1000L);
                        System.out.println("Timer started for " + TIMER_SECONDS + " seconds");
                    }
                }

                previousGestureDetected = gestureDetected;

                if (timerEndAtMs > 0L) {
                    long remainingMs = Math.max(0L, timerEndAtMs - now);
                    long remainingSeconds = (long) Math.ceil(remainingMs / 1000.0);
                    System.out.println("Timer remaining: " + remainingSeconds + "s");

                    if (remainingMs <= 0L) {
                        System.out.println("Timer complete");
                        break;
                    }
                }

                frame.release();
                sleepQuietly(FRAME_DELAY_MS);
            }
        } finally {
            frameSource.close();
        }

        System.out.println("Open67 headless session finished");
    }

    private FrameSource openFrameSource() {
        WebcamFrameSource webcam = new WebcamFrameSource(0);
        if (webcam.isOpen()) {
            return webcam;
        }

        webcam.close();
        return new SyntheticFrameSource();
    }

    private void sleepQuietly(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }
}