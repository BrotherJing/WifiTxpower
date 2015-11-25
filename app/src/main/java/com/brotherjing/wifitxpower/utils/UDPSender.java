package com.brotherjing.wifitxpower.utils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by Brotherjing on 2015-11-22.
 */
public class UDPSender {

    static String dstAddr = "224.0.0.1";
    static int dstPort = 5678;
    static int TTL = 4;

    public static void send()throws Exception{
        InetAddress dst = InetAddress.getByName(dstAddr);
        MulticastSocket multicastSocket = new MulticastSocket();
        multicastSocket.setTimeToLive(TTL);
        byte[] msg = "hello world".getBytes();
        DatagramPacket dp = new DatagramPacket(msg,msg.length,dst,dstPort);
        multicastSocket.send(dp);
        multicastSocket.close();
    }

}
