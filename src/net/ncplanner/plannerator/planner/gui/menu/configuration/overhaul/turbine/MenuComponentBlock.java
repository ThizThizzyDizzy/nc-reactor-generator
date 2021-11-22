package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.turbine;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.Component;
public class MenuComponentBlock extends Component{
    public final Block block;
    public final Button edit = add(new Button(0, 0, 0, 0, "", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.fillTri(x+width*.25f, y+height*.75f,
                    x+width*.375f, y+height*.75f,
                    x+width*.25f, y+height*.625f);
            renderer.fillQuad(x+width*.4f, y+height*.725f,
                    x+width*.275f, y+height*.6f,
                    x+width*.5f, y+height*.375f,
                    x+width*.625f, y+height*.5f);
            renderer.fillQuad(x+width*.525f, y+height*.35f,
                    x+width*.65f, y+height*.475f,
                    x+width*.75f, y+height*.375f,
                    x+width*.625f, y+height*.25f);
        }
    }.setTooltip("Modify block"));
    public final Button delete = add(new Button(0, 0, 0, 0, "", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.fillQuad(x+width*.1f, y+height*.8f,
                    x+width*.2f, y+height*.9f,
                    x+width*.9f, y+height*.2f,
                    x+width*.8f, y+height*.1f);
            renderer.fillQuad(x+width*.1f, y+height*.2f,
                    x+width*.2f, y+height*.1f,
                    x+width*.9f, y+height*.8f,
                    x+width*.8f, y+height*.9f);
        }
    }.setTooltip("Delete block"));
    public MenuComponentBlock(Block block, Runnable onEditPressed, Runnable onDeletePressed){
        super(0, 0, 0, 100);
        this.block = block;
        edit.addAction(onEditPressed);
        delete.addAction(onDeletePressed);
    }
    @Override
    public void drawBackground(double deltaTime){
        super.drawBackground(deltaTime);
        delete.x = width-height/2-height/4;
        edit.x = delete.x - height;
        delete.y = edit.y = height/4;
        delete.width = delete.height = edit.width = edit.height = height/2;
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverUnselectableComponentColor(Core.getThemeIndex(this)));
        else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        renderer.fillRect(x, y, x+width, y+height);
    }
    @Override
    public void drawForeground(double deltaTime){
        Renderer renderer = new Renderer();
        renderer.setWhite();
        if(block.texture!=null)renderer.drawImage(block.displayTexture, x, y, x+height, y+height);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        ArrayList<String> strs = new ArrayList<>();
        strs.add(block.getDisplayName());
        if(block.blade){
            String blade = block.bladeStator?"Stator":"Blade";
            strs.add(blade+" Efficiency: "+block.bladeEfficiency);
            strs.add(blade+" Expansion: "+block.bladeExpansion);
        }
        if(block.coil)strs.add("Coil Efficiency: "+block.coilEfficiency);
        if(block.bearing)strs.add("Bearing");
        if(block.shaft)strs.add("Shaft");
        if(block.connector)strs.add("Connector");
        if(block.controller)strs.add("Controller");
        if(block.casing)strs.add("Casing");
        if(block.casingEdge)strs.add("Casing Edge");
        if(block.inlet)strs.add("Inlet");
        if(block.outlet)strs.add("Outlet");
        while(strs.size()<5)strs.add("");
        for(int i = 0; i<strs.size(); i++){
            String str = strs.get(i);
            renderer.drawText(x+height, y+height/strs.size()*i, x+width, y+height/strs.size()*(i+1), str);
        }
    }
}