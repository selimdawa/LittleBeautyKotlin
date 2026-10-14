<h1 align="center">Beauty Touch - Kotlin</h1>

<p align="center">
 <a><img alt="Min SDK" src="https://img.shields.io/badge/Min SDK-24-020290?logo=android&logoColor=white"/></a>
 <a><img alt="Target SDK" src="https://img.shields.io/badge/Target SDK-37-0EB265?logo=android&logoColor=0EB265"/></a>
 <a href="https://kotlinlang.org"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.4.20-blue?logo=kotlin&logoColor=white"/></a>
 <a href="https://opensource.org/licenses/MIT"><img alt="License" src="https://img.shields.io/badge/License-MIT-CC9900?logo=MIT&logoColor=white"/></a>
</p>

## Day Mode

Home | Shopping Centers | Details Post
--- | --- | --- | 
![](UI/1.jpg) | ![](UI/2.jpg) | ![](UI/3.jpg) | 

Profile | Edit Profile | Favorites 
--- | --- | --- | 
![](UI/4.jpg) | ![](UI/5.jpg) | ![](UI/6.jpg) | 

Earn Points | Challenge 1 | Challenge 2 
--- | --- | --- | 
![](UI/7.jpg) | ![](UI/8.jpg) | ![](UI/9.jpg) | 

About App | About Me | Share App 
--- | --- | --- | 
![](UI/10.jpg) | ![](UI/11.jpg) | ![](UI/12.jpg) | 

## Night Mode

Home | Shopping Centers | Details Post
--- | --- | --- | 
![](UI/13.jpg) | ![](UI/14.jpg) | ![](UI/15.jpg) | 

Profile | Edit Profile | Favorites 
--- | --- | --- | 
![](UI/16.jpg) | ![](UI/17.jpg) | ![](UI/18.jpg) | 

Earn Points | Challenge 1 | Challenge 2 
--- | --- | --- | 
![](UI/19.jpg) | ![](UI/20.jpg) | ![](UI/21.jpg) | 

About App | About Me | Share App 
--- | --- | --- | 
![](UI/22.jpg) | ![](UI/23.jpg) | ![](UI/24.jpg) | 

---

### ✨ Core Functionalities
*   **Robust Authentication Flow**: Secure identity management via **Firebase Authentication**.
*   **Beauty Post & Shopping Centers Management**: Discover beauty posts, shopping centers, and details.
*   **Points & Challenges System**: Earn points and complete interactive beauty challenges.
*   **Context-Aware Theming**: Adaptive UI support for dark and light modes.
*   **Resilient Offline Capabilities**: "Local-first" data strategy using **Room Database**.
*   **Elastic Cloud Synchronization**: Distributed data persistence via **Firebase**.

---

## 🛠️ Built With
*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **UI Framework:** Material Design 3, ViewBinding
*   **Database:** [Room](https://developer.android.com/training/data-storage/room)
*   **Cloud Infrastructure:** [Firebase](https://firebase.google.com/) (Auth, Realtime Database)
*   **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
*   **Async Operations:** Coroutines & Flow

---

### 🏗️ Technical Architecture

#### User App
```text
app/src/main/java/com/flatcode/beautytouch/
├── db/                 # Room Database Configuration & DAOs
├── di/                 # Dependency Injection (Hilt modules)
├── model/              # Data Entities & Models
├── repository/         # Repository Pattern Implementation
├── ui/                 # Presentation Layer
│   ├── adapter/        # RecyclerView Adapters
│   ├── auth/           # Authentication (Login, Register, Forget Password)
│   ├── main/           # Main Dashboard & Navigation
│   ├── post/           # Beauty Posts & Details
│   ├── profile/        # User Profile, Edit Profile & Favorites
│   ├── reward/         # Earn Points & Challenges
│   └── shopping/       # Shopping Centers & Details
└── utils/              # Extensions, Constants & Utility Classes
```

#### Admin App
```text
app/src/main/java/com/flatcode/beautytouchadmin/
├── di/                 # Dependency Injection (Hilt modules)
├── model/              # Data Entities & Models
├── repository/         # Repository Pattern Implementation
├── ui/                 # Admin Presentation Layer
│   ├── ads/            # Ads Management
│   ├── auth/           # Admin Authentication
│   ├── main/           # Admin Dashboard
│   ├── other/          # Points & Rewards Management
│   ├── post/           # Post Management & Approval
│   ├── profile/        # Admin Profile Settings
│   ├── shopping/       # Shopping Centers Management
│   └── user/           # User Management
└── utils/              # Extensions, Constants & Utility Classes
```

---

## 🚀 Getting Started

### Prerequisites
*   Android Studio Ladybug or newer.
*   JDK 21.
*   Android SDK Level 37 (Compile SDK).

### Installation
1.  Clone the repository:
    ```bash
    git clone https://github.com/selimdawa/BeautyTouchKotlin.git
    ```
2.  Open the project in Android Studio.
3.  Sync Project with Gradle Files.
4.  Add your `google-services.json` file to the `app/` directory.
5.  Run the app on your device or emulator.

---

### 🛠️ Technology Stack
*   **Language**: Kotlin (Modern, expressive, and safe development).
*   **Dependency Injection**: **Dagger Hilt** (Simplified, standard DI for Android).
*   **Cloud Infrastructure**: **Firebase** (Scalable authentication and real-time NoSQL storage).
*   **Local Persistence**: **Room** (SQLite abstraction layer for robust offline data).
*   **Concurrency**: **Coroutines & Flow** (High-performance asynchronous data streams).
*   **View Interop**: **ViewBinding** (Safe and efficient UI component interaction).

---

## 🔗 Links & Resources
*   **Legacy Versions:**
    *   [Kotlin Old Code Version](https://github.com/selimdawa/BeautyTouchKotlinOld/)
    *   [Java Old Code Version](https://github.com/selimdawa/BeautyTouch/)
*   **Author:** [Selim Dawa](https://github.com/selimdawa)

---

## 📜 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
