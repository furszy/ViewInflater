package com.example.mati.draw.ui.inflater;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.AnalogClock;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.example.mati.draw.R;
import com.example.mati.draw.view.views.FermatTextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;


/**
 * Created by Matias Furszyfer 20/09/2015
 *
 *   matiasfurszyfer@gmail.com
 */

public class ViewInflater {


    final String NAMESPACE = "http://schemas.android.com/apk/res/android";

    Stack<ViewGroup> layoutStack;
    private Hashtable<String, Integer> ids;
    Context context;
    int idg;


    private ViewGroup parent;


    public ViewInflater(Context context) {
        this.layoutStack = new Stack<ViewGroup>();
        this.ids = new Hashtable<String, Integer>();
        this.context = context;
        this.idg = 0;
    }


    public View inflate(String text,ViewGroup parent) {
        XmlPullParser parse;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            parse = factory.newPullParser();
            parse.setInput(new StringReader(text));
            return inflate(parse,parent);
        }
        catch (XmlPullParserException ex) { return null; }
        catch (IOException ex) { ex.printStackTrace();
            return null;}
    }

    public View inflate(XmlPullParser parse,ViewGroup parent) throws XmlPullParserException, IOException {

        try{


            layoutStack.clear();
            ids.clear();
            parent = null;

            Stack<StringBuffer> data = new Stack<StringBuffer>();
            int evt = parse.getEventType();
            View root = null;

            if(parent != null){
                this.parent = parent;
                root = parent;
                layoutStack.push((ViewGroup)parent);
            }

            while (evt != XmlPullParser.END_DOCUMENT) {
                switch (evt) {
                    case XmlPullParser.START_DOCUMENT:
                        data.clear();
                        break;
                    case XmlPullParser.START_TAG:
                        data.push(new StringBuffer());
                        View v = createView(parse);
                        if (v == null)
                            continue;
                        if (root == null) {
                            root = v;
                        }
                        else {
                            layoutStack.peek().addView(v);
                        }
                        if (v instanceof ViewGroup) {
                            layoutStack.push((ViewGroup)v);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        data.peek().append(parse.getText());
                        break;
                    case XmlPullParser.END_TAG:
                        data.pop();
                        if (isLayout(parse.getName()))
                            layoutStack.pop();

                }
                evt = parse.next();
            }
            return root;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    protected View createView(XmlPullParser parse) {
        String name = parse.getName();
        View result = null;
        AttributeSet atts = Xml.asAttributeSet(parse);
        if (name.equals("LinearLayout")) {
            result = new LinearLayout(context);
        } else if (name.equals("RadioGroup")) {
            result = new RadioGroup(context);
        } else if (name.equals("TableRow")) {
            result = new TableRow(context);
        } else if (name.equals("TableLayout")) {
            result = new TableLayout(context);
        } else if (name.equals("AbsoluteLayout")) {
            result = new AbsoluteLayout(context);
        } else if (name.equals("RelativeLayout")) {
            result = new RelativeLayout(context);
        } else if (name.equals("android.support.v4.widget.SwipeRefreshLayout")) {
            result = new SwipeRefreshLayout(context);
        } else if (name.equals("android.support.v7.widget.RecyclerView")) {
            result = new RecyclerView(context);
        } else if (name.equals("ScrollView")) {
            result = new ScrollView(context);
        } else if (name.equals("FrameLayout")) {
            result = new FrameLayout(context);
        } else if (name.equals("ExpandableListView")) {
            result = new ExpandableListView(context);
        } else if (name.equals("TextView")) {
            result = new TextView(context);
        } else if (name.equals("com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView")) {
            result = new FermatTextView(context);
        } else if (name.equals("AutoCompleteTextView")) {
            result = new AutoCompleteTextView(context);
        } else if (name.equals("AnalogClock")) {
            result = new AnalogClock(context);
        } else if (name.equals("Button")) {
            result = new Button(context);
        } else if (name.equals("CheckBox")) {
            result = new CheckBox(context);
        } else if (name.equals("DatePicker")) {
            result = new DatePicker(context);
        } else if (name.equals("DigitalClock")) {
            result = new DigitalClock(context);
        } else if (name.equals("EditText")) {
            result = new EditText(context);
        } else if (name.equals("ProgressBar")) {
            result = new ProgressBar(context);
        } else if (name.equals("RadioButton")) {
            result = new RadioButton(context);
        } else if (name.equals("ImageView")) {
            result = new ImageView(context);
        } else {
            Toast.makeText(context, "Unhandled tag:" + name,
                    Toast.LENGTH_SHORT).show();
        }

        if (result == null)
            return null;

        String id = findAttribute(atts, "android:id");

        if (id != null) {
            int idNumber = lookupId(id);
            if (idNumber > -1) {
                result.setId(idNumber);
            }
        }

        if (result instanceof TextView) {
            TextView tv = (TextView) result;
            String text = findAttribute(atts, "android:text");
            if (text != null) {
                text = text.replace("\\n", "\n");
                tv.setText(text);
            }
        }
        if (result instanceof ImageView) {
            ImageView imageView = (ImageView) result;
            String src = findAttribute(atts, "android:src");
            if (src != null) {
                //Picasso.with(context).load(R.drawable.person1).into(imageView);
                //resourceProviderManager.getImageResource("person1",null,null);
                //Bitmap bitmap = BitmapFactory.decodeByteArray()
                imageView.setImageResource(R.drawable.mati_profile);
            }
        }

        if (result instanceof CompoundButton) {
            CompoundButton cb = (CompoundButton) result;
            String checked = findAttribute(atts, "android:checked");
            cb.setChecked("true".equals(checked));
        }

        if (result instanceof ProgressBar) {
            ProgressBar pb = (ProgressBar) result;
            String indet = findAttribute(atts, "android:indeterminate");
            if (indet != null) {
                pb.setIndeterminate("true".equals(indet));
            }
                        /*String orientation = findAttribute(atts, "android:orientation");
                        if (orientation != null) {
                                if ("horizontal".equals(orientation)) {
                                        pb.setOrientation(ProgressBar.HORIZONTAL);
                                }
                                else if ("vertical".equals(orientation)) {
                                        pb.setOrientation(ProgressBar.VERTICAL);
                                }
                        }*/
            String max = findAttribute(atts, "android:max");
            if (max != null) {
                pb.setMax(Integer.parseInt(max));
            }
        }

        if (result instanceof LinearLayout) {
            LinearLayout ll = (LinearLayout) result;
            String orient = findAttribute(atts, "android:orientation");
            if (orient != null) {
                if (orient.equals("horizontal"))
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                else if (orient.equals("vertical"))
                    ll.setOrientation(LinearLayout.VERTICAL);
            }
        }

        if (result instanceof RadioGroup) {
            RadioGroup rg = (RadioGroup) result;
            String cid = findAttribute(atts, "android:checkedButton");
            if (cid != null) {
                rg.check(Integer.parseInt(cid));
            }
        }

        if (result instanceof View) {
            View v = (View) result;
                /* API 11
                 String alpha = findAttribute(atts, "android:alpha");
                 if (alpha != null) {
                        v.setAlpha(Float.parseFloat(alpha));
                 }
                */
            maybeSetBoolean(v, "setClickable", atts, "android:clickable");
            maybeSetBoolean(v, "setFocusable", atts, "android:focusable");
            maybeSetBoolean(v, "setHapticFeedbackEnabled", atts, "android:hapticFeedbackEnabled");

            String visibility = findAttribute(atts, "android:visibility");
            if (visibility != null) {
                int code = -1;
                if ("visible".equals(visibility)) {
                    code = View.VISIBLE;
                } else if ("invisible".equals(visibility)) {
                    code = View.INVISIBLE;
                } else if ("gone".equals(visibility)) {
                    code = View.GONE;
                }
                if (code != -1) {
                    v.setVisibility(code);
                }
            }
            loadBasics(v, atts);
        }

        if (layoutStack.size() > 0) {
            result.setLayoutParams(loadLayoutParams(atts, layoutStack.peek()));
        }

        return result;

    }

    private void setPadding(View view,AttributeSet atts){
        String padding = findAttribute(atts, "android:padding");
        String paddingBottom = findAttribute(atts,"android:paddingBottom");
        String paddingTop = findAttribute(atts, "android:paddingTop");
        String paddingLeft = findAttribute(atts,"android:paddingLeft");
        String paddingRight = findAttribute(atts, "android:paddingRight");

        int paddingValue = 0;
        int paddingLeftValue = 0;
        int paddingTopValue = 0;
        int paddingRightValue = 0;
        int paddingBottomValue = 0;

        if(padding!=null){
            paddingValue = (int)convertInFloatValue(padding);
        }
        if(paddingBottom!=null){
            paddingBottomValue = (int)convertInFloatValue(paddingBottom);
        }
        if(paddingTop!=null){
            paddingTopValue = (int)convertInFloatValue(paddingTop);
        }
        if(paddingLeft!=null){
            paddingLeftValue = (int)convertInFloatValue(paddingLeft);
        }
        if(paddingRight!=null){
            paddingRightValue = (int)convertInFloatValue(paddingRight);
        }


        boolean[] deco = new boolean[4];

        deco[0] = paddingLeftValue == 0;;
        deco[1] = paddingTopValue == 0;
        deco[2] = paddingRightValue == 0;
        deco[3] = paddingBottomValue == 0;

        if(deco[0]){
            paddingLeftValue = paddingValue;
        }
        if(deco[1]){
            paddingTopValue = paddingValue;
        }
        if(deco[2]){
            paddingRightValue = paddingValue;
        }
        if(deco[3]){
            paddingBottomValue = paddingValue;
        }
        view.setPadding(paddingLeftValue,paddingTopValue,paddingRightValue,paddingBottomValue);




    }



    private void loadBasics(View view,AttributeSet atts){


        setPadding(view,atts);
        String value;

        // background
        //value = atts.getAttributeValue(NAMESPACE,"background");
        value = findAttribute(atts,"android:background");
        if(value!=null){
            if(value.indexOf("@")==-1){
                //view.setBackground();
                view.setBackgroundColor(Color.parseColor(value));
            }else{
                // view.setBackgroundColor(Color.parseColor(value));
            }
        }
        // elevation
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            //value = findAttribute(atts,"android:elevation");
            //if(value!=null){
            //view.setElevation(Float.parseFloat(value));
            //}
        }


        // Gravity
        String gravity = findAttribute(atts,"android:gravity");

        //l.gravity = Integer.parseInt(gravity);
        if(view instanceof LinearLayout){
            if (gravity != null) {

                switch (gravity) {
                    case "center":
                        ((LinearLayout) view).setGravity(Gravity.CENTER);
                        break;
                    case "right":
                        ((LinearLayout) view).setGravity(Gravity.RIGHT);
                        break;
                    case "left":
                        ((LinearLayout) view).setGravity(Gravity.LEFT);
                        break;
                    case "bottom":
                        ((LinearLayout) view).setGravity(Gravity.BOTTOM);
                        break;
                }
            }

        }
        else if (view instanceof RadioGroup){

        }
        else if (view instanceof TableLayout){

        }
        else if (view instanceof TableRow){

        }
        else if (view instanceof AbsoluteLayout){

        }
        else if (view instanceof RelativeLayout){

        }
        else if (view instanceof SwipeRefreshLayout){

        }
        else if (view instanceof RecyclerView){

        }
        else if (view instanceof ScrollView){

        }
        else if (view instanceof FrameLayout){

        }
        else if (view instanceof ExpandableListView){

        }
        else if (view instanceof TextView){
            if (gravity != null) {

                switch (gravity) {
                    case "center":
                        ((TextView) view).setGravity(Gravity.CENTER);
                        break;
                    case "right":
                        ((TextView) view).setGravity(Gravity.RIGHT);
                        break;
                    case "left":
                        ((TextView) view).setGravity(Gravity.LEFT);
                        break;
                    case "bottom":
                        ((TextView) view).setGravity(Gravity.BOTTOM);
                        break;
                }
            }

            value = findAttribute(atts,"android:textSize");
            if(value!=null){
                ((TextView) view).setTextSize(readSize(value));
            }
            value = findAttribute(atts,"android:textColor");
            if(value!=null){
                //((TextView) view).setTextColor(Color.parseColor(value));
            }
        }
        else if (view instanceof AutoCompleteTextView){

        }
        else if (view instanceof AnalogClock){

        }else if (view instanceof Button){
            if (gravity != null) {

                switch (gravity) {
                    case "center":
                        ((Button) view).setGravity(Gravity.CENTER);
                        break;
                    case "right":
                        ((Button) view).setGravity(Gravity.RIGHT);
                        break;
                    case "left":
                        ((Button) view).setGravity(Gravity.LEFT);
                        break;
                    case "bottom":
                        ((Button) view).setGravity(Gravity.BOTTOM);
                        break;
                }
            }
        }

        else if (view instanceof CheckBox){

        }
        else if (view instanceof DatePicker){

        }
        else if (view instanceof DigitalClock){

        }
        else if (view instanceof EditText){
            if (gravity != null) {

                switch (gravity) {
                    case "center":
                        ((EditText) view).setGravity(Gravity.CENTER);
                        break;
                    case "right":
                        ((EditText) view).setGravity(Gravity.RIGHT);
                        break;
                    case "left":
                        ((EditText) view).setGravity(Gravity.LEFT);
                        break;
                    case "bottom":
                        ((EditText) view).setGravity(Gravity.BOTTOM);
                        break;
                }
            }
        }
        else if (view instanceof ProgressBar){

        }
        else if (view instanceof ImageView){

        }




    }


    private boolean maybeSetBoolean(View v, String method, AttributeSet atts, String arg) {
        return maybeSetBoolean(v, method, findAttribute(atts, arg));
    }

    protected static boolean isLayout(String name) {
        return name.endsWith("Layout") ||
                name.equals("RadioGroup") ||
                name.equals("TableRow") ||
                name.equals("ScrollView");
    }

    protected int lookupId(String id) {
        int ix = id.indexOf("/");
        if (ix != -1) {
            String idName = id.substring(ix+1);
            Integer n = ids.get(idName);
            if (n == null && id.startsWith("@+")) {
                n = new Integer(idg++);
                ids.put(idName, n);
            }
            if (n != null)
                return n.intValue();
        }
        return -1;
    }

    protected String findAttribute(AttributeSet atts, String id) {
        for (int i=0;i<atts.getAttributeCount();i++) {
            if (atts.getAttributeName(i).equals(id)) {

                return atts.getAttributeValue(i);
            }
        }
        int ix = id.indexOf(":");
        if (ix != -1) {
            return atts.getAttributeValue("http://schemas.android.com/apk/res/android", id.substring(ix+1));
        }
        else {
            return null;
        }
    }

    protected ViewGroup.LayoutParams loadLayoutParams(AttributeSet atts, ViewGroup vg) {
        ViewGroup.LayoutParams lps = null;

        String width = findAttribute(atts, "android:layout_width");
        String height = findAttribute(atts, "android:layout_height");
        int w, h;
        w =(int) readSize(width);
        h =(int) readSize(height);

        if (vg instanceof RadioGroup) {
            lps = new RadioGroup.LayoutParams(w, h);
        }
        else if (vg instanceof TableRow) {
            lps = new TableRow.LayoutParams();
        }
        else if (vg instanceof TableLayout) {
            lps = new TableLayout.LayoutParams();
        }
        else if (vg instanceof LinearLayout) {
            lps = new LinearLayout.LayoutParams(w,h);
        }
        else if (vg instanceof SwipeRefreshLayout){
            lps = new SwipeRefreshLayout.LayoutParams(w,h);
        }
        else if (vg instanceof AbsoluteLayout) {
            String x = findAttribute(atts, "android:layout_x");
            String y = findAttribute(atts, "android:layout_y");

            lps = new AbsoluteLayout.LayoutParams(w, h, (int)readSize(x),(int) readSize(y));
        }
        else if (vg instanceof RelativeLayout) {
            lps = new RelativeLayout.LayoutParams(w,h);
        }
        else if (vg instanceof ScrollView) {
            lps = new ScrollView.LayoutParams(w,h);
        }
        else if (vg instanceof FrameLayout) {
            lps = new FrameLayout.LayoutParams(w,h);
        }

        if (lps instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams l = (LinearLayout.LayoutParams)lps;
            String gravity = findAttribute(atts, "android:layout_gravity");
            if (gravity != null) {
                switch (gravity){
                    case "center":
                        l.gravity = Gravity.CENTER;
                        break;
                    case "right":
                        l.gravity = Gravity.RIGHT;
                        break;
                    case "left":
                        l.gravity = Gravity.LEFT;
                        break;
                    case "bottom":
                        l.gravity = Gravity.BOTTOM;
                        break;

                }
                //l.gravity = Integer.parseInt(gravity);
            }

            String weight = findAttribute(atts, "android:layout_weight");
            if (weight != null) {
                l.weight = Float.parseFloat(weight);
            }

            String bottom = findAttribute(atts, "android:layout_marginBottom");
            String left = findAttribute(atts, "android:layout_marginLeft");
            String right = findAttribute(atts, "android:layout_marginRight");
            String top = findAttribute(atts, "android:layout_marginTop");
            int bottomInt=0, leftInt=0, rightInt=0, topInt=0;
            if (bottom != null)
                bottomInt =(int) readSize(bottom);
            if (left != null)
                leftInt = (int)readSize(left);
            if (right != null)
                rightInt =(int) readSize(right);
            if (top != null)
                topInt =(int) readSize(top);

            l.setMargins(leftInt, topInt, rightInt, bottomInt);



            lps = l;
        }

        if (lps instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams)lps;
            for (int i=0;i<relative_strings.length;i++) {
                String id  = findAttribute(atts, relative_strings[i]);
                if (id != null) {
                    int idN = lookupId(id);
                    l.addRule(relative_verbs[i], idN);
                }
            }
            String bottom = findAttribute(atts, "android:layout_marginBottom");
            String left = findAttribute(atts, "android:layout_marginLeft");
            String right = findAttribute(atts, "android:layout_marginRight");
            String top = findAttribute(atts, "android:layout_marginTop");
            int bottomInt=0, leftInt=0, rightInt=0, topInt=0;
            if (bottom != null)
                bottomInt = (int) readSize(bottom);
            if (left != null)
                leftInt =(int) readSize(left);
            if (right != null)
                rightInt = (int)readSize(right);
            if (top != null)
                topInt =(int) readSize(top);

            l.setMargins(leftInt, topInt, rightInt, bottomInt);
        }

        return lps;
    }

    protected float readSize(String val) {
        if ("wrap_content".equals(val)) {
            return ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        else if ("fill_parent".equals(val)) {
            return ViewGroup.LayoutParams.FILL_PARENT;
        }
        else if("match_parent".equals(val)){
            return ViewGroup.LayoutParams.MATCH_PARENT;
        }
        else if (val != null) {
            return convertInFloatValue(val);

        }
        else {
            return ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }


    /**
     *  Example convert 16dp in 16
     *
     * @param value
     * @return
     */
    private float convertInFloatValue(String value){
        float converterer = 0;
        int indexValue = value.indexOf("dp");
        if (indexValue != -1){
            //converterer =Math.round( convertPixelsToDp(Float.parseFloat(value.substring(0, indexValue))));
            converterer = convertToDp(Float.parseFloat(value.substring(0, indexValue)));
        } else{
            indexValue = value.indexOf("px");
            if (indexValue != -1){
                converterer = Float.parseFloat(value.substring(0, indexValue));
            }else{
                indexValue = value.indexOf("sp");
                if (indexValue != -1) {
                    converterer = Float.parseFloat(value.substring(0, indexValue));
                }else converterer = 0;
            }
        }
        return converterer;
    }


    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public float convertToDp(float px){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,px, context.getResources().getDisplayMetrics());
    }


    boolean maybeSetBoolean(View view, String method, String value) {
        if (value == null) {
            return false;
        }
        value = value.toLowerCase();
        Boolean boolValue = null;
        if ("true".equals(value)) {
            boolValue = Boolean.TRUE;
        } else if ("false".equals(value)) {
            boolValue = Boolean.FALSE;
        } else {
            return false;
        }
        try {
            Method m = View.class.getMethod(method, boolean.class);
            m.invoke(view, boolValue);
            return true;
        } catch (NoSuchMethodException ex) {
            Log.e("ViewInflater", "No such method: " + method, ex);
        } catch (IllegalArgumentException e) {
            Log.e("ViewInflater", "Call", e);
        } catch (IllegalAccessException e) {
            Log.e("ViewInflater", "Call", e);
        } catch (InvocationTargetException e) {
            Log.e("ViewInflater", "Call", e);
        }
        return false;
    }

    String[] relative_strings = new String[]
            {"android:layout_above",
                    "android:layout_alignBaseline",
                    "android:layout_alignBottom",
                    "android:layout_alignLeft",
                    "android:layout_alignParentBottom",
                    "android:layout_alignParentLeft",
                    "android:layout_alignParentRight",
                    "android:layout_alignParentTop",
                    "android:layout_alignRight",
                    "android:layout_alignTop",
                    "android:layout_below",
                    "android:layout_centerHorizontal",
                    "android:layout_centerInParent",
                    "android:layout_centerVertical",
                    "android:layout_toLeftOf",
                    "android:layout_toRightOf"};

    int[] relative_verbs = new int[]
            {RelativeLayout.ABOVE,
                    RelativeLayout.ALIGN_BASELINE,
                    RelativeLayout.ALIGN_BOTTOM,
                    RelativeLayout.ALIGN_LEFT,
                    RelativeLayout.ALIGN_PARENT_BOTTOM,
                    RelativeLayout.ALIGN_PARENT_LEFT,
                    RelativeLayout.ALIGN_PARENT_RIGHT,
                    RelativeLayout.ALIGN_PARENT_TOP,
                    RelativeLayout.ALIGN_RIGHT,
                    RelativeLayout.ALIGN_TOP,
                    RelativeLayout.BELOW,
                    RelativeLayout.CENTER_HORIZONTAL,
                    RelativeLayout.CENTER_IN_PARENT,
                    RelativeLayout.CENTER_VERTICAL,
                    RelativeLayout.LEFT_OF,
                    RelativeLayout.RIGHT_OF,
            };


    public int getIdFromName(String id){
        return ids.get(id);
    }

    public Map<String,Integer> getIdsFromLayout(){
        return ids;
    }
}