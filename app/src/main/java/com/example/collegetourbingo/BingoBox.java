package com.example.collegetourbingo;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import static android.content.Context.VIBRATOR_SERVICE;

public class BingoBox extends AppCompatTextView {
    private boolean marked = false;

    public BingoBox(Context context) {
        super(context);
        setOnClickListener(new BingoBoxClickListener());
        setBackgroundColor(Color.LTGRAY);
    }

    private class BingoBoxClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            // Vibrate slightly when marking a space
            if(!marked) {
                // Methods of vibrating changed between versions
                // Before SDK 26 it worked differently
                // https://stackoverflow.com/a/45605249
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);
                    if(vibrator.hasVibrator())
                        vibrator.vibrate(VibrationEffect.createOneShot(40, 5));
                }
            }
            setMarked(!marked);
            MainActivity.getInstance().updateBingos();
        }
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
        if(marked) {
            setBackgroundColor(Color.BLUE);
            setTextColor(Color.WHITE);
        } else {
            setBackgroundColor(Color.LTGRAY);
            setTextColor(getResources().getColor(R.color.colorPrimary, getContext().getTheme()));
        }
    }

    public void setPartOfBingo(boolean bingo) {
        if(bingo) {
            setBackgroundColor(Color.RED);
            setTextColor(Color.WHITE);
        } else {
            setMarked(marked);
        }
    }
}
