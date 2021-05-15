package planner.menu.component;
import planner.Core;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.components.MenuComponentTextBox;
public class MenuComponentMinimalistTextBox extends MenuComponentTextBox{
    private static final int NONE = 0;
    private static final int INT = 1;
    private static final int FLOAT = 2;
    private int filter = NONE;
    private String suffix = "";
    private Number min;
    private Number max;
    public String title;
    public double titleInset = 0;
    public double titleness = 0;
    public double titlenessSpeed = 0.5;
    public double actualTitleness;
    public MenuComponentMinimalistTextBox(double x, double y, double width, double height, String text, boolean editable){
        this(x, y, width, height, text, editable, null);
    }
    public MenuComponentMinimalistTextBox(double x, double y, double width, double height, String text, boolean editable, String title){
        this(x, y, width, height, text, editable, title, 16);
    }
    public MenuComponentMinimalistTextBox(double x, double y, double width, double height, String text, boolean editable, String title, double titleInset){
        super(x, y, width, height, text, editable);
        this.title = title;
        if(titleInset==0)titlenessSpeed = 0;
        this.titleInset = titleInset;
    }
    @Override
    public void tick(){
        super.tick();
        if(title!=null){
            if(titlenessSpeed==0)titleness = text==null||text.isEmpty()?1:0;
            if(text==null||text.isEmpty())titleness = Math.min(1, titleness+titlenessSpeed);
            else titleness = Math.max(0, titleness-titlenessSpeed);
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        if(title!=null){
            if(titlenessSpeed==0)actualTitleness = text==null||text.isEmpty()?1:0;
            if(text==null||text.isEmpty())actualTitleness = Math.min(1, titleness+(millisSinceLastTick/50d)*titlenessSpeed);
            else actualTitleness = Math.max(0, titleness-(millisSinceLastTick/50d)*titlenessSpeed);
        }
        super.render(millisSinceLastTick);
    }
    @Override
    public void render(){
        if(textInset<0){
            textInset = height/10;
        }
        Core.applyColor(Core.theme.getTextBoxBorderColor());
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getTextBoxColor());
        drawRect(x+textInset/2, y+textInset/2, x+width-textInset/2, y+height-textInset/2, 0);
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        if(editable){
            if(title!=null){
                drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset-titleInset, text+(((gui.tick&20)<10&&isSelected)?"_":"")+suffix);
                Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)), 0.75f);
                drawText(x+textInset, Core.getValueBetweenTwoValues(1, y+textInset, 0, y+height-textInset-titleInset, actualTitleness), x+width-textInset, Core.getValueBetweenTwoValues(1, y+height-textInset-titleInset, 0, y+height-textInset, actualTitleness), title);
//                if(text.isEmpty())drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset-titleInset, title);
//                else              drawText(x+textInset, y+height-textInset-titleInset, x+width-textInset, y+height-textInset, title);
                Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            }else{
                drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, text+(((gui.tick&20)<10&&isSelected)?"_":"")+suffix);
            }
        }else{
            drawCenteredText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, text+suffix);
        }
    }
    @Override
    public void onCharTyped(char c){
        String lastText = text;
        super.onCharTyped(c);
        if(filter==INT){
            if(text.trim().isEmpty())text = "0";
            if(text.endsWith("-"))text = "-"+text.substring(0, text.length()-1);
            if(text.startsWith("--"))text = text.substring(2);
            try{
                int val = Integer.parseInt(text);
                if(min!=null&&val<min.intValue()){
                    text = min.toString();
                }
                if(max!=null&&val>max.intValue()){
                    text = max.toString();
                }
            }catch(NumberFormatException ex){
                text = lastText;
            }
        }
        if(filter==FLOAT){
            if(text.trim().isEmpty())text = "0";
            if(text.endsWith("-"))text = "-"+text.substring(0, text.length()-1);
            if(text.startsWith("--"))text = text.substring(2);
            try{
                float val = Float.parseFloat(text);
                if(min!=null&&val<min.floatValue()){
                    text = min.toString();
                }
                if(max!=null&&val>max.floatValue()){
                    text = max.toString();
                }
            }catch(NumberFormatException ex){
                text = lastText;
            }
        }
        while(text.startsWith("0")&&!text.startsWith("0.")&&text.length()>1)text = text.substring(1);
        if(text.startsWith(".")){
            while(text.endsWith("0"))text = text.substring(0, text.length()-1);
            if(text.equals("."))text = "0";
        }
    }
    @Override
    public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
        String lastText = text;
        super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
        if(filter==INT){
            if(text.trim().isEmpty())text = "0";
            if(text.endsWith("-"))text = "-"+text.substring(0, text.length()-1);
            if(text.startsWith("--"))text = text.substring(2);
            try{
                int val = Integer.parseInt(text);
                if(min!=null&&val<min.intValue()){
                    text = min.toString();
                }
                if(max!=null&&val>max.intValue()){
                    text = max.toString();
                }
            }catch(NumberFormatException ex){
                text = lastText;
            }
        }
        if(filter==FLOAT){
            if(text.trim().isEmpty())text = "0";
            if(text.endsWith("-"))text = "-"+text.substring(0, text.length()-1);
            if(text.startsWith("--"))text = text.substring(2);
            try{
                float val = Float.parseFloat(text);
                if(min!=null&&val<min.floatValue()){
                    text = min.toString();
                }
                if(max!=null&&val>max.floatValue()){
                    text = max.toString();
                }
            }catch(NumberFormatException ex){
                text = lastText;
            }
        }
        while(text.startsWith("0")&&!text.startsWith("0.")&&text.length()>1)text = text.substring(1);
        if(text.startsWith(".")){
            while(text.endsWith("0"))text = text.substring(0, text.length()-1);
            if(text.equals("."))text = "0";
        }
    }
    public MenuComponentMinimalistTextBox setIntFilter(){
        return setIntFilter(null, null);
    }
    public MenuComponentMinimalistTextBox setIntFilter(Integer min, Integer max){
        this.min = min;
        this.max = max;
        filter = INT;
        return this;
    }
    public MenuComponentMinimalistTextBox setFloatFilter(){
        return setFloatFilter(null, null);
    }
    public MenuComponentMinimalistTextBox setFloatFilter(Float min, Float max){
        this.min = min;
        this.max = max;
        filter = FLOAT;
        return this;
    }
    public MenuComponentMinimalistTextBox setSuffix(String suffix){
        this.suffix = suffix;
        return this;
    }
    @Override
    public MenuComponentMinimalistTextBox setTooltip(String tooltip){
        this.tooltip = tooltip;
        return this;
    }
}