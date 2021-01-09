package com.example.football_assistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnNG, btnTS, btnLT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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