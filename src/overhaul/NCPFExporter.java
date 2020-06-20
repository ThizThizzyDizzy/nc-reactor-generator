package overhaul;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
public class NCPFExporter{
    public static void exportSFR(Reactor reactor, File file) throws FileNotFoundException{
        FileOutputStream stream = new FileOutputStream(file);
        saveHeader(stream);
        saveConfig(stream);
        saveReactor(reactor, stream);
    }
    private static void saveHeader(FileOutputStream stream){
        Config header = Config.newConfig();
        header.set("version", (byte)1);
        header.set("count", 1);
        header.save(stream);
    }
    private static void saveConfig(FileOutputStream stream){
        Config config = Config.newConfig();
        config.set("partial", true);
        Config overhaulCfg = Config.newConfig();
        Config fissionSFR = Config.newConfig();
        ConfigList blocks = new ConfigList();
        for(ReactorPart part : ReactorPart.parts){
            Config block = Config.newConfig();
            block.set("name", part.toString());
            block.set("functional", true);//everything but conductors
            switch(part.type){
                case AIR:
                case CASING:
                    continue;
                case CONDUCTOR:
                    block.set("functional", false);
                    block.set("cluster", true);
                    break;
                case FUEL_CELL:
                    if(((FuelCell)part).efficiency!=0)continue;//that's a source!
                    block.set("fuelCell", true);
                    block.set("cluster", true);
                    block.set("createCluster", true);
                    block.set("blocksLOS", true);
                    break;
                case HEATSINK:
                    Heatsink sink = (Heatsink) part;
                    block.set("cluster", true);
                    block.set("cooling", sink.cooling);
                    ConfigList rules = new ConfigList();
                    for(PlacementRule rule : sink.rules){
                        Config rul = Config.newConfig();
                        switch(rule.type){
                            case AXIS:
                                rul.set("min", (byte)1);
                                rul.set("max", (byte)3);
                                if(rule.bit instanceof ReactorPart){
                                    rul.set("type", (byte)1);
                                    rul.set("block", (byte)(byte)(rule.bit==ReactorPart.AIR?0:ReactorPart.parts.indexOf(rule.bit)+1));
                                }else{
                                    rul.set("type", (byte)3);
                                    switch((ReactorPart.Type)rule.bit){
                                        case AIR:
                                            rul.set("block", (byte)0);
                                            break;
                                        case CASING:
                                            rul.set("block", (byte)1);
                                            break;
                                        case HEATSINK:
                                            rul.set("block", (byte)2);
                                            break;
                                        case FUEL_CELL:
                                            rul.set("block", (byte)3);
                                            break;
                                        case MODERATOR:
                                            if(part instanceof NeutronShield){
                                                rul.set("block", (byte)6);
                                            }else rul.set("block", (byte)4);
                                            break;
                                        case REFLECTOR:
                                            rul.set("block", (byte)5);
                                            break;
                                        case IRRADIATOR:
                                            rul.set("block", (byte)7);
                                            break;
                                        case CONDUCTOR:
                                            rul.set("block", (byte)8);
                                            break;
                                    }
                                }
                            case BETWEEN:
                                rul.set("min", (byte)rule.min);
                                rul.set("max", (byte)Math.min(6,rule.max));
                                if(rule.bit instanceof ReactorPart){
                                    rul.set("type", (byte)0);
                                    rul.set("block", (byte)(rule.bit==ReactorPart.AIR?0:ReactorPart.parts.indexOf(rule.bit)+1));
                                }else{
                                    rul.set("type", (byte)2);
                                    switch((ReactorPart.Type)rule.bit){
                                        case AIR:
                                            rul.set("block", (byte)0);
                                            break;
                                        case CASING:
                                            rul.set("block", (byte)1);
                                            break;
                                        case HEATSINK:
                                            rul.set("block", (byte)2);
                                            break;
                                        case FUEL_CELL:
                                            rul.set("block", (byte)3);
                                            break;
                                        case MODERATOR:
                                            if(part instanceof NeutronShield){
                                                rul.set("block", (byte)6);
                                            }else rul.set("block", (byte)4);
                                            break;
                                        case REFLECTOR:
                                            rul.set("block", (byte)5);
                                            break;
                                        case IRRADIATOR:
                                            rul.set("block", (byte)7);
                                            break;
                                        case CONDUCTOR:
                                            rul.set("block", (byte)8);
                                            break;
                                    }
                                }
                                break;
                        }
                        rules.add(rul);
                    }
                    block.set("rules", rules);
                    break;
                case IRRADIATOR:
                    block.set("irradiator", true);
                    block.set("cluster", true);
                    block.set("createCluster", true);
                    break;
                case MODERATOR:
                    block.set("moderator", true);
                    block.set("flux", ((Moderator)part).fluxFactor);
                    block.set("efficiency", (float)((Moderator)part).efficiencyFactor);
                    if(part instanceof NeutronShield){
                        block.set("shield", true);
                        block.set("heatMult", ((NeutronShield) part).heatMult);
                        block.set("cluster", true);
                        block.set("createCluster", true);
                    }else{
                        block.set("activeModerator", true);
                    }
                    break;
                case REFLECTOR:
                    block.set("blocksLOS", true);
                    block.set("reflector", true);
                    block.set("efficiency", ((Reflector)part).efficiency);
                    block.set("reflectivity", ((Reflector)part).reflectivity);
                    block.set("blocksLOS", true);
                    break;
            }
            blocks.add(block);
        }
        fissionSFR.set("blocks", blocks);
        ConfigList fuels = new ConfigList();
        for(Fuel f : Fuel.fuels){
            for(Fuel.Type typ : Fuel.Type.values()){
                Config fuel = Config.newConfig();
                fuel.set("name", "["+typ.name()+"]"+f.toString());
                fuel.set("efficiency", f.efficiency.get(typ).floatValue());
                fuel.set("heat", f.heat.get(typ).floatValue());
                fuel.set("criticality", f.criticality.get(typ).intValue());
                fuel.set("selfPriming", f.selfPriming.get(typ).intValue()==1);
                fuel.set("time", 0);
                fuels.add(fuel);
            }
        }
        fissionSFR.set("fuels", fuels);
        ConfigList sources = new ConfigList();
        for(ReactorPart part : ReactorPart.GROUP_CELLS){
            if(((FuelCell)part).efficiency==0)continue;
            Config source = Config.newConfig();
            String nam = part.toString();
            source.set("name", nam.substring(11, nam.length()-1));
            source.set("efficiency", ((FuelCell)part).efficiency);
            sources.add(source);
        }
        fissionSFR.set("sources", sources);
        ConfigList irradiatorRecipes = new ConfigList();
        fissionSFR.set("irradiatorRecipes", irradiatorRecipes);
        overhaulCfg.set("fissionSFR", fissionSFR);
        config.set("overhaul", overhaulCfg);
        config.save(stream);
    }
    private static void saveReactor(Reactor reactor, FileOutputStream stream){
        Config react = Config.newConfig();
        react.set("id", 1);
        ConfigNumberList size = new ConfigNumberList();
        size.add((byte)reactor.x);
        size.add((byte)reactor.y);
        size.add((byte)reactor.z);
        react.set("size", size);
        boolean compact = reactor.getEmptySpace()<.25;
        react.set("compact", compact);
        ConfigNumberList blocks = new ConfigNumberList();
        ConfigNumberList fuels = new ConfigNumberList();
        ConfigNumberList sources = new ConfigNumberList();
        ConfigNumberList irradiatorRecipes = new ConfigNumberList();
        for(int x = 0; x<reactor.x; x++){
            for(int y = 0; y<reactor.x; y++){
                for(int z = 0; z<reactor.x; z++){
                    ReactorPart part = reactor.parts[x][y][z];
                    if(part instanceof FuelCell){
                        sources.add((byte)ReactorPart.GROUP_CELLS.indexOf(part));
                        fuels.add((byte)(Fuel.fuels.indexOf(reactor.fuel[x][y][z])*4+reactor.fuelType[x][y][z].ordinal()));
                        part = ReactorPart.FUEL_CELL;
                    }
                    if(part instanceof Irradiator){
                        irradiatorRecipes.add((byte)0);
                    }
                    if(!compact){
                        blocks.add((byte)x);
                        blocks.add((byte)y);
                        blocks.add((byte)z);
                    }
                    blocks.add((byte)(ReactorPart.parts.indexOf(part)+1));
                }
            }
        }
        react.set("blocks", blocks);
        react.set("fuels", fuels);
        react.set("sources", sources);
        react.set("irradiatorRecipes", irradiatorRecipes);
        react.save(stream);
    }
}