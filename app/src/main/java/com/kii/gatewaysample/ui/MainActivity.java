package com.kii.gatewaysample.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.kii.gatewaysample.R;
import com.kii.gatewaysample.ui.fragments.AppWizardFragment;
import com.kii.gatewaysample.ui.fragments.GatewayWizardFragment;
import com.kii.gatewaysample.ui.fragments.KiiUserWizardFragment;
import com.kii.gatewaysample.ui.fragments.OnboardWizardFragment;
import com.kii.gatewaysample.ui.fragments.WizardFragment;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements WizardFragment.WizardController {

    private static final int WIZARD_PAGE_SIZE = 4;
    private FragmentStatePagerAdapter adapter;
    @Bind(R.id.step_pager)
    ViewPager viewPager;
    @Bind(R.id.wizard_next_button)
    Button nextButton;
    @Bind(R.id.wizard_previous_button)
    Button previousButton;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.adapter = new WizardPagerAdapter(getSupportFragmentManager());

        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                View currentFocus = MainActivity.this.getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
                if (currentPosition < position) {
                    ((WizardFragment) adapter.instantiateItem(viewPager, currentPosition)).onInactivate(WizardFragment.EXIT_NEXT);
                } else {
                    ((WizardFragment) adapter.instantiateItem(viewPager, currentPosition)).onInactivate(WizardFragment.EXIT_PREVIOUS);
                }
                WizardFragment wizardFragment = (WizardFragment) adapter.instantiateItem(viewPager, position);
                wizardFragment.onActivate();
                currentPosition = position;
            }
        };
        this.viewPager.addOnPageChangeListener(onPageChangeListener);
        this.viewPager.setAdapter(this.adapter);
        this.nextButton = (Button)findViewById(R.id.wizard_next_button);
        this.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = viewPager.getCurrentItem();
                final WizardFragment currentWizardFragment = (WizardFragment) adapter.instantiateItem(viewPager, position);
                if (position + 1 < WIZARD_PAGE_SIZE) {
                    new AndroidDeferredManager().when(new DeferredAsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackgroundSafe(final Void... params) throws Exception {
                            currentWizardFragment.execute();
                            return null;
                        }
                    }).done(new DoneCallback<Void>() {
                        @Override
                        public void onDone(final Void result) {
                            viewPager.setCurrentItem(position + 1);
                            WizardFragment wizardFragment = (WizardFragment) adapter.getItem(position + 1);
                            nextButton.setText(wizardFragment.getNextButtonText());
                            previousButton.setText(wizardFragment.getPreviousButtonText());
                        }
                    }).fail(new FailCallback<Throwable>() {
                        @Override
                        public void onFail(final Throwable tr) {
                            Toast.makeText(MainActivity.this, tr.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (position + 1 == WIZARD_PAGE_SIZE) {
                    new AndroidDeferredManager().when(new DeferredAsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackgroundSafe(final Void... params) throws Exception {
                            currentWizardFragment.execute();
                            return null;
                        }
                    }).done(new DoneCallback<Void>() {
                        @Override
                        public void onDone(final Void result) {
                            Toast.makeText(MainActivity.this, "Gateway is onboarded", Toast.LENGTH_SHORT).show();
                        }
                    }).fail(new FailCallback<Throwable>() {
                        @Override
                        public void onFail(final Throwable tr) {
                            Toast.makeText(MainActivity.this, tr.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        this.nextButton.setText("Next");
        this.previousButton = (Button)findViewById(R.id.wizard_previous_button);
        this.previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getCurrentItem();
                WizardFragment wizardFragment = (WizardFragment) adapter.instantiateItem(viewPager, position);
                if (position > 0) {
                    position--;
                    viewPager.setCurrentItem(position);
                    wizardFragment = (WizardFragment) adapter.getItem(position);
                    nextButton.setText(wizardFragment.getNextButtonText());
                    previousButton.setText(wizardFragment.getPreviousButtonText());
                } else {
                    finish();
                }
            }
        });
        this.previousButton.setText("Exit");

    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setNextButtonEnabled(boolean enabled) {
        this.nextButton.setEnabled(enabled);
    }

    private class WizardPagerAdapter extends FragmentStatePagerAdapter {
        private static final int PAGE_APP_SETTING  = 0;
        private static final int PAGE_KII_USER_SETTING = 1;
        private static final int PAGE_GATEWAY_SETTING = 2;
        private static final int PAGE_ONBOARD_GATEWAY = 3;

        public WizardPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            WizardFragment fragment = null;
            switch (position) {
                case PAGE_APP_SETTING:
                    fragment = AppWizardFragment.newFragment();
                    break;
                case PAGE_KII_USER_SETTING:
                    fragment = KiiUserWizardFragment.newFragment();
                    break;
                case PAGE_GATEWAY_SETTING:
                    fragment = GatewayWizardFragment.newFragment();
                    break;
                case PAGE_ONBOARD_GATEWAY:
                    fragment = OnboardWizardFragment.newFragment();
                    break;
            }
            fragment.setController(MainActivity.this);
            return fragment;
        }
        @Override
        public int getCount() {
            return WIZARD_PAGE_SIZE;
        }
    }
}
