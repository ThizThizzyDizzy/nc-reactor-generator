package planner.menu.component.editor;
import java.util.ArrayList;
import multiblock.Axis;
import multiblock.Block;
import multiblock.Decal;
import multiblock.EditorSpace;
import multiblock.Multiblock;
import multiblock.action.MSRAllShieldsAction;
import multiblock.action.MSRSourceAction;
import multiblock.action.MSRToggleAction;
import multiblock.action.SFRAllShieldsAction;
import multiblock.action.SFRSourceAction;
import multiblock.action.SFRToggleAction;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import planner.editor.suggestion.Suggestion;
import planner.menu.MenuEdit;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentEditorGrid extends MenuComponent{
    private final Object synchronizer = new Object();
    private final Multiblock multiblock;
    public final int layer;
    public final MenuEdit editor;
    public int blockSize;
    public int[] mouseover;
    private static final int resonatingTime = 60;
    private static final float resonatingMin = .25f;
    private static final float resonatingMax = .5f;
    private int resonatingTick = 0;
    private float resonatingAlpha = 0;
    public final Axis axis;
    public final Axis xAxis;
    public final Axis yAxis;
    private final EditorSpace editorSpace;
    public final int x1;
    public final int y1;
    public final int x2;
    public final int y2;
    private final int blocksWide, blocksHigh;
    public MenuComponentEditorGrid(int x, int y, int blockSize, MenuEdit editor, Multiblock multiblock, EditorSpace editorSpace, int x1, int y1, int x2, int y2, Axis axis, int layer){
        super(x, y, blockSize*(x2-x1+1), blockSize*(y2-y1+1));
        this.multiblock = multiblock;
        this.axis = axis;
        this.layer = layer;
        this.editor = editor;
        this.blockSize = blockSize;
        this.editorSpace = editorSpace;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        blocksWide = x2-x1+1;
        blocksHigh = y2-y1+1;
        xAxis = axis.get2DXAxis();
        yAxis = axis.get2DYAxis();
    }
    @Override
    public void tick(){
        if(!(gui.mouseWereDown.contains(0))){
            editor.getSelectedTool(0).mouseReset(editorSpace, 0);
        }
        if(!(gui.mouseWereDown.contains(1))){
            editor.getSelectedTool(0).mouseReset(editorSpace, 1);
        }
        resonatingTick++;
        if(resonatingTick>resonatingTime)resonatingTick-=resonatingTime;
    }
    @Override
    public void render(int millisSinceLastTick){
        float tick = resonatingTick+(Math.max(0, Math.min(1, millisSinceLastTick/50)));
        resonatingAlpha = (float) (-Math.cos(2*Math.PI*tick/resonatingTime)/(2/(resonatingMax-resonatingMin))+(resonatingMax+resonatingMin)/2);
        super.render(millisSinceLastTick);
    }
    @Override
    public void render(){
        synchronized(synchronizer){
            if(!isMouseOver)mouseover = null;
            if(mouseover!=null){
                if(mouseover[0]<0||mouseover[1]<0||mouseover[0]>blocksWide-1||mouseover[1]>blocksHigh-1)mouseover = null;
            }
            blockSize = (int) Math.min(width/blocksWide, height/blocksHigh);
            Core.applyColor(Core.theme.getEditorBackgroundColor());
            drawRect(x,y,x+width,y+height,0);
            if(mouseover!=null){
                Core.applyColor(Core.theme.getEditorBackgroundMouseoverColor());
                drawRect(x+mouseover[0]*blockSize, y+mouseover[1]*blockSize, x+(mouseover[0]+1)*blockSize, y+(mouseover[1]+1)*blockSize, 0);
            }
        }
        Core.applyColor(Core.theme.getEditorGridColor());
        for(int x = 0; x<=blocksWide; x++){
            double border = blockSize/32d;
            double X = this.x+x*blockSize;
            drawRect(X-(x==0?0:border), y, X+(x==blocksWide?0:border), y+height, 0);
        }
        for(int y = 0; y<=blocksHigh; y++){
            double border = blockSize/32d;
            double Y = this.y+y*blockSize;
            drawRect(x, Y-(y==0?0:border), x+width, Y+(y==blocksHigh?0:border), 0);
        }
        for(int x = 0; x<blocksWide; x++){
            for(int y = 0; y<blocksHigh; y++){
                int bx = (x+x1)*xAxis.x+(y+y1)*yAxis.x+layer*axis.x;
                int by = (x+x1)*xAxis.y+(y+y1)*yAxis.y+layer*axis.y;
                int bz = (x+x1)*xAxis.z+(y+y1)*yAxis.z+layer*axis.z;
                if(!multiblock.contains(bx, by, bz))continue;
                Block block = multiblock.getBlock(bx, by, bz);
                double X = this.x+x*blockSize;
                double Y = this.y+y*blockSize;
                if(block!=null){
                    block.render(X, Y, blockSize, blockSize, true, multiblock);
                    boolean recipeMatches = false;
                    if(multiblock instanceof OverhaulSFR){
                        multiblock.overhaul.fissionsfr.Block bl = (multiblock.overhaul.fissionsfr.Block)block;
                        if(bl.recipe!=null&&bl.recipe==editor.getSelectedOverhaulSFRBlockRecipe(0))recipeMatches = true;
                    }
                    if(multiblock instanceof OverhaulMSR){
                        multiblock.overhaul.fissionmsr.Block bl = (multiblock.overhaul.fissionmsr.Block)block;
                        if(bl.recipe!=null&&bl.recipe==editor.getSelectedOverhaulMSRBlockRecipe(0))recipeMatches = true;
                    }
                    if(multiblock instanceof OverhaulFusionReactor){
                        multiblock.overhaul.fusion.Block bl = (multiblock.overhaul.fusion.Block)block;
                        if(bl.recipe!=null&&bl.recipe==editor.getSelectedOverhaulFusionBlockRecipe(0))recipeMatches = true;
                    }
                    if(recipeMatches){
                        Core.applyColor(Core.theme.getSelectionColor(), resonatingAlpha);
                        Renderer2D.drawRect(X, Y, X+blockSize, Y+blockSize, 0);
                    }
                }
                if(multiblock instanceof OverhaulFusionReactor&&((OverhaulFusionReactor)multiblock).getLocationCategory(bx, by, bz)==OverhaulFusionReactor.LocationCategory.PLASMA){
                    Core.applyWhite();
                    drawRect(X, Y, X+blockSize, Y+blockSize, ImageStash.instance.getTexture("/textures/overhaul/fusion/plasma.png"));
                }
                if(Core.isControlPressed()){
                    if(block==null||(Core.isShiftPressed()&&block.canBeQuickReplaced())){
                        if(editorSpace.isSpaceValid(editor.getSelectedBlock(0), bx, by, bz)&&multiblock.isValid(editor.getSelectedBlock(0), bx, by, bz)){
                            editor.getSelectedBlock(0).render(X, Y, blockSize, blockSize, false, resonatingAlpha, multiblock);
                        }
                    }
                }
                synchronized(multiblock.decals){
                    for(Object o : multiblock.decals){
                        Decal decal = (Decal)o;
                        if(decal.x==bx&&decal.y==by&&decal.z==bz){
                            decal.render(X, Y, blockSize);
                        }
                    }
                }
                if(isSelected(x, y)){
                    Core.applyColor(Core.theme.getSelectionColor(), .5f);
                    Renderer2D.drawRect(X, Y, X+blockSize, Y+blockSize, 0);
                    Core.applyColor(Core.theme.getSelectionColor());
                    double border = blockSize/8f;
                    boolean top = isSelected(x, y-1);
                    boolean right = isSelected(x+1, y);
                    boolean bottom = isSelected(x, y+1);
                    boolean left = isSelected(x-1, y);
                    if(!top||!left||!isSelected(x-1, y-1)){//top left
                        Renderer2D.drawRect(X, Y, X+border, Y+border, 0);
                    }
                    if(!top){//top
                        Renderer2D.drawRect(X+border, Y, X+blockSize-border, Y+border, 0);
                    }
                    if(!top||!right||!isSelected(x+1, y-1)){//top right
                        Renderer2D.drawRect(X+blockSize-border, Y, X+blockSize, Y+border, 0);
                    }
                    if(!right){//right
                        Renderer2D.drawRect(X+blockSize-border, Y+border, X+blockSize, Y+blockSize-border, 0);
                    }
                    if(!bottom||!right||!isSelected(x+1, y+1)){//bottom right
                        Renderer2D.drawRect(X+blockSize-border, Y+blockSize-border, X+blockSize, Y+blockSize, 0);
                    }
                    if(!bottom){//bottom
                        Renderer2D.drawRect(X+border, Y+blockSize-border, X+blockSize-border, Y+blockSize, 0);
                    }
                    if(!bottom||!left||!isSelected(x-1, y+1)){//bottom left
                        Renderer2D.drawRect(X, Y+blockSize-border, X+border, Y+blockSize, 0);
                    }
                    if(!left){//left
                        Renderer2D.drawRect(X, Y+border, X+border, Y+blockSize-border, 0);
                    }
                }
                //TODO there's a better way do do this, but this'll do for now
                for(Suggestion s : editor.getSuggestions()){
                    if(affects(s, x, y)){
                        if(s.selected&&s.result!=null){
                            Block b = s.result.getBlock(bx, by, bz);
                            Core.applyWhite(resonatingAlpha+.5f);
                            if(b==null){
                                drawRect(X, Y, X+blockSize, Y+blockSize, 0);
                            }else{
                                b.render(X, Y, blockSize, blockSize, false, resonatingAlpha+.5f, s.result);
                            }
                        }
                        Core.applyColor(Core.theme.getSuggestionOutlineColor());
                        double border = blockSize/40f;
                        if(s.selected)border*=3;
                        boolean top = affects(s, x, y-1);
                        boolean right = affects(s, x+1, y);
                        boolean bottom = affects(s, x, y+1);
                        boolean left = affects(s, x-1, y);
                        if(!top||!left||!affects(s, x-1, y-1)){//top left
                            Renderer2D.drawRect(X, Y, X+border, Y+border, 0);
                        }
                        if(!top){//top
                            Renderer2D.drawRect(X+border, Y, X+blockSize-border, Y+border, 0);
                        }
                        if(!top||!right||!affects(s, x+1, y-1)){//top right
                            Renderer2D.drawRect(X+blockSize-border, Y, X+blockSize, Y+border, 0);
                        }
                        if(!right){//right
                            Renderer2D.drawRect(X+blockSize-border, Y+border, X+blockSize, Y+blockSize-border, 0);
                        }
                        if(!bottom||!right||!affects(s, x+1, y+1)){//bottom right
                            Renderer2D.drawRect(X+blockSize-border, Y+blockSize-border, X+blockSize, Y+blockSize, 0);
                        }
                        if(!bottom){//bottom
                            Renderer2D.drawRect(X+border, Y+blockSize-border, X+blockSize-border, Y+blockSize, 0);
                        }
                        if(!bottom||!left||!affects(s, x-1, y+1)){//bottom left
                            Renderer2D.drawRect(X, Y+blockSize-border, X+border, Y+blockSize, 0);
                        }
                        if(!left){//left
                            Renderer2D.drawRect(X, Y+border, X+border, Y+blockSize-border, 0);
                        }
                    }
                }
            }
        }
        editor.getSelectedTool(0).drawGhosts(editorSpace, x1, y1, x2, y2, blocksWide, blocksHigh, axis, layer, x, y, width, height, blockSize, (editor.getSelectedBlock(0)==null?0:Core.getTexture(editor.getSelectedBlock(0).getTexture())));
        synchronized(synchronizer){
            if(mouseover!=null){
                double X = this.x+mouseover[0]*blockSize;
                double Y = this.y+mouseover[1]*blockSize;
                double border = blockSize/8;
                Core.applyColor(Core.theme.getEditorMouseoverLightColor(), .6375f);
                drawRect(X, Y, X+border, Y+border, 0);
                drawRect(X+blockSize-border, Y, X+blockSize, Y+border, 0);
                drawRect(X, Y+blockSize-border, X+border, Y+blockSize, 0);
                drawRect(X+blockSize-border, Y+blockSize-border, X+blockSize, Y+blockSize, 0);
                Core.applyColor(Core.theme.getEditorMouseoverDarkColor(), .6375f);
                drawRect(X+border, Y, X+blockSize-border, Y+border, 0);
                drawRect(X+border, Y+blockSize-border, X+blockSize-border, Y+blockSize, 0);
                drawRect(X, Y+border, X+border, Y+blockSize-border, 0);
                drawRect(X+blockSize-border, Y+border, X+blockSize, Y+blockSize-border, 0);
                Core.applyColor(Core.theme.getEditorMouseoverLineColor(), 0.6375f);
                drawRect(this.x, Y+blockSize/2-border/2, X, Y+blockSize/2+border/2, 0);
                drawRect(X+blockSize, Y+blockSize/2-border/2, this.x+this.width, Y+blockSize/2+border/2, 0);
                drawRect(X+blockSize/2-border/2, this.y, X+blockSize/2+border/2, Y, 0);
                drawRect(X+blockSize/2-border/2, Y+blockSize, X+blockSize/2+border/2, this.y+this.height, 0);
            }
            for(MenuComponent comp : editor.multibwauk.components){
                if(comp instanceof MenuComponentEditorGrid){
                    MenuComponentEditorGrid grid = (MenuComponentEditorGrid)comp;
                    if(grid==this)continue;
                    if(grid.axis!=axis)continue;
                    if(grid.x1!=x1)continue;
                    if(grid.x2!=x2)continue;
                    if(grid.y1!=y1)continue;
                    if(grid.y2!=y2)continue;
                    if(grid.mouseover==null)continue;
                    double X = this.x+grid.mouseover[0]*blockSize;
                    double Y = this.y+grid.mouseover[1]*blockSize;
                    double border = blockSize/6;
                    Core.applyColor(Core.theme.getEditorMouseoverLineColor(), 0.6375f);
                    drawRect(X+blockSize/2-border/2, Y+blockSize/2-border/2, X+blockSize/2+border/2, Y+blockSize/2+border/2, 0);
                }
            }
        }
    }
    @Override
    public void onMouseMove(double x, double y){
        super.onMouseMove(x, y);
        synchronized(synchronizer){
            mouseover = new int[]{(int)x/blockSize,(int)y/blockSize};
        }
        for(int i : gui.mouseWereDown){
            mouseDragged(x, y, i);
        }
        if(Double.isNaN(x)||Double.isNaN(y)){
            return;
        }
        int sx = Math.max(x1, Math.min(x2, x1+(int) (x/blockSize)));
        int sy = Math.max(y1, Math.min(y2, y1+(int) (y/blockSize)));
        int bx = sx*xAxis.x+sy*yAxis.x+layer*axis.x;
        int by = sx*xAxis.y+sy*yAxis.y+layer*axis.y;
        int bz = sx*xAxis.z+sy*yAxis.z+layer*axis.z;
        editor.getSelectedTool(0).mouseMoved(this, editorSpace, bx, by, bz);
    }
    @Override
    public void onMouseMovedElsewhere(double x, double y){
        super.onMouseMovedElsewhere(x, y);
        synchronized(synchronizer){
            if(mouseover!=null)editor.getSelectedTool(0).mouseMovedElsewhere(this, editorSpace);
            mouseover = null;
        }
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(Double.isNaN(x)||Double.isNaN(y)){
            return;
        }
        int sx = Math.max(x1, Math.min(x2, x1+(int) (x/blockSize)));
        int sy = Math.max(y1, Math.min(y2, y1+(int) (y/blockSize)));
        int bx = sx*xAxis.x+sy*yAxis.x+layer*axis.x;
        int by = sx*xAxis.y+sy*yAxis.y+layer*axis.y;
        int bz = sx*xAxis.z+sy*yAxis.z+layer*axis.z;
        if(pressed){
            Block block = multiblock.getBlock(bx, by, bz);
            boolean didSomething = false;
            if(editor.getSelectedTool(0).isEditTool()&&Core.isShiftPressed()&&block!=null){
                if(editor.getSelectedTool(0).isEditTool()&&multiblock instanceof OverhaulSFR&&Core.isShiftPressed()){
                    multiblock.overhaul.fissionsfr.Block b = (multiblock.overhaul.fissionsfr.Block)block;
                    if(b.template.shield||b.template.parent!=null||b.template.coolantVent){
                        if(Core.isControlPressed()&&b.template.shield)editor.action(new SFRAllShieldsAction(!b.isToggled), true);
                        else editor.action(new SFRToggleAction(b), true);
                        didSomething = true;
                    }
                    if(b.template.fuelCell&&(b.template.fuelCellHasBaseStats||b.recipe!=null)){
                        boolean self = b.recipe==null?b.template.fuelCellSelfPriming:b.recipe.fuelCellSelfPriming;
                        if(!self){
                            ArrayList<multiblock.configuration.overhaul.fissionsfr.Block> sources = new ArrayList<>();
                            for(multiblock.configuration.overhaul.fissionsfr.Block possible : multiblock.getConfiguration().overhaul.fissionSFR.allBlocks){
                                if(possible.source)sources.add(possible);
                            }
                            int idx = sources.size()-1;
                            if(b.source!=null)idx = sources.indexOf(b.source.template)-1;
                            if(idx<-1)idx = sources.size()-1;
                            editor.action(new SFRSourceAction(b, idx==-1?null:sources.get(idx)), true);
                            didSomething = true;
                        }
                    }
                }
                if(editor.getSelectedTool(0).isEditTool()&&multiblock instanceof OverhaulMSR&&Core.isShiftPressed()){
                    multiblock.overhaul.fissionmsr.Block b = (multiblock.overhaul.fissionmsr.Block) block;
                    if(b.template.shield||b.template.parent!=null){
                        if(Core.isControlPressed()&&b.template.shield)editor.action(new MSRAllShieldsAction(!b.isToggled), true);
                        else editor.action(new MSRToggleAction(b), true);
                        didSomething = true;
                    }
                    if(b.template.fuelVessel&&(b.template.fuelVesselHasBaseStats||b.recipe!=null)){
                        boolean self = b.recipe==null?b.template.fuelVesselSelfPriming:b.recipe.fuelVesselSelfPriming;
                        if(!self){
                            ArrayList<multiblock.configuration.overhaul.fissionmsr.Block> sources = new ArrayList<>();
                            for(multiblock.configuration.overhaul.fissionmsr.Block possible : multiblock.getConfiguration().overhaul.fissionMSR.allBlocks){
                                if(possible.source)sources.add(possible);
                            }
                            int idx = sources.size()-1;
                            if(b.source!=null)idx = sources.indexOf(b.source.template)-1;
                            if(idx<-1)idx = sources.size()-1;
                            editor.action(new MSRSourceAction(b, idx==-1?null:sources.get(idx)), true);
                            didSomething = true;
                        }
                    }
                }
            }
            if(!didSomething){
                if(button==GLFW.GLFW_MOUSE_BUTTON_MIDDLE){
                    editor.setSelectedBlock(block);
                }
                editor.getSelectedTool(0).mousePressed(this, editorSpace, bx, by, bz, button);
            }
        }else{
            editor.getSelectedTool(0).mouseReleased(this, editorSpace, bx, by, bz, button);
        }
    }
    public void mouseDragged(double x, double y, int button){
        if(button!=0&&button!=1)return;
        int sx = Math.max(x1, Math.min(x2, x1+(int) (x/blockSize)));
        int sy = Math.max(y1, Math.min(y2, y1+(int) (y/blockSize)));
        int bx = sx*xAxis.x+sy*yAxis.x+layer*axis.x;
        int by = sx*xAxis.y+sy*yAxis.y+layer*axis.y;
        int bz = sx*xAxis.z+sy*yAxis.z+layer*axis.z;
        editor.getSelectedTool(0).mouseDragged(this, editorSpace, bx, by, bz, button);
    }
    public boolean isSelected(int x, int y){
        x+=x1;
        y+=y1;
        if(x<x1||y<y1||x>x2||y>y2)return false;
        int bx = x*xAxis.x+y*yAxis.x+layer*axis.x;
        int by = x*xAxis.y+y*yAxis.y+layer*axis.y;
        int bz = x*xAxis.z+y*yAxis.z+layer*axis.z;
        return editor.isSelected(0, bx, by, bz);
    }
    private boolean affects(Suggestion s, int x, int y){
        x+=x1;
        y+=y1;
        if(x<x1||y<y1||x>x2||y>y2)return false;
        int bx = x*xAxis.x+y*yAxis.x+layer*axis.x;
        int by = x*xAxis.y+y*yAxis.y+layer*axis.y;
        int bz = x*xAxis.z+y*yAxis.z+layer*axis.z;
        return s.affects(bx, by, bz);
    }
    @Override
    public String getTooltip(){
        synchronized(synchronizer){
            if(mouseover==null)return null;
            int bx = mouseover[0]*xAxis.x+mouseover[1]*yAxis.x+layer*axis.x;
            int by = mouseover[0]*xAxis.y+mouseover[1]*yAxis.y+layer*axis.y;
            int bz = mouseover[0]*xAxis.z+mouseover[1]*yAxis.z+layer*axis.z;
            if(!multiblock.contains(bx, by, bz))return null;
            Block block = multiblock.getBlock(bx,by,bz);
            String tooltip = "";
            for(Object o : multiblock.decals){
                Decal decal = (Decal)o;
                if(decal.x==bx&&decal.y==by&&decal.z==bz){
                    tooltip+=decal.getTooltip()+"\n";
                }
            }
            if(block==null){
                return tooltip.isEmpty()?null:tooltip;
            }else{
                return tooltip.isEmpty()?block.getTooltip(multiblock):(tooltip+"\n"+block.getTooltip(multiblock));
            }
        }
    }
    @Override
    public double getTooltipOffsetX(){
        synchronized(synchronizer){
            return mouseover!=null?mouseover[0]*blockSize:0;
        }
    }
    @Override
    public double getTooltipOffsetY(){
        synchronized(synchronizer){
            return mouseover!=null?(mouseover[1]+1)*blockSize:height;
        }
    }
}