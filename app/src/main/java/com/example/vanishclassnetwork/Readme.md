# Vanish Class Network

An Android chat application built for the Mobile Application Development (MAD) Assignment 3.

## 📱 Project Overview
"Vanish Class Network" is a shared chat room where every message permanently disappears 30 seconds after it is sent. The app is designed to demonstrate accurate countdown timing, offline queuing, and permanent local storage deletion.

## 🛠️ Platform & Tech Stack
* **IDE:** Android Studio
* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material 3)
* **Architecture:** MVVM (Model-View-ViewModel)
* **Local Database:** Room (SQLite)
* **Asynchronous Operations:** Kotlin Coroutines & Flow

## 🚀 How to Build and Run
1. Clone this repository to your local machine.
2. Open the project in Android Studio.
3. Let Gradle sync completely.
4. Select an Android Emulator (API 26 or higher) and click the **Run (▶)** button.

## 🌐 Relay Address
* **Current Status:** The app is currently using a `RelayService` mock to simulate network connectivity and offline queuing.
* **Future Integration:** Once the class relay server IP/URL and protocol are provided, the mock inside `RelayService.kt` will be replaced with the actual WebSocket connection.

## ✨ Key Features Implemented
* **30-Second Countdown:** Calculated using absolute timestamps (`expiresAt`). Backgrounding or rotating the device does not reset the timer.
* **Offline Queue:** Messages sent while offline are saved as `PENDING` in the database. They are only sent when the network returns *if* they haven't expired.
* **Duplicate Prevention:** Every message has a unique UUID. Room Database ignores insertions with duplicate IDs.
* **Permanent Deletion:** The `ChatViewModel` runs a background loop that issues SQL `DELETE` commands for expired messages, ensuring they are removed from storage, not just hidden from the UI.

## ⚠️ Known Limitations (Honesty Section)
* **Screenshots:** The app cannot prevent a user from taking a screenshot or using another camera to photograph the screen.
* **Relay Memory:** While the client deletes expired messages from local storage, we cannot guarantee the class relay server does the same unless the protocol specifies it.
* **Phone Off:** If a phone is turned off when a message expires, it will never receive that message when it turns back on.