# GI Health App  

## Overview  
The GI Health App is designed to help users track, manage, and better understand what triggers their gastrointestinal health. It provides tools for logging symptoms, meals, weight changes, and daily habits, while helping users identify patterns over time.

---

## Motivation  
We created this app to make digestive health tracking simple and useful. Many existing apps are either too complex or not focused on GI-specific needs, so our goal was to build something clean, easy to use, and actually helpful for everyday users.

---

## Features  

### Meal Tracking  
- Log meals throughout the day  
- Capture nutritional information  
- Record time and date of meals  

### Wellbeing Insights  
- Mood tracking (happiness score)  
- Journal logging at any time  
- Compare lifestyle habits with GI trends  

### Health Monitoring  
- Track weight over time  
- Monitor alcohol and cigarette consumption  
- Record medications and dosages  

### Data Visualization  
- Graphs for nutrition, weight, mood, and habits  
- Trend tracking over time  

---

## Project Structure  

```
GI-Health-App/
│── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── assets/                         # App assets
│   │   │   ├── java/com/example/gihealth/
│   │   │   │   ├── data/                      # Database, DAO, data handling
│   │   │   │   ├── models/                    # Data models / entities
│   │   │   │   ├── ui/                        # UI layer (Jetpack Compose)
│   │   │   │   │   ├── logroutes/             # Navigation / routing
│   │   │   │   │   ├── onboarding/            # Onboarding screens
│   │   │   │   │   ├── screens/               # Main app screens
│   │   │   │   │   ├── theme/                 # UI theming
│   │   │   │   ├── viewmodel/                 # ViewModels
│   │   │   │   ├── utils/                     # Helper functions
│   │   │   │   ├── MainActivity.kt
│   │   │   ├── res/
│   │   │   ├── AndroidManifest.xml
│   │   ├── androidTest/
│   │   ├── test/
│── .idea/
│── .gitignore
│── build.gradle.kts
```
### How the App is Structured  

The app follows an MVVM (Model-View-ViewModel) architecture:

- **Models (`models/`)** → Define the data structure used throughout the app  
- **Data (`data/`)** → Handles database operations using Room  
- **UI (`ui/`)** → Built with Jetpack Compose and contains all screens and navigation  
- **ViewModels (`viewmodel/`)** → Manage app logic and state between UI and data  
- **Utils (`utils/`)** → Helper functions used across the app  

### How Screens Are Built  

All screens are built using Jetpack Compose and are located in the `ui/screens` folder. Each screen is implemented as a Kotlin file and represents a specific feature such as logging food, tracking weight, journaling, or viewing analytics.  

Navigation between screens is handled through the `logroutes` package, allowing smooth transitions across the app.  

## Technologies Used  

- **IDE:** Android Studio  
- **Language:** Kotlin  
- **UI Framework:** Jetpack Compose  
- **Architecture:** MVVM (ViewModel + LiveData)  
- **Database:** Room  
- **Async:** Kotlin Coroutines  
- **Version Control:** Git 

---

## Dependencies  

- Jetpack Compose  
- AndroidX Lifecycle (ViewModel, LiveData)  
- Room Database  
- Kotlin Coroutines  

---

## Setup
Before running the GI Health App you need to make sure you have the following things download into your computer. We will be covering both Mac and Windows installation.

1. **Android Studio** 
- Download and Install from [Android Studio](https://developer.android.com/studio)

2. **Git**
- Download and Install Git from [GIT](https://git-scm.com/downloads)
- Once Downloaded Verify Installation with:
     ```bash
  git --version
     ```
  - If further help is needed here is a youtube Video [Mac](https://www.youtube.com/watch?v=9GZmaxaQV0c) or [Windows](https://www.youtube.com/watch?v=t2-l3WvWvqg)
  - Never used GIT before not a problem here is a tutorial [video](https://www.youtube.com/watch?v=MnUd31TvBoU&t=217s)

  ## Once installed, you can clone repository:
     ```bash
  git clone https://github.com/BryanLucio12/GI-Health-App.git
     ```
     ```bash
  cd GI-Health-App
     ```
## Team  

- Nicky Joy Thayil  
- Ajay Ahluwalia  
- Luis Compean  
- Cody Mercer  
- Bryan Lucio  
- Erik Guerrero 
