package fr.ensisa.mathsmind;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import fr.ensisa.mathsmind.level.Level;
import fr.ensisa.mathsmind.level.LevelHolder;
import fr.ensisa.mathsmind.question.DifficultyType;
import fr.ensisa.mathsmind.question.Question;
import fr.ensisa.mathsmind.question.QuestionProblem;
import io.grpc.internal.JsonParser;


public class GameActivity extends AppCompatActivity
{
    protected ImageView character;
    protected ConstraintLayout view;
    private Level level;
    private static final String FILE_PATH = "cordonnee.json";
    private JSONArray xleft;
    private JSONArray yleft;
    private JSONArray xright;
    private JSONArray yright;
    private JSONArray xcenter;
    private JSONArray ycenter;
    private int mapvar;

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
        this.level = LevelHolder.getInstance().getLevel();


        if(this.level.getNumActualQuestion() > this.level.getTotalQuestion())
        {
            Intent nextActivity = new Intent(this, EndGameActivity.class);
            nextActivity.putExtra("mapPre",mapvar);
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
        if(level.isPreBossQuestion() || level.isBossQuestion())
        {
            btnRight.setVisibility(View.GONE);
            btnLeft.setVisibility(View.GONE);
        }
        ImageButton[] arrayBtn = new ImageButton[]{btnUp, btnRight, btnLeft};
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

    //On doit changer int type par une enum
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
                    nextActivity = new Intent(this, GameActivity.class);
                else
                    nextActivity = new Intent(this, EndGameActivity.class);

                SharedPreferences.Editor editor = this.getSharedPreferences(getString(R.string.file_level), Context.MODE_PRIVATE).edit();
                editor.putInt(getString(R.string.num_level), LevelHolder.getInstance().getLevel().getNumLevel());
                editor.putInt(getString(R.string.num_act_quest), LevelHolder.getInstance().getLevel().getNumActualQuestion());
                editor.putInt(getString(R.string.score), LevelHolder.getInstance().getLevel().getScore());
                editor.commit();
                startActivity(nextActivity);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            });
            dialog.show();
        }
        else
        {
            QuestionDialog dialog = new QuestionDialog(view.getContext(), question);
            dialog.setDialogResult(result -> {
                if(result)
                    LevelHolder.getInstance().getLevel().incrementScore(difficultyType);

                LevelHolder.getInstance().getLevel().incrementNumActualQuestion();
                Intent nextActivity;

                //S'il reste des questions
                if(LevelHolder.getInstance().getLevel().getNumActualQuestion() <= LevelHolder.getInstance().getLevel().getTotalQuestion())
                    nextActivity = new Intent(this, GameActivity.class);
                else
                    nextActivity = new Intent(this, EndGameActivity.class);

                SharedPreferences.Editor editor = this.getSharedPreferences(getString(R.string.file_level), Context.MODE_PRIVATE).edit();
                editor.putInt(getString(R.string.num_level), LevelHolder.getInstance().getLevel().getNumLevel());
                editor.putInt(getString(R.string.num_act_quest), LevelHolder.getInstance().getLevel().getNumActualQuestion());
                editor.putInt(getString(R.string.score), LevelHolder.getInstance().getLevel().getScore());
                editor.commit();
                startActivity(nextActivity);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            });
            dialog.show();
        }
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