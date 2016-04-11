package com.kii.gatewaysample.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.kii.gatewaysample.R;
import com.kii.gatewaysample.ui.fragments.EndnodeFragment;
import com.kii.gatewaysample.ui.fragments.GatewayFragment;
import com.kii.gatewaysample.ui.fragments.PagerFragment;
import com.kii.gatewaysample.ui.views.SlidingTabLayout;
import com.kii.thingif.exception.StoredGatewayAPIInstanceNotFoundException;
import com.kii.thingif.gateway.GatewayAPI;

public class MainActivity extends AppCompatActivity {

    private GatewayAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(viewPager);
    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            this.api = GatewayAPI.loadFromStoredInstance(this, "gateway");
        } catch (StoredGatewayAPIInstanceNotFoundException e) {
            Intent intent = new Intent();
            intent.setClassName(this, GatewaySettingsActivity.class.getName());
            startActivity(intent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent();
            intent.setClassName(this, GatewaySettingsActivity.class.getName());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("GatewayAPI", this.api);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.api = (GatewayAPI) savedInstanceState.getParcelable("GatewayAPI");
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
                    return "Gateway";
                case 1:
                    return "Endnode";
                default:
                    throw new RuntimeException("Unxepected flow.");
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return GatewayFragment.newFragment(api);
                case 1:
                    return EndnodeFragment.newFragment(api);
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
