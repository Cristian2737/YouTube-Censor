/* GPL-3.0-or-later. Channel-page action using Morphe's shared toolbar/browse hooks.
 * Toolbar placement follows the Morphe ChannelSearchPatch approach (GPLv3;
 * upstream NOTICE preserved). Search's own listener and button are untouched.
 */
package app.morphe.extension.youtube.patches;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.List;

import app.morphe.extension.shared.Logger;
import app.morphe.extension.shared.ResourceUtils;
import app.morphe.extension.shared.Utils;
import app.morphe.extension.shared.theme.ThemeUtils;
import app.morphe.extension.youtube.settings.Settings;

public final class ProfanityChannelWhitelist {
    private static String browseId = "";
    private static WeakReference<View> searchParent = new WeakReference<>(null);
    private static WeakReference<View> actionRef = new WeakReference<>(null);
    private static final int TOOLBAR_ID = ResourceUtils.getIdIdentifier("toolbar");
    private ProfanityChannelWhitelist() {}

    public static void setBrowseId(String value) {
        browseId = ProfanityChannels.isChannelId(value) ? value : "";
        refresh();
    }
    public static void clearBrowseId() { setBrowseId(""); }
    public static void setSearchButtonView(String name, View parent, ImageView original) {
        if (!"SEARCH".equals(name) && !"SEARCH_BOLD".equals(name) && !"SEARCH_CAIRO".equals(name)) return;
        searchParent = new WeakReference<>(parent);
        refresh(); parent.post(ProfanityChannelWhitelist::refresh);
    }
    static List<ProfanityChannels.Entry> entries() {
        return ProfanityChannels.parse(Settings.PROFANITY_NEVER_CHANNELS.get(), Settings.PROFANITY_CHANNEL_LABELS.get());
    }
    static void saveEntries(List<ProfanityChannels.Entry> draft) {
        List<ProfanityChannels.Entry> checked = ProfanityChannels.validate(draft);
        // Validate everything before writing. Preserve the old exclusion key for compatibility.
        Settings.PROFANITY_CHANNEL_LABELS.save(ProfanityChannels.labels(checked));
        Settings.PROFANITY_NEVER_CHANNELS.save(ProfanityChannels.keys(checked));
        ProfanityFilterPatch.channelRulesChanged();
        refresh();
    }
    /** Never borrow a playing video's name for a different viewed channel. */
    private static String knownName(String id) {
        String name = id.equals(VideoInformation.getChannelId()) ? VideoInformation.getChannelName() : "";
        return name == null ? "" : name;
    }
    private static boolean whitelisted(String id) {
        return ProfanityChannels.matches(Settings.PROFANITY_NEVER_CHANNELS.get(), id, knownName(id));
    }
    private static int dp(View view, int value) {
        return Math.round(value * view.getResources().getDisplayMetrics().density);
    }
    private static void style(View action) {
        boolean allowed = whitelisted(browseId);
        action.setContentDescription(allowed ? "Whitelisted channel. Tap to enable censoring" : "Whitelist this channel from YouTube Censor");
        action.setSelected(allowed);
        ImageView icon = (ImageView)((FrameLayout)action).getChildAt(0);
        Drawable drawable = ResourceUtils.getDrawable(allowed ? "morphe_censor_channel_allowed" : "morphe_censor_channel_add");
        if (drawable != null) { drawable.setTint(ThemeUtils.getAppForegroundColor()); icon.setImageDrawable(drawable); }
    }
    private static void refresh() {
        try {
            View original = searchParent.get(), item = original;
            ViewGroup toolbar = null;
            if (original != null) {
                View current = original;
                while (current.getParent() instanceof ViewGroup parent) {
                    if (current.getId() != View.NO_ID && current.getResources().getResourceEntryName(current.getId()).startsWith("menu_item_")) {
                        toolbar = parent; item = current; break;
                    }
                    if (TOOLBAR_ID != 0 && parent.getId() == TOOLBAR_ID) { toolbar = parent; item = current; break; }
                    current = parent;
                }
            }
            View action = actionRef.get();
            if (action != null && action.getParent() != toolbar) {
                if (action.getParent() instanceof ViewGroup old) old.removeView(action);
                actionRef = new WeakReference<>(null); action = null;
            }
            if (action != null) {
                action.setVisibility(browseId.isEmpty() ? View.GONE : View.VISIBLE);
                if (!browseId.isEmpty()) style(action);
                return;
            }
            if (browseId.isEmpty() || toolbar == null || item == null) return;
            FrameLayout button = new FrameLayout(item.getContext());
            ViewGroup.LayoutParams params = item.getLayoutParams();
            if (params instanceof android.widget.LinearLayout.LayoutParams linear) {
                var copy = new android.widget.LinearLayout.LayoutParams(linear); copy.width=dp(item,48); copy.weight=0; params=copy;
            } else if (params instanceof FrameLayout.LayoutParams frame) params = new FrameLayout.LayoutParams(frame);
            else if (params instanceof ViewGroup.MarginLayoutParams margins) params = new ViewGroup.MarginLayoutParams(margins);
            else params = params == null ? new ViewGroup.LayoutParams(dp(item,48),dp(item,48)) : new ViewGroup.LayoutParams(params);
            button.setLayoutParams(params); button.setFocusable(true); button.setClickable(true);
            button.setMinimumWidth(dp(item,48)); button.setMinimumHeight(dp(item,48));
            ImageView icon = new ImageView(item.getContext()); icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            button.addView(icon,new FrameLayout.LayoutParams(dp(item,24),dp(item,24),Gravity.CENTER));
            button.setOnClickListener(view -> confirm(button));
            style(button); toolbar.addView(button,Math.max(toolbar.indexOfChild(item),0));
            actionRef = new WeakReference<>(button);
        } catch (Exception error) { Logger.printException(() -> "YouTube Censor channel button unavailable",error); }
    }
    private static void confirm(View view) {
        final String id = browseId;
        if (!ProfanityChannels.isChannelId(id)) return;
        String name = knownName(id);
        if (whitelisted(id)) {
            new AlertDialog.Builder(view.getContext()).setTitle("Enable censoring for this channel?")
                    .setMessage(id + "\nThis removes matching channel exceptions from your whitelist.")
                    .setPositiveButton("Remove from whitelist",(dialog,which) -> {
                        // Re-read so a stale dialog cannot overwrite another settings change.
                        List<ProfanityChannels.Entry> current = entries();
                        current.removeIf(e -> e.key().equalsIgnoreCase(id) || (!name.isBlank() && e.key().equalsIgnoreCase(name)));
                        saveEntries(current); Utils.showToastShort("Channel removed from whitelist");
                    }).setNegativeButton("Cancel",null).show();
        } else {
            EditText label = new EditText(view.getContext()); label.setSingleLine(true);
            label.setHint("Channel label (optional)"); label.setText(name == null ? "" : name);
            android.widget.LinearLayout holder = new android.widget.LinearLayout(view.getContext());
            holder.setPadding(dp(view,20),dp(view,8),dp(view,20),0);
            holder.addView(label,new android.widget.LinearLayout.LayoutParams(-1,-2));
            AlertDialog dialog = new AlertDialog.Builder(view.getContext()).setTitle("Whitelist this channel?")
                    .setMessage(id+"\nYouTube Censor will skip videos from this channel. You can edit the label later in settings.")
                    .setView(holder).setPositiveButton("Whitelist",null).setNegativeButton("Cancel",null).create();
            dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(button -> {
                try {
                    List<ProfanityChannels.Entry> current = entries();
                    if (!ProfanityChannels.matches(ProfanityChannels.keys(current),id,null))
                        current.add(new ProfanityChannels.Entry(id,ProfanityChannels.normalizeLabel(label.getText().toString())));
                    saveEntries(current); Utils.showToastShort("Channel whitelisted"); dialog.dismiss();
                } catch (IllegalArgumentException error) { label.setError(error.getMessage()); }
            }));
            dialog.show();
        }
    }
}
