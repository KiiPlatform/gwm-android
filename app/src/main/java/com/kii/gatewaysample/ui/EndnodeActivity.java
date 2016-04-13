package com.kii.gatewaysample.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.kii.gatewaysample.R;
import com.kii.gatewaysample.ui.fragments.OnboardedNodesFragment;
import com.kii.gatewaysample.ui.fragments.PagerFragment;
import com.kii.gatewaysample.ui.fragments.PendingNodesFragment;
import com.kii.gatewaysample.ui.views.SlidingTabLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EndnodeActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.sliding_tabs)
    SlidingTabLayout slidingTabLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endnode);
        ButterKnife.bind(this);
        setSupportActionBar(this.toolbar);
        this.viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        this.slidingTabLayout.setViewPager(this.viewPager);
    }



    class MyAdapter extends FragmentPagerAdapter {

        private PagerFragment currentFragment;
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return "Pending Nodes";
                case 1:
                    return "Onboarded Nodes";
                default:
                    throw new RuntimeException("Unxepected flow.");
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PendingNodesFragment.newFragment();
                case 1:
                    return OnboardedNodesFragment.newFragment();
                default:
                    throw new RuntimeException("Unknown flow");
            }
        }
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (object instanceof PagerFragment && this.currentFragment != object) {
                if (getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                ((PagerFragment) object).onVisible(true);
                if (this.currentFragment != null) {
                    this.currentFragment.onVisible(false);
                }
                this.currentFragment = ((PagerFragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

    }


}
