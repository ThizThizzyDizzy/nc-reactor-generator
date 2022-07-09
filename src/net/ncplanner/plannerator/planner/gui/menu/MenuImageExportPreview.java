package net.ncplanner.plannerator.planner.gui.menu;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SingleColumnGridLayout;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuOverlaySettings;
import org.lwjgl.glfw.GLFW;
public class MenuImageExportPreview extends Menu{
    private Image image;
    int sidebarWidth = 300;
    private final SingleColumnGridLayout buttons = add(new SingleColumnGridLayout(0, 0, 300, 40));
    private final Button back = buttons.add(new Button(0, 0, 300, 40, "Back", true));
    private final ToggleBox includeCasing = buttons.add(new ToggleBox(0, 80, 300, 40, "Include Casing", Core.imageExportCasing, true));
    private final ToggleBox preview3D = buttons.add(new ToggleBox(0, 40, 300, 40, "3D view", Core.imageExport3DView, true));
    private final ToggleBox includeCasing3D = buttons.add(new ToggleBox(0, 80, 300, 40, "Include Casing in 3D View", Core.imageExportCasing3D, true));
    private final Button overlays = buttons.add(new Button(0, 120, 300, 40, "Overlays", true, true));
    private final Button save = buttons.add(new Button(0, 160, 300, 40, "Save", true));
    private final Supplier<Image> img;
    public MenuImageExportPreview(GUI gui, Menu parent, Supplier<Image> image, Runnable onExport, ArrayList<EditorOverlay> overlays, Multiblock multiblock){
        super(gui, parent);
        this.img = image;
        refresh();
        back.addAction(() -> {
            gui.open(parent);
        });
        save.addAction(() -> {
            gui.open(parent);
            onExport.run();
        });
        preview3D.onChange(() -> {
            Core.imageExport3DView = preview3D.isToggledOn;
            refresh();
        });
        includeCasing.onChange(() -> {
            Core.imageExportCasing = includeCasing.isToggledOn;
            refresh();
        });
        includeCasing3D.onChange(() -> {
            Core.imageExportCasing3D = includeCasing3D.isToggledOn;
            refresh();
        });
        this.overlays.addAction(() -> {
            new MenuOverlaySettings(gui, this, overlays, multiblock, this::refresh).open();
        });
    }
    @Override
    public void render2d(double deltaTime){
        super.render2d(deltaTime);
        float xScale = (gui.getWidth()-sidebarWidth)/(float)image.getWidth();
        float yScale = gui.getHeight()/(float)image.getHeight();
        float scale = Math.min(xScale,yScale);
        Renderer renderer = new Renderer();
        renderer.setWhite();
        if(xScale>yScale){
            float wid = image.getWidth()*scale;
            renderer.drawImage(image, sidebarWidth+(gui.getWidth()-sidebarWidth)/2-wid/2, 0, sidebarWidth+(gui.getWidth()-sidebarWidth)/2+wid/2, gui.getHeight());
        }else{
            float hig = image.getHeight()*scale;
            renderer.drawImage(image, sidebarWidth+0, gui.getHeight()/2-hig/2, sidebarWidth+(gui.getWidth()-sidebarWidth), gui.getHeight()/2+hig/2);
        }
    }
    @Override
    public void onKeyEvent(int key, int scancode, int action, int mods){
        super.onKeyEvent(key, scancode, action, mods);
        if(action==GLFW.GLFW_PRESS&&key==GLFW.GLFW_KEY_ESCAPE)gui.open(parent);
    }
    public void refresh(){
        buttons.components.remove(includeCasing3D);
        if(Core.imageExport3DView){
            buttons.components.add(3, includeCasing3D);
        }
        this.image = img.get().flip();
    }
}