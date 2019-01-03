package travel.maptracking.activity;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPolyline;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapPolyline;
import com.here.android.mpa.mapping.SupportMapFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import travel.maptracking.R;
import travel.maptracking.model.scheduleRoute;
import travel.maptracking.util.Util;

public class MapFragmentView {
    private SupportMapFragment m_mapFragment;
    private AppCompatActivity m_activity;
    private Map m_map;
    private Button m_polyline_button;
    private MapPolyline m_polyline;
    List<scheduleRoute.Data> dataRoute = new ArrayList<>();
    private String fromactivity="";

    public MapFragmentView(AppCompatActivity activity) {
        m_activity = activity;
        initMapFragment();
    }

    public MapFragmentView(AppCompatActivity activity,String from) {

        m_activity = activity;
        initMapFragment();
        fromactivity=from;
    }

    private SupportMapFragment getMapFragment() {
        return (SupportMapFragment) m_activity.getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    private void initMapFragment() {
        /* Locate the mapFragment UI element */
        m_mapFragment = getMapFragment();

        // Set up disk cache path for the map service for this application  // It is recommended to use a path under your application folder for storing the disk cache
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(m_activity.getApplicationContext().getExternalFilesDir(null) + File.separator + ".here-maps", "map_intent"); /* ATTENTION! Do not forget to update {YOUR_INTENT_NAME} */
        if (!success) {
            Toast.makeText(m_activity.getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_LONG);
        } else {
            m_mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE) {      // retrieve a reference of the map from the map fragment
                        m_map = m_mapFragment.getMap();      // Set the map center to the Vancouver region (no animation)

                        getDataRoute();
                        if (dataRoute.size()>0){
                            if (Util.parseDouble(dataRoute.get(0).getLat())!=0&&Util.parseDouble(dataRoute.get(0).getLongi())!=0){
                                m_map.setCenter(new GeoCoordinate(Util.parseDouble(dataRoute.get(0).getLat()),  Util.parseDouble(dataRoute.get(0).getLongi()), 0.0), Map.Animation.BOW);      // Set the zoom level to the average between min and max
                                m_map.setZoomLevel((m_map.getMaxZoomLevel() + m_map.getMinZoomLevel()) / 1);
                                initCreatePolylineButton();
                            }
                        }

                    } else {
                        System.out.println("ERROR: Cannot initialize Map Fragment");     }    }   });  }

        ;


                    }




    /**
     * Initialize Create Polyline Button to add/remove MapPolyline.
     */
    private void initCreatePolylineButton() {
//        m_polyline_button = (Button) m_activity.findViewById(R.id.polyline_button);
//
//        m_polyline_button.setOnClickListener(new View.OnClickListener() {
//            // if MapPolyline already exists on map, then remove MapPolyline, otherwise create
//            // MapPolyline.
//            @Override
//            public void onClick(View v) {
//                if (m_map != null && m_polyline != null) {
//                    m_map.removeMapObject(m_polyline);
//                    m_polyline = null;
//                } else {
//                    createPolyline();
//                }
//            }
//        });

        if (m_map != null && m_polyline != null) {
            m_map.removeMapObject(m_polyline);
            m_polyline = null;
        } else {
            createPolyline();
        }
    }

    /**
     * Create a MapPolyline and add the MapPolyline to active map view.
     */
    private void createPolyline() {
        getDataRoute();
        List<GeoCoordinate> testPoints = new ArrayList<GeoCoordinate>();


        for (int i = 0; i < dataRoute.size(); i++) {
            if (Util.parseDouble(dataRoute.get(i).getLat())!=0&&Util.parseDouble(dataRoute.get(i).getLongi())!=0){
                testPoints.add(new GeoCoordinate( Util.parseDouble(dataRoute.get(i).getLat()), Util.parseDouble(dataRoute.get(i).getLongi()), 0.0));
            }
        }

//        testPoints.add(new GeoCoordinate(-7.939587, 112.624899, 0.0));
//        testPoints.add(new GeoCoordinate(-7.941436, 112.622679, 0.0));
//        testPoints.add(new GeoCoordinate(-7.940565, 112.621606, 0.0));
//        testPoints.add(new GeoCoordinate(-7.938418, 112.618559, 0.0));
//        testPoints.add(new GeoCoordinate(-7.938106, 112.618790, 0.0));

        if (testPoints.size()>1){
            GeoPolyline polyline = new GeoPolyline(testPoints);
            m_polyline = new MapPolyline(polyline);

//        // create boundingBox centered at current location
//        GeoBoundingBox boundingBox = new GeoBoundingBox(m_map.getCenter(), 1000, 1000);
//        // add boundingBox's top left and bottom right vertices to list of GeoCoordinates
//        List<GeoCoordinate> coordinates = new ArrayList<GeoCoordinate>();
//        coordinates.add(boundingBox.getTopLeft());
//        coordinates.add(boundingBox.getBottomRight());
//        // create GeoPolyline with list of GeoCoordinates
//        GeoPolyline geoPolyline = new GeoPolyline(coordinates);
//        m_polyline = new MapPolyline(geoPolyline);

            m_polyline.setLineColor(Color.BLUE);
            m_polyline.setLineWidth(11);
            // add GeoPolyline to current active map
            m_map.addMapObject(m_polyline);
        }

    }

    private void getDataRoute(){
        FragmentActivity  FragmentActivity = m_activity.getSupportFragmentManager().findFragmentById(R.id.mapfragment).getActivity();


        if (fromactivity.equals("scheduletracking")){
            try{
                TrackingDriverActivity main = (TrackingDriverActivity) FragmentActivity ;
                dataRoute = main.getDataScheduleRoute();


            }catch (ClassCastException e){

            }
        }else if (fromactivity.equals("scheduledetail")){

        }
    }

}