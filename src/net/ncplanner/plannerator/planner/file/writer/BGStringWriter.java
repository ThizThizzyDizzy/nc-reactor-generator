package net.ncplanner.plannerator.planner.file.writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.element.NCPFBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFBlockTagElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFOredictElement;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.StringFormatWriter;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuMessageDialog;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.design.MultiblockDesign;
import net.ncplanner.plannerator.planner.ncpf.module.GlobalElementsModule;
import net.ncplanner.plannerator.planner.ncpf.module.TagsModule;
public class BGStringWriter extends StringFormatWriter{
    private HashMap<NCPFElementDefinition, NCPFElement> tagMap = new HashMap<>();
    @Override
    public boolean isMultiblockSupported(Multiblock multi){
        return true;
    }
    @Override
    public void openExportSettings(Project ncpf, Runnable onExport){
        ArrayList<Runnable> exportPrompts = new ArrayList<>();

        for(Design design : ncpf.designs){
            if(design instanceof MultiblockDesign){
                NCPFConfiguration config = ((MultiblockDesign)design).toMultiblock().getSpecificConfiguration();
                GlobalElementsModule gem = config.getModule(GlobalElementsModule::new);
                for(NCPFElementDefinition element : (Set<NCPFElementDefinition>)design.getElements()){
                    String tag = null;
                    if(element instanceof NCPFBlockTagElement){
                        tag = ((NCPFBlockTagElement)element).name;
                    }
                    if(element instanceof NCPFOredictElement){
                        tag = ((NCPFOredictElement)element).oredict;
                    }
                    if(tag!=null){
                        ArrayList<NCPFElement> elements = new ArrayList<>();
                        if(gem!=null){
                            for(NCPFElement elem : gem.elements){
                                TagsModule tags = elem.getModule(TagsModule::new);
                                if(tags!=null&&tags.tags.contains(tag))elements.add(elem);
                            }
                        }
                        if(elements.isEmpty()){
                            throw new IllegalArgumentException("Could not find any matches for oredict or tag: "+tag);
                        }
                        if(elements.size()==1){
                            tagMap.put(element, elements.get(0));
                            continue;
                        }
                        String theTag = tag;
                        exportPrompts.add(() -> {
                            MenuMessageDialog dialog = new MenuMessageDialog(Core.gui, Core.gui.menu, "Choose element for tag or oredict:\n"+theTag);
                            for(NCPFElement elem : elements){
                                dialog.addButton(elem.getDisplayName(), () -> {
                                    tagMap.put(element, elem);
                                    exportPrompts.remove(0).run();
                                }, true);
                            }
                            dialog.addButton("Cancel", true);
                            dialog.open();
                        });
                    }
                }
            }
        }

        exportPrompts.add(() -> super.openExportSettings(ncpf, onExport));
        exportPrompts.remove(0).run();
    }
    @Override
    public String write(Project ncpf){
        boolean hasRecipeWarned = false;
        if(!ncpf.designs.isEmpty()){
            if(ncpf.designs.size()>1)throw new IllegalArgumentException("Multiple designs are not supported by Building Gadget String!");
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
                            if(tagMap.containsKey(definition))definition = tagMap.get(definition).definition;
                            if(definition instanceof NCPFLegacyBlockElement){
                                NCPFLegacyBlockElement elem = (NCPFLegacyBlockElement)definition;
                                name = elem.name;
                                for(String key : elem.blockstate.keySet()){
                                    props += ","+key+":\""+elem.blockstate.get(key)+"\"";
                                }
                                if(!props.isEmpty())props = "Properties:{"+props.substring(1)+"},";
                            }else if(definition instanceof NCPFBlockElement){
                                NCPFBlockElement elem = (NCPFBlockElement)definition;
                                name = elem.name;
                                for(String key : elem.blockstate.keySet()){
                                    props += ","+key+":\""+elem.blockstate.get(key)+"\"";
                                }
                                if(!props.isEmpty())props = "Properties:{"+props.substring(1)+"},";
                            }else
                                throw new IllegalArgumentException("Cannot export element definition in BG String: "+definition.type+" ("+definition.toString()+")");
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
                statesS += ","+i;
            }
            String positionsS = "";
            for(int i : positions){
                positionsS += ","+i;
            }
            String mapS = "";
            for(int i = 0; i<map.size(); i++){
                String s = map.get(i);
                mapS += ",{mapSlot:"+(i+1)+"s,"+s+"}";
            }
            return "{stateIntArray:[I;"+statesS.substring(1)+"],dim:0,posIntArray:[I;"+positionsS.substring(1)+"],startPos:{X:0,Y:0,Z:0},mapIntState:["+mapS.substring(1)+"],endPos:{X:"+(bbox.x2-bbox.x1)+",Y:"+(bbox.y2-bbox.y1)+",Z:"+(bbox.z2-bbox.z1)+"}}";
        }else{
            throw new UnsupportedOperationException("Cannot export NCPF configuration to Building Gadget String!");
        }
    }
}
