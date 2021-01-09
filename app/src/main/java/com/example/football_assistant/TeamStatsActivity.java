package com.example.football_assistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeamStatsActivity extends AppCompatActivity {

    private FirebaseDatabase rootNode;
    private DatabaseReference gameReference;
    private DatabaseReference teamReference;
    private Button btnBack, btnSearch, btnGames;
    private EditText etTeam;
    private TextView tvPoints, tvWins, tvLosses, tvDraws, tvGF, tvGA; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_stats);
        tvPoints = (TextView) findViewById(R.id.tvDispPoints); 
        tvWins = (TextView) findViewById(R.id.tvDispWins);
        tvLosses = (TextView) findViewById(R.id.tvDispLooses);
        tvDraws = (TextView) findViewById(R.id.tvDispDraws);
        tvGF = (TextView) findViewById(R.id.tvDispGF);
        tvGA = (TextView) findViewById(R.id.tvDispGA);
        etTeam = (EditText) findViewById(R.id.etTeamStat);
        btnBack = (Button) findViewById(R.id.btnBackFromTS);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnGames = (Button) findViewById(R.id.btnShowGames);
        btnGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTeam.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter wanted team", Toast.LENGTH_SHORT).show();
                    return;
                }
                openShowGamesActivity(etTeam.getText().toString());
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTeam.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter wanted team", Toast.LENGTH_SHORT).show();
                    return;
                }
                getTeamStats();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnMainActivity();
            }
        });
    }

    private void openShowGamesActivity(String tName) {
        Intent intent = new Intent(this, ShowGamesActivity.class);
        intent.putExtra("TEAM_NAME", tName);
        startActivity(intent);
    }

    public void getTeamStats(){
        try{
            rootNode = FirebaseDatabase.getInstance("https://football-assistant-1bc4c-default-rtdb.firebaseio.com/");
            gameReference = rootNode.getReference("Games");
            teamReference = rootNode.getReference("Teams");
            String name = etTeam.getText().toString();
            Query searchedTeam = teamReference.orderByChild("name").equalTo(name);
            searchedTeam.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //Initial intent with all relevant data
                        String wins = dataSnapshot.child(name).child("wins").getValue(String.class);
                        String losses = dataSnapshot.child(name).child("losses").getValue(String.class);
                        String draws = dataSnapshot.child(name).child("draws").getValue(String.class);
                        String GF = dataSnapshot.child(name).child("gf").getValue(String.class);
                        String GA = dataSnapshot.child(name).child("ga").getValue(String.class);
                        String points = dataSnapshot.child(name).child("points").getValue(String.class);
                        displayResult(wins, losses, draws, GF, GA, points);
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
            Toast.makeText(getApplicationContext(),"Failure accured while try to save in DB:\n" + e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void displayResult(String wins, String losses, String draws, String gf, String ga, String points) {
        tvWins.setText(wins);
        tvLosses.setText(losses);
        tvDraws.setText(draws);
        tvGF.setText(gf);
        tvGA.setText(ga);
        tvPoints.setText(points);
    }

    public void returnMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}