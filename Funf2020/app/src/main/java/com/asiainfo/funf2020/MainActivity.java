package com.asiainfo.funf2020;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.pipeline.BasicPipeline;
import edu.mit.media.funf.probe.Probe.DataListener;
import edu.mit.media.funf.probe.builtin.AudioFeaturesProbe;
import edu.mit.media.funf.probe.builtin.LinearAccelerationSensorProbe;
import edu.mit.media.funf.probe.builtin.MagneticFieldSensorProbe;
import edu.mit.media.funf.probe.builtin.OrientationSensorProbe;
import edu.mit.media.funf.probe.builtin.PressureSensorProbe;
import edu.mit.media.funf.probe.builtin.ProcessStatisticsProbe;
import edu.mit.media.funf.probe.builtin.ProximitySensorProbe;
import edu.mit.media.funf.probe.builtin.RotationVectorSensorProbe;
import edu.mit.media.funf.probe.builtin.RunningApplicationsProbe;
import edu.mit.media.funf.probe.builtin.ScreenProbe;
import edu.mit.media.funf.probe.builtin.ServicesProbe;
import edu.mit.media.funf.probe.builtin.SimpleLocationProbe;
import edu.mit.media.funf.probe.builtin.SmsProbe;
import edu.mit.media.funf.probe.builtin.TelephonyProbe;
import edu.mit.media.funf.probe.builtin.TemperatureSensorProbe;
import edu.mit.media.funf.probe.builtin.TimeOffsetProbe;
import edu.mit.media.funf.probe.builtin.VideoCaptureProbe;
import edu.mit.media.funf.probe.builtin.VideoMediaProbe;
import edu.mit.media.funf.probe.builtin.WifiProbe;
import edu.mit.media.funf.storage.NameValueDatabaseHelper;

import com.asiainfo.funf2020.utils.AssetsUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class MainActivity extends AppCompatActivity implements DataListener {


	public static final long RECORD_LONG = 10 * 1000L;
    public static final String PIPELINE_NAME = "default";
    private FunfManager funfManager;
    private BasicPipeline pipeline;
//    private WifiProbe wifiProbe;
//    private SimpleLocationProbe locationProbe;
//	private SmsProbe mSmsProbe;
//	private TelephonyProbe mTelephonyProbe;
//	private ServicesProbe mServicesProbe;
//	private ScreenProbe mScreenProbe;
//	private LinearAccelerationSensorProbe mLinearAccelerationSensorProbe;
//	private MagneticFieldSensorProbe mMagneticFieldSensorProbe;
//	private OrientationSensorProbe mOrientationSensorProbe;
//	private RotationVectorSensorProbe mRotationVectorSensorProbe;
//	private ProximitySensorProbe mProximitySensorProbe;
//	private TemperatureSensorProbe mTemperatureSensorProbe;
//	private PressureSensorProbe mPressureSensorProbe;
//	private RunningApplicationsProbe mProbe;
//	private ProcessStatisticsProbe mProcessStatisticsProbe;
//	private TimeOffsetProbe mTimeOffsetProbe;
//	private VideoCaptureProbe mVideoCaptureProbe;
//	private VideoMediaProbe mVideoMediaProbe;

	private AudioFeaturesProbe mAudioFeaturesProbe;

    private CheckBox enabledCheckbox;
    private Button archiveButton, scanNowButton;
    private Button uploadButton;
    private TextView dataCountView;
    private Handler handler;
    private ServiceConnection funfManagerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            funfManager = ((FunfManager.LocalBinder)service).getManager();

            Gson gson = funfManager.getGson();

//            wifiProbe = gson.fromJson(new JsonObject(), WifiProbe.class);
//            locationProbe = gson.fromJson(new JsonObject(), SimpleLocationProbe.class);
//	        mSmsProbe = gson.fromJson(new JsonObject(), SmsProbe.class);
//	        mTelephonyProbe = gson.fromJson(new JsonObject(), TelephonyProbe.class);
//	        mServicesProbe = gson.fromJson(new JsonObject(), ServicesProbe.class);
//	        mScreenProbe = gson.fromJson(new JsonObject(), ScreenProbe.class);
//	        mLinearAccelerationSensorProbe = gson.fromJson(new JsonObject(), LinearAccelerationSensorProbe.class);
//	        mMagneticFieldSensorProbe = gson.fromJson(new JsonObject(), MagneticFieldSensorProbe.class);
//	        mOrientationSensorProbe = gson.fromJson(new JsonObject(), OrientationSensorProbe.class);
//	        mRotationVectorSensorProbe = gson.fromJson(new JsonObject(), RotationVectorSensorProbe.class);
//	        mProximitySensorProbe = gson.fromJson(new JsonObject(), ProximitySensorProbe.class);
//	        mTemperatureSensorProbe = gson.fromJson(new JsonObject(), TemperatureSensorProbe.class);
//	        mPressureSensorProbe = gson.fromJson(new JsonObject(), PressureSensorProbe.class);
//	        mProbe = gson.fromJson(new JsonObject(), RunningApplicationsProbe.class);
//	        mProcessStatisticsProbe = gson.fromJson(new JsonObject(), ProcessStatisticsProbe.class);
//	        mTimeOffsetProbe = gson.fromJson(new JsonObject(), TimeOffsetProbe.class);
//	        mVideoCaptureProbe = gson.fromJson(new JsonObject(), VideoCaptureProbe.class);
//	        mVideoMediaProbe = gson.fromJson(new JsonObject(), VideoMediaProbe.class);
	        mAudioFeaturesProbe = gson.fromJson(new JsonObject(), AudioFeaturesProbe.class);

			        pipeline = (BasicPipeline) funfManager.getRegisteredPipeline(PIPELINE_NAME);
//            wifiProbe.registerPassiveListener(MainActivity.this);
//            locationProbe.registerPassiveListener(MainActivity.this);
//	        mSmsProbe.registerPassiveListener(MainActivity.this);
//	        mTelephonyProbe.registerPassiveListener(MainActivity.this);

//	        mServicesProbe.registerPassiveListener(MainActivity.this);
//	        mScreenProbe.registerPassiveListener(MainActivity.this);
//	        mLinearAccelerationSensorProbe.registerPassiveListener(MainActivity.this);
//	        mMagneticFieldSensorProbe.registerPassiveListener(MainActivity.this);
//	        mOrientationSensorProbe.registerPassiveListener(MainActivity.this);
//	        mRotationVectorSensorProbe.registerPassiveListener(MainActivity.this);
//	        mProximitySensorProbe.registerPassiveListener(MainActivity.this);
//	        mTemperatureSensorProbe.registerPassiveListener(MainActivity.this);
//	        mPressureSensorProbe.registerPassiveListener(MainActivity.this);
//	        mProbe.registerPassiveListener(MainActivity.this);
//	        mProcessStatisticsProbe.registerPassiveListener(MainActivity.this);
//	        mTimeOffsetProbe.registerPassiveListener(MainActivity.this);
//	        mVideoCaptureProbe.registerPassiveListener(MainActivity.this);
	        mAudioFeaturesProbe.registerPassiveListener(MainActivity.this);

            // This checkbox enables or disables the pipeline
            enabledCheckbox.setChecked(pipeline.isEnabled());
            enabledCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (funfManager != null) {
                        if (isChecked) {
                            funfManager.enablePipeline(PIPELINE_NAME);
                            pipeline = (BasicPipeline) funfManager.getRegisteredPipeline(PIPELINE_NAME);
                        } else {
                            funfManager.disablePipeline(PIPELINE_NAME);
                        }
                    }
                }
            });

            // Set UI ready to use, by enabling buttons
            enabledCheckbox.setEnabled(true);
            archiveButton.setEnabled(true);
            scanNowButton.setEnabled(true);
            uploadButton.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            funfManager = null;


        }
    };

	private void stopAudioFeatureProbeDelay() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mAudioFeaturesProbe.stop();

				scanNowButton.setEnabled(true);
			}
		}, RECORD_LONG);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Displays the count of rows in the data
        dataCountView = (TextView) findViewById(R.id.dataCountText);

        scanNowButton = (Button) findViewById(R.id.scanNowButton);
        scanNowButton.setEnabled(false);
        scanNowButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pipeline.isEnabled()) {
                    // Manually register the pipeline
//                    wifiProbe.registerListener(pipeline);
//                    locationProbe.registerListener(pipeline);
//	                mSmsProbe.registerListener(pipeline);
//	                mTelephonyProbe.registerListener(pipeline);
//	                mServicesProbe.registerListener(pipeline);
//	                mScreenProbe.registerListener(pipeline);
//	                mLinearAccelerationSensorProbe.registerListener(pipeline);
//	                mMagneticFieldSensorProbe.registerListener(pipeline);
//	                mOrientationSensorProbe.registerListener(pipeline);
//	                mRotationVectorSensorProbe.registerListener(pipeline);
//	                mProximitySensorProbe.registerListener(pipeline);
//	                mTemperatureSensorProbe.registerListener(pipeline);
//	                mPressureSensorProbe.registerListener(pipeline);
//	                mProbe.registerListener(pipeline);
//	                mProcessStatisticsProbe.registerListener(pipeline);
//	                mTimeOffsetProbe.registerListener(pipeline);
//	                mVideoCaptureProbe.registerListener(pipeline);
	                mAudioFeaturesProbe.registerListener(pipeline);
					// 停止录音
	                stopAudioFeatureProbeDelay();

                } else {
                    Toast.makeText(getBaseContext(), "Pipeline is not enabled.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Used to make interface changes on main thread
        handler = new Handler();

        enabledCheckbox = (CheckBox) findViewById(R.id.enabledCheckbox);
        enabledCheckbox.setEnabled(false);

        // Runs an archive if pipeline is enabled
        archiveButton = (Button) findViewById(R.id.archiveButton);
        archiveButton.setEnabled(false);
        archiveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pipeline.isEnabled()) {
                    pipeline.onRun(BasicPipeline.ACTION_ARCHIVE, null);

                    // Wait 1 second for archive to finish, then refresh the UI
                    // (Note: this is kind of a hack since archiving is seamless and there are no messages when it occurs)
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "Archived!", Toast.LENGTH_SHORT).show();
                            updateScanCount();
                        }
                    }, 1000L);
                } else {
                    Toast.makeText(getBaseContext(), "Pipeline is not enabled.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setEnabled(false);

        // Bind to the service, to create the connection with FunfManager
        bindService(new Intent(this, FunfManager.class), funfManagerConn, BIND_AUTO_CREATE);
    }

    @Override
    public void onDataCompleted(IJsonObject probeConfig, JsonElement checkpoint) {
        updateScanCount();
        // Re-register to keep listening after probe completes.
//        wifiProbe.registerPassiveListener(this);
//        locationProbe.registerPassiveListener(this);
//	    mSmsProbe.registerPassiveListener(this);
//	    mTelephonyProbe.registerPassiveListener(this);
//	    mServicesProbe.registerPassiveListener(this);
//	    mScreenProbe.registerPassiveListener(this);
//	    mLinearAccelerationSensorProbe.registerPassiveListener(this);
//	    mMagneticFieldSensorProbe.registerPassiveListener(this);
//	    mOrientationSensorProbe.registerPassiveListener(this);
//	    mRotationVectorSensorProbe.registerPassiveListener(this);
//	    mProximitySensorProbe.registerPassiveListener(this);
//	    mTemperatureSensorProbe.registerPassiveListener(this);
//	    mPressureSensorProbe.registerPassiveListener(this);
//	    mProbe.registerPassiveListener(this);
//	    mProcessStatisticsProbe.registerPassiveListener(this);
//	    mTimeOffsetProbe.registerPassiveListener(this);
//	    mVideoCaptureProbe.registerPassiveListener(this);

	    mAudioFeaturesProbe.registerPassiveListener(this);
    }

    private static final String TOTAL_COUNT_SQL = "SELECT count(*) FROM " + NameValueDatabaseHelper.DATA_TABLE.name;

    /**
     * Queries the database of the pipeline to determine how many rows of data we have recorded so far.
     */
    private void updateScanCount() {
        // Query the pipeline db for the count of rows in the data table
        SQLiteDatabase db = pipeline.getDatabaseHelper().getReadableDatabase();
        Cursor mcursor = db.rawQuery(TOTAL_COUNT_SQL, null);
        mcursor.moveToFirst();
        final int count = mcursor.getInt(0);
        // Update interface on main thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataCountView.setText("Data Count: " + count);
            }
        });
    }


    @Override
    public void onDataReceived(IJsonObject probeConfig, IJsonObject data) {
        // TODO Auto-generated method stub

    }

    public void upload(View v) {
        if (pipeline.isEnabled()) {
            pipeline.onRun(BasicPipeline.ACTION_UPLOAD, null);

            // Wait 1 second for archive to finish, then refresh the UI
            // (Note: this is kind of a hack since archiving is seamless and there are no messages when it occurs)
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), "Uploaded!", Toast.LENGTH_SHORT).show();
                    updateScanCount();
                }
            }, 1000L);
        } else {
            Toast.makeText(getBaseContext(), "Pipeline is not enabled.", Toast.LENGTH_SHORT).show();
        }
    }

}
