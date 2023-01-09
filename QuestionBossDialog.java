package fr.ensisa.mathsmind;

import static android.os.VibrationEffect.DEFAULT_AMPLITUDE;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import fr.ensisa.mathsmind.question.QuestionProblem;

public class QuestionBossDialog extends Dialog
{
    private QuestionProblem question;
    private Context context;
    private OnMyDialogResult result;
    TextView prop1;
    TextView prop2;
    TextView prop3;
    TextView prop4;
    public QuestionBossDialog(@NonNull Context context, QuestionProblem question)
    {
        super(context);
        this.question = question;
        this.context = context;
    }


    Button btnOk;



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.popup_boss_question);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);

        this.setCanceledOnTouchOutside(false);

        prop1 = findViewById(R.id.proposition_1);
        prop2 = findViewById(R.id.proposition_2);
        prop3 = findViewById(R.id.proposition_3);
        prop4 = findViewById(R.id.proposition_4);
        prop1.setText(question.getPropositions()[0]+"");
        prop2.setText(question.getPropositions()[1]+"");
        prop3.setText(question.getPropositions()[2]+"");
        prop4.setText(question.getPropositions()[3]+"");

        prop1.setOnClickListener(v -> {
            verifyResult(prop1);
        });
        prop2.setOnClickListener(v -> {
            verifyResult(prop2);
        });
        prop3.setOnClickListener(v -> {
            verifyResult(prop3);
        });
        prop4.setOnClickListener(v -> {
            verifyResult(prop4);
        });

        TextView title = findViewById(R.id.tv_question_title_boss);
        title.setText(question.getTitle());

        TextView tv_question = findViewById(R.id.katex_text_boss);
        tv_question.setText(question.getQuestion());

    }

    private void verifyResult(TextView textView)
    {
        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        int result = Integer.parseInt(textView.getText().toString());
        boolean answer = result == this.question.getAnswer();
        showAnswer();
        if(!answer){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                final VibrationEffect vibrationEffect1;
                vibrationEffect1 = VibrationEffect.createOneShot(200, DEFAULT_AMPLITUDE);
                vibrator.cancel();
                vibrator.vibrate(vibrationEffect1);
            }
        }
        Button quit = findViewById(R.id.btn_quit);
        quit.setVisibility(View.VISIBLE);
        quit.setOnClickListener(v -> {
            this.result.finish(answer);
            QuestionBossDialog.this.dismiss();
        });
    }

    private void showAnswer()
    {
        TextView[] tabTV = new TextView[]{prop1, prop2, prop3, prop4};
        for (TextView tv : tabTV)
        {
            int result = Integer.parseInt(tv.getText().toString());
            boolean answer = result == this.question.getAnswer();
            if (answer)
                tv.setBackgroundResource(R.color.green);
            else
                tv.setBackgroundResource(R.color.red);
            tv.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    public void setDialogResult(OnMyDialogResult dialogResult)
    {
        result = dialogResult;
    }

    public interface OnMyDialogResult
    {
        void finish(boolean result);
    }

    @Override
    public void onBackPressed() {}
}
