package fr.ensisa.mathsmind;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.type.DateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fr.ensisa.mathsmind.level.Level;
import fr.ensisa.mathsmind.level.LevelHolder;
import fr.ensisa.mathsmind.question.DifficultyType;
import fr.ensisa.mathsmind.question.Question;
import fr.ensisa.mathsmind.question.QuestionProblem;

public class GameOnlineActivity extends AppCompatActivity
{
    protected String TAG = "GameOnline";
    private static final String FILE_PATH = "cordonnee.json";
    protected ImageView character;
    protected ConstraintLayout view;
    private Level level;
    private Timestamp endDate;
    private JSONArray xleft;
    private JSONArray yleft;
    private JSONArray xright;
    private JSONArray yright;
    private JSONArray xcenter;
    private JSONArray ycenter;
    private int mapvar;
    FirebaseFirestore db;

    public void Coordonnee(AssetManager manager)
    {
        int mapVar;
        if(this.level.isPreBossQuestion())
        {
            this.view.setBackgroundResource(R.drawable.cartepredonjon);
            mapVar = 5;
        }
        else if(this.level.isBossQuestion())
        {
            this.view.setBackgroundResource(R.drawable.cartedonjon);
            mapVar = 6;
        }
        else
        {
            int[] id={700025,700026,700028,700031,700022};
            mapVar=(int) (Math.random()*4)+1;
            int mapPrec= getIntent().getIntExtra("mapPre",0);
            while(mapVar==mapPrec){ mapVar=(int) (Math.random()*5)+1;}

            switch(id[mapVar]){
                case 700025:
                    this.view.setBackgroundResource(R.drawable.carte1);
                    break;
                case 700026:
                    this.view.setBackgroundResource(R.drawable.carte2);
                    break;
                case 700028:
                    this.view.setBackgroundResource(R.drawable.carte3);
                    break;
                case 700031:
                    this.view.setBackgroundResource(R.drawable.carte4);
                    break;
                case 700022:
                    this.view.setBackgroundResource(R.drawable.carte5);
                    break;
                default:
                    this.view.setBackgroundResource(R.drawable.carte1);
            }
        }

        String jsonString;
        JSONObject json;
        try
        {
            InputStream is = manager.open(FILE_PATH);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, StandardCharsets.UTF_8);
            json = new JSONObject(jsonString);
            Log.e("json", json.toString());

            JSONArray array=(JSONArray)json.get("map");
            JSONObject mapSelect= (JSONObject) array.get(mapVar);


            JSONObject arraysleft=(JSONObject) mapSelect.get("left");
            JSONArray xleft=(JSONArray)arraysleft.get("x");
            this.xleft=xleft;
            JSONArray yleft=(JSONArray)arraysleft.get("y");
            this.yleft=yleft;

            JSONObject arrayscenter=(JSONObject)mapSelect.get("center");
            JSONArray xcenter=(JSONArray)arrayscenter.get("x");
            this.xcenter=xcenter;
            JSONArray ycenter=(JSONArray)arrayscenter.get("y");
            this.ycenter=ycenter;

            JSONObject arraysright=(JSONObject) mapSelect.get("right");
            JSONArray xright=(JSONArray)arraysright.get("x");
            this.xright=xright;
            JSONArray yright=(JSONArray)arraysright.get("y");
            this.yright=yright;
            this.mapvar=mapVar;
        }
        catch (IOException | JSONException ex)
        {
            Log.e("erreur", ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        db = FirebaseFirestore.getInstance();
        this.level = LevelHolder.getInstance().getLevel();


        Timestamp ts = Timestamp.now();
        endDate = getIntent().getParcelableExtra("endDate");


        if(this.level.getNumActualQuestion() > this.level.getTotalQuestion() || endDate.compareTo(ts) < 0)
        {
            Intent nextActivity = new Intent(this, EndGameOnlineActivity.class);
            nextActivity.putExtra("IdJ1", getIntent().getStringExtra("IdJ1"));
            nextActivity.putExtra("mapPre",mapvar);
            nextActivity.putExtra("j1", getIntent().getBooleanExtra("j1", true));
            nextActivity.putExtra("endDate", endDate);
            nextActivity.putExtra("id", getIntent().getStringExtra("id"));
            nextActivity.putExtra("oppscore", getIntent().getLongExtra("oppscore", 0));
            nextActivity.putExtra("oppNumLevel", getIntent().getLongExtra("oppNumLevel", 0));
            startActivity(nextActivity);
            finish();
        }

        TextView numQuestion = findViewById(R.id.num_question);
        String numStr = level.getNumActualQuestion() + "/" + level.getTotalQuestion();
        numQuestion.setText(numStr);

        character = findViewById(R.id.character);
        view = findViewById(R.id.game_view);

        ImageButton btnUp = findViewById(R.id.btn_move_up);
        ImageButton btnLeft = findViewById(R.id.btn_move_left);
        ImageButton btnRight = findViewById(R.id.btn_move_right);
        ImageButton[] arrayBtn = new ImageButton[]{btnUp, btnRight, btnLeft};

        if(level.isPreBossQuestion() || level.isBossQuestion())
        {
            btnRight.setVisibility(View.GONE);
            btnLeft.setVisibility(View.GONE);
        }
        Coordonnee(getAssets());
        btnUp.setOnClickListener(v -> {
            if (mapvar==4){
                try {
                    character.animate().y((float)((double)ycenter.get(0)*view.getHeight())).setDuration(1000).withEndAction(()-> {
                        character.setImageResource(R.drawable.character_left);
                        try {
                            character.animate().x((float)((double)xcenter.get(0)*view.getWidth())).setDuration(700).withEndAction(()-> {
                                character.setImageResource(R.drawable.character_back);
                                try {
                                    character.animate().y((float)((double)ycenter.get(1)*view.getHeight())).setDuration(700).withEndAction(() -> createQuestion(DifficultyType.NORMAL));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }}
            else{
                try {
                    character.animate().y((float)((double)ycenter.get(0)*view.getHeight())).setDuration(700).withEndAction(() -> createQuestion(DifficultyType.NORMAL));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            this.disableButtons(arrayBtn);
        });

        btnLeft.setOnClickListener(v ->{
            try {
                character.animate().y((float) ((double)yleft.get(0)*view.getHeight())).setDuration(700).withEndAction(() -> {
                    character.setImageResource(R.drawable.character_left);
                    try {
                        character.animate().x((float) ((double)xleft.get(0)*view.getWidth())).setDuration(700).withEndAction(()-> {
                            character.setImageResource(R.drawable.character_back);
                            try {
                                character.animate().y((float) ((double)yleft.get(1)*view.getHeight())).setDuration(700).withEndAction(() -> {
                                    character.setImageResource(R.drawable.character_left);
                                    try {
                                        character.animate().x((float) ((double)xleft.get(1)*view.getWidth())).setDuration(700).withEndAction(() -> createQuestion(DifficultyType.EASY));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();

            }
            this.disableButtons(arrayBtn);
        });

        btnRight.setOnClickListener(v ->{
            try {
                character.animate().y((float) ((double)yright.get(0)*view.getHeight())).setDuration(700).withEndAction(() -> {
                    character.setImageResource(R.drawable.character_right);
                    try {
                        character.animate().x((float) ((double)xright.get(0)*view.getWidth())).setDuration(700).withEndAction(()-> {
                            character.setImageResource(R.drawable.character_back);
                            try {
                                character.animate().y((float)((double)yright.get(1)*view.getHeight())).setDuration(700).withEndAction(() -> {
                                    character.setImageResource(R.drawable.character_right);
                                    try {
                                        character.animate().x((float) ((double)xright.get(0)*view.getWidth())).setDuration(700).withEndAction(() -> createQuestion(DifficultyType.HARD));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.disableButtons(arrayBtn);
        });
    }

    private void createQuestion(DifficultyType difficultyType)
    {
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Question question = level.createQuestion(difficultyType);

        if(level.isBossQuestion() || level.isPreBossQuestion())
        {
            QuestionBossDialog dialog = new QuestionBossDialog(view.getContext(), (QuestionProblem) question);
            dialog.setDialogResult(result -> {
                if(result)
                    LevelHolder.getInstance().getLevel().incrementScore(difficultyType);

                LevelHolder.getInstance().getLevel().incrementNumActualQuestion();
                Intent nextActivity;

                //S'il reste des questions
                if(LevelHolder.getInstance().getLevel().getNumActualQuestion() <= LevelHolder.getInstance().getLevel().getTotalQuestion())
                    nextActivity = new Intent(this, GameOnlineActivity.class);
                else
                    nextActivity = new Intent(this, EndGameOnlineActivity.class);

                nextActivity.putExtra("j1", getIntent().getBooleanExtra("j1", true));
                nextActivity.putExtra("id", getIntent().getStringExtra("id"));
                nextActivity.putExtra("oppscore", getIntent().getLongExtra("oppscore", 0));
                nextActivity.putExtra("oppNumLevel", getIntent().getLongExtra("oppNumLevel", 0));
                nextActivity.putExtra("endDate", endDate);
                nextActivity.putExtra("IdJ1", getIntent().getStringExtra("IdJ1"));

                if(getIntent().getBooleanExtra("j1", true))
                    updateScore("1");
                else
                    updateScore("2");
                startActivity(nextActivity);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            });
            dialog.show();
        }
        else {
            QuestionDialog dialog = new QuestionDialog(view.getContext(), question);
            dialog.setDialogResult(result -> {
                if (result)
                    LevelHolder.getInstance().getLevel().incrementScore(difficultyType);

                LevelHolder.getInstance().getLevel().incrementNumActualQuestion();
                Intent nextActivity;

                //S'il reste des questions
                if (LevelHolder.getInstance().getLevel().getNumActualQuestion() <= LevelHolder.getInstance().getLevel().getTotalQuestion())
                    nextActivity = new Intent(this, GameOnlineActivity.class);
                else
                    nextActivity = new Intent(this, EndGameOnlineActivity.class);

                nextActivity.putExtra("j1", getIntent().getBooleanExtra("j1", true));
                nextActivity.putExtra("id", getIntent().getStringExtra("id"));
                nextActivity.putExtra("oppscore", getIntent().getLongExtra("oppscore", 0));
                nextActivity.putExtra("oppNumLevel", getIntent().getLongExtra("oppNumLevel", 0));
                nextActivity.putExtra("endDate", endDate);
                nextActivity.putExtra("IdJ1", getIntent().getStringExtra("IdJ1"));

                if (getIntent().getBooleanExtra("j1", true))
                    updateScore("1");
                else
                    updateScore("2");

                startActivity(nextActivity);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            });
            dialog.show();
        }
    }

    private void updateScore(String numPlayer)
    {
        // update the new best Score of the player logged
        Map<String, Object> update= new HashMap<>();
        update.put("ScoreJ"+numPlayer,level.getScore());
        update.put("NumActualQuestionJ"+numPlayer, level.getNumActualQuestion());
        DocumentReference document= db.collection("Game").document(getIntent().getStringExtra("id"));
        document
                .update(update)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }


    private void disableButtons(ImageButton[] arrayBtn)
    {
        for(ImageButton btn : arrayBtn)
        {
            btn.setEnabled(false);
        }
    }

    @Override
    public void finish() {
        super.finish();
    }
}