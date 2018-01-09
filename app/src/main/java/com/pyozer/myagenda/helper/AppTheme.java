package com.pyozer.myagenda.helper;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.pyozer.myagenda.R;
import com.pyozer.myagenda.preferences.PrefManagerConfig;

public class AppTheme {

    private String theme;
    private int color;
    private boolean dark;
    private boolean headerCustom;

    private int style;
    private int drawableGradient;

    private Context context;
    private boolean noActionBar;
    private boolean getStyleDo = false;

    public AppTheme(Context context, boolean noActionBar) {
        this.context = context;
        this.noActionBar = noActionBar;
        PrefManagerConfig prefManager = new PrefManagerConfig(context);
        theme = prefManager.getPrefTheme();
        color = prefManager.getPrefThemeColorPrimary();
        dark = prefManager.isPrefDarkTheme();
        headerCustom = prefManager.isPrefThemeHeader();
        drawableGradient = R.drawable.drawer_grey_500;
    }

    /**
     * Permet de déterminer le style
     *
     * @return style
     */
    public int getStyle() {
        switch (theme) {
            case "light":
                style = (noActionBar) ? R.style.AppTheme_NoActionBar : R.style.AppTheme;
                break;
            case "dark":
                style = (noActionBar) ? R.style.AppThemeNight_NoActionBar : R.style.AppThemeNight;
                break;
            case "custom":
                if (color == ContextCompat.getColor(context, R.color.md_red_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeRedDark_NoAction : R.style.AppThemeRed_NoAction) : ((dark) ? R.style.AppThemeRedDark : R.style.AppThemeRed);
                    drawableGradient = R.drawable.drawer_red;
                } else if (color == ContextCompat.getColor(context, R.color.md_red_700)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeRed700Dark_NoAction : R.style.AppThemeRed700_NoAction) : ((dark) ? R.style.AppThemeRed700Dark : R.style.AppThemeRed700);
                    drawableGradient = R.drawable.drawer_red;
                } else if (color == ContextCompat.getColor(context, R.color.md_pink_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemePinkDark_NoAction : R.style.AppThemePink_NoAction) : ((dark) ? R.style.AppThemePinkDark : R.style.AppThemePink);
                    drawableGradient = R.drawable.drawer_pink;
                } else if (color == ContextCompat.getColor(context, R.color.md_purple_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemePurpleDark_NoAction : R.style.AppThemePurple_NoAction) : ((dark) ? R.style.AppThemePurpleDark : R.style.AppThemePurple);
                    drawableGradient = R.drawable.drawer_purple;
                } else if (color == ContextCompat.getColor(context, R.color.md_deep_purple_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeDeepPurpleDark_NoAction : R.style.AppThemeDeepPurple_NoAction) : ((dark) ? R.style.AppThemeDeepPurpleDark : R.style.AppThemeDeepPurple);
                    drawableGradient = R.drawable.drawer_deep_purple;
                } else if (color == ContextCompat.getColor(context, R.color.bg_gradient_start)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeGradientStartDark_NoAction : R.style.AppThemeGradientStart_NoAction) : ((dark) ? R.style.AppThemeGradientStartDark : R.style.AppThemeGradientStart);
                    drawableGradient = R.drawable.drawer_gradient;
                } else if (color == ContextCompat.getColor(context, R.color.bg_gradient_end)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeGradientEndDark_NoAction : R.style.AppThemeGradientEnd_NoAction) : ((dark) ? R.style.AppThemeGradientEndDark : R.style.AppThemeGradientEnd);
                    drawableGradient = R.drawable.drawer_gradient;
                } else if (color == ContextCompat.getColor(context, R.color.md_indigo_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeIndigoDark_NoAction : R.style.AppThemeIndigo_NoAction) : ((dark) ? R.style.AppThemeIndigoDark : R.style.AppThemeIndigo);
                    drawableGradient = R.drawable.drawer_indigo;
                } else if (color == ContextCompat.getColor(context, R.color.md_blue_700)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeBlue700Dark_NoAction : R.style.AppThemeBlue700_NoAction) : ((dark) ? R.style.AppThemeBlue700Dark : R.style.AppThemeBlue700);
                    drawableGradient = R.drawable.drawer_blue;
                } else if (color == ContextCompat.getColor(context, R.color.md_light_blue_700)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeLightBlue700Dark_NoAction : R.style.AppThemeLight700Blue_NoAction) : ((dark) ? R.style.AppThemeLightBlue700Dark : R.style.AppThemeLight700Blue);
                    drawableGradient = R.drawable.drawer_light_blue;
                } else if (color == ContextCompat.getColor(context, R.color.md_blue_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeBlueDark_NoAction : R.style.AppThemeBlue_NoAction) : ((dark) ? R.style.AppThemeBlueDark : R.style.AppThemeBlue);
                    drawableGradient = R.drawable.drawer_blue;
                } else if (color == ContextCompat.getColor(context, R.color.md_light_blue_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeLightBlueDark_NoAction : R.style.AppThemeLightBlue_NoAction) : ((dark) ? R.style.AppThemeLightBlueDark : R.style.AppThemeLightBlue);
                    drawableGradient = R.drawable.drawer_light_blue;
                } else if (color == ContextCompat.getColor(context, R.color.md_cyan_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeCyanDark_NoAction : R.style.AppThemeCyan_NoAction) : ((dark) ? R.style.AppThemeCyanDark : R.style.AppThemeCyan);
                    drawableGradient = R.drawable.drawer_cyan;
                } else if (color == ContextCompat.getColor(context, R.color.md_teal_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeTealDark_NoAction : R.style.AppThemeTeal_NoAction) : ((dark) ? R.style.AppThemeTealDark : R.style.AppThemeTeal);
                    drawableGradient = R.drawable.drawer_teal;
                } else if (color == ContextCompat.getColor(context, R.color.md_green_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeGreenDark_NoAction : R.style.AppThemeGreen_NoAction) : ((dark) ? R.style.AppThemeGreenDark : R.style.AppThemeGreen);
                    drawableGradient = R.drawable.drawer_green;
                } else if (color == ContextCompat.getColor(context, R.color.md_light_green_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeLightGreenDark_NoAction : R.style.AppThemeLightGreen_NoAction) : ((dark) ? R.style.AppThemeLightGreenDark : R.style.AppThemeLightGreen);
                    drawableGradient = R.drawable.drawer_light_green;
                } else if (color == ContextCompat.getColor(context, R.color.md_lime_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeLimeDark_NoAction : R.style.AppThemeLime_NoAction) : ((dark) ? R.style.AppThemeLimeDark : R.style.AppThemeLime);
                    drawableGradient = R.drawable.drawer_lime;
                } else if (color == ContextCompat.getColor(context, R.color.md_amber_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeAmberDark_NoAction : R.style.AppThemeAmber_NoAction) : ((dark) ? R.style.AppThemeAmberDark : R.style.AppThemeAmber);
                    drawableGradient = R.drawable.drawer_amber;
                } else if (color == ContextCompat.getColor(context, R.color.md_orange_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeOrangeDark_NoAction : R.style.AppThemeOrange_NoAction) : ((dark) ? R.style.AppThemeOrangeDark : R.style.AppThemeOrange);
                    drawableGradient = R.drawable.drawer_orange;
                } else if (color == ContextCompat.getColor(context, R.color.md_deep_orange_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeDeepOrangeDark_NoAction : R.style.AppThemeDeepOrange_NoAction) : ((dark) ? R.style.AppThemeDeepOrangeDark : R.style.AppThemeDeepOrange);
                    drawableGradient = R.drawable.drawer_deep_orange;
                } else if (color == ContextCompat.getColor(context, R.color.md_brown_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeBrownDark_NoAction : R.style.AppThemeBrown_NoAction) : ((dark) ? R.style.AppThemeBrownDark : R.style.AppThemeBrown);
                    drawableGradient = R.drawable.drawer_brown;
                } else if (color == ContextCompat.getColor(context, R.color.md_blue_grey_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeBlueGreyDark_NoAction : R.style.AppThemeBlueGrey_NoAction) : ((dark) ? R.style.AppThemeBlueGreyDark : R.style.AppThemeBlueGrey);
                    drawableGradient = R.drawable.drawer_blue_grey;
                } else if (color == ContextCompat.getColor(context, R.color.md_grey_500)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeGreyDark_NoAction : R.style.AppThemeGrey_NoAction) : ((dark) ? R.style.AppThemeGreyDark : R.style.AppThemeGrey);
                    drawableGradient = R.drawable.drawer_grey_500;
                } else if (color == ContextCompat.getColor(context, R.color.md_grey_800)) {
                    style = (noActionBar) ? ((dark) ? R.style.AppThemeHardGreyDark_NoAction : R.style.AppThemeHardGrey_NoAction) : ((dark) ? R.style.AppThemeHardGreyDark : R.style.AppThemeHardGrey);
                    drawableGradient = R.drawable.drawer_grey_500;
                }
                break;
        }
        drawableGradient = (headerCustom) ? drawableGradient : R.drawable.drawer_grey_500;
        getStyleDo = true;
        return style;
    }

    /**
     * Retourne le gradient pour le Drawer
     *
     * @return drawableGradient
     */
    public int getGradient() {
        if (!getStyleDo) {
            getStyle();
        }
        return drawableGradient;
    }
}
