package discord.keyword;
import discord.Keyword;
import java.awt.Color;
public class KeywordBlockRange extends Keyword{
    public String block;
    public int min = 0;
    public int max = Integer.MAX_VALUE;
    public KeywordBlockRange(){
        super("Block Range");
    }
    @Override
    public boolean doRead(String input){
        String truncated = input.replace(" ", "");
        boolean lessThan = false;
        boolean greaterThan = false;
        boolean exactly = false;
        boolean no = false;
        if(truncated.startsWith("fewerthan")||truncated.startsWith("lessthan"))lessThan = true;
        if(truncated.startsWith("morethan")||truncated.startsWith("greaterthan"))greaterThan = true;
        if(truncated.startsWith("exactly"))exactly = true;
        if(truncated.startsWith("no"))no = true;
        int matches = 0;
        if(lessThan)matches++;
        if(greaterThan)matches++;
        if(exactly)matches++;
        if(no)matches++;
        if(matches>1)return false;
        truncated = truncated.replaceFirst("((fewer|more|less|greater) ?th[ea]n|exactly) ?|(no ?)", "");
        String num1 = "";
        while(!truncated.isEmpty()&&Character.isDigit(truncated.charAt(0))){
            num1+=truncated.charAt(0);
            truncated = truncated.substring(1);
        }
        int i = num1.isEmpty()?-1:Integer.parseInt(num1);
        truncated = truncated.replaceFirst("(to|-)(\\d+)", "$2");
        String num2 = "";
        while(!truncated.isEmpty()&&Character.isDigit(truncated.charAt(0))){
            num2+=truncated.charAt(0);
            truncated = truncated.substring(1);
        }
        block = input.replaceAll(getRegex(), "$7");
        int j = num2.isEmpty()?-1:Integer.parseInt(num2);
        if(lessThan){
            max = i;
        }else if(greaterThan){
            min = i;
        }else if(exactly){
            min = max = i;
        }else if(no){
            min = max = 0;
        }else{
            min = i;
            max = j==-1?i:j;
        }
        return true;
    }
    @Override
    public Color getColor(){
        return new Color(255, 0, 127);
    }
    @Override
    public String getRegex(){
        return "(((fewer|more|less|greater) ?th[ea]n|exactly) ?)?(no |[<>]?\\d+( ?(to|-) ?\\d+)?) ?(((liquid|fuel|heavy) ?)?[a-z-]{4,}( ?(coolers?|(heat)?(sink|er)s?|moderators?|reflectors?|shields?))?)";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordBlockRange();
    }
    @Override
    public boolean caseSensitive(){
        return false;
    }
}