package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
public class MenuOverlaySettings extends MenuDialog{
    public MenuOverlaySettings(GUI gui, Menu parent, ArrayList<EditorOverlay> overlays, Multiblock multiblock){
        super(gui, parent);
        setContent(new Component(0, 0, 600, 312){
            {
                for(EditorOverlay overlay : overlays){
                    add(new Component(0, 0, width, 80){
                        ArrayList<Button> buttons = new ArrayList<>();
                        {
                            add(new ToggleBox(0, 0, width, 40, overlay.name, overlay.isActive()).onChange((t) -> {
                                overlay.setActive((boolean)t);
                                if(overlay.isActive())overlay.refresh(multiblock);
                            }).setTooltip(overlay.description));
                            if(!overlay.modes.isEmpty()){
                                float wid = width/overlay.modes.size();
                                for(int i = 0; i<overlay.modes.size(); i++){
                                    final int mode = i;
                                    buttons.add(add(new Button(i*wid, 40, wid, 40, (String)overlay.modes.get(i), overlay.getMode()!=i, true).setTooltip((String)overlay.modeTooltips.get(i)).addAction(() -> {
                                        overlay.setMode(mode);
                                        overlay.refresh(multiblock);
                                        for(int j = 0; j<buttons.size(); j++){
                                            buttons.get(j).enabled = j!=mode;
                                        }
                                    })));
                                }
                            }
                        }
                        @Override
                        public void drawBackground(double deltaTime){
                            height = overlay.modes.isEmpty()||!overlay.isActive()?40:80;
                            super.drawBackground(deltaTime);
                        }
                    });
                }
            }
            @Override
            public void drawBackground(double deltaTime){
                int h = 20;
                for(Component c : components){
                    c.y = h;
                    h+=c.height;
                }
                height = h;
                super.drawBackground(deltaTime);
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getComponentTextColor(0));
                renderer.drawCenteredText(x, y, x+width, y+20, "Overlays");
            }
        });
        addButton("Done", () -> {
            close();
        });
    }
}