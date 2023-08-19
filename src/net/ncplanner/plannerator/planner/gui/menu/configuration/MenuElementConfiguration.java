package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFModuleContainer;
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
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import static net.ncplanner.plannerator.ncpf.element.NCPFSettingsElement.Type;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.IconButton;
import net.ncplanner.plannerator.planner.gui.menu.component.LayoutPanel;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.BorderLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.LayeredLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListButtonsLayout;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuInputDialog;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFSettingsModule;
public class MenuElementConfiguration extends ConfigurationMenu{
    private final NCPFConfiguration config;
    private final NCPFElement element;
    public MenuElementConfiguration(Menu parent, NCPFConfigurationContainer configuration, NCPFConfiguration config, NCPFElement element){
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
                gui.open(new MenuElementConfiguration(parent, configuration, config, element));
            }).open();
        }));
        definitionHeader.add(new TextBox(element.getOrCreateModule(DisplayNameModule::new).displayName, true, "Display Name").onChange((t) -> {
            element.getOrCreateModule(DisplayNameModule::new).displayName = t;
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
                onOpen(() -> {
                    blockstateList.components.clear();
                    for(String key : map.keySet()){
                        LayeredLayout stateComp = blockstateList.add(new LayeredLayout());
                        stateComp.height = 48;
                        stateComp.add(new Label(key+"="+map.get(key).toString(), true));
                        ListButtonsLayout buttons = stateComp.add(new ListButtonsLayout());
                        buttons.add(new IconButton("delete", true).addAction(() -> {
                            map.remove(key);
                            setState.accept(map);
                            refresh();
                        }));
                    }
                });
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
                            refresh();
                        }, true).addButton("Cancel").open();
                    }).addButton("Cancel").open();
                }));
            }else definitionFields.add(new Panel());
        }else definitionList.add(new Panel());
        GridLayout settings = add(new GridLayout(1, 0));
        GridLayout moduleLists = settings.add(new GridLayout(0, 1));
        BorderLayout functionListContainer = moduleLists.add(new BorderLayout());
        functionListContainer.add(new Label("Functions", true), BorderLayout.TOP, 48);
        SingleColumnList functionList = functionListContainer.add(new SingleColumnList(16), BorderLayout.CENTER);
        BorderLayout otherListContainer = moduleLists.add(new BorderLayout());
        otherListContainer.add(new Label("Other Modules", true), BorderLayout.TOP, 48);
        SingleColumnList otherList = otherListContainer.add(new SingleColumnList(16), BorderLayout.CENTER);
        onOpen(() -> {
            functionList.components.clear();
            otherList.components.clear();
            ArrayList<Supplier<NCPFModule>> possibleFunctions = new ArrayList<>(Arrays.asList(element.getPreferredModules()));
            ArrayList<Supplier<NCPFModule>> possibleModules = new ArrayList<>();
            for(Supplier<NCPFModule> s : NCPFModuleContainer.recognizedModules.values()){
                if(s.get().name.equals(new TextureModule().name))continue;
                if(s.get().name.equals(new DisplayNameModule().name))continue;
                if(s.get() instanceof ElementModule)possibleModules.add(s);
            }
        
            ArrayList<NCPFModule> functions = new ArrayList<>();
            ArrayList<NCPFModule> modules = new ArrayList<>();

            for(Iterator<Supplier<NCPFModule>> it = possibleFunctions.iterator(); it.hasNext();){
                Supplier<NCPFModule> func = it.next();
                NCPFModule module = element.getModule(func);
                if(module!=null){
                    functions.add(module);
                    it.remove();
                }
            }
            for(Iterator<Supplier<NCPFModule>> it = possibleModules.iterator(); it.hasNext();){
                Supplier<NCPFModule> mod = it.next();
                NCPFModule module = element.getModule(mod);
                if(module!=null){
                    modules.add(module);
                    it.remove();
                }
            }

            for(NCPFModule module : element.modules.modules.values()){
                if(module.name.equals(new TextureModule().name))continue;
                if(module.name.equals(new DisplayNameModule().name))continue;
                if(modules.contains(module)||functions.contains(module))continue;//already visited
                if(module instanceof ElementModule){
                    modules.add(module);
                    for(Iterator<Supplier<NCPFModule>> it = possibleModules.iterator(); it.hasNext();){
                        Supplier<NCPFModule> mod = it.next();
                        if(module.name.equals(mod.get().name))it.remove();
                    }
                }else{
                    functions.add(module);
                    for(Iterator<Supplier<NCPFModule>> it = possibleFunctions.iterator(); it.hasNext();){
                        Supplier<NCPFModule> func = it.next();
                        if(module.name.equals(func.get().name))it.remove();
                    }
                }
            }

            if(!possibleFunctions.isEmpty()||!functions.isEmpty()){
                for(NCPFModule mod : functions)functionList.add(makeModuleComponent(element, mod));
                for(Supplier<NCPFModule> s : possibleFunctions)functionList.add(makePossibleModuleComponent(element, s));
            }
            if(!possibleModules.isEmpty()||!modules.isEmpty()){
                for(NCPFModule mod : modules)otherList.add(makeModuleComponent(element, mod));
                for(Supplier<NCPFModule> s : possibleModules)otherList.add(makePossibleModuleComponent(element, s));
            }
        });
