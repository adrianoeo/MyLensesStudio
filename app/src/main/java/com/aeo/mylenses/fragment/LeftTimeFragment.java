package com.aeo.mylenses.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.aeo.mylenses.R;
import com.aeo.mylenses.adapter.TimeLensesCollectionPagerAdapter;
import com.aeo.mylenses.dao.TimeLensesDAO;
import com.aeo.mylenses.vo.TimeLensesVO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//import com.aeo.mylenses.dao.AlarmDAO;

@SuppressLint("SimpleDateFormat")
public class LeftTimeFragment extends DialogFragment {
    private static Button btnDateLeft;
    private DatePickerFragment fragmentDate;
    private static NumberPicker numberPickerLeft;
    private static CheckBox cbInUseLeft;
    private static NumberPicker qtdLeft;
    private static Spinner spinnerLeft;
    private static Button btnCopyToRight;

    private static Button btnDateRight;
    private static NumberPicker numberPickerRight;
    private static CheckBox cbInUseRight;
    private static NumberPicker qtdRight;
    private static Spinner spinnerRight;
    private MenuItem menuItemEdit;

    public static final String DATE_LEFT_EYE = "DATE_LEFT_EYE";
    public static final String KEY_ID_LENS = "KEY_ID_LENS";

    private View view;

    public static int idLenses;
    public static TimeLensesCollectionPagerAdapter timeLensesCollectionPagerAdapter;

    private Context context;

    public static TimeLensesVO timeLensesVO;

    public static LeftTimeFragment newInstance(int idLens, TimeLensesCollectionPagerAdapter timeLensesCollectionPagerAdapter1) {
        timeLensesCollectionPagerAdapter = timeLensesCollectionPagerAdapter1;

        LeftTimeFragment lensFragment = new LeftTimeFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_ID_LENS, idLens);
        lensFragment.setArguments(args);
        return lensFragment;
    }

    public LeftTimeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idLenses = getArguments() != null ? getArguments().getInt(KEY_ID_LENS)
                : 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        idLenses = getArguments() != null ? getArguments().getInt(KEY_ID_LENS)
                : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_left_time, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        context = getContext();

        spinnerLeft = (Spinner) view.findViewById(R.id.spinnerLeft);
        numberPickerLeft = (NumberPicker) view
                .findViewById(R.id.numberPickerLeft);
        btnDateLeft = (Button) view.findViewById(R.id.btnDateLeft);
        cbInUseLeft = (CheckBox) view.findViewById(R.id.cbxWearLeft);
        qtdLeft = (NumberPicker) view.findViewById(R.id.qtdLeft);
        btnCopyToRight = (Button) view.findViewById(R.id.btnCopyToRight);

        btnDateLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar date = Calendar.getInstance();
                try {
                    String dateFormat = context.getResources().getString(R.string.locale);
                    date.setTime(new SimpleDateFormat(dateFormat)
                            .parse(btnDateLeft.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                fragmentDate = DatePickerFragment.newInstance(/*
                                                             * LeftTimeFragment.this,
															 */DATE_LEFT_EYE,
                        date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                        date.get(Calendar.DAY_OF_MONTH));
                fragmentDate.show(getFragmentManager(), "datePickerLeft");
            }
        });

        btnCopyToRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copy();
            }
        });

        setHasOptionsMenu(true);

        setNumberPicker();
        setSpinnerDiscard();
        setLensValues();

        enableControls(menuItemEdit != null && menuItemEdit.isVisible());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menuItemEdit = menu.findItem(R.id.menuEditLenses);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (idLenses == 0) {
            enableControls(true);
        }
    }

    private void setSpinnerDiscard() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, R.array.discard_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerLeft.setAdapter(adapter);

        spinnerLeft.setSelection(1);
    }

    private void setNumberPicker() {
        numberPickerLeft.setMinValue(1);
        numberPickerLeft.setMaxValue(100);
        numberPickerLeft.setWrapSelectorWheel(false);
        qtdLeft.setMinValue(0);
        qtdLeft.setMaxValue(30);
        qtdLeft.setWrapSelectorWheel(false);
    }

    private void setDate() {
//        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);

//        String strDate = new StringBuilder(String.format("%02d", day))
//                .append("/").append(String.format("%02d", month + 1))
//                .append("/").append(String.valueOf(year)).toString();

        String dateFormat = context.getResources().getString(R.string.locale);

        String strDate = new SimpleDateFormat(dateFormat).format(new Date());

        btnDateLeft.setText(strDate);
    }

    private void setLensValues() {
        TimeLensesDAO dao = TimeLensesDAO.getInstance(context);
        timeLensesVO = dao.getById(idLenses);
        if (timeLensesVO != null) {
            btnDateLeft.setText(timeLensesVO.getDateLeft());
            numberPickerLeft.setValue(timeLensesVO.getExpirationLeft());
            spinnerLeft.setSelection(timeLensesVO.getTypeLeft());
            cbInUseLeft.setChecked(timeLensesVO.getInUseLeft() == 1 ? true : false);
            qtdLeft.setValue(timeLensesVO.getQtdLeft());
        } else {
            setDate();
            cbInUseLeft.setChecked(true);

            //Seta qtd
            TimeLensesVO lastLenses = dao.getLastLens();

            if (lastLenses != null) {
                int qtd = lastLenses.getQtdLeft() - 1;
                qtdLeft.setValue(qtd >= 0 ? qtd : 0);
            }
        }
    }

    private void enableControls(boolean enabled) {
        btnDateLeft.setEnabled(enabled);
        numberPickerLeft.setEnabled(enabled);
        spinnerLeft.setEnabled(enabled);
        cbInUseLeft.setEnabled(enabled);
        qtdLeft.setEnabled(enabled);
        btnCopyToRight.setEnabled(enabled);
    }

    public void copy() {
        Fragment rightFragment = timeLensesCollectionPagerAdapter.getFragment(1);

        if (rightFragment != null) {
            View rightView = rightFragment.getView();

            if (rightView != null) {
                spinnerRight = (Spinner) rightView.findViewById(R.id.spinnerRight);
                numberPickerRight = (NumberPicker) rightView
                        .findViewById(R.id.numberPickerRight);
                btnDateRight = (Button) rightView.findViewById(R.id.btnDateRight);
                cbInUseRight = (CheckBox) rightView.findViewById(R.id.cbxWearRight);
                qtdRight = (NumberPicker) rightView.findViewById(R.id.qtdRight);

                btnDateRight.setText(btnDateLeft.getText().toString());
                numberPickerLeft.clearFocus();
                numberPickerRight.setValue(numberPickerLeft.getValue());
                spinnerRight.setSelection(spinnerLeft.getSelectedItemPosition());
                cbInUseRight.setChecked(cbInUseLeft.isChecked());
                qtdRight.setValue(qtdLeft.getValue());
            }
        }


    }

}
