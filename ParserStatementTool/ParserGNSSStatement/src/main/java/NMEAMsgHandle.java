import com.sun.deploy.net.MessageHeader;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class NMEAMsgHandle {

    private static final byte TOOL_NMEA_HEAD = (byte) 0x24;
    private static final byte TOOL_NMEA_TAIL_1 = (byte) 0x0D;
    private static final byte TOOL_NMEA_TAIL_2 = (byte) 0x0A;
    private static final byte TOOL_SIGNAL_COMMA = (byte) 0x2C;
    private static final byte TOOL_SIGNAL_ASTERISK = (byte) 0x2A;

    private static final String TOOL_NMEA_GGA = "GGA";
    private static final int TOOOL_NMEA_GGA_PARA_COUNT = 14;


    private static final String TOOL_NMEA_GSV = "GSV";
    private static final int TOOL_NMEA_GSV_PARA_COUNT = 20;

    private static final String TOOL_NMEA_PQTMIMU = "PQTMIMU";
    private static final int TOOL_NMEA_PQTMIMU_PARA_COUNT = 9;

    private static final String TOOL_NMEA_PQTMVEHMSG = "PQTMVEHMSG";
    private static final int TOOL_NMEA_PQTMVEHMSG_PARA_COUNT = 4;

    private static int recordAllGGAStatement = 0;

    static class FRAME_MSG_GSV{
        ArrayList<NMEA_DATA_MSG_GSV> GP_GSV_LIST;
        ArrayList<NMEA_DATA_MSG_GSV> BD_GSV_LIST;
        ArrayList<NMEA_DATA_MSG_GSV> GA_GSV_LIST;
        ArrayList<NMEA_DATA_MSG_GSV> GL_GSV_LIST;
        ArrayList<NMEA_DATA_MSG_GSV> GQ_GSV_LIST;
    }
    public static ArrayList<FRAME_MSG_GSV> FRAME_GSV = new ArrayList<>();

    public static ArrayList<NMEA_DATA_MSG_GSV> GP_GSV_LIST = new ArrayList<>();
    public static ArrayList<NMEA_DATA_MSG_GSV> GB_GSV_LIST = new ArrayList<>();
    public static ArrayList<NMEA_DATA_MSG_GSV> GA_GSV_LIST = new ArrayList<>();
    public static ArrayList<NMEA_DATA_MSG_GSV> GL_GSV_LIST = new ArrayList<>();
    public static ArrayList<NMEA_DATA_MSG_GSV> GQ_GSV_LIST = new ArrayList<>();


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
                   // System.out.println(nmea_data_msg);
                }
            }
        }
    }

    ArrayList<NMEA_DATA_MSG_CURRENT_INFO> nmea_data_msg_current_infoArrayList = new ArrayList<>();



    public static void parserNMEAStatement(NMEA_DATA_MSG nmea_data_msg){

        String talkID;
        int localCount = recordAllGGAStatement;

        if(nmea_data_msg.talkID != null){
            talkID = nmea_data_msg.talkID;

            if(talkID.indexOf(TOOL_NMEA_GGA) > 0){
              //  recordAllGGAStatement++;
                parserNmeaStatementGGA(nmea_data_msg);
            }

            else if(talkID.equals(TOOL_NMEA_PQTMIMU)){
             //   parserNmeaStatementIMU(nmea_data_msg);
            }

            else if (talkID.equals(TOOL_NMEA_PQTMVEHMSG)){
               // parserNmeaStatementVEGMSG(nmea_data_msg);
            }

            else if(talkID.indexOf(TOOL_NMEA_GSV) > 0){

//                NMEA_DATA_MSG_GSV temp_gsv;
//               // System.out.println(nmea_data_msg);
//
//                temp_gsv =  parserNmeaStatementGSV(nmea_data_msg);
//
//                if(temp_gsv != null){
//                    if("GPGSV".equals(temp_gsv.talkID)){
//                        GP_GSV_LIST.add(temp_gsv);
//                    }else if("GLGSV".equals(temp_gsv.talkID)){
//                        GL_GSV_LIST.add(temp_gsv);
//                    }else if("GAGSV".equals(temp_gsv.talkID)){
//                        GA_GSV_LIST.add(temp_gsv);
//                    }else if ("GBGSV".equals(temp_gsv.talkID)){
//                        GB_GSV_LIST.add(temp_gsv);
//                    }else if("GQGSV".equals(temp_gsv.talkID)){
//                        GQ_GSV_LIST.add(temp_gsv);
//                    }
//                }


            }else {
               // System.out.println(talkID);
            }
        }
    }



    public static NMEA_DATA_MSG_GSV parserNmeaStatementGSV(NMEA_DATA_MSG nmea_data_msg){

        /**
         *         String totalNumberSentence;
         *         String currentSentence;
         *         String totalNumberSV;
         *
         *         SV_INFO []sv_info;
         *
         *         String signalID;
         */

        NMEA_DATA_MSG_GSV nmea_data_msg_gsv = null;
        if(nmea_data_msg.msgContent != null){
            String [] splitData = nmea_data_msg.msgContent.split(",");

            int svCount = (splitData.length - 4) / 4;

            if((svCount == 1) || (svCount  == 2) || (svCount == 3) || (svCount == 4)){

                nmea_data_msg_gsv = new NMEA_DATA_MSG_GSV();
                nmea_data_msg_gsv.talkID = nmea_data_msg.talkID;
                nmea_data_msg_gsv.totalNumberSentence = splitData[0];
                nmea_data_msg_gsv.currentSentence = splitData[1];
                nmea_data_msg_gsv.totalNumberSV = splitData[2];
                /**
                 *         String sVID;
                 *         String elevationAngle;
                 *         String azimuth;
                 *         String SNR;
                 */

                SV_INFO [] arrays_SV = new SV_INFO[svCount];
                for (int i = 0; i < svCount; i++) {
                    SV_INFO sv_info = new SV_INFO();
                    sv_info.sVID = splitData[3 + i];
                    sv_info.elevationAngle = splitData[4 + i];
                    sv_info.azimuth = splitData[5 + i];
                    sv_info.SNR = splitData[6 + i];
                    arrays_SV[i] = sv_info;
                }

                nmea_data_msg_gsv.sv_info = arrays_SV;
                nmea_data_msg_gsv.signalID = splitData[splitData.length - 1];
                nmea_data_msg_gsv.checkSum = nmea_data_msg.checkSum;


            }
        }
      //  System.out.println(nmea_data_msg_gsv);
        return nmea_data_msg_gsv;
    }

    public static ArrayList<Integer> arrayListWheelTickCount = new ArrayList<>();
    public static void parserNmeaStatementVEGMSG(NMEA_DATA_MSG nmea_data_msg){

        /**
         *         String MsgType;
         *         String Timestam;
         *         String WheelTickCount;
         *         String Reserved;
         */
        if(nmea_data_msg.msgContent != null){
            String [] splitData = nmea_data_msg.msgContent.split(",",TOOL_NMEA_PQTMVEHMSG_PARA_COUNT);
            NMEA_DATA_MSG_HYF_VEHMSG msg_hyf_vehmsg = new NMEA_DATA_MSG_HYF_VEHMSG();
            msg_hyf_vehmsg.MsgType = splitData[0];
            msg_hyf_vehmsg.Timestam = splitData[1];
            msg_hyf_vehmsg.WheelTickCount = splitData[2];
            msg_hyf_vehmsg.Reserved = splitData[3];

            arrayListWheelTickCount.add(Integer.parseInt(splitData[2]));
            System.out.println(msg_hyf_vehmsg);
        }
    }

    public static ArrayList<Integer> arrayListWheelTick = new ArrayList<>();
    public static ArrayList<Integer> arrayListTimeTick = new ArrayList<>();

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

            System.out.println(nmea_data_msg_hyf_pqtmimu);
        }
    }

    public static  ArrayList<Integer> arrayListUseSVCount = new ArrayList<>();

    public static ArrayList<CalculateUtils.Point> points = new ArrayList<>();

    public static NMEA_DATA_MSG_GGA parserNmeaStatementGGA(NMEA_DATA_MSG nmea_data_msg){
        // "083447.000,3149.316803,N,11706.907667,E,1,10,2.05,41.7,M,-3.6,M,,"
        NMEA_DATA_MSG_GGA data_msg_gga = null;
        if(nmea_data_msg.msgContent != null){
            String [] splitData = nmea_data_msg.msgContent.split(",",TOOOL_NMEA_GGA_PARA_COUNT);
            if(splitData.length == TOOOL_NMEA_GGA_PARA_COUNT){
                data_msg_gga = new NMEA_DATA_MSG_GGA();
                data_msg_gga.talkID = nmea_data_msg.talkID;
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
                data_msg_gga.checkSum = nmea_data_msg.checkSum;

                CalculateUtils.Point point = new CalculateUtils.Point();

                if(!splitData[1].equals("") && !splitData[3].equals("")){
                    point.Latitude = Double.parseDouble(splitData[1]);
                    point.Longitude = Double.parseDouble(splitData[3]);

                    points.add(point);
                }
                arrayListUseSVCount.add(Integer.parseInt(data_msg_gga.useSvNumber));
            }
              System.out.println(data_msg_gga);
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

        @Override
        public String toString() {
            return "NMEA_DATA_MSG_CURRENT_INFO{" +
                    "currentUTCTime='" + currentUTCTime + '\'' +
                    ", currentUseSVNumber=" + currentUseSVNumber +
                    ", GPSViewSVNumber=" + GPSViewSVNumber +
                    ", GLONASSViewSVNumber=" + GLONASSViewSVNumber +
                    ", BDSViewSVNumber=" + BDSViewSVNumber +
                    ", GALILEOViewSVNumber=" + GALILEOViewSVNumber +
                    ", QZSSAViewSVNumber=" + QZSSAViewSVNumber +
                    ", GPSAvgCn0=" + GPSAvgCn0 +
                    ", GLONASSAvgCn0=" + GLONASSAvgCn0 +
                    ", BDSAvgCn0=" + BDSAvgCn0 +
                    ", GALILEOAvgCn0=" + GALILEOAvgCn0 +
                    ", QZSSAvgCn0=" + QZSSAvgCn0 +
                    '}';
        }
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


    static class SV_INFO{
        String sVID;
        String elevationAngle;
        String azimuth;
        String SNR;

        @Override
        public String toString() {
            return "SV_INFO{" +
                    "sVID='" + sVID + '\'' +
                    ", elevationAngle='" + elevationAngle + '\'' +
                    ", azimuth='" + azimuth + '\'' +
                    ", SNR='" + SNR + '\'' +
                    '}';
        }
    }
    static class NMEA_DATA_MSG_GSV{
        String talkID;
        String totalNumberSentence;
        String currentSentence;
        String totalNumberSV;

        SV_INFO []sv_info;

        String signalID;
        String checkSum;

        @Override
        public String toString() {
            return "NMEA_DATA_MSG_GSV{" +
                    "talkID='" + talkID + '\'' +
                    ", totalNumberSentence='" + totalNumberSentence + '\'' +
                    ", currentSentence='" + currentSentence + '\'' +
                    ", totalNumberSV='" + totalNumberSV + '\'' +
                    ", sv_info=" + Arrays.toString(sv_info) +
                    ", signalID='" + signalID + '\'' +
                    ", checkSum='" + checkSum + '\'' +
                    '}';
        }
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

    static class NMEA_DATA_MSG_HYF_VEHMSG{
        String MsgType;
        String Timestam;
        String WheelTickCount;
        String Reserved;

        @Override
        public String toString() {
            return "NMEA_DATA_MSG_VEHMSG{" +
                    "MsgType='" + MsgType + '\'' +
                    ", Timestam='" + Timestam + '\'' +
                    ", WheelTickCount='" + WheelTickCount + '\'' +
                    ", Reserved='" + Reserved + '\'' +
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
