package net.ncplanner.plannerator.discord.play.smivilization;
import java.util.ArrayList;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
public class HutBunch{
    public long owner;
    public static HutBunch load(Config cfg){
        long owner = cfg.getLong("owner");
        HutBunch bunch = new HutBunch(owner);
        if(cfg.hasProperty("huts")){
            ConfigList theHuts = cfg.getConfigList("huts");
            for(int i = 0; i<theHuts.size(); i++){
                bunch.huts.add(Hut.load(bunch, theHuts.get(i)));
            }
            bunch.mainHut = cfg.getInt("mainHut");
        }else{
            bunch.huts.add(Hut.load(bunch, cfg));
        }
        return bunch;
    }
    public HutBunch(long owner){
        this.owner = owner;
    }
    public ArrayList<Hut> huts = new ArrayList<>();
    public int mainHut = 0;
    public Config save(Config config){
        ConfigList list = new ConfigList();
        for(Hut hut : huts){
            list.add(hut.save(Config.newConfig()));
        }
        config.set("huts", list);
        config.set("mainHut", mainHut);
        config.set("owner", owner);
        return config;
    }
}
