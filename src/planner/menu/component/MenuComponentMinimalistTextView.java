package planner.menu.component;
import java.util.ArrayList;
import planner.Core;
import planner.FormattedText;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentMinimalistTextView extends MenuComponentMinimalistScrollable{
    private ArrayList<FormattedText> text = new ArrayList<>();
    private final MenuComponent view;
    public int textHeight = 20;
    public int textInset = textHeight/2;
    public boolean wordWrap = true;
    public MenuComponentMinimalistTextView(double x, double y, double width, double height, double horizScrollbarHeight, double vertScrollbarWidth){
        super(x, y, width, height, 0, vertScrollbarWidth);//TODO scrollbar height based on wordWrap
        view = add(new MenuComponent(0, 0, width, height){
            @Override
            public void render(){
                Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
                FontManager.setFont("font");
                double Y = y+textInset;
                for(int i = 0; i<text.size(); i++){
                    FormattedText t = text.get(i);
                    do{
                        if(wordWrap){
                            t = Core.drawFormattedTextWithWordWrap(x+textInset, Y, x+width-textInset, Y+textHeight, t, -1);
                        }else{
                            Core.drawFormattedText(x+textInset, Y, x+width-textInset, Y+textHeight, t, -1);
                            t = null;
                        }
                        Y+=textHeight;
                    }while(t!=null&&!t.isEmpty());
                }
                FontManager.setFont("high resolution");
            }
        });
    }
    public static String drawTextWithWordWrap(double leftEdge, double topEdge, double rightPossibleEdge, double bottomEdge, String text){
        String[] words = text.split(" ");
        String str = words[0];
        double height = bottomEdge-topEdge;
        double length = rightPossibleEdge-leftEdge;
        for(int i = 1; i<words.length; i++){
            String string = str+" "+words[i];
            if(FontManager.getLengthForStringWithHeight(string.trim(), height)>=length){
                drawTextWithWrap(leftEdge, topEdge, rightPossibleEdge, bottomEdge, str.trim());
                return text.replaceFirst("\\Q"+str, "").trim();
            }else{
                str = string;
            }
        }
        return drawTextWithWrap(leftEdge, topEdge, rightPossibleEdge, bottomEdge, text);
    }
    @Override
    public void render(int millisSinceLastTick){
        Core.applyColor(Core.theme.getTextViewBackgroundColor());
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
        view.height = textHeight*(text.size()+extraLines)+textInset*2;
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
}