import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;



public class NMEAMsgHandle {

    private static final byte TOOL_NMEA_HEAD = (byte) 0x24;
    private static final byte TOOL_NMEA_TAIL_1 = (byte) 0x0D;
    private static final byte TOOL_NMEA_TAIL_2 = (byte) 0x0A;
    private static final byte TOOL_SIGNAL_COMMA = (byte) 0x2C;
    private static final byte TOOL_SIGNAL_ASTERISK = (byte) 0x2A;

    private static final String TOOL_NMEA_GGA = "GGA";
    private static final int TOOOL_NMEA_GNGGA_PARA_COUNT = 14;

    private static final String TOOL_NMEA_PQTMIMU = "PQTMIMU";
    private static final int TOOL_NMEA_PQTMIMU_PARA_COUNT = 9;

    public static ArrayList<Integer> arrayListWheelTick = new ArrayList<>();
    public static ArrayList<Integer> arrayListTimeTick = new ArrayList<>();


    public static void spliceGnssNMEAStatement(BufferedInputStream bufferedInputStream) throws IOException {
        byte buffer;
        StringBuilder stringBuilderTalk = new StringBuilder();
        NMEA_DATA_MSG nmea_data_msg = new NMEA_DATA_MSG();
        while ((buffer = (byte) bufferedInputStream.read()) != TOOL_SIGNAL_COMMA){
            stringBuilderTalk.append((char) buffer);
        }
        nmea_data_msg.talkID = stringBuilderTalk.toString();

        StringBuilder stringBuilderContent = new StringBuilder();
        while ((buffer = (byte) bufferedInputStream.read()) != TOOL_SIGNAL_ASTERISK){
            stringBuilderContent.append((char) buffer);
        }
        nmea_data_msg.msgContent = stringBuilderContent.toString();

        byte []originalCheckSum = new byte[2];
        if(bufferedInputStream.read(originalCheckSum,0,2) != -1){
            nmea_data_msg.checkSum = new String(originalCheckSum);
        }

        byte [] endSignal = new byte[2];
        if(bufferedInputStream.read(endSignal,0,2) != -1){
            if((TOOL_NMEA_TAIL_1 == endSignal[0]) && (TOOL_NMEA_TAIL_2 == endSignal[1])){

                if(nmea_data_msg.checkSum.equals(Tool_Check_XOR(nmea_data_msg))){
                    parserNMEAStatement(nmea_data_msg);
//                    System.out.println(nmea_data_msg);
                }
            }
        }
    }

    public static void parserNMEAStatement(NMEA_DATA_MSG nmea_data_msg){

        String talkID;

        if(nmea_data_msg.talkID != null){
            talkID = nmea_data_msg.talkID;


            if(talkID.matches(TOOL_NMEA_GGA)){
                parserNmeaStatementGGA(nmea_data_msg);
            }

            else if(talkID.matches(TOOL_NMEA_PQTMIMU)){
                parserNmeaStatementIMU(nmea_data_msg);
            }
        }
    }



    public static void parserNmeaStatementIMU(NMEA_DATA_MSG nmea_data_msg){

        // 3875764,-0.977783,0.098389,-0.190063,-2.618321,-0.648855,0.381679,247461,3770205
        /**
         *         String Timestam;
         *         String ACC_X;
         *         String ACC_Y;
         *         String ACC_Z;
         *         String AngRate_X;
         *         String AngRate_Y;
         *         String AngRate_Z;
         *         String TickCount;
         *         String LastTick_Timestam;
         */
        if(nmea_data_msg.msgContent != null){
            String [] splitData = nmea_data_msg.msgContent.split(",",TOOL_NMEA_PQTMIMU_PARA_COUNT);
            NMEA_DATA_MSG_HYF_PQTMIMU nmea_data_msg_hyf_pqtmimu = new NMEA_DATA_MSG_HYF_PQTMIMU();
            nmea_data_msg_hyf_pqtmimu.Timestam = splitData[0];
            nmea_data_msg_hyf_pqtmimu.ACC_X = splitData[1];
            nmea_data_msg_hyf_pqtmimu.ACC_Y = splitData[2];
            nmea_data_msg_hyf_pqtmimu.ACC_Z = splitData[3];
            nmea_data_msg_hyf_pqtmimu.AngRate_X = splitData[4];
            nmea_data_msg_hyf_pqtmimu.AngRate_Y = splitData[5];
            nmea_data_msg_hyf_pqtmimu.AngRate_Z = splitData[6];
            nmea_data_msg_hyf_pqtmimu.TickCount = splitData[7];
            nmea_data_msg_hyf_pqtmimu.LastTick_Timestam = splitData[8];


            arrayListWheelTick.add(Integer.parseInt(nmea_data_msg_hyf_pqtmimu.TickCount));
            arrayListTimeTick.add(Integer.parseInt(nmea_data_msg_hyf_pqtmimu.LastTick_Timestam));

          //  System.out.println(nmea_data_msg_hyf_pqtmimu);

        }
    }

