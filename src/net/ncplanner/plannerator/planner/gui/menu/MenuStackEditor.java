package net.ncplanner.plannerator.planner.gui.menu;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.Main;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.StackUnderflowError;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
import net.ncplanner.plannerator.planner.dssl.token.Token;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.ScrollableCodeEditor;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextView;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuDialog;
import static org.lwjgl.glfw.GLFW.*;
public class MenuStackEditor extends Menu{
    public Script script = null;
    public Button done = add(new Button(0, 0, 192, 48, "Done", true, true));
    public Button run = add(new Button(0, 0, 0, 48, "Run", true, true));
    public Button step = add(new Button(0, 0, 192, 48, "Step", true, true));
    public SingleColumnList stackDisplay = add(new SingleColumnList(0, 64, 192, 0, 32));
    public SingleColumnList variablesDisplay = add(new SingleColumnList(0, 64, 192, 0, 32));
    public ScrollableCodeEditor editor;
    public Thread scriptThread;
    public TextView output = add(new TextView(192, 0, 0, 192, 20, 20){
        {
            bottomWhitespaceLines++;
        }
    });
    private boolean autostep = false;
    private File saveFile;
    private Runnable onSave;
    private boolean unsavedChanges = false;
    private double saved = 0;
    public boolean debug;
    private boolean testing;
    private boolean firstRun = true;
    public MenuStackEditor(GUI gui, Menu parent){
        this(gui, parent, null, "");
    }
    private MenuStackEditor(GUI gui, Menu parent, File file, String scriptText){
        super(gui, parent);
        this.saveFile = file;
        editor = add(new ScrollableCodeEditor(192, 64, 0, 0, 20, 20, scriptText, this));
        editor.editor.onChange = ()->{
            unsavedChanges = true;
        };
        done.addAction(() -> {
            gui.open(parent);
        });
        run.addAction(() -> {
            if(scriptThread!=null){
                script.halt();
                return;
            }
            if(script==null||script.isFinished())createScript(Core.isShiftPressed());
            if(Core.isControlPressed()&&Core.isShiftPressed()){
                autostep = true;
            }else{
                autostep = false;
                ArrayList<Token> breakpoints = new ArrayList<>();
                for(Token token : script.script){
                    int startX = 0;
                    int startY = 0;
                    int pos = token.start;
                    while(pos>0){
                        startX++;
                        String txt = editor.editor.text.get(startY);
                        if(startX>=txt.length()){
                            startY++;
                            startX = 0;
                            pos--;
                        }
                        if(!txt.isEmpty())pos--;
                    }
                    int lineStartX = 0;
                    for(char c : editor.editor.text.get(startY).toCharArray()){
                        if(c==' ')lineStartX++;
                        else break;
                    }
                    if(editor.editor.breakpoints.contains(startY)&&startX==lineStartX){
                        breakpoints.add(token);
                    }
                }
                scriptThread = new Thread(() -> {
                    RuntimeException e = null;
                    long tim = System.nanoTime();
                    try{
                        script.run(debug?breakpoints:null);
                    }catch(Exception ex){
                        e = new RuntimeException(ex);
                    }
                    if(!debug)output.addText(new FormattedText("Time: "+(System.nanoTime()-tim)/1000000+"ms", Core.theme.getCodeCommentTextColor()));
                    scriptThread = null;
                    if(e!=null)throw(e);
                });
                scriptThread.start();
            }
        });
        step.addAction(() -> {
            if((Core.isShiftPressed()||Core.isControlPressed())){
                if(script!=null&&!script.isFinished())script.halt();
                else{
                    if(!testing){
                        testing = true;
                        output.setText("Preparing for DSSL test\n");
                        new Thread(() -> {
                            try{
                                File dssl = new File("dssl.jar");
                                if(!dssl.exists()||firstRun){
                                    firstRun = false;
                                    InputStream stream = Main.getRemoteInputStream("https://api.github.com/repos/tomdodd4598/Dodd-Simple-Stack-Language/releases/latest");
                                    if(stream==null){
                                        output.addText("Failed to get release data from github!\n");
                                        if(!dssl.exists()){
                                            output.addText("Please download the latest DSSL (Dodd Simple Stack Language), name it dssl.jar, and put it in the same fulder as this planner.");
                                            return;
                                        }
                                    }else{
                                        JSON.JSONObject api = JSON.parse(stream);
                                        String name = api.getJSONArray("assets").getJSONObject(0).getString("name");
                                        dssl.delete();
                                        output.addText("Downloading "+name+"\n");
                                        Main.downloadFile(api.getJSONArray("assets").getJSONObject(0).getString("browser_download_url"), dssl);
                                    }
                                }
                                output.addText("Preparing file...\n");
                                File scrpt = new File("internal_test.dssl");
                                if(scrpt.exists())scrpt.delete();
                                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(scrpt)))) {
                                    for(String s : editor.editor.text)writer.write(s+"\n");
                                }
                                output.setText("== BEGIN DSSL EXECUTION ==\n");
                                long tim = System.nanoTime();
                                Process p = Main.startJava(new String[0], new String[]{scrpt.getAbsolutePath()}, dssl);
                                new Thread("Script output"){
                                    public void run(){
                                        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                                        try{
                                            while(p.isAlive()){
                                                int read = in.read();
                                                if(read==-1){
                                                    Thread.sleep(1);
                                                    continue;
                                                }
                                                char c = (char)read;
                                                output.addText(c+"");
                                            }
                                            output.addText("== END DSSL EXECUTION ==\n");
                                            output.addText(new FormattedText("Time: "+(System.nanoTime()-tim)/1000000+"ms", Core.theme.getCodeCommentTextColor()));
                                            testing = false;
                                            scrpt.delete();
                                        }catch(IOException | InterruptedException ex){
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                }.start();
                                new Thread("Script error output"){
                                    public void run(){
                                        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                                        try{
                                            while(p.isAlive()){
                                                int read = in.read();
                                                if(read==-1){
                                                    Thread.sleep(1);
                                                    continue;
                                                }
                                                char c = (char)read;
                                                output.addText(c+"");
                                            }
                                        }catch(IOException | InterruptedException ex){
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                }.start();
                            }catch(Exception ex){
                                testing = false;
                                throw new RuntimeException(ex);
                            }
                        }).start();
                    }
                }
                return;
            }
            if(scriptThread!=null)return;
            autostep = false;
            if(script==null||script.isFinished())createScript(true);
            script.step();
        });
    }
    @Override
    public void onOpened(){
        super.onOpened();
        focusedComponent = editor;
        editor.isFocused = true;
        Core.setWindowTitle("S'tack Editor");
    }
    @Override
    public void onClosed(){
        super.onClosed();
        Core.resetWindowTitle();
    }
    @Override
    public void render2d(double deltaTime){
        if(script!=null&&!script.isFinished()&&autostep){
            script.step();
        }
        if(saved>0)saved = Math.max(0, saved-deltaTime*20);
        Renderer renderer = new Renderer();
        renderer.setWhite();
        run.text = (Core.isShiftPressed()?"Debug"+(Core.isControlPressed()?" (Autostep)":""):"Run");
        step.text = script!=null&&!script.isFinished()&&(Core.isShiftPressed()||Core.isControlPressed())?"Halt":((Core.isShiftPressed()||Core.isControlPressed())?"Test":"Step");
        if(script!=null&&!script.isFinished()){
            run.text = debug?"Continue":"Running...";
        }
        if(scriptThread!=null)run.text = "Halt";
        run.x = done.width;
        step.x = gui.getWidth()-step.width;
        run.width = step.x-run.x;
        variablesDisplay.y = output.y;
        variablesDisplay.height = output.height;
        editor.x = output.x = stackDisplay.width;
        editor.width = output.width = gui.getWidth()-editor.x;
        output.y = gui.getHeight()-output.height;
        editor.height = output.y-editor.y-10;
        stackDisplay.height = editor.height;
        super.render2d(deltaTime);
        renderer.setColor(Core.theme.getComponentTextColor(0));
        float textHeight = 40;
        float textLength = renderer.getStringWidth("Saved", textHeight);
        renderer.bound(editor.x, editor.y, editor.x+editor.width, editor.y+editor.height);
        renderer.drawText(editor.x+editor.width-textLength-editor.vertScrollbarWidth, editor.y-textHeight+(float)saved*textHeight/20, editor.x+editor.width, editor.y+(float)saved*textHeight/20, "Saved");
        renderer.unBound();
    }
    private void createScript(boolean debug){
        this.debug = debug;
        output.setText("");
        stackDisplay.components = new ArrayList<>();
        variablesDisplay.components = new ArrayList<>();
        script = new Script(new Stack<StackObject>(){
            @Override
            public StackObject push(StackObject obj){
                if(obj==null)throw new IllegalArgumentException("Tried to push real null to the stack!");
                if(debug){
                    ArrayList<Component> components = new ArrayList<>(stackDisplay.components);
                    components.add(new Label(0, 0, 100, 20, Objects.toString(obj).replace("\n", "\\n"), components.size()%2==0){
                        @Override
                        public void drawText(Renderer renderer){
                            renderer.drawCenteredText(x, y, x+width, y+height, text);
                        }
                    });
                    stackDisplay.components = components;
                }
                return super.push(obj);
            }
            @Override
            public StackObject pop(){
                if(isEmpty())throw new StackUnderflowError();
                if(debug){
                    ArrayList<Component> components = new ArrayList<>(stackDisplay.components);
                    components.remove(components.get(components.size()-1));
                    stackDisplay.components = components;
                }
                return super.pop();
            }
        }, new HashMap<String, StackVariable>(), editor.getText(), (str) -> {
            while(str.contains("$$LINE")){
                String s = str.substring(str.indexOf("$$LINE")+7);
                String num = "";
                for(int i = 0; i<s.length(); i++){
                    char c = s.charAt(i);
                    if(!Character.isDigit(c))break;
                    num+=c;
                }
                int c = Integer.parseInt(num);
                int startX = 0;
                int startY = 0;
                int pos = c;
                while(pos>0){
                    startX++;
                    String txt = editor.editor.text.get(startY);
                    if(startX>=txt.length()){
                        startY++;
                        startX = 0;
                        pos--;
                    }
                    if(!txt.isEmpty())pos--;
                }
                str = str.replaceFirst("\\$\\$LINE\\{\\d+\\}", startY+1+"");
            }
            output.addText(str);
            output.drawBackground(0);
            output.scrollVert(output.height);
        }){
            @Override
            public void step(){
                super.step();
                if(debug){
                    variablesDisplay.components = new ArrayList<>();
                    Script s = this;
                    while(!s.subscripts.isEmpty()){
                        Object peek = s.subscripts.peek();
                        if(peek instanceof Script)s = (Script)peek;
                        else break;
                    }
                    HashMap<String, StackVariable> vars = s.variables;
                    ArrayList<String> variableNames = new ArrayList< >(vars.keySet());
                    Collections.sort(variableNames);
                    for(String var : variableNames){
                        ArrayList<Component> comps = new ArrayList<>(variablesDisplay.components);
                        comps.add(new Label(0, 0, 100, 20, var, comps.size()%2==0){
                            @Override
                            public void drawText(Renderer renderer){
                                renderer.drawCenteredText(x, y, x+width, y+height, vars.get(var).toString().replace("\n", "\\n"));
                            }
                        });
                        variablesDisplay.components = comps;
                    }
                }
            }
        };
    }
    @Override
    public void onKeyEvent(int key, int scancode, int action, int mods){
        super.onKeyEvent(key, scancode, action, mods);
        if(key==GLFW_KEY_S&&action==GLFW_PRESS&&Core.isControlPressed()){
            save(Core.isShiftPressed());
        }
    }
    private void save(boolean as){
        if(saveFile==null||as){
            try{
                Core.createFileChooser(saveFile, (t) -> {
                    if(t.getName().contains("."))saveFile = t;
                    else saveFile = new File(t.getAbsolutePath()+".dssl");
                    save(false);
                }, FileFormat.DSSL, "dssl");
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
    public void onFilesDropped(String[] files){
        if(files.length!=1)return;
        if(unsavedChanges){
            new MenuDialog(gui, this){
                {
                    textBox.setText("Unsaved changes detected!\nSave changes?");
                    addButton("Save", () -> {
                        close();
                        save(false);
                        onSave = () -> {
                            MenuStackEditor.this.onFilesDropped(files);
                        };
                    });
                    addButton("Discard", () -> {
                        close();
                        unsavedChanges = false;
                        MenuStackEditor.this.onFilesDropped(files);
                    });
                    addButton("Cancel", () -> {
                        close();
                    });
                }
            }.open();
        }
        for(String s : files){
            File f = new File(s);
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))){
                String allTheText = "";
                String line;
                while((line = reader.readLine())!=null){
                    allTheText+="\n"+line.replace("\t", " ");//TODO adjustable number of spaces
                }
                gui.open(new MenuStackEditor(gui, parent, f, allTheText.substring(1)));
            }catch(IOException ex){
                throw new RuntimeException("Failed to load script!", ex);
            }
        }
    }
}