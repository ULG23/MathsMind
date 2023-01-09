package fr.ensisa.mathsmind;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import fr.ensisa.mathsmind.level.LevelHolder;

public class EndGameActivity extends AppCompatActivity {
    private static final String TAG = "EndGame";
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String StrName, StrMail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    long ScoreActuEasy=0, ScoreActuMedium=0, ScoreActuHard=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            StrName = personName;
            StrMail = personEmail;
        }

        TextView score = findViewById(R.id.text_score);
        String scoreStr = score.getText().toString() + " " + String.valueOf(LevelHolder.getInstance().getLevel().getScore()) + "/" + String.valueOf(LevelHolder.getInstance().getLevel().getMaxScorePossible());
        score.setText(scoreStr);


        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener((view) -> {
            finish();
        });

        View view = findViewById(R.id.end_game_view);
        ImageView character = findViewById(R.id.character);
        ViewTreeObserver observer = view.getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                character.animate().y((float)0.47*view.getHeight()).setDuration(2000).withEndAction(() -> {});


            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    private void refreshScoreOnline()
    {
        if(LevelHolder.getInstance().getLevel().getNumLevel()==1)
            ScoreActuEasy = LevelHolder.getInstance().getLevel().getScore();
        else if(LevelHolder.getInstance().getLevel().getNumLevel() == 2)
            ScoreActuMedium = LevelHolder.getInstance().getLevel().getScore();
        else if(LevelHolder.getInstance().getLevel().getNumLevel() == 3)
            ScoreActuHard = LevelHolder.getInstance().getLevel().getScore();


        DocumentReference docRef = db.collection("users").document(StrMail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        long ScoreEasy = document.getLong("ScoreEasy");
                        if (ScoreEasy < ScoreActuEasy){
                            Log.e(TAG, "innnnnnnnnnnnnn");
                            DocumentReference update= db.collection("users").document(StrMail);
                            update.update("ScoreEasy", ScoreActuEasy);
                        }
                        long ScoreMedium = document.getLong("ScoreMedium");
                        if (ScoreEasy < ScoreActuMedium){
                            DocumentReference update= db.collection("users").document(StrMail);
                            update.update("ScoreMedium", ScoreActuMedium);
                        }
                        long ScoreHard = document.getLong("ScoreHard");
                        if (ScoreEasy < ScoreActuHard){
                            DocumentReference update= db.collection("users").document(StrMail);
                            update.update("ScoreHard", ScoreActuHard);
                        }
                    }
                }
            }
        });
    }


    @Override
    public void finish() {
        SharedPreferences.Editor editor = this.getSharedPreferences(getString(R.string.file_level), Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        refreshScoreOnline();
        super.finish();
    }

}