    public static NMEA_DATA_MSG_GGA parserNmeaStatementGGA(NMEA_DATA_MSG nmea_data_msg){
        // "083447.000,3149.316803,N,11706.907667,E,1,10,2.05,41.7,M,-3.6,M,,"
        NMEA_DATA_MSG_GGA data_msg_gga = null;
        if(nmea_data_msg.msgContent != null){
            String [] splitData = nmea_data_msg.msgContent.split(",",TOOOL_NMEA_GNGGA_PARA_COUNT);
            data_msg_gga = new NMEA_DATA_MSG_GGA();
            data_msg_gga.fixCurrentTime = splitData[0];
            data_msg_gga.latitude = splitData[1];
            data_msg_gga.direction_NS = splitData[2];
            data_msg_gga.longitude = splitData[3];
            data_msg_gga.direction_EW = splitData[4];
            data_msg_gga.IndicationMode = splitData[5];
            data_msg_gga.useSvNumber = splitData[6];
            data_msg_gga.HorizontalPrecisionFactor = splitData[7];
            data_msg_gga.altitude = splitData[8];
            data_msg_gga.altitudeUnit = splitData[9];
            data_msg_gga.geoIdGap = splitData[10];
            data_msg_gga.geoIdGapUnit = splitData[11];
            data_msg_gga.DifferentialSatelliteNavigationSystemDataAge = splitData[12];
            data_msg_gga.DifferentialBaseStationIdentificationNumber = splitData[13];

          //  System.out.println(data_msg_gga);

          //  arrayList.add(Integer.parseInt(data_msg_gga.useSvNumber));
        }
        return data_msg_gga;
    }

    static class NMEA_DATA_MSG_CURRENT_INFO{
        String currentUTCTime;

        int currentUseSVNumber;

        int GPSViewSVNumber;
        int GLONASSViewSVNumber;
        int BDSViewSVNumber;
        int GALILEOViewSVNumber;
        int QZSSAViewSVNumber;

        int GPSAvgCn0;
        int GLONASSAvgCn0;
        int BDSAvgCn0;
        int GALILEOAvgCn0;
        int QZSSAvgCn0;
    }

    static class NMEA_DATA_MSG{
        char head = TOOL_NMEA_HEAD;
        String talkID;
        String msgContent;
        char asterisk = TOOL_SIGNAL_ASTERISK;
        String checkSum;

        public int getMsgLength(){
            return talkID.length() + msgContent.length() + 1;
        }


        @Override
        public String toString() {
            return "NMEA_DATA_MSG{" +
                    "head=" + head +
                    ", talkID='" + talkID + '\'' +
                    ", msgContent='" + msgContent + '\'' +
                    ", asterisk=" + asterisk +
                    ", checkSum='" + checkSum + '\'' +
                    '}';
        }
    }

    static class NMEA_DATA_MSG_HYF_PQTMIMU{
        String Timestam;
        String ACC_X;
        String ACC_Y;
        String ACC_Z;
        String AngRate_X;
        String AngRate_Y;
        String AngRate_Z;
        String TickCount;
        String LastTick_Timestam;

        @Override
        public String toString() {
            return "NMEA_DATA_MSG_HYF_PQTMIMU{" +
                    "Timestam='" + Timestam + '\'' +
                    ", ACC_X='" + ACC_X + '\'' +
                    ", ACC_Y='" + ACC_Y + '\'' +
                    ", ACC_Z='" + ACC_Z + '\'' +
                    ", AngRate_X='" + AngRate_X + '\'' +
                    ", AngRate_Y='" + AngRate_Y + '\'' +
                    ", AngRate_Z='" + AngRate_Z + '\'' +
                    ", TickCount='" + TickCount + '\'' +
                    ", LastTick_Timestam='" + LastTick_Timestam + '\'' +
                    '}';
        }
    }


    static class NMEA_DATA_MSG_GSV{
        String totalNumberSentence;
        String currentSentence;
        String totalNumberSV;
        String sVID;
        String elevationAngle;

        String azimuth;

    }

    static class NMEA_DATA_MSG_GGA{
        String talkID;
        String fixCurrentTime;
        String latitude;
        String direction_NS;
        String longitude;
        String direction_EW;
        String IndicationMode;
        String useSvNumber;
        String HorizontalPrecisionFactor;
        String altitude;
        String altitudeUnit;
        String geoIdGap;
        String geoIdGapUnit;
        String DifferentialSatelliteNavigationSystemDataAge;
        String DifferentialBaseStationIdentificationNumber;
        String checkSum;

        @Override
        public String toString() {
            return "NMEA_DATA_MSG_GGA{" +
                    "talkID='" + talkID + '\'' +
                    ", fixCurrentTime='" + fixCurrentTime + '\'' +
                    ", latitude='" + latitude + '\'' +
                    ", direction_NS='" + direction_NS + '\'' +
                    ", longitude='" + longitude + '\'' +
                    ", direction_EW='" + direction_EW + '\'' +
                    ", IndicationMode='" + IndicationMode + '\'' +
                    ", useSvNumber='" + useSvNumber + '\'' +
                    ", HorizontalPrecisionFactor='" + HorizontalPrecisionFactor + '\'' +
                    ", altitude='" + altitude + '\'' +
                    ", altitudeUnit='" + altitudeUnit + '\'' +
                    ", geoIdGap='" + geoIdGap + '\'' +
                    ", geoIdGapUnit='" + geoIdGapUnit + '\'' +
                    ", DifferentialSatelliteNavigationSystemDataAge='" + DifferentialSatelliteNavigationSystemDataAge + '\'' +
                    ", DifferentialBaseStationIdentificationNumber='" + DifferentialBaseStationIdentificationNumber + '\'' +
                    ", checkSum='" + checkSum + '\'' +
                    '}';
        }
    }

    private static String Tool_Check_XOR(NMEA_DATA_MSG msg) {
        int msgLength = msg.getMsgLength();
        byte result = 0;

        String stringBuffer = msg.talkID +
                "," +
                msg.msgContent;

        char []arrays = stringBuffer.toCharArray();

        for(int i = 0; i < msgLength; i++)
        {
            result ^= arrays[i];
        }
        return Integer.toHexString(result  & 0xFF | 0xFFFFFF00).toUpperCase().substring(6);
    }
}
