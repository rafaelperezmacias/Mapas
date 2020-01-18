package com.zamnadev.mapas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String[] PERMISOS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private final int CODIGO = 12;

    private FrameLayout frmMapa;

    private SupportMapFragment supportMapFragment;

    private GoogleMap mMap;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frmMapa = findViewById(R.id.frmMapa);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }

        if (checharPermisos()) {
            proceso();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISOS, CODIGO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO) {
            boolean ban = true;
            for (int x : grantResults) {
                if (x != PackageManager.PERMISSION_GRANTED) {
                    ban = false;
                }
            }
            if (ban) {
                proceso();
                frmMapa.setVisibility(View.VISIBLE);
            } else {
                frmMapa.setVisibility(View.GONE);
            }

        }
    }

    private void proceso() {
        supportMapFragment.getMapAsync(this);
    }

    private boolean checharPermisos() {
        for (String permiso : PERMISOS) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permiso) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(17)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.addMarker(new MarkerOptions().position(latLng).title("Hola").snippet("Loma Dorada"));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setMyLocationEnabled(true);

        googleMap.setInfoWindowAdapter(new MiObjetoMapa("sadasd"));

        if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1.0f, listener);
        locationManager.removeUpdates(listener);

        Geocoder geocoder = new Geocoder(MainActivity.this,Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocationName("Loma sombreada #8246",1);
            if (addresses.size() > 0) {
                LatLng latLng = new LatLng(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(17)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.addMarker(new MarkerOptions().position(latLng).title("Mi casa").snippet("Loma sombreada #8246"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        };

    }

    class MiObjetoMapa implements GoogleMap.InfoWindowAdapter {

        private String img;

        public MiObjetoMapa(String img) {
            this.img = img;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.mapa,null);

            TextView txtTitulo = view.findViewById(R.id.txtMapa);

            txtTitulo.setText(marker.getTitle());


            TextView txtDescripcion = view.findViewById(R.id.txtDescripcion);

            txtDescripcion.setText(marker.getSnippet());

            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }


}
