# listing-watch plan

## The project

**listing-watch** is a small REST API that tracks product listings from online marketplaces and flags ones that look like counterfeits. A brand registers itself with a few details (name, keywords, a normal price range). Listings get submitted for that brand. The API can run a check on a listing, which asks an LLM to judge whether it looks suspicious and stores the verdict.

It's a toy version of real brand-protection software, which keeps it realistic without being big. Don't name any specific company in the repo. The README should describe it on its own terms.

### Data model (starting point; refine it with Tyler in Milestone 2)
- **brands**: id, name, keywords, min_price, max_price, created_at
- **listings**: id, brand_id, marketplace, title, price, currency, seller_name, url, created_at
- **checks**: id, listing_id, verdict (likely_counterfeit / suspicious / looks_ok), confidence, reasons, model, created_at

Store money as `NUMERIC`, never as a float. Decide with Tyler how to store keywords and reasons (a Postgres array, JSON, or a separate table) and discuss the tradeoffs.

### Endpoints (rough)
- `GET /health`
- `POST /brands`, `GET /brands/{id}`
- `POST /brands/{id}/listings`, `GET /brands/{id}/listings`
- `POST /listings/{id}/check` runs the LLM check and stores the result
- `GET /listings/{id}/checks`

## Milestones

Rough size estimates assume part-time work.

### Milestone 0: Setup, orientation, and CI (1 session)
- Split the original handoff into `CLAUDE.md` and this file, then delete it.
- Generate a minimal Ktor project with a `GET /health` endpoint and one test for it, the Gradle wrapper, `.gitignore`, `.gitattributes` (so scripts keep the right line endings on Windows and Linux), and an empty `docs/LEARNING_LOG.md`.
- Push to the GitHub repo and add a GitHub Actions workflow that runs the tests on every push and PR.
- **Concepts:** Gradle Kotlin DSL and the wrapper, how a Ktor app starts (engine, modules, plugins), project layout, CI basics.
- **Interview angles:** Ktor vs Spring Boot. Netty vs CIO as the server engine. What the Gradle wrapper guarantees for a team. What CI should block a merge on.
- **Done when:** `test` and `run` both work locally, CI is green on the PR, and the PR is merged.

### Milestone 1: Routes and JSON, in memory (1 to 2 sessions)
- Brand and listing endpoints backed by an in-memory store, with request and response data classes, input validation, and proper HTTP status codes.
- Centralized error handling with Ktor's StatusPages plugin.
- Route tests with `testApplication`.
- **Concepts:** routing, kotlinx.serialization, status codes, validation, why errors are handled in one place.
- **Interview angles:** Choosing status codes (201 with a Location header, 400 vs 422, 404, 409). Idempotency of HTTP methods (POST vs PUT). Validating at the boundary. Thread safety of a shared in-memory store under concurrent requests. Kotlin features that model a domain well (data classes, sealed classes, value classes for IDs). Fakes vs mocks in tests.

### Milestone 2: PostgreSQL with Exposed (2 sessions)
- Swap the in-memory store for PostgreSQL through a small repository layer, so routes don't touch the database directly.
- Define tables with Exposed and create the schema. Introduce a migration tool like Flyway only if Tyler wants to, and explain why teams use one. Add a `docker-compose.yml` for local Postgres.
- Connection pooling with HikariCP. Database settings in `application.conf`, overridable by environment variables.
- Database tests. Choose the approach with Tyler. Testcontainers runs a real Postgres in Docker and is the industry standard, and GitHub's Ubuntu runners have Docker. Embedded Postgres needs no Docker but is less common.
- **Concepts:** SQL refresher (keys, joins, indexes), Exposed DSL vs DAO, transactions, why database calls run in `newSuspendedTransaction` or on `Dispatchers.IO`, blocking vs non-blocking I/O, connection pooling, config vs secrets, and what R2DBC changes (discussion only, no implementation).
- **Interview angles:** Exposed vs JPA/Hibernate. The N+1 query problem. Which columns to index and why, and reading `EXPLAIN ANALYZE` output for a real query. Transaction isolation levels, with a concrete race condition. How to size a connection pool. Why a repository layer exists. Running migrations safely when several app instances deploy.

