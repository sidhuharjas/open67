package com.open67.vision;

import org.opencv.core.Rect;

public record GestureDetectionResult(
	boolean detected,
	Rect bounds,
	int defectCount,
	double solidity,
	double contourArea,
	String message) {
}