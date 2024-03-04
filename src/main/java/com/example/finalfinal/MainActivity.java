package com.example.finalfinal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button[] buttons = new Button[6];
    private Button startButton;
    private VideoView videoView;
    private Random random = new Random();
    private int correctChoice;
    private int numWins = 0;
    private int numAttempts = 0;
    private boolean buttonSelected = false;
    private boolean cutscenePlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttons[0] = findViewById(R.id.btn1);
        buttons[1] = findViewById(R.id.btn2);
        buttons[2] = findViewById(R.id.btn3);
        buttons[3] = findViewById(R.id.btn4);
        buttons[4] = findViewById(R.id.btn5);
        buttons[5] = findViewById(R.id.btn6);

        startButton = findViewById(R.id.startButton);
        videoView = findViewById(R.id.videoView);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cutscenePlaying) {
                    Toast.makeText(MainActivity.this, "Please wait for the cutscene to finish.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!buttonSelected) {
                    Toast.makeText(MainActivity.this, "Please select a button first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                initializeGame(); // Reset game state
                numAttempts = 0;
                playCutscene();
            }
        });

        for (int i = 0; i < 6; i++) {
            final int choice = i + 1;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!buttonSelected) {
                        buttonSelected = true;
                        checkGuess(choice);
                    } else {
                        Toast.makeText(MainActivity.this, "You can only select one button per chance.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initializeGame() {
        // Choose a random correct choice
        correctChoice = random.nextInt(6) + 1; // Random number between 1 and 6
        buttonSelected = false;
    }

    private void playCutscene() {
        int resourceId;
        if (numAttempts == 3) {
            // If 3 attempts have been made, play the result cutscene
            if (numWins == 1) {
                resourceId = R.raw.won_once;
            } else if (numWins == 2) {
                resourceId = R.raw.won_twice;
            } else if (numWins == 3) {
                resourceId = R.raw.perfect;
            } else {
                resourceId = R.raw.you_lose;
            }
            numAttempts = 0; // Reset the number of attempts for the next game
            playResultCutscene(resourceId);
            numWins = 0; // Reset the number of wins for the next game
        } else {
            // Otherwise, play the regular cutscene
            resourceId = R.raw.cutscene;
            cutscenePlaying = true;
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resourceId));
            videoView.setVisibility(View.VISIBLE);
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    cutscenePlaying = false;
                    videoView.setVisibility(View.GONE);
                    if (numWins < 3) {
                        // Play the main video again if the game is not over yet
                        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cutscene));
                        videoView.start();
                    }
                    enableButtons(true); // Enable buttons after the cutscene finishes
                }
            });
            videoView.start();
            buttonSelected = false; // Reset button selection for the new game
        }
    }

    private void checkGuess(int choice) {
        if (choice == correctChoice) {
            numWins++; // Increment numWins if the correct button is selected
        }
        numAttempts++; // Increment numAttempts after each selection

        // Check if 3 rounds (3 attempts) have been completed
        if (numAttempts == 3) {
            playCutscene(); // Trigger the cutscene after every 3 rounds
        }
    }

    private void endGame() {
        numAttempts = 0; // Reset the number of attempts for the next game
        int resourceId;
        if (numWins == 1) {
            resourceId = R.raw.won_once;
        } else if (numWins == 2) {
            resourceId = R.raw.won_twice;
        } else if (numWins == 3) {
            resourceId = R.raw.perfect;
        } else {
            resourceId = R.raw.you_lose;
        }
        playResultCutscene(resourceId);
        numWins = 0; // Reset the number of wins for the next game
        // Close the activity after playing the result cutscene
        closeActivity();
    }

    private void playResultCutscene(int resourceId) {
        cutscenePlaying = true;
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resourceId));
        videoView.setVisibility(View.VISIBLE);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                cutscenePlaying = false;
                videoView.setVisibility(View.GONE);
                initializeGame(); // Reset the game state after playing the result cutscene
                enableButtons(true); // Enable buttons after the cutscene finishes
            }
        });
        videoView.start();
    }

    private void enableButtons(boolean enable) {
        for (Button button : buttons) {
            button.setEnabled(enable);
        }
    }

    private void closeActivity() {
        // Close the activity
        finish();
    }
}
