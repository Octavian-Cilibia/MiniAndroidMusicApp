package com.example.proiectpdm;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText tabEditText;
    private Button playButton;
    private SoundPool soundPool;
    private Map<Character, Integer> soundMap;
    private ArrayList<Nota> notes;
    private RecyclerView recyclerView;
    private String draggedNote;


    Button drag_E;
    Button drag_A;
    Button drag_D;
    Button drag_G;
    Button drag_B;
    Button drag_E2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set the custom text
        getSupportActionBar().setTitle("ChordRecord");

// Set the custom color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF0000")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // -------Set the flags for immersive mode--------
        View decorView = getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(flags);

        // Set a touch listener to control the visibility of the bars
        decorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Hide the system bars again
                    v.setSystemUiVisibility(flags);
                }
                return true;
            }
        });

        recyclerView = findViewById(R.id.tabulatura);
        notes = new ArrayList<Nota>();
        setNotesInfo();
        setAdapter();

        playButton = findViewById(R.id.playButton);

        // Initialize the SoundPool
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(attributes)
                .build();

        // Load sound samples into the SoundPool
        soundMap = new HashMap<>();
        soundMap.put('E', soundPool.load(this, R.raw.e_note, 1));
        soundMap.put('A', soundPool.load(this, R.raw.a_note, 1));
        soundMap.put('D', soundPool.load(this, R.raw.d_note, 1));
        soundMap.put('G', soundPool.load(this, R.raw.g_note, 1));
        soundMap.put('B', soundPool.load(this, R.raw.b_note, 1));
        soundMap.put('e', soundPool.load(this, R.raw.e2_note, 1));
        // Add more sound samples for other notes

        //Declaration of view variables
        drag_E = findViewById(R.id.drag_E);
        drag_A = findViewById(R.id.drag_A);
        drag_D = findViewById(R.id.drag_D);
        drag_G = findViewById(R.id.drag_G);
        drag_B = findViewById(R.id.drag_B);
        drag_E2 = findViewById(R.id.drag_E2);

        View.OnLongClickListener dragListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String note = (String) v.getTag(); // Get the note associated with the view
                draggedNote = note; // Set the dragged note

                // Create a ClipData object with the note as the data
                ClipData clipData = ClipData.newPlainText("note", note);

                // Create a DragShadowBuilder for the view
                View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(v);

                // Start the drag operation
                v.startDrag(clipData, dragShadowBuilder, null, 0);

                return true;
            }
        };

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                            String note = (String) v.getTag(); // Get the note associated with the button
                            playSound(note); // Play the associated sound
            }
        };

        drag_E.setOnClickListener(clickListener);
        drag_A.setOnClickListener(clickListener);
        drag_D.setOnClickListener(clickListener);
        drag_G.setOnClickListener(clickListener);
        drag_B.setOnClickListener(clickListener);
        drag_E2.setOnClickListener(clickListener);

        drag_E.setOnLongClickListener(dragListener);
        drag_E.setTag("E");
        drag_A.setOnLongClickListener(dragListener);
        drag_A.setTag("A");
        drag_D.setOnLongClickListener(dragListener);
        drag_D.setTag("D");
        drag_G.setOnLongClickListener(dragListener);
        drag_G.setTag("G");
        drag_B.setOnLongClickListener(dragListener);
        drag_B.setTag("B");
        drag_E2.setOnLongClickListener(dragListener);
        drag_E2.setTag("e");

        RecyclerView.OnDragListener recyclerListener = new RecyclerView.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // Check if the drag event contains the desired data
                        if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            return true;
                        }
                        return false;
                    case DragEvent.ACTION_DROP:
                        // Get the dragged note from the event
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        final String note = item.getText().toString();

                        // Create an EditText for the user to enter the delay
                        final EditText delayEditText = new EditText(MainActivity.this);
                        delayEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                        // Create an AlertDialog to prompt the user for the delay
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Enter Delay");
                        builder.setMessage("Enter the delay for the note (in milliseconds):");
                        builder.setView(delayEditText);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String delayStr = delayEditText.getText().toString();
                                if (!delayStr.isEmpty()) {
                                    int delay = Integer.parseInt(delayStr);

                                    // Add the note to the ArrayList<Nota> with the entered delay
                                    notes.add(new Nota(note, delay));
                                    recyclerView.getAdapter().notifyItemInserted(notes.size() - 1);
                                } else {
                                    // Field is empty, show a message or handle the case accordingly
                                    Toast.makeText(MainActivity.this, "Delay field is empty", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        builder.setNegativeButton("Cancel", null);
                        builder.show();
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        return true;
                }
                return false;
            }
        };

        recyclerView.setOnDragListener(recyclerListener);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playTab();
            }
        });

    }

    //------------------------------- METHODS ___________________________________________
    private void setAdapter() {
        TabulaturaAdapter adapter = new TabulaturaAdapter(notes, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setNotesInfo() {
        notes.add(new Nota("E"));
        notes.add(new Nota("A"));
        notes.add(new Nota("D"));
        notes.add(new Nota("G"));
        notes.add(new Nota("B"));
        notes.add(new Nota("E2"));
    }

    private void playTab() {
        for (Nota nota : notes) {
            String noteValue = nota.getName();
            playSound(noteValue);

            try {
                Thread.sleep(nota.getDelay());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void playSound(String note) {
        if (soundMap.containsKey(note.charAt(0))) {
            int soundId = soundMap.get(note.charAt(0));
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(position);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void deleteItem(int position) {
        notes.remove(position);
        recyclerView.getAdapter().notifyItemRemoved(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
        soundMap.clear();
        soundMap = null;
    }
}
