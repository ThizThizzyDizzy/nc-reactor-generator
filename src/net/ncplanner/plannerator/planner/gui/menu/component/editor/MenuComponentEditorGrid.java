package net.ncplanner.plannerator.planner.gui.menu.component.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.BlockPosConsumer;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.action.MSRAllShieldsAction;
import net.ncplanner.plannerator.multiblock.editor.action.MSRSourceAction;
import net.ncplanner.plannerator.multiblock.editor.action.MSRToggleAction;
import net.ncplanner.plannerator.multiblock.editor.action.SFRAllShieldsAction;
import net.ncplanner.plannerator.multiblock.editor.action.SFRSourceAction;
import net.ncplanner.plannerator.multiblock.editor.action.SFRToggleAction;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestion;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.MenuEdit;
import static org.lwjgl.glfw.GLFW.*;
public class MenuComponentEditorGrid extends Component{
    private final Object synchronizer = new Object();
    private final Multiblock multiblock;
    public final int layer;
    public final MenuEdit editor;
    public int blockSize;
    public int[] mouseover;
    private static final int resonatingTime = 60;
    private static final float resonatingMin = .25f;
    private static final float resonatingMax = .5f;
    private float resonatingTick = 0;
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
    public void render2d(double deltaTime){
        if(glfwGetMouseButton(Core.window, GLFW_MOUSE_BUTTON_LEFT)==GLFW_RELEASE){
            editor.getSelectedTool(0).mouseReset(editorSpace, 0);
        }
        if(glfwGetMouseButton(Core.window, GLFW_MOUSE_BUTTON_RIGHT)==GLFW_RELEASE){
            editor.getSelectedTool(0).mouseReset(editorSpace, 1);
        }
        resonatingTick+=deltaTime*20;
        if(resonatingTick>resonatingTime)resonatingTick-=resonatingTime;
        resonatingAlpha = (float) (-Math.cos(2*Math.PI*resonatingTick/resonatingTime)/(2/(resonatingMax-resonatingMin))+(resonatingMax+resonatingMin)/2);
        super.render2d(deltaTime);
    }
    private void forEachMouseover(BlockPosConsumer func){
        int[] bmo = mouseover==null?null:toBlockCoords(mouseover[0], mouseover[1]);
        if(mouseover==null){
            for(Component comp : editor.multibwauk.components){
                if(comp instanceof MenuComponentEditorGrid){
                    MenuComponentEditorGrid grid = (MenuComponentEditorGrid)comp;
                    if(grid==this)continue;
                    if(grid.axis!=axis)continue;
                    if(grid.x1!=x1)continue;
                    if(grid.x2!=x2)continue;
                    if(grid.y1!=y1)continue;
                    if(grid.y2!=y2)continue;
                    if(grid.mouseover==null)continue;
                    bmo = grid.toBlockCoords(grid.mouseover[0], grid.mouseover[1]);
                    break;
                }
            }
        }
        if(bmo==null)return;
        editor.getSymmetry().apply(bmo[0], bmo[1], bmo[2], multiblock.getBoundingBox(), func);
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        synchronized(synchronizer){
            if(!isMouseFocused)mouseover = null;
            if(mouseover!=null){
                if(mouseover[0]<0||mouseover[1]<0||mouseover[0]>blocksWide-1||mouseover[1]>blocksHigh-1)mouseover = null;
            }
            blockSize = (int) Math.min(width/blocksWide, height/blocksHigh);
            renderer.setColor(Core.theme.getEditorBackgroundColor());
            renderer.fillRect(x,y,x+width,y+height);
            forEachMouseover((bx, by, bz) -> {
                int[] coords = toMouseCoords(bx, by, bz);
                if(coords==null)return;
                renderer.setColor(Core.theme.getEditorBackgroundMouseoverColor());
                renderer.fillRect(x+coords[0]*blockSize, y+coords[1]*blockSize, x+(coords[0]+1)*blockSize, y+(coords[1]+1)*blockSize);
            });
        }
        renderer.setColor(Core.theme.getEditorGridColor());
        for(int x = 0; x<=blocksWide; x++){
            float border = blockSize/32f;
            float X = this.x+x*blockSize;
            renderer.fillRect(X-(x==0?0:border), y, X+(x==blocksWide?0:border), y+height);
        }
        for(int y = 0; y<=blocksHigh; y++){
            float border = blockSize/32f;
            float Y = this.y+y*blockSize;
            renderer.fillRect(x, Y-(y==0?0:border), x+width, Y+(y==blocksHigh?0:border));
        }
        for(int x = 0; x<blocksWide; x++){
            for(int y = 0; y<blocksHigh; y++){
                int bx = (x+x1)*xAxis.x+(y+y1)*yAxis.x+layer*axis.x;
                int by = (x+x1)*xAxis.y+(y+y1)*yAxis.y+layer*axis.y;
                int bz = (x+x1)*xAxis.z+(y+y1)*yAxis.z+layer*axis.z;
                if(!multiblock.contains(bx, by, bz))continue;
                Block block = multiblock.getBlock(bx, by, bz);
                float X = this.x+x*blockSize;
                float Y = this.y+y*blockSize;
                if(block!=null){
                    block.render(renderer, X, Y, blockSize, blockSize, editor.overlays, multiblock);
                    boolean recipeMatches = false;
                    if(multiblock instanceof OverhaulSFR){
                        net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block bl = (net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)block;
                        if(bl.recipe!=null&&bl.recipe==editor.getSelectedOverhaulSFRBlockRecipe(0))recipeMatches = true;
                    }
                    if(multiblock instanceof OverhaulMSR){
                        net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block bl = (net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)block;
                        if(bl.recipe!=null&&bl.recipe==editor.getSelectedOverhaulMSRBlockRecipe(0))recipeMatches = true;
                    }
                    if(multiblock instanceof OverhaulFusionReactor){
                        net.ncplanner.plannerator.multiblock.overhaul.fusion.Block bl = (net.ncplanner.plannerator.multiblock.overhaul.fusion.Block)block;
                        if(bl.recipe!=null&&bl.recipe==editor.getSelectedOverhaulFusionBlockRecipe(0))recipeMatches = true;
                    }
                    if(recipeMatches){
                        renderer.setColor(Core.theme.getSelectionColor(), resonatingAlpha);
                        renderer.fillRect(X, Y, X+blockSize, Y+blockSize);
                    }
                }
                if(multiblock instanceof OverhaulFusionReactor&&((OverhaulFusionReactor)multiblock).getLocationCategory(bx, by, bz)==OverhaulFusionReactor.LocationCategory.PLASMA){
                    renderer.setWhite();
                    renderer.drawImage(TextureManager.getImage("overhaul/fusion/plasma"), X, Y, X+blockSize, Y+blockSize);
                }
                if(Core.isControlPressed()){
                    if(block==null||(Core.isShiftPressed()&&block.canBeQuickReplaced())){
                        if(editorSpace.isSpaceValid(editor.getSelectedBlock(0), bx, by, bz)&&multiblock.isValid(editor.getSelectedBlock(0), bx, by, bz)){
                            editor.getSelectedBlock(0).render(renderer, X, Y, blockSize, blockSize, null, resonatingAlpha, multiblock);
                        }
                    }
                }
                synchronized(multiblock.decals){
                    for(Object o : multiblock.decals){
                        Decal decal = (Decal)o;
                        if(decal.x==bx&&decal.y==by&&decal.z==bz){
                            decal.render(renderer, X, Y, blockSize);
                        }
                    }
                }
                for(EditorOverlay overlay : editor.overlays){
                    if(!overlay.active)continue;
                    for(Object o : overlay.decals){
                        Decal decal = (Decal)o;
                        if(decal.x==bx&&decal.y==by&&decal.z==bz){
                            decal.render(renderer, X, Y, blockSize);
                        }
                    }
                }
                if(isSelected(x, y)){
                    renderer.setColor(Core.theme.getSelectionColor(), .5f);
                    renderer.fillRect(X, Y, X+blockSize, Y+blockSize);
                    renderer.setColor(Core.theme.getSelectionColor());
                    float border = blockSize/8f;
                    boolean top = isSelected(x, y-1);
                    boolean right = isSelected(x+1, y);
                    boolean bottom = isSelected(x, y+1);
                    boolean left = isSelected(x-1, y);
                    if(!top||!left||!isSelected(x-1, y-1)){//top left
                        renderer.fillRect(X, Y, X+border, Y+border);
                    }
                    if(!top){//top
                        renderer.fillRect(X+border, Y, X+blockSize-border, Y+border);
                    }
                    if(!top||!right||!isSelected(x+1, y-1)){//top right
                        renderer.fillRect(X+blockSize-border, Y, X+blockSize, Y+border);
                    }
                    if(!right){//right
                        renderer.fillRect(X+blockSize-border, Y+border, X+blockSize, Y+blockSize-border);
                    }
                    if(!bottom||!right||!isSelected(x+1, y+1)){//bottom right
                        renderer.fillRect(X+blockSize-border, Y+blockSize-border, X+blockSize, Y+blockSize);
                    }
                    if(!bottom){//bottom
                        renderer.fillRect(X+border, Y+blockSize-border, X+blockSize-border, Y+blockSize);
                    }
                    if(!bottom||!left||!isSelected(x-1, y+1)){//bottom left
                        renderer.fillRect(X, Y+blockSize-border, X+border, Y+blockSize);
                    }
                    if(!left){//left
                        renderer.fillRect(X, Y+border, X+border, Y+blockSize-border);
                    }
                }
                {
                    boolean stl = false, st = false, str = false, sr = false, sbr = false, sb = false, sbl = false, sl = false, sg = false;
                    for(Suggestion s : editor.getSuggestions()){
                        if(affects(s, x, y)){
                            if(s.selected&&s.result!=null){
                                Block b = s.result.getBlock(bx, by, bz);
                                renderer.setWhite(resonatingAlpha+.5f);
                                if(b==null){
                                    renderer.fillRect(X, Y, X+blockSize, Y+blockSize);
                                }else{
                                    b.render(renderer, X, Y, blockSize, blockSize, null, resonatingAlpha+.5f, s.result);
                                }
                            }
                            renderer.setColor(Core.theme.getSuggestionOutlineColor());
                            if(s.selected)sg = true;
                            boolean top = affects(s, x, y-1);
                            boolean right = affects(s, x+1, y);
                            boolean bottom = affects(s, x, y+1);
                            boolean left = affects(s, x-1, y);
                            stl |= (!top||!left||!affects(s, x-1, y-1));
                            st |= (!top);
                            str |= (!top||!right||!affects(s, x+1, y-1));
                            sr |= (!right);
                            sbr |= (!bottom||!right||!affects(s, x+1, y+1));
                            sb |= (!bottom);
                            sbl |= (!bottom||!left||!affects(s, x-1, y+1));
                            sl |= (!left);
                        }
                    }
                    renderer.setColor(Core.theme.getSuggestionOutlineColor());
                    float border = blockSize/40f;
                    if(sg)border*=3;
                    if(stl)renderer.fillRect(X, Y, X+border, Y+border);
                    if(st)renderer.fillRect(X+border, Y, X+blockSize-border, Y+border);
                    if(str)renderer.fillRect(X+blockSize-border, Y, X+blockSize, Y+border);
                    if(sr)renderer.fillRect(X+blockSize-border, Y+border, X+blockSize, Y+blockSize-border);
                    if(sbr)renderer.fillRect(X+blockSize-border, Y+blockSize-border, X+blockSize, Y+blockSize);
                    if(sb)renderer.fillRect(X+border, Y+blockSize-border, X+blockSize-border, Y+blockSize);
                    if(sbl)renderer.fillRect(X, Y+blockSize-border, X+border, Y+blockSize);
                    if(sl)renderer.fillRect(X, Y+border, X+border, Y+blockSize-border);
                }
            }
        }
        editor.getSelectedTool(0).drawGhosts(renderer, editorSpace, x1, y1, x2, y2, blocksWide, blocksHigh, axis, layer, x, y, width, height, blockSize, (editor.getSelectedBlock(0)==null?null:editor.getSelectedBlock(0).getTexture()));
        synchronized(synchronizer){
            forEachMouseover((x, y, z) -> {
                int[] coords = toMouseCoords(x, y, z);
                if(coords==null)return;
                float X = this.x+coords[0]*blockSize;
                float Y = this.y+coords[1]*blockSize;
                float border = blockSize/8;
                renderer.setColor(Core.theme.getEditorMouseoverLightColor(), .6375f);
                renderer.fillRect(X, Y, X+border, Y+border);
                renderer.fillRect(X+blockSize-border, Y, X+blockSize, Y+border);
                renderer.fillRect(X, Y+blockSize-border, X+border, Y+blockSize);
                renderer.fillRect(X+blockSize-border, Y+blockSize-border, X+blockSize, Y+blockSize);
                renderer.setColor(Core.theme.getEditorMouseoverDarkColor(), .6375f);
                renderer.fillRect(X+border, Y, X+blockSize-border, Y+border);
                renderer.fillRect(X+border, Y+blockSize-border, X+blockSize-border, Y+blockSize);
                renderer.fillRect(X, Y+border, X+border, Y+blockSize-border);
                renderer.fillRect(X+blockSize-border, Y+border, X+blockSize, Y+blockSize-border);
            });
            if(mouseover!=null){
                float X = this.x+mouseover[0]*blockSize;
                float Y = this.y+mouseover[1]*blockSize;
                float border = blockSize/8;
                renderer.setColor(Core.theme.getEditorMouseoverLineColor(), 0.6375f);
                renderer.fillRect(this.x, Y+blockSize/2-border/2, X, Y+blockSize/2+border/2);
                renderer.fillRect(X+blockSize, Y+blockSize/2-border/2, this.x+this.width, Y+blockSize/2+border/2);
                renderer.fillRect(X+blockSize/2-border/2, this.y, X+blockSize/2+border/2, Y);
                renderer.fillRect(X+blockSize/2-border/2, Y+blockSize, X+blockSize/2+border/2, this.y+this.height);
            }
            for(Component comp : editor.multibwauk.components){
                if(comp instanceof MenuComponentEditorGrid){
                    MenuComponentEditorGrid grid = (MenuComponentEditorGrid)comp;
                    if(grid==this)continue;
                    if(grid.axis!=axis)continue;
                    if(grid.x1!=x1)continue;
                    if(grid.x2!=x2)continue;
                    if(grid.y1!=y1)continue;
                    if(grid.y2!=y2)continue;
                    if(grid.mouseover==null)continue;
                    float X = this.x+grid.mouseover[0]*blockSize;
                    float Y = this.y+grid.mouseover[1]*blockSize;
                    float border = blockSize/6;
                    renderer.setColor(Core.theme.getEditorMouseoverLineColor(), 0.6375f);
                    renderer.fillRect(X+blockSize/2-border/2, Y+blockSize/2-border/2, X+blockSize/2+border/2, Y+blockSize/2+border/2);
                }
            }
        }
    }
    public int[] toBlockCoords(int sx, int sy){
        int bx = sx*xAxis.x+sy*yAxis.x+layer*axis.x;
        int by = sx*xAxis.y+sy*yAxis.y+layer*axis.y;
        int bz = sx*xAxis.z+sy*yAxis.z+layer*axis.z;
        return new int[]{bx,by,bz};
    }
    public int[] toMouseCoords(int bx, int by, int bz){
        int sx = xAxis.x*bx+xAxis.y*by+xAxis.z*bz;
        int sy = yAxis.x*bx+yAxis.y*by+yAxis.z*bz;
        if(axis.x!=0&&bx!=layer)return null;
        if(axis.y!=0&&by!=layer)return null;
        if(axis.z!=0&&bz!=layer)return null;
        if(sx<x1)return null;
        if(sx>x2)return null;
        if(sy<y1)return null;
        if(sy>y2)return null;
        return new int[]{sx,sy};
    }
    @Override
    public void onCursorMoved(double x, double y){
        super.onCursorMoved(x, y);
        synchronized(synchronizer){
            mouseover = new int[]{(int)x/blockSize,(int)y/blockSize};
        }
        if(glfwGetMouseButton(Core.window, GLFW_MOUSE_BUTTON_LEFT)==GLFW_PRESS)mouseDragged(x, y, 0);
        if(glfwGetMouseButton(Core.window, GLFW_MOUSE_BUTTON_RIGHT)==GLFW_PRESS)mouseDragged(x, y, 1);
        if(glfwGetMouseButton(Core.window, GLFW_MOUSE_BUTTON_MIDDLE)==GLFW_PRESS)mouseDragged(x, y, 2);
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
    public void onCursorExited(){
        super.onCursorExited();
        synchronized(synchronizer){
            if(mouseover!=null)editor.getSelectedTool(0).mouseMovedElsewhere(this, editorSpace);
            mouseover = null;
        }
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        super.onMouseButton(x, y, button, action, mods);
        int sx = Math.max(x1, Math.min(x2, x1+(int) (x/blockSize)));
        int sy = Math.max(y1, Math.min(y2, y1+(int) (y/blockSize)));
        int bx = sx*xAxis.x+sy*yAxis.x+layer*axis.x;
        int by = sx*xAxis.y+sy*yAxis.y+layer*axis.y;
        int bz = sx*xAxis.z+sy*yAxis.z+layer*axis.z;
        if(action==GLFW_PRESS){
            Block block = multiblock.getBlock(bx, by, bz);
            boolean didSomething = false;
            if(editor.getSelectedTool(0).isEditTool()&&Core.isShiftPressed()&&block!=null){
                if(editor.getSelectedTool(0).isEditTool()&&multiblock instanceof OverhaulSFR&&Core.isShiftPressed()){
                    net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)block;
                    if(b.template.shield||b.template.parent!=null||b.template.coolantVent){
                        if(Core.isControlPressed()&&b.template.shield)editor.action(new SFRAllShieldsAction(!b.isToggled), true);
                        else editor.action(new SFRToggleAction(b), true);
                        didSomething = true;
                    }
                    if(b.template.fuelCell&&(b.template.fuelCellHasBaseStats||b.recipe!=null)){
                        boolean self = b.recipe==null?b.template.fuelCellSelfPriming:b.recipe.fuelCellSelfPriming;
                        if(!self){
                            ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block> sources = new ArrayList<>();
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block possible : multiblock.getConfiguration().overhaul.fissionSFR.allBlocks){
                                if(possible.source)sources.add(possible);
                            }
                            sources.sort((o1, o2) -> {
                                return (int)((o1.sourceEfficiency-o2.sourceEfficiency)*10000);
                            });
                            int idx = sources.size()-1;
                            if(b.source!=null)idx = sources.indexOf(b.source.template)-1;
                            if(idx<-1)idx = sources.size()-1;
                            editor.action(new SFRSourceAction(b, idx==-1?null:sources.get(idx)), true);
                            didSomething = true;
                        }
                    }
                }
                if(editor.getSelectedTool(0).isEditTool()&&multiblock instanceof OverhaulMSR&&Core.isShiftPressed()){
                    net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block) block;
                    if(b.template.shield||b.template.parent!=null){
                        if(Core.isControlPressed()&&b.template.shield)editor.action(new MSRAllShieldsAction(!b.isToggled), true);
                        else editor.action(new MSRToggleAction(b), true);
                        didSomething = true;
                    }
                    if(b.template.fuelVessel&&(b.template.fuelVesselHasBaseStats||b.recipe!=null)){
                        boolean self = b.recipe==null?b.template.fuelVesselSelfPriming:b.recipe.fuelVesselSelfPriming;
                        if(!self){
                            ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block> sources = new ArrayList<>();
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block possible : multiblock.getConfiguration().overhaul.fissionMSR.allBlocks){
                                if(possible.source)sources.add(possible);
                            }
                            sources.sort((o1, o2) -> {
                                return (int)((o1.sourceEfficiency-o2.sourceEfficiency)*10000);
                            });
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
                if(button==GLFW_MOUSE_BUTTON_MIDDLE){
                    editor.setSelectedBlock(block);
                }
                editor.getSelectedTool(0).mousePressed(this, editorSpace, bx, by, bz, button);
            }
        }else{
            if(isMouseFocused)editor.getSelectedTool(0).mouseReleased(this, editorSpace, bx, by, bz, button);
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
    public float getTooltipOffsetX(){
        synchronized(synchronizer){
            return mouseover!=null?mouseover[0]*blockSize:0;
        }
    }
    @Override
    public float getTooltipOffsetY(){
        synchronized(synchronizer){
            return mouseover!=null?(mouseover[1]+1)*blockSize:height;
        }
    }
}