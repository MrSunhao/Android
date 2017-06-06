package com.company.sun.intelligentfan;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * Created by Sun on 2017/5/16.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 5;
    private WindsFragment windsFragment = null;
    private AppointmentFragment appointmentFragment = null;
    private TimerFragment timerFragment = null;
    private CountFragment countFragment = null;
    private SettingFragment settingFragment = null;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        windsFragment = new WindsFragment();
        appointmentFragment = new AppointmentFragment();
        timerFragment = new TimerFragment();
        countFragment = new CountFragment();
        settingFragment = new SettingFragment();
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MainActivity.PAGE_ONE:
                fragment = windsFragment;
                break;
            case MainActivity.PAGE_TWO:
                fragment = appointmentFragment;
                break;
            case MainActivity.PAGE_THREE:
                fragment = timerFragment;
                break;
            case MainActivity.PAGE_FOUR:
                fragment = countFragment;
                break;
            case MainActivity.PAGE_FIVE:
                fragment = settingFragment;
                break;
        }
        return fragment;
    }
}
