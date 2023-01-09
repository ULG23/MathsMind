package fr.ensisa.mathsmind;

import static android.provider.Settings.System.SCREEN_BRIGHTNESS;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    int flagSelect;

    private SeekBar seekVolume = null;
    private AudioManager audioManager = null;

    private SeekBar seekBrightness = null;
    private int brightnessLevel;
    private ContentResolver cResolver;
    private Window window;


    Switch bVibration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        flagSelect = R.drawable.france_flag;

        cResolver = getContentResolver();
        window = getWindow();



        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        bVibration = findViewById(R.id.simpleswitch);

        bVibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final VibrationEffect vibrationEffect1;

                ImageView vib = findViewById(R.id.vibration);

                if (bVibration.isChecked()){
                    vib.setImageResource(R.drawable.phone_vibration);
                }
                else{
                    vib.setImageResource(R.drawable.phone_novibration);
                }



                // this is the only type of the vibration which requires system version Oreo (API 26)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

                    // this effect creates the vibration of default amplitude for 1000ms(1 sec)
                    vibrationEffect1 = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);

                    // it is safe to cancel other vibrations currently taking place
                    vibrator.cancel();
                    vibrator.vibrate(vibrationEffect1);


                }
            }
        });


        ImageView brightness = findViewById(R.id.brightness);
        SeekBar seekBrightness = findViewById(R.id.niveau_luminosite);
        seekBrightness.setKeyProgressIncrement(1);

        // on recupere le niveau de luminosite de l'ecran
        try {
            brightnessLevel = Settings.System.getInt(cResolver, SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        // on "set" la valeur recuperee en tant que niveau de luminosite
        seekBrightness.setProgress(brightnessLevel);

        seekBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                brightnessLevel = progress;
                seekBar.setProgress(progress);

                if (brightnessLevel == 0) {
                    brightness.setImageResource(R.drawable.brightnessdown);
                } else {
                    brightness.setImageResource(R.drawable.brightness);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                try {

                    /*int brightnessMode = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE);

                    if (brightnessMode == android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                        android.provider.Settings.System.putInt(getContentResolver(),android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    }
                    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);*/

                    Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightnessLevel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LayoutParams layoutpars = window.getAttributes();
                layoutpars.screenBrightness = brightnessLevel / (float) 255;
                window.setAttributes(layoutpars);
            }
        });




        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener((view) -> {
            finish();
        });


        ImageView sound = findViewById(R.id.soundon);
        SeekBar seekVolume = (SeekBar) findViewById(R.id.niveau_de_son);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {}
            @Override
            public void onStartTrackingTouch(SeekBar arg0) {}
            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                int soundLevel = seekVolume.getProgress();

                if (soundLevel == 0) {
                    sound.setImageResource(R.drawable.soundoff);
                } else {
                    sound.setImageResource(R.drawable.soundon);
                }
            }
        });







    }
}