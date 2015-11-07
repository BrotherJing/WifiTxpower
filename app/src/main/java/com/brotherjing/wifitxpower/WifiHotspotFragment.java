package com.brotherjing.wifitxpower;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


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

    private Button btn_connect,btn_create;

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
        btn_create = (Button)view.findViewById(R.id.btn_hotspot);

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
    }

}
