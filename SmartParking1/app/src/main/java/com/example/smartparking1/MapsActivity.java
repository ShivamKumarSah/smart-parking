package com.example.smartparking1;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.clustering.ClusterItem;
import android.Manifest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;

import android.content.DialogInterface;
import android.content.Intent;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ListView listView;
    private List<Marker> markerList;
    private List<HashMap<String, Object>> facilityList;
    private FacilityAdapter adapter;
    private DatabaseReference databaseReference;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    public View getInfoWindow(Marker marker) {
        return null; // Returning null here will use the default info window
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Inflate a custom layout for the info window
        View infoView = getLayoutInflater().inflate(R.layout.custom_info_window, null);

        // Find the views in the custom layout
        TextView titleTextView = infoView.findViewById(R.id.titleTextView);
        TextView snippetTextView = infoView.findViewById(R.id.snippetTextView);

        // Set the title and snippet
        titleTextView.setText(marker.getTitle());
        snippetTextView.setText(marker.getSnippet());

        return infoView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize the marker list
        markerList = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // to show floating parking view over the map
        FloatingActionButton fab = findViewById(R.id.fab);
        LinearLayout parkingSlotsView = findViewById(R.id.parkingSlotsView);
        parkingSlotsView.setVisibility(View.VISIBLE);

        // on arrow button press
        fab.setOnClickListener(new View.OnClickListener() {
            boolean isExpanded = false;
            int heightInDp = (int) getResources().getDimension(R.dimen.view_height_220dp);
            Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            @Override
            public void onClick(View view) {
                if (isExpanded) {
                    parkingSlotsView.getLayoutParams().height = heightInDp;
                    parkingSlotsView.startAnimation(slideDown);
                    fab.animate().rotation(180f); // Rotate the FAB to 180 degrees (down position)
                } else {
                    parkingSlotsView.startAnimation(slideUp);
                    parkingSlotsView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    fab.animate().rotation(0f); // Rotate the FAB to 0 degrees (up position)
                }
                isExpanded = !isExpanded;
                parkingSlotsView.requestLayout();
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // to show the parking area details in list :
        listView = findViewById(R.id.listView);
        facilityList = new ArrayList<>();
        adapter = new FacilityAdapter(this, R.layout.list_item_table, facilityList);
        listView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        fetchDataFromFirebase();

        // Set the click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected facility from the list
                HashMap<String, Object> selectedFacility = facilityList.get(position);

                // Check if selectedFacility is not null
                if (selectedFacility != null) {
                    // Extract the required information from the selectedFacility HashMap
                    String parkingName = Objects.requireNonNull(selectedFacility.get("name")).toString();
                    String address = Objects.requireNonNull(selectedFacility.get("location")).toString();
                    String availableSlots = Objects.requireNonNull(selectedFacility.get("Total Empty Slots")).toString();

                    // Create a Dialog or AlertDialog to show the popup card
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    View cardView = getLayoutInflater().inflate(R.layout.popup_card_layout, null);
                    builder.setView(cardView);

                    // Find the views inside the cardView
                    TextView titleTextView = cardView.findViewById(R.id.titleTextView);
                    TextView addressTextView = cardView.findViewById(R.id.addressTextView);
                    TextView availableSlotsView = cardView.findViewById(R.id.availableSlots);
                    TextView bookingBtn = cardView.findViewById(R.id.bookingButton);

                    // Set the values for the popup views
                    titleTextView.setText(parkingName);
                    addressTextView.setText(address);
                    if(availableSlots.equals("0")) {
                        availableSlotsView.setText("All slots Full.");
                    }
                    else {
                        availableSlotsView.setText("Total " + availableSlots + " slots available.");
                    }

                    // Show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    bookingBtn.setOnClickListener(v -> {
                        Intent intent = new Intent(MapsActivity.this, SlotChoose.class);
//                        Intent intent = new Intent(MapsActivity.this, BookingActivity.class);
                        intent.putExtra("selectedFacility", selectedFacility);
                        startActivity(intent);
                    });

                }
            }
        });
    }

    private void fetchDataFromFirebase() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                // Retrieve the facility object as a HashMap from the DataSnapshot
                HashMap<String, Object> facility = (HashMap<String, Object>) dataSnapshot.getValue();
                facilityList.add(facility);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            // Implement other ChildEventListener methods as needed

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur while retrieving data from Firebase
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set the custom info window adapter
        mMap.setInfoWindowAdapter(this);

        // Enable multiple info windows
        ClusterManager<MarkerItem> clusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 11.0f));
                }
            }
        });

        LatLng kolkataLatLng = new LatLng(22.567957, 88.415656);
        MarkerOptions kolkataMarkerOptions = new MarkerOptions()
                .position(kolkataLatLng)
                .title("Kolkata")
                .snippet("Slots: 1")
                .icon(bitmapDescriptorFromResourceWithColor(R.drawable.baseline_directions_car_24, "#EE4B2B", 98)); // Increase size to 98 pixels
        Marker kolkataMarker = mMap.addMarker(kolkataMarkerOptions);
        markerList.add(kolkataMarker);
        kolkataMarker.showInfoWindow(); // Show tooltip

        LatLng barrackporeLatLng = new LatLng(22.7661, 88.3516);
        MarkerOptions barrackporeMarkerOptions = new MarkerOptions()
                .position(barrackporeLatLng)
                .title("Barrackpore")
                .snippet("Slots: 1")
                .icon(bitmapDescriptorFromResourceWithColor(R.drawable.baseline_directions_car_24, "#EE4B2B", 98)); // Increase size to 98 pixels
        Marker barrackporeMarker = mMap.addMarker(barrackporeMarkerOptions);
        markerList.add(barrackporeMarker);
        barrackporeMarker.showInfoWindow(); // Show tooltip

        // Show all info windows
        kolkataMarker.showInfoWindow();
        barrackporeMarker.showInfoWindow();

    }
    private BitmapDescriptor bitmapDescriptorFromResourceWithColor(int resourceId, String colorHex, int size) {
        Drawable drawable = ContextCompat.getDrawable(this, resourceId);
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(Color.parseColor(colorHex), PorterDuff.Mode.SRC_IN);
            drawable.setBounds(0, 0, size, size);
        }
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (marker != null && marker.getTitle().equals("Kolkata")) {
            showCustomDialog("Kolkata");
        }

        if (marker != null && marker.getTitle().equals("Barrackpore")) {
            showCustomDialog("Barrackpore");
        }
    }

    private void showCustomDialog(String location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(location);
        builder.setMessage("Slots: 1");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null && marker.getTitle().equals("Kolkata")) {
            Intent intent1 = new Intent(MapsActivity.this, ParkingSlots.class);
            startActivity(intent1);
        }
        if (marker != null && marker.getTitle().equals("Barrackpore")) {
            Intent intent2 = new Intent(MapsActivity.this, ParkingSlots.class);
            startActivity(intent2);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, re-initiate the map
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
        }
    }
}

