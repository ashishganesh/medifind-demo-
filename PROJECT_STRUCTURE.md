# MediFind — Project File Structure & Responsibility Map

## Overview
MediFind is an AI-ready medicine availability discovery platform for Smart India Hackathon (SIH). This document maps out the entire directory structure and explains the distinct role and responsibility of every source file in the project.

---

## Workspace Root Configuration
- **`build.gradle.kts`**: Root Gradle build configuration script specifying repositories and plugin management.
- **`settings.gradle.kts`**: Root Gradle settings specifying project name (`MediFind`) and included submodules (`:app`).
- **`gradle.properties`**: Environment options for JVM memory allocation, AndroidX enablement, and parallel execution.
- **`metadata.json`**: AI Studio platform metadata defining application identity, name, and major capabilities.
- **`firestore.rules`**: Production-ready Firestore Security Rules enforcing role-based access control (RBAC), role immutability, and pharmacy verification permissions.
- **`AUTHENTICATION.md`**: Complete Authentication and Role-Based Access Control (RBAC) architecture, admin provisioning, and pharmacy verification guide.
- **`FIREBASE_SETUP.md`**: Complete Firebase & Firestore architecture setup guide and collection schema reference.
- **`PROJECT_STRUCTURE.md`**: Complete file responsibility reference and architectural documentation.
- **`.env.example`**: Template for environment-level API keys and secrets.
- **`.gitignore`**: Version control ignore directives for build artifacts, local settings, and temporary files.

---

## App Module Configuration (`/app`)
- **`app/build.gradle.kts`**: Main module build script containing Android SDK targets (`compileSdk = 36`), Jetpack Compose options, dependencies, and build types.
- **`app/proguard-rules.pro`**: Shrinking and code obfuscation rules for production builds.
- **`app/src/main/AndroidManifest.xml`**: Android App Manifest declaring permissions (Internet, Network State, Location), launcher activity, and application theme.

---

## Core Application Source (`/app/src/main/java/com/example`)

### Entry Point
- **`MainActivity.kt`**: Android `ComponentActivity` entry point that configures edge-to-edge window display, attaches `MediFindViewModel`, and renders the primary `MediFindNavigationHost`.

---

### Data Layer (`/app/src/main/java/com/example/data`)
- **`MockData.kt`**: Single source of truth for prototype dataset containing realistic Indian essential medicines, verified local pharmacies, inventory records, sample users, availability logs, search history, and state health analytics data.

---

### Data Models (`/app/src/main/java/com/example/model`)
- **`User.kt`**: Data model for app users, profiles, role bindings (`PATIENT`, `PHARMACY`, `ADMIN`), pharmacy linkage ID, and verification flags (`isVerified`, `isActive`).
- **`UserRole.kt`**: Enum representing the three primary app stakeholders (`PATIENT`, `PHARMACY`, `ADMIN`).
- **`AvailabilityStatus.kt`**: Enum defining stock levels (`AVAILABLE` for >10, `LOW_STOCK` for 1–10, `OUT_OF_STOCK` for <=0).
- **`Medicine.kt`**: Data model representing medicine details (ID, name, brand, strength, category, prescription requirement).
- **`Pharmacy.kt`**: Data model representing pharmacy details (ID, name, address, location coordinates, contact, distance, verification status, owner ID).
- **`InventoryRecord.kt`**: Data model linking a specific medicine to a pharmacy, tracking stock count, unit price, and last updated timestamp.
- **`AvailabilityLog.kt`**: Audit log model recording stock changes, previous/new quantities, timestamp, and user ID.
- **`SearchHistory.kt`**: Citizen query log tracking search strings, location context, timestamp, and result counts.
- **`AnalyticsData.kt`**: Data models for district demand analytics, shortage alerts, and health metrics.

---

### Services Layer (`/app/src/main/java/com/example/service`)
- **`FirebaseConfig.kt`**: Runtime detector checking Firebase configuration and managing seamless fallback to Demo Mode.
- **`AuthService.kt`**: Centralized authentication service managing login, patient registration, pharmacy registration with pending verification status, password reset, session logout, and Demo Mode switching.
- **`MedicineService.kt`**: Data access service for medicine queries, category filtering, and catalog management.
- **`PharmacyService.kt`**: Data access service for pharmacy lookups, store profile editing, location filtering, and verification toggling.
- **`InventoryService.kt`**: Data access service for inventory querying, stock updating with non-negative constraints (`stock >= 0`), status calculations, and audit log generation.
- **`AvailabilityService.kt`**: Core search matching service connecting searched medicines to nearby stocked pharmacies.
- **`UserService.kt`**: User session helper service.

