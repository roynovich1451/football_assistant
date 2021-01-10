package com.example.football_assistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowTeamStatsActivity extends AppCompatActivity {

    TextView tvWins, tvLosses, tvGames, tvDraws, tvGA, tvGF, tvPoints, tvName;
    private FirebaseDatabase rootNode;
    private DatabaseReference teamReference;
    Button btnBack;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_team_stats);

        rootNode = FirebaseDatabase.getInstance("https://football-assistant-1bc4c-default-rtdb.firebaseio.com/");
        teamReference = rootNode.getReference("Teams");

        tvGames = (TextView)findViewById(R.id.tvGames);
        tvName = (TextView)findViewById(R.id.tvName);
        tvPoints = (TextView)findViewById(R.id.tvPoints);
        tvWins = (TextView)findViewById(R.id.tvWins);
        tvDraws = (TextView)findViewById(R.id.tvDraws);
        tvLosses = (TextView)findViewById(R.id.tvLosses);
        tvGA = (TextView)findViewById(R.id.tvGA);
        tvGF = (TextView)findViewById(R.id.tvGF);

        btnBack = (Button) findViewById(R.id.btnReaturnFromTeamStats);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainMenu();
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            Toast.makeText(getApplicationContext(), "Could not get Team name, please return to last screen and try again", Toast.LENGTH_SHORT).show();
        }
        name = extras.getString("TEAM_NAME");
        if(extras == null) {
            Toast.makeText(getApplicationContext(), "Could not get Team name, please return to last screen and try again", Toast.LENGTH_SHORT).show();
        }
        displayTeamStats(name);
    }

    private void returnToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void displayResult(String wins, String losses, String draws, String gf, String ga, String points, String games) {
        tvName.setText(name);
        tvWins.setText(wins);
        tvLosses.setText(losses);
        tvDraws.setText(draws);
        tvGF.setText(gf);
        tvGA.setText(ga);
        tvPoints.setText(points);
        tvGames.setText(games);
    }

    private void displayTeamStats(String name) {
        try{
            Query searchedTeam = teamReference.orderByChild("name").equalTo(name);
            searchedTeam.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String wins = dataSnapshot.child(name).child("wins").getValue(String.class);
                        String losses = dataSnapshot.child(name).child("losses").getValue(String.class);
                        String draws = dataSnapshot.child(name).child("draws").getValue(String.class);
                        String GF = dataSnapshot.child(name).child("gf").getValue(String.class);
                        String GA = dataSnapshot.child(name).child("ga").getValue(String.class);
                        String points = dataSnapshot.child(name).child("points").getValue(String.class);
                        String games = String.valueOf(Integer.parseInt(wins) + Integer.parseInt(draws) + Integer.parseInt(losses));
                        displayResult(wins, losses, draws, GF, GA, points, games);
                    } else {
                        Toast.makeText(getApplicationContext(), "Team '" + name + "' could not be found in DB", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Failure while try to Load from DB:\n" + e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}