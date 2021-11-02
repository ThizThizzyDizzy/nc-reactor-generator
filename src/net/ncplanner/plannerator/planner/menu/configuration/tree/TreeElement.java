package net.ncplanner.plannerator.planner.menu.configuration.tree;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.IBlockTemplate;
import net.ncplanner.plannerator.multiblock.configuration.IBlockType;
import net.ncplanner.plannerator.planner.Core;
import simplelibrary.font.FontManager;
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
    public void render(Renderer renderer, double x, double y, double width, double height, float alpha){
        if(isSpecificBlock)renderer.drawImage(template.getDisplayTexture(), x, y, x+width, y+height);
        else{
            String text = blockType.getDisplayName();
            double textLength = FontManager.getLengthForStringWithHeight(text, height);
            double scale = Math.min(1, (width)/textLength);
            double textHeight = (int)((height)*scale)-4;
            renderer.setColor(Core.theme.getComponentTextColor(0), alpha);
            renderer.drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
        }
    }
    public String getTooltip(){
        if(isSpecificBlock){
            String tooltip = template.getDisplayName();
            if(template instanceof net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block){
                net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block)template;
                if(b.cooling!=0)tooltip+="\nCooling: "+b.cooling;
            }
            if(template instanceof net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block){
                net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block)template;
                if(b.isHeatsink()){
                    if(b.isHeatsinkHasBaseStats())tooltip+="\nCooling: "+b.heatsinkCooling;
                }
            }
            if(template instanceof net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block){
                net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b = (net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block)template;
                if(b.isHeater()){
                    if(b.isHeaterHasBaseStats())tooltip+="\nCooling: "+b.heaterCooling;
                }
            }
            if(template instanceof net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block){
                net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block b = (net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block)template;
                if(b.isHeatsink()){
                    if(b.isHeatsinkHasBaseStats())tooltip+="\nCooling: "+b.heatsinkCooling;
                }
            }
            if(template instanceof net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block){
                net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b = (net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block)template;
                if(b.isCoil())tooltip+="\nEfficieny: "+Math.round(b.coilEfficiency*100)/100d+"%";
            }
            return tooltip;
        }
        else return null;
    }
}