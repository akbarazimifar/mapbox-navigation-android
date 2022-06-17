package com.mapbox.navigation.qa_test_app.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;

import com.mapbox.geojson.Point;
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI;
import com.mapbox.navigation.dropin.NavigationViewListener;
import com.mapbox.navigation.qa_test_app.databinding.LayoutFragmentNavigationViewBinding;

/**
 * Fragment for testing usage of NavigationView in Java code.
 */
@OptIn(markerClass = ExperimentalPreviewMapboxNavigationAPI.class)
public class NavigationViewFragment extends Fragment {

    public static String TAG = NavigationViewFragment.class.getSimpleName();

    private LayoutFragmentNavigationViewBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentNavigationViewBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.navigationView.addListener(new NavigationViewListener() {
            @Override
            public void onDestinationChanged(@Nullable Point destination) {
                Log.d(TAG, "onDestinationChanged " + destination);
            }

            @Override
            public void onFreeDriveStarted() {
                Log.d(TAG, "onFreeDriveStarted");
            }

            @Override
            public void onDestinationPreviewStared() {
                Log.d(TAG, "onDestinationPreviewStared");
            }

            @Override
            public void onRoutePreviewStared() {
                Log.d(TAG, "onRoutePreviewStared");
            }

            @Override
            public void onActiveNavigationStared() {
                Log.d(TAG, "onActiveNavigationStared");
            }

            @Override
            public void onArrivalStared() {
                Log.d(TAG, "onArrivalStared");
            }
        });
    }
}