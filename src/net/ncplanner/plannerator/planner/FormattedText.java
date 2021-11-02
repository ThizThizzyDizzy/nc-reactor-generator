package net.ncplanner.plannerator.planner;
import java.util.ArrayList;
import simplelibrary.image.Color;
public class FormattedText{
    public String text;
    public FormattedText next;
    public final boolean italic,underline;
    public Color color;
    public final boolean bold;
    public final boolean strikethrough;
    public FormattedText(String text, Color color, boolean bold, boolean italic, boolean underline, boolean strikethrough){
        this.text = text;
        this.color = color;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.strikethrough = strikethrough;
    }
    public FormattedText(String text, Color color){
        this(text, color, false, false, false, false);
    }
    public FormattedText(String text){
        this(text, null);
    }
    public FormattedText(){
        this("");
    }
    @Override
    public String toString(){
        if(next==null)return text;
        return text+next.toString();
    }
    public FormattedText addText(FormattedText text){
        if(next==null){
            next = text;
        }else{
            next.addText(text);
        }
        return this;
    }
    public FormattedText addText(String text, Color color, boolean bold, boolean italic, boolean underline, boolean strikethrough){
        if(next==null){
            next = new FormattedText(text, color, bold, italic, underline, strikethrough);
        }else{
            next.addText(text, color, bold, italic, underline, strikethrough);
        }
        return this;
    }
    public FormattedText addText(String text, Color color){
        if(next==null){
            next = new FormattedText(text, color);
        }else{
            next.addText(text, color);
        }
        return this;
    }
    public FormattedText addText(String text){
        if(next==null){
            next = new FormattedText(text);
        }else{
            next.addText(text);
        }
        return this;
    }
    public void trimSlightly(){
        if(next!=null){
            next.trimSlightly();
            if(next.text.isEmpty()){
                next = null;
            }
        }else{
            if(text.endsWith("...")){
                text = text.substring(0, text.length()-3).trim();
            }
            if(text.isEmpty())return;
            text = text.substring(0, text.length()-1)+"...";
        }
    }
    public void trimSlightlyWithoutElipses(){
        if(next!=null){
            next.trimSlightly();
            if(next.text.isEmpty()){
                next = null;
            }
        }else{
            if(text.isEmpty())return;
            text = text.substring(0, text.length()-1);
        }
    }
    public boolean isEmpty(){
        return text.isEmpty()&&(next==null||next.isEmpty());
    }
    public ArrayList<FormattedText> split(String regex){
        ArrayList<FormattedText> result = new ArrayList<>();
        if(!text.isEmpty()){
            String[] strs = text.split(regex);
            for(String s : strs){
                result.add(new FormattedText(s, color, bold, italic, underline, strikethrough));
            }
        }
        if(next!=null)result.addAll(next.split(regex));
        return result;
    }
    public ArrayList<FormattedText> splitButKeep(String split){
        ArrayList<FormattedText> result = new ArrayList<>();
        if(!text.isEmpty()){
            String[] strs = text.split("\\Q"+split, -1);
            for(int i = 0; i<strs.length; i++){
                String s = strs[i];
                result.add(new FormattedText(s+(i==strs.length-1?"":split), color, bold, italic, underline, strikethrough));
            }
        }
        if(next!=null)result.addAll(next.splitButKeep(split));
        return result;
    }
    public ArrayList<FormattedText> splitLines(){
        ArrayList<FormattedText> input = splitButKeep("\n");
        ArrayList<FormattedText> result = new ArrayList<>();
        boolean newLine = true;
        for(FormattedText text : input){
            if(newLine)result.add(text);
            else result.get(result.size()-1).addText(text);
            newLine = text.text.endsWith("\n");
        }
        return result;
    }
    public void clear(){
        text = "";
        next = null;
    }
}