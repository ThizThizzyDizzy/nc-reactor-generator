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
    String[] textures = new String[]{"fluids/templates/liquid",
                                     "fluids/templates/molten",
                                     "fluids/templates/steam",
                                     "fluids/templates/gas"};
    public String texture = textures[0];
    public Color color = Color.WHITE;
    private ToggleBox nak;
    private ToggleBox flibe;
    private ToggleBox hotnak;
    public MenuGenerateTexture(GUI gui, Menu parent, String textureName, Consumer<Image> setTextureFunc){
        super(gui, parent);
        setContent(new Component(0, 0, 600, 318){
            {
                for(int i = 0; i<textures.length; i++){
                    final int j = i;
                    add(new Button(i*150, 40, 150, 150, "", true, true){
                        {
                            addAction(() -> {
                                texture = textures[j];
                            });
                        }
                        @Override
                        public void drawText(Renderer renderer, double deltaTime){
                            renderer.setColor(MenuGenerateTexture.this.getColor(), texture.equals(textures[j])?1f:0.25f);
                            renderer.drawImage(TextureManager.getImage(textures[j]), x, y, x+width, y+height);
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
            Image image = TextureManager.getImage(texture);
            for(int x = 0; x<image.getWidth(); x++){
                for(int y = 0; y<image.getHeight(); y++){
                    int rgb = image.getRGB(x, y);
                    Color grayColor = new Color(rgb);
                    Color theColor = getColor();
                    int r = (int)(theColor.getRed()*(grayColor.getRed()/255f));
                    int g = (int)(theColor.getGreen()*(grayColor.getGreen()/255f));
                    int b = (int)(theColor.getBlue()*(grayColor.getBlue()/255f));
                    image.setRGB(x, y, new Color(r, g, b, grayColor.getAlpha()).getRGB());
                }
            }
            setTextureFunc.accept(image);
            gui.open(parent);
        });
        addButton("Cancel", () -> {
            close();
        });
    }
    private Color getColor(){
        if(nak.isToggledOn)return blend(color, 0xFFe5BC, .375f);
        if(hotnak.isToggledOn)return blend(color, 0xFFe5BC, 0.2f);
        if(flibe.isToggledOn)return blend(color, 0xC1C8B0, 0.4f);
        return color;
    }
    public static Color blend(Color color1, int color2, float blendRatio){
            blendRatio = Math.max(0,Math.min(1, blendRatio));

            int alpha1 = color1.getAlpha();
            int red1 = color1.getRed();
            int green1 = color1.getGreen();
            int blue1 = color1.getBlue();

            int alpha2 = color2 >> 24 & 0xFF;
            int red2 = (color2 & 0xFF0000) >> 16;
            int green2 = (color2 & 0xFF00) >> 8;
            int blue2 = color2 & 0xFF;

            int alpha = Math.max(alpha1, alpha2);
            int red = (int) (red1 + (red2 - red1) * blendRatio);
            int green = (int) (green1 + (green2 - green1) * blendRatio);
            int blue = (int) (blue1 + (blue2 - blue1) * blendRatio);
            return new Color(red, green, blue, alpha);
    }
}