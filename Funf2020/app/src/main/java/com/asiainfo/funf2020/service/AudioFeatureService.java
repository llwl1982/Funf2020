package com.asiainfo.funf2020.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.asiainfo.funf2020.R;
import com.asiainfo.funf2020.activity.AudioRecordActivity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.ref.WeakReference;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.pipeline.BasicPipeline;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.AudioFeaturesProbe;

/**
 * 控制音频数据采集，归档和上传的Service，设置成前台服务，以尽量保证服务存活
 * Created by Justin Z on 2016/9/8.
 * 502953057@qq.com
 */
public class AudioFeatureService extends Service implements Probe.DataListener {

	private final static String TAG = AudioFeatureService.class.getSimpleName();

	private final static int NOTIFICATION_ID = 0x00023;
	public static final String PIPELINE_NAME = "default";

	public static final int MSG_ARCHIVE = 1;
	public static final int MSG_UPLOAD = 2;


	private FunfManager funfManager;

	private BasicPipeline pipeline;
	private AudioFeaturesProbe mAudioFeatureProbe;

	private AudioHandler mAudioHandler;


	// 同FunfManager的连接
	private ServiceConnection funfManagerConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			funfManager = ((FunfManager.LocalBinder) service).getManager();
			Gson gson = funfManager.getGson();
			mAudioFeatureProbe = gson.fromJson(new JsonObject(), AudioFeaturesProbe.class);
			funfManager.enablePipeline(PIPELINE_NAME);
			pipeline = (BasicPipeline) funfManager.getRegisteredPipeline(PIPELINE_NAME);

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			funfManager = null;
		}
	};

	/**
	 * 上传数据
	 */
	private void upload() {
		pipeline.onRun(BasicPipeline.ACTION_UPLOAD, null);
	}

	private void archive() {
		if (pipeline.isEnabled()) {
			pipeline.onRun(BasicPipeline.ACTION_ARCHIVE, null);
		}
	}

	private void sendArchiveMessage() {
		mAudioHandler.sendEmptyMessageDelayed(MSG_ARCHIVE, 10 * 1000);
	}

	private void sendUploadMessage() {
		mAudioHandler.sendEmptyMessageDelayed(MSG_UPLOAD, 10 * 1000);
	}

	private void startRecord() {
		if (mAudioFeatureProbe == null) return;
		mAudioFeatureProbe.registerListener(pipeline);
		mAudioFeatureProbe.registerPassiveListener(this);
		sendArchiveMessage();
	}

	private void stopRecord() {
		if (mAudioFeatureProbe == null) return;
		mAudioFeatureProbe.unregisterListener(pipeline);
		mAudioFeatureProbe.unregisterPassiveListener(this);
		mAudioFeatureProbe.stop();
	}

	static class AudioHandler extends Handler {

		private final WeakReference<AudioFeatureService> mService;

		public AudioHandler(AudioFeatureService service) {
			mService = new WeakReference<>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			AudioFeatureService service = mService.get();
			if (service != null) {
				switch (msg.what) {
					case MSG_ARCHIVE:
						service.archive();
						// 发送上传数据消息
						service.sendUploadMessage();
						// 发送归档消息
						service.sendArchiveMessage();
						break;
					case MSG_UPLOAD:
						service.upload();
						break;
					default:
						break;
				}
			}
		}
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return new AudioFeatureBinder();
	}

	/**
	 * 自定义Binder类，将Service数据传递给客户端
	 */
	public class AudioFeatureBinder extends Binder {

		/**
		 * 获取服务实例
		 *
		 * @return AudioFeatureService
		 */
		public AudioFeatureService getService() {
			return AudioFeatureService.this;
		}
	}

	public boolean getStatus() {
		if (funfManager != null)
			return true;
		return false;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mAudioHandler = new AudioHandler(this);

		// 启动成为前台服务
		startForegroundCompat();
		// Bind to the service, to create the connection with FunfManager
		bindService(new Intent(this, FunfManager.class), funfManagerConn, BIND_AUTO_CREATE);
		// 启动funfManager服务，并绑定到改服务，随后开始录制
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startRecord();
			}
		}, 1000);

	}

	/**
	 * 设置为前台服务
	 */
	private void startForegroundCompat() {
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
		Intent notificationIntent = new Intent(this, AudioRecordActivity.class);
		PendingIntent pdIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		Notification.Builder builder = new Notification.Builder(this);
		builder.setSmallIcon(R.mipmap.ic_launcher);
		builder.setTicker(getString(R.string.audio_capture));
		builder.setContent(contentView);
		builder.setContentIntent(pdIntent);
		// 设置服务为前台服务
		startForeground(NOTIFICATION_ID, builder.build());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		startRecord();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("AudioFeatureService", "--AudioFeatureService挂了");
		// 结束前台服务
		stopForeground(true);

		// 解除绑定
		unbindService(funfManagerConn);
		stopRecord();
	}


	@Override
	public void onDataReceived(IJsonObject probeConfig, IJsonObject data) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDataCompleted(IJsonObject probeConfig, JsonElement checkpoint) {
		mAudioFeatureProbe.registerPassiveListener(this);
	}
}
