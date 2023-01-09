package fr.ensisa.mathsmind;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

import fr.ensisa.mathsmind.level.Level;
import fr.ensisa.mathsmind.level.LevelHolder;

public class OnGoingGameFragment extends Fragment {

    private static final String TAG = "OnlineFragment";
    LinearLayout layout;
    boolean colorSwitch = true;
    FirebaseFirestore db;
    String StrMail;

    public OnGoingGameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_on_going_game, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this.getContext());
        if (acct != null)
            StrMail = acct.getEmail();

        layout = view.findViewById(R.id.view_game_shower);

        findGame("IdJ1", "IdJ2");
        findGame("IdJ2", "IdJ1");
    }

    private void findGame(String userField, String opponentField)
    {
        db.collection("Game")
                .whereEqualTo(userField, StrMail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String opponent = document.getString(opponentField);
                            Timestamp temp = (Timestamp) document.get("EndDate");
                            Date date1 = temp.toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            String time  = sdf.format(date1);
                            String id = document.getId();
                            DocumentReference docref = db.collection("Game").document(id);
                            layout.addView(createGameShower(opponent, time, document), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0));
                        }
                    }
                    else
                    {
                        Log.e("BDD", "error L:78");
                    }
                });
    }

    private View createGameShower(String opponent, String time, QueryDocumentSnapshot doc)
    {
        opponent = " " + opponent;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
        LinearLayout bigLayout = new LinearLayout(this.getContext());
        bigLayout.setOrientation(LinearLayout.HORIZONTAL);
        if(colorSwitch)
            bigLayout.setBackgroundColor(getResources().getColor(R.color.white));
        else
            bigLayout.setBackgroundColor(getResources().getColor(R.color.near_white));
        colorSwitch = !colorSwitch;

        LinearLayout subBigLayout = new LinearLayout(this.getContext());
        subBigLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout sub2BigLayout = new LinearLayout(this.getContext());
        sub2BigLayout.setOrientation(LinearLayout.HORIZONTAL);
        layoutParams.setMargins(10,0,0,0);
        TextView sender = new TextView(this.getContext());
        sender.setText(getString(R.string.on_going_opponent));
        sender.setTextColor(getResources().getColor(R.color.black));
        sub2BigLayout.addView(sender, layoutParams);

        TextView txtOpponent = new TextView(getContext());
        txtOpponent.setText(opponent);
        txtOpponent.setTextColor(getResources().getColor(R.color.black));
        sub2BigLayout.addView(txtOpponent);
        subBigLayout.addView(sub2BigLayout);

        LinearLayout sub3BigLayout = new LinearLayout(this.getContext());
        sub2BigLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView duration = new TextView(this.getContext());
        duration.setText(getString(R.string.on_going_end));
        duration.setTextColor(getResources().getColor(R.color.black));
        sub3BigLayout.addView(duration, layoutParams);

        TextView txtDuree = new TextView(getContext());
        txtDuree.setText(" "+time);
        txtDuree.setTextColor(getResources().getColor(R.color.black));
        sub3BigLayout.addView(txtDuree);
        subBigLayout.addView(sub3BigLayout);
        bigLayout.addView(subBigLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0));
        Button validate = new Button(getContext());
        validate.setText(R.string.on_going_launch);
        validate.setOnClickListener(v -> {
            launchGame(doc);
        });
        layoutParams.setMargins(20,0,0,0);
        bigLayout.addView(validate, layoutParams);
        return bigLayout;
    }

    private void launchGame(QueryDocumentSnapshot doc)
    {
        long difficulte, score, actualQuest;
        boolean j1 ;
        if(StrMail.equals(doc.getString("IdJ1")))
        {
            j1 = true;
            difficulte = doc.getLong("NumLevelJ1");
            score = doc.getLong("ScoreJ1");
            actualQuest = doc.getLong("NumActualQuestionJ1");
        }
        else
        {
            j1 = false;
            difficulte = doc.getLong("NumLevelJ2");
            score = doc.getLong("ScoreJ2");
            actualQuest = doc.getLong("NumActualQuestionJ2");
        }
        LevelHolder.getInstance().setLevel(Level.recreateLevel((int)difficulte, getActivity().getAssets(), (int)score, (int)actualQuest));
        Intent intent = new Intent(getContext(), GameOnlineActivity.class);
        intent.putExtra("j1", j1);
        intent.putExtra("id", doc.getId());
        Timestamp endDate = (Timestamp) doc.get("EndDate");
        intent.putExtra("endDate", endDate);
        if(j1)
        {
            intent.putExtra("oppscore", doc.getLong("ScoreJ2"));
            intent.putExtra("oppNumLevel", doc.getLong("NumLevelJ2"));
        }
        else
        {
            intent.putExtra("oppscore", doc.getLong("ScoreJ1"));
            intent.putExtra("oppNumLevel", doc.getLong("NumLevelJ1"));
        }
        startActivity(intent);
        getActivity().finish();
    }
}