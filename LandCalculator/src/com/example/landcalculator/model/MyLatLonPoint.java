package com.example.landcalculator.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MyLatLonPoint implements Parcelable{

	public double Lat, Lon;
	public boolean IsNull;
	
	public MyLatLonPoint (double lat, double lon) {
		Lat = lat;
		Lon = lon;
		IsNull = false;
	}

	public MyLatLonPoint (Parcel source)
	{
		Lat = source.readDouble();
		Lon = source.readDouble();
		
		boolean[] booleanArray = new boolean[1];
		source.readBooleanArray(booleanArray);
		IsNull = booleanArray[0];
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeDouble(this.Lat);
		dest.writeDouble(this.Lon);
		dest.writeBooleanArray(new boolean[] {IsNull});
	}
	
	public static final Parcelable.Creator CREATOR
	= new Parcelable.Creator() {
	    public MyLatLonPoint createFromParcel(Parcel in) {
	        return new MyLatLonPoint(in);
	    }

	    public MyLatLonPoint[] newArray(int size) {
	        return new MyLatLonPoint[size];
	    }
	};
}