### Milestone 3: The LLM check (2 sessions)
- A `ListingChecker` interface with a real implementation that calls the LLM API through the Ktor HTTP client, and a fake for tests.
- A prompt that includes the brand's details and the listing and gets back **structured JSON** (verdict, confidence, reasons). Compare two approaches with Tyler: asking for JSON in the prompt and parsing it, or using the API's tool use or structured output support to enforce a schema. Parse safely and handle bad or failed responses.
- Timeouts, a simple retry policy, and storing each result in `checks`.
- Real calls need prepaid Anthropic API credits and are optional. Everything can be built and tested with the fake.
- **Concepts:** the Ktor HTTP client, dependency injection by hand (constructor parameters, no framework needed), prompt design for structured output, handling unreliable external services, cost and latency tradeoffs of LLM calls.
- **Interview angles:** Which errors are worth retrying (timeouts, 429, 5xx) and which aren't. Exponential backoff with jitter. The circuit breaker pattern. Prompt injection, since sellers write the listing titles and that text goes straight into the prompt. Running the check inside the request vs in a background job queue (202 Accepted, a worker, status polling). Cost and latency control through model choice, token counts, and caching. How you'd measure whether the checker gives good verdicts. Hand-written DI vs a framework like Koin.

### Milestone 4: README, polish, and mock interview (1 to 2 sessions)
- A README Tyler is proud of. It should cover what the project does, an architecture overview (a simple diagram is welcome), how to run it locally, how to run the tests, a CI badge, and design decisions with tradeoffs. Draft it together, with Tyler writing or rewriting key parts in his own voice. Be accurate about how it was built. It's a learning project built with AI assistance, and he should be comfortable saying so.
- **Mock interview.** Claude plays a backend interviewer for about 30 to 45 minutes. Cover a two-minute project pitch, tracing one request from the route to the database and back, defending two or three design choices, a scaling follow-up ("How would this handle ten million listings a day?"), one SQL question, and one coroutines question. Give honest feedback afterward and record weak spots in the learning log.
- **Concepts:** what a good README shows a reviewer, how to present a project in an interview.
- **Interview angles:** Scaling a service (stateless instances, read replicas, caching, queues, and where Redis and OpenSearch would fit). What you would do differently next time. How you used AI tools responsibly (reviewing output, writing tests, understanding every line).

### Stretch milestone: Observability with Datadog (after Milestone 4, about 3 sessions plus the trial)
**Why:** Hands-on experience instrumenting and troubleshooting a real app with Datadog Real User Monitoring (RUM), Application Performance Monitoring (APM), and LLM Observability. Tyler uses Datadog's 14-day free trial and starts it only once the app runs end to end, so Part 1 must be finished and tested before the trial clock starts.

**Prerequisites:** Milestones 2 and 3 merged (Postgres and the LLM check) and Docker Desktop installed.

**Ground rules**
- Claude sets up and explains the instrumentation. Tyler does all of the investigating. In Part 3, Claude only sets up each scenario, never diagnoses it, and gives hints only when Tyler asks.
- `DD_API_KEY`, the RUM application ID, and the RUM client token come from environment variables only and are never committed. The README lists the variable names with no values. The client token is designed to be visible in the browser, so "environment only" means it's injected at runtime, not hidden from people viewing the page.
- Instrumentation is off by default. `test`, `run`, and CI behave exactly as before. The default build never downloads or references the Datadog agent jar.

