# Trinity Mobile Client

## Contributors

- James Marshall - ST10434366
- Lyle Richardson - ST10322521
- Sky Martin - ST10286905
- Simon Slingerland - ST10438897

## Project Overview

The Trinity Mobile Client is a native Android application for PROG7314 Part 2.

The application is being designed to provide a mobile interface for interacting with a remote instance/Trinity server and its connected agents. The Android client combines Firebase Authentication, local settings persistence, and RESTful API integration to provide authenticated access to current functionality.

---

## Purpose and Scope

The purpose of the Trinity Mobile Client is to create a mobile client for the Trinity System that is currently in development.

Part 2 of this project is a prototype focusing on some core features such as:

- User registration and authentication
  - Email and Password
  - Single Sign-On (SSO)

- Persistent application settings
  - Theme selection
  - Automatic application lockout during absence

- Communication with the Trinity REST API currently in development
  - Server status
  - Server information
  - Listener management
  - Task management
  - Agent command functionality
  - Remote payload configuration and build

- Logging and error handling

---

## Architecture

The project uses a layered structure that separates the Android user interface, authentication, local persistence, and network communication.

### High Level Architecture

[![]\(https://mermaid.ink/img/pako\:eNptkm9v2jAQxr-K5dcQCND801QpLXSgbcBIUKUte2GSI1gQO3Ic2o7y3XcOUBUxv_Kdf_fc47MPNJUZ0ICud_Il3TClSTxMBMFV1atcsXJDQpEpybPfCT3vSFiWO54yzaVI6J8TbtZygtCTYnkBQlekQ8JU8z3XHEywnFzB4TIeG81ab5D-n1w0iuPJ9GuEVARac5EbmYWUxRU2HcXPs8U3pKagX6Ta_mCC5aCuoMUoXsyeJjFSC9BKrrlGrdl2rHXZgJ9vQdrt-8bfTfJi6ebgbOLGVXN4aX46BZFd2oVzM7EwmluIk0epgDzDyqS_rFTnnpW8s7c7lmWZolJynOons8MHLJ7LSucKop_fm5IxhpARKchyVQtdkwjU_jyLU9XFjHH2jtMYRTFOYhzH84S-m9Yf1s7EkGm2YhUQlqZQVYYaPtAWzRXPaKBVDS1agCqYCenBlCcUH7WAhAa4VZDVr-2MqW07lTuJXhJxxPqSiV_4lhcJJet8Q4M121UY1WXGNAw5wz9YfGQVjgHUo8Sb0cD2Gw0aHOgrRgPXct2-7zme7Qy6Tu-uRd9o4HUtv3_X7Xld17Fd2_OOLfq36dq1XL_nOp7T8-2-bw-cwfEfJU7nSg?type=png\)](https://mermaid.live/edit#pako:eNptkmFv2jAQhv-K5c8QGpoAiaZKaaEDbQOWBFXqsg8mOYIFsSPHoe0o_33nAFURzSff3XPvvT5nT1OZAfXpaitf0jVTmsTDRBD8qnqZK1auSSAyJXn2J6GnEwnKcstTprkUCf17xM23mCD0qFhegNAV6ZAg1XzHNQcTLCYXcLCIx0az1mukv5KLRnE8mX6PkIpAay5yIxNKWVxg01H8NAt_IDUF_SLV5hcTLAd1AYWjOJw9TmKkQtBKrrhGrdlmrHXZgJ9vQdrtu8bfVfJs6apwMnHlqimehx-rILLzuGBuNhZEcwtx8iAVkCdYmvS3percsZJ3dnbHsizTVEqOW_1kdniPzXNZ6VxB9Ptn0zLGEDIiBVksa6FrEoHanXZx7DqbMc7ecRujKMZNjON4ntB3M_rD2okYMs2WrALC0hSqylDDe9qiueIZ9bWqoUULUAUzId2b9oTioxaQUB-PCrL6tZ0xtWmncivRSyIO2F8y8YxveZZQss7X1F-xbYVRXWZMw5Az_AeLj6zCNYB6kHgz6tu9RoP6e_qK0c3Achyn2_Xsruv1ey5W36jftvuWfes6bt_zuq5727cPLfqvGXtjeR5mBp7j9Qeu23Ptw38HROd6)

The application also has a separate local Room database that currently stores basic user-specific application preferences.

### Android Layer

The application separates packages into:

- `auth` - Firebase authentication
- `api` - REST API communication
- `database` - Room database
- `settings` - User preference management
- `model` - Application data models
- `ui` - UI-specific components

The authentication layer uses `AuthProvider` and `AuthRepository` to abstract Firebase Authentication from the UI.

The settings layer uses `SettingsRepository`, Room, and `UserPreferences` to keep user-specific settings locally.

`NetworkManager` and `ApiClient` provide the centralised REST communication.

---

## Technology Stack

**_Android:_** Kotlin, Android SDK, AndroidX, Material 3, View Binding

**_Authentication:_** Firebase Authentication, Google Identity, Credential Manager

**_Local storage:_** Room/SQLite

**_Networking:_** Retrofit, OkHttp, Gson

**_Backend:_** ASP.NET Core Web API, PostgreSQL, Ubuntu

**_Development:_** Gradle, Git, GitHub, GitHub Actions

---

## Testing and CI

Application logic is covered by automated unit tests and can be manually triggered with:

`./gradlew test`

`./gradlew assembleDebug`

GitHub Actions automatically builds and tests the project on pushes and pull requests.

---

## Version Control

The project is maintained using Git and GitHub. Feature branches are made as new features are added, and regular commits, pushes, and pull requests integrate desired changes.

![[image.png]]

---

## Demonstration

Video Demonstration:

---

## Disclosure of AI Usage

AI usage will be documented as entries using the format below:

Disclosure of AI Usage in my Assessment:

```
Sections: README file.

Name of AI tool(s) used: ChatGPT.

Purpose/intention behind use: To fix bad spelling

Date(s) in which generative AI was used: 22/09/2026

A link to the actual generative AI chat: [\[https://generic.ai/share/xyz\](https://generic.ai/share/xyz)](https://chatgpt.com/share/6ab29fc8-90fc-83ea-ad52-27d710697265)
```

```
Sections: Values/Colors

Name of AI tool(s) used: ChatGPT.

Purpose/intention behind use: To generate a light palot

Date(s) in which generative AI was used: 22/09/2026

A link to the actual generative AI chat: [\[\\[https://generic.ai/share/xyz\\](https://generic.ai/share/xyz)\](https://chatgpt.com/share/6ab29fc8-90fc-83ea-ad52-27d710697265)](https://chatgpt.com/share/6ab2a08c-bc58-83ea-bea1-4464cda5734c)
```

---
