package net.ncplanner.plannerator.planner.menu;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentLabel;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimaList;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistButton;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistTextView;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentScrollableCodeEditor;
import net.ncplanner.plannerator.planner.menu.dialog.MenuDialog;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.StackUnderflowError;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
import static org.lwjgl.glfw.GLFW.*;
import simplelibrary.Stack;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuStackEditor extends Menu{
    public Script script = null;
    public MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 192, 48, "Done", true, true));
    public MenuComponentMinimalistButton run = add(new MenuComponentMinimalistButton(0, 0, 0, 48, "Run", true, true));
    public MenuComponentMinimalistButton step = add(new MenuComponentMinimalistButton(0, 0, 192, 48, "Step", true, true));
    public MenuComponentMinimaList stackDisplay = add(new MenuComponentMinimaList(0, 64, 192, 0, 32));
    public MenuComponentMinimaList variablesDisplay = add(new MenuComponentMinimaList(0, 64, 192, 0, 32));
    public MenuComponentScrollableCodeEditor editor;
    public MenuComponentMinimalistTextView output = add(new MenuComponentMinimalistTextView(192, 0, 0, 192, 20, 20){
        {
            bottomWhitespaceLines++;
        }
    });
    private boolean autostep = false, autostepren = false;
    private File saveFile;
    private Runnable onSave;
    private boolean unsavedChanges = false;
    private int saved = 0;
    public MenuStackEditor(GUI gui, Menu parent){
        this(gui, parent, null, "");
    }
    private MenuStackEditor(GUI gui, Menu parent, File file, String scriptText){
        super(gui, parent);
        this.saveFile = file;
        editor = add(new MenuComponentScrollableCodeEditor(192, 64, 0, 0, 20, 20, scriptText));
        editor.editor.onChange = ()->{
            unsavedChanges = true;
        };
        done.addActionListener((e) -> {
            gui.open(parent);
        });
        run.addActionListener((e) -> {
            if(script==null||script.isFinished())createScript();
            if(Core.isShiftPressed()){
                autostep = true;
            }else if(Core.isControlPressed()){
                autostepren = true;
            }else script.run();
        });
        step.addActionListener((e) -> {
            autostep = false;
            autostepren = false;
            if(script==null||script.isFinished())createScript();
            script.step();
        });
    }
    @Override
    public void onGUIOpened(){
        super.onGUIOpened();
        selected = editor;
        editor.isSelected = true;
        Core.setWindowTitle("S'tack Editor");
    }
    @Override
    public void onGUIClosed(){
        super.onGUIClosed();
        Core.resetWindowTitle();
    }
    @Override
    public void tick(){
        super.tick();
        if(script!=null&&!script.isFinished()&&autostep){
            script.step();
        }
        if(saved>0)saved--;
    }
    @Override
    public void render(int millisSinceLastTick){
        Renderer renderer = new Renderer();
        renderer.setWhite();
        if(script!=null&&!script.isFinished()&&autostepren){
            script.step();
        }
        run.x = done.width;
        step.x = gui.helper.displayWidth()-step.width;
        run.width = step.x-run.x;
        variablesDisplay.y = output.y;
        variablesDisplay.height = output.height;
        editor.x = output.x = stackDisplay.width;
        editor.width = output.width = gui.helper.displayWidth()-editor.x;
        output.y = gui.helper.displayHeight()-output.height;
        editor.height = output.y-editor.y-10;
        stackDisplay.height = editor.height;
        super.render(millisSinceLastTick);
        renderer.setColor(Core.theme.getComponentTextColor(0));
        double savedActual = Math.min(20, saved-millisSinceLastTick/50d);
        double textHeight = 40;
        double textLength = FontManager.getLengthForStringWithHeight("Saved", textHeight);
        drawTextWithBounds(editor.x+editor.width-textLength-editor.vertScrollbarWidth, editor.y-textHeight+savedActual*textHeight/20, editor.x+editor.width, editor.y+savedActual*textHeight/20, editor.x, editor.y, editor.x+editor.width, editor.y+editor.height, "Saved");
    }
    private void createScript(){
        output.setText("");
        stackDisplay.components.clear();
        variablesDisplay.components.clear();
        script = new Script(new Stack<StackObject>(){
            @Override
            public void push(StackObject obj){
                stackDisplay.add(new MenuComponentLabel(0, 0, 100, 20, obj.toString(), stackDisplay.components.size()%2==0){
                    @Override
                    public void drawText(){
                        drawCenteredText(x, y, x+width, y+height, text);
                    }
                });
                super.push(obj);
            }
            @Override
            public StackObject pop(){
                if(isEmpty())throw new StackUnderflowError();
                stackDisplay.components.remove(stackDisplay.components.get(stackDisplay.components.size()-1));
                return super.pop();
            }
        }, new HashMap<String, StackVariable>(), editor.getText(), (str) -> {
            output.addText(str+"\n");
            output.renderBackground();
            output.scrollVert(output.height);
        }){
            @Override
            public void step(){
                super.step();
//                variablesDisplay.components.clear();
//                Script s = this;
//                while(!s.subscripts.isEmpty()){
//                    Object peek = s.subscripts.peek();
//                    if(peek instanceof Script)s = (Script)peek;
//                    else break;
//                }
//                HashMap<String, StackVariable> vars = s.variables;
//                ArrayList<String> variableNames = new ArrayList<>(vars.keySet());
//                Collections.sort(variableNames);
//                for(String var : variableNames){
//                    variablesDisplay.add(new MenuComponentLabel(0, 0, 100, 20, var, variablesDisplay.components.size()%2==0){
//                        @Override
//                        public void drawText(){
//                            drawCenteredText(x, y, x+width, y+height, vars.get(var).toString());
//                        }
//                    });
//                }
            }
        };
    }
    @Override
    public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
        super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
        if(key==GLFW_KEY_S&&isPress&&!isRepeat&&Core.isControlPressed()){
            save(Core.isShiftPressed());
        }
    }
    private void save(boolean as){
        if(saveFile==null||as){
            try{
                Core.createFileChooser(saveFile, (t) -> {
                    if(t.getName().contains("."))saveFile = t;
                    else saveFile = new File(t.getAbsolutePath()+".stack");
                    save(false);
                }, FileFormat.STACK);
            }catch(IOException ex){
                throw new RuntimeException("Failed to save script!", ex);
            }
        }else{
            try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile)))){
                for(int i = 0; i<editor.editor.text.size(); i++){
                    writer.write((i==0?"":"\n")+editor.editor.text.get(i));
                }
                unsavedChanges = false;
                if(onSave!=null)onSave.run();
                onSave = null;
                saved = 40;
            }catch(IOException ex){
                throw new RuntimeException("Failed to save script!", ex);
            }
        }
    }
    @Override
    public boolean onFilesDropped(double x, double y, String[] files){
        if(files.length!=1)return false;
        if(unsavedChanges){
            gui.menu = new MenuDialog(gui, this){
                {
                    textBox.setText("Unsaved changes detected!\nSave changes?");
                    addButton("Save", (e) -> {
                        close();
                        save(false);
                        onSave = () -> {
                            MenuStackEditor.this.onFilesDropped(x, y, files);
                        };
                    });
                    addButton("Discard", (e) -> {
                        close();
                        unsavedChanges = false;
                        MenuStackEditor.this.onFilesDropped(x, y, files);
                    });
                    addButton("Cancel", (e) -> {
                        close();
                    });
                }
            };
            return true;
        }
        for(String s : files){
            File f = new File(s);
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))){
                String allTheText = "";
                String line;
                while((line = reader.readLine())!=null){
                    allTheText+="\n"+line;
                }
                gui.open(new MenuStackEditor(gui, parent, f, allTheText.substring(1)));
            }catch(IOException ex){
                throw new RuntimeException("Failed to load script!", ex);
            }
        }
        return true;
    }
}