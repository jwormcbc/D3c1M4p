package com.example.sistemast1;

import java.util.HashMap;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DeciMap extends Activity {
  static final LatLng TOSANWICHO = new LatLng(22.1609792,-100.99310046);
  private GoogleMap map;
  SQLiteDatabase db ;
  DeciMapSQLiteHelper usdbh;
  static final String DB_NAME="DeciMapDB",TABLE_NAME="GeoDBM";
  HashMap<String,DBMpoint> hmDBM=new HashMap<String, DBMpoint>();
	
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_deci_map);
    
    
    usdbh = new DeciMapSQLiteHelper(DeciMap.this,DB_NAME, null, 1); //nombre de la base de datos :)
	db = usdbh.getWritableDatabase();
    
    
    
    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
        .getMap();
    
    
    
    
    Cursor c=db.query(TABLE_NAME,null,null,null,null,null,null);
	if(c.moveToFirst())
		do{
			int id=c.getInt(0);
			String pos=c.getString(1);
			String rssi=c.getString(2);
			String rssidbm=c.getString(3);
			String cellid=c.getString(4);
			String tipored=c.getString(5);
			String codearea=c.getString(6);
			Log.d("Out","id -> " + String.valueOf(id)  + " |   pos -> " + pos + " |  rssi ->  " + rssi + " |  rssidbm ->  " + rssidbm + " |   cellid -> " + cellid + " |  tipored ->  " + tipored + " | codearea -> "+codearea) ;
		    hmDBM.put(String.valueOf(id),new DBMpoint(pos,rssidbm,cellid));
			
			
		}while(c.moveToNext());
    
    
	
	
	for(int i=0;i<hmDBM.size();i++){
		
		if(hmDBM.get(String.valueOf(i)).getDbms()<-80){
		map.addMarker(new MarkerOptions().position(new LatLng(hmDBM.get(String.valueOf(i)).getLat(),hmDBM.get(String.valueOf(i)).getLon()))
		        .title(String.valueOf(hmDBM.get(String.valueOf(i)).getDbms()) + "  Dbms")
                .snippet("Id antena: " + hmDBM.get(String.valueOf(i)).getCellid())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		}else if(hmDBM.get(String.valueOf(i)).getDbms()>=-80){
			map.addMarker(new MarkerOptions().position(new LatLng(hmDBM.get(String.valueOf(i)).getLat(),hmDBM.get(String.valueOf(i)).getLon()))
			        .title(String.valueOf(hmDBM.get(String.valueOf(i)).getDbms()) + "  Dbms")
	                .snippet("Id antena: " + hmDBM.get(String.valueOf(i)).getCellid())
	                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
		}

		
	}
	
	
    
	/*
    Marker hamburg = map.addMarker(new MarkerOptions().position(TOSANWICHO)
        .title("Center"));
   */

    // Move the camera instantly to hamburg with a zoom of 15.
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(TOSANWICHO, 18));
    // Zoom in, animating the camera.
    map.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deci_map, menu);
		return true;
	}

}




class DBMpoint{
	double lat,lon;
	int dbms;
	String cellid;
	
	public String getCellid() {
		return cellid;
	}

	public void setCellid(String cellid) {
		this.cellid = cellid;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public int getDbms() {
		return dbms;
	}

	public void setDbms(int dbms) {
		this.dbms = dbms;
	}

	public DBMpoint(String pos,String rssidbm,String cellid){
		this.lat=Double.parseDouble(pos.split(",")[0]);
		this.lon=Double.parseDouble(pos.split(",")[1]);
		this.dbms=Integer.parseInt(rssidbm);
		this.cellid=cellid;
	}
	
	
}