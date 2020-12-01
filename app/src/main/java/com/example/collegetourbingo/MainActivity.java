package com.example.collegetourbingo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;

import androidx.room.Room;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // The number of terms in a row or column
    public static final int BINGO_WIDTH = 5;

    private Random rand = new Random();

    private int bingos = 0;
    // A timestamp from when the player got their first bingo this game
    private long timeOfFirstBingo = 0;
    // A timestamp from when the game started
    private long gameStartTime = 0;

    public int numBingoTerms;
    public List<String> allTerms = new ArrayList<>();

    private String college = "";

    private BingoBox[][] bingoBoard = new BingoBox[BINGO_WIDTH][BINGO_WIDTH];
    // The menu item that displays the number of bingos that have been gotten
    private MenuItem bingoDisplay = null;

    public static CollegeDatabase database;

    private static MainActivity instance;
    public static MainActivity getInstance() {
        return instance;
    }

    public MainActivity() {
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The number of bingo terms is held in a string so only one file needs to be changed when adding terms
        numBingoTerms = Integer.parseInt(getString(R.string.num_bingo_terms));
        for(int i = 0; i < numBingoTerms; i++) {
            allTerms.add(getString(getResources().getIdentifier("bingo_term_" + i, "string", getPackageName())));
        }

        setContentView(R.layout.activity_main);

        database = Room
                .databaseBuilder(getApplicationContext(), CollegeDatabase.class, "collegeData")
                .allowMainThreadQueries()
                .build();

        // Programmatically add the BingoBoxes to the grid
        for(int i = 0; i < BINGO_WIDTH; i++) {
            for(int j = 0; j < BINGO_WIDTH; j++) {
                // https://stackoverflow.com/a/10348166
                Point size = new Point();
                getWindowManager().getDefaultDisplay().getSize(size);

                GridLayout.Spec row = GridLayout.spec(i);
                GridLayout.Spec column = GridLayout.spec(j);
                BingoBox bingobox = new BingoBox(this);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams(row, column);
                params.width = size.x / BINGO_WIDTH - 2;
                params.height = size.y / 6;
                params.setMargins(0,2,2,0);

                bingobox.setLayoutParams(params);
                bingobox.setGravity(Gravity.CENTER_HORIZONTAL);

                bingoBoard[i][j] = bingobox;
                ((GridLayout)findViewById(R.id.bingolayout)).addView(bingobox, params);
            }
        }

        newGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        bingoDisplay = menu.findItem(R.id.bingos);
        if(bingos > 0) {
            bingoDisplay.setTitle(getString(R.string.bingo) + ((bingos == 1) ? "" : " x" + bingos));
        } else {
            bingoDisplay.setTitle("");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.select_college:
                Intent intent = new Intent(this, CollegeSelectActivity.class);

                startActivity(intent);
                break;
            case R.id.new_game:
                // Warn the player if they're going to clear a game in-progress
                if(anyMarked()) {
                    // https://stackoverflow.com/a/2478662
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    newGame();
                                    updateBingos();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.dialog_clear_board).setPositiveButton(R.string.dialog_yes, dialogClickListener)
                            .setNegativeButton(R.string.dialog_no, dialogClickListener).show();
                } else {
                    newGame();
                    updateBingos();
                }
                break;
            case R.id.save_results:
                // Save the all the terms that were marked to a single field in the table
                String termsMarked = "";
                for(BingoBox[] row : bingoBoard) {
                    for(BingoBox b : row) {
                        if(b.isMarked()) {
                            // Separate terms with tabs
                            termsMarked += b.getText() + "\t";
                        }
                    }
                }

                database.collegeDataDao().add(college, bingos, timeOfFirstBingo - gameStartTime, termsMarked);
                break;
            case R.id.view_results:
                intent = new Intent(this, PlayedCollegesActivity.class);

                startActivity(intent);
                break;
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        // https://stackoverflow.com/a/5441329
        menu.findItem(R.id.save_results).setEnabled(!college.equals(""));
        return true;
    }

    /**
     * Prepares the board for a new game
     */
    public void newGame() {
        // Keep track of the terms that haven't been added so we don't accidentally add duplicates
        List<String> remainingTerms = new ArrayList<>(allTerms);

        // Unmark the squares and rerandomize the text
        for(BingoBox[] row : bingoBoard) {
            for(BingoBox b : row) {
                b.setMarked(false);
                String term = remainingTerms.get(rand.nextInt(remainingTerms.size()));
                // If we don't have enough terms to fill the board without duplicates, then allow for duplicates
                if(numBingoTerms >= BINGO_WIDTH * BINGO_WIDTH)
                    remainingTerms.remove(term);

                b.setText(term);
            }
        }

        // https://stackoverflow.com/a/2478662
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(instance, CollegeSelectActivity.class);

                        startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        college = "";
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_select_college).setPositiveButton(R.string.dialog_yes, dialogClickListener)
                .setNegativeButton(R.string.dialog_no, dialogClickListener).show();

        // Reset the game time
        gameStartTime = System.currentTimeMillis();
        timeOfFirstBingo = 0;
    }

    /**
     * Checks if any squares have been marked
     *
     */
    public boolean anyMarked() {
        for(BingoBox[] row : bingoBoard) {
            for(BingoBox b : row) {
                if(b.isMarked())
                    return true;
            }
        }

        return false;
    }

    /**
     * Updates the number of bingos the player has gotten.
     *
     * @return the number of bingos
     */
    public int updateBingos() {
        int oldBingos = bingos;
        bingos = 0;

        // Row bingos
        for(int i = 0; i < BINGO_WIDTH; i++) {
            boolean bingo = true;
            for(int j = 0; j < BINGO_WIDTH; j++) {
                bingo &= bingoBoard[i][j].isMarked();
                // Reset the Bingo status of every square so that they don't stay red after being unmarked
                bingoBoard[i][j].setPartOfBingo(false);
            }
            if(bingo) {
                bingos++;
                for(int j = 0; j < BINGO_WIDTH; j++) {
                    bingoBoard[i][j].setPartOfBingo(true);
                }
            }
        }
        // Column bingos
        for(int i = 0; i < BINGO_WIDTH; i++) {
            boolean bingo = true;
            for(int j = 0; j < BINGO_WIDTH; j++) {
                bingo &= bingoBoard[j][i].isMarked();
            }
            if(bingo) {
                bingos++;
                for(int j = 0; j < BINGO_WIDTH; j++) {
                    bingoBoard[j][i].setPartOfBingo(true);
                }
            }
        }
        // Diagonal bingos
        boolean diagBingo1 = true, diagBingo2 = true;
        for(int i = 0; i < BINGO_WIDTH; i++) {
            diagBingo1 &= bingoBoard[i][i].isMarked();
            diagBingo2 &= bingoBoard[i][BINGO_WIDTH - 1 - i].isMarked();
        }
        if(diagBingo1) bingos++;
        if(diagBingo2) bingos++;

        for(int i = 0; i < BINGO_WIDTH; i++) {
            if(diagBingo1) bingoBoard[i][i].setPartOfBingo(true);
            if(diagBingo2) bingoBoard[i][BINGO_WIDTH - 1 - i].setPartOfBingo(true);
        }

        if(bingos > 0) {
            bingoDisplay.setTitle(getString(R.string.bingo) + ((bingos == 1) ? "" : " x" + bingos));
        } else {
            bingoDisplay.setTitle("");
        }

        if(oldBingos <= 0 && bingos > 0) {
            timeOfFirstBingo = System.currentTimeMillis();
        }

        return bingos;
    }

    public void setCollege(CharSequence c) {
        college = c.toString();
        getSupportActionBar().setSubtitle(c);
    }
}
