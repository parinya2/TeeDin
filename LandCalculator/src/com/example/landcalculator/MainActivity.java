package com.example.landcalculator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.landcalculator.model.MyLatLonPoint;
import com.example.landcalculator.model.MyPoint;
import com.example.landcalculator.utilities.UtilMath;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	ArrayList<MyLatLonPoint> LatLonArray;
	ArrayList<MyLatLonPoint> MockLatLonArray;
	MyLatLonPoint userCurrentPosition;
	GPSTracker GPS;
	String FILENAME = "save_file";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        initNecessaryData();
        
        Button recordBtn = (Button)findViewById(R.id.recordBtn);
        recordBtn.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        recordBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				recordBtnOnClick();
			}
		});
        
        Button clearBtn = (Button)findViewById(R.id.clearBtn);
        clearBtn.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        clearBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				clearBtnOnClick();
			}
		});
        
        Button clearLastBtn = (Button)findViewById(R.id.clearLastBtn);
        clearLastBtn.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        clearLastBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				clearLastBtnOnClick();
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

        Button saveBtn = (Button)findViewById(R.id.saveBtn);
        saveBtn.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
        saveBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				saveBtnOnClick();
			}
		});           

        Button loadBtn = (Button)findViewById(R.id.loadBtn);
        loadBtn.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
        loadBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				loadBtnOnClick();
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
    	TextView showAreaTextView = (TextView)findViewById(R.id.showAreaTextView);
    	showAreaTextView.setText(text);	
    }

    public void setCalculateDistanceText(String text) {
    	TextView showDistanceTextView = (TextView)findViewById(R.id.showDistanceTextView);
    	showDistanceTextView.setText(text);	
    }
    
    public MyLatLonPoint getLatLonPointFromGPS() {
    	try {
    		GPS = new GPSTracker(this);
    		Location location = GPS.getLocation();
    		MyLatLonPoint latLonPoint = new MyLatLonPoint(location.getLatitude(), location.getLongitude());
    		
    		return latLonPoint;
    		//return MockLatLonArray.get(LatLonArray.size());	
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
    		setCalculateAreaText("ละติจูด: " + String.format("%.8f", newLatLonPoint.Lat));
    		setCalculateDistanceText("ลองติจูด: " + String.format("%.8f", newLatLonPoint.Lon));
    	}
    	 	
    	setRecordCountText(LatLonArray.size());
    }
    
    public void clearBtnOnClick() {
    	
    	new AlertDialog.Builder(this)
        .setTitle("Delete All Records")
        .setMessage("Are you sure you want to clear all records?")
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {                 
            	LatLonArray = new ArrayList<MyLatLonPoint>();
            	setRecordCountText(0);
            	setCalculateAreaText("");
            	setCalculateDistanceText("");
            }
         })
        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // do nothing
            }
         })
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show();
    }
 
    public void clearLastBtnOnClick() {
    	
    	new AlertDialog.Builder(this)
        .setTitle("Remove Last Record")
        .setMessage("Are you sure you want to remove last record?")
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	if (LatLonArray.size() > 1)
            	{
            		LatLonArray.remove(LatLonArray.size()-1);	
            	}
            	setRecordCountText(LatLonArray.size());
            	setCalculateAreaText("");
            	setCalculateDistanceText("");
            }
         })
        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // do nothing
            }
         })
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show();
    }    
    
    public void calculateAreaBtnOnClick() {
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
    	//MyLatLonPoint userCurrentPosition = new MyLatLonPoint(52.502958, 13.408728);
    	userCurrentPosition = getLatLonPointFromGPS();
    	
    	if (LatLonArray != null)
    	{
    		intent.putParcelableArrayListExtra("LatLonArray", LatLonArray);	
    	}
    	
    	if (userCurrentPosition != null)
    	{
    		intent.putExtra("UserCurrentLat", userCurrentPosition.Lat);
        	intent.putExtra("UserCurrentLon", userCurrentPosition.Lon);	
    	}
    	
    	intent.putExtra("Mode", mode);
    	startActivity(intent);
    }
    
    public void saveBtnOnClick() {
    	new AlertDialog.Builder(this)
        .setTitle("Save Records To File")
        .setMessage("Are you sure you want to save records to file?")
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {                 
            	saveDataToFile();
            }
         })
        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // do nothing
            }
         })
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show();   	

    }
    
    public void loadBtnOnClick() {
    	
    	new AlertDialog.Builder(this)
        .setTitle("Load Records From File")
        .setMessage("Are you sure you want to load records from file?")
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {                 
            	loadDataFromFile();
            }
         })
        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // do nothing
            }
         })
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show();
    }
    
    public void saveDataToFile()
    {
    	BufferedWriter buffWriter;
		try {
			buffWriter = new BufferedWriter(new FileWriter(new File(getFilesDir()+"/"+FILENAME)));
			
			for (int i = 0; i < LatLonArray.size(); i++)
			{
				MyLatLonPoint p = LatLonArray.get(i);
				buffWriter.write(p.Lat+","+p.Lon);
				
				if (i != LatLonArray.size() - 1)
				{
					buffWriter.newLine();		
				}
			}
			buffWriter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setCalculateAreaText(e.toString());
		}   	
    }
    
    public void loadDataFromFile()
    {
    	try {
    		LatLonArray = new ArrayList<MyLatLonPoint>();
    		StringBuffer data = new StringBuffer();
			FileInputStream fis = openFileInput(FILENAME);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader buffReader = new BufferedReader(isr);
			int c = 0;
			String readString;
			
			readString = buffReader.readLine();
			while(readString != null) 
			{
				String[] tmp = readString.split(",");
				if (tmp.length == 2)
				{
					double lat = Double.parseDouble(tmp[0]);
					double lon = Double.parseDouble(tmp[1]);
					MyLatLonPoint p = new MyLatLonPoint(lat, lon);
					LatLonArray.add(p);
				}
				
				readString = buffReader.readLine();
			}
			isr.close();
			setRecordCountText(LatLonArray.size());
			setCalculateAreaText("");
			setCalculateDistanceText("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   	
    }
}