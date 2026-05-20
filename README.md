# Open67

Open67 is a browser-based webcam app backed by Java. The browser owns camera access, runs MediaPipe Hands for live landmarks, and sends gesture signals to the Java server for counting and timer flow.

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
5. Keep both palms up and alternate which hand is higher to count reps.

## Server endpoints

- `/` serves the single-page webcam UI.
- `/api/frame` receives browser-captured frames and returns detection state.
- `/api/state` returns the current counter and timer state.
- `/api/reset` clears the current session state.

## Notes

- Hand landmark extraction is powered by MediaPipe Hands in the browser.
- Java still owns game/session state: countdown, counting, target, and timer completion.
- This is the Codespaces-friendly workaround when Java cannot access the webcam directly.