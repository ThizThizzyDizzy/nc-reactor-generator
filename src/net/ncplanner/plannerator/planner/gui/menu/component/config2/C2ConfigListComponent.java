package net.ncplanner.plannerator.planner.gui.menu.component.config2;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.config2.ConfigNumberList;
public class C2ConfigListComponent extends C2ConfigComponentBase{
    public C2ConfigListComponent(ConfigList config){
        for(int i = 0; i<config.size(); i++){
            Object value = config.get(i);
            C2ConfigComponentBase expandable = null;
            if(value instanceof Config){
                expandable = new C2ConfigComponent((Config)value);
            }else if(value instanceof ConfigList){
                expandable = new C2ConfigListComponent((ConfigList)value);
            }else if(value instanceof ConfigNumberList){
                expandable = new C2ConfigNumberListComponent((ConfigNumberList)value);
            }
            final C2ConfigComponentBase expandabl = expandable;
            final int idx = i;
            content.add(new C2ConfigEntryComponent(i+"", value, (expand)->{
                if(expandabl!=null)expandabl.expanded = expand;
            }, (val)->{
                config.remove(idx);
                config.add(idx, val);
            }));
            if(expandable!=null)content.add(expandable);
        }
    }
}