//        GridLayout lists = settings.add(new GridLayout(0, 1));//block recipes
        //TODO add lists
    }
    private Component makeModuleComponent(NCPFElement element, NCPFModule module){
        ListLayout list = new ListLayout(48);
        LayoutPanel panel = list.add(new LayoutPanel(new LayeredLayout()));
        panel.add(new Label(module.getFriendlyName()));
        ListButtonsLayout buttons = panel.add(new ListButtonsLayout());
        buttons.add(new IconButton("delete", true).addAction(() -> {
            element.removeModule(module);
            refresh();
        }));
        if(module instanceof NCPFSettingsModule){
            NCPFSettingsModule mod = (NCPFSettingsModule)module;
            if(!mod.settings.isEmpty()){
                GridLayout grid = list.add(new GridLayout(0, 1));
                for(String setting : mod.settings){
                    switch(mod.types.get(setting)){
                        case BOOLEAN:
                            grid.add(new ToggleBox(mod.titles.get(setting), ((Supplier<Boolean>)mod.gets.get(setting)).get(), true).onChange((t) -> {
                                mod.sets.get(setting).accept(t);
                            }));
                            break;
                        case INTEGER:
                            grid.add(new TextBox(mod.gets.get(setting).get()+"", true, mod.titles.get(setting)).onChange((t) -> {
                                mod.sets.get(setting).accept(Integer.valueOf(t));
                            }));
                            break;
                        case FLOAT:
                            grid.add(new TextBox(mod.gets.get(setting).get()+"", true, mod.titles.get(setting)).onChange((t) -> {
                                mod.sets.get(setting).accept(Float.valueOf(t));
                            }));
                            break;
                        case DOUBLE:
                            grid.add(new TextBox(mod.gets.get(setting).get()+"", true, mod.titles.get(setting)).onChange((t) -> {
                                mod.sets.get(setting).accept(Double.valueOf(t));
                            }));
                            break;
                        case STRING_LIST:
                            List<String> lst = ((Supplier<List<String>>)mod.gets.get(setting)).get();
                            list.add(new Label(mod.titles.get(setting), true));
                            for(String str : lst){
                                LayeredLayout strComp = list.add(new LayeredLayout());
                                strComp.add(new Label(str, false));
                                ListButtonsLayout btns = strComp.add(new ListButtonsLayout());
                                btns.add(new IconButton("delete", true).addAction(() -> {
                                    lst.remove(str);
                                    mod.sets.get(setting).accept(lst);
                                    refresh();
                                }));
                            }
                            list.add(new Button("Add", true).addAction(() -> {
                                new MenuInputDialog(gui, this, "", "Add String").addButton("OK", (str) -> {
                                    if(!str.isBlank())lst.add(str);
                                    mod.sets.get(setting).accept(lst);
                                    refresh();
                                }, true).addButton("Cancel").open();
                            }));
                            break;

                        default:
                            throw new UnsupportedOperationException("Unrecognized setting type: "+mod.types.get(setting));
                    }
                }
                if(grid.components.isEmpty())list.components.remove(grid);
            }
        }
        if(module instanceof BlockRulesModule){
            list.add(new Label("TODO block rules"));
        }
        return list;
    }
    private Component makePossibleModuleComponent(NCPFElement element, Supplier<NCPFModule> mod){
        NCPFModule module = mod.get();
        ListLayout list = new ListLayout(48);
        LayoutPanel panel = list.add(new LayoutPanel(new LayeredLayout()));
        panel.add(new Label(module.getFriendlyName(), true));
        ListButtonsLayout buttons = panel.add(new ListButtonsLayout());
        buttons.add(new Button("+", true).addAction(() -> {
            element.setModule(module);
            refresh();
        }));
        return list;
    }
}