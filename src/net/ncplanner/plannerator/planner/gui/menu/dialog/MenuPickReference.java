package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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
import net.ncplanner.plannerator.planner.ncpf.module.AirModule;
public class MenuPickReference extends MenuDialog{
    private final BorderLayout layout;
    private final ListLayout list;
    public MenuPickReference(Menu parent, NCPFConfiguration config, boolean allowModules, Consumer<NCPFElementReference> onSelect){
        super(parent);
        layout = setContent(new BorderLayout());
        layout.width = 580;
        layout.height = 318;
        GridLayout tabs = layout.add(new GridLayout(0, 1), BorderLayout.TOP, 48);
        list = layout.add(new ListLayout(96), BorderLayout.CENTER);
        ArrayList<Supplier<NCPFModule>> modules = new ArrayList<>();
        modules.add(AirModule::new);
        for(List<NCPFElement> elements : config.getElements()){
            if(elements.isEmpty())continue;
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
                    list.add(new NCPFElementComponent(elem).addButton(">", null, () -> {
                        close();
                        onSelect.accept(new NCPFElementReference(elem));
                    }));
                    list.componentHeight = 96;
                }
            }));
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
        layout.height = 48+list.getTotalHeight();
        super.render2d(deltaTime);
    }
}