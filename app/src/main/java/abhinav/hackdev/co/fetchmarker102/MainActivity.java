package abhinav.hackdev.co.fetchmarker102;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.concurrent.TimeUnit;

import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, MapboxMap.OnMarkerClickListener, MapboxMap.OnCameraChangeListener{

    public static final String VIDEO_URL = "VIDEO_URL";
    public static final int REQUEST_MAPVIEW_PERMS = 1 ;
    private static final String TAG = "SPLTAG";
    private static final String[] MAPVIEW_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private MapView mapView ;
    private MapboxMap mapboxMapGlobal ;
    private PublishSubject<LatLng> fetchMarkersSubject ;
    private MarkerDataList markerDataList ;

    private FragmentManager fm ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!hasPermissionsGranted(MAPVIEW_PERMS)){
            requestMapviewPermissions();
        }
        mapView = (MapView) findViewById(R.id.mapview) ;

        assert mapView != null;
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        setupReactiveExtensions() ;
    }

    private void setupReactiveExtensions() {
        Log.d(TAG, "setupReactiveExtensions() called with: " + "");
        fetchMarkersSubject = PublishSubject.create() ;
        fetchMarkersSubject
                .debounce(400, TimeUnit.MILLISECONDS)
                .map(new Func1<LatLng, MarkerDataList>() {
                    @Override
                    public MarkerDataList call(LatLng latLng) {
                        MarkerDataList markerDataList = new MarkerFetcher().fetcherNetworkCall(latLng);
                        Log.d(TAG, "call() called with: " + "latLng = [" + latLng + "]");
                        return markerDataList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MarkerDataList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MarkerDataList markerDataListLocal) {
//                        Log.d(TAG, "onNext: ");
//                        Log.d(TAG, "onNext() called with: " + "markerDataList = [" + markerDataList + "]");
//                        Log.d(TAG, "fetcherNetworkCall: " + markerDataList.getMarkerDataList().get(0).getUsername());
                        markerDataList = markerDataListLocal;
                        plotMarkers() ;
                    }
                });

    }

    private void plotMarkers() {
        mapboxMapGlobal.clear();
        for (MarkerDataList.MarkerData markerData : markerDataList.getMarkerDataList()){
            mapboxMapGlobal.addMarker(new MarkerOptions()
                                        .title(markerData.getUsername())
                                        .position(new LatLng((double) markerData.getLatVal(),(double) markerData.getLongVal() ))) ;
        }

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        mapboxMapGlobal = mapboxMap ;
        fetchMarkersSubject.onNext(mapboxMap.getCameraPosition().target);
        Log.d(TAG, "onMapReady() called with: " + "mapboxMap = [" + mapboxMap + "]");
        mapboxMapGlobal.setOnCameraChangeListener(this);
        mapboxMapGlobal.setOnMarkerClickListener(this);
    }



    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Log.d(TAG, "onMarkerClick() called with: " + "marker = [" + marker + "]");
        for (MarkerDataList.MarkerData markerData : markerDataList.getMarkerDataList()){
            if(marker.getTitle() == markerData.getUsername()){
                fetchUserVideo(markerData.getVidUrl()) ;
                break ;
            }
        }

        return false;
    }

    private void fetchUserVideo(String vidUrl) {
        Intent i = new Intent(this, VideoPlayer.class);
        i.putExtra(VIDEO_URL,vidUrl);
        startActivity(i);
    }

    @Override
    public void onCameraChange(CameraPosition position) {
        Log.d(TAG, "onCameraChange() called with: " + "position = [" + position + "]");
        //new MarkerFetcher().fetcherNetworkCall(position.target);
        fetchMarkersSubject.onNext(position.target);
    }

    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    private void requestMapviewPermissions() {
        if (shouldShowRequestPermissionRationale(MAPVIEW_PERMS)) {
            new ConfirmationDialog().show(fm, "test");
        } else {
            ActivityCompat.requestPermissions(this, MAPVIEW_PERMS, REQUEST_MAPVIEW_PERMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_MAPVIEW_PERMS) {
            if (grantResults.length == MAPVIEW_PERMS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        ErrorDialog.newInstance(getString(R.string.permission_request))
                                .show(fm, TAG);
                        break;
                    }
                }
            } else {
                ErrorDialog.newInstance(getString(R.string.permission_request))
                        .show(fm, TAG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static class ConfirmationDialog extends android.app.DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity parent = getActivity();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.permission_request)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(parent, MAPVIEW_PERMS,
                                    REQUEST_MAPVIEW_PERMS);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                    .create();
        }

    }

    public static class ErrorDialog extends android.app.DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create();

        }

    }

}
