package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.gui.Component;
import static org.lwjgl.glfw.GLFW.*;
public class TextBox extends Component{
    private static final int NONE = 0;
    private static final int INT = 1;
    private static final int FLOAT = 2;
    private static final int DOUBLE = 3;
    private int filter = NONE;
    private String suffix = "";
    private Number min;
    private Number max;
    public String title;
    public float titleInset = 0;
    public float titleness = 0;
    public float titlenessSpeed = 10;
    public boolean editable;
    public String text;
    public float textInset = -1;
    public boolean oscillator;
    public double oscillatorTimer = 0;
    private ArrayList<Runnable> changeListeners = new ArrayList<>();
    public TextBox(float x, float y, float width, float height, String text, boolean editable){
        this(x, y, width, height, text, editable, null);
    }
    public TextBox(float x, float y, float width, float height, String text, boolean editable, String title){
        this(x, y, width, height, text, editable, title, 16);
    }
    public TextBox(float x, float y, float width, float height, String text, boolean editable, String title, float titleInset){
        super(x, y, width, height);
        this.text = text;
        this.editable = editable;
        this.title = title;
        if(titleInset==0)titlenessSpeed = 0;
        this.titleInset = titleInset;
    }
    @Override
    public void render2d(double deltaTime){
        oscillatorTimer+=deltaTime;
        if(oscillatorTimer>=.5){
            oscillator = !oscillator;
            oscillatorTimer-=.5;
        }
        if(title!=null){
            if(titlenessSpeed==0)titleness = text==null||text.isEmpty()?1:0;
            if(text==null||text.isEmpty())titleness = (float)Math.min(1, titleness+deltaTime*titlenessSpeed);
            else titleness = (float)Math.max(0, titleness-deltaTime*titlenessSpeed);
        }
        super.render2d(deltaTime);
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        if(textInset<0){
            textInset = height/10;
        }
        renderer.setColor(Core.theme.getTextBoxBorderColor());
        renderer.fillRect(x, y, x+width, y+height);
        renderer.setColor(Core.theme.getTextBoxColor());
        renderer.fillRect(x+textInset/2, y+textInset/2, x+width-textInset/2, y+height-textInset/2);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        if(editable){
            if(title!=null){
                renderer.drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset-titleInset, text+((oscillator&&isFocused)?"_":"")+suffix);
                renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)), 0.75f);
                renderer.drawText(x+textInset, MathUtil.getValueBetweenTwoValues(1, y+textInset, 0, y+height-textInset-titleInset, titleness), x+width-textInset, MathUtil.getValueBetweenTwoValues(1, y+height-textInset-titleInset, 0, y+height-textInset, titleness), title);
//                if(text.isEmpty())drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset-titleInset, title);
//                else              drawText(x+textInset, y+height-textInset-titleInset, x+width-textInset, y+height-textInset, title);
                renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            }else{
                renderer.drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, text+((oscillator&&isFocused)?"_":"")+suffix);
            }
        }else{
            renderer.drawCenteredText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, text+suffix);
        }
    }
    @Override
    public void onCharTyped(char c){
        String lastText = text;
        if(editable)text+=c;//TODO allow cursor moving
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
        if(filter==DOUBLE){
            if(text.trim().isEmpty())text = "0";
            if(text.endsWith("-"))text = "-"+text.substring(0, text.length()-1);
            if(text.startsWith("--"))text = text.substring(2);
            try{
                double val = Double.parseDouble(text);
                if(min!=null&&val<min.doubleValue()){
                    text = min.toString();
                }
                if(max!=null&&val>max.doubleValue()){
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
        changeListeners.forEach(Runnable::run);
    }
    @Override
    public void onKeyEvent(int key, int scancode, int action, int mods){
        if(!editable)return;
        String lastText = text;
        if(action==GLFW_PRESS||action==GLFW_REPEAT){
            if(key==GLFW_KEY_BACKSPACE&&!text.isEmpty())text = text.substring(0, text.length()-1);//TODO allow cursor moving
        }
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
        if(filter==DOUBLE){
            if(text.trim().isEmpty())text = "0";
            if(text.endsWith("-"))text = "-"+text.substring(0, text.length()-1);
            if(text.startsWith("--"))text = text.substring(2);
            try{
                double val = Double.parseDouble(text);
                if(min!=null&&val<min.doubleValue()){
                    text = min.toString();
                }
                if(max!=null&&val>max.doubleValue()){
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
        changeListeners.forEach(Runnable::run);
    }
    public TextBox setIntFilter(){
        return setIntFilter(null, null);
    }
    public TextBox setIntFilter(Integer min, Integer max){
        this.min = min;
        this.max = max;
        filter = INT;
        return this;
    }
    public TextBox setFloatFilter(){
        return setFloatFilter(null, null);
    }
    public TextBox setFloatFilter(Float min, Float max){
        this.min = min;
        this.max = max;
        filter = FLOAT;
        return this;
    }
    public TextBox setDoubleFilter(){
        return setDoubleFilter(null, null);
    }
    public TextBox setDoubleFilter(Double min, Double max){
        this.min = min;
        this.max = max;
        filter = DOUBLE;
        return this;
    }
    public TextBox setSuffix(String suffix){
        this.suffix = suffix;
        return this;
    }
    @Override
    public TextBox setTooltip(String tooltip){
        this.tooltip = tooltip;
        return this;
    }
    public TextBox onChange(Runnable r){
        changeListeners.add(r);
        return this;
    }
}