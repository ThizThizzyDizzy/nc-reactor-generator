package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.element.NCPFSettingsElement;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.Panel;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.TextureButton;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SplitLayout;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuPickElementDefinition;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import static net.ncplanner.plannerator.ncpf.element.NCPFSettingsElement.Type;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.BorderLayout;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuInputDialog;
public class ElementConfigurationMenu extends ConfigurationMenu{
    private final NCPFConfiguration config;
    private final NCPFElement element;
    public ElementConfigurationMenu(Menu parent, NCPFConfigurationContainer configuration, NCPFConfiguration config, NCPFElement element){
        super(parent, configuration, element.getDisplayName(), new SplitLayout(SplitLayout.Y_AXIS, 0, 192, 0));
        this.config = config;
        this.element = element;
        SplitLayout definition = add(new SplitLayout(SplitLayout.X_AXIS, 0, 192, 0));
        definition.add(new TextureButton(()->element.getOrCreateModule(TextureModule::new).texture, (img)->element.getOrCreateModule(TextureModule::new).texture = img));
        SplitLayout definitionList = definition.add(new SplitLayout(SplitLayout.Y_AXIS, 0, 48, 0));
        SplitLayout definitionHeader = definitionList.add(new SplitLayout(SplitLayout.X_AXIS, 0.3f));
        definitionHeader.add(new Button(element.definition.getTypeName(), true).addAction(() -> {
            new MenuPickElementDefinition(gui, this, (def) -> {
                element.definition = def;
                gui.open(new ElementConfigurationMenu(parent, configuration, config, element));
            }).open();
        }));
        definitionHeader.add(new TextBox(element.getOrCreateModule(DisplayNamesModule::new).displayName, true, "Display Name").onChange((t) -> {
            element.getOrCreateModule(DisplayNamesModule::new).displayName = t;
        }));
        if(element.definition instanceof NCPFSettingsElement){
            NCPFSettingsElement def = (NCPFSettingsElement)element.definition;
            String blockstate = null;
            String metadata = null;
            for(String key : def.types.keySet()){
                Type type = def.types.get(key);
                if(type==Type.METADATA)metadata = key;
                if(type==Type.BLOCKSTATE)blockstate = key;
            }
            SplitLayout definitionFields = definitionList.add(new SplitLayout(SplitLayout.X_AXIS, blockstate==null?1:0.7f));
            ListLayout defFields = definitionFields.add(new ListLayout(48));
            for(String setting : def.settings){
                if(def.types.get(setting)==Type.METADATA||def.types.get(setting)==Type.BLOCKSTATE)continue;
                Supplier<String> get = def.gets.get(setting);
                Consumer<String> set = def.sets.get(setting);
                TextBox box = new TextBox(get.get(), true, def.titles.get(setting)).onChange((s) -> {
                    if(s.isEmpty())set.accept(null);
                    else set.accept(s);
                });
                if(metadata!=null){
                    SplitLayout line = defFields.add(new SplitLayout(SplitLayout.X_AXIS, 0.7f));
                    Supplier<Integer> getMeta = def.gets.get(metadata);
                    Consumer<Integer> setMeta = def.sets.get(metadata);
                    line.add(box);
                    Integer meta = getMeta.get();
                    line.add(new TextBox(meta!=null?""+meta:"", true, "Metadata").setIntFilter().allowEmpty().onChange((s)->{
                        Integer val = null;
                        try{
                            val = Integer.valueOf(s);
                        }catch(NumberFormatException ex){}
                        setMeta.accept(val);
                    }));
                    metadata = null;
                }else defFields.add(box);
            }
            if(blockstate!=null){
                Supplier<HashMap<String, Object>> getState = def.gets.get(blockstate);
                Consumer<HashMap<String, Object>> setState = def.sets.get(blockstate);
                BorderLayout blockstatePanel = definitionFields.add(new BorderLayout());
                blockstatePanel.add(new Label("Blockstate", true), BorderLayout.TOP, 40);
                SingleColumnList blockstateList = blockstatePanel.add(new SingleColumnList(16), BorderLayout.CENTER);
                HashMap<String, Object> map = getState.get();
                for(String key : map.keySet()){
                    blockstateList.add(new BlockstateComponent(key, map.get(key)).addButton("delete", null, () -> {
                        map.remove(key);
                        setState.accept(map);
                        gui.open(new ElementConfigurationMenu(parent, configuration, config, element));
                    }));
                }
                GridLayout buttons = blockstatePanel.add(new GridLayout(0, 1), BorderLayout.BOTTOM, 40);
                buttons.add(new Button("Add State", true).addAction(() -> {
                    new MenuInputDialog(gui, this, "", "Key").addButton("OK", (key)->{
                        if(key.isBlank())return;
                        new MenuInputDialog(gui, this, "", "Value").addButton("OK", (val)->{
                            Object value = val;
                            try{
                                value = Integer.valueOf(val);
                            }catch(NumberFormatException ex){}
                            map.put(key, value);
                            setState.accept(map);
                            gui.open(new ElementConfigurationMenu(parent, configuration, config, element));
                        }).addButton("Cancel").open();
                    }).addButton("Cancel").open();
                }));
            }else definitionFields.add(new Panel());
        }else definitionList.add(new Panel());
        SplitLayout settings = add(new SplitLayout(SplitLayout.Y_AXIS, 0.5f));
        
        element.getPreferredModules(); //TODO something with this lol, separate block functions and other modules
        
        GridLayout moduleLists = settings.add(new GridLayout(0, 1));
        //TODO add modules
        GridLayout lists = settings.add(new GridLayout(0, 1));//block recipes
        //TODO add lists
    }
}