# MediFind — Firebase & Firestore Setup Guide

## Overview
MediFind supports dual operational modes:
1. **Firebase / Firestore Live Mode**: Real-time cloud sync across citizens, pharmacies, and state administrators.
2. **Demo Mode (Fallback)**: Seamless client-side evaluation using verified local sample data when Firebase credentials are unavailable or disconnected.

---

## Firestore Database Schema

### Collections & Document Fields

#### 1. `users`
- **`id`** (`string`): Firebase Auth UID.
- **`name`** (`string`): Full name of citizen, pharmacy staff, or admin.
- **`email`** (`string`): Registered email address.
- **`role`** (`string`): User role (`PATIENT` | `PHARMACY` | `ADMIN`).
- **`pharmacyId`** (`string?`): Linked pharmacy ID (required if role is `PHARMACY`).
- **`createdAt`** (`number`): Creation epoch timestamp.

#### 2. `pharmacies`
- **`id`** (`string`): Unique pharmacy identifier.
- **`name`** (`string`): Store / Hospital pharmacy name.
- **`address`** (`string`): Physical street address & district.
- **`distanceKm`** (`number`): Proximity in kilometers.
- **`phone`** (`string`): Direct contact number.
- **`openStatus`** (`string`): Operational status string (e.g. `24/7 Open`, `Open now`).
- **`timing`** (`string`): Operating hours.
- **`isVerified`** (`boolean`): State health department verification flag.
- **`facilityType`** (`string`): Store classification (`Retail Pharmacy`, `Jan Aushadhi Kendra`, `Hospital Pharmacy`).
- **`rating`** (`number`): Rating out of 5.0.

#### 3. `medicines`
- **`id`** (`string`): Unique medicine identifier.
- **`name`** (`string`): Commercial brand name (e.g. `Dolo 650mg`).
- **`genericName`** (`string`): Active pharmaceutical ingredient.
- **`category`** (`string`): Therapeutic category.
- **`form`** (`string`): Dosage form (`Tablet`, `Capsule`, `Injection`, `Sachet`).
- **`strength`** (`string`): Active dosage strength (e.g. `500 mg`).
- **`description`** (`string`): Medical indication summary.
- **`requiresPrescription`** (`boolean`): Rx mandatory flag.

#### 4. `inventory`
- **`id`** (`string`): Unique inventory item identifier.
- **`pharmacyId`** (`string`): Reference to `pharmacies.id`.
- **`medicineId`** (`string`): Reference to `medicines.id`.
- **`stockCount`** (`number`): Units currently available (Must be `>= 0`).
- **`unitPriceRupees`** (`number`): Retail price per unit in INR.
- **`status`** (`string`): Dynamic status (`AVAILABLE` for >10, `LOW_STOCK` for 1–10, `OUT_OF_STOCK` for <=0).
- **`lastUpdated`** (`string`): Human-readable or ISO timestamp.

#### 5. `availabilityLogs`
- **`id`** (`string`): Audit log identifier.
- **`inventoryId`** (`string`): Reference to `inventory.id`.
- **`pharmacyId`** (`string`): Reference to `pharmacies.id`.
- **`medicineId`** (`string`): Reference to `medicines.id`.
- **`previousCount`** (`number`): Stock level prior to change.
- **`newCount`** (`number`): Stock level after change.
- **`previousStatus`** (`string`): Previous status.
- **`newStatus`** (`string`): New status.
- **`updatedByUserId`** (`string`): User ID performing the update.
- **`timestamp`** (`string`): Timestamp of modification.

#### 6. `searchHistory`
- **`id`** (`string`): Search log identifier.
- **`query`** (`string`): Search query text.
- **`userId`** (`string`): Citizen ID.
- **`location`** (`string`): Location context at query time.
- **`timestamp`** (`string`): Epoch/string timestamp.
- **`resultsCount`** (`number`): Number of matching inventory items returned.

---

## Security Rules deployment
Apply the security rules defined in `/firestore.rules` directly in the Firebase Console under **Firestore Database > Rules**.

---

## Demo Mode Operation
When `FIREBASE_PROJECT_ID` or credentials are missing from environment configuration, `FirebaseConfig` automatically falls back to **Demo Mode**. All operations utilize `MockData.kt` through the service abstractions without throwing exceptions or showing blank screens.
