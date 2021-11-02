package net.ncplanner.plannerator.planner.menu.component;
import java.util.ArrayList;
import net.ncplanner.plannerator.Renderer;
import static org.lwjgl.glfw.GLFW.*;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.s_tack.Tokenizer;
import net.ncplanner.plannerator.planner.s_tack.token.BoolValueToken;
import net.ncplanner.plannerator.planner.s_tack.token.CharValueToken;
import net.ncplanner.plannerator.planner.s_tack.token.CommentToken;
import net.ncplanner.plannerator.planner.s_tack.token.FloatValueToken;
import net.ncplanner.plannerator.planner.s_tack.token.IdentifierToken;
import net.ncplanner.plannerator.planner.s_tack.token.IntValueToken;
import net.ncplanner.plannerator.planner.s_tack.token.InvalidToken;
import net.ncplanner.plannerator.planner.s_tack.token.LabelToken;
import net.ncplanner.plannerator.planner.s_tack.token.StringValueToken;
import net.ncplanner.plannerator.planner.s_tack.token.Token;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.Keyword;
import net.ncplanner.plannerator.planner.s_tack.token.operator.Operator;
import simplelibrary.font.FontManager;
import simplelibrary.image.Color;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentCodeEditor extends MenuComponent{
    public ArrayList<String> text = new ArrayList<String>(){
        @Override
        public String set(int index, String element){
            if(onChange!=null)onChange.run();
            return super.set(index, element);
        }
        @Override
        public boolean add(String e){
            if(onChange!=null)onChange.run();
            return super.add(e);
        }
        @Override
        public void add(int index, String element){
            if(onChange!=null)onChange.run();
            super.add(index, element);
        }
        @Override
        public boolean remove(Object o){
            if(onChange!=null)onChange.run();
            return super.remove(o);
        }
        @Override
        public String remove(int index){
            if(onChange!=null)onChange.run();
            return super.remove(index);
        }
    };
    public int cursorX, cursorY;
    public FormattedText textDisplay = new FormattedText();
    public final int textHeight;
    public final int textWidth;
    public final int border;
    public boolean pendingDisplayUpdate = true;
    private Thread displayUpdateThread = null;
    private int cursorTimer;
    private boolean cursorVisible;
    public Runnable onChange;
    public MenuComponentCodeEditor(String text){
        this(text, 20);
    }
    public MenuComponentCodeEditor(String text, int textHeight){
        super(0, 0, 0, 0);
        this.textHeight = textHeight;
        this.textWidth = textHeight/2;
        this.border = textWidth;
        for(String s : text.split("\n", -1))this.text.add(s);
        if(this.text.isEmpty())this.text.add("");//Not sure if splitting an empty string results in an empty string
    }
    @Override
    public void render(int millisSinceLastTick){
        FontManager.setFont("monospaced");
        int charsWide = 0, charsHigh = text.size();
        for(String s : text)charsWide = Math.max(charsWide, s.length());
        charsWide+=(charsHigh+"").length();//line number
        width = Math.max(((MenuComponent)parent).width, charsWide*textWidth+border*3+20);
        height = Math.max(((MenuComponent)parent).height, charsHigh*textHeight+border*2+20);
        super.render(millisSinceLastTick);
        FontManager.setFont("high resolution");
    }
    @Override
    public void render(){
        Renderer renderer = new Renderer();
        ArrayList<FormattedText> lines = textDisplay.splitLines();
        int xOff = (lines.size()+"").length()*textWidth;
        for(int i = 0; i<lines.size(); i++){
            FormattedText line = lines.get(i);
            renderer.drawFormattedText(x+xOff+border*2, y+border+i*textHeight, x+width-border, y+border+(i+1)*textHeight, line, -1);
        }
        renderer.setColor(Core.theme.getCodeTextColor());
        if(isSelected&&cursorVisible)drawRect(x+xOff+cursorX*textWidth+border*2, y+border+cursorY*textHeight, x+xOff+cursorX*textWidth+border*2+2, y+border+(cursorY+1)*textHeight, 0);
        renderer.setColor(Core.theme.getCodeLineMarkerColor());
        drawRect(x, y, x+xOff+border*3/2, y+height, 0);
        renderer.setColor(Core.theme.getCodeLineMarkerTextColor());
        for(int i = 0; i<Math.max(1, text.size()); i++){
            renderer.drawText(x+border, y+border+i*textHeight, x+xOff+border, y+border+(i+1)*textHeight, (i+1)+"");
        }
        renderer.setWhite();
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        ArrayList<FormattedText> lines = textDisplay.splitLines();
        int xOff = (lines.size()+"").length()*textWidth;
        x-=xOff;
        y-=border;
        x-=border*2;
        if(button==0&&pressed){
            cursorY = Math.min(text.size()-1, (int)(y/textHeight));
            cursorX = Math.min(text.get(cursorY).length(), (int)Math.round(x/textWidth));
        }
    }
    @Override
    public void tick(){
        if(pendingDisplayUpdate){
            textDisplay.clear();
            for(String s : text)textDisplay.addText(s+"\n", Core.theme.getCodeTextColor());
            updateScroll();
            if(displayUpdateThread==null){
                displayUpdateThread = new Thread(() -> {
                    pendingDisplayUpdate = false;
                    cursorTimer = 0;
                    cursorVisible = true;
                    ArrayList<Token> tokens = Tokenizer.tokenize(getText());
                    if(pendingDisplayUpdate){
                        displayUpdateThread = null;
                        return;
                    }
                    ArrayList<String> cachedLabels = new ArrayList<>();
                    textDisplay.clear();
                    for(Token token : tokens){
                        Color col = Core.theme.getCodeTextColor();
                        if(token instanceof Keyword){
                            col = Core.theme.getCodeKeywordTextColor();
                        }
                        if(token instanceof Operator){
                            col = Core.theme.getCodeOperatorTextColor();
                        }
                        if(token instanceof BoolValueToken){
                            col = Core.theme.getCodeBooleanTextColor();
                        }
                        if(token instanceof CharValueToken){
                            col = Core.theme.getCodeCharTextColor();
                        }
                        if(token instanceof CommentToken){
                            col = Core.theme.getCodeCommentTextColor();
                        }
                        if(token instanceof FloatValueToken){
                            col = Core.theme.getCodeFloatTextColor();
                        }
                        if(token instanceof IntValueToken){
                            col = Core.theme.getCodeIntTextColor();
                        }
                        if(token instanceof LabelToken){
                            col = Core.theme.getCodeLabelTextColor();
                        }
                        if(token instanceof StringValueToken){
                            col = Core.theme.getCodeStringTextColor();
                        }
                        if(token instanceof IdentifierToken){
                            String nam = ((IdentifierToken)token).text;
                            if(!cachedLabels.contains(nam)){
                                for(Token t : tokens){
                                    if(t instanceof LabelToken){
                                        if(nam.equals(((LabelToken)t).label)){
                                            cachedLabels.add(nam);
                                            break;
                                        }
                                    }
                                }
                            }
                            if(cachedLabels.contains(nam))col = Core.theme.getCodeIdentifierTextColor();
                        }
                        if(token instanceof InvalidToken){
                            col = Core.theme.getCodeInvalidTextColor();
                        }
                        textDisplay.addText(token.text, col);
                    }
                    displayUpdateThread = null;
                });
                displayUpdateThread.setDaemon(true);
                displayUpdateThread.start();
            }
        }
        super.tick();
        cursorTimer++;
        if(cursorTimer>=10){
            cursorTimer-=10;
            cursorVisible = !cursorVisible;
        }
    }
    @Override
    public void onCharTyped(char c){
        String line = text.get(cursorY);
        if(cursorX==line.length()){
            if((c=='}'||c==']')&&line.trim().isEmpty()&&line.length()>=4){
                line = line.substring(4);
                cursorX-=4;
            }
            line+=c;
        }
        else{
            line = line.substring(0, cursorX)+c+line.substring(cursorX);
        }
        cursorX++;
        text.set(cursorY, line);
        updateDisplay();
    }
    @Override
    public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
        if(!(isPress||isRepeat))return;
        switch(key){
            case GLFW_KEY_BACKSPACE:
                if(cursorX==0){
                    if(cursorY==0)return;
                    cursorX = text.get(cursorY-1).length();
                    text.set(cursorY-1, text.get(cursorY-1)+text.get(cursorY));
                    text.remove(cursorY);
                    cursorY--;
                }else{
                    int numToDelete = 1;
                    String line = text.get(cursorY);
                    if(Core.isControlPressed()){
                        char prev = line.charAt(cursorX-1);
                        for(int i = 1; i<cursorX; i++){
                            char c = line.charAt(cursorX-i-1);
                            if(canDel(c, prev)){
                                numToDelete++;
                            }else break;
                            prev = c;
                        }
                    }
                    text.set(cursorY, line.substring(0, cursorX-numToDelete)+line.substring(cursorX));
                    cursorX-=numToDelete;
                }
                updateDisplay();
                break;
            case GLFW_KEY_DELETE:
                if(cursorX==text.get(cursorY).length()){
                    if(cursorY==text.size()-1)return;
                    text.set(cursorY, text.get(cursorY)+text.get(cursorY+1));
                    text.remove(cursorY+1);
                }else{
                    int numToDelete = 1;
                    String line = text.get(cursorY);
                    if(Core.isControlPressed()){
                        char prev = line.charAt(cursorX);
                        for(int i = 1; i<line.length()-cursorX; i++){
                            char c = line.charAt(cursorX+i);
                            if(canDel(prev, c)){
                                numToDelete++;
                            }else break;
                            prev = c;
                        }
                    }
                    text.set(cursorY, line.substring(0, cursorX)+line.substring(cursorX+numToDelete));
                }
                updateDisplay();
                break;
            case GLFW_KEY_TAB:
                if(Core.isShiftPressed()){
                    int numSpaces = 0;
                    String line = text.get(cursorY);
                    for(int i = 0; i<line.length(); i++){
                        if(line.charAt(i)==' ')numSpaces++;
                        else break;
                    }
                    if(numSpaces==0)return;
                    int newSpaces = numSpaces/4*4;
                    if(newSpaces==numSpaces)newSpaces-=4;
                    text.set(cursorY, line.substring(numSpaces-newSpaces));
                    cursorX = newSpaces;
                }else{
                    int numSpaces = 4-(cursorX%4);
                    String spaces = "";
                    for(int i = 0; i<numSpaces; i++)spaces+=" ";
                    String line = text.get(cursorY);
                    if(cursorX==line.length())line+=spaces;
                    else{
                        line = line.substring(0, cursorX)+spaces+line.substring(cursorX);
                    }
                    cursorX+=numSpaces;
                    text.set(cursorY, line);
                }
                updateDisplay();
                break;
            case GLFW_KEY_ENTER:
                String indent = "";
                String line = text.get(cursorY);
                for(int i = 0; i<line.length(); i++){
                    if(line.charAt(i)==' ')indent+=" ";
                    else break;
                }
                if(line.trim().endsWith("{")||line.trim().endsWith("["))indent+="    ";
                String newline = indent+line.substring(cursorX);
                text.set(cursorY, line.substring(0, cursorX));
                text.add(cursorY+1, newline);
                cursorY++;
                cursorX = indent.length();
                updateDisplay();
                break;
            case GLFW_KEY_LEFT:
                if(cursorX==0){
                    if(cursorY==0)return;
                    cursorY--;
                    cursorX = text.get(cursorY).length();
                }else{
                    int numToGoLeft = 1;
                    line = text.get(cursorY);
                    if(Core.isControlPressed()){
                        char prev = line.charAt(cursorX-1);
                        for(int i = 1; i<cursorX; i++){
                            char c = line.charAt(cursorX-i-1);
                            if(canDel(c, prev)){
                                numToGoLeft++;
                            }else break;
                            prev = c;
                        }
                    }
                    cursorX-=numToGoLeft;
                }
                updateDisplay();
                break;
            case GLFW_KEY_RIGHT:
                if(cursorX==text.get(cursorY).length()){
                    if(cursorY==text.size()-1)return;
                    cursorY++;
                    cursorX = 0;
                }else{
                    int numToGoRight = 1;
                    line = text.get(cursorY);
                    if(Core.isControlPressed()){
                        char prev = line.charAt(cursorX);
                        for(int i = 1; i<line.length()-cursorX; i++){
                            char c = line.charAt(cursorX+i);
                            if(canDel(prev, c)){
                                numToGoRight++;
                            }else break;
                            prev = c;
                        }
                    }
                    cursorX+=numToGoRight;
                }
                updateDisplay();
                break;
            case GLFW_KEY_UP:
                if(cursorY==0)return;
                cursorY--;
                cursorX = Math.min(cursorX, text.get(cursorY).length());
                updateDisplay();
                break;
            case GLFW_KEY_DOWN:
                if(cursorY==text.size()-1)return;
                cursorY++;
                cursorX = Math.min(cursorX, text.get(cursorY).length());
                updateDisplay();
                break;
            case GLFW_KEY_HOME:
                if(Core.isControlPressed()){
                    cursorX = cursorY = 0;
                }else{
                    int indentation = 0;
                    line = text.get(cursorY);
                    for(int i = 0; i<line.length(); i++){
                        if(line.charAt(i)==' ')indentation++;
                        else break;
                    }
                    if(cursorX<=indentation&&cursorX>0)cursorX = 0;
                    else cursorX = indentation;
                }
                updateDisplay();
                break;
            case GLFW_KEY_END:
                if(Core.isControlPressed())cursorY = text.size()-1;
                cursorX = text.get(cursorY).length();
                updateDisplay();
                break;
        }
    }
    private boolean canDel(char l, char r){
        if(Character.isLetter(l)){
            if(Character.isLetter(r)){
                if(Character.isUpperCase(l))return true;
                if(Character.isLowerCase(l)&&Character.isLowerCase(r))return true;
            }
        }
        return Character.getType(l)==Character.getType(r);
    }
    public String getText(){
        String txt = "";
        for(String s : text)txt+="\n"+s;
        txt = txt.substring(1);
        return txt;
    }
    public void updateScroll(){}
    private void updateDisplay(){
        pendingDisplayUpdate = true;
    }
}