package com.example.myforgraundservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.NotificationCompat
import com.example.myforgraundservice.databinding.ActivityMainBinding
import java.util.logging.Level

class MainActivity : AppCompatActivity() {

    companion object {
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        const val NOTIFICATION_ID = 0
        const val ACTION_UPDATE_NOTIFICATION =
            "com.example.myforgraundservice.ACTION_UPDATE_NOTIFICATION"
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var notifyManager: NotificationManager

    private val receiver = NotificationReceiver()

    private lateinit var btnNotify: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerReceiver(receiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))

        initComponentsUI()
        createNotificationChannel()
        setNotificationButtonState(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )

        btnNotify.setOnClickListener {
            sendNotification()
        }

        btnUpdate.setOnClickListener {
            updateNotification()
        }

        btnCancel.setOnClickListener {
            cancelNotification()
        }
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun initComponentsUI() {
        binding.also {
            btnNotify = it.btnNotify
            btnUpdate = it.btnUpdate
            btnCancel = it.btnCancel
        }
    }

    private fun sendNotification() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID,
            updateIntent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.addAction(
            R.drawable.ic_update,
            "Update Notification",
            updatePendingIntent
        )
        notifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())

        setNotificationButtonState(
            isNotifyEnabled = false,
            isUpdateEnabled = true,
            isCancelEnabled = true
        )
    }

    private fun updateNotification() {
        val androidImage = BitmapFactory.decodeResource(resources, R.drawable.mascot_1)
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated")
        )
        notifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())

        setNotificationButtonState(
            isNotifyEnabled = false,
            isUpdateEnabled = false,
            isCancelEnabled = true
        )
    }

    private fun cancelNotification() {
        notifyManager.cancel(NOTIFICATION_ID)
        setNotificationButtonState(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )
    }

    private fun setNotificationButtonState(
        isNotifyEnabled: Boolean,
        isUpdateEnabled: Boolean,
        isCancelEnabled: Boolean,
    ) {
        btnNotify.isEnabled = isNotifyEnabled
        btnUpdate.isEnabled = isUpdateEnabled
        btnCancel.isEnabled = isCancelEnabled
    }


    private fun createNotificationChannel() {
        notifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Veldan Notification",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification from Veldan"

                notifyManager.createNotificationChannel(this)
            }
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID).apply {
            setContentTitle("You been notified")
            setContentText("This is your notification text.")
            setSmallIcon(R.drawable.ic_notification)
            setContentIntent(pendingIntent)
            setAutoCancel(true)
            setDefaults(NotificationCompat.DEFAULT_ALL)
            priority = NotificationCompat.PRIORITY_HIGH
        }
    }

    inner class NotificationReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            updateNotification()
        }
    }
}