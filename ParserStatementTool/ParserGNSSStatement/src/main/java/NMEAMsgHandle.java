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

   private static final String TOOL_NMEA_GNGGA = "GNGGA";


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

            String msgCheckSum = new String(originalCheckSum);
            nmea_data_msg.checkSum = msgCheckSum;
        }

        byte [] endSignal = new byte[2];
        if(bufferedInputStream.read(endSignal,0,2) != -1){
            if((TOOL_NMEA_TAIL_1 == endSignal[0]) && (TOOL_NMEA_TAIL_2 == endSignal[1])){

                if(nmea_data_msg.checkSum.equals(Tool_Check_XOR(nmea_data_msg))){
                    parserNMEAStatement(nmea_data_msg);
                    System.out.println(nmea_data_msg);
                }
            }
        }
    }

    public static void parserNMEAStatement(NMEA_DATA_MSG nmea_data_msg){

        String talkID;

        if(nmea_data_msg.talkID != null){
            talkID = nmea_data_msg.talkID;

            switch (talkID){
                case TOOL_NMEA_GNGGA:
                    parserNmeaStatementGGA(nmea_data_msg);
                    break;

            }
        }

    }


    public static void parserNmeaStatementGGA(NMEA_DATA_MSG nmea_data_msg){

        // "083447.000,3149.316803,N,11706.907667,E,1,10,2.05,41.7,M,-3.6,M,,"
        if(nmea_data_msg.msgContent != null){
            String [] splitData = nmea_data_msg.msgContent.split(",",14);
            NMEA_DATA_MSG_GGA data_msg_gga = new NMEA_DATA_MSG_GGA();
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

            System.out.println(data_msg_gga);

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
