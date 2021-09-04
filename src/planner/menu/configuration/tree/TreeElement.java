package planner.menu.configuration.tree;
import multiblock.configuration.IBlockTemplate;
import multiblock.configuration.IBlockType;
import planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.Renderer2D;
public class TreeElement<BlockType extends IBlockType, Template extends IBlockTemplate>{
    public final boolean isSpecificBlock;
    public final BlockType blockType;
    public final Template template;
    public final int tier;
    public TreeElement(Template template, int tier){
        this.template = template;
        blockType = null;
        this.tier = tier;
        isSpecificBlock = true;
    }
    public TreeElement(BlockType blockType, int tier){
        this.blockType = blockType;
        template = null;
        this.tier = tier;
        isSpecificBlock = false;
    }
    public void render(double x, double y, double width, double height, float alpha){
        if(isSpecificBlock)Renderer2D.drawRect(x, y, x+width, y+height, Core.getTexture(template.getDisplayTexture()));
        else{
            String text = blockType.getDisplayName();
            double textLength = FontManager.getLengthForStringWithHeight(text, height);
            double scale = Math.min(1, (width)/textLength);
            double textHeight = (int)((height)*scale)-4;
            Core.applyColor(Core.theme.getComponentTextColor(0), alpha);
            Renderer2D.drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
        }
    }
    public String getTooltip(){
        if(isSpecificBlock){
            String tooltip = template.getDisplayName();
            if(template instanceof multiblock.configuration.underhaul.fissionsfr.Block){
                multiblock.configuration.underhaul.fissionsfr.Block b = (multiblock.configuration.underhaul.fissionsfr.Block)template;
                if(b.cooling!=0)tooltip+="\nCooling: "+b.cooling;
            }
            if(template instanceof multiblock.configuration.overhaul.fissionsfr.Block){
                multiblock.configuration.overhaul.fissionsfr.Block b = (multiblock.configuration.overhaul.fissionsfr.Block)template;
                if(b.isHeatsink()){
                    if(b.isHeatsinkHasBaseStats())tooltip+="\nCooling: "+b.heatsinkCooling;
                }
            }
            if(template instanceof multiblock.configuration.overhaul.fissionmsr.Block){
                multiblock.configuration.overhaul.fissionmsr.Block b = (multiblock.configuration.overhaul.fissionmsr.Block)template;
                if(b.isHeater()){
                    if(b.isHeaterHasBaseStats())tooltip+="\nCooling: "+b.heaterCooling;
                }
            }
            if(template instanceof multiblock.configuration.overhaul.fusion.Block){
                multiblock.configuration.overhaul.fusion.Block b = (multiblock.configuration.overhaul.fusion.Block)template;
                if(b.isHeatsink()){
                    if(b.isHeatsinkHasBaseStats())tooltip+="\nCooling: "+b.heatsinkCooling;
                }
            }
            if(template instanceof multiblock.configuration.overhaul.turbine.Block){
                multiblock.configuration.overhaul.turbine.Block b = (multiblock.configuration.overhaul.turbine.Block)template;
                if(b.isCoil())tooltip+="\nEfficieny: "+Math.round(b.coilEfficiency*100)/100d+"%";
            }
            return tooltip;
        }
        else return null;
    }
}