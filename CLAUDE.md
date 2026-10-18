# listing-watch

A learning project. Tyler is building a small Kotlin REST API to learn backend development and practice for technical interviews. The project details and milestones are in `docs/PLAN.md`. Read it at the start of each milestone, not every session.

## Tyler
- Strong in Kotlin (game development with LibGDX, LibKTX, and Fleks) and Java (REST integrations and test tooling). Knows Git and Android Studio.
- Rusty on SQL, server-side coroutines, and testing web services. New to Ktor, Exposed, Postgres, Docker, CI, LLM APIs, and cloud platforms.
- Wants to write most of the code himself and be able to explain every line.

## Writing style
Use plain, natural writing in the README, PR descriptions, commit messages, and docs. Don't use em dashes to interject a clause, and don't use colons to splice two clauses together in prose.

## Working agreement
1. **Explain before building.** Start each milestone with a short explanation of its concepts and how production teams use them. Propose a plan and wait for Tyler's go-ahead.
2. **Ask which mode he wants.** Drive (you scaffold, he fills `TODO(tyler)` gaps, you review), Pair (small explained steps with pauses), or Watch then quiz. Default to Drive. Never write large files for him unasked.
3. **Show alternatives.** For each design choice, name the main alternative and why it wasn't picked.
4. **Keep it small.** No features beyond the milestone. Ask before adding any dependency.
5. **One branch and one PR per milestone.** Small, readable commits with no `Co-Authored-By` line. PR descriptions have three short sections: what changed, key concepts, how to run or test it.
6. **Cover the interview angles** listed for the milestone in `docs/PLAN.md` while the related code is being written.
7. **End each milestone with three interview-style questions** from those angles. Give honest feedback, including what a strong answer would add.
8. **Update `docs/LEARNING_LOG.md`** after each milestone with concepts, gotchas, weak spots, and one or two sentences Tyler could say in an interview. Keep it tight.
9. **Never commit secrets.** Keys come from environment variables only.
10. **Be token-efficient.** Read only what you need and don't restate the plan every turn.

## Budget
Keep the project free. Ask before anything that costs money. Real LLM calls are optional, since tests and development use a fake.

## Stack
Kotlin on JDK 25 (Amazon Corretto), Gradle Kotlin DSL with the wrapper, Ktor (server and client), kotlinx.serialization, Exposed (DSL first) on PostgreSQL with HikariCP, the Anthropic API (key from `ANTHROPIC_API_KEY`), JUnit with Ktor's `testApplication`, GitHub Actions. Check current stable versions instead of relying on memory. Exposed 1.0 renamed its packages, so older examples are out of date.

## Environment
- Windows 10 with IntelliJ IDEA. Give commands for PowerShell (`.\gradlew.bat test`) and for Linux or macOS (`./gradlew test`).
- Never change the global `JAVA_HOME` or `PATH`. Java 8 is on `PATH`, and Tyler has a game release to protect. When running Gradle, set `JAVA_HOME` for that command only. The JDK is at `C:\Users\Admin\.jdks\corretto-25.0.4.1`.
- CI runs on GitHub Actions (Ubuntu) and must pass from a clean checkout.
- Tests never call the real LLM API. The LLM call sits behind an interface, and tests use a fake.
- Docker Desktop arrives in Milestone 2 for local Postgres.
- Repo: https://github.com/tdavi156/listing-watch
