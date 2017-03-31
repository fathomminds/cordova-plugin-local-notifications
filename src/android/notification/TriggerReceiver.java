/*
 * Copyright (c) 2013-2015 by appPlant UG. All rights reserved.
 *
 * @APPPLANT_LICENSE_HEADER_START@
 *
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apache License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://opensource.org/licenses/Apache-2.0/ and read it before using this
 * file.
 *
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 *
 * @APPPLANT_LICENSE_HEADER_END@
 */

package de.appplant.cordova.plugin.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * The alarm receiver is triggered when a scheduled alarm is fired. This class
 * reads the information in the intent and displays this information in the
 * Android notification bar. The notification uses the default notification
 * sound and it vibrates the phone.
 */
public class TriggerReceiver extends AbstractTriggerReceiver {

    /**
     * Called when a local notification was triggered. Does present the local
     * notification and re-schedule the alarm if necessary.
     *
     * @param notification
     *      Wrapper around the local notification
     * @param updated
     *      If an update has triggered or the original
     */
    @Override
    public void onTrigger (Notification notification, boolean updated) {
        Boolean isExactRepeater = notification.getOptions().isExactRepeater();
        if (isExactRepeater) {
            Integer id = notification.getOptions().getId();
            long interval = notification.getOptions().getRepeatInterval();
            long triggerTime = System.currentTimeMillis() + interval;
            Context context = notification.getContext();
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // Use RepeaterReceiver
            //receiver = RepeaterReceiver.class;
            Intent intent = new Intent(context, TriggerReceiver.class)
                    .setAction(notification.getOptions().getIdStr())
                    .putExtra(Options.EXTRA, notification.getOptions().toString());

            PendingIntent pi = PendingIntent.getBroadcast(
                    context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            // Set an ExactAndAllowWhileIdle schedule with the RepeaterReceiver
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pi);
            }
            else if (android.os.Build.VERSION.SDK_INT >= 19) {
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pi);
            }
            else
            {
                alarmMgr.set(AlarmManager.RTC_WAKEUP, triggerTime, pi);
            }
        }
        notification.show();
    }

    /**
     * Build notification specified by options.
     *
     * @param builder
     *      Notification builder
     */
    @Override
    public Notification buildNotification (Builder builder) {
        return builder.build();
    }

}