**Part 1: prepare before the trial starts**
- **Opt-in launch.** Add a separate Gradle task (for example `runWithDatadog`) that attaches `dd-java-agent` from a path given in an environment variable and fails with a clear message if it's missing. The jar is downloaded by hand and kept out of the repo. Compare this with `JAVA_TOOL_OPTIONS`, which attaches the agent to every JVM started from that shell, Gradle included. Never set it globally, since that would also attach it to Tyler's other projects.
- **How a Java agent works.** Explain `-javaagent`, the `premain` entry point, and how the agent rewrites library bytecode as classes load. That's why Netty, JDBC, and HTTP clients get traced without code changes.
- **Check what actually gets traced in this stack.** Look up current dd-trace-java support for the Netty server, Ktor routing, Kotlin coroutines, JDBC through Exposed, and the Ktor client engine chosen in Milestone 3. Two known risks: coroutines can switch threads and lose trace context, and an uninstrumented client engine means the outbound LLM call won't show up as a span. Confirm on the first real run, and add manual spans only where needed.
- **Unified service tagging.** Set `DD_SERVICE`, `DD_ENV`, and `DD_VERSION` (taken from the Gradle `version`), and use the same values in the RUM config. Explain why mismatched tags break the links between products.
- **Datadog Agent or agentless.** Decide with Tyler. APM traces from the Java tracer normally go through a Datadog Agent, while agentless mode mainly applies to LLM Observability (`dd.llmobs.agentless.enabled`). Verify both against the current docs. If the Agent wins, run it in `docker-compose.yml` under a separate profile, so a plain `docker compose up` doesn't start it.
- **LLM Observability.** Plan the setup with `dd.llmobs.enabled` and `dd.llmobs.ml.app`, checking the current docs for exact settings and the minimum tracer version. The LLM call goes through the raw Ktor client, so auto-instrumentation probably won't recognize it as an LLM call. It will likely need manual spans through the Java LLM Observability SDK, which may add a compile-time dependency. Ask before adding it, and confirm it does nothing when the agent isn't attached.
- **Cost check.** Real prompt and token data needs real Anthropic calls, which use prepaid credits. Decide with Tyler between a small model with a handful of calls, or LLM spans around the fake checker that show the structure without real tokens.
- **A small frontend.** One static HTML page served by Ktor (`staticResources`, no new dependency). It lets Tyler create or pick a brand, submit a listing, run a check, and see the verdict. Plain HTML and JavaScript, no framework.
- **RUM SDK.** Load Datadog's browser SDK on that page. Its settings (application ID, client token, site, service, env, version) come from a small endpoint that builds them from environment variables and turns RUM off when they're unset.
- **RUM-to-APM linking.** Configure `allowedTracingUrls` so the SDK adds tracing headers to API calls. Explain the headers (Datadog's `x-datadog-*` headers and the W3C `traceparent` and `tracestate`). The page and the API share an origin, so the browser needs no CORS (Cross-Origin Resource Sharing) setup for these calls. Explain what a cross-origin setup would need (a preflight request and `Access-Control-Allow-Headers` listing the tracing headers), and optionally serve the page from a second port to see that failure for real.

**Part 2: once the trial starts (Tyler drives)**
- Confirm data appears for RUM sessions, APM traces (route, then the Postgres query, then the outbound LLM call), and LLM Observability (prompts, responses, latency, tokens).
- Claude gives a short tour of each product area, then Tyler explores on his own.

**Part 3: break it on purpose (Tyler diagnoses, Claude only sets up the scenario)**
1. **Missing sessions:** lower `sessionSampleRate`, then visit with an ad blocker on.
2. **Incorrect tagging:** ship a build with a wrong env or version tag.
3. **Front-end error:** a button that throws a JavaScript error.
4. **Slow backend:** an artificially slow Postgres query or a missing index. This connects to `EXPLAIN ANALYZE` from Milestone 2.
5. **Flaky dependency:** the LLM call times out or returns malformed JSON. Watch how it shows up across APM, LLM Observability, and RUM. The Milestone 3 retry policy may hide some failures, and that's worth noticing too.

For each scenario, Tyler writes a short playbook in `docs/playbooks/` with the symptom, how he found it, the root cause, and the fix. Afterward Claude reviews each one and says what an experienced engineer would have checked that he missed.

- **Done when:** all three products show live data, RUM-to-APM linking works, Tyler has written all five playbooks himself, and he can explain the setup without notes.
- **Learning log:** an entry with interview talking points about tracing a problem across the browser, the backend, and the LLM call.

### Stretch goals (only if Tyler wants them, one at a time)
- **Redis:** cache check results so re-checking an unchanged listing doesn't call the LLM again. Runs free in Docker.
- **OpenSearch:** index listings and add a search endpoint (full-text search on titles). Runs free in Docker.
- **gRPC:** expose the check as a gRPC service alongside REST, and compare the two.
- **Coroutines in depth:** check a batch of listings concurrently with structured concurrency and a concurrency limit.
- **Deploy to AWS:** run it on AWS with a managed Postgres database, and explain the pieces involved (accounts, IAM, compute, RDS, networking). This can cost money. Before creating any resource, check the current free tier terms with Tyler, set up a budget alert, and plan how to tear everything down afterward.

## Definition of done for the whole project
- All milestones 0 to 4 merged, tests green in CI.
- `docs/LEARNING_LOG.md` has an entry for every milestone.
- The mock interview is done, and its weak spots have been reviewed.
- Tyler can, without notes, walk someone through a request from the HTTP route down to the database and back, explain the LLM check end to end, and defend two or three design choices.
