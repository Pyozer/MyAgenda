package com.pyozer.myagenda.ui.adapter.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pyozer.myagenda.R;
import com.pyozer.myagenda.model.Cours;
import com.pyozer.myagenda.model.EventCustom;
import com.pyozer.myagenda.preferences.PrefManagerConfig;

public class CoursViewHolder extends RecyclerView.ViewHolder {

    private TextView titre;
    private TextView description;
    private TextView date;

    private TextView coursJour;

    private LinearLayout coursLayout;
    private LinearLayout coursSeparator;

    private View coursHasNote;

    private Context context;
    private PrefManagerConfig prefManager;

    private Cours cours;

    private boolean isDarkTheme;

    public CoursViewHolder(View view) {
        super(view);
        context = view.getContext();

        prefManager = new PrefManagerConfig(context);
        isDarkTheme = (prefManager.isPrefDarkTheme() && !prefManager.getPrefTheme().equals("light")) || prefManager.getPrefTheme().equals("dark");

        titre = view.findViewById(R.id.cours_titre);
        description = view.findViewById(R.id.cours_description);
        date = view.findViewById(R.id.cours_date);

        coursJour = view.findViewById(R.id.cours_jour);

        coursLayout = view.findViewById(R.id.cours_layout);
        coursSeparator = view.findViewById(R.id.cours_separator);

        coursHasNote = view.findViewById(R.id.cours_has_note);
        coursHasNote.setVisibility(View.INVISIBLE);
    }

    public void bind(@NonNull Cours cours) {
        this.cours = cours;

        if (cours.isHeader())
            setupHeaderRow();
        else {
            initDefaultView();

            if (cours instanceof EventCustom) {
                int colorBg = ((EventCustom) cours).getColor();
                coursLayout.setBackgroundColor(colorBg);
                showNoteExist(colorBg);
                setTextToWhite();
            }

            if (cours.isExam())
                setupExamView();

            if (cours.hasNote())
                showNoteExist(prefManager.getNoteColor());
        }
    }

    private void setupHeaderRow() {
        coursLayout.setVisibility(View.GONE);
        coursSeparator.setVisibility(View.VISIBLE);
        coursHasNote.setVisibility(View.GONE);

        coursJour.setText(cours.getTitre());
    }

    private void initDefaultView() {
        coursLayout.setVisibility(View.VISIBLE);
        coursSeparator.setVisibility(View.GONE);
        coursHasNote.setVisibility(View.INVISIBLE);

        titre.setText(cours.getTitre());
        description.setText(cours.getDescription());
        date.setText(cours.getDateFormat());

        int bgActual = (isDarkTheme) ? R.color.backgroundNightDark : R.color.md_white;
        coursLayout.setBackgroundColor(ContextCompat.getColor(context, bgActual));

        int textPrimaryColor = (isDarkTheme) ? R.color.textColorPrimaryNight : R.color.textColorPrimary;
        int textSecondColor = (isDarkTheme) ? R.color.textColorSecondaryNight : R.color.textColorSecondary;

        setTextColor(textPrimaryColor, textPrimaryColor, textSecondColor);
    }

    private void setTextToWhite() {
        setTextColor(R.color.white, R.color.white, R.color.md_grey_100);
    }

    private void setTextColor(int colorTitle, int colorDesc, int colorDate) {
        titre.setTextColor(ContextCompat.getColor(context, colorTitle));
        description.setTextColor(ContextCompat.getColor(context, colorDesc));
        date.setTextColor(ContextCompat.getColor(context, colorDate));
    }

    private void showNoteExist(int colorBg) {
        coursHasNote.setVisibility(View.VISIBLE);
        coursHasNote.setBackgroundColor(colorBg);
    }

    private void setupExamView() {
        coursLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

        setTextToWhite();

        coursHasNote.setVisibility(View.VISIBLE);
        coursHasNote.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
    }
}