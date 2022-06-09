import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final double R_A = 6378137.0;

    private static final int ARRAYS_LIST_DATA_LENGTH = 1024 * 2;

    private static final byte BCM_HEAD_0xB5 = (byte) 0xB5;
    private static final byte BCM_HEAD_0x62 = (byte) 0x62;
    private static final byte NMEA_HEAD_0x24 = (byte) 0x24;
    private static final byte RTCM_HEAD_0xD3 = (byte) 0xD3;


    static ArrayList<Byte> arrayListBCM_MSG = new ArrayList<>();



    public static void main(String[] args) throws Exception{


        File file = new File("C:\\Users\\kahn.wei\\Desktop\\Broadcom\\ISSUE-SCY\\OverSea\\20220608-C29DA-BETA0530-OVERSEA\\LC29D-0607_165046_COM11 Without corrections.log");
       // File file = new File("C:\\Users\\kahn.wei\\Desktop\\test.txt");

        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));

        int count = 0;

        while (bufferedInputStream.available() > 0){
            int bufferRead_1 = 0;
            int bufferRead_2 = 0;
            if((bufferRead_1 = bufferedInputStream.read()) != -1){

                if(BCM_HEAD_0xB5 == (byte) bufferRead_1){
                    if((bufferRead_2= bufferedInputStream.read()) != -1){
                        if((BCM_HEAD_0xB5 == (byte) bufferRead_1) && (BCM_HEAD_0x62 == (byte) bufferRead_2)){
                            BreamMsgHandle.spliceGnssBreamStatement(bufferedInputStream);
                        }
                    }

                } else if(NMEA_HEAD_0x24 == (byte) bufferRead_1){

                    NMEAMsgHandle.spliceGnssNMEAStatement(bufferedInputStream);

                } else if(RTCM_HEAD_0xD3 == (byte) bufferRead_1){


                }else {

                }
            }
        }


//            final DataTimeSeries_AWT demo = new DataTimeSeries_AWT( "WheelSpeed", NMEAMsgHandle.arrayListWheelTickCount , NMEAMsgHandle.arrayListWheelTickCount);
//            demo.pack();
//            demo.setVisible( true );

//        ArrayList<Double> arrayList = CalculateUtils.CalculateIMUGetSpeed(NMEAMsgHandle.arrayListWheelTick,
//                NMEAMsgHandle.arrayListTimeTick,
//                NMEAMsgHandle.arrayListTimeTick.size(),
//                0.04);
//
//        if(arrayList.size() > 0){
//            final DataTimeSeries_AWT demo = new DataTimeSeries_AWT( "WheelSpeed",  arrayList,arrayList,"double");
//            demo.pack();
//            demo.setVisible( true );
//        }


//        System.out.println(NMEAMsgHandle.GPS_GSV_LIST.size());
//
        if(NMEAMsgHandle.points.size() > 0){
            CalculateUtils.Point real = new CalculateUtils.Point();
//        real.Longitude = 117.11635553333333;
//        real.Latitude = 31.82207612222222;
            real.Latitude = 44.82381250;
            real.Longitude = 20.41597023;

            CalculateUtils.CalculateCEP50(NMEAMsgHandle.points,real);

            if(CalculateUtils.arrayListCEP.size() > 0){

                final DataTimeSeries_AWT demo = new DataTimeSeries_AWT( "WheelSpeed",  CalculateUtils.arrayListCEP, NMEAMsgHandle.arrayListUseSVCount,"double");
                demo.pack();
                demo.setVisible( true );
            }
        }




