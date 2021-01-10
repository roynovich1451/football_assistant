package com.example.football_assistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LeagueTableActivity extends AppCompatActivity {

    private TableLayout tb;
    private int rowNum;
    private ArrayList<Team> teams;
    private FirebaseDatabase rootNode;
    private DatabaseReference teamReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_table);

        teams = new ArrayList<Team>();
        getTeamsFromDB();
        createTable();
    }

    private void createTable() {
        TextView tvName, tvPoints, tvWins, tvDraws, tbLosses, tvGFGA, tvDiff, tvGames;
        String name, points, wins, draws, losses, gf, ga, gfga, diff, oag;

        rowNum = 5; //teams.size();
        tb = (TableLayout) findViewById(R.id.displayLinear);
        for (int i = 0; i < rowNum; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            name = teams.get(i).getName();
            points = teams.get(i).getPoints();
            wins = teams.get(i).getWins();
            draws = teams.get(i).getDraws();
            losses = teams.get(i).getLosses();
            gf = teams.get(i).getGF();
            ga = teams.get(i).getGA();
            gfga = String.format("%s:%s", gf, ga);
            diff = String.valueOf(Integer.parseInt(gf) - Integer.parseInt(ga));
            oag = String.valueOf(Integer.parseInt(wins) + Integer.parseInt(draws) + Integer.parseInt(losses));

            tvName = new TextView(this);
            tvName.setText(name);
            tvName.setGravity(Gravity.CENTER);
            tvPoints = new TextView(this);
            tvPoints.setText(points);
            tvPoints.setGravity(Gravity.CENTER);
            tvWins = new TextView(this);
            tvWins.setText(wins);
            tvWins.setGravity(Gravity.CENTER);
            tvDraws = new TextView(this);
            tvDraws.setText(draws);
            tvDraws.setGravity(Gravity.CENTER);
            tbLosses = new TextView(this);
            tbLosses.setText(losses);
            tbLosses.setGravity(Gravity.CENTER);
            tvGFGA = new TextView(this);
            tvGFGA.setText(gfga);
            tvGFGA.setGravity(Gravity.CENTER);
            tvDiff = new TextView(this);
            tvDiff.setText(diff);
            tvDiff.setGravity(Gravity.CENTER);
            tvGames = new TextView(this);
            tvGames.setText(oag);
            tvGames.setGravity(Gravity.CENTER);

            row.addView(tvName);
            row.addView(tvGames);
            row.addView(tvWins);
            row.addView(tvDraws);
            row.addView(tbLosses);
            row.addView(tvGFGA);
            row.addView(tvDiff);
            row.addView(tvPoints);
            tb.addView(row, i);
        }

    }

    private void getTeamsFromDB() {
        //TODO: implement getTeamsFromDB();
        rootNode = FirebaseDatabase.getInstance("https://football-assistant-1bc4c-default-rtdb.firebaseio.com/");
        teamReference = rootNode.getReference("Teams");
        Query searchedTeam = teamReference.orderByChild("points");
        searchedTeam.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot team : dataSnapshot.getChildren()) {
                    String name = team.child("name").getValue(String.class);
                    String points = team.child("points").getValue(String.class);
                    String wins = team.child("wins").getValue(String.class);
                    String draws = team.child("draws").getValue(String.class);
                    String losses = team.child("losses").getValue(String.class);
                    String gf = team.child("gf").getValue(String.class);
                    String ga = team.child("ga").getValue(String.class);
                    Team currTeam = new Team(name, wins, draws, losses, gf, ga, points);
                    teams.add(currTeam);
                    Log.d("TeamAdd",currTeam.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}