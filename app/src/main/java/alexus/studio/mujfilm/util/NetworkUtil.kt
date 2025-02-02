package alexus.studio.mujfilm.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtil {
    fun isOnline(context: Context): Boolean {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            Log.d("NetworkUtil", "Internet connection available: $hasInternet")
            return hasInternet
            
        } catch (e: Exception) {
            Log.e("NetworkUtil", "Error checking internet connection", e)
            return false
        }
    }

    fun canReachTmdbApi(): Boolean {
        return try {
            val url = URL("https://api.themoviedb.org/3/")
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                connectTimeout = 5000
                readTimeout = 5000
                requestMethod = "HEAD"
            }
            
            val responseCode = connection.responseCode
            Log.d("NetworkUtil", "TMDB API response code: $responseCode")
            responseCode in 200..399
            
        } catch (e: IOException) {
            Log.e("NetworkUtil", "Cannot reach TMDB API", e)
            false
        } finally {
            Log.d("NetworkUtil", "Finished checking TMDB API availability")
        }
    }
}
