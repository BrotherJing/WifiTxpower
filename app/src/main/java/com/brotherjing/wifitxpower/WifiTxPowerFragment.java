package com.brotherjing.wifitxpower;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WifiTxPowerFragment.OnWifiTxPowerListener} interface
 * to handle interaction events.
 */
public class WifiTxPowerFragment extends Fragment implements View.OnClickListener{

    private EditText m_cEditTextLanName,et_txpower;
    private TextView m_cResultText;
    private CheckBox m_cUseMulticall;

    private OnWifiTxPowerListener mListener;

    public static WifiTxPowerFragment newInstance(){
        return new WifiTxPowerFragment();
    }

    public WifiTxPowerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main,container,false);
        this.m_cResultText = ((TextView)view.findViewById(R.id.tv_result));
        this.m_cEditTextLanName = ((EditText)view.findViewById(R.id.et_name));
        this.m_cUseMulticall = ((CheckBox)view.findViewById(R.id.cb_useiw));
        this.m_cUseMulticall.setOnClickListener(this);
        this.et_txpower = (EditText)view.findViewById(R.id.et_txpower);
        view.findViewById(R.id.btn_4dbm).setOnClickListener(this);
        view.findViewById(R.id.btn_11dbm).setOnClickListener(this);
        view.findViewById(R.id.btn_18dbm).setOnClickListener(this);
        view.findViewById(R.id.btn_25dbm).setOnClickListener(this);
        view.findViewById(R.id.btn_32dbm).setOnClickListener(this);
        view.findViewById(R.id.btn_auto).setOnClickListener(this);
        view.findViewById(R.id.btn_set).setOnClickListener(this);
        return view;
    }

    public void updateLog(String log){
        m_cResultText.setText(log);
    }

    public void setWlanName(String name){
        m_cEditTextLanName.setText(name);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnWifiTxPowerListener) activity;
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
    public interface OnWifiTxPowerListener {
        // TODO: Update argument type and name
        void SetTxPower(String name,int dbm);
        void AutoLanName();
        void UpdateResult(String name);
    }

    @Override
    public void onClick(View paramView)
    {
        if(mListener==null)return;
        String name = m_cEditTextLanName.getText().toString();
        switch (paramView.getId())
        {
            case R.id.btn_4dbm:
                mListener.SetTxPower(name,4);
                return;
            case R.id.btn_11dbm:
                mListener.SetTxPower(name,11);
                return;
            case R.id.btn_18dbm:
                mListener.SetTxPower(name,18);
                return;
            case R.id.btn_25dbm:
                mListener.SetTxPower(name,25);
                return;
            case R.id.btn_32dbm:
                mListener.SetTxPower(name,31);
                return;
            case R.id.btn_set:
                mListener.SetTxPower(name,Integer.parseInt(et_txpower.getText().toString()));
                return;
            case R.id.btn_auto:
                mListener.AutoLanName();
                mListener.UpdateResult(name);
                return;
            case R.id.cb_useiw:
                mListener.SetTxPower(name,getActivity().getSharedPreferences("Common", 0).getInt("TX power", 32));
                return;
        }
    }
}
