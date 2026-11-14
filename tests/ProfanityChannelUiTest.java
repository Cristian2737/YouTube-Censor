/* GPL-3.0-or-later. Real channel toolbar/settings callbacks, host view doubles. */
package app.morphe.extension.youtube.patches;
import android.app.AlertDialog;
import android.content.Context;
import android.view.*;
import android.widget.*;
import app.morphe.extension.youtube.settings.Settings;
public final class ProfanityChannelUiTest {
    static int checks;
    static void check(boolean yes,String text){checks++;if(!yes)throw new AssertionError(text);}
    static AlertDialog latest(){return AlertDialog.dialogs.get(AlertDialog.dialogs.size()-1);}
    static <T>T child(View view,Class<T> type){if(type.isInstance(view))return type.cast(view);if(view instanceof ViewGroup g)for(View v:g.children){T result=child(v,type);if(result!=null)return result;}return null;}
    static Button button(View view,String label){if(view instanceof Button b&&b.text.equals(label))return b;if(view instanceof ViewGroup g)for(View v:g.children){Button result=button(v,label);if(result!=null)return result;}return null;}
    public static void main(String[] args){
        String id="UC1234567890123456789012",other="UCabcdefghijklmnopqrstuv";
        Context context=new Context();LinearLayout toolbar=new LinearLayout(context);toolbar.id=1;
        FrameLayout search=new FrameLayout(context);search.id=2;search.setLayoutParams(new LinearLayout.LayoutParams(48,48));toolbar.addView(search);
        ImageView original=new ImageView(context);search.addView(original);int[] searches={0};original.setOnClickListener(v->searches[0]++);
        ProfanityChannelWhitelist.setSearchButtonView("SEARCH",search,original);
        check(toolbar.getChildCount()==1,"No button on a non-channel page");
        ProfanityChannelWhitelist.setBrowseId(id);
        check(toolbar.getChildCount()==2,"Channel page gets a separate action");
        View action=toolbar.getChildAt(0);
        check(action.description.contains("Whitelist this channel"),"Accessible action label");
        ProfanityChannelWhitelist.setSearchButtonView("SEARCH",search,original);
        check(toolbar.getChildCount()==2,"Rebinding does not duplicate the action");
        original.performClick();check(searches[0]==1,"Original search action preserved");
        action.performClick();AlertDialog add=latest();
        check(child(add.view,EditText.class).getText().toString().isEmpty(),"Do not borrow another playing channel's name");
        ProfanityChannelWhitelist.setBrowseId(other);
        child(add.view,EditText.class).setText("Viewed channel");add.getButton(-1).performClick();
        check(Settings.PROFANITY_NEVER_CHANNELS.get().equals(id),"Dialog keeps the channel captured when clicked");
        check(ProfanityChannelWhitelist.entries().get(0).label().equals("Viewed channel"),"Readable label saved separately");
        check(ProfanityFilterPatch.refreshes==1,"Whitelist action requests immediate filter refresh");
        ProfanityChannelWhitelist.setBrowseId(id);check(action.selected,"Whitelisted channel gets checked icon state");
        action.performClick();latest().getButton(-2).performClick();
        check(Settings.PROFANITY_NEVER_CHANNELS.get().equals(id),"Cancel removal leaves whitelist intact");
        action.performClick();latest().getButton(-1).performClick();
        check(Settings.PROFANITY_NEVER_CHANNELS.get().isEmpty()&&!action.selected,"Removal updates persistence and icon");
        ProfanityChannelWhitelist.clearBrowseId();check(action.visibility==View.GONE,"Search feed hides the button");
        ProfanityChannelWhitelist.setBrowseId("FEwhat_to_watch");check(action.visibility==View.GONE,"Home cannot inherit a channel action");
        ProfanityChannelWhitelist.setBrowseId(other);action.performClick();
        check(child(latest().view,EditText.class).getText().toString().equals(VideoInformation.name),"Matching playing channel may provide label");
        latest().getButton(-2).performClick();
        LinearLayout replacement=new LinearLayout(context);replacement.id=1;
        FrameLayout nextSearch=new FrameLayout(context);nextSearch.id=2;replacement.addView(nextSearch);
        ProfanityChannelWhitelist.setSearchButtonView("SEARCH_BOLD",nextSearch,new ImageView(context));
        check(toolbar.getChildCount()==1&&replacement.getChildCount()==2,"Toolbar replacement detaches obsolete action");
        Settings.PROFANITY_NEVER_CHANNELS.save("legacy name,"+id);Settings.PROFANITY_CHANNEL_LABELS.save(id+"\tA label");
        ProfanityChannelsPreference pref=new ProfanityChannelsPreference(context);pref.performClick();
        AlertDialog manager=latest();ListView list=child(manager.view,ListView.class);EditText query=child(manager.view,EditText.class);
        check(list.adapter.getCount()==2,"Manager shows legacy and ID entries");
        query.setText("A label");check(list.adapter.getCount()==1,"Manager searches readable labels");
        button(list.adapter.getView(0,null,list),"Remove").performClick();manager.getButton(-2).performClick();
        check(Settings.PROFANITY_NEVER_CHANNELS.get().contains(id),"Manager Cancel discards removals");
        pref.performClick();manager=latest();list=child(manager.view,ListView.class);
        query=child(manager.view,EditText.class);query.setText("A label");button(list.adapter.getView(0,null,list),"Edit").performClick();
        LinearLayout form=(LinearLayout)latest().view;((EditText)form.children.get(3)).setText("Renamed label");latest().getButton(-1).performClick();
        manager.getButton(-1).performClick();
        check(Settings.PROFANITY_NEVER_CHANNELS.get().contains(id),"Editing a label keeps stable matching key");
        check(Settings.PROFANITY_CHANNEL_LABELS.get().contains("Renamed label"),"Edited label persists");
        pref.performClick();manager=latest();button(manager.view,"Add channel").performClick();
        child(latest().view,EditText.class).setText(id);latest().getButton(-1).performClick();
        check(latest().showing&&child(latest().view,EditText.class).error!=null,"Duplicate channel gets inline validation");latest().getButton(-2).performClick();
        button(manager.view,"Add channel").performClick();child(latest().view,EditText.class).setText("https://youtube.com/channel/"+other);latest().getButton(-1).performClick();manager.getButton(-1).performClick();
        check(Settings.PROFANITY_NEVER_CHANNELS.get().contains(other),"Manager accepts normalized channel URLs");
        pref.performClick();manager=latest();list=child(manager.view,ListView.class);
        while(list.adapter.getCount()>0)button(list.adapter.getView(0,null,list),"Remove").performClick();manager.getButton(-1).performClick();
        check(Settings.PROFANITY_NEVER_CHANNELS.get().isEmpty()&&Settings.PROFANITY_CHANNEL_LABELS.get().isEmpty(),"Saving an empty whitelist removes stale metadata");
        System.out.println("Channel toolbar/editor callbacks: "+checks+" checks passed (host doubles, not device rendering)");
    }
}