//        while (bufferedInputStream.available() > 0){
//
//            if((bufferedInputStream.available() - 6) >= 0){
//                byte[] BCM_MSG_HEAD = new byte[2];
//                if(bufferedInputStream.read(BCM_MSG_HEAD,0,2) != -1){
//
//                    if((BCM_HEAD_0xB5 == BCM_MSG_HEAD[0]) && (BCM_HEAD_0x62 == BCM_MSG_HEAD[1])){
//                        arrayListBCM_MSG.add(BCM_MSG_HEAD[0]);
//                        arrayListBCM_MSG.add(BCM_MSG_HEAD[1]);
//
//                        byte [] BCM_MSG_GROUP_ID = new byte[2];
//                        if(bufferedInputStream.read(BCM_MSG_GROUP_ID,0,2) != -1){
//                            arrayListBCM_MSG.add(BCM_MSG_GROUP_ID[0]);
//                            arrayListBCM_MSG.add(BCM_MSG_GROUP_ID[1]);
//                        }
//
//                        byte [] BCM_MSG_DATA_LENGTH = new byte[2];
//                        if(bufferedInputStream.read(BCM_MSG_DATA_LENGTH,0,2) != -1){
//                            arrayListBCM_MSG.add(BCM_MSG_DATA_LENGTH[0]);
//                            arrayListBCM_MSG.add(BCM_MSG_DATA_LENGTH[1]);
//
//                            // 进行无效数据的清除
////                            if(BCM_INVALID_DATA_LENGTH == BCM_MSG_DATA_LENGTH[1]){
////                                arrayListBCM_MSG.clear();
////                            }
//                        }
//
////                        for (byte b: arrayListBCM_MSG) {
////                            System.out.printf("%2X ",b);
////                        }
//
//                        if(arrayListBCM_MSG.size() > 0){
//
//                            int BCM_Data_Length = 0;
//
//                            BCM_Data_Length = BCM_MSG_DATA_LENGTH[0] & 0xFF | ((BCM_MSG_DATA_LENGTH[1] << 8) & 0xFF00);
//
//                            if(bufferedInputStream.available() - BCM_Data_Length > 0){
//
//                                byte[] byteContent = new byte[BCM_Data_Length];
//
//                                if(bufferedInputStream.read(byteContent, 0 ,BCM_Data_Length) != -1){
//
//                                    for(int i = 0; i <  BCM_Data_Length; i++){
//                                        arrayListBCM_MSG.add(byteContent[i]);
//                                    }
//                                }
//                            }
//                          //  System.out.println(BCM_Data_Length);
//                        }
//
//                        if(bufferedInputStream.available() - 2 > 0){
//                            byte[] byteCrcVerify = new byte[2];
//                            if(bufferedInputStream.read(byteCrcVerify, 0 ,2) != -1){
//                                arrayListBCM_MSG.add(byteCrcVerify[0]);
//                                arrayListBCM_MSG.add(byteCrcVerify[1]);
//                            }
//                        }
//
////                            for (byte b: arrayListBCM_MSG) {
////                                System.out.printf("%02X ",b);
////                            }
//
//                       // System.out.printf("checksum[0]:%02X,checksum[1]:%02X\r\n",arrayListBCM_MSG.get(arrayListBCM_MSG.size() - 2) ,arrayListBCM_MSG.get(arrayListBCM_MSG.size() - 1));
//                        count++;
//                        //System.out.println(arrayListBCM_MSG.size());
//                        BreamMsgHandle.judgeBreamStatementStartLength(arrayListBCM_MSG);
//                        arrayListBCM_MSG.clear();
//
//                    }
//
//                }
//
//            }else {
//                /* 步长小于6 */
//
//                bufferedInputStream.read();
//            }
//        }
//
//        System.out.println(count);

/* ------------------------------------------- TODO -------------------------------------------*/
//        File file = new File("C:\\Users\\kahn.wei\\Desktop\\BRM-MEAS.txt");
//
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//
//        String readStr = null;
//
//        while ((readStr = bufferedReader.readLine()) != null){
//
//            String[] ArrayString = readStr.trim().split(" ");
//
//            for(int i = 0; i < ArrayString.length; i++){
//                arrayListBCM_MSG.add(hexStringToHexByte(ArrayString[i]));
//            }
//
//
//        }
//
//        System.out.println(arrayListBCM_MSG.size());
//
//        BreamMsgHandle.judgeBreamStatementStartLength(arrayListBCM_MSG);
    }



