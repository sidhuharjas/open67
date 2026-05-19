package com.open67.web;

import com.open67.vision.Gesture67Detector;
import com.open67.vision.GestureDetectionResult;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.time.Duration;

final class GestureSession {

    private static final long COUNT_DEBOUNCE_MS = 250L;
    private static final int REQUIRED_CONSECUTIVE = 2;
    private static final int COUNTDOWN_SECONDS = 3;

    private enum State {
        IDLE, COUNTDOWN, ACTIVE, TIMING, COMPLETE
    }

    private int targetCount = 3;
    private int timerSeconds = 10;
    private int gestureCount;
    private int detectionStreak;
    private long lastCountedAtMs;
    private State state = State.IDLE;
    private long countdownEndAtMs = -1L;
    private long timerEndAtMs = -1L;
    private String lastMessage = "Ready";

    synchronized void setConfiguration(int targetCount, int timerSeconds) {
        this.targetCount = Math.max(1, targetCount);
        this.timerSeconds = Math.max(1, timerSeconds);
    }

    synchronized int targetCount() {
        return targetCount;
    }

    synchronized int timerSeconds() {
        return timerSeconds;
    }

    synchronized void reset() {
        gestureCount = 0;
        lastCountedAtMs = 0L;
        timerEndAtMs = -1L;
        countdownEndAtMs = -1L;
        state = State.IDLE;
        lastMessage = "Reset";
    }

    synchronized void startCountdown() {
        if (state == State.IDLE || state == State.COMPLETE) {
            long now = System.currentTimeMillis();
            countdownEndAtMs = now + COUNTDOWN_SECONDS * 1000L;
            state = State.COUNTDOWN;
            gestureCount = 0;
            detectionStreak = 0;
            lastMessage = "Countdown";
        }
    }

    synchronized GestureDetectionResultView processFrame(Mat frame, Gesture67Detector detector) {
        GestureDetectionResult detectionResult = detector.detect(frame);
        long now = System.currentTimeMillis();
        String stateName = state.name();

        // COUNTDOWN: wait until countdown expires, show remaining time
        if (state == State.COUNTDOWN) {
            if (now >= countdownEndAtMs) {
                state = State.ACTIVE;
                lastMessage = "Go";
            } else {
                long leftMs = countdownEndAtMs - now;
                lastMessage = "Countdown";
                return new GestureDetectionResultView(
                        detectionResult.detected(),
                        detectionResult.bounds(),
                        detectionResult.defectCount(),
                        (double) (detectionResult instanceof com.open67.vision.GestureDetectionResult ? ((com.open67.vision.GestureDetectionResult) detectionResult).solidity() : 0.0),
                        (double) (detectionResult instanceof com.open67.vision.GestureDetectionResult ? ((com.open67.vision.GestureDetectionResult) detectionResult).contourArea() : 0.0),
                        detectionResult.message(),
                        detectionStreak,
                        gestureCount,
                        targetCount,
                        timerSeconds,
                        timerEndAtMs > 0L,
                        Math.max(0L, timerRemainingMillis(now)),
                        timerEndAtMs > 0L && timerRemainingMillis(now) <= 0L,
                        state.name(),
                        lastMessage
                );
            }
        }

        boolean gestureDetected = detectionResult.detected();

        if (gestureDetected) {
            detectionStreak++;
        } else {
            detectionStreak = 0;
        }

        // Only count when active, we have enough consecutive positive frames and debounce time passed
        if (state == State.ACTIVE && detectionStreak >= REQUIRED_CONSECUTIVE && now - lastCountedAtMs >= COUNT_DEBOUNCE_MS) {
            gestureCount++;
            lastCountedAtMs = now;
            detectionStreak = 0; // reset streak to avoid immediate double-counting

            if (gestureCount >= targetCount && timerEndAtMs < 0L) {
                timerEndAtMs = now + Duration.ofSeconds(timerSeconds).toMillis();
                state = State.TIMING;
            }
        }

        // TIMING: check for completion
        if (state == State.TIMING) {
            if (timerRemainingMillis(now) <= 0L) {
                state = State.COMPLETE;
                lastMessage = "Timer complete";
            }
        }

        lastMessage = detectionResult.message();

        long remainingMs = timerRemainingMillis(now);
        return new GestureDetectionResultView(
                detectionResult.detected(),
                detectionResult.bounds(),
                detectionResult.defectCount(),
                (double) (detectionResult instanceof com.open67.vision.GestureDetectionResult ? ((com.open67.vision.GestureDetectionResult) detectionResult).solidity() : 0.0),
                (double) (detectionResult instanceof com.open67.vision.GestureDetectionResult ? ((com.open67.vision.GestureDetectionResult) detectionResult).contourArea() : 0.0),
                detectionResult.message(),
                detectionStreak,
                gestureCount,
                targetCount,
                timerSeconds,
                timerEndAtMs > 0L,
                Math.max(0L, remainingMs),
                timerEndAtMs > 0L && remainingMs <= 0L,
                state.name(),
                lastMessage
        );
    }

    synchronized GestureDetectionResultView emptyResult(String message) {
        lastMessage = message;
        long now = System.currentTimeMillis();
        long remainingMs = timerRemainingMillis(now);
        return new GestureDetectionResultView(
                false,
                null,
                0,
                0.0,
                0.0,
                message,
                detectionStreak,
                gestureCount,
                targetCount,
                timerSeconds,
                timerEndAtMs > 0L,
                Math.max(0L, remainingMs),
                timerEndAtMs > 0L && remainingMs <= 0L,
            state.name(),
            lastMessage
        );
    }

    synchronized String toJson() {
        return toJson(emptyResult(lastMessage));
    }

    synchronized String toJson(GestureDetectionResultView result) {
        String boundsJson = result.bounds() == null ? "null" : "{"
                + "\"x\":" + result.bounds().x + ","
                + "\"y\":" + result.bounds().y + ","
                + "\"width\":" + result.bounds().width + ","
                + "\"height\":" + result.bounds().height
                + "}";

        return "{"
                + "\"detected\":" + result.detected() + ","
                + "\"bounds\":" + boundsJson + ","
                + "\"defectCount\":" + result.defectCount() + ","
                + "\"message\":\"" + escape(result.message()) + "\"," 
                + "\"solidity\":" + result.solidity() + ","
                + "\"contourArea\":" + result.contourArea() + ","
                + "\"detectionCount\":" + result.detectionCount() + ","
                + "\"gestureCount\":" + result.gestureCount() + ","
                + "\"targetCount\":" + result.targetCount() + ","
                + "\"timerSeconds\":" + result.timerSeconds() + ","
                + "\"timerActive\":" + result.timerActive() + ","
                + "\"timerRemainingMillis\":" + result.timerRemainingMillis() + ","
                + "\"timerComplete\":" + result.timerComplete() + ","
                + "\"state\":\"" + escape(result.state()) + "\"," 
                + "\"lastMessage\":\"" + escape(result.lastMessage()) + "\""
                + "}";
    }

    private long timerRemainingMillis(long now) {
        if (timerEndAtMs < 0L) {
            return 0L;
        }

        return timerEndAtMs - now;
    }

    private String escape(String value) {
        return value == null
                ? ""
                : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}