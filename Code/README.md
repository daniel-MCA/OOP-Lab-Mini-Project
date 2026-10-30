# AdaptQ — Self-Calibrating Question Bank Engine

A complete Spring Boot + MySQL/H2 web application for adaptive learning. The system uses learner-specific ability, question difficulty, accuracy, response time and topic performance to select the next suitable question and recalibrate after every answer.

## Included
- Landing page with interactive “How it works” demo
- Login + Create Account with Student/Admin role selection
- Student dashboard with separate pages for Overview, Practice, Tests, Skill Matrix, Achievements and Settings
- Subject selection: Java, Python, C++, Operating Systems, DSA
- Topic-aware adaptive practice
- Full adaptive tests
- Time-Attack mode with per-question timer
- Per-question response-time display
- XP, levels/streak-ready gamification state and achievements page
- Accuracy, average time and difficulty visualizations
- Learner-specific ability for every subject
- Question-level live calibration (difficulty + discrimination)
- Learner-level calibration after every response
- Recent-question exclusion so different learner accounts do not simply receive the same sequence
- Admin Command Center with question calibration, difficulty analytics, anomaly alerts and learner profiles
- Responsive mixed light/dark UI with white cards and purple/blue accents
- Seeded question bank with multiple subjects/topics

## Important adaptive behavior
The question bank is shared, but learner state is NOT shared. Each account has independent ability values for Java, Python, C++, OS and DSA.

When a learner answers a question, the engine records:
- correct / incorrect
- time taken
- question difficulty at attempt
- learner ability at attempt
- subject/topic

Then it updates the learner ability and question difficulty. The next question is selected close to that learner’s current ability, with a small stretch target.

## Run locally
1. Open this folder in IntelliJ IDEA.
2. Set the Project SDK to Java 21 or a newer JDK. The Maven build is explicitly configured for Java 21, so it does not fall back to obsolete source 1.5 settings.
3. Open `src/main/resources/application.properties`.
4. The default configuration uses an H2 file database, so the application can run without MySQL setup.
5. Run `com.qbank.QbankApplication`.
6. Open `http://localhost:8080/`.

## Demo accounts
- Admin: `admin` / `admin123`
- Student: `student` / `student123`

You can also create new accounts from the Create Account page.

## MySQL option
If you want MySQL, create a database named `qbank_db` and replace the H2 datasource block in `application.properties` with the MySQL block provided there. Set the real MySQL root password on your machine; no password is hard-coded in this project.

## Main Java packages
- `com.qbank.model` — User, Question, Response, TestAttempt
- `com.qbank.repository` — JPA repositories
- `com.qbank.service` — authentication, response collection, calibration, analytics and test composition
- `com.qbank.controller` — auth, student, practice and admin APIs
- `com.qbank.config` — password encoder, web configuration and data seeding

## Project flow
Landing → How It Works → Login/Create Account → Student or Admin → Student Overview / Admin Command Center → Subject → Topic → Practice/Test/Time Attack → Answer + Time → XP + Calibration → Personalized next question → Analytics.