//    public static void main(String[] args) throws Exception{
//
//        String fileAddress = "C:\\Users\\kahn.wei\\Desktop\\bream_binary.bin";
//
//        File file = new File(fileAddress);
//
//        FileInputStream fileInputStream = new FileInputStream(file);
//
//        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
//
//        final int BUFFER_SIZE = 1024 * 2;
//
//        byte[] buffer = new byte[BUFFER_SIZE];
//
//        int length = 0;
//
//        long timerStart = System.currentTimeMillis();
//
//        int circleCount = 0;
//
//        List<Byte> arrayListByte = new ArrayList<>();
//
//        while ((length = bufferedInputStream.read(buffer)) != -1){
//
//            circleCount++;
//            int i;
//            byte Group = 0;
//            byte MessageNumber = 0;
//
//            for(i = 0; i < buffer.length - 1; i++){
//
//                if((buffer[i] == (byte)0xB5) && (buffer[i + 1] == (byte)0x62)){
//                    arrayListByte.add(buffer[i]);
//                    arrayListByte.add(buffer[i + 1]);
//
//                    if((i + 2) >= buffer.length){
//                        return;
//                    }else {
//                        i += 2;
//                    }
//
//                    Group = buffer[i];
//                    arrayListByte.add(buffer[i]);
//
//                    switch (Group){
//                        case (byte) 0x02:
//                            MessageNumber =  buffer[i + 1];
//                            arrayListByte.add(buffer[i + 1]);
//
//                            switch (MessageNumber){
//                                case (byte) 0x15:
//                                    if((i + 2) >= buffer.length){
//                                        return;
//                                    }else {
//                                        i += 2;
//                                    }
//
//                                    int contentLength = 0;
//
//                                    contentLength = buffer[i] + (buffer[i + 1] << 8);
//
//                                    for(; i <= contentLength + 5; i++){
//                                        arrayListByte.add(buffer[i]);
//                                    }
//
//                                    arrayListByte.add(buffer[i]);
//                                    i++;
//                                    arrayListByte.add(buffer[i]);
//
//                                    System.out.println("arrayListByte.size: " + arrayListByte.size());
//                                    System.out.println("contentLength: " + contentLength);
//
//                                   byte[] checksum =  BreamMsgHandle.BreamMSGCheckSum(arrayListByte);
//
//                                    System.out.println();
//                                   if((checksum[0] == arrayListByte.get(arrayListByte.size() - 2)) && checksum[1] ==  arrayListByte.get(arrayListByte.size() - 1)){
//
//                                       System.out.println("success!");
//                                   }
//
//                                    break;
//
//                                default:
//                                    break;
//                            }
//                            break;
//
//                        default:
//                            break;
//                    }
//
//                }
//
//
//            }
//        }
//
//        long timerEnd = System.currentTimeMillis();
//
//        System.out.println();
//        for (byte b: arrayListByte
//             ) {
//
//            System.out.printf("%X ", b);
//        }
//        System.out.println();
//        System.out.println("Time: " + (timerEnd - timerStart));
//
//    //    FileDataHandle fileDataHandle= new FileDataHandle();
//
//       // fileDataHandle.fileDeal("C:\\Users\\kahn.wei\\Desktop\\test.txt");
//      //  Process process = Runtime.getRuntime().exec(new String[]{ });
//
//
//       // String str_matting  = "[]";
//
//       // System.out.println(str_matting.matches("\\[\\]"));
//
//       // SerialPortHelp serialPortHelp = new SerialPortHelp();
//       // serialPortHelp.getSerialPortList();
//
//    }



    double Ql_GetDistance(double lat1, double long1, double lat2, double long2)
    {
        double radLat1 = 0.0;
        double radLat2 = 0.0;
        double a = 0.0;
        double b = 0.0;
        double s = 0.0;
        radLat1 = Ql_Rad_Calculate(lat1);
        radLat2 = Ql_Rad_Calculate(lat2);
        a = radLat1 - radLat2;
        b = Ql_Rad_Calculate(long1) - Ql_Rad_Calculate(long2);
        s = 2 * Math.asin( Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b/2),2)) );
        s = s * R_A;
        return s;
    }
    double Ql_Rad_Calculate(double d) {
        return (d*Math.PI/180.0);
    }

    public static byte[] BreamMSGCheckSum(List<Byte> byteList){

        byte[] checksum = new byte[2];

        for(int i = 2; i < byteList.size() - 2; i++ ){
            checksum[0] = (byte) (checksum[0] + byteList.get(i));
            checksum[1] = (byte) (checksum[1] + checksum[0]);
        }

        System.out.printf("checksum[0]: %X checksum[1]: %X", checksum[0], checksum[1]);

        return checksum;
    }

    public static byte hexStringToHexByte(String hexString){

        byte reByte = 0;

        hexString = hexString.toUpperCase();

        char [] bytes = hexString.toCharArray();

        reByte = (byte) (charToByte(bytes[0]) << 4 | charToByte(bytes[1]));

        return reByte;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}


