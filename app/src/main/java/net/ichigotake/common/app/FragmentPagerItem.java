package net.ichigotake.common.app;

import android.app.Fragment;

import net.ichigotake.common.lang.InvalidImplementationException;

public interface FragmentPagerItem {

    Fragment getItem(int position) throws InvalidImplementationException;

    CharSequence getPageTitle(int position);

    int getCount();

}