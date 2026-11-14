/* GPL-3.0-or-later. Compatible channel exceptions and readable whitelist labels. */
package app.morphe.extension.youtube.patches;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class ProfanityChannels {
    record Entry(String key, String label) {}
    private ProfanityChannels() {}

    static boolean isChannelId(String value) {
        return value != null && value.matches("UC[A-Za-z0-9_-]{22}");
    }
    private static String folded(String value) { return value.toLowerCase(Locale.ROOT); }
    static boolean matches(String values, String id, String name) {
        if (values == null || values.isBlank()) return false;
        for (String raw : values.split("[,;\\r\\n]+")) {
            String key = raw.strip();
            if (!key.isEmpty() && (key.equalsIgnoreCase(id) || key.equalsIgnoreCase(name))) return true;
        }
        return false;
    }
    static String normalizeKey(String value) {
        String key = value == null ? "" : value.strip();
        if (key.startsWith("https://") || key.startsWith("http://")) {
            try {
                URI uri = URI.create(key);
                String host = uri.getHost();
                if (host == null || !(host.equalsIgnoreCase("youtube.com")
                        || host.equalsIgnoreCase("www.youtube.com") || host.equalsIgnoreCase("m.youtube.com")))
                    throw new IllegalArgumentException();
                String[] parts = uri.getPath().split("/");
                if (parts.length != 3 || !parts[1].equals("channel") || !isChannelId(parts[2]))
                    throw new IllegalArgumentException();
                key = parts[2];
            } catch (IllegalArgumentException error) {
                throw new IllegalArgumentException("Use a YouTube /channel/ URL, channel ID, or exact display name.");
            }
        }
        if (key.isEmpty() || key.length() > 100 || key.startsWith("@")
                || key.contains(",") || key.contains(";") || key.contains("://")
                || key.chars().anyMatch(Character::isISOControl))
            throw new IllegalArgumentException("Enter a channel ID or exact display name, not an @handle or a list.");
        return key;
    }
    static String normalizeLabel(String value) {
        String label = value == null ? "" : value.strip().replaceAll("[\\s\\p{Z}]+", " ");
        if (label.length() > 100 || label.chars().anyMatch(Character::isISOControl))
            throw new IllegalArgumentException("Use at most 100 characters for the channel label.");
        return label;
    }
    static List<Entry> parse(String values, String labels) {
        Map<String,String> names = new LinkedHashMap<>();
        if (labels != null) for (String line : labels.split("[\\r\\n]+")) {
            String[] parts = line.split("\t", 2);
            if (parts.length == 2) names.put(folded(parts[0]), parts[1]);
        }
        Map<String,Entry> entries = new LinkedHashMap<>();
        if (values != null) for (String raw : values.split("[,;\\r\\n]+")) {
            String key = raw.strip();
            if (!key.isEmpty()) entries.putIfAbsent(folded(key), new Entry(key, names.getOrDefault(folded(key), "")));
        }
        return new ArrayList<>(entries.values());
    }
    static List<Entry> validate(List<Entry> draft) {
        if (draft.size() > 500) throw new IllegalArgumentException("Use at most 500 channel exceptions.");
        Map<String,Entry> entries = new LinkedHashMap<>();
        for (Entry entry : draft) {
            String key = normalizeKey(entry.key());
            if (entries.containsKey(folded(key))) throw new IllegalArgumentException("That channel is already in your whitelist.");
            entries.put(folded(key), new Entry(key, normalizeLabel(entry.label())));
        }
        List<Entry> result = new ArrayList<>(entries.values());
        if (keys(result).length() > 30000 || labels(result).length() > 60000)
            throw new IllegalArgumentException("The channel list is too large.");
        return result;
    }
    static String keys(List<Entry> entries) {
        List<String> values = new ArrayList<>();
        for (Entry entry : entries) values.add(entry.key());
        return String.join("\n",values);
    }
    static String labels(List<Entry> entries) {
        List<String> values = new ArrayList<>();
        for (Entry entry : entries) if (!entry.label().isBlank()) values.add(entry.key()+"\t"+entry.label());
        return String.join("\n",values);
    }
    static String display(Entry entry) { return entry.label().isBlank() ? entry.key() : entry.label(); }
}
