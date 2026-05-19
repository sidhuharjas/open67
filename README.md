# Open67

Open67 is a browser-based webcam app backed by Java. The browser owns camera access, sends frames to the Java server, and the server detects the gesture, counts it, and starts a timer.

## How to run

```bash
mvn compile
mvn exec:java
```

By default the server listens on port `8080`. In Codespaces, forward that port and open the forwarded browser URL.

## How to use

1. Open the app in your browser.
2. Click `Start camera`.
3. Allow webcam permission.
4. Set your gesture target and timer seconds.
5. Hold the gesture in view and watch the counter update.

## Server endpoints

- `/` serves the single-page webcam UI.
- `/api/frame` receives browser-captured frames and returns detection state.
- `/api/state` returns the current counter and timer state.
- `/api/reset` clears the current session state.

## Notes

- The gesture detector is heuristic, not a trained ML model.
- This is the Codespaces-friendly workaround when Java cannot access the webcam directly.