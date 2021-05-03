package planner.menu.component;
import simplelibrary.image.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import planner.Core;
import simplelibrary.font.FontManager;
import static simplelibrary.opengl.Renderer2D.drawCenteredText;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentLabel extends MenuComponent implements Searchable{
    public String text;
    public final boolean darker;
    public double textInset = 4;
    public Supplier<Color> textColor = () -> {
        return Core.theme.getTextColor();
    };
    private boolean noBackground;
    public MenuComponentLabel(double x, double y, double width, double height, String label){
        this(x, y, width, height, label, false);
    }
    public MenuComponentLabel(double x, double y, double width, double height, String label, boolean darker){
        super(x, y, width, height);
        this.text = label;
        this.darker = darker;
    }
    public MenuComponentLabel setTextColor(Supplier<Color> color){
        textColor = color;
        return this;
    }
    @Override
    public void render(){
        if(!noBackground){
            Core.applyColor(darker?Core.theme.getDarkButtonColor():Core.theme.getButtonColor());
            drawRect(x, y, x+width, y+height, 0);
        }
        Core.applyColor(textColor.get());
        drawText();
    }
    public void drawText(){
        double textLength = FontManager.getLengthForStringWithHeight(text, height);
        double scale = Math.min(1, (width-textInset*2)/textLength);
        double textHeight = (int)((height-textInset*2)*scale)-4;
        drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public MenuComponentLabel setTooltip(String tooltip){
        this.tooltip = tooltip;
        return this;
    }
    public MenuComponentLabel noBackground(){
        noBackground = true;
        return this;
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> lst = new ArrayList<>(Arrays.asList(text));
        String tooltip = getTooltip();
        if(tooltip!=null)for(String s : tooltip.split("\n"))lst.add(s.trim());
        return lst;
    }
}