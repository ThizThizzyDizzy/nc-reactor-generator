package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.gui.Component;
public class TextView extends Scrollable{
    private ArrayList<FormattedText> text = new ArrayList<>();
    private final Component view;
    public int textHeight = 20;
    public int textInset = textHeight/2;
    public boolean wordWrap = true;
    public int bottomWhitespaceLines = 0;
    public TextView(float x, float y, float width, float height, float horizScrollbarHeight, float vertScrollbarWidth){
        super(x, y, width, height, 0, vertScrollbarWidth);//TODO scrollbar height based on wordWrap
        view = add(new Component(0, 0, width, height){
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
                renderer.setFont(Core.theme.getTextViewFont());
                float Y = y+textInset;
                for(int i = 0; i<text.size(); i++){
                    FormattedText t = text.get(i);
                    do{
                        if(wordWrap){
                            t = renderer.drawFormattedTextWithWordWrap(x+textInset, Y, x+width-textInset, Y+textHeight, t, -1);
                        }else{
                            renderer.drawFormattedText(x+textInset, Y, x+width-textInset, Y+textHeight, t, -1);
                            t = null;
                        }
                        Y+=textHeight;
                    }while(t!=null&&!t.isEmpty());
                }
                renderer.resetFont();
            }
        });
    }
    @Override
    public void render2d(double deltaTime){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getTextViewBackgroundColor());
        renderer.fillRect(x, y, x+width, y+height);
        float width = 0;
        int extraLines = 0;
        for(FormattedText t : text){
            float len = renderer.getStringWidth(t.text, textHeight)+10+textInset*2;
            if(len>this.width&&wordWrap){
                extraLines+=(int)(len/this.width);
            }
            width = Math.max(width, len);
        }
        view.height = textHeight*(text.size()+extraLines+bottomWhitespaceLines)+textInset*2;
        view.width = wordWrap?this.width:width;
        super.render2d(deltaTime);
    }
    public void setText(String s){
        ArrayList<FormattedText> newText = new ArrayList<>();
        String[] strs = s.split("\n", -1);
        for(String str : strs)newText.add(new FormattedText(str));
        text = newText;
    }
    public void setText(FormattedText formattedText){
        ArrayList<FormattedText> newText = new ArrayList<>();
        newText.addAll(formattedText.split("\n"));
        text = newText;
    }
    public void addText(String s){
        ArrayList<FormattedText> newText = new ArrayList<>(text);
        String[] strs = s.split("\n", -1);
        if(newText.isEmpty())newText.add(new FormattedText(strs[0]));
        else newText.get(newText.size()-1).text+=strs[0];
        for(int i = 1; i<strs.length; i++){
            newText.add(new FormattedText(strs[i]));
        }
        text = newText;
    }
    public void addText(FormattedText formattedText){
        ArrayList<FormattedText> newText = new ArrayList<>(text);
        newText.addAll(formattedText.split("\n"));
        text = newText;
    }
}