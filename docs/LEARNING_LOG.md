# Learning log

One short entry per milestone covering concepts learned, gotchas hit, weak spots, and a sentence or two to use in interviews.

## Milestone 0: Setup, health check, and CI

**Concepts**
- The Gradle wrapper pins one Gradle version in the repo. Running `gradlew` downloads it, so every machine and CI build the same way.
- The JDK that compiles the code (`jvmToolchain`) is separate from the JDK that runs Gradle.
- Ktor's `EngineMain` reads `application.conf`, starts the engine (Netty), and loads the listed modules. Only installed plugins run.
- A GitHub Actions workflow has triggers, a fresh runner per run, and steps that either use an action or run a command. `permissions: contents: read` keeps the token read-only.
- A pull request (PR) proposes merging a branch into `main`, with a diff, review comments, and CI checks.

**Gotchas**
- The project generator extracted into a nested folder, and IntelliJ kept reopening the old project.
- `.\gradlew.bat` only works from the project folder, in a terminal whose `JAVA_HOME` points at JDK 25.
- A test that used the same wrong path as the route passed while the endpoint was broken.
- Windows line endings and a missing executable bit break `gradlew` on Linux CI. `.gitattributes` and `git update-index --chmod=+x` fix them.

**Weak spots**
- Ktor vs Spring Boot. Name concrete tradeoffs (explicit plugins, coroutines, Spring's ecosystem and built-in DI) instead of "lighter".
- CI enforcement. CI only reports a result. Branch protection rules with required status checks block the merge. Merge conflicts and required reviews are separate from CI.

**Interview lines**
- "I chose Ktor because it's explicit and coroutine-based. I install only the plugins I need, and handlers don't block threads while waiting on I/O. For a large team that wants security, data access, and monitoring built in, Spring Boot would be the stronger choice."
- "One of my tests passed while the endpoint was broken, because it copied the route's mistake. Now I write tests from the requirement and watch each one fail before making it pass."
