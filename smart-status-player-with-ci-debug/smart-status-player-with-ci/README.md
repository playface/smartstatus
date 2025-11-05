# SmartStatusPlayer (Debug-ready) with CI

This package is a debug-ready Android project that includes an AccessibilityService and overlay player.
Edit VideoFetcher.kt to set your backend server, then push to GitHub to run CI which builds a debug APK.

Steps:
1. Edit app/src/main/java/com/example/smartstatus/VideoFetcher.kt -> set BACKEND_BASE to your server.
2. Initialize git, push to GitHub.
3. Actions will run and produce APK artifact (app-debug.apk).
