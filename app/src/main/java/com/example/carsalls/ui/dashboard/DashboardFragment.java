package com.example.carsalls.ui.dashboard;

import static com.example.carsalls.R.drawable.autocaste;
import static com.example.carsalls.R.drawable.auva;
import static com.example.carsalls.R.drawable.avatar;
import static com.example.carsalls.R.drawable.cs;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.carsalls.R;
import com.example.carsalls.ValenBisi.ValenBisiApi;
import com.example.carsalls.databinding.FragmentDashboardBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String BASE_URL = "http://api.citybik.es/v2/networks/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        ValenBisiApi valenBisiApi = retrofit.create(ValenBisiApi.class);
        IMapController mapController = binding.map.getController();
        mapController.setZoom(14.5);
        GeoPoint startPoint = new GeoPoint(39.4715612, -0.3930977);
        mapController.setCenter(startPoint);


        MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), binding.map);
        myLocationNewOverlay.enableMyLocation();
        binding.map.getOverlays().add(myLocationNewOverlay);

        CompassOverlay compassOverlay = new CompassOverlay(requireContext(), new InternalCompassOrientationProvider(requireContext()), binding.map);
        compassOverlay.enableCompass();
        binding.map.getOverlays().add(compassOverlay);

        requestPermissionsIfNecessary(new String[]{
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }
        );

        Resources res = getResources();
        Drawable drawable = ResourcesCompat.getDrawable(res, autocaste, null);

        Resources res2 = getResources();
        Drawable drawable2 = ResourcesCompat.getDrawable(res, R.drawable.aut, null);

        Resources res3 = getResources();
        Drawable drawable3 = ResourcesCompat.getDrawable(res, cs, null);

        Resources res4 = getResources();
        Drawable drawable4 = ResourcesCompat.getDrawable(res, auva, null);

        //Concesionarios DATOS ESTATICOS:

        Marker marker = new Marker(binding.map);
        marker.setPosition(new GeoPoint(39.974464, -0.076447));
        marker.setTextLabelFontSize(40);
        marker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_car_repair_24));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
        marker.setTitle("AUTOMOVILES V CASTELLÓN");
        marker.setImage(drawable);
        binding.map.getOverlays().add(marker);

        Marker marker2 = new Marker(binding.map);
        marker2.setPosition(new GeoPoint(39.4715612, -0.3930977));
        marker2.setTextLabelFontSize(40);
        marker2.setIcon(getResources().getDrawable(R.drawable.ic_baseline_car_repair_24));
        marker2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
        marker2.setTitle("AUTOMOVILES VALENCIA");
        marker2.setImage(drawable2);
        binding.map.getOverlays().add(marker2);


        Marker marker3 = new Marker(binding.map);
        marker3.setPosition(new GeoPoint(39.487425, -0.447312));
        marker3.setTextLabelFontSize(40);
        marker3.setIcon(getResources().getDrawable(R.drawable.ic_baseline_car_repair_24));
        marker3.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
        marker3.setTitle("AUTOMOVILES MANISES");
        marker3.setImage(drawable3);
        binding.map.getOverlays().add(marker3);

        Marker marker4 = new Marker(binding.map);
        marker4.setPosition(new GeoPoint(39.462010, -0.375484));
        marker4.setTextLabelFontSize(40);
        marker4.setIcon(getResources().getDrawable(R.drawable.ic_baseline_car_repair_24));
        marker4.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
        marker4.setTitle("RUZAFA CARS");
        marker4.setImage(drawable4);
        binding.map.getOverlays().add(marker4);

    }//onViewCreated

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        binding.map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Configuration.getInstance().save(getContext(), prefs);
        binding.map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}