package com.codemolly.drawing;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.FragmentGridPagerAdapter;


import java.util.List;

/**
 * Created by mrand on 9/30/15.
 */
public class SampleGridPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;
    private List mRows;

    public SampleGridPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
    }

    @Override
    public Fragment getFragment(int row, int column) {
        switch (column) {
            case 0:
                break;
            case 1:
            case 2:
                break;
        }
        return null;
    }



    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return 3;
    }
}
