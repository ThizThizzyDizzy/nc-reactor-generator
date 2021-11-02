package net.ncplanner.plannerator.planner.menu.component;
import java.util.ArrayList;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentMinimalistTextView extends MenuComponentMinimalistScrollable{
    private ArrayList<FormattedText> text = new ArrayList<>();
    private final MenuComponent view;
    public int textHeight = 20;
    public int textInset = textHeight/2;
    public boolean wordWrap = true;
    public int bottomWhitespaceLines = 0;
    public MenuComponentMinimalistTextView(double x, double y, double width, double height, double horizScrollbarHeight, double vertScrollbarWidth){
        super(x, y, width, height, 0, vertScrollbarWidth);//TODO scrollbar height based on wordWrap
        view = add(new MenuComponent(0, 0, width, height){
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
                FontManager.setFont("font");
                double Y = y+textInset;
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
                FontManager.setFont("high resolution");
            }
        });
    }
    @Override
    public void render(int millisSinceLastTick){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getTextViewBackgroundColor());
        drawRect(x, y, x+width, y+height, 0);
        double width = 0;
        int extraLines = 0;
        for(FormattedText t : text){
            double len = FontManager.getLengthForStringWithHeight(t.text, textHeight)+10+textInset*2;
            if(len>this.width&&wordWrap){
                extraLines+=(int)(len/this.width);
            }
            width = Math.max(width, len);
        }
        view.height = textHeight*(text.size()+extraLines+bottomWhitespaceLines)+textInset*2;
        view.width = wordWrap?this.width:width;
        super.render(millisSinceLastTick);
    }
    public void setText(String s){
        text.clear();
        String[] strs = s.split("\n");
        for(String str : strs)text.add(new FormattedText(str));
    }
    public void setText(FormattedText formattedText){
        text.clear();
        text.addAll(formattedText.split("\n"));
    }
    public void addText(String s){
        text.addAll(new FormattedText(s).split("\n"));
    }
    public void addText(FormattedText formattedText){
        text.addAll(formattedText.split("\n"));
    }
}