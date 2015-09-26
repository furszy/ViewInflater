package com.example.mati.draw;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.mati.draw.ui.inflater.ViewInflater;

public class ParseActivity extends Activity {
	public static final String DATA = "data";
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.setTitle("MAtiDraw: Preview");
		String data = getIntent().getStringExtra(DATA);
		


		LinearLayout linearLayout = new LinearLayout(this);

		linearLayout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		linearLayout.setLayoutParams(lps);

		ViewInflater inflater = new ViewInflater(this);

		XmlPullParser parse;		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			parse = factory.newPullParser();
			
			parse.setInput(new StringReader(data));
			
			View v = inflater.inflate(parse,linearLayout);

			setContentView(v);
		}
		catch (XmlPullParserException ex) { error(ex); }
		catch (IOException ex) { error(ex); }		
	}
	
	protected void error(Exception ex) {
		ex.printStackTrace();
	}
}
