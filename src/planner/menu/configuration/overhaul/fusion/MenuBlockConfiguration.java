package planner.menu.configuration.overhaul.fusion;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import multiblock.configuration.overhaul.fusion.Block;
import planner.Core;
import planner.Main;
import planner.file.FileFormat;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuBlockConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true)).setTooltip("The name of this block. This should never change");
    private final MenuComponentMinimalistButton texture = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Select Texture", true, true).setTooltip("Change the texture for this block"));
    private final MenuComponentMinimalistTextBox cooling = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The amount of cooling this block provides as a heat sink");
    private final MenuComponentMinimalistOptionButton cluster = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Can Cluster", true, true, 0, "FALSE", "TRUE")).setTooltip("If true, this block can be part of a cluster");
    private final MenuComponentMinimalistOptionButton createCluster = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Creates Cluster", true, true, 0, "FALSE", "TRUE")).setTooltip("If true, this block will create a cluster");
    private final MenuComponentMinimalistOptionButton conductor = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Conductor", true, true, 0, "FALSE", "TRUE")).setTooltip("If true, this block will connect clusters to the casing, but will not connect them together");
    private final MenuComponentMinimalistOptionButton connector = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Connector", true, true, 0, "FALSE", "TRUE")).setTooltip("Connectors connect the reactor core to the toroid");
    private final MenuComponentMinimalistOptionButton core = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Core", true, true, 0, "FALSE", "TRUE")).setTooltip("Used for the fusion reactor Core");
    private final MenuComponentMinimalistOptionButton electromagnet = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Electromagnet", true, true, 0, "FALSE", "TRUE")).setTooltip("");
    private final MenuComponentMinimalistOptionButton heatingBlanket = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Heating Blanket", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistOptionButton reflector = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Reflector", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistOptionButton breedingBlanket = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Breeding Blanket", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistOptionButton augmentedBreedingBlanket = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Augmented Breeding Blanket", true, true, 0, "FALSE", "TRUE")).setTooltip("If false, this block will never be treated as an augmented breeding blanket for placement rules");
    private final MenuComponentMinimalistOptionButton shielding = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Neutron Shielding", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistTextBox efficiency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox shieldiness = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistOptionButton functional = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Functional", true, true, 0, "FALSE", "TRUE")).setTooltip("If true, this block will count against the sparsity penalty");
    private final MenuComponentMinimalistButton rules = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Placement Rules", true, true).setTooltip("Add, remove, or modify placement rules"));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Block block;
    private final int numComps = 21;
    public MenuBlockConfiguration(GUI gui, Menu parent, Block block){
        super(gui, parent);
        texture.addActionListener((e) -> {
            Core.createFileChooser((file, format) -> {
                try{
                    BufferedImage img = ImageIO.read(file);
                    if(img==null)return;
                    if(img.getWidth()!=img.getHeight()){
                        if(Main.hasAWT){
                            javax.swing.JOptionPane.showMessageDialog(null, "Image is not square!", "Error loading image", javax.swing.JOptionPane.ERROR_MESSAGE);
                        }else{
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                        }
                        return;
                    }
                    block.setTexture(img);
                }catch(IOException ex){}
            }, FileFormat.PNG);
        });
        rules.addActionListener((e) -> {
            gui.open(new MenuPlacementRulesConfiguration(gui, this, block));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.block = block;
    }
    @Override
    public void onGUIOpened(){
        name.text = block.name;
        cooling.text = block.cooling+"";
        cluster.setIndex(block.cluster?1:0);
        createCluster.setIndex(block.createCluster?1:0);
        conductor.setIndex(block.conductor?1:0);
        connector.setIndex(block.connector?1:0);
        core.setIndex(block.core?1:0);
        electromagnet.setIndex(block.electromagnet?1:0);
        heatingBlanket.setIndex(block.heatingBlanket?1:0);
        reflector.setIndex(block.reflector?1:0);
        breedingBlanket.setIndex(block.breedingBlanket?1:0);
        augmentedBreedingBlanket.setIndex(block.augmentedBreedingBlanket?1:0);
        shielding.setIndex(block.shielding?1:0);
        efficiency.text = block.efficiency+"";
        shieldiness.text = block.shieldiness+"";
        functional.setIndex(block.functional?1:0);
    }
    @Override
    public void onGUIClosed(){
        block.name = name.text;
        block.cooling = Integer.parseInt(cooling.text);
        block.cluster = cluster.getIndex()==1;
        block.createCluster = createCluster.getIndex()==1;
        block.conductor = conductor.getIndex()==1;
        block.connector = connector.getIndex()==1;
        block.core = core.getIndex()==1;
        block.electromagnet = electromagnet.getIndex()==1;
        block.heatingBlanket = heatingBlanket.getIndex()==1;
        block.reflector = reflector.getIndex()==1;
        block.breedingBlanket = breedingBlanket.getIndex()==1;
        block.augmentedBreedingBlanket = augmentedBreedingBlanket.getIndex()==1;
        block.shielding = shielding.getIndex()==1;
        block.efficiency = Float.parseFloat(efficiency.text);
        block.shieldiness = Float.parseFloat(shieldiness.text);
        block.functional = functional.getIndex()==1;
    }
    @Override
    public void render(int millisSinceLastTick){
        drawRect(0, gui.helper.displayHeight()/numComps, gui.helper.displayHeight()/numComps, gui.helper.displayHeight()/numComps*2, Core.getTexture(block.texture));
        cluster.enabled = conductor.getIndex()==0;
        conductor.enabled = cluster.getIndex()==0;
        createCluster.enabled = cluster.getIndex()==1;
        augmentedBreedingBlanket.enabled = breedingBlanket.getIndex()==1;
        efficiency.editable = reflector.getIndex()==1;
        shieldiness.editable = shielding.getIndex()==1;
        heatingBlanket.enabled = reflector.getIndex()==0&&breedingBlanket.getIndex()==0;
        reflector.enabled = heatingBlanket.getIndex()==0&&breedingBlanket.getIndex()==0;
        breedingBlanket.enabled = heatingBlanket.getIndex()==0&&reflector.getIndex()==0;
        cooling.width = efficiency.width = shieldiness.width = gui.helper.displayWidth()*.75;
        cooling.x = efficiency.x = shieldiness.x = gui.helper.displayWidth()-cooling.width;
        functional.width = shielding.width = augmentedBreedingBlanket.width = breedingBlanket.width = reflector.width = heatingBlanket.width = conductor.width = connector.width = core.width = electromagnet.width = createCluster.width = cluster.width = name.width = rules.width = back.width = gui.helper.displayWidth();
        texture.x = texture.height = functional.height = shieldiness.height = efficiency.height = shielding.height = augmentedBreedingBlanket.height = breedingBlanket.height = reflector.height = heatingBlanket.height = conductor.height = connector.height = core.height = electromagnet.height = createCluster.height = cluster.height = cooling.height = name.height = rules.height = back.height = gui.helper.displayHeight()/numComps;
        texture.width = gui.helper.displayWidth()-texture.x;
        texture.y = name.height;
        cooling.y = texture.y+texture.height;
        cluster.y = cooling.y+cooling.height;
        createCluster.y = cluster.y+cluster.height;
        conductor.y = createCluster.y+createCluster.height;
        connector.y = conductor.y+conductor.height;
        core.y = connector.y+connector.height;
        electromagnet.y = core.y+core.height;
        heatingBlanket.y = electromagnet.y+electromagnet.height;
        reflector.y = heatingBlanket.y+heatingBlanket.height;
        breedingBlanket.y = reflector.y+reflector.height;
        augmentedBreedingBlanket.y = breedingBlanket.y+breedingBlanket.height;
        shielding.y = augmentedBreedingBlanket.y+augmentedBreedingBlanket.height;
        efficiency.y = shielding.y+shielding.height;
        shieldiness.y = efficiency.y+efficiency.height;
        functional.y = shieldiness.y+shieldiness.height;
        rules.y = functional.y+functional.height;
        back.y = gui.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, cooling.y, cooling.x, cooling.y+cooling.height, "Cooling");
        drawText(0, efficiency.y, efficiency.x, efficiency.y+efficiency.height, "Efficiency");
        drawText(0, shieldiness.y, shieldiness.x, shieldiness.y+shieldiness.height, "Shieldiness");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}