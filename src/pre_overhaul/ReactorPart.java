package pre_overhaul;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.imageio.ImageIO;
public class ReactorPart implements ReactorBit{
    public static final ArrayList<ReactorPart> parts = new ArrayList<>();
    public static final ReactorPart AIR = air();
    public static final ReactorPart FUEL_CELL = fuelCell();
    public static final ReactorPart COOLER_WATER = cooler("Water", "Water", 60, PlacementRule.or(PlacementRule.atLeast(1, Type.FUEL_CELL),PlacementRule.atLeast(1, Type.MODERATOR)));
    public static final ReactorPart COOLER_REDSTONE = cooler("Redstone", "Redstone", 90, PlacementRule.atLeast(1, Type.FUEL_CELL));
    public static final ReactorPart COOLER_QUARTZ = cooler("Quartz", "Quartz", 90, PlacementRule.atLeast(1, Type.MODERATOR));
    public static final ReactorPart COOLER_GOLD = cooler("Gold", "Gold", 120, PlacementRule.atLeast(1, COOLER_WATER), PlacementRule.atLeast(1, COOLER_REDSTONE));
    public static final ReactorPart COOLER_GLOWSTONE = cooler("Glowstone", "Glowstone", 130, PlacementRule.atLeast(2, Type.MODERATOR));
    public static final ReactorPart COOLER_LAPIS = cooler("Lapis", "Lapis", 120, PlacementRule.atLeast(1, Type.FUEL_CELL), PlacementRule.atLeast(1, Type.CASING));
    public static final ReactorPart COOLER_DIAMOND = cooler("Diamond", "Diamond", 150, PlacementRule.atLeast(1, COOLER_WATER), PlacementRule.atLeast(1, COOLER_QUARTZ));
    public static final ReactorPart COOLER_HELIUM = cooler("Helium", "Helium", 140, PlacementRule.exactly(1, COOLER_REDSTONE), PlacementRule.atLeast(1, Type.CASING));
    public static final ReactorPart COOLER_ENDERIUM = cooler("Enderium", "Enderium", 120, PlacementRule.exactly(3, Type.CASING));
    public static final ReactorPart COOLER_CRYOTHEUM = cooler("Cryotheum", "Cryotheum", 160, PlacementRule.atLeast(2, Type.FUEL_CELL));
    public static final ReactorPart COOLER_IRON = cooler("Iron", "Iron", 80, PlacementRule.atLeast(1, COOLER_GOLD));
    public static final ReactorPart COOLER_EMERALD = cooler("Emerald", "Emerald", 160, PlacementRule.atLeast(1, Type.MODERATOR), PlacementRule.atLeast(1, Type.FUEL_CELL));
    public static final ReactorPart COOLER_COPPER = cooler("Copper", "Copper", 80, PlacementRule.atLeast(1, COOLER_GLOWSTONE));
    public static final ReactorPart COOLER_TIN = cooler("Tin", "Tin", 120, PlacementRule.axis(COOLER_LAPIS));
    public static final ReactorPart COOLER_MAGNESIUM = cooler("Magnesium", "Magnesium", 110, PlacementRule.atLeast(1, Type.CASING), PlacementRule.atLeast(1, Type.MODERATOR));
    public static final ReactorPart GRAPHITE = moderator("Graphite", "Graphite");
    public static final ReactorPart BERYLLIUM = moderator("Beryllium", "Beryllium");
    public static ReactorPart CASING = new ReactorPart(Type.CASING, "Casing", null, null);
    private static ReactorPart add(ReactorPart p){
        parts.add(p);
        return p;
    }
    private static ReactorPart air(){
        return add(new ReactorPart(Type.AIR, "Air", null, "air"));
    }
    private static ReactorPart fuelCell(){
        return add(new ReactorPart(Type.FUEL_CELL, "Fuel Cell", "FuelCell", "cell"));
    }
    private static ReactorPart cooler(String name, String jsonName, int cooling, PlacementRule... rules){
        return add(new Cooler(name, jsonName, cooling, rules));
    }
    private static ReactorPart moderator(String name, String jsonName){
        return add(new ReactorPart(Type.MODERATOR, name+" Moderator", jsonName, name.replace(" ", "_").toLowerCase()));
    }
    public static ReactorPart random(Random rand){
        int[] is = Main.instance.listParts.getSelectedIndices();
        if(is.length==0)return ReactorPart.AIR;
        return parts.get(is[rand.nextInt(is.length)]);
    }
    public static ReactorPart parse(String string){
        if(string.contains(";")){
            String[] strs = string.split("\\Q;");
            string = strs[strs.length-1];
            if(string.equals("None"))return FUEL_CELL;
        }
        for(ReactorPart part : parts){
            if(part.name.replace(" ", "_").equalsIgnoreCase(string))return part;
        }
        for(ReactorPart part : parts){
            if(part.name.toLowerCase().replace(" ", "_").startsWith(string.toLowerCase()))return part;
        }
        for(ReactorPart part : parts){
            if(part.name.toLowerCase().replace(" ", "_").contains(string.toLowerCase()))return part;
        }
        for(ReactorPart part : parts){
            if(part.name.toLowerCase().replace(" ", "").startsWith(string.toLowerCase()))return part;
        }
        for(ReactorPart part : parts){
            if(part.name.toLowerCase().replace(" ", "").contains(string.toLowerCase()))return part;
        }
        if(string.contains(" "))return parse(string.replace(" ", "").trim());
        return null;
    }
    public final Type type;
    private final String name;
    private BufferedImage image;
    private final String texture;
    /**
     * For exporting to Hellrage's Reactor Planner
     */
    String jsonName;
    public ReactorPart(Type type, String name, String jsonName, String texture){
        this.type = type;
        this.name = name;
        this.jsonName = jsonName;
        this.texture = texture;
    }
    @Override
    public String toString(){
        return name;
    }
    boolean matches(ReactorBit bit){
        return this==bit||type==bit;
    }
    boolean matches(Iterable<ReactorBit> bits){
        for(ReactorBit bit : bits){
            if(matches(bit))return true;
        }
        return false;
    }
    public BufferedImage getImage(){
        if(image!=null)return image;
        try{
            if(new File("nbproject").exists()){
                image = ImageIO.read(new File("src\\textures\\"+texture.replace("/", "\\")+".png"));
            }else{
                JarFile jar = new JarFile(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ")));
                Enumeration enumEntries = jar.entries();
                while(enumEntries.hasMoreElements()){
                    JarEntry file = (JarEntry)enumEntries.nextElement();
                    System.out.println(file.getName());
                    if(file.getName().equals("textures/"+texture.replace("\\", "/")+".png")){
                        image = ImageIO.read(jar.getInputStream(file));
                        break;
                    }
                }
            }
        }catch(IOException ex){
            System.err.println("Couldn't read file: "+texture);
            image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
        return image;
    }
    public static enum Type implements ReactorBit{
        AIR(false),
        FUEL_CELL(true),
        COOLER(true),
        MODERATOR(false),
        CASING(false);
        public final boolean canCluster;//For some reason, this is the only type-specific variable that I didn't hard-code everywhere (like Line-of-sight)
        private Type(boolean canCluster){
            this.canCluster = canCluster;
        }
    }
}