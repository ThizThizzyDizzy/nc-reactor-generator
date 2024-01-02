package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.Locale;
import java.util.function.Consumer;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
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
    public MenuGenerateTexture(GUI gui, Menu parent, String textureName, Consumer<Image> setTextureFunc){
        super(gui, parent);
        setContent(new Component(0, 0, 600, 318){
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
                        public void drawText(Renderer renderer, double deltaTime){
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
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getComponentTextColor(0));
                renderer.drawCenteredText(x, y, x+width, y+20, textureName!=null?"Generate "+textureName+" Texture":"Generate Texture");
                renderer.drawCenteredText(x, y+20, x+width, y+40, texture.substring("fluids/templates/".length()));
            }
        });
        addButton("Confirm", () -> {
            setTextureFunc.accept(TextureManager.generateTexture(texture, getColor()));
            gui.open(parent);
        });
        addButton("Cancel", () -> {
            close();
        });
    }
    private Color getColor(){
        if(nak.isToggledOn)return TextureManager.getNaKColor(color);
        if(hotnak.isToggledOn)return TextureManager.getHotNaKColor(color);
        if(flibe.isToggledOn)return TextureManager.getFLiBeColor(color);
        return color;
    }
}