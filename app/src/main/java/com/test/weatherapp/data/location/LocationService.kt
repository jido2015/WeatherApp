package com.test.weatherapp.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.location.Geocoder
import android.location.Address
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class LocationService @Inject constructor(
    private val context: Context, // Application context injected
    private val fusedLocationProviderClient: FusedLocationProviderClient // FusedLocationProviderClient injected
) {

    // Check if the location permission is granted
    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Suspend function to retrieve the last known location, with permission handling
    suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { continuation ->
        if (isLocationPermissionGranted()) {
            try {
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location ->
                        continuation.resume(location) // Return the location
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception) // Handle the failure
                    }
            } catch (e: SecurityException) {
                // Handle the SecurityException if permission is not granted at runtime
                continuation.resume(null)
            }
        } else {
            // If permission is not granted, return null or handle the case appropriately
            continuation.resume(null)
        }
    }

}

// Extension function to get city name from latitude and longitude using Geocoder API
suspend fun Geocoder.getCity(
    latitude: Double,
    longitude: Double,
): String? = withContext(Dispatchers.IO) {
    try {
        val addresses: List<Address>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { cont ->
                getFromLocation(latitude, longitude, 1) {
                    cont.resume(it)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            suspendCancellableCoroutine { cont ->
                val addressList = getFromLocation(latitude, longitude, 1)
                cont.resume(addressList)
            }
        }

        // Extract the city from the first address
        val address = addresses?.firstOrNull()
        return@withContext address?.locality // Return the city name or null
    } catch (e: Exception) {
        Log.d("LocationService", "Error getting city", e)
        null
    }
}
