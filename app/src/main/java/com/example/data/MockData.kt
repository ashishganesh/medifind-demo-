package com.example.data

import com.example.model.*

object MockData {

    val sampleMedicines = listOf(
        Medicine(
            id = "med_1",
            name = "Paracetamol 500mg",
            genericName = "Paracetamol / Acetaminophen",
            category = "Analgesics & Antipyretics",
            form = "Tablet",
            strength = "500 mg",
            description = "Common pain reliever and fever reducer used for headaches, muscle aches, and viral fevers.",
            dosageInfo = "1 tablet every 6 hours as required. Max 4g daily under medical supervision.",
            requiresPrescription = false,
            genericAlternatives = listOf("Dolo 650mg", "Calpol 500mg", "Crocin Advance")
        ),
        Medicine(
            id = "med_2",
            name = "Dolo 650mg",
            genericName = "Paracetamol",
            category = "Analgesics & Antipyretics",
            form = "Tablet",
            strength = "650 mg",
            description = "High-potency antipyretic formulation for high fever, dengue symptoms, and body pain relief.",
            dosageInfo = "Take as directed by practitioner after meals.",
            requiresPrescription = false,
            genericAlternatives = listOf("Paracetamol 500mg", "Pacimol 650")
        ),
        Medicine(
            id = "med_3",
            name = "Amoxicillin 500mg",
            genericName = "Amoxicillin Trihydrate",
            category = "Antibiotics (Penicillin)",
            form = "Capsule",
            strength = "500 mg",
            description = "Broad-spectrum antibiotic for bacterial chest infections, ear infections, and urinary tract infections.",
            dosageInfo = "1 capsule 3 times daily for 5-7 days. Complete full prescribed course.",
            requiresPrescription = true,
            genericAlternatives = listOf("Mox 500", "Novamox 500")
        ),
        Medicine(
            id = "med_4",
            name = "Metformin 500mg",
            genericName = "Metformin Hydrochloride",
            category = "Antidiabetic",
            form = "Tablet (SR)",
            strength = "500 mg",
            description = "First-line oral blood glucose regulation therapy for Type 2 Diabetes Mellitus.",
            dosageInfo = "Take with or immediately after evening meal.",
            requiresPrescription = true,
            genericAlternatives = listOf("Glycomet 500", "Glyciphage 500")
        ),
        Medicine(
            id = "med_5",
            name = "Pantoprazole 40mg",
            genericName = "Pantoprazole Sodium",
            category = "Gastrointestinal (PPI)",
            form = "Tablet",
            strength = "40 mg",
            description = "Proton pump inhibitor for acid reflux, GERD, and peptic ulcer relief.",
            dosageInfo = "Take 1 tablet in morning 30 mins before breakfast.",
            requiresPrescription = false,
            genericAlternatives = listOf("Pan 40", "Pantocid 40")
        ),
        Medicine(
            id = "med_6",
            name = "Cetirizine 10mg",
            genericName = "Cetirizine Hydrochloride",
            category = "Antihistamines",
            form = "Tablet",
            strength = "10 mg",
            description = "Non-drowsy allergy management for runny nose, allergic rhinitis, and skin hives.",
            dosageInfo = "1 tablet daily before bedtime.",
            requiresPrescription = false,
            genericAlternatives = listOf("Cetzine 10", "Alerid 10")
        ),
        Medicine(
            id = "med_7",
            name = "ORS Electrolyte Powder",
            genericName = "Oral Rehydration Salts (WHO Formula)",
            category = "Rehydration & Nutrition",
            form = "Sachet",
            strength = "21.8 g",
            description = "WHO recommended electrolyte fluid balance restorer for dehydration due to diarrhea or heat.",
            dosageInfo = "Dissolve entire sachet in 1 Litre boiled & cooled drinking water.",
            requiresPrescription = false,
            genericAlternatives = listOf("Electral Powder", "ORS Apple Tetrapack")
        ),
        Medicine(
            id = "med_8",
            name = "Azithromycin 500mg",
            genericName = "Azithromycin Dihydrate",
            category = "Antibiotics (Macrolide)",
            form = "Tablet",
            strength = "500 mg",
            description = "Short-course antibiotic for throat infections, typhoid, and pneumonia.",
            dosageInfo = "1 tablet daily for 3 to 5 days as prescribed.",
            requiresPrescription = true,
            genericAlternatives = listOf("Azee 500", "Azithral 500")
        ),
        Medicine(
            id = "med_9",
            name = "Insulin Glargine 100 IU/ml",
            genericName = "Insulin Glargine (Recombinant)",
            category = "Hormones & Endocrinology",
            form = "Injection Pen",
            strength = "3 ml",
            description = "Long-acting basal human insulin analog for diabetes management. Requires cold storage.",
            dosageInfo = "Subcutaneous injection once daily at a fixed time.",
            requiresPrescription = true,
            genericAlternatives = listOf("Lantus Solostar", "Basalog")
        ),
        Medicine(
            id = "med_10",
            name = "Anti-Rabies Vaccine",
            genericName = "Purified Rabies Vaccine (PVRV)",
            category = "Vaccines & Critical Care",
            form = "Injection",
            strength = "1 dose vial",
            description = "Post-exposure prophylaxis for animal bites and rabies virus prevention. Cold-chain essential.",
            dosageInfo = "Administered on Days 0, 3, 7, 14, 28 by healthcare provider.",
            requiresPrescription = true,
            genericAlternatives = listOf("Rabipur", "Abhayrab")
        ),
        Medicine(
            id = "med_11",
            name = "Ibuprofen 400mg",
            genericName = "Ibuprofen",
            category = "Pain Relief & Anti-inflammatory",
            form = "Tablet",
            strength = "400 mg",
            description = "Non-steroidal anti-inflammatory drug (NSAID) for joint pain, dental pain, and inflammation.",
            dosageInfo = "Take 1 tablet after meals twice daily.",
            requiresPrescription = false,
            genericAlternatives = listOf("Brufen 400", "Ibugesic 400")
        ),
        Medicine(
            id = "med_12",
            name = "Amlodipine 5mg",
            genericName = "Amlodipine Besylate",
            category = "Blood Pressure & Cardiac",
            form = "Tablet",
            strength = "5 mg",
            description = "Calcium channel blocker used to treat high blood pressure (hypertension) and angina.",
            dosageInfo = "Take 1 tablet daily in the morning.",
            requiresPrescription = true,
            genericAlternatives = listOf("Amlokind 5", "Amlopin 5")
        ),
        Medicine(
            id = "med_13",
            name = "Telmisartan 40mg",
            genericName = "Telmisartan",
            category = "Blood Pressure & Cardiac",
            form = "Tablet",
            strength = "40 mg",
            description = "Angiotensin II receptor blocker (ARB) for managing essential hypertension and cardiovascular protection.",
            dosageInfo = "1 tablet daily at bedtime.",
            requiresPrescription = true,
            genericAlternatives = listOf("Telma 40", "Tazloc 40")
        ),
        Medicine(
            id = "med_14",
            name = "Omeprazole 20mg",
            genericName = "Omeprazole",
            category = "Gastrointestinal",
            form = "Capsule",
            strength = "20 mg",
            description = "Proton pump inhibitor for treating heart-burn, gastritis, and stomach acid excess.",
            dosageInfo = "Take 1 capsule on an empty stomach 30 mins before morning breakfast.",
            requiresPrescription = false,
            genericAlternatives = listOf("Omez 20", "Omecip 20")
        ),
        Medicine(
            id = "med_15",
            name = "Vitamin C 500mg (Chewable)",
            genericName = "Ascorbic Acid",
            category = "Vitamins & Supplements",
            form = "Chewable Tablet",
            strength = "500 mg",
            description = "Essential antioxidant vitamin supplement for immune system strengthening and tissue repair.",
            dosageInfo = "Chew 1 tablet daily after food.",
            requiresPrescription = false,
            genericAlternatives = listOf("Limcee 500", "Celin 500")
        ),
        Medicine(
            id = "med_16",
            name = "Calcium + Vitamin D3",
            genericName = "Calcium Carbonate + Cholecalciferol",
            category = "Vitamins & Supplements",
            form = "Tablet",
            strength = "500mg / 250 IU",
            description = "Nutritional supplement for bone density, osteoporosis prevention, and calcium deficiency.",
            dosageInfo = "1 tablet daily after lunch.",
            requiresPrescription = false,
            genericAlternatives = listOf("Shelcal 500", "Cipcal 500")
        ),
        Medicine(
            id = "med_17",
            name = "Cough Syrup Dextromethorphan",
            genericName = "Dextromethorphan Hydrobromide + Chlorpheniramine",
            category = "Cold & Cough",
            form = "Syrup",
            strength = "100 ml",
            description = "Cough suppressant syrup for dry throat irritation, allergic cough, and broncho-spasms.",
            dosageInfo = "10 ml thrice daily after meals.",
            requiresPrescription = false,
            genericAlternatives = listOf("Benadryl DR", "Ascoril D Junior")
        ),
        Medicine(
            id = "med_18",
            name = "Levocetirizine 5mg",
            genericName = "Levocetirizine Dihydrochloride",
            category = "Allergy & Cold",
            form = "Tablet",
            strength = "5 mg",
            description = "Active non-sedating antihistamine for seasonal allergic rhinitis, watery eyes, and sneezing.",
            dosageInfo = "1 tablet once daily at night.",
            requiresPrescription = false,
            genericAlternatives = listOf("Levocet 5", "1-AL 5")
        ),
        Medicine(
            id = "med_19",
            name = "Povidone Iodine Ointment 5%",
            genericName = "Povidone Iodine",
            category = "Antiseptic & Wound Care",
            form = "Ointment",
            strength = "15 g Tube",
            description = "Topical microbicidal ointment for minor cuts, burns, skin abrasions, and wound disinfection.",
            dosageInfo = "Apply thin layer to affected clean skin 1-2 times daily.",
            requiresPrescription = false,
            genericAlternatives = listOf("Betadine 5%", "Cipladine")
        ),
        Medicine(
            id = "med_20",
            name = "Salbutamol Inhaler 100mcg",
            genericName = "Salbutamol Sulfate",
            category = "Respiratory Care",
            form = "Inhaler",
            strength = "200 doses",
            description = "Short-acting bronchodilator rescue inhaler for acute asthma attacks and wheezing.",
            dosageInfo = "1 to 2 puffs as needed for shortness of breath.",
            requiresPrescription = true,
            genericAlternatives = listOf("Asthalin Inhaler", "Ventolin Inhaler")
        ),
        Medicine(
            id = "med_21",
            name = "Ciprofloxacin 500mg",
            genericName = "Ciprofloxacin Hydrochloride",
            category = "Antibiotics",
            form = "Tablet",
            strength = "500 mg",
            description = "Fluoroquinolone antibiotic used for severe urinary, abdominal, and bone infections.",
            dosageInfo = "1 tablet twice daily for 5-7 days. Avoid taking with milk or calcium.",
            requiresPrescription = true,
            genericAlternatives = listOf("Ciplox 500", "Cifran 500")
        ),
        Medicine(
            id = "med_22",
            name = "Multivitamin + Zinc Capsules",
            genericName = "Multivitamins with Zinc & Minerals",
            category = "Vitamins & Supplements",
            form = "Capsule",
            strength = "1 Capsule",
            description = "Comprehensive daily nutritional support for stamina, recovery, and immunity booster.",
            dosageInfo = "1 capsule daily after breakfast.",
            requiresPrescription = false,
            genericAlternatives = listOf("Becosules Z", "Zincovit")
        )
    )

