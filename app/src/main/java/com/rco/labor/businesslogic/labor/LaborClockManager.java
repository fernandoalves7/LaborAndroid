package com.rco.labor.businesslogic.labor;

import android.util.Log;

import com.rco.labor.businesslogic.BusinessRules;
import com.rco.labor.businesslogic.rms.User;
import com.rco.labor.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Fernando on 10/21/2018.
 * This class is a business layer related class, specialized in Labor Clock functionality.
 */
public class LaborClockManager {
    private User user;

    private Float overTimeStartMills;
    private Float doubleOvertimeStartMills;

    private ArrayList<ClockItem> clockItems;
    private ArrayList<BreakItem> breakItems;

    private String currentLaborStatus = "Off";
    private Calendar creationDateTime;

    public LaborClockManager(User user, float overtimeStart, float doubleOvertimeStart) {
        this.user = user;
        this.overTimeStartMills = overtimeStart * 1000;
        this.doubleOvertimeStartMills = doubleOvertimeStart * 1000;

        clockItems = new ArrayList<>();
        breakItems = new ArrayList<>();

        creationDateTime = Calendar.getInstance();
    }

    // Getters and setters

    public User getUser() {
        return user;
    }

    public String getEmployeeId() {
        return user.getEmployeeId();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    // Clock management

    public boolean isCurrentStatus(String value) {
        return currentLaborStatus != null && currentLaborStatus.equalsIgnoreCase(value);
    }

    public boolean existsPendingHours() {
        return getTimecardTotalMills() > 0 || getTodayBreakTotalMills() > 0;
    }

    public String getCurrentStatus() {
        return currentLaborStatus;
    }

    public void setCurrentStatus(String value) {
        currentLaborStatus = value;
    }

    public void clockIn() {
        if (isCurrentStatus("Clock-In"))
            return;

        if (isCurrentStatus("StartBreak"))
            endBreak();

        clockItems.add(new ClockItem());
        getLatestClockItem().clockIn();
        setCurrentStatus("Clock-In");
    }

    public void clockOut() {
        if (isCurrentStatus("Clock-Out"))
            return;

        ClockItem clockItem = getLatestClockItem();

        if (clockItem == null)
            return;

        clockItem.clockOut();
        setCurrentStatus("Clock-Out");
    }

    public void startBreak() {
        if (isCurrentStatus("StartBreak"))
            return;

        clockOut();

        breakItems.add(new BreakItem());
        getLatestBreakItem().startBreak();
        setCurrentStatus("StartBreak");
    }

    public void endBreak() {
        if (isCurrentStatus("EndBreak"))
            return;

        BreakItem breakItem = getLatestBreakItem();

        if (breakItem == null)
            return;

        breakItem.endBreak();
        setCurrentStatus("EndBreak");
    }

    private ClockItem getLatestClockItem() {
        return clockItems.get(clockItems.size()-1);
    }

    private BreakItem getLatestBreakItem() {
        return breakItems.get(breakItems.size()-1);
    }

    public ArrayList<ClockItem> getClockItems() {
        return clockItems;
    }

    public ArrayList<BreakItem> getBreakItems() {
        return breakItems;
    }

    // Helper methods

    public float getTimecardTotalMills() {
        if (clockItems == null || clockItems.size() == 0)
            return 0;

        float result = 0;

        for (ClockItem item : clockItems)
            if (item != null)
                result += item.getMillisDifference();

        return result;
    }

    public String getTimecardTotalHoursStr() {
        return getFormattedHoursStr(getTimecardTotalMills() / 1000);
    }

    public float getTodayHoursMills() {
        // Today Hours: clock-out - clock-in if <= TimecardStraightTimeHours otherwise TimecardStraightTimeHours

        if (clockItems == null || clockItems.size() == 0)
            return 0;

        float result = 0;
        String nowYyyyMmDdStr = com.rco.labor.utils.DateUtils.getYyyyMmDdStr(Calendar.getInstance().getTime());

        for (ClockItem item : clockItems)
            if (item != null)
                if (nowYyyyMmDdStr.equalsIgnoreCase(item.getStartDateStr()))
                    result += item.getMillisDifference();

        return result;
    }

    public String getTodayHoursStr() {
        return getFormattedHoursStr(getTodayHoursMills() / 1000);
    }

    public float getTodayOvertimeMills() {
        if (overTimeStartMills == null)
            return 0;

        if (getTimecardTotalMills() < overTimeStartMills)
            return 0;

        return getTimecardTotalMills() - overTimeStartMills;
    }

    public String getTodayOvertimeStr() {
        return getFormattedHoursStr(getTodayOvertimeMills() / 1000);
    }

    public float getTodayOvertimeDoubleMills() {
        if (doubleOvertimeStartMills == null)
            return 0;

        if (getTimecardTotalMills() < doubleOvertimeStartMills)
            return 0;

        return getTimecardTotalMills() - doubleOvertimeStartMills;
    }

    public String getTodayOvertimeDoubleStr() {
        return getFormattedHoursStr(getTodayOvertimeDoubleMills() / 1000);
    }

    public float getTodayBreakTotalMills() {
        if (breakItems == null || breakItems.size() == 0)
            return 0;

        float result = 0;

        for (BreakItem item : breakItems)
            if (item != null)
                result += item.getMillisDifference();

        return result;
    }

    public String getTodayBreakTotalHoursStr() {
        return getFormattedHoursStr(getTodayBreakTotalMills() / 1000);
    }

    private static String getFormattedHoursStr(float hours) {
        return String.format("%.2f", hours);
    }

    public Date getCreationDateTime() {
        return creationDateTime.getTime();
    }

    public String getCreationDateTimeStr() {
        return DateUtils.getYyyyMmDdHhmmssStr(getCreationDateTime());
    }

    // Helper classes

    public class ClockItem {
        private Calendar clockIn;
        private Calendar clockOut;

        public ClockItem() {
            clockIn = null;
            clockOut = null;
        }

        public boolean isClockedOut() {
            return clockOut != null;
        }

        public void clockIn() {
            clockIn = Calendar.getInstance();
        }

        public void clockOut() {
            clockOut = Calendar.getInstance();
        }

        public long getMillisDifference() {
            if (clockIn == null || clockOut == null)
                return 0;

            return clockOut.getTimeInMillis() - clockIn.getTimeInMillis();
        }

        public String getHoursDifferenceStr() {
            long secs = getMillisDifference() / 1000;
            return getFormattedHoursStr(secs);
        }

        public Date getStartDateTime() {
            return clockIn.getTime();
        }

        public String getStartDateStr() {
            return com.rco.labor.utils.DateUtils.getYyyyMmDdStr(getStartDateTime());
        }

        public String getStartTimeStr() {
            return com.rco.labor.utils.DateUtils.getHhmmssStr(getStartDateTime());
        }

        public Date getEndDateTime() {
            return clockOut.getTime();
        }

        public String getEndDateTimeStr() {
            return com.rco.labor.utils.DateUtils.getYyyyMmDdHhmmssStr(getEndDateTime());
        }

        public String getEndDateStr() {
            return com.rco.labor.utils.DateUtils.getYyyyMmDdStr(getEndDateTime());
        }

        public String getEndTimeStr() {
            return com.rco.labor.utils.DateUtils.getHhmmssStr(getEndDateTime());
        }

        public float getOvertimeMills() {
            if (overTimeStartMills == null)
                return 0;

            if (getMillisDifference() < overTimeStartMills)
                return 0;

            return getMillisDifference() - overTimeStartMills;
        }

        public String getOvertimeStr() {
            return getFormattedHoursStr(getOvertimeMills() / 1000);
        }

        public float getOvertimeDoubleMills() {
            if (doubleOvertimeStartMills == null)
                return 0;

            if (getMillisDifference() < doubleOvertimeStartMills)
                return 0;

            return getMillisDifference() - doubleOvertimeStartMills;
        }

        public String getOvertimeDoubleStr() {
            return getFormattedHoursStr(getOvertimeDoubleMills() / 1000);
        }
    }

    public class BreakItem {
        private Calendar breakStart;
        private Calendar breakEnd;

        public BreakItem() {
            breakStart = null;
            breakEnd = null;
        }

        public void startBreak() {
            breakStart = Calendar.getInstance();
        }

        public void endBreak() {
            breakEnd = Calendar.getInstance();
        }

        public long getMillisDifference() {
            if (breakStart == null || breakEnd == null)
                return 0;

            return breakEnd.getTimeInMillis() - breakStart.getTimeInMillis();
        }

        public String getHoursDifferenceStr() {
            long secs = getMillisDifference() / 1000;
            return getFormattedHoursStr(secs);
        }

        public Date getStartDateTime() {
            return breakStart.getTime();
        }

        public String getStartDateStr() {
            return com.rco.labor.utils.DateUtils.getYyyyMmDdStr(getStartDateTime());
        }

        public String getStartTimeStr() {
            return com.rco.labor.utils.DateUtils.getHhmmssStr(getStartDateTime());
        }

        public Date getEndDateTime() {
            return breakEnd.getTime();
        }

        public String getEndDateTimeStr() {
            return com.rco.labor.utils.DateUtils.getYyyyMmDdHhmmssStr(getEndDateTime());
        }

        public String getEndDateStr() {
            return com.rco.labor.utils.DateUtils.getYyyyMmDdStr(getEndDateTime());
        }

        public String getEndTimeStr() {
            return com.rco.labor.utils.DateUtils.getHhmmssStr(getEndDateTime());
        }
    }
}
