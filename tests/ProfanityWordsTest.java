/* GPL-3.0-or-later. Word-manager migration and matching regression checks. */
package app.morphe.extension.youtube.patches;

import java.util.ArrayList;
import java.util.List;

public final class ProfanityWordsTest {
    private static int checks;
    private static void check(boolean condition, String label) {
        checks++;
        if (!condition) throw new AssertionError(label);
    }
    private static void rejected(Runnable task, String label) {
        boolean failed = false;
        try { task.run(); } catch (IllegalArgumentException expected) { failed = true; }
        check(failed, label);
    }
    public static void main(String[] args) {
        List<String> full = ProfanityRules.editableWords(true, true, "");
        check(full.size() == 23, "All default entries are visible");
        check(full.containsAll(List.of("fuck", "jesus christ", "oh my god")), "Default phrases included");
        check(ProfanityRules.saveEditableWords(true, true, "", full).isEmpty(), "Default roundtrip preserves families");
        check(ProfanityRules.includesWordForms(true, true, "", "fuck"), "Family label is accurate");
        check(!ProfanityRules.includesWordForms(true, true, "", "ass"), "Exact words do not claim variants");
        String previous = "-hell\ncustom phrase\nfuck";
        List<String> migrated = ProfanityRules.editableWords(true, false, previous);
        check(!migrated.contains("hell"), "Legacy exclusions stay excluded");
        check(migrated.contains("custom phrase"), "Legacy additions are visible");
        check(!migrated.contains("god"), "Disabled preset groups remain disabled");
        String roundtrip = ProfanityRules.saveEditableWords(true, false, previous, migrated);
        var old = ProfanityRules.compile(true, false, previous, "ble+p", true);
        var saved = ProfanityRules.compile(true, false, roundtrip, "ble+p", true);
        for (String sample : List.of("hell", "fucking", "fuck", "custom phrase", "Jesus Christ", "bleep", "[ __ ]"))
            check(ProfanityRules.censor(sample, old).equals(ProfanityRules.censor(sample, saved)), "Migration preserves " + sample);
        check(!ProfanityRules.includesWordForms(true, false, roundtrip, "fuck"), "Literal default overrides remain literal");
        List<String> edited = new ArrayList<>(full);
        edited.remove("hell"); edited.remove("shit"); edited.add("New phrase"); edited.add("a+b");
        String custom = ProfanityRules.saveEditableWords(true, true, "", edited);
        var pattern = ProfanityRules.compile(true, true, custom, "", true);
        check(!ProfanityRules.contains("hell shit shitting", pattern), "Removing defaults also removes their endings");
        check(ProfanityRules.contains("new   phrase", pattern), "New phrases match flexible spaces");
        check(ProfanityRules.contains("a+b", pattern), "Regex metacharacters are literal");
        check(!ProfanityRules.contains("aaab", pattern), "New entries cannot become regex accidentally");
        check(ProfanityRules.contains("fucking", pattern), "Unedited default inflections remain");
        check(ProfanityRules.contains("[ __ ]", pattern), "Mandatory marker remains independent");
        String empty = ProfanityRules.saveEditableWords(true, true, "", List.of());
        check(ProfanityRules.editableWords(true, true, empty).isEmpty(), "Empty list is supported");
        check(!ProfanityRules.contains("fuck God", ProfanityRules.compile(true, true, empty)), "Empty list disables visible words");
        check(ProfanityRules.contains("[ __ ]", ProfanityRules.compile(true, true, empty)), "Empty list keeps marker behavior");
        String duplicates = ProfanityRules.saveEditableWords(false, false, "", List.of(" Test ", "test", "TEST"));
        check(duplicates.equals("test"), "Case and spacing duplicates collapse");
        check(ProfanityRules.normalizeEditableWord("  GOOD\u00a0 phrase ").equals("good phrase"), "Unicode spacing normalized");
        rejected(() -> ProfanityRules.normalizeEditableWord(" "), "Blank entries rejected");
        rejected(() -> ProfanityRules.normalizeEditableWord("-word"), "Hidden exclusion syntax rejected");
        rejected(() -> ProfanityRules.normalizeEditableWord("one,two"), "Multiple comma entries rejected");
        rejected(() -> ProfanityRules.normalizeEditableWord("one;two"), "Multiple semicolon entries rejected");
        rejected(() -> ProfanityRules.normalizeEditableWord("a".repeat(101)), "Long entries rejected");
        rejected(() -> ProfanityRules.saveEditableWords(false, false, "", java.util.Collections.nCopies(501, "x")), "Oversized list rejected");
        List<String> tooLarge = new ArrayList<>();
        for (int i=0;i<300;i++) tooLarge.add("x".repeat(90)+i);
        rejected(() -> ProfanityRules.saveEditableWords(false, false, "", tooLarge), "Oversized setting rejected");
        check(ProfanityRules.editableWords(true, true, "").equals(full), "Restoring defaults recovers original list");
        System.out.println("Profanity word manager: " + checks + " checks passed");
    }
}