    val samplePharmacies = listOf(
        Pharmacy(
            id = "pharm_1",
            name = "Apollo Pharmacy Civil Lines",
            address = "Gate #1 Road, Civil Lines, Near DDUGU Campus, Gorakhpur",
            latitude = 26.7565,
            longitude = 83.3742,
            distanceKm = 0.1,
            phone = "+91 98390 12345",
            openStatus = "Open now",
            timing = "8:00 AM - 11:00 PM",
            isVerified = true,
            facilityType = "Retail Chain",
            rating = 4.8,
            lastUpdated = "10 mins ago",
            availableMedicinesCount = 124
        ),
        Pharmacy(
            id = "pharm_2",
            name = "Jan Aushadhi Kendra DDU Gate",
            address = "Pradhan Mantri Kendra, Opp DDUGU Main Gate, Gorakhpur",
            latitude = 26.7550,
            longitude = 83.3728,
            distanceKm = 0.2,
            phone = "+91 94150 99887",
            openStatus = "Open now",
            timing = "9:00 AM - 8:30 PM",
            isVerified = true,
            facilityType = "Jan Aushadhi Kendra",
            rating = 4.7,
            lastUpdated = "25 mins ago",
            availableMedicinesCount = 180
        ),
        Pharmacy(
            id = "pharm_3",
            name = "New Gorakhpur Medical Store",
            address = "University Road, Near Pant Park, Civil Lines, Gorakhpur",
            latitude = 26.7572,
            longitude = 83.3755,
            distanceKm = 0.3,
            phone = "+91 0551 2334455",
            openStatus = "Open now",
            timing = "8:30 AM - 10:00 PM",
            isVerified = false,
            facilityType = "Map Listed Pharmacy",
            rating = 4.2,
            lastUpdated = "Map Listed",
            availableMedicinesCount = 0
        ),
        Pharmacy(
            id = "pharm_4",
            name = "Sanjeevani Chemist & Druggist",
            address = "Golghar Market Road, Near City Mall, Gorakhpur",
            latitude = 26.7602,
            longitude = 83.3720,
            distanceKm = 0.5,
            phone = "+91 0551 4001122",
            openStatus = "24/7 Open",
            timing = "24 Hours Open",
            isVerified = true,
            facilityType = "Retail Pharmacy",
            rating = 4.6,
            lastUpdated = "15 mins ago",
            availableMedicinesCount = 145
        ),
        Pharmacy(
            id = "pharm_5",
            name = "Gupta Medical Hall",
            address = "Park Road, Near Jubilee Inter College, Gorakhpur",
            latitude = 26.7585,
            longitude = 83.3768,
            distanceKm = 0.4,
            phone = "+91 0551 6780000",
            openStatus = "Open now",
            timing = "9:00 AM - 9:30 PM",
            isVerified = false,
            facilityType = "Map Listed Pharmacy",
            rating = 4.1,
            lastUpdated = "Map Listed",
            availableMedicinesCount = 0
        ),
        Pharmacy(
            id = "pharm_6",
            name = "City Pharmacy & Healthcare",
            address = "Town Hall Road, Civil Lines, Gorakhpur",
            latitude = 26.7630,
            longitude = 83.3690,
            distanceKm = 0.8,
            phone = "+91 99188 77665",
            openStatus = "Closes at 10:30 PM",
            timing = "8:30 AM - 10:30 PM",
            isVerified = true,
            facilityType = "Retail Pharmacy",
            rating = 4.4,
            lastUpdated = "1 hour ago",
            availableMedicinesCount = 98
        ),
        Pharmacy(
            id = "pharm_7",
            name = "Standard Chemist & Medicos",
            address = "Buxipur Crossing, Near DDU Central Library Road, Gorakhpur",
            latitude = 26.7538,
            longitude = 83.3685,
            distanceKm = 0.6,
            phone = "+91 93352 11009",
            openStatus = "Open now",
            timing = "9:00 AM - 10:00 PM",
            isVerified = false,
            facilityType = "Map Listed Pharmacy",
            rating = 4.0,
            lastUpdated = "Map Listed",
            availableMedicinesCount = 0
        ),
        Pharmacy(
            id = "pharm_8",
            name = "BRD Medical College Pharmacy",
            address = "BRD Medical College Campus, Medical College Road, Gorakhpur",
            latitude = 26.7820,
            longitude = 83.3850,
            distanceKm = 2.9,
            phone = "+91 98380 55443",
            openStatus = "24/7 Open",
            timing = "24 Hours Open",
            isVerified = true,
            facilityType = "Govt Hospital Pharmacy",
            rating = 4.9,
            lastUpdated = "5 mins ago",
            availableMedicinesCount = 310
        )
    )

    // Initial Inventory state connecting pharmacies with stock
    val initialInventory = listOf(
        // Paracetamol 500mg
        InventoryRecord("inv_1", "pharm_1", "med_1", stockCount = 42, unitPriceRupees = 15.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "10 mins ago"),
        InventoryRecord("inv_2", "pharm_2", "med_1", stockCount = 120, unitPriceRupees = 6.5, status = AvailabilityStatus.AVAILABLE, lastUpdated = "25 mins ago"),
        InventoryRecord("inv_3", "pharm_3", "med_1", stockCount = 450, unitPriceRupees = 5.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "5 mins ago"),
        InventoryRecord("inv_4", "pharm_4", "med_1", stockCount = 8, unitPriceRupees = 18.0, status = AvailabilityStatus.LOW_STOCK, lastUpdated = "15 mins ago"),
        InventoryRecord("inv_5", "pharm_5", "med_1", stockCount = 200, unitPriceRupees = 16.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "2 mins ago"),
        InventoryRecord("inv_6", "pharm_6", "med_1", stockCount = 0, unitPriceRupees = 15.0, status = AvailabilityStatus.OUT_OF_STOCK, lastUpdated = "1 hour ago"),

        // Dolo 650mg
        InventoryRecord("inv_7", "pharm_1", "med_2", stockCount = 35, unitPriceRupees = 30.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "10 mins ago"),
        InventoryRecord("inv_8", "pharm_2", "med_2", stockCount = 85, unitPriceRupees = 14.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "20 mins ago"),
        InventoryRecord("inv_9", "pharm_4", "med_2", stockCount = 5, unitPriceRupees = 32.0, status = AvailabilityStatus.LOW_STOCK, lastUpdated = "12 mins ago"),

        // Amoxicillin 500mg
        InventoryRecord("inv_10", "pharm_1", "med_3", stockCount = 28, unitPriceRupees = 65.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "10 mins ago"),
        InventoryRecord("inv_11", "pharm_3", "med_3", stockCount = 180, unitPriceRupees = 35.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "5 mins ago"),
        InventoryRecord("inv_12", "pharm_6", "med_3", stockCount = 0, unitPriceRupees = 70.0, status = AvailabilityStatus.OUT_OF_STOCK, lastUpdated = "2 hours ago"),

        // Metformin 500mg
        InventoryRecord("inv_13", "pharm_1", "med_4", stockCount = 50, unitPriceRupees = 24.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "10 mins ago"),
        InventoryRecord("inv_14", "pharm_2", "med_4", stockCount = 210, unitPriceRupees = 11.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "30 mins ago"),

        // Pantoprazole 40mg
        InventoryRecord("inv_15", "pharm_1", "med_5", stockCount = 65, unitPriceRupees = 48.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "10 mins ago"),
        InventoryRecord("inv_16", "pharm_4", "med_5", stockCount = 12, unitPriceRupees = 52.0, status = AvailabilityStatus.LOW_STOCK, lastUpdated = "18 mins ago"),

        // ORS Electrolyte
        InventoryRecord("inv_17", "pharm_1", "med_7", stockCount = 8, unitPriceRupees = 22.0, status = AvailabilityStatus.LOW_STOCK, lastUpdated = "10 mins ago"),
        InventoryRecord("inv_18", "pharm_2", "med_7", stockCount = 150, unitPriceRupees = 12.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "15 mins ago"),
        InventoryRecord("inv_19", "pharm_3", "med_7", stockCount = 500, unitPriceRupees = 10.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "5 mins ago"),

        // Insulin Glargine
        InventoryRecord("inv_20", "pharm_1", "med_9", stockCount = 0, unitPriceRupees = 650.0, status = AvailabilityStatus.OUT_OF_STOCK, lastUpdated = "1 hour ago"),
        InventoryRecord("inv_21", "pharm_3", "med_9", stockCount = 24, unitPriceRupees = 480.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "5 mins ago"),
        InventoryRecord("inv_22", "pharm_5", "med_9", stockCount = 18, unitPriceRupees = 620.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "10 mins ago"),

        // Anti-Rabies Vaccine
        InventoryRecord("inv_23", "pharm_3", "med_10", stockCount = 45, unitPriceRupees = 0.0, status = AvailabilityStatus.AVAILABLE, lastUpdated = "5 mins ago"),
        InventoryRecord("inv_24", "pharm_1", "med_10", stockCount = 0, unitPriceRupees = 380.0, status = AvailabilityStatus.OUT_OF_STOCK, lastUpdated = "4 hours ago")
    )

    val popularSearchQueries = listOf(
        "Paracetamol 500mg",
        "Dolo 650mg",
        "Amoxicillin 500mg",
        "Insulin Glargine",
        "ORS Powder",
        "Anti-Rabies Vaccine",
        "Pantoprazole 40mg",
        "Azithromycin 500mg"
    )

    val sampleCategories = listOf(
        "Analgesics & Antipyretics",
        "Pain Relief & Anti-inflammatory",
        "Antibiotics",
        "Antidiabetic",
        "Blood Pressure & Cardiac",
        "Gastrointestinal",
        "Vitamins & Supplements",
        "Cold & Cough",
        "Allergy & Cold",
        "Antiseptic & Wound Care",
        "Respiratory Care",
        "Rehydration & Nutrition",
        "Vaccines & Critical Care"
    )

    val sampleAnalytics = AnalyticsData(
        totalPharmaciesTracked = 142,
        activePharmacies24h = 138,
        totalMedicinesCataloged = 1250,
        activeShortagesCount = 4,
        popularSearches = listOf(
            SearchFrequency("Paracetamol 500mg", 3420, 14.5),
            SearchFrequency("Dolo 650mg", 2890, 8.2),
            SearchFrequency("Amoxicillin 500mg", 1950, -3.1),
            SearchFrequency("Insulin Glargine", 1420, 22.4),
            SearchFrequency("ORS Electrolyte", 1100, 18.0)
        ),
        districtData = listOf(
            DistrictAvailability("Hazratganj & Central", 28, 92, 2),
            DistrictAvailability("Aliganj & Mahanagar", 34, 88, 3),
            DistrictAvailability("Gomti Nagar & Trans-Gomti", 42, 94, 1),
            DistrictAvailability("Chowk & Old City", 22, 78, 6),
            DistrictAvailability("Sarojini Nagar & South", 16, 72, 8)
        )
    )

    val sampleUsers = listOf(
        User("usr_1", "Aarav Sharma", "aarav@example.com", UserRole.PATIENT),
        User("usr_2", "Ramesh Sharma", "ramesh@sharmamedical.in", UserRole.PHARMACY, pharmacyId = "pharm_1"),
        User("usr_3", "Dr. Rajesh Verma", "admin@health.up.gov.in", UserRole.ADMIN)
    )

    val initialAvailabilityLogs = listOf(
        AvailabilityLog(
            id = "log_1",
            inventoryId = "inv_4",
            pharmacyId = "pharm_4",
            medicineId = "med_1",
            previousCount = 20,
            newCount = 8,
            previousStatus = AvailabilityStatus.AVAILABLE,
            newStatus = AvailabilityStatus.LOW_STOCK,
            updatedByUserId = "usr_2",
            timestamp = "15 mins ago"
        ),
        AvailabilityLog(
            id = "log_2",
            inventoryId = "inv_6",
            pharmacyId = "pharm_6",
            medicineId = "med_1",
            previousCount = 5,
            newCount = 0,
            previousStatus = AvailabilityStatus.LOW_STOCK,
            newStatus = AvailabilityStatus.OUT_OF_STOCK,
            updatedByUserId = "usr_2",
            timestamp = "1 hour ago"
        )
    )

    val sampleSearchHistory = listOf(
        SearchHistory("sh_1", "Paracetamol 500mg", "usr_1", "Hazratganj, Lucknow", "10 mins ago", 5),
        SearchHistory("sh_2", "Insulin Glargine", "usr_1", "Gomti Nagar, Lucknow", "1 hour ago", 2)
    )
}
