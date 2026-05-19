package com.open67.web;

import org.opencv.core.Rect;

record GestureDetectionResultView(
        boolean detected,
        Rect bounds,
        int defectCount,
        double solidity,
        double contourArea,
        String message,
        int detectionCount,
        int gestureCount,
        int targetCount,
        int timerSeconds,
        boolean timerActive,
        long timerRemainingMillis,
        boolean timerComplete,
        String state,
        String lastMessage) {
}