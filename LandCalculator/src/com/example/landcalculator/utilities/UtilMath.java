package com.example.landcalculator.utilities;

import java.util.ArrayList;
import com.example.landcalculator.model.MyLatLonPoint;
import com.example.landcalculator.model.MyPoint;

public class UtilMath {

	public static double calculateShapeSize(ArrayList<MyLatLonPoint> latLonArray) 
	{
		ArrayList<ArrayList<MyLatLonPoint>> latLonArrayTotal = new ArrayList<ArrayList<MyLatLonPoint>>();
		latLonArrayTotal.add(latLonArray);
		MyLatLonPoint refLatLonPoint = UtilGraphics.getRefLatLonPoint(latLonArrayTotal);
		
		//transform LatLonPoint array into NormalPoint array
		ArrayList<MyPoint> normalPointArray  = genNormalPointArray(latLonArray, refLatLonPoint);
		
		
		//calculate shape area
		double sum = 0.0;
		for (int i = 0; i < normalPointArray.size(); i++)
		{
			MyPoint p1, p2;
			
			p1 = normalPointArray.get(i);
			if (i == normalPointArray.size() - 1)
				p2 = normalPointArray.get(0);
			else
				p2 = normalPointArray.get(i + 1);
			
			double tmp = p1.X * p2.Y - p1.Y * p2.X;
			sum += tmp;
		}
		
		return Math.abs(sum / 2.0);
	}
	
	public static double calculateShapePerimeter(ArrayList<MyLatLonPoint> latLonArray)
	{
		double sum = 0.0;
		for (int i = 0; i< latLonArray.size(); i++)
		{
			MyLatLonPoint p1, p2;
			p1 = latLonArray.get(i);
			if (i == latLonArray.size() - 1)
				p2 = latLonArray.get(0);
			else
				p2 = latLonArray.get(i + 1);
			
			double tmp = getLatLonDistance(p1.Lat, p1.Lon, p2.Lat, p2.Lon);
			sum += tmp;
		}
		return sum;
	}
	
	public static double calculateDistance(ArrayList<MyLatLonPoint> latLonArray)
	{
		double sum = 0.0;
		for (int i = 0; i < latLonArray.size() - 1; i++)
		{
			MyLatLonPoint p1, p2;
			p1 = latLonArray.get(i);
			p2 = latLonArray.get(i + 1);
			
			double tmp = getLatLonDistance(p1.Lat, p1.Lon, p2.Lat, p2.Lon);
			sum += tmp;
		}
		return sum;
	}
	
	public static ArrayList<MyPoint> genNormalPointArray(ArrayList<MyLatLonPoint> latLonArray, MyLatLonPoint refLatLonPoint)
	{
		ArrayList<MyPoint> normalPointArray = new ArrayList<MyPoint>();
		for (int i = 0; i< latLonArray.size(); i++)
		{
			MyLatLonPoint currentPoint = latLonArray.get(i);
			double distX = getLatLonDistance(refLatLonPoint.Lat, refLatLonPoint.Lon, 
											 refLatLonPoint.Lat, currentPoint.Lon);
			double distY = getLatLonDistance(refLatLonPoint.Lat, refLatLonPoint.Lon, 
											 currentPoint.Lat, refLatLonPoint.Lon);
			MyPoint normalPoint = new MyPoint(distX, distY);
			normalPointArray.add(normalPoint);
		}
		return normalPointArray;
	}
	
	public static double getLatLonDistance(double lat_s, double lon_s, double lat_f, double lon_f)
	{
		double distance = 0.0;
		double EARTH_RADIUS = 6732.8;
		if (lat_s == lat_f)	lat_f += 0.000000001;
		double delta_alpha = Math.acos(Math.sin(getRadianFromDegree(lat_s)) * Math.sin(getRadianFromDegree(lat_f)) + 
				Math.cos(getRadianFromDegree(lat_s)) * Math.cos(getRadianFromDegree(lat_f)) * 
				Math.cos(getRadianFromDegree(lon_s - lon_f)));
		
		distance = EARTH_RADIUS * delta_alpha * 1000;
		return distance;
	}
	
	public static double getRadianFromDegree(double a)
	{
		return a * Math.PI / 180.0;
	}
	
	public static double getDegreeFromRadian(double a)
	{
		return a * 180.0 / Math.PI;
	}
}
