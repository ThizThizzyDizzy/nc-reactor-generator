package planner.menu;
import planner.menu.component.MenuComponentMultiblock;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.configuration.MenuConfiguration;
import java.awt.Color;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.multiblock.Multiblock;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuMain extends Menu{
    private static final Color headerColor = new Color(.2f, .2f, .4f, 1f);
    private static final Color header2Color = new Color(.25f, .25f, .5f, 1f);
    private static final Color textColor = new Color(.1f, .1f, .2f, 1f);
    private MenuComponentMinimaList multiblocks = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private MenuComponentButton addMultiblock = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "+", true, true));
    private MenuComponentButton editMetadata = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true));
    private MenuComponentButton settings = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true){
        @Override
        public void drawText(){
            double holeRad = width*.1;
            int teeth = 8;
            double averageRadius = width*.3;
            double toothSize = width*.1;
            double rot = 360/16d;
            int resolution = (int)(2*Math.PI*averageRadius*2);//an extra *2 to account for wavy surface?
            GL11.glBegin(GL11.GL_QUADS);
            double angle = rot;
            double radius = averageRadius+toothSize/2;
            for(int i = 0; i<teeth*resolution; i++){
                double inX = x+width/2+Math.cos(Math.toRadians(angle-90))*holeRad;
                double inY = y+height/2+Math.sin(Math.toRadians(angle-90))*holeRad;
                GL11.glVertex2d(inX, inY);
                double outX = x+width/2+Math.cos(Math.toRadians(angle-90))*radius;
                double outY = y+height/2+Math.sin(Math.toRadians(angle-90))*radius;
                GL11.glVertex2d(outX,outY);
                angle+=(360d/(teeth*resolution));
                if(angle>=360)angle-=360;
                radius = averageRadius+(toothSize/2)*Math.cos(Math.toRadians(teeth*(angle-rot)));
                outX = x+width/2+Math.cos(Math.toRadians(angle-90))*radius;
                outY = y+height/2+Math.sin(Math.toRadians(angle-90))*radius;
                GL11.glVertex2d(outX,outY);
                inX = x+width/2+Math.cos(Math.toRadians(angle-90))*holeRad;
                inY = y+height/2+Math.sin(Math.toRadians(angle-90))*holeRad;
                GL11.glVertex2d(inX, inY);
            }
            GL11.glEnd();
        }
    });
    private boolean forceMetaUpdate = true;
    private MenuComponent metadataPanel = add(new MenuComponent(0, 0, 0, 0){
        MenuComponentMulticolumnMinimaList list = add(new MenuComponentMulticolumnMinimaList(0, 0, 0, 0, 0, 50, 50));
        MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true));
        {
            done.addActionListener((e) -> {
                Core.resetMetadata();
                for(int i = 0; i<list.components.size(); i+=2){
                    MenuComponentMinimalistTextBox key = (MenuComponentMinimalistTextBox) list.components.get(i);
                    MenuComponentMinimalistTextBox value = (MenuComponentMinimalistTextBox) list.components.get(i+1);
                    if(key.text.trim().isEmpty()&&value.text.trim().isEmpty())continue;
                    Core.metadata.put(key.text, value.text);
                }
                metadating = false;
                refresh();
            });
        }
        @Override
        public void renderBackground(){
            list.width = width;
            list.y = height/16;
            list.height = height-height/8;
            list.columnWidth = (list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0))/2;
            done.width = width;
            done.y = height-height/16;
            done.height = height/16;
        }
        @Override
        public void render(){
            GL11.glColor4d(.18, .225, .45, 1);
            drawRect(x, y, x+width, y+height, 0);
            applyColor(new Color(.225f,.225f,.45f,1f));
            drawRect(x, y, x+width, y+height/16, 0);
            applyColor(textColor);
            drawCenteredText(x, y, x+width, y+height/16, "Metadata");
        }
        @Override
        public void tick(){
            if(forceMetaUpdate){
                forceMetaUpdate = false;
                list.components.clear();
                for(String key : Core.metadata.keySet()){
                    String value = Core.metadata.get(key);
                    list.add(new MenuComponentMinimalistTextBox(0,0,0,0,key, true));
                    list.add(new MenuComponentMinimalistTextBox(0,0,0,0,value, true));
                }
            }
            if(!metadating)return;
            ArrayList<MenuComponent> remove = new ArrayList<>();
            boolean add = list.components.isEmpty();
            for(int i = 0; i<list.components.size(); i+=2){
                MenuComponentMinimalistTextBox key = (MenuComponentMinimalistTextBox) list.components.get(i);
                MenuComponentMinimalistTextBox value = (MenuComponentMinimalistTextBox) list.components.get(i+1);
                if(i==list.components.size()-2){//the last one
                    if(!(key.text.trim().isEmpty()&&value.text.trim().isEmpty())){
                        add = true;
                    }
                }else{
                    if(key.text.trim().isEmpty()&&value.text.trim().isEmpty()){
                        remove.add(key);
                        remove.add(value);
                    }
                }
            }
            list.components.removeAll(remove);
            if(add){
                list.add(new MenuComponentMinimalistTextBox(0,0,0,0,"", true));
                list.add(new MenuComponentMinimalistTextBox(0,0,0,0,"", true));
            }
        }
    });
    private ArrayList<MenuComponentMinimalistButton> multiblockButtons = new ArrayList<>();
    private boolean adding = false;
    private int addingScale = 0;
    private final int addingTime = 3;
    private boolean metadating = false;
    private int metadatingScale = 0;
    private final int metadatingTime = 4;
    public MenuMain(GUI gui){
        super(gui, null);
        for(Multiblock m : Core.multiblockTypes){
            MenuComponentMinimalistButton button = add(new MenuComponentMinimalistButton(0, 0, 0, 0, m.getDefinitionName(), true, true){
                @Override
                public void drawText(){
                    drawCenteredText(x+textInset, y+textInset, x+width-textInset, y+textInset+height/5, m.getDefinitionName());
                }
            });
            button.addActionListener((e) -> {
                Core.multiblocks.add(m.newInstance());
                adding = false;
                refresh();
            });
            multiblockButtons.add(button);
        }
        addMultiblock.addActionListener((e) -> {
            adding = true;
        });
        editMetadata.addActionListener((e) -> {
            metadating = true;
            forceMetaUpdate = true;
        });
        settings.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, new MenuConfiguration(gui, this), MenuTransition.SlideTransition.slideFrom(0, -1), 5));
        });
    }
    @Override
    public void renderBackground(){
        applyColor(headerColor);
        drawRect(0, 0, Display.getWidth(), Display.getHeight()/16, 0);
        applyColor(header2Color);
        drawRect(0, Display.getHeight()/16, Display.getWidth()/3, Display.getHeight()/8, 0);
        applyColor(textColor);
        drawCenteredText(0, Display.getHeight()/16, Display.getWidth()/3-Display.getHeight()/16, Display.getHeight()/8, "Multiblocks");
    }
    @Override
    public void tick(){
        super.tick();
        if(adding)addingScale = Math.min(addingScale+1, addingTime);
        else addingScale = Math.max(addingScale-1, 0);
        if(metadating)metadatingScale = Math.min(metadatingScale+1, metadatingTime);
        else metadatingScale = Math.max(metadatingScale-1, 0);
    }
    @Override
    public void render(int millisSinceLastTick){
        editMetadata.x = Display.getWidth()/3;
        editMetadata.width = Display.getWidth()*2/3-Display.getHeight()/16;
        editMetadata.height = Display.getHeight()/16;
        settings.x = Display.getWidth()-Display.getHeight()/16;
        settings.width = settings.height = Display.getHeight()/16;
        multiblocks.y = Display.getHeight()/8;
        multiblocks.height = Display.getHeight()-multiblocks.y;
        multiblocks.width = Display.getWidth()/3;
        for(MenuComponent c : multiblocks.components){
            c.width = multiblocks.width-(multiblocks.hasScrollbar()?multiblocks.vertScrollbarWidth:0);
            ((MenuComponentMultiblock) c).edit.enabled = !(adding||metadating);
        }
        addMultiblock.x = Display.getWidth()/3-Display.getHeight()/16;
        addMultiblock.y = Display.getHeight()/16;
        addMultiblock.width = addMultiblock.height = Display.getHeight()/16;
        addMultiblock.enabled = !(adding||metadating);
        editMetadata.enabled = !(adding||metadating);
        settings.enabled = !(adding||metadating);
        for(MenuComponentMinimalistButton b : multiblockButtons){
            b.enabled = adding;
        }
        metadataPanel.width = Display.getWidth()*.75;
        metadataPanel.height = Display.getHeight()*.75;
        metadataPanel.x = Display.getWidth()/2-metadataPanel.width/2;
        double addScale = Math.min(1,Math.max(0,(adding?(addingScale+(millisSinceLastTick/50d)):(addingScale-(millisSinceLastTick/50d)))/addingTime));
        for(int i = 0; i<multiblockButtons.size(); i++){
            MenuComponentMinimalistButton button = multiblockButtons.get(i);
            double midX = Display.getWidth()/(multiblockButtons.size()+1d)*(i+1);
            double midY = Display.getHeight()/2;
            button.width = button.height = Display.getWidth()/multiblockButtons.size()/2*addScale;
            button.x = midX-button.width/2;
            button.y = midY-button.height/2;
        }
        double metadataScale = Math.min(1,Math.max(0,(metadating?(metadatingScale+(millisSinceLastTick/50d)):(metadatingScale-(millisSinceLastTick/50d)))/metadatingTime));
        metadataPanel.y = Display.getHeight()/2-metadataPanel.height/2-Display.getHeight()*(1-metadataScale);
        super.render(millisSinceLastTick);
    }
    @Override
    public void onGUIOpened(){
        refresh();
    }
    public void refresh(){
        multiblocks.components.clear();
        for(Multiblock multi : Core.multiblocks){
            multiblocks.add(new MenuComponentMultiblock(multi));
        }
        editMetadata.label = Core.metadata.containsKey("Name")?Core.metadata.get("Name"):"";
    }
    private void applyColor(Color color){
        GL11.glColor4f(color.getRed()/255f,color.getGreen()/255f,color.getBlue()/255f,color.getAlpha()/255f);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(MenuComponent c : multiblocks.components){
            if(c instanceof MenuComponentMultiblock){
                if(button==((MenuComponentMultiblock) c).edit){
                    gui.open(new MenuTransition(gui, this, new MenuEdit(gui, this, ((MenuComponentMultiblock) c).multiblock), MenuTransition.SlideTransition.slideFrom(1, 0), 5));
                }
            }
        }
    }
}