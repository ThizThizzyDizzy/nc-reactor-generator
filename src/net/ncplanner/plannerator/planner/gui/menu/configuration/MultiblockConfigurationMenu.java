package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
public class MultiblockConfigurationMenu extends ConfigurationMenu{
    private ArrayList<ArrayList<TextBox>> settings = new ArrayList<>();
    private HashMap<TextBox, Supplier<String>> open = new HashMap<>();
    private HashMap<TextBox, Consumer<String>> close = new HashMap<>();
    private ArrayList<Label> labels = new ArrayList<>();
    private ArrayList<SingleColumnList> lists = new ArrayList<>();
    private ArrayList<Button> listButtons = new ArrayList<>();
    private HashMap<Label, Supplier<String>> labelNames = new HashMap<>();
    private HashMap<SingleColumnList, Consumer<SingleColumnList>> adds = new HashMap<>();
    private boolean refreshNeeded = false;
    public MultiblockConfigurationMenu(GUI gui, Menu parent, Configuration configuration, String name){
        super(gui, parent, configuration, name);
    }
    protected void addSettingRow(){
        settings.add(new ArrayList<>());
    }
    protected TextBox addSettingInt(String title, Supplier<Integer> defaultValue, Consumer<Integer> onSet){
        return addSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Integer.parseInt(str))).setIntFilter();
    }
    protected TextBox addSettingFloat(String title, Supplier<Float> defaultValue, Consumer<Float> onSet){
        return addSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Float.parseFloat(str))).setFloatFilter();
    }
    protected TextBox addSetting(String title, Supplier<String> defaultValue, Consumer<String> onSet){
        if(settings.isEmpty())addSettingRow();
        TextBox box = settings.size()==1?null:settings.get(settings.size()-2).get(0);
        settings.get(settings.size()-1).add(add(box = new TextBox(0, box!=null?box.y+box.height:0, 0, configuration.addon?0:48, "", true, title)));
        open.put(box, defaultValue);
        close.put(box, onSet);
        return box;
    }
    protected void addList(Supplier<String> title, String buttonLabel, Runnable onButtonPressed, Consumer<SingleColumnList> addComponentsFunc){
        addList(title, buttonLabel, onButtonPressed, addComponentsFunc, buttonLabel, onButtonPressed, false);
    }
    protected void addList(Supplier<String> title, String buttonLabel, Runnable onButtonPressed, Consumer<SingleColumnList> addComponentsFunc, String sneakyButtonLabel, Runnable onSneakyButtonPressed, boolean sneakyEnabled){
        TextBox box = settings.isEmpty()?null:settings.get(settings.size()-1).get(0);
        Label label;
        labels.add(label = add(new Label(0, box!=null?box.y+box.height:0, 0, 48, title.get())));
        labelNames.put(label, title);
        SingleColumnList list;
        lists.add(list = add(new SingleColumnList(0, label.y+label.height, 0, 0, 16)));
        listButtons.add(add(new Button(0, 0, 0, 48, buttonLabel, true, true){
            {
                addAction(() -> {
                    if(sneakyEnabled&&Core.isShiftPressed())onSneakyButtonPressed.run();
                    else onButtonPressed.run();
                });
            }
            @Override
            public void render2d(double deltaTime){
                if(sneakyEnabled&&Core.isShiftPressed())text = sneakyButtonLabel;
                else text = buttonLabel;
                super.render2d(deltaTime);
            }
        }));
        adds.put(list, addComponentsFunc);
    }
    @Override
    public void render2d(double deltaTime){
        if(refreshNeeded){
            onOpened();
            refreshNeeded = false;
        }
        float w = gui.getWidth()-sidebar.width;
        for(ArrayList<TextBox> row : settings){
            float compWidth = w/row.size();
            for(int i = 0; i<row.size(); i++){
                TextBox setting = row.get(i);
                setting.x = sidebar.width+compWidth*i;
                setting.width = compWidth;
            }
        }
        for(int i = 0; i<lists.size(); i++){
            Label label = labels.get(i);
            SingleColumnList list = lists.get(i);
            Button button = listButtons.get(i);
            label.width = list.width = button.width = w/lists.size();
            label.x = list.x = button.x = sidebar.width+w/lists.size()*i;
            button.y = gui.getHeight()-button.height;
            list.height = button.y-(label.y+label.height);
        }
        super.render2d(deltaTime);
    }
    @Override
    public void onOpened(){
        for(TextBox box : open.keySet()){
            box.text = open.get(box).get();
            if(box.text==null)box.text = "";
        }
        for(Label label : labelNames.keySet()){
            label.text = labelNames.get(label).get();
        }
        for(SingleColumnList list : adds.keySet()){
            list.components.clear();
            adds.get(list).accept(list);
        }
    }
    @Override
    public void onClosed(){
        for(TextBox box : close.keySet()){
            close.get(box).accept(box.text);
        }
    }
    public void refresh(){
        refreshNeeded = true;
    }
}