package com.aeo.mylenses.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aeo.mylenses.dao.AlarmDAO;
import com.aeo.mylenses.dao.TimeLensesDAO;
import com.aeo.mylenses.fragment.ListReplaceLensFragment;
import com.aeo.mylenses.vo.AlarmVO;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

			int idLenses = ListReplaceLensFragment.listLenses == null ? TimeLensesDAO.getInstance(
					context).getLastIdLens() : ListReplaceLensFragment.listLenses
					.get(0).getId();

			AlarmDAO alarmDAO = AlarmDAO.getInstance(context);
			alarmDAO.setAlarm(idLenses);
			
			//Daily notification
			AlarmVO alarmVO = alarmDAO.getAlarm();
			TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);
			if (alarmVO.getRemindEveryDay() == 1) {
				Long[] daysToExpire = timeLensesDAO.getDaysToExpire(idLenses);

				if (daysToExpire[0] > 0 || daysToExpire[1] > 0) {
					alarmDAO.setAlarmManagerDaily(alarmVO.getHour(), alarmVO.getMinute());
				}
			}
		}
	}
}
