package com.atp.rfreaderinterface;

import com.atp.rfreaderinterface.SerialPort;

import java.io.IOException;
import java.security.InvalidParameterException;


abstract public class RFReader {
    SerialControl mPort;
public  RFReader()
{
    mPort = new SerialControl();
    mPort.setPort("/dev/ttyXRM0");
    mPort.setBaudRate("115200");
    OpenComPort(mPort);
}

   private void OpenComPort(SerialHelper ComPort){
        try
        {
            ComPort.open();
        } catch (SecurityException e) {
            onErrorMessage("Failed to open Serial Port : no serial read/write access!");
        } catch (IOException e) {
            onErrorMessage("Open Serial Port Failed: Unkonw Error!");
        } catch (InvalidParameterException e) {
            onErrorMessage("Open Serial Port Failed : Parameter error 1");
        }
    }
    private void CloseComPort(SerialHelper ComPort){
        if (ComPort!=null){
            ComPort.stopSend();
            ComPort.close();
        }
    }
    private class SerialControl extends SerialHelper{

        //		public SerialControl(String sPort, String sBaudRate){
//			super(sPort, sBaudRate);
//		}
        public SerialControl(){
        }

        @Override
        protected void onDataReceived(final ComBean ComRecData)
        {
            // Data reception is large or a soft keyboard pops up when receiving, the interface will be stuck, may be related to the display performance of the 6410
            // Direct refresh display, when the amount of received data is large, the card is obvious, but the reception is synchronized with the display.
            // Use the thread timing refresh display to get a smoother display, but when the receiving data speed is faster than the display speed, the display will lag.
            // The final effect is almost -_-, the thread timing refresh is slightly better.
            //  DispQueue.AddQueue(ComRecData);// Thread timing refresh display (recommended)
            String sMsg="";
            if(ComRecData.bRec.length==9)
            {
                if(ComRecData.bRec[2]==0x04)
                {
                    byte[] TagID;
                    TagID = new byte[6];
                    for(int i=3;i<9;i++){
                        TagID[i-3]=ComRecData.bRec[i];
                    }
                    sMsg=MyFunc.ByteArrToHex(TagID);
                    recievedTagId(sMsg);
                }
            }


        }
    }

    protected abstract void onErrorMessage(String mMsg);
    protected abstract void recievedTagId(String tagId);
}

