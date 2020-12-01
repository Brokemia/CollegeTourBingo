package com.example.collegetourbingo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewCollegeActivity extends AppCompatActivity {
    private String name;
    // Use a Set instead of a List to leave out duplicates
    private Set<String> termsMarked = new HashSet<>();
    private LinearLayout layout;
    private TextView collegeName;
    private TextView avgBingoCount;
    private TextView avgFirstBingotime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_college);

        name = getIntent().getStringExtra("name");
        layout = findViewById(R.id.view_college_layout);
        collegeName = findViewById(R.id.college_name);
        avgBingoCount = findViewById(R.id.avg_bingos);
        avgFirstBingotime = findViewById(R.id.avg_first_bingo_time);

        float avgBingos = 0;
        long avgTimeToBingo = 0;
        List<CollegeData> rounds = MainActivity.database.collegeDataDao().getFromCollege(name);
        // Average the bingo counts and time to first bingo
        for(CollegeData data : rounds) {
            avgBingos += data.bingos;
            // Don't count games where a bingo was never gotten
            if(data.firstBingoTime > 0)
                avgTimeToBingo += data.firstBingoTime;

            List<String> terms = Arrays.asList(data.termsMarked.split("\t"));
            termsMarked.addAll(terms);
        }
        avgBingos /= rounds.size();
        avgTimeToBingo /= rounds.size();

        collegeName.setText(name);
        avgBingoCount.setText(getString(R.string.avg_bingo_count, avgBingos));
        avgFirstBingotime.setText(getString(R.string.avg_first_bingo_time, avgTimeToBingo / 60000f));
        // Display all the marked terms after the heading for it
        for(String term : termsMarked) {
            TextView termView = new TextView(this);
            termView.setText(term);
            termView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            termView.setPadding(termView.getPaddingLeft(), 3, termView.getPaddingRight(), termView.getPaddingBottom());
            layout.addView(termView);
        }
    }

}
