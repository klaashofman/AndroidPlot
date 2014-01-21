package com.example.androidplot;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import com.androidplot.xy.*;
//import com.example.androidplot.MainActivity.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
 
/**
* A straightforward example of using AndroidPlot to plot some data.
*/
public class SimpleXYPlotActivity extends Activity
{
 
    private XYPlot plot;
    public class Data{
		Number date[];
		Number temp[];
		Number humid[];
	}
	
	public static final String SENSOR_DATA_FILENAME = "sensors.csv";
	public static final String SENSOR_DATA_DIRNAME="AndroidPlot";
	
	private Data mySensorData = new Data();
	public void getSensorData(Data data){
		
		if ( !isExternalStorageReadable() ) {
			Log.d("androidplot", "no external storage present, aborting");
			finish();
		}
		if ( !readSensorDataFile(data) ) {
			Log.d("androidplot", "no storage data file present, aborting");
			finish();
		}
			
	}
	
	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	public static boolean createDirIfNotExists(String path) {
	    boolean ret = true;

	    File file = new File(Environment.getExternalStorageDirectory(), path);
	    if (!file.exists()) {
	        if (!file.mkdirs()) {
	            Log.e("adroidplot :: ", "Problem creating sensor data folder");
	            ret = false;
	        }
	    }
	    return ret;
	}
	
	public File getExternalStoragePrivateFile() throws IOException{
		
		createDirIfNotExists(SENSOR_DATA_DIRNAME);	
	    File file = new File(getExternalFilesDir(SENSOR_DATA_DIRNAME), SENSOR_DATA_FILENAME);
	    Log.d("androidplot", "file:" + file.getAbsolutePath());
	    return file;
	}
	
	
	public void getCSVData(Data data, File file){
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file.getPath()));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
			String line;
			int i = 0;
			while ((line = reader.readLine()) != null){
				String[] RowData = line.split(",");
				data.date[i] = Integer.parseInt(RowData[0]);
				data.temp[i] = Float.parseFloat(RowData[1]);
				data.humid[i] = Float.parseFloat(RowData[2]);
				i++;
			}
		} catch (Exception e) {
			 Log.e("adroidplot :: ", "error reading csv file", e);
		}
		finally {
			try {
	            reader.close();
	        }
	        catch (IOException e) {
	            // handle exception
	        	Log.e("adroidplot :: ", "error closing csv file", e);
	        }
		}	
			
	}
	public boolean readSensorDataFile(Data data){
		try {
			File file = getExternalStoragePrivateFile();
			getCSVData(data, file);
			
		} catch (IOException e) {
	    	Log.w("androidplot", "error reading sensor data file ", e);
	    	return false;
	    }
		return true;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	getSensorData(mySensorData);
    	super.onCreate(savedInstanceState);
 
        // fun little snippet that prevents users from taking screenshots
        // on ICS+ devices :-)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                                 WindowManager.LayoutParams.FLAG_SECURE);
 
        setContentView(R.layout.simple_xy_plot_example);
 
        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
 
        // Create a couple arrays of y-values to plot:      
       
        Number[] numberSeriesTemperature = mySensorData.temp;
        Number[] numberSeriesHumidity = mySensorData.humid;
        
        // Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(numberSeriesTemperature),          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "Temperature");                             // Set the display title of the series
 
        // same as above
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(numberSeriesHumidity), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Humidity");
 
        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter());
        series1Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_plf1);
 
        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);
 
        // same as above:
        LineAndPointFormatter series2Format = new LineAndPointFormatter();
        series2Format.setPointLabelFormatter(new PointLabelFormatter());
        series2Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_plf2);
        plot.addSeries(series2, series2Format);
 
        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.getGraphWidget().setDomainLabelOrientation(-45);
 
    }
}
