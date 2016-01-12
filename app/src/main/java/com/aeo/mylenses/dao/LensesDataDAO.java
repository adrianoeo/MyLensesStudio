package com.aeo.mylenses.dao;

import android.annotation.SuppressLint;
import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aeo.mylenses.activity.MainActivity;
import com.aeo.mylenses.db.DB;
import com.aeo.mylenses.util.Utility;
import com.aeo.mylenses.vo.DataLensesVO;

public class LensesDataDAO {

	private static String tableName = "reg_lenses";
	private static String[] columns = { "id", "description_left", "brand_left",
			"discard_type_left", "type_left", "power_left", "cylinder_left",
			"axis_left", "add_left", "buy_site_left", "date_ini_left",
			"number_units_left", "description_right", "brand_right",
			"discard_type_right", "type_right", "power_right",
			"cylinder_right", "axis_right", "add_right", "buy_site_right",
			"date_ini_right", "number_units_right", "bc_left", "bc_right", "dia_left", "dia_right" };
	private SQLiteDatabase db;
	private static LensesDataDAO instance;
	private Context context;
	BackupManager mBackupManager;

	public static LensesDataDAO getInstance(Context context) {
		if (instance == null) {
			return new LensesDataDAO(context);
		}
		return instance;
	}

	public LensesDataDAO(Context context) {
		this.context = context;
		db = new DB(context).getWritableDatabase();
		mBackupManager = new BackupManager(context);
	}

	public boolean insert(DataLensesVO vo) {
		synchronized (MainActivity.sDataLock) {
			ContentValues content = getContentValues(vo);

			mBackupManager.dataChanged();
			return db.insert(tableName, null, content) > 0;
		}
	}

	public boolean update(DataLensesVO vo) {
		synchronized (MainActivity.sDataLock) {
			ContentValues content = getContentValues(vo);

			mBackupManager.dataChanged();
			return db.update(tableName, content, "id=?", new String[] { vo
					.getId().toString() }) > 0;
		}
	}

	private ContentValues getContentValues(DataLensesVO vo) {
		ContentValues content = new ContentValues();
		content.put("description_left", vo.getDescription_left().trim());
		content.put("brand_left", vo.getBrand_left().trim());
		content.put("discard_type_left", vo.getDiscard_type_left());
		content.put("type_left", vo.getType_left());
		content.put("power_left", vo.getPower_left());
		content.put("cylinder_left", vo.getCylinder_left());
		content.put("axis_left", vo.getAxis_left());
		content.put("add_left", vo.getAdd_left());
		content.put("buy_site_left", vo.getBuy_site_left().trim());
		content.put("date_ini_left",
				Utility.formatDateToSqlite(vo.getDate_ini_left(), context));
		content.put("number_units_left", vo.getNumber_units_left());
		content.put("description_right", vo.getDescription_right().trim());
		content.put("brand_right", vo.getBrand_right().trim());
		content.put("discard_type_right", vo.getDiscard_type_right());
		content.put("type_right", vo.getType_right());
		content.put("power_right", vo.getPower_right());
		content.put("cylinder_right", vo.getCylinder_right());
		content.put("axis_right", vo.getAxis_right());
		content.put("add_right", vo.getAdd_right());
		content.put("buy_site_right", vo.getBuy_site_right().trim());
		content.put("date_ini_right",
				Utility.formatDateToSqlite(vo.getDate_ini_right(), context));
		content.put("number_units_right", vo.getNumber_units_right());
		content.put("bc_left", vo.getBc_left());
		content.put("bc_right", vo.getBc_right());
		content.put("dia_left", vo.getDia_left());
		content.put("dia_right", vo.getDia_right());
		return content;
	}

	public DataLensesVO getById(Integer id) {
		Cursor rs = db.query(tableName, columns, "id=?",
				new String[] { id.toString() }, null, null, null);

		DataLensesVO vo = null;
		if (rs.moveToFirst()) {
			vo = setLensesVO(rs);
		}
		return vo;
	}

	public DataLensesVO getLastLenses() {
		Cursor c = db.rawQuery("select * from " + tableName
				+ " order by id desc limit 1", null);

		DataLensesVO vo = null;
		if (c.moveToFirst()) {
			vo = setLensesVO(c);
		}
		return vo;
	}

	private DataLensesVO setLensesVO(Cursor c) {
		DataLensesVO vo;
		vo = new DataLensesVO();
		vo.setId(c.getInt(c.getColumnIndex("id")));
		vo.setDescription_left(c.getString(c.getColumnIndex("description_left")));
		vo.setBrand_left(c.getString(c.getColumnIndex("brand_left")));
		vo.setDiscard_type_left(c.getString(c
				.getColumnIndex("discard_type_left")));
		vo.setType_left(c.getString(c.getColumnIndex("type_left")));
		vo.setPower_left(c.getString(c.getColumnIndex("power_left")));
		vo.setCylinder_left(c.getString(c.getColumnIndex("cylinder_left")));
		vo.setAxis_left(c.getString(c.getColumnIndex("axis_left")));
		vo.setAdd_left(c.getString(c.getColumnIndex("add_left")));
		vo.setBuy_site_left(c.getString(c.getColumnIndex("buy_site_left")));
		vo.setDate_ini_left(Utility.formatDateDefault(c.getString(c
				.getColumnIndex("date_ini_left")), context));
		vo.setNumber_units_left(c.getInt(c.getColumnIndex("number_units_left")));
		vo.setDescription_right(c.getString(c
				.getColumnIndex("description_right")));
		vo.setBrand_right(c.getString(c.getColumnIndex("brand_right")));
		vo.setDiscard_type_right(c.getString(c
				.getColumnIndex("discard_type_right")));
		vo.setType_right(c.getString(c.getColumnIndex("type_right")));
		vo.setPower_right(c.getString(c.getColumnIndex("power_right")));
		vo.setCylinder_right(c.getString(c.getColumnIndex("cylinder_right")));
		vo.setAxis_right(c.getString(c.getColumnIndex("axis_right")));
		vo.setAdd_right(c.getString(c.getColumnIndex("add_right")));
		vo.setBuy_site_right(c.getString(c.getColumnIndex("buy_site_right")));
		vo.setDate_ini_right(Utility.formatDateDefault(c.getString(c
				.getColumnIndex("date_ini_right")), context));
		vo.setNumber_units_right(c.getInt(c
				.getColumnIndex("number_units_right")));
		vo.setBc_left(c.getDouble(c.getColumnIndex("bc_left")));
		vo.setBc_right(c.getDouble(c.getColumnIndex("bc_right")));
		vo.setDia_left(c.getDouble(c.getColumnIndex("dia_left")));
		vo.setDia_right(c.getDouble(c.getColumnIndex("dia_right")));
		return vo;
	}

	public int getLastIdLens() {
		Cursor rs = db.rawQuery("select max(id) from " + tableName, null);

		if (rs.moveToFirst()) {
			return rs.getInt(0);
		}
		return 0;
	}
/*
	public int[] getUnitsRemaining() {
//		HistoryDAO dao = HistoryDAO.getInstance(context);
//		int unitsLeft = dao.getSaldoLensLeft();
//		int unitsRight = dao.getSaldoLensRight();

		int unitsLeft = ListReplaceLensFragment.listLenses == null ? TimeLensesDAO.getInstance(
				context).getLastLens().getQtdLeft() : ListReplaceLensFragment.listLenses
				.get(0).getQtdLeft();

		int unitsRight = ListReplaceLensFragment.listLenses == null ? TimeLensesDAO.getInstance(
				context).getLastLens().getQtdRight() : ListReplaceLensFragment.listLenses
				.get(0).getQtdRight();

		return new int[] { unitsLeft, unitsRight };
	}
*/

	@SuppressLint("SimpleDateFormat")
	public boolean updateDate(String column, int idLensesData, String date) {
		synchronized (MainActivity.sDataLock) {
			ContentValues content = new ContentValues();
			content.put(column, date);

			mBackupManager.dataChanged();
			return db.update(tableName, content, "id=?",
					new String[] { String.valueOf(idLensesData) }) > 0;
		}
	}

}
