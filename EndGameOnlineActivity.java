package fr.ensisa.mathsmind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.ensisa.mathsmind.level.Level;
import fr.ensisa.mathsmind.level.LevelHolder;

public class EndGameOnlineActivity extends AppCompatActivity {
    private static final String TAG = "EndGameOnline";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    long ScoreActuEasy=0, ScoreActuMedium=0, ScoreActuHard=0;
    String StrName, StrMail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end_online);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            StrName = personName;
            StrMail = personEmail;
        }

        long maxJ1 = LevelHolder.getInstance().getLevel().getMaxScorePossible();
        long scoreJ1 = LevelHolder.getInstance().getLevel().getScore();
        double ratioJ1 = ((double)scoreJ1/maxJ1);
        String StrRatioJ1 = "("+Double.toString(Math.round(ratioJ1*10000.0)/100.0)+"%)";



        TextView score = findViewById(R.id.score_end_game_online);
        String scoreStr = String.valueOf(LevelHolder.getInstance().getLevel().getScore()) + "/" + String.valueOf(LevelHolder.getInstance().getLevel().getMaxScorePossible())+ StrRatioJ1;
        score.setText(scoreStr);



        TextView scoreOpp = findViewById(R.id.score_end_game_opponent_online);
        int scoreOppo = (int)getIntent().getLongExtra("oppNumLevel", 0);
        long maxJ2 = Level.getMaxScoreForNumLevel(scoreOppo, getAssets());
        long scoreJ2 = getIntent().getLongExtra("oppscore", 0);
        double ratioJ2 = ((double)scoreJ2/maxJ2);
        String StrRatioJ2 = "("+Double.toString(Math.round(ratioJ2*10000.0)/100.0)+"%)";

        scoreStr = getIntent().getLongExtra("oppscore", 0) + "/" + Level.getMaxScoreForNumLevel(scoreOppo, getAssets()) + StrRatioJ2;
        scoreOpp.setText(scoreStr);


        ImageView imgView =(ImageView) findViewById(R.id.winnerImage);


        if (ratioJ1 > ratioJ2 && getIntent().getBooleanExtra("id",true)){
            imgView.setImageResource(R.drawable.gagne);

        }
        else {
            imgView.setImageResource(R.drawable.perdu);
        }


    }

    private void refreshScoreOnline()
    {
        if(LevelHolder.getInstance().getLevel().getNumLevel()==1)
            ScoreActuEasy = LevelHolder.getInstance().getLevel().getScore();
        else if(LevelHolder.getInstance().getLevel().getNumLevel() == 2)
            ScoreActuMedium = LevelHolder.getInstance().getLevel().getScore();
        else if(LevelHolder.getInstance().getLevel().getNumLevel() == 3)
            ScoreActuHard = LevelHolder.getInstance().getLevel().getScore();


        DocumentReference doc = db.collection("users").document(StrMail);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        long ScoreEasy = document.getLong("ScoreEasy");
                        if (ScoreEasy < ScoreActuEasy){
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
        refreshScoreOnline();
        super.finish();
    }
}