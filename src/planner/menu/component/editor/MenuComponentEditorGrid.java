package planner.menu.component.editor;
import planner.Core;
import planner.menu.MenuEdit;
import multiblock.Block;
import multiblock.Multiblock;
import multiblock.action.MSRAllShieldsAction;
import multiblock.action.MSRShieldAction;
import multiblock.action.SFRSourceAction;
import multiblock.action.MSRSourceAction;
import multiblock.action.SFRAllShieldsAction;
import multiblock.action.SFRShieldAction;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import org.lwjgl.glfw.GLFW;
import planner.editor.suggestion.Suggestion;
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
    private int[] mouseover;
    private static final int resonatingTime = 60;
    private static final float resonatingMin = .25f;
    private static final float resonatingMax = .5f;
    private int resonatingTick = 0;
    private float resonatingAlpha = 0;
    public MenuComponentEditorGrid(int x, int y, int blockSize, MenuEdit editor, Multiblock multiblock, int layer){
        super(x, y, blockSize*multiblock.getX(), blockSize*multiblock.getZ());
        this.multiblock = multiblock;
        this.layer = layer;
        this.editor = editor;
        this.blockSize = blockSize;
    }
    @Override
    public void tick(){
        if(!(gui.mouseWereDown.contains(0))){
            editor.getSelectedTool(0).mouseReset(0);
        }
        if(!(gui.mouseWereDown.contains(1))){
            editor.getSelectedTool(0).mouseReset(1);
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
                if(mouseover[0]<0||mouseover[1]<0||mouseover[0]>=multiblock.getX()||mouseover[1]>=multiblock.getZ())mouseover = null;
            }
            blockSize = (int) Math.min(width/multiblock.getX(), height/multiblock.getZ());
            Core.applyColor(Core.theme.getEditorListBorderColor());
            drawRect(x,y,x+width,y+height,0);
            if(mouseover!=null){
                Core.applyColor(Core.theme.getEditorListBorderColor().brighter());//TODO .brighter()
                drawRect(x+mouseover[0]*blockSize, y+mouseover[1]*blockSize, x+(mouseover[0]+1)*blockSize, y+(mouseover[1]+1)*blockSize, 0);
            }
        }
        Core.applyColor(Core.theme.getTextColor());
        for(int x = 0; x<=multiblock.getX(); x++){
            double border = blockSize/32d;
            double X = this.x+x*blockSize;
            drawRect(X-(x==0?0:border), y, X+(x==multiblock.getX()?0:border), y+height, 0);
        }
        for(int z = 0; z<=multiblock.getZ(); z++){
            double border = blockSize/32d;
            double Y = this.y+z*blockSize;
            drawRect(x, Y-(z==0?0:border), x+width, Y+(z==multiblock.getZ()?0:border), 0);
        }
        for(int x = 0; x<multiblock.getX(); x++){
            for(int z = 0; z<multiblock.getZ(); z++){
                Block block = multiblock.getBlock(x, layer, z);
                double X = this.x+x*blockSize;
                double Y = this.y+z*blockSize;
                double border = blockSize/8;
                if(block!=null){
                    block.render(X, Y, blockSize, blockSize, true, multiblock);
                    if((multiblock instanceof OverhaulMSR&&((multiblock.overhaul.fissionmsr.Block)block).fuel==editor.getSelectedOverMSRFuel(0))||(multiblock instanceof OverhaulSFR&&((multiblock.overhaul.fissionsfr.Block)block).fuel==editor.getSelectedOverSFRFuel(0))){
                        Core.applyColor(Core.theme.getSelectionColor(), resonatingAlpha);
                        Renderer2D.drawRect(X, Y, X+blockSize, Y+blockSize, 0);
                    }
                }
                if(multiblock instanceof OverhaulFusionReactor&&((OverhaulFusionReactor)multiblock).getLocationCategory(x, layer, z)==OverhaulFusionReactor.LocationCategory.PLASMA){
                    Core.applyWhite();
                    drawRect(X, Y, X+blockSize, Y+blockSize, ImageStash.instance.getTexture("/textures/overhaul/fusion/plasma.png"));
                }
                if(Core.isControlPressed()){
                    if(block==null||(Core.isShiftPressed()&&block.canBeQuickReplaced())){
                        if(multiblock.isValid(editor.getSelectedBlock(0), x, layer, z)){
                            editor.getSelectedBlock(0).render(X, Y, blockSize, blockSize, false, resonatingAlpha, multiblock);
                        }
                    }
                }
                if(isSelected(x, z)){
                    Core.applyColor(Core.theme.getSelectionColor(), .5f);
                    Renderer2D.drawRect(X, Y, X+blockSize, Y+blockSize, 0);
                    Core.applyColor(Core.theme.getSelectionColor());
                    border = blockSize/8f;
                    boolean top = isSelected(x, z-1);
                    boolean right = isSelected(x+1, z);
                    boolean bottom = isSelected(x, z+1);
                    boolean left = isSelected(x-1, z);
                    if(!top||!left||!isSelected(x-1, z-1)){//top left
                        Renderer2D.drawRect(X, Y, X+border, Y+border, 0);
                    }
                    if(!top){//top
                        Renderer2D.drawRect(X+border, Y, X+blockSize-border, Y+border, 0);
                    }
                    if(!top||!right||!isSelected(x+1, z-1)){//top right
                        Renderer2D.drawRect(X+blockSize-border, Y, X+blockSize, Y+border, 0);
                    }
                    if(!right){//right
                        Renderer2D.drawRect(X+blockSize-border, Y+border, X+blockSize, Y+blockSize-border, 0);
                    }
                    if(!bottom||!right||!isSelected(x+1, z+1)){//bottom right
                        Renderer2D.drawRect(X+blockSize-border, Y+blockSize-border, X+blockSize, Y+blockSize, 0);
                    }
                    if(!bottom){//bottom
                        Renderer2D.drawRect(X+border, Y+blockSize-border, X+blockSize-border, Y+blockSize, 0);
                    }
                    if(!bottom||!left||!isSelected(x-1, z+1)){//bottom left
                        Renderer2D.drawRect(X, Y+blockSize-border, X+border, Y+blockSize, 0);
                    }
                    if(!left){//left
                        Renderer2D.drawRect(X, Y+border, X+border, Y+blockSize-border, 0);
                    }
                }
                //TODO there's a better way do do this, but this'll do for now
                for(Suggestion s : editor.getSuggestions()){
                    if(s.affects(x, layer, z)){
                        if(s.selected&&s.result!=null){
                            Block b = s.result.getBlock(x, layer, z);
                            Core.applyWhite(resonatingAlpha+.5f);
                            if(b==null){
                                drawRect(X, Y, X+blockSize, Y+blockSize, 0);
                            }else{
                                b.render(X, Y, blockSize, blockSize, false, resonatingAlpha+.5f, s.result);
                            }
                        }
                        Core.applyColor(Core.theme.getGreen());
                        border = blockSize/40f;
                        if(s.selected)border*=3;
                        boolean top = s.affects(x, layer, z-1);
                        boolean right = s.affects(x+1, layer, z);
                        boolean bottom = s.affects(x, layer, z+1);
                        boolean left = s.affects(x-1, layer, z);
                        if(!top||!left||!s.affects(x-1, layer, z-1)){//top left
                            Renderer2D.drawRect(X, Y, X+border, Y+border, 0);
                        }
                        if(!top){//top
                            Renderer2D.drawRect(X+border, Y, X+blockSize-border, Y+border, 0);
                        }
                        if(!top||!right||!s.affects(x+1, layer, z-1)){//top right
                            Renderer2D.drawRect(X+blockSize-border, Y, X+blockSize, Y+border, 0);
                        }
                        if(!right){//right
                            Renderer2D.drawRect(X+blockSize-border, Y+border, X+blockSize, Y+blockSize-border, 0);
                        }
                        if(!bottom||!right||!s.affects(x+1, layer, z+1)){//bottom right
                            Renderer2D.drawRect(X+blockSize-border, Y+blockSize-border, X+blockSize, Y+blockSize, 0);
                        }
                        if(!bottom){//bottom
                            Renderer2D.drawRect(X+border, Y+blockSize-border, X+blockSize-border, Y+blockSize, 0);
                        }
                        if(!bottom||!left||!s.affects(x-1, layer, z+1)){//bottom left
                            Renderer2D.drawRect(X, Y+blockSize-border, X+border, Y+blockSize, 0);
                        }
                        if(!left){//left
                            Renderer2D.drawRect(X, Y+border, X+border, Y+blockSize-border, 0);
                        }
                    }
                }
            }
        }
        editor.getSelectedTool(0).drawGhosts(layer, x, y, width, height, blockSize, (editor.getSelectedBlock(0)==null?0:Core.getTexture(editor.getSelectedBlock(0).getTexture())));
        synchronized(synchronizer){
            if(mouseover!=null){
                double X = this.x+mouseover[0]*blockSize;
                double Y = this.y+mouseover[1]*blockSize;
                double border = blockSize/8;
                Core.applyColor(Core.theme.getEditorListBorderColor(), .6375f);
                drawRect(X, Y, X+border, Y+border, 0);
                drawRect(X+blockSize-border, Y, X+blockSize, Y+border, 0);
                drawRect(X, Y+blockSize-border, X+border, Y+blockSize, 0);
                drawRect(X+blockSize-border, Y+blockSize-border, X+blockSize, Y+blockSize, 0);
                Core.applyColor(Core.theme.getTextColor(), .6375f);
                drawRect(X+border, Y, X+blockSize-border, Y+border, 0);
                drawRect(X+border, Y+blockSize-border, X+blockSize-border, Y+blockSize, 0);
                drawRect(X, Y+border, X+border, Y+blockSize-border, 0);
                drawRect(X+blockSize-border, Y+border, X+blockSize, Y+blockSize-border, 0);
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
        int blockX = Math.max(0, Math.min(multiblock.getX()-1, (int) (x/blockSize)));
        int blockZ = Math.max(0, Math.min(multiblock.getZ()-1, (int) (y/blockSize)));
        editor.getSelectedTool(0).mouseMoved(this, blockX, layer, blockZ);
    }
    @Override
    public void onMouseMovedElsewhere(double x, double y){
        super.onMouseMovedElsewhere(x, y);
        synchronized(synchronizer){
            if(mouseover!=null)editor.getSelectedTool(0).mouseMovedElsewhere(this);
            mouseover = null;
        }
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(Double.isNaN(x)||Double.isNaN(y)){
            return;
        }
        int blockX = Math.max(0, Math.min(multiblock.getX()-1, (int) (x/blockSize)));
        int blockZ = Math.max(0, Math.min(multiblock.getZ()-1, (int) (y/blockSize)));
        if(pressed){
            if(editor.getSelectedTool(0).isEditTool()&&multiblock instanceof OverhaulSFR&&Core.isShiftPressed()&&((multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, layer, blockZ))!=null&&((multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, layer, blockZ)).isFuelCell()&&!((multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, layer, blockZ)).fuel.selfPriming){
                multiblock.overhaul.fissionsfr.Block b = (multiblock.overhaul.fissionsfr.Block) multiblock.getBlock(blockX, layer, blockZ);
                if(b!=null){
                    int index = Core.configuration.overhaul.fissionSFR.allSources.indexOf(b.source);
                    index--;
                    if(index>=Core.configuration.overhaul.fissionSFR.allSources.size())index = 0;
                    if(index<-1)index = Core.configuration.overhaul.fissionSFR.allSources.size()-1;
                    editor.action(new SFRSourceAction(b, index==-1?null:Core.configuration.overhaul.fissionSFR.allSources.get(index)), true);
                }
            }else if(editor.getSelectedTool(0).isEditTool()&&multiblock instanceof OverhaulMSR&&Core.isShiftPressed()&&((multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, layer, blockZ))!=null&&((multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, layer, blockZ)).isFuelVessel()&&!((multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, layer, blockZ)).fuel.selfPriming){
                multiblock.overhaul.fissionmsr.Block b = (multiblock.overhaul.fissionmsr.Block) multiblock.getBlock(blockX, layer, blockZ);
                if(b!=null){
                    int index = Core.configuration.overhaul.fissionMSR.allSources.indexOf(b.source);
                    index--;
                    if(index>=Core.configuration.overhaul.fissionMSR.allSources.size())index = 0;
                    if(index<-1)index = Core.configuration.overhaul.fissionMSR.allSources.size()-1;
                    editor.action(new MSRSourceAction(b, index==-1?null:Core.configuration.overhaul.fissionMSR.allSources.get(index)), true);
                }
            }else if(editor.getSelectedTool(0).isEditTool()&&multiblock instanceof OverhaulSFR&&Core.isShiftPressed()&&((multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, layer, blockZ))!=null&&((multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, layer, blockZ)).template.shield){
                multiblock.overhaul.fissionsfr.Block b = (multiblock.overhaul.fissionsfr.Block) multiblock.getBlock(blockX, layer, blockZ);
                if(b!=null){
                    if(Core.isControlPressed())editor.action(new SFRAllShieldsAction(!b.closed), true);
                    else editor.action(new SFRShieldAction(b), true);
                }
            }else if(editor.getSelectedTool(0).isEditTool()&&multiblock instanceof OverhaulMSR&&Core.isShiftPressed()&&((multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, layer, blockZ))!=null&&((multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, layer, blockZ)).template.shield){
                multiblock.overhaul.fissionmsr.Block b = (multiblock.overhaul.fissionmsr.Block) multiblock.getBlock(blockX, layer, blockZ);
                if(b!=null){
                    if(Core.isControlPressed())editor.action(new MSRAllShieldsAction(!b.closed), true);
                    else editor.action(new MSRShieldAction(b), true);
                }
            }else{
                if(button==GLFW.GLFW_MOUSE_BUTTON_MIDDLE){
                    editor.setSelectedBlock(multiblock.getBlock(blockX, layer, blockZ));
                }
                editor.getSelectedTool(0).mousePressed(this, blockX, layer, blockZ, button);
            }
        }else{
            editor.getSelectedTool(0).mouseReleased(this, blockX, layer, blockZ, button);
        }
    }
    public void mouseDragged(double x, double y, int button){
        if(button!=0&&button!=1)return;
        int blockX = Math.max(0, Math.min(multiblock.getX()-1, (int) (x/blockSize)));
        int blockZ = Math.max(0, Math.min(multiblock.getZ()-1, (int) (y/blockSize)));
        editor.getSelectedTool(0).mouseDragged(this, blockX, layer, blockZ, button);
    }
    public boolean isSelected(int x, int z){
        return editor.isSelected(0, x,layer,z);
    }
    @Override
    public String getTooltip(){
        synchronized(synchronizer){
            if(mouseover==null)return null;
            Block block = multiblock.getBlock(mouseover[0],layer,mouseover[1]);
            return block==null?null:block.getTooltip(multiblock);
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