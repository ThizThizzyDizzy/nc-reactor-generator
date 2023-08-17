package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.TextureButton;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
public class PartConfigurationMenu extends ConfigurationMenu{//for blocks, recipes, coolant recipes, etc.
    private boolean refreshNeeded = false;
    private ArrayList<ComponentPile> piles = new ArrayList<>();
    private ArrayList<Component> currentRow = new ArrayList<>();
    private ArrayList<Component> currentColumnSection = new ArrayList<>();
    private HashMap<TextBox, Supplier<String>> open = new HashMap<>();
    private HashMap<TextBox, Consumer<String>> close = new HashMap<>();
    private HashMap<ToggleBox, Supplier<Boolean>> openb = new HashMap<>();
    private HashMap<ToggleBox, Consumer<Boolean>> closeb = new HashMap<>();
    private ArrayList<Label> labels = new ArrayList<>();
    private ArrayList<SingleColumnList> lists = new ArrayList<>();
    private ArrayList<Button> listButtons = new ArrayList<>();
    private HashMap<Label, Supplier<String>> labelNames = new HashMap<>();
    private HashMap<SingleColumnList, Consumer<SingleColumnList>> adds = new HashMap<>();
    private ArrayList<ArrayList<SmallComponentPile>> columns = new ArrayList<>();
    private HashMap<Label, Supplier<Boolean>> labelConditions = new HashMap<>();
    public PartConfigurationMenu(GUI gui, Menu parent, Configuration configuration, String name){
        super(gui, parent, configuration, name);
    }
    protected void addSecondarySection(String tag, Supplier<Image> getTextureFunc, Consumer<Image> setTextureFunc, String nameTooltip, String displayNameTooltip, Supplier<String> getNameFunc, Supplier<String> getDisplayNameFunc, Consumer<String> setNameFunc, Consumer<String> setDisplayNameFunc){
        final String label = tag==null?"":tag+" ";
        piles.add(new ComponentPile(){
            private TextureButton texture;
            private TextBox name, displayName;
            {
                texture = add(new TextureButton(0, 0, 128, 128, tag, true, getTextureFunc, setTextureFunc));
                name = add(new TextBox(0, 0, 0, 48, "", true, label+"Name").setTooltip(nameTooltip));
                displayName = add(new TextBox(0, 0, 0, 48, "", true, label+"Display Name").setTooltip(displayNameTooltip));
            }
            @Override
            float arrange(float y){
                name.x = displayName.x = sidebar.width;
                name.width = displayName.width = gui.getWidth()-sidebar.width-texture.width;;
                texture.y = name.y = y;
                displayName.y = name.y+name.height;
                texture.x = gui.getWidth()-texture.width;
                return texture.height;
            }
            @Override
            void onOpened(){
                name.text = getNameFunc.get();
                String dName = getDisplayNameFunc.get();
                displayName.text = dName==null?"":dName;
            }
            @Override
            void onClosed(){
                setNameFunc.accept(name.text);
                setDisplayNameFunc.accept(displayName.text.trim().isEmpty()?null:displayName.text);
            }
        });
    }
    protected void addPortSection(Supplier<Image> getInputTextureFunc, Consumer<Image> setInputTextureFunc, Supplier<Image> getOutputTextureFunc, Consumer<Image> setOutputTextureFunc, String nameTooltip, String inputDisplayNameTooltip, String outputDisplayNameTooltip, Supplier<String> getNameFunc, Supplier<String> getInputDisplayNameFunc, Supplier<String> getOutputDisplayNameFunc, Consumer<String> setNameFunc, Consumer<String> setInputDisplayNameFunc, Consumer<String> setOutputDisplayNameFunc, Supplier<Boolean> conditional){
        piles.add(new ComponentPile(){
            private TextureButton inputTexture, outputTexture;
            private TextBox name, inputDisplayName, outputDisplayName;
            {
                inputTexture = add(new TextureButton(0, 0, 72, 72, "Port Input", true, getInputTextureFunc, setInputTextureFunc));
                outputTexture = add(new TextureButton(0, 0, 72, 72, "Port Output", true, getOutputTextureFunc, setOutputTextureFunc));
                name = add(new TextBox(0, 0, 0, 48, "", true, "Port Name").setTooltip(nameTooltip));
                inputDisplayName = add(new TextBox(0, 0, 0, 48, "", true, "Port Input Display Name").setTooltip(inputDisplayNameTooltip));
                outputDisplayName = add(new TextBox(0, 0, 0, 48, "", true, "Port Output Display Name").setTooltip(outputDisplayNameTooltip));
            }
            @Override
            float arrange(float y){
                inputTexture.x = outputTexture.x = sidebar.width;
                name.x = inputDisplayName.x = outputDisplayName.x = inputTexture.x+inputTexture.width;
                inputTexture.height = outputTexture.height = conditional.get()?72:0;
                name.height = inputDisplayName.height = outputDisplayName.height = conditional.get()?48:0;
                inputTexture.y = y;
                outputTexture.y = y+inputTexture.height;
                name.y = y;
                inputDisplayName.y = name.y+name.height;
                outputDisplayName.y = inputDisplayName.y+inputDisplayName.height;
                name.width = inputDisplayName.width = outputDisplayName.width = gui.getWidth()-sidebar.width-inputTexture.width;
                return inputTexture.height*2;
            }
            @Override
            void onOpened(){
                name.text = getNameFunc.get();
                String dName = getInputDisplayNameFunc.get();
                inputDisplayName.text = dName==null?"":dName;
                dName = getOutputDisplayNameFunc.get();
                outputDisplayName.text = dName==null?"":dName;
            }
            @Override
            void onClosed(){
                setNameFunc.accept(name.text);
                setInputDisplayNameFunc.accept(inputDisplayName.text.trim().isEmpty()?null:inputDisplayName.text);
                setOutputDisplayNameFunc.accept(outputDisplayName.text.trim().isEmpty()?null:outputDisplayName.text);
            }
        });
    }
    protected void finishSettingRow(){
        if(currentRow.isEmpty())return;
        piles.add(new ComponentPile() {
            private ArrayList<Component> row;
            {
                row = new ArrayList<>(currentRow);
                currentRow.clear();
            }
            @Override
            float arrange(float y){
                float w = gui.getWidth()-sidebar.width;
                float compWidth = w/row.size();
                for(int i = 0; i<row.size(); i++){
                    Component setting = row.get(i);
                    setting.y = y;
                    setting.x = sidebar.width+compWidth*i;
                    setting.width = compWidth;
                }
                return row.get(0).height;//assumes they're all the same height
            }
        });
    }
    protected TextBox addSettingInt(String title, Supplier<Integer> defaultValue, Consumer<Integer> onSet){
        return addSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Integer.parseInt(str))).setIntFilter();
    }
    protected TextBox addSettingFloat(String title, Supplier<Float> defaultValue, Consumer<Float> onSet){
        return addSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Float.parseFloat(str))).setFloatFilter();
    }
    protected TextBox addSettingDouble(String title, Supplier<Double> defaultValue, Consumer<Double> onSet){
        return addSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Double.parseDouble(str))).setDoubleFilter();
    }
    protected TextBox addSetting(String title, Supplier<String> defaultValue, Consumer<String> onSet){
        TextBox box;
        currentRow.add(add(box = new TextBox(0, 0, 0, 48, "", true, title)));
        open.put(box, defaultValue);
        close.put(box, onSet);
        return box;
    }
    protected ToggleBox addSettingBoolean(String title, Supplier<Boolean> defaultValue, Consumer<Boolean> onSet){
        ToggleBox box;
        currentRow.add(add(box = new ToggleBox(0, 0, 0, 48, title)));//TODO 36 height?
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
    protected ToggleBox addColumnSectionToggle(String title, Supplier<Boolean> defaultValue, Consumer<Boolean> onSet){
        finishSettingRow();
        if(!currentColumnSection.isEmpty())finishColumnSection();
        ToggleBox box;
        currentColumnSection.add(add(box = new ToggleBox(0, 0, 0, 48, title)));
        openb.put(box, defaultValue);
        closeb.put(box, onSet);
        return box;
    }
    protected void addColumnSectionLabel(String title, Supplier<Boolean> conditional){
        if(!currentColumnSection.isEmpty())finishColumnSection();
        Label label;
        currentColumnSection.add(add(label = new Label(0, 0, 0, 48, title)));
        labelConditions.put(label, conditional);
    }
    protected ToggleBox addColumnSubsectionToggle(String title, Supplier<Boolean> defaultValue, Consumer<Boolean> onSet){
        return addColumnToggle(title, defaultValue, onSet, true);
    }
    protected ToggleBox addColumnSettingBoolean(String title, Supplier<Boolean> defaultValue, Consumer<Boolean> onSet){
        return addColumnToggle(title, defaultValue, onSet, false);
    }
    protected ToggleBox addColumnToggle(String title, Supplier<Boolean> defaultValue, Consumer<Boolean> onSet, boolean subsection){
        ToggleBox box;
        currentColumnSection.add(add(box = new ToggleBox(0, 0, 0, 32, title)));
        if(subsection)currentColumnSection.add(null);//to mark that this collapses the next ones
        openb.put(box, defaultValue);
        closeb.put(box, onSet);
        return box;
    }
    protected TextBox addColumnSettingInt(String title, Supplier<Integer> defaultValue, Consumer<Integer> onSet){
        return addColumnSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Integer.parseInt(str))).setIntFilter();
    }
    protected TextBox addColumnSettingFloat(String title, Supplier<Float> defaultValue, Consumer<Float> onSet){
        return addColumnSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Float.parseFloat(str))).setFloatFilter();
    }
    protected TextBox addColumnSettingDouble(String title, Supplier<Double> defaultValue, Consumer<Double> onSet){
        return addColumnSetting(title, ()-> {return defaultValue.get()+"";}, (str) -> onSet.accept(Double.parseDouble(str))).setDoubleFilter();
    }
    protected TextBox addColumnSetting(String title, Supplier<String> defaultValue, Consumer<String> onSet){
        TextBox box;
        currentColumnSection.add(add(box = new TextBox(0, 0, 0, 48, "", true, title)));
        open.put(box, defaultValue);
        close.put(box, onSet);
        return box;
    }
    protected void addColumnSettingTexture(String tag, Supplier<Image> getTextureFunc, Consumer<Image> setTextureFunc){
        currentColumnSection.add(add(new TextureButton(0, 0, 96, 96, tag, true, getTextureFunc, setTextureFunc)));
    }
    protected void finishColumnSection(){
        if(currentColumnSection.isEmpty())return;
        addColumnPile(new SmallComponentPile(){
            private ArrayList<Component> section;
            private HashMap<Component, Float> defaultHeights = new HashMap<>();
            {
                section = new ArrayList<>(currentColumnSection);
                currentColumnSection.clear();
                for(Component comp : section){
                    if(comp==null)continue;
                    defaultHeights.put(comp, comp.height);
                }
            }
            @Override
            float arrange(float x, float y, float width){
                float totalHeight = 0;
                float settingHeight = 0;
                float textureHeight = 0;
                float textureWidth = 0;
                boolean mainConditional = true;
                boolean secondaryConditional = true;
                for(int i = 0; i<section.size(); i++){
                    Component comp = section.get(i);
                    if(comp==null)continue;
                    if(comp instanceof Label&&i==0){
                        Label label = (Label)comp;
                        if(labelConditions.containsKey(label)){
                            mainConditional = labelConditions.get(label).get();
                        }
                    }
                    boolean conditional = mainConditional&&secondaryConditional;
                    if(comp instanceof ToggleBox){
                        if(i==0)mainConditional = ((ToggleBox)comp).isToggledOn;
                        else{
                            if(i<section.size()-1&&section.get(i+1)==null){
                                secondaryConditional = ((ToggleBox)comp).isToggledOn;
                                conditional = mainConditional;//this toggle box should show up anyway
                            }
                        }
                    }
                    comp.height = conditional?defaultHeights.get(comp):0;
                    if(comp instanceof TextureButton){
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
            void onOpened(){
                throw new UnsupportedOperationException("Not supported yet.");
            }
            @Override
            void onClosed(){
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }
    protected void addList(Supplier<String> title, String buttonLabel, Runnable onButtonPressed, Consumer<SingleColumnList> addComponentsFunc){
        finishColumnSection();
        Label label;
        labels.add(label = add(new Label(0, 0, 0, 48, title.get())));
        labelNames.put(label, title);
        SingleColumnList list;
        lists.add(list = add(new SingleColumnList(0, label.y+label.height, 0, 0, 16)));
        listButtons.add(add(new Button(0, 0, 0, 48, buttonLabel, true, true){
            {
                addAction(onButtonPressed);
            }
        }));
        adds.put(list, addComponentsFunc);
    }
    public void doRefresh(){
        onOpened();
    }
    @Override
    public void render2d(double deltaTime){
        for(ComponentPile pile : piles)pile.tick();
        if(refreshNeeded){
            doRefresh();
            refreshNeeded = false;
        }
        float w = gui.getWidth()-sidebar.width;
        float y = 0;
        for(ComponentPile pile : piles){
            y += pile.arrange(y);
        }
        float cw = w/columns.size();
        float nextY = y;
        for(int col = 0; col<columns.size(); col++){
            float localY = y;
            ArrayList<SmallComponentPile> column = columns.get(col);
            for(SmallComponentPile pile: column){
                localY+=pile.arrange(sidebar.width+cw*col, localY, cw);
            }
            if(localY>nextY)nextY = localY;
        }
        y = nextY;
        for(int i = 0; i<lists.size(); i++){
            Label label = labels.get(i);
            SingleColumnList list = lists.get(i);
            Button button = listButtons.get(i);
            label.y = y;
            list.y = label.y+label.height;
            label.width = list.width = button.width = w/lists.size();
            label.x = list.x = button.x = sidebar.width+w/lists.size()*i;
            button.y = gui.getHeight()-button.height;
            list.height = button.y-(label.y+label.height);
        }
        super.render2d(deltaTime);
    }
    @Override
    public void onOpened(){
        for(ComponentPile pile : piles)pile.onOpened();
        for(Label label : labelNames.keySet()){
            label.text = labelNames.get(label).get();
        }
        for(SingleColumnList list : adds.keySet()){
            list.components.clear();
            adds.get(list).accept(list);
        }
        for(TextBox box : open.keySet()){
            box.text = open.get(box).get();
            if(box.text==null)box.text = "";
        }
        for(ToggleBox box : openb.keySet()){
            box.isToggledOn = openb.get(box).get();
        }
    }
    @Override
    public void onClosed(){
        for(ComponentPile pile : piles)pile.onClosed();
        for(TextBox box : close.keySet()){
            close.get(box).accept(box.text);
        }
        for(ToggleBox box : closeb.keySet()){
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
        abstract float arrange(float y);
        void onOpened(){}
        void onClosed(){}
        void tick(){}
    }
    private static abstract class SmallComponentPile{
        abstract float arrange(float x, float y, float width);
        void onOpened(){}
        void onClosed(){}
        void tick(){}
    }
}