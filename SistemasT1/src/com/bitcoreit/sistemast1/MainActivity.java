package com.bitcoreit.sistemast1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.example.sistemast1.R;


import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneticNameStyle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Runnable,LocationListener{
	TelephonyManager tm,tm2;
	TextView tv1,tv2,tv2_2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10,tv11,uiState;
	EditText et_DBName;
	HashMap<String,String> gsmParams=new HashMap<String, String>();
	boolean tActivo=false;
	String phoneType = null;
	GsmCellLocation cellLocationGSM;
	CdmaCellLocation cellLocationCDMA;
	int phoneTypeInt,dbmGSM;
	int myLatitude, myLongitude;
	OpenCellID openCellID;
	Button boton1,botonstop,botonDbMap;
	String cid,lac,mcc,mnc;
	double lat=0.0;
    double lng=0.0;
	int IDCELL=0;
	DeciMapSQLiteHelper usdbh ;
	SQLiteDatabase db ;
	boolean gpssn=false;
	Cursor c;
	
	//BD CONSTANTES
	static final String DB_NAME="DeciMapDB";
	String TABLE_NAME="GeoDBM";
	/*
	 * GeoDBM =  garciadiego casa
	 * 
	 */
	
	
	
	//gpsssss
	  private TextView latituteField;
	  private TextView longitudeField;
	  private LocationManager locationManager;
	  private String provider;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*
		 * GPS inicio
		 */
		
		
	    latituteField = (TextView) findViewById(R.id.tvLat);
	    longitudeField = (TextView) findViewById(R.id.tvLon);

	    // Get the location manager
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the locatioin provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.GPS_PROVIDER;//getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);

	    // Initialize the location fields
	    if (location != null) {
	     Log.d("Out","provider   " + provider + " has been selected.");
	      onLocationChanged(location);
	      gpssn=true;
	    } else {
	      latituteField.setText("Activa gps");
	      longitudeField.setText("Activa gps");
	      gpssn=false;
	    }
		
	    
		final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("GPS!");
        builder.setMessage(" Se Recomienda antes de abrir la app, Encender el GPS. Suerte ");
        builder.setPositiveButton("OK!",new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int which) {
    						// TODO Auto-generated method stub	
    				finish();
    				}});
        if(!gpssn)
        builder.show();
	    
		
		/*
		 * GPS final
		 */
		
		
	    
	    
	    usdbh = new DeciMapSQLiteHelper(MainActivity.this,DB_NAME, null, 1); //nombre de la base de datos :)
		db = usdbh.getWritableDatabase();
		
		  tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		  tm.listen(mPhoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);// lo que querras escuchar man
		  
		  
		  
		  tm2 = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		  tm2.listen(mPhoneListener2, PhoneStateListener.LISTEN_CELL_LOCATION);// lo que querras escuchar man
		  
		  
		  
		  
		  String networkOperator = tm.getNetworkOperator();
	        mcc = networkOperator.substring(0, 3);
	        mnc = networkOperator.substring(3);
	        Log.d("Out","netOperator:   " +tm.getNetworkOperatorName());
	       
	        
	        
	        
	        
		  phoneTypeInt = tm.getPhoneType();
		  phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_GSM ? "gsm" : phoneType;
		  phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_CDMA ? "cdma" : phoneType;
		  
		  if(phoneType.equals("gsm")){
		  cellLocationGSM=(GsmCellLocation)tm.getCellLocation();
		  }else if(phoneType.equals("cdma")){
			  cellLocationCDMA=(CdmaCellLocation)tm.getCellLocation();
		  }

		  cid=String.valueOf(cellLocationGSM.getCid());
		  lac=String.valueOf(cellLocationGSM.getLac());
		  
		     
	       Log.d("Out","MCC : " +  mcc);
	       Log.d("Out","MNC : " + mnc);
	       Log.d("Out","CID : " +  cid);
	       Log.d("Out","LAC : " + lac);
		 
		  
	     
		  tv1=(TextView)findViewById(R.id.tv1);
		  tv2=(TextView)findViewById(R.id.tv2);
		  tv2_2=(TextView)findViewById(R.id.tv2_2);
		  tv3=(TextView)findViewById(R.id.tv3);
		  tv4=(TextView)findViewById(R.id.tv4);
		  tv5=(TextView)findViewById(R.id.tv5);
		  tv6=(TextView)findViewById(R.id.tv6);
		  tv7=(TextView)findViewById(R.id.tv7);
		  tv8=(TextView)findViewById(R.id.tv8);
		  tv9=(TextView)findViewById(R.id.tv9);
		  tv10=(TextView)findViewById(R.id.tv10);
		  tv11=(TextView)findViewById(R.id.tv11);
		  et_DBName=(EditText)findViewById(R.id.eTDB_Name);
		  uiState=(TextView)findViewById(R.id.uiState);
		  boton1=(Button)findViewById(R.id.button1);
		  botonstop=(Button)findViewById(R.id.button2);
		  botonDbMap=(Button)findViewById(R.id.dbMap);
		  boton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//new OpenCellID().execute("lalalalalalal");	Funciona bien llama a un servicio externo pero no tiene la info que necesitamos
			
			        
			        
			        
			        
				
			
				
	            
	            
			}
		});
		  
		  
		  botonstop.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					

					TABLE_NAME=et_DBName.getText().toString();
					if(tActivo){

						Toast.makeText(getApplicationContext(), "registro="+TABLE_NAME + "  DETENIDO..", Toast.LENGTH_LONG).show();
						tActivo=false;
						uiState.setText("detenido");
						uiState.setTextColor(Color.RED);
						botonstop.setTextColor(Color.GREEN);
						botonstop.setText("Inicio");
						botonDbMap.setTextColor(Color.BLUE);
						botonDbMap.setEnabled(true);
						
					}else{
						
						if(et_DBName.getText().length()>0  && et_DBName!=null){
						tActivo=true;
						try{
						c=db.query(TABLE_NAME,null,null,null,null,null,null);
						IDCELL=c.getCount();
						Toast.makeText(getApplicationContext(), "registro="+TABLE_NAME + "  AGREGANDO datos...", Toast.LENGTH_LONG).show();
						new Thread(MainActivity.this).start();
						uiState.setText("running");
						uiState.setTextColor(Color.GREEN);
						botonstop.setTextColor(Color.RED);
						botonstop.setText("Detener");
						botonDbMap.setTextColor(Color.RED);
						botonDbMap.setEnabled(false);
						}catch(SQLiteException e){
							Toast.makeText(getApplicationContext(), "registro ="+TABLE_NAME + "  NUEVOS  datos...", Toast.LENGTH_LONG).show();
							new Thread(MainActivity.this).start();
							uiState.setText("running");
							uiState.setTextColor(Color.GREEN);
							botonstop.setTextColor(Color.RED);
							botonstop.setText("Detener");
							botonDbMap.setTextColor(Color.RED);
							botonDbMap.setEnabled(false);
						}
						
					
						}else{
							
							   Toast.makeText(getApplicationContext(),Html.fromHtml("<b><font color=\"red\" >"+"ELIGE NOMBRE PARA EL REGISTRO!</font></b>"),Toast.LENGTH_LONG).show();
							   
						}
						
					}
					
				}
			});
		  
		  botonDbMap.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if(et_DBName.getText().length()>0  && et_DBName!=null){
					TABLE_NAME=et_DBName.getText().toString();
			        Intent intent = new Intent(MainActivity.this,DeciMap.class);
						try{
							c=db.query(TABLE_NAME,null,null,null,null,null,null);
						//startActivity(intent);
						   intent.putExtra("tabla",TABLE_NAME);
						   final int result=1;
						   startActivityForResult(intent, result);
						   MainActivity.this.finish();
						
						}catch(SQLiteException e){
							Toast.makeText(getApplicationContext(), "No Existe el  REGISTRO! " + TABLE_NAME,Toast.LENGTH_LONG).show();
						
						}
					}else{
						  Toast.makeText(getApplicationContext(), Html.fromHtml("<b><font color=\"red\" >"+"ELIGE NOMBRE PARA EL REGISTRO!</font></b>"),Toast.LENGTH_LONG).show();
					}
			          
						
						   
						   
			          return;
					
				}
			});
		  
		
		gsmParams.put("tipo","tipo de Red ?  " + phoneType);  
	    gsmParams.put("gsmsignalstrength","gsm intensidad de la señal : ");
	    gsmParams.put("dbmgsm","gsm signal in dbms  " + phoneType);
	    gsmParams.put("gsmbiterrrate"," gsm bit err rate :  ");
	    gsmParams.put("describe"," describe  " );  
	    gsmParams.put("cdmaecIodb10","Cdma dbm : ");
	    gsmParams.put("rssicdmadbm","gsm intensidad de la señal :  ");
	    gsmParams.put("evdosnr","tipo de Red ?  " );  
	    gsmParams.put("evdoecio","Cdma dbm : ");
	    gsmParams.put("evdodbm","gsm intensidad de la señal :  ");
	    gsmParams.put("vecinos","#vecinos  :  ");

	    if(phoneType.equals("gsm"))
		gsmParams.put("celllocation","cell Location gsm [codearea , cell Id , x]:" + cellLocationGSM.toString());
	    else if(phoneType.equals("cdma"))
	    gsmParams.put("celllocation","cell Location cdma [codearea , cell Id , x]:" + cellLocationCDMA.toString());
		
	    /*  
	    openCellID = new OpenCellID();
	       openCellID.setMcc(mcc);
	       openCellID.setMnc(mnc);
	       openCellID.setCallID(cellLocationGSM.getCid());
	       openCellID.setCallLac(cellLocationGSM.getLac());
	        try {
	   openCellID.GetOpenCellID();
	     
	   if(!openCellID.isError()){

	       Log.d("Out","locacion : " + openCellID.getLocation());

	       Log.d("Out","Url enviada : " + "\n\n URL sent: \n" + openCellID.getstrURLSent() + "\n\n    response: \n" + openCellID.GetOpenCellID_fullresult);
	   }else{

	       Log.d("Out","Chinga u Puta madre : Error");
	   }
	  } catch (Exception e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	   Log.d("Out","REEEEChinga u Puta madre : Error" + e.toString());
	  }*/
		  //new Thread(this).start();
		 }

	
	
	/*
	 * Start
	 *
	 * Listeners de los diferentes tipos de eventos celulares
	 * 
	 * 
	 */
		 private PhoneStateListener mPhoneListener = new PhoneStateListener() {
			 
		  public void onCallStateChanged(int state, String incomingNumber) {
		   try {
		    switch (state) {
		    case TelephonyManager.CALL_STATE_RINGING:
		     Log.d("Out","ringinnnngg");
		     break;
		    case TelephonyManager.CALL_STATE_OFFHOOK:
			     Log.d("Out","en llamada");
		     break;
		    case TelephonyManager.CALL_STATE_IDLE:
			     Log.d("Out","  idle osea trankilon ");
		     break;
		    default:
		     Log.i("Default", "Unknown phone state=" + state);
		    }
		   } catch (Exception e) {
		    Log.i("Exception", "PhoneStateListener() e = " + e);
		   }
		  }
		  
		  
		  public void onSignalStrengthsChanged(SignalStrength signalStrength)
		  {
		    /*Log.d("Out", "Gsm signal strenght    "+String.valueOf(signalStrength.getGsmSignalStrength()));
		    Log.d("Out", "Gsm bit error rate " + String.valueOf(signalStrength.getGsmBitErrorRate()));
		    Log.d("Out", "descibe " + String.valueOf(signalStrength.describeContents()));
		    Log.d("Out", "CDMA Ec/Io valor en dB*10 " + String.valueOf(signalStrength.getCdmaEcio()));
			Log.d("Out", "RSSI  cdma valor en dbm = " + String.valueOf(signalStrength.getCdmaDbm()));
		    Log.d("Out", "Relacion señal a ruido EVDO (0 a 8) " + String.valueOf(signalStrength.getEvdoSnr()));
		    Log.d("Out", "EVDO ec / io  " + String.valueOf(signalStrength.getEvdoEcio()));
		    Log.d("Out", "EVDO dbm  " + String.valueOf(signalStrength.getEvdoDbm()));*/
			
		  
		    gsmParams.remove("tipo");  
		    gsmParams.remove("gsmsignalstrength");
		    gsmParams.remove("dbmgsm");
		    gsmParams.remove("gsmbiterrrate");
		    gsmParams.remove("describe");  
		    gsmParams.remove("cdmaecIodb10");
		    gsmParams.remove("rssicdmadbm");
		    gsmParams.remove("evdosnr");  
		    gsmParams.remove("evdoecio");
		    gsmParams.remove("evdodbm");
		    gsmParams.remove("celllocation");
		   

		    
		    gsmParams.put("tipo","tipo de Red :  " + phoneType);  
		    gsmParams.put("gsmsignalstrength","Gsm Signal Strength : "+String.valueOf(signalStrength.getGsmSignalStrength()));
		    
		    if(signalStrength.getGsmSignalStrength() == 99)
		    	dbmGSM=0;
		    else  if(signalStrength.getGsmSignalStrength() == 0)
		    	dbmGSM=-113;
		    else  if(signalStrength.getGsmSignalStrength() == 1)
		    	dbmGSM=109;
		    else  if(signalStrength.getGsmSignalStrength() == 2)
		    	dbmGSM=-107;
		    else  if(signalStrength.getGsmSignalStrength() == 3)
		    	dbmGSM=-105;
		    else  if(signalStrength.getGsmSignalStrength() == 4)
		    	dbmGSM=-103;
		    else  if(signalStrength.getGsmSignalStrength() == 5)
		    	dbmGSM=-101;
		    else  if(signalStrength.getGsmSignalStrength() == 30)
		    	dbmGSM=-53;
		    else  if(signalStrength.getGsmSignalStrength() == 31)
		    	dbmGSM=-51;
		    else
		    {
		    	
		    	int auxCont=1;
		    	int dbAuxIni=-53;
		    	for(int ajua=29;ajua>5;ajua--){
		    		
		    		if (signalStrength.getGsmSignalStrength()==ajua)
		    		dbmGSM=( dbAuxIni - auxCont  );
		    		
		    		auxCont+=2;
		    	
		    	}
		    	
		    }
		    
		    
		    
		    gsmParams.put("dbmgsm"," dbm signal Strenght :  " +  dbmGSM);
		    gsmParams.put("gsmbiterrrate","gsm Bit ERR rate :  " +  String.valueOf(signalStrength.getGsmBitErrorRate()));
		    gsmParams.put("describe","Descripcion :  "  + String.valueOf(signalStrength.describeContents()) );  
		    gsmParams.put("cdmaecIodb10","CDMA ec/io en DB*10 : " + String.valueOf(signalStrength.getCdmaEcio()));
		    gsmParams.put("rssicdmadbm","RSSI  cdma valor en dbm : " + String.valueOf(signalStrength.getCdmaDbm()));
		    gsmParams.put("evdosnr","Relacion señal a ruido EVDO (0 a 8) : " + String.valueOf(signalStrength.getEvdoSnr()));  
		    gsmParams.put("evdoecio", "EVDO ec / io  : " + String.valueOf(signalStrength.getEvdoEcio()));
		    gsmParams.put("evdodbm","EVDO dbm  : " + String.valueOf(signalStrength.getEvdoDbm()));
		    
		    if(phoneType.equals("gsm"))
				gsmParams.put("celllocation","cell [codearea,cellId ,umtscode]:" + cellLocationGSM.toString() + cellLocationGSM.getCid());
			    else if(phoneType.equals("cdma"))
			    gsmParams.put("celllocation","cell [codearea,cellId ,umtscode]:" + cellLocationCDMA.toString());
		    
		   
		  }
		  
	
		  
		 };
		 
		 private PhoneStateListener mPhoneListener2 = new PhoneStateListener() {
			 
			  public void onCellLocationChanged(CellLocation cl){
				  Log.d("Out", "dentro de oncelchanged man" + cl.toString());
			  }
			  
			 };
		 
			 
 
	/*
	 * Fin
	 *
	 * Listeners de los diferentes tipos de eventos celulares
	 * 
	 * 
	 */
		 
		 public void refreshOtrosValores(){
			  phoneTypeInt=tm.getPhoneType();
			  phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_GSM ? "gsm" : phoneType;
			  phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_CDMA ? "cdma" : phoneType;
			  
			  if(phoneType.equals("gsm")){
				  cellLocationGSM=(GsmCellLocation)tm.getCellLocation();
				  }else if(phoneType.equals("cdma")){
					  cellLocationCDMA=(CdmaCellLocation)tm.getCellLocation();
				  }
			 
		 }

		 
		 //USADO POR QUE TE APENDEJASTE AL CAPTURAR EL RSSID, PUSISTE MAL EL KEY DE HASHMAP PENDEJE
		 public int rssiToDBM(int rssi) {
			 
			 if(rssi == 99)
			    	dbmGSM=0;
			    else  if(rssi == 0)
			    	dbmGSM=-113;
			    else  if(rssi == 1)
			    	dbmGSM=109;
			    else  if(rssi == 2)
			    	dbmGSM=-107;
			    else  if(rssi == 3)
			    	dbmGSM=-105;
			    else  if(rssi == 4)
			    	dbmGSM=-103;
			    else  if(rssi == 5)
			    	dbmGSM=-101;
			    else  if(rssi == 30)
			    	dbmGSM=-53;
			    else  if(rssi == 31)
			    	dbmGSM=-51;
			    else
			    {
			    	
			    	int auxCont=1;
			    	int dbAuxIni=-53;
			    	for(int ajua=29;ajua>5;ajua--){
			    		
			    		if (rssi==ajua)
			    		dbmGSM=( dbAuxIni - auxCont  );
			    		
			    		auxCont+=2;
			    	
			    	}
			    	
			    }
			 Log.d("Out"," " + dbmGSM);
			 
			 return dbmGSM;
		 }
		 
		 
		 public void dataBaseTransacciones(){

		        
			db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (id INTEGER, posicion TEXT,rssi TEXT,rssidbm TEXT,cellid TEXT,tipored TEXT,codearea TEXT)");
			Log.d("Out",TABLE_NAME+" Creada si no existe  ");		      
		    
  
if(db != null)
db.execSQL("INSERT INTO "+TABLE_NAME+" (id,posicion,rssi,rssidbm,cellid,tipored,codearea) VALUES ("+IDCELL+", '" + lat+","+lng +"','"+ gsmParams.get("gsmsignalstrength")+"'," +
																												  "'"+String.valueOf(rssiToDBM(Integer.parseInt(gsmParams.get("gsmsignalstrength").split(":")[1].replace(' ','0'))))+"'," +
																												  "'"+cellLocationGSM.getCid()+"'," +
																												  "'"+gsmParams.get("tipo")+"'," +
																												  "'"+cellLocationGSM.getLac()+"')");
      
ContentValues cv = new ContentValues();
				 
c=db.query(TABLE_NAME,null,null,null,null,null,null);

			if(c.moveToFirst())
				do{
					int id=c.getInt(0);
					String pos=c.getString(1);
					String rssi=c.getString(2);
					
					/*UPDATED UTIL
					 * cv = new ContentValues();
					 cv.put("rssidbm",String.valueOf(rssiToDBM(Integer.parseInt(rssi.split(":")[1].replace(' ','0'))))); //These Fields should be your String values of actual column names
					 db.update(TABLE_NAME, cv, "id"+"="+id, null);*/
					
					String rssidbm=c.getString(3);
					String cellid=c.getString(4);
					String tipored=c.getString(5);
					String codearea=c.getString(6);
					Log.d("Out","id -> " + String.valueOf(id)  + " |   pos -> " + pos + " |  rssi ->  " + rssi + " |  rssidbm ->  " + rssidbm + " |   cellid -> " + cellid + " |  tipored ->  " + tipored + " | codearea -> "+codearea) ;
				}while(c.moveToNext());
			
			IDCELL++;
			 
		 }
		 
		 
		 public void run(){
			 
			 
			 while(tActivo) 
			    	try{
			    		Thread.sleep(10000);
			    		refreshOtrosValores();
			    		dataBaseTransacciones();
			    		
			    		
			    		
			    		
			    		runOnUiThread (new Runnable(){ 
			    				public void run() {
			    					
			    					 gsmParams.remove("vecinos");
			    					 gsmParams.put("vecinos","# Vecinos  : " + String.valueOf(tm2.getNeighboringCellInfo().size()));
			    					
			    				if(tv1.length()>0){
			    				tv1.setText(gsmParams.get("tipo"));
			    				tv2.setText(gsmParams.get("gsmsignalstrength"));
			    				tv2_2.setText(gsmParams.get("dbmgsm"));
			    				tv3.setText(gsmParams.get("gsmbiterrrate"));
			    				tv4.setText(gsmParams.get("describe"));
			    				tv5.setText(gsmParams.get("cdmaecIodb10"));
			    				tv6.setText(gsmParams.get("rssicdmadbm"));
			    				tv7.setText(gsmParams.get("evdosnr"));
			    				tv8.setText(gsmParams.get("evdoecio"));
			    				tv9.setText(gsmParams.get("evdodbm"));
			    				tv10.setText(gsmParams.get("celllocation"));
			    				tv11.setText(gsmParams.get("vecinos"));
			    				
			    				
			    				}
			    				
			    				
			    				}
			        });
			    		
			    	}catch(InterruptedException e){
			    		Log.d("Out","Hilo MainActivity   :   " + e.toString());
			    	}
		
		 
		 
		 }
		 
		 

		 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	 public class OpenCellID extends AsyncTask<String, Integer, Double> {
		  
		    
		  Boolean error;
		  String strURLSent;
		  String GetOpenCellID_fullresult;
		    
		  String latitude;
		  String longitude;
		    
		  public Boolean isError(){
		   return error;
		  }
		    
		  public void setMcc(String value){
		   mcc = value;
		  }
		    
		  public void setMnc(String value){
		   mnc = value;
		  }
		    
		
		    
		  public void setCallLac(int value){
		   lac = String.valueOf(value);
		  }
		    
		  public String getLocation(){
		   return(latitude + " : " + longitude);
		  }
		    
		  public void groupURLSent(){
		   strURLSent =
		    "http://www.opencellid.org/cell/get?mcc=" + mcc
		    +"&mnc=" + mnc
		    +"&cellid=" + cid
		    +"&lac=" + lac+"&fmt=txt";

			  Log.d("Out",strURLSent);
		  }
		    
		  public String getstrURLSent(){
		   return strURLSent;
		  }
		    
		  public String getGetOpenCellID_fullresult(){
		   return GetOpenCellID_fullresult;
		  }
		    
		  public void GetOpenCellID() throws Exception {
		   groupURLSent();
		   HttpClient client = new DefaultHttpClient();
		   HttpGet request = new HttpGet(strURLSent);
		   HttpResponse response = client.execute(request);
		   
		   GetOpenCellID_fullresult = EntityUtils.toString(response.getEntity());
		   spliteResult();
		  }
		    
		  private void spliteResult(){
		   if(GetOpenCellID_fullresult.equalsIgnoreCase("err")){
		    error = true;
		   }else{
		    error = false;
		    String[] tResult = GetOpenCellID_fullresult.split(",");
		    latitude = tResult[0];
		    longitude = tResult[1];
		   }
		   
		   Log.d("Out","Lat -> " + latitude + "    Lon -> " + longitude);
		  }
		  
			@Override
			protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
		    Log.d("Out","----->>>" + params[0]);
			postData(params[0]);
			return null;
			}
			 
			protected void onPostExecute(Double result){
				//pb.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
}
			protected void onProgressUpdate(Integer... progress){
		    //pb.setProgress(progress[0]);
			Log.d("Out","progreso segun  -- - - - -   " + String.valueOf(progress[0]));
			}
			 
			public void postData(String valueIWantToSend) {
		    groupURLSent();
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(strURLSent);
			 
			try {

			    Log.d("Out","----->>>peticion en progreso..");
			HttpResponse response = httpclient.execute(httpget);
			GetOpenCellID_fullresult = EntityUtils.toString(response.getEntity());
		    Log.d("Out","----->>> peticion terminada! ");
			spliteResult();
			} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			} catch (IOException e) {
			// TODO Auto-generated catch block
			}
			}
		  
		 }
	
	

	 /*
	  * 
	  * 
	  * GPSSS Listeners implementesd metodosssss
	  * 
	  * 
	  */
	
	 
	 /* Request updates at startup */
	  @Override
	  protected void onResume() {
	    super.onResume();
	    locationManager.requestLocationUpdates(provider, 400, 1, this);
	  }

	  /* Remove the locationlistener updates when Activity is paused */
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	  }

	  @Override
	  public void onLocationChanged(Location location) {
	     this.lat =  (location.getLatitude());
	     this.lng =  (location.getLongitude());
	    latituteField.setText(String.valueOf(lat));
	    longitudeField.setText(String.valueOf(lng));
	  }

	  @Override
	  public void onStatusChanged(String provider, int status, Bundle extras) {
	    // TODO Auto-generated method stub

	  }

	  @Override
	  public void onProviderEnabled(String provider) {
	    Toast.makeText(this, "GPS habilitado -->>  " + provider,
	        Toast.LENGTH_SHORT).show();

	    latituteField.setTextColor(Color.BLUE);	 
	    longitudeField.setTextColor(Color.BLUE);
	    latituteField.setText("Obteniendo posicion ..");
	    longitudeField.setText("Obteniendo posicion ..");
	    
	  }

	  @Override
	  public void onProviderDisabled(String provider) {
		  
	    Toast.makeText(this, " GPS desabilitado " + provider,
	        Toast.LENGTH_SHORT).show();
	    latituteField.setTextColor(Color.RED);	 
	    longitudeField.setTextColor(Color.RED);
	    latituteField.setText("Activa gps");
	    longitudeField.setText("Activa gps");
	  }
	  
	  @Override
	  public void onBackPressed(){
		  final AlertDialog.Builder builder=new AlertDialog.Builder(this);
	        builder.setTitle("Salir");
	        builder.setMessage(" Se detendran los procesos corriendo de captura. seguro? ");
	        builder.setIcon(android.R.drawable.ic_dialog_alert);

	        
	        builder.setPositiveButton("Proceder",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					tActivo=false;
					uiState.setText("detenido");
					uiState.setTextColor(Color.RED);
					botonstop.setTextColor(Color.GREEN);
					botonstop.setText("Inicio");
					botonDbMap.setTextColor(Color.GREEN);
					botonDbMap.setEnabled(false);
					finish();
				}});
				 
				 
				 builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							return;
						}});
				
					builder.show();

		  
	  }
	 
	 
}
