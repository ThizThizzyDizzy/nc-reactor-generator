package old;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
public enum ReactorParts{
    CELL('C'),
    MODERATOR('M'),
    WATER('W'),
    REDSTONE('R'),
    QUARTZ('Q'),
    GOLD('O'),
    GLOWSTONE('G'),
    LAPIS('L'),
    DIAMOND('D'),
    HELIUM('H'),
    ENDERIUM('E'),
    CRYOTHEUM('Y'),
    IRON('I'),
    EMERALD('V'),
    COPPER('P'),
    TIN('T'),
    MAGNESIUM('N'),
    AIR(' ');
    static ReactorParts fromChar(char charAt) {
        for(ReactorParts part : values()){
            if(part.c==charAt)return part;
        }
        return null;
    }
    public final char c;
    private final String imageName;
    private BufferedImage image;
    private ReactorParts(char c){
        this.c = c;
        imageName = name().toLowerCase();
    }
    public BufferedImage getImage(){
        if(image!=null)return image;
        try {
            if(new File("nbproject").exists()){
                image = ImageIO.read(new File("src\\textures\\"+imageName+".png"));
            }else{
                JarFile jar = new JarFile(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ")));
                Enumeration enumEntries = jar.entries();
                while(enumEntries.hasMoreElements()){
                    JarEntry file = (JarEntry)enumEntries.nextElement();
                    System.out.println(file.getName());
                    if(file.getName().equals("textures/"+imageName+".png")){
                        image = ImageIO.read(jar.getInputStream(file));
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ReactorParts.class.getName()).log(Level.SEVERE, null, ex);
            image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
        return image;
    }
}