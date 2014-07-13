package com.example.landcalculator;

import java.util.ArrayList;

import com.example.landcalculator.model.MyLatLonPoint;
import com.example.landcalculator.utilities.UtilGraphics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ShowMapActivity extends Activity {

	ArrayList<MyLatLonPoint> LatLonArray;
	MyLatLonPoint userCurrentPosition;
	ArrayList<ArrayList<MyLatLonPoint>> latLonArrayTotal;
	int mode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		Intent intent = getIntent();
		LatLonArray = intent.getParcelableArrayListExtra("LatLonArray");
		mode = intent.getIntExtra("Mode", 0);
		double userCurrentLat = intent.getDoubleExtra("UserCurrentLat", 0.0);
		double userCurrentLon = intent.getDoubleExtra("UserCurrentLon", 0.0);
		userCurrentPosition = new MyLatLonPoint(userCurrentLat, userCurrentLon);
		
		latLonArrayTotal = new ArrayList<ArrayList<MyLatLonPoint>>();
		
		setContentView(new MyView(this));
	}
	
	 public class MyView extends View {
         public MyView(Context context) {
              super(context);
              // TODO Auto-generated constructor stub

         }

         @Override
         protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
      		
            if (LatLonArray != null)
            {
                latLonArrayTotal.add(LatLonArray);
                
                UtilGraphics.PIXEL_PER_LATLON = UtilGraphics.getPixelPerLatLon(latLonArrayTotal);
                UtilGraphics.WIDTH = getWidth();
                UtilGraphics.HEIGHT = getHeight();
                UtilGraphics.AXIS_LENGTH = UtilGraphics.WIDTH - 10;
                UtilGraphics.DRAW_MODE = mode;

                MyLatLonPoint refLatLonPoint = UtilGraphics.getRefLatLonPoint(latLonArrayTotal);
                UtilGraphics.drawMapFromLatLonArray(LatLonArray, canvas, refLatLonPoint);     
                
                if (userCurrentPosition != null)
                {
                	UtilGraphics.drawUserCurrentPosition(userCurrentPosition, canvas, refLatLonPoint);	
                }
            }

            
           
        }
     }
}
