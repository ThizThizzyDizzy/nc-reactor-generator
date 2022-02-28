package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Searchable;
import net.ncplanner.plannerator.planner.gui.Component;
public class Label extends Component implements Searchable{
    public String text;
    public final boolean darker;
    public float textInset = 4;
    public Supplier<Color> textColor = () -> {
        return Core.theme.getComponentTextColor(Core.getThemeIndex(this));
    };
    private boolean noBackground;
    private boolean alignLeft;
    public Label(String label){
        this(0, 0, 0, 0, label);
    }
    public Label(float x, float y, float width, float height, String label){
        this(x, y, width, height, label, false);
    }
    public Label(float x, float y, float width, float height, String label, boolean darker){
        super(x, y, width, height);
        this.text = label;
        this.darker = darker;
    }
    public Label setTextColor(Supplier<Color> color){
        textColor = color;
        return this;
    }
    public Label setInset(float inset){
        textInset = inset;
        return this;
    }
    public Label alignLeft(){
        alignLeft = true;
        return this;
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        if(!noBackground){
            renderer.setColor(darker?Core.theme.getSecondaryComponentColor(Core.getThemeIndex(this)):Core.theme.getComponentColor(Core.getThemeIndex(this)));
            renderer.fillRect(x, y, x+width, y+height);
        }
        renderer.setColor(textColor.get());
        drawText(renderer);
    }
    public void drawText(Renderer renderer){
        float textLength = renderer.getStringWidth(text, height);
        float scale = Math.min(1, (width-textInset*2)/textLength);
        float textHeight = (int)((height-textInset*2)*scale);
        if(alignLeft)renderer.drawText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
        else renderer.drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public Label setTooltip(String tooltip){
        this.tooltip = tooltip;
        return this;
    }
    public Label noBackground(){
        noBackground = true;
        return this;
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> lst = getSimpleSearchableNames();
        String tooltip = getTooltip();
        if(tooltip!=null)for(String s : tooltip.split("\n"))lst.add(s.trim());
        return lst;
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        ArrayList<String> lst = new ArrayList<>(Arrays.asList(text));
        return lst;
    }
}