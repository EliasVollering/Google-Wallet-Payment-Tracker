package com.workflow.google_wallet_payment_tracker

import android.app.Activity
import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import com.workflow.google_wallet_payment_tracker.data.AppDatabase
import com.workflow.google_wallet_payment_tracker.data.Purchase
import com.workflow.google_wallet_payment_tracker.ui.theme.GoogleWalletPaymentTrackerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Check if notification access permission is granted after the user returns from the settings activity
            if (!isNotificationAccessGranted()) {
                finishAffinity()
                // Permission still not granted
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleWalletPaymentTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainDisplay(modifier = Modifier, this)
                }
            }
        }
        if (!isNotificationAccessGranted()) {
            requestNotificationAccess()
        }
    }
    private fun isNotificationAccessGranted(): Boolean {
        val notificationListener = ComponentName(this, NotificationListener::class.java)
        val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(this)
        return enabledListeners.contains(notificationListener.packageName)
    }

    private fun requestNotificationAccess() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        notificationPermissionLauncher.launch(intent)
    }
}

fun toMoney(input: String): Double {
    return input.substring(input.indexOf('$') + 1, input.indexOf(' ')).toDouble()
}
fun toCard(input:String):String{
    return input.substring(input.length - 4)
}
//test
class NotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(notification: StatusBarNotification) {
        val packageManager = applicationContext.packageManager
        val appName = try {
            packageManager.getApplicationLabel(packageManager.getApplicationInfo(notification.packageName, PackageManager.GET_META_DATA)).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            notification.packageName
        }
        val location = notification.notification.extras.getString(Notification.EXTRA_TITLE).toString()
        val text = notification.notification.extras.getString(Notification.EXTRA_TEXT).toString()
        /////////////////////////////////////////////DATE///////////////////////////////////////
        val timestamp = notification.postTime
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateString = dateFormat.format(date)
        ///////////////////////////////////////////////////////////////////////////////////////
        val newcontext = this
        if (((appName.toString()).indexOf("Google") >= 0) && (text.indexOf("$") >= 0)){
            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getDatabase(newcontext).purchaseDao().upsertPurchase(Purchase(
                    location, dateString, toMoney(text), toCard(text))
                )
            }
        }
    }
    override fun onNotificationRemoved(notification: StatusBarNotification) {
        // Handle notification removal if necessary
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainDisplay( modifier: Modifier = Modifier, context: Context) {
    val purchaseDao = AppDatabase.getDatabase(context).purchaseDao()
    val purchaseList by purchaseDao.getListOfPurchases().collectAsState(initial = emptyList())
    val purchasedToday by purchaseDao.getListOfPurchasesOnDay(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).collectAsState(initial = emptyList())
    /*
    yyyy-MM-dd
     */
    var totalSpent = 0.0

    for (purchase in purchasedToday){
        Log.d("IDk", "${purchase.amount}")
        totalSpent += purchase.amount
    }

    Box(modifier = modifier.fillMaxSize()){
        if(purchaseList.isEmpty()){
            Box(modifier = modifier.fillMaxSize(), Alignment.Center){
                Text(text = "database is Empty!")
            }
        }
        else{
            LazyColumn(modifier = modifier.fillMaxSize()){
                item {
                    Box(modifier = modifier.fillMaxSize()){
                        Column(
                            modifier = modifier.fillMaxSize().align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Spent Today:",
                                fontSize = 30.sp
                            )
                            Text(
                                text = "$$totalSpent",
                                fontSize = 60.sp
                            )
                        }
                    }
                }
                for (purchase in purchaseList){
                    item {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = purchase.location,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = purchase.date,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = purchase.amount.toString(),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = purchase.card,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GoogleWalletPaymentTrackerTheme {

    }
}