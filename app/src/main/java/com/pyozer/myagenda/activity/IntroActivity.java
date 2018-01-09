package com.pyozer.myagenda.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.pyozer.myagenda.R;
import com.pyozer.myagenda.preferences.PrefManagerConfig;

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(AppIntro2Fragment.newInstance(
                getString(R.string.intro_welcome_title),
                getString(R.string.intro_welcome_desc),
                R.mipmap.icon,
                Color.TRANSPARENT));

        addSlide(AppIntro2Fragment.newInstance(
                getString(R.string.intro_agenda_title),
                getString(R.string.intro_agenda_desc),
                R.mipmap.intro_group,
                Color.TRANSPARENT));

        addSlide(AppIntro2Fragment.newInstance(
                getString(R.string.intro_customization_title),
                getString(R.string.intro_customization_desc),
                R.mipmap.intro_theme,
                Color.TRANSPARENT));

        addSlide(AppIntro2Fragment.newInstance(
                getString(R.string.intro_note_title),
                getString(R.string.intro_note_desc),
                R.mipmap.intro_note,
                Color.TRANSPARENT));

        addSlide(AppIntro2Fragment.newInstance(
                getString(R.string.intro_event_title),
                getString(R.string.intro_event_desc),
                R.mipmap.intro_event,
                Color.TRANSPARENT));

        addSlide(AppIntro2Fragment.newInstance(
                getString(R.string.intro_offline_title),
                getString(R.string.intro_offline_desc),
                R.mipmap.intro_internet,
                Color.TRANSPARENT));


        // Hide Skip/Done button.
        showSkipButton(false);

        setFadeAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        PrefManagerConfig prefManager = new PrefManagerConfig(this);
        prefManager.setFirstTimeLaunch(false);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(Fragment oldFragment, Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}