package com.example.mati.draw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Hashtable;
import java.util.Stack;

public class AnDroidDraw extends Activity implements  View.OnClickListener{
	Stack<ViewGroup> layoutStack;
	Hashtable<String, Integer> ids;

	TextView lt;
	Button preview;
	Handler h;

	String layout;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		this.setContentView(R.layout.main);
		this.lt = (TextView)this.findViewById(R.id.text);
		this.preview = (Button)this.findViewById(R.id.layout);
		this.preview.setOnClickListener(this);
		((Button)this.findViewById(R.id.clean)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				lt.setText("");
			}
		});

		this.h = new Handler();
	}

	public void updateLayout() {
		lt.setText(layout);
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}



	public void onClick(View v) {
		if (v == preview) {
			Intent i = new Intent(this, ParseActivity.class);
			String data;
			if (lt.getText().length() > 0) {
				data = lt.getText().toString();
			}
			else {
				data = layout;
			}
			i.putExtra(ParseActivity.DATA, data);
			startActivity(i);
		}
	}
}