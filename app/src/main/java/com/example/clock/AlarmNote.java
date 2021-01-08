package com.example.clock;

import java.io.Serializable;
import java.util.Calendar;

public class AlarmNote implements Serializable {

    protected boolean repeatable;
    Calendar calendar;

    public AlarmNote(int day_of_week, int hour, int minute, boolean repeatable){
        this.calendar = Calendar.getInstance();

        this.calendar.set(Calendar.DAY_OF_WEEK, day_of_week);
        this.calendar.set(Calendar.HOUR_OF_DAY, hour);
        this.calendar.set(Calendar.MINUTE, minute);

        this.repeatable = repeatable;
    }

    public AlarmNote(int year, int month, int day_of_month, int hour,
                                                int minute, boolean repeatable){
        this.calendar = Calendar.getInstance();

        this.calendar.set(Calendar.YEAR, year);
        this.calendar.set(Calendar.MONTH, month);
        this.calendar.set(Calendar.DAY_OF_MONTH, day_of_month);
        this.calendar.set(Calendar.HOUR_OF_DAY, hour);
        this.calendar.set(Calendar.MINUTE, minute);

        this.repeatable = repeatable;
    }

    public AlarmNote(Calendar calendar){
        this.calendar = calendar;

        this.repeatable = false;
    }


    public boolean isRepeatable(){
        return this.repeatable;
    }
    public void setRepeatable(boolean repeatable){
        this.repeatable = repeatable;
    }

}
