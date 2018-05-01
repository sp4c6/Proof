package com.proof.ly.space.proof.Fragments.windows;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proof.ly.space.proof.Adapters.AAVPAdapter;
import com.proof.ly.space.proof.CustomViews.MViewPager;
import com.proof.ly.space.proof.Interfaces.FragmentInterface;
import com.proof.ly.space.proof.R;

/**
 * Created by aman on 4/16/18.
 */

public class MAuthFragment extends Fragment implements FragmentInterface {
    private static MViewPager vpager;
    private AAVPAdapter adapter;
    public MAuthFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.auth_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initObjects();
        initSetters();
    }

    @Override
    public void initViews(View itemView) {
        vpager = itemView.findViewById(R.id.vpager);
    }

    @Override
    public void initTypeface() {

    }

    @Override
    public void initOnClick() {

    }

    @Override
    public void initObjects() {
        adapter = new AAVPAdapter(getChildFragmentManager());
    }

    @Override
    public void initSetters() {
        vpager.setAdapter(adapter);
        vpager.setAllowedSwipeDirection(MViewPager.SwipeDirection.none);
    }

    public static void toRegisterPage(){
        vpager.setCurrentItem(2);
    }
}