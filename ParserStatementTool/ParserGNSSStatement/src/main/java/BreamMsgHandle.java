import java.io.BufferedInputStream;
import java.util.Arrays;


public class BreamMsgHandle {

    private static final byte TOOL_BREAM_SYNC1 = (byte) 0xB5;
    private static final byte TOOL_BREAM_SYNC2 = (byte) 0x62;

    private static final byte TOOL_BREAM_MSG_GROUP_ASC = (byte) 0x02;
    private static final byte TOOL_BREAM_MSG_ID_ASC_MEAS = (byte) 0x15;

    private static final byte BCM_INVALID_DATA_LENGTH_1 = (byte) 0x30;
    private static final byte BCM_INVALID_DATA_LENGTH_2 = (byte) 0x5C;


    /***
     *  从原始数据读取数据并拼接为一帧数据
     * @param bufferedInputStream 字节流
     * @throws Exception 文件不存在
     */
    public static void spliceGnssBreamStatement(BufferedInputStream bufferedInputStream) throws Exception{

        BCM_DATA_MSG_TEMPLATE bcm_data_msg_template_template = new BCM_DATA_MSG_TEMPLATE();

        byte [] BCM_MSG_GROUP_ID = new byte[2];
        if(bufferedInputStream.read(BCM_MSG_GROUP_ID,0,2) != -1) {
            bcm_data_msg_template_template.MSG_GROUP = BCM_MSG_GROUP_ID[0];
            bcm_data_msg_template_template.MSG_ID = BCM_MSG_GROUP_ID[1];
        }

        byte [] BCM_MSG_DATA_LENGTH = new byte[2];
        if(bufferedInputStream.read(BCM_MSG_DATA_LENGTH,0,2) != -1){
            // 进行无效数据的清除
            if((BCM_INVALID_DATA_LENGTH_1 == BCM_MSG_DATA_LENGTH[0]) &&
                    BCM_INVALID_DATA_LENGTH_2 == BCM_MSG_DATA_LENGTH[1]){
                return;
            }
            bcm_data_msg_template_template.MSG_LENGTH_1 = BCM_MSG_DATA_LENGTH[0];
            bcm_data_msg_template_template.MSG_LENGTH_2 = BCM_MSG_DATA_LENGTH[1];
        }

        int BCM_Data_Length = bcm_data_msg_template_template.getMsgContentLength();
        if(bufferedInputStream.available() - BCM_Data_Length > 0){
            byte[] byteContent = new byte[BCM_Data_Length];
            if(bufferedInputStream.read(byteContent, 0 ,BCM_Data_Length) != -1){
                bcm_data_msg_template_template.MSG_Content = byteContent;
            }
        }else {
            //  数据直接抛弃
            return;
        }

        if(bufferedInputStream.available() - 2 > 0){
            byte[] byteCrcVerify = new byte[2];
            if(bufferedInputStream.read(byteCrcVerify, 0 ,2) != -1){
                bcm_data_msg_template_template.MSG_CheckSum_1 = byteCrcVerify[0];
                bcm_data_msg_template_template.MSG_CheckSum_2 = byteCrcVerify[1];
            }
        }

        if(bcm_data_msg_template_template.MSG_Content != null){
            byte [] checksum = BreamMsgCheckSum(bcm_data_msg_template_template);
            if((checksum[0] == bcm_data_msg_template_template.MSG_CheckSum_1) &&
                    (checksum[1] == bcm_data_msg_template_template.MSG_CheckSum_2)){
                parserSelectBreamStatement(bcm_data_msg_template_template);
            }
        }
    }


    public static void parserSelectBreamStatement(BCM_DATA_MSG_TEMPLATE bcmDataTemplate){
        byte TOOL_BREAM_MSG_GROUP = bcmDataTemplate.MSG_GROUP;
        byte TOOL_BREAM_MSG_ID = bcmDataTemplate.MSG_ID;

        switch (TOOL_BREAM_MSG_GROUP){
            case TOOL_BREAM_MSG_GROUP_ASC:
                switch (TOOL_BREAM_MSG_ID){
                    case TOOL_BREAM_MSG_ID_ASC_MEAS:
                        parserBreamMsgAscMeas(bcmDataTemplate.MSG_Content);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }


    /***
     * 将内容进行解析
     * @param msgContent Content 解析
     */
    private static void parserBreamMsgAscMeas(byte[] msgContent) {

        double rcvTow = 0;
        int week;
        short leapS;
        int numMeas;
        int recStat;

        int needRepeatedCount = msgContent.length % 32;

        for(int i = 0; i < 8; i++){
            rcvTow += msgContent[i] << (i * 8) ;
        }

        week = ((msgContent[8] & 0xFF) | ((msgContent[9] << 8) & 0xFF00)) & 0xFFFF;

        leapS = msgContent[10];

        numMeas = msgContent[11];

        recStat = msgContent[12];

        //msgContent[18];msgContent[19];msgContent[20];  Reserved

        System.out.println("{rcvTow=" + rcvTow + ",week=" + week + ",leapS=" + leapS + ",numMeas=" + numMeas + ",ecStat=" + recStat + "}");

        for(int i = 0; i < needRepeatedCount; i++){

            BCM_DATA_MSG_CONTENT_ASC_MEAS_TEMPLATE template = new BCM_DATA_MSG_CONTENT_ASC_MEAS_TEMPLATE();

            for(int j = 0; j < 8; j++){
                template.prMes += msgContent[16 + j + i * 32] << (j * 8);
            }
            for(int j = 0; j < 8; j++){
                template.cpMes += msgContent[24 + j + i * 32] << (j * 8) ;
            }
            for(int j = 0; j < 4; j++){
                template.doMes += msgContent[32 + j + i * 32] << (j * 8) ;
            }

            template.gnssId = msgContent[36 + i * 32] & 0xFF;

            template.svId = msgContent[37 + i * 32] & 0xFF;

            template.sigId = msgContent[38 + i * 32] & 0xFF;

            template.freqId = msgContent[39 + i * 32] & 0xFF;

            template.locktime = ((msgContent[40 + i * 32] & 0xFF) | (msgContent[41 + i * 32] << 8) & 0xFF00) & 0xFFFF;

            template.cn0 = msgContent[42 + i * 32] & 0xFF;

            template.prStdev = msgContent[43 + i * 32] & 0xFF;

            template.cpStdev = msgContent[44 + i * 32] & 0xFF;

            template.doStdev = msgContent[45 + i * 32] & 0xFF;

            template.trkStat = msgContent[46 + i * 32] & 0xFF;

            template.frac_cn0 = msgContent[47 + i * 32] & 0xFF;

            System.out.println(template);
        }

    }


    /**
     * 博通协议累加校验
     * @param bcmDataTemplate 需要校验的数据
     * @return [0] [1]
     */
    public static  byte[] BreamMsgCheckSum(BCM_DATA_MSG_TEMPLATE bcmDataTemplate){
        byte[] checksum = new byte[2];
        byte[] byteBreamArray = new byte[bcmDataTemplate.getMsgContentLength() + 4];
        byteBreamArray[0] = bcmDataTemplate.MSG_GROUP;
        byteBreamArray[1] = bcmDataTemplate.MSG_ID;
        byteBreamArray[2] = bcmDataTemplate.MSG_LENGTH_1;
        byteBreamArray[3] = bcmDataTemplate.MSG_LENGTH_2;

        if(bcmDataTemplate.MSG_Content != null){

            for(int i = 4 ;i < bcmDataTemplate.getMsgContentLength() + 4;i++){
                byteBreamArray[i] = bcmDataTemplate.MSG_Content[i - 4];
            }

            for (byte b : byteBreamArray) {
                checksum[0] = (byte) (checksum[0] + b);
                checksum[1] = (byte) (checksum[1] + checksum[0]);
            }
        }
        return checksum;
    }


    /**
     * BCM_DATA_TEMPLATE
     * 博通协议模板
     */

    static class BCM_DATA_MSG_TEMPLATE{

        byte MSG_HEAD_1 = TOOL_BREAM_SYNC1;
        byte MSG_HEAD_2 = TOOL_BREAM_SYNC2;

        byte MSG_GROUP;
        byte MSG_ID;

        byte MSG_LENGTH_1;
        byte MSG_LENGTH_2;

        byte[] MSG_Content;

        byte MSG_CheckSum_1;
        byte MSG_CheckSum_2;

        public int  getMsgContentLength(){
            return (MSG_LENGTH_1 & 0xFF | ((MSG_LENGTH_2 << 8) & 0xFF00));
        }

        @Override
        public String toString() {
            return "{" +
                    "MSG_HEAD_1=" + MSG_HEAD_1 +
                    ", MSG_HEAD_2=" + MSG_HEAD_2 +
                    ", MSG_GROUP=" + MSG_GROUP +
                    ", MSG_ID=" + MSG_ID +
                    ", MSG_LENGTH_1=" + MSG_LENGTH_1 +
                    ", MSG_LENGTH_2=" + MSG_LENGTH_2 +
                    ", MSG_Content=" + Arrays.toString(MSG_Content) +
                    ", MSG_CheckSum_1=" + MSG_CheckSum_1 +
                    ", MSG_CheckSum_2=" + MSG_CheckSum_2 +
                    '}';
        }
    }


    /**
     * BRM_ASC_MEAS_MSG
     * 实体数据
     */

    static class BCM_DATA_MSG_CONTENT_ASC_MEAS_TEMPLATE{
        private double prMes;
        private double cpMes;
        private float doMes;
        private int gnssId;
        private int svId;
        private int sigId;
        private int freqId;
        private int locktime;
        private int cn0;
        private int prStdev;
        private int cpStdev;
        private int doStdev;
        private int trkStat;
        private int frac_cn0;

        @Override
        public String toString() {
            return "{" +
                    "prMes=" + prMes +
                    ",cpMes=" + cpMes +
                    ",doMes=" + doMes +
                    ",gnssId=" + gnssId +
                    ",svId=" + svId +
                    ",sigId=" + sigId +
                    ",freqId=" + freqId +
                    ",locktime=" + locktime +
                    ",cn0=" + cn0 +
                    ",prStdev=" + prStdev +
                    ",cpStdev=" + cpStdev +
                    ",doStdev=" + doStdev +
                    ",trkStat=" + trkStat +
                    ",frac_cn0=" + frac_cn0 +
                    '}';
        }
    }
}
