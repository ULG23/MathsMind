package fr.ensisa.mathsmind;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.ensisa.mathsmind.level.Level;
import fr.ensisa.mathsmind.level.LevelHolder;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Myactivity";
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String StrName, StrMail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();




    public ArrayList<String> QEasy = new ArrayList<String>();
    public ArrayList<String> QMedium = new ArrayList<String>();
    public ArrayList<String> QHard = new ArrayList<String>();

    long bestScoreEa, bestScoreMe, bestScoreHa, bestTime;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

        setContentView(R.layout.activity_main);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            StrName = personName;
            StrMail = personEmail;
        }

        ImageButton newGame = findViewById(R.id.nouvellepartie);
        newGame.setOnClickListener((view) -> {
            launchNewGame();
        });

        ImageButton loadGame = findViewById(R.id.chargerpartie);
        loadGame.setOnClickListener((view) -> {
            SharedPreferences pref = this.getSharedPreferences(getString(R.string.file_level), Context.MODE_PRIVATE);
            if (pref.contains(getString(R.string.num_level))) {
                int numLevel = pref.getInt(getString(R.string.num_level), 1);
                int numActQuest = pref.getInt(getString(R.string.num_act_quest), 1);
                int score = pref.getInt(getString(R.string.score), 1);

                LevelHolder.getInstance().setLevel(Level.recreateLevel(numLevel, getAssets(), score, numActQuest));
                Intent myIntent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(myIntent);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.error).setMessage(R.string.no_level_save).setCancelable(true);
                builder.create().show();
            }
        });

        ImageButton opt = findViewById(R.id.options);
        opt.setOnClickListener((view) -> {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        });

        ImageButton quitter = findViewById(R.id.quitter);
        quitter.setOnClickListener((view) -> {
            System.exit(0);
        });

        ImageButton stats = findViewById(R.id.statistiques);
        stats.setOnClickListener((view) -> {
            Intent intent = new Intent(getApplicationContext(), StatActivity.class);
            startActivity(intent);
        });

        ImageButton SignOut = findViewById(R.id.signout);
        SignOut.setOnClickListener((view) -> {
            signOut();
        });

        ImageButton online = findViewById(R.id.online);
        online.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), OnlineActivity.class);
            startActivity(intent);
        });

        //récupération du meilleur temps / score
        DocumentReference docRef = db.collection("users").document(StrMail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        bestScoreEa = (long) document.get("ScoreEasy");
                        bestScoreMe = (long) document.get("ScoreMedium");
                        bestScoreHa = (long) document.get("ScoreHard");
                        bestTime = document.getLong("Time");
                    } else {
                        Log.e("main", "zero");
                        Log.d(TAG, "No such document");
                        bestScoreEa = 0;
                        bestScoreMe = 0;
                        bestScoreHa = 0;
                        bestTime = 0;
                    }
                    saveScore();
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        //récupération des questions dans la BDD

        db.collection("Beginner")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getId());
                                QEasy.add(document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("Intermediary")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getId());
                                QMedium.add(document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        db.collection("Advanced")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getId());
                                QHard.add(document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

   private void saveScore ()
        {
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("name", StrName);
            user.put("mail", StrMail);
            user.put("Time", bestTime);
            user.put("ScoreEasy", bestScoreEa);
            user.put("ScoreMedium", bestScoreMe);
            user.put("ScoreHard", bestScoreHa);

            // Add a new document with a generated ID
            db.collection("users").document(StrMail)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
        }

        private void launchNewGame ()
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.ask_level_title)
                    .setItems(R.array.ask_level, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            LevelHolder.getInstance().setLevel(new Level(which + 1, getAssets()));
                            Intent myIntent = new Intent(getApplicationContext(), GameActivity.class);
                            startActivity(myIntent);
                        }
                    });
            builder.create().show();

        }

        void signOut () {
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    finish();
                    startActivity(new Intent(MainActivity.this, ConnectActivity.class));
                }
            });

        }
}
