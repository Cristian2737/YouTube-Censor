#!/usr/bin/env python3
"""Actual channel toolbar/editor callbacks with host view doubles, not phone UI."""
import argparse
import os
from pathlib import Path
import subprocess
import tempfile
from run_word_editor_tests import STUBS as WORD_STUBS, ROOT

STUBS = dict(WORD_STUBS)
STUBS.update({
    "android/view/View.java": """package android.view; public class View {
    public static final int NO_ID=-1,VISIBLE=0,GONE=8; public int id=NO_ID,visibility=0;
    public ViewGroup parent; public boolean selected; public String description; public Object params;
    private android.content.Context context;
    public View(){this(new android.content.Context());} public View(android.content.Context c){context=c;}
    public android.content.Context getContext(){return context;}
    public android.content.res.Resources getResources(){return context.getResources();}
    public ViewGroup getParent(){return parent;} public int getId(){return id;}
    public interface OnClickListener{void onClick(View v);} public OnClickListener click;
    public void setOnClickListener(OnClickListener l){click=l;} public void performClick(){if(click!=null)click.onClick(this);}
    public void setPadding(int a,int b,int c,int d){} public void setContentDescription(String s){description=s;}
    public void setSelected(boolean b){selected=b;} public void setVisibility(int n){visibility=n;}
    public void setFocusable(boolean b){} public void setClickable(boolean b){}
    public void setMinimumWidth(int n){} public void setMinimumHeight(int n){}
    public void post(Runnable r){r.run();} public void requestFocus(){}
    public void setLayoutParams(ViewGroup.LayoutParams p){params=p;}
    public ViewGroup.LayoutParams getLayoutParams(){return (ViewGroup.LayoutParams)params;} }
    """,
    "android/view/ViewGroup.java": """package android.view; import java.util.*; public class ViewGroup extends View {
    public final List<View> children=new ArrayList<>(); public ViewGroup(){super();} public ViewGroup(android.content.Context c){super(c);}
    public void addView(View v){children.add(v);v.parent=this;} public void addView(View v,int at){children.add(at,v);v.parent=this;}
    public void addView(View v,LayoutParams p){v.setLayoutParams(p);addView(v);}
    public void removeView(View v){children.remove(v);v.parent=null;} public int indexOfChild(View v){return children.indexOf(v);}
    public View getChildAt(int i){return children.get(i);} public int getChildCount(){return children.size();}
    public static class LayoutParams {public int width,height; public LayoutParams(int w,int h){width=w;height=h;}
    public LayoutParams(LayoutParams p){this(p.width,p.height);}}
    public static class MarginLayoutParams extends LayoutParams {public MarginLayoutParams(LayoutParams p){super(p);}} }
    """,
    "android/widget/LinearLayout.java": """package android.widget; public class LinearLayout extends android.view.ViewGroup {
    public static final int VERTICAL=1; public LinearLayout(android.content.Context c){super(c);}
    public void setOrientation(int n){} public void setGravity(int n){}
    public static class LayoutParams extends android.view.ViewGroup.MarginLayoutParams {
    public float weight; public LayoutParams(int w,int h){super(new android.view.ViewGroup.LayoutParams(w,h));}
    public LayoutParams(int w,int h,float f){this(w,h);weight=f;}
    public LayoutParams(LayoutParams p){this(p.width,p.height,p.weight);}} }
    """,
    "android/widget/FrameLayout.java": """package android.widget; public class FrameLayout extends android.view.ViewGroup {
    public FrameLayout(android.content.Context c){super(c);}
    public static class LayoutParams extends android.view.ViewGroup.MarginLayoutParams {
    public LayoutParams(int w,int h,int g){super(new android.view.ViewGroup.LayoutParams(w,h));}
    public LayoutParams(LayoutParams p){super(p);}} }
    """,
    "android/widget/ImageView.java": """package android.widget; public class ImageView extends android.view.View {
    public ImageView(android.content.Context c){super(c);} public enum ScaleType {FIT_CENTER}
    public void setScaleType(ScaleType t){} public void setImageDrawable(android.graphics.drawable.Drawable d){} }
    """,
    "android/view/Gravity.java": "package android.view; public class Gravity {public static final int CENTER_VERTICAL=16,CENTER=17;}",
    "android/graphics/drawable/Drawable.java": "package android.graphics.drawable; public class Drawable {public void setTint(int c){}}",
    "android/content/res/Resources.java": """package android.content.res; public class Resources {
    public android.util.DisplayMetrics getDisplayMetrics(){return new android.util.DisplayMetrics();}
    public String getResourceEntryName(int id){return id==1?"toolbar":"menu_item_search";} }
    """,
    "app/morphe/extension/shared/ResourceUtils.java": """package app.morphe.extension.shared; public class ResourceUtils {
    public static int getIdIdentifier(String s){return 1;}
    public static android.graphics.drawable.Drawable getDrawable(String s){return new android.graphics.drawable.Drawable();} }
    """,
    "app/morphe/extension/shared/Logger.java": """package app.morphe.extension.shared; public class Logger {
    public static void printException(java.util.function.Supplier<String> s,Throwable t){throw new AssertionError(s.get(),t);} }
    """,
    "app/morphe/extension/youtube/settings/Settings.java": """package app.morphe.extension.youtube.settings; public class Settings {
    public static class Value<T>{private T v;public Value(T x){v=x;}public T get(){return v;}public void save(T x){v=x;}}
    public static final Value<String> PROFANITY_NEVER_CHANNELS=new Value<>(""),PROFANITY_CHANNEL_LABELS=new Value<>(""); }
    """,
    "app/morphe/extension/youtube/patches/ProfanityFilterPatch.java": """package app.morphe.extension.youtube.patches; public class ProfanityFilterPatch {
    public static int refreshes;public static void channelRulesChanged(){refreshes++;} }
    """,
    "app/morphe/extension/youtube/patches/VideoInformation.java": """package app.morphe.extension.youtube.patches; public class VideoInformation {
    public static String id="UCabcdefghijklmnopqrstuv",name="Different playing channel";
    public static String getChannelId(){return id;}public static String getChannelName(){return name;} }
    """,
})

def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--java-home",type=Path,default=os.environ.get("JAVA_HOME"))
    args=parser.parse_args()
    def exe(name):
        return str(args.java_home/"bin"/(name+(".exe" if os.name=="nt" else ""))) if args.java_home else name
    with tempfile.TemporaryDirectory(prefix="censor-channel-editor-") as temp:
        root=Path(temp); sources=[ROOT/"src"/name for name in ("ProfanityChannels.java","ProfanityChannelWhitelist.java","ProfanityChannelsPreference.java")]
        sources.append(ROOT/"tests/ProfanityChannelUiTest.java")
        for name,value in STUBS.items():
            file=root/name;file.parent.mkdir(parents=True,exist_ok=True);file.write_text(value,encoding="utf-8");sources.append(file)
        classes=root/"classes";classes.mkdir()
        subprocess.run([exe("javac"),"-encoding","UTF-8","-d",str(classes),*map(str,sources)],check=True)
        subprocess.run([exe("java"),"-cp",str(classes),"app.morphe.extension.youtube.patches.ProfanityChannelUiTest"],check=True)

if __name__=="__main__": main()
