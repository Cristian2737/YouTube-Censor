/* GPL-3.0-or-later. Editable native word list for YouTube Censor. */
package app.morphe.extension.youtube.patches;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.Preference;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.morphe.extension.shared.Utils;
import app.morphe.extension.shared.theme.ThemeUtils;
import app.morphe.extension.youtube.settings.Settings;

/** All changes are a draft until Save; Cancel never changes filter settings. */
@SuppressWarnings({"deprecation", "unused"})
public final class ProfanityWordsPreference extends Preference {
    public ProfanityWordsPreference(Context context) { super(context); }
    public ProfanityWordsPreference(Context context, AttributeSet attrs) { super(context, attrs); }
    public ProfanityWordsPreference(Context context, AttributeSet attrs, int style) { super(context, attrs, style); }

    private int dp(int value) { return Math.round(value * getContext().getResources().getDisplayMetrics().density); }
    private TextView text(String value, int size) {
        TextView view = new TextView(getContext());
        view.setText(value); view.setTextSize(size);
        view.setTextColor(ThemeUtils.getAppForegroundColor());
        return view;
    }
    private Button button(String label, Runnable action) {
        Button button = new Button(getContext());
        button.setText(label); button.setAllCaps(false); button.setTextSize(12);
        button.setMinWidth(0); button.setMinimumWidth(0);
        button.setOnClickListener(view -> action.run());
        return button;
    }
    private static TextWatcher changes(Runnable action) {
        return new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { action.run(); }
            public void afterTextChanged(Editable s) {}
        };
    }

    @Override protected void onClick() {
        final boolean defaults = Settings.PROFANITY_DEFAULT_WORDS.get();
        final boolean reverent = Settings.PROFANITY_REVERENT_PHRASES.get();
        final String previous = Settings.PROFANITY_CUSTOM_WORDS.get();
        final List<String> draft = new ArrayList<>(ProfanityRules.editableWords(defaults, reverent, previous));
        // Presets are a draft too: restoring defaults must not write on Cancel.
        final boolean[] presets = {defaults, reverent};
        final String[] source = {previous};
        LinearLayout content = new LinearLayout(getContext());
        content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(20), dp(4), dp(20), 0);
        TextView help = text("All listed words and phrases are censored. Edit or remove an entry, or add your own. No regex needed.", 13);
        content.addView(help);
        TextView count = text("", 12); count.setPadding(0, dp(8), 0, dp(4)); content.addView(count);
        EditText search = new EditText(getContext());
        search.setSingleLine(true); search.setTextSize(16); search.setHint("Search words and phrases");
        search.setContentDescription("Search censored words");
        search.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        content.addView(search, new LinearLayout.LayoutParams(-1, -2));
        ListView list = new ListView(getContext());
        // A bounded, scrollable list leaves room for actions and the keyboard.
        int height = Math.min(dp(350), getContext().getResources().getDisplayMetrics().heightPixels / 3);
        content.addView(list, new LinearLayout.LayoutParams(-1, height));
        TextView notice = text("YouTube's [ __ ] caption markers are always filtered. Advanced regex rules are managed separately.", 12);
        notice.setPadding(0, dp(8), 0, dp(8));

        class WordsAdapter extends BaseAdapter {
            final List<String> visible = new ArrayList<>();
            void refresh() {
                draft.sort(String.CASE_INSENSITIVE_ORDER); visible.clear();
                String query = search.getText().toString().strip().toLowerCase(Locale.ROOT);
                for (String word : draft) if (word.contains(query)) visible.add(word);
                count.setText(draft.size() + " censored entries" + (query.isEmpty() ? "" : " · " + visible.size() + " shown"));
                notifyDataSetChanged();
            }
            public int getCount() { return visible.size(); }
            public Object getItem(int position) { return visible.get(position); }
            public long getItemId(int position) { return position; }
            void edit(String old) {
                EditText input = new EditText(getContext());
                input.setSingleLine(true); input.setTextSize(16); input.setHint("Word or phrase");
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                if (old != null) { input.setText(old); input.setSelection(old.length()); }
                LinearLayout holder = new LinearLayout(getContext());
                holder.setPadding(dp(20), dp(8), dp(20), 0);
                holder.addView(input, new LinearLayout.LayoutParams(-1, -2));
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle(old == null ? "Add censored word" : "Edit censored word")
                        .setView(holder).setPositiveButton("Done", null).setNegativeButton("Cancel", null).create();
                dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                    try {
                        String word = ProfanityRules.normalizeEditableWord(input.getText().toString());
                        if (draft.contains(word) && !word.equals(old)) throw new IllegalArgumentException("That entry is already in your list.");
                        if (old == null && draft.size() >= 500) throw new IllegalArgumentException("Use at most 500 entries.");
                        if (old != null) draft.remove(old);
                        draft.add(word); refresh(); dialog.dismiss();
                    } catch (IllegalArgumentException error) { input.setError(error.getMessage()); }
                }));
                dialog.show();
                if (dialog.getWindow() != null) dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                input.requestFocus();
            }
            public View getView(int position, View recycled, ViewGroup parent) {
                String word = visible.get(position);
                LinearLayout row = new LinearLayout(getContext()); row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(0, dp(6), 0, dp(6));
                LinearLayout labels = new LinearLayout(getContext()); labels.setOrientation(LinearLayout.VERTICAL);
                TextView name = text(word, 16); labels.addView(name);
                if (ProfanityRules.includesWordForms(presets[0], presets[1], source[0], word))
                    labels.addView(text("Includes related word forms", 11));
                row.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));
                Button edit = button("Edit", () -> edit(word)); edit.setContentDescription("Edit " + word); row.addView(edit);
                Button remove = button("Remove", () -> { draft.remove(word); refresh(); });
                remove.setContentDescription("Remove " + word); row.addView(remove);
                return row;
            }
        }
        WordsAdapter adapter = new WordsAdapter(); list.setAdapter(adapter);
        search.addTextChangedListener(changes(adapter::refresh));
        content.addView(button("Add word or phrase", () -> adapter.edit(null)), new LinearLayout.LayoutParams(-1, -2));
        content.addView(notice);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle("Censored words and phrases")
                .setView(content).setPositiveButton("Save", null).setNegativeButton("Cancel", null)
                .setNeutralButton("Restore defaults", null).create();
        dialog.setOnShowListener(ignored -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                try {
                    String saved = ProfanityRules.saveEditableWords(presets[0], presets[1], source[0], draft);
                    Settings.PROFANITY_DEFAULT_WORDS.save(presets[0]);
                    Settings.PROFANITY_REVERENT_PHRASES.save(presets[1]);
                    Settings.PROFANITY_CUSTOM_WORDS.save(saved);
                    setSummary(draft.size() + " words and phrases · tap to view or edit. Restart YouTube after saving.");
                    Utils.showToastShort("Word list saved. Restart YouTube to apply changes to loaded videos.");
                    dialog.dismiss();
                } catch (IllegalArgumentException error) { count.setText(error.getMessage()); }
            });
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(view ->
                new AlertDialog.Builder(getContext()).setTitle("Restore the default word list?")
                    .setMessage("This replaces your word-list edits when you Save. Advanced regex and channel settings stay unchanged.")
                    .setPositiveButton("Restore", (confirm, which) -> {
                        presets[0] = true; presets[1] = true; source[0] = "";
                        draft.clear(); draft.addAll(ProfanityRules.editableWords(true, true, ""));
                        search.setText(""); adapter.refresh();
                    }).setNegativeButton("Cancel", null).show());
        });
        adapter.refresh();
        dialog.show();
        if (dialog.getWindow() != null) dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
}
