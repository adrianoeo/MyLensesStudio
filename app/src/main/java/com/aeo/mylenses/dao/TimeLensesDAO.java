package com.aeo.mylenses.dao;

import android.annotation.SuppressLint;
import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aeo.mylenses.R;
import com.aeo.mylenses.activity.MainActivity;
import com.aeo.mylenses.db.DB;
import com.aeo.mylenses.fragment.ListReplaceLensFragment;
import com.aeo.mylenses.util.Utility;
import com.aeo.mylenses.vo.TimeLensesVO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeLensesDAO {

    private static String tableName = "lens";
    private static String[] columns = {"id", "date_left", "date_right",
            "expiration_left", "expiration_right", "type_left", "type_right",
            "num_days_not_used_left", "num_days_not_used_right", "in_use_left",
            "in_use_right", "count_unit_left", "count_unit_right", "qtd_left", "qtd_right"};
    private SQLiteDatabase db;
    private static TimeLensesDAO instance;
    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";

    /**
     * Also cache a reference to the Backup Manager
     */
    BackupManager mBackupManager;
    private Context context;

    public static TimeLensesDAO getInstance(Context context) {
        if (instance == null) {
            instance = new TimeLensesDAO(context);
        }
        return instance;
    }

    public TimeLensesDAO(Context context) {
        db = new DB(context).getWritableDatabase();
        /** It is handy to keep a BackupManager cached */
        mBackupManager = new BackupManager(context);
        this.context = context;
    }

    public boolean insert(TimeLensesVO lensVO) {
        synchronized (MainActivity.sDataLock) {
            mBackupManager.dataChanged();
            return db.insert(tableName, null, getContentValues(lensVO)) > 0;
        }
    }

    public boolean update(TimeLensesVO lensVO) {
        synchronized (MainActivity.sDataLock) {
            ContentValues content = new ContentValues();
            content.put("date_left",
                    Utility.formatDateToSqlite(lensVO.getDateLeft(), context));
            content.put("date_right",
                    Utility.formatDateToSqlite(lensVO.getDateRight(), context));
            content.put("expiration_left", lensVO.getExpirationLeft());
            content.put("expiration_right", lensVO.getExpirationRight());
            content.put("type_left", lensVO.getTypeLeft());
            content.put("type_right", lensVO.getTypeRight());
            content.put("in_use_left", lensVO.getInUseLeft());
            content.put("in_use_right", lensVO.getInUseRight());
            content.put("count_unit_left", lensVO.getCountUnitLeft());
            content.put("count_unit_right", lensVO.getCountUnitRight());
            content.put("qtd_left", lensVO.getQtdLeft());
            content.put("qtd_right", lensVO.getQtdRight());
            mBackupManager.dataChanged();
            return db.update(tableName, content, "id=?", new String[]{lensVO
                    .getId().toString()}) > 0;
        }
    }

    public boolean incrementDaysNotUsed(TimeLensesVO lensVO) {
        synchronized (MainActivity.sDataLock) {
            // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            ContentValues content = new ContentValues();
            if (lensVO.getInUseLeft() == 1) {
                content.put("num_days_not_used_left",
                        lensVO.getNumDaysNotUsedLeft() + 1);
            }
            if (lensVO.getInUseRight() == 1) {
                content.put("num_days_not_used_right",
                        lensVO.getNumDaysNotUsedRight() + 1);
            }
            mBackupManager.dataChanged();
            return db.update(tableName, content, "id=?", new String[]{lensVO
                    .getId().toString()}) > 0;
        }
    }

    public boolean updateDaysNotUsed(int days, String side, int idLens) {
        synchronized (MainActivity.sDataLock) {
            ContentValues content = new ContentValues();
            if (LEFT.equals(side)) {
                content.put("num_days_not_used_left", days);
            } else {
                content.put("num_days_not_used_right", days);
            }

            mBackupManager.dataChanged();
            return db.update(tableName, content, "id=?",
                    new String[]{String.valueOf(idLens)}) > 0;
        }
    }

    public boolean delete(Integer id) {
        synchronized (MainActivity.sDataLock) {
            mBackupManager.dataChanged();
            return db.delete(tableName, "id=?", new String[]{id.toString()}) > 0;
        }
    }

    private ContentValues getContentValues(TimeLensesVO lensVO) {
        ContentValues content = new ContentValues();
        content.put("date_left",
                Utility.formatDateToSqlite(lensVO.getDateLeft(), context));
        content.put("date_right",
                Utility.formatDateToSqlite(lensVO.getDateRight(), context));
        content.put("expiration_left", lensVO.getExpirationLeft());
        content.put("expiration_right", lensVO.getExpirationRight());
        content.put("type_left", lensVO.getTypeLeft());
        content.put("type_right", lensVO.getTypeRight());
        content.put("num_days_not_used_left", lensVO.getNumDaysNotUsedLeft());
        content.put("num_days_not_used_right", lensVO.getNumDaysNotUsedRight());
        content.put("in_use_left", lensVO.getInUseLeft());
        content.put("in_use_right", lensVO.getInUseRight());
        content.put("count_unit_left", lensVO.getCountUnitLeft());
        content.put("count_unit_right", lensVO.getCountUnitRight());
        content.put("qtd_left", lensVO.getQtdLeft());
        content.put("qtd_right", lensVO.getQtdRight());

        return content;
    }

    public TimeLensesVO getById(Integer id) {
        Cursor cursor = db.query(tableName, columns, "id=?",
                new String[]{id.toString()}, null, null, null);

        TimeLensesVO vo = null;
        if (cursor.moveToFirst()) {
            vo = setLensStatusVO(cursor);
        }
        return vo;
    }

    public List<TimeLensesVO> getListLens() {
        Cursor cursor = db.query(tableName, columns, null, null, null, null,
                "id desc");

        List<TimeLensesVO> listVO = new ArrayList<TimeLensesVO>();

        while (cursor.moveToNext()) {
            listVO.add(setLensStatusVO(cursor));
        }
        return listVO;
    }

    private TimeLensesVO setLensStatusVO(Cursor cursor) {
        TimeLensesVO vo = new TimeLensesVO();
        vo.setId(cursor.getInt(cursor.getColumnIndex("id")));
        vo.setDateLeft(Utility.formatDateDefault(cursor.getString(cursor
                .getColumnIndex("date_left")), context));
        vo.setDateRight(Utility.formatDateDefault(cursor.getString(cursor
                .getColumnIndex("date_right")), context));
        vo.setExpirationLeft(cursor.getInt(cursor
                .getColumnIndex("expiration_left")));
        vo.setExpirationRight(cursor.getInt(cursor
                .getColumnIndex("expiration_right")));
        vo.setTypeLeft(cursor.getInt(cursor.getColumnIndex("type_left")));
        vo.setTypeRight(cursor.getInt(cursor.getColumnIndex("type_right")));
        vo.setInUseLeft(cursor.getInt(cursor.getColumnIndex("in_use_left")));
        vo.setInUseRight(cursor.getInt(cursor.getColumnIndex("in_use_right")));
        vo.setCountUnitLeft(cursor.getInt(cursor
                .getColumnIndex("count_unit_left")));
        vo.setCountUnitRight(cursor.getInt(cursor
                .getColumnIndex("count_unit_right")));
        vo.setNumDaysNotUsedLeft(cursor.getInt(cursor
                .getColumnIndex("num_days_not_used_left")));
        vo.setNumDaysNotUsedRight(cursor.getInt(cursor
                .getColumnIndex("num_days_not_used_right")));
        vo.setQtdLeft(cursor.getInt(cursor.getColumnIndex("qtd_left")));
        vo.setQtdRight(cursor.getInt(cursor.getColumnIndex("qtd_right")));

        return vo;
    }

    public int getLastIdLens() {
        Cursor cursor = db.rawQuery("select max(id) from " + tableName, null);

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return 0;
    }

    public TimeLensesVO getLastLens() {
        Cursor cursor = db.rawQuery("select * from " + tableName
                + " order by id desc limit 1", null);

        if (cursor.moveToFirst()) {
            return setLensStatusVO(cursor);
        }

        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public Long[] getDaysToExpire(int idLenses) {
        long daysExpLeft = 0;
        long daysExpRight = 0;

        Calendar[] calendars = getDateAlarm(idLenses);

        Calendar dateExpLeft = Calendar.getInstance();
        Calendar dateExpRight = Calendar.getInstance();

        dateExpLeft.setTime(calendars[0].getTime());
        dateExpRight.setTime(calendars[1].getTime());

        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.DAY_OF_MONTH, calendarToday.get(Calendar.DAY_OF_MONTH));
        calendarToday.set(Calendar.MONTH, calendarToday.get(Calendar.MONTH));
        calendarToday.set(Calendar.YEAR, calendarToday.get(Calendar.YEAR));
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.MILLISECOND, 0);

        daysExpLeft = dateExpLeft.getTimeInMillis() - calendarToday.getTimeInMillis();
        daysExpRight = dateExpRight.getTimeInMillis() - calendarToday.getTimeInMillis();

        return new Long[]{TimeUnit.DAYS.convert(daysExpLeft, TimeUnit.MILLISECONDS),
                TimeUnit.DAYS.convert(daysExpRight, TimeUnit.MILLISECONDS)};
    }

    @SuppressLint("SimpleDateFormat")
    public Calendar[] getDateAlarm(int id) {
        Calendar dateExpLeft = Calendar.getInstance();
        Calendar dateExpRight = Calendar.getInstance();

        String format = context.getResources().getString(R.string.locale);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        int totalDaysLeft = 0;
        int totalDaysRight = 0;

        TimeLensesVO lensVO = getById(id);
        if (lensVO != null) {
            int expirationLeft = lensVO.getExpirationLeft();
            int expirationRight = lensVO.getExpirationRight();
            int dayNotUsedLeft = lensVO.getNumDaysNotUsedLeft();
            int dayNotUsedRight = lensVO.getNumDaysNotUsedRight();

            try {
                if (lensVO.getDateLeft() != null) {
                    if (lensVO.getTypeLeft() == 0) {
                        totalDaysLeft = expirationLeft;
                    } else if (lensVO.getTypeLeft() == 1) {
                        totalDaysLeft = expirationLeft * 30;
                    } else if (lensVO.getTypeLeft() == 2) {
                        totalDaysLeft = expirationLeft * 365;
                    }
                    dateExpLeft.setTime(dateFormat.parse(lensVO.getDateLeft()));
                    int totalLeft = totalDaysLeft + dayNotUsedLeft;
                    dateExpLeft.add(Calendar.DATE, totalLeft);
                }
                if (lensVO.getDateRight() != null) {
                    if (lensVO.getTypeRight() == 0) {
                        totalDaysRight = expirationRight;
                    } else if (lensVO.getTypeRight() == 1) {
                        totalDaysRight = expirationRight * 30;
                    } else if (lensVO.getTypeRight() == 2) {
                        totalDaysRight = expirationRight * 365;
                    }

                    dateExpRight
                            .setTime(dateFormat.parse(lensVO.getDateRight()));
                    int totalRight = totalDaysRight + dayNotUsedRight;
                    dateExpRight.add(Calendar.DATE, totalRight);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return new Calendar[]{dateExpLeft, dateExpRight};
    }

    public int[] getUnitsRemaining() {
        int unitsLeft = 0;
        int unitsRight = 0;

        if (ListReplaceLensFragment.listLenses == null) {
            TimeLensesVO lastLens = TimeLensesDAO.getInstance(context).getLastLens();
            if (lastLens != null) {
                unitsLeft = lastLens.getQtdLeft();
                unitsRight = lastLens.getQtdRight();
            }
        } else{
            unitsLeft =  ListReplaceLensFragment.listLenses.get(0).getQtdLeft();
            unitsRight = ListReplaceLensFragment.listLenses.get(0).getQtdRight();
        }

//        int unitsLeft = ListReplaceLensFragment.listLenses == null ? TimeLensesDAO.getInstance(
//                context).getLastLens().getQtdLeft() : ListReplaceLensFragment.listLenses
//                .get(0).getQtdLeft();
//
//        int unitsRight = ListReplaceLensFragment.listLenses == null ? TimeLensesDAO.getInstance(
//                context).getLastLens().getQtdRight() : ListReplaceLensFragment.listLenses
//                .get(0).getQtdRight();

        return new int[]{unitsLeft, unitsRight};
    }

    @SuppressLint("SimpleDateFormat")
    public void save(TimeLensesVO timeLensesVO) {
        TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);
        AlarmDAO alarmDAO = AlarmDAO.getInstance(context);

        int idLens = timeLensesVO.getId() == null ? 0 : timeLensesVO.getId();
        if (idLens != 0) {
            if (!timeLensesVO.equals(timeLensesDAO.getById(idLens))) {
                if (timeLensesDAO.update(timeLensesVO)) {
                    alarmDAO.setAlarm(idLens);
                }
            }
        } else {
            if (timeLensesDAO.insert(timeLensesVO)) {
                alarmDAO.setAlarm(idLens);
            }
        }
    }
}
