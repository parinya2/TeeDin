package com.example.landcalculator.utilities;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import com.example.landcalculator.model.MyLatLonPoint;

public class UtilGraphics {

	public static int WIDTH = 800;
	public static int HEIGHT = 1134;
	public static int AXIS_OFFSET_X = 100;
	public static int AXIS_OFFSET_Y = 300;
	public static int AXIS_LENGTH = 450;
	public static int PIXEL_PER_LATLON = -1;
	public static int PIXEL_OFFSET = 20;
	public static int DRAW_MODE = 0;
	 
	static Point RefPoint = new Point(); 
	
	public static MyLatLonPoint getRefLatLonPoint(ArrayList<ArrayList<MyLatLonPoint>> latLonArrayTotal) 
	{
		double minLat = 999.0;
		double minLon = 999.0;
		
		for (int k = 0; k < latLonArrayTotal.size(); k++)
		{
			ArrayList<MyLatLonPoint> latLonArray = latLonArrayTotal.get(k);
			for (int i = 0; i < latLonArray.size(); i++)
			{
				MyLatLonPoint p = latLonArray.get(i);
				if (p.Lat < minLat)	minLat = p.Lat;
				if (p.Lon < minLon)	minLon = p.Lon;
			}
		}
		
		return new MyLatLonPoint(minLat, minLon);
	}
	
	public static void drawMapFromLatLonArray(ArrayList<MyLatLonPoint> latLonArray, Canvas canvas, 
												MyLatLonPoint refLatLonPoint)
	{
		Paint paint = new Paint();
		
		
		RefPoint.x = AXIS_OFFSET_X;
		RefPoint.y = HEIGHT - AXIS_OFFSET_Y;
		
		for (int i = 0; i < latLonArray.size(); i++)
		{
			paint.setColor(Color.BLUE);
			MyLatLonPoint latLonPoint = latLonArray.get(i);
			double dLat = latLonPoint.Lat - refLatLonPoint.Lat;
			double dLon = latLonPoint.Lon - refLatLonPoint.Lon;
			int dPixelX = (int)(dLon * PIXEL_PER_LATLON);
			int dPixelY = (int)(dLat * PIXEL_PER_LATLON);
		
			int pixelX = RefPoint.x + PIXEL_OFFSET + dPixelX;
			int pixelY = RefPoint.y - PIXEL_OFFSET - dPixelY;
			
			int circleRad = 20;
			
			canvas.drawCircle((float)pixelX, (float)pixelY, (float)circleRad, paint);
			
			MyLatLonPoint latLonPointNext;
			if (i < latLonArray.size() - 1)
				latLonPointNext = latLonArray.get(i+1);
			else
				latLonPointNext = latLonArray.get(0);
			
			double dLatNext = latLonPointNext.Lat - refLatLonPoint.Lat;
			double dLonNext = latLonPointNext.Lon - refLatLonPoint.Lon;
			int dPixelXNext = (int)(dLonNext * PIXEL_PER_LATLON);
			int dPixelYNext = (int)(dLatNext * PIXEL_PER_LATLON);
			
			int pixelXNext = RefPoint.x + PIXEL_OFFSET + dPixelXNext;
			int pixelYNext = RefPoint.y - PIXEL_OFFSET - dPixelYNext;
			
			paint.setStrokeWidth(10.0f);
			
			if (!((DRAW_MODE == 2) && (i == latLonArray.size() - 1)))
			{
				canvas.drawLine(pixelX, pixelY, pixelXNext, pixelYNext, paint);
				
				float middleX = (pixelX + pixelXNext) / 2.0f;
				float middleY = (pixelY + pixelYNext) / 2.0f;
				int distance = (int) UtilMath.getLatLonDistance(latLonPoint.Lat, latLonPoint.Lon, 
															latLonPointNext.Lat, latLonPointNext.Lon);
				
				paint.setTextSize(35.0f);
				paint.setColor(Color.RED);
				
				double slope = (pixelYNext - pixelY) * -1.0 / (pixelXNext - pixelX);
				int offsetX = 0, offsetY = 0;
				
				if (slope >= 1 || slope <= -1 || Double.isNaN(slope))
				{
					offsetX = 20;
				} else if (slope >= 0 && slope <= 1)
				{
					offsetY = 30;
				} else
				{
					offsetY = -30;
				}
				canvas.drawText(distance+" m", middleX + offsetX, middleY + offsetY, paint);
			}
				
		}
		
		paint.setTextSize(40.0f);
	}
	
	public static void drawUserCurrentPosition(MyLatLonPoint userCurrentPosition, Canvas canvas, 
												MyLatLonPoint refLatLonPoint)
	{
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		
		RefPoint.x = AXIS_OFFSET_X;
		RefPoint.y = HEIGHT - AXIS_OFFSET_Y;
		
		double dLat = userCurrentPosition.Lat - refLatLonPoint.Lat;
		double dLon = userCurrentPosition.Lon - refLatLonPoint.Lon;
		int dPixelX = (int)(dLon * PIXEL_PER_LATLON);
		int dPixelY = (int)(dLat * PIXEL_PER_LATLON);
		
		int pixelX = RefPoint.x + PIXEL_OFFSET + dPixelX;
		int pixelY = RefPoint.y - PIXEL_OFFSET - dPixelY;
			
		int circleRad = 20;
			
		canvas.drawCircle((float)pixelX, (float)pixelY, (float)circleRad, paint);		
	}
	
	public static int getPixelPerLatLon(ArrayList<ArrayList<MyLatLonPoint>> latLonArrayTotal)
	{
		double result;
		
		for (int k = 0; k < latLonArrayTotal.size(); k++)
		{
			ArrayList<MyLatLonPoint> latLonArray = latLonArrayTotal.get(k);
			if (latLonArray.size() == 0)
			{
				return -1;
			}
		}
		
		MyLatLonPoint first = latLonArrayTotal.get(0).get(0);
		double minLat = first.Lat;
		double maxLat = first.Lat;
		double minLon = first.Lon;
		double maxLon = first.Lon;
		
		for (int k = 0; k < latLonArrayTotal.size(); k++)
		{
			ArrayList<MyLatLonPoint> latLonArray = latLonArrayTotal.get(k);
			
			for (int i = 0; i < latLonArray.size(); i++)
			{
				MyLatLonPoint p = latLonArray.get(i);
				if (p.Lat < minLat)	minLat = p.Lat;
				if (p.Lat > maxLat) maxLat = p.Lat;
				if (p.Lon < minLon) minLon = p.Lon;
				if (p.Lon > maxLon) maxLon = p.Lon;
			}
		}
		
		double dLat = maxLat - minLat;
		double dLon = maxLon - minLon;
		int pixelCountX = WIDTH - 2 * AXIS_OFFSET_X;
		int pixelCountY = HEIGHT - 2 * AXIS_OFFSET_Y;
		double ratioX = dLon / pixelCountX;
		double ratioY = dLat / pixelCountY;
		
		result = (ratioY > ratioX) ? pixelCountY / dLat : pixelCountX / dLon;
		return (int)result;
	}
}
