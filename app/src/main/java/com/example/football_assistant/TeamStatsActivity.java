package com.example.football_assistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TeamStatsActivity extends AppCompatActivity {

    private FirebaseDatabase rootNode;
    private DatabaseReference teamReference;
    private Button btnRemove, btnSearch, btnGames;
    private EditText etTeam;
//    private boolean teamExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_stats);

        etTeam = (EditText) findViewById(R.id.etTeamStat);

        btnRemove = (Button) findViewById(R.id.btnRemoveTeam);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTeamFromDB();
            }
        });

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTeam.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter wanted team", Toast.LENGTH_SHORT).show();
                    return;
                }
                openTeamStatActivity(etTeam.getText().toString());
            }
        });

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
    }

    private void openTeamStatActivity(String tName) {
//        checkIfTeamExist();

            Intent intent = new Intent(this, ShowTeamStatsActivity.class);
            intent.putExtra("TEAM_NAME", tName);
            startActivity(intent);
    }

    private void removeTeamFromDB() {
        try {
            rootNode = FirebaseDatabase.getInstance("https://football-assistant-1bc4c-default-rtdb.firebaseio.com/");
            teamReference = rootNode.getReference("Teams");
            String name = etTeam.getText().toString();
            Query searchedTeam = teamReference.orderByChild("name").equalTo(name);
            searchedTeam.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
//                        teamExist = true;
                        teamReference.child(name).removeValue();
                        Toast.makeText(getApplicationContext(), "Team '" + name + "' has been deleted from DB", Toast.LENGTH_SHORT).show();
                    } else {
//                        teamExist = false;
                        Toast.makeText(getApplicationContext(), "Team '" + name + "' could not be found in DB", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failure while try to remove from DB:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

//    public void checkIfTeamExist() {
//        try {
//            rootNode = FirebaseDatabase.getInstance("https://football-assistant-1bc4c-default-rtdb.firebaseio.com/");
//            teamReference = rootNode.getReference("Teams");
//            String name = etTeam.getText().toString();
//            Query searchedTeam = teamReference.orderByChild("name").equalTo(name);
//            searchedTeam.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        teamExist = true;
//                    } else {
//                        teamExist = false;
//                        Toast.makeText(getApplicationContext(),"Team '" + name + "' could not be found in DB", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                }
//            });
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "Failure while try to search in DB:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }


    private void openShowGamesActivity(String tName) {
//        checkIfTeamExist();

        Intent intent = new Intent(this, ShowGamesActivity.class);
        intent.putExtra("TEAM_NAME", tName);
        startActivity(intent);
    }
}