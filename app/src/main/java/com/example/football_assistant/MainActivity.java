package com.example.football_assistant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Button btnNG, btnTS, btnLT, btnRem;
    private FirebaseDatabase rootNode;
    private DatabaseReference gameReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootNode = FirebaseDatabase.getInstance("https://football-assistant-1bc4c-default-rtdb.firebaseio.com/");
        gameReference = rootNode.getReference("Games");

        btnNG = (Button)findViewById(R.id.btnNewGame);
        btnTS = (Button)findViewById(R.id.btnTeamStats);
        btnLT = (Button)findViewById(R.id.btnLeagueTable);

        btnNG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewGameActivity();
            }
        });
        btnTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openteamStatsActivity();
            }
        });
        btnLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLeagueTableActivity();
            }
        });
        gameReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                notification();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void notification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("n", "n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                .setContentTitle("Football assistant")
                .setSmallIcon(R.drawable.football_icon)
                .setAutoCancel(true)
                .setContentText("New game just added!");
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());
    }

    public  void openNewGameActivity(){
        Intent intent = new Intent(this, NewGameActivity.class);
        startActivity(intent);
    }
    public  void openteamStatsActivity(){
        Intent intent = new Intent(this, TeamStatsActivity.class);
        startActivity(intent);
    }
    public  void openLeagueTableActivity(){
        Intent intent = new Intent(this, LeagueTableActivity.class);
        startActivity(intent);
    }
}