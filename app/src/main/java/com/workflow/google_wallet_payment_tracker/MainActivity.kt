package com.workflow.google_wallet_payment_tracker

import android.app.Activity
import android.app.Notification
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat
import com.workflow.google_wallet_payment_tracker.ui.theme.GoogleWalletPaymentTrackerTheme
//test comment for dev branch

var temp_title = ""


class MainActivity : ComponentActivity() {
    private val notificationPermissionCode = 1001
    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Check if notification access permission is granted after the user returns from the settings activity
            if (!isNotificationAccessGranted()) {
                // Permission still not granted, handle this case appropriately
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleWalletPaymentTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
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

class NotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(notification: StatusBarNotification) {
        val title = notification.notification.extras.getString(Notification.EXTRA_TITLE)
        val text = notification.notification.extras.getString(Notification.EXTRA_TEXT)
        temp_title = title.toString()
        // Store the notification title and text in SharedPreferences or a database
    }

    override fun onNotificationRemoved(notification: StatusBarNotification) {
        // Handle notification removal if necessary
    }
}


@Composable
fun Greeting( modifier: Modifier = Modifier) {
    var refreshNotifications = remember {
        mutableStateOf(temp_title)
    }
    Box(modifier = modifier.fillMaxSize(), Alignment.Center) {
        Text(text = refreshNotifications.value)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GoogleWalletPaymentTrackerTheme {
        Greeting()
    }
}