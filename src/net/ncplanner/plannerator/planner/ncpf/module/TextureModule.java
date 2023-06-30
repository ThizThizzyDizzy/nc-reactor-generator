package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class TextureModule extends NCPFModule{
    public Image texture;
    public TextureModule(){
        super("plannerator:texture");
    }
    @Override
    public void conglomerate(NCPFModule addon){}
    @Override
    public void convertFromObject(NCPFObject ncpf){
        texture = Image.fromBase64(ncpf.getString("texture"));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("texture", texture.toBase64());
    }
}