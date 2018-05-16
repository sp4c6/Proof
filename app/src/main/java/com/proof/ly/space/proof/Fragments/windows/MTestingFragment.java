package com.proof.ly.space.proof.Fragments.windows;

import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.proof.ly.space.proof.Adapters.ViewPagerAdapter;
import com.proof.ly.space.proof.CustomViews.MViewPager;
import com.proof.ly.space.proof.Data.JsonQuestion;
import com.proof.ly.space.proof.Data.Question;
import com.proof.ly.space.proof.Fragments.TestingFragment;
import com.proof.ly.space.proof.Helpers.QManager;
import com.proof.ly.space.proof.Helpers.SettingsManager;
import com.proof.ly.space.proof.Helpers.TinyDB;
import com.proof.ly.space.proof.Interfaces.FragmentInterface;
import com.proof.ly.space.proof.MainActivity;
import com.proof.ly.space.proof.R;

import static android.content.ContentValues.TAG;
import static com.proof.ly.space.proof.Helpers.SettingsManager.colored;
import static com.proof.ly.space.proof.Helpers.SettingsManager.cycleMode;
import static com.proof.ly.space.proof.Helpers.SettingsManager.autoflip;
import static com.proof.ly.space.proof.Helpers.SettingsManager.quesCount;

import java.util.ArrayList;


/**
 * Created by aman on 4/14/18.
 */

public class MTestingFragment extends Fragment implements FragmentInterface {

    private static MViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private ArrayList<Question> mQuestionArrayList = new ArrayList<>();
    private ArrayList<JsonQuestion> mJsonQuestionArrayList = new ArrayList<>();
    public static final int MINI = 20, BIGI = 40;
    public static int COUNT = BIGI;
    private static Handler mHandler;
    private Menu mMenu;
    private SettingsManager mSettingsManager;
    private QManager mQManager;
    private TinyDB mTinyDB;
    public static boolean loading = false;


    public MTestingFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingsManager = ((MainActivity) getActivity()).getmSettingsManager();
        colored = mSettingsManager.getColoredState();
        cycleMode = mSettingsManager.getCycleModeState();
        autoflip = mSettingsManager.getAutoflipState();
        quesCount = mSettingsManager.getQuesCount();
        mTinyDB = ((MainActivity) getActivity()).getTinyDB();

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.testing_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        new loading().execute();

    }

    @Override
    public void initViews(View itemView) {
        mViewPager = itemView.findViewById(R.id.vpager);
    }

    @Override
    public void initTypeface() {


    }

    @Override
    public void initOnClick() {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                if (position == 1)
                    mViewPager.setAllowedSwipeDirection(MViewPager.SwipeDirection.right);
                else if (position == mJsonQuestionArrayList.size())
                    mViewPager.setAllowedSwipeDirection(MViewPager.SwipeDirection.left);
                else if (position == mJsonQuestionArrayList.size() + 1) {
                    mViewPager.setAllowedSwipeDirection(MViewPager.SwipeDirection.none);
                    for (int i = 0; i < mMenu.size(); i++) {
                        mMenu.getItem(i).setVisible(false);
                    }
                } else mViewPager.setAllowedSwipeDirection(MViewPager.SwipeDirection.all);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initObjects() {

        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), mJsonQuestionArrayList.size());
        mHandler = new Handler();
        mQManager = ((MainActivity) getActivity()).getmQuestionManager();


    }

    @Override
    public void initSetters() {
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAllowedSwipeDirection(MViewPager.SwipeDirection.none);


    }


    private class loading extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //initViews();
            initTypeface();
            initObjects();
            initSetters();
            loading = true;
            Log.d(TAG, "onPreExecute: " + mViewPagerAdapter.getCount());


        }

        @Override
        protected Void doInBackground(Void... voids) {


            COUNT = quesCount ? BIGI : MINI;
            mJsonQuestionArrayList = mQManager.generateQFromDB(COUNT);
            mQuestionArrayList = mQManager.generateQuestionsList(); //question.size = count

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loading = false;
            mViewPagerAdapter.setCount(mQuestionArrayList.size());
            initOnClick();
            Log.d(TAG, "onPreExecute: " + mViewPagerAdapter.getCount());
            Log.d("MTestingFragment", "size: " + mQuestionArrayList.size());
            QManager.isGenerate = true;


        }
    }

    public static void nextPage(final int size) {
        if (SettingsManager.autoflip) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mViewPager.getCurrentItem() != size)
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
            }, 320);
        }

    }

    public static void restartTesting() {
        mViewPager.setCurrentItem(1);
    }

    public static void startTesting() {
        mViewPager.setCurrentItem(1);
    }

    public static void finishTesting(final int size) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(size + 1);
            }
        }, 320);

    }

    public void stopTesting() {
        for (int i = 0; i < mQuestionArrayList.size(); i++) {
            if (!mQuestionArrayList.get(i).isChecked()) {
                for (int j = 0; j < mQuestionArrayList.get(i).getArrayListAnswers().size(); j++) {
                    if (mQuestionArrayList.get(i).getArrayListAnswers().get(j).isCorrect()) {
                        mQuestionArrayList.get(i).getArrayListAnswers().get(j).setIsChecked(1);
                        mQuestionArrayList.get(i).getArrayListAnswers().get(j).setCorrectChecked(true);
                    }

                }
                for (int j = 0; j < mQuestionArrayList.get(i).getArrayListAnswers().size(); j++) {
                    if (mQuestionArrayList.get(i).getArrayListAnswers().get(j).getIsChecked() != 1) {
                        mQuestionArrayList.get(i).getArrayListAnswers().get(j).setIsChecked(2);
                        mQuestionArrayList.get(i).getArrayListAnswers().get(j).setEnabled(false);
                    }
                }

            }
        }
        finishTesting(mJsonQuestionArrayList.size());

    }

    public void all() {


        String s = "android:switcher:"; //2131230929 = R.id.mViewPager


        for (int i = 0; i <= mQuestionArrayList.size(); i++) {
            Fragment fragment = getChildFragmentManager().findFragmentByTag(s + R.id.vpager + ":" + i);
            if (fragment != null && fragment instanceof TestingFragment) {
                ((TestingFragment) fragment).getAdapter().notifyDataSetChanged();
            }
        }
        mViewPagerAdapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_testing, menu);

        if (menu.getItem(0).getIcon() != null)
            if (colored)
                menu.getItem(0).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            else
                menu.getItem(0).getIcon().setColorFilter(((MainActivity) getActivity()).getClickedColor(), PorterDuff.Mode.SRC_ATOP);
        this.mMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.colored:

                colored = !colored;
                if (colored)
                    item.getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                else
                    item.getIcon().setColorFilter(((MainActivity) getActivity()).getClickedColor(), PorterDuff.Mode.SRC_ATOP);
                mSettingsManager.saveColoredState(colored);
                all();
                break;
            case R.id.stop:
                stopTesting();
                break;
            case R.id.settings:
                ((MainActivity) getActivity()).replaceFragment(new MSettingsFragment(), getResources().getString(R.string.tag_settings));
                break;
        }
        return false;
    }


    public TinyDB getTinyDB() {

        return mTinyDB;
    }
}
