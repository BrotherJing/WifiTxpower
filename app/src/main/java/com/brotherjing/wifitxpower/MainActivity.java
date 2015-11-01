package com.brotherjing.wifitxpower;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    private final String TAG = "yj";

    private EditText m_cEditTextLanName,et_txpower;
    private TextView m_cResultText;
    private CheckBox m_cUseMulticall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.m_cResultText = ((TextView)findViewById(R.id.tv_result));
        this.m_cEditTextLanName = ((EditText)findViewById(R.id.et_name));
        this.m_cUseMulticall = ((CheckBox)findViewById(R.id.cb_useiw));
        this.m_cUseMulticall.setOnClickListener(this);
        this.et_txpower = (EditText)findViewById(R.id.et_txpower);
        findViewById(R.id.btn_4dbm).setOnClickListener(this);
        findViewById(R.id.btn_11dbm).setOnClickListener(this);
        findViewById(R.id.btn_18dbm).setOnClickListener(this);
        findViewById(R.id.btn_25dbm).setOnClickListener(this);
        findViewById(R.id.btn_32dbm).setOnClickListener(this);
        findViewById(R.id.btn_auto).setOnClickListener(this);
        findViewById(R.id.btn_set).setOnClickListener(this);
        SharedPreferences localSharedPreferences = getSharedPreferences("Common", 0);
        this.m_cEditTextLanName.setText(localSharedPreferences.getString("Lan name", ""));
        /*if (this.m_cEditTextLanName.getText().toString().length() == 0)
            AutoLanName();*/
        this.m_cUseMulticall.setChecked(localSharedPreferences.getBoolean("Use iwmulticall", false));
        UpdateResult();
    }

    private void AutoLanName()
    {
        java.lang.Process localProcess = null;
        this.m_cEditTextLanName.setText("");
        try
        {
            Log.i(TAG,"start auto name");
            localProcess = Runtime.getRuntime().exec("su -c getprop");
            BufferedReader localBufferedReader = null;
            String str;
            /*if (localProcess.waitFor() == 0)
            {

            }*/
            localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
            while (true) {
                str = localBufferedReader.readLine();
                Log.i(TAG,str);
                if (str != null) {
                    int i = str.indexOf(':');
                    if ((i < 0) || (!str.substring(0, i).replace("[", "").replace("]", "").trim().equalsIgnoreCase("wifi.interface")))
                        continue;
                    this.m_cEditTextLanName.setText(str.substring(i + 1).replace("[", "").replace("]", "").trim());
                }else{
                    break;
                }
            }
            localBufferedReader.close();
            /*while (true)
            {
                localBufferedReader.close();
                return;
                localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getErrorStream()));
                break;
                label96: int i = str.indexOf(':');
                if ((i < 0) || (!str.substring(0, i).replace("[", "").replace("]", "").trim().equalsIgnoreCase("wifi.interface")))
                    break;
                this.m_cEditTextLanName.setText(str.substring(i + 1).replace("[", "").replace("]", "").trim());
            }*/
        }
        catch (Exception localException)
        {
        }
        finally
        {
            if (localProcess != null)
                localProcess.destroy();
        }
    }

    private void SetTxPower(int paramInt)
    {
        this.m_cResultText.setText("");
        java.lang.Process localProcess = null;
        StringBuilder localStringBuilder = new StringBuilder();
        String str1 = this.m_cEditTextLanName.getText().toString();
        try
        {
            Log.i(TAG,"su");
            localProcess = Runtime.getRuntime().exec("su");
            DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
            if (this.m_cUseMulticall.isChecked())
            {
                Object[] arrayOfObject2 = new Object[2];
                arrayOfObject2[0] = str1;
                arrayOfObject2[1] = paramInt;
                localDataOutputStream.writeBytes(String.format("iwmulticall iwconfig %s txpower %d\n", arrayOfObject2));
                //localDataOutputStream.flush();
            }else{
                Log.i(TAG,"iwconfig txpower");
                Object[] arrayOfObject1 = new Object[2];
                arrayOfObject1[0] = str1;
                arrayOfObject1[1] = paramInt;
                localDataOutputStream.writeBytes(String.format("iwconfig %s txpower %d\n", arrayOfObject1));
                //localDataOutputStream.flush();
            }
            Log.i(TAG,"wait");
            /*if (localProcess.waitFor() != 0)
                return;*/
            /*BufferedReader localBufferedReader;
            String str2;
            localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
            str2 = localBufferedReader.readLine();
            if (str2 != null){
                localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getErrorStream()));
                localStringBuilder.append(str2);
                localStringBuilder.append("\n");
            }
            localBufferedReader.close();*/
            SharedPreferences.Editor localEditor = getSharedPreferences("Common", 0).edit();
            localEditor.putInt("TX power", paramInt);
            localEditor.putString("Lan name", str1);
            localEditor.commit();
            //this.m_cResultText.setText(localStringBuilder.toString());
            this.m_cResultText.setText("success");
            UpdateResult();
            //UpdateRemoteIcons();
            while (true)
            {
                localDataOutputStream.writeBytes("exit\n");
                //localDataOutputStream.flush();
                if (localProcess.waitFor() == 0)
                    break;

            }
        }
        catch (Exception localException)
        {
        }
        finally
        {
            if (localProcess != null)
                localProcess.destroy();
        }
    }

    protected void UpdateResult()
    {
        this.m_cResultText.setText("");
        java.lang.Process localProcess = null;
        StringBuilder localStringBuilder = new StringBuilder();
        String str1 = this.m_cEditTextLanName.getText().toString();
        str1 = "wlan0";
        try
        {
            boolean bool = this.m_cUseMulticall.isChecked();
            localProcess = null;
            BufferedReader localBufferedReader = null;
            if (bool)
            {
                localProcess = Runtime.getRuntime().exec("iwmulticall iwconfig " + str1);
            }else {
                Log.i(TAG,"iwconfig "+str1);
                localProcess = Runtime.getRuntime().exec("su -c iwconfig " + str1);
            }
            Log.i(TAG,"get input stream");
            localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
            /*if (localProcess.waitFor() != 0)
                localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getErrorStream()));
            else
                localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));*/
            while (true) {
                String str2 = localBufferedReader.readLine();
                if (str2 == null) {
                    localBufferedReader.close();
                    localProcess.destroy();
                    break;
                }
                Log.i(TAG,str2);
                localStringBuilder.append(str2);
                localStringBuilder.append("\n");
            }
            this.m_cResultText.setText(localStringBuilder.toString());
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
            Log.i(TAG,"exception");
        }
        finally
        {
            if (localProcess != null)
                localProcess.destroy();
        }
    }

    @Override
    public void onClick(View paramView)
    {
        switch (paramView.getId())
        {
            case R.id.btn_4dbm:
                SetTxPower(4);
                return;
            case R.id.btn_11dbm:
                SetTxPower(11);
                return;
            case R.id.btn_18dbm:
                SetTxPower(18);
                return;
            case R.id.btn_25dbm:
                SetTxPower(25);
                return;
            case R.id.btn_32dbm:
                SetTxPower(31);
                return;
            case R.id.btn_set:
                SetTxPower(Integer.parseInt(et_txpower.getText().toString()));
                return;
            case R.id.btn_auto:
                AutoLanName();
                UpdateResult();
                return;
            case R.id.cb_useiw:
                SetTxPower(getSharedPreferences("Common", 0).getInt("TX power", 32));
                return;
        }
    }
}
