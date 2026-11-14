/* GPL-3.0-or-later. Actual preference callbacks with Android host-test doubles. */
package app.morphe.extension.youtube.patches;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.*;
import app.morphe.extension.youtube.settings.Settings;

public final class ProfanityWordsPreferenceTest {
    static int checks;
    static void check(boolean yes, String name) { checks++; if (!yes) throw new AssertionError(name); }
    static AlertDialog latest() { return AlertDialog.dialogs.get(AlertDialog.dialogs.size()-1); }
    static <T> T child(View view, Class<T> type) {
        if (type.isInstance(view)) return type.cast(view);
        if (view instanceof LinearLayout layout) for (View item : layout.children) {
            T found = child(item, type); if (found != null) return found;
        }
        return null;
    }
    static Button button(View view, String label) {
        if (view instanceof Button b && b.text.equals(label)) return b;
        if (view instanceof LinearLayout layout) for (View item : layout.children) {
            Button found = button(item,label); if (found != null) return found;
        }
        return null;
    }
    static void add(AlertDialog manager, String value) {
        button(manager.view,"Add word or phrase").performClick();
        child(latest().view,EditText.class).setText(value);
        latest().getButton(-1).performClick();
    }
    public static void main(String[] args) {
        Settings.PROFANITY_CUSTOM_WORDS.save("-hell\nlegacy phrase");
        ProfanityWordsPreference pref = new ProfanityWordsPreference(new Context()); pref.performClick();
        AlertDialog manager = latest(); ListView list = child(manager.view,ListView.class);
        check(list.adapter.getCount()==23,"Defaults and previous custom words visible immediately");
        check(Settings.PROFANITY_CUSTOM_WORDS.get().equals("-hell\nlegacy phrase"),"Opening never rewrites settings");
        EditText search = child(manager.view,EditText.class); search.setText("legacy");
        check(list.adapter.getCount()==1 && list.adapter.getItem(0).equals("legacy phrase"),"Search filters actual adapter");
        button(list.adapter.getView(0,null,list),"Remove").performClick();
        check(list.adapter.getCount()==0,"Removing updates visible results");
        manager.getButton(-2).performClick();
        check(Settings.PROFANITY_CUSTOM_WORDS.get().equals("-hell\nlegacy phrase"),"Cancel discards removals");
        pref.performClick(); manager=latest(); list=child(manager.view,ListView.class);
        add(manager,"Fresh phrase");
        check(list.adapter.getCount()==24,"Add updates draft");
        add(manager,"fresh PHRASE");
        check(latest().showing && child(latest().view,EditText.class).error!=null,"Duplicate stays open with inline error");
        latest().getButton(-2).performClick();
        add(manager," ");
        check(latest().showing && child(latest().view,EditText.class).error!=null,"Blank entry is rejected without closing");
        latest().getButton(-2).performClick();
        search=child(manager.view,EditText.class); search.setText("fresh");
        button(list.adapter.getView(0,null,list),"Edit").performClick();
        child(latest().view,EditText.class).setText("edited phrase"); latest().getButton(-1).performClick();
        check(list.adapter.getCount()==0,"Editing updates filtered results");
        search.setText("edited");
        check(list.adapter.getCount()==1,"Edited phrase is searchable");
        manager.getButton(-1).performClick();
        var words=ProfanityRules.editableWords(true,true,Settings.PROFANITY_CUSTOM_WORDS.get());
        check(words.contains("edited phrase") && !words.contains("fresh phrase"),"Save commits edited list");
        check(!words.contains("hell") && words.contains("legacy phrase"),"Save retains previous choices");
        check(!manager.showing && pref.summary.contains("Restart"),"Save closes manager and explains restart");
        String before=Settings.PROFANITY_CUSTOM_WORDS.get();
        pref.performClick(); manager=latest(); manager.getButton(-3).performClick(); latest().getButton(-1).performClick();
        check(Settings.PROFANITY_CUSTOM_WORDS.get().equals(before),"Restore is a draft until Save");
        manager.getButton(-2).performClick();
        check(Settings.PROFANITY_CUSTOM_WORDS.get().equals(before),"Cancel also discards Restore defaults");
        pref.performClick(); manager=latest(); manager.getButton(-3).performClick(); latest().getButton(-1).performClick(); manager.getButton(-1).performClick();
        check(Settings.PROFANITY_CUSTOM_WORDS.get().isEmpty(),"Restore and Save clear overrides");
        check(ProfanityRules.editableWords(true,true,Settings.PROFANITY_CUSTOM_WORDS.get()).size()==23,"Restore recovers full default list");
        pref.performClick(); manager=latest(); list=child(manager.view,ListView.class);
        while(list.adapter.getCount()>0) button(list.adapter.getView(0,null,list),"Remove").performClick();
        manager.getButton(-1).performClick();
        check(ProfanityRules.editableWords(true,true,Settings.PROFANITY_CUSTOM_WORDS.get()).isEmpty(),"Removing all entries saves an empty list");
        pref.performClick(); manager=latest();
        check(child(manager.view,ListView.class).adapter.getCount()==0,"Empty list reopens without silently restoring defaults");
        manager.getButton(-2).performClick();
        System.out.println("Word editor callbacks: "+checks+" checks passed (host view doubles, not device rendering)");
    }
}
