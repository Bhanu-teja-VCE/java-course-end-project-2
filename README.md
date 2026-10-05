# 🏏 Campus Premier League (CPL) — Java Course End Project

**Course:** Object Oriented Programming through Java (VCE-R25)  
**Department:** Computer Science & Engineering  
**Institution:** Vardhaman College of Engineering, Hyderabad  

---

## 📌 Project Overview

The **Campus Premier League (CPL)** is an end-to-end, high-performance desktop application modeling a complete T20 franchise cricket tournament:
- **Live Multi-threaded Player Auction:** Real-time player bidding with an autonomous `Auctioneer` coordinator thread and 4 competing AI franchise bots using Java thread monitors (`synchronized`, `wait()`, `notifyAll()`).
- **Realistic T20 Match Simulator:** Ball-by-ball physics engine accounting for batter vs. bowler duel mechanics, pitch degradation, over phase urgency, and pressure dynamics.
- **Custom 2D Graphics Canvases:** Custom AWT `Graphics2D` rendering for 360-degree radial shot Wagon Wheels and comparative Run-Rate Worm graphs.
- **Tournament League & Official Net Run Rate (NRR):** 56 round-robin fixtures, live Points Table with mathematical Net Run Rate calculations, Orange Cap / Purple Cap leaderboards, and playoffs.
- **Dual-Mode Persistence:** Online mode with MySQL 8.0 (stored procedures, callable statements, ACID transactions) + 100% resilient offline fallback (in-memory repository & Java Object Serialization).

The complete project codebase, documentation, tests, and assets are located in:  
👉 **[`Course_End_Project_Java_Campus_Premier_League/`](./Course_End_Project_Java_Campus_Premier_League/)**

---

## 🚀 How to Open and Run the Simulator

### Option 1: Instant Launch (Double-Click)
1. Navigate to:  
   `C:\Users\bhanu\Desktop\java cep\shreyan\Course_End_Project_Java_Campus_Premier_League`
2. Double-click **`run_cpl.bat`**.
3. The CPL Stadium GUI will launch immediately!

### Option 2: Run via Terminal (PowerShell / Command Prompt)
```bat
cd "C:\Users\bhanu\Desktop\java cep\shreyan\Course_End_Project_Java_Campus_Premier_League"
.\run_cpl.bat
```

### Option 3: Run the Shaded JAR directly
```bat
cd "C:\Users\bhanu\Desktop\java cep\shreyan\Course_End_Project_Java_Campus_Premier_League\app"
java -jar target/CampusPremierLeague.jar
```

---

## 🎮 Simulator Features & GUI Navigation

| Tab | Screen Name | Key Features |
|---|---|---|
| **Tab 1** | **Auction Hall** | Real-time bidding war, countdown timer (3s), live purse updates, human bid buttons, fast-forward option. |
| **Tab 2** | **Squads & Purses** | Roster viewer for 8 campus franchises, balance tracking, role counters, overseas quotas. |
| **Tab 3** | **Fixtures & Schedule** | 56-match round-robin tournament calendar, status filters, 1-click season simulation. |
| **Tab 4** | **Live Match & Graphics** | Ball-by-ball commentary, radar Wagon Wheel (AWT polar coordinates), Run-Rate Worm graph. |
| **Tab 5** | **Standings & Caps** | Points Table sorted by Net Run Rate (NRR), Orange Cap (top batter) & Purple Cap (top bowler). |
| **Tab 6** | **Thread Monitor** | Live inspection of active Java threads (`RUNNABLE`, `TIMED_WAITING`, `WAITING`, `TERMINATED`). |

---

## 🧪 Testing & Verification
- **25 Passing JUnit 5 Tests:** Unit tests, concurrency tests, and database fallback integration tests.
- **Headless Experiment (200 matches):** Verified average 1st innings score of 152.5 runs, 6.61 wickets, 0 ties, 970 matches/sec throughput, and 0 invariant violations.

---

## 📚 Complete Project Documentation
- 📘 [Detailed Project Documentation (README.md)](./Course_End_Project_Java_Campus_Premier_League/README.md)
- 📐 [Architectural Design Blueprint (DESIGN.md)](./Course_End_Project_Java_Campus_Premier_League/DESIGN.md)
- 📝 [Academic Report Notes & Viva Guide (REPORT_NOTES.md)](./Course_End_Project_Java_Campus_Premier_League/REPORT_NOTES.md)
- 🌐 [Interactive HTML Architecture Explainer (project-explained.html)](./Course_End_Project_Java_Campus_Premier_League/project-explained.html)
