import java.util.ArrayList;

public class CalculateUtils {

    /**
     * 获取PQTMIMU语句中当前速度值
     * @param wheelTick 获取的WheelTick
     * @param timeStamp 获取的时间戳
     * @param dataLength 数据个数
     * @param precisionValue 精度值
     * @return List
     */

    public static ArrayList<Double> CalculateIMUGetSpeed(ArrayList<Integer> wheelTick, ArrayList<Integer> timeStamp, int dataLength, double precisionValue){

        ArrayList<Double> arrayListWheelSpeed = new ArrayList<>();
        double tempWheelSpeed;

        double wheelTickDiscrepancy = 0;
        double timeStampDiscrepancy = 0;

        for (int i = dataLength - 1; i > 0; --i) {

            wheelTickDiscrepancy = wheelTick.get(i) - wheelTick.get(i - 1);

            timeStampDiscrepancy = timeStamp.get(i) - timeStamp.get(i - 1);

            if(timeStampDiscrepancy == 0) {
                arrayListWheelSpeed.add(0.0);
                continue;
            }
            tempWheelSpeed = (wheelTickDiscrepancy  /  timeStampDiscrepancy) * 1000 * precisionValue;

            arrayListWheelSpeed.add(tempWheelSpeed);
        }
        return arrayListWheelSpeed;
    }
}
