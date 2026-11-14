/* GPL-3.0-or-later. Native editable channel whitelist, no comma syntax required. */
package app.morphe.extension.youtube.patches;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.Preference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import app.morphe.extension.shared.theme.ThemeUtils;

@SuppressWarnings({"unused","deprecation"})
public final class ProfanityChannelsPreference extends Preference {
    public ProfanityChannelsPreference(Context context) { super(context); }
    public ProfanityChannelsPreference(Context context,AttributeSet attrs) { super(context,attrs); }
    public ProfanityChannelsPreference(Context context,AttributeSet attrs,int style) { super(context,attrs,style); }
    private int dp(int n) { return Math.round(n*getContext().getResources().getDisplayMetrics().density); }
    private TextView text(String value,int size) {
        TextView view=new TextView(getContext()); view.setText(value); view.setTextSize(size);
        view.setTextColor(ThemeUtils.getAppForegroundColor()); return view;
    }
    private Button button(String label,Runnable action) {
        Button b=new Button(getContext()); b.setText(label); b.setAllCaps(false); b.setTextSize(12);
        b.setMinWidth(0); b.setMinimumWidth(0); b.setOnClickListener(v->action.run()); return b;
    }
    @Override protected void onClick() {
        List<ProfanityChannels.Entry> draft=new ArrayList<>(ProfanityChannelWhitelist.entries());
        LinearLayout content=new LinearLayout(getContext()); content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20),dp(4),dp(20),0);
        content.addView(text("YouTube Censor skips these channels. Add channels using the button on their channel page, or edit the list here.",13));
        TextView count=text("",12); content.addView(count);
        EditText search=new EditText(getContext()); search.setSingleLine(true); search.setTextSize(16);
        search.setHint("Search channel labels or IDs"); content.addView(search,new LinearLayout.LayoutParams(-1,-2));
        ListView list=new ListView(getContext());
        content.addView(list,new LinearLayout.LayoutParams(-1,Math.min(dp(330),getContext().getResources().getDisplayMetrics().heightPixels/3)));
        class Adapter extends BaseAdapter {
            final List<ProfanityChannels.Entry> visible=new ArrayList<>();
            void refresh() {
                visible.clear(); String query=search.getText().toString().strip().toLowerCase(Locale.ROOT);
                for(var e:draft) if((e.key()+" "+e.label()).toLowerCase(Locale.ROOT).contains(query)) visible.add(e);
                count.setText(draft.isEmpty()?"No channels whitelisted":draft.size()+" whitelisted channels · "+visible.size()+" shown");
                notifyDataSetChanged();
            }
            public int getCount(){return visible.size();}
            public Object getItem(int position){return visible.get(position);}
            public long getItemId(int position){return position;}
            void edit(ProfanityChannels.Entry old) {
                LinearLayout form=new LinearLayout(getContext()); form.setOrientation(LinearLayout.VERTICAL);
                form.setPadding(dp(20),dp(8),dp(20),0);
                form.addView(text("Channel ID, /channel/ URL, or exact display name",12));
                EditText key=new EditText(getContext()); key.setTextSize(16); key.setSingleLine(true);
                key.setHint("UC… or exact channel name"); if(old!=null)key.setText(old.key());
                form.addView(key,new LinearLayout.LayoutParams(-1,-2));
                form.addView(text("Label (optional; does not change which channel matches)",12));
                EditText label=new EditText(getContext()); label.setTextSize(16); label.setSingleLine(true);
                if(old!=null)label.setText(old.label()); form.addView(label,new LinearLayout.LayoutParams(-1,-2));
                AlertDialog dialog=new AlertDialog.Builder(getContext()).setTitle(old==null?"Add whitelisted channel":"Edit whitelisted channel")
                    .setView(form).setPositiveButton("Done",null).setNegativeButton("Cancel",null).create();
                dialog.setOnShowListener(ignored->dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view->{
                    try {
                        var entry=new ProfanityChannels.Entry(ProfanityChannels.normalizeKey(key.getText().toString()),ProfanityChannels.normalizeLabel(label.getText().toString()));
                        List<ProfanityChannels.Entry> changed=new ArrayList<>(draft); if(old!=null)changed.remove(old); changed.add(entry);
                        ProfanityChannels.validate(changed); draft.clear(); draft.addAll(changed); refresh(); dialog.dismiss();
                    } catch(IllegalArgumentException error){key.setError(error.getMessage());}
                }));
                dialog.show();
            }
            public View getView(int position,View recycled,ViewGroup parent) {
                var entry=visible.get(position); LinearLayout row=new LinearLayout(getContext()); row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(0,dp(5),0,dp(5)); LinearLayout labels=new LinearLayout(getContext()); labels.setOrientation(LinearLayout.VERTICAL);
                labels.addView(text(ProfanityChannels.display(entry),16));
                if(!entry.label().isBlank())labels.addView(text(entry.key(),11));
                row.addView(labels,new LinearLayout.LayoutParams(0,-2,1));
                Button edit=button("Edit",()->edit(entry)); edit.setContentDescription("Edit "+ProfanityChannels.display(entry)); row.addView(edit);
                Button remove=button("Remove",()->{draft.remove(entry);refresh();}); remove.setContentDescription("Remove "+ProfanityChannels.display(entry)); row.addView(remove);
                return row;
            }
        }
        Adapter adapter=new Adapter(); list.setAdapter(adapter);
        search.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            public void onTextChanged(CharSequence s,int a,int b,int c){adapter.refresh();}
            public void afterTextChanged(Editable e){}
        });
        content.addView(button("Add channel",()->adapter.edit(null)),new LinearLayout.LayoutParams(-1,-2));
        content.addView(text("Channel IDs stay valid if a channel changes its name. Legacy name entries still match exact display names.",12));
        AlertDialog dialog=new AlertDialog.Builder(getContext()).setTitle("Whitelisted channels")
            .setView(content).setPositiveButton("Save",null).setNegativeButton("Cancel",null).create();
        dialog.setOnShowListener(ignored->dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view->{
            try{ProfanityChannelWhitelist.saveEntries(draft);setSummary(draft.size()+" whitelisted channels · tap to view or edit");dialog.dismiss();}
            catch(IllegalArgumentException error){count.setText(error.getMessage());}
        }));
        adapter.refresh(); dialog.show();
    }
}
