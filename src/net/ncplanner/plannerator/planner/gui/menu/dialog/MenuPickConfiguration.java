package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.BorderLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListLayout;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.module.AirModule;
public class MenuPickConfiguration extends MenuDialog{
    private final BorderLayout layout;
    private final ListLayout list;
    private final GridLayout tabs;
    public MenuPickConfiguration(Menu parent, Consumer<Configuration> onSelect){
        super(parent);
        layout = setContent(new BorderLayout());
        layout.width = 580;
        layout.height = 318;
        tabs = layout.add(new GridLayout(1, 0), BorderLayout.TOP, 48);
        list = layout.add(new ListLayout(96), BorderLayout.CENTER);
        ArrayList<Supplier<NCPFModule>> modules = new ArrayList<>();
        modules.add(AirModule::new);
        tabs.add(new Button("Configurations", true).addAction(() -> {
            list.components.clear();
            for(Configuration configuration : Configuration.configurations){
                list.add(new Button(configuration.getName(), true).addAction(() -> {
                    close();
                    onSelect.accept(configuration);
                }));
                list.componentHeight = 96;
            }
        }));
        ((Button)tabs.components.get(0)).runActions();
//        tabs.add(new Button("Addons", true).addAction(() -> {
//            list.components.clear();
//            for(Addon addon : Configuration.internalAddons){
//                list.add(new Button(addon.getName(), true).addAction(() -> {
//                    close();
//                    onSelect.accept(addon.configuration);
//                }));
//                list.componentHeight = 96;
//            }
//        }));
        addButton("Cancel");
    }
    @Override
    public void render2d(double deltaTime){
        layout.topHeight = 48*Math.max(1, (tabs.components.size()+tabs.columns-1)/tabs.columns);
        layout.height = layout.topHeight.floatValue()+list.getTotalHeight();
        super.render2d(deltaTime);
    }
}
