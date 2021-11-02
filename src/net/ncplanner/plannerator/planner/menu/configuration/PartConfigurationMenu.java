package net.ncplanner.plannerator.planner.menu.configuration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentLabel;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimaList;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistButton;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistTextBox;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentTextureButton;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentToggleBox;
import simplelibrary.image.Image;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class PartConfigurationMenu extends ConfigurationMenu{//for blocks, recipes, coolant recipes, etc.
    private boolean refreshNeeded = false;
    private ArrayList<ComponentPile> piles = new ArrayList<>();
    private ArrayList<MenuComponent> currentRow = new ArrayList<>();
    private ArrayList<MenuComponent> currentColumnSection = new ArrayList<>();
    private HashMap<MenuComponentMinimalistTextBox, Supplier<String>> open = new HashMap<>();
    private HashMap<MenuComponentMinimalistTextBox, Consumer<String>> close = new HashMap<>();
    private HashMap<MenuComponentToggleBox, Supplier<Boolean>> openb = new HashMap<>();
    private HashMap<MenuComponentToggleBox, Consumer<Boolean>> closeb = new HashMap<>();
    private ArrayList<MenuComponentLabel> labels = new ArrayList<>();
    private ArrayList<MenuComponentMinimaList> lists = new ArrayList<>();
    private ArrayList<MenuComponentMinimalistButton> listButtons = new ArrayList<>();
    private HashMap<MenuComponentLabel, Supplier<String>> labelNames = new HashMap<>();
    private HashMap<MenuComponentMinimaList, Consumer<MenuComponentMinimaList>> adds = new HashMap<>();
    private ArrayList<ArrayList<SmallComponentPile>> columns = new ArrayList<>();
    private HashMap<MenuComponentLabel, Supplier<Boolean>> labelConditions = new HashMap<>();
    public PartConfigurationMenu(GUI gui, Menu parent, Configuration configuration, String name){
        super(gui, parent, configuration, name);
    }
    protected void addMainSection(String tag, Supplier<Image> getTextureFunc, Consumer<Image> setTextureFunc, String nameTooltip, String displayNameTooltip, Supplier<String> getNameFunc, Supplier<String> getDisplayNameFunc, Supplier<ArrayList<String>> getLegacyNamesFunc, Consumer<String> setNameFunc, Consumer<String> setDisplayNameFunc, Consumer<ArrayList<String>> setLegacyNamesFunc){
        final String label = tag==null?"":tag+" ";
        piles.add(new ComponentPile(){
            private MenuComponentTextureButton texture;
            private MenuComponentMinimalistTextBox name, displayName;
            private MenuComponentLabel legacyNamesLabel;
            private MenuComponentMinimaList legacyNamesList;
            {
                texture = add(new MenuComponentTextureButton(0, 0, 192, 192, tag, true, true, getTextureFunc, setTextureFunc));
                name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true, label+"Name").setTooltip(nameTooltip));
                displayName = add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true, label+"Display Name").setTooltip(displayNameTooltip));
                legacyNamesLabel = add(new MenuComponentLabel(0, 0, 0, 32, "Legacy Names", true).setTooltip("A list of old names for NCPF back-compatibility"));
                legacyNamesList = add(new MenuComponentMinimaList(0, 0, 0, 0, 16));
            }
            @Override
            double arrange(double y){
                double w = gui.helper.displayWidth()-sidebar.width-texture.width;
                texture.y = name.y = displayName.y = y;
                texture.x = sidebar.width;
                name.width = displayName.width = w/2;
                name.x = legacyNamesLabel.x = legacyNamesList.x = texture.x+texture.width;
                displayName.x = name.x+name.width;
                legacyNamesLabel.width = legacyNamesList.width = w;
                legacyNamesLabel.y = name.y+name.height;
                legacyNamesList.y = legacyNamesLabel.y+legacyNamesLabel.height;
                legacyNamesList.height = texture.height-legacyNamesLabel.height-name.height;
                return texture.height;
            }
            @Override
            void onGUIOpened(){
                name.text = getNameFunc.get();
                String dName = getDisplayNameFunc.get();
                displayName.text = dName==null?"":dName;
                legacyNamesList.components.clear();
                for(String s : getLegacyNamesFunc.get()){
                    legacyNamesList.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, s, true));
                }
            }
            @Override
            void onGUIClosed(){
                setNameFunc.accept(name.text);
                setDisplayNameFunc.accept(displayName.text.trim().isEmpty()?null:displayName.text);
                ArrayList<String> legacyNames = new ArrayList<>();
                for(MenuComponent c : legacyNamesList.components){
                    if(c instanceof MenuComponentMinimalistTextBox){
                        MenuComponentMinimalistTextBox box = (MenuComponentMinimalistTextBox)c;
                        if(box.text.trim().isEmpty())continue;
                        legacyNames.add(box.text);
                    }
                }
                setLegacyNamesFunc.accept(legacyNames);
            }
            @Override
            void tick(){
                ArrayList<MenuComponent> toRemove = new ArrayList<>();
                boolean hasEmpty = false;
                for(int i = 0; i<legacyNamesList.components.size(); i++){
                    MenuComponent comp = legacyNamesList.components.get(i);
                    if(comp instanceof MenuComponentMinimalistTextBox){
                        if(((MenuComponentMinimalistTextBox)comp).text.trim().isEmpty()){
                            if(i==legacyNamesList.components.size()-1)hasEmpty = true;
                            else toRemove.add(comp);
                        }
                    }
                }
                if(!hasEmpty)legacyNamesList.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true));
                legacyNamesList.components.removeAll(toRemove);
            }
        });
    }
    protected void addSecondarySection(String tag, Supplier<Image> getTextureFunc, Consumer<Image> setTextureFunc, String nameTooltip, String displayNameTooltip, Supplier<String> getNameFunc, Supplier<String> getDisplayNameFunc, Consumer<String> setNameFunc, Consumer<String> setDisplayNameFunc){
        final String label = tag==null?"":tag+" ";
        piles.add(new ComponentPile(){
            private MenuComponentTextureButton texture;
            private MenuComponentMinimalistTextBox name, displayName;
            {
                texture = add(new MenuComponentTextureButton(0, 0, 128, 128, tag, true, true, getTextureFunc, setTextureFunc));
                name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true, label+"Name").setTooltip(nameTooltip));
                displayName = add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true, label+"Display Name").setTooltip(displayNameTooltip));
            }
            @Override
            double arrange(double y){
                name.x = displayName.x = sidebar.width;
                name.width = displayName.width = gui.helper.displayWidth()-sidebar.width-texture.width;;
                texture.y = name.y = y;
                displayName.y = name.y+name.height;
                texture.x = gui.helper.displayWidth()-texture.width;
                return texture.height;
            }
            @Override
            void onGUIOpened(){
                name.text = getNameFunc.get();
                String dName = getDisplayNameFunc.get();
                displayName.text = dName==null?"":dName;
            }
            @Override
            void onGUIClosed(){
                setNameFunc.accept(name.text);
                setDisplayNameFunc.accept(displayName.text.trim().isEmpty()?null:displayName.text);
            }
        });
    }
    protected void addPortSection(Supplier<Image> getInputTextureFunc, Consumer<Image> setInputTextureFunc, Supplier<Image> getOutputTextureFunc, Consumer<Image> setOutputTextureFunc, String nameTooltip, String inputDisplayNameTooltip, String outputDisplayNameTooltip, Supplier<String> getNameFunc, Supplier<String> getInputDisplayNameFunc, Supplier<String> getOutputDisplayNameFunc, Consumer<String> setNameFunc, Consumer<String> setInputDisplayNameFunc, Consumer<String> setOutputDisplayNameFunc, Supplier<Boolean> conditional){
        piles.add(new ComponentPile(){
            private MenuComponentTextureButton inputTexture, outputTexture;
            private MenuComponentMinimalistTextBox name, inputDisplayName, outputDisplayName;
            {
                inputTexture = add(new MenuComponentTextureButton(0, 0, 72, 72, "Port Input", true, true, getInputTextureFunc, setInputTextureFunc));
                outputTexture = add(new MenuComponentTextureButton(0, 0, 72, 72, "Port Output", true, true, getOutputTextureFunc, setOutputTextureFunc));
                name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true, "Port Name").setTooltip(nameTooltip));
                inputDisplayName = add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true, "Port Input Display Name").setTooltip(inputDisplayNameTooltip));
                outputDisplayName = add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true, "Port Output Display Name").setTooltip(outputDisplayNameTooltip));
            }
            @Override
            double arrange(double y){
                inputTexture.x = outputTexture.x = sidebar.width;
                name.x = inputDisplayName.x = outputDisplayName.x = inputTexture.x+inputTexture.width;
                inputTexture.height = outputTexture.height = conditional.get()?72:0;
                name.height = inputDisplayName.height = outputDisplayName.height = conditional.get()?48:0;
                inputTexture.y = y;
                outputTexture.y = y+inputTexture.height;
                name.y = y;
                inputDisplayName.y = name.y+name.height;
                outputDisplayName.y = inputDisplayName.y+inputDisplayName.height;
                name.width = inputDisplayName.width = outputDisplayName.width = gui.helper.displayWidth()-sidebar.width-inputTexture.width;
                return inputTexture.height*2;
            }
            @Override
            void onGUIOpened(){
                name.text = getNameFunc.get();
                String dName = getInputDisplayNameFunc.get();
                inputDisplayName.text = dName==null?"":dName;
                dName = getOutputDisplayNameFunc.get();
                outputDisplayName.text = dName==null?"":dName;
            }
            @Override
            void onGUIClosed(){
                setNameFunc.accept(name.text);
                setInputDisplayNameFunc.accept(inputDisplayName.text.trim().isEmpty()?null:inputDisplayName.text);
                setOutputDisplayNameFunc.accept(outputDisplayName.text.trim().isEmpty()?null:outputDisplayName.text);
            }
        });
    }
    protected void finishSettingRow(){
        if(currentRow.isEmpty())return;
        piles.add(new ComponentPile() {
            private ArrayList<MenuComponent> row;
            {
                row = new ArrayList<>(currentRow);
                currentRow.clear();
            }
            @Override
            double arrange(double y){
                double w = gui.helper.displayWidth()-sidebar.width;
                double compWidth = w/row.size();
                for(int i = 0; i<row.size(); i++){
                    MenuComponent setting = row.get(i);
                    setting.y = y;
                    setting.x = sidebar.width+compWidth*i;
                    setting.width = compWidth;
                }
                return row.get(0).height;//assumes they're all the same height
            }
        });
    }
    protected MenuComponentMinimalistTextBox addSettingInt(String title, Supplier<Integer> defaultValue, Consumer<Integer> onSet){
        return addSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Integer.parseInt(str))).setIntFilter();
    }
    protected MenuComponentMinimalistTextBox addSettingFloat(String title, Supplier<Float> defaultValue, Consumer<Float> onSet){
        return addSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Float.parseFloat(str))).setFloatFilter();
    }
    protected MenuComponentMinimalistTextBox addSettingDouble(String title, Supplier<Double> defaultValue, Consumer<Double> onSet){
        return addSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Double.parseDouble(str))).setDoubleFilter();
    }
    protected MenuComponentMinimalistTextBox addSetting(String title, Supplier<String> defaultValue, Consumer<String> onSet){
        MenuComponentMinimalistTextBox box;
        currentRow.add(add(box = new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true, title)));
        open.put(box, defaultValue);
        close.put(box, onSet);
        return box;
    }
    protected MenuComponentToggleBox addSettingBoolean(String title, Supplier<Boolean> defaultValue, Consumer<Boolean> onSet){
        MenuComponentToggleBox box;
        currentRow.add(add(box = new MenuComponentToggleBox(0, 0, 0, 48, title)));//TODO 36 height?
        openb.put(box, defaultValue);
        closeb.put(box, onSet);
        return box;
    }
    protected void finishColumn(){
        finishColumnSection();
        columns.add(new ArrayList<>());
    }
    private void addColumnPile(SmallComponentPile pile){
        if(columns.isEmpty())finishColumn();
        columns.get(columns.size()-1).add(pile);
    }
    protected MenuComponentToggleBox addColumnSectionToggle(String title, Supplier<Boolean> defaultValue, Consumer<Boolean> onSet){
        finishSettingRow();
        if(!currentColumnSection.isEmpty())finishColumnSection();
        MenuComponentToggleBox box;
        currentColumnSection.add(add(box = new MenuComponentToggleBox(0, 0, 0, 48, title)));
        openb.put(box, defaultValue);
        closeb.put(box, onSet);
        return box;
    }
    protected void addColumnSectionLabel(String title, Supplier<Boolean> conditional){
        if(!currentColumnSection.isEmpty())finishColumnSection();
        MenuComponentLabel label;
        currentColumnSection.add(add(label = new MenuComponentLabel(0, 0, 0, 48, title)));
        labelConditions.put(label, conditional);
    }
    protected MenuComponentToggleBox addColumnSubsectionToggle(String title, Supplier<Boolean> defaultValue, Consumer<Boolean> onSet){
        return addColumnToggle(title, defaultValue, onSet, true);
    }
    protected MenuComponentToggleBox addColumnSettingBoolean(String title, Supplier<Boolean> defaultValue, Consumer<Boolean> onSet){
        return addColumnToggle(title, defaultValue, onSet, false);
    }
    protected MenuComponentToggleBox addColumnToggle(String title, Supplier<Boolean> defaultValue, Consumer<Boolean> onSet, boolean subsection){
        MenuComponentToggleBox box;
        currentColumnSection.add(add(box = new MenuComponentToggleBox(0, 0, 0, 32, title)));
        if(subsection)currentColumnSection.add(null);//to mark that this collapses the next ones
        openb.put(box, defaultValue);
        closeb.put(box, onSet);
        return box;
    }
    protected MenuComponentMinimalistTextBox addColumnSettingInt(String title, Supplier<Integer> defaultValue, Consumer<Integer> onSet){
        return addColumnSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Integer.parseInt(str))).setIntFilter();
    }
    protected MenuComponentMinimalistTextBox addColumnSettingFloat(String title, Supplier<Float> defaultValue, Consumer<Float> onSet){
        return addColumnSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Float.parseFloat(str))).setFloatFilter();
    }
    protected MenuComponentMinimalistTextBox addColumnSettingDouble(String title, Supplier<Double> defaultValue, Consumer<Double> onSet){
        return addColumnSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Double.parseDouble(str))).setDoubleFilter();
    }
    protected MenuComponentMinimalistTextBox addColumnSetting(String title, Supplier<String> defaultValue, Consumer<String> onSet){
        MenuComponentMinimalistTextBox box;
        currentColumnSection.add(add(box = new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true, title)));
        open.put(box, defaultValue);
        close.put(box, onSet);
        return box;
    }
    protected void addColumnSettingTexture(String tag, Supplier<Image> getTextureFunc, Consumer<Image> setTextureFunc){
        currentColumnSection.add(add(new MenuComponentTextureButton(0, 0, 96, 96, tag, true, true, getTextureFunc, setTextureFunc)));
    }
    protected void finishColumnSection(){
        if(currentColumnSection.isEmpty())return;
        addColumnPile(new SmallComponentPile(){
            private ArrayList<MenuComponent> section;
            private HashMap<MenuComponent, Double> defaultHeights = new HashMap<>();
            {
                section = new ArrayList<>(currentColumnSection);
                currentColumnSection.clear();
                for(MenuComponent comp : section){
                    if(comp==null)continue;
                    defaultHeights.put(comp, comp.height);
                }
            }
            @Override
            double arrange(double x, double y, double width){
                double totalHeight = 0;
                double settingHeight = 0;
                double textureHeight = 0;
                double textureWidth = 0;
                boolean mainConditional = true;
                boolean secondaryConditional = true;
                for(int i = 0; i<section.size(); i++){
                    MenuComponent comp = section.get(i);
                    if(comp==null)continue;
                    if(comp instanceof MenuComponentLabel&&i==0){
                        MenuComponentLabel label = (MenuComponentLabel)comp;
                        if(labelConditions.containsKey(label)){
                            mainConditional = labelConditions.get(label).get();
                        }
                    }
                    boolean conditional = mainConditional&&secondaryConditional;
                    if(comp instanceof MenuComponentToggleBox){
                        if(i==0)mainConditional = ((MenuComponentToggleBox)comp).isToggledOn;
                        else{
                            if(i<section.size()-1&&section.get(i+1)==null){
                                secondaryConditional = ((MenuComponentToggleBox)comp).isToggledOn;
                                conditional = mainConditional;//this toggle box should show up anyway
                            }
                        }
                    }
                    comp.height = conditional?defaultHeights.get(comp):0;
                    if(comp instanceof MenuComponentTextureButton){
                        comp.x = x+width-comp.width;
                        comp.y = y+textureHeight;
                        textureHeight+=comp.height;
                        textureWidth = Math.max(textureWidth, comp.width);
                    }else{
                        comp.x = x;
                        comp.y = y+settingHeight;
                        comp.width = width-textureWidth;
                        settingHeight+=comp.height;
                        if(textureWidth==0)textureHeight+=comp.height;
                    }
                }
                return totalHeight+Math.max(settingHeight, textureHeight);
            }
            @Override
            void onGUIOpened(){
                throw new UnsupportedOperationException("Not supported yet.");
            }
            @Override
            void onGUIClosed(){
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }
    protected void addList(Supplier<String> title, String buttonLabel, Runnable onButtonPressed, Consumer<MenuComponentMinimaList> addComponentsFunc){
        finishColumnSection();
        MenuComponentLabel label;
        labels.add(label = add(new MenuComponentLabel(0, 0, 0, 48, title.get())));
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
        for(ComponentPile pile : piles)pile.tick();
        if(refreshNeeded){
            doRefresh();
            refreshNeeded = false;
        }
        super.tick();
    }
    public void doRefresh(){
        onGUIOpened();
    }
    @Override
    public void render(int millisSinceLastTick){
        double w = gui.helper.displayWidth()-sidebar.width;
        double y = 0;
        for(ComponentPile pile : piles){
            y += pile.arrange(y);
        }
        double cw = w/columns.size();
        double nextY = y;
        for(int col = 0; col<columns.size(); col++){
            double localY = y;
            ArrayList<SmallComponentPile> column = columns.get(col);
            for(SmallComponentPile pile: column){
                localY+=pile.arrange(sidebar.width+cw*col, localY, cw);
            }
            if(localY>nextY)nextY = localY;
        }
        y = nextY;
        for(int i = 0; i<lists.size(); i++){
            MenuComponentLabel label = labels.get(i);
            MenuComponentMinimaList list = lists.get(i);
            MenuComponentMinimalistButton button = listButtons.get(i);
            label.y = y;
            list.y = label.y+label.height;
            label.width = list.width = button.width = w/lists.size();
            label.x = list.x = button.x = sidebar.width+w/lists.size()*i;
            button.y = gui.helper.displayHeight()-button.height;
            list.height = button.y-(label.y+label.height);
        }
        super.render(millisSinceLastTick);
    }
    @Override
    public void onGUIOpened(){
        for(ComponentPile pile : piles)pile.onGUIOpened();
        for(MenuComponentLabel label : labelNames.keySet()){
            label.text = labelNames.get(label).get();
        }
        for(MenuComponentMinimaList list : adds.keySet()){
            list.components.clear();
            adds.get(list).accept(list);
        }
        for(MenuComponentMinimalistTextBox box : open.keySet()){
            box.text = open.get(box).get();
            if(box.text==null)box.text = "";
        }
        for(MenuComponentToggleBox box : openb.keySet()){
            box.isToggledOn = openb.get(box).get();
        }
    }
    @Override
    public void onGUIClosed(){
        for(ComponentPile pile : piles)pile.onGUIClosed();
        for(MenuComponentMinimalistTextBox box : close.keySet()){
            close.get(box).accept(box.text);
        }
        for(MenuComponentToggleBox box : closeb.keySet()){
            closeb.get(box).accept(box.isToggledOn);
        }
    }
    public void refresh(){
        refreshNeeded = true;
    }
    private static abstract class ComponentPile{
        /**
         * Arrange the components
         * @param y the top of this pile
         * @return the total height of this pile
         */
        abstract double arrange(double y);
        void onGUIOpened(){}
        void onGUIClosed(){}
        void tick(){}
    }
    private static abstract class SmallComponentPile{
        abstract double arrange(double x, double y, double width);
        void onGUIOpened(){}
        void onGUIClosed(){}
        void tick(){}
    }
}