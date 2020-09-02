package planner.menu.configuration.underhaul.fissionsfr;
import multiblock.configuration.Configuration;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import static simplelibrary.opengl.Renderer2D.drawText;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuFissionSFRConfiguration extends Menu{
    private final MenuComponentMinimalistButton blocks = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Blocks", true, true));
    private final MenuComponentMinimalistButton fuels = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Fuels", true, true));
    private final MenuComponentMinimalistTextBox minSize = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox maxSize = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox neutronReach = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox moderatorExtraPower = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox moderatorExtraHeat = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox activeCoolerRate = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Configuration configuration;
    public MenuFissionSFRConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        blocks.addActionListener((e) -> {
            gui.open(new MenuBlocksConfiguration(gui, this, configuration));
        });
        fuels.addActionListener((e) -> {
            gui.open(new MenuFuelsConfiguration(gui, this, configuration));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.configuration = configuration;
    }
    @Override
    public void onGUIOpened(){
        blocks.label = "Blocks ("+configuration.underhaul.fissionSFR.blocks.size()+")";
        fuels.label = "Fuels ("+configuration.underhaul.fissionSFR.fuels.size()+")";
        minSize.text = configuration.underhaul.fissionSFR.minSize+"";
        maxSize.text = configuration.underhaul.fissionSFR.maxSize+"";
        neutronReach.text = configuration.underhaul.fissionSFR.neutronReach+"";
        moderatorExtraPower.text = configuration.underhaul.fissionSFR.moderatorExtraPower+"";
        moderatorExtraHeat.text = configuration.underhaul.fissionSFR.moderatorExtraHeat+"";
        activeCoolerRate.text = configuration.underhaul.fissionSFR.activeCoolerRate+"";
    }
    @Override
    public void onGUIClosed(){
        configuration.underhaul.fissionSFR.minSize = Integer.parseInt(minSize.text);
        configuration.underhaul.fissionSFR.maxSize = Integer.parseInt(maxSize.text);
        configuration.underhaul.fissionSFR.neutronReach = Integer.parseInt(neutronReach.text);
        configuration.underhaul.fissionSFR.moderatorExtraPower = Float.parseFloat(moderatorExtraPower.text);
        configuration.underhaul.fissionSFR.moderatorExtraHeat = Float.parseFloat(moderatorExtraHeat.text);
        configuration.underhaul.fissionSFR.activeCoolerRate = Integer.parseInt(activeCoolerRate.text);
    }
    @Override
    public void render(int millisSinceLastTick){
        minSize.width = maxSize.width = neutronReach.width = moderatorExtraPower.width = moderatorExtraHeat.width = activeCoolerRate.width = Core.helper.displayWidth()*.75;
        minSize.x = maxSize.x = neutronReach.x = moderatorExtraPower.x = moderatorExtraHeat.x = activeCoolerRate.x = Core.helper.displayWidth()*.25;
        blocks.width = fuels.width = back.width = Core.helper.displayWidth();
        minSize.height = maxSize.height = neutronReach.height = moderatorExtraPower.height = moderatorExtraHeat.height = activeCoolerRate.height = blocks.height = fuels.height = back.height = Core.helper.displayHeight()/16;
        fuels.y = blocks.height;
        minSize.y = fuels.y+fuels.height;
        maxSize.y = minSize.y+minSize.height;
        neutronReach.y = maxSize.y+maxSize.height;
        moderatorExtraPower.y = neutronReach.y+neutronReach.height;
        moderatorExtraHeat.y = moderatorExtraPower.y+moderatorExtraPower.height;
        activeCoolerRate.y = moderatorExtraHeat.y+moderatorExtraHeat.height;
        back.y = Core.helper.displayHeight()-back.height;
        if(configuration.addon){
            minSize.y = maxSize.y = neutronReach.y = moderatorExtraPower.y = moderatorExtraHeat.y = activeCoolerRate.y = -minSize.height;
        }
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, minSize.y+Core.helper.displayHeight()/64, Core.helper.displayWidth()*.25, minSize.y+minSize.height-Core.helper.displayHeight()/64, "Minimum reactor size");
        drawText(0, maxSize.y+Core.helper.displayHeight()/64, Core.helper.displayWidth()*.25, maxSize.y+maxSize.height-Core.helper.displayHeight()/64, "Maximum reactor size");
        drawText(0, neutronReach.y+Core.helper.displayHeight()/64, Core.helper.displayWidth()*.25, neutronReach.y+neutronReach.height-Core.helper.displayHeight()/64, "Neutron reach");
        drawText(0, moderatorExtraPower.y+Core.helper.displayHeight()/64, Core.helper.displayWidth()*.25, moderatorExtraPower.y+moderatorExtraPower.height-Core.helper.displayHeight()/64, "Moderator extra power");
        drawText(0, moderatorExtraHeat.y+Core.helper.displayHeight()/64, Core.helper.displayWidth()*.25, moderatorExtraHeat.y+moderatorExtraHeat.height-Core.helper.displayHeight()/64, "Moderator extra heat");
        drawText(0, activeCoolerRate.y+Core.helper.displayHeight()/64, Core.helper.displayWidth()*.25, activeCoolerRate.y+activeCoolerRate.height-Core.helper.displayHeight()/64, "Active cooler fluid rate");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}