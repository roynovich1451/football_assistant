package com.example.football_assistant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewGameActivity extends AppCompatActivity {
    private Button btnAdd, btnBack;
    private EditText etTeamA, etTeamB, etScoreA, etScoreB, etPlace;
    private DatePicker dpDate;
    private FirebaseDatabase rootNode;
    private DatabaseReference gameReference;
    private DatabaseReference teamReference;
    private int gameID = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        rootNode = FirebaseDatabase.getInstance("https://football-assistant-1bc4c-default-rtdb.firebaseio.com/");
        gameReference = rootNode.getReference("Games");
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
        teamReference = rootNode.getReference("Teams");

        etTeamA = (EditText) findViewById(R.id.etTeamA);
        etTeamB = (EditText) findViewById(R.id.etTeamB);
        etScoreA = (EditText) findViewById(R.id.etScoreA);
        etScoreB = (EditText) findViewById(R.id.etScoreB);
        etPlace = (EditText) findViewById(R.id.etPlace);
        dpDate = (DatePicker) findViewById(R.id.dpDate);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnBack = (Button) findViewById(R.id.btnBackFromNG);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateUserInput();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnMainActivity();
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

    private void ValidateUserInput() {
        if (!isAllDataFilled()){
            Toast.makeText(getApplicationContext(),"Please fill all data before ADD",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isDateValid())
            return;
        new AlertDialog.Builder(this)
                .setTitle("Add Game")
                .setMessage("Do you really want to save game into DB?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        StoreToMD();
                        cleanInputs();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
        return;
    }

    private void cleanInputs() {
        etPlace.setText("");
        etTeamA.setText("");
        etScoreA.setText("");
        etScoreB.setText("");
        etTeamB.setText("");
    }

    private boolean isDateValid() {

        Date currDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        String strDate = formatter.format(currDate);
        Date checked = new Date(dpDate.getYear()-1900, dpDate.getMonth(),dpDate.getDayOfMonth());
        if (currDate.before(checked)){
            Toast.makeText(getApplicationContext(),"Date is not valid, can't be later then today: " + strDate,Toast.LENGTH_SHORT).show();
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
            String date = String.format("%02d/%02d/%d", dpDate.getDayOfMonth(), dpDate.getMonth()+1, dpDate.getYear());
            Game game = new Game(getText(etTeamA), getText(etTeamB),
                    getText(etScoreA), getText(etScoreB), getText(etPlace), date);
            gameReference.child(String.valueOf(gameID++)).setValue(game);
            Toast.makeText(getApplicationContext(),"New game saved",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Failure while try to save in DB:\n" + e.getMessage(),Toast.LENGTH_LONG).show();
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
                    }
                    else {
                        Team newTeam = new Team();
                        newTeam.setName(name);
                        newTeam.updateAfterGame(GF, GA);
                        teamReference.child(name).setValue(newTeam);
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
        return !(scoreA || scoreB || teamA || teamB || place);
    }

    private String getText(EditText et){
        return et.getText().toString().toLowerCase().trim();
    }
}