---

### ViewModel & State Management (`/app/src/main/java/com/example/viewmodel`)
- **`MediFindViewModel.kt`**: Central state engine delegating to the Services layer, managing auth state, route permissions, and exposing `StateFlow` for UI screens.

---

### UI Layer (`/app/src/main/java/com/example/ui`)

#### Navigation (`ui/navigation`)
- **`MediFindNavigation.kt`**: Central Compose navigation controller hosting auth routes, patient routes, pharmacy routes, admin routes, route security guards, bottom navigation bar, and top app bar integration.

#### Authentication Pages (`ui/auth`)
- **`LoginScreen.kt`**: Sign in page supporting email/password authentication, password visibility toggle, forgot password link, registration link, and SIH Demo Mode persona switcher.
- **`RegisterScreen.kt`**: Public registration page with Patient and Pharmacy tabs (Admin role strictly excluded), store information fields for pharmacies, and pending verification notices.
- **`ForgotPasswordScreen.kt`**: Password reset request page with email input and verification confirmation banner.
- **`UnauthorizedScreen.kt`**: Access restricted error page for unauthorized role route attempts.

#### Reusable UI Components (`ui/components`)
- **`AppHeader.kt`**: Standard top app bar displaying page title, back navigation, location context, current role badge, and logout action.
- **`SihDemoBanner.kt`**: Demo control banner enabling reviewers to switch between Patient, Pharmacy, and Admin modes with explicit Demo Mode indicator.
- **`MedicineCard.kt`**: Reusable list card displaying medicine brand, category, dosage, and stock badge.
- **`PharmacyCard.kt`**: Reusable card for pharmacy listings with distance, stock count, verification tick, and directions action.
- **`SearchBar.kt`**: Styled search input box with clear button and filter action trigger.
- **`StatusBadge.kt`**: Color-coded availability tag (`In Stock`, `Low Stock`, `Out of Stock`).
- **`StatCard.kt`**: Dashboard metric card displaying key counts and statistics.
- **`EmptyState.kt`**: Centered placeholder for empty query results or empty lists.
- **`NotificationAlert.kt`**: Temporary banner/toast alert overlay for user actions.
- **`UpdateStockDialog.kt`**: Interactive modal dialog for pharmacy staff to adjust stock quantity and price.

#### Patient/User Pages (`ui/user`)
- **`UserHomeScreen.kt`**: Patient portal landing view with hero search bar, quick health categories, search guide, and popular items.
- **`SearchMedicineScreen.kt`**: Search and filter screen supporting location selection, distance radius, category, and availability filters.
- **`SearchResultsScreen.kt`**: Medicine search results screen with dual toggle: List View and Radar/Map simulation.
- **`MedicineDetailsScreen.kt`**: Detailed view for a specific medicine showing total nearby availability and stocked pharmacy list.
- **`PharmacyListScreen.kt`**: Directory view of nearby registered pharmacies and dispensaries.
- **`PharmacyDetailsScreen.kt`**: Detailed pharmacy profile showing operational hours, contact details, and live in-stock medicine inventory.

#### Pharmacy Portal Pages (`ui/pharmacy`)
- **`PharmacyDashboardScreen.kt`**: Pharmacy manager dashboard with verification alert banners, stock summary widgets, low-stock warnings, and inventory management triggers.
- **`PharmacyInventoryScreen.kt`**: Real-time inventory table allowing stock searches, filtering by status, and editing quantities.
- **`PharmacyProfileScreen.kt`**: Pharmacy profile details page displaying store details, editable store information, and verification status.

#### State Admin Portal Pages (`ui/admin`)
- **`AdminDashboardScreen.kt`**: State health department command center dashboard with state-wide availability statistics and alerts.
- **`AdminPharmaciesScreen.kt`**: State pharmacy registry oversight table for managing store verifications.
- **`AdminInventoryScreen.kt`**: Statewide medicine stock monitoring screen highlighting critical shortages.
- **`AdminAnalyticsScreen.kt`**: High-level public health analytics showing high-demand medicine trends and district stock distribution.

---

## Unit & Integration Testing (`/app/src/test/java/com/example`)
- **`MediFindUnitTest.kt`**: Local JVM unit test validating basic execution environment.
- **`MediFindAppRobolectricTest.kt`**: Robolectric JVM test verifying Android context and resource string resolution.
- **`MediFindComponentScreenshotTest.kt`**: Roborazzi visual regression test rendering and capturing Compose component screenshots.
