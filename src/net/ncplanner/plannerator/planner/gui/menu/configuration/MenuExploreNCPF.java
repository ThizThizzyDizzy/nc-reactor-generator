package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.config2.ConfigNumberList;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.IBlockType;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.file.reader.NCPF11Reader;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.ProgressBar;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SingleColumnGridLayout;
public class MenuExploreNCPF extends ConfigurationMenu{
    private final SingleColumnList list;
    private final ProgressBar progress;
    private final ArrayList<Config> configs;
    private boolean consolidate = false;
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
            ArrayList<String> path = new ArrayList<>();
            for(int i = 0; i<configs.size(); i++){
                task.name = "Loading file... ("+(i+1)+"/"+configs.size()+")";
                Task task = subtasks.get(i);
                path.add("["+i+"]");
                Config c = configs.get(i);
                add(new Label(str(c)).setInset(0).alignLeft(), getNCPFComponent(path, c));
                addComponents(c, path, task);
                path.remove(path.size()-1);
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
            for(int i = 0; i<=header.getInt("count"); i++)configs.add(Config.newConfig(stream).load());
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
    private void addComponents(Config config, ArrayList<String> path, Task task){
        String pre = task.name;
        String[] properties = config.properties();
        for(int i = 0; i<properties.length; i++){
            task.name = pre+" ("+(i+1)+"/"+properties.length+")";
            task.setProgress(i/(double)properties.length);
            String key = properties[i];
            String indent = indent(path);
            path.add(key);
            Object val = config.get(key);
            int listIndex = list.components.size();
            add(new Label(indent+key+": "+str(val)).setInset(0).alignLeft(), getNCPFComponent(path, val));
            boolean consolidate = this.consolidate;
            this.consolidate = false;
            if(val instanceof Config)addComponents((Config)val, path, task.addSubtask("Loading Config "+str(path)));
            if(val instanceof ConfigList)addComponents((ConfigList)val, path, task.addSubtask("Loading ConfigList "+str(path)));
//            if(val instanceof ConfigNumberList)addComponents((ConfigNumberList)val, path, task.addSubtask("Loading ConfigNumberList "+str(path)));
            if(consolidate){
                ArrayList<Component> comps = new ArrayList<>();
                for(int j = listIndex+1; j<list.components.size();){
                    comps.add(list.components.remove(j));
                }
                SingleColumnGridLayout gl = new SingleColumnGridLayout(20){
                    @Override
                    public void onFocusGained(){
                        addAll(comps);
                    }
                    @Override
                    public void onFocusLost(){
                        for(int j = 1; j<components.size();){
                            components.remove(j);
                        }
                    }
                };
                gl.add(list.components.remove(listIndex));
                list.add(gl);
            }
            path.remove(path.size()-1);
        }
        task.finish();
    }
    private void addComponents(ConfigList lst, ArrayList<String> path, Task task){
        String pre = task.name;
        for(int i = 0; i<lst.size(); i++){
            task.name = pre+" ("+(i+1)+"/"+lst.size()+")";
            task.setProgress(i/(double)lst.size());
            String indent = indent(path);
            path.add("["+i+"]");
            Object val = lst.get(i);
            int listIndex = list.components.size();
            add(new Label(indent+i+": "+str(val)).setInset(0).alignLeft(), getNCPFComponent(path, val));
            boolean consolidate = this.consolidate;
            this.consolidate = false;
            if(val instanceof Config)addComponents((Config)val, path, task.addSubtask("Loading Config"));
            if(val instanceof ConfigList)addComponents((ConfigList)val, path, task.addSubtask("Loading ConfigList"));
//            if(val instanceof ConfigNumberList)addComponents((ConfigNumberList)val, path, task.addSubtask("Loading ConfigNumberList"));
            if(consolidate){
                ArrayList<Component> comps = new ArrayList<>();
                for(int j = listIndex+1; j<list.components.size();){
                    comps.add(list.components.remove(j));
                }
                SingleColumnGridLayout gl = new SingleColumnGridLayout(20){
                    @Override
                    public void onFocusGained(){
                        addAll(comps);
                    }
                    @Override
                    public void onFocusLost(){
                        for(int j = 1; j<components.size();){
                            components.remove(j);
                        }
                    }
                };
                gl.add(list.components.remove(listIndex));
                list.add(gl);
            }
            path.remove(path.size()-1);
        }
        task.finish();
    }
    private void addComponents(ConfigNumberList list, ArrayList<String> path, Task task){
        String pre = task.name;
        for(int i = 0; i<list.size(); i++){
            task.name = pre+" ("+(i+1)+"/"+list.size()+")";
            task.setProgress(i/(double)list.size());
            String indent = indent(path);
            path.add("["+i+"]");
            long val = list.get(i);
            add(new Label(indent+i+": "+val).setInset(0).alignLeft(), getNCPFComponent(path, val));
            path.remove(path.size()-1);
        }
        task.finish();
        consolidate = true;
    }
    private String str(Object o){
        if(o instanceof Config)return "Config (size="+((Config)o).properties().length+")";
        if(o instanceof String)return "\""+o+"\"";
        if(o instanceof Integer)return (int)o+" ("+hex((int)o)+")";
        if(o instanceof Boolean)return ""+(boolean)o;
        if(o instanceof Float)return (float)o+"f";
        if(o instanceof Long)return (long)o+" ("+hex((long)o)+")";
        if(o instanceof Double)return (double)o+"d";
        if(o instanceof ConfigList)return "ConfigList (size="+((ConfigList)o).size()+")";
        if(o instanceof ConfigNumberList)return "ConfigNumberList (size="+((ConfigNumberList)o).size()+")";
        if(o instanceof Byte)return (byte)o+" ("+hex((byte)o)+")";
        if(o instanceof Short)return (short)o+" ("+hex((short)o)+")";
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
    private String indent(ArrayList<String> path){
        String str = "";
        for(String s : path){
            str+=" ";
        }
        return str;
    }
    private Component getNCPFComponent(ArrayList<String> path, Object val){
        String text = "";
        ArrayList<Image> textures = new ArrayList<>();
        int whichConfig = Integer.parseInt(path.get(0).substring(1, path.get(0).length()-1));
        switch(whichConfig){
            case 0:
                if(path.size()==1)text = "Header";
                else{
                    switch(path.get(1)){
                        case "version":
                            text = "NCPF "+val;
                            break;
                        case "count":
                            text = val+" Multiblocks";
                            break;
                    }
                }
                break;
            case 1:
                if(path.size()==1){
                    if(val instanceof Config){
                        Config c = (Config)val;
                        String name = "";
                        Object n = c.get("name");
                        if(n!=null)name = " "+n.toString();
                        String vers = "";
                        Object v = c.get("version");
                        if(v!=null)vers = " "+v.toString();
                        String uvers = "";
                        Object uv = c.get("underhaulVersion");
                        if(uv!=null)uvers = " | "+uv.toString();
                        text = "Configuration:"+name+vers+uvers;
                        consolidate = true;
                    }else text = "Configuration";
                }
                else{
                    String s = str(path).split("\\.", 2)[1];
                    if(s.matches(".*\\.partial")&&Objects.equals(val, true))text = "Partial";
                    if(s.matches(".*\\.addon")&&Objects.equals(val, true))text = "Addon";
                    if(s.matches("underhaul\\.fissionSFR")){
                        text = "Underhaul SFR Configuration";
                        consolidate = true;
                    }
                    if(s.matches("overhaul\\.fissionSFR")){
                        text = "Overhaul SFR Configuration";
                        consolidate = true;
                    }
                    if(s.matches("overhaul\\.fissionMSR")){
                        text = "Overhaul MSR Configuration";
                        consolidate = true;
                    }
                    if(s.matches("overhaul\\.turbine")){
                        text = "Overhaul Turbine Configuration";
                        consolidate = true;
                    }
                    if(s.matches("overhaul\\.fusion")){
                        text = "Overhaul Fusion TEST Configuration";
                        consolidate = true;
                    }
                    if(s.matches(".*\\.(display|legacy)?[Nn]ame")||s.equals("name"))text = val+"";
                    if(s.matches(".*addons\\.\\[\\d+\\]")){
                        if(val instanceof Config){
                            Config c = (Config)val;
                            String name = "";
                            Object n = c.get("name");
                            if(n!=null)name = " "+n.toString();
                            text = "Addon:"+name;
                            consolidate = true;
                        }else text = "Invalid Addon!";
                    }
                    if(s.matches(".*.*\\.\\w*[tT]exture")){
                        if(val instanceof ConfigNumberList){
                            text = ((ConfigNumberList)val).get(0)+"x"+((ConfigNumberList)val).get(0);
                            textures.add(loadNCPFTexture((ConfigNumberList)val));
                        }else text = "Invalid type (Expected ConfigNumbeRList)";
                    }
                    if(s.matches(".*\\.blocks.\\[\\d+\\]")){
                        if(val instanceof Config){
                            Config c = (Config)val;
                            consolidate = true;
                            String name = "";
                            Object n = c.get("name");
                            if(n!=null)name = " "+n.toString();
                            n = c.get("displayName");
                            if(n!=null)name = " "+n.toString();
                            Image tex = null;
                            Object t = c.get("texture");
                            if(t instanceof ConfigNumberList){
                                tex = loadNCPFTexture((ConfigNumberList)t);
                            }
                            text = "Block:"+name;
                            if(tex!=null)textures.add(tex);
                        }else text = "Invalid type for Block!";
                    }
                    if(s.matches(".*\\.blocks.*\\.port")){
                        if(val instanceof Config){
                            Config c = (Config)val;
                            consolidate = true;
                            Image tex = null;
                            Object t = c.get("inputTexture");
                            if(t instanceof ConfigNumberList){
                                tex = loadNCPFTexture((ConfigNumberList)t);
                            }
                            if(tex!=null)textures.add(tex);
                            tex = null;
                            t = c.get("outputTexture");
                            if(t instanceof ConfigNumberList){
                                tex = loadNCPFTexture((ConfigNumberList)t);
                            }
                            if(tex!=null)textures.add(tex);
                            text = "Port";
                        }else text = "Invalid type for Block!";
                    }
                    if(s.matches(".*underhaul\\.fissionSFR.*\\.fuels.\\[\\d+\\]")){
                        if(val instanceof Config){
                            Config c = (Config)val;
                            consolidate = true;
                            String name = "";
                            Object n = c.get("name");
                            if(n!=null)name = " "+n.toString();
                            n = c.get("displayName");
                            if(n!=null)name = " "+n.toString();
                            Image tex = null;
                            Object t = c.get("texture");
                            if(t instanceof ConfigNumberList){
                                tex = loadNCPFTexture((ConfigNumberList)t);
                            }
                            text = "Fuel:"+name;
                            if(tex!=null)textures.add(tex);
                        }else text = "Invalid type for Fuel!";
                    }
                    if(s.matches(".*\\.blocks\\..*\\.recipes\\.\\[\\d+\\]")){
                        if(val instanceof Config){
                            Config c = (Config)val;
                            Config c2 = c;
                            consolidate = true;
                            String name = "";
                            Object o = c.get("output");
                            if(o instanceof Config)c2 = (Config)o;
                            o = c.get("input");
                            if(o instanceof Config)c = (Config)o;
                            Object n = c.get("name");
                            if(n!=null)name = " "+n.toString();
                            n = c.get("displayName");
                            if(n!=null)name = " "+n.toString();
                            Image tex = null;
                            Object t = c.get("texture");
                            if(t instanceof ConfigNumberList){
                                tex = loadNCPFTexture((ConfigNumberList)t);
                            }
                            Image tex2 = null;
                            Object t2 = c2.get("texture");
                            if(t2 instanceof ConfigNumberList){
                                tex2 = loadNCPFTexture((ConfigNumberList)t2);
                            }
                            text = "Block Recipe:"+name;
                            if(tex!=null)textures.add(tex);
                            if(tex2!=null)textures.add(tex2);
                        }else text = "Invalid type for Block Recipe!";
                    }
                    if(s.matches(".*\\.coolantRecipes\\.\\[\\d+\\]")){
                        if(val instanceof Config){
                            Config c = (Config)val;
                            Config c2 = c;
                            consolidate = true;
                            String name = "";
                            Object o = c.get("output");
                            if(o instanceof Config)c2 = (Config)o;
                            o = c.get("input");
                            if(o instanceof Config)c = (Config)o;
                            Object n = c.get("name");
                            if(n!=null)name = " "+n.toString();
                            n = c.get("displayName");
                            if(n!=null)name = " "+n.toString();
                            Image tex = null;
                            Object t = c.get("texture");
                            if(t instanceof ConfigNumberList){
                                tex = loadNCPFTexture((ConfigNumberList)t);
                            }
                            Image tex2 = null;
                            Object t2 = c2.get("texture");
                            if(t2 instanceof ConfigNumberList){
                                tex2 = loadNCPFTexture((ConfigNumberList)t2);
                            }
                            text = "Coolant Recipe:"+name;
                            if(tex!=null)textures.add(tex);
                            if(tex2!=null)textures.add(tex2);
                        }else text = "Invalid type for Coolant Recipe!";
                    }
                    if(s.matches(".*\\.turbine\\.recipes\\.\\[\\d+\\]")){
                        if(val instanceof Config){
                            Config c = (Config)val;
                            Config c2 = c;
                            consolidate = true;
                            String name = "";
                            Object o = c.get("output");
                            if(o instanceof Config)c2 = (Config)o;
                            o = c.get("input");
                            if(o instanceof Config)c = (Config)o;
                            Object n = c.get("name");
                            if(n!=null)name = " "+n.toString();
                            n = c.get("displayName");
                            if(n!=null)name = " "+n.toString();
                            Image tex = null;
                            Object t = c.get("texture");
                            if(t instanceof ConfigNumberList){
                                tex = loadNCPFTexture((ConfigNumberList)t);
                            }
                            Image tex2 = null;
                            Object t2 = c2.get("texture");
                            if(t2 instanceof ConfigNumberList){
                                tex2 = loadNCPFTexture((ConfigNumberList)t2);
                            }
                            text = "Turbine Recipe:"+name;
                            if(tex!=null)textures.add(tex);
                            if(tex2!=null)textures.add(tex2);
                        }else text = "Invalid type for Turbine Recipe!";
                    }
                    if(s.matches(".*\\.\\w*ecipes\\w*\\.\\[\\d+\\].input")){
                        if(val instanceof Config){
                            Config c = (Config)val;
                            consolidate = true;
                            String name = "";
                            Object n = c.get("name");
                            if(n!=null)name = " "+n.toString();
                            n = c.get("displayName");
                            if(n!=null)name = " "+n.toString();
                            Image tex = null;
                            Object t = c.get("texture");
                            if(t instanceof ConfigNumberList){
                                tex = loadNCPFTexture((ConfigNumberList)t);
                            }
                            text = "Input:"+name;
                            if(tex!=null)textures.add(tex);
                        }else text = "Invalid type for Input!";
                    }
                    if(s.matches(".*\\.\\w*ecipes\\w*\\.\\[\\d+\\].output")){
                        if(val instanceof Config){
                            Config c = (Config)val;
                            consolidate = true;
                            String name = "";
                            Object n = c.get("name");
                            if(n!=null)name = " "+n.toString();
                            n = c.get("displayName");
                            if(n!=null)name = " "+n.toString();
                            Image tex = null;
                            Object t = c.get("texture");
                            if(t instanceof ConfigNumberList){
                                tex = loadNCPFTexture((ConfigNumberList)t);
                            }
                            text = "Output:"+name;
                            if(tex!=null)textures.add(tex);
                        }else text = "Invalid type for Output!";
                    }
                    if(s.matches(".*\\.rules.\\[\\d+\\]")){
                        if(val instanceof Config){
                            Config c = (Config)val;
                            String minmax = "";
                            String type = "";
                            String block = "";
                            if(c.hasProperty("min")&&c.hasProperty("max")){
                                minmax = " "+c.get("min")+" - "+c.get("max");
                            }
                            if(c.hasProperty("type")){
                                Object typ = c.get("type");
                                if(typ instanceof Number)type = " "+net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule.RuleType.values()[((Number)typ).intValue()].toString();
                            }
                            if(c.hasProperty("block")||c.hasProperty("blockType")){
                                Object issb = c.get("isSpecificBlock");
                                Object blok = c.get("block");
                                if(blok==null)blok = c.get("blockType");
                                if(issb instanceof Boolean){
                                    if((Boolean)issb){
                                        if(blok instanceof Number){
                                            Block bl = getBlock(s, ((Number)blok).intValue());
                                            textures.add(bl.texture);
                                            block = " "+bl.name();
                                        }
                                    }else{
                                        if(blok instanceof Number)block = " "+getBlockType(s, ((Number)blok).intValue()).toString();
                                    }
                                }
                            }
                            consolidate = true;
                            text = "Placement Rule:"+type+minmax+block;
                        }else text = "Invalid type for Placement Rule!";
                    }
                    if(s.matches(".*(rules.\\[\\d+])+\\.type")){
                        if(val instanceof Number){
                            text = net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule.RuleType.values()[((Number)val).intValue()].toString();
                        }
                    }
                    if(s.matches(".*(rules.\\[\\d+])+\\.blockType")){
                        if(val instanceof Number){
                            text = getBlockType(s, ((Number)val).intValue()).toString();
                        }
                    }
                    if(text.isEmpty())System.out.println(s);
                }
                break;
            default:
                if(path.size()==1)text = "Multiblock "+(whichConfig-1);
                break;
        }
        Label l = new Label(indent(path).substring(1)+text).setInset(0).alignLeft();
        textures.forEach(l::addImage);
        return l;
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
    private String str(ArrayList<String> path){
        String str = "";
        for(String s : path){
            if(!str.isEmpty())str+=".";
            str+=s;
        }
        return str;
    }
    private IBlockType getBlockType(String s, int type){
        if(s.contains("underhaul.fissionSFR")){
            return net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.values()[type];
        }
        if(s.contains("overhaul.fissionSFR")){
            return net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.values()[type];
        }
        if(s.contains("overhaul.fissionMSR")){
            return net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.values()[type];
        }
        if(s.contains("overhaul.turbine")){
            return net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.PlacementRule.BlockType.values()[type];
        }
        return null;
    }
    private Block getBlock(String s, int type){
        return getBlocks(s).get(type);
    }
    private ArrayList<Block> getBlocks(String s){
        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(new Block(null, "air", "Air"));
        if(s.contains("underhaul.fissionSFR")){
            ConfigList blocksList = configs.get(1).getConfig("underhaul").getConfig("fissionSFR").getConfigList("blocks");
            for(int i = 0; i<blocksList.size(); i++){
                Config c = blocksList.getConfig(i);
                blocks.add(new Block(loadNCPFTexture(c.getConfigNumberList("texture")), c.getString("name"), c.getString("displayName")));
            }
        }
        if(s.contains("overhaul.fissionSFR")){
            ArrayList<String> skips = new ArrayList<>();
            ConfigList blocksList = configs.get(1).getConfig("overhaul").getConfig("fissionSFR").getConfigList("blocks");
            for(int i = 0; i<blocksList.size(); i++){
                Config c = blocksList.getConfig(i);
                skips.add(c.getString("name"));
                blocks.add(new Block(loadNCPFTexture(c.getConfigNumberList("texture")), c.getString("name"), c.getString("displayName")));
                if(c.getConfigList("recipes", new ConfigList()).size()>0)blocks.add(new Block(loadNCPFTexture(c.getConfigNumberList("texture")), c.getConfig("port", Config.newConfig()).getString("name"), c.getConfig("port", Config.newConfig()).getString("inputDisplayName")));
            }
            ConfigList addonsList = configs.get(1).getConfigList("addons", new ConfigList());
            for(int i = 0; i<addonsList.size(); i++){
                Config cc = addonsList.getConfig(i);
                ConfigList blst = cc.getConfig("overhaul", Config.newConfig()).getConfig("fissionSFR", Config.newConfig()).getConfigList("blocks", new ConfigList());
                for(int j = 0; j<blst.size(); j++){
                    Config c = blst.getConfig(j);
                    if(skips.contains(c.getString(name)))continue;
                    blocks.add(new Block(loadNCPFTexture(c.getConfigNumberList("texture")), c.getString("name"), c.getString("displayName")));
                    if(c.getConfigList("recipes", new ConfigList()).size()>0)blocks.add(new Block(loadNCPFTexture(c.getConfigNumberList("texture")), c.getConfig("port", Config.newConfig()).getString("name"), c.getConfig("port", Config.newConfig()).getString("inputDisplayName")));
                }
            }
        }
        if(s.contains("overhaul.fissionMSR")){
            ArrayList<String> skips = new ArrayList<>();
            ConfigList blocksList = configs.get(1).getConfig("overhaul", Config.newConfig()).getConfig("fissionMSR", Config.newConfig()).getConfigList("blocks", new ConfigList());
            for(int i = 0; i<blocksList.size(); i++){
                Config c = blocksList.getConfig(i);
                skips.add(c.getString("name"));
                blocks.add(new Block(loadNCPFTexture(c.getConfigNumberList("texture")), c.getString("name"), c.getString("displayName")));
                if(c.getConfigList("recipes", new ConfigList()).size()>0)blocks.add(new Block(loadNCPFTexture(c.getConfigNumberList("texture")), c.getConfig("port", Config.newConfig()).getString("name"), c.getConfig("port", Config.newConfig()).getString("inputDisplayName")));
            }
            ConfigList addonsList = configs.get(1).getConfigList("addons", new ConfigList());
            for(int i = 0; i<addonsList.size(); i++){
                Config cc = addonsList.getConfig(i);
                ConfigList blst = cc.getConfig("overhaul", Config.newConfig()).getConfig("fissionMSR", Config.newConfig()).getConfigList("blocks", new ConfigList());
                for(int j = 0; j<blst.size(); j++){
                    Config c = blst.getConfig(j);
                    if(skips.contains(c.getString(name)))continue;
                    blocks.add(new Block(loadNCPFTexture(c.getConfigNumberList("texture")), c.getString("name"), c.getString("displayName")));
                    if(c.getConfigList("recipes", new ConfigList()).size()>0)blocks.add(new Block(loadNCPFTexture(c.getConfigNumberList("texture")), c.getConfig("port", Config.newConfig()).getString("name"), c.getConfig("port", Config.newConfig()).getString("inputDisplayName")));
                }
            }
        }
        if(s.contains("overhaul.turbine")){
            ConfigList blocksList = configs.get(1).getConfig("overhaul").getConfig("turbine").getConfigList("blocks");
            for(int i = 0; i<blocksList.size(); i++){
                Config c = blocksList.getConfig(i);
                blocks.add(new Block(loadNCPFTexture(c.getConfigNumberList("texture")), c.getString("name"), c.getString("displayName")));
            }
            ConfigList addonsList = configs.get(1).getConfigList("addons", new ConfigList());
            for(int i = 0; i<addonsList.size(); i++){
                Config cc = addonsList.getConfig(i);
                ConfigList blst = cc.getConfig("overhaul", Config.newConfig()).getConfig("turbine", Config.newConfig()).getConfigList("blocks", new ConfigList());
                for(int j = 0; j<blst.size(); j++){
                    Config c = blst.getConfig(j);
                    blocks.add(new Block(loadNCPFTexture(c.getConfigNumberList("texture")), c.getString("name"), c.getString("displayName")));
                }
            }
        }
        return blocks;
    }
    private Image loadNCPFTexture(ConfigNumberList lst){
        return lst==null?null:NCPF11Reader.loadNCPFTexture(lst);
    }
    private class Block{
        private final Image texture;
        private final String name;
        private final String displayName;
        public Block(Image texture, String name, String displayName){
            this.texture = texture;
            this.name = name;
            this.displayName = displayName;
        }
        private String name(){
            return displayName==null?name:displayName;
        }
    }
}