package com.aeo.mylensesstudio.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
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
import android.widget.TextView;

import com.aeo.mylensesstudio.R;
import com.aeo.mylensesstudio.dao.LensDAO;
import com.aeo.mylensesstudio.vo.LensStatusVO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//import com.aeo.mylensesstudio.dao.AlarmDAO;

@SuppressLint("SimpleDateFormat")
public class RightPeriodFragment extends DialogFragment {
	private static Button btnDateRight;
	private DatePickerFragment fragmentDate;
	private static NumberPicker numberPickerRight;
	private static CheckBox cbInUseRight;
	private static CheckBox cbCountUnitRight;
	private static TextView tvIdLens;

	public static final String DATE_RIGHT_EYE = "DATE_RIGHT_EYE";
	public static final String KEY_ID_LENS = "KEY_ID_LENS";

	private View view;
	private static Spinner spinnerRight;
	private MenuItem menuItemEdit;
	private MenuItem menuItemSave;
	private MenuItem menuItemCancel;
	private MenuItem menuItemDelete;
	public static int idLenses;

	private Context context;

	public static LensStatusVO lensVO;

	public static RightPeriodFragment newInstance(int idLens) {
		RightPeriodFragment lensFragment = new RightPeriodFragment();
		Bundle args = new Bundle();
		args.putInt(KEY_ID_LENS, idLens);
		lensFragment.setArguments(args);
		return lensFragment;
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

		view = inflater.inflate(R.layout.fragment_right_period, container, false);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		context = getContext();

		spinnerRight = (Spinner) view.findViewById(R.id.spinnerRight);
		numberPickerRight = (NumberPicker) view
				.findViewById(R.id.numberPickerRight);
		btnDateRight = (Button) view.findViewById(R.id.btnDateRight);
		cbInUseRight = (CheckBox) view.findViewById(R.id.cbxWearRight);
		cbCountUnitRight = (CheckBox) view.findViewById(R.id.cbxCountUnitRight);
		tvIdLens = (TextView) view.findViewById(R.id.tvIdLens);

		btnDateRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar date = Calendar.getInstance();
				try {
					date.setTime(new SimpleDateFormat("dd/MM/yyyy")
							.parse(btnDateRight.getText().toString()));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				fragmentDate = DatePickerFragment.newInstance(/*
															 * RightPeriodFragment.this,
															 */DATE_RIGHT_EYE,
						date.get(Calendar.YEAR), date.get(Calendar.MONTH),
						date.get(Calendar.DAY_OF_MONTH));
				fragmentDate.show(getFragmentManager(), "datePickerRight");
			}
		});

		setHasOptionsMenu(true);

		// setDate();
		setNumberPicker();
		setSpinnerDiscard();
		setLensValues();

		enableControls(menuItemEdit != null && menuItemEdit.isVisible());

		return view;
	}

	private void setSpinnerDiscard() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				context, R.array.discard_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerRight.setAdapter(adapter);

		spinnerRight.setSelection(1);
	}

	private void setNumberPicker() {
		numberPickerRight.setMinValue(1);
		numberPickerRight.setMaxValue(100);
		numberPickerRight.setWrapSelectorWheel(false);
	}

	private void setDate() {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		String strDate = new StringBuilder(String.format("%02d", day))
				.append("/").append(String.format("%02d", month + 1))
				.append("/").append(String.valueOf(year)).toString();

		btnDateRight.setText(strDate);
	}

	private void setLensValues() {
		LensDAO dao = LensDAO.getInstance(context);
		lensVO = dao.getById(idLenses);
		if (lensVO != null) {
			btnDateRight.setText(lensVO.getDateRight());
			numberPickerRight.setValue(lensVO.getExpirationRight());
			spinnerRight.setSelection(lensVO.getTypeRight());
			cbInUseRight.setChecked(lensVO.getInUseRight() == 1 ? true : false);
			cbCountUnitRight.setChecked(lensVO.getCountUnitRight() == 1 ? true
					: false);
			tvIdLens.setText(lensVO.getId().toString());
		} else {
			setDate();
			cbInUseRight.setChecked(true);
			cbCountUnitRight.setChecked(true);
			setLensStatusVO();
		}
	}

	private void enableControls(boolean enabled) {
		btnDateRight.setEnabled(enabled);
		numberPickerRight.setEnabled(enabled);
		spinnerRight.setEnabled(enabled);
		cbInUseRight.setEnabled(enabled);
		cbCountUnitRight.setEnabled(enabled);
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

//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		getActivity().getMenuInflater().inflate(R.menu.time_lenses, menu);
//		menuItemEdit = menu.findItem(R.id.menuEditLenses);
//		menuItemSave = menu.findItem(R.id.menuSaveLenses);
//		menuItemCancel = menu.findItem(R.id.menuCancelLenses);
//		menuItemDelete = menu.findItem(R.id.menuDeleteLenses);
//	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (idLenses == 0) {
			enableMenuEdit(false);
			enableMenuSaveCancel(true);
			enableControls(true);
		} else {
			enableMenuSaveCancel(false);
			enableMenuEdit(LensDAO.getInstance(context).getLastIdLens() == idLenses);
		}
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.menuSaveLenses:
//			saveLens();
//			getActivity().finish();
//			return true;
//		case R.id.menuEditLenses:
//			enableControls(true);
//			item.setVisible(false);
//			enableMenuEdit(false);
//			enableMenuSaveCancel(true);
//			return true;
//		case R.id.menuCancelLenses:
//			getActivity().finish();
//			return true;
//		case R.id.menuDeleteLenses:
//			deleteLens(idLenses);
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}

	private void saveLens() {
		/*
		 * lensVO = new LensStatusVO();
		 *
		 * lensVO.setDateRight(btnDateRight.getText().toString());
		 * lensVO.setDateRight(btnDateRight.getText().toString());
		 * lensVO.setExpirationRight(numberPickerRight.getValue());
		 * lensVO.setExpirationRight(numberPickerRight.getValue());
		 * lensVO.setTypeRight(spinnerRight.getSelectedItemPosition());
		 * lensVO.setTypeRight(spinnerRight.getSelectedItemPosition());
		 * lensVO.setInUseRight(cbInUseRight.isChecked() ? 1 : 0);
		 * lensVO.setInUseRight(cbInUseRight.isChecked() ? 1 : 0);
		 */

		// setLensStatusVO();

		// LensDAO lensDAO = LensDAO.getInstance(context);
		// AlarmDAO alarmDAO = AlarmDAO.getInstance(context);
		// if (existsLens()) {
		// lensVO.setId(idLenses);
		// if (!lensVO.equals(lensDAO.getById(idLenses))) {
		// if (lensDAO.update(lensVO)) {
		// HistoryDAO.getInstance(context).insert();
		// alarmDAO.setAlarm(idLenses);
		// }
		// }
		// } else {
		// if (lensDAO.insert(lensVO)) {
		// alarmDAO.setAlarm(lensDAO.getLastIdLens());
		// HistoryDAO.getInstance(context).insert();
		// }
		// }

		LensDAO lensDAO = LensDAO.getInstance(context);
		lensDAO.save(setLensStatusVO());

	}

	public static LensStatusVO setLensStatusVO() {
		lensVO = new LensStatusVO();

		lensVO.setDateRight(btnDateRight.getText().toString());
		lensVO.setExpirationRight(numberPickerRight.getValue());
		lensVO.setTypeRight(spinnerRight.getSelectedItemPosition());
		lensVO.setInUseRight(cbInUseRight.isChecked() ? 1 : 0);
		lensVO.setCountUnitRight(cbCountUnitRight.isChecked() ? 1 : 0);

		if (!"".equals(tvIdLens.getText().toString())) {
			lensVO.setId(Integer.valueOf(tvIdLens.getText().toString()));
		}
		return lensVO;
	}

	private void deleteLens(final int id) {
		Builder builder = new Builder(context);
		builder.setMessage(R.string.msgDelete);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.btn_yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						LensDAO lensDAO = LensDAO.getInstance(context);
						lensDAO.delete(id);

//						AlarmDAO alarmDAO = AlarmDAO.getInstance(context);
//						alarmDAO.setAlarm(lensDAO.getLastIdLens());

						getActivity().finish();
					}
				});
		builder.setNegativeButton(R.string.btn_no, null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isVisibleToUser && btnDateRight != null && btnDateRight.isEnabled()) {
			saveLens();
//			getActivity().finish();
		}
	}

}