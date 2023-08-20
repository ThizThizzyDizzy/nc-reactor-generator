package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.ncpf.NCPFModuleReference;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.BorderLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListLayout;
import net.ncplanner.plannerator.planner.gui.menu.configuration.NCPFElementComponent;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.module.AirModule;
public class MenuPickReference extends MenuDialog{
    private final BorderLayout layout;
    private final ListLayout list;
    private final GridLayout tabs;
    public MenuPickReference(Menu parent, Configuration cnfg, NCPFConfiguration config, boolean allowModules, Consumer<NCPFElementReference> onSelect){
        this(parent, cnfg, config, allowModules, onSelect, null);
    }
    public MenuPickReference(Menu parent, Configuration cnfg, NCPFConfiguration config, boolean allowModules, Consumer<NCPFElementReference> onSelect, Function<NCPFElement, Boolean> filter){
        super(parent);
        layout = setContent(new BorderLayout());
        layout.width = 580;
        layout.height = 318;
        tabs = layout.add(new GridLayout(4, 0), BorderLayout.TOP, 48);
        list = layout.add(new ListLayout(96), BorderLayout.CENTER);
        ArrayList<Supplier<NCPFModule>> modules = new ArrayList<>();
        modules.add(AirModule::new);
        for(NCPFConfiguration confg : cnfg.getConfigurations(config.name)){
            for(List<NCPFElement> elements : confg.getElements()){
                HashSet<NCPFElement> skip = new HashSet<>();
                if(filter!=null){
                    elements.forEach((t) -> {
                        if(!filter.apply(t))skip.add(t);
                    });
                }
                NCPFConfiguration parnt = cnfg.configuration.configurations.get(config.name);
                boolean hasRelevant = !elements.isEmpty();
                if(parnt!=null&&parnt!=confg){
                    hasRelevant = false;
                    for(NCPFElement elem : elements){
                        boolean foundMatch = false;
                        for(List<NCPFElement> elems : parnt.getElements()){
                            for(NCPFElement e : elems){
                                if(e.definition.matches(elem.definition)){
                                    skip.add(elem);
                                    foundMatch = true;
                                    break;
                                }
                            }
                        }
                        if(!foundMatch)hasRelevant = true;
                    }
                }
                if(!hasRelevant||skip.size()>=elements.size())continue;
                for(NCPFElement elem : elements){
                    FOR:for(Supplier<NCPFModule> module : elem.getPreferredModules()){
                        for(Supplier<NCPFModule> other : modules){
                            if(module.get().name.equals(other.get().name))continue FOR;
                        }
                        modules.add(module);
                    }
                }
                tabs.add(new Button(elements.get(0).getTitle()+"s", true).addAction(()->{
                    list.components.clear();
                    for(NCPFElement elem : elements){
                        if(skip.contains(elem))continue;
                        list.add(new NCPFElementComponent(elem).addButton(">", null, () -> {
                            close();
                            onSelect.accept(new NCPFElementReference(elem));
                        }));
                        list.componentHeight = 96;
                    }
                }));
            }
        }
        if(allowModules){
            tabs.add(new Button("Modules", true).addAction(() -> {
                list.components.clear();
                for(Supplier<NCPFModule> module : modules){
                    list.add(new NCPFElementComponent(new NCPFElement(new NCPFModuleElement(module))).addButton(">", null, () -> {
                        close();
                        onSelect.accept(new NCPFModuleReference(module));
                    }));
                    list.componentHeight = 48;
                }
            }));
        }
        addButton("Cancel");
    }
    @Override
    public void render2d(double deltaTime){
        layout.topHeight = 48*Math.max(1, (tabs.components.size()+tabs.columns-1)/tabs.columns);
        layout.height = layout.topHeight.floatValue()+list.getTotalHeight();
        super.render2d(deltaTime);
    }
}