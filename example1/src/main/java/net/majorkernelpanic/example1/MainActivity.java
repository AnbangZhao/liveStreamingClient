package net.majorkernelpanic.example1;

import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspServer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * A straightforward example of how to use the RTSP server included in libstreaming.
 */
public class MainActivity extends Activity {

	private final static String TAG = "MainActivity";

	private SurfaceView mSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mSurfaceView = (SurfaceView) findViewById(R.id.surface);
		
		// Sets the port of the RTSP server to 1234
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString(RtspServer.KEY_PORT, String.valueOf(1234));
		editor.commit();

		// Configures the SessionBuilder
		SessionBuilder.getInstance()
		.setSurfaceView(mSurfaceView)
		.setPreviewOrientation(90)
		.setContext(getApplicationContext())
		.setAudioEncoder(SessionBuilder.AUDIO_NONE)
		.setVideoEncoder(SessionBuilder.VIDEO_H264);
		
		// Starts the RTSP server
		this.startService(new Intent(this,RtspServer.class));
        new NetConnection().execute(null);
	}


    private class NetConnection extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String url = null;
                url = getUrl();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getLocalIpAddress() {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface
                        .getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf
                            .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        System.out.println("ip1--:" + inetAddress);
                        System.out.println("ip2--:" + inetAddress.getHostAddress());

                        // for getting IPV4 format
                        if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                            String ipv4 = inetAddress.getHostAddress();
                            String ip = inetAddress.getHostAddress().toString();
                            System.out.println("ip---::" + ip);
                            // return inetAddress.getHostAddress().toString();
                            return ipv4;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        private String getUrl() throws UnknownHostException {
            String myip = getLocalIpAddress();
            //String myip = Inet4Address.getLocalHost().getHostAddress();
            String url = "http://10.0.0.11:8000/openstream?username=anbang&appname=liveStreaming&stream=myStream&clientIP="+myip;
            System.err.println("url:" + url);
            return url;
        }
    }
	
}