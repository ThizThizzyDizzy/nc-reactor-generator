package net.ncplanner.plannerator.planner.gui.menu.component.config2;
import net.ncplanner.plannerator.config2.ConfigNumberList;
public class C2ConfigNumberListComponent extends C2ConfigComponentBase{
    public C2ConfigNumberListComponent(ConfigNumberList config){
        for(int i = 0; i<config.size(); i++){
            long value = config.get(i);
            final int idx = i;
            content.add(new C2ConfigEntryComponent(i+"", value, null, (val)->{
                config.remove(idx);
                config.add(idx, (Number)val);
            }));
        }
    }
}