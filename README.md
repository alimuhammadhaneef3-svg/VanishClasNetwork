# 📱 Vanish Class Network

**Vanish Class Network** is a modern, ephemeral Android chat application built with Kotlin and Jetpack Compose. Designed for a shared classroom environment, the app's core promise is absolute privacy through impermanence: **every message permanently vanishes from the screen and the local database exactly 30 seconds after it is sent.**

Built as the final project for the Mobile Application Development (MAD) course, this app demonstrates advanced Android architecture, robust offline handling, and precise background timing.

---

## ✨ Key Features

*   **⏱️ Absolute 30-Second Timer:** Uses absolute timestamps (`expiresAt`) rather than UI countdown loops. The timer remains 100% accurate even if the app is backgrounded, rotated, or killed.
*   **📡 Offline Queue & Auto-Sync:** Messages composed while offline are saved to a local Room database with a `PENDING` status. When the network returns, the app automatically flushes the queue and sends them exactly once.
*   **🚫 Expiry Drop (The "0:20 to 0:50" Rule):** If a message is queued offline and expires before the network returns, it is automatically dropped and permanently deleted. It will never be sent.
*   **🗄️ Permanent Database Deletion:** Hiding a message on the UI isn't enough. A background Coroutine loop executes raw SQL `DELETE` queries to permanently wipe expired messages from the SQLite storage.
*   **🧠 MVVM Architecture:** Clean separation of concerns using `AndroidViewModel`, `StateFlow`, and Jetpack Compose state hoisting.
*   **🎨 Modern UI/UX:** Built entirely with Jetpack Compose and Material 3. Features clean message bubbles, status indicators, and a responsive layout.

---

## 🛠️ Technology Stack

*   **Language:** Kotlin
*   **UI Toolkit:** Jetpack Compose (Material 3)
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Local Database:** Room (SQLite)
*   **Asynchronous Programming:** Kotlin Coroutines & Flow
*   **Networking (Mock):** Interfaces prepared for WebSocket integration

---

## 🚀 How to Build and Run

1.  Clone this repository to your local machine:
    ```bash
    git clone https://github.com/alimuhammadhaneef3-svg/VanishClassNetwork.git
