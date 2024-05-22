package com.example.notification;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private Button toastNotificationButton;
    private Button statusNotificationButton;
    private Button dialogNotificationButton;

    private static final String CHANNEL_ID = "example_channel";
    private static final int NOTIFICATION_PERMISSION_CODE = 100;
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toastNotificationButton = findViewById(R.id.toastNotificationButton);
        statusNotificationButton = findViewById(R.id.statusNotificationButton);
        dialogNotificationButton = findViewById(R.id.dialogNotificationButton);

        toastNotificationButton.setOnClickListener(view -> showToastNotification());
        statusNotificationButton.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
                } else {
                    showStatusNotification();
                }
            } else {
                showStatusNotification();
            }
        });
        dialogNotificationButton.setOnClickListener(view -> showDialogNotification());

        createNotificationChannel();
    }

    private void showToastNotification() {
        Toast.makeText(this, "This is a Toast Notification", Toast.LENGTH_SHORT).show();
    }

    private void showStatusNotification() {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Special Offer!")
                .setContentText("Get 10% off on all vegetables!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void showDialogNotification() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dialog Notification")
                .setMessage("This is a Dialog Notification")
                .setPositiveButton("OK", (dialog, which) -> Toast.makeText(MainActivity.this, "OK clicked", Toast.LENGTH_SHORT).show())
                .setNegativeButton("Cancel", (dialog, which) -> Toast.makeText(MainActivity.this, "Cancel clicked", Toast.LENGTH_SHORT).show());
        builder.create().show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Example Channel";
            String description = "This is an example notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showStatusNotification();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}