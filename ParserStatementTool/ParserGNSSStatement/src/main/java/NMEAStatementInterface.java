public interface NMEAStatementInterface {

     byte TOOL_NMEA_HEAD = (byte) 0x24;
     byte TOOL_NMEA_TAIL = (byte) 0x2A;

     void parserNMEAStatementGGA(String strGGA);


}
