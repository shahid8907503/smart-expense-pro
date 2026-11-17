# Smart Expense Manager Pro — The 200 Master Questions & Answers
**Complete Technical & Architectural Defense Manual · Prepared for Syed Shahid Farwez**

---

## 🧭 Master Guide Navigation
- **[Level 1: Beginner / Foundational Level (Questions 1 to 65)](#level-1-beginner--foundational-level-q1q65)**
  - *Core Concepts, App Purpose, Kotlin Basics, Jetpack Compose, Basic UI Components, Navigation, Inputs & Validation*
- **[Level 2: Intermediate / Core Engineering Level (Questions 66 to 135)](#level-2-intermediate--core-engineering-level-q66q135)**
  - *Room SQLite, DAOs, Coroutines & Dispatchers, StateFlow, Biometrics, Security Flags, WorkManager, Testing & Build Systems*
- **[Level 3: Advanced / Principal Architect Level (Questions 136 to 200)](#level-3-advanced--principal-architect-level-q136q200)**
  - *System Design, ACID & WAL Internals, Concurrency Contention, Idempotency, Conflict Resolution, Memory Profiling, R8 Bytecode, Scaling to 1,000,000 Users*

---

## Level 1: Beginner / Foundational Level (Q1–Q65)

### Q1: What is Smart Expense Manager Pro in one sentence?
**Answer:** It is a 100% offline-first, native Android personal finance application built with Kotlin and Jetpack Compose that enables voice-driven expense tracking, local SQLite persistence, and bank-grade privacy.

### Q2: What core problem does this app solve for users?
**Answer:** It eliminates the manual typing friction of daily expense logging through sub-second voice NLP entry and provides private, uncompromised cashflow analytics without corporate data scraping or subscription paywalls.

### Q3: Why did you build this as an Android app rather than a mobile web page?
**Answer:** Native Android provides direct hardware access to low-latency on-device SpeechRecognizer, hardware BiometricPrompt, WindowManager FLAG_SECURE display protection, and high-performance offline SQLite queries without network latency.

### Q4: What programming language is the Android app written in?
**Answer:** 100% modern Kotlin (v1.9+), leveraging idiomatic features like coroutines, Flow, sealed classes, data classes, and extension functions.

### Q5: What UI toolkit is used for the user interface?
**Answer:** Jetpack Compose with Material Design 3 (M3). There are zero legacy XML view layouts in the presentation layer.

### Q6: What is declarative UI and how does Compose differ from legacy XML views?
**Answer:** In declarative UI, you describe what the UI should look like for a given state; the framework automatically updates the UI when state changes. In legacy XML (imperative UI), you had to manually find views (`findViewById`) and mutate them, which frequently led to state desynchronization bugs.

### Q7: Does the app require an internet connection to function?
**Answer:** No. The application is completely offline-first. All reading, writing, calculating, and budgeting happens locally on the device in under 10 milliseconds.

### Q8: Can a user use the app on an airplane or in areas with no cellular network?
**Answer:** Yes. Because all data is stored in a local SQLite database, 100% of the app's features (logging, analytics, budgets, voice transcription, PIN lock) work with zero connectivity.

### Q9: What minimum Android OS version (minSdk) does the app support?
**Answer:** It targets `minSdk = 26` (Android 8.0 Oreo) and compiles against `targetSdk = 34` (Android 14), covering over 95% of active Android devices globally.

### Q10: What is the approximate size of the compiled release APK?
**Answer:** Approximately 8.7 MB for the production App Bundle (AAB) and ~24.8 MB for the universal standalone debug APK with uncompressed assets.

### Q11: How does the voice expense entry work from a user's perspective?
**Answer:** The user taps the microphone button on the dashboard or voice screen, speaks a natural sentence (e.g., 'Chai aur samosa 40 rupees PhonePe'), and the app automatically extracts the amount, assigns the Food category, sets the payment mode to UPI, and commits the transaction in under 0.5 seconds.

### Q12: What languages does the voice input support?
**Answer:** English, Hindi, and code-mixed conversational Hinglish, along with regional vocabulary dictionaries for everyday Indian spending terms.

### Q13: How does the app prevent accidental voice errors or misheard amounts?
**Answer:** It presents a confirmation preview card allowing the user to verify or tweak the detected amount and category with one tap before committing to the database.

### Q14: What are the 7 primary screens in the application?
**Answer:** 1. Home Dashboard, 2. Transaction History / Activity Feed, 3. Add/Edit Transaction, 4. Budget Manager, 5. Analytics & Charts, 6. Voice AI Assistant, and 7. Profile / Security Settings.

### Q15: What categories are supported by default?
**Answer:** Food, Travel, Shopping, Bills, Health, Education, Entertainment, Rent, and Other.

### Q16: Can users search and filter their past transactions?
**Answer:** Yes. In the Transaction History screen, users can search by merchant name or description, and filter by category, date range, or transaction type (Income vs Expense).

### Q17: How does the monthly budget warning system work?
**Answer:** Users define monthly spending limits per category. When spending reaches 80% of the limit, the indicator turns Amber; if spending exceeds 100%, it turns Crimson Red and triggers an alert.

### Q18: What currencies does the app support?
**Answer:** Indian Rupee (INR - ₹), US Dollar (USD - $), Euro (EUR - €), British Pound (GBP - £), and UAE Dirham (AED), with dynamic exchange rate re-indexing across all cards.

### Q19: What is the Split Bill feature?
**Answer:** A utility that divides group dinner or trip expenses among multiple friends, calculating each person's exact share and generating a one-tap WhatsApp payment link pre-filled with UPI details.

### Q20: What is the EMI Calculator dialog?
**Answer:** A financial tool that calculates monthly loan installments, total interest payable, and total payback amounts based on principal, annual interest rate, and tenure in months.

### Q21: What design system inspired the app's visual identity?
**Answer:** The Coinpay and Revolut fintech design language: Royal Cobalt Blue primary accent, clean porcelain white surfaces, low-glare matte charcoal dark mode, soft pastel circular badges, and tabular numerical typography.

### Q22: Does the app support both Light and Dark themes?
**Answer:** Yes. It automatically matches the system appearance by default, but also provides a dedicated 3-way toggle in Settings: [ System Auto | Light Mode | Matte Dark ].

### Q23: Why did you avoid pure black (#000000) and bright neon colors in dark mode?
**Answer:** Pure black creates harsh OLED contrast and purple smear during scrolling, while glowing neon causes severe eye fatigue during morning financial checks. We used matte obsidian charcoal (#0B0F19 to #111827) for comfortable low-glare viewing.

### Q24: What is the Cashflow Retention bar on the dashboard?
**Answer:** A dual-tone visual progress indicator showing the ratio of retained savings versus expended income for the current month.

### Q25: What is tabular typography and why is it used for monetary figures?
**Answer:** Tabular figures use fixed-width numbers so digits align vertically in columns and lists, preventing visual jitter when numbers animate or update.

### Q26: What are the quick action tools on the dashboard?
**Answer:** One-tap shortcuts for Bank SMS auto-track, Split Bill, Savings Goals, Calendar Expense View, Recurring Bills, Debt/Loan Tracker, and CSV Data Export.

### Q27: How are empty states handled when there are no transactions?
**Answer:** Clean, illustrative placeholder cards with friendly guidance directing first-time users to tap the microphone or plus button to log their first expense.

### Q28: How does the app provide tactile feedback during user actions?
**Answer:** Smooth Compose animations (`animateFloatAsState`, `AnimatedVisibility`) and immediate Toast notifications confirming actions like data saving, email verification, and budget updates.

### Q29: How are dialogs and modal sheets designed?
**Answer:** Bottom-sheet modals with 24dp rounded corners, clear dismissing gestures, and responsive padding ensuring accessibility across both compact and large smartphone displays.

### Q30: What font family is used across the application?
**Answer:** Modern sans-serif typography (Plus Jakarta Sans / Inter hierarchy) with distinct font weights (ExtraBold headers, SemiBold amounts, Medium labels).

### Q31: Can multiple user accounts exist on the same device?
**Answer:** Yes. Each account has its own isolated profile, credentials, and encrypted database records keyed by a unique `userId`.

### Q32: How fast is the login and registration process?
**Answer:** Under 10 milliseconds. Local account creation and session authentication happen directly in local storage without waiting for external network roundtrips.

### Q33: What happens if a user forgets their password?
**Answer:** They tap 'Forgot Password', enter their email, receive a 6-digit OTP code in their Gmail inbox (or view the backup code on screen), and set a new password in under 10 seconds.

### Q34: How does the password reset dispatch emails without a paid third-party email service?
**Answer:** Through an internal `GmailSmtpDispatcher` that connects directly to Google's official SMTP relay (`smtp.gmail.com:587`) using TLS encryption.

### Q35: What input validation is performed during account registration?
**Answer:** Ensuring names are non-empty, emails match valid `@` domain syntax, passwords contain at least 6 characters, and duplicate emails are rejected.

### Q36: What is the demo one-tap login button on the sign-in screen?
**Answer:** A pre-configured testing shortcut (`demo@expense.com` / `Demo1234`) that populates initial dummy transactions so reviewers can test the app without manual typing.

### Q37: How is user session state maintained across app restarts?
**Answer:** In `SharedPreferences` storing an active user session token, user ID, display name, preferred currency, and theme preferences.

### Q38: What happens when a user logs out?
**Answer:** The session token is cleared from memory and disk, and navigation redirects to the Login screen with the backstack completely purged (`popUpTo(0)`).

### Q39: What is the 2-Factor Authentication (2FA) feature in the app?
**Answer:** An optional security layer using Time-Based One-Time Passwords (TOTP) that prompts for a 6-digit authenticator code before granting access to sensitive financial ledgers.

### Q40: How are passwords masked in the UI?
**Answer:** `PasswordVisualTransformation` masks inputs with dots, paired with a clickable eye icon to toggle plaintext visibility for accessibility.

### Q41: What is FLAG_SECURE and why did you enable it?
**Answer:** It is an Android WindowManager security flag that blocks OS screenshots, disables third-party screen recorders, and blanks out the app thumbnail in the Android recent apps carousel to protect financial privacy.

### Q42: How does the Biometric unlock work?
**Answer:** It integrates Android's official `BiometricPrompt` API, allowing users to unlock the app with fingerprint or face recognition, with a secure fallback to device PIN.

### Q43: What is the 15-second auto-lock feature?
**Answer:** If the user minimizes or backgrounds the app for more than 15 seconds, it automatically locks itself and requires biometric or PIN re-authentication upon resume.

### Q44: Does the app request dangerous Android permissions like READ_SMS or ACCESS_FINE_LOCATION?
**Answer:** No. The app intentionally requests zero dangerous permissions to uphold user trust and comply with privacy-first standards.

### Q45: How does the app read bank SMS without SMS permissions?
**Answer:** It uses Android's zero-permission `ACTION_SEND` intent: when a user receives a transaction alert, they share the text to Smart Expense Pro, which parses it on-device.

### Q46: What is GDPR data erasure compliance in the app?
**Answer:** A one-tap 'Erase All Data' feature in settings that permanently wipes all SQLite tables, preferences, and session tokens from the device.

### Q47: How is the in-app PIN protected against brute-force guessing?
**Answer:** After 5 failed PIN attempts, the app enforces an automated 30-second lockout timer that disables all input fields.

### Q48: Are transactions shared with third-party advertising trackers?
**Answer:** Zero transactions or user identities are transmitted to advertisers, analytics brokers, or credit scoring agencies.

### Q49: What encryption standard protects the database file on modern Android devices?
**Answer:** Android OS file-based encryption (FBE) with AES-256-XTS at the hardware level, tied to the user's device lock credential.

### Q50: Where are the user's biometric fingerprints stored?
**Answer:** In the device's hardware Secure Enclave / Trusted Execution Environment (TEE). The app never has direct access to raw fingerprint images; it receives only a cryptographic boolean confirmation from the OS.

### Q51: How does the CSV data export work?
**Answer:** It formats all user transactions into RFC-4180 compliant CSV text and shares it via Android's secure `FileProvider` to WhatsApp, Google Drive, or email.

### Q52: What columns are included in the exported CSV report?
**Answer:** Transaction ID, Date, Amount, Type (Income/Expense), Category, Payment Method, Description, and Creation Timestamp.

### Q53: What is the Savings Goals tracker?
**Answer:** A tool where users set target savings amounts (e.g., 'Emergency Fund ₹50,000') and track their progress as they allocate funds.

### Q54: What is the Debt & Loan Tracker?
**Answer:** A ledger module that tracks borrowed and lent money with debtor names, due dates, and settlement status.

### Q55: What is the Recurring Bills manager?
**Answer:** A tracking dialog for subscriptions and fixed recurring expenses (Netflix, Wi-Fi, Rent) with frequency intervals and due dates.

### Q56: What is the Calendar Expense view?
**Answer:** An interactive monthly calendar grid displaying daily spend badges, allowing users to inspect transactions for any specific calendar date.

### Q57: How does the app seed sample data for new accounts?
**Answer:** `ExpenseRepository.seedSampleDataIfEmpty()` inserts 7 realistic transaction rows (Salary, Lunch, Petrol, Shirt purchase, Electricity bill) so new users immediately see populated analytics.

### Q58: Can a user edit an existing transaction?
**Answer:** Yes. Tapping any transaction in the history feed opens the Add/Edit screen pre-populated with its details for editing or deletion.

### Q59: How are deleted transactions handled in SQLite?
**Answer:** They are permanently removed via `transactionDao.deleteById(id)`, freeing space and updating financial totals immediately.

### Q60: What build tool is used to compile the project?
**Answer:** Gradle with Kotlin DSL (`build.gradle.kts`), Gradle wrapper v8.6, and Android Gradle Plugin v8.3+.

### Q61: How is the app signed for production release?
**Answer:** Using a secure Java Keystore (`smart_expense_pro.jks`) configured with release signing configs in Gradle properties.

### Q62: What is the purpose of `.gitignore` in your project repository?
**Answer:** It prevents compiled binaries (`.apk`, `.aab`), build caches (`.gradle/`, `build/`), local machine SDK paths (`local.properties`), and IDE temp files from being committed to Git.

### Q63: Where is the source code publicly hosted?
**Answer:** On GitHub at `https://github.com/shahid8907503/smart-expense-pro` under the `main` branch.

### Q64: How is the project's documentation structured?
**Answer:** It includes a comprehensive README with feature highlights, architecture diagrams, testing guides, and a hosted landing page on GitHub Pages.

### Q65: How long does a clean build of the application take on your machine?
**Answer:** Approximately 30 to 50 seconds for debug builds, and ~4 minutes for full release bundles with R8 bytecode optimization.

## Level 2: Intermediate / Core Engineering Level (Q66–Q135)

### Q66: What is Room and why is it preferred over raw SQLiteOpenHelper?
**Answer:** Room is Google's object-mapping abstraction over SQLite that provides compile-time verification of SQL queries, eliminates boilerplate cursor mapping, and natively returns reactive asynchronous streams like Kotlin `Flow`.

### Q67: How do Room annotations work in your data models?
**Answer:** Entities are annotated with `@Entity(tableName = 'transactions')`, primary keys with `@PrimaryKey`, columns with `@ColumnInfo(name = '...')`, and queries with `@Dao`, `@Insert`, `@Update`, and `@Query`.

### Q68: What does Write-Ahead Logging (WAL) mode do in Room?
**Answer:** WAL allows concurrent readers to query the database simultaneously while a background thread is writing new records into a write-ahead log file, completely eliminating database locked exceptions (`SQLiteDatabaseLockedException`).

### Q69: Why do your DAO methods return `Flow<List<TransactionEntity>>`?
**Answer:** Room registers table invalidation observers on the underlying SQLite engine. Whenever an insert, update, or delete occurs, Room automatically re-executes the query and emits the fresh list to active Compose collectors without manual polling.

### Q70: What is the difference between `@Insert(onConflict = OnConflictStrategy.REPLACE)` and `ABORT`?
**Answer:** `REPLACE` automatically overwrites an existing row if a primary key conflict occurs, making offline sync operations idempotent. `ABORT` cancels the transaction and throws a SQLiteConstraintException.

### Q71: How are date ranges queried efficiently in SQLite?
**Answer:** `@Query('SELECT * FROM transactions WHERE userId = :uid AND transactionDate BETWEEN :start AND :end ORDER BY transactionDate DESC')`. With an index on `transactionDate`, lookup complexity is $O(\log N)$.

### Q72: How are monthly category sums calculated in the database layer?
**Answer:** `@Query('SELECT SUM(amount) FROM transactions WHERE userId = :uid AND category = :cat AND type = "expense"')`. Offloading calculation to SQLite's C-engine is 50x faster than filtering objects in Kotlin heap memory.

### Q73: What happens during a Room schema migration?
**Answer:** When adding columns or tables, Room executes a migration script (`Migration(1, 2)`) containing raw SQL `ALTER TABLE` statements. If a migration is missing and `fallbackToDestructiveMigration` is enabled, tables are dropped and recreated.

### Q74: How do you ensure Room operations never run on the Main Thread?
**Answer:** All DAO query methods are either declared as Kotlin `suspend` functions (which Room automatically dispatches to an internal background thread pool) or return `Flow<T>` which executes asynchronously.

### Q75: What is the purpose of in-memory Room database creation in your unit tests?
**Answer:** `Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()` creates a transient SQLite database entirely in RAM. It executes unit tests in milliseconds and discards data immediately upon test teardown.

### Q76: What is a Kotlin Coroutine and how does it differ from a Java Thread?
**Answer:** A Coroutine is a lightweight, cooperative multitasking user-space thread. While OS threads require ~1 MB of stack memory and heavy kernel context switching, thousands of coroutines can execute on a single thread pool with negligible overhead.

### Q77: What are the 3 primary Coroutine Dispatchers used in the app?
**Answer:** 1. `Dispatchers.Main`: UI rendering and user interactions.
2. `Dispatchers.IO`: Disk reads/writes (Room, SharedPreferences) and network requests.
3. `Dispatchers.Default`: CPU-intensive computations (Regex parsing, heavy mathematical aggregations).

### Q78: What is Structured Concurrency in Kotlin?
**Answer:** A paradigm ensuring that background coroutines are tied to defined lifecycles (`CoroutineScope`). If a parent job is cancelled or fails, all child coroutines launched within its scope are automatically cancelled, preventing orphaned background leaks.

### Q79: What is the difference between `launch` and `async`?
**Answer:** `launch` is a fire-and-forget builder returning a `Job` (used for writes where no return value is needed). `async` returns a `Deferred<T>` and is used when you need to `await()` a computed result from concurrent tasks.

### Q80: Why did you use `viewModelScope` and `rememberCoroutineScope()`?
**Answer:** `rememberCoroutineScope()` provides a scope tied directly to a composable's composition lifecycle. When the composable leaves the screen, active animations and transient tasks launched inside it are cancelled immediately.

### Q81: What is `withContext(Dispatchers.IO)` and when should it be used?
**Answer:** It switches the coroutine's execution context to the background I/O thread pool for a specific code block and returns the result back to the caller without blocking the original thread.

### Q82: What is the difference between `StateFlow` and `SharedFlow`?
**Answer:** `StateFlow` is a state-holder observable flow that always has an initial value and emits the latest state to new collectors (conflated). `SharedFlow` is a hot event bus that emits one-time transient events (like navigation commands or snackbars) to active collectors.

### Q83: How does `collectAsState()` bridge Coroutines and Compose?
**Answer:** It subscribes to a Kotlin `StateFlow` or `Flow` and converts emitted values into a Compose `State<T>`, automatically triggering UI recomposition whenever a new value is emitted.

### Q84: How do you handle exceptions in Coroutines?
**Answer:** Using `CoroutineExceptionHandler`, enclosing blocks in Kotlin's `runCatching {}`, or using supervisor jobs (`SupervisorJob()`) so that a failure in one child task does not cascade and cancel sibling coroutines.

### Q85: What happens if a coroutine calls a blocking I/O operation on `Dispatchers.Main`?
**Answer:** It freezes the UI thread, causing dropped frames (jank) and triggering Android's Application Not Responding (ANR) dialog after 5 seconds of unresponsiveness.

### Q86: What is State Hoisting in Jetpack Compose?
**Answer:** The architectural pattern of moving state up to the caller to make child composables stateless, reusable, and easy to test. The child receives state as parameters and notifies the parent of changes via lambda events.

### Q87: What is Recomposition in Jetpack Compose?
**Answer:** The process where Compose re-executes composable functions whose observed state has changed to emit updated UI trees, skipping composables whose inputs have not changed (smart recomposition).

### Q88: How do you prevent unnecessary recompositions in LazyColumn lists?
**Answer:** By supplying a unique, stable key to each item: `items(transactions, key = { it.id })`. This allows Compose to track items across insertions, deletions, and reorders without rebuilding every row.

### Q89: What is the purpose of `remember { mutableStateOf() }`?
**Answer:** `mutableStateOf` creates an observable state holder that Compose tracks for recomposition. `remember` preserves that state across recompositions so it isn't reset to its default value on every frame.

### Q90: What is the difference between `remember` and `rememberSaveable`?
**Answer:** `remember` survives recompositions during runtime. `rememberSaveable` survives both recompositions AND Android configuration changes (like screen rotation or process recreation) by saving state into an Android `Bundle`.

### Q91: What is `derivedStateOf` and when should it be used?
**Answer:** It creates a derived state from other state objects that only recalculates when the underlying inputs change, preventing recomposition on every intermediate tick (e.g., calculating if a scroll offset is greater than 100).

### Q92: How is the Unidirectional Data Flow (UDF) loop structured in your screens?
**Answer:** User Action (Tap/Speech) $	o$ UI Event Dispatched $	o$ Repository Executes Mutation on `Dispatchers.IO` $	o$ SQLite Updates $	o$ Room Emits New Flow $	o$ StateFlow Updates $	o$ Compose UI Recomposes.

### Q93: What are Semantic Tokens in your Material 3 theme?
**Answer:** Color tokens that convey functional meaning (`surface`, `onSurface`, `primary`, `error`) rather than hardcoded hex codes, allowing the UI to adapt dynamically between light and dark modes.

### Q94: How are Compose animations implemented for the balance counter?
**Answer:** Using `animateFloatAsState` with `tween(durationMillis = 950, easing = FastOutSlowInEasing)`, creating a smooth rolling transition whenever the ledger balance updates.

### Q95: How does `Scaffold` structure screen layouts in Compose?
**Answer:** It provides standardized Material layout slots for TopBar, BottomBar, FloatingActionButton, and SnackBarHost, automatically calculating window insets (`innerPadding`) to prevent system bar overlap.

### Q96: What is Android's `BiometricPrompt` and what authenticators does it support?
**Answer:** The official AndroidX biometric security API supporting `BIOMETRIC_STRONG` (hardware-backed fingerprint or 3D face scan) and `DEVICE_CREDENTIAL` (device screen lock PIN, pattern, or password).

### Q97: What is the Android Keystore system and why is it secure?
**Answer:** A dedicated cryptographic key storage container where private keys are generated inside the device's hardware Secure Enclave / TEE. Key material never enters the application's RAM, preventing extraction even on rooted devices.

### Q98: How does RFC-6238 TOTP calculate verification codes?
**Answer:** By hashing a shared base32 secret with the current Unix epoch time divided by a 30-second window using HMAC-SHA1: `HOTP(Secret, UnixTime / 30)`. The resulting hash is dynamically truncated to 6 digits.

### Q99: Why is your TOTP 2FA engine 100% offline?
**Answer:** Because TOTP is mathematically self-contained based on time-synchronization. As long as the phone's internal clock is accurate, the app generates and validates authentic codes without communicating with any external server.

### Q100: How does `FLAG_SECURE` protect against malicious background apps?
**Answer:** The Android WindowManager blocks the surface flinger from composing the window into screen recordings or virtual displays, preventing spyware from capturing financial inputs.

### Q101: How is user session data stored in `SharedPreferences` protected?
**Answer:** Keys and session tokens can be encrypted using AES-256-GCM via `EncryptedSharedPreferences`, ensuring raw XML files in `/data/data/` cannot be read via local file inspection.

### Q102: What is the difference between Symmetric and Asymmetric encryption in mobile apps?
**Answer:** Symmetric encryption (AES-256) uses a single shared key for both encryption and decryption (used for local database and disk data). Asymmetric encryption (RSA/ECC) uses a public/private key pair (used for digital signatures and TLS handshakes).

### Q103: How does your app prevent brute-force attacks on the lock screen?
**Answer:** A persistent failed-attempt counter in `AppLockScreen`. Exceeding 5 consecutive failures triggers an enforced coroutine timer that disables the keypad for 30 seconds.

### Q104: How do you handle Android's `FileProvider` securely for CSV exports?
**Answer:** `FileProvider` replaces dangerous `file://` URIs with content-scoped `content://` URIs, granting temporary, read-only permissions (`FLAG_GRANT_READ_URI_PERMISSION`) exclusively to the targeted sharing app.

### Q105: What is the Right to Erasure (GDPR) and how is it implemented?
**Answer:** `AuthRepository` and `ExpenseRepository` provide atomic purge functions that delete all SQLite tables, wipe cached preferences, clear keystore aliases, and restart the app into a factory-clean state.

### Q106: What is Android's `WorkManager` and why is it preferred over AlarmManager or JobScheduler?
**Answer:** `WorkManager` is the recommended solution for persistent, deferrable background work. It automatically chooses between `JobScheduler`, `GcmNetworkManager`, or `AlarmManager` based on OS API level and survives device reboots.

### Q107: What constraints are placed on `SyncWorker` in your app?
**Answer:** `Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()`. Sync execution is paused when the device is offline or in airplane mode and resumes automatically upon connectivity.

### Q108: How does WorkManager handle execution failures and retries?
**Answer:** When `doWork()` returns `Result.retry()`, WorkManager schedules a retry using an **Exponential Backoff criteria** (starting at 10 seconds, doubling up to a maximum limit), preventing server flooding.

### Q109: What is the difference between OneTimeWorkRequest and PeriodicWorkRequest?
**Answer:** `OneTimeWorkRequest` executes a non-repeating task once (used for immediate offline mutation sync). `PeriodicWorkRequest` runs recurring tasks at specified intervals with a minimum interval of 15 minutes.

### Q110: How does the app ingest bank SMS alerts without a background service?
**Answer:** By registering an `intent-filter` for `Intent.ACTION_SEND` in `AndroidManifest.xml`. When the user shares an SMS from their messaging app, Android launches `MainActivity` which routes the text into `BankSmsParser`.

### Q111: What regex patterns are used in `BankSmsParser` to detect UPI transactions?
**Answer:** Regex scanning for transaction triggers (`credited`, `debited`, `sent`, `paid`), monetary markers (`Rs`, `INR`, `₹`), and merchant descriptors (`to VPA`, `at`, `UPI Ref`).

### Q112: How does `BankSmsParser` prevent duplicate transaction entries from identical SMS shares?
**Answer:** It computes an SHA-256 hash or composite key of `amount + timestamp + merchant` and checks against existing records before persisting.

### Q113: How does Android's Doze Mode affect background execution?
**Answer:** When a device is unplugged and stationary with the screen off, Doze Mode suspends network access and background jobs. `WorkManager` respects Doze mode and executes jobs during scheduled OS maintenance windows.

### Q114: Why shouldn't you use Kotlin `Thread.sleep()` in background tasks?
**Answer:** `Thread.sleep()` blocks the underlying operating system thread, preventing it from executing other tasks. In Coroutines, `delay()` suspends the task without blocking the thread pool.

### Q115: How are background notification channels registered in Android 8.0+?
**Answer:** In `NotificationHelper`, `NotificationChannel` instances with defined importance levels (`IMPORTANCE_HIGH` for budget warnings) are registered with `NotificationManager` during application cold start.

### Q116: What is Retrofit and how does it interface with OkHttp?
**Answer:** Retrofit is a type-safe HTTP client that turns HTTP APIs into Kotlin interfaces using annotations (`@GET`, `@POST`). It delegates actual HTTP socket communication, connection pooling, and caching to OkHttp.

### Q117: What is Gson / Kotlinx Serialization and why is it required?
**Answer:** A JSON converter factory that serializes Kotlin data classes into JSON payload strings for HTTP requests and deserializes JSON responses into strongly-typed Kotlin DTOs.

### Q118: What is an OkHttp Interceptor and how is it used in `NetworkClient`?
**Answer:** `AuthInterceptor` intercepts outgoing HTTP requests to attach standard `Authorization: Bearer <token>` headers, while `HttpLoggingInterceptor` logs request and response bodies during debug sessions.

### Q119: Why did you lower the network timeout from 15 seconds to 2 seconds in `NetworkClient`?
**Answer:** To eliminate UI freezing on real Android devices. If a local development backend is unreachable, OkHttp fails within 2 seconds instead of hanging for 15-30 seconds, enabling instant local offline fallback.

### Q120: What is the difference between HTTP status code 401 and 403?
**Answer:** `401 Unauthorized` indicates missing or expired authentication credentials (token expired). `403 Forbidden` indicates the server understands the caller's identity but refuses authorization for that specific resource.

### Q121: How are network errors represented in Kotlin without crashing?
**Answer:** By encapsulating asynchronous calls in Kotlin's `Result<T>` monad. Functions return `Result.success(data)` or `Result.failure(exception)`, forcing callers to handle error paths explicitly.

### Q122: What is a DTO (Data Transfer Object) and why is it kept separate from a Database Entity?
**Answer:** A DTO represents the contract of remote API payloads. Keeping it decoupled from `TransactionEntity` ensures database schema changes do not break API integration and vice versa (**Separation of Concerns**).

### Q123: How does the app handle TLS/SSL certificate verification?
**Answer:** OkHttp uses Android's platform TrustManager to validate server X.509 certificates against trusted system Root Certificate Authorities (CAs) over TLS v1.2 and v1.3.

### Q124: What is Cleartext HTTP traffic and why is it disabled by default?
**Answer:** Unencrypted HTTP traffic (`http://`) transmits passwords and financial data in plaintext. Android blocks cleartext by default to prevent packet sniffing on public Wi-Fi networks.

### Q125: How does the live Gmail SMTP dispatcher open a connection to `smtp.gmail.com`?
**Answer:** It constructs a JavaMail `Session` with `mail.smtp.starttls.enable=true` and `mail.smtp.auth=true`, establishing an encrypted STARTTLS socket on port 587 authenticated by an application-specific password.

### Q126: What is the difference between a Unit Test and an Instrumented (UI) Test in Android?
**Answer:** Unit tests (`test/`) run on the local development machine's JVM in milliseconds without an Android device. Instrumented tests (`androidTest/`) run on a physical device or emulator to test hardware APIs and UI rendering.

### Q127: What unit tests are currently implemented in your project?
**Answer:** 19 automated unit tests across 4 suites: `BankSmsParserTest`, `FinancialMathJavaServiceTest`, `NlpTransactionParserTest`, and `TotpManagerTest`.

### Q128: What does `FinancialMathJavaServiceTest` verify?
**Answer:** It validates cashflow retention algorithms, compound savings projections, and debt interest calculations for mathematical accuracy.

### Q129: What does `NlpTransactionParserTest` verify?
**Answer:** It tests the natural language parser against conversational spending sentences in Hindi, English, and Hinglish, validating amount extraction and category classification.

### Q130: What does `TotpManagerTest` verify?
**Answer:** It validates that generated 6-digit TOTP codes match RFC-6238 reference test vectors across various time stamps.

### Q131: How does `kotlinx-coroutines-test` enable testing of asynchronous coroutines?
**Answer:** It provides `runTest` and `StandardTestDispatcher`, allowing tests to skip virtual time (`advanceUntilIdle()`) so that delays and timeouts execute instantaneously without sleeping threads.

### Q132: What is Mockito / MockK and how is it used in testing?
**Answer:** Mocking libraries that create simulated test doubles of interfaces (like `ApiService` or `Context`), allowing developers to test business logic in isolation without real network or hardware dependencies.

### Q133: What does the Gradle command `./gradlew testDebugUnitTest` do?
**Answer:** It compiles the debug build variant, executes all JUnit unit tests on the local JVM, and outputs an HTML test results report in `app/build/reports/tests/`.

### Q134: What is the Gradle Daemon and why did we use `--no-daemon` in automated scripts?
**Answer:** The Gradle Daemon is a persistent background JVM process that accelerates subsequent builds. `--no-daemon` is used in automated and CI environments to ensure builds start with a clean JVM and terminate completely upon completion.

### Q135: How is R8 different from legacy ProGuard?
**Answer:** ProGuard was a separate tool running after bytecode compilation. R8 is integrated directly into the Android Gradle compiler (D8), performing shrinking, desugaring, inlining, and obfuscation in a single pass 2x faster.

## Level 3: Advanced / Principal Architect Level (Q136–Q200)

### Q136: What is an Offline-First Distributed System in mobile computing?
**Answer:** A distributed systems pattern where the mobile node is treated as an autonomous, self-contained system of record rather than a dumb terminal. Local mutations are committed immediately to local ACID storage and synchronized asynchronously with central servers using conflict-resolution consensus.

### Q137: How does your sync engine solve the 'Two Generals Problem' in network synchronization?
**Answer:** By utilizing **Idempotency Keys and At-Least-Once Delivery**. Every mutation in `SyncQueueEntity` carries a client-generated UUID. If an acknowledgment is lost due to connection drops, the client retries the request; the server recognizes the duplicate UUID and acknowledges success without duplicating the record.

### Q138: What is Last-Write-Wins (LWW) conflict resolution and what are its trade-offs?
**Answer:** LWW resolves update conflicts by comparing UTC timestamps and accepting the record with the latest timestamp. Its advantage is mathematical simplicity and determinism without requiring user intervention. Its trade-off is susceptibility to clock skew if device system clocks drift.

### Q139: How do you prevent Clock Skew from corrupting timestamps across offline devices?
**Answer:** By computing a monotonic **NTP Server Delta Offset** during the first successful network handshake. The app calculates `currentRealTime = System.currentTimeMillis() + serverDeltaOffset`, mitigating errors caused by manually manipulated device clocks.

### Q140: What are Conflict-Free Replicated Data Types (CRDTs) and could they be used in this app?
**Answer:** CRDTs are mathematically sound data structures that converge automatically across distributed replicas without requiring centralized lock consensus. An observed-removed set (OR-Set) or state-based PN-Counter can resolve concurrent budget and expense additions across multi-device federations.

### Q141: What is the difference between Write-Ahead Logging (WAL) and Rollback Journaling in SQLite?
**Answer:** In rollback journaling, original database pages are copied to a journal file before changes are written directly to the main file, requiring exclusive database locks. In WAL, changes are appended to a separate log file (`-wal`), enabling lock-free concurrent reads while writes occur simultaneously.

### Q142: What is the checkpointing process in SQLite WAL mode?
**Answer:** The process of transferring accumulated transaction frames from the `-wal` log file back into the primary `.db` database file. Room triggers passive and truncated checkpoints automatically when WAL exceeds 1,000 pages or during database closure.

### Q143: How do you guarantee ACID Atomicity when writing both an expense and updating a sync queue?
**Answer:** By executing both operations inside Room's `@Transaction` scope: `db.withTransaction { transactionDao.insert(tx); syncQueueDao.enqueue(mutation) }`. If either statement throws, SQLite rolls back the journal, leaving the database state pristine.

### Q144: How do B-Tree indexes improve query performance in Room?
**Answer:** SQLite stores indexes in balanced search trees (B-Trees). A query filtering `WHERE transactionDate BETWEEN :start AND :end` executes in $O(\log N + K)$ index lookups (where $K$ is matched rows) instead of iterating through all $N$ database records in an $O(N)$ table scan.

### Q145: What is the database indexing trade-off?
**Answer:** Indexes significantly accelerate read queries but introduce write overhead (every insert/update must update both the primary table and the index B-Trees) and consume additional disk storage space.

### Q146: How do you design a database schema to prevent circular foreign key dependencies?
**Answer:** By maintaining strict hierarchical parent-child relationships with single-direction foreign keys (e.g., `BudgetEntity` $	o$ `UserEntity` with `onDelete = ForeignKey.CASCADE`), preventing recursive locking deadlocks.

### Q147: How would you implement End-to-End Encryption (E2EE) for cloud sync in this architecture?
**Answer:** By deriving a 256-bit AES-GCM encryption key on the client using PBKDF2 or Argon2id from the user's master passcode. The sync queue encrypts the `payloadJson` before transmitting it to the server. The server stores ciphertext and cannot inspect financial contents (**Zero-Knowledge Architecture**).

### Q148: How do you handle schema versioning across distributed clients with differing app versions?
**Answer:** By using backward-compatible Protobuf or JSON schema contracts with optional fields. The server rejects payloads from deprecated client versions with HTTP 426 (Upgrade Required) and provides migration adapters for older schemas.

### Q149: What is the Outbox Pattern in software architecture and how is it mirrored in this app?
**Answer:** `SyncQueueEntity` represents an implementation of the Transactional Outbox Pattern: database mutations and their outgoing sync messages are saved atomically within the same local database transaction, guaranteeing that a message is never sent without being locally persisted.

### Q150: How do you prevent starvation in background sync queues?
**Answer:** By implementing FIFO (First-In, First-Out) queue processing sorted strictly by `id ASC` or `timestamp ASC`, ensuring older transactions are committed to the backend before subsequent updates to the same entity are dispatched.

### Q151: What is the difference between Thread Concurrency and Thread Parallelism?
**Answer:** Concurrency is the composition of independently executing processes (managing multiple tasks at once via context switching). Parallelism is the simultaneous execution of multiple computational tasks on physically distinct CPU cores.

### Q152: How does Kotlin Coroutines achieve non-blocking suspension under the hood?
**Answer:** The Kotlin compiler transforms suspending functions into a **State Machine** using Continuation-Passing Style (CPS). A hidden parameter `Continuation<T>` tracks execution state, allowing the thread to be released to other tasks while the coroutine awaits I/O completion.

### Q153: What is Memory Churn and how does it degrade Compose rendering performance?
**Answer:** Memory churn occurs when large numbers of short-lived temporary objects are allocated in rapid succession (e.g., inside Compose recomposition loops). This triggers frequent Android Garbage Collection (GC) pauses, causing visible frame drops and stuttering.

### Q154: How did you eliminate memory churn in custom Canvas charts?
**Answer:** By hoisting paint objects, geometry bounds, and path builders outside the drawing loop, and computing coordinates via primitive floats rather than instantiating new `Point` or `Rect` wrapper objects on every frame.

### Q155: What are WeakReferences and SoftReferences in Java/Android memory management?
**Answer:** A `WeakReference` does not prevent its referent from being collected during the next GC cycle (used to prevent Activity memory leaks in listeners). A `SoftReference` is collected only when the JVM is running low on memory (used for sensitive image or data caches).

### Q156: How does R8 perform dead-code elimination and class inlining?
**Answer:** R8 builds a whole-program call graph starting from entry points (Activities, Services declared in AndroidManifest). Unreachable classes and unused methods are stripped, and single-caller small classes (such as lambda adapters) are inlined directly into call sites.

### Q157: What is Dalvik Executable (DEX) method limit and how does Multidex solve it?
**Answer:** A single DEX file can reference a maximum of 65,536 methods due to a 16-bit method index limit. Multidex splits compiled bytecode into secondary DEX files (`classes.dex`, `classes2.dex`), which the Android runtime loads at startup.

### Q158: How do you profile memory leaks in Android applications using LeakCanary?
**Answer:** LeakCanary registers a `FragmentLifecycleCallbacks` and watches destroyed Activity instances. If an Activity is not garbage collected within 5 seconds of `onDestroy()`, it triggers an automated heap dump (`.hprof`) and computes the shortest GC-root path holding the reference.

### Q159: What is the difference between Shallow Heap and Retained Heap in memory analysis?
**Answer:** Shallow heap is the memory consumed by the object itself (its reference fields and primitive values). Retained heap is the amount of memory that would be freed if that specific object were garbage collected, including all objects transitively referenced by it.

### Q160: How do you detect background thread deadlocks in Java/Kotlin?
**Answer:** Using JVM thread dumps (`jstack` or Android Studio CPU Profiler). Deadlocks appear as circular monitor lock dependencies where Thread A holds Lock 1 and waits for Lock 2, while Thread B holds Lock 2 and waits for Lock 1.

### Q161: What is the volatile keyword in Java/Kotlin and when is it necessary?
**Answer:** `@Volatile` guarantees that writes to a field are immediately made visible to all other CPU caches, preventing threads from reading stale cached values across multiple physical CPU cores.

### Q162: What is an AtomicInteger / AtomicBoolean and how does it achieve lock-free thread safety?
**Answer:** They use hardware-level **Compare-And-Swap (CAS)** machine instructions. If the value at a memory address equals the expected value, it updates it atomically without acquiring expensive operating system mutex locks.

### Q163: Why should you avoid using GlobalScope in production Kotlin applications?
**Answer:** `GlobalScope` launches top-level, unbounded coroutines that live for the lifetime of the entire application process. They ignore structured concurrency, cannot be cancelled as a unit, and easily lead to memory leaks when referencing destroyed UI contexts.

### Q164: How does `CoroutineScope(SupervisorJob() + Dispatchers.IO)` isolate failures?
**Answer:** A standard `Job` cancels all child coroutines if any single child fails with an unhandled exception. A `SupervisorJob` allows child failures to be handled independently without cascading cancellation to sibling tasks.

### Q165: What is the purpose of Android's `StrictMode` during development?
**Answer:** `StrictMode` detects unintended disk I/O, network calls, or cursor leaks running on the Main Thread during runtime and alerts developers via logcat or visual screen flashes.

### Q166: What is the difference between Class 1, Class 2, and Class 3 Biometric authenticators in Android?
**Answer:** Class 1 (Convenience) has a False Acceptance Rate (FAR) under 10% and cannot authenticate cryptographic keys. Class 2 (Weak) has spoof resistance under 20%. Class 3 (Strong) has FAR under 0.002% and spoof resistance above 70%, backed by hardware TEEs to authorize cryptographic operations.

### Q167: How does BiometricPrompt authenticate an encrypted cryptographic cipher?
**Answer:** The app creates a `Cipher` with a hardware-backed key, initializes it, wraps it in a `BiometricPrompt.CryptoObject(cipher)`, and passes it to `biometricPrompt.authenticate()`. The Keymaster hardware unlocks the key ONLY after biometric confirmation.

### Q168: What is AES-256-GCM and why is it preferred over AES-CBC?
**Answer:** Galois/Counter Mode (GCM) is an **Authenticated Encryption with Associated Data (AEAD)** cipher. Unlike CBC mode, GCM provides both confidentiality AND cryptographic integrity validation via an authentication tag, preventing bit-flipping and padding oracle attacks.

### Q169: What is Key Attestation in the Android Keystore?
**Answer:** A hardware security feature where the Keystore provides a certificate chain signed by a Google root key verifying that a cryptographic key pair was genuinely generated within a hardware-backed secure chip (TEE or StrongBox).

### Q170: How do you prevent reverse engineers from bypassing PIN checks via Frida or Xposed dynamic instrumentation?
**Answer:** By employing runtime integrity checks: detecting debugger attachments (`Debug.isDebuggerConnected()`), inspecting `/proc/self/maps` for Frida injection libraries, checking for root binaries (`/system/bin/su`), and verifying APK signing certificate hashes at runtime.

### Q171: What is SSL/TLS Certificate Pinning and how is it implemented in OkHttp?
**Answer:** Hardcoding the public key hashes (SHA-256 pin) of the backend server's SSL certificate in `CertificatePinner.Builder().add('api.domain.com', 'sha256/...')`. This prevents MITM attacks even if a user installs a malicious root certificate on their phone.

### Q172: How does PBKDF2 protect passwords against GPU rainbow table attacks?
**Answer:** Password-Based Key Derivation Function 2 applies a pseudorandom function (HMAC-SHA256) along with a cryptographic salt across tens of thousands of iterations (e.g., 100,000 rounds), making brute-force dictionary attacks computationally infeasible.

### Q173: What is the difference between a Bearer Token and an API Key?
**Answer:** An API key identifies the client application or project. A Bearer token (such as a JWT) represents an authenticated user's session and authorization claims, carrying cryptographic signatures verifying identity without querying the database on every request.

### Q174: What is a Timing Attack in software security and how do you prevent it?
**Answer:** An attack where an attacker measures the execution time of password/token comparisons to deduce characters. It is prevented by using **Constant-Time Comparison algorithms** (`MessageDigest.isEqual()`), which always take identical time regardless of where differences occur.

### Q175: How does Android's Application Sandbox enforce process isolation?
**Answer:** Android assigns a unique Linux User ID (UID) to every installed application. The Linux kernel enforces file system and process isolation so that App A cannot read App B's memory, files, or network sockets without explicit inter-process permissions.

### Q176: What is the difference between internal storage (`/data/data/`) and external storage (`/sdcard/`)?
**Answer:** Internal storage is private to the application UID and encrypted by default; files are deleted when the app is uninstalled. External storage is shared across apps with runtime permissions, making it insecure for financial ledgers.

### Q177: How do you prevent tapjacking attacks on Android security dialogs?
**Answer:** `android:filterTouchesWhenObscured='true'`. This tells the window manager to drop user touch events whenever another window or floating overlay (like a screen filter or malicious accessibility app) obscures the button.

### Q178: How are cryptographic salts generated securely in Java/Android?
**Answer:** Using `java.security.SecureRandom`, which seeds entropy from hardware sources (`/dev/urandom`), unlike `java.util.Random` which is deterministic and cryptographically predictable.

### Q179: What is the Principle of Least Privilege in mobile architecture?
**Answer:** An architectural standard where an app or module is granted only the absolute minimum permissions and access rights necessary to perform its intended function, reducing the attack surface in the event of an exploit.

### Q180: What is Content Security Policy (CSP) and how does it relate to mobile apps?
**Answer:** While primarily a web standard, mobile apps enforce CSP equivalents via Network Security Config XML, restricting domains, enforcing HTTPS, and preventing unencrypted cleartext socket connections.

### Q181: What is Deterministic Finite Automaton (DFA) regex tokenization in your NLP parser?
**Answer:** A lexical scanning algorithm that processes an input transcript character-by-character through state transitions, matching patterns (monetary amounts, currency identifiers) in guaranteed linear time $O(N)$ without recursive backtracking.

### Q182: What is the difference between Lemmatization and Stemming in Natural Language Processing?
**Answer:** Stemming cuts off word endings using heuristic rules (e.g., 'spending' $	o$ 'spend'). Lemmatization uses a vocabulary and morphological analysis to return the true dictionary base form (lemma) of a word.

### Q183: How does your NLP categorizer handle Out-Of-Vocabulary (OOV) merchant words?
**Answer:** By computing token similarity or fallback evaluation. If an unknown word appears alongside strong context markers ('bought', 'paid at', 'ordered from'), it extracts the token as a merchant name and prompts the user to assign a category.

### Q184: What is Word Error Rate (WER) in Speech Recognition systems?
**Answer:** A standard metric measuring speech-to-text accuracy: $\text{WER} = \frac{S + D + I}{N}$ where $S$ is substitutions, $D$ is deletions, $I$ is insertions, and $N$ is total words spoken.

### Q185: How could an on-device Small Language Model (SLM) like Gemini Nano be integrated in the future?
**Answer:** Via Android 14's AICore and MediaPipe LLM Inference API. The quantized 4-bit model runs directly on the device's Neural Processing Unit (NPU) with zero cloud connectivity, providing natural conversational finance reasoning.

### Q186: Why is on-device AI inference superior to cloud inference for personal finance apps?
**Answer:** 1. Guaranteed user privacy (zero financial data transmission).
2. Sub-50ms inference latency.
3. Zero per-user recurring cloud server and API token costs.
4. Full offline reliability without cellular dependence.

### Q187: What is an N-gram model in speech and text processing?
**Answer:** A contiguous sequence of $n$ items from a given sample of text. The NLP parser evaluates bigrams ('paid rupees', 'chai samosa') to disambiguate whether '40' represents an expense amount, a date, or a bus route number.

### Q188: How do you handle homophones in speech-to-text financial transcription?
**Answer:** Contextual disambiguation: recognizing that 'to' or 'two' followed by a currency marker or merchant represents an amount or preposition, normalizing numerical strings before routing into database entities.

### Q189: What is the memory footprint of on-device regex tokenization versus an embedding model?
**Answer:** A compiled regex pattern dictionary consumes **under 150 KB of RAM**, whereas a local embedding model (like MobileBERT) requires **40 to 120 MB of RAM** and significant CPU inference overhead.

### Q190: How is speech audio sampled and processed by Android's SpeechRecognizer?
**Answer:** Audio is sampled via the microphone at 16 kHz or 44.1 kHz, 16-bit mono PCM. Digital Signal Processing (DSP) filters background noise, and acoustic models compute phoneme probabilities before emitting text tokens.

### Q191: If this app grew to 1,000,000 active users tomorrow, what would break first on the backend?
**Answer:** The backend database connection pool and single-node SQLite/H2 database would immediately saturate under concurrent write locks. The server would exhaust memory threads and drop incoming HTTP sync requests.

### Q192: How would you scale the backend microservice to support 1,000,000 concurrent mobile users?
**Answer:** 1. Migrate database to **Amazon Aurora PostgreSQL** with read-replicas and HikariCP connection pooling.
2. Containerize Spring Boot with Docker and deploy across an **Auto-Scaling Kubernetes (EKS)** cluster behind an AWS ALB.
3. Deploy **Redis Cluster** for session tokens and distributed caching.
4. Use an asynchronous message broker (**Apache Kafka**) to buffer incoming sync queues and process ledger commits asynchronously.

### Q193: What is Database Sharding and how would you shard financial user data?
**Answer:** Horizontally partitioning database tables across multiple physical database servers based on a shard key. Financial data is perfectly sharded by `userId` (Hash-based sharding): all transactions for User A reside on Node 1, while User B resides on Node 2, eliminating cross-shard joins.

### Q194: What is the CAP Theorem and where does Smart Expense Pro fit within it?
**Answer:** The CAP theorem states that a distributed data store can guarantee at most two of: **Consistency (C)**, **Availability (A)**, and **Partition Tolerance (P)**. Smart Expense Pro is an **AP (Available + Partition Tolerant)** system: the local client is 100% available offline during network partitions, achieving **Eventual Consistency** once synchronization completes.

### Q195: What is Eventual Consistency and how does it apply to this app?
**Answer:** A consistency model in distributed computing where, given that no new updates are made, all replicas will eventually converge to the same value. The client database and cloud server reach identical state once all sync queue mutations are processed.

### Q196: How do you monitor distributed systems in production using OpenTelemetry and Prometheus?
**Answer:** By instrumenting Spring Boot microservices with OpenTelemetry to trace end-to-end request latency, while Prometheus scrapes JVM metrics (GC pauses, heap usage, HTTP error rates) and Grafana renders real-time operational dashboards.

### Q197: What is a Circuit Breaker pattern and how does it protect backends from cascading failures?
**Answer:** A stability pattern implemented via Resilience4j. If backend error rates exceed 50%, the circuit breaker 'trips open', instantly rejecting new requests with a fast fallback instead of waiting for timeouts and exhausting server thread pools.

### Q198: What is Rate Limiting and how would you prevent API abuse on your sync endpoints?
**Answer:** Implementing the **Token Bucket or Leaky Bucket algorithm** in an API Gateway (like Kong or AWS API Gateway). Clients are limited to e.g. 60 requests per minute per IP or JWT token; excess requests receive HTTP 429 (Too Many Requests).

### Q199: What was the single most difficult architectural decision you made in this project and why?
**Answer:** Deciding to build the app as **100% offline-first with an internal SQLite ledger** rather than relying on a cloud-hosted Firebase backend. While offline-first required engineering a custom sync queue, conflict resolution, WAL database concurrency, and local NLP tokenizers, it gave users **instant 10ms response times, zero cloud infrastructure bills, and complete financial privacy**.

### Q200: What would you build if you had another month of dedicated engineering time?
**Answer:** 1. **End-to-End Zero-Knowledge Encryption (E2EE)** for the cloud sync engine.
2. **On-device Receipt Scanning (OCR)** using Google ML Kit to parse physical paper restaurant bills with the phone camera.
3. **Bank Statement PDF Parser** allowing users to import historical bank statements directly into their private local database.

