package com.kwover.eventcheck_in.util;

import android.content.Context;

/**
 * Created by keithwong on 10/31/18.
 */

public interface ActivityCallbackInterface {
    void onSyncFinishOk();
    void onSyncFinishBad();
    void onUpdateFinished(Boolean finishedOk);
}
