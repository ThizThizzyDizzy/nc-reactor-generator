package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.config2.ConfigNumberList;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.ProgressBar;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
public class MenuExploreNCPF extends ConfigurationMenu{
    private final SingleColumnList list;
    private final ProgressBar progress;
    private final ArrayList<Config> configs;
    public MenuExploreNCPF(GUI gui, Menu parent, Configuration configuration, File file) throws FileNotFoundException{
        this(gui, parent, configuration, new FileInputStream(file));
    }
    public MenuExploreNCPF(GUI gui, Menu parent, Configuration configuration, InputStream stream){
        this(gui, parent, configuration, loadConfigs(stream));
    }
    private Task task;
    public MenuExploreNCPF(GUI gui, Menu parent, Configuration configuration, ArrayList<Config> configs){
        super(gui, parent, configuration, "Explore NCPF");
        progress = add(new ProgressBar(sidebar.width, 0, 0, 0){
            @Override
            public Task getTask(){
                return task;
            }
        });
        list = add(new SingleColumnList(sidebar.width, 0, 0, 0, 32));
        this.configs = configs;
    }
    @Override
    public void onOpened(){
        super.onOpened();
        Thread t = new Thread(() -> {
            task = new Task("Loading file...");
            ArrayList<Task> subtasks = new ArrayList<>();
            for(Config c : configs){
                subtasks.add(task.addSubtask("Loading Config"));
            }
            Stack<String> path = new Stack<>();
            for(int i = 0; i<configs.size(); i++){
                task.name = "Loading file... ("+(i+1)+"/"+configs.size()+")";
                Task task = subtasks.get(i);
                path.add("["+i+"]");
                Config c = configs.get(i);
                add(new Label(str(c)).setInset(0).alignLeft(), getNCPFComponent(path, c));
                addComponents(c, path, task);
                path.pop();
                task.finish();
            }
            task.finish();
            task = null;
        }, "NCPF Explorer Thread");
        t.setDaemon(true);
        t.start();
    }
    @Override
    public synchronized void render2d(double deltaTime){
        progress.width = list.width = gui.getWidth()-sidebar.width;
        list.y = progress.height = progress.getTaskHeight();
        list.height = gui.getHeight()-list.y;
        super.render2d(deltaTime);
    }
    private static ArrayList<Config> loadConfigs(InputStream stream){
        ArrayList<Config> configs = new ArrayList<>();
        try{
            Config header = Config.newConfig(stream);
            header.load();
            configs.add(header);
        }catch(Exception ex){
            Core.warning("Failed to load NCPF!", ex);
        }
        return configs;
    }
    private synchronized void add(Component c1, Component c2){
        GridLayout lay = new GridLayout(20, 2);
        lay.add(c1);
        lay.add(c2);
        list.add(lay);
    }
    private void addComponents(Config config, Stack<String> path, Task task){
        String pre = task.name;
        String[] properties = config.properties();
        for(int i = 0; i<properties.length; i++){
            task.name = pre+" ("+(i+1)+"/"+properties.length+")";
            task.setProgress(i/(double)properties.length);
            String key = properties[i];
            String indent = indent(path);
            path.push(key);
            Object val = config.get(key);
            add(new Label(indent+key+": "+str(val)).setInset(0).alignLeft(), getNCPFComponent(path, val));
            if(val instanceof Config)addComponents((Config)val, path, task.addSubtask("Loading Config "+str(path)));
            if(val instanceof ConfigList)addComponents((ConfigList)val, path, task.addSubtask("Loading ConfigList "+str(path)));
//            if(val instanceof ConfigNumberList)addComponents((ConfigNumberList)val, path, task.addSubtask("Loading ConfigNumberList "+str(path)));
            path.pop();
        }
        task.finish();
    }
    private void addComponents(ConfigList list, Stack<String> path, Task task){
        String pre = task.name;
        for(int i = 0; i<list.size(); i++){
            task.name = pre+" ("+(i+1)+"/"+list.size()+")";
            task.setProgress(i/(double)list.size());
            String indent = indent(path);
            path.push("["+i+"]");
            Object val = list.get(i);
            add(new Label(indent+i+": "+str(val)).setInset(0).alignLeft(), getNCPFComponent(path, val));
            if(val instanceof Config)addComponents((Config)val, path, task.addSubtask("Loading Config"));
            if(val instanceof ConfigList)addComponents((ConfigList)val, path, task.addSubtask("Loading ConfigList"));
//            if(val instanceof ConfigNumberList)addComponents((ConfigNumberList)val, path, task.addSubtask("Loading ConfigNumberList"));
            path.pop();
        }
        task.finish();
    }
    private void addComponents(ConfigNumberList list, Stack<String> path, Task task){
        String pre = task.name;
        for(int i = 0; i<list.size(); i++){
            task.name = pre+" ("+(i+1)+"/"+list.size()+")";
            task.setProgress(i/(double)list.size());
            String indent = indent(path);
            path.push("["+i+"]");
            long val = list.get(i);
            add(new Label(indent+i+": "+val).setInset(0).alignLeft(), getNCPFComponent(path, val));
            path.pop();
        }
        task.finish();
    }
    private String str(Object o){
        if(o instanceof Config)return "Config (size="+((Config)o).properties().length+")";
        if(o instanceof String)return "String \""+o+"\"";
        if(o instanceof Integer)return "int "+(int)o+" ("+hex((int)o)+")";
        if(o instanceof Boolean)return "boolean "+(boolean)o;
        if(o instanceof Float)return "float "+(float)o;
        if(o instanceof Long)return "long "+(long)o+" ("+hex((long)o)+")";
        if(o instanceof Double)return "double "+(double)o;
        if(o instanceof ConfigList)return "ConfigList (size="+((ConfigList)o).size()+")";
        if(o instanceof ConfigNumberList)return "ConfigNumberList (size="+((ConfigNumberList)o).size()+")";
        if(o instanceof Byte)return "byte "+(byte)o+" ("+hex((byte)o)+")";
        if(o instanceof Short)return "short "+(short)o+" ("+hex((short)o)+")";
        return "Unknown";
    }
    private String hex(byte b){
        return hex(b, 2);
    }
    private String hex(short s){
        return hex(s, 4);
    }
    private String hex(int i){
        return hex(i, 8);
    }
    private String hex(long l){
        return hex(l, 16);
    }
    private String hex(long l, int len){
        String s = Long.toHexString(l).toUpperCase();
        while(s.length()<len)s = "0"+s;
        return s;
    }
    private String indent(Stack<String> path){
        String str = "";
        for(String s : path){
            str+=" ";
        }
        return str;
    }
    private Component getNCPFComponent(Stack<String> path, Object val){
        String text = "";
        if(path.size()==1){
            String s = path.get(0);
            int i = Integer.parseInt(s.substring(1, s.length()-1));
            text = "Multiblock "+(i-1);
            if(i==0)text = "Header";
            if(i==1)text = "Configuration";
        }
        return new Label(text).setInset(0).alignLeft();
    }
    @Override
    public synchronized void onCharTyped(char c){
        super.onCharTyped(c);
    }
    @Override
    public synchronized void onCharTypedWithModifiers(char c, int mods){
        super.onCharTypedWithModifiers(c, mods);
    }
    @Override
    public synchronized void onCursorEnteredWindow(){
        super.onCursorEnteredWindow();
    }
    @Override
    public synchronized void onCursorExitedWindow(){
        super.onCursorExitedWindow();
    }
    @Override
    public synchronized void onCursorMoved(double xpos, double ypos){
        super.onCursorMoved(xpos, ypos);
    }
    @Override
    public synchronized void onCursorEntered(){
        super.onCursorEntered();
    }
    @Override
    public synchronized void onCursorExited(){
        super.onCursorExited();
    }
    @Override
    public synchronized void onFilesDropped(String[] files){
        super.onFilesDropped(files);
    }
    @Override
    public synchronized void onKeyEvent(int key, int scancode, int action, int mods){
        super.onKeyEvent(key, scancode, action, mods);
    }
    @Override
    public synchronized void onMouseButton(double x, double y, int button, int action, int mods){
        super.onMouseButton(x, y, button, action, mods);
    }
    @Override
    public synchronized void onFocusGained(){
        super.onFocusGained();
    }
    @Override
    public synchronized void onFocusLost(){
        super.onFocusLost();
    }
    @Override
    public synchronized boolean onScroll(double dx, double dy){
        return super.onScroll(dx, dy);
    }
    @Override
    public synchronized void onWindowClosed(){
        super.onWindowClosed();
    }
    @Override
    public synchronized void onWindowFocusGained(){
        super.onWindowFocusGained();
    }
    @Override
    public synchronized void onWindowFocusLost(){
        super.onWindowFocusLost();
    }
    @Override
    public synchronized void onWindowIconified(){
        super.onWindowIconified();
    }
    @Override
    public synchronized void onWindowUniconified(){
        super.onWindowUniconified();
    }
    @Override
    public synchronized void onWindowMaximized(){
        super.onWindowMaximized();
    }
    @Override
    public synchronized void onWindowUnmaximized(){
        super.onWindowUnmaximized();
    }
    @Override
    public synchronized void onWindowMoved(int xpos, int ypos){
        super.onWindowMoved(xpos, ypos);
    }
    @Override
    public synchronized void render3d(double deltaTime){
        super.render3d(deltaTime);
    }
    private String str(Stack<String> path){
        String str = "";
        for(String s : path){
            if(!str.isEmpty())s+=".";
            str+=s;
        }
        return str;
    }
}