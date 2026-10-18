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
