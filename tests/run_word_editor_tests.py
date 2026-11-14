#!/usr/bin/env python3
"""Run real word-editor callbacks against Android view doubles, not a device UI."""
import argparse
import os
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parent.parent
STUBS = {
    "android/content/Context.java": """package android.content; public class Context {
        public android.content.res.Resources getResources(){return new android.content.res.Resources();}}
    """,
    "android/content/res/Resources.java": """package android.content.res; public class Resources {
        public android.util.DisplayMetrics getDisplayMetrics(){return new android.util.DisplayMetrics();}}
    """,
    "android/util/DisplayMetrics.java": "package android.util; public class DisplayMetrics { public float density=1; public int heightPixels=800; }",
    "android/util/AttributeSet.java": "package android.util; public interface AttributeSet {}",
    "android/content/DialogInterface.java": """package android.content; public interface DialogInterface {
        interface OnClickListener {void onClick(DialogInterface d,int which);}
        interface OnShowListener {void onShow(DialogInterface d);}}
    """,
    "android/view/View.java": """package android.view; public class View {
        public interface OnClickListener {void onClick(View v);}
        public OnClickListener click;
        public void setOnClickListener(OnClickListener l){click=l;}
        public void performClick(){if(click!=null)click.onClick(this);}
        public void setPadding(int a,int b,int c,int d){} public void setContentDescription(String s){}
        public void requestFocus(){} }
    """,
    "android/view/ViewGroup.java": "package android.view; public class ViewGroup extends View {}",
    "android/view/Gravity.java": "package android.view; public class Gravity {public static final int CENTER_VERTICAL=16;}",
    "android/view/WindowManager.java": "package android.view; public interface WindowManager {class LayoutParams {public static final int SOFT_INPUT_ADJUST_RESIZE=16;}}",
    "android/view/Window.java": "package android.view; public class Window {public void setSoftInputMode(int m){}}",
    "android/text/Editable.java": "package android.text; public class Editable {}",
    "android/text/TextWatcher.java": """package android.text; public interface TextWatcher {
        void beforeTextChanged(CharSequence s,int a,int b,int c);
        void onTextChanged(CharSequence s,int a,int b,int c); void afterTextChanged(Editable e);}
    """,
    "android/text/InputType.java": "package android.text; public class InputType {public static final int TYPE_CLASS_TEXT=1,TYPE_TEXT_FLAG_NO_SUGGESTIONS=2;}",
    "android/widget/TextView.java": """package android.widget; public class TextView extends android.view.View {
        public String text=""; public TextView(android.content.Context c){} public void setText(String s){text=s;}
        public CharSequence getText(){return text;} public void setTextSize(int s){} public void setTextColor(int c){} }
    """,
    "android/widget/EditText.java": """package android.widget; import java.util.*;
        public class EditText extends TextView {
        public String error; public final List<android.text.TextWatcher> watchers=new ArrayList<>();
        public EditText(android.content.Context c){super(c);} public void setSingleLine(boolean b){}
        public void setHint(String s){} public void setInputType(int t){} public void setSelection(int n){}
        public void setError(String s){error=s;} public void addTextChangedListener(android.text.TextWatcher w){watchers.add(w);}
        @Override public void setText(String s){super.setText(s); for(var w:watchers)w.onTextChanged(s,0,0,s.length());}}
    """,
    "android/widget/Button.java": """package android.widget; public class Button extends TextView {
        public Button(android.content.Context c){super(c);} public void setAllCaps(boolean b){}
        public void setMinWidth(int n){} public void setMinimumWidth(int n){} }
    """,
    "android/widget/LinearLayout.java": """package android.widget; import java.util.*;
        public class LinearLayout extends android.view.ViewGroup {
        public static final int VERTICAL=1; public final List<android.view.View> children=new ArrayList<>();
        public LinearLayout(android.content.Context c){} public void setOrientation(int n){} public void setGravity(int g){}
        public void addView(android.view.View v){children.add(v);} public void addView(android.view.View v,LayoutParams p){children.add(v);}
        public static class LayoutParams {public LayoutParams(int a,int b){} public LayoutParams(int a,int b,float c){}} }
    """,
    "android/widget/BaseAdapter.java": """package android.widget; public abstract class BaseAdapter {
        public abstract int getCount(); public abstract Object getItem(int p); public abstract long getItemId(int p);
        public abstract android.view.View getView(int p,android.view.View v,android.view.ViewGroup parent);
        public void notifyDataSetChanged(){} }
    """,
    "android/widget/ListView.java": """package android.widget; public class ListView extends android.view.ViewGroup {
        public BaseAdapter adapter; public ListView(android.content.Context c){} public void setAdapter(BaseAdapter a){adapter=a;} }
    """,
    "android/preference/Preference.java": """package android.preference; public class Preference {
        private android.content.Context context; public String summary;
        public Preference(android.content.Context c){context=c;}
        public Preference(android.content.Context c,android.util.AttributeSet a){this(c);}
        public Preference(android.content.Context c,android.util.AttributeSet a,int s){this(c);}
        public android.content.Context getContext(){return context;} protected void onClick(){}
        public void performClick(){onClick();} public void setSummary(String s){summary=s;} }
    """,
    "android/app/AlertDialog.java": """package android.app; import java.util.*; import android.content.*;
        import android.widget.*; public class AlertDialog implements DialogInterface {
        public static final int BUTTON_POSITIVE=-1,BUTTON_NEGATIVE=-2,BUTTON_NEUTRAL=-3;
        public static final List<AlertDialog> dialogs=new ArrayList<>();
        public final Map<Integer,Button> buttons=new HashMap<>(); public android.view.View view;
        public String title; public boolean showing; private OnShowListener onShow;
        public void setOnShowListener(OnShowListener l){onShow=l;} public Button getButton(int id){return buttons.get(id);}
        public android.view.Window getWindow(){return new android.view.Window();}
        public void show(){dialogs.add(this);showing=true;if(onShow!=null)onShow.onShow(this);}
        public void dismiss(){showing=false;}
        public static class Builder {
        private final AlertDialog d=new AlertDialog(); private final Context c;
        public Builder(Context c){this.c=c;} public Builder setTitle(String s){d.title=s;return this;}
        public Builder setMessage(String s){return this;} public Builder setView(android.view.View v){d.view=v;return this;}
        private Builder button(int id,String s,OnClickListener l){Button b=new Button(c);b.setText(s);
        b.setOnClickListener(v->{if(l!=null)l.onClick(d,id);d.dismiss();});d.buttons.put(id,b);return this;}
        public Builder setPositiveButton(String s,OnClickListener l){return button(-1,s,l);}
        public Builder setNegativeButton(String s,OnClickListener l){return button(-2,s,l);}
        public Builder setNeutralButton(String s,OnClickListener l){return button(-3,s,l);}
        public AlertDialog create(){return d;} public AlertDialog show(){d.show();return d;} } }
    """,
    "app/morphe/extension/shared/theme/ThemeUtils.java": "package app.morphe.extension.shared.theme; public class ThemeUtils {public static int getAppForegroundColor(){return 0;}}",
    "app/morphe/extension/shared/Utils.java": "package app.morphe.extension.shared; public class Utils {public static void showToastShort(String s){}}",
    "app/morphe/extension/youtube/settings/Settings.java": """package app.morphe.extension.youtube.settings;
        public class Settings { public static class Value<T> {private T value; public int saves;
        public Value(T v){value=v;} public T get(){return value;} public void save(T v){value=v;saves++;}}
        public static final Value<Boolean> PROFANITY_DEFAULT_WORDS=new Value<>(true),PROFANITY_REVERENT_PHRASES=new Value<>(true);
        public static final Value<String> PROFANITY_CUSTOM_WORDS=new Value<>(""); }
    """,
}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--java-home", type=Path, default=os.environ.get("JAVA_HOME"))
    args = parser.parse_args()
    def exe(name):
        return str(args.java_home / "bin" / (name + (".exe" if os.name == "nt" else ""))) if args.java_home else name
    with tempfile.TemporaryDirectory(prefix="censor-word-editor-") as temporary:
        root = Path(temporary)
        sources = [ROOT / "src/ProfanityRules.java", ROOT / "src/ProfanityWordsPreference.java", ROOT / "tests/ProfanityWordsPreferenceTest.java"]
        for name, content in STUBS.items():
            target = root / name
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(content, encoding="utf-8")
            sources.append(target)
        classes = root / "classes"
        classes.mkdir()
        subprocess.run([exe("javac"), "-encoding", "UTF-8", "-d", str(classes), *map(str, sources)], check=True)
        subprocess.run([exe("java"), "-cp", str(classes), "app.morphe.extension.youtube.patches.ProfanityWordsPreferenceTest"], check=True)


if __name__ == "__main__":
    main()
