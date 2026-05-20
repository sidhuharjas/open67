package com.open67.ui;

import com.open67.capture.FrameSource;
import com.open67.capture.SyntheticFrameSource;
import com.open67.capture.WebcamFrameSource;
import com.open67.vision.Gesture67Detector;
import com.open67.vision.GestureDetectionResult;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.Timer;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class MainWindow {

    private static final int DEFAULT_TARGET_COUNT = 3;
    private static final int DEFAULT_TIMER_SECONDS = 10;
    private static final int FRAME_PERIOD_MS = 33;
    private static final long COUNT_DEBOUNCE_MS = 850L;

    private final Gesture67Detector detector = new Gesture67Detector();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "open67-capture-loop");
        thread.setDaemon(true);
        return thread;
    });

    private final JFrame frame = new JFrame("Open67 Gesture Counter");
    private final JLabel previewLabel = new JLabel("Camera preview will appear here", SwingConstants.CENTER);
    private final JLabel sourceLabel = new JLabel("Source: idle");
    private final JLabel statusLabel = new JLabel("Status: waiting to start");
    private final JLabel gestureCountLabel = new JLabel("0");
    private final JLabel timerLabel = new JLabel("Idle");
    private final JTextField targetCountField = new JTextField(Integer.toString(DEFAULT_TARGET_COUNT), 6);
    private final JTextField timerSecondsField = new JTextField(Integer.toString(DEFAULT_TIMER_SECONDS), 6);
    private final JButton startButton = new JButton("Start");
    private final JButton calibrateButton = new JButton("Calibrate");
    private final JPanel calibrateOverlay = new JPanel();
    private final JLabel calibrateText = new JLabel("Put both hands up with palms facing camera", SwingConstants.CENTER);
    private final JLabel calibrateCountdown = new JLabel("3", SwingConstants.CENTER);

    private volatile FrameSource frameSource;
    private volatile boolean running;
    private volatile boolean previousGestureDetected;
    private volatile long lastCountedAtMs;
    private volatile int gestureCount;
    private volatile int targetCount = DEFAULT_TARGET_COUNT;
    private volatile int timerSeconds = DEFAULT_TIMER_SECONDS;
    private volatile long timerEndAtMs = -1L;
    private volatile ScheduledFuture<?> captureTask;

    public MainWindow() {
        buildUi();
        wireEvents();
    }

    public void showWindow() {
        frame.setVisible(true);
    }

    private void buildUi() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(16, 16));
        frame.getContentPane().setBackground(new Color(18, 20, 28));
        frame.setMinimumSize(new Dimension(1200, 820));

        previewLabel.setOpaque(true);
        previewLabel.setBackground(Color.BLACK);
        previewLabel.setForeground(Color.WHITE);
        previewLabel.setPreferredSize(new Dimension(960, 540));
        previewLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel sidebar = new JPanel(new GridLayout(0, 1, 12, 12));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        sidebar.setBackground(new Color(18, 20, 28));

        JLabel title = new JLabel("Open67", SwingConstants.LEFT);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JLabel subtitle = new JLabel("Webcam gesture counter with timer trigger");
        subtitle.setForeground(new Color(180, 188, 208));

        sidebar.add(title);
        sidebar.add(subtitle);
        sidebar.add(createInputRow("Gesture target", targetCountField));
        sidebar.add(createInputRow("Timer seconds", timerSecondsField));
        sidebar.add(createMetricCard("Gesture count", gestureCountLabel));
        sidebar.add(createMetricCard("Timer", timerLabel));
        sidebar.add(createMetricCard("Source", sourceLabel));
        sidebar.add(createMetricCard("Status", statusLabel));
        sidebar.add(startButton);
        sidebar.add(calibrateButton);

        startButton.setFocusable(false);
        calibrateButton.setFocusable(false);

        JPanel previewPanel = new JPanel();
        previewPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        previewPanel.setBackground(new Color(18, 20, 28));
        previewPanel.setLayout(new OverlayLayout(previewPanel));

        // configure overlay (hidden by default)
        calibrateOverlay.setOpaque(true);
        calibrateOverlay.setBackground(new Color(0, 0, 0, 180));
        calibrateOverlay.setLayout(new BorderLayout());
        calibrateText.setForeground(Color.WHITE);
        calibrateText.setFont(calibrateText.getFont().deriveFont(Font.BOLD, 18f));
        calibrateCountdown.setForeground(Color.WHITE);
        calibrateCountdown.setFont(calibrateCountdown.getFont().deriveFont(Font.BOLD, 32f));

        JPanel box = new JPanel(new BorderLayout());
        box.setOpaque(false);
        box.add(calibrateText, BorderLayout.CENTER);
        box.add(calibrateCountdown, BorderLayout.SOUTH);
        box.setBorder(BorderFactory.createEmptyBorder(24,24,24,24));

        calibrateOverlay.add(box, BorderLayout.CENTER);
        calibrateOverlay.setVisible(false);

        previewPanel.add(previewLabel);
        previewPanel.add(calibrateOverlay);

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(previewPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private JPanel createInputRow(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(8, 4));
        panel.setBackground(new Color(28, 31, 42));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);

        field.setColumns(8);
        field.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMetricCard(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout(8, 4));
        panel.setBackground(new Color(28, 31, 42));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(180, 188, 208));

        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 20f));

        panel.add(label, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private void wireEvents() {
        startButton.addActionListener(event -> {
            if (running) {
                stopCapture();
            } else {
                startCapture();
            }
        });
        calibrateButton.addActionListener(event -> {
            if (!running) {
                updateStatusOnEdt("Status: start camera first to calibrate");
                return;
            }

            detector.startCalibration();
            updateStatusOnEdt("Status: put both hands up to calibrate");

            // show overlay and run 3s countdown
            SwingUtilities.invokeLater(() -> {
                calibrateCountdown.setText("3");
                calibrateOverlay.setVisible(true);
            });

            final int[] remaining = {3};
            Timer timer = new Timer(1000, null);
            timer.addActionListener(e -> {
                remaining[0] -= 1;
                if (remaining[0] > 0) {
                    SwingUtilities.invokeLater(() -> calibrateCountdown.setText(Integer.toString(remaining[0])));
                } else {
                    timer.stop();
                    SwingUtilities.invokeLater(() -> {
                        calibrateCountdown.setText("Done");
                        calibrateOverlay.setVisible(false);
                        updateStatusOnEdt("Status: calibration complete");
                    });
                }
            });
            timer.setInitialDelay(1000);
            timer.start();
        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent event) {
                stopCapture();
            }
        });
    }

    private void startCapture() {
        int requestedTarget = parsePositiveInt(targetCountField.getText().trim(), DEFAULT_TARGET_COUNT);
        int requestedTimerSeconds = parsePositiveInt(timerSecondsField.getText().trim(), DEFAULT_TIMER_SECONDS);

        targetCount = requestedTarget;
        timerSeconds = requestedTimerSeconds;
        gestureCount = 0;
        previousGestureDetected = false;
        lastCountedAtMs = 0L;
        timerEndAtMs = -1L;
        running = true;

        frameSource = openFrameSource();
        sourceLabel.setText("Source: " + frameSource.description());
        gestureCountLabel.setText("0");
        timerLabel.setText("Idle");
        statusLabel.setText("Status: running");
        startButton.setText("Stop");

        captureTask = executor.scheduleAtFixedRate(this::captureFrame, 0L, FRAME_PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    private FrameSource openFrameSource() {
        WebcamFrameSource webcam = new WebcamFrameSource(0);
        if (webcam.isOpen()) {
            return webcam;
        }

        webcam.close();
        statusLabel.setText("Status: webcam unavailable, using fallback feed");
        return new SyntheticFrameSource();
    }

    private void captureFrame() {
        if (!running) {
            return;
        }

        FrameSource source = frameSource;
        if (source == null) {
            return;
        }

        Mat frame = source.capture();
        if (frame.empty()) {
            updateStatusOnEdt("Status: no frame available");
            return;
        }

        GestureDetectionResult detectionResult = detector.detect(frame);
        long now = System.currentTimeMillis();
        boolean gestureDetected = detectionResult.detected();

        if (gestureDetected && !previousGestureDetected && now - lastCountedAtMs >= COUNT_DEBOUNCE_MS) {
            lastCountedAtMs = now;
            gestureCount++;
            updateCounterOnEdt(gestureCount);

            if (gestureCount >= targetCount && timerEndAtMs < 0L) {
                timerEndAtMs = now + Duration.ofSeconds(timerSeconds).toMillis();
                updateStatusOnEdt("Status: target reached, timer started");
            }
        }

        previousGestureDetected = gestureDetected;

        Mat annotated = frame.clone();
        renderOverlay(annotated, detectionResult, now);
        BufferedImage bufferedImage = toBufferedImage(annotated);

        SwingUtilities.invokeLater(() -> previewLabel.setIcon(new ImageIcon(bufferedImage)));

        annotated.release();
        frame.release();
    }

    private void renderOverlay(Mat frame, GestureDetectionResult result, long nowMs) {
        Scalar green = new Scalar(42, 190, 90);
        Scalar red = new Scalar(60, 60, 240);
        Scalar amber = new Scalar(60, 180, 240);

        String statusText = result.message();
        Scalar color = result.detected() ? green : amber;

        Rect bounds = result.bounds();
        if (bounds != null) {
            Imgproc.rectangle(frame, bounds, color, 2);
        }

        Imgproc.putText(frame, statusText, new Point(24, 32), Imgproc.FONT_HERSHEY_SIMPLEX, 0.8, color, 2);
        Imgproc.putText(frame, "Count: " + gestureCount + " / " + targetCount, new Point(24, 64),
                Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255, 255, 255), 2);

        if (timerEndAtMs > 0L) {
            long remainingMs = Math.max(0L, timerEndAtMs - nowMs);
            long remainingSeconds = (long) Math.ceil(remainingMs / 1000.0);
            Imgproc.putText(frame, "Timer: " + remainingSeconds + "s", new Point(24, 96),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, red, 2);

            if (remainingMs <= 0L) {
                timerEndAtMs = -1L;
                updateStatusOnEdt("Status: timer complete");
            }
        }
    }

    private void stopCapture() {
        running = false;
        timerEndAtMs = -1L;
        startButton.setText("Start");

        ScheduledFuture<?> task = captureTask;
        captureTask = null;
        if (task != null) {
            task.cancel(true);
        }

        FrameSource source = frameSource;
        frameSource = null;
        if (source != null) {
            source.close();
        }

        updateStatusOnEdt("Status: stopped");

        SwingUtilities.invokeLater(() -> previewLabel.setIcon(null));
    }

    private void updateCounterOnEdt(int value) {
        SwingUtilities.invokeLater(() -> gestureCountLabel.setText(Integer.toString(value)));
    }

    private void updateStatusOnEdt(String value) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(value));
    }

    private int parsePositiveInt(String text, int fallback) {
        try {
            int value = Integer.parseInt(text);
            return Math.max(1, value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private BufferedImage toBufferedImage(Mat mat) {
        int type = switch (mat.channels()) {
            case 1 -> BufferedImage.TYPE_BYTE_GRAY;
            case 4 -> BufferedImage.TYPE_4BYTE_ABGR;
            default -> BufferedImage.TYPE_3BYTE_BGR;
        };

        byte[] source = new byte[(int) (mat.total() * mat.channels())];
        mat.get(0, 0, source);

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        byte[] target = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(source, 0, target, 0, source.length);
        return image;
    }
}