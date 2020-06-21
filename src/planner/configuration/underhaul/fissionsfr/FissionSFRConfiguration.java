package planner.configuration.underhaul.fissionsfr;
import java.util.ArrayList;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class FissionSFRConfiguration{
    public ArrayList<Block> blocks = new ArrayList<>();
    public ArrayList<Fuel> fuels = new ArrayList<>();
    public int minSize;
    public int maxSize;
    public int neutronReach;
    public float moderatorExtraPower;
    public float moderatorExtraHeat;
    public int activeCoolerRate;
    public String[] getBlockStringList(){
        String[] strs = new String[blocks.size()];
        for(int i = 0; i<strs.length; i++){
            strs[i] = blocks.get(i).name;
        }
        return strs;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("minSize", minSize);
        config.set("maxSize", maxSize);
        config.set("neutronReach", neutronReach);
        config.set("moderatorExtraPower", moderatorExtraPower);
        config.set("moderatorExtraHeat", moderatorExtraHeat);
        config.set("activeCoolerRate", activeCoolerRate);
        ConfigList blocks = new ConfigList();
        for(Block b : this.blocks){
            blocks.add(b.save());
        }
        config.set("blocks", blocks);
        ConfigList fuels = new ConfigList();
        for(Fuel f : this.fuels){
            fuels.add(f.save());
        }
        config.set("fuels", fuels);
        return config;
    }
}