import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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


    static class Point{
        double Latitude;   // 纬度
        double Longitude;  // 经度
    }


    public static ArrayList<Double> arrayListCEP = new ArrayList<>();

    public static void CalculateCEP50(ArrayList<Point> listPoint, Point realPoint){

        double CEP = 0;

        for (int i = 0; i < listPoint.size(); i++) {

            double LatitudeConvert = Math.round(listPoint.get(i).Latitude / 100) + (listPoint.get(i).Latitude  - Math.round(listPoint.get(i).Latitude / 100) * 100) / 60;
            double LongitudeConvert = Math.round(listPoint.get(i).Longitude / 100) + (listPoint.get(i).Longitude  - Math.round(listPoint.get(i).Longitude / 100) * 100) / 60 ;

//            System.out.println("LatitudeConvert"+LatitudeConvert);
//            System.out.println("LongitudeConvert"+ LongitudeConvert);

            CEP =  Math.sqrt(Math.pow(Math.cos(realPoint.Latitude * Math.PI / 180) * (LongitudeConvert * Math.PI / 180 - realPoint.Longitude * Math.PI / 180), 2) +
                    Math.pow((LatitudeConvert * Math.PI / 180 - realPoint.Latitude * Math.PI / 180) , 2));

            CEP *= 6371004;

//            System.out.printf("%f10",CEP);
//            System.out.println();

            arrayListCEP.add(CEP);
        }

        Collections.sort(arrayListCEP);   // 排序

        System.out.println(arrayListCEP.get(arrayListCEP.size() / 100 * 50));

        System.out.println(arrayListCEP.get(arrayListCEP.size() / 100 * 68));

        System.out.println(arrayListCEP.get(arrayListCEP.size() / 100 * 95));
    }

    public static void main(String[] args) {

        ArrayList<Point> fixPoint = new ArrayList<>();

        Point a = new Point();
        a.Latitude = 3149.297901;
        a.Longitude = 11706.919326;

        fixPoint.add(a);

        Point real = new Point();
        real.Longitude = 117.11635553333333;
        real.Latitude = 31.82207612222222;

        CalculateCEP50(fixPoint, real);
        System.out.println();
    }
}
