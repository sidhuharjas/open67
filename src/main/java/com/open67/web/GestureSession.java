package com.open67.web;

import com.open67.vision.Gesture67Detector;
import com.open67.vision.GestureDetectionResult;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

final class GestureSession {

    private static final long COUNT_DEBOUNCE_MS = 80L;
    private static final int COUNTDOWN_SECONDS = 3;

    private enum State {
        IDLE, COUNTDOWN, ACTIVE, COMPLETE
    }

    private int targetCount = 3;
    private int timerSeconds = 10;
    private int gestureCount;
    private int detectionHits;
    private int detectionStreak;
    private boolean previousDetectedSignal;
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
        detectionHits = 0;
        detectionStreak = 0;
        previousDetectedSignal = false;
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
            detectionStreak = 0;
            previousDetectedSignal = false;
            timerEndAtMs = -1L;
            lastMessage = "Countdown";
        }
    }

    synchronized GestureDetectionResultView processFrame(Mat frame, Gesture67Detector detector) {
        GestureDetectionResult detectionResult = detector.detect(frame);
        return processDetection(
                detectionResult.detected(),
                detectionResult.message(),
                detectionResult.defectCount(),
                detectionResult.solidity(),
                detectionResult.contourArea());
    }

    synchronized GestureDetectionResultView processDetection(boolean detected, String message, int strength, double confidence,
            double area) {
        return updateSession(detected, message, strength, confidence, area);
    }

    private GestureDetectionResultView updateSession(boolean detected, String message, int strength, double confidence,
            double area) {
        long now = System.currentTimeMillis();

        // COUNTDOWN: wait until countdown expires, show remaining time
        if (state == State.COUNTDOWN) {
            if (now >= countdownEndAtMs) {
                state = State.ACTIVE;
                timerEndAtMs = now + timerSeconds * 1000L;
                previousDetectedSignal = false;
                lastMessage = "Go";
            } else {
                lastMessage = "Countdown";
                return buildResult(false, null, strength, confidence, area, "Countdown", now);
            }
        }

        if (detected) {
            detectionStreak++;
        } else {
            detectionStreak = 0;
        }

        // Count on a clean rising edge from the client gesture signal.
        boolean risingEdge = detected && !previousDetectedSignal;
        if (state == State.ACTIVE && risingEdge && now - lastCountedAtMs >= COUNT_DEBOUNCE_MS) {
            gestureCount++;
            detectionHits++;
            lastCountedAtMs = now;
        }

        if (state == State.ACTIVE) {
            if (timerRemainingMillis(now) <= 0L) {
                state = State.COMPLETE;
                lastMessage = "Timer complete";
            }
        }

        previousDetectedSignal = detected;

        if (state == State.ACTIVE) {
            lastMessage = message;
        }

        return buildResult(detected, null, strength, confidence, area, lastMessage, now);
    }

    synchronized GestureDetectionResultView emptyResult(String message) {
        lastMessage = message;
        long now = System.currentTimeMillis();
        return buildResult(false, null, 0, 0.0, 0.0, message, now);
    }

    private GestureDetectionResultView buildResult(boolean detected, Rect bounds, int strength, double confidence,
            double area, String message, long now) {
        long remainingMs = timerRemainingMillis(now);
        return new GestureDetectionResultView(
                detected,
                bounds,
                strength,
                confidence,
                area,
                message,
                detectionHits,
                gestureCount,
                targetCount,
                timerSeconds,
                state == State.ACTIVE && timerEndAtMs > 0L,
                Math.max(0L, remainingMs),
                state == State.COMPLETE,
                state.name(),
                lastMessage);
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