package com.brotherjing.wifitxpower;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnWifiHotspotListener} interface
 * to handle interaction events.
 * Use the {@link WifiHotspotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WifiHotspotFragment extends Fragment {

    private OnWifiHotspotListener mListener;

    private Button btn_open,btn_connect,btn_create;
    private TextView tv_rssi;
    private LineChart lineChart;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WifiHotspotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WifiHotspotFragment newInstance() {
        WifiHotspotFragment fragment = new WifiHotspotFragment();
        return fragment;
    }

    public WifiHotspotFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wifi_hotspot, container, false);
        btn_connect = (Button)view.findViewById(R.id.btn_connect);
        btn_open = (Button)view.findViewById(R.id.btn_open);
        btn_create = (Button)view.findViewById(R.id.btn_hotspot);
        tv_rssi = (TextView)view.findViewById(R.id.tv_rssi);
        lineChart = (LineChart) view.findViewById(R.id.chart);

        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null)mListener.open();
            }
        });
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null)mListener.connect();
            }
        });
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null)mListener.create();
            }
        });
        LineData data = new LineData();
        lineChart.setData(data);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnWifiHotspotListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnWifiHotspotListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setRssi(int level){
        tv_rssi.setText(level+"");
        addEntry(level);
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleSize(2f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    private void addEntry(int level){
        LineData data = lineChart.getLineData();
        if(data!=null){
            LineDataSet set = data.getDataSetByIndex(0);
            if(set==null){
                set=createSet();
                data.addDataSet(set);
            }
            data.addXValue(data.getXValCount()+"");
            data.addEntry(new Entry(level, set.getEntryCount()), 0);
            lineChart.notifyDataSetChanged();
            lineChart.moveViewToX(data.getXValCount()-121);
        }
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
    public interface OnWifiHotspotListener {
        // TODO: Update argument type and name
        void connect();
        void create();
        void open();
    }

}
