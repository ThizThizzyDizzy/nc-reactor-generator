package net.ncplanner.plannerator.planner.file.writer;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.ncpf.element.NCPFBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.LegacyNCPFFile;
import net.ncplanner.plannerator.planner.file.StringFormatWriter;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.design.MultiblockDesign;
public class BGStringWriter extends StringFormatWriter{
    @Override
    public boolean isMultiblockSupported(Multiblock multi){
        return true;
    }
    @Override
    public String write(Project ncpf){
        boolean hasRecipeWarned = false;
        if(!ncpf.designs.isEmpty()){
            if(ncpf.designs.size()>1)throw new IllegalArgumentException("Multible designs are not supported by Building Gadget String!");
            Design design = ncpf.designs.get(0);
            if(!(design instanceof MultiblockDesign))throw new IllegalArgumentException("Cannot export non-multiblock design as a Building Gadget String!");
            Multiblock multi = ((MultiblockDesign)design).toMultiblock();
            BoundingBox bbox = multi.getBoundingBox();
            ArrayList<String> map = new ArrayList<>();
            ArrayList<Integer> states = new ArrayList<>();
            ArrayList<Integer> positions = new ArrayList<>();
            for(int y = bbox.y1; y<=bbox.y2; y++){
                for(int z = bbox.z1; z<=bbox.z2; z++){
                    for(int x = bbox.x1; x<=bbox.x2; x++){
                        int X = x-bbox.x1;
                        int Y = y-bbox.y1;
                        int Z = z-bbox.z1;
                        if(multi.contains(x, y, z)){
                            AbstractBlock block = multi.getBlock(x, y, z);
                            if(block==null)continue;
                            //<editor-fold defaultstate="collapsed" desc="Validation">
                            if(multi instanceof OverhaulSFR){
                                net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)block;
                                if(b.getRecipe()!=null){
                                    if(!hasRecipeWarned){
                                        Core.warning("Warning: Pasted reactor will not be filtered! Make sure to filter cells and irradiators if you have multiple recipes.", null);
                                        hasRecipeWarned = true;
                                    }
                                }
                            }
                            if(multi instanceof OverhaulMSR){
                                net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)block;
                                if(b.getRecipe()!=null){
                                    if(!hasRecipeWarned){
                                        Core.warning("Warning: Pasted reactor will not be filtered! Make sure to filter vessels and irradiators if you have multiple recipes.", null);
                                        hasRecipeWarned = true;
                                    }
                                }
                            }
//</editor-fold>
                            String props = "";
                            String name = "";
                            NCPFElementDefinition definition = block.getTemplate().definition;
                            if(definition instanceof NCPFLegacyBlockElement){
                                NCPFLegacyBlockElement elem = (NCPFLegacyBlockElement)definition;
                                name = elem.name;
                                for(String key : elem.blockstate.keySet()){
                                    props+=","+key+":\""+elem.blockstate.get(key)+"\"";
                                }
                                if(!props.isEmpty())props = "Properties:{"+props.substring(1)+"},";
                            }else if(definition instanceof NCPFBlockElement){
                                NCPFBlockElement elem = (NCPFBlockElement)definition;
                                name = elem.name;
                                for(String key : elem.blockstate.keySet()){
                                    props+=","+key+":\""+elem.blockstate.get(key)+"\"";
                                }
                                props = "Properties:{"+props.substring(1)+"},";
                            }else throw new IllegalArgumentException("Cannot export element definition in BG String: "+definition.type+" ("+definition.toString()+")");
                            String s = "mapState:{"+props+"Name:\""+name+"\"}";
                            if(!map.contains(s))map.add(s);
                            states.add(map.indexOf(s)+1);
                            int px = (X&0xFF)<<16;
                            int py = (Y&0xFF)<<8;
                            int pz = Z&0xFF;
                            positions.add(px+py+pz);
                        }
                    }
                }
            }
            String statesS = "";
            for(int i : states){
                statesS+=","+i;
            }
            String positionsS = "";
            for(int i : positions){
                positionsS+=","+i;
            }
            String mapS = "";
            for(int i = 0; i<map.size(); i++){
                String s = map.get(i);
                mapS+=",{mapSlot:"+(i+1)+"s,"+s+"}";
            }
            return "{stateIntArray:[I;"+statesS.substring(1)+"],dim:0,posIntArray:[I;"+positionsS.substring(1)+"],startPos:{X:0,Y:0,Z:0},mapIntState:["+mapS.substring(1)+"],endPos:{X:"+(bbox.x2-bbox.x1)+",Y:"+(bbox.y2-bbox.y1)+",Z:"+(bbox.z2-bbox.z1)+"}}";
        }else{
            throw new UnsupportedOperationException("Cannot export NCPF configuration to Building Gadget String!");
        }
    }
}