package com.brotherjing.wifitxpower;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class ControlPanelActivity extends AppCompatActivity implements WifiTxPowerFragment.OnWifiTxPowerListener,WifiHotspotFragment.OnWifiHotspotListener{

    final private String TAG = "ControlPanel";

    WifiAdmin mWifiAdmin;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_control_panel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new Fragment[2];
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0){
                fragments[0] =WifiTxPowerFragment.newInstance();
                return fragments[0];
            }
            else {
                fragments[1]=WifiHotspotFragment.newInstance();
                return fragments[1];
            }
        }

        public Fragment getFragment(int position){
            return fragments[position];
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "WifiTxPower";
                case 1:
                    return "Wifi Hotspot";
            }
            return null;
        }
    }

    public void SetTxPower(String name,int paramInt)
    {
        //this.m_cResultText.setText("");
        java.lang.Process localProcess = null;
        StringBuilder localStringBuilder = new StringBuilder();
        //String str1 = this.m_cEditTextLanName.getText().toString();
        try
        {
            Log.i(TAG, "su");
            localProcess = Runtime.getRuntime().exec("su");
            DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());

            Log.i(TAG,"iwconfig txpower");
            Object[] arrayOfObject1 = new Object[2];
            arrayOfObject1[0] = name;
            arrayOfObject1[1] = paramInt;
            localDataOutputStream.writeBytes(String.format("iwconfig %s txpower %d\n", arrayOfObject1));
            //localDataOutputStream.flush();

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
            localEditor.putString("Lan name", name);
            localEditor.commit();
            //this.m_cResultText.setText(localStringBuilder.toString());
            //this.m_cResultText.setText("success");
            UpdateResult(name);
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

    public void UpdateResult(String name)
    {
        //this.m_cResultText.setText("");
        java.lang.Process localProcess = null;
        StringBuilder localStringBuilder = new StringBuilder();
        String str1 = name;
        try
        {
            //boolean bool = this.m_cUseMulticall.isChecked();
            localProcess = null;
            BufferedReader localBufferedReader = null;

            Log.i(TAG,"iwconfig "+str1);
            localProcess = Runtime.getRuntime().exec("su -c iwconfig " + str1);

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
            ((WifiTxPowerFragment)(mSectionsPagerAdapter.getFragment(0))).updateLog(localStringBuilder.toString());
            //this.m_cResultText.setText(localStringBuilder.toString());
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
    public void AutoLanName() {
        java.lang.Process localProcess = null;
        //this.m_cEditTextLanName.setText("");
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
                    ((WifiTxPowerFragment)(mSectionsPagerAdapter.getFragment(0))).setWlanName(str.substring(i + 1).replace("[", "").replace("]", "").trim());
                    //this.m_cEditTextLanName.setText(str.substring(i + 1).replace("[", "").replace("]", "").trim());
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

    @Override
    public void connect() {
        mWifiAdmin = new WifiAdmin(this) {

            @Override
            public void myUnregisterReceiver(BroadcastReceiver receiver) {
                // TODO Auto-generated method stub
                unregisterReceiver(receiver);
            }

            @Override
            public Intent myRegisterReceiver(BroadcastReceiver receiver,
                                             IntentFilter filter) {
                // TODO Auto-generated method stub
                registerReceiver(receiver, filter);
                return null;
            }

            @Override
            public void onNotifyWifiConnected() {
                // TODO Auto-generated method stub
                Log.v(TAG, "have connected success!");
                Log.v(TAG, "###############################");
            }

            @Override
            public void onNotifyWifiConnectFailed() {
                // TODO Auto-generated method stub
                Log.v(TAG, "have connected failed!");
                Log.v(TAG, "###############################");
            }
        };
        mWifiAdmin.openWifi();
        mWifiAdmin.addNetwork(mWifiAdmin.createWifiInfo("ssid", "pswd", WifiAdmin.TYPE_WPA));
    }

    @Override
    public void create() {
        WifiApAdmin wifiAp = new WifiApAdmin(this);
        wifiAp.startWifiAp("\"HotSpot\"", "hhhhhh123");
    }
}
