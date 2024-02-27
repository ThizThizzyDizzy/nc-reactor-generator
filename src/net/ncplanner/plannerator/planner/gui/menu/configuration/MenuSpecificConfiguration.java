package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.BorderLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SplitLayout;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuPickReference;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.module.RecipesBlockModule;
public class MenuSpecificConfiguration extends ConfigurationMenu{
    public MenuSpecificConfiguration(Menu parent, Configuration cnfg, NCPFConfigurationContainer configuration, NCPFConfiguration config){
        super(parent, configuration, config.getName().replace(" Configuration", ""), new SplitLayout(SplitLayout.Y_AXIS, 0).fitSize());
        NCPFSettingsModule settings = null;
        for(NCPFModule module : config.modules.modules.values()){
            if(module instanceof NCPFSettingsModule){
                settings = (NCPFSettingsModule)module;
            }
        }
        addSettings(settings);
        GridLayout lists = add(new GridLayout(0, 1));
        List<NCPFElement>[] elements = config.getElements();
        Supplier<NCPFElement>[] suppliers = config.getElementSuppliers();
        for(int i = 0; i<elements.length; i++){
            Supplier<NCPFElement> supplier = suppliers[i];
            String title = supplier.get().getTitle();
            List<NCPFElement> elems = elements[i];
            BorderLayout list = lists.add(new BorderLayout());
            Label label = list.add(new Label(title+"s"), BorderLayout.TOP, 48);
            SingleColumnList lst = list.add(new SingleColumnList(16), BorderLayout.CENTER);
            GridLayout buttons = list.add(new GridLayout(0, 1), BorderLayout.BOTTOM, 48);
            boolean hasRules = false;
            for(NCPFElement elem : elems){
                for(NCPFModule m : elem.modules.modules.values()){
                    if(m instanceof BlockRulesModule)hasRules = true;
                }
            }
            boolean hasRecipes = false;
            for(NCPFConfiguration confg : cnfg.getConfigurations(config.name)){
                if(confg==config)break;
                for(NCPFElement elem : confg.getElements()[i]){
                    if(elem instanceof BlockRecipesElement)hasRecipes = true;
                }
            }
            boolean hasRuls = hasRules;
            buttons.add(new Button("Add "+title, true, true).addAction(() -> {
                NCPFElement elem = supplier.get().copyTo(supplier);
                elems.add(elem);
                gui.open(new MenuElementConfiguration(this, cnfg, configuration, config, elem));
            }));
            if(hasRecipes){
                buttons.add(new Button("Add Parent "+title, true, true).addAction(() -> {
                    new MenuPickReference(this, cnfg, config, false, (t) -> {
                        NCPFElement elem = supplier.get().copyTo(supplier);
                        elem.definition = t.target.definition;
                        elems.add(elem);
                        gui.open(new MenuElementConfiguration(this, cnfg, configuration, config, elem));
                    }, (elem) -> {
                        if(!(elem instanceof BlockRecipesElement))return false;
                        for(NCPFModule module : elem.modules.modules.values()){
                            if(module instanceof RecipesBlockModule)return true;
                        }
                        return false;
                    }).open();
                }));
            }
            onOpen(() -> {
                label.text = title+"s ("+elems.size()+")";
                lst.components.clear();
                for(NCPFElement elem : elems){
                    NCPFElementComponent button = lst.add(new NCPFElementComponent(elem).addIconButton("delete", "Delete "+title, () -> {
                        elems.remove(elem);
                        refresh();
                    }).addIconButton("pencil", "Modify "+title, () -> {
                        gui.open(new MenuElementConfiguration(this, cnfg, configuration, config, elem));
                    }));
                    button.height = 96;
                }
            });
        }
        //TODO placement rule tree
    }
    private void addSettings(NCPFSettingsModule settings){
        if(settings==null){
            add(new GridLayout(0, 1));
            return;
        }
        int rows = Math.max(1, (settings.settings.size()-1)/4+1);
        ((SplitLayout)content).minSize1 = rows*48;
        GridLayout settingsPanel = add(new GridLayout(0, rows));
        for(String setting : settings.settings){
            Supplier<? extends Number> get = settings.gets.get(setting);
            Consumer<? extends Number> set = settings.sets.get(setting);
            TextBox box = settingsPanel.add(new TextBox(get.get()+"", true, settings.titles.get(setting)).setTooltip(settings.tooltips.get(setting)));
            switch(settings.types.get(setting)){
                case FLOAT:
                    box.setFloatFilter();
                    box.onChange((t) -> {
                        ((Consumer<Float>)set).accept(Float.valueOf(t));
                    });
                    break;
                case INTEGER:
                    box.setIntFilter();
                    box.onChange((t) -> {
                        ((Consumer<Integer>)set).accept(Integer.valueOf(t));
                    });
                    break;
            }
            onOpen(()->{
                box.text = get.get()+"";
            });
        }
    }
}