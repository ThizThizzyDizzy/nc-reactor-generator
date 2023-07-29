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
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.LegacyNCPFFile;
import net.ncplanner.plannerator.planner.file.StringFormatWriter;
public class BGStringWriter extends StringFormatWriter{
    @Override
    public boolean isMultiblockSupported(Multiblock multi){
        return true;
    }
    @Override
    public String write(LegacyNCPFFile ncpf){
        boolean hasRecipeWarned = false;
        if(!ncpf.multiblocks.isEmpty()){
            if(ncpf.multiblocks.size()>1)throw new IllegalArgumentException("Multible multiblocks are not supported by Building Gadget String!");
            Multiblock multi = ncpf.multiblocks.get(0);
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
                            String blockName = block.getBaseName();
                            String[] split = blockName.split("\\:");
                            if(split.length<2||split.length>3)throw new IllegalArgumentException("Cannot export block to BG String: "+blockName+"! Block name must be written as namespace:name");
                            HashMap<String, String> keys = new HashMap<>();
                            //<editor-fold defaultstate="collapsed" desc="Validation">
                            if(multi instanceof OverhaulSFR){
                                net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)block;
                                if(b.recipe!=null){
                                    if(!hasRecipeWarned){
                                        Core.warning("Warning: Pasted reactor will not be filtered! Make sure to filter cells and irradiators if you have multiple recipes.", null);
                                        hasRecipeWarned = true;
                                    }
                                }
                                if(b.template.coolantVent||b.template.parent!=null||b.template.shield){//vent or port or shield
                                    keys.put("active", ((net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)block).isToggled?"true":"false");
                                }
                            }
                            if(multi instanceof OverhaulMSR){
                                net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)block;
                                if(b.recipe!=null){
                                    if(!hasRecipeWarned){
                                        Core.warning("Warning: Pasted reactor will not be filtered! Make sure to filter vessels and irradiators if you have multiple recipes.", null);
                                        hasRecipeWarned = true;
                                    }
                                }
                                if(b.template.parent!=null||b.template.shield){//port or shield
                                    keys.put("active", ((net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)block).isToggled?"true":"false");
                                }
                            }
                            if(split.length==3){
                                if(multi instanceof UnderhaulSFR){
                                    if(blockName.startsWith("nuclearcraft:active_cooler")){
                                        keys = null;
                                    }else{
                                        //<editor-fold defaultstate="collapsed" desc="USFR blocks">
                                        switch(blockName){
                                            case "nuclearcraft:fission_block:0":
                                                keys.put("type", "casing");
                                                break;
                                            case "nuclearcraft:cooler:1":
                                                keys.put("type", "water");
                                                break;
                                            case "nuclearcraft:cooler:2":
                                                keys.put("type", "redstone");
                                                break;
                                            case "nuclearcraft:cooler:3":
                                                keys.put("type", "quartz");
                                                break;
                                            case "nuclearcraft:cooler:4":
                                                keys.put("type", "gold");
                                                break;
                                            case "nuclearcraft:cooler:5":
                                                keys.put("type", "glowstone");
                                                break;
                                            case "nuclearcraft:cooler:6":
                                                keys.put("type", "lapis");
                                                break;
                                            case "nuclearcraft:cooler:7":
                                                keys.put("type", "diamond");
                                                break;
                                            case "nuclearcraft:cooler:8":
                                                keys.put("type", "helium");
                                                break;
                                            case "nuclearcraft:cooler:9":
                                                keys.put("type", "enderium");
                                                break;
                                            case "nuclearcraft:cooler:10":
                                                keys.put("type", "cryotheum");
                                                break;
                                            case "nuclearcraft:cooler:11":
                                                keys.put("type", "iron");
                                                break;
                                            case "nuclearcraft:cooler:12":
                                                keys.put("type", "emerald");
                                                break;
                                            case "nuclearcraft:cooler:13":
                                                keys.put("type", "copper");
                                                break;
                                            case "nuclearcraft:cooler:14":
                                                keys.put("type", "tin");
                                                break;
                                            case "nuclearcraft:cooler:15":
                                                keys.put("type", "magnesium");
                                                break;
                                            case "nuclearcraft:ingot_block:8":
                                                keys.put("type", "graphite");
                                                break;
                                            case "nuclearcraft:ingot_block:9":
                                                keys.put("type", "beryllium");
                                                break;
                                        }
//</editor-fold>
                                    }
                                }
                                if(multi instanceof OverhaulSFR){
                                    //<editor-fold defaultstate="collapsed" desc="OSFR blocks">
                                    switch(blockName){
                                        case "nuclearcraft:fission_source:0":
                                            keys.put("type", "radium_beryllium");
                                            break;
                                        case "nuclearcraft:fission_source:1":
                                            keys.put("type", "polonium_beryllium");
                                            break;
                                        case "nuclearcraft:fission_source:2":
                                            keys.put("type", "californium");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:0":
                                            keys.put("type", "water");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:1":
                                            keys.put("type", "iron");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:2":
                                            keys.put("type", "redstone");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:3":
                                            keys.put("type", "quartz");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:4":
                                            keys.put("type", "obsidian");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:5":
                                            keys.put("type", "nether_brick");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:6":
                                            keys.put("type", "glowstone");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:7":
                                            keys.put("type", "lapis");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:8":
                                            keys.put("type", "gold");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:9":
                                            keys.put("type", "prismarine");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:10":
                                            keys.put("type", "slime");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:11":
                                            keys.put("type", "end_stone");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:12":
                                            keys.put("type", "purpur");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:13":
                                            keys.put("type", "diamond");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:14":
                                            keys.put("type", "emerald");
                                            break;
                                        case "nuclearcraft:solid_fission_sink:15":
                                            keys.put("type", "copper");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:0":
                                            keys.put("type", "tin");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:1":
                                            keys.put("type", "lead");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:2":
                                            keys.put("type", "boron");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:3":
                                            keys.put("type", "lithium");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:4":
                                            keys.put("type", "magnesium");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:5":
                                            keys.put("type", "manganese");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:6":
                                            keys.put("type", "aluminum");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:7":
                                            keys.put("type", "silver");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:8":
                                            keys.put("type", "fluorite");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:9":
                                            keys.put("type", "villiaumite");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:10":
                                            keys.put("type", "carobbiite");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:11":
                                            keys.put("type", "arsenic");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:12":
                                            keys.put("type", "liquid_nitrogen");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:13":
                                            keys.put("type", "liquid_helium");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:14":
                                            keys.put("type", "enderium");
                                            break;
                                        case "nuclearcraft:solid_fission_sink2:15":
                                            keys.put("type", "cryotheum");
                                            break;
                                        case "nuclearcraft:ingot_block:8":
                                            keys.put("type", "graphite");
                                            break;
                                        case "nuclearcraft:ingot_block:9":
                                            keys.put("type", "beryllium");
                                            break;
                                        case "nuclearcraft:fission_reflector:0":
                                            keys.put("type", "beryllium_carbon");
                                            break;
                                        case "nuclearcraft:fission_reflector:1":
                                            keys.put("type", "lead_steel");
                                            break;
                                        case "nuclearcraft:fission_shield:0":
                                            keys.put("type", "boron_silver");
                                            break;
                                        case "qmd:fission_reflector:0":
                                            keys.put("type", "tungsten_carbide");
                                            break;
                                        case "qmd:fission_shield:0":
                                            keys.put("type", "hafnium");
                                            break;
                                    }
//</editor-fold>
                                }
                                if(multi instanceof OverhaulMSR){
                                    //<editor-fold defaultstate="collapsed" desc="OMSR blocks">
                                    switch(blockName){
                                        case "nuclearcraft:fission_source:0":
                                            keys.put("type", "radium_beryllium");
                                            break;
                                        case "nuclearcraft:fission_source:1":
                                            keys.put("type", "polonium_beryllium");
                                            break;
                                        case "nuclearcraft:fission_source:2":
                                            keys.put("type", "californium");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:0":
                                            keys.put("type", "water");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:1":
                                            keys.put("type", "iron");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:2":
                                            keys.put("type", "redstone");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:3":
                                            keys.put("type", "quartz");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:4":
                                            keys.put("type", "obsidian");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:5":
                                            keys.put("type", "nether_brick");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:6":
                                            keys.put("type", "glowstone");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:7":
                                            keys.put("type", "lapis");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:8":
                                            keys.put("type", "gold");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:9":
                                            keys.put("type", "prismarine");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:10":
                                            keys.put("type", "slime");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:11":
                                            keys.put("type", "end_stone");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:12":
                                            keys.put("type", "purpur");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:13":
                                            keys.put("type", "diamond");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:14":
                                            keys.put("type", "emerald");
                                            break;
                                        case "nuclearcraft:salt_fission_heater:15":
                                            keys.put("type", "copper");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:0":
                                            keys.put("type", "tin");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:1":
                                            keys.put("type", "lead");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:2":
                                            keys.put("type", "boron");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:3":
                                            keys.put("type", "lithium");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:4":
                                            keys.put("type", "magnesium");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:5":
                                            keys.put("type", "manganese");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:6":
                                            keys.put("type", "aluminum");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:7":
                                            keys.put("type", "silver");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:8":
                                            keys.put("type", "fluorite");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:9":
                                            keys.put("type", "villiaumite");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:10":
                                            keys.put("type", "carobbiite");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:11":
                                            keys.put("type", "arsenic");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:12":
                                            keys.put("type", "liquid_nitrogen");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:13":
                                            keys.put("type", "liquid_helium");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:14":
                                            keys.put("type", "enderium");
                                            break;
                                        case "nuclearcraft:salt_fission_heater2:15":
                                            keys.put("type", "cryotheum");
                                            break;
                                        case "nuclearcraft:fission_heater_port:0":
                                            keys.put("type", "water");
                                            break;
                                        case "nuclearcraft:fission_heater_port:1":
                                            keys.put("type", "iron");
                                            break;
                                        case "nuclearcraft:fission_heater_port:2":
                                            keys.put("type", "redstone");
                                            break;
                                        case "nuclearcraft:fission_heater_port:3":
                                            keys.put("type", "quartz");
                                            break;
                                        case "nuclearcraft:fission_heater_port:4":
                                            keys.put("type", "obsidian");
                                            break;
                                        case "nuclearcraft:fission_heater_port:5":
                                            keys.put("type", "nether_brick");
                                            break;
                                        case "nuclearcraft:fission_heater_port:6":
                                            keys.put("type", "glowstone");
                                            break;
                                        case "nuclearcraft:fission_heater_port:7":
                                            keys.put("type", "lapis");
                                            break;
                                        case "nuclearcraft:fission_heater_port:8":
                                            keys.put("type", "gold");
                                            break;
                                        case "nuclearcraft:fission_heater_port:9":
                                            keys.put("type", "prismarine");
                                            break;
                                        case "nuclearcraft:fission_heater_port:10":
                                            keys.put("type", "slime");
                                            break;
                                        case "nuclearcraft:fission_heater_port:11":
                                            keys.put("type", "end_stone");
                                            break;
                                        case "nuclearcraft:fission_heater_port:12":
                                            keys.put("type", "purpur");
                                            break;
                                        case "nuclearcraft:fission_heater_port:13":
                                            keys.put("type", "diamond");
                                            break;
                                        case "nuclearcraft:fission_heater_port:14":
                                            keys.put("type", "emerald");
                                            break;
                                        case "nuclearcraft:fission_heater_port:15":
                                            keys.put("type", "copper");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:0":
                                            keys.put("type", "tin");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:1":
                                            keys.put("type", "lead");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:2":
                                            keys.put("type", "boron");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:3":
                                            keys.put("type", "lithium");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:4":
                                            keys.put("type", "magnesium");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:5":
                                            keys.put("type", "manganese");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:6":
                                            keys.put("type", "aluminum");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:7":
                                            keys.put("type", "silver");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:8":
                                            keys.put("type", "fluorite");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:9":
                                            keys.put("type", "villiaumite");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:10":
                                            keys.put("type", "carobbiite");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:11":
                                            keys.put("type", "arsenic");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:12":
                                            keys.put("type", "liquid_nitrogen");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:13":
                                            keys.put("type", "liquid_helium");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:14":
                                            keys.put("type", "enderium");
                                            break;
                                        case "nuclearcraft:fission_heater_port2:15":
                                            keys.put("type", "cryotheum");
                                            break;
                                        case "nuclearcraft:ingot_block:8":
                                            keys.put("type", "graphite");
                                            break;
                                        case "nuclearcraft:ingot_block:9":
                                            keys.put("type", "beryllium");
                                            break;
                                        case "nuclearcraft:fission_reflector:0":
                                            keys.put("type", "beryllium_carbon");
                                            break;
                                        case "nuclearcraft:fission_reflector:1":
                                            keys.put("type", "lead_steel");
                                            break;
                                        case "nuclearcraft:fission_shield:0":
                                            keys.put("type", "boron_silver");
                                            break;
                                        case "qmd:fission_reflector:0":
                                            keys.put("type", "tungsten_carbide");
                                            break;
                                        case "qmd:fission_shield:0":
                                            keys.put("type", "hafnium");
                                            break;
                                    }
//</editor-fold>
                                }
                                if(multi instanceof OverhaulTurbine){
                                    //<editor-fold defaultstate="collapsed" desc="OTurbine blocks">
                                    switch(blockName){
                                        case "nuclearcraft:turbine_dynamo_coil:0":
                                            keys.put("type", "magnesium");
                                            break;
                                        case "nuclearcraft:turbine_dynamo_coil:1":
                                            keys.put("type", "beryllium");
                                            break;
                                        case "nuclearcraft:turbine_dynamo_coil:2":
                                            keys.put("type", "aluminum");
                                            break;
                                        case "nuclearcraft:turbine_dynamo_coil:3":
                                            keys.put("type", "gold");
                                            break;
                                        case "nuclearcraft:turbine_dynamo_coil:4":
                                            keys.put("type", "copper");
                                            break;
                                        case "nuclearcraft:turbine_dynamo_coil:5":
                                            keys.put("type", "silver");
                                            break;
                                    }
//</editor-fold>
                                }
                                if(keys!=null&&keys.isEmpty()){
                                    if(split[2].equals("0")){
                                        Core.warning("Warning: Unrecognized block: "+blockName+"!", null);
                                    }
                                    throw new IllegalArgumentException("Cannot export unrecognized block: "+blockName+"!");
                                }
                            }
//</editor-fold>
                            String props = "";
                            if(keys!=null&&!keys.isEmpty()){
                                for(String key : keys.keySet()){
                                    props+=","+key+":\""+keys.get(key)+"\"";
                                }
                                props = "Properties:{"+props.substring(1)+"},";
                            }
                            String s = "mapState:{"+props+"Name:\""+split[0]+":"+split[1]+"\"}";
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