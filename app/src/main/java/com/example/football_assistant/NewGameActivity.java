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

import java.nio.file.Path;
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
    public static final int START_YEAR = 1900;
    public static final int VALID_YEAR = 1950;
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


    private void ValidateUserInput() {
        if (!isAllDataFilled()) {
            Toast.makeText(getApplicationContext(), "Please fill all data before ADD", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sameTeams()){
            Toast.makeText(getApplicationContext(),"Teams names must be different",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkNamesAndPlace())
            return;
        if (!isDateValid())
            return;
        new AlertDialog.Builder(this)
                .setTitle("Add Game")
                .setMessage("Are you sure you want to add this game?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        StoreToMD();
                        cleanInputs();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
        return;
    }

    private boolean checkNamesAndPlace() {
//        Pattern ptr_name = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*",Pattern.CASE_INSENSITIVE);
//        Pattern ptr_place = Pattern.compile("[a-zA-Z]+",Pattern.CASE_INSENSITIVE);
//        Matcher match_nameA = ptr_name.matcher(getText(etTeamA));
//        Matcher match_nameB = ptr_name.matcher(getText(etTeamB));
//        Matcher match_place = ptr_place.matcher(getText(etPlace));
//        boolean b_nameA = match_nameA.find();
//        boolean b_nameB = match_nameB.find();
//        boolean b_place = match_place.find();
//        if (!b_nameA || !b_nameB){
//            Toast.makeText(getApplicationContext(),"Teams name must start with letter and contain only chars and numbers",Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (!b_place){
//            Toast.makeText(getApplicationContext(),"Place name can contain only chars",Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (!IterateString(getText(etTeamA), "Team A: ") ||
            !IterateString(getText(etTeamB), "Team B: ") ||
            !IterateString(getText(etPlace), "Place: ")){
            return false;
        }
        return true;
    }

    private boolean IterateString(String s, String prefix){
        for (int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if (i == 0 && !Character.isLetter(c)){
                Toast.makeText(getApplicationContext(),prefix+"'"+s+"' must start with letter",Toast.LENGTH_LONG).show();
                return false;
            }
            else if (!Character.isLetter(c) && !Character.isDigit(c) && !(c == ' ')){
                Toast.makeText(getApplicationContext(),prefix+"'"+s+"' must contain only letters, numbers and spaces",Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private boolean sameTeams() {
        return getText(etTeamA).equals(getText(etTeamB));
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
        Date checked = new Date(dpDate.getYear() - START_YEAR, dpDate.getMonth(),dpDate.getDayOfMonth());
        if (checked.getYear() + START_YEAR < VALID_YEAR){
            Toast.makeText(getApplicationContext(),"Valid year start in 1950",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (currDate.before(checked)){
            Toast.makeText(getApplicationContext(),"Date is not valid, can't be later then today: " + strDate,Toast.LENGTH_LONG).show();
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