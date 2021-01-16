package com.example.football_assistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AtomicFile;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
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
import com.google.firebase.internal.InternalTokenProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class LeagueTableActivity extends AppCompatActivity {

    private TableLayout tb;
    private int rowNum;
    private ArrayList<Team> teams;
    private FirebaseDatabase rootNode;
    private DatabaseReference teamReference;
    public static final double BREAK_LINE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_table);
        tb = (TableLayout) findViewById(R.id.displayLinear);

        getTeamsFromDB();
    }

    private void createTable(ArrayList<Team> teams) {
        TextView tvName, tvPoints, tvWins, tvDraws, tbLosses, tvGFGA, tvDiff, tvGames;
        String name, points, wins, draws, losses, gf, ga, gfga, diff, oag;
        rowNum = teams.size();
        Collections.sort(teams);
        TableRow row;
        TableRow.LayoutParams lp;
        for (int i = 0; i < rowNum; i++) {
            row = createRow(i+1);
            lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            name = teams.get(i).getName();
            int padRow = (int)Math.ceil(name.length()/BREAK_LINE);
            points = padString(teams.get(i).getPoints(), padRow);
            wins = teams.get(i).getWins();
            draws = teams.get(i).getDraws();
            losses = teams.get(i).getLosses();
            gf = teams.get(i).getGF();
            ga = teams.get(i).getGA();
            gfga = String.format("%s:%s", gf, ga);
            diff = String.valueOf(Integer.parseInt(gf) - Integer.parseInt(ga));
            oag = String.valueOf(Integer.parseInt(wins) + Integer.parseInt(draws) + Integer.parseInt(losses));

            tvName = setView(name);
            tvPoints = setView(points);
            tvWins = setView(wins);
            tvDraws = setView(draws);
            tbLosses = setView(losses);
            tvGFGA = setView(gfga);
            tvDiff = setView(diff);
            tvGames = setView(oag);

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
        //create title row
        row = createRow(0);
        lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        row.addView(setView("Name"));
        row.addView(setView("Games"));
        row.addView(setView("Wins"));
        row.addView(setView("Draws"));
        row.addView(setView("Losses"));
        row.addView(setView("GF:GA"));
        row.addView(setView("Diff"));
        row.addView(setView("Points"));
        tb.addView(row, 0);
    }

    private String padString(String s, int p) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 1; i < p; i++){
            sb.append("\n");
        }
        return sb.toString();
    }

    private TextView setView(String text){
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setWidth(this.getWindow().getDecorView().getWidth()/8);
        
        return tv;
    }
    
    private TableRow createRow(int index){
        TableRow row = new TableRow(this);
        String backColor;
        if (index == 0) //Title
            backColor = "#00897B";
        else if (index%2 == 0) //even
            backColor = "#9E9E9E";
        else
            backColor = "#E0E0E0";
        row.setBackgroundColor(Color.parseColor(backColor));
        return row;
    }
    
    private void getTeamsFromDB() {
        //TODO: implement getTeamsFromDB();
        ArrayList<Team> teams = new ArrayList<Team>();
        rootNode = FirebaseDatabase.getInstance("https://football-assistant-1bc4c-default-rtdb.firebaseio.com/");
        teamReference = rootNode.getReference("Teams");
        teamReference.addValueEventListener(new ValueEventListener() {
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
                }
                createTable(teams);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}