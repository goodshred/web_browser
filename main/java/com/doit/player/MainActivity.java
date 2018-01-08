package com.doit.player;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView url;
    WebView show;

    String[] booksArray = new String[]
            {
                    "http://maps.google.com",
                    "http://maps.baidu.com",
                    "http://qq.com",
                    "www.baidu.com",
                    "www.163.com"
            };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Activity activity = this;

        show = (WebView)findViewById(R.id.show);
        show.getSettings().setJavaScriptEnabled(true);
        show.getSettings().setBuiltInZoomControls(true);
        //show.getSettings().setDisplayZoomControls(false);
        show.setWebViewClient(new WebViewClient()
        {
            public boolean shouldOverrideUrlLoading(WebView view, String strUrl)
            {
                view.loadUrl(strUrl);
                url.setText(strUrl);
                return false;
            }

            public void onPageStarted(WebView view, String strUrl, Bitmap favicon)
            {
                super.onPageStarted(view, strUrl, favicon);
                url.setText(strUrl);
            }

            public void onPageFinished(WebView view, String strUrl)
            {
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {}
        });


        url = (AutoCompleteTextView)findViewById(R.id.url);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, booksArray);
        url.setAdapter(aa);
        url.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent ev)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    String strUrl = url.getText().toString();

                    Pattern p = Pattern.compile("http://([\\w-]+\\.)+[\\w-]+(/[\\w-\\./?%=]*)?");
                    Matcher m = p.matcher(strUrl);
                    if (!m.find())
                    {
                        strUrl = "http://" + strUrl;
                    }

                    show.loadUrl(strUrl);

                    return true;
                }

                return false;
            }
        });

        // button
        final Button backBtn = (Button)findViewById(R.id.back);
        final Button forwardBtn = (Button)findViewById(R.id.forward);
        Button refreshBtn = (Button)findViewById(R.id.refresh);
        Button homeBtn = (Button)findViewById(R.id.home);
        backBtn.setEnabled(false);
        forwardBtn.setEnabled(false);

        backBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                show.goBack();
            }
        });

        forwardBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // TODO
                show.goForward();
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // TODO
                String strUrl = url.getText().toString();
                show.loadUrl(strUrl);
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // TODO
                show.loadUrl("http://maps.google.com");
            }
        });

        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == 0x1111)
                {
                    // whether can go back
                    if (show.canGoBack())
                    {
                        backBtn.setEnabled(true);
                    }
                    else
                    {
                        backBtn.setEnabled(false);
                    }

                    // whether can go forward
                    if (show.canGoForward())
                    {
                        forwardBtn.setEnabled(true);
                    }
                    else
                    {
                        forwardBtn.setEnabled(false);
                    }
                }

                super.handleMessage(msg);
            }
        };

        // create thread to change button states
        new Timer().schedule(new TimerTask()
        {
            public void run()
            {
                Message msg = new Message();
                msg.what = 0x1111;
                handler.sendMessage(msg);
            }
        }, 0, 100);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && show.canGoBack())
        {
            show.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}


