package com.example.football_assistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowGamesActivity extends AppCompatActivity {

    private FirebaseDatabase rootNode;
    private DatabaseReference gameReference;
    private DatabaseReference teamReference;
    private ListView lvGames;
    String name;
    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};
    ArrayList<String> gamesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_games);

        lvGames = (ListView) findViewById(R.id.lvGames);
        Bundle extras = getIntent().getExtras();
        if(extras == null){
            Toast.makeText(getApplicationContext(), "Could not get Team name, please return to last screen and try again", Toast.LENGTH_SHORT).show();
        }
        name = extras.getString("TEAM_NAME");
        if(extras == null) {
            Toast.makeText(getApplicationContext(), "Could not get Team name, please return to last screen and try again", Toast.LENGTH_SHORT).show();
        }
        displayGamesList(name);
    }


    private void displayGamesList(String name) {
        getGamesFromDB(name);
    }

    private void getGamesFromDB(String name) {
        try{
            ArrayList<Game> games = new ArrayList<Game>();
            rootNode = FirebaseDatabase.getInstance("https://football-assistant-1bc4c-default-rtdb.firebaseio.com/");
            gameReference = rootNode.getReference("Games");
            gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot game : dataSnapshot.getChildren()) {
                        String teamA = game.child("teamA").getValue(String.class);
                        String teamB = game.child("teamB").getValue(String.class);
                        if (!teamA.equals(name) && !teamB.equals(name))
                            continue;
                        String date = game.child("date").getValue(String.class);
                        String place = game.child("place").getValue(String.class);
                        String scoreA = game.child("scoreA").getValue(String.class);
                        String scoreB = game.child("scoreB").getValue(String.class);
                        Game currGame = new Game(teamA, teamB, scoreA, scoreB, place, date);
                        games.add(currGame);
                        Log.d("GameAdd",currGame.toString());
                    }
                    show(games);
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

    private void show(ArrayList<Game> games) {
        if (games.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Could not found games for '" + name + "' in DB", Toast.LENGTH_LONG).show();
            return;
        }
        gamesArray = new ArrayList<String>();
        for (Game g: games){
            gamesArray.add(g.toString());
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, gamesArray);
        lvGames.setAdapter(adapter);
    }
}