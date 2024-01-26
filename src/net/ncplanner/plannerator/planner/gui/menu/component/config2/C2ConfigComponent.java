package net.ncplanner.plannerator.planner.gui.menu.component.config2;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.config2.ConfigNumberList;
public class C2ConfigComponent extends C2ConfigComponentBase{
    public C2ConfigComponent(Config config){
        for(String key : config.properties()){
            Object value = config.get(key);
            C2ConfigComponentBase expandable = null;
            if(value instanceof Config){
                expandable = new C2ConfigComponent((Config)value);
            }else if(value instanceof ConfigList){
                expandable = new C2ConfigListComponent((ConfigList)value);
            }else if(value instanceof ConfigNumberList){
                expandable = new C2ConfigNumberListComponent((ConfigNumberList)value);
            }
            final C2ConfigComponentBase expandabl = expandable;
            content.add(new C2ConfigEntryComponent(key, value, (expand)->{
                if(expandabl!=null)expandabl.expanded = expand;
            }, (val)->config.set(key, val)));
            if(expandable!=null)content.add(expandable);
            
        }
    }
}