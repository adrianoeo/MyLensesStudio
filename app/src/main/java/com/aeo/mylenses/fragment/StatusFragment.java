package com.aeo.mylenses.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aeo.mylenses.R;
import com.aeo.mylenses.dao.TimeLensesDAO;
import com.aeo.mylenses.slidetab.SlidingTabLayout;
import com.aeo.mylenses.util.AnalyticsApplication;
import com.aeo.mylenses.util.Utility;
import com.aeo.mylenses.vo.TimeLensesVO;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class StatusFragment extends Fragment {

    private TextView tvDaysRemainingLeftEye;
    private TextView tvDaysRemainingRightEye;
    private TextView tvStrDayLeft;
    private TextView tvStrDayRight;
    private TextView tvStrUnitsLeft;
    private TextView tvStrUnitsRight;
    private TextView tvStrUnitsRemainingLeft;
    private TextView tvStrUnitsRemainingRight;
    private Button btnDaysNotUsedLeft;
    private Button btnDaysNotUsedRight;
    private TextView tvStrDaysNotUsedLeft;
    private TextView tvStrDaysNotUsedRight;
    private TextView tvLeftEye;
    private TextView tvRightEye;
    private TextView tvEmpty;
    private LinearLayout linearLayoutLeft;
    private LinearLayout linearLayoutRight;
    private LinearLayout linearLayoutEmpty;

    private Context context;

    private Animation animation;
    private Tracker mTracker;
    private AdView mAdView;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        setHasOptionsMenu(true);

        context = getActivity();

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        linearLayoutLeft = (LinearLayout) view.findViewById(R.id.layoutLeft);
        linearLayoutRight = (LinearLayout) view.findViewById(R.id.layoutRight);
        linearLayoutEmpty = (LinearLayout) view.findViewById(R.id.layoutEmpty);

        tvLeftEye = (TextView) view.findViewById(R.id.tvLeftEye);
        tvRightEye = (TextView) view.findViewById(R.id.tvRightEye);
        tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);

        tvDaysRemainingLeftEye = (TextView) view
                .findViewById(R.id.tvDaysRemainingLeftEye);
        tvDaysRemainingRightEye = (TextView) view
                .findViewById(R.id.tvDaysRemainingRightEye);
        tvStrDayLeft = (TextView) view.findViewById(R.id.tvStrDayLeft);
        tvStrDayRight = (TextView) view.findViewById(R.id.tvStrDayRight);
        tvStrUnitsLeft = (TextView) view.findViewById(R.id.tvStrUnitsLeft);
        tvStrUnitsRight = (TextView) view.findViewById(R.id.tvStrUnitsRight);
        tvStrUnitsRemainingLeft = (TextView) view
                .findViewById(R.id.tvStrUnitsRemainingLeft);
        tvStrUnitsRemainingRight = (TextView) view
                .findViewById(R.id.tvStrUnitsRemainingRight);
        btnDaysNotUsedLeft = (Button) view
                .findViewById(R.id.btnDaysNotUsedLeft);
        btnDaysNotUsedRight = (Button) view
                .findViewById(R.id.btnDaysNotUsedRight);
        tvStrDaysNotUsedLeft = (TextView) view
                .findViewById(R.id.tvStrDaysNotUsedLeft);
        tvStrDaysNotUsedRight = (TextView) view
                .findViewById(R.id.tvStrDaysNotUsedRight);

        btnDaysNotUsedLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogNumber(v);
            }
        });

        btnDaysNotUsedRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogNumber(v);
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("color_bg", "FFFFFF");
        bundle.putString("color_border", "0000FF");
        bundle.putString("color_link", "0066FF");
        bundle.putString("color_text", "000000");
        bundle.putString("color_url", "0033FF");

        animation = AnimationUtils.loadAnimation(context, R.anim.scale);

        //Retira Tab referente ao Fragment do Periodo das lentes
        View viewMain = getActivity().findViewById(R.id.drawer_layout);
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) viewMain.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(null);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_action_new);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.replaceFragmentWithBackStack(new TimeLensesFragment(), getFragmentManager());
                toolbar.setTitle(R.string.title_periodo);
            }
        });

        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        linearLayoutLeft.setVisibility(View.INVISIBLE);
        linearLayoutRight.setVisibility(View.INVISIBLE);
        linearLayoutEmpty.setVisibility(View.INVISIBLE);

        return view;
    }

    @SuppressLint("ResourceAsColor")
    public void setDays(TimeLensesVO timeLensesVO) {
        TimeLensesDAO dao = TimeLensesDAO.getInstance(getContext());

        int idLenses = dao.getLastIdLens();

        Long[] days = dao.getDaysToExpire(idLenses);

        // Left eye
        tvDaysRemainingLeftEye.setVisibility(View.VISIBLE);
        tvStrDayLeft.setVisibility(View.VISIBLE);

        tvDaysRemainingLeftEye.setText(String.valueOf(days[0]));

        if (days[0].compareTo(1L) == 0) {
            tvStrDayLeft.setText(R.string.str_day_remaining);
            tvStrDayLeft.setTextColor(getColor(R.color.black));
            tvDaysRemainingLeftEye.setTextColor(getColor(
                    R.color.black));
        } else if (days[0].compareTo(0L) == 0) {
            tvStrDayLeft.setText(R.string.str_time_to_replace);
            tvStrDayLeft.setTextColor(getColor(R.color.red));
            tvDaysRemainingLeftEye.setTextColor(getColor(
                    R.color.red));
            tvDaysRemainingLeftEye.setAnimation(animation);
        } else if (days[0].compareTo(0L) < 0) {
            tvDaysRemainingLeftEye.setText(String.valueOf(days[0] * -1));
            if (days[0] == -1) {
                tvStrDayLeft.setText(R.string.str_day_expired);
            } else {
                tvStrDayLeft.setText(R.string.str_days_expired);
            }
            tvStrDayLeft.setTextColor(getColor(R.color.red));
            tvDaysRemainingLeftEye.setTextColor(getColor(
                    R.color.red));
            tvDaysRemainingLeftEye.setAnimation(animation);

        } else {
            tvStrDayLeft.setText(R.string.str_days_remaining);
            tvStrDayLeft.setTextColor(getColor(R.color.black));
            tvDaysRemainingLeftEye.setTextColor(getColor(
                    R.color.black));
        }

        boolean isLeftVisible = timeLensesVO != null && timeLensesVO.getInUseLeft() == 1;

        tvDaysRemainingLeftEye.setVisibility(isLeftVisible ? View.VISIBLE
                : View.INVISIBLE);
        tvStrDayLeft.setVisibility(isLeftVisible ? View.VISIBLE
                : View.INVISIBLE);

        if (!isLeftVisible) {
            tvDaysRemainingLeftEye.clearAnimation();
        }

        // Right eye
        tvDaysRemainingRightEye.setVisibility(View.VISIBLE);
        tvStrDayRight.setVisibility(View.VISIBLE);

        tvDaysRemainingRightEye.setText(String.valueOf(days[1]));
        if (days[1].compareTo(1L) == 0) {
            tvStrDayRight.setText(R.string.str_day_remaining);
            tvStrDayRight.setTextColor(getColor(R.color.black));
            tvDaysRemainingRightEye.setTextColor(getColor(
                    R.color.black));
        } else if (days[1].compareTo(0L) == 0) {
            tvStrDayRight.setText(R.string.str_time_to_replace);
            tvStrDayRight.setTextColor(getColor(R.color.red));
            tvDaysRemainingRightEye.setTextColor(getColor(
                    R.color.red));
            tvDaysRemainingRightEye.setAnimation(animation);
        } else if (days[1].compareTo(0L) < 0) {
            tvDaysRemainingRightEye.setText(String.valueOf(days[1] * -1));
            if (days[1] == -1) {
                tvStrDayRight.setText(R.string.str_day_expired);
            } else {
                tvStrDayRight.setText(R.string.str_days_expired);
            }
            tvStrDayRight.setTextColor(getColor(R.color.red));
            tvDaysRemainingRightEye.setTextColor(getColor(
                    R.color.red));
            tvDaysRemainingRightEye.setAnimation(animation);
        } else {
            tvStrDayRight.setText(R.string.str_days_remaining);
            tvStrDayRight.setTextColor(getColor(R.color.black));
            tvDaysRemainingRightEye.setTextColor(getColor(
                    R.color.black));
        }

        boolean isRightVisible = timeLensesVO != null && timeLensesVO.getInUseRight() == 1;

        tvDaysRemainingRightEye.setVisibility(isRightVisible ? View.VISIBLE : View.INVISIBLE);
        tvStrDayRight.setVisibility(isRightVisible ? View.VISIBLE : View.INVISIBLE);

        if (!isRightVisible) {
            tvDaysRemainingRightEye.clearAnimation();
        }

        // Labels
        tvLeftEye.setVisibility(timeLensesVO != null ? View.VISIBLE : View.INVISIBLE);
        tvRightEye.setVisibility(timeLensesVO != null ? View.VISIBLE : View.INVISIBLE);
        tvEmpty.setVisibility(timeLensesVO == null ? View.VISIBLE : View.GONE);

        linearLayoutLeft.setVisibility(timeLensesVO == null ? View.GONE : View.VISIBLE);
        linearLayoutRight.setVisibility(timeLensesVO == null ? View.GONE : View.VISIBLE);
        linearLayoutEmpty.setVisibility(timeLensesVO == null ? View.VISIBLE : View.GONE);

     /*   tvEmpty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.setScreen(R.id.nav_periodo, toolbar, getActivity().getSupportFragmentManager());
            }
        });*/


    }

    public void setNumUnitsLenses(TimeLensesVO timeLensesVO) {
        if (timeLensesVO != null) {
            int[] unitsRemaining = TimeLensesDAO.getInstance(getContext()).getUnitsRemaining();

            int unitsLeft = unitsRemaining[0] < 0 ? 0 : unitsRemaining[0];
            int unitsRight = unitsRemaining[1] < 0 ? 0 : unitsRemaining[1];

            tvStrUnitsLeft.setText(String.valueOf(unitsLeft));
            tvStrUnitsRight.setText(String.valueOf(unitsRight));
            tvStrUnitsLeft.setTextColor(
                    unitsLeft > 1 ? getColor(R.color.black) : getColor(R.color.red));
            tvStrUnitsRight.setTextColor(
                    unitsRight > 1 ? getColor(R.color.black) : getColor(R.color.red));

            if (unitsLeft <= 1) {
                tvStrUnitsLeft.setAnimation(animation);
            }

            String unitsRemainingLeft = null;
            String unitsRemainingRight = null;

            unitsRemainingLeft = unitsLeft == 1 ? getString(R.string.str_unit_remaining)
                    : getString(R.string.str_units_remaining);
            unitsRemainingRight = unitsRight == 1 ? getString(R.string.str_unit_remaining)
                    : getString(R.string.str_units_remaining);

            tvStrUnitsRemainingLeft.setText(unitsRemainingLeft);
            tvStrUnitsRemainingRight.setText(unitsRemainingRight);

            tvStrUnitsRemainingLeft.setTextColor(
                    unitsLeft > 1 ? getColor(R.color.black) : getColor(R.color.red));
            tvStrUnitsRemainingRight.setTextColor(
                    unitsRight > 1 ? getColor(R.color.black) : getColor(R.color.red));

            if (unitsRight <= 1) {
                tvStrUnitsRight.setAnimation(animation);
            }

            boolean isLeftVisible = (timeLensesVO.getInUseLeft() == 1);
            setVisibleUnitLeft(isLeftVisible ? View.VISIBLE : View.INVISIBLE);

            boolean isRightVisible = (timeLensesVO.getInUseRight() == 1);
            setVisibleUnitRight(isRightVisible ? View.VISIBLE : View.INVISIBLE);

            if (!isLeftVisible) {
                tvStrUnitsLeft.clearAnimation();
            }

            if (!isRightVisible) {
                tvStrUnitsRight.clearAnimation();
            }

        } else {
            setVisibleUnitLeft(View.GONE);
            setVisibleUnitRight(View.GONE);
        }
    }

    public int getColor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(id, null);
        } else {
            return getResources().getColor(id);
        }
    }

    private void setDaysNotUsed(TimeLensesVO timeLensesVO) {

        if (timeLensesVO != null) {
            // Left
            int daysNotUsedLeft = timeLensesVO.getNumDaysNotUsedLeft();
            btnDaysNotUsedLeft.setText(String.valueOf(daysNotUsedLeft));

            int strLeft = daysNotUsedLeft != 1 ? R.string.str_days_not_used
                    : R.string.str_day_not_used;
            tvStrDaysNotUsedLeft.setText(strLeft);

            // Right
            int daysNotUsedRight = timeLensesVO.getNumDaysNotUsedRight();
            btnDaysNotUsedRight.setText(String.valueOf(daysNotUsedRight));

            int strRight = daysNotUsedRight != 1 ? R.string.str_days_not_used
                    : R.string.str_day_not_used;
            tvStrDaysNotUsedRight.setText(strRight);

            int left = timeLensesVO.getInUseLeft();
            int right = timeLensesVO.getInUseRight();

            btnDaysNotUsedLeft.setVisibility(left == 1 ? View.VISIBLE
                    : View.INVISIBLE);
            tvStrDaysNotUsedLeft.setVisibility(left == 1 ? View.VISIBLE
                    : View.INVISIBLE);
            btnDaysNotUsedRight.setVisibility(right == 1 ? View.VISIBLE
                    : View.INVISIBLE);
            tvStrDaysNotUsedRight.setVisibility(right == 1 ? View.VISIBLE
                    : View.INVISIBLE);
        } else {
            btnDaysNotUsedLeft.setVisibility(View.INVISIBLE);
            tvStrDaysNotUsedLeft.setVisibility(View.INVISIBLE);
            btnDaysNotUsedRight.setVisibility(View.INVISIBLE);
            tvStrDaysNotUsedRight.setVisibility(View.INVISIBLE);

        }
    }

    private void setVisibleUnitLeft(int visibility) {
        tvStrUnitsLeft.setVisibility(visibility);
        tvStrUnitsRemainingLeft.setVisibility(visibility);
    }

    private void setVisibleUnitRight(int visibility) {
        tvStrUnitsRight.setVisibility(visibility);
        tvStrUnitsRemainingRight.setVisibility(visibility);
    }

    @Override
    public void onResume() {
        super.onResume();
        TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);
        TimeLensesVO timeLensesVO = timeLensesDAO.getLastLens();

        setDays(timeLensesVO);
        setNumUnitsLenses(timeLensesVO);
        setDaysNotUsed(timeLensesVO);

        if (timeLensesVO == null) {
            CoordinatorLayout.LayoutParams params
                    = new CoordinatorLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
            fab.setLayoutParams(params);
            fab.show();
        } else {
            fab.hide();
        }

        toolbar.setTitle(R.string.title_status);

        mTracker.setScreenName("StatusFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    public void openDialogNumber(View view) {
        final View v = view;
        final RelativeLayout layout = new RelativeLayout(context);
        final NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setTag("NUMBER_PICKER_DAYS");
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(60);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams numberParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        numberParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        layout.setLayoutParams(layoutParams);
        layout.addView(numberPicker, numberParams);

        if (v.getId() == R.id.btnDaysNotUsedLeft) {
            numberPicker.setValue(Integer.valueOf(btnDaysNotUsedLeft.getText()
                    .toString()));
        } else if (v.getId() == R.id.btnDaysNotUsedRight) {
            numberPicker.setValue(Integer.valueOf(btnDaysNotUsedRight.getText()
                    .toString()));
        }

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.title_number_picker_days_not_used);
        dialog.setView(layout);

        dialog.setCancelable(false)
                .setPositiveButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                NumberPicker numPicker = (NumberPicker) layout
                                        .findViewWithTag("NUMBER_PICKER_DAYS");

                                int num = numPicker.getValue();
                                int str = num > 1 ? R.string.str_days_not_used
                                        : R.string.str_day_not_used;
                                String side = null;

                                if (v.getId() == R.id.btnDaysNotUsedLeft) {
                                    btnDaysNotUsedLeft.setText(String
                                            .valueOf(num));
                                    tvStrDaysNotUsedLeft.setText(str);
                                    side = TimeLensesDAO.LEFT;
                                } else if (v.getId() == R.id.btnDaysNotUsedRight) {
                                    btnDaysNotUsedRight.setText(String
                                            .valueOf(num));
                                    tvStrDaysNotUsedRight.setText(str);
                                    side = TimeLensesDAO.RIGHT;
                                }

                                TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);
                                TimeLensesVO timeLensesVO = timeLensesDAO.getLastLens();
                                timeLensesDAO.updateDaysNotUsed(num, side,
                                        timeLensesVO.getId());

                                setDays(timeLensesVO);
//                                AlarmDAO.getInstance(context).setAlarm(
//                                        timeLensesVO.getId());
                            }
                        })
                .setNegativeButton(R.string.btn_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}
