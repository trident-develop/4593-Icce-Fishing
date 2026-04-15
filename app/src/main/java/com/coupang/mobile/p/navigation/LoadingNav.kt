package com.coupang.mobile.p.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coupang.mobile.p.LoadingActivity
import com.coupang.mobile.p.MainActivity
import com.coupang.mobile.p.gray5.G5Storage
import com.coupang.mobile.p.gray5.Gray5
import com.coupang.mobile.p.gray5.STUB_STORAGE_VALUE_TRUE
import com.coupang.mobile.p.gray5.k
import com.coupang.mobile.p.screens.ConnectScreen
import com.coupang.mobile.p.screens.LoadingScreen
import com.coupang.mobile.p.screens.isFlowersConnected
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@Composable
fun LoadingGraph() {

    val navController = rememberNavController()
    val activity = LocalActivity.current
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = if (context.isFlowersConnected()) Routes.LOADING else Routes.CONNECT
    ) {
        composable(Routes.LOADING) {

            val conn = remember { context.isFlowersConnected() }

            if (conn) {
                Gray5(toStub = {
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d("TAGG", "Save Stub TRUE")
                        val storage = G5Storage.getInstance(context)
                        storage.put(k("sx5"), STUB_STORAGE_VALUE_TRUE)
                        Log.d("TAGG", "Delete push token")
                        try {
                            FirebaseMessaging.getInstance().deleteToken()
                        }catch (e: Exception){

                        }
                    }
                    context.startActivity(Intent(context, MainActivity::class.java))
                    activity?.finish()
                }, toNoInternet = {
                    navController.navigate(Routes.CONNECT) {
                        popUpTo(Routes.LOADING) { inclusive = true }
                    }
                })
            } else {
                LaunchedEffect(Unit) {
                    delay(2133)
                    navController.navigate(Routes.CONNECT) {
                        popUpTo(Routes.LOADING) { inclusive = true }
                    }
                }
            }

//            LaunchedEffect(Unit) {
//                delay(2000)
//                context.startActivity(Intent(context, MainActivity::class.java))
//                context.finish()
//            }

            LoadingScreen({})
        }

        composable(Routes.CONNECT) {
            ConnectScreen(navController)
        }
    }
}