package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import com.thizthizzydizzy.dizzyengine.graphics.image.Image;
import net.ncplanner.plannerator.graphics.PlanneratorRenderer;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
class TextureImportPanel extends Component{
    private final NCPFElement target;
    private final Configuration configuration;
    private final List<Candidate> candidates = new ArrayList<>();
    private final Set<String> textureKeys = new java.util.HashSet<>();
    private final SingleColumnList results;
    private final TextBox search;
    private final Label source;
    private Candidate selected;
    TextureImportPanel(NCPFElement target, Configuration configuration){
        super(0, 0, 600, 318);
        this.target = target;
        this.configuration = configuration;
        source = add(new Label(0, 40, 600, 24, "Current Configuration", true));
        add(new Button(0, 0, 200, 40, "Current Configuration", true).addAction(this::loadCurrent));
        add(new Button(200, 0, 200, 40, "Choose Configuration", true).addAction(() -> {
            new MenuPickConfiguration(gui.menu, cfg -> {
                candidates.clear();
                textureKeys.clear();
                collect(cfg.configuration);
                cfg.addons.forEach(addon -> collect(addon.configuration));
                source.text = cfg.getName();
                refresh();
            }).open();
        }));
        add(new Button(400, 0, 200, 40, "Load File", true).addAction(() -> {
            try{
                Core.createFileChooser(file -> {
                    try{
                        Project project = FileReader.read(file);
                        Core.runOnUIThread(() -> {
                            candidates.clear();
                            textureKeys.clear();
                            collect(project.configuration);
                            project.addons.forEach(addon -> collect(addon.configuration));
                            source.text = file.getName();
                            refresh();
                        });
                    }catch(Exception ex){
                        Core.error("Failed to load textures from "+file.getName()+"!", ex);
                    }
                }, FileFormat.ALL_PLANNER_FORMATS, "configuration");
            }catch(IOException ex){
                Core.error("Failed to open configuration!", ex);
            }
        }));
        search = add(new TextBox(0, 64, 600, 48, "", true, "Search by element definition or name").onChange(text -> refresh()));
        results = add(new SingleColumnList(0, 112, 600, 206, 16));
        loadCurrent();
    }
    private void loadCurrent(){
        candidates.clear();
        textureKeys.clear();
        // Use the live editor configuration, which can contain unsaved changes.
        if(configuration!=null){
            collect(configuration.configuration);
            configuration.addons.forEach(addon -> collect(addon.configuration));
        }
        collect(Core.project.configuration);
        Core.project.addons.forEach(addon -> collect(addon.configuration));
        source.text = "Current Configuration";
        refresh();
    }
    private void collect(NCPFConfigurationContainer container){
        for(NCPFConfiguration config : container.configurations.values()){
            Set<NCPFElement> visited = Collections.newSetFromMap(new IdentityHashMap<>());
            for(List<NCPFElement> elements : config.getAllElements()){
                for(NCPFElement element : elements)collect(element, config.name, visited);
            }
        }
    }
    private void collect(NCPFElement element, String type, Set<NCPFElement> visited){
        if(!visited.add(element))return;
        if(element!=target && element.getTexture()!=null){
            Candidate candidate = new Candidate(element, type, isMatch(element));
            String key = type+"\n"+element.definition+"\n"+element.getTexture().toBase64();
            if(textureKeys.add(key))candidates.add(candidate);
        }
        if(element instanceof BlockRecipesElement){
            List<? extends NCPFElement> recipes = ((BlockRecipesElement)element).getBlockRecipes();
            if(recipes!=null)for(NCPFElement recipe : recipes)collect(recipe, type, visited);
        }
    }
    private boolean isMatch(NCPFElement element){
        if(target==null)return false;
        return element.definition.matches(target.definition)
                || sameName(element.getName(), target.getName())
                || sameName(element.getDisplayName(), target.getDisplayName());
    }
    private static boolean sameName(String a, String b){
        return a!=null && b!=null && !a.isBlank() && a.equalsIgnoreCase(b);
    }
    private void refresh(){
        selected = null;
        results.components.clear();
        results.scrollY = 0;
        String query = search.text.trim().toLowerCase(Locale.ROOT);
        candidates.sort(java.util.Comparator.comparing((Candidate c) -> !c.match)
                .thenComparing(c -> c.element.getDisplayName(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(c -> c.type));
        for(Candidate candidate : candidates){
            NCPFElement element = candidate.element;
            String names = element.definition.toString()+" "+element.getName()+" "+element.getDisplayName();
            if(!names.toLowerCase(Locale.ROOT).contains(query))continue;
            Button row = results.add(new Button(0, 0, 600, 64, "", true){
                @Override
                public void drawText(PlanneratorRenderer renderer, double deltaTime){
                    renderer.setWhite();
                    renderer.drawImage(element.getTexture(), x+4, y+4, x+60, y+60);
                    renderer.setColor(Core.theme.getComponentTextColor(0));
                    drawFittedText(renderer, x+68, y+4, x+width-8, 24,
                            (candidate==selected?"> ":"")+(candidate.match?"Suggested: ":"")+element.getDisplayName());
                    drawFittedText(renderer, x+68, y+30, x+width-8, 16, element.definition.toString());
                    drawFittedText(renderer, x+68, y+48, x+width-8, 12, candidate.type);
                }
            }.addAction(() -> selected = candidate));
            row.setTooltip(element.getDisplayName()+"\n"+element.definition+"\n"+candidate.type);
        }
        if(results.components.isEmpty()){
            results.add(new Label(0, 0, 600, 64, candidates.isEmpty()?"No elements with textures in this configuration":"No textures match your search"));
        }
    }
    private static void drawFittedText(PlanneratorRenderer renderer, float left, float top, float right, float height, String text){
        float textWidth = renderer.getStringWidth(text, height);
        float textHeight = textWidth>right-left?height*(right-left)/textWidth:height;
        renderer.drawText(left, top, right, top+textHeight, text);
    }
    Image getSelectedTexture(){
        return selected==null?null:selected.element.getTexture();
    }
    private static class Candidate{
        final NCPFElement element;
        final String type;
        final boolean match;
        Candidate(NCPFElement element, String type, boolean match){
            this.element = element;
            this.type = type;
            this.match = match;
        }
    }
}
