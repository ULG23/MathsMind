package fr.ensisa.mathsmind;

import static android.os.VibrationEffect.DEFAULT_AMPLITUDE;
import static android.os.VibrationEffect.EFFECT_CLICK;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.effect.Effect;
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
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import fr.ensisa.mathsmind.question.Question;

public class QuestionDialog extends Dialog
{
    private Question question;
    private Context context;
    private EditText editText;
    private OnMyDialogResult result;
    public QuestionDialog(@NonNull Context context, Question question)
    {
        super(context);
        this.question = question;
        this.context = context;
    }


    Button btnOk;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.popup_question);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);

        this.setCanceledOnTouchOutside(false);


        TextView title = findViewById(R.id.tv_question_title);
        title.setText(question.getTitle());

        TextView tv_question = findViewById(R.id.katex_text);
        tv_question.setText(question.getQuestion());
        /*KatexView kv = this.findViewById(R.id.katex_text);
        String text = "$$ " + question.toString() + " $$";
        kv.setText(text);*/

        this.editText = findViewById(R.id.edt_quest);

        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        btnOk = findViewById(R.id.btn_answer);



        btnOk.setOnClickListener(v -> {

            if(!TextUtils.isEmpty(editText.getText().toString())) {
                editText.setEnabled(false);
                boolean answer = Integer.parseInt(this.editText.getText().toString()) == this.question.getAnswer();
                if(answer){
                    editText.setBackgroundResource(R.color.green);
                }
                else {
                    editText.setBackgroundResource(R.color.red);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                        final VibrationEffect vibrationEffect1;

                        vibrationEffect1 = VibrationEffect.createOneShot(200, DEFAULT_AMPLITUDE);

                        vibrator.cancel();
                        vibrator.vibrate(vibrationEffect1);

                    }

                }
                editText.setTextColor(Color.parseColor("#FFFFFF"));
                btnOk.setText("Quitter");

                btnOk.setOnClickListener(v2 ->{
                    result.finish(answer);
                    QuestionDialog.this.dismiss();
                });
            }

        });
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
