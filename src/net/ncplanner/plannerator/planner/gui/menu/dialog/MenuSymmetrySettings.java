package net.ncplanner.plannerator.planner.gui.menu.dialog;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Symmetry;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
public class MenuSymmetrySettings extends MenuDialog{
    public MenuSymmetrySettings(GUI gui, Menu parent, Symmetry symmetry){
        super(gui, parent);
        setContent(new Component(0, 0, 600, 212){
            {
                add(new ToggleBox(0, 20, 300, 64, "X Mirror", symmetry.mx){
                    @Override
                    public void onMouseButton(double x, double y, int button, int action, int mods){
                        super.onMouseButton(x, y, button, action, mods);
                        symmetry.mx = isToggledOn;
                    }
                });
                add(new ToggleBox(0, 84, 300, 64, "Y Mirror", symmetry.my){
                    @Override
                    public void onMouseButton(double x, double y, int button, int action, int mods){
                        super.onMouseButton(x, y, button, action, mods);
                        symmetry.my = isToggledOn;
                    }
                });
                add(new ToggleBox(0, 148, 300, 64, "Z Mirror", symmetry.mz){
                    @Override
                    public void onMouseButton(double x, double y, int button, int action, int mods){
                        super.onMouseButton(x, y, button, action, mods);
                        symmetry.mz = isToggledOn;
                    }
                });
                add(new ToggleBox(300, 20, 300, 64, "180 X Rotational", symmetry.rx180){
                    @Override
                    public void onMouseButton(double x, double y, int button, int action, int mods){
                        super.onMouseButton(x, y, button, action, mods);
                        symmetry.rx180 = isToggledOn;
                    }
                });
                add(new ToggleBox(300, 84, 300, 64, "180 Y Rotational", symmetry.ry180){
                    @Override
                    public void onMouseButton(double x, double y, int button, int action, int mods){
                        super.onMouseButton(x, y, button, action, mods);
                        symmetry.ry180 = isToggledOn;
                    }
                });
                add(new ToggleBox(300, 148, 300, 64, "180 Z Rotational", symmetry.rz180){
                    @Override
                    public void onMouseButton(double x, double y, int button, int action, int mods){
                        super.onMouseButton(x, y, button, action, mods);
                        symmetry.rz180 = isToggledOn;
                    }
                });
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getComponentTextColor(0));
                renderer.drawCenteredText(x, y, x+width, y+20, "Symmetry Settings");
            }
        });
        addButton("Done", () -> {
            close();
        });
    }
}