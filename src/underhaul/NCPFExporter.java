package underhaul;
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
        Config underhaulCfg = Config.newConfig();
        Config fissionSFR = Config.newConfig();
        ConfigList blocks = new ConfigList();
        for(ReactorPart part : ReactorPart.parts){
            Config block = Config.newConfig();
            block.set("name", part.toString());
            switch(part.type){
                case AIR:
                case CASING:
                    continue;
                case COOLER:
                    block.set("cooling", ((Cooler) part).cooling);
                    ConfigList rules = new ConfigList();
                    for(PlacementRule rule : ((Cooler) part).rules){
                        Config rul = Config.newConfig();
                        switch(rule.type){
                            case AXIS:
                                rul.set("min", (byte)1);
                                rul.set("max", (byte)3);
                                if(rule.bit instanceof ReactorPart){
                                    rul.set("type", (byte)1);
                                    rul.set("block", (byte)ReactorPart.parts.indexOf(rule.bit));
                                }else{
                                    rul.set("type", (byte)3);
                                    switch((ReactorPart.Type)rule.bit){
                                        case AIR:
                                            rul.set("block", (byte)0);
                                            break;
                                        case CASING:
                                            rul.set("block", (byte)1);
                                            break;
                                        case COOLER:
                                            rul.set("block", (byte)2);
                                            break;
                                        case FUEL_CELL:
                                            rul.set("block", (byte)3);
                                            break;
                                        case MODERATOR:
                                            rul.set("block", (byte)4);
                                            break;
                                    }
                                }
                                break;
                            case BETWEEN:
                                rul.set("min", (byte)rule.min);
                                rul.set("max", (byte)Math.min(6, rule.max));
                                if(rule.bit instanceof ReactorPart){
                                    rul.set("type", (byte)0);
                                    rul.set("block", (byte)ReactorPart.parts.indexOf(rule.bit));
                                }else{
                                    rul.set("type", (byte)2);
                                    switch((ReactorPart.Type)rule.bit){
                                        case AIR:
                                            rul.set("block", (byte)0);
                                            break;
                                        case CASING:
                                            rul.set("block", (byte)1);
                                            break;
                                        case COOLER:
                                            rul.set("block", (byte)2);
                                            break;
                                        case FUEL_CELL:
                                            rul.set("block", (byte)3);
                                            break;
                                        case MODERATOR:
                                            rul.set("block", (byte)4);
                                            break;
                                    }
                                }
                                break;
                            case NO_PANCAKE:
                                rul.set("type", (byte)4);
                                break;
                            case OR:
                                rul.set("type", (byte)5);
                                ConfigList rules2 = new ConfigList();
                                for(PlacementRule rule2 : rule.rules){
                                    Config rul2 = Config.newConfig();
                                    switch(rule2.type){
                                        case AXIS:
                                            rul2.set("min", (byte)1);
                                            rul2.set("max", (byte)3);
                                            if(rule2.bit instanceof ReactorPart){
                                                rul2.set("type", (byte)1);
                                                rul2.set("block", (byte)ReactorPart.parts.indexOf(rule2.bit));
                                            }else{
                                                rul2.set("type", (byte)3);
                                                switch((ReactorPart.Type)rule2.bit){
                                                    case AIR:
                                                        rul2.set("block", (byte)0);
                                                        break;
                                                    case CASING:
                                                        rul2.set("block", (byte)1);
                                                        break;
                                                    case COOLER:
                                                        rul2.set("block", (byte)2);
                                                        break;
                                                    case FUEL_CELL:
                                                        rul2.set("block", (byte)3);
                                                        break;
                                                    case MODERATOR:
                                                        rul2.set("block", (byte)4);
                                                        break;
                                                }
                                            }
                                            break;
                                        case BETWEEN:
                                            rul2.set("min", (byte)rule2.min);
                                            rul2.set("max", (byte)Math.min(6, rule2.max));
                                            if(rule2.bit instanceof ReactorPart){
                                                rul2.set("type", (byte)0);
                                                rul2.set("block", (byte)ReactorPart.parts.indexOf(rule2.bit));
                                            }else{
                                                rul2.set("type", (byte)2);
                                                switch((ReactorPart.Type)rule2.bit){
                                                    case AIR:
                                                        rul2.set("block", (byte)0);
                                                        break;
                                                    case CASING:
                                                        rul2.set("block", (byte)1);
                                                        break;
                                                    case COOLER:
                                                        rul2.set("block", (byte)2);
                                                        break;
                                                    case FUEL_CELL:
                                                        rul2.set("block", (byte)3);
                                                        break;
                                                    case MODERATOR:
                                                        rul2.set("block", (byte)4);
                                                        break;
                                                }
                                            }
                                            break;
                                        case NO_PANCAKE:
                                            rul2.set("type", (byte)4);
                                            break;
                                    }
                                    rules2.add(rul2);
                                }
                                rul.set("rules", rules2);
                                break;
                        }
                        rules.add(rul);
                    }
                    block.set("rules", rules);
                    break;
                case FUEL_CELL:
                    block.set("fuelCell", true);
                    break;
                case MODERATOR:
                    block.set("moderator", true);
                    break;
            }
            blocks.add(block);
        }
        fissionSFR.set("blocks", blocks);
        ConfigList fuels = new ConfigList();
        for(Fuel f : Fuel.fuels){
            Config fuel = Config.newConfig();
            fuel.set("name", f.toString());
            fuel.set("power", (float)f.power);
            fuel.set("heat", (float)f.heat);
            fuel.set("time", f.time);
            fuels.add(fuel);
        }
        fissionSFR.set("fuels", fuels);
        underhaulCfg.set("fissionSFR", fissionSFR);
        config.set("underhaul", underhaulCfg);
        config.save(stream);
    }
    private static void saveReactor(Reactor reactor, FileOutputStream stream){
        Config react = Config.newConfig();
        react.set("id", 0);
        ConfigNumberList size = new ConfigNumberList();
        size.add((byte)reactor.x);
        size.add((byte)reactor.y);
        size.add((byte)reactor.z);
        react.set("size", size);
        boolean compact = reactor.getEmptySpace()<.25;
        react.set("compact", compact);
        react.set("fuel", Fuel.fuels.indexOf(reactor.fuel));
        ConfigNumberList blocks = new ConfigNumberList();
        for(int x = 0; x<reactor.x; x++){
            for(int y = 0; y<reactor.x; y++){
                for(int z = 0; z<reactor.x; z++){
                    if(!compact){
                        blocks.add((byte)x);
                        blocks.add((byte)y);
                        blocks.add((byte)z);
                    }
                    blocks.add((byte)(ReactorPart.parts.indexOf(reactor.parts[x][y][z])+1));
                }
            }
        }
        react.set("blocks", blocks);
        react.save(stream);
    }
}