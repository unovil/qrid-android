# QR-ID: A Research Project 📸🌈

Welcome to the repository for QR-ID, an application designed and coded by me for my group's research requirements.
This was our research entry for our Grade 11, school year 2024 - 2025.

## Why create QR-ID?

The goal of this app is to allow administrators to scan public school student ID attendance using QR Codes. This is an 
alternative to the current digital methods such as NFC and biometrics, which have their own set of problems.

**Biometrics authentication** is arguably the most secure as fingerprints are (mostly) unique. However, especially as
disease rates rise and hygiene in many public schools is abyssmal, it raises health concerns and can even be a cause
of contact illnesses.

**NFC authentication** is also very common among universities and private schools, however the cost of making IDs with
NFC chips are expensive, and special equipment just for NFC identification is even more expensive. Also, NFC IDs
cannot be replicated without special hardware, and are prone to weathering and rain damage depending on the quality.

**QR codes** are thus the safest and most reliable option for public schools. They can be easily replicated just by
taking a picture of the code. There is no special equipment needed to scan a QR code, and any camera or smartphone
will suffice. QR code production is also extremely cheap on plastic card IDs as they are just pictures.

## Technology used to create QR-ID

* Kotlin - app language
* Android Studio - project environment
* MLKit and CameraX - barcode and camera
* Jetpack Compose - framework
* Supabase - database
