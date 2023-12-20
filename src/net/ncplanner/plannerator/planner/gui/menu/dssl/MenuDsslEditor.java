package net.ncplanner.plannerator.planner.gui.menu.dssl;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.dssl.DSSLProcessor;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.MenuMain;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.DropdownList;
import net.ncplanner.plannerator.planner.gui.menu.component.HorizontalList;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.TextView;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuMessageDialog;
import static org.lwjgl.glfw.GLFW.*;
public class MenuDsslEditor extends Menu{
    public Button fileMain;
    public DropdownList file = new DropdownList(0, 0, 192, 48){
        {
            add(fileMain = new Button(0, 0, 192, 48, "File", true, true));
        }
        @Override
        public void onMouseButton(double x, double y, int button, int action, int mods) {
            if(!fileMain.enabled)return;
            super.onMouseButton(x, y, button, action, mods);
        }
        @Override
        public void render2d(double deltaTime){
            setSelectedIndex(0);
            super.render2d(deltaTime);
        }
    }.hideButton();
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
    public boolean showOutput = false;
    public TextBox input = add(new TextBox(0, 0, 0, 32, "", true));
    public ScrollableDsslEditor editor;
    public DSSLProcessor processor = new DSSLProcessor((str) -> {
        showOutput = true;
        output.addText(str);
    }, (str)->{
        showOutput = true;
        output.addText(new FormattedText(str, Color.LIGHT_GRAY));
    },()->{
        showOutput = true;
        //TODO input read
        throw new UnsupportedOperationException("Not supported yet.");
    });
    private EditorTab currentTab;
    public MenuDsslEditor(GUI gui, Menu parent) {
        super(gui, parent);
        file.add(new Button("New", true, false).addAction(() -> {
            file.isDown = false;
            file.isFocused = false;
            MenuDsslEditor.this.focusedComponent = null;
            createNew();
        }));
        file.add(new Button("Open", true, false).addAction(() -> {
            file.isDown = false;
            file.isFocused = false;
            MenuDsslEditor.this.focusedComponent = null;
            open();
        }));
        file.add(new Button("Save", true, false).addAction(() -> {
            file.isDown = false;
            file.isFocused = false;
            MenuDsslEditor.this.focusedComponent = null;
            save(false);
        }));
        file.add(new Button("Save as...", true, false).addAction(() -> {
            file.isDown = false;
            file.isFocused = false;
            MenuDsslEditor.this.focusedComponent = null;
            save(true);
        }));
        file.add(new Button("Exit", true, false).addAction(() -> {
            gui.open(parent);
        }));
        run.addAction(() -> {
            if(processor.isActive()){
                if(!processor.isRunning())processor.run();
                return;
            }
            resetOutput();
            save(false, () -> {
                editor.debug = false;
                editor.editor.processor = processor;
                processor.init(currentTab.file);
                processor.run();
            });
        });
        debug.addAction(() -> {
            if(processor.isActive()){
                processor.halt();
                return;
            }
            resetOutput();
            save(false, () -> {
                editor.debug = true;
                editor.editor.processor = processor;
                processor.init(currentTab.file);
                processor.setBreakpoints(editor.editor.breakpoints);
                processor.run();
            });
        });
        step.addAction(() -> {
            if(processor.isActive())processor.step();
            else{
                resetOutput();
                save(false, () -> {
                    editor.editor.processor = processor;
                    processor.init(currentTab.file);
                    processor.setBreakpoints(editor.editor.breakpoints);
                    editor.debug = true;
                    processor.step();
                });
            }
        });
        editor = add(new ScrollableDsslEditor(0, 48+32, 0, 0, 20, 20));
        add(file);//on top of everything
    }
//    private void createScript(boolean debug){
//        output.setText("");
//        stackDisplay.components = new ArrayList<>();
//        variablesDisplay.components = new ArrayList<>();
//        editor.script = new Script(new Stack<StackObject>(){
//            @Override
//            public StackObject push(StackObject obj){
//                if(obj==null)throw new IllegalArgumentException("Tried to push real null to the stack!");
//                if(debug){
//                    ArrayList<Component> components = new ArrayList<>(stackDisplay.components);
//                    components.add(new Label(0, 0, 100, 20, cap(100, Objects.toString(obj).replace("\n", "\\n")), components.size()%2==0){
//                        @Override
//                        public void drawText(Renderer renderer){
//                            renderer.drawCenteredText(x, y, x+width, y+height, text);
//                        }
//                    });
//                    stackDisplay.components = components;
//                }
//                return super.push(obj);
//            }
//            @Override
//            public StackObject pop(){
//                if(isEmpty())throw new StackUnderflowError();
//                if(debug){
//                    ArrayList<Component> components = new ArrayList<>(stackDisplay.components);
//                    components.remove(components.get(components.size()-1));
//                    stackDisplay.components = components;
//                }
//                return super.pop();
//            }
//        }, new HashMap<String, StackVariable>(), editor.getText(), (str) -> {
//            while(str.contains("$$LINE")){
//                String s = str.substring(str.indexOf("$$LINE")+7);
//                String num = "";
//                for(int i = 0; i<s.length(); i++){
//                    char c = s.charAt(i);
//                    if(!Character.isDigit(c))break;
//                    num+=c;
//                }
//                int c = Integer.parseInt(num);
//                int startX = 0;
//                int startY = 0;
//                int pos = c;
//                while(pos>0){
//                    startX++;
//                    String txt = editor.editor.text.get(startY);
//                    if(startX>=txt.length()){
//                        startY++;
//                        startX = 0;
//                        pos--;
//                    }
//                    if(!txt.isEmpty())pos--;
//                }
//                str = str.replaceFirst("\\$\\$LINE\\{\\d+\\}", startY+1+"");
//            }
//            output.addText(str);
//            output.drawBackground(0);
//            output.scrollVert(output.height);
//        }){
//            @Override
//            public void step(){
//                super.step();
//                if(debug){
//                    variablesDisplay.components = new ArrayList<>();
//                    Script s = this;
//                    while(!s.subscripts.isEmpty()){
//                        Object peek = s.subscripts.peek();
//                        if(peek instanceof Script)s = (Script)peek;
//                        else break;
//                    }
//                    HashMap<String, StackVariable> vars = s.variables;
//                    ArrayList<String> variableNames = new ArrayList< >(vars.keySet());
//                    Collections.sort(variableNames);
//                    for(String var : variableNames){
//                        ArrayList<Component> comps = new ArrayList<>(variablesDisplay.components);
//                        comps.add(new Label(0, 0, 100, 20, var, comps.size()%2==0){
//                            @Override
//                            public void drawText(Renderer renderer){
//                                renderer.drawCenteredText(x, y, x+width, y+height, cap(100, vars.get(var).toString().replace("\n", "\\n")));
//                            }
//                        });
//                        variablesDisplay.components = comps;
//                    }
//                }
//            }
//        };
//    }
    private String cap(int maxLen, String s) {
        return s.substring(0, Math.min(maxLen,s.length()));
    }
    private float saved = 0;
    @Override
    public void render2d(double deltaTime) {
        if(saved>0)saved = Math.max(0, saved-(float)deltaTime*20);
        run.text = processor.isActive()?"Continue":"Run";
        debug.text = processor.isActive()?"Halt":"Debug";
        step.x = gui.getWidth()-step.width;
        debug.x = step.x-debug.width;
        run.width = gui.getWidth()-file.width-debug.width-step.width;
        run.x = debug.x-run.width;
        editor.x = tabsList.x = input.x = output.x = stackDisplay.width = variablesDisplay.width = editor.debug?gui.getWidth()/4:0;
        editor.width = tabsList.width = input.width = output.width = gui.getWidth()-tabsList.x;
        output.height = processor.isActive()||showOutput?192:0;
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
        float saved = Math.min(this.saved, 20);
        renderer.drawText(editor.x+editor.width-textLength-editor.vertScrollbarWidth, editor.y-textHeight+saved*textHeight/20, editor.x+editor.width, editor.y+saved*textHeight/20, "Saved");
        renderer.unBound();
    }
    @Override
    public void onFilesDropped(String[] files){
        for(String s : files){
            File f = new File(s);
            loadFile(f);
        }
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
        if(key==GLFW_KEY_S&&action==GLFW_PRESS&&Core.isControlPressed()){
            save(Core.isShiftPressed());
            return;
        }
        if(key==GLFW_KEY_W&&action==GLFW_PRESS&&Core.isControlPressed()){
            close(currentTab);
            return;
        }
        if(key==GLFW_KEY_N&&action==GLFW_PRESS&&Core.isControlPressed()){
            createNew();
            return;
        }
        if(key==GLFW_KEY_O&&action==GLFW_PRESS&&Core.isControlPressed()){
            open();
            return;
        }
        if(key==GLFW_KEY_TAB&&action==GLFW_PRESS&&Core.isControlPressed()){
            int direction = 1;
            if(Core.isShiftPressed())direction = -1;
            int tab = tabs.indexOf(currentTab);
            int newTab = tab+direction;
            if(newTab>=tabs.size())newTab = 0;
            if(newTab<0)newTab = tabs.size()-1;
            if(tabs.isEmpty())newTab = -1;
            if(newTab!=-1)((EditorTabComponent)tabsList.components.get(newTab)).select();
            return;
        }
        super.onKeyEvent(key, scancode, action, mods);
    }
    private void save(boolean as){
        save(as, null);
    }
    private void save(boolean as, Runnable onSaved){
        if(currentTab==null)return;
        if(currentTab.file==null||as){
            try{
                Core.createFileChooser(currentTab.file, (t) -> {
                    if(t.getName().contains("."))currentTab.file = t;
                    else currentTab.file = new File(t.getAbsolutePath()+(currentTab.editor.essl?".essl":".dssl"));
                    save(false, onSaved);
                    currentTab.name = currentTab.file.getName();
                }, FileFormat.DSSL.extensions, "dssl");
            }catch(IOException ex){
                throw new RuntimeException("Failed to save script!", ex);
            }
        }else{
            try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(currentTab.file)))){
                for(int i = 0; i<editor.editor.text.size(); i++){
                    writer.write((i==0?"":"\n")+editor.editor.text.get(i));
                }
                currentTab.unsavedChanges = false;
                saved = 40;
                writer.close();
                if(onSaved!=null)onSaved.run();
            }catch(IOException ex){
                throw new RuntimeException("Failed to save script!", ex);
            }
        }
    }
    private void close(EditorTab tab) {
        if(tab.unsavedChanges){
            new MenuMessageDialog(gui, this, "Unsaved changes detected!\nSave changes?").addButton("save", () -> {
                save(false, () -> {
                    close(tab);
                });
            }, true).addButton("Discard", () -> {
                tab.unsavedChanges = false;
                close(tab);
            }, true).addButton("Cancel", true).open();
            return;
        }
        int idx = tabs.indexOf(tab);
        tabs.remove(tab);
        tabsList.components.remove(idx);
        currentTab = tabs.isEmpty()?null:tabs.get(idx = Math.min(idx, tabs.size()-1));
        if(editor.editor==tab.editor){
            editor.setEditor(currentTab==null?null:currentTab.editor);
            tabsList.setSelectedIndex(idx);
        }
    }
    private void loadFile(File file){
        if(file.isDirectory())return;
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
            String allTheText = "";
            String line;
            while((line = reader.readLine())!=null){
                allTheText+="\n"+line.replace("\t", "    ");//TODO adjustable number of spaces
            }
            EditorTab tab;
            tabs.add(tab = new EditorTab(file, allTheText.substring(Math.min(1, allTheText.length()))));
            tabsList.add(new EditorTabComponent(tab).onClick(()->{
                resetOutput();
                currentTab = tab;
                editor.setEditor(tab.editor);
            }));
            currentTab = tab;
            editor.setEditor(tab.editor);
            tabsList.setSelectedIndex(tabs.size()-1);
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    private void createNew(){
        EditorTab tab;
        tabs.add(tab = new EditorTab("Untitled", ""));
        EditorTabComponent comp;
        tabsList.add(comp = new EditorTabComponent(tab).onClick(()->{
            resetOutput();
            currentTab = tab;
            editor.setEditor(tab.editor);
        }));
        comp.select();
    }
    private void open(){
        try{
            Core.createFileChooser((file) -> {
                loadFile(file);
            }, FileFormat.DSSL, "dssl");
        }catch(IOException ex){
            Core.error("Failed to load configuration!", ex);
        }
    }
    private void resetOutput(){
        showOutput = false;
        output.setText("");
        stackDisplay.components = new ArrayList<>();
        variablesDisplay.components = new ArrayList<>();
    }
}