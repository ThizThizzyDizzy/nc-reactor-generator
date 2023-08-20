package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SplitLayout;
import net.ncplanner.plannerator.planner.ncpf.Addon;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
public class MenuAddon extends ConfigurationMenu{
    private final Addon addon;
    public MenuAddon(Menu parent, Configuration cnfg, Addon addon){
        super(parent, addon.configuration, addon.getName(), new ListLayout());
        this.addon = addon;
        onOpen(() -> {
            content.components.clear();
            for(String key : NCPFConfigurationContainer.configOrder){
                Supplier<NCPFConfiguration> cfg = NCPFConfigurationContainer.recognizedConfigurations.get(key);
                configuration.withConfiguration(cfg, (config) -> {
                    SplitLayout split = add(new SplitLayout(SplitLayout.X_AXIS, 0.7f));
                    split.height = 96;
                    GridLayout left = split.add(new GridLayout(1, 2));
                    left.add(new Label(config.getName(), true));
                    GridLayout fields = left.add(new GridLayout(0, 1));
                    config.withModule(ConfigurationMetadataModule::new, (meta)->{
                        fields.add(new TextBox(meta.name==null?"":meta.name, true, "Name").onChange((str) -> meta.name = str));
                        fields.add(new TextBox(meta.version==null?"":meta.version, true, "Version").onChange((str) -> meta.version = str));
                    });
                    GridLayout buttons = split.add(new GridLayout(1, 2));
                    buttons.add(new Button("Edit", true).addAction(() -> {
                        config.convertToObject(new NCPFObject());//set all module references
                        gui.open(new MenuSpecificConfiguration(this, cnfg, super.configuration, config));
                    }));
                    buttons.add(new Button("Delete (Shift)", false){
                        @Override
                        public void render2d(double deltaTime){
                            enabled = Core.isShiftPressed();
                            super.render2d(deltaTime);
                        }
                    }.addAction(() -> {
                        configuration.configurations.remove(key);
                        onOpened();
                    }));
                });
                if(!configuration.hasConfiguration(cfg)){
                    SplitLayout split = add(new SplitLayout(SplitLayout.X_AXIS, 0.7f));
                    split.height = 48;
                    split.add(new Label(cfg.get().getName(), true));
                    split.add(new Button("Create (Shift)", false){
                        @Override
                        public void render2d(double deltaTime){
                            enabled = Core.isShiftPressed();
                            super.render2d(deltaTime);
                        }
                    }.addAction(() -> {
                        NCPFConfiguration c = cfg.get();
                        c.init(true);
                        configuration.setConfiguration(c);
                        onOpened();
                    }));
                }
            }
            for(String key : configuration.configurations.keySet()){
                if(!NCPFConfigurationContainer.configOrder.contains(key)){
                    add(new Label(0, 0, 0, 48, configuration.configurations.get(key).getName()+" ("+key+")", true));
                }
            }
        });
    }
}