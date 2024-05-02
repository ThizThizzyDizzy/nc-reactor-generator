package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFSettingsElement;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.IconButton;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.Panel;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.BorderLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.LayeredLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListButtonsLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SplitLayout;
public class MenuModifyElementDefinition extends MenuDialog{
    private boolean refreshNeeded;
    private final ArrayList<Runnable> onOpen = new ArrayList<>();
    public MenuModifyElementDefinition(GUI gui, Menu parent, NCPFElementDefinition element, Runnable onConfirm, Runnable onCancel){
        super(gui, parent);
        addButton("Cancel", () -> {
            if(onCancel!=null)onCancel.run();
        }, true);
        addButton("Done", ()->{
            onConfirm.run();
        }, true);
        SplitLayout definitionList = setContent(new SplitLayout(SplitLayout.Y_AXIS, 0, 48, 0));
        definitionList.width = 500;
        definitionList.height = 200;
        definitionList.add(new Label(element.getTypeName()));
        if(element instanceof NCPFSettingsElement){//TODO this is STILL a mess, you just copy-pasted the other mess x.x
            NCPFSettingsElement def = (NCPFSettingsElement)element;
            String blockstate = null;
            String metadata = null;
            String elementList = null;
            for(String key : def.types.keySet()){
                NCPFSettingsElement.Type type = def.types.get(key);
                if(type==NCPFSettingsElement.Type.METADATA)metadata = key;
                if(type==NCPFSettingsElement.Type.BLOCKSTATE)blockstate = key;
                if(type==NCPFSettingsElement.Type.ELEMENT_LIST)elementList = key;
            }
            SplitLayout definitionFields = definitionList.add(new SplitLayout(SplitLayout.X_AXIS, blockstate==null?1:0.7f));
            ListLayout defFields = definitionFields.add(new ListLayout(48));
            for(String setting : def.settings){
                if(def.types.get(setting).special)continue;
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
            if(defFields.components.isEmpty()&&elementList!=null)definitionFields.components.remove(defFields);
            //this is broken and I don't care to fix it because you shouldn't be nesting lists or blockstates in lists anyway x.x
            if(elementList!=null){
                Supplier<ArrayList<NCPFElementDefinition>> getState = def.gets.get(elementList);
                Consumer<ArrayList<NCPFElementDefinition>> setState = def.sets.get(elementList);
                BorderLayout elementsPanel = definitionFields.add(new BorderLayout());
                elementsPanel.add(new Label("Elements", true), BorderLayout.TOP, 40);
                SingleColumnList elementsList = elementsPanel.add(new SingleColumnList(16), BorderLayout.CENTER);
                ArrayList<NCPFElementDefinition> list = getState.get();
                onOpen(() -> {
                    elementsList.components.clear();
                    for(NCPFElementDefinition elem : list){
                        LayeredLayout stateComp = elementsList.add(new LayeredLayout());
                        stateComp.height = 48;
                        stateComp.add(new Label(elem.toString(), true));
                        ListButtonsLayout buttons = stateComp.add(new ListButtonsLayout());
                        buttons.add(new IconButton("delete", true).addAction(() -> {
                            list.remove(elem);
                            setState.accept(list);
                            refresh();
                        }));
                        buttons.add(new IconButton("pencil", true).addAction(() -> {
                            new MenuModifyElementDefinition(gui, this, elem, ()->{
                                refresh();
                            },null).open();
                        }));
                    }
                });
                GridLayout buttons = elementsPanel.add(new GridLayout(0, 1), BorderLayout.BOTTOM, 40);
                buttons.add(new Button("Add Element", true).addAction(() -> {
                    new MenuPickElementDefinition(gui, this, (newDef) -> {
                        new MenuModifyElementDefinition(gui, this, newDef, ()->{
                            list.add(newDef);
                            setState.accept(list);
                            refresh();
                        }, null).open();
                    }).open();
                }));
            };
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
    }
    @Override
    public void onOpened(){
        onOpen.forEach(Runnable::run);
    }
    public void onOpen(Runnable r){
        onOpen.add(r);
    }
    @Override
    public void render2d(double deltaTime){
        if(refreshNeeded)onOpened();
        refreshNeeded = false;
        super.render2d(deltaTime);
    }
    public void refresh(){
        refreshNeeded = true;
    }
}
