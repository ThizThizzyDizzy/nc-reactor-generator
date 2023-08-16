package net.ncplanner.plannerator.planner.gui.menu.component;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.BorderLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.LayeredLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListButtonsLayout;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
public class NCPFElementComponent extends LayoutPanel{
    private final ListButtonsLayout buttons;
    public NCPFElementComponent(NCPFElement element){
        super(new LayeredLayout());
        add(new Panel().setBackgroundColor(Core.theme::getTextViewBackgroundColor));
        BorderLayout content = add(new BorderLayout());
        element.withModule(TextureModule::new, (tex)->{
            content.add(new Panel().setImage(tex.texture), BorderLayout.LEFT, 96);
        });
        TextDisplay display = content.add(new TextDisplay().fitText(), BorderLayout.CENTER);
        element.withModule(DisplayNamesModule::new, (nam)->{
            display.addText(nam.displayName);
        });
        display.addText("\n"+element.definition.toString());
        for(NCPFModule module : element.modules.modules.values()){
            if(module instanceof BlockFunctionModule){
                display.addText("\n"+((BlockFunctionModule)module).getFunctionName());
            }
        }
        buttons = add(new ListButtonsLayout());
    }
    public NCPFElementComponent addButton(String icon, String tooltip, Runnable onClick){
        buttons.add(new Button("", true, true){
            @Override
            public void drawForeground(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
                renderer.drawElement(icon, x, y, width, height);
            }
        }.setTooltip(tooltip).addAction(onClick));
        return this;
    }
}