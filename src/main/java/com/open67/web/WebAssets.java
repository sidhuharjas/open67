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
                  <title>Open67</title>
                  <style>
                    :root {
                      color-scheme: dark;
                      --bg: #0d1117;
                      --panel: #161b22;
                      --panel-2: #1f2630;
                      --text: #e6edf3;
                      --muted: #8b949e;
                      --accent: #58a6ff;
                      --ok: #3fb950;
                      --warn: #d29922;
                      --danger: #f85149;
                    }
                    * { box-sizing: border-box; }
                    body {
                      margin: 0;
                      font-family: Inter, ui-sans-serif, system-ui, sans-serif;
                      background: radial-gradient(circle at top, #142033, var(--bg) 55%);
                      color: var(--text);
                      min-height: 100vh;
                    }
                    .app {
                      display: grid;
                      grid-template-columns: minmax(320px, 1fr) minmax(340px, 460px);
                      gap: 20px;
                      padding: 20px;
                      max-width: 1400px;
                      margin: 0 auto;
                    }
                    .hero, .panel {
                      background: linear-gradient(180deg, rgba(255,255,255,0.04), rgba(255,255,255,0.02)), var(--panel);
                      border: 1px solid rgba(255,255,255,0.08);
                      border-radius: 24px;
                      box-shadow: 0 24px 80px rgba(0,0,0,.35);
                    }
                    .hero { padding: 20px; }
                    .panel { padding: 20px; }
                    h1 { margin: 0 0 8px; font-size: clamp(2.4rem, 4vw, 4rem); }
                    .lede { color: var(--muted); max-width: 62ch; line-height: 1.5; }
                    .grid {
                      display: grid;
                      grid-template-columns: repeat(2, minmax(0, 1fr));
                      gap: 14px;
                      margin: 18px 0;
                    }
                    .card {
                      background: var(--panel-2);
                      border: 1px solid rgba(255,255,255,0.07);
                      border-radius: 18px;
                      padding: 14px;
                    }
                    .label { color: var(--muted); font-size: .88rem; margin-bottom: 6px; }
                    .value { font-size: 1.35rem; font-weight: 700; }
                    .controls {
                      display: grid;
                      grid-template-columns: repeat(3, minmax(0, 1fr));
                      gap: 12px;
                      margin: 18px 0 12px;
                    }
                    input, button {
                      width: 100%;
                      border: 1px solid rgba(255,255,255,0.08);
                      border-radius: 14px;
                      background: #0f1722;
                      color: var(--text);
                      padding: 12px 14px;
                      font-size: 1rem;
                    }
                    button { background: linear-gradient(180deg, #2f81f7, #1f6feb); font-weight: 700; cursor: pointer; }
                    button.secondary { background: #30363d; }
                    video, canvas {
                      width: 100%;
                      border-radius: 18px;
                      background: #000;
                      aspect-ratio: 16 / 10;
                      object-fit: cover;
                      border: 1px solid rgba(255,255,255,0.08);
                    }
                    .stack { display: grid; gap: 12px; }
                    .status { color: var(--muted); min-height: 1.4em; }
                    .pill { display: inline-flex; gap: 8px; align-items: center; padding: 6px 10px; border-radius: 999px; background: rgba(88,166,255,.12); color: #b6d8ff; }
                    
                    /* Overlay and countdown styles */
                    #overlay {
                      position: fixed;
                      top: 0;
                      left: 0;
                      width: 100%;
                      height: 100%;
                      background: rgba(0, 0, 0, 0.95);
                      display: none;
                      z-index: 9999;
                      flex-direction: column;
                      align-items: center;
                      justify-content: center;
                    }
                    #overlay.active {
                      display: flex;
                    }
                    #countdown {
                      font-size: 20rem;
                      font-weight: 900;
                      color: var(--accent);
                      text-shadow: 0 0 40px rgba(88, 166, 255, 0.8);
                      line-height: 1;
                      animation: pulse 0.6s ease-in-out;
                    }
                    @keyframes pulse {
                      0% { transform: scale(1.2); opacity: 0.5; }
                      50% { transform: scale(1); opacity: 1; }
                      100% { transform: scale(0.9); opacity: 1; }
                    }
                    
                    /* Results screen */
                    #resultScreen {
                      position: fixed;
                      top: 0;
                      left: 0;
                      width: 100%;
                      height: 100%;
                      background: rgba(0, 0, 0, 0.98);
                      display: none;
                      z-index: 9998;
                      flex-direction: column;
                      align-items: center;
                      justify-content: center;
                      gap: 30px;
                    }
                    #resultScreen.active {
                      display: flex;
                    }
                    #resultCount {
                      font-size: 15rem;
                      font-weight: 900;
                      color: var(--ok);
                      text-shadow: 0 0 60px rgba(63, 185, 80, 0.8);
                      line-height: 1;
                    }
                    #resultText {
                      font-size: 2.5rem;
                      color: var(--text);
                      text-align: center;
                    }
                    #resultButtons {
                      display: flex;
                      gap: 20px;
                      margin-top: 20px;
                    }
                    #resultButtons button {
                      min-width: 200px;
                      padding: 16px 24px;
                      font-size: 1.2rem;
                    }
                    
                    #livePanel {
                      transition: opacity 0.3s ease;
                    }
                    #livePanel.hidden {
                      opacity: 0;
                      pointer-events: none;
                    }
                    
                    @media (max-width: 980px) {
                      .app { grid-template-columns: 1fr; }
                    }
                  </style>
                </head>
                <body>
                  <div id="overlay">
                    <div id="countdown">3</div>
                  </div>
                  
                  <div id="resultScreen">
                    <div id="resultCount">0</div>
                    <div id="resultText">gestures completed</div>
                    <div id="resultButtons">
                      <button id="tryAgainBtn">Try Again</button>
                      <button id="resultResetBtn" class="secondary">Back to Setup</button>
                    </div>
                  </div>

                  <div class="app">
                    <section class="hero">
                      <span class="pill">Browser webcam API + Java backend</span>
                      <h1>Open67</h1>
                      <p class="lede">Open your webcam in the browser, stream frames to the Java server, detect the 67 gesture, and trigger the timer once the target count is reached. This is the Codespaces-friendly workaround.</p>

                      <div class="controls">
                        <label><div class="label">Gesture target</div><input id="targetCount" type="number" min="1" value="3"></label>
                        <label><div class="label">Timer seconds</div><input id="timerSeconds" type="number" min="1" value="10"></label>
                        <label><div class="label">Frame interval ms</div><input id="frameInterval" type="number" min="100" value="150"></label>
                      </div>

                      <div class="controls">
                        <button id="startBtn">Start camera</button>
                        <button id="goBtn">Go</button>
                        <button id="resetBtn" class="secondary">Reset counter</button>
                        <button id="stopBtn" class="secondary">Stop camera</button>
                      </div>

                      <div class="stack">
                        <video id="video" playsinline autoplay muted></video>
                        <canvas id="canvas" hidden></canvas>
                        <div id="status" class="status">Idle.</div>
                      </div>
                    </section>

                    <aside class="panel" id="livePanel">
                      <h2 style="margin-top:0">Live state</h2>
                      <div class="grid">
                        <div class="card"><div class="label">Detection hits</div><div class="value" id="detectionCount">0</div></div>
                        <div class="card"><div class="label">Gesture count</div><div class="value" id="gestureCount">0</div></div>
                        <div class="card"><div class="label">Target</div><div class="value" id="targetDisplay">3</div></div>
                        <div class="card"><div class="label">Timer</div><div class="value" id="timerDisplay">Idle</div></div>
                        <div class="card"><div class="label">Detection</div><div class="value" id="detectionDisplay">Waiting</div></div>
                      </div>
                      <p class="lede">Tip: in Codespaces, run the app and forward port 8080. The browser page owns the webcam permission, and Java only receives image frames.</p>
                    </aside>
                  </div>

                  <script>
                    const video = document.getElementById('video');
                    const canvas = document.getElementById('canvas');
                    const status = document.getElementById('status');
                    const detectionCount = document.getElementById('detectionCount');
                    const gestureCount = document.getElementById('gestureCount');
                    const targetDisplay = document.getElementById('targetDisplay');
                    const timerDisplay = document.getElementById('timerDisplay');
                    const detectionDisplay = document.getElementById('detectionDisplay');
                    const targetCount = document.getElementById('targetCount');
                    const timerSeconds = document.getElementById('timerSeconds');
                    const frameInterval = document.getElementById('frameInterval');
                    const startBtn = document.getElementById('startBtn');
                    const goBtn = document.getElementById('goBtn');
                    const stopBtn = document.getElementById('stopBtn');
                    const resetBtn = document.getElementById('resetBtn');
                    const overlay = document.getElementById('overlay');
                    const countdown = document.getElementById('countdown');
                    const resultScreen = document.getElementById('resultScreen');
                    const resultCount = document.getElementById('resultCount');
                    const resultText = document.getElementById('resultText');
                    const tryAgainBtn = document.getElementById('tryAgainBtn');
                    const resultResetBtn = document.getElementById('resultResetBtn');
                    const livePanel = document.getElementById('livePanel');

                    let stream = null;
                    let timerHandle = null;
                    let sessionActive = false;
                    let sessionComplete = false;

                    function setStatus(message) {
                      status.textContent = message;
                    }

                    function showOverlay() {
                      overlay.classList.add('active');
                      livePanel.classList.add('hidden');
                    }

                    function hideOverlay() {
                      overlay.classList.remove('active');
                    }

                    function showResults(count) {
                      resultScreen.classList.add('active');
                      resultCount.textContent = count;
                      sessionActive = false;
                      sessionComplete = true;
                    }

                    function hideResults() {
                      resultScreen.classList.remove('active');
                      livePanel.classList.remove('hidden');
                      sessionComplete = false;
                    }

                    function updateUi(state) {
                      detectionCount.textContent = state.detectionCount ?? 0;
                      gestureCount.textContent = state.gestureCount ?? 0;
                      targetDisplay.textContent = state.targetCount ?? targetCount.value;
                      detectionDisplay.textContent = state.detected ? `${state.detectionCount ?? 0} hits` : (state.message || 'Waiting');
                      
                      if (state.timerActive) {
                        timerDisplay.textContent = `${Math.ceil((state.timerRemainingMillis || 0) / 1000)}s`;
                      } else {
                        timerDisplay.textContent = state.timerComplete ? 'Done' : 'Idle';
                      }

                      if (state.state === 'COMPLETE' && sessionActive) {
                        showResults(state.gestureCount ?? 0);
                      }
                    }

                    async function getState() {
                      const response = await fetch('/api/state');
                      return await response.json();
                    }

                    async function resetCounter() {
                      const response = await fetch('/api/reset', { method: 'POST' });
                      updateUi(await response.json());
                    }

                    async function sendFrame() {
                      if (!stream) {
                        return;
                      }
                      canvas.width = video.videoWidth || 640;
                      canvas.height = video.videoHeight || 400;
                      const context = canvas.getContext('2d', { willReadFrequently: true });
                      context.drawImage(video, 0, 0, canvas.width, canvas.height);
                      const imageDataUrl = canvas.toDataURL('image/jpeg', 0.75);

                      const body = new URLSearchParams({
                        imageDataUrl,
                        targetCount: targetCount.value,
                        timerSeconds: timerSeconds.value
                      });

                      const response = await fetch('/api/frame', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                        body
                      });
                      const state = await response.json();
                      updateUi(state);
                    }

                    async function startCamera() {
                      if (stream) {
                        return;
                      }
                      stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'user' }, audio: false });
                      video.srcObject = stream;
                      await video.play();
                      setStatus('Camera started. Click Go to begin.');
                      await resetCounter();
                      const loop = async () => {
                        if (!stream) {
                          return;
                        }
                        try {
                          await sendFrame();
                        } catch (error) {
                          setStatus(`Frame upload failed: ${error.message}`);
                        }
                        timerHandle = window.setTimeout(loop, Number(frameInterval.value) || 250);
                      };
                      loop();
                    }

                    async function startSession() {
                      try {
                        sessionActive = true;
                        showOverlay();

                        const body = new URLSearchParams({ targetCount: targetCount.value, timerSeconds: timerSeconds.value });
                        await fetch('/api/start', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' }, body });

                        // Big centered countdown: 3..2..1
                        countdown.textContent = '3';
                        await new Promise(r => setTimeout(r, 1000));
                        countdown.textContent = '2';
                        await new Promise(r => setTimeout(r, 1000));
                        countdown.textContent = '1';
                        await new Promise(r => setTimeout(r, 1000));
                        countdown.textContent = 'GO!';
                        await new Promise(r => setTimeout(r, 500));
                        hideOverlay();
                        setStatus('Go! Detect the 67 gesture...');
                      } catch (err) {
                        setStatus('Start failed: ' + (err.message || err));
                        sessionActive = false;
                      }
                    }

                    function stopCamera() {
                      if (timerHandle) {
                        clearTimeout(timerHandle);
                        timerHandle = null;
                      }
                      if (stream) {
                        stream.getTracks().forEach(track => track.stop());
                        stream = null;
                      }
                      video.srcObject = null;
                      setStatus('Camera stopped.');
                    }

                    startBtn.addEventListener('click', async () => {
                      try {
                        await startCamera();
                      } catch (error) {
                        setStatus(`Could not start camera: ${error.message}`);
                      }
                    });
                    goBtn.addEventListener('click', async () => {
                      if (!sessionComplete && !sessionActive) {
                        await startSession();
                      }
                    });
                    stopBtn.addEventListener('click', stopCamera);
                    resetBtn.addEventListener('click', resetCounter);
                    tryAgainBtn.addEventListener('click', async () => {
                      hideResults();
                      await resetCounter();
                      await startSession();
                    });
                    resultResetBtn.addEventListener('click', async () => {
                      hideResults();
                      await resetCounter();
                      setStatus('Reset. Click Go to start again.');
                    });
                    targetCount.addEventListener('change', () => targetDisplay.textContent = targetCount.value);

                    getState().then(updateUi).catch(() => setStatus('Java server is not ready yet.'));
                  </script>
                </body>
                </html>
                """;
    }
}