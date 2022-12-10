package net.ncplanner.plannerator.planner.gui.menu.dssl;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.Stack;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.Main;
import net.ncplanner.plannerator.planner.Task;
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
import net.ncplanner.plannerator.planner.gui.menu.MenuMain;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.HorizontalList;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.TextView;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
public class MenuDsslEditor extends Menu{
    public Button done = add(new Button(0, 0, 192, 48, "Done", true, true));
    public Button run = add(new Button(0, 0, 0, 48, "Run", true, false));
    public Button debug = add(new Button(0, 0, 192, 48, "Debug", true, true));
    public Button step = add(new Button(0, 0, 128, 48, "Step", true, true));
    public SingleColumnList stackDisplay = add(new SingleColumnList(0, 48, 384, 0, 32));
    public SingleColumnList variablesDisplay = add(new SingleColumnList(0, 48, 384, 0, 32));
    public HorizontalList tabsList = add(new HorizontalList(0, 48, 0, 32, 0));
    public ArrayList<EditorTab> tabs = new ArrayList<>();
    public TextView output = add(new TextView(0, 0, 0, 192, 20, 20){
        {
            font = Core.FONT_MONO_20;
            bottomWhitespaceLines++;
        }
    });
    public TextBox input = add(new TextBox(0, 0, 0, 32, "", true));
    public ScrollableDsslEditor editor;
    public Thread scriptThread;
    private boolean testing;
    private boolean firstRun = true;
    private EditorTab currentTab;
    private Task loadLibraries;
    private File[] loadingLibs;
    private boolean reloadNeeded = false;
    private final HashMap<String, HashSet<String>> libraries = new HashMap<>();
    public MenuDsslEditor(GUI gui, Menu parent) {
        super(gui, parent);
        done.addAction(() -> {
            gui.open(parent);
        });
        run.addAction(() -> {
            if(scriptThread!=null){
                editor.script.halt();
                return;
            }
            if(editor.script==null||editor.script.isFinished()){
                editor.debug = false;
                createScript(false);
            }
            scriptThread = new Thread(() -> {
                RuntimeException e = null;
                long tim = System.nanoTime();
                try{
                    editor.script.run(null);
                }catch(Exception ex){
                    e = new RuntimeException(ex);
                }
                output.addText(new FormattedText("Time: "+(System.nanoTime()-tim)/1000000+"ms", Core.theme.getCodeCommentTextColor()));
                scriptThread = null;
                if(e!=null)throw(e);
            });
            scriptThread.start();
        });
        debug.addAction(() -> {
            if(scriptThread!=null){
                editor.script.halt();
                return;
            }
            if(editor.script==null||editor.script.isFinished()){
                editor.debug = true;
                createScript(true);
            }
            ArrayList<Token> breakpoints = new ArrayList<>();
            for(Token token : editor.script.script){
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
                    editor.script.run(breakpoints);
                }catch(Exception ex){
                    e = new RuntimeException(ex);
                }
                scriptThread = null;
                if(e!=null)throw(e);
            });
            scriptThread.start();
        });
        step.addAction(() -> {
            if((Core.isShiftPressed()||Core.isControlPressed())){
                if(editor.script!=null&&!editor.script.isFinished())editor.script.halt();
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
            if(editor.script==null||editor.script.isFinished()){
                createScript(true);
                editor.debug = true;
            }
            editor.script.step();
        });
        editor = add(new ScrollableDsslEditor(0, 48+32, 0, 0, 20, 20));
        editor.libraries = libraries;
        reloadLibraries();
    }
    private void createScript(boolean debug){
        output.setText("");
        stackDisplay.components = new ArrayList<>();
        variablesDisplay.components = new ArrayList<>();
        editor.script = new Script(new Stack<StackObject>(){
            @Override
            public StackObject push(StackObject obj){
                if(obj==null)throw new IllegalArgumentException("Tried to push real null to the stack!");
                if(debug){
                    ArrayList<Component> components = new ArrayList<>(stackDisplay.components);
                    components.add(new Label(0, 0, 100, 20, cap(100, Objects.toString(obj).replace("\n", "\\n")), components.size()%2==0){
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
                                renderer.drawCenteredText(x, y, x+width, y+height, cap(100, vars.get(var).toString().replace("\n", "\\n")));
                            }
                        });
                        variablesDisplay.components = comps;
                    }
                }
            }
        };
    }
    private String cap(int maxLen, String s) {
        return s.substring(0, Math.min(maxLen,s.length()));
    }
    private double saved = 0;
    @Override
    public void render2d(double deltaTime) {
        if(reloadNeeded)reloadLibraries();
        if(saved>0)saved = Math.max(0, saved-deltaTime*20);
        run.text = "Run";
        debug.text = "Debug";
        step.text = editor.script!=null&&!editor.script.isFinished()&&(Core.isShiftPressed()||Core.isControlPressed())?"Halt":((Core.isShiftPressed()||Core.isControlPressed())?"Test":"Step");
        if(editor.script!=null&&!editor.script.isFinished()){
            run.text = editor.debug?"Finish":"Running...";
            debug.text = editor.debug?"Continue":"Debug";
        }
        if(scriptThread!=null)run.text = "Halt";
        step.x = gui.getWidth()-step.width;
        debug.x = step.x-debug.width;
        run.width = gui.getWidth()-done.width-debug.width-step.width;
        run.x = debug.x-run.width;
        editor.x = tabsList.x = input.x = output.x = stackDisplay.width = variablesDisplay.width = editor.debug?gui.getWidth()/4:0;
        editor.width = tabsList.width = input.width = output.width = gui.getWidth()-tabsList.x;
        output.height = editor.script==null?0:192;
        input.height = 0;//TODO make taller when running and input is requested
        editor.height = stackDisplay.height = output.y-editor.y;
        input.y = gui.getHeight()-input.height;
        variablesDisplay.y = output.y = input.y-output.height;
        variablesDisplay.height = input.height+output.height;
        super.render2d(deltaTime);
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getComponentTextColor(0));
        float textHeight = 40;
        float textLength = renderer.getStringWidth("Saved", textHeight);
        renderer.bound(editor.x, editor.y, editor.x+editor.width, editor.y+editor.height);
        renderer.drawText(editor.x+editor.width-textLength-editor.vertScrollbarWidth, editor.y-textHeight+(float)saved*textHeight/20, editor.x+editor.width, editor.y+(float)saved*textHeight/20, "Saved");
        renderer.unBound();
    }
    @Override
    public void onFilesDropped(String[] files){
        for(String s : files){
            File f = new File(s);
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))){
                String allTheText = "";
                String line;
                while((line = reader.readLine())!=null){
                    allTheText+="\n"+line.replace("\t", "    ");//TODO adjustable number of spaces
                }
                EditorTab tab;
                tabs.add(tab = new EditorTab(f, allTheText.substring(Math.min(1, allTheText.length()))));
                tabsList.add(new EditorTabComponent(tab).onClick(()->{
                    resetScript();
                    currentTab = tab;
                    editor.setEditor(tab.editor);
                }));
            }catch(IOException ex){
                Core.warning("Failed to load script!", ex);
            }
        }
    }
    private void resetScript() {
        if(editor.script!=null)editor.script.halt();
        editor.script = null;
        editor.debug = false;
    }
    @Override
    public void onOpened() {
        super.onOpened();
        MenuMain.enables = true;
        focusedComponent = editor;
        editor.isFocused = true;
        Core.setWindowTitle("DSSL Editor");
    }
    @Override
    public void onClosed() {
        super.onClosed();
        Core.resetWindowTitle();
    }
    @Override
    public void onKeyEvent(int key, int scancode, int action, int mods){
        super.onKeyEvent(key, scancode, action, mods);
        if(key==GLFW_KEY_S&&action==GLFW_PRESS&&Core.isControlPressed()){
            save(Core.isShiftPressed());
        }
    }
    private void save(boolean as){
        if(currentTab==null)return;
        if(currentTab.file==null||as){
            try{
                Core.createFileChooser(currentTab.file, (t) -> {
                    if(t.getName().contains("."))currentTab.file = t;
                    else currentTab.file = new File(t.getAbsolutePath()+".dssl");
                    save(false);
                    currentTab.name = currentTab.file.getName();
                }, FileFormat.DSSL, "dssl");
            }catch(IOException ex){
                throw new RuntimeException("Failed to save script!", ex);
            }
        }else{
            try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(currentTab.file)))){
                for(int i = 0; i<editor.editor.text.size(); i++){
                    writer.write((i==0?"":"\n")+editor.editor.text.get(i));
                }
                saved = 40;
                reloadLibraries();
            }catch(IOException ex){
                throw new RuntimeException("Failed to save script!", ex);
            }
        }
    }
    private void reloadLibraries(){
        if(loadingLibs!=null){
            reloadNeeded = true;
            return;
        }
        reloadNeeded = false;
        loadLibraries = new Task("Loading Libraries...");
        File libs = new File("dssl");
        if(libs.exists()&&libs.isDirectory()){
            loadingLibs = libs.listFiles();
        }
        libraries.clear();
        Thread t = new Thread(() -> {
            int i = 0;
            for(File f : loadingLibs){
                loadLibraries.progress = i/(float)loadingLibs.length;
                i++;
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))){
                    String allTheText = "";
                    String line;
                    while((line = reader.readLine())!=null){
                        allTheText+="\n"+line.replace("\t", "    ");//TODO adjustable number of spaces
                    }
                    Script s = new Script(allTheText);
                    s.run(null);
                    libraries.put(f.getName(), new HashSet<>(s.variables.keySet()));
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
            loadLibraries.finish();
            loadingLibs = null;
            loadLibraries = null;
        }, "DSSL Editor Library Loader");
        t.setDaemon(true);
        t.start();
    }
}