package com.example.football_assistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.Random;

public class NewGameActivity extends AppCompatActivity {
    private Button btnAdd, btnBack;
    private EditText etTeamA, etTeamB, etScoreA, etScoreB, etPlace, etDate;
    private FirebaseDatabase rootNode;
    private DatabaseReference gameReference;
    private DatabaseReference teamReference;
    private int gameID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        rootNode = FirebaseDatabase.getInstance("https://football-assistant-1bc4c-default-rtdb.firebaseio.com/");
        gameReference = rootNode.getReference("Games");
        teamReference = rootNode.getReference("Teams");
        gameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    gameID = (int) dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        etTeamA = (EditText) findViewById(R.id.etTeamA);
        etTeamB = (EditText) findViewById(R.id.etTeamB);
        etScoreA = (EditText) findViewById(R.id.etScoreA);
        etScoreB = (EditText) findViewById(R.id.etScoreB);
        etPlace = (EditText) findViewById(R.id.etPlace);
        etDate = (EditText) findViewById(R.id.etDate);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnBack = (Button) findViewById(R.id.btnBackFromNG);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidateUserInput()){
                    StoreToMD();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnMainActivity();
            }
        });
    }

    private boolean ValidateUserInput() {
        //TODO: input check for dates, places and teams.
        if (!isAllDataFilled()){
            Toast.makeText(getApplicationContext(),"Please fill all data before ADD",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void returnMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public  void StoreToMD() {

        String nameA = getText(etTeamA);
        String nameB = getText(etTeamB);

        updateTeam(nameA, getText(etScoreA), getText(etScoreB));
        updateTeam(nameB, getText(etScoreB), getText(etScoreA));

        try{
            Game game = new Game(getText(etTeamA), getText(etTeamB),
                    getText(etScoreA), getText(etScoreB), getText(etPlace), getText(etDate));
            gameReference.child(String.valueOf(gameID)).setValue(game);
            Toast.makeText(getApplicationContext(),"Data saved",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Failure accured while try to save in DB:\n" + e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void updateTeam(String name, String GF, String GA) {
        try{
            Query searchedTeam = teamReference.orderByChild("name").equalTo(name);
            searchedTeam.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        //update Team DB
                        String wins = dataSnapshot.child(name).child("wins").getValue(String.class);
                        String losses = dataSnapshot.child(name).child("losses").getValue(String.class);
                        String draws = dataSnapshot.child(name).child("draws").getValue(String.class);
                        String cGF = dataSnapshot.child(name).child("gf").getValue(String.class);
                        String cGA = dataSnapshot.child(name).child("ga").getValue(String.class);
                        String points = dataSnapshot.child(name).child("points").getValue(String.class);

                        Team updated = new Team(name, wins, draws, losses, cGF, cGA, points);
                        updated.updateAfterGame(GF, GA);
                        teamReference.child(name).setValue(updated);
                        Toast.makeText(getApplicationContext(),"Team "+ name + " updated",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Team newTeam = new Team();
                        newTeam.setName(name);
                        newTeam.updateAfterGame(GF, GA);
                        teamReference.child(name).setValue(newTeam);
                        Toast.makeText(getApplicationContext(),"Team "+ name + " created",Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        catch (Exception e){

        }

    }

    private boolean isEmpty(EditText et){
        return et.getText().toString().matches("");
    }

    private boolean isAllDataFilled(){
        boolean scoreA, scoreB, teamA,teamB,place, date;
        scoreA = isEmpty(etScoreA);
        scoreB = isEmpty(etScoreB);
        teamA = isEmpty(etTeamA);
        teamB = isEmpty(etTeamB);
        place = isEmpty(etPlace);
        date = isEmpty(etDate);
        return !(scoreA || scoreB || teamA || teamB || place || date);
    }

    private String getText(EditText et){
        return et.getText().toString();
    }


}