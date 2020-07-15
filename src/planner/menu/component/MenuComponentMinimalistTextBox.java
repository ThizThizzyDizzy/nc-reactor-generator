package planner.menu.component;
import planner.Core;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.components.MenuComponentTextBox;
public class MenuComponentMinimalistTextBox extends MenuComponentTextBox{
    private static final int NONE = 0;
    private static final int INT = 1;
    private static final int FLOAT = 2;
    private int filter = NONE;
    public MenuComponentMinimalistTextBox(double x, double y, double width, double height, String text, boolean editable){
        super(x, y, width, height, text, editable);
    }
    @Override
    public void render(){
        if(textInset<0){
            textInset = height/10;
        }
        Core.applyColor(Core.theme.getListColor().darker());//TODO remove .darker()
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getListColor());
        drawRect(x+textInset/2, y+textInset/2, x+width-textInset/2, y+height-textInset/2, 0);
        Core.applyColor(Core.theme.getTextColor());
        if(editable){
            drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, text+(((gui.tick&20)<10&&isSelected)?"_":""));
        }else{
            drawCenteredText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, text);
        }
    }
    @Override
    public void processKeyboard(char character, int key, boolean pressed, boolean repeat){
        String lastText = text;
        super.processKeyboard(character, key, pressed, repeat);
        if(filter==INT){
            if(text.trim().isEmpty())text = "0";
            try{
                Integer.parseInt(text);
            }catch(NumberFormatException ex){
                text = lastText;
            }
        }
        if(filter==FLOAT){
            if(text.trim().isEmpty())text = "0";
            try{
                Float.parseFloat(text);
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
        filter = INT;
        return this;
    }
    public MenuComponentMinimalistTextBox setFloatFilter(){
        filter = FLOAT;
        return this;
    }
}