package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.model.UserRole
import com.example.ui.admin.AdminAnalyticsScreen
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.admin.AdminInventoryScreen
import com.example.ui.admin.AdminPharmaciesScreen
import com.example.ui.admin.AdminAlternativesScreen
import com.example.ui.auth.*
import com.example.ui.components.AppHeader
import com.example.ui.components.NotificationAlert
import com.example.ui.components.SihDemoBanner
import com.example.ui.components.UpdateStockDialog
import com.example.ui.pharmacy.PharmacyDashboardScreen
import com.example.ui.pharmacy.PharmacyInventoryScreen
import com.example.ui.pharmacy.PharmacyProfileScreen
import com.example.ui.user.*
import com.example.viewmodel.MediFindViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    // Auth routes
    object Login : Screen("login", "Sign In", Icons.Default.Login)
    object Register : Screen("register", "Register", Icons.Default.HowToReg)
    object ForgotPassword : Screen("forgot_password", "Reset Password", Icons.Default.LockReset)
    object Unauthorized : Screen("unauthorized", "Access Denied", Icons.Default.GppBad)

    // Patient routes
    object UserHome : Screen("user_home", "Home", Icons.Default.Home)
    object UserSearch : Screen("user_search", "Search", Icons.Default.Search)
    object UserResults : Screen("user_results", "Results", Icons.Default.List)
    object UserPharmacies : Screen("user_pharmacies", "Pharmacies", Icons.Default.Storefront)

    // Pharmacy routes
    object PharmacyDashboard : Screen("pharmacy_dashboard", "Dashboard", Icons.Default.Dashboard)
    object PharmacyInventory : Screen("pharmacy_inventory", "Inventory", Icons.Default.Inventory2)
    object PharmacyPredictions : Screen("pharmacy_predictions", "Stock Predictions", Icons.Default.AutoGraph)
    object PharmacyProfile : Screen("pharmacy_profile", "Profile", Icons.Default.Person)

    // Admin routes
    object AdminDashboard : Screen("admin_dashboard", "Overview", Icons.Default.Analytics)
    object AdminPharmacies : Screen("admin_pharmacies", "Registry", Icons.Default.Verified)
    object AdminInventory : Screen("admin_inventory", "Shortages", Icons.Default.Warning)
    object AdminAlternatives : Screen("admin_alternatives", "Alternatives", Icons.Default.MedicalServices)
    object AdminAnalytics : Screen("admin_analytics", "Reports", Icons.Default.BarChart)
}

@Composable
fun MediFindApp(
    viewModel: MediFindViewModel,
    navController: NavHostController = rememberNavController()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    val currentRole = currentUser?.role ?: UserRole.PATIENT

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val maxDistanceKm by viewModel.maxDistanceKm.collectAsStateWithLifecycle()
    val availabilityFilter by viewModel.availabilityFilter.collectAsStateWithLifecycle()
    val inventoryList by viewModel.inventoryList.collectAsStateWithLifecycle()
    val pharmacies by viewModel.pharmacies.collectAsStateWithLifecycle()
    val medicines by viewModel.medicines.collectAsStateWithLifecycle()
    val availabilityLogs by viewModel.availabilityLogs.collectAsStateWithLifecycle()
    val stockPredictions by viewModel.stockPredictions.collectAsStateWithLifecycle()
    val verifiedAlternatives by viewModel.verifiedAlternatives.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val editingRecord by viewModel.editingRecord.collectAsStateWithLifecycle()

    val currentUserLocation by viewModel.currentUserLocation.collectAsStateWithLifecycle()
    val selectedSortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    var showLocationDialog by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.UserHome.route

    val userPharmacy = pharmacies.find { it.id == currentUser?.pharmacyId } ?: pharmacies.first()

    val isAuthScreen = currentRoute == Screen.Login.route ||
            currentRoute == Screen.Register.route ||
            currentRoute == Screen.ForgotPassword.route ||
            currentRoute == Screen.Unauthorized.route

    // Role-based Route Security Guard
    LaunchedEffect(currentRole, isAuthenticated, currentRoute) {
        if (!isAuthenticated && !isAuthScreen) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        } else if (isAuthenticated) {
            when (currentRole) {
                UserRole.PATIENT -> {
                    if (currentRoute.startsWith("pharmacy_") || currentRoute.startsWith("admin_")) {
                        navController.navigate(Screen.Unauthorized.route)
                    }
                }
                UserRole.PHARMACY -> {
                    if (currentRoute.startsWith("admin_")) {
                        navController.navigate(Screen.Unauthorized.route)
                    }
                }
                UserRole.ADMIN -> {
                    // Admin has full oversight access
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                SihDemoBanner(
                    currentRole = currentRole,
                    onRoleSelected = { viewModel.switchDemoRole(it) },
                    onResetDemoData = { viewModel.resetDemoData() }
                )
                if (!isAuthScreen) {
                    AppHeader(
                        title = "MediFind Platform",
                        currentRole = currentRole,
                        userLocation = userLocation,
                        onBackClick = if (currentRoute != Screen.UserHome.route && currentRoute != Screen.PharmacyDashboard.route && currentRoute != Screen.AdminDashboard.route) {
                            { navController.popBackStack() }
                        } else null,
                        onLogoutClick = {
                            viewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            if (!isAuthScreen) {
                NavigationBar {
                    val items = when (currentRole) {
                        UserRole.PATIENT -> listOf(Screen.UserHome, Screen.UserSearch, Screen.UserPharmacies)
                        UserRole.PHARMACY -> listOf(Screen.PharmacyDashboard, Screen.PharmacyInventory, Screen.PharmacyPredictions, Screen.PharmacyProfile)
                        UserRole.ADMIN -> listOf(Screen.AdminDashboard, Screen.AdminPharmacies, Screen.AdminInventory, Screen.AdminAlternatives, Screen.AdminAnalytics)
                    }

                    items.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.UserHome.route
            ) {
                // Auth Routes
                composable(Screen.Login.route) {
                    LoginScreen(
                        onLoginSubmit = { email, pass ->
                            if (viewModel.login(email, pass)) {
                                val target = when (viewModel.currentUser.value?.role) {
                                    UserRole.PHARMACY -> Screen.PharmacyDashboard.route
                                    UserRole.ADMIN -> Screen.AdminDashboard.route
                                    else -> Screen.UserHome.route
                                }
                                navController.navigate(target) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) },
                        onRegisterClick = { navController.navigate(Screen.Register.route) },
                        onDemoRoleClick = { role ->
                            viewModel.switchDemoRole(role)
                            val target = when (role) {
                                UserRole.PHARMACY -> Screen.PharmacyDashboard.route
                                UserRole.ADMIN -> Screen.AdminDashboard.route
                                else -> Screen.UserHome.route
                            }
                            navController.navigate(target) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        errorMessage = authError
                    )
                }

                composable(Screen.Register.route) {
                    RegisterScreen(
                        onRegisterPatientSubmit = { name, email, pass, confirm ->
                            if (viewModel.registerPatient(name, email, pass, confirm)) {
                                navController.navigate(Screen.UserHome.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        onRegisterPharmacySubmit = { ownerName, email, pass, confirm, pharmName, addr, phone, facility ->
                            if (viewModel.registerPharmacy(ownerName, email, pass, confirm, pharmName, addr, phone, facility)) {
                                navController.navigate(Screen.PharmacyDashboard.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        onLoginClick = { navController.navigate(Screen.Login.route) },
                        errorMessage = authError
                    )
                }

                composable(Screen.ForgotPassword.route) {
                    ForgotPasswordScreen(
                        onResetPasswordSubmit = { email -> viewModel.resetPassword(email) },
                        onBackToLoginClick = { navController.navigate(Screen.Login.route) }
                    )
                }

                composable(Screen.Unauthorized.route) {
                    UnauthorizedScreen(
                        onReturnClick = {
                            val target = when (currentRole) {
                                UserRole.PHARMACY -> Screen.PharmacyDashboard.route
                                UserRole.ADMIN -> Screen.AdminDashboard.route
                                else -> Screen.UserHome.route
                            }
                            navController.navigate(target) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                // Patient Routes
                composable(Screen.UserHome.route) {
                    UserHomeScreen(
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        userLocation = currentUserLocation,
                        onChangeLocationClick = { showLocationDialog = true },
                        onFindMedicineClick = { navController.navigate(Screen.UserResults.route) },
                        onExplorePharmaciesClick = { navController.navigate(Screen.UserPharmacies.route) },
                        onMedicineClick = { med ->
                            navController.navigate("user_medicine_details/${med.id}")
                        }
                    )
                }

                composable(Screen.UserSearch.route) {
                    SearchMedicineScreen(
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        selectedCategory = selectedCategory,
                        onCategoryChange = { viewModel.setCategory(it) },
                        maxDistanceKm = maxDistanceKm,
                        onDistanceChange = { viewModel.setMaxDistance(it) },
                        availabilityFilter = availabilityFilter,
                        onAvailabilityFilterChange = { viewModel.setAvailabilityFilter(it) },
                        userLocation = currentUserLocation,
                        onChangeLocationClick = { showLocationDialog = true },
                        onSearchSubmit = { navController.navigate(Screen.UserResults.route) },
                        onMedicineClick = { med ->
                            navController.navigate("user_medicine_details/${med.id}")
                        }
                    )
                }

                composable(Screen.UserResults.route) {
                    val searchResults = viewModel.getSearchResultForMedicine(searchQuery)
                    SearchResultsScreen(
                        query = searchQuery,
                        results = searchResults,
                        userLocation = currentUserLocation,
                        maxDistanceKm = maxDistanceKm,
                        selectedSortBy = selectedSortBy,
                        onChangeLocationClick = { showLocationDialog = true },
                        onDistanceChange = { viewModel.setMaxDistance(it) },
                        onSortChange = { viewModel.setSortBy(it) },
                        onPharmacyClick = { id -> navController.navigate("user_pharmacy_details/$id") },
                        onDirectionsClick = { lat, lon, name ->
                            com.example.utils.NavigationUtils.openDirections(context, lat, lon, name)
                        },
                        onResetFilters = {
                            viewModel.updateSearchQuery("Paracetamol 500mg")
                            viewModel.setMaxDistance(10.0)
                            viewModel.setAvailabilityFilter("All")
                            viewModel.setCategory("All")
                            viewModel.setSortBy("Nearest")
                        }
                    )
                }

                composable("user_medicine_details/{medicineId}") { backStackEntry ->
                    val medicineId = backStackEntry.arguments?.getString("medicineId")
                    val medicine = medicines.find { it.id == medicineId } ?: medicines.first()
                    val medicineRecords = inventoryList.filter { it.medicineId == medicine.id }
                    val altList = viewModel.getAlternativesWithAvailabilityForMedicine(medicine.id)

                    MedicineDetailsScreen(
                        medicine = medicine,
                        inventoryRecords = medicineRecords,
                        pharmacies = pharmacies,
                        onPharmacyClick = { id -> navController.navigate("user_pharmacy_details/$id") },
                        onDirectionsClick = { pharmId ->
                            val targetPharm = pharmacies.find { it.id == pharmId }
                            if (targetPharm != null) {
                                com.example.utils.NavigationUtils.openDirections(context, targetPharm.latitude, targetPharm.longitude, targetPharm.name)
                            }
                        },
                        onViewVerifiedAlternativesClick = { medId ->
                            navController.navigate("user_alternatives/$medId")
                        },
                        verifiedAlternativesList = altList
                    )
                }

                composable("user_alternatives/{medicineId}") { backStackEntry ->
                    val medicineId = backStackEntry.arguments?.getString("medicineId")
                    val medicine = medicines.find { it.id == medicineId }
                    val alternativesWithAvailability = if (medicineId != null) {
                        viewModel.getAlternativesWithAvailabilityForMedicine(medicineId)
                    } else emptyList()

                    AlternativeMedicinesScreen(
                        sourceMedicine = medicine,
                        alternativesWithAvailability = alternativesWithAvailability,
                        onBackClick = { navController.popBackStack() },
                        onViewMedicineDetails = { medId ->
                            navController.navigate("user_medicine_details/$medId")
                        },
                        onViewPharmaciesClick = { medId ->
                            val targetMed = medicines.find { it.id == medId }
                            if (targetMed != null) {
                                viewModel.updateSearchQuery(targetMed.name)
                                navController.navigate(Screen.UserResults.route)
                            }
                        }
                    )
                }

                composable(Screen.UserPharmacies.route) {
                    PharmacyListScreen(
                        pharmacies = pharmacies,
                        userLocation = currentUserLocation,
                        onPharmacyClick = { id -> navController.navigate("user_pharmacy_details/$id") },
                        onDirectionsClick = { pharmId ->
                            val targetPharm = pharmacies.find { it.id == pharmId }
                            if (targetPharm != null) {
                                com.example.utils.NavigationUtils.openDirections(context, targetPharm.latitude, targetPharm.longitude, targetPharm.name)
                            }
                        }
                    )
                }

                composable("user_pharmacy_details/{pharmacyId}") { backStackEntry ->
                    val pharmacyId = backStackEntry.arguments?.getString("pharmacyId")
                    val pharmacy = pharmacies.find { it.id == pharmacyId } ?: pharmacies.first()

                    PharmacyDetailsScreen(
                        pharmacy = pharmacy,
                        inventoryRecords = inventoryList,
                        medicines = medicines,
                        onDirectionsClick = {
                            com.example.utils.NavigationUtils.openDirections(context, pharmacy.latitude, pharmacy.longitude, pharmacy.name)
                        }
                    )
                }

                // Pharmacy Routes
                composable(Screen.PharmacyDashboard.route) {
                    PharmacyDashboardScreen(
                        pharmacy = userPharmacy,
                        inventoryList = inventoryList,
                        medicines = medicines,
                        predictions = stockPredictions,
                        onOpenInventoryClick = { navController.navigate(Screen.PharmacyInventory.route) },
                        onViewPredictionsClick = { navController.navigate(Screen.PharmacyPredictions.route) },
                        onEditStockClick = { viewModel.showEditStockDialog(it) }
                    )
                }

                composable(Screen.PharmacyPredictions.route) {
                    com.example.ui.pharmacy.StockPredictionsScreen(
                        pharmacy = userPharmacy,
                        predictions = stockPredictions,
                        onBackClick = { navController.popBackStack() },
                        onEditStockClick = { invId ->
                            val rec = inventoryList.find { it.id == invId }
                            if (rec != null) {
                                viewModel.showEditStockDialog(rec)
                            }
                        }
                    )
                }

                composable(Screen.PharmacyInventory.route) {
                    PharmacyInventoryScreen(
                        pharmacy = userPharmacy,
                        inventoryList = inventoryList,
                        medicines = medicines,
                        availabilityLogs = availabilityLogs,
                        onEditStockClick = { viewModel.showEditStockDialog(it) },
                        onAddMedicineClick = { medId, stock, price ->
                            viewModel.addNewInventoryItem(userPharmacy.id, medId, stock, price)
                        }
                    )
                }

                composable(Screen.PharmacyProfile.route) {
                    PharmacyProfileScreen(
                        pharmacy = userPharmacy,
                        onSaveProfileClick = { addr, phone, timing, facility ->
                            viewModel.updatePharmacyProfile(addr, phone, timing, facility)
                        }
                    )
                }

                // Admin Routes
                composable(Screen.AdminDashboard.route) {
                    AdminDashboardScreen(
                        onNavigatePharmacies = { navController.navigate(Screen.AdminPharmacies.route) },
                        onNavigateInventory = { navController.navigate(Screen.AdminInventory.route) },
                        onNavigateAnalytics = { navController.navigate(Screen.AdminAnalytics.route) },
                        onNavigateAlternatives = { navController.navigate(Screen.AdminAlternatives.route) }
                    )
                }

                composable(Screen.AdminPharmacies.route) {
                    AdminPharmaciesScreen(
                        pharmacies = pharmacies,
                        onToggleVerification = { viewModel.togglePharmacyVerification(it) }
                    )
                }

                composable(Screen.AdminInventory.route) {
                    AdminInventoryScreen(
                        medicines = medicines,
                        inventoryList = inventoryList
                    )
                }

                composable(Screen.AdminAlternatives.route) {
                    AdminAlternativesScreen(
                        alternatives = verifiedAlternatives,
                        medicines = medicines,
                        onCreateMapping = { sourceId, altId, relType, notes, status ->
                            viewModel.createAlternativeMapping(sourceId, altId, relType, notes, status)
                        },
                        onVerifyMapping = { id -> viewModel.verifyAlternativeMapping(id) },
                        onRejectMapping = { id, reason -> viewModel.rejectAlternativeMapping(id, reason) },
                        onDeactivateMapping = { id -> viewModel.deactivateAlternativeMapping(id) },
                        onDeleteMapping = { id -> viewModel.deleteAlternativeMapping(id) }
                    )
                }

                composable(Screen.AdminAnalytics.route) {
                    AdminAnalyticsScreen()
                }
            }

            // Notification alert overlay
            NotificationAlert(
                message = toastMessage,
                onDismiss = { viewModel.clearToast() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            // Stock edit dialog overlay
            if (editingRecord != null) {
                val record = editingRecord!!
                val medicine = medicines.find { it.id == record.medicineId } ?: medicines.first()
                UpdateStockDialog(
                    record = record,
                    medicine = medicine,
                    onDismiss = { viewModel.hideEditStockDialog() },
                    onConfirm = { newStock, newPrice ->
                        viewModel.updateStockQuantity(record.id, newStock, newPrice)
                    }
                )
            }

            // Location selection modal overlay
            if (showLocationDialog) {
                com.example.ui.components.LocationSelectionModalDialog(
                    currentLocation = currentUserLocation,
                    onDismiss = { showLocationDialog = false },
                    onSelectDemoLocation = { loc ->
                        viewModel.setDemoLocation(loc)
                        showLocationDialog = false
                    },
                    onSelectManualLocation = { area, city, lat, lng ->
                        viewModel.setManualLocation(area, city, lat, lng)
                        showLocationDialog = false
                    },
                    onRequestGpsLocation = {
                        viewModel.requestGpsLocation(context)
                        showLocationDialog = false
                    }
                )
            }
        }
    }
}
