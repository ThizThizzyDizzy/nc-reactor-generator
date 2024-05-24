package net.ncplanner.plannerator.planner.gui.menu.component;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.ImageIO;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuGenerateTexture;
import static org.lwjgl.glfw.GLFW.*;
public class TextureButton extends Button{
    private final Supplier<Image> texture;
    private final Consumer<Image> setTextureFunc;
    private final String textureName;
    public TextureButton(Supplier<Image> texture, Consumer<Image> setTextureFunc){
        this(true, texture, setTextureFunc);
    }
    public TextureButton(boolean enabled, Supplier<Image> texture, Consumer<Image> setTextureFunc){
        this("", enabled, texture, setTextureFunc);
    }
    public TextureButton(String textureName, boolean enabled, Supplier<Image> texture, Consumer<Image> setTextureFunc){
        this(0, 0, 0, 0, textureName, enabled, texture, setTextureFunc);
    }
    public TextureButton(float x, float y, float width, float height, String textureName, boolean enabled, Supplier<Image> texture, Consumer<Image> setTextureFunc){
        super(x, y, width, height, textureName!=null?"Set "+textureName+" Texture":"Set Texture", enabled);
        this.textureName = textureName;
        this.texture = texture;
        this.setTextureFunc = setTextureFunc;
        addAction(() -> {
            try{
                Core.createFileChooser((file) -> {
                    try{
                        Image img = ImageIO.read(file);
                        if(img.getWidth()!=img.getHeight()){
                            Core.error("Image is not square!", null);
                            return;
                        }
                        setTextureFunc.accept(img);
                    }catch(IOException ex){
                        Core.error("Failed to load texture "+file.getName()+"!", ex);
                    }
                }, FileFormat.PNG, "texture");
            }catch(IOException ex){
                Core.error("Failed to load texture!", ex);
            }
        });
        setTooltip("Click or drop files to change "+(textureName==null?"":(textureName.toLowerCase(Locale.ROOT)+" "))+"texture\nOr right click to generate a texture");
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        Image tex = texture.get();
        if(tex!=null){
            renderer.setWhite();
            renderer.drawImage(tex, x, y, x+width, y+height);
            return;
        }
        super.draw(deltaTime);
    }
    @Override
    public void onFilesDropped(String[] files){
        for(String s : files){
            try{
                Image img = ImageIO.read(new File(s));
                if(img.getWidth()!=img.getHeight()){
                    Core.error("Image is not square!", null);
                    continue;
                }
                setTextureFunc.accept(img);
            }catch(IOException ex){
                Core.error("Failed to load texture "+s+"!", ex);
            }
        }
        super.onFilesDropped(files);
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        super.onMouseButton(x, y, button, action, mods);
        if(action==GLFW_PRESS&&enabled&&button==GLFW_MOUSE_BUTTON_RIGHT&&!pressed){
            new MenuGenerateTexture(gui, gui.menu, textureName, setTextureFunc).open();
        }
    }
}