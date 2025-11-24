# 📱 Android Intern Sample Task – Kotlin (Android)

This project is an **Android (Kotlin)** prototype made for the **Android Intern Assignment**.  
It includes three sample recording tasks and a Task History screen that stores all completed tasks locally.

---
<img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/ab9cace1-d180-401c-aa53-d1fdd9b50e89" /><img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/b322190f-f6be-4560-93a5-32152a6ba2e4" /><img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/de9ad9fd-6716-4bb0-999e-e867e14cab3e" /><img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/71d584e1-3f00-4ce5-ae99-bd0cb7b9e6cb" /><img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/57a7b37f-5896-40ed-b259-f0e50bcf2931" /><img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/38f5a74a-489b-4fcb-b147-cdd152343a07" />







## ⭐ Objective  
Build an Android app that allows users to perform:

- **Text Reading Task**  
- **Image Description Task**  
- **Photo Capture Task**  
- View all recorded tasks in a **Task History list**

---

# 🚀 Application Flow

## **1. Start Screen**
**Purpose:** Entry point for the app.

**UI Requirements:**
- Heading: *“Let’s start with a Sample Task for practice.”*  
- Sub-text: *“Pehele hum ek sample task karte hain.”*  
- Button: **Start Sample Task**  
- On Click → Navigate to **Noise Test Screen**

---

## **2. Noise Test Screen**
**Purpose:** Check background noise before recording.

**Features:**
- Display decibel meter (0–60 dB)
- Button: **Start Test**
- Mic input or simulated noise level
- If **avg < 40 dB** → “Good to proceed”
- If **avg ≥ 40 dB** → “Please move to a quieter place”
- On success → Navigate to **Task Selection Screen**

---

## **3. Task Selection Screen**
**Purpose:** User selects which task to perform.

**Options:**
1. **Text Reading Task**
2. **Image Description Task**
3. **Photo Capture Task**

Each option opens its respective task screen.

---

# 🎤 Tasks

## **4. Text Reading Task**
**Purpose:** User reads a passage aloud.

**Data Source:**  
`https://dummyjson.com/products`

**UI Features:**
- Show product description text  
- Instruction: *“Read the passage aloud in your native language.”*
- **Mic Button (Press & Hold)**
  - Start recording on press  
  - Stop on release  
  - Duration must be **10–20 seconds**  
  - Show inline errors:
    - “Recording too short (min 10 s).”
    - “Recording too long (max 20 s).”
- After recording:
  - Playback bar  
  - Checkboxes:
    - No background noise  
    - No mistakes while reading  
    - *“Beech me koi galti nahi hai”*
- Buttons:
  - **Record again**
  - **Submit** (enabled when all checkboxes are ticked)

**Saved Format:**
```json
{
  "task_type": "text_reading",
  "text": "Example product description...",
  "audio_path": "/local/path/audio.mp3",
  "duration_sec": 15,
  "timestamp": "2025-11-12T10:00:00"
}
