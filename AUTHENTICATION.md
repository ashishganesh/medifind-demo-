# MediFind — Authentication & Role-Based Access Control (RBAC) Architecture

## Overview
MediFind implements secure authentication and role-based authorization for three distinct stakeholders:
1. **`PATIENT`**: Citizens searching for medicine availability and nearby verified pharmacies.
2. **`PHARMACY`**: Licensed pharmacy owners and staff managing inventory, stock counts, and store profiles.
3. **`ADMIN`**: State Health Department administrators overseeing pharmacy verifications, inventory shortage alerts, medicine master catalog, and public health analytics.

---

## 1. Authentication Architecture

MediFind utilizes **Firebase Authentication** (Email & Password) for credential verification, backed by Firestore user documents (`users/{uid}`) storing user identity, email, assigned role, pharmacy ownership binding, and verification state.

### Centralized Auth State Engine
- **`AuthService.kt`**: Handles login, registration, password resets, session persistence, and logout.
- **`MediFindViewModel.kt`**: Exposes centralized auth reactive flows (`currentUser`, `isAuthenticated`, `currentRole`, `authError`) to Jetpack Compose UI screens.

---

## 2. Public Registration & Role Matrix

| User Role | Public Registration Allowed? | Default Status On Registration | Role Capabilities |
| :--- | :--- | :--- | :--- |
| **`PATIENT`** | ✅ Yes | `isVerified: true`, `isActive: true` | Search medicines, view nearby stocked pharmacies, view stock levels. |
| **`PHARMACY`** | ✅ Yes | `isVerified: false` (Pending Approval) | Manage store profile, view/update own inventory, log stock changes. |
| **`ADMIN`** | ❌ **STRICTLY NO** | Provisioned via Admin process | Full oversight, approve/verify pharmacies, manage master catalog, view state analytics. |

> ⚠️ **CRITICAL SECURITY RULE**: The `ADMIN` role is strictly excluded from public client registration screens and API endpoints. Attempting to pass `role = "ADMIN"` from client-side requests is explicitly blocked by Firestore Security Rules.

---

## 3. Admin Provisioning Mechanism

Admin accounts cannot be created via public registration. In production environments, Admin accounts are provisioned via:
1. **Firebase Admin SDK Script / CLI**: Setting custom claims or creating a user document directly in the Firestore database console.
2. **Pre-seeded Goverment Identity**: Pre-verified administrator accounts bound to official state health email domains (e.g. `admin@health.up.gov.in`).

---

## 4. Pharmacy Onboarding & Verification Workflow

```
[ Pharmacy Public Registration ]
              │
              ▼
[ Account Created: isVerified = false, status = "Pending Verification" ]
              │
              ▼
[ Pharmacy Dashboard Displays Warning Banner: "Pending Verification" ]
              │
              ▼
[ State Health Admin Reviews License & Registration in Admin Portal ]
              │
              ▼
[ Admin Clicks "Verify Pharmacy" ]
              │
              ▼
[ Account Updated: isVerified = true, status = "Verified & Public" ]
```

- Unverified pharmacies can log in and manage their inventory internally, but are flagged in the administrative portal and excluded from verified citizen search listings until approved.

---

## 5. Route Protection Engine

Route authorization is guarded in Jetpack Compose navigation (`MediFindNavigation.kt`):
- **Unauthenticated users**: Redirected to `/login`.
- **`PATIENT`**: Access to `/pharmacy/*` or `/admin/*` redirects to `/unauthorized`.
- **`PHARMACY`**: Access to `/admin/*` redirects to `/unauthorized`.
- **`ADMIN`**: Full oversight access across all routes.

---

## 6. Firestore Security Model (`firestore.rules`)

1. **Role Immutability**: Users are strictly forbidden from modifying their own `role` field (`request.resource.data.role == resource.data.role`).
2. **Ownership Binding**: Pharmacy users can only update inventory documents where `request.resource.data.pharmacyId == getUserData().pharmacyId`.
3. **Self-Verification Prevention**: Pharmacy owners cannot toggle their store's `isVerified` flag from `false` to `true`.
4. **Master Catalog Protection**: Only `ADMIN` users can create, update, or delete entries in `/medicines`.

---

## 7. Dual Operational Modes (Firebase vs Demo Mode)

- **Live Firebase Mode**: Active when Firebase credentials and initialization succeed.
- **SIH Demo Mode**: Active as a seamless fallback when Firebase is disconnected. Provides explicit persona switchers ("Continue as Patient", "Continue as Pharmacy", "Continue as Admin") clearly labeled as `"SIH Demo Mode — Sample Account Switcher"` to facilitate evaluator testing without compromising production security logic.
