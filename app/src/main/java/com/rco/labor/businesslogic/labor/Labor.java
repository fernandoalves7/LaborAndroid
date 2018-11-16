package com.rco.labor.businesslogic.labor;

import com.rco.labor.businesslogic.PairList;

/**
 * Created by Fernando on 10/23/2018.
 */

public class Labor extends com.rco.labor.businesslogic.rms.User {
    private LaborClockManager laborClockManager;
    private final int overtimeStart = 8;
    private final int doubleOvertimeStart = 12;

    public Labor(PairList pairList) {
        setFromList(pairList);
    }

    public LaborClockManager getClockManager() {
        if (laborClockManager == null)
            laborClockManager = new LaborClockManager(this, overtimeStart, doubleOvertimeStart);

        return laborClockManager;
    }

    public void clearClockManager() {
        laborClockManager = new LaborClockManager(this, overtimeStart, doubleOvertimeStart);
    }
}
