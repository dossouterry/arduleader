/**
 * Generated class : msg_radio_status
 * DO NOT MODIFY!
 **/
package org.mavlink.messages.ardupilotmega;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.IMAVLinkCRC;
import org.mavlink.MAVLinkCRC;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/**
 * Class msg_radio_status
 * Status generated by radio
 **/
public class msg_radio_status extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_RADIO_STATUS = 109;
  private static final long serialVersionUID = MAVLINK_MSG_ID_RADIO_STATUS;
  public msg_radio_status(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_RADIO_STATUS;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 9;
}

  /**
   * receive errors
   */
  public int rxerrors;
  /**
   * count of error corrected packets
   */
  public int fixed;
  /**
   * local signal strength
   */
  public int rssi;
  /**
   * remote signal strength
   */
  public int remrssi;
  /**
   * how full the tx buffer is as a percentage
   */
  public int txbuf;
  /**
   * background noise level
   */
  public int noise;
  /**
   * remote background noise level
   */
  public int remnoise;
/**
 * Decode message with raw data
 */
public void decode(ByteBuffer dis) throws IOException {
  rxerrors = (int)dis.getShort()&0x00FFFF;
  fixed = (int)dis.getShort()&0x00FFFF;
  rssi = (int)dis.get()&0x00FF;
  remrssi = (int)dis.get()&0x00FF;
  txbuf = (int)dis.get()&0x00FF;
  noise = (int)dis.get()&0x00FF;
  remnoise = (int)dis.get()&0x00FF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+9];
   ByteBuffer dos = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
  dos.put((byte)0xFE);
  dos.put((byte)(length & 0x00FF));
  dos.put((byte)(sequence & 0x00FF));
  dos.put((byte)(sysId & 0x00FF));
  dos.put((byte)(componentId & 0x00FF));
  dos.put((byte)(messageType & 0x00FF));
  dos.putShort((short)(rxerrors&0x00FFFF));
  dos.putShort((short)(fixed&0x00FFFF));
  dos.put((byte)(rssi&0x00FF));
  dos.put((byte)(remrssi&0x00FF));
  dos.put((byte)(txbuf&0x00FF));
  dos.put((byte)(noise&0x00FF));
  dos.put((byte)(remnoise&0x00FF));
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 9);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[15] = crcl;
  buffer[16] = crch;
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_RADIO_STATUS : " +   "  rxerrors="+rxerrors+  "  fixed="+fixed+  "  rssi="+rssi+  "  remrssi="+remrssi+  "  txbuf="+txbuf+  "  noise="+noise+  "  remnoise="+remnoise;}
}
