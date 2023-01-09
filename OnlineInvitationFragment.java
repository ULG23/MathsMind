package fr.ensisa.mathsmind;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fr.ensisa.mathsmind.level.Level;
import fr.ensisa.mathsmind.level.LevelHolder;

public class OnlineInvitationFragment extends Fragment
{
    private static final String TAG = "OnlineFragment";
    boolean colorSwitch = true;
    private long NumLevelJ1, NumLevelJ2;
    LinearLayout layout;
    FirebaseFirestore db;
    String StrMail;
    String StrMailSender;

    QueryDocumentSnapshot docu;
    public OnlineInvitationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_invitation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this.getContext());
        if (acct != null)
            StrMail = acct.getEmail();

        layout = view.findViewById(R.id.view_invitation);

        db.collection("GameInvite")
                .whereEqualTo("IdJ2", StrMail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            NumLevelJ1 = document.getLong("NumLevelJ1");
                            StrMailSender = document.getString("IdJ1");
                            Timestamp date = (Timestamp) document.get("Date");
                            Date date1 = date.toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            String time  = sdf.format(date1);
                            long duration = document.getLong("Durée");
                            layout.addView(createInvitation(StrMailSender, time, duration, document.getId()), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0));
                        }
                    }
                    else
                    {
                        Log.e("BDD", "error L:78");
                    }
                });
    }

    private View createInvitation(String email, String time, long durationTime, String id)
    {
        email = " " + email;
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
                    sender.setText(getString(R.string.invitation_sender));
                    sender.setTextColor(getResources().getColor(R.color.black));
                    sub2BigLayout.addView(sender, layoutParams);

                    TextView txtEmail = new TextView(getContext());
                    txtEmail.setText(email);
                    txtEmail.setTextColor(getResources().getColor(R.color.black));
                    sub2BigLayout.addView(txtEmail);
                subBigLayout.addView(sub2BigLayout);

                LinearLayout sub3BigLayout = new LinearLayout(this.getContext());
                sub2BigLayout.setOrientation(LinearLayout.HORIZONTAL);
                    TextView duration = new TextView(this.getContext());
                    duration.setText(getString(R.string.invitation_duration));
                    duration.setTextColor(getResources().getColor(R.color.black));
                    sub3BigLayout.addView(duration, layoutParams);

                    TextView txtDuree = new TextView(getContext());
                    txtDuree.setText(" "+time + " | " + durationTime + "min");
                    txtDuree.setTextColor(getResources().getColor(R.color.black));
                    sub3BigLayout.addView(txtDuree);
                subBigLayout.addView(sub3BigLayout);
        bigLayout.addView(subBigLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0));
        Button validate = new Button(getContext());
        validate.setText(R.string.invitation_validate);
        validate.setOnClickListener(v -> {

            long ts = Timestamp.now().getSeconds();
            long newTime = ts+(durationTime)*60;
            Timestamp now = Timestamp.now();


            createGame(StrMailSender, now , newTime, id);


            findGame("IdJ1", "IdJ2");
            findGame("IdJ2", "IdJ1");
            //launchGame(docu);

        });
        layoutParams.setMargins(20,0,0,0);
        bigLayout.addView(validate, layoutParams);
        return bigLayout;
    }


    public void createGame(String senderEmail, Timestamp dateNow, long maxTime, String id)
    {
        Timestamp timeMax = new Timestamp(maxTime, 12);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.ask_level_title)
                .setItems(R.array.ask_level, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        NumLevelJ2 = which+1;
                        Map<String, Object> game= new HashMap<>();
                        game.put("IdJ1",senderEmail); //IdJoueur invitant
                        game.put("IdJ2", StrMail); //IdJoeur invité
                        game.put("Date", dateNow);
                        game.put("EndDate", timeMax);
                        game.put("ScoreJ1", 0);
                        game.put("ScoreJ2", 0);
                        game.put("NumLevelJ1", NumLevelJ1); //difficulté du jeux du J1
                        game.put("NumActualQuestionJ1", 1);
                        game.put("NumLevelJ2", NumLevelJ2);
                        game.put("NumActualQuestionJ2", 1);

                        db.collection("Game").document().set(game)
                                .addOnSuccessListener(command -> {
                                    db.collection("GameInvite").document(id).delete();
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    getActivity().finish();
                                    startActivity(getActivity().getIntent());
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    }
                });
        builder.create().show();

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
        intent.putExtra("IdJ1", doc.getString("IdJ1"));

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
                            docu = document;
                        }
                    }
                    else
                    {
                        Log.e("BDD", "error L:78");
                    }
                });

    }




}