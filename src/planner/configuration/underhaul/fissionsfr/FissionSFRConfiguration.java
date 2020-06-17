package planner.configuration.underhaul.fissionsfr;
import java.util.ArrayList;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class FissionSFRConfiguration{
    public ArrayList<Block> blocks = new ArrayList<>();
    public ArrayList<Fuel> fuels = new ArrayList<>();
    public String[] getBlockStringList(){
        String[] strs = new String[blocks.size()];
        for(int i = 0; i<strs.length; i++){
            strs[i] = blocks.get(i).name;
        }
        return strs;
    }
    public Config save(){
        Config config = Config.newConfig();
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