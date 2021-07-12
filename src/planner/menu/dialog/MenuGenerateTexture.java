package planner.menu.dialog;
import java.util.Locale;
import java.util.function.Consumer;
import multiblock.configuration.TextureManager;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.component.MenuComponentToggleBox;
import simplelibrary.image.Color;
import simplelibrary.image.Image;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuGenerateTexture extends MenuDialog{
    String[] textures = new String[]{"fluids/templates/liquid",
                                     "fluids/templates/molten",
                                     "fluids/templates/steam",
                                     "fluids/templates/gas"};
    public String texture = textures[0];
    public Color color = Color.WHITE;
    private MenuComponentToggleBox nak;
    private MenuComponentToggleBox flibe;
    private MenuComponentToggleBox hotnak;
    public MenuGenerateTexture(GUI gui, Menu parent, String textureName, Consumer<Image> setTextureFunc){
        super(gui, parent);
        setContent(new MenuComponent(0, 0, 600, 318){
            {
                for(int i = 0; i<textures.length; i++){
                    final int j = i;
                    add(new MenuComponentMinimalistButton(i*150, 40, 150, 150, "", true, true){
                        @Override
                        public void drawText(){
                            Core.applyColor(MenuGenerateTexture.this.getColor(), texture.equals(textures[j])?1f:0.25f);
                            drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/"+textures[j]+".png"));
                        }
                        @Override
                        public void action(){
                            texture = textures[j];
                        }
                    });
                }
                add(new MenuComponentMinimalistTextBox(0, 190, 600, 64, "#FFFFFF", true, "Color (decimal; prefix with # for hex)"){
                    @Override
                    public void onCharTyped(char c){
                        super.onCharTyped(c);
                        updateColor();
                    }
                    @Override
                    public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
                        super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
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
                nak = add(new MenuComponentToggleBox(0, 254, 200, 64, "NaK", false){
                    @Override
                    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                        super.onMouseButton(x, y, button, pressed, mods);
                        if(isToggledOn)hotnak.isToggledOn = flibe.isToggledOn = false;
                    }
                });
                hotnak = add(new MenuComponentToggleBox(200, 254, 200, 64, "Hot NaK", false){
                    @Override
                    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                        super.onMouseButton(x, y, button, pressed, mods);
                        if(isToggledOn)nak.isToggledOn = flibe.isToggledOn = false;
                    }
                });
                flibe = add(new MenuComponentToggleBox(400, 254, 200, 64, "FLiBe", false){
                    @Override
                    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                        super.onMouseButton(x, y, button, pressed, mods);
                        if(isToggledOn)nak.isToggledOn = hotnak.isToggledOn = false;
                    }
                });
            }
            @Override
            public void render(){
                Core.applyColor(Core.theme.getComponentTextColor(0));
                drawCenteredText(x, y, x+width, y+20, textureName!=null?"Generate "+textureName+" Texture":"Generate Texture");
                drawCenteredText(x, y+20, x+width, y+40, texture.substring("fluids/templates/".length()));
            }
        });
        addButton("Confirm", (e) -> {
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
        addButton("Cancel", (e) -> {
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