package net.ncplanner.plannerator.planner.configuration;
import java.util.ArrayList;
import net.ncplanner.ncpf.structure.NcpfRoot;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.file.ncpf.NcpfBridge;
public class CannedConfiguration extends Configuration{
    public final ArrayList<String> aliases = alternatives;
    public CannedConfiguration(){this(new Project());}
    public CannedConfiguration(Configuration original){
        this();
        configuration=original.configuration;
        addons=original.addons;
        path=original.path;
        aliases.addAll(original.alternatives);
    }
    public CannedConfiguration(NcpfRoot ncpf){this(ncpf,null);}
    public CannedConfiguration(NcpfRoot ncpf,String path){this(NcpfBridge.toProject(ncpf),path);}
    public CannedConfiguration(Project project){this(project,null);}
    public CannedConfiguration(Project project,String path){super(project,path);}
    public CannedConfiguration safeCopy(){
        CannedConfiguration copy=new CannedConfiguration(toProject(),path);
        copy.aliases.addAll(aliases);
        return copy;
    }
    public void impose(Project project){
        project.configuration=configuration;
        project.addons=addons;
        project.conglomerate();
    }
    public CannedConfiguration addAlias(String alias){aliases.add(alias);return this;}
}
