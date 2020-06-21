package planner.menu.configuration.underhaul.fissionsfr;
import java.awt.Color;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import static simplelibrary.opengl.Renderer2D.drawText;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuFissionSFRConfiguration extends Menu{
    private static final Color textColor = new Color(.1f, .1f, .2f, 1f);
    private final MenuComponentMinimalistButton blocks = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Blocks", true, true));
    private final MenuComponentMinimalistButton fuels = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Fuels", true, true));
    private final MenuComponentMinimalistTextBox minSize = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox maxSize = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox neutronReach = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox moderatorExtraPower = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox moderatorExtraHeat = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox activeCoolerRate = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    public MenuFissionSFRConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        blocks.addActionListener((e) -> {
            gui.open(new MenuBlocksConfiguration(gui, this));
        });
        fuels.addActionListener((e) -> {
            gui.open(new MenuFuelsConfiguration(gui, this));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void onGUIOpened(){
        blocks.label = "Blocks ("+Core.configuration.underhaul.fissionSFR.blocks.size()+")";
        fuels.label = "Fuels ("+Core.configuration.underhaul.fissionSFR.fuels.size()+")";
        minSize.text = Core.configuration.underhaul.fissionSFR.minSize+"";
        maxSize.text = Core.configuration.underhaul.fissionSFR.maxSize+"";
        neutronReach.text = Core.configuration.underhaul.fissionSFR.neutronReach+"";
        moderatorExtraPower.text = Core.configuration.underhaul.fissionSFR.moderatorExtraPower+"";
        moderatorExtraHeat.text = Core.configuration.underhaul.fissionSFR.moderatorExtraHeat+"";
        activeCoolerRate.text = Core.configuration.underhaul.fissionSFR.activeCoolerRate+"";
    }
    @Override
    public void onGUIClosed(){
        Core.configuration.underhaul.fissionSFR.minSize = Integer.parseInt(minSize.text);
        Core.configuration.underhaul.fissionSFR.maxSize = Integer.parseInt(maxSize.text);
        Core.configuration.underhaul.fissionSFR.neutronReach = Integer.parseInt(neutronReach.text);
        Core.configuration.underhaul.fissionSFR.moderatorExtraPower = Float.parseFloat(moderatorExtraPower.text);
        Core.configuration.underhaul.fissionSFR.moderatorExtraHeat = Float.parseFloat(moderatorExtraHeat.text);
        Core.configuration.underhaul.fissionSFR.activeCoolerRate = Integer.parseInt(activeCoolerRate.text);
    }
    @Override
    public void render(int millisSinceLastTick){
        minSize.width = maxSize.width = neutronReach.width = moderatorExtraPower.width = moderatorExtraHeat.width = activeCoolerRate.width = Display.getWidth()*.75;
        minSize.x = maxSize.x = neutronReach.x = moderatorExtraPower.x = moderatorExtraHeat.x = activeCoolerRate.x = Display.getWidth()*.25;
        blocks.width = fuels.width = back.width = Display.getWidth();
        minSize.height = maxSize.height = neutronReach.height = moderatorExtraPower.height = moderatorExtraHeat.height = activeCoolerRate.height = blocks.height = fuels.height = back.height = Display.getHeight()/16;
        fuels.y = blocks.height;
        minSize.y = fuels.y+fuels.height;
        maxSize.y = minSize.y+minSize.height;
        neutronReach.y = maxSize.y+maxSize.height;
        moderatorExtraPower.y = neutronReach.y+neutronReach.height;
        moderatorExtraHeat.y = moderatorExtraPower.y+moderatorExtraPower.height;
        activeCoolerRate.y = moderatorExtraHeat.y+moderatorExtraHeat.height;
        back.y = Display.getHeight()-back.height;
        GL11.glColor4f(textColor.getRed()/255f, textColor.getGreen()/255f, textColor.getBlue()/255f, textColor.getAlpha()/255f);
        drawText(0, minSize.y+Display.getHeight()/64, Display.getWidth()*.25, minSize.y+minSize.height-Display.getHeight()/64, "Minimum reactor size");
        drawText(0, maxSize.y+Display.getHeight()/64, Display.getWidth()*.25, maxSize.y+maxSize.height-Display.getHeight()/64, "Maximum reactor size");
        drawText(0, neutronReach.y+Display.getHeight()/64, Display.getWidth()*.25, neutronReach.y+neutronReach.height-Display.getHeight()/64, "Neutron reach");
        drawText(0, moderatorExtraPower.y+Display.getHeight()/64, Display.getWidth()*.25, moderatorExtraPower.y+moderatorExtraPower.height-Display.getHeight()/64, "Moderator extra power");
        drawText(0, moderatorExtraHeat.y+Display.getHeight()/64, Display.getWidth()*.25, moderatorExtraHeat.y+moderatorExtraHeat.height-Display.getHeight()/64, "Moderator extra heat");
        drawText(0, activeCoolerRate.y+Display.getHeight()/64, Display.getWidth()*.25, activeCoolerRate.y+activeCoolerRate.height-Display.getHeight()/64, "Active cooler fluid rate");
        GL11.glColor4f(1, 1, 1, 1);
        super.render(millisSinceLastTick);
    }
}