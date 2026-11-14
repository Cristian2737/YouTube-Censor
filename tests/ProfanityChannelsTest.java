/* GPL-3.0-or-later. Stable IDs and legacy whitelist compatibility. */
package app.morphe.extension.youtube.patches;
import java.util.List;
public final class ProfanityChannelsTest {
    static int checks;
    static void check(boolean yes,String label){checks++;if(!yes)throw new AssertionError(label);}
    static void reject(Runnable action,String label){boolean rejected=false;try{action.run();}catch(IllegalArgumentException e){rejected=true;}check(rejected,label);}
    public static void main(String[] args){
        String id="UC1234567890123456789012",other="UCabcdefghijklmnopqrstuv";
        check(ProfanityChannels.isChannelId(id),"Stable channel ID accepted");
        check(!ProfanityChannels.isChannelId("UCshort"),"Short ID is not a channel page");
        check(!ProfanityChannels.isChannelId("FEwhat_to_watch"),"Home is not a channel");
        check(!ProfanityChannels.isChannelId(null),"Null browse page is safe");
        check(ProfanityChannels.normalizeKey("https://www.youtube.com/channel/"+id+"?view=1").equals(id),"Channel URL normalized");
        check(ProfanityChannels.normalizeKey("https://m.youtube.com/channel/"+id+"/").equals(id),"Trailing slash/mobile URL supported");
        check(ProfanityChannels.normalizeKey(" A channel ").equals("A channel"),"Legacy exact name accepted");
        reject(()->ProfanityChannels.normalizeKey("https://evil.example/channel/"+id),"Unrelated URLs rejected");
        reject(()->ProfanityChannels.normalizeKey("https://youtube.com/@example"),"Handle URLs not mistaken for stable IDs");
        reject(()->ProfanityChannels.normalizeKey("@example"),"Unresolvable handles rejected");
        reject(()->ProfanityChannels.normalizeKey("one,two"),"Comma input cannot silently add several channels");
        reject(()->ProfanityChannels.normalizeKey("one;two"),"Semicolon input rejected");
        reject(()->ProfanityChannels.normalizeKey("one\ntwo"),"Control separator rejected");
        reject(()->ProfanityChannels.normalizeKey(" "),"Empty entry rejected");
        reject(()->ProfanityChannels.normalizeLabel("x".repeat(101)),"Oversized label rejected");
        check(ProfanityChannels.normalizeLabel(" A\n B ").equals("A B"),"Label spacing normalized");
        var list=ProfanityChannels.parse(id+",ibz\nIBZ;"+other,id+"\tFirst channel\n"+other+"\tOther");
        check(list.size()==3,"Legacy separators and case duplicates handled");
        check(list.get(0).label().equals("First channel"),"Stored labels restored");
        check(ProfanityChannels.display(list.get(1)).equals("ibz"),"Unlabelled legacy names readable");
        check(ProfanityChannels.matches(ProfanityChannels.keys(list),id,"Renamed channel"),"ID matching survives rename");
        check(ProfanityChannels.matches(ProfanityChannels.keys(list),null,"IBZ"),"Legacy name matching retained");
        check(!ProfanityChannels.matches("ibz",id,"ibz extras"),"Names match exactly");
        check(!ProfanityChannels.matches(id,other,"other"),"Other channel not exempted");
        check(!ProfanityChannels.matches(null,id,"name"),"Null settings safe");
        check(!ProfanityChannels.matches(" ",id,"name"),"Empty whitelist safe");
        check(ProfanityChannels.parse(ProfanityChannels.keys(list),ProfanityChannels.labels(list)).equals(list),"Keys/labels roundtrip");
        reject(()->ProfanityChannels.validate(List.of(new ProfanityChannels.Entry(id,"A"),new ProfanityChannels.Entry(id,"B"))),"Duplicates rejected before writing");
        check(ProfanityChannels.validate(List.of()).isEmpty(),"Empty whitelist valid");
        check(ProfanityChannels.labels(List.of(new ProfanityChannels.Entry(id,""))).isEmpty(),"Empty labels do not add spurious metadata");
        check(!ProfanityChannels.matches(id,null,null),"Null video metadata never matches");
        System.out.println("Channel whitelist rules: "+checks+" checks passed");
    }
}
