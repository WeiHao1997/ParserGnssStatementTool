import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;

public class PeriodUpdateMillisecond extends Millisecond {

    private int periodStepSize;

    PeriodUpdateMillisecond(int millisecond, int second, int minute, int hour,
                            int day, int month, int year){

        super(millisecond, second, minute, hour, day, month, year);

    }

    @Override
    public RegularTimePeriod next() {
        RegularTimePeriod result = null;
        if (getMillisecond() != LAST_MILLISECOND_IN_SECOND) {
            result = new Millisecond((int) (getMillisecond() + 1), getSecond());
        } else {
            Second next = (Second) getSecond().next();
            if (next != null) {
                result = new Millisecond(FIRST_MILLISECOND_IN_SECOND, next);
            }
        }
        return result;
    }
}
