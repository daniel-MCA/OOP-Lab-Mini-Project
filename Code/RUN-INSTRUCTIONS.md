# Running AdaptQ — do this exactly, in order

## 1. Make sure NOTHING old is still running
If you've run this project before (even an older zip), an old process may
still be sitting on port 8080 and serving stale files.

- Close every terminal window/tab that ran `mvn spring-boot:run` before.
- **Windows**: open Task Manager → find any `java.exe` → End Task.
  (or in a terminal: `taskkill /IM java.exe /F`)
- **Mac/Linux**: `lsof -i :8080` to find the PID, then `kill <PID>`.

## 2. Unzip this fresh copy
Unzip `AdaptQ.zip`. You should get ONE folder named `AdaptQ` containing
`pom.xml` directly inside it, with `src/` next to it — not scattered files.

## 3. Run it
```
cd AdaptQ
mvn spring-boot:run
```
Wait for the terminal to show Spring Boot's startup banner and go quiet
(no more log lines appearing) — that means it's ready and listening.

## 4. Open it — in a fresh/incognito tab
```
http://localhost:8080/login.html
```
Using a fresh/incognito window avoids old cached HTML from a previous run.
If you already had a tab open from before, hard-refresh it:
- Windows/Linux: Ctrl+Shift+R
- Mac: Cmd+Shift+R

## 5. Log in and go to "Overview"
The Recommendation Engine card ("🎯 Recommended for you") lives on the
**Overview** page (`student-home.html`) — the first item in the sidebar,
not "Skill Matrix" and not the Admin Center.

- Log in as a student who already has some answered questions (e.g. `renu`)
  to immediately see live Weak/Developing/Unseen recommendations, OR
- Log in as a new student, go to **Practice**, answer 5–10 questions
  (get some wrong on purpose), then go back to **Overview**.

## Still not seeing something new?
Press Ctrl+U (view page source) on the page in question and search for a
piece of text you expect (e.g. `recoCard`). If it's missing from the page
source itself, the server is still serving an old file — go back to step 1.
If it's present in the source but not visually showing, open DevTools (F12)
→ Console tab and send me the exact red error text.
