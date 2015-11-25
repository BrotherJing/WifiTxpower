package com.brotherjing.wifitxpower;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.UserHandle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.Shell;
import com.tqd.utils.IPSeeker;

import net.sourceforge.jpcap.net.LinkLayer;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.PacketFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnListenListener} interface
 * to handle interaction events.
 * Use the {@link ListenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListenFragment extends Fragment {

    private OnListenListener mListener;

    public static final String DIR="ipdatabase";
    public static final String FILE_NAME="qqwry.dat";
    private static final String TAG = "yj";

    UserHandle userHandle=android.os.Process.myUserHandle();
    ListView mListViewPacket;
    PacketListAdapter pla;
    public static int tip=0;
    public IPSeeker mIPSeeker;
    public static long mPacketCount=0;
    public TextView mTVPacketcount;

    private Button btnListen;
    private TextView tvLog;
    private ScrollView svLog;

    public static ListenFragment newInstance() {
        ListenFragment fragment = new ListenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ListenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadIpDatabase();
    }

    @Override
    public void onStart() {
        //RootTools.findBinary("pcap");
        super.onStart();

        //MyRunner是我自己写的一个继承于Runner的类,用来执行命令的一个线程类
        //本来使用这个的RootTools.runBinary(context, binaryName, parameter);但是不知道命令执行的输出数据，如何获取，

        MyRunner mr=new MyRunner(getActivity(), "/system/xbin/pcap", "");
        mr.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listen, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        //tvLog = (TextView)view.findViewById(R.id.tv_log);
        btnListen = (Button)view.findViewById(R.id.btn_listen);
        //svLog = (ScrollView)view.findViewById(R.id.sv_log);

        mTVPacketcount=(TextView) view.findViewById(R.id.packet_count);
        mTVPacketcount.setText("已抓取"+mPacketCount+"个packet");
        mListViewPacket=(ListView) view.findViewById(R.id.packet_list);
        pla=new PacketListAdapter(getActivity());
        mListViewPacket.setAdapter(pla);

        RootTools rt=new RootTools();
        RootTools.default_Command_Timeout=1000*1000;  //设置执行命令超时的值，pcap命令是个死循环，这个设置大一点好些
        RootTools.debugMode=true;


        mListViewPacket.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                Packet packet=(Packet) pla.getItem(position);

                Toast.makeText(getActivity(), packet.toColoredString(false), Toast.LENGTH_LONG).show();

            }
        });
    }

    Handler mHandler=new Handler()
    {
        public void handleMessage(android.os.Message msg) {

            if(msg.what==0 && msg.obj instanceof Packet)
            {
                Packet packet=(Packet) msg.obj;
                pla.addPacket(packet);
                mPacketCount++;
                mTVPacketcount.setText("已抓取"+mPacketCount+"个packet");

            }

            if(msg.what==1 && msg.obj instanceof String)
            {
                Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_LONG).show();

                mIPSeeker=new IPSeeker(FILE_NAME, Environment.getExternalStorageDirectory()+File.separator+DIR);
                pla.setIPSeeker(mIPSeeker);
            }

            if(msg.what==2 && msg.obj instanceof String)
            {
                Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_LONG).show();
            }

        };
    };

    /**
     * 加载本地ip数据库
     */
    private void loadIpDatabase()
    {
        new Thread()
        {


            @Override
            public void run() {
                boolean isSuccess=true;
                // TODO Auto-generated method stub
                Log.i(TAG,"ip数据库默认路径"+Environment.getExternalStorageDirectory()+DIR+File.separator+FILE_NAME);
                //查看sdcard指定目录中的本地ip数据库是否存在
                File file=new File(Environment.getExternalStorageDirectory()+DIR+File.separator+FILE_NAME);
                Log.i(TAG, Environment.getExternalStorageDirectory()+DIR+File.separator+FILE_NAME+file.getAbsolutePath());
                //不存在就从asset中复制到sdcard指定目录中
                if(!file.exists())
                {
                    Log.i(TAG, "不存在ip数据库，从asset复制到sdcard中");
                    AssetManager am=getActivity().getAssets();
                    InputStream is = null;
                    try {
                        is=	am.open("qqwry.dat");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Log.i(TAG, "从asset读取ip数据库失败");
                        isSuccess=false;
                        e.printStackTrace();
                    }
                    if(is!=null)

                        isSuccess=	writeToSDCard(is);
                    else
                        Log.i(TAG, "从asset复制ip数据库到sdcard中失败");
                }
                //将事件发送到ui线程
                if(isSuccess)

                    mHandler.obtainMessage(1, "本地ip数据库加载完毕").sendToTarget();
                else
                    mHandler.obtainMessage(2, "本地ip数据库加载失败").sendToTarget();




            }


        }.start();




    }

    /**
     * @author Administrator
     * 这个类包含了命令执行时，数据输出的回调函数 commandOutput 我们主要在这个函数里做我们想要的事
     *
     */
    class MyCommandCapture extends Command
    {
        private StringBuilder sb = new StringBuilder();

        public MyCommandCapture(int id, String... command) {
            super(id, command);
        }

        public MyCommandCapture(int id, boolean handlerEnabled, String... command) {
            super(id, handlerEnabled, command);
        }

        public MyCommandCapture(int id, int timeout, String... command) {
            super(id, timeout, command);
        }


        /* (non-Javadoc)
         * @see com.stericson.RootTools.execution.Command#commandOutput(int, java.lang.String)
         */
        @Override
        public void commandOutput(int id, String line) {
            sb.append(line).append('\n');


            tip++;
		        /*
		         * 在main.c中，我们每抓到一个包，数据输出三行，
		         *
		         *  printf("the cap len: %d,the pcaket len:%d\n    ",length,plen);
                	printf("the timeinfo:tv_sec:%ld, tv_usec:%ld\n",time.tv_sec,time.tv_usec);
                	int i;
                	for(i=0;i<packet->len;i++)
                	printf("%02x",data[i]);

                	printf("\n");
		         *
		         * 第一，二行是 const struct pcap_pkthdr
		         * 第三行 是包的数据 byte[]的String形式
		         */
            switch(tip)
            {
                case 1:
                    //RootTools.log("Command", "ID: " + 1 + ", " + line);
                    break;
                case 2:
                    //RootTools.log("Command", "ID: " + 2 + ", " + line);
                    break;
                case 3:
                    //RootTools.log("Command", "ID: " + 3 + ", " + line);
                    //Log.i("data length", ""+line.length());
                    byte []data=hexStringToBytes(line);
                    //将byte数组，解析成具体的包，第一个参数是数据链路层类型，我的手机是：wifi下是LinkLayer.EN10MB 移动的2G网是LinkLayer.LINUX_SLL
                    //我们可以在main.c里面获得这个具体的值，int pcap_datalink(pcap_t *p) 返回，例如DLT_EN10MB
                    //这里写死了，
                    //数据包的解析用的是jpcap的库，我这里只是用了一部分java代码，没有涉及native层的，纯java，
                    Packet packet=	 PacketFactory.dataToPacket(LinkLayer.EN10MB, data) ;
                    //	  ByteBuffer bb=ByteBuffer.wrap(data);

                    mHandler.obtainMessage(0, packet).sendToTarget();

                    tip=0;
                    break;
                default: break;
            }
        }


        public String bytesToHexString(byte[] src){
            StringBuilder stringBuilder = new StringBuilder("");
            if (src == null || src.length <= 0) {
                return null;
            }
            for (int i = 0; i < src.length; i++) {
                int v = src[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(hv);
            }
            return stringBuilder.toString();
        }

        /**
         * 十六进制的字符串转化为byte[]
         * @param hexString
         * @return
         */
        public byte[] hexStringToBytes(String hexString) {
            if (hexString == null || hexString.equals("")) {
                return null;
            }
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];
            for (int i = 0; i < length; i++) {
                int pos = i * 2;
                d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }
            return d;
        }
        /**
         * Convert char to byte
         * @param c char
         * @return byte
         */
        private byte charToByte(char c) {
            return (byte) "0123456789ABCDEF".indexOf(c);
        }
        @Override
        public void commandTerminated(int id, String reason) {
            //pass
        }

        @Override
        public void commandCompleted(int id, int exitcode) {
            //pass
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }

    class MyRunner extends Thread
    {

        private static final String LOG_TAG = "RootTools::Runner";

        Context context;
        String binaryName;
        String parameter;

        /**
         * @param context 这个参数，
         * @param binaryName 可执行二进制文件的路径
         * @param parameter 命令的参数   例如 ls -l  这个parameter就是指后面的-l
         */
        public MyRunner(Context context, String binaryName, String parameter) {

            this.context = context;
            this.binaryName = binaryName;
            this.parameter = parameter;
        }

        public void run() {


            try {
                //这个类里面包含了执行命令输出的数据的回调函数，
                MyCommandCapture command = new MyCommandCapture(0, false, binaryName + " " + parameter);
                //命令执行，参数是个超时的值,不太懂，设置的尽量大，不小了，找不到几个包，就结束了
                Shell.startRootShell(10000 * 10000).add(command);
                // Shell.startRootShell().add(command);
                commandWait(command);

            } catch (Exception e) {}
        }


        private void commandWait(Command cmd) {
            synchronized (cmd) {
                try {
                    if (!cmd.isFinished()) {
                        cmd.wait(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }



    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnListenListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnListenListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListenListener {
    }

    /**
     *  将ip数据库文件数据流写入到sdcard指定文件
     * @param is
     */
    public boolean writeToSDCard(InputStream is)
    {

        String root=	Environment.getExternalStorageDirectory().getAbsolutePath();

        File dir=new File(root+File.separator+DIR);
        Log.i(TAG, "dir path: " + dir.getAbsolutePath());
        //不存在目录，则创建目录
        if(!dir.exists())
            dir.mkdir();
        Log.i(TAG, "file path: "+dir.getAbsolutePath()+File.separator+FILE_NAME);
        File file=new File(dir.getAbsolutePath()+File.separator+FILE_NAME);
        //不存在文件，则创建文件
        if(!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        int temp=-1;
        byte[] buffer=new byte[1024];

        try {
            while((temp=is.read(buffer))!=-1)
            {
                fos.write(buffer, 0, temp);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            fos.flush();
            fos.close();
            is.close();

            return true;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;

    }
}
