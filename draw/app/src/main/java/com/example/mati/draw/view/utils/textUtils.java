package com.example.mati.draw.view.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Matias Furszyfer on 2015.08.13..
 */
public class textUtils {

    /**
     * copy text to clipboard
    */

    public void copyToClipboard(Activity activity,String text) {

        ClipboardManager myClipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
        Toast.makeText(activity.getApplicationContext(), "Text Copied",
                Toast.LENGTH_SHORT).show();
    }

}
