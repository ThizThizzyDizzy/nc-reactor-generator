package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.Locale;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import java.util.function.Consumer;
import net.ncplanner.plannerator.graphics.PlanneratorRenderer;
import com.thizthizzydizzy.dizzyengine.graphics.image.Color;
import com.thizthizzydizzy.dizzyengine.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
public class MenuGenerateTexture extends MenuDialog{
    public String texture = TextureManager.textureTemplates[0];
    public Color color = Color.WHITE;
    private ToggleBox nak;
    private ToggleBox flibe;
    private ToggleBox hotnak;
    private boolean importing;
    private TextureImportPanel importPanel;
    public MenuGenerateTexture(GUI gui, Menu parent, String textureName, Consumer<Image> setTextureFunc){
        this(gui, parent, textureName, setTextureFunc, null, null);
    }
    public MenuGenerateTexture(GUI gui, Menu parent, String textureName, Consumer<Image> setTextureFunc, NCPFElement element, Configuration configuration){
        super(gui, parent);
        maxWidth = 0.9f;
        maxHeight = 0.8f;
        Component layout = setContent(new Component(0, 0, 600, 366));
        Component generator = new Component(0, 48, 600, 318){
            {
                for(int i = 0; i<TextureManager.textureTemplates.length; i++){
                    final int j = i;
                    add(new Button(i*150, 40, 150, 150, "", true, true){
                        {
                            addAction(() -> {
                                texture = TextureManager.textureTemplates[j];
                            });
                        }
                        @Override
                        public void drawText(PlanneratorRenderer renderer, double deltaTime){
                            renderer.setColor(MenuGenerateTexture.this.getColor(), texture.equals(TextureManager.textureTemplates[j])?1f:0.25f);
                            renderer.drawImage(TextureManager.getImage(TextureManager.textureTemplates[j]), x, y, x+width, y+height);
                        }
                    });
                }
                add(new TextBox(0, 190, 600, 64, "#FFFFFF", true, "Color (decimal; prefix with # for hex)"){
                    @Override
                    public void onCharTyped(char c){
                        super.onCharTyped(c);
                        updateColor();
                    }
                    @Override
                    public void onKeyEvent(int key, int scancode, int action, int mods){
                        super.onKeyEvent(key, scancode, action, mods);
                        updateColor();
                    }
                    public void updateColor(){
                        if(text.isEmpty())return;
                        try{
                            text = text.toUpperCase(Locale.ROOT);
                            if(text.startsWith("#")){
                                //hex
                                String text = this.text;
                                if(text.length()==7)MenuGenerateTexture.this.color = new Color(Integer.parseInt(text.substring(1), 16)|0xff000000);
                            }else{
                                MenuGenerateTexture.this.color = new Color(Integer.parseInt(text)|0xff000000);
                            }
                        }catch(NumberFormatException ex){}
                    }
                });
                nak = add(new ToggleBox(0, 254, 200, 64, "NaK", false){
                    @Override
                    public void onMouseButton(double x, double y, int button, int action, int mods){
                        super.onMouseButton(x, y, button, action, mods);
                        if(isToggledOn)hotnak.isToggledOn = flibe.isToggledOn = false;
                    }
                });
                hotnak = add(new ToggleBox(200, 254, 200, 64, "Hot NaK", false){
                    @Override
                    public void onMouseButton(double x, double y, int button, int action, int mods){
                        super.onMouseButton(x, y, button, action, mods);
                        if(isToggledOn)nak.isToggledOn = flibe.isToggledOn = false;
                    }
                });
                flibe = add(new ToggleBox(400, 254, 200, 64, "FLiBe", false){
                    @Override
                    public void onMouseButton(double x, double y, int button, int action, int mods){
                        super.onMouseButton(x, y, button, action, mods);
                        if(isToggledOn)nak.isToggledOn = hotnak.isToggledOn = false;
                    }
                });
            }
            @Override
            public void draw(double deltaTime){
                PlanneratorRenderer renderer = new PlanneratorRenderer();
                renderer.setColor(Core.theme.getComponentTextColor(0));
                renderer.drawCenteredText(x, y, x+width, y+20, textureName!=null?"Generate "+textureName+" Texture":"Generate Texture");
                renderer.drawCenteredText(x, y+20, x+width, y+40, texture.substring("fluids/templates/".length()));
            }
        };
        importPanel = new TextureImportPanel(element, configuration);
        importPanel.y = 48;
        Button generateTab = layout.add(new Button(0, 0, 300, 48, "Generate", true));
        Button importTab = layout.add(new Button(300, 0, 300, 48, "Import", true));
        Runnable updateTab = () -> {
            layout.components.remove(generator);
            layout.components.remove(importPanel);
            layout.focusedComponent = null;
            layout.mouseFocusedComponent = null;
            generateTab.enabled = importing;
            importTab.enabled = !importing;
            layout.add(importing?importPanel:generator);
        };
        generateTab.addAction(() -> { importing = false; updateTab.run(); });
        importTab.addAction(() -> { importing = true; updateTab.run(); });
        updateTab.run();
        addButton("Confirm", () -> {
            setTextureFunc.accept(importing?importPanel.getSelectedTexture().copy():TextureManager.generateTexture(texture, getColor()));
            gui.open(parent);
        });
        addButton("Cancel", () -> {
            close();
        });
    }
    @Override
    public void render2d(double deltaTime){
        buttons.get(0).enabled = !importing || importPanel.getSelectedTexture()!=null;
        super.render2d(deltaTime);
    }
    private Color getColor(){
        if(nak.isToggledOn)return TextureManager.getNaKColor(color);
        if(hotnak.isToggledOn)return TextureManager.getHotNaKColor(color);
        if(flibe.isToggledOn)return TextureManager.getFLiBeColor(color);
        return color;
    }
}
