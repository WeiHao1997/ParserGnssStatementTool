import org.jfree.chart.util.Args;
import org.jfree.data.time.*;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class PeriodUpdateMillisecond extends Millisecond implements Serializable{


    private static final long serialVersionUID = 1L;

    private int periodTimeMs;

    public PeriodUpdateMillisecond(int periodMs, int millisecond, Second second){
        super(millisecond, second);
        this.periodTimeMs = periodMs;
    }

    public PeriodUpdateMillisecond(int millisecond, int second, int minute, int hour,
                                   int day, int month, int year,int periodTimeMs){
        super(millisecond, new Second(second, minute, hour, day, month, year));
        this.periodTimeMs = periodTimeMs;
    }

    @Override
    public RegularTimePeriod next() {

        RegularTimePeriod result = null;
        if (getMillisecond() + periodTimeMs <= LAST_MILLISECOND_IN_SECOND) {
            result = new PeriodUpdateMillisecond(periodTimeMs, (int) (getMillisecond() + periodTimeMs), getSecond());
        }
        else {
            Second next = (Second) getSecond().next();
            if (next != null) {
                result = new PeriodUpdateMillisecond(periodTimeMs, (int) (getMillisecond() + periodTimeMs - LAST_MILLISECOND_IN_SECOND - 1), next);
            }
        }
        return result;
    }

}
