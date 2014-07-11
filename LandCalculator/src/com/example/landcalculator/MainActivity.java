package com.example.landcalculator;

import java.util.ArrayList;
import java.util.List;

import com.example.landcalculator.model.MyLatLonPoint;
import com.example.landcalculator.model.MyPoint;
import com.example.landcalculator.utilities.UtilMath;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	ArrayList<MyLatLonPoint> LatLonArray;
	ArrayList<MyLatLonPoint> MockLatLonArray;
	MyLatLonPoint userCurrentPosition;
	GPSTracker GPS;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initNecessaryData();
        
        Button recordBtn = (Button)findViewById(R.id.recordBtn);
        recordBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				recordBtnOnClick();
			}
		});
        
        Button clearBtn = (Button)findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				clearBtnOnClick();
			}
		});
        
        Button calculateAreaBtn = (Button)findViewById(R.id.calculateAreaBtn);
        calculateAreaBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				calculateAreaBtnOnClick();
			}
		});
        
        Button calculateDistanceBtn = (Button)findViewById(R.id.calculateDistanceBtn);
        calculateDistanceBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				calculateDistanceBtnOnClick();
			}
		});
        
        Button showAreaMapBtn = (Button)findViewById(R.id.showAreaMapBtn);
        showAreaMapBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showAreaMapBtnOnClick(1);
			}
		});

        Button showDistanceMapBtn = (Button)findViewById(R.id.showDistanceMapBtn);
        showDistanceMapBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showAreaMapBtnOnClick(2);
			}
		});        
        
    }
    
    public void initNecessaryData() {
    	LatLonArray = new ArrayList<MyLatLonPoint>();
    	GPS = new GPSTracker(this);
    	generateMockUpData();

    }
    
    public void generateMockUpData() {
    	MockLatLonArray = new ArrayList<MyLatLonPoint>();
    	for (int i=0; i<7; i++) {
    		MyLatLonPoint p = new MyLatLonPoint(52.501959, 13.408706);
    		
    		if (i == 0) 	p = new MyLatLonPoint(52.501959, 13.408706);
    		if (i == 1) 	p = new MyLatLonPoint(52.502195, 13.410101);
    		if (i == 2) 	p = new MyLatLonPoint(52.503592, 13.410938);
    		if (i == 3) 	p = new MyLatLonPoint(52.503958, 13.408728);
    		if (i == 4) 	p = new MyLatLonPoint(52.504337, 13.407226);
    		if (i == 5) 	p = new MyLatLonPoint(52.503031, 13.406303);
    		if (i == 6) 	p = new MyLatLonPoint(52.502116, 13.407161);
    		
    		MockLatLonArray.add(p);
    	}
    }
    
    public void setRecordCountText(int count) {
    	TextView recordCountTextView = (TextView)findViewById(R.id.recordCountTextView);
    	recordCountTextView.setText(""+count);	
    }

    public void setCalculateAreaText(String text) {
    	TextView recordCountTextView = (TextView)findViewById(R.id.showAreaTextView);
    	recordCountTextView.setText(text);	
    }

    public void setCalculateDistanceText(String text) {
    	TextView recordCountTextView = (TextView)findViewById(R.id.showDistanceTextView);
    	recordCountTextView.setText(text);	
    }
    
    public MyLatLonPoint getLatLonPointFromGPS() {
    	try {
    		double latitude = GPS.getLatitude();
    		double longtitude = GPS.getLongitude();
    		MyLatLonPoint latLonPoint = new MyLatLonPoint(latitude, longtitude);
    		
    		//return latLonPoint;
    		return MockLatLonArray.get(LatLonArray.size());	
    	}
    	catch(Exception e) {
    		return null;
    	}
    }
    
    public void recordBtnOnClick() {

    	if (!GPS.canGetLocation()) {
    		GPS.showSettingsAlert();
    		return;
    	}
    	
    	MyLatLonPoint newLatLonPoint = getLatLonPointFromGPS();
    	
    	if (newLatLonPoint != null) {
    		LatLonArray.add(newLatLonPoint);
    		setCalculateAreaText(newLatLonPoint.Lat+"");
    		setCalculateDistanceText(newLatLonPoint.Lon+"");
    	}
    	 	
    	setRecordCountText(LatLonArray.size());
    }
    
    public void clearBtnOnClick() {
    	LatLonArray = new ArrayList<MyLatLonPoint>();
    	setRecordCountText(0);
    	setCalculateAreaText("");
    	setCalculateDistanceText("");
    }
    
    public void calculateAreaBtnOnClick() {
    	System.out.println("ZZZZ");
    	Log.i("ZXCVB","fuuu");
    	int SQ_WAR_PER_RAI = 400;
    	double areaSizeSqMeter = UtilMath.calculateShapeSize(LatLonArray);
    	double areaSizeSqWar = areaSizeSqMeter / 4.0;
    	int areaSizeRai = (int) (areaSizeSqWar / SQ_WAR_PER_RAI);
    	double remainSqWar = areaSizeSqWar - (areaSizeRai * SQ_WAR_PER_RAI);
    	
    	double distanceMeter = UtilMath.calculateShapePerimeter(LatLonArray);
    	setCalculateAreaText("พื้นที่= " + areaSizeRai + " ไร่ " + Math.round(remainSqWar) + " ตร.ว.");
    	setCalculateDistanceText("เส้นรอบวง= " + Math.round(distanceMeter) + " เมตร ");
    }
    
    public void calculateDistanceBtnOnClick() {
    	double distanceMeter = UtilMath.calculateDistance(LatLonArray);
    	setCalculateDistanceText("ระยะทาง= " + Math.round(distanceMeter) + " เมตร ");
    	setCalculateAreaText("");
    }
    
    public void showAreaMapBtnOnClick(int mode) {
    	Intent intent = new Intent(this, ShowMapActivity.class);
    	MyLatLonPoint userCurrentPosition = new MyLatLonPoint(52.503958, 13.408728);
    	intent.putParcelableArrayListExtra("LatLonArray", LatLonArray);
    	intent.putExtra("UserCurrentLat", userCurrentPosition.Lat);
    	intent.putExtra("UserCurrentLon", userCurrentPosition.Lon);
    	intent.putExtra("Mode", mode);
    	startActivity(intent);
    }
}