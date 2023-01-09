package fr.ensisa.mathsmind;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class OnlineInviteFragment extends Fragment {
    private static final String TAG = "OnlineFragment";

    FirebaseFirestore db;
    String StrMail;
    public OnlineInviteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_invite, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NumberPicker numberPicker = view.findViewById(R.id.invite_time);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(60);

        //Date object
        Date date= new Date();
        Timestamp ts = new Timestamp(date);

        db = FirebaseFirestore.getInstance();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this.getContext());
        if (acct != null)
            StrMail = acct.getEmail();


        Button validate = view.findViewById(R.id.invite_validate);
        validate.setOnClickListener(v -> {
            EditText edt = view.findViewById(R.id.invite_edt);

            Map<String, Object> gameInvite = new HashMap<>();
            gameInvite.put("IdJ1",StrMail); // IdJoueur invitant
            gameInvite.put("IdJ2", edt.getText().toString()); // IdJoeur invité
            gameInvite.put("Date", ts);

            gameInvite.put("Durée", numberPicker.getValue()); // durée en min
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.ask_level_title)
                    .setItems(R.array.ask_level, (dialog, which) -> {
                        gameInvite.put("NumLevelJ1", which+1);
                        db.collection("GameInvite").document()
                                .set(gameInvite)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                    });
            builder.create().show();
        });
    }
}