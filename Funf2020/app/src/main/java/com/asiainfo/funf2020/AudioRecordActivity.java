package com.asiainfo.funf2020;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.pipeline.BasicPipeline;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.AudioFeaturesProbe;
import edu.mit.media.funf.storage.NameValueDatabaseHelper;

/**
 * Created by Rocky on 16/8/30.
 */
public class AudioRecordActivity extends AppCompatActivity implements Probe.DataListener {



    public static final String PIPELINE_NAME = "default";
    public static final long RECORD_LONG = 10 * 1000L;
    public static final int MSG_ARCHIVE = 1;
    public static final int MSG_UPLOAD = 2;

    private FunfManager funfManager;
    private BasicPipeline pipeline;;
    private AudioFeaturesProbe mAudioFeatureProbe;
    private CheckBox enabledCheckbox;
    private Button archiveButton, scanNowButton;
    private Button uploadButton;
    private TextView dataCountView;
    private Handler handler;
    private boolean isRecording;

    private ServiceConnection funfManagerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            funfManager = ((FunfManager.LocalBinder)service).getManager();

            Gson gson = funfManager.getGson();

            mAudioFeatureProbe = gson.fromJson(new JsonObject(), AudioFeaturesProbe.class);
            pipeline = (BasicPipeline) funfManager.getRegisteredPipeline(PIPELINE_NAME);

            // This checkbox enables or disables the pipeline
            enabledCheckbox.setChecked(pipeline.isEnabled());
            enabledCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);

        // Used to make interface changes on main thread
        handler = new AudioHandler();

        // Displays the count of rows in the data
        dataCountView = (TextView) findViewById(R.id.dataCountText);

        scanNowButton = (Button) findViewById(R.id.scanNowButton);
        scanNowButton.setEnabled(false);
        scanNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pipeline.isEnabled()) {

                    if (isRecording) {
                        stopRecord();
                    } else {
                        startRecord();
                    }

                } else {
                    Toast.makeText(getBaseContext(), "Pipeline is not enabled.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        enabledCheckbox = (CheckBox) findViewById(R.id.enabledCheckbox);
        enabledCheckbox.setEnabled(false);

        // Runs an archive if pipeline is enabled
        archiveButton = (Button) findViewById(R.id.archiveButton);
        archiveButton.setEnabled(false);
        archiveButton.setOnClickListener(new View.OnClickListener() {
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

    private void stopAudioFeatureProbeDelay() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAudioFeatureProbe.stop();

                scanNowButton.setEnabled(true);
            }
        }, RECORD_LONG);
    }

    private void startRecord() {
        mAudioFeatureProbe.registerListener(pipeline);
        mAudioFeatureProbe.registerPassiveListener(AudioRecordActivity.this);

        scanNowButton.setText(R.string.stop);

        isRecording = true;

        sendArchiveMessage();
    }

    private void stopRecord() {
        mAudioFeatureProbe.unregisterListener(pipeline);
        mAudioFeatureProbe.unregisterPassiveListener(AudioRecordActivity.this);
        mAudioFeatureProbe.stop();

        scanNowButton.setText(R.string.record);

        isRecording = false;
    }

    private void archive() {
        if (pipeline.isEnabled()) {
            pipeline.onRun(BasicPipeline.ACTION_ARCHIVE, null);
        }
    }

    @Override
    public void onDataCompleted(IJsonObject probeConfig, JsonElement checkpoint) {
        updateScanCount();
        // Re-register to keep listening after probe completes.
        mAudioFeatureProbe.registerPassiveListener(this);
        //      locationProbe.registerPassiveListener(this);
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

    public void upload() {
        pipeline.onRun(BasicPipeline.ACTION_UPLOAD, null);
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

    private void sendArchiveMessage() {
        handler.sendEmptyMessageDelayed(MSG_ARCHIVE, 10 * 1000);
    }

    class AudioHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_ARCHIVE:
                    archive();

                    handler.sendEmptyMessageDelayed(MSG_UPLOAD, 2 *1000);

                    if (isRecording) {
                        sendArchiveMessage();
                    }
                    break;
                case MSG_UPLOAD:
                    upload();
                    break;
                default:
                    break;
            }
        }
    }

}
