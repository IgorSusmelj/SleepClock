package com.pps.sleepcalc;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;

public class showGraphActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.graph);
        
		final WebView webview = (WebView) findViewById(R.id.webview);
		
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
		webview.setInitialScale(100);
		
		webview.loadUrl("file:///android_asset/test.html");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
