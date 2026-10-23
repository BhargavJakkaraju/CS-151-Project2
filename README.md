# Gym Membership System
CS 151 — Fall 2026 — Project 2

## Overview
A Java console application for managing gym members, memberships,
trainers, fitness sessions, and bookings.

Users can purchase, renew, freeze, and resume memberships; check in;
and manage group classes and personal training sessions.

Data is stored in memory and is cleared when the program exits.

## Developers
- Member 1: Min Myat Thu
- Member 2: [Add name]
- Member 3: [Add name]

## Design
- `Member` manages membership information and membership actions.
- `Gym` manages registration, check-ins, sessions, and bookings.
- `Trainer` manages session assignments and unavailable times.
- `FitnessSession` is the abstract parent of `GroupClass` and
  `PersonalTrainingSession`.
- `Bookable` defines enrollment and removal operations for both
  session types.
- `Booking` tracks enrollment, cancellation, rescheduling, and attendance.
- `ConsoleUI` handles user input, and `Main` starts the application.

The system uses inheritance, interfaces, and polymorphism to handle
different fitness sessions. Custom exceptions report full sessions,
duplicate enrollment, and invalid operations.

## Installation
Install a Java Development Kit. Development used JDK 25.0.1.

Clone the repository:

```bash
git clone https://github.com/BhargavJakkaraju/CS-151-Project2.git
cd CS-151-Project2
```

Compile from the project root:

```bash
javac -d out src/gym/*.java
```

## Usage
Start the application:

```bash
java -cp out gym.Main
```

Enter the number of a menu option and follow the prompts.

For a booking:
1. Register a member and purchase a membership.
2. Register a trainer.
3. Create a future fitness session.
4. Book the member into that session.

Enter session times in this format: `2026-12-01T14:30`.
The session must occur before the member's membership expires.

Enter `EXIT` at any prompt to quit, or choose `0` from the main menu.

## Testing
Compile the source files and membership tests:

```bash
javac -d out src/gym/*.java test/gym/MemberTest.java test/gym/GymTest.java
```

Run each test program:

```bash
java -cp out gym.MemberTest
java -cp out gym.GymTest
```

These tests cover membership actions, member identity, registration,
duplicate prevention, check-in validation, and member capacity limits.

## Contributions
### Member 1 — Min Myat Thu
- Implemented `Member` and `Gym`.
- Created `Main` and the console menu.
- Connected membership, trainer, session, and booking features to the menu.
- Added membership and gym tests.
- Added integration fixes and validation.
- Added README installation, usage, and test instructions.

### Member 2 - Bhargav Jakkaraju
- Implemented abstract `FitnessSession` base class.
- Implemented `Trainer` (assignment, cancellation, blocking, availability).
- Implemented `PersonalTrainingSession` (client assignment, exercise plan, completion tracking, Standard vs. Premium pricing).
- Added unit tests `TrainerTest` and `PersonalTrainingSessionTest`.


### Member 3
Add your contributions here.