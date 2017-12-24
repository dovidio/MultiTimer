package io.dovid.multitimer.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;

import io.dovid.multitimer.R;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by umber on 12/20/2017.
 */

public class MyTourGuide {

    public enum TutorialStep {
        POINT_TO_FAB, POINT_TO_TIMER;
    }

    public static TourGuide loadTutorial(TutorialStep step, View target, TourGuide tourGuideHandler, Activity activity) {
        if (target != null) {
            if (step.equals(TutorialStep.POINT_TO_FAB)) {
                tourGuideHandler = loadFirstStep(target, activity, tourGuideHandler);
            } else if (step.equals(TutorialStep.POINT_TO_TIMER)) {
                tourGuideHandler = loadSecondStep(target, activity, tourGuideHandler);
            }
        }
        return tourGuideHandler;
    }

    private static TourGuide loadFirstStep(View target, Activity activity, final TourGuide tourGuide) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return TourGuide.init(activity).with(TourGuide.Technique.Click)
                .setPointer(new Pointer().setGravity(Gravity.TOP))
                .setToolTip(new ToolTip().setTitle(activity.getString(R.string.welcome)).
                        setDescription(activity.getString(R.string.click_on_this_button)).setGravity(Gravity.TOP))
                .setOverlay(new Overlay().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tourGuide != null) {
                            tourGuide.cleanUp();
                            sharedPreferences.edit()
                                    .putBoolean(TutorialStep.POINT_TO_FAB.toString(), true)
                                    .apply();
                        }
                    }
                }))
                .playOn(target);
    }

    private static TourGuide loadSecondStep(View target, Activity activity, final TourGuide tourGuide) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return TourGuide.init(activity).with(TourGuide.Technique.VerticalDownward)
                .setToolTip(new ToolTip()
                        .setDescription(activity.getString(R.string.long_press_timer))
                        .setGravity(Gravity.CENTER)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (tourGuide != null) {
                                    tourGuide.cleanUp();
                                }
                                sharedPreferences.edit()
                                        .putBoolean(TutorialStep.POINT_TO_TIMER.toString(), true)
                                        .apply();
                            }
                        })).setOverlay(new Overlay().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tourGuide != null) {
                            tourGuide.cleanUp();
                        }
                    }
                }).setStyle(Overlay.Style.Rectangle)).playOn(target);
    }
}
