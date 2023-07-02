package com.example.istima

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.istima.services.FirebaseFirestoreService
import com.example.istima.ui.theme.IStimaTheme
import com.example.istima.utils.Global
import com.example.istima.utils.Routes
import com.example.istima.views.FeedPage
import com.example.istima.views.MainPage
import com.example.istima.views.NewReport
import com.example.istima.views.SplashScreen
import com.example.istima.views.auth.LoginPage
import com.example.istima.views.auth.RegisterPage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1234

class MainActivity : ComponentActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var locationPermissionGranted = mutableStateOf(false)

    lateinit var fusedLocationClient: FusedLocationProviderClient
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        FirebaseApp.initializeApp(this)

        val sharedPreferences: SharedPreferences =
            getSharedPreferences(Global.sharedPreferencesName, Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)
        val editor = sharedPreferences.edit()

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        editor.putString(Global.sharedPreferencesUserName, user!!.displayName)
        editor.putString(Global.sharedPreferencesUserId, user.uid)
        editor.apply()

        val firebaseFirestoreService = FirebaseFirestoreService()

        firebaseFirestoreService.getAllReports()

//        if (isFirstLaunch) {
        var startPage = "splash"
//        editor.putBoolean("isFirstLaunch", false)
//        editor.apply()
//        } else {
//            if (sharedPrefs.getString("userEmail", "null") == "null") {
//                startPage = "login"
//            } else {
//                Log.d("ABC", "MAIN TO LOAD")
//                startPage = "main"
//            }
//        }

        sharedPreferences.getString("userEmail", "null")?.let { Log.d("ABC", it) }
        getLocationPermission()

        setContent {
            IStimaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    LoginPage()
//                    FeedPage()
//                    RegisterPage()

                    val navController = rememberNavController()
                    NavigationAppHost(
                        navController = navController,
                        startDestination = startPage
                    )
                }
            }
        }
    }

//    fun startLocationUpdates() {
//        // Get the last known location
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location: Location? ->
//                // Use the location
//                location?.let {
//                    val latitude = location.latitude
//                    val longitude = location.longitude
//                    // Do something with latitude and longitude values
//                    Log.d("ABC", "Longitude: $longitude, latitude: $latitude")
//                }
//            }
//    }

    fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted.value = true//we already have the permission
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            locationPermissionGranted.value=true
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationAppHost(navController: NavHostController, startDestination: String) {
    val ctx = LocalContext.current

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.MainPage.route) { MainPage(navController) }
        composable(Routes.LoginPage.route) { LoginPage(navController) }
        composable(Routes.RegisterPage.route) { RegisterPage(navController) }
        composable(Routes.FeedPage.route) { FeedPage(navController) }
        composable(Routes.NewReport.route) { NewReport(navController) }
        composable(Routes.SplashScreen.route) { SplashScreen(navController) }
//        composable(Routes.ChatPage.route) { navBackStackEntry ->
//            val macAddress = navBackStackEntry.arguments?.getString("macAddress")
//            if (macAddress == null) {
//                Toast.makeText(ctx, "Address not provided", Toast.LENGTH_SHORT).show()
//            } else {
//                ChatPage(navController = navController, macAddress = macAddress)
//            }
//        }
    }
}
