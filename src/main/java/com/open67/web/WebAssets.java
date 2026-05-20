package com.open67.web;

final class WebAssets {

    private WebAssets() {
    }

    static String indexHtml() {
        return """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>Open67 Motion Lab</title>
                  <link rel="preconnect" href="https://fonts.googleapis.com">
                  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                  <link href="https://fonts.googleapis.com/css2?family=Audiowide&family=Manrope:wght@400;600;700;800&display=swap" rel="stylesheet">
                  <style>
                    :root {
                      --bg-ink: #08131a;
                      --bg-deep: #111319;
                      --panel: rgba(7, 31, 40, 0.72);
                      --panel-border: rgba(142, 248, 255, 0.26);
                      --text: #e8f6ff;
                      --muted: #9cc4d1;
                      --accent: #38ffd6;
                      --accent-2: #38a8ff;
                      --danger: #ff6e6e;
                      --ok: #9dff7d;
                    }

                    * { box-sizing: border-box; }

                    body {
                      margin: 0;
                      min-height: 100vh;
                      font-family: Manrope, Segoe UI, sans-serif;
                      color: var(--text);
                      background:
                        radial-gradient(1200px 700px at 5% 10%, rgba(56, 255, 214, 0.16), transparent 62%),
                        radial-gradient(900px 500px at 92% 18%, rgba(56, 168, 255, 0.14), transparent 68%),
                        linear-gradient(160deg, var(--bg-ink) 0%, var(--bg-deep) 100%);
                    }

                    .shell {
                      max-width: 1320px;
                      margin: 0 auto;
                      padding: 20px;
                      display: grid;
                      grid-template-columns: minmax(0, 1.25fr) minmax(320px, 0.75fr);
                      gap: 16px;
                    }

                    .glass {
                      border-radius: 24px;
                      border: 1px solid var(--panel-border);
                      background: linear-gradient(160deg, rgba(11, 29, 40, 0.82), rgba(6, 20, 28, 0.62));
                      box-shadow: 0 28px 50px rgba(0, 0, 0, 0.4), inset 0 0 0 1px rgba(255, 255, 255, 0.06);
                      backdrop-filter: blur(12px);
                    }

                    .studio {
                      padding: 18px;
                      display: grid;
                      gap: 14px;
                    }

                    .heading {
                      display: flex;
                      justify-content: space-between;
                      gap: 12px;
                      align-items: flex-start;
                      flex-wrap: wrap;
                    }

                    .brand {
                      font-family: Audiowide, Manrope, sans-serif;
                      letter-spacing: 1px;
                      font-size: clamp(1.6rem, 3vw, 2.4rem);
                      margin: 0;
                    }

                    .tagline {
                      margin: 6px 0 0;
                      color: var(--muted);
                      font-size: 0.98rem;
                    }

                    .phase {
                      font-weight: 800;
                      padding: 8px 12px;
                      border-radius: 999px;
                      color: #04231f;
                      background: linear-gradient(90deg, var(--accent), #7fffed);
                    }

                    .camWrap {
                      position: relative;
                      border-radius: 20px;
                      overflow: hidden;
                      border: 1px solid rgba(157, 255, 125, 0.25);
                      background: #021017;
                      aspect-ratio: 16 / 10;
                    }

                    video, canvas {
                      position: absolute;
                      inset: 0;
                      width: 100%;
                      height: 100%;
                      object-fit: cover;
                    }

                    #overlayCanvas {
                      pointer-events: none;
                    }

                    .traceBar {
                      position: absolute;
                      left: 0;
                      right: 0;
                      bottom: 0;
                      padding: 10px 12px;
                      background: linear-gradient(180deg, transparent, rgba(0, 0, 0, 0.55));
                      display: flex;
                      justify-content: space-between;
                      font-weight: 700;
                    }

                    .controls {
                      display: grid;
                      grid-template-columns: repeat(4, minmax(0, 1fr));
                      gap: 10px;
                    }

                    label {
                      display: grid;
                      gap: 6px;
                      font-size: 0.85rem;
                      color: var(--muted);
                    }

                    input, button {
                      border-radius: 12px;
                      border: 1px solid rgba(157, 255, 125, 0.22);
                      background: rgba(0, 19, 26, 0.9);
                      color: var(--text);
                      font-size: 0.98rem;
                      padding: 11px 12px;
                      font-family: Manrope, sans-serif;
                    }

                    button {
                      font-weight: 800;
                      cursor: pointer;
                      transition: transform .14s ease, box-shadow .18s ease;
                    }

                    button:hover {
                      transform: translateY(-1px);
                      box-shadow: 0 10px 24px rgba(56, 255, 214, 0.2);
                    }

                    .actionRow {
                      display: grid;
                      grid-template-columns: repeat(4, minmax(0, 1fr));
                      gap: 10px;
                    }

                    .primary {
                      background: linear-gradient(120deg, #3bc8ff, #38ffd6);
                      color: #001018;
                      border: none;
                    }

                    .secondary {
                      background: rgba(12, 29, 37, 0.92);
                    }

                    .stats {
                      padding: 18px;
                      display: grid;
                      gap: 12px;
                    }

                    .kpiGrid {
                      display: grid;
                      grid-template-columns: repeat(2, minmax(0, 1fr));
                      gap: 10px;
                    }

                    .kpi {
                      border-radius: 16px;
                      border: 1px solid rgba(56, 168, 255, 0.32);
                      padding: 14px;
                      background: linear-gradient(180deg, rgba(4, 28, 39, 0.88), rgba(5, 20, 29, 0.75));
                    }

                    .kpi .label {
                      color: var(--muted);
                      font-size: 0.78rem;
                    }

                    .kpi .value {
                      margin-top: 6px;
                      font-size: 1.6rem;
                      font-weight: 800;
                      letter-spacing: 0.3px;
                    }

                    .guide {
                      margin: 0;
                      color: var(--muted);
                      line-height: 1.5;
                    }

                    .signal {
                      display: inline-flex;
                      align-items: center;
                      gap: 8px;
                      color: #062620;
                      background: rgba(157, 255, 125, 0.9);
                      border-radius: 999px;
                      padding: 7px 12px;
                      font-weight: 800;
                      font-size: 0.86rem;
                    }

                    .dot {
                      width: 9px;
                      height: 9px;
                      border-radius: 999px;
                      background: #02772c;
                      box-shadow: 0 0 0 6px rgba(2, 119, 44, 0.15);
                    }

                    .statusLine {
                      min-height: 1.4em;
                      font-weight: 600;
                    }

                    .danger { color: var(--danger); }

                    #countdownScreen,
                    #resultScreen {
                      position: fixed;
                      inset: 0;
                      z-index: 60;
                      display: none;
                      align-items: center;
                      justify-content: center;
                      flex-direction: column;
                      background: rgba(2, 10, 15, 0.94);
                      backdrop-filter: blur(8px);
                    }

                    #countdownScreen.active,
                    #resultScreen.active {
                      display: flex;
                    }

                    #countdownValue,
                    #resultValue {
                      font-family: Audiowide, Manrope, sans-serif;
                      font-size: clamp(6rem, 22vw, 14rem);
                      line-height: 1;
                      text-shadow: 0 0 48px rgba(56, 255, 214, 0.62);
                      animation: pop .5s ease;
                    }

                    #resultText {
                      margin-top: 12px;
                      font-size: clamp(1.1rem, 2.5vw, 1.8rem);
                      color: var(--muted);
                    }

                    @keyframes pop {
                      0% { transform: scale(0.7); opacity: 0.4; }
                      100% { transform: scale(1); opacity: 1; }
                    }

                    @media (max-width: 1040px) {
                      .shell { grid-template-columns: 1fr; }
                    }

                    @media (max-width: 760px) {
                      .controls { grid-template-columns: 1fr; }
                      .actionRow { grid-template-columns: repeat(2, minmax(0, 1fr)); }
                    }
                  </style>
                </head>
                <body>
                  <div id="countdownScreen">
                    <div id="countdownValue">3</div>
                  </div>

                  <div id="resultScreen">
                    <div id="resultValue">0</div>
                    <div id="resultText">completed reps</div>
                    <div class="actionRow" style="width:min(560px,88vw); margin-top:20px;">
                      <button id="againBtn" class="primary">Run Again</button>
                      <button id="closeResultBtn" class="secondary">Back To Setup</button>
                    </div>
                  </div>

                  <main class="shell">
                    <section class="glass studio">
                      <header class="heading">
                        <div>
                          <h1 class="brand">OPEN67 MOTION LAB</h1>
                          <p class="tagline">MediaPipe Hands in browser + Java session engine. Alternate hand elevation with palms up to count reps.</p>
                        </div>
                        <div id="phaseBadge" class="phase">IDLE</div>
                      </header>

                      <div class="camWrap">
                        <video id="video" playsinline autoplay muted></video>
                        <canvas id="overlayCanvas"></canvas>
                        <canvas id="sendCanvas" hidden></canvas>
                        <div class="traceBar">
                          <span id="traceLeft">Hands: 0</span>
                          <span id="traceRight">Signal: waiting</span>
                        </div>
                      </div>

                      <div class="controls">
                        <label>Target reps
                          <input id="targetCount" type="number" min="1" value="6" />
                        </label>
                        <label>Timer seconds
                          <input id="timerSeconds" type="number" min="1" value="10" />
                        </label>
                        <label>Frame interval ms
                          <input id="frameInterval" type="number" min="60" value="120" />
                        </label>
                        <label>Parallel uploads
                          <input id="parallelUploads" type="number" min="1" max="16" value="4" />
                        </label>
                      </div>

                      <div class="actionRow">
                        <button id="startCamBtn" class="primary">Start Camera</button>
                        <button id="goBtn" class="primary">Go</button>
                        <button id="resetBtn" class="secondary">Reset</button>
                        <button id="stopCamBtn" class="secondary">Stop Camera</button>
                      </div>

                      <div id="status" class="statusLine">Waiting to start camera.</div>
                    </section>

                    <aside class="glass stats">
                      <div class="signal"><span class="dot"></span><span id="signalText">Session ready</span></div>
                      <div class="kpiGrid">
                        <div class="kpi"><div class="label">Gesture Count</div><div id="gestureCount" class="value">0</div></div>
                        <div class="kpi"><div class="label">Detection Hits</div><div id="detectionCount" class="value">0</div></div>
                        <div class="kpi"><div class="label">Target</div><div id="targetDisplay" class="value">6</div></div>
                        <div class="kpi"><div class="label">Timer</div><div id="timerDisplay" class="value">Idle</div></div>
                        <div class="kpi"><div class="label">Hands Visible</div><div id="handsDisplay" class="value">0</div></div>
                        <div class="kpi"><div class="label">Confidence</div><div id="confidenceDisplay" class="value">0.00</div></div>
                      </div>
                      <p class="guide">
                        Gesture67 tracking rule in this build: move either visible hand up and down with confidence above 40%.
                        Each completed motion cycle emits a rep signal to the Java backend.
                      </p>
                    </aside>
                  </main>

                  <script src="https://cdn.jsdelivr.net/npm/@mediapipe/camera_utils/camera_utils.js"></script>
                  <script src="https://cdn.jsdelivr.net/npm/@mediapipe/drawing_utils/drawing_utils.js"></script>
                  <script src="https://cdn.jsdelivr.net/npm/@mediapipe/hands/hands.js"></script>
                  <script>
                    const video = document.getElementById('video');
                    const overlayCanvas = document.getElementById('overlayCanvas');
                    const sendCanvas = document.getElementById('sendCanvas');
                    const statusEl = document.getElementById('status');
                    const phaseBadge = document.getElementById('phaseBadge');
                    const traceLeft = document.getElementById('traceLeft');
                    const traceRight = document.getElementById('traceRight');
                    const signalText = document.getElementById('signalText');
                    const gestureCount = document.getElementById('gestureCount');
                    const detectionCount = document.getElementById('detectionCount');
                    const targetDisplay = document.getElementById('targetDisplay');
                    const timerDisplay = document.getElementById('timerDisplay');
                    const handsDisplay = document.getElementById('handsDisplay');
                    const confidenceDisplay = document.getElementById('confidenceDisplay');
                    const targetCountInput = document.getElementById('targetCount');
                    const timerSecondsInput = document.getElementById('timerSeconds');
                    const frameIntervalInput = document.getElementById('frameInterval');
                    const parallelUploadsInput = document.getElementById('parallelUploads');
                    const startCamBtn = document.getElementById('startCamBtn');
                    const stopCamBtn = document.getElementById('stopCamBtn');
                    const goBtn = document.getElementById('goBtn');
                    const resetBtn = document.getElementById('resetBtn');
                    const countdownScreen = document.getElementById('countdownScreen');
                    const countdownValue = document.getElementById('countdownValue');
                    const resultScreen = document.getElementById('resultScreen');
                    const resultValue = document.getElementById('resultValue');
                    const againBtn = document.getElementById('againBtn');
                    const closeResultBtn = document.getElementById('closeResultBtn');

                    const sendCtx = sendCanvas.getContext('2d', { willReadFrequently: true });
                    const overlayCtx = overlayCanvas.getContext('2d');

                    const hands = new Hands({
                      locateFile: file => `https://cdn.jsdelivr.net/npm/@mediapipe/hands/${file}`
                    });
                    hands.setOptions({
                      maxNumHands: 2,
                      modelComplexity: 1,
                      minDetectionConfidence: 0.4,
                      minTrackingConfidence: 0.4
                    });

                    let camera = null;
                    let frameLoopHandle = null;
                    let latestSignal = {
                      handCount: 0,
                      gestureDetected: false,
                      confidence: 0,
                      clientMessage: 'No hands',
                      phase: 'IDLE'
                    };
                    let running = false;
                    let sessionActive = false;
                    let triggerUntil = 0;
                    let inFlightRequests = 0;
                    let latestSentSequence = 0;
                    let latestAppliedSequence = 0;
                    let lastLeftCycleAt = 0;
                    let lastRightCycleAt = 0;
                    let lastRepAt = 0;

                    const MOTION_AMPLITUDE = 0.022;
                    const MOTION_DELTA = 0.003;
                    const MOTION_COOLDOWN_MS = 110;
                    const REP_CONFIDENCE_THRESHOLD = 0.40;
                    const REP_COOLDOWN_MS = 280;
                    const SIGNAL_HOLD_MS = 220;

                    const leftTrack = createMotionTrack();
                    const rightTrack = createMotionTrack();

                    function setStatus(message, isError = false) {
                      statusEl.textContent = message;
                      statusEl.classList.toggle('danger', !!isError);
                    }

                    function setPhase(text) {
                      phaseBadge.textContent = text;
                    }

                    function maxParallelUploads() {
                      const requested = Number(parallelUploadsInput.value) || 1;
                      return Math.max(1, Math.min(16, requested));
                    }

                    function createMotionTrack() {
                      return {
                        lastY: null,
                        minY: 1,
                        maxY: 0,
                        sawUp: false,
                        sawDown: false,
                        cooldownUntil: 0,
                        lastSeenAt: 0
                      };
                    }

                    function resetMotionTrack(track) {
                      track.lastY = null;
                      track.minY = 1;
                      track.maxY = 0;
                      track.sawUp = false;
                      track.sawDown = false;
                      track.cooldownUntil = 0;
                      track.lastSeenAt = 0;
                    }

                    function updateMotionTrack(track, y, now) {
                      track.lastSeenAt = now;

                      if (track.lastY === null) {
                        track.lastY = y;
                        track.minY = y;
                        track.maxY = y;
                        return false;
                      }

                      const delta = y - track.lastY;
                      track.lastY = y;

                      if (y < track.minY) track.minY = y;
                      if (y > track.maxY) track.maxY = y;

                      if (delta <= -MOTION_DELTA) track.sawUp = true;
                      if (delta >= MOTION_DELTA) track.sawDown = true;

                      const amplitude = track.maxY - track.minY;
                      const canFire = now >= track.cooldownUntil;
                      if (canFire && track.sawUp && track.sawDown && amplitude >= MOTION_AMPLITUDE) {
                        track.cooldownUntil = now + MOTION_COOLDOWN_MS;
                        track.sawUp = false;
                        track.sawDown = false;
                        track.minY = y;
                        track.maxY = y;
                        return true;
                      }

                      return false;
                    }

                    function handSnapshot(landmarks) {
                      const wrist = landmarks[0];
                      const middleMcp = landmarks[9];
                      const indexTip = landmarks[8];
                      const middleTip = landmarks[12];
                      const ringTip = landmarks[16];
                      const pinkyTip = landmarks[20];
                      const avgTipsY = (indexTip.y + middleTip.y + ringTip.y + pinkyTip.y) / 4;
                      return {
                        centerX: middleMcp.x,
                        centerY: middleMcp.y,
                        wristY: wrist.y,
                        avgTipsY
                      };
                    }

                    function updateLocalGestureSignal(results) {
                      const now = Date.now();
                      const snapshots = (results.multiHandLandmarks || [])
                        .map(handSnapshot)
                        .sort((a, b) => a.centerX - b.centerX);
                      const handCount = snapshots.length;

                      const handednessScores = (results.multiHandedness || [])
                        .map(entry => entry?.classification?.[0]?.score)
                        .filter(score => Number.isFinite(score));
                      const handednessConfidence = handednessScores.length > 0
                        ? handednessScores.reduce((sum, value) => sum + value, 0) / handednessScores.length
                        : 0;
                      const baseConfidence = handCount === 0 ? 0 : (handCount === 1 ? 0.45 : 0.72);
                      const confidence = Math.max(baseConfidence, handednessConfidence);

                      let clientMessage = 'Move hand(s) up and down';
                      let phase = 'TRACKING';

                      if (handCount === 0) {
                        if (now - leftTrack.lastSeenAt > 280) {
                          resetMotionTrack(leftTrack);
                        }
                        if (now - rightTrack.lastSeenAt > 280) {
                          resetMotionTrack(rightTrack);
                        }
                        latestSignal = {
                          handCount,
                          gestureDetected: false,
                          confidence,
                          clientMessage: 'No hands',
                          phase: 'NO_HANDS'
                        };
                        return;
                      }

                      const leftOnScreen = snapshots[0];
                      const rightOnScreen = snapshots[1] || null;

                      const leftCycle = updateMotionTrack(leftTrack, leftOnScreen.centerY, now);
                      const rightCycle = rightOnScreen
                        ? updateMotionTrack(rightTrack, rightOnScreen.centerY, now)
                        : false;

                      if (!rightOnScreen && now - rightTrack.lastSeenAt > 280) {
                        resetMotionTrack(rightTrack);
                      }

                      if (leftCycle) {
                        lastLeftCycleAt = now;
                      }
                      if (rightCycle) {
                        lastRightCycleAt = now;
                      }

                      const anyCycle = leftCycle || rightCycle;
                      const confidenceOk = confidence >= REP_CONFIDENCE_THRESHOLD;
                      const cooldownOk = now - lastRepAt >= REP_COOLDOWN_MS;

                      if (anyCycle && confidenceOk && cooldownOk) {
                        lastRepAt = now;
                        triggerUntil = now + SIGNAL_HOLD_MS;
                        clientMessage = 'Rep detected';
                        phase = 'REP';
                      } else {
                        if (anyCycle && !confidenceOk) {
                          clientMessage = 'Motion seen, confidence below 0.40';
                        } else if (handCount === 1) {
                          clientMessage = 'One hand tracked: keep moving up/down';
                        } else {
                          clientMessage = 'Move either hand up/down quickly';
                        }
                        phase = 'TRACKING';
                      }

                      const freshSignal = now < triggerUntil;

                      latestSignal = {
                        handCount,
                        gestureDetected: freshSignal,
                        confidence,
                        clientMessage,
                        phase
                      };
                    }

                    hands.onResults((results) => {
                      const width = video.videoWidth || 960;
                      const height = video.videoHeight || 540;
                      overlayCanvas.width = width;
                      overlayCanvas.height = height;

                      overlayCtx.save();
                      overlayCtx.clearRect(0, 0, width, height);

                      const landmarksList = results.multiHandLandmarks || [];
                      landmarksList.forEach((landmarks) => {
                        drawConnectors(overlayCtx, landmarks, HAND_CONNECTIONS, { color: '#32f8ff', lineWidth: 3 });
                        drawLandmarks(overlayCtx, landmarks, { color: '#9dff7d', lineWidth: 1, radius: 4 });
                      });

                      overlayCtx.restore();

                      updateLocalGestureSignal(results);

                      traceLeft.textContent = `Hands: ${latestSignal.handCount}`;
                      traceRight.textContent = `Signal: ${latestSignal.clientMessage}`;
                      handsDisplay.textContent = latestSignal.handCount;
                      confidenceDisplay.textContent = Number(latestSignal.confidence || 0).toFixed(2);
                    });

                    async function getState() {
                      const response = await fetch('/api/state');
                      return await response.json();
                    }

                    function updateUi(state) {
                      detectionCount.textContent = state.detectionCount ?? 0;
                      gestureCount.textContent = state.gestureCount ?? 0;
                      targetDisplay.textContent = state.targetCount ?? targetCountInput.value;

                      if (state.timerActive) {
                        timerDisplay.textContent = `${Math.ceil((state.timerRemainingMillis || 0) / 1000)}s`;
                      } else {
                        timerDisplay.textContent = state.timerComplete ? 'Done' : 'Idle';
                      }

                      setPhase(state.state || 'IDLE');
                      signalText.textContent = state.lastMessage || 'Ready';

                      if (state.state === 'COMPLETE' && sessionActive) {
                        resultValue.textContent = String(state.gestureCount || 0);
                        resultScreen.classList.add('active');
                        sessionActive = false;
                      }
                    }

                    async function sendFrameSignal(sequence) {
                      if (!running || !video.srcObject) return;

                      sendCanvas.width = video.videoWidth || 640;
                      sendCanvas.height = video.videoHeight || 400;
                      sendCtx.drawImage(video, 0, 0, sendCanvas.width, sendCanvas.height);
                      const imageDataUrl = sendCanvas.toDataURL('image/jpeg', 0.65);

                      const payload = new URLSearchParams({
                        hasClientSignal: 'true',
                        gestureDetected: String(!!latestSignal.gestureDetected),
                        handCount: String(latestSignal.handCount || 0),
                        confidence: String(latestSignal.confidence || 0),
                        clientMessage: latestSignal.clientMessage || 'Tracking',
                        imageDataUrl,
                        targetCount: targetCountInput.value,
                        timerSeconds: timerSecondsInput.value
                      });

                      const response = await fetch('/api/frame', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                        body: payload
                      });
                      const state = await response.json();
                      if (sequence >= latestAppliedSequence) {
                        latestAppliedSequence = sequence;
                        updateUi(state);
                      }
                    }

                    function queueFrameSignal() {
                      if (!running || !video.srcObject) return;
                      if (inFlightRequests >= maxParallelUploads()) return;

                      inFlightRequests++;
                      const sequence = ++latestSentSequence;
                      sendFrameSignal(sequence)
                        .catch((error) => {
                          setStatus('Frame sync failed: ' + (error.message || error), true);
                        })
                        .finally(() => {
                          inFlightRequests = Math.max(0, inFlightRequests - 1);
                        });
                    }

                    async function startCamera() {
                      if (running) return;

                      const stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'user' }, audio: false });
                      video.srcObject = stream;
                      await video.play();

                      camera = new Camera(video, {
                        onFrame: async () => {
                          await hands.send({ image: video });
                        },
                        width: 960,
                        height: 540
                      });

                      await camera.start();
                      running = true;
                      inFlightRequests = 0;
                      latestSentSequence = 0;
                      latestAppliedSequence = 0;
                      lastLeftCycleAt = 0;
                      lastRightCycleAt = 0;
                      lastRepAt = 0;
                      resetMotionTrack(leftTrack);
                      resetMotionTrack(rightTrack);

                      const hardwareThreads = navigator.hardwareConcurrency || 8;
                      parallelUploadsInput.value = String(Math.max(2, Math.min(12, Math.floor(hardwareThreads / 2))));
                      setStatus('Camera active. Press Go to start countdown.');

                      const loop = async () => {
                        if (!running) return;
                        queueFrameSignal();
                        frameLoopHandle = window.setTimeout(loop, Number(frameIntervalInput.value) || 140);
                      };
                      loop();
                    }

                    async function resetSession() {
                      const response = await fetch('/api/reset', { method: 'POST' });
                      updateUi(await response.json());
                    }

                    async function startSession() {
                      try {
                        sessionActive = true;
                        lastLeftCycleAt = 0;
                        lastRightCycleAt = 0;
                        lastRepAt = 0;
                        resetMotionTrack(leftTrack);
                        resetMotionTrack(rightTrack);
                        resultScreen.classList.remove('active');
                        countdownScreen.classList.add('active');

                        const body = new URLSearchParams({ targetCount: targetCountInput.value, timerSeconds: timerSecondsInput.value });
                        await fetch('/api/start', {
                          method: 'POST',
                          headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                          body
                        });

                        const ticks = ['3', '2', '1', 'GO'];
                        for (const value of ticks) {
                          countdownValue.textContent = value;
                          await new Promise(resolve => setTimeout(resolve, value === 'GO' ? 550 : 1000));
                        }

                        countdownScreen.classList.remove('active');
                        setStatus('Session running. Alternate hand height with palms up.');
                      } catch (error) {
                        sessionActive = false;
                        countdownScreen.classList.remove('active');
                        setStatus('Could not start session: ' + (error.message || error), true);
                      }
                    }

                    async function stopCamera() {
                      running = false;
                      sessionActive = false;
                      if (frameLoopHandle) {
                        clearTimeout(frameLoopHandle);
                        frameLoopHandle = null;
                      }
                      if (camera) {
                        await camera.stop();
                        camera = null;
                      }
                      const stream = video.srcObject;
                      if (stream) {
                        stream.getTracks().forEach(track => track.stop());
                      }
                      video.srcObject = null;
                      overlayCtx.clearRect(0, 0, overlayCanvas.width, overlayCanvas.height);
                      setStatus('Camera stopped.');
                    }

                    startCamBtn.addEventListener('click', async () => {
                      try {
                        await startCamera();
                        await resetSession();
                      } catch (error) {
                        setStatus('Unable to start camera: ' + (error.message || error), true);
                      }
                    });

                    goBtn.addEventListener('click', async () => {
                      if (!running) {
                        setStatus('Start camera first.', true);
                        return;
                      }
                      if (!sessionActive) {
                        await startSession();
                      }
                    });

                    stopCamBtn.addEventListener('click', () => stopCamera());
                    resetBtn.addEventListener('click', () => resetSession());
                    againBtn.addEventListener('click', async () => {
                      resultScreen.classList.remove('active');
                      await resetSession();
                      await startSession();
                    });
                    closeResultBtn.addEventListener('click', async () => {
                      resultScreen.classList.remove('active');
                      await resetSession();
                      setStatus('Ready for a new run.');
                    });

                    targetCountInput.addEventListener('change', () => {
                      targetDisplay.textContent = targetCountInput.value;
                    });

                    getState().then(updateUi).catch(() => {
                      setStatus('Server not ready yet.', true);
                    });
                  </script>
                </body>
                </html>
                """;
    }
}
