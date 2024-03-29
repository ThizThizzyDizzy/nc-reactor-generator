package net.ncplanner.plannerator.multiblock;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.Queue;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
import net.ncplanner.plannerator.planner.ncpf.module.RecipesBlockModule;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
public abstract class AbstractBlock implements Pinnable{
    protected NCPFConfigurationContainer configuration;
    public int x;
    public int y;
    public int z;
    private Image grayscaleTexture = null;
    public AbstractBlock(NCPFConfigurationContainer configuration, int x, int y, int z){
        this.configuration = configuration;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public abstract AbstractBlock newInstance(int x, int y, int z);
    public abstract void copyProperties(AbstractBlock other);
    public Image getBaseTexture(){
        return getTemplate().getTexture();
    }
    public Image getTexture(){
        return getTemplate().getDisplayTexture();
    }
    public String getBaseName(){
        return getTemplate().getName();
    }
    public String getName(){
        return getTemplate().getDisplayName();
    }
    private Image getGrayscaleTexture(){
        if(grayscaleTexture!=null)return grayscaleTexture;
        Image img = getTexture();
        if(img==null)return null;
        Image grayscale = new Image(img.getWidth(), img.getHeight());
        for(int x = 0; x<img.getWidth(); x++){
            for(int y = 0; y<img.getHeight(); y++){
                Color c = img.getColor(x, y);
                grayscale.setColor(x, y, Color.fromHSB(c.getHue(), c.getSaturation()*.25f, c.getBrightness(), c.getAlpha()));
            }
        }
        return grayscaleTexture = grayscale;
    }
    public abstract NCPFElement getTemplate();
    @Override
    public String getPinnedName(){
        return getTemplate().getPinnedName();
    }
    public abstract void clearData();
    public <T extends AbstractBlock> Queue<T> getAdjacent(Multiblock<T> multiblock){
        Queue<T> adjacent = new Queue<>();
        for(Direction direction : Direction.values()){
            if(!multiblock.contains(x+direction.x, y+direction.y, z+direction.z))continue;
            T b = multiblock.getBlock(x+direction.x, y+direction.y, z+direction.z);
            if(b!=null)adjacent.enqueue(b);
        }
        return adjacent;
    }
    public <T extends AbstractBlock> Queue<T> getActiveAdjacent(Multiblock<T> multiblock){
        Queue<T> adjacent = new Queue<>();
        for(Direction direction : Direction.values()){
            if(!multiblock.contains(x+direction.x, y+direction.y, z+direction.z))continue;
            T b = multiblock.getBlock(x+direction.x, y+direction.y, z+direction.z);
            if(b!=null&&b.isActive())adjacent.enqueue(b);
        }
        return adjacent;
    }
    public abstract String getTooltip(Multiblock multiblock);
    public String getListTooltip(){
        String tip = getName();
        for(NCPFModule module : getTemplate().modules.modules.values()){
            if(module instanceof BlockFunctionModule){
                BlockFunctionModule mod = (BlockFunctionModule)module;
                tip+="\n"+mod.getFunctionName();
            }
            if(module instanceof ElementStatsModule){
                ElementStatsModule mod = (ElementStatsModule)module;
                tip+="\n"+mod.getTooltip().trim();
            }
        }
        return tip;
    }
    public void render(Renderer renderer, float x, float y, float width, float height, ArrayList<EditorOverlay> overlays, Multiblock multiblock){
        render(renderer, x, y, width, height, overlays, 1, multiblock);
    }
    public void render(Renderer renderer, float x, float y, float width, float height, ArrayList<EditorOverlay> overlays, float alpha, Multiblock multiblock){
        if(getTexture()==null){
            renderer.setColor(new Color(255,0,255));
            renderer.fillRect(x, y, x+width, y+height);
            renderer.setColor(new Color(0,0,0));
            renderer.fillRect(x, y, x+width/2, y+height/2);
            renderer.fillRect(x+width/2, y+height/2, x+width, y+height);
        }else{
            renderer.setWhite(alpha);
            renderer.drawImage(getTexture(), x, y, x+width, y+height);
        }
        if(overlays!=null){
            for(EditorOverlay overlay : overlays){
                if(overlay.isActive())overlay.render(renderer, x, y, width, height, this, multiblock);
            }
        }
    }
    public void render(Renderer renderer, float x, float y, float z, float width, float height, float depth, ArrayList<EditorOverlay> overlays, float alpha, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc){
        float[] bounds = multiblock.getCubeBounds(this);
        bounds[0] *= width;
        bounds[1] *= height;
        bounds[2] *= depth;
        bounds[3] *= width;
        bounds[4] *= height;
        bounds[5] *= depth;
        if(getTexture()==null){
            renderer.setColor(Core.theme.getBlockUnknownColor(), alpha);
            renderer.drawCube(x+bounds[0], y+bounds[1], z+bounds[2], x+bounds[3], y+bounds[4], z+bounds[5], null, faceRenderFunc);
        }else{
            renderer.setWhite(alpha);
            renderer.drawCube(x+bounds[0], y+bounds[1], z+bounds[2], x+bounds[3], y+bounds[4], z+bounds[5], getTexture(), faceRenderFunc);
        }
//        if(overlays!=null){
//            for(EditorOverlay overlay : overlays){
//                if(overlay.active)overlay.render(renderer, x+bounds[0], y+bounds[1], z+bounds[2], bounds[3], bounds[4], bounds[5], this, multiblock, faceRenderFunc);
//            }
//        }
        if(overlays!=null)renderOverlay(renderer, x+bounds[0], y+bounds[1], z+bounds[2], bounds[3], bounds[4], bounds[5], multiblock,faceRenderFunc);
    }
    public void renderGrayscale(Renderer renderer, float x, float y, float width, float height, ArrayList<EditorOverlay> overlays, Multiblock multiblock){
        renderGrayscale(renderer, x, y, width, height, overlays, 1, multiblock);
    }
    public void renderGrayscale(Renderer renderer, float x, float y, float width, float height, ArrayList<EditorOverlay> overlays, float alpha, Multiblock multiblock){
        if(getGrayscaleTexture()==null){
            renderer.setColor(new Color(191,191,191));
            renderer.fillRect(x, y, x+width, y+height);
            renderer.setColor(new Color(0,0,0));
            renderer.fillRect(x, y, x+width/2, x+height/2);
            renderer.fillRect(x+width/2, y+height/2, x+width, x+height);
        }else{
            renderer.setWhite(alpha);
            renderer.drawImage(getGrayscaleTexture(), x, y, x+width, y+height);
        }
        if(overlays!=null){
            for(EditorOverlay overlay : overlays){
                if(overlay.isActive())overlay.render(renderer, x, y, width, height, this, multiblock);
            }
        }
    }
    @Deprecated
    public abstract void renderOverlay(Renderer renderer, float x, float y, float z, float width, float height, float depth, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc);
    public void drawCircle(Renderer renderer, float x, float y, float width, float height, Color color){
        renderer.setColor(color);
        renderer.drawImage(Core.sourceCircle, x, y, x+width, y+height);
        renderer.setWhite();
    }
    public void drawCircle(Renderer renderer, float x, float y, float z, float width, float height, float depth, Color color, Function<Direction, Boolean> faceRenderFunc){
        boolean px = faceRenderFunc.apply(Direction.PX);
        boolean py = faceRenderFunc.apply(Direction.PY);
        boolean pz = faceRenderFunc.apply(Direction.PZ);
        boolean nx = faceRenderFunc.apply(Direction.NX);
        boolean ny = faceRenderFunc.apply(Direction.NY);
        boolean nz = faceRenderFunc.apply(Direction.NZ);
        if(!px&&!py&&!pz&&!nx&&!ny&&!nz)return;//no faces are actually rendering, save some GL calls
        renderer.setColor(color);
        if(py)drawCircleBit(renderer,x,y,z,width,height,depth);
        if(pz){
            renderer.model(new Matrix4f()
                    .translate(x+width/2, y+height/2, z+depth/2)
                    .rotate((float)MathUtil.toRadians(90), 1, 0, 0)
                    .translate(-x-width/2, -y-height/2, -z-depth/2));
            drawCircleBit(renderer,x,y,z,width,height,depth);
            renderer.resetModelMatrix();
        }
        if(nz){
            renderer.model(new Matrix4f()
                    .translate(x+width/2, y+height/2, z+depth/2)
                    .rotate((float)MathUtil.toRadians(90), -1, 0, 0)
                    .translate(-x-width/2, -y-height/2, -z-depth/2));
            drawCircleBit(renderer,x,y,z,width,height,depth);
            renderer.resetModelMatrix();
        }
        if(nx){
            renderer.model(new Matrix4f()
                    .translate(x+width/2, y+height/2, z+depth/2)
                    .rotate((float)MathUtil.toRadians(90), 0, 0, 1)
                    .translate(-x-width/2, -y-height/2, -z-depth/2));
            drawCircleBit(renderer,x,y,z,width,height,depth);
            renderer.resetModelMatrix();
        }
        if(px){
            renderer.model(new Matrix4f()
                    .translate(x+width/2, y+height/2, z+depth/2)
                    .rotate((float)MathUtil.toRadians(90), 0, 0, -1)
                    .translate(-x-width/2, -y-height/2, -z-depth/2));
            drawCircleBit(renderer,x,y,z,width,height,depth);
            renderer.resetModelMatrix();
        }
        if(ny){
            renderer.model(new Matrix4f()
                    .translate(x+width/2, y+height/2, z+depth/2)
                    .rotate((float)MathUtil.toRadians(180), 0, 0, 1)
                    .translate(-x-width/2, -y-height/2, -z-depth/2));
            drawCircleBit(renderer,x,y,z,width,height,depth);
            renderer.resetModelMatrix();
        }
        renderer.setWhite();
    }
    private void drawCircleBit(Renderer renderer, float x, float y, float z, float width, float height, float depth){
        float innerRadius = width/4;
        float outerRadius = width/8*3;
        int resolution = (int)(Math.max(12,2*Math.PI*outerRadius*100));
        renderer.unbindTexture();
        float angle = 0;
        float thickness = width/32;
        for(int i = 0; i<resolution; i++){
            float nextAngle = angle+(360f/resolution);
            if(nextAngle>=360)nextAngle-=360;
            float inX = (float)(x+width/2+MathUtil.cos(MathUtil.toRadians(angle-90))*innerRadius);
            float inZ = (float)(z+depth/2+MathUtil.sin(MathUtil.toRadians(angle-90))*innerRadius);
            float outX = (float)(x+width/2+MathUtil.cos(MathUtil.toRadians(angle-90))*outerRadius);
            float outZ = (float)(z+depth/2+MathUtil.sin(MathUtil.toRadians(angle-90))*outerRadius);
            float nextInX = (float)(x+width/2+MathUtil.cos(MathUtil.toRadians(nextAngle-90))*innerRadius);
            float nextInZ = (float)(z+depth/2+MathUtil.sin(MathUtil.toRadians(nextAngle-90))*innerRadius);
            float nextOutX = (float)(x+width/2+MathUtil.cos(MathUtil.toRadians(nextAngle-90))*outerRadius);
            float nextOutZ = (float)(z+depth/2+MathUtil.sin(MathUtil.toRadians(nextAngle-90))*outerRadius);
            //inner face
            renderer.drawQuad(
                    new Vector3f(inX, y+height, inZ),
                    new Vector3f(nextInX, y+height, nextInZ),
                    new Vector3f(nextInX, y+height+thickness, nextInZ),
                    new Vector3f(inX, y+height+thickness, inZ), new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f(), new Vector3f());
            //middle face
            renderer.drawQuad(
                    new Vector3f(inX, y+height+thickness, inZ),
                    new Vector3f(nextInX, y+height+thickness, nextInZ),
                    new Vector3f(nextOutX, y+height+thickness, nextOutZ),
                    new Vector3f(outX, y+height+thickness, outZ), new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f(), new Vector3f());
            //outer face
            renderer.drawQuad(
                    new Vector3f(outX, y+height, outZ),
                    new Vector3f(nextOutX, y+height, nextOutZ),
                    new Vector3f(nextOutX, y+height+thickness, nextOutZ),
                    new Vector3f(outX, y+height+thickness, outZ), new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f(), new Vector3f());
            angle = nextAngle;
        }
    }
    public void drawOutline(Renderer renderer, float x, float y, float width, float height, Color color){
        renderer.setColor(color);
        renderer.drawImage(Core.outlineSquare, x, y, x+width, y+height);
        renderer.setWhite();
    }
    public void drawOutline(Renderer renderer, float x, float y, float z, float width, float height, float depth, Color color, Function<Direction, Boolean> faceRenderFunc){
        boolean px = faceRenderFunc.apply(Direction.PX);
        boolean py = faceRenderFunc.apply(Direction.PY);
        boolean pz = faceRenderFunc.apply(Direction.PZ);
        boolean nx = faceRenderFunc.apply(Direction.NX);
        boolean ny = faceRenderFunc.apply(Direction.NY);
        boolean nz = faceRenderFunc.apply(Direction.NZ);
        if(!px&&!py&&!pz&&!nx&&!ny&&!nz)return;//no faces are actually rendering, save some GL calls
        renderer.setColor(color);
        if(py)drawOutlineBit(renderer,x,y,z,width,height,depth);
        if(pz){
            renderer.model(new Matrix4f().translate(x+width/2, y+height/2, z+depth/2).rotate((float)MathUtil.toRadians(90), 1, 0, 0).translate(-x-width/2, -y-height/2, -z-depth/2));
            drawOutlineBit(renderer,x,y,z,width,height,depth);
            renderer.resetModelMatrix();
        }
        if(nz){
            renderer.model(new Matrix4f().translate(x+width/2, y+height/2, z+depth/2).rotate((float)MathUtil.toRadians(90), -1, 0, 0).translate(-x-width/2, -y-height/2, -z-depth/2));
            drawOutlineBit(renderer,x,y,z,width,height,depth);
            renderer.resetModelMatrix();
        }
        if(nx){
            renderer.model(new Matrix4f().translate(x+width/2, y+height/2, z+depth/2).rotate((float)MathUtil.toRadians(90), 0, 0, 1).translate(-x-width/2, -y-height/2, -z-depth/2));
            drawOutlineBit(renderer,x,y,z,width,height,depth);
            renderer.resetModelMatrix();
        }
        if(px){
            renderer.model(new Matrix4f().translate(x+width/2, y+height/2, z+depth/2).rotate((float)MathUtil.toRadians(90), 0, 0, -1).translate(-x-width/2, -y-height/2, -z-depth/2));
            drawOutlineBit(renderer,x,y,z,width,height,depth);
            renderer.resetModelMatrix();
        }
        if(ny){
            renderer.model(new Matrix4f().translate(x+width/2, y+height/2, z+depth/2).rotate((float)MathUtil.toRadians(180), 0, 0, 1).translate(-x-width/2, -y-height/2, -z-depth/2));
            drawOutlineBit(renderer,x,y,z,width,height,depth);
            renderer.resetModelMatrix();
        }
        renderer.setWhite();
    }
    private void drawOutlineBit(Renderer renderer, float x, float y, float z, float width, float height, float depth){
        float w = width/32f;//pixel
        float h = height/32f;//pixel
        float d = depth/32f;//pixel
        Function<Direction, Boolean> func = (Direction t) -> t!=Direction.NY;//don't render the bottom face; this is rendering on top
        renderer.drawCube(x+w/2, y+height, z+d/2, x+width-w/2, y+height+h/2, z+d*3/2, null, func);//top
        renderer.drawCube(x+w/2, y+height, z+width-d*3/2, x+width-w/2, y+height+h/2, z+width-d/2, null, func);//bottom
        renderer.drawCube(x+w/2, y+height, z+d*3/2, x+w*3/2, y+height+h/2, z+width-d*3/2, null, func);//left
        renderer.drawCube(x+width-w*3/2, y+height, z+d*3/2, x+width-w/2, y+height+h/2, z+width-d*3/2, null, func);//right
    }
    public AbstractBlock copy(int x, int y, int z){
        AbstractBlock b = newInstance(x, y, z);
        NCPFElement recipe = getRecipe();
        if(recipe!=null)b.setRecipe(recipe);
        copyProperties(b);
        return b;
    }
    public abstract boolean isValid();
    public abstract boolean isActive();
    @Deprecated
    public abstract boolean isCore();
    public List<NCPFPlacementRule> getRules(){
        for(NCPFModule module : getTemplate().modules.modules.values()){
            if(module instanceof BlockRulesModule)return ((BlockRulesModule)module).rules;
        }
        NCPFElement recipe = getRecipe();
        if(recipe!=null){
            for(NCPFModule module : recipe.modules.modules.values()){
                if(module instanceof BlockRulesModule)return ((BlockRulesModule)module).rules;
            }
        }
        return new ArrayList<>();
    }
    public boolean matches(AbstractBlock template){
        if(template==null)return getTemplate()==null;
        if(getTemplate()==null)return false;
        return getTemplate().definition.matches(template.getTemplate().definition);
    }
    public abstract boolean canRequire(AbstractBlock other);
    public boolean requires(AbstractBlock other, Multiblock mb){
        int totalDist = Math.abs(other.x-x)+Math.abs(other.y-y)+Math.abs(other.z-z);
        if(totalDist>1)return false;//too far away
        for(NCPFPlacementRule rule : getRules()){
            if(rule.containsTarget(other.getTemplate().definition))return true;
        }
        return false;
    }
    public abstract boolean canGroup();
    public abstract boolean canBeQuickReplaced();
    public boolean defaultEnabled(){
        return true;
    }
    public abstract AbstractBlock copy();
    public boolean isEqual(AbstractBlock other){//TODO difference between this and matches??
        return matches(other);
    }
    public boolean roughMatch(String blockNam){
        blockNam = StringUtil.toLowerCase(blockNam);
        if(blockNam.endsWith("s"))blockNam = blockNam.substring(0, blockNam.length()-1);
        blockNam = StringUtil.superRemove(StringUtil.replace(blockNam, "_", " "), "liquid ", " cooler", " heat sink", " heatsink", " sink", " neutron shield", " shield", " moderator", " coolant", " heater", "fuel ", " reflector");
        if(blockNam.endsWith("s"))blockNam = blockNam.substring(0, blockNam.length()-1);
        String blockName = getName();
        if(blockName.endsWith("s"))blockName = blockName.substring(0, blockName.length()-1);
        blockName = StringUtil.superRemove(StringUtil.replace(StringUtil.toLowerCase(blockName), "_", " "), "reactor ", "liquid ", " cooler", " heat sink", " heatsink", " sink", " neutron shield", " shield", " moderator", " coolant", " heater", "fuel ", " reflector");
        if(blockName.endsWith("s"))blockName = blockName.substring(0, blockName.length()-1);
        return blockNam.equalsIgnoreCase(blockName);
    }
    public boolean isFullBlock(){
        return true;
    }
    public NCPFConfigurationContainer getConfiguration(){
        return configuration;
    }
    public boolean shouldRenderFace(AbstractBlock against){
        if(against==null)return true;
        if(matches(against))return false;
        return Core.hasAlpha(against.getBaseTexture());
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> searchables = getTemplate().getSearchableNames();
        for(String s : StringUtil.split(getListTooltip(), "\n"))searchables.add(s.trim());
        return searchables;
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        return getTemplate().getSimpleSearchableNames();
    }
    public boolean hasRecipes(){
        for(NCPFModule module : getTemplate().modules.modules.values()){
            if(module instanceof RecipesBlockModule)return true;
        }
        return false;
    }
    public abstract List<? extends IBlockRecipe> getRecipes();
    public abstract NCPFElement getRecipe();
    public abstract void setRecipe(NCPFElement recipe);
    public boolean isToggled(){
        return false;
    }
    public void setToggled(boolean toggled){}
}