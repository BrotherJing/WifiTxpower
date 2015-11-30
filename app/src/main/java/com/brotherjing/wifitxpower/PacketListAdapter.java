package com.brotherjing.wifitxpower;

import java.util.ArrayList;
import java.util.List;

import com.tqd.utils.IPSeeker;

import net.sourceforge.jpcap.net.ARPPacket;
import net.sourceforge.jpcap.net.EthernetPacket;
import net.sourceforge.jpcap.net.ICMPPacket;
import net.sourceforge.jpcap.net.IPPacket;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.net.UDPPacket;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Typeface;


public class PacketListAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;

    List<Packet> mPacketList;
    private IPSeeker mIPSeeker;

    public PacketListAdapter(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mPacketList = new ArrayList<Packet>();

    }

    public void clear() {
        if (mPacketList != null) {
            mPacketList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mPacketList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mPacketList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Packet packet = mPacketList.get(position);
        ViewHolder view;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.packet_list_item, parent, false);
            view = new ViewHolder(convertView);
            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }

        TextView tvType = (TextView) convertView.findViewById(R.id.item_type);


        if (packet instanceof EthernetPacket) {
            EthernetPacket ePacket = (EthernetPacket) packet;

            tvType.setText("Ethernet");

            // String address=      ePacket.getSourceHwAddress()+">>"+ePacket.getDestinationHwAddress();
            view.setText(ePacket.getSourceHwAddress(), ePacket.getDestinationHwAddress());
            //     tvAddress.setText(address);
        }
        if (packet instanceof ARPPacket) {

            ARPPacket aPacket = (ARPPacket) packet;


            tvType.setText("ARP");

            String address = aPacket.getSourceHwAddress() + ">>" + aPacket.getDestinationHwAddress();
            view.setText(aPacket.getSourceHwAddress(), aPacket.getDestinationHwAddress());

            //  tvAddress.setText(address);
        }
        //����ICMPЭ��
        if (packet instanceof ICMPPacket) {
            ICMPPacket iPacket = (ICMPPacket) packet;

            tvType.setText("ICMP");

            String address = iPacket.getSourceAddress() + ">>" + iPacket.getDestinationAddress();
            String source = iPacket.getSourceAddress();
            String destination = iPacket.getDestinationAddress();
            //   String address1=mIPSeeker.getCountry(source)+" "+
            //  mIPSeeker.getArea(source)+">>"+mIPSeeker.getCountry(destination)+" "+mIPSeeker.getArea(destination);

            view.setText(source, destination);
            if (mIPSeeker != null)
                view.setAddress(mIPSeeker.getCountry(source), mIPSeeker.getCountry(destination));

        }
        //����IP
        if (packet instanceof IPPacket) {
            IPPacket iPacket = (IPPacket) packet;


            String source = iPacket.getSourceAddress();
            String destination = iPacket.getDestinationAddress();
            //    String address1=mIPSeeker.getCountry(source)+" "+
            //  mIPSeeker.getArea(source)+">>"+mIPSeeker.getCountry(destination)+" "+mIPSeeker.getArea(destination);

            //   tvAddress.setText(address1);

            view.setText(source, destination);
            if (mIPSeeker != null)
                view.setAddress(mIPSeeker.getCountry(source), mIPSeeker.getCountry(destination));

            //UDP
            if (iPacket instanceof UDPPacket) {

                UDPPacket udpPacket = (UDPPacket) iPacket;

                String data = udpPacket.getUDPData().toString();

                view.setText(data,"");

                tvType.setText("UDP");
                //view.setText(source + ":" + udpPacket.getSourcePort(), destination + ":" + udpPacket.getDestinationPort());
                //   String address=      udpPacket.getSourceAddress()+":"+udpPacket.getSourcePort()+">>"+udpPacket.getDestinationAddress()+":"+udpPacket.getDestinationPort();

                //   tvAddress.setText(address);

            }
            if (iPacket instanceof TCPPacket) {
                TCPPacket tcpPacket = (TCPPacket) iPacket;

                tvType.setText("TCP");
                view.setText(source + ":" + tcpPacket.getSourcePort(), destination + ":" + tcpPacket.getDestinationPort());
                //   String address=      tcpPacket.getSourceAddress()+":"+tcpPacket.getSourcePort()+">>"+tcpPacket.getDestinationAddress()+":"+tcpPacket.getDestinationPort();

                //   tvAddress.setText(address);
            }


        }
        return convertView;
    }


    public void addPacket(Packet packet) {

        if (packet != null) {
            mPacketList.add(0, packet);
            this.notifyDataSetChanged();
        }

    }


    class ViewHolder {
        TextView source;
        TextView destination;
        TextView type;
        TextView sourceAddress;
        TextView destinationAddress;

        public ViewHolder(View parent) {
            // TODO Auto-generated constructor stub

            source = (TextView) parent.findViewById(R.id.source);
            destination = (TextView) parent.findViewById(R.id.destination);
            type = (TextView) parent.findViewById(R.id.item_type);
            sourceAddress = (TextView) parent.findViewById(R.id.sourceaddress);
            destinationAddress = (TextView) parent.findViewById(R.id.destinationaddress);

        }

        public void setText(String s, String d) {
            source.setText("from " + s);
            destination.setText("to " + d);
        }

        public void setAddress(String s, String d) {
            sourceAddress.setText("src" + s);
            destinationAddress.setText("dest " + d);
        }

    }


    public void setIPSeeker(IPSeeker mIPSeeker2) {
        // TODO Auto-generated method stub
        this.mIPSeeker = mIPSeeker2;

    }

}
