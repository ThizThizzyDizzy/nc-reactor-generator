package overhaul;
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
    public static final ReactorPart FUEL_CELL = fuelCell(null, "{FUEL};False;None", 0);
    public static final ReactorPart FUEL_CELL_CF_252 = fuelCell("Cf-252", "{FUEL};True;Cf-252", 1);
    public static final ReactorPart FUEL_CELL_PO_BE = fuelCell("Po-Be", "{FUEL};True;Po-Be", .95);
    public static final ReactorPart FUEL_CELL_RA_BE = fuelCell("Ra-Be", "{FUEL};True;Ra-Be", .9);
    public static final ReactorPart HEATSINK_WATER = heatsink("Water", "Water", 55, PlacementRule.atLeast(1, Type.FUEL_CELL));
    public static final ReactorPart HEATSINK_IRON = heatsink("Iron", "Iron", 50, PlacementRule.atLeast(1, Type.MODERATOR));
    public static final ReactorPart HEATSINK_REDSTONE = heatsink("Redstone", "Redstone", 85, PlacementRule.atLeast(1, Type.FUEL_CELL), PlacementRule.atLeast(1, Type.MODERATOR));
    public static final ReactorPart HEATSINK_QUARTZ = heatsink("Quartz", "Quartz", 75, PlacementRule.atLeast(1, HEATSINK_REDSTONE));
    public static final ReactorPart HEATSINK_GLOWSTONE = heatsink("Glowstone", "Glowstone", 100, PlacementRule.atLeast(2, Type.MODERATOR));
    public static final ReactorPart HEATSINK_OBSIDIAN = heatsink("Obsidian", "Obsidian", 70, PlacementRule.axis(HEATSINK_GLOWSTONE));
    public static final ReactorPart HEATSINK_NETHER_BRICK = heatsink("Nether Brick", "NetherBrick", 105, PlacementRule.atLeast(1, HEATSINK_OBSIDIAN));
    public static final ReactorPart HEATSINK_LAPIS = heatsink("Lapis", "Lapis", 95, PlacementRule.atLeast(1, Type.FUEL_CELL), PlacementRule.atLeast(1, Type.CASING));
    public static final ReactorPart HEATSINK_GOLD = heatsink("Gold", "Gold", 110, PlacementRule.atLeast(2, HEATSINK_IRON));
    public static final ReactorPart HEATSINK_PRISMARINE = heatsink("Prismarine", "Prismarine", 115, PlacementRule.atLeast(2, HEATSINK_WATER));
    public static final ReactorPart HEATSINK_LEAD = heatsink("Lead", "Lead", 60, PlacementRule.atLeast(1, HEATSINK_IRON));
    public static final ReactorPart HEATSINK_SLIME = heatsink("Slime", "Slime", 135, PlacementRule.exactly(1, HEATSINK_WATER), PlacementRule.atLeast(2, HEATSINK_LEAD));
    public static final ReactorPart HEATSINK_END_STONE = heatsink("End Stone", "EndStone", 65, PlacementRule.atLeast(1, Type.REFLECTOR));
    public static final ReactorPart HEATSINK_PURPUR = heatsink("Purpur", "Purpur", 90, PlacementRule.exactly(1, HEATSINK_IRON), PlacementRule.atLeast(1, HEATSINK_END_STONE));
    public static final ReactorPart HEATSINK_DIAMOND = heatsink("Diamond", "Diamond", 195, PlacementRule.atLeast(1, HEATSINK_GOLD),PlacementRule.atLeast(1, Type.FUEL_CELL));
    public static final ReactorPart HEATSINK_EMERALD = heatsink("Emerald", "Emerald", 190, PlacementRule.atLeast(1, HEATSINK_PRISMARINE),PlacementRule.atLeast(1, Type.MODERATOR));
    public static final ReactorPart HEATSINK_COPPER = heatsink("Copper", "Copper", 80, PlacementRule.atLeast(1, HEATSINK_WATER));
    public static final ReactorPart HEATSINK_TIN = heatsink("Tin", "Tin", 120, PlacementRule.axis(HEATSINK_LAPIS));
    public static final ReactorPart HEATSINK_BORON = heatsink("Boron", "Boron", 165, PlacementRule.exactly(1, HEATSINK_QUARTZ), PlacementRule.atLeast(1, Type.CASING));
    public static final ReactorPart HEATSINK_LITHIUM = heatsink("Lithium", "Lithium", 130, PlacementRule.axis(HEATSINK_LEAD), PlacementRule.atLeast(1, Type.CASING));
    public static final ReactorPart HEATSINK_MAGNESIUM = heatsink("Magnesium", "Magnesium", 125, PlacementRule.exactly(1, Type.MODERATOR), PlacementRule.atLeast(1, Type.CASING));
    public static final ReactorPart HEATSINK_MANGANESE = heatsink("Manganese", "Manganese", 145, PlacementRule.atLeast(2, Type.FUEL_CELL));
    public static final ReactorPart HEATSINK_ALUMINUM = heatsink("Aluminum", "Aluminum", 185, PlacementRule.atLeast(1, HEATSINK_QUARTZ), PlacementRule.atLeast(1, HEATSINK_LAPIS));
    public static final ReactorPart HEATSINK_SILVER = heatsink("Silver", "Silver", 170, PlacementRule.atLeast(2, HEATSINK_GLOWSTONE), PlacementRule.atLeast(1, HEATSINK_TIN));
    public static final ReactorPart HEATSINK_FLUORITE = heatsink("Fluorite", "Fluorite", 175, PlacementRule.atLeast(1, HEATSINK_GOLD), PlacementRule.atLeast(1, HEATSINK_PRISMARINE));
    public static final ReactorPart HEATSINK_VILLIAUMITE = heatsink("Villiaumite", "Villiaumite", 160, PlacementRule.atLeast(1, HEATSINK_END_STONE), PlacementRule.atLeast(1, HEATSINK_REDSTONE));
    public static final ReactorPart HEATSINK_CAROBBIITE = heatsink("Carobbiite", "Carobbiite", 150, PlacementRule.atLeast(1, HEATSINK_COPPER), PlacementRule.atLeast(1, HEATSINK_END_STONE));
    public static final ReactorPart HEATSINK_ARSENIC = heatsink("Arsenic", "Arsenic", 140, PlacementRule.axis(Type.REFLECTOR));
    public static final ReactorPart HEATSINK_NITROGEN = heatsink("Nitrogen", "Nitrogen", 180, PlacementRule.atLeast(2, HEATSINK_COPPER), PlacementRule.atLeast(1, HEATSINK_PURPUR));
    public static final ReactorPart HEATSINK_HELIUM = heatsink("Helium", "Helium", 200, PlacementRule.exactly(2, HEATSINK_REDSTONE));
    public static final ReactorPart HEATSINK_ENDERIUM = heatsink("Enderium", "Enderium", 155, PlacementRule.atLeast(3, Type.MODERATOR));
    public static final ReactorPart HEATSINK_CRYOTHEUM = heatsink("Cryotheum", "Cryotheum", 205, PlacementRule.atLeast(3, Type.FUEL_CELL));
    public static final ReactorPart BERYLLIUM = moderator("Beryllium", "Beryllium", 22, 1.05);
    public static final ReactorPart GRAPHITE = moderator("Graphite", "Graphite", 10, 1.1);
    public static final ReactorPart HEAVY_WATER = moderator("Heavy Water", "HeavyWater", 36, 1);
    public static final ReactorPart CONDUCTOR = conductor();
    public static final ReactorPart REFLECTOR_BERYLLIUM_CARBON = reflector("Beryllium-Carbon", "Beryllium-Carbon", 1, .5);
    public static final ReactorPart REFLECTOR_LEAD_STEEL = reflector("Lead-Steel", "Lead-Steel", 0.5, .25);
    public static ReactorPart CASING = new ReactorPart(Type.CASING, "Casing", null, null);
    private static ReactorPart add(ReactorPart p){
        parts.add(p);
        return p;
    }
    private static ReactorPart air(){
        return add(new ReactorPart(Type.AIR, "Air", null, "air"));
    }
    private static ReactorPart fuelCell(String name, String jsonName, double efficiency){
        return add(new FuelCell(name, jsonName, efficiency));
    }
    private static ReactorPart heatsink(String name, String jsonName, int cooling, PlacementRule... rules){
        return add(new Heatsink(name, jsonName, cooling, rules));
    }
    private static ReactorPart moderator(String name, String jsonName, int fluxFactor, double efficiencyFactor){
        return add(new Moderator(name, fluxFactor, jsonName, efficiencyFactor));
    }
    private static ReactorPart conductor(){
        return add(new ReactorPart(Type.CONDUCTOR, "Conductor", "Conductors", "conductor"));
    }
    private static ReactorPart reflector(String name, String jsonName, double reflectivity, double efficiency){
        return add(new Reflector(name, reflectivity, jsonName, efficiency));
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
        HEATSINK(true),
        MODERATOR(false),
        CONDUCTOR(false),
        REFLECTOR(false),
        CASING(false);
        public final boolean canCluster;//For some reason, this is the only type-specific variable that I didn't hard-code everywhere (like Line-of-sight)
        private Type(boolean canCluster){
            this.canCluster = canCluster;
        }
    }
}