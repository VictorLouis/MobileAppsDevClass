package com.example.elsatovar.tr3senlinea;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import android.media.MediaPlayer;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextMessage;

    private Button[][] buttons = new Button[3][3];

    private boolean player1Turn = true;

    private int roundCount;

    private int player1Points;
    private int player2Points;

    private TextView textViewPlayer1;
    private TextView textViewPlayer2;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    private SharedPreferences mPrefs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        /*
        final SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        final ListPreference difficultyLevelPref = (ListPreference) findPreference("difficulty_level");
        String difficulty = prefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_expert));
        difficultyLevelPref.setSummary((CharSequence) difficulty);

        difficultyLevelPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                difficultyLevelPref.setSummary((CharSequence) newValue);

                // Since we are handling the pref, we must save it
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString("difficulty_level", newValue.toString());
                ed.commit();
                return true;
            }
        });*/
        /*
        final EditTextPreference victoryMessagePref = (EditTextPreference)
                findPreference("victory_message");
        String victoryMessage = prefs.getString("victory_message",
                getResources().getString(R.string.result_human_wins));*/

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }

        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }

        ((Button) v).setText("X");
        roundCount++;
        if (checkForWin()) {
            player1Wins();
        }
        else if(roundCount == 9){
            draw();
        }
        cpuTurn();
        roundCount++;
        if (checkForWin()) {
            player2Wins();
        }
        else if(roundCount == 9){
            draw();
        }

    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1])
                    && field[i][0].equals(field[i][2])
                    && !field[i][0].equals("")) {
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i])
                    && field[0][i].equals(field[2][i])
                    && !field[0][i].equals("")) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1])
                && field[0][0].equals(field[2][2])
                && !field[0][0].equals("")) {
            return true;
        }

        if (field[0][2].equals(field[1][1])
                && field[0][2].equals(field[2][0])
                && !field[0][2].equals("")) {
            return true;
        }

        return false;
    }

    private void player1Wins() {
        player1Points++;
        String defaultMessage = getResources().getString(R.string.result_human_wins);
        Toast.makeText(this, mPrefs.getString("victory_message", defaultMessage), Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();

    }

    private void player2Wins() {
        player2Points++;
        Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void draw() {
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void updatePointsText() {
        textViewPlayer1.setText("Player 1: " + player1Points);
        textViewPlayer2.setText("Player 2: " + player2Points);
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }

        roundCount = 0;
        player1Turn = true;
    }

    private void cpuTurn() {
        mComputerMediaPlayer.start();

        int x = new Random().nextInt(3);
        int y = new Random().nextInt(3);
        while(!buttons[x][y].getText().toString().equals("")){
            x = new Random().nextInt(3);
            y = new Random().nextInt(3);
        }
        buttons[x][y].setText("O");
    }

    private void resetGame() {
        player1Points = 0;
        player2Points = 0;
        updatePointsText();
        resetBoard();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("roundCount", roundCount);
        outState.putInt("player1Points", player1Points);
        outState.putInt("player2Points", player2Points);
        outState.putBoolean("player1Turn", player1Turn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        roundCount = savedInstanceState.getInt("roundCount");
        player1Points = savedInstanceState.getInt("player1Points");
        player2Points = savedInstanceState.getInt("player2Points");
        player1Turn = savedInstanceState.getBoolean("player1Turn");
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.click);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.macc);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                resetGame();
                return true;
            case R.id.ai_settings:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;
            case R.id.quit:
                //showDialog(DIALOG_QUIT_ID);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings

            //mSoundOn = mPrefs.getBoolean("sound", true);

            String difficultyLevel = mPrefs.getString("difficulty_level", getResources().getString(R.string.difficulty_harder));

            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy))) {
                //mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            } else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder))) {
                //mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            } else {
                //mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
            }
        }
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    //startActivityForResult(new Intent(this, Settings.class), 0);
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

}
