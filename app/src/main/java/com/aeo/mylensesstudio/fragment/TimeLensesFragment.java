package com.aeo.mylensesstudio.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.aeo.mylensesstudio.R;
import com.aeo.mylensesstudio.adapter.PeriodLensesCollectionPagerAdapter;
import com.aeo.mylensesstudio.dao.LensDAO;
import com.aeo.mylensesstudio.slidetab.SlidingTabLayout;
import com.aeo.mylensesstudio.vo.TimeLensesVO;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link TimeLensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeLensesFragment extends Fragment {
    // TODO: Rename and change types of parameters
    private int idLenses;

//    private OnFragmentInteractionListener mListener;

    PeriodLensesCollectionPagerAdapter periodLensesCollectionPagerAdapter;
    ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    private MenuItem menuItemEdit;
    private MenuItem menuItemSave;
    private MenuItem menuItemCancel;
    private MenuItem menuItemDelete;

    private static Button btnDateLeft;
    private static NumberPicker numberPickerLeft;
    private static CheckBox cbInUseLeft;
    private static NumberPicker qtdLeft;
    private static Spinner spinnerLeft;

    private static Button btnDateRight;
    private static NumberPicker numberPickerRight;
    private static CheckBox cbInUseRight;
    private static NumberPicker qtdRight;
    private static Spinner spinnerRight;

    public static final String DATE_LEFT_EYE = "DATE_LEFT_EYE";
    public static final String KEY_ID_LENS = "KEY_ID_LENS";


    public TimeLensesFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idLenses idLenses.
     * @return A new instance of fragment TimeLensesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimeLensesFragment newInstance(int idLenses) {
        TimeLensesFragment fragment = new TimeLensesFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_ID_LENS, idLenses);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idLenses = getArguments() != null ? getArguments().getInt(KEY_ID_LENS) : 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        idLenses = getArguments() != null ? getArguments().getInt(KEY_ID_LENS) : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_lenses, container, false);

        periodLensesCollectionPagerAdapter
                = new PeriodLensesCollectionPagerAdapter(getFragmentManager(), getContext(), idLenses);

        mViewPager = (ViewPager) view.findViewById(R.id.pagerTimeLenses);
        mViewPager.setAdapter(periodLensesCollectionPagerAdapter);

        View viewMain = getActivity().findViewById(R.id.drawer_layout);

        mSlidingTabLayout = (SlidingTabLayout) viewMain.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab, 0);
        mSlidingTabLayout.setViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.hide();

        Toolbar toolbar = (Toolbar) viewMain.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationContentDescription(R.string.title_periodo);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnPreviousFragment();
            }
        });

        return view;
    }

    private void returnPreviousFragment() {
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (idLenses == 0) {
            enableMenuEdit(false);
            enableMenuSaveCancel(true);
        } else {
            enableMenuSaveCancel(false);
            enableMenuEdit(LensDAO.getInstance(getContext()).getLastIdLens() == idLenses);
        }
    }

    private void enableMenuEdit(boolean enabled) {
        if (menuItemEdit != null) {
            menuItemEdit.setVisible(enabled);
        }
        if (menuItemDelete != null) {
            menuItemDelete.setVisible(enabled);
        }
    }

    private void enableMenuSaveCancel(boolean enabled) {
        if (menuItemSave != null) {
            menuItemSave.setVisible(enabled);
        }
        if (menuItemCancel != null) {
            menuItemCancel.setVisible(enabled);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.period_lenses, menu);
        menuItemEdit = menu.findItem(R.id.menuEditLenses);
        menuItemSave = menu.findItem(R.id.menuSaveLenses);
        menuItemCancel = menu.findItem(R.id.menuCancelLenses);
        menuItemDelete = menu.findItem(R.id.menuDeleteLenses);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveLens();
                returnPreviousFragment();
                return true;
            case R.id.menuSaveLenses:
                saveLens();
                returnPreviousFragment();
                return true;
            case R.id.menuEditLenses:
                enableControls(true);
                item.setVisible(false);
                enableMenuEdit(false);
                enableMenuSaveCancel(true);
                return true;
            case R.id.menuCancelLenses:
                returnPreviousFragment();
                return true;
            case R.id.menuDeleteLenses:
                deleteLens(idLenses);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void enableControls(boolean enabled) {
        if (getViewsFragmentLenses()) {
            btnDateLeft.setEnabled(enabled);
            btnDateRight.setEnabled(enabled);
            numberPickerLeft.setEnabled(enabled);
            numberPickerRight.setEnabled(enabled);
            spinnerLeft.setEnabled(enabled);
            spinnerRight.setEnabled(enabled);
            cbInUseLeft.setEnabled(enabled);
            cbInUseRight.setEnabled(enabled);
            qtdLeft.setEnabled(enabled);
            qtdRight.setEnabled(enabled);
        }
    }


    private void saveLens() {
        if (getViewsFragmentLenses()) {
            LensDAO lensDAO = LensDAO.getInstance(getContext());

            lensDAO.save(setTimeLensesVO());

            Toast.makeText(getContext(), R.string.msgSaved, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteLens(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.msgDelete);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.btn_yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LensDAO lensDAO = LensDAO.getInstance(getContext());
                        lensDAO.delete(id);

//						AlarmDAO alarmDAO = AlarmDAO.getInstance(context);
//						alarmDAO.setAlarm(lensDAO.getLastIdLens());

//                        getFragmentManager().popBackStack();
                    }
                });
        builder.setNegativeButton(R.string.btn_no, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean getViewsFragmentLenses() {
        Fragment leftFragment = periodLensesCollectionPagerAdapter.getFragment(0);

        if (leftFragment != null) {
            View leftView = leftFragment.getView();

            if (leftView != null) {
                spinnerLeft = (Spinner) leftView.findViewById(R.id.spinnerLeft);
                numberPickerLeft = (NumberPicker) leftView
                        .findViewById(R.id.numberPickerLeft);
                btnDateLeft = (Button) leftView.findViewById(R.id.btnDateLeft);
                cbInUseLeft = (CheckBox) leftView.findViewById(R.id.cbxWearLeft);
                qtdLeft = (NumberPicker) leftView.findViewById(R.id.qtdLeft);
            }
        }

        Fragment rightFragment = periodLensesCollectionPagerAdapter.getFragment(1);

        if (rightFragment != null) {
            View rightView = rightFragment.getView();

            if (rightView != null) {
                spinnerRight = (Spinner) rightView.findViewById(R.id.spinnerRight);
                numberPickerRight = (NumberPicker) rightView
                        .findViewById(R.id.numberPickerRight);
                btnDateRight = (Button) rightView.findViewById(R.id.btnDateRight);
                cbInUseRight = (CheckBox) rightView.findViewById(R.id.cbxWearRight);
                qtdRight = (NumberPicker) rightView.findViewById(R.id.qtdRight);
            }
        }

        return leftFragment != null && rightFragment != null;
    }

    private TimeLensesVO setTimeLensesVO() {
        TimeLensesVO timeLensesVO = new TimeLensesVO();

        timeLensesVO.setDateLeft(btnDateLeft.getText().toString());
        timeLensesVO.setDateRight(btnDateRight.getText().toString());
        timeLensesVO.setExpirationLeft(numberPickerLeft.getValue());
        timeLensesVO.setExpirationRight(numberPickerRight.getValue());
        timeLensesVO.setTypeLeft(spinnerLeft.getSelectedItemPosition());
        timeLensesVO.setTypeRight(spinnerRight.getSelectedItemPosition());
        timeLensesVO.setInUseLeft(cbInUseLeft.isChecked() ? 1 : 0);
        timeLensesVO.setInUseRight(cbInUseRight.isChecked() ? 1 : 0);
        timeLensesVO.setQtdLeft(qtdLeft.getValue());
        timeLensesVO.setQtdRight(qtdRight.getValue());

        if (idLenses != 0) {
            timeLensesVO.setId(idLenses);
        }
        return timeLensesVO;
    }


    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
