package planner.menu.configuration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import multiblock.configuration.Configuration;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MultiblockConfigurationMenu extends ConfigurationMenu{
    private ArrayList<ArrayList<MenuComponentMinimalistTextBox>> settings = new ArrayList<>();
    private HashMap<MenuComponentMinimalistTextBox, Supplier<String>> open = new HashMap<>();
    private HashMap<MenuComponentMinimalistTextBox, Consumer<String>> close = new HashMap<>();
    private ArrayList<MenuComponentLabel> labels = new ArrayList<>();
    private ArrayList<MenuComponentMinimaList> lists = new ArrayList<>();
    private ArrayList<MenuComponentMinimalistButton> listButtons = new ArrayList<>();
    private HashMap<MenuComponentLabel, Supplier<String>> labelNames = new HashMap<>();
    private HashMap<MenuComponentMinimaList, Consumer<MenuComponentMinimaList>> adds = new HashMap<>();
    private boolean refreshNeeded = false;
    public MultiblockConfigurationMenu(GUI gui, Menu parent, Configuration configuration, String name){
        super(gui, parent, configuration, name);
    }
    protected void addSettingRow(){
        settings.add(new ArrayList<>());
    }
    protected MenuComponentMinimalistTextBox addSettingInt(String title, Supplier<Integer> defaultValue, Consumer<Integer> onSet){
        return addSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Integer.parseInt(str))).setIntFilter();
    }
    protected MenuComponentMinimalistTextBox addSettingFloat(String title, Supplier<Float> defaultValue, Consumer<Float> onSet){
        return addSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Float.parseFloat(str))).setFloatFilter();
    }
    protected MenuComponentMinimalistTextBox addSetting(String title, Supplier<String> defaultValue, Consumer<String> onSet){
        if(settings.isEmpty())addSettingRow();
        MenuComponentMinimalistTextBox box = settings.size()==1?null:settings.get(settings.size()-2).get(0);
        settings.get(settings.size()-1).add(add(box = new MenuComponentMinimalistTextBox(0, box!=null?box.y+box.height:0, 0, configuration.addon?0:48, "", true, title)));
        open.put(box, defaultValue);
        close.put(box, onSet);
        return box;
    }
    protected void addList(Supplier<String> title, String buttonLabel, Runnable onButtonPressed, Consumer<MenuComponentMinimaList> addComponentsFunc){
        MenuComponentMinimalistTextBox box = settings.isEmpty()?null:settings.get(settings.size()-1).get(0);
        MenuComponentLabel label;
        labels.add(label = add(new MenuComponentLabel(0, box!=null?box.y+box.height:0, 0, 48, title.get())));
        labelNames.put(label, title);
        MenuComponentMinimaList list;
        lists.add(list = add(new MenuComponentMinimaList(0, label.y+label.height, 0, 0, 16)));
        listButtons.add(add(new MenuComponentMinimalistButton(0, 0, 0, 48, buttonLabel, true, true){
            @Override
            public void action(){
                onButtonPressed.run();
            }
        }));
        adds.put(list, addComponentsFunc);
    }
    @Override
    public void tick(){
        if(refreshNeeded){
            onGUIOpened();
            refreshNeeded = false;
        }
        super.tick();
    }
    @Override
    public void render(int millisSinceLastTick){
        double w = gui.helper.displayWidth()-sidebar.width;
        for(ArrayList<MenuComponentMinimalistTextBox> row : settings){
            double compWidth = w/row.size();
            for(int i = 0; i<row.size(); i++){
                MenuComponentMinimalistTextBox setting = row.get(i);
                setting.x = sidebar.width+compWidth*i;
                setting.width = compWidth;
            }
        }
        for(int i = 0; i<lists.size(); i++){
            MenuComponentLabel label = labels.get(i);
            MenuComponentMinimaList list = lists.get(i);
            MenuComponentMinimalistButton button = listButtons.get(i);
            label.width = list.width = button.width = w/lists.size();
            label.x = list.x = button.x = sidebar.width+w/lists.size()*i;
            button.y = gui.helper.displayHeight()-button.height;
            list.height = button.y-(label.y+label.height);
        }
        super.render(millisSinceLastTick);
    }
    @Override
    public void onGUIOpened(){
        for(MenuComponentMinimalistTextBox box : open.keySet()){
            box.text = open.get(box).get();
            if(box.text==null)box.text = "";
        }
        for(MenuComponentLabel label : labelNames.keySet()){
            label.text = labelNames.get(label).get();
        }
        for(MenuComponentMinimaList list : adds.keySet()){
            list.components.clear();
            adds.get(list).accept(list);
        }
    }
    @Override
    public void onGUIClosed(){
        for(MenuComponentMinimalistTextBox box : close.keySet()){
            close.get(box).accept(box.text);
        }
    }
    public void refresh(){
        refreshNeeded = true;
    }
}