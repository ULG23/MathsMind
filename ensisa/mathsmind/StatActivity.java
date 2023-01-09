package fr.ensisa.mathsmind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StatActivity extends AppCompatActivity {
    TextView name, scoreEa, scoreMe, scoreHa, time;
    String StrName, StrMail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Myactivity";
    long bestScoreEa, bestScoreMe, bestScoreHa, bestTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        name = findViewById(R.id.name);
        scoreEa = findViewById(R.id.scoreEa);
        scoreMe = findViewById(R.id.scoreMe);
        scoreHa = findViewById(R.id.scoreHa);
        time = findViewById(R.id.time);

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener((view) -> {
            finish();
        });
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            name.setText(personName);
            StrMail = personEmail;

        }
        StrName = name.getText().toString();


        //récupération du meilleur temps / score
        DocumentReference docRef = db.collection("users").document(StrMail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        bestScoreEa = (long) document.get("ScoreEasy");
                        bestScoreMe = (long) document.get("ScoreMedium");
                        bestScoreHa = (long) document.get("ScoreHard");
                        bestTime = (long) document.get("Time");
                        scoreEa.setText("Meilleur Score Facile: " +String.valueOf(bestScoreEa));
                        scoreMe.setText("Meilleur Score Medium: " +String.valueOf(bestScoreMe));
                        scoreHa.setText("Meilleur Score Hard: " +String.valueOf(bestScoreHa));
                        time.setText("Meilleur Temps : " + String.valueOf(bestTime));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



    }
}