package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends Login {

    private static final String PREFS_NAME = "NotePrefs";
    private static final String KEY_NOTE_COUNT = "NoteCount";
    private LinearLayout notesContainer;
    private List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesContainer = findViewById(R.id.notesContainer);
        Button saveButton = findViewById(R.id.saveButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogout();
            }
        });


        noteList = new ArrayList<>();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        loadNotesFromPreferences();
        displayNotes();
    }




    private int loadModeFromPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt("NightMode", AppCompatDelegate.MODE_NIGHT_NO); // Default là chế độ sáng
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppCompatDelegate.setDefaultNightMode(loadModeFromPreferences());
    }

    private void saveNote() {
        EditText titleEditText = findViewById(R.id.titleEditText);
        EditText contentEditText = findViewById(R.id.contentEditText);

        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        if (!title.isEmpty() && !content.isEmpty()) {
            Note note = new Note(title, content);
            noteList.add(note);
            createNoteView(note);
            saveNotesToPreferences();
            clearInputFields();

            Toast.makeText(MainActivity.this, "Note saved successfully", Toast.LENGTH_SHORT).show();
        }
        showSavedNotification(content);
    }

    private void clearInputFields() {
        EditText titleEditText = findViewById(R.id.titleEditText);
        EditText contentEditText = findViewById(R.id.contentEditText);

        titleEditText.getText().clear();
        contentEditText.getText().clear();
    }

    private void createNoteView(final Note note) {
        View noteView = getLayoutInflater().inflate(R.layout.note_item, null);
        TextView titleTextView = noteView.findViewById(R.id.titleTextView);
        TextView contentTextView = noteView.findViewById(R.id.contentTextView);

        titleTextView.setText(note.getTitle());
        contentTextView.setText(note.getContent());

        noteView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(note);
                return true;
            }
        });

        notesContainer.addView(noteView);
    }

    private void showDeleteDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this note?");
        builder.setMessage("Are you sure you want to delete this note?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNoteAndRefresh(note);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteNoteAndRefresh(Note note) {
        noteList.remove(note);
        saveNotesToPreferences();
        refreshNoteViews();

    }

    private void refreshNoteViews() {
        notesContainer.removeAllViews();
        displayNotes();
    }


    private void displayNotes() {
        for (Note note : noteList) {
            createNoteView(note);
        }
    }

    private void loadNotesFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int noteCount = sharedPreferences.getInt(KEY_NOTE_COUNT, 0);

        for (int i = 0; i < noteCount; i++) {
            String title = sharedPreferences.getString("note_title_" + i, "");
            String content = sharedPreferences.getString("note_content_" + i, "");
            Note note = new Note(title, content);
            noteList.add(note);
        }
    }

    private void saveNotesToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        int noteCount = noteList.size();
        editor.putInt(KEY_NOTE_COUNT, noteCount);

        for (int i = 0; i < noteCount; i++) {
            editor.putString("note_title_" + i, noteList.get(i).getTitle());
            editor.putString("note_content_" + i, noteList.get(i).getContent());
        }

        editor.apply();
    }



    private void performLogout() {
        SharedPreferences preferences = getSharedPreferences("MY_APP", MODE_PRIVATE);
        preferences.edit().remove("LoggedInUser").apply();

        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showSavedNotification(String noteContent) {
        Log.d("Notification", "showSavedNotification is called with note content: " + noteContent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Note Saved")
                .setContentText(noteContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onStart() {
        super.onStart();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                // Mạng có sẵn
            }

            @Override
            public void onLost(Network network) {
                // Mạng bị mất
            }
        };

        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }



}