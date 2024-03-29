/*
 * Copyright (C) 2013-2019 Federico Iosue (federico@iosue.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.feio.android.omninotes.helpers.date;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Time;
import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrence;
import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrenceFormatter;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker.RecurrenceOption;
import it.feio.android.omninotes.OmniNotes;
import it.feio.android.omninotes.R;
import it.feio.android.omninotes.helpers.LogDelegate;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.property.RRule;

public class RecurrenceHelper {

  public static String formatRecurrence (Context mContext, String recurrenceRule) {
    if (!TextUtils.isEmpty(recurrenceRule)) {
      EventRecurrence recurrenceEvent = new EventRecurrence();
      recurrenceEvent.setStartDate(new Time("" + new Date().getTime()));
      recurrenceEvent.parse(recurrenceRule);
      return EventRecurrenceFormatter.getRepeatString(mContext.getApplicationContext(),
          mContext.getResources(), recurrenceEvent, true);
    } else {
      return "";
    }
  }

  public static Long nextReminderFromRecurrenceRule (long reminder, String recurrenceRule) {
    return nextReminderFromRecurrenceRule(reminder, Calendar.getInstance().getTimeInMillis(), recurrenceRule);
  }

  public static Long nextReminderFromRecurrenceRule (long reminder, long currentTime, String recurrenceRule) {
    RRule rule = new RRule();
    try {
      rule.setValue(recurrenceRule);
      long startTimestamp = reminder + 60 * 1000;
      if (startTimestamp < currentTime) {
        startTimestamp = currentTime;
      }
      Date nextDate = rule.getRecur().getNextDate(new DateTime(reminder), new DateTime(startTimestamp));
      return nextDate == null ? 0L : nextDate.getTime();
    } catch (ParseException e) {
      LogDelegate.e("Error parsing rrule");
    }
    return 0L;
  }

  public static String getNoteReminderText (long reminder) {
    return OmniNotes.getAppContext().getString(R.string.alarm_set_on) + " " + DateHelper.getDateTimeShort(OmniNotes
        .getAppContext(), reminder);
  }

  public static String getNoteRecurrentReminderText (long reminder, String rrule) {
    return formatRecurrence(OmniNotes.getAppContext(), rrule) + " " + OmniNotes.getAppContext().getString
        (R.string.starting_from) + " " + DateHelper.getDateTimeShort(OmniNotes.getAppContext(), reminder);
  }

  public static String buildRecurrenceRuleByRecurrenceOptionAndRule (RecurrenceOption recurrenceOption,
      String recurrenceRule) {
    if (recurrenceRule == null) {
      Frequency freq = Frequency.valueOf(recurrenceOption.toString());
      Recur recur = new Recur(freq, new DateTime());
      return new RRule(recur).toString().replace("RRULE:", "");
    }
    return recurrenceRule;
  }

}
