package com.example.football_assistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telecom.Call;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import javax.security.auth.callback.Callback;

public class TeamStatsActivity extends AppCompatActivity {

    private FirebaseDatabase rootNode;
    private DatabaseReference teamReference;
    private Button btnRemove, btnSearch, btnGames;
    private Spinner spinner;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_stats);

        spinner = (Spinner) findViewById(R.id.spTeams);
        createSpinner();
        btnRemove = (Button) findViewById(R.id.btnRemoveTeam);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(TeamStatsActivity.this)
                        .setTitle("Remove Game")
                        .setMessage("Do you really want to Remove Team '"+spinner.getSelectedItem().toString()+"' From DB?\nTHIS ACTION CAN NOT UNDO")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                checkIfTeamExist("Remove");
//                                createSpinner();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfTeamExist("Stats");
            }
        });

        btnGames = (Button) findViewById(R.id.btnShowGames);
        btnGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfTeamExist("Games");
            }
        });
    }
    private void createSpinner() {
        try{
            ArrayList<String> teams_names = new ArrayList<String>();
            rootNode = FirebaseDatabase.getInstance("https://football-assistant-1bc4c-default-rtdb.firebaseio.com/");
            teamReference = rootNode.getReference("Teams");
            teamReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    teams_names.clear();
                    for (DataSnapshot team : dataSnapshot.getChildren()) {
                        teams_names.add(team.child("name").getValue(String.class));
                    }
                    showList(teams_names);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "Failure while try to load from DB:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void showList(ArrayList<String> teams_names) {
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.spinner_item,teams_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(null);
        spinner.setAdapter(adapter);
    }

    private void openTeamStatActivity(String tName) {
            Intent intent = new Intent(getApplicationContext(), ShowTeamStatsActivity.class);
            intent.putExtra("TEAM_NAME", tName);
            startActivity(intent);
    }

    private void openShowGamesActivity(String tName) {
        Intent intent = new Intent(getApplicationContext(), ShowGamesActivity.class);
        intent.putExtra("TEAM_NAME", tName);
        startActivity(intent);
    }

    public void checkIfTeamExist(String type) {
        try {
            rootNode = FirebaseDatabase.getInstance("https://football-assistant-1bc4c-default-rtdb.firebaseio.com/");
            teamReference = rootNode.getReference("Teams");
            String name = spinner.getSelectedItem().toString();
            Query searchedTeam = teamReference.orderByChild("name").equalTo(name);
            searchedTeam.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        switch(type) {
                            case "Remove":
                                teamReference.child(name).removeValue();
                                Toast.makeText(getApplicationContext(), "Team '" + name + "' has been deleted from DB", Toast.LENGTH_SHORT).show();
                                return;
                            case "Games":
                                openShowGamesActivity(name);
                                break;
                            case "Stats":
                                openTeamStatActivity(name);
                                break;
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),"Team '" + name + "' could not be found in DB", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failure while try to search in DB:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}