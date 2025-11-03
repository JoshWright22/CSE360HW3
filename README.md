# HW3 System Integration Tests and Javadoc

This repository contains the system integration tests for the application and the generated Javadoc documentation.

## Task Overview

### Task 1: Automated Tests
- Implemented five automated tests based on the user stories:
  1. **Create First User as Admin** – first registered user automatically receives admin privileges and can log in.
  2. **Invite User and Join** – admin creates an invite code; a new user registers with the code and receives the correct role.
  3. **Role Management by Admin** – admin can add and remove roles for other users but cannot remove their own admin role.
  4. **Student Question and Resolve** – students can post questions, other users can submit answers, and questions can be marked resolved with proper list updates.
  5. **Reviewer Create, Update, and Score** – reviewers can create reviews, update them, receive feedback, and update their score.

### Task 2: Internal Documentation / Javadoc
- All tests are documented with Javadoc comments describing their purpose, actions, and expected outcomes.
- Generated Javadoc output is included for the five automated tests.
- Example of professional-looking Javadoc source referenced: [Oracle Javadoc Examples](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html)
  - Compelling aspects: clear formatting, organized summaries, links to related classes, and method-level documentation.

## Generated Javadoc
- Javadoc HTML files are located in the `doc/` directory.
- Key files:
  - `doc/application/SystemIntegrationTests.html` – detailed documentation of each automated test.
  - `doc/application/package-summary.html` – summary of the `application` package.

## Notes
- Tests use simplified stub services instead of a full database to focus on user-story behavior.
- All tests are implemented with `JUnit 5`.
- Running Javadoc does not require Eclipse; it can be generated via the command line:
