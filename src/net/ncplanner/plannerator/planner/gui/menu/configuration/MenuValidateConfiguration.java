package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.AddonConfiguration;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.OverhaulConfiguration;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.UnderhaulConfiguration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
public class MenuValidateConfiguration extends ConfigurationMenu{
    private static final String namespaceChars = "0123456789abcdefghijklmnopqrstuvwxyz_-.";
    private static final String namespacePathChars = "0123456789abcdefghijklmnopqrstuvwxyz_-./";
    public Validator validator = new Validator() {
        @Override
        public void stage(String stage){
            label.text = "Validating... ("+stage+")";
        }
        @Override
        public void onFinish(){
            label.text = list.components.isEmpty()?"All good!":"Found "+list.components.size()+" issue"+(list.components.size()==1?"":"s");
        }
        @Override
        public void message(Validator.ValidatorMessage message){
            String tooltip = null;
            if(message.hint!=null)tooltip = message.hint;
            if(message.solveHint!=null){
                if(tooltip==null)tooltip = "Solve: "+message.solveHint;
                else tooltip+="\nSolve: "+message.solveHint;
            }
            Label lbl = list.add(new Label(0, 0, 0, 32, message.message, true).setTextColor(() -> {
                return message.color;
            }).setTooltip(tooltip));
            if(message.solveFunc!=null){
                Button button = lbl.add(new Button(0, 0, lbl.height*4, lbl.height, "Solve", true, true){
                    @Override
                    public void render2d(double deltaTime){
                        x = lbl.width-width;
                        super.render2d(deltaTime);
                    }
                }.setTooltip(message.solveHint));
                button.addAction(() -> {
                    message.solveFunc.run();
                    list.components.remove(lbl);
                });
            }
        }
    };
    private final Label label;
    private final SingleColumnList list;
    public MenuValidateConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent, configuration, "Validate");
        label = add(new Label(sidebar.width, 0, 0, 48, "Validating..."));
        list = add(new SingleColumnList(sidebar.width, label.height, 0, 0, 32));
    }
    @Override
    public void onOpened(){
        super.onOpened();
        list.components.clear();
        Thread t = new Thread(() -> {
            Runnable modifyConfig = () -> {
                gui.open(parent);
            };
            if(configuration.name.equals("NuclearCraft")){
                validator.stage("Checking internal NuclearCraft Underhaul configuration");
                validator.warn("Configuration uses NuclearCraft name!").hint("\"NuclearCraft\" should only be used as the configuration name if this is the vanilla NC configuration.").solve(modifyConfig, "Go to the Modify Configuration menu");
                boolean underhaulGood = configuration.isUnderhaulConfigurationEqual(Configuration.NUCLEARCRAFT)||!configuration.underhaulNameMatches(Configuration.NUCLEARCRAFT);
                if(!underhaulGood)validator.error("Underhaul configuration does not match internal NuclearCraft configuration!").hint("\"NuclearCraft\" should only be used as the configuration name if this is the vanilla NC configuration.").solve(modifyConfig, "Go to the Modify Configuration menu");
                boolean overhaulGood = configuration.isOverhaulConfigurationEqual(Configuration.NUCLEARCRAFT)||!configuration.overhaulNameMatches(Configuration.NUCLEARCRAFT);
                if(!overhaulGood)validator.error("Overhaul configuration does not match internal NuclearCraft configuration!").hint("\"NuclearCraft\" should only be used as the configuration name if this is the vanilla NC configuration.").solve(modifyConfig, "Go to the Modify Configuration menu");
            }
            validator.stage("Checking configuration metadata");
            if(configuration.underhaul==null&&configuration.overhaul==null)validator.error("Configuration has neither underhaul nor overhaul configuration!").solve(modifyConfig, "Go to the Modify Configuration menu");
            if(configuration.overhaul==null&&configuration.overhaulVersion!=null)validator.error("Configuration has overhaul version "+configuration.overhaulVersion+", but no overhaul configuration!").solve(() -> {
                configuration.overhaulVersion = null;
            }, "Clear the overhaul version");
            if(configuration.underhaul==null&&configuration.underhaulVersion!=null)validator.error("Configuration has underhaul version "+configuration.underhaulVersion+", but no underhaul configuration!").solve(() -> {
                configuration.underhaulVersion = null;
            }, "Clear the underhaul version");;
            if(configuration.overhaul!=null&&configuration.overhaulVersion==null)validator.error("Configuration has overhaul configuration, but no overhaul version!").solve(modifyConfig, "Go to the Modify Configuration menu");
            if(configuration.underhaul!=null&&configuration.underhaulVersion==null)validator.error("Configuration has underhaul configuration, but no underhaul version!").solve(modifyConfig, "Go to the Modify Configuration menu");
            if(configuration.underhaul!=null){
                validator.stage("Checking underhaul configuration");
                UnderhaulConfiguration underhaul = configuration.underhaul;
                if(underhaul.fissionSFR==null){
                    validator.warn("Found empty underhaul configuration!").solve(() -> {
                        configuration.underhaul = null;
                        configuration.underhaulVersion = null;
                    }, "Remove the underhaul configuration");
                }else{
                    validator.stage("Checking underhaul SFR configuration");
                    net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration sfr = underhaul.fissionSFR;
                    if(configuration.addon){
                        //<editor-fold defaultstate="collapsed" desc="Size">
                        if(sfr.minSize!=0)validator.error("Underhaul SFR minimum size must be zero! ("+sfr.minSize+")").solve(() -> {
                            sfr.minSize = 0;
                        }, "Set minimum size to 0");
                        if(sfr.maxSize!=0)validator.error("Underhaul SFR maximum size must be zero! ("+sfr.maxSize+")").solve(() -> {
                            sfr.maxSize = 0;
                        }, "Set maximum size to 0");
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Moderator power/heat">
                        if(sfr.moderatorExtraPower!=0)validator.error("Underhaul SFR moderator extra power must be zero! ("+sfr.moderatorExtraPower+")").solve(() -> {
                            sfr.moderatorExtraPower = 0;
                        }, "Set moderator extra power to 0");
                        if(sfr.moderatorExtraHeat!=0)validator.error("Underhaul SFR moderator extra heat must be zero! ("+sfr.moderatorExtraHeat+")").solve(() -> {
                            sfr.moderatorExtraHeat = 0;
                        }, "Set moderator extra heat to 0");
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Neutron Reach">
                        if(sfr.neutronReach!=0)validator.error("Underhaul SFR neutron reach must be zero! ("+sfr.neutronReach+")").solve(() -> {
                            sfr.neutronReach = 0;
                        }, "Set neutron reach to 0");
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Active Cooler Rate">
                        if(sfr.activeCoolerRate!=0)validator.error("Underhaul SFR active cooler rate must be zero! ("+sfr.activeCoolerRate+")").solve(() -> {
                            sfr.activeCoolerRate = 0;
                        }, "Set active cooler rate to 0");
                        //</editor-fold>
                    }else{
                        //<editor-fold defaultstate="collapsed" desc="Size">
                        if(sfr.minSize<1)validator.error("Underhaul SFR minimum size must be positive! ("+sfr.minSize+")").solve(() -> {
                            sfr.minSize = 1;
                        }, "Set minimum size to 1");
                        if(sfr.maxSize<1)validator.error("Underhaul SFR maximum size must be positive! ("+sfr.maxSize+")").solve(() -> {
                            sfr.maxSize = 1;
                        }, "Set maximum size to 1");
                        if(sfr.minSize>64)validator.warn("Underhaul SFR minimum size is too big! ("+sfr.minSize+")").hint("Sizes above 64x64x64 can cause very large file sizes and significant performance issues!").solve(() -> {
                            sfr.minSize = 64;
                        }, "Set minimum size to 64");
                        if(sfr.maxSize>64)validator.warn("Underhaul SFR maximum size is too big! ("+sfr.maxSize+")").hint("Sizes above 64x64x64 can cause very large file sizes and significant performance issues!").solve(() -> {
                            sfr.maxSize = 64;
                        }, "Set maximum size to 64");
                        if(sfr.maxSize<sfr.minSize)validator.error("Underhaul SFR maximum size is less than minimum size! ("+sfr.maxSize+"<"+sfr.minSize+")").solve(() -> {
                            sfr.minSize = sfr.maxSize;
                        }, "Set minimum size to equal maximum size");
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Moderator power/heat">
                        if(!Float.isFinite(sfr.moderatorExtraPower))validator.error("Underhaul SFR moderator extra power is not finite! ("+sfr.moderatorExtraPower+")").solve(() -> {
                            sfr.moderatorExtraPower = 0;
                        }, "Set moderator extra power to 0");
                        if(sfr.moderatorExtraPower<0)validator.error("Underhaul SFR moderator extra power is negative! ("+sfr.moderatorExtraPower+")").solve(() -> {
                            sfr.moderatorExtraPower = 0;
                        }, "Set moderator extra power to 0");
                        if(!Float.isFinite(sfr.moderatorExtraHeat))validator.error("Underhaul SFR moderator extra heat is not finite! ("+sfr.moderatorExtraHeat+")").solve(() -> {
                            sfr.moderatorExtraHeat = 0;
                        }, "Set moderator extra heat to 0");
                        if(sfr.moderatorExtraHeat<0)validator.error("Underhaul SFR moderator extra heat is negative! ("+sfr.moderatorExtraHeat+")").solve(() -> {
                            sfr.moderatorExtraHeat = 0;
                        }, "Set moderator extra heat to 0");
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Neutron Reach">
                        if(sfr.neutronReach<=0)validator.error("Underhaul SFR neutron reach must be positive! ("+sfr.neutronReach+")").solve(() -> {
                            sfr.neutronReach = 1;
                        }, "Set neutron reach to 1");
                        if(sfr.neutronReach>sfr.maxSize-2)validator.error("Underhaul SFR neutron reach is larger than is possible in the max size! ("+sfr.neutronReach+">"+(sfr.maxSize-2)+")").solve(() -> {
                            sfr.neutronReach = sfr.maxSize-2;
                        }, "Set neutron reach to maximum size - 2");
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Active Cooler Rate">
                        if(sfr.activeCoolerRate<=0)validator.error("Underhaul SFR active cooler rate must be positive! ("+sfr.activeCoolerRate+")").solve(() -> {
                            sfr.activeCoolerRate = 1;
                        }, "Set active cooler rate to 1");
                        //</editor-fold>
                    }
                    //<editor-fold defaultstate="collapsed" desc="Blocks">
                    validator.stage("Checking blocks...");
                    for(int i = 0; i<sfr.blocks.size(); i++){
                        validator.stage("Checking blocks... ("+(i+1)+"/"+sfr.blocks.size()+")");
                        net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block b = sfr.blocks.get(i);
                        Runnable blockConfig = () -> {
                            gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.underhaul.fissionsfr.MenuBlockConfiguration(gui, this, configuration, b));
                        };
                        //<editor-fold defaultstate="collapsed" desc="Name">
                        if(b.name==null){
                            validator.error("Underhaul SFR block name is null!").solve(blockConfig, "Go to Block configuration");
                        }else{
                            String[] split = b.name.split("\\:");
                            if(split.length<2||split.length>3){
                                validator.error("Underhaul SFR block name is invalid! ("+b.name+")").hint("Block name must be a namespaced ID! (namespace:name or namespace:name:metadata").solve(blockConfig, "Go to Block configuration");
                            }else{
                                for(char c : split[0].toCharArray()){
                                    if(namespaceChars.indexOf(c)==-1){
                                        validator.error("Underhaul SFR block namespace is invalid! ("+split[0]+")").hint("Namespaces can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame block.").solve(blockConfig, "Go to Block configuration");
                                    }
                                }
                                for(char c : split[1].toCharArray()){
                                    if(namespacePathChars.indexOf(c)==-1){
                                        validator.error("Underhaul SFR block name is invalid! ("+split[1]+")").hint("block names can only contain characters 0-9, a-z, _, -, ., and /! This should be the name of the ingame block.").solve(blockConfig, "Go to Block configuration");
                                    }
                                }
                                if(split.length==3){
                                    try{
                                        Integer.parseInt(split[2]);
                                    }catch(Exception ex){
                                        validator.error("Underhaul SFR block metadata is invalid! ("+split[2]+")").hint("Metadata must be an integer! If the ingame block does not have metadata, use only namespace:name.").solve(blockConfig, "Go to Block configuration");
                                    }
                                }
                            }
                            for(net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block b2 : sfr.blocks){
                                if(b==b2)continue;
                                if(b.name.equals(b2.name))validator.error("Underhaul SFR block name is not unique! ("+b.name+")").hint("This configuration contains multiple blocks with the same name.").solve(blockConfig, "Go to Block configuration");
                            }
                        }
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Display name">
                        if(b.displayName==null||b.displayName.isEmpty()){
                            validator.warn("Underhaul SFR block has no display name!").solve(blockConfig, "Go to Block configuration");
                        }
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Legacy names">
                        for(Configuration c : Configuration.configurations){
                            if(c.underhaul!=null&&c.underhaul.fissionSFR!=null){
                                for(net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block b2 : c.underhaul.fissionSFR.blocks){
                                    if(b.name.equals(b2.name)){
                                        if(!b.legacyNames.equals(b2.legacyNames))validator.warn("Underhaul SFR block legacy names do not match! ("+b.name+")").hint("Another block was found in Configuration "+c.name+" with different legacy names").solve(blockConfig, "Go to Block configuration");
                                    }else{
                                        for(String s : b.legacyNames)if(b2.legacyNames.contains(s))validator.warn("Underhaul SFR block legacy names are not unique! ("+s+")").hint("Another block was found in configuration "+c.name+" with the same legacy name. These blocks could be confused when loading old files").solve(blockConfig, "Go to Block configuration");
                                    }
                                }
                            }
                        }
                        for(AddonConfiguration c : Configuration.internalAddonCache.values()){
                            if(c.self.underhaul!=null&&c.self.underhaul.fissionSFR!=null){
                                for(net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block b2 : c.self.underhaul.fissionSFR.blocks){
                                    if(b.name.equals(b2.name)&&!b.legacyNames.equals(b2.legacyNames)){
                                        validator.warn("Underhaul SFR block legacy names do not match! ("+b.name+")").hint("Another block was found in Addon "+c.name+" with different legacy names.").solve(blockConfig, "Go to Block configuration");
                                    }
                                }
                            }
                        }
                        //</editor-fold>
                        if(b.texture==null)validator.warn("Underhaul SFR block "+b.name+" has no texture!").solve(blockConfig, "Go to Block configuration");
                        //<editor-fold defaultstate="collapsed" desc="Everything Else">
                        ArrayList<String> things = new ArrayList<>();
                        if(b.fuelCell)things.add("Fuel Cell");
                        if(b.moderator)things.add("Moderator");
                        if(b.casing)things.add("Casing");
                        if(b.controller)things.add("Controller");
                        if(b.cooling!=0)things.add("Cooler");
                        if(things.isEmpty())validator.error("Underhaul SFR block has no function: "+b.name).hint("Underhaul SFR blocks must be a cell, moderator, casing, controller, or cooler.").solve(blockConfig, "Go to Block configuration");
                        if(things.size()>1)validator.error("Underhaul SFR block has multiple functions: "+b.name+" "+things.toString()).hint("Underhaul SFR blocks may only have one function.").solve(blockConfig, "Go to Block configuration");
                        if(b.active!=null&&b.cooling==0)validator.error("Underhaul SFR block has active coolant, but is not a cooler: "+b.name).solve(() -> {
                            b.active = null;
                        }, "Set active coolant to null");
                        validate(b.rules, "Underhaul SFR block "+b.name, blockConfig);
                        //</editor-fold>
                    }
                    //</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Fuels">
                    validator.stage("Checking fuels...");
                    for(int i = 0; i<sfr.fuels.size(); i++){
                        validator.stage("Checking fuels... ("+(i+1)+"/"+sfr.fuels.size()+")");
                        net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel f = sfr.fuels.get(i);
                        Runnable fuelConfig = () -> {
                            gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.underhaul.fissionsfr.MenuFuelConfiguration(gui, this, configuration, f));
                        };
                        //<editor-fold defaultstate="collapsed" desc="Name">
                        if(f.name==null){
                            validator.error("Underhaul SFR fuel name is null!").solve(fuelConfig, "Go to Fuel configuration");
                        }else{
                            String[] split = f.name.split("\\:");
                            if(split.length<2||split.length>3){
                                validator.error("Underhaul SFR fuel name is invalid! ("+f.name+")").hint("Fuel name must be a namespaced ID! (namespace:name or namespace:name:metadata").solve(fuelConfig, "Go to Fuel configuration");
                            }else{
                                for(char c : split[0].toCharArray()){
                                    if(namespaceChars.indexOf(c)==-1){
                                        validator.error("Underhaul SFR fuel namespace is invalid! ("+split[0]+")").hint("Namespaces can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame fuel.").solve(fuelConfig, "Go to Fuel configuration");
                                    }
                                }
                                for(char c : split[1].toCharArray()){
                                    if(namespacePathChars.indexOf(c)==-1){
                                        validator.error("Underhaul SFR fuel name is invalid! ("+split[1]+")").hint("fuel names can only contain characters 0-9, a-z, _, -, ., and /! This should be the name of the ingame fuel.").solve(fuelConfig, "Go to Fuel configuration");
                                    }
                                }
                                if(split.length==3){
                                    try{
                                        Integer.parseInt(split[2]);
                                    }catch(Exception ex){
                                        validator.error("Underhaul SFR fuel metadata is invalid! ("+split[2]+")").hint("Metadata must be an integer! If the ingame fuel does not have metadata, use only namespace:name.").solve(fuelConfig, "Go to Fuel configuration");
                                    }
                                }
                            }
                            for(net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel f2 : sfr.fuels){
                                if(f==f2)continue;
                                if(f.name.equals(f2.name))validator.error("Underhaul SFR fuel name is not unique! ("+f.name+")").hint("This configuration contains multiple fuels with the same name.").solve(fuelConfig, "Go to Fuel configuration");
                            }
                        }
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Display name">
                        if(f.displayName==null||f.displayName.isEmpty()){
                            validator.warn("Underhaul SFR fuel has no display name!").solve(fuelConfig, "Go to Fuel configuration");
                        }
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Legacy names">
                        for(Configuration c : Configuration.configurations){
                            if(c.underhaul!=null&&c.underhaul.fissionSFR!=null){
                                for(net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel f2 : c.underhaul.fissionSFR.fuels){
                                    if(f.name.equals(f2.name)){
                                        if(!f.legacyNames.equals(f2.legacyNames))validator.warn("Underhaul SFR fuel legacy names do not match! ("+f.name+")").hint("Another fuel was found in Configuration "+c.name+" with different legacy names.").solve(fuelConfig, "Go to Fuel configuration");
                                    }else{
                                        for(String s : f.legacyNames)if(f2.legacyNames.contains(s))validator.warn("Underhaul SFR fuel legacy names are not unique! ("+s+")").hint("Another fuel was found in configuration "+c.name+" with the same legacy name. These fuels could be confused when loading old files").solve(fuelConfig, "Go to Fuel configuration");
                                    }
                                }
                            }
                        }
                        for(AddonConfiguration c : Configuration.internalAddonCache.values()){
                            if(c.self.underhaul!=null&&c.self.underhaul.fissionSFR!=null){
                                for(net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel f2 : c.self.underhaul.fissionSFR.fuels){
                                    if(f.name.equals(f2.name)&&!f.legacyNames.equals(f2.legacyNames)){
                                        validator.warn("Underhaul SFR fuel legacy names do not match! ("+f.name+")").hint("Another fuel was found in Addon "+c.name+" with different legacy names.").solve(fuelConfig, "Go to Fuel configuration");
                                    }
                                }
                            }
                        }
                        //</editor-fold>
                        if(f.texture==null)validator.warn("Underhaul SFR fuel "+f.name+" has no texture!").solve(fuelConfig, "Go to Fuel configuration");
                        //<editor-fold defaultstate="collapsed" desc="Fuel settings">
                        if(!Float.isFinite(f.power))validator.error("Underhaul SFR fuel power is not finite! ("+f.power+")").solve(() -> {
                            f.power = 0;
                        }, "Set power to 0");
                        if(f.power<0)validator.error("Underhaul SFR fuel power is negative! ("+f.power+")").solve(() -> {
                            f.power = 0;
                        }, "Set fuel power to 0");
                        if(!Float.isFinite(f.heat))validator.error("Underhaul SFR fuel heat is not finite! ("+f.heat+")").solve(() -> {
                            f.heat = 0;
                        }, "Set heat to 0");
                        if(f.heat<0)validator.error("Underhaul SFR fuel heat is negative! ("+f.heat+")").solve(() -> {
                            f.heat = 0;
                        }, "Set fuel heat to 0");
                        if(f.time<=0)validator.error("Underhaul SFR fuel time must be positive! ("+f.time+")").solve(() -> {
                            f.time = 1;
                        }, "Set fuel time to 1");
                        //</editor-fold>
                    }
                    //</editor-fold>
                }
            }
            if(configuration.overhaul!=null){
                validator.stage("Checking overhaul configuration");
                OverhaulConfiguration overhaul = configuration.overhaul;
                if(overhaul.fissionSFR==null&&overhaul.fissionMSR==null&&overhaul.turbine==null&&overhaul.fusion==null){
                    validator.warn("Found empty overhaul configuration!").solve(() -> {
                        configuration.overhaul = null;
                        configuration.overhaulVersion = null;
                    }, "Remove the overhaul configuration");
                }else{
                    if(overhaul.fissionSFR!=null){
                        validator.stage("Checking overhaul SFR configuration");
                        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration sfr = overhaul.fissionSFR;
                        if(configuration.addon){
                            //<editor-fold defaultstate="collapsed" desc="Size">
                            if(sfr.minSize!=0)validator.error("Overhaul SFR minimum size must be zero! ("+sfr.minSize+")").solve(() -> {
                                sfr.minSize = 0;
                            }, "Set minimum size to 0");
                            if(sfr.maxSize!=0)validator.error("Overhaul SFR maximum size must be zero! ("+sfr.maxSize+")").solve(() -> {
                                sfr.maxSize = 0;
                            }, "Set maximum size to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Sparsity Penalty">
                            if(sfr.sparsityPenaltyMult!=0)validator.error("Overhaul SFR sparsity penalty mult must be zero! ("+sfr.sparsityPenaltyMult+")").solve(() -> {
                                sfr.sparsityPenaltyMult = 0;
                            }, "Set sparsity penalty mult to 0");
                            if(sfr.sparsityPenaltyThreshold!=0)validator.error("Overhaul SFR sparsity penalty threshold must be zero! ("+sfr.sparsityPenaltyThreshold+")").solve(() -> {
                                sfr.sparsityPenaltyThreshold = 0;
                            }, "Set sparsity penalty threshold to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Neutron Reach">
                            if(sfr.neutronReach!=0)validator.error("Overhaul SFR neutron reach must be zero! ("+sfr.neutronReach+")").solve(() -> {
                                sfr.neutronReach = 0;
                            }, "Set neutron reach to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Active Cooler Rate">
                            if(sfr.coolingEfficiencyLeniency!=0)validator.error("Overhaul SFR cooling efficiency leniency must be zero! ("+sfr.coolingEfficiencyLeniency+")").solve(() -> {
                                sfr.coolingEfficiencyLeniency = 0;
                            }, "Set cooling efficiency leniency to 0");
                            //</editor-fold>
                        }else{
                            //<editor-fold defaultstate="collapsed" desc="Size">
                            if(sfr.minSize<1)validator.error("Overhaul SFR minimum size must be positive! ("+sfr.minSize+")").solve(() -> {
                                sfr.minSize = 1;
                            }, "Set minimum size to 1");
                            if(sfr.maxSize<1)validator.error("Overhaul SFR maximum size must be positive! ("+sfr.maxSize+")").solve(() -> {
                                sfr.maxSize = 1;
                            }, "Set maximum size to 1");
                            if(sfr.minSize>64)validator.warn("Overhaul SFR minimum size is too big! ("+sfr.minSize+")").hint("Sizes above 64x64x64 can cause very large file sizes and significant performance issues!").solve(() -> {
                                sfr.minSize = 64;
                            }, "Set minimum size to 64");
                            if(sfr.maxSize>64)validator.warn("Overhaul SFR maximum size is too big! ("+sfr.maxSize+")").hint("Sizes above 64x64x64 can cause very large file sizes and significant performance issues!").solve(() -> {
                                sfr.maxSize = 64;
                            }, "Set maximum size to 64");
                            if(sfr.maxSize<sfr.minSize)validator.error("Overhaul SFR maximum size is less than minimum size! ("+sfr.maxSize+"<"+sfr.minSize+")").solve(() -> {
                                sfr.minSize = sfr.maxSize;
                            }, "Set minimum size to equal maximum size");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Sparsity Penalty">
                            if(!Float.isFinite(sfr.sparsityPenaltyMult))validator.error("Overhaul SFR sparsity penalty mult is not finite! ("+sfr.sparsityPenaltyMult+")").solve(() -> {
                                sfr.sparsityPenaltyMult = 0;
                            }, "Set sparsity penalty mult to 0");
                            if(sfr.sparsityPenaltyMult<0)validator.error("Overhaul SFR sparsity penalty mult is negative! ("+sfr.sparsityPenaltyMult+")").solve(() -> {
                                sfr.sparsityPenaltyMult = 0;
                            }, "Set sparsity penalty mult to 0");
                            if(!Float.isFinite(sfr.sparsityPenaltyThreshold))validator.error("Overhaul SFR sparsity penalty threshold is not finite! ("+sfr.sparsityPenaltyThreshold+")").solve(() -> {
                                sfr.sparsityPenaltyThreshold = 0;
                            }, "Set sparsity penalty threshold to 0");
                            if(sfr.sparsityPenaltyThreshold<0)validator.error("Overhaul SFR sparsity penalty threshold is negative! ("+sfr.sparsityPenaltyThreshold+")").solve(() -> {
                                sfr.sparsityPenaltyThreshold = 0;
                            }, "Set sparsity penalty threshold to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Neutron Reach">
                            if(sfr.neutronReach<=0)validator.error("Overhaul SFR neutron reach must be positive! ("+sfr.neutronReach+")").solve(() -> {
                                sfr.neutronReach = 1;
                            }, "Set neutron reach to 1");
                            if(sfr.neutronReach>sfr.maxSize-2)validator.error("Overhaul SFR neutron reach is larger than is possible in the max size! ("+sfr.neutronReach+">"+(sfr.maxSize-2)+")").solve(() -> {
                                sfr.neutronReach = sfr.maxSize-2;
                            }, "Set neutron reach to maximum size - 2");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Active Cooler Rate">
                            if(sfr.coolingEfficiencyLeniency<0)validator.error("Overhaul SFR cooling efficiency leniency must not be negative! ("+sfr.coolingEfficiencyLeniency+")").solve(() -> {
                                sfr.coolingEfficiencyLeniency = 0;
                            }, "Set cooling efficiency leniency to 0");
                            //</editor-fold>
                        }
                        //<editor-fold defaultstate="collapsed" desc="Blocks">
                        validator.stage("Checking blocks...");
                        for(int i = 0; i<sfr.blocks.size(); i++){
                            validator.stage("Checking blocks... ("+(i+1)+"/"+sfr.blocks.size()+")");
                            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b = sfr.blocks.get(i);
                            Runnable blockConfig = () -> {
                                gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionsfr.MenuBlockConfiguration(gui, this, configuration, b));
                            };
                            //<editor-fold defaultstate="collapsed" desc="Name">
                            if(b.name==null){
                                validator.error("Overhaul SFR block name is null!").solve(blockConfig, "Go to Block configuration");
                            }else{
                                String[] split = b.name.split("\\:");
                                if(split.length<2||split.length>3){
                                    validator.error("Overhaul SFR block "+b.name+" name is invalid! ("+b.name+")").hint("Block name must be a namespaced ID! (namespace:name or namespace:name:metadata").solve(blockConfig, "Go to Block configuration");
                                }else{
                                    for(char c : split[0].toCharArray()){
                                        if(namespaceChars.indexOf(c)==-1){
                                            validator.error("Overhaul SFR block "+b.name+" namespace is invalid! ("+split[0]+")").hint("Namespaces can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame block.").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                    for(char c : split[1].toCharArray()){
                                        if(namespacePathChars.indexOf(c)==-1){
                                            validator.error("Overhaul SFR block "+b.name+" name is invalid! ("+split[1]+")").hint("block names can only contain characters 0-9, a-z, _, -, ., and /! This should be the name of the ingame block.").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                    if(split.length==3){
                                        try{
                                            Integer.parseInt(split[2]);
                                        }catch(Exception ex){
                                            validator.error("Overhaul SFR block "+b.name+" metadata is invalid! ("+split[2]+")").hint("Metadata must be an integer! If the ingame block does not have metadata, use only namespace:name.").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                }
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b2 : sfr.blocks){
                                    if(b==b2)continue;
                                    if(b.name.equals(b2.name))validator.error("Overhaul SFR block "+b.name+" name is not unique!").hint("This configuration contains multiple blocks with the same name.").solve(blockConfig, "Go to Block configuration");
                                }
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Display name">
                            if(b.displayName==null||b.displayName.isEmpty()){
                                validator.warn("Overhaul SFR block "+b.name+" has no display name!").solve(blockConfig, "Go to Block configuration");
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Legacy names">
                            for(Configuration c : Configuration.configurations){
                                if(c.overhaul!=null&&c.overhaul.fissionSFR!=null){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b2 : c.overhaul.fissionSFR.blocks){
                                        if(b.name.equals(b2.name)){
                                            if(!b.legacyNames.equals(b2.legacyNames))validator.warn("Overhaul SFR block "+b.name+" legacy names do not match!").hint("Another block was found in Configuration "+c.name+" with different legacy names.").solve(blockConfig, "Go to Block configuration");
                                        }else{
                                            for(String s : b.legacyNames)if(b2.legacyNames.contains(s))validator.warn("Overhaul SFR block "+b.name+" legacy names are not unique! ("+s+")").hint("Another block was found in configuration "+c.name+" with the same legacy name. These blocks could be confused when loading old files").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                }
                            }
                            for(AddonConfiguration c : Configuration.internalAddonCache.values()){
                                if(c.self.overhaul!=null&&c.self.overhaul.fissionSFR!=null){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b2 : c.self.overhaul.fissionSFR.blocks){
                                        if(b.name.equals(b2.name)&&!b.legacyNames.equals(b2.legacyNames)){
                                            validator.warn("Overhaul SFR block "+b.name+" legacy names do not match!").hint("Another block was found in Addon "+c.name+" with different legacy names.").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                }
                            }
                            //</editor-fold>
                            if(b.texture==null)validator.warn("Overhaul SFR block "+b.name+" has no texture!").solve(blockConfig, "Go to Block configuration");
                            //<editor-fold defaultstate="collapsed" desc="Settings">
                            if(b.createCluster&&!b.cluster)validator.error("Overhaul SFR block "+b.name+" creates clusters, but cannot be part of a cluster!").solve(() -> {
                                b.cluster = true;
                            }, "Set cluster to true");
                            if((b.fuelCell||b.irradiator||b.shield||b.heatsink)&&!b.cluster)validator.error("Overhaul SFR block "+b.name+" handles heat, but cannot be part of a cluster!").hint("This block is a fuel cell, irradiator, shield, or heatsink").solve(() -> {
                                b.cluster = true;
                            }, "Set cluster to true");
                            if(b.cluster&&b.conductor)validator.error("Overhaul SFR block "+b.name+" can be part of a cluster, but is also a conductor!").hint("The \"Conductor\" setting lets this block connect clusters to the casing, but won't connect clusters together.").solve(() -> {
                                b.cluster = false;
                            }, "Set cluster to false");
                            if(b.casing&&(b.cluster||b.createCluster||b.conductor||b.functional||b.blocksLOS))validator.error("Overhaul SFR block "+b.name+" is a casing block, but has internal reactor functionality!").hint("This block can cluster, creates clusters, is a conductor, is functional, or blocks line of sight").solve(() -> {
                                b.casing = false;
                            }, "Set casing to false");
                            if(b.casingEdge&&(b.cluster||b.createCluster||b.conductor||b.functional||b.blocksLOS))validator.error("Overhaul SFR block "+b.name+" is a casing edge block, but has internal reactor functionality!").hint("This block can cluster, creates clusters, is a conductor, is functional, or blocks line of sight").solve(() -> {
                                b.casingEdge = false;
                            }, "Set casing edge to false");
                            if(b.coolantVent){
                                if(!b.casing)validator.error("Overhaul SFR block "+b.name+" is a coolant vent, but not part of the casing!").solve(() -> {
                                    b.casing = true;
                                }, "Set casing to true");
                                if(b.coolantVentOutputDisplayName==null||b.coolantVentOutputDisplayName.isEmpty()){
                                    validator.warn("Overhaul SFR coolant vent "+b.name+" has no output display name!").solve(blockConfig, "Go to Block configuration");
                                }
                                if(b.coolantVentOutputTexture==null)validator.warn("Overhaul SFR coolant vent "+b.name+" has no output texture!").solve(blockConfig, "Go to Block configuration");
                            }else{
                                if(b.coolantVentOutputDisplayName!=null)validator.warn("Overhaul SFR block "+b.name+" has coolant vent output display name, but is not a coolant vent!").solve(() -> {
                                    b.coolantVentOutputDisplayName = null;
                                }, "Set display name to null");
                                if(b.coolantVentOutputTexture!=null)validator.warn("Overhaul SFR block "+b.name+" has coolant vent output texture, but is not a coolant vent!").solve(() -> {
                                    b.coolantVentOutputTexture = null;
                                    b.coolantVentOutputDisplayTexture = null;
                                }, "Set texture to null");
                            }
                            if(b.controller&&!b.casing)validator.error("Overhaul SFR controller "+b.name+" is not part of the casing!").solve(() -> {
                                b.casing = true;
                            }, "Set casing to true");
                            if(b.controller&&b.casingEdge)validator.error("Overhaul SFR controller "+b.name+" is part of the casing edge!").solve(() -> {
                                b.casingEdge = false;
                            }, "Set casing edge to false");
                            if(!b.fuelCell||!b.fuelCellHasBaseStats){
                                if(b.fuelCellEfficiency!=0)validator.error("Overhaul SFR block "+b.name+" has fuel cell efficiency, but is not a fuel cell or has no base stats!").solve(() -> {
                                    b.fuelCellEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.fuelCellHeat!=0)validator.error("Overhaul SFR block "+b.name+" has fuel cell heat, but is not a fuel cell or has no base stats!").solve(() -> {
                                    b.fuelCellHeat = 0;
                                }, "Set heat to 0");
                                if(b.fuelCellCriticality!=0)validator.error("Overhaul SFR block "+b.name+" has fuel cell criticality, but is not a fuel cell or has no base stats!").solve(() -> {
                                    b.fuelCellCriticality = 0;
                                }, "Set criticality to 0");
                                if(b.fuelCellSelfPriming)validator.error("Overhaul SFR block "+b.name+" is fuel cell self priming, but is not a fuel cell or has no base stats!").solve(() -> {
                                    b.fuelCellSelfPriming = false;
                                }, "Set self priming to false");
                            }else{
                                if(b.fuelCellEfficiency<0)validator.error("Overhaul SFR block "+b.name+" fuel cell efficiency must not be negative!").solve(() -> {
                                    b.fuelCellEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.fuelCellHeat<0)validator.error("Overhaul SFR block "+b.name+" fuel cell heat must not be negative!").solve(() -> {
                                    b.fuelCellHeat = 0;
                                }, "Set heat to 0");
                                if(b.fuelCellCriticality<0)validator.error("Overhaul SFR block "+b.name+" fuel cell criticality must not be negative!").solve(() -> {
                                    b.fuelCellCriticality = 0;
                                }, "Set criticality to 0");
                            }
                            if(!Float.isFinite(b.fuelCellEfficiency))validator.error("Overhaul SFR block "+b.name+" fuel cell efficiency is not finite!").solve(() -> {
                                b.fuelCellEfficiency = 0;
                            }, "Set efficiency to 0");
                            if(!b.irradiator||!b.irradiatorHasBaseStats){
                                if(b.irradiatorEfficiency!=0)validator.error("Overhaul SFR block "+b.name+" has irradiator efficiency, but is not a irradiator or has no base stats!").solve(() -> {
                                    b.irradiatorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.irradiatorHeat!=0)validator.error("Overhaul SFR block "+b.name+" has irradiator heat, but is not a irradiator or has no base stats!").solve(() -> {
                                    b.irradiatorHeat = 0;
                                }, "Set heat to 0");
                            }else{
                                if(b.irradiatorEfficiency<0)validator.error("Overhaul SFR block "+b.name+" irradiator efficiency must not be negative!").solve(() -> {
                                    b.irradiatorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.irradiatorHeat<0)validator.error("Overhaul SFR block "+b.name+" irradiator heat must not be negative!").solve(() -> {
                                    b.irradiatorHeat = 0;
                                }, "Set heat to 0");
                            }
                            if(!Float.isFinite(b.irradiatorEfficiency))validator.error("Overhaul SFR block "+b.name+" irradiator efficiency is not finite!").solve(() -> {
                                b.irradiatorEfficiency = 0;
                            }, "Set efficiency to 0");
                            if(!Float.isFinite(b.irradiatorHeat))validator.error("Overhaul SFR block "+b.name+" irradiator heat is not finite!").solve(() -> {
                                b.irradiatorHeat = 0;
                            }, "Set heat to 0");
                            if(!b.reflector||!b.reflectorHasBaseStats){
                                if(b.reflectorEfficiency!=0)validator.error("Overhaul SFR block "+b.name+" has reflector efficiency, but is not a reflector or has no base stats!").solve(() -> {
                                    b.reflectorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.reflectorReflectivity!=0)validator.error("Overhaul SFR block "+b.name+" has reflector reflectivity, but is not a reflector or has no base stats!").solve(() -> {
                                    b.reflectorReflectivity = 0;
                                }, "Set reflectivity to 0");
                            }else{
                                if(b.reflectorEfficiency<0)validator.error("Overhaul SFR block "+b.name+" reflector efficiency must not be negative!").solve(() -> {
                                    b.reflectorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.reflectorReflectivity<0)validator.error("Overhaul SFR block "+b.name+" reflector reflectivity must not be negative!").solve(() -> {
                                    b.reflectorReflectivity = 0;
                                }, "Set reflectivity to 0");
                            }
                            if(!Float.isFinite(b.reflectorEfficiency))validator.error("Overhaul SFR block "+b.name+" reflector efficiency is not finite!").solve(() -> {
                                b.reflectorEfficiency = 0;
                            }, "Set efficiency to 0");
                            if(!Float.isFinite(b.reflectorReflectivity))validator.error("Overhaul SFR block "+b.name+" reflector reflectivity is not finite!").solve(() -> {
                                b.reflectorReflectivity = 0;
                            }, "Set reflectivity to 0");
                            if(!b.moderator||!b.moderatorHasBaseStats){
                                if(b.moderatorEfficiency!=0)validator.error("Overhaul SFR block "+b.name+" has moderator efficiency, but is not a moderator or has no base stats!").solve(() -> {
                                    b.moderatorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.moderatorFlux!=0)validator.error("Overhaul SFR block "+b.name+" has moderator flux, but is not a moderator or has no base stats!").solve(() -> {
                                    b.moderatorFlux = 0;
                                }, "Set flux to 0");
                                if(b.moderatorActive)validator.error("Overhaul SFR block "+b.name+" is moderator active, but is not a moderator or has no base stats!").solve(() -> {
                                    b.moderatorActive = false;
                                }, "Set active to false");
                            }else{
                                if(b.moderatorEfficiency<0)validator.error("Overhaul SFR block "+b.name+" moderator efficiency must not be negative!").solve(() -> {
                                    b.moderatorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.moderatorFlux<0)validator.error("Overhaul SFR block "+b.name+" moderator flux must not be negative!").solve(() -> {
                                    b.moderatorFlux = 0;
                                }, "Set flux to 0");
                            }
                            if(!Float.isFinite(b.moderatorEfficiency))validator.error("Overhaul SFR block "+b.name+" moderator efficiency is not finite!").solve(() -> {
                                b.moderatorEfficiency = 0;
                            }, "Set efficiency to 0");
                            if(!b.shield||!b.shieldHasBaseStats){
                                if(b.shieldEfficiency!=0)validator.error("Overhaul SFR block "+b.name+" has shield efficiency, but is not a shield or has no base stats!").solve(() -> {
                                    b.shieldEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.shieldHeat!=0)validator.error("Overhaul SFR block "+b.name+" has shield heat, but is not a shield or has no base stats!").solve(() -> {
                                    b.shieldHeat = 0;
                                }, "Set heat to 0");
                            }else{
                                if(b.shieldEfficiency<0)validator.error("Overhaul SFR block "+b.name+" shield efficiency must not be negative!").solve(() -> {
                                    b.shieldEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.shieldHeat<0)validator.error("Overhaul SFR block "+b.name+" shield heat must not be negative!").solve(() -> {
                                    b.shieldHeat = 0;
                                }, "Set heat to 0");
                            }
                            if(!Float.isFinite(b.shieldEfficiency))validator.error("Overhaul SFR block "+b.name+" shield efficiency is not finite!").solve(() -> {
                                b.shieldEfficiency = 0;
                            }, "Set efficiency to 0");
                            if(b.shield){
                                if(b.shieldClosedTexture==null)validator.warn("Overhaul SFR shield "+b.name+" has no closed texture!").solve(blockConfig, "Go to Block configuration");
                            }else{
                                if(b.shieldClosedTexture!=null)validator.warn("Overhaul SFR block "+b.name+" has shield closed texture, but is not a shield!").solve(() -> {
                                    b.shieldClosedTexture = null;
                                    b.shieldClosedDisplayTexture = null;
                                }, "Set texture to null");
                            }
                            if(!b.heatsink||!b.heatsinkHasBaseStats){
                                if(b.heatsinkCooling!=0)validator.error("Overhaul SFR block "+b.name+" has heat sink cooling, but is not a heatsink or has no base stats!").solve(() -> {
                                    b.heatsinkCooling = 0;
                                }, "Set cooling to 0");
                            }
                            if(!b.source){
                                if(b.sourceEfficiency!=0)validator.error("Overhaul SFR block "+b.name+" has source efficiency, but is not a neutron source!").solve(() -> {
                                    b.sourceEfficiency = 0;
                                }, "Set efficiency to 0");
                            }
                            if(!Float.isFinite(b.sourceEfficiency))validator.error("Overhaul SFR block "+b.name+" source efficiency is not finite!").solve(() -> {
                                b.sourceEfficiency = 0;
                            }, "Set efficiency to 0");
                            if(b.parent!=null){
                                if(b.portOutputTexture==null)validator.warn("Overhaul SFR port "+b.name+" has no output texture!").solve(blockConfig, "Go to Block configuration");
                                if(b.portOutputDisplayName==null||b.portOutputDisplayName.isEmpty()){
                                    validator.warn("Overhaul SFR port "+b.name+" has no output display name!").solve(blockConfig, "Go to Block configuration");
                                }
                            }else{
                                if(b.portOutputDisplayName!=null)validator.warn("Overhaul SFR block "+b.name+" has port output display name, but is not a port!").solve(() -> {
                                    b.portOutputDisplayName = null;
                                }, "Set display name to null");
                                if(b.portOutputTexture!=null)validator.warn("Overhaul SFR block "+b.name+" has port output texture, but is not a port!").solve(() -> {
                                    b.portOutputTexture = null;
                                    b.portOutputDisplayTexture = null;
                                }, "Set texture to null");
                            }
                            //</editor-fold>
                            validate(b.rules, "Overhaul SFR block "+b.name, blockConfig);
                            //<editor-fold defaultstate="collapsed" desc="Block Recipes">
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe br : b.recipes){
                                Runnable blockRecipeConfig = () -> {
                                    gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionsfr.MenuBlockRecipeConfiguration(gui, this, configuration, b, br));
                                };
                                //<editor-fold defaultstate="collapsed" desc="Input Name">
                                if(br.inputName==null){
                                    validator.error("Overhaul SFR block recipe input name is null!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                }else{
                                    String[] split = br.inputName.split("\\:");
                                    if(split.length<2||split.length>3){
                                        validator.error("Overhaul SFR block recipe input name is invalid! ("+br.inputName+")").hint("Item input name must be a namespaced ID! (namespace:inputname or namespace:inputname:metadata").solve(blockConfig, "Go to Block Recipe configuration");
                                    }else{
                                        for(char c : split[0].toCharArray()){
                                            if(namespaceChars.indexOf(c)==-1){
                                                validator.error("Overhaul SFR block recipe namespace is invalid! ("+split[0]+")").hint("Namespaces can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                            }
                                        }
                                        for(char c : split[1].toCharArray()){
                                            if(namespacePathChars.indexOf(c)==-1){
                                                validator.error("Overhaul SFR block recipe input name is invalid! ("+split[1]+")").hint("block recipe input names can only contain characters 0-9, a-z, _, -, ., and /! This should be the input name of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                            }
                                        }
                                        if(split.length==3){
                                            try{
                                                Integer.parseInt(split[2]);
                                            }catch(Exception ex){
                                                validator.error("Overhaul SFR block recipe metadata is invalid! ("+split[2]+")").hint("Metadata must be an integer! If the ingame block recipe does not have metadata, use only namespace:input name.").solve(blockConfig, "Go to Block Recipe configuration");
                                            }
                                        }
                                    }
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe c2 : b.recipes){
                                        if(br==c2)continue;
                                        if(br.inputName.equals(c2.inputName))validator.error("Overhaul SFR block recipe input name is not unique! ("+br.inputName+")").hint("This block contains multiple block recipes with the same name.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    }
                                }
                                //</editor-fold>
                                //<editor-fold defaultstate="collapsed" desc="Input Display name">
                                if(br.inputDisplayName==null||br.inputDisplayName.isEmpty()){
                                    validator.warn("Overhaul SFR block recipe has no display name!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                }
                                //</editor-fold>
                                //<editor-fold defaultstate="collapsed" desc="Output Name">
                                if(br.outputName==null){
                                    validator.error("Overhaul SFR block recipe output name is null!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                }else{
                                    String[] split = br.outputName.split("\\:");
                                    if(split.length<2||split.length>3){
                                        validator.error("Overhaul SFR block recipe output name is invalid! ("+br.outputName+")").hint("Item output name must be a namespaced ID! (namespace:outputname or namespace:outputname:metadata").solve(blockConfig, "Go to Block Recipe configuration");
                                    }else{
                                        for(char c : split[0].toCharArray()){
                                            if(namespaceChars.indexOf(c)==-1){
                                                validator.error("Overhaul SFR block recipe namespace is invalid! ("+split[0]+")").hint("Namespaces can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                            }
                                        }
                                        for(char c : split[1].toCharArray()){
                                            if(namespacePathChars.indexOf(c)==-1){
                                                validator.error("Overhaul SFR block recipe output name is invalid! ("+split[1]+")").hint("block recipe output names can only contain characters 0-9, a-z, _, -, ., and /! This should be the output name of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                            }
                                        }
                                        if(split.length==3){
                                            try{
                                                Integer.parseInt(split[2]);
                                            }catch(Exception ex){
                                                validator.error("Overhaul SFR block recipe metadata is invalid! ("+split[2]+")").hint("Metadata must be an integer! If the ingame block recipe does not have metadata, use only namespace:output name.").solve(blockConfig, "Go to Block Recipe configuration");
                                            }
                                        }
                                    }
                                }
                                //</editor-fold>
                                //<editor-fold defaultstate="collapsed" desc="Output Display name">
                                if(br.outputDisplayName==null||br.outputDisplayName.isEmpty()){
                                    validator.warn("Overhaul SFR block recipe has no display name!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                }
                                //</editor-fold>
                                //<editor-fold defaultstate="collapsed" desc="Legacy names">
                                for(Configuration cf : Configuration.configurations){
                                    if(cf.overhaul!=null&&cf.overhaul.fissionSFR!=null){
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b2 : cf.overhaul.fissionSFR.blocks){
                                            if(!b.name.equals(b2.name))continue;
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe r2 : b2.recipes){
                                                if(br.inputName.equals(r2.inputName)){
                                                    if(!br.inputLegacyNames.equals(r2.inputLegacyNames))validator.warn("Overhaul SFR block recipe legacy names do not match! ("+br.inputName+")").hint("Another block recipe was found in block "+b2.name+" of Configuration "+cf.name+" with different legacy names.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                }else{
                                                    for(String s : br.inputLegacyNames)if(r2.inputLegacyNames.contains(s))validator.warn("Overhaul SFR block recipe legacy names are not unique! ("+s+")").hint("Another block recipe was found in block "+b2.name+" of configuration "+cf.name+" with the same legacy name. These blocks could be confused when loading old files").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                        }
                                    }
                                }
                                for(AddonConfiguration cf : Configuration.internalAddonCache.values()){
                                    if(cf.self.overhaul!=null&&cf.self.overhaul.fissionSFR!=null){
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b2 : cf.self.overhaul.fissionSFR.blocks){
                                            if(!b.name.equals(b2.name))continue;
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe r2 : b2.recipes){
                                                if(br.inputName.equals(r2.inputName)&&!br.inputLegacyNames.equals(r2.inputLegacyNames)){
                                                    validator.warn("Overhaul SFR block recipe legacy names do not match! ("+br.inputName+")").hint("Another block recipe was found in block "+b2.name+" of Configuration "+cf.name+" with different legacy names.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                        }
                                    }
                                }
                                //</editor-fold>
                                if(br.inputTexture==null)validator.warn("Overhaul SFR block recipe "+br.inputName+" of block "+b.name+" has no input texture!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                if(br.outputTexture==null)validator.warn("Overhaul SFR block recipe "+br.outputName+" of block "+b.name+" has no output texture!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                if(!b.fuelCell){
                                    if(br.fuelCellEfficiency!=0)validator.error("Overhaul SFR block recipe has fuel cell efficiency, but is not for a fuel cell!").solve(() -> {
                                        br.fuelCellEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(br.fuelCellHeat!=0)validator.error("Overhaul SFR block recipe has fuel cell heat, but is not for a fuel cell!").solve(() -> {
                                        br.fuelCellHeat = 0;
                                    }, "Set heat to 0");
                                    if(br.fuelCellCriticality!=0)validator.error("Overhaul SFR block recipe has fuel cell criticality, but is not for a fuel cell!").solve(() -> {
                                        br.fuelCellCriticality = 0;
                                    }, "Set criticality to 0");
                                    if(br.fuelCellSelfPriming)validator.error("Overhaul SFR block recipe is fuel cell self priming, but is not for a fuel cell!").solve(() -> {
                                        br.fuelCellSelfPriming = false;
                                    }, "Set self priming to false");
                                }else{
                                    if(br.fuelCellEfficiency<0)validator.error("Overhaul SFR block recipe fuel cell efficiency must not be negative!").solve(() -> {
                                        br.fuelCellEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(br.fuelCellHeat<0)validator.error("Overhaul SFR block recipe fuel cell heat must not be negative!").solve(() -> {
                                        br.fuelCellHeat = 0;
                                    }, "Set heat to 0");
                                    if(br.fuelCellCriticality<0)validator.error("Overhaul SFR block recipe fuel cell criticality must not be negative!").solve(() -> {
                                        br.fuelCellCriticality = 0;
                                    }, "Set criticality to 0");
                                }
                                if(!Float.isFinite(br.fuelCellEfficiency))validator.error("Overhaul SFR block recipe fuel cell efficiency is not finite!").solve(() -> {
                                    br.fuelCellEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(!b.irradiator){
                                    if(br.irradiatorEfficiency!=0)validator.error("Overhaul SFR block recipe has irradiator efficiency, but is not for an irradiator!").solve(() -> {
                                        br.irradiatorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(br.irradiatorHeat!=0)validator.error("Overhaul SFR block recipe has irradiator heat, but is not for an irradiator!").solve(() -> {
                                        br.irradiatorHeat = 0;
                                    }, "Set heat to 0");
                                }else{
                                    if(br.irradiatorEfficiency<0)validator.error("Overhaul SFR block recipe irradiator efficiency must not be negative!").solve(() -> {
                                        br.irradiatorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(br.irradiatorHeat<0)validator.error("Overhaul SFR block recipe irradiator heat must not be negative!").solve(() -> {
                                        br.irradiatorHeat = 0;
                                    }, "Set heat to 0");
                                }
                                if(!Float.isFinite(br.irradiatorEfficiency))validator.error("Overhaul SFR block recipe irradiator efficiency is not finite!").solve(() -> {
                                    br.irradiatorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(!Float.isFinite(br.irradiatorHeat))validator.error("Overhaul SFR block recipe irradiator heat is not finite!").solve(() -> {
                                    br.irradiatorHeat = 0;
                                }, "Set heat to 0");
                                if(!b.reflector){
                                    if(br.reflectorEfficiency!=0)validator.error("Overhaul SFR block recipe has reflector efficiency, but is not for a reflector!").solve(() -> {
                                        br.reflectorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(br.reflectorReflectivity!=0)validator.error("Overhaul SFR block recipe has reflector reflectivity, but is not for a reflector!").solve(() -> {
                                        br.reflectorReflectivity = 0;
                                    }, "Set reflectivity to 0");
                                }else{
                                    if(br.reflectorEfficiency<0)validator.error("Overhaul SFR block recipe reflector efficiency must not be negative!").solve(() -> {
                                        br.reflectorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(br.reflectorReflectivity<0)validator.error("Overhaul SFR block recipe reflector reflectivity must not be negative!").solve(() -> {
                                        br.reflectorReflectivity = 0;
                                    }, "Set reflectivity to 0");
                                }
                                if(!Float.isFinite(br.reflectorEfficiency))validator.error("Overhaul SFR block recipe reflector efficiency is not finite!").solve(() -> {
                                    br.reflectorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(!Float.isFinite(br.reflectorReflectivity))validator.error("Overhaul SFR block recipe reflector reflectivity is not finite!").solve(() -> {
                                    br.reflectorReflectivity = 0;
                                }, "Set reflectivity to 0");
                                if(!b.moderator){
                                    if(br.moderatorEfficiency!=0)validator.error("Overhaul SFR block recipe has moderator efficiency, but is not for a moderator!").solve(() -> {
                                        br.moderatorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(br.moderatorFlux!=0)validator.error("Overhaul SFR block recipe has moderator flux, but is not for a moderator!").solve(() -> {
                                        br.moderatorFlux = 0;
                                    }, "Set flux to 0");
                                    if(br.moderatorActive)validator.error("Overhaul SFR block recipe is moderator active, but is not for a moderator!").solve(() -> {
                                        br.moderatorActive = false;
                                    }, "Set active to false");
                                }else{
                                    if(br.moderatorEfficiency<0)validator.error("Overhaul SFR block recipe moderator efficiency must not be negative!").solve(() -> {
                                        br.moderatorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(br.moderatorFlux<0)validator.error("Overhaul SFR block recipe moderator flux must not be negative!").solve(() -> {
                                        br.moderatorFlux = 0;
                                    }, "Set flux to 0");
                                }
                                if(!Float.isFinite(br.moderatorEfficiency))validator.error("Overhaul SFR block recipe moderator efficiency is not finite!").solve(() -> {
                                    br.moderatorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(!b.shield){
                                    if(br.shieldEfficiency!=0)validator.error("Overhaul SFR block recipe has shield efficiency, but is not for a shield!").solve(() -> {
                                        br.shieldEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(br.shieldHeat!=0)validator.error("Overhaul SFR block recipe has shield heat, but is not for a shield!").solve(() -> {
                                        br.shieldHeat = 0;
                                    }, "Set heat to 0");
                                }else{
                                    if(br.shieldEfficiency<0)validator.error("Overhaul SFR block recipe shield efficiency must not be negative!").solve(() -> {
                                        br.shieldEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(br.shieldHeat<0)validator.error("Overhaul SFR block recipe shield heat must not be negative!").solve(() -> {
                                        br.shieldHeat = 0;
                                    }, "Set heat to 0");
                                }
                                if(!Float.isFinite(br.shieldEfficiency))validator.error("Overhaul SFR block recipe shield efficiency is not finite!").solve(() -> {
                                    br.shieldEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(!b.heatsink){
                                    if(br.heatsinkCooling!=0)validator.error("Overhaul SFR block recipe has heat sink cooling, but is not for a heatsink!").solve(() -> {
                                        br.heatsinkCooling = 0;
                                    }, "Set cooling to 0");
                                }
                            }
                            //</editor-fold>
                        }
                        //</editor-fold>
                        if(configuration.addon){
                            //<editor-fold defaultstate="collapsed" desc="Addon Blocks">
                            validator.stage("Checking Addon Blocks...");
                            for(int i = 0; i<sfr.allBlocks.size(); i++){
                                validator.stage("Checking Addon blocks... ("+(i+1)+"/"+sfr.allBlocks.size()+")");
                                net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b = sfr.allBlocks.get(i);
                                Runnable blockConfig = () -> {
                                    gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionsfr.MenuBlockConfiguration(gui, this, configuration, b));
                                };
                                //<editor-fold defaultstate="collapsed" desc="Name">
                                if(b.name==null){
                                    validator.error("Overhaul SFR addon block name is null!").solve(blockConfig, "Go to Block configuration");
                                }else{
                                    net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block match = null;
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b2 : Core.configuration.overhaul.fissionSFR.allBlocks){
                                        if(b.name.equals(b2.name))match = b2;
                                    }
                                    if(match==null)validator.error("Overhaul SFR addon block "+b.name+" does not match any blocks in the parent configuration!").solve(blockConfig, "Go to Block configuration");
                                    else{
                                        if(match.fuelCell != b.fuelCell)validator.error("Overhaul SFR addon block "+b.name+" setting Fuel Cell does not match that of the corresponding block in the parent configuration!").solve(blockConfig, "Go to Block Configuration");
                                        if(match.moderator != b.moderator)validator.error("Overhaul SFR addon block "+b.name+" setting Moderator does not match that of the corresponding block in the parent configuration!").solve(blockConfig, "Go to Block Configuration");
                                        if(match.reflector != b.reflector)validator.error("Overhaul SFR addon block "+b.name+" setting Reflector does not match that of the corresponding block in the parent configuration!").solve(blockConfig, "Go to Block Configuration");
                                        if(match.irradiator != b.irradiator)validator.error("Overhaul SFR addon block "+b.name+" setting Irradiator does not match that of the corresponding block in the parent configuration!").solve(blockConfig, "Go to Block Configuration");
                                        if(match.heatsink != b.heatsink)validator.error("Overhaul SFR addon block "+b.name+" setting Heatsink does not match that of the corresponding block in the parent configuration!").solve(blockConfig, "Go to Block Configuration");
                                        if(match.shield != b.shield)validator.error("Overhaul SFR addon block "+b.name+" setting Shield does not match that of the corresponding block in the parent configuration!").solve(blockConfig, "Go to Block Configuration");
                                    }
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b2 : sfr.allBlocks){
                                        if(b==b2)continue;
                                        if(b.name.equals(b2.name))validator.error("Overhaul SFR addon block "+b.name+" is duplicated!").hint("This configuration contains multiple of the same addon blocks.").solve(blockConfig, "Go to Block configuration");
                                    }
                                }
                                //</editor-fold>
                                //<editor-fold defaultstate="collapsed" desc="Block Recipes">
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe br : b.recipes){
                                    Runnable blockRecipeConfig = () -> {
                                        gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionsfr.MenuBlockRecipeConfiguration(gui, this, configuration, b, br));
                                    };
                                    //<editor-fold defaultstate="collapsed" desc="Input Name">
                                    if(br.inputName==null){
                                        validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" input name is null!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    }else{
                                        String[] split = br.inputName.split("\\:");
                                        if(split.length<2||split.length>3){
                                            validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" input name is invalid! ("+br.inputName+")").hint("Item input name must be a namespaced ID! (namespace:inputname or namespace:inputname:metadata").solve(blockConfig, "Go to Block Recipe configuration");
                                        }else{
                                            for(char c : split[0].toCharArray()){
                                                if(namespaceChars.indexOf(c)==-1){
                                                    validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" namespace is invalid! ("+split[0]+")").hint("Namespaces can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                            for(char c : split[1].toCharArray()){
                                                if(namespacePathChars.indexOf(c)==-1){
                                                    validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" input name is invalid! ("+split[1]+")").hint("block recipe input names can only contain characters 0-9, a-z, _, -, ., and /! This should be the input name of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                            if(split.length==3){
                                                try{
                                                    Integer.parseInt(split[2]);
                                                }catch(Exception ex){
                                                    validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" metadata is invalid! ("+split[2]+")").hint("Metadata must be an integer! If the ingame block recipe does not have metadata, use only namespace:input name.").solve(blockConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                        }
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe c2 : b.recipes){
                                            if(br==c2)continue;
                                            if(br.inputName.equals(c2.inputName))validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" input name is not unique! ("+br.inputName+")").hint("This block contains multiple block recipes with the same name.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        }
                                    }
                                    //</editor-fold>
                                    //<editor-fold defaultstate="collapsed" desc="Input Display name">
                                    if(br.inputDisplayName==null||br.inputDisplayName.isEmpty()){
                                        validator.warn("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has no display name!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    }
                                    //</editor-fold>
                                    //<editor-fold defaultstate="collapsed" desc="Output Name">
                                    if(br.outputName==null){
                                        validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" output name is null!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    }else{
                                        String[] split = br.outputName.split("\\:");
                                        if(split.length<2||split.length>3){
                                            validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" output name is invalid! ("+br.outputName+")").hint("Item output name must be a namespaced ID! (namespace:outputname or namespace:outputname:metadata").solve(blockConfig, "Go to Block Recipe configuration");
                                        }else{
                                            for(char c : split[0].toCharArray()){
                                                if(namespaceChars.indexOf(c)==-1){
                                                    validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" namespace is invalid! ("+split[0]+")").hint("Namespaces can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                            for(char c : split[1].toCharArray()){
                                                if(namespacePathChars.indexOf(c)==-1){
                                                    validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" output name is invalid! ("+split[1]+")").hint("block recipe output names can only contain characters 0-9, a-z, _, -, ., and /! This should be the output name of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                            if(split.length==3){
                                                try{
                                                    Integer.parseInt(split[2]);
                                                }catch(Exception ex){
                                                    validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" metadata is invalid! ("+split[2]+")").hint("Metadata must be an integer! If the ingame block recipe does not have metadata, use only namespace:output name.").solve(blockConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                        }
                                    }
                                    //</editor-fold>
                                    //<editor-fold defaultstate="collapsed" desc="Output Display name">
                                    if(br.outputDisplayName==null||br.outputDisplayName.isEmpty()){
                                        validator.warn("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has no display name!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    }
                                    //</editor-fold>
                                    //<editor-fold defaultstate="collapsed" desc="Legacy names">
                                    for(Configuration cf : Configuration.configurations){
                                        if(cf.overhaul!=null&&cf.overhaul.fissionSFR!=null){
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b2 : cf.overhaul.fissionSFR.blocks){
                                                if(!b.name.equals(b2.name))continue;
                                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe r2 : b2.recipes){
                                                    if(br.inputName.equals(r2.inputName)){
                                                        if(!br.inputLegacyNames.equals(r2.inputLegacyNames))validator.warn("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" legacy names do not match! ("+br.inputName+")").hint("Another block recipe was found in block "+b2.name+" of Configuration "+cf.name+" with different legacy names.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                    }else{
                                                        for(String s : br.inputLegacyNames)if(r2.inputLegacyNames.contains(s))validator.warn("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" legacy names are not unique! ("+s+")").hint("Another block recipe was found in block "+b2.name+" of configuration "+cf.name+" with the same legacy name. These blocks could be confused when loading old files").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    for(AddonConfiguration cf : Configuration.internalAddonCache.values()){
                                        if(cf.self.overhaul!=null&&cf.self.overhaul.fissionSFR!=null){
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b2 : cf.self.overhaul.fissionSFR.blocks){
                                                if(!b.name.equals(b2.name))continue;
                                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe r2 : b2.recipes){
                                                    if(br.inputName.equals(r2.inputName)&&!br.inputLegacyNames.equals(r2.inputLegacyNames)){
                                                        validator.warn("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" legacy names do not match! ("+br.inputName+")").hint("Another block recipe was found in block "+b2.name+" of Configuration "+cf.name+" with different legacy names.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //</editor-fold>
                                    if(br.inputTexture==null)validator.warn("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has no input texture!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    if(br.outputTexture==null)validator.warn("Overhaul SFR addon block recipe "+br.outputName+" of block "+b.name+" has no output texture!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    if(!b.fuelCell){
                                        if(br.fuelCellEfficiency!=0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has fuel cell efficiency, but is not for a fuel cell!").solve(() -> {
                                            br.fuelCellEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.fuelCellHeat!=0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has fuel cell heat, but is not for a fuel cell!").solve(() -> {
                                            br.fuelCellHeat = 0;
                                        }, "Set heat to 0");
                                        if(br.fuelCellCriticality!=0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has fuel cell criticality, but is not for a fuel cell!").solve(() -> {
                                            br.fuelCellCriticality = 0;
                                        }, "Set criticality to 0");
                                        if(br.fuelCellSelfPriming)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" is fuel cell self priming, but is not for a fuel cell!").solve(() -> {
                                            br.fuelCellSelfPriming = false;
                                        }, "Set self priming to false");
                                    }else{
                                        if(br.fuelCellEfficiency<0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" fuel cell efficiency must not be negative!").solve(() -> {
                                            br.fuelCellEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.fuelCellHeat<0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" fuel cell heat must not be negative!").solve(() -> {
                                            br.fuelCellHeat = 0;
                                        }, "Set heat to 0");
                                        if(br.fuelCellCriticality<0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" fuel cell criticality must not be negative!").solve(() -> {
                                            br.fuelCellCriticality = 0;
                                        }, "Set criticality to 0");
                                    }
                                    if(!Float.isFinite(br.fuelCellEfficiency))validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" fuel cell efficiency is not finite!").solve(() -> {
                                        br.fuelCellEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!b.irradiator){
                                        if(br.irradiatorEfficiency!=0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has irradiator efficiency, but is not for an irradiator!").solve(() -> {
                                            br.irradiatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.irradiatorHeat!=0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has irradiator heat, but is not for an irradiator!").solve(() -> {
                                            br.irradiatorHeat = 0;
                                        }, "Set heat to 0");
                                    }else{
                                        if(br.irradiatorEfficiency<0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" irradiator efficiency must not be negative!").solve(() -> {
                                            br.irradiatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.irradiatorHeat<0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" irradiator heat must not be negative!").solve(() -> {
                                            br.irradiatorHeat = 0;
                                        }, "Set heat to 0");
                                    }
                                    if(!Float.isFinite(br.irradiatorEfficiency))validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" irradiator efficiency is not finite!").solve(() -> {
                                        br.irradiatorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!Float.isFinite(br.irradiatorHeat))validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" irradiator heat is not finite!").solve(() -> {
                                        br.irradiatorHeat = 0;
                                    }, "Set heat to 0");
                                    if(!b.reflector){
                                        if(br.reflectorEfficiency!=0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has reflector efficiency, but is not for a reflector!").solve(() -> {
                                            br.reflectorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.reflectorReflectivity!=0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has reflector reflectivity, but is not for a reflector!").solve(() -> {
                                            br.reflectorReflectivity = 0;
                                        }, "Set reflectivity to 0");
                                    }else{
                                        if(br.reflectorEfficiency<0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" reflector efficiency must not be negative!").solve(() -> {
                                            br.reflectorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.reflectorReflectivity<0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" reflector reflectivity must not be negative!").solve(() -> {
                                            br.reflectorReflectivity = 0;
                                        }, "Set reflectivity to 0");
                                    }
                                    if(!Float.isFinite(br.reflectorEfficiency))validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" reflector efficiency is not finite!").solve(() -> {
                                        br.reflectorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!Float.isFinite(br.reflectorReflectivity))validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" reflector reflectivity is not finite!").solve(() -> {
                                        br.reflectorReflectivity = 0;
                                    }, "Set reflectivity to 0");
                                    if(!b.moderator){
                                        if(br.moderatorEfficiency!=0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has moderator efficiency, but is not for a moderator!").solve(() -> {
                                            br.moderatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.moderatorFlux!=0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has moderator flux, but is not for a moderator!").solve(() -> {
                                            br.moderatorFlux = 0;
                                        }, "Set flux to 0");
                                        if(br.moderatorActive)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" is moderator active, but is not for a moderator!").solve(() -> {
                                            br.moderatorActive = false;
                                        }, "Set active to false");
                                    }else{
                                        if(br.moderatorEfficiency<0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" moderator efficiency must not be negative!").solve(() -> {
                                            br.moderatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.moderatorFlux<0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" moderator flux must not be negative!").solve(() -> {
                                            br.moderatorFlux = 0;
                                        }, "Set flux to 0");
                                    }
                                    if(!Float.isFinite(br.moderatorEfficiency))validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" moderator efficiency is not finite!").solve(() -> {
                                        br.moderatorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!b.shield){
                                        if(br.shieldEfficiency!=0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has shield efficiency, but is not for a shield!").solve(() -> {
                                            br.shieldEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.shieldHeat!=0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has shield heat, but is not for a shield!").solve(() -> {
                                            br.shieldHeat = 0;
                                        }, "Set heat to 0");
                                    }else{
                                        if(br.shieldEfficiency<0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" shield efficiency must not be negative!").solve(() -> {
                                            br.shieldEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.shieldHeat<0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" shield heat must not be negative!").solve(() -> {
                                            br.shieldHeat = 0;
                                        }, "Set heat to 0");
                                    }
                                    if(!Float.isFinite(br.shieldEfficiency))validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" shield efficiency is not finite!").solve(() -> {
                                        br.shieldEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!b.heatsink){
                                        if(br.heatsinkCooling!=0)validator.error("Overhaul SFR addon block recipe "+br.inputName+" of block "+b.name+" has heat sink cooling, but is not for a heatsink!").solve(() -> {
                                            br.heatsinkCooling = 0;
                                        }, "Set cooling to 0");
                                    }
                                }
                                //</editor-fold>
                            }
                            //</editor-fold>
                        }
                        //<editor-fold defaultstate="collapsed" desc="Coolant Recipes">
                        validator.stage("Checking coolant recipes...");
                        for(int i = 0; i<sfr.coolantRecipes.size(); i++){
                            validator.stage("Checking coolant recipes... ("+(i+1)+"/"+sfr.coolantRecipes.size()+")");
                            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe c = sfr.coolantRecipes.get(i);
                            Runnable coolantRecipeConfig = () -> {
                                gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionsfr.MenuCoolantRecipeConfiguration(gui, this, configuration, c));
                            };
                            //<editor-fold defaultstate="collapsed" desc="Input Name">
                            if(c.inputName==null){
                                validator.error("Overhaul SFR coolant recipe input name is null!").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                            }else{
                                if(c.inputName.contains(":"))validator.error("Overhaul SFR coolant recipe input name is invalid! ("+c.inputName+")").hint("Fluid name must be the name of the fluid! (ex. water, not fluid:water or minecraft:water)").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                                for(char ch : c.inputName.toCharArray()){
                                    if(namespaceChars.indexOf(ch)==-1){
                                        validator.error("Overhaul SFR coolant recipe input name is invalid! ("+c.inputName+")").hint("Fluid names can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame fluid.").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                                    }
                                }
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe c2 : sfr.coolantRecipes){
                                    if(c==c2)continue;
                                    if(c.inputName.equals(c2.inputName))validator.error("Overhaul SFR coolant recipe input name is not unique! ("+c.inputName+")").hint("This configuration contains multiple coolant recipes with the same name.").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                                }
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Input Display name">
                            if(c.inputDisplayName==null||c.inputDisplayName.isEmpty()){
                                validator.warn("Overhaul SFR coolant recipe has no display name!").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Output Name">
                            if(c.outputName==null){
                                validator.error("Overhaul SFR coolant recipe output name is null!").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                            }else{
                                if(c.outputName.contains(":"))validator.error("Overhaul SFR coolant recipe output name is invalid! ("+c.outputName+")").hint("Fluid name must be the name of the fluid! (ex. water, not fluid:water or minecraft:water)").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                                for(char ch : c.outputName.toCharArray()){
                                    if(namespaceChars.indexOf(ch)==-1){
                                        validator.error("Overhaul SFR coolant recipe output name is invalid! ("+c.outputName+")").hint("Fluid names can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame fluid.").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                                    }
                                }
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Output Display name">
                            if(c.outputDisplayName==null||c.outputDisplayName.isEmpty()){
                                validator.warn("Overhaul SFR coolant recipe has no display name!").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Legacy names">
                            for(Configuration cf : Configuration.configurations){
                                if(cf.overhaul!=null&&cf.overhaul.fissionSFR!=null){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe c2 : cf.overhaul.fissionSFR.coolantRecipes){
                                        if(c.inputName.equals(c2.inputName)){
                                            if(!c.inputLegacyNames.equals(c2.inputLegacyNames))validator.warn("Overhaul SFR block legacy names do not match! ("+c.inputName+")").hint("Another block was found in Configuration "+cf.name+" with different legacy names.").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                                        }else{
                                            for(String s : c.inputLegacyNames)if(c2.inputLegacyNames.contains(s))validator.warn("Overhaul SFR block legacy names are not unique! ("+s+")").hint("Another block was found in configuration "+cf.name+" with the same legacy name. These blocks could be confused when loading old files").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                                        }
                                    }
                                }
                            }
                            for(AddonConfiguration cf : Configuration.internalAddonCache.values()){
                                if(cf.self.overhaul!=null&&cf.self.overhaul.fissionSFR!=null){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe c2 : cf.self.overhaul.fissionSFR.coolantRecipes){
                                        if(c.inputName.equals(c2.inputName)&&!c.inputLegacyNames.equals(c2.inputLegacyNames)){
                                            validator.warn("Overhaul SFR block legacy names do not match! ("+c.inputName+")").hint("Another block was found in Addon "+cf.name+" with different legacy names.").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                                        }
                                    }
                                }
                            }
                            //</editor-fold>
                            if(c.inputTexture==null)validator.warn("Overhaul SFR coolant recipe "+c.inputName+" has no input texture!").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                            if(c.outputTexture==null)validator.warn("Overhaul SFR coolant recipe "+c.outputName+" has no output texture!").solve(coolantRecipeConfig, "Go to Coolant Recipe configuration");
                            if(!Float.isFinite(c.outputRatio))validator.error("Overhaul SFR coolant recipe output ratio is not finite! ("+c.outputRatio+")").solve(() -> {
                                c.outputRatio = 0;
                            }, "Set output ratio to 0");
                            if(c.outputRatio<0)validator.error("Overhaul SFR coolant recipe output ratio is negative! ("+c.outputRatio+")").solve(() -> {
                                c.outputRatio = 0;
                            }, "Set output ratio to 0");
                            if(c.heat<0)validator.error("Overhaul SFR coolant recipe heat must not be negative! ("+c.heat+")").solve(() -> {
                                c.heat = 0;
                            }, "Set heat to 0");
                        }
                        //</editor-fold>
                    }
                    if(overhaul.fissionMSR!=null){
                        validator.stage("Checking overhaul MSR configuration");
                        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration msr = overhaul.fissionMSR;
                        if(configuration.addon){
                            //<editor-fold defaultstate="collapsed" desc="Size">
                            if(msr.minSize!=0)validator.error("Overhaul MSR minimum size must be zero! ("+msr.minSize+")").solve(() -> {
                                msr.minSize = 0;
                            }, "Set minimum size to 0");
                            if(msr.maxSize!=0)validator.error("Overhaul MSR maximum size must be zero! ("+msr.maxSize+")").solve(() -> {
                                msr.maxSize = 0;
                            }, "Set maximum size to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Sparsity Penalty">
                            if(msr.sparsityPenaltyMult!=0)validator.error("Overhaul MSR sparsity penalty mult must be zero! ("+msr.sparsityPenaltyMult+")").solve(() -> {
                                msr.sparsityPenaltyMult = 0;
                            }, "Set sparsity penalty mult to 0");
                            if(msr.sparsityPenaltyThreshold!=0)validator.error("Overhaul MSR sparsity penalty threshold must be zero! ("+msr.sparsityPenaltyThreshold+")").solve(() -> {
                                msr.sparsityPenaltyThreshold = 0;
                            }, "Set sparsity penalty threshold to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Neutron Reach">
                            if(msr.neutronReach!=0)validator.error("Overhaul MSR neutron reach must be zero! ("+msr.neutronReach+")").solve(() -> {
                                msr.neutronReach = 0;
                            }, "Set neutron reach to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Active Cooler Rate">
                            if(msr.coolingEfficiencyLeniency!=0)validator.error("Overhaul MSR cooling efficiency leniency must be zero! ("+msr.coolingEfficiencyLeniency+")").solve(() -> {
                                msr.coolingEfficiencyLeniency = 0;
                            }, "Set cooling efficiency leniency to 0");
                            //</editor-fold>
                        }else{
                            //<editor-fold defaultstate="collapsed" desc="Size">
                            if(msr.minSize<1)validator.error("Overhaul MSR minimum size must be positive! ("+msr.minSize+")").solve(() -> {
                                msr.minSize = 1;
                            }, "Set minimum size to 1");
                            if(msr.maxSize<1)validator.error("Overhaul MSR maximum size must be positive! ("+msr.maxSize+")").solve(() -> {
                                msr.maxSize = 1;
                            }, "Set maximum size to 1");
                            if(msr.minSize>64)validator.warn("Overhaul MSR minimum size is too big! ("+msr.minSize+")").hint("Sizes above 64x64x64 can cause very large file sizes and significant performance issues!").solve(() -> {
                                msr.minSize = 64;
                            }, "Set minimum size to 64");
                            if(msr.maxSize>64)validator.warn("Overhaul MSR maximum size is too big! ("+msr.maxSize+")").hint("Sizes above 64x64x64 can cause very large file sizes and significant performance issues!").solve(() -> {
                                msr.maxSize = 64;
                            }, "Set maximum size to 64");
                            if(msr.maxSize<msr.minSize)validator.error("Overhaul MSR maximum size is less than minimum size! ("+msr.maxSize+"<"+msr.minSize+")").solve(() -> {
                                msr.minSize = msr.maxSize;
                            }, "Set minimum size to equal maximum size");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Sparsity Penalty">
                            if(!Float.isFinite(msr.sparsityPenaltyMult))validator.error("Overhaul MSR sparsity penalty mult is not finite! ("+msr.sparsityPenaltyMult+")").solve(() -> {
                                msr.sparsityPenaltyMult = 0;
                            }, "Set sparsity penalty mult to 0");
                            if(msr.sparsityPenaltyMult<0)validator.error("Overhaul MSR sparsity penalty mult is negative! ("+msr.sparsityPenaltyMult+")").solve(() -> {
                                msr.sparsityPenaltyMult = 0;
                            }, "Set sparsity penalty mult to 0");
                            if(!Float.isFinite(msr.sparsityPenaltyThreshold))validator.error("Overhaul MSR sparsity penalty threshold is not finite! ("+msr.sparsityPenaltyThreshold+")").solve(() -> {
                                msr.sparsityPenaltyThreshold = 0;
                            }, "Set sparsity penalty threshold to 0");
                            if(msr.sparsityPenaltyThreshold<0)validator.error("Overhaul MSR sparsity penalty threshold is negative! ("+msr.sparsityPenaltyThreshold+")").solve(() -> {
                                msr.sparsityPenaltyThreshold = 0;
                            }, "Set sparsity penalty threshold to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Neutron Reach">
                            if(msr.neutronReach<=0)validator.error("Overhaul MSR neutron reach must be positive! ("+msr.neutronReach+")").solve(() -> {
                                msr.neutronReach = 1;
                            }, "Set neutron reach to 1");
                            if(msr.neutronReach>msr.maxSize-2)validator.error("Overhaul MSR neutron reach is larger than is possible in the max size! ("+msr.neutronReach+">"+(msr.maxSize-2)+")").solve(() -> {
                                msr.neutronReach = msr.maxSize-2;
                            }, "Set neutron reach to maximum size - 2");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Active Cooler Rate">
                            if(msr.coolingEfficiencyLeniency<0)validator.error("Overhaul MSR cooling efficiency leniency must not be negative! ("+msr.coolingEfficiencyLeniency+")").solve(() -> {
                                msr.coolingEfficiencyLeniency = 0;
                            }, "Set cooling efficiency leniency to 0");
                            //</editor-fold>
                        }
                        //<editor-fold defaultstate="collapsed" desc="Blocks">
                        validator.stage("Checking blocks...");
                        for(int i = 0; i<msr.blocks.size(); i++){
                            validator.stage("Checking blocks... ("+(i+1)+"/"+msr.blocks.size()+")");
                            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b = msr.blocks.get(i);
                            Runnable blockConfig = () -> {
                                gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr.MenuBlockConfiguration(gui, this, configuration, b));
                            };
                            //<editor-fold defaultstate="collapsed" desc="Name">
                            if(b.name==null){
                                validator.error("Overhaul MSR block name is null!").solve(blockConfig, "Go to Block configuration");
                            }else{
                                String[] split = b.name.split("\\:");
                                if(split.length<2||split.length>3){
                                    validator.error("Overhaul MSR block name is invalid! ("+b.name+")").hint("Block name must be a namespaced ID! (namespace:name or namespace:name:metadata").solve(blockConfig, "Go to Block configuration");
                                }else{
                                    for(char c : split[0].toCharArray()){
                                        if(namespaceChars.indexOf(c)==-1){
                                            validator.error("Overhaul MSR block namespace is invalid! ("+split[0]+")").hint("Namespaces can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame block.").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                    for(char c : split[1].toCharArray()){
                                        if(namespacePathChars.indexOf(c)==-1){
                                            validator.error("Overhaul MSR block name is invalid! ("+split[1]+")").hint("block names can only contain characters 0-9, a-z, _, -, ., and /! This should be the name of the ingame block.").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                    if(split.length==3){
                                        try{
                                            Integer.parseInt(split[2]);
                                        }catch(Exception ex){
                                            validator.error("Overhaul MSR block metadata is invalid! ("+split[2]+")").hint("Metadata must be an integer! If the ingame block does not have metadata, use only namespace:name.").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                }
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : msr.blocks){
                                    if(b==b2)continue;
                                    if(b.name.equals(b2.name))validator.error("Overhaul MSR block name is not unique! ("+b.name+")").hint("This configuration contains multiple blocks with the same name.").solve(blockConfig, "Go to Block configuration");
                                }
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Display name">
                            if(b.displayName==null||b.displayName.isEmpty()){
                                validator.warn("Overhaul MSR block has no display name!").solve(blockConfig, "Go to Block configuration");
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Legacy names">
                            for(Configuration c : Configuration.configurations){
                                if(c.overhaul!=null&&c.overhaul.fissionMSR!=null){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : c.overhaul.fissionMSR.blocks){
                                        if(b.name.equals(b2.name)){
                                            if(!b.legacyNames.equals(b2.legacyNames))validator.warn("Overhaul MSR block "+b.name+" legacy names do not match!").hint("Another block was found in Configuration "+c.name+" with different legacy names.").solve(blockConfig, "Go to Block configuration");
                                        }else{
                                            for(String s : b.legacyNames)if(b2.legacyNames.contains(s))validator.warn("Overhaul MSR block "+b.name+" legacy names are not unique! ("+s+")").hint("Another block was found in configuration "+c.name+" with the same legacy name. These blocks could be confused when loading old files").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                }
                            }
                            for(AddonConfiguration c : Configuration.internalAddonCache.values()){
                                if(c.self.overhaul!=null&&c.self.overhaul.fissionMSR!=null){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : c.self.overhaul.fissionMSR.blocks){
                                        if(b.name.equals(b2.name)&&!b.legacyNames.equals(b2.legacyNames)){
                                            validator.warn("Overhaul MSR block "+b.name+" legacy names do not match!").hint("Another block was found in Addon "+c.name+" with different legacy names.").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                }
                            }
                            //</editor-fold>
                            if(b.texture==null)validator.warn("Overhaul MSR block "+b.name+" has no texture!").solve(blockConfig, "Go to Block configuration");
                            //<editor-fold defaultstate="collapsed" desc="Settings">
                            if(b.createCluster&&!b.cluster)validator.error("Overhaul MSR block "+b.name+" creates clusters, but cannot be part of a cluster!").solve(() -> {
                                b.cluster = true;
                            }, "Set cluster to true");
                            if((b.fuelVessel||b.irradiator||b.shield||b.heater)&&!b.cluster)validator.error("Overhaul MSR block "+b.name+" handles heat, but cannot be part of a cluster!").hint("This block is a fuel vessel, irradiator, shield, or heater").solve(() -> {
                                b.cluster = true;
                            }, "Set cluster to true");
                            if(b.cluster&&b.conductor)validator.error("Overhaul MSR block "+b.name+" can be part of a cluster, but is also a conductor!").hint("The \"Conductor\" setting lets this block connect clusters to the casing, but won't connect clusters together.").solve(() -> {
                                b.cluster = false;
                            }, "Set cluster to false");
                            if(b.casing&&(b.cluster||b.createCluster||b.conductor||b.functional||b.blocksLOS))validator.error("Overhaul MSR block "+b.name+" is a casing block, but has internal reactor functionality!").hint("This block can cluster, creates clusters, is a conductor, is functional, or blocks line of sight").solve(() -> {
                                b.casing = false;
                            }, "Set casing to false");
                            if(b.casingEdge&&(b.cluster||b.createCluster||b.conductor||b.functional||b.blocksLOS))validator.error("Overhaul MSR block "+b.name+" is a casing edge block, but has internal reactor functionality!").hint("This block can cluster, creates clusters, is a conductor, is functional, or blocks line of sight").solve(() -> {
                                b.casingEdge = false;
                            }, "Set casing edge to false");
                            if(b.controller&&!b.casing)validator.error("Overhaul MSR controller "+b.name+" is not part of the casing!").solve(() -> {
                                b.casing = true;
                            }, "Set casing to true");
                            if(b.controller&&b.casingEdge)validator.error("Overhaul MSR controller "+b.name+" is part of the casing edge!").solve(() -> {
                                b.casingEdge = false;
                            }, "Set casing edge to false");
                            if(!b.fuelVessel||!b.fuelVesselHasBaseStats){
                                if(b.fuelVesselEfficiency!=0)validator.error("Overhaul MSR block "+b.name+" has fuel vessel efficiency, but is not a fuel vessel or has no base stats!").solve(() -> {
                                    b.fuelVesselEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.fuelVesselHeat!=0)validator.error("Overhaul MSR block "+b.name+" has fuel vessel heat, but is not a fuel vessel or has no base stats!").solve(() -> {
                                    b.fuelVesselHeat = 0;
                                }, "Set heat to 0");
                                if(b.fuelVesselCriticality!=0)validator.error("Overhaul MSR block "+b.name+" has fuel vessel criticality, but is not a fuel vessel or has no base stats!").solve(() -> {
                                    b.fuelVesselCriticality = 0;
                                }, "Set criticality to 0");
                                if(b.fuelVesselSelfPriming)validator.error("Overhaul MSR block "+b.name+" is fuel vessel self priming, but is not a fuel vessel or has no base stats!").solve(() -> {
                                    b.fuelVesselSelfPriming = false;
                                }, "Set self priming to false");
                            }else{
                                if(b.fuelVesselEfficiency<0)validator.error("Overhaul MSR block "+b.name+" fuel vessel efficiency must not be negative!").solve(() -> {
                                    b.fuelVesselEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.fuelVesselHeat<0)validator.error("Overhaul MSR block "+b.name+" fuel vessel heat must not be negative!").solve(() -> {
                                    b.fuelVesselHeat = 0;
                                }, "Set heat to 0");
                                if(b.fuelVesselCriticality<0)validator.error("Overhaul MSR block "+b.name+" fuel vessel criticality must not be negative!").solve(() -> {
                                    b.fuelVesselCriticality = 0;
                                }, "Set criticality to 0");
                            }
                            if(!Float.isFinite(b.fuelVesselEfficiency))validator.error("Overhaul MSR block "+b.name+" fuel vessel efficiency is not finite!").solve(() -> {
                                b.fuelVesselEfficiency = 0;
                            }, "Set efficiency to 0");
                            if(!b.irradiator||!b.irradiatorHasBaseStats){
                                if(b.irradiatorEfficiency!=0)validator.error("Overhaul MSR block "+b.name+" has irradiator efficiency, but is not a irradiator or has no base stats!").solve(() -> {
                                    b.irradiatorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.irradiatorHeat!=0)validator.error("Overhaul MSR block "+b.name+" has irradiator heat, but is not a irradiator or has no base stats!").solve(() -> {
                                    b.irradiatorHeat = 0;
                                }, "Set heat to 0");
                            }else{
                                if(b.irradiatorEfficiency<0)validator.error("Overhaul MSR block "+b.name+" irradiator efficiency must not be negative!").solve(() -> {
                                    b.irradiatorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.irradiatorHeat<0)validator.error("Overhaul MSR block "+b.name+" irradiator heat must not be negative!").solve(() -> {
                                    b.irradiatorHeat = 0;
                                }, "Set heat to 0");
                            }
                            if(!Float.isFinite(b.irradiatorEfficiency))validator.error("Overhaul MSR block "+b.name+" irradiator efficiency is not finite!").solve(() -> {
                                b.irradiatorEfficiency = 0;
                            }, "Set efficiency to 0");
                            if(!Float.isFinite(b.irradiatorHeat))validator.error("Overhaul MSR block "+b.name+" irradiator heat is not finite!").solve(() -> {
                                b.irradiatorHeat = 0;
                            }, "Set heat to 0");
                            if(!b.reflector||!b.reflectorHasBaseStats){
                                if(b.reflectorEfficiency!=0)validator.error("Overhaul MSR block "+b.name+" has reflector efficiency, but is not a reflector or has no base stats!").solve(() -> {
                                    b.reflectorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.reflectorReflectivity!=0)validator.error("Overhaul MSR block "+b.name+" has reflector reflectivity, but is not a reflector or has no base stats!").solve(() -> {
                                    b.reflectorReflectivity = 0;
                                }, "Set reflectivity to 0");
                            }else{
                                if(b.reflectorEfficiency<0)validator.error("Overhaul MSR block "+b.name+" reflector efficiency must not be negative!").solve(() -> {
                                    b.reflectorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.reflectorReflectivity<0)validator.error("Overhaul MSR block "+b.name+" reflector reflectivity must not be negative!").solve(() -> {
                                    b.reflectorReflectivity = 0;
                                }, "Set reflectivity to 0");
                            }
                            if(!Float.isFinite(b.reflectorEfficiency))validator.error("Overhaul MSR block "+b.name+" reflector efficiency is not finite!").solve(() -> {
                                b.reflectorEfficiency = 0;
                            }, "Set efficiency to 0");
                            if(!Float.isFinite(b.reflectorReflectivity))validator.error("Overhaul MSR block "+b.name+" reflector reflectivity is not finite!").solve(() -> {
                                b.reflectorReflectivity = 0;
                            }, "Set reflectivity to 0");
                            if(!b.moderator||!b.moderatorHasBaseStats){
                                if(b.moderatorEfficiency!=0)validator.error("Overhaul MSR block "+b.name+" has moderator efficiency, but is not a moderator or has no base stats!").solve(() -> {
                                    b.moderatorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.moderatorFlux!=0)validator.error("Overhaul MSR block "+b.name+" has moderator flux, but is not a moderator or has no base stats!").solve(() -> {
                                    b.moderatorFlux = 0;
                                }, "Set flux to 0");
                                if(b.moderatorActive)validator.error("Overhaul MSR block "+b.name+" is moderator active, but is not a moderator or has no base stats!").solve(() -> {
                                    b.moderatorActive = false;
                                }, "Set active to false");
                            }else{
                                if(b.moderatorEfficiency<0)validator.error("Overhaul MSR block "+b.name+" moderator efficiency must not be negative!").solve(() -> {
                                    b.moderatorEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.moderatorFlux<0)validator.error("Overhaul MSR block "+b.name+" moderator flux must not be negative!").solve(() -> {
                                    b.moderatorFlux = 0;
                                }, "Set flux to 0");
                            }
                            if(!Float.isFinite(b.moderatorEfficiency))validator.error("Overhaul MSR block "+b.name+" moderator efficiency is not finite!").solve(() -> {
                                b.moderatorEfficiency = 0;
                            }, "Set efficiency to 0");
                            if(!b.shield||!b.shieldHasBaseStats){
                                if(b.shieldEfficiency!=0)validator.error("Overhaul MSR block "+b.name+" has shield efficiency, but is not a shield or has no base stats!").solve(() -> {
                                    b.shieldEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.shieldHeat!=0)validator.error("Overhaul MSR block "+b.name+" has shield heat, but is not a shield or has no base stats!").solve(() -> {
                                    b.shieldHeat = 0;
                                }, "Set heat to 0");
                            }else{
                                if(b.shieldEfficiency<0)validator.error("Overhaul MSR block "+b.name+" shield efficiency must not be negative!").solve(() -> {
                                    b.shieldEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.shieldHeat<0)validator.error("Overhaul MSR block "+b.name+" shield heat must not be negative!").solve(() -> {
                                    b.shieldHeat = 0;
                                }, "Set heat to 0");
                            }
                            if(!Float.isFinite(b.shieldEfficiency))validator.error("Overhaul MSR block "+b.name+" shield efficiency is not finite!").solve(() -> {
                                b.shieldEfficiency = 0;
                            }, "Set efficiency to 0");
                            if(b.shield){
                                if(b.shieldClosedTexture==null)validator.warn("Overhaul MSR shield "+b.name+" has no closed texture!").solve(blockConfig, "Go to Block configuration");
                            }else{
                                if(b.shieldClosedTexture!=null)validator.warn("Overhaul MSR block "+b.name+" has shield closed texture, but is not a shield!").solve(() -> {
                                    b.shieldClosedTexture = null;
                                    b.shieldClosedDisplayTexture = null;
                                }, "Set texture to null");
                            }
                            if(!b.heater||!b.heaterHasBaseStats){
                                if(b.heaterCooling!=0)validator.error("Overhaul MSR block "+b.name+" has heater cooling, but is not a heater or has no base stats!").solve(() -> {
                                    b.heaterCooling = 0;
                                }, "Set cooling to 0");
                            }
                            if(!b.source){
                                if(b.sourceEfficiency!=0)validator.error("Overhaul MSR block "+b.name+" has source efficiency, but is not a neutron source!").solve(() -> {
                                    b.sourceEfficiency = 0;
                                }, "Set efficiency to 0");
                            }
                            if(!Float.isFinite(b.sourceEfficiency))validator.error("Overhaul MSR block "+b.name+" source efficiency is not finite!").solve(() -> {
                                b.sourceEfficiency = 0;
                            }, "Set efficiency to 0");
                            if(b.parent!=null){
                                if(b.portOutputTexture==null)validator.warn("Overhaul MSR port "+b.name+" has no output texture!").solve(blockConfig, "Go to Block configuration");
                                if(b.portOutputDisplayName==null||b.portOutputDisplayName.isEmpty()){
                                    validator.warn("Overhaul MSR port "+b.name+" has no output display name!").solve(blockConfig, "Go to Block configuration");
                                }
                            }else{
                                if(b.portOutputDisplayName!=null)validator.warn("Overhaul MSR block "+b.name+" has port output display name, but is not a port!").solve(() -> {
                                    b.portOutputDisplayName = null;
                                }, "Set display name to null");
                                if(b.portOutputTexture!=null)validator.warn("Overhaul MSR block "+b.name+" has port output texture, but is not a port!").solve(() -> {
                                    b.portOutputTexture = null;
                                    b.portOutputDisplayTexture = null;
                                }, "Set texture to null");
                            }
                            //</editor-fold>
                            validate(b.rules, "Overhaul MSR block "+b.name, blockConfig);
                            if(b.irradiator){
                                //<editor-fold defaultstate="collapsed" desc="Block Recipes">
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe br : b.recipes){
                                    Runnable blockRecipeConfig = () -> {
                                        gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr.MenuBlockRecipeConfiguration(gui, this, configuration, b, br));
                                    };
                                    //<editor-fold defaultstate="collapsed" desc="Input Name">
                                    if(br.inputName==null){
                                        validator.error("Overhaul MSR block recipe input name is null!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    }else{
                                        String[] split = br.inputName.split("\\:");
                                        if(split.length<2||split.length>3){
                                            validator.error("Overhaul MSR block recipe input name is invalid! ("+br.inputName+")").hint("Item input name must be a namespaced ID! (namespace:inputname or namespace:inputname:metadata").solve(blockConfig, "Go to Block Recipe configuration");
                                        }else{
                                            for(char c : split[0].toCharArray()){
                                                if(namespaceChars.indexOf(c)==-1){
                                                    validator.error("Overhaul MSR block recipe namespace is invalid! ("+split[0]+")").hint("Namespaces can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                            for(char c : split[1].toCharArray()){
                                                if(namespacePathChars.indexOf(c)==-1){
                                                    validator.error("Overhaul MSR block recipe input name is invalid! ("+split[1]+")").hint("block recipe input names can only contain characters 0-9, a-z, _, -, ., and /! This should be the input name of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                            if(split.length==3){
                                                try{
                                                    Integer.parseInt(split[2]);
                                                }catch(Exception ex){
                                                    validator.error("Overhaul MSR block recipe metadata is invalid! ("+split[2]+")").hint("Metadata must be an integer! If the ingame block recipe does not have metadata, use only namespace:input name.").solve(blockConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                        }
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe c2 : b.recipes){
                                            if(br==c2)continue;
                                            if(br.inputName.equals(c2.inputName))validator.error("Overhaul MSR block recipe input name is not unique! ("+br.inputName+")").hint("This block contains multiple block recipes with the same name.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        }
                                    }
                                    //</editor-fold>
                                    //<editor-fold defaultstate="collapsed" desc="Input Display name">
                                    if(br.inputDisplayName==null||br.inputDisplayName.isEmpty()){
                                        validator.warn("Overhaul MSR block recipe has no display name!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    }
                                    //</editor-fold>
                                    //<editor-fold defaultstate="collapsed" desc="Output Name">
                                    if(br.outputName==null){
                                        validator.error("Overhaul MSR block recipe output name is null!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    }else{
                                        String[] split = br.outputName.split("\\:");
                                        if(split.length<2||split.length>3){
                                            validator.error("Overhaul MSR block recipe output name is invalid! ("+br.outputName+")").hint("Item output name must be a namespaced ID! (namespace:outputname or namespace:outputname:metadata").solve(blockConfig, "Go to Block Recipe configuration");
                                        }else{
                                            for(char c : split[0].toCharArray()){
                                                if(namespaceChars.indexOf(c)==-1){
                                                    validator.error("Overhaul MSR block recipe namespace is invalid! ("+split[0]+")").hint("Namespaces can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                            for(char c : split[1].toCharArray()){
                                                if(namespacePathChars.indexOf(c)==-1){
                                                    validator.error("Overhaul MSR block recipe output name is invalid! ("+split[1]+")").hint("block recipe output names can only contain characters 0-9, a-z, _, -, ., and /! This should be the output name of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                            if(split.length==3){
                                                try{
                                                    Integer.parseInt(split[2]);
                                                }catch(Exception ex){
                                                    validator.error("Overhaul MSR block recipe metadata is invalid! ("+split[2]+")").hint("Metadata must be an integer! If the ingame block recipe does not have metadata, use only namespace:output name.").solve(blockConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                        }
                                    }
                                    //</editor-fold>
                                    //<editor-fold defaultstate="collapsed" desc="Output Display name">
                                    if(br.outputDisplayName==null||br.outputDisplayName.isEmpty()){
                                        validator.warn("Overhaul MSR block recipe has no display name!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    }
                                    //</editor-fold>
                                    //<editor-fold defaultstate="collapsed" desc="Legacy names">
                                    for(Configuration cf : Configuration.configurations){
                                        if(cf.overhaul!=null&&cf.overhaul.fissionMSR!=null){
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : cf.overhaul.fissionMSR.blocks){
                                                if(!b.name.equals(b2.name))continue;
                                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r2 : b2.recipes){
                                                    if(br.inputName.equals(r2.inputName)){
                                                        if(!br.inputLegacyNames.equals(r2.inputLegacyNames))validator.warn("Overhaul MSR block recipe legacy names do not match! ("+br.inputName+")").hint("Another block recipe was found in block "+b2.name+" of Configuration "+cf.name+" with different legacy names.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                    }else{
                                                        for(String s : br.inputLegacyNames)if(r2.inputLegacyNames.contains(s))validator.warn("Overhaul MSR block recipe legacy names are not unique! ("+s+")").hint("Another block recipe was found in block "+b2.name+" of configuration "+cf.name+" with the same legacy name. These blocks could be confused when loading old files").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    for(AddonConfiguration cf : Configuration.internalAddonCache.values()){
                                        if(cf.self.overhaul!=null&&cf.self.overhaul.fissionMSR!=null){
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : cf.self.overhaul.fissionMSR.blocks){
                                                if(!b.name.equals(b2.name))continue;
                                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r2 : b2.recipes){
                                                    if(br.inputName.equals(r2.inputName)&&!br.inputLegacyNames.equals(r2.inputLegacyNames)){
                                                        validator.warn("Overhaul MSR block recipe legacy names do not match! ("+br.inputName+")").hint("Another block recipe was found in block "+b2.name+" of Configuration "+cf.name+" with different legacy names.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //</editor-fold>
                                    if(br.inputTexture==null)validator.warn("Overhaul MSR block recipe "+br.inputName+" of block "+b.name+" has no input texture!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    if(br.outputTexture==null)validator.warn("Overhaul MSR block recipe "+br.outputName+" of block "+b.name+" has no output texture!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    if(!b.fuelVessel){
                                        if(br.fuelVesselEfficiency!=0)validator.error("Overhaul MSR block recipe has fuel vessel efficiency, but is not for a fuel vessel!").solve(() -> {
                                            br.fuelVesselEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.fuelVesselHeat!=0)validator.error("Overhaul MSR block recipe has fuel vessel heat, but is not for a fuel vessel!").solve(() -> {
                                            br.fuelVesselHeat = 0;
                                        }, "Set heat to 0");
                                        if(br.fuelVesselCriticality!=0)validator.error("Overhaul MSR block recipe has fuel vessel criticality, but is not for a fuel vessel!").solve(() -> {
                                            br.fuelVesselCriticality = 0;
                                        }, "Set criticality to 0");
                                        if(br.fuelVesselSelfPriming)validator.error("Overhaul MSR block recipe is fuel vessel self priming, but is not for a fuel vessel!").solve(() -> {
                                            br.fuelVesselSelfPriming = false;
                                        }, "Set self priming to false");
                                    }else{
                                        if(br.fuelVesselEfficiency<0)validator.error("Overhaul MSR block recipe fuel vessel efficiency must not be negative!").solve(() -> {
                                            br.fuelVesselEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.fuelVesselHeat<0)validator.error("Overhaul MSR block recipe fuel vessel heat must not be negative!").solve(() -> {
                                            br.fuelVesselHeat = 0;
                                        }, "Set heat to 0");
                                        if(br.fuelVesselCriticality<0)validator.error("Overhaul MSR block recipe fuel vessel criticality must not be negative!").solve(() -> {
                                            br.fuelVesselCriticality = 0;
                                        }, "Set criticality to 0");
                                    }
                                    if(!Float.isFinite(br.fuelVesselEfficiency))validator.error("Overhaul MSR block recipe fuel vessel efficiency is not finite!").solve(() -> {
                                        br.fuelVesselEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!b.irradiator){
                                        if(br.irradiatorEfficiency!=0)validator.error("Overhaul MSR block recipe has irradiator efficiency, but is not for an irradiator!").solve(() -> {
                                            br.irradiatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.irradiatorHeat!=0)validator.error("Overhaul MSR block recipe has irradiator heat, but is not for an irradiator!").solve(() -> {
                                            br.irradiatorHeat = 0;
                                        }, "Set heat to 0");
                                    }else{
                                        if(br.irradiatorEfficiency<0)validator.error("Overhaul MSR block recipe irradiator efficiency must not be negative!").solve(() -> {
                                            br.irradiatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.irradiatorHeat<0)validator.error("Overhaul MSR block recipe irradiator heat must not be negative!").solve(() -> {
                                            br.irradiatorHeat = 0;
                                        }, "Set heat to 0");
                                    }
                                    if(!Float.isFinite(br.irradiatorEfficiency))validator.error("Overhaul MSR block recipe irradiator efficiency is not finite!").solve(() -> {
                                        br.irradiatorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!Float.isFinite(br.irradiatorHeat))validator.error("Overhaul MSR block recipe irradiator heat is not finite!").solve(() -> {
                                        br.irradiatorHeat = 0;
                                    }, "Set heat to 0");
                                    if(!b.reflector){
                                        if(br.reflectorEfficiency!=0)validator.error("Overhaul MSR block recipe has reflector efficiency, but is not for a reflector!").solve(() -> {
                                            br.reflectorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.reflectorReflectivity!=0)validator.error("Overhaul MSR block recipe has reflector reflectivity, but is not for a reflector!").solve(() -> {
                                            br.reflectorReflectivity = 0;
                                        }, "Set reflectivity to 0");
                                    }else{
                                        if(br.reflectorEfficiency<0)validator.error("Overhaul MSR block recipe reflector efficiency must not be negative!").solve(() -> {
                                            br.reflectorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.reflectorReflectivity<0)validator.error("Overhaul MSR block recipe reflector reflectivity must not be negative!").solve(() -> {
                                            br.reflectorReflectivity = 0;
                                        }, "Set reflectivity to 0");
                                    }
                                    if(!Float.isFinite(br.reflectorEfficiency))validator.error("Overhaul MSR block recipe reflector efficiency is not finite!").solve(() -> {
                                        br.reflectorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!Float.isFinite(br.reflectorReflectivity))validator.error("Overhaul MSR block recipe reflector reflectivity is not finite!").solve(() -> {
                                        br.reflectorReflectivity = 0;
                                    }, "Set reflectivity to 0");
                                    if(!b.moderator){
                                        if(br.moderatorEfficiency!=0)validator.error("Overhaul MSR block recipe has moderator efficiency, but is not for a moderator!").solve(() -> {
                                            br.moderatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.moderatorFlux!=0)validator.error("Overhaul MSR block recipe has moderator flux, but is not for a moderator!").solve(() -> {
                                            br.moderatorFlux = 0;
                                        }, "Set flux to 0");
                                        if(br.moderatorActive)validator.error("Overhaul MSR block recipe is moderator active, but is not for a moderator!").solve(() -> {
                                            br.moderatorActive = false;
                                        }, "Set active to false");
                                    }else{
                                        if(br.moderatorEfficiency<0)validator.error("Overhaul MSR block recipe moderator efficiency must not be negative!").solve(() -> {
                                            br.moderatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.moderatorFlux<0)validator.error("Overhaul MSR block recipe moderator flux must not be negative!").solve(() -> {
                                            br.moderatorFlux = 0;
                                        }, "Set flux to 0");
                                    }
                                    if(!Float.isFinite(br.moderatorEfficiency))validator.error("Overhaul MSR block recipe moderator efficiency is not finite!").solve(() -> {
                                        br.moderatorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!b.shield){
                                        if(br.shieldEfficiency!=0)validator.error("Overhaul MSR block recipe has shield efficiency, but is not for a shield!").solve(() -> {
                                            br.shieldEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.shieldHeat!=0)validator.error("Overhaul MSR block recipe has shield heat, but is not for a shield!").solve(() -> {
                                            br.shieldHeat = 0;
                                        }, "Set heat to 0");
                                    }else{
                                        if(br.shieldEfficiency<0)validator.error("Overhaul MSR block recipe shield efficiency must not be negative!").solve(() -> {
                                            br.shieldEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.shieldHeat<0)validator.error("Overhaul MSR block recipe shield heat must not be negative!").solve(() -> {
                                            br.shieldHeat = 0;
                                        }, "Set heat to 0");
                                    }
                                    if(!Float.isFinite(br.shieldEfficiency))validator.error("Overhaul MSR block recipe shield efficiency is not finite!").solve(() -> {
                                        br.shieldEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!b.heater){
                                        if(br.heaterCooling!=0)validator.error("Overhaul MSR block recipe has heater cooling, but is not for a heater!").solve(() -> {
                                            br.heaterCooling = 0;
                                        }, "Set cooling to 0");
                                    }
                                }
                                //</editor-fold>
                            }else{
                                //<editor-fold defaultstate="collapsed" desc="Block Recipes">
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe br : b.recipes){
                                    Runnable blockRecipeConfig = () -> {
                                        gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr.MenuBlockRecipeConfiguration(gui, this, configuration, b, br));
                                    };
                                    //<editor-fold defaultstate="collapsed" desc="Input Name">
                                    if(br.inputName==null){
                                        validator.error("Overhaul MSR block recipe input name is null!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    }else{
                                        if(br.inputName.contains(":"))validator.error("Overhaul MSR block recipe input name is invalid! ("+br.inputName+")").hint("Fluid name must be the name of the fluid! (ex. water, not fluid:water or minecraft:water)").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        for(char ch : br.inputName.toCharArray()){
                                            if(namespaceChars.indexOf(ch)==-1){
                                                validator.error("Overhaul MSR block recipe input name is invalid! ("+br.inputName+")").hint("Fluid names can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame fluid.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                            }
                                        }
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe c2 : b.recipes){
                                            if(br==c2)continue;
                                            if(br.inputName.equals(c2.inputName))validator.error("Overhaul MSR block recipe input name is not unique! ("+br.inputName+")").hint("This configuration contains multiple block recipes with the same name.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        }
                                    }
                                    //</editor-fold>
                                    //<editor-fold defaultstate="collapsed" desc="Input Display name">
                                    if(br.inputDisplayName==null||br.inputDisplayName.isEmpty()){
                                        validator.warn("Overhaul MSR block recipe has no display name!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    }
                                    //</editor-fold>
                                    //<editor-fold defaultstate="collapsed" desc="Output Name">
                                    if(br.outputName==null){
                                        validator.error("Overhaul MSR block recipe output name is null!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    }else{
                                        if(br.outputName.contains(":"))validator.error("Overhaul MSR block recipe output name is invalid! ("+br.outputName+")").hint("Fluid name must be the name of the fluid! (ex. water, not fluid:water or minecraft:water)").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        for(char ch : br.outputName.toCharArray()){
                                            if(namespaceChars.indexOf(ch)==-1){
                                                validator.error("Overhaul MSR block recipe output name is invalid! ("+br.outputName+")").hint("Fluid names can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame fluid.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                            }
                                        }
                                    }
                                    //</editor-fold>
                                    //<editor-fold defaultstate="collapsed" desc="Output Display name">
                                    if(br.outputDisplayName==null||br.outputDisplayName.isEmpty()){
                                        validator.warn("Overhaul MSR block recipe has no display name!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    }
                                    //</editor-fold>
                                    //<editor-fold defaultstate="collapsed" desc="Legacy names">
                                    for(Configuration cf : Configuration.configurations){
                                        if(cf.overhaul!=null&&cf.overhaul.fissionMSR!=null){
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : cf.overhaul.fissionMSR.blocks){
                                                if(!b.name.equals(b2.name))continue;
                                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r2 : b2.recipes){
                                                    if(br.inputName.equals(r2.inputName)){
                                                        if(!br.inputLegacyNames.equals(r2.inputLegacyNames))validator.warn("Overhaul MSR block recipe "+br.inputName+" of block "+b.name+" legacy names do not match!").hint("Another block was found in Configuration "+cf.name+" with different legacy names.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                    }else{
                                                        for(String s : br.inputLegacyNames)if(r2.inputLegacyNames.contains(s))validator.warn("Overhaul MSR block recipe "+br.inputName+" of block "+b.name+" legacy names are not unique! ("+s+")").hint("Another block was found in configuration "+cf.name+" with the same legacy name. These blocks could be confused when loading old files").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    for(AddonConfiguration cf : Configuration.internalAddonCache.values()){
                                        if(cf.self.overhaul!=null&&cf.self.overhaul.fissionMSR!=null){
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : cf.self.overhaul.fissionMSR.blocks){
                                                if(!b.name.equals(b2.name))continue;
                                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r2 : b2.recipes){
                                                    if(br.inputName.equals(r2.inputName)&&!br.inputLegacyNames.equals(r2.inputLegacyNames)){
                                                        validator.warn("Overhaul MSR block recipe "+br.inputName+" of block "+b.name+" legacy names do not match! ("+br.inputName+")").hint("Another block was found in Addon "+cf.name+" with different legacy names.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //</editor-fold>
                                    if(br.inputTexture==null)validator.warn("Overhaul MSR block recipe "+br.inputName+" of block "+b.name+" has no input texture!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    if(br.outputTexture==null)validator.warn("Overhaul MSR block recipe "+br.outputName+" of block "+b.name+" has no output texture!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                    if(!b.fuelVessel){
                                        if(br.fuelVesselEfficiency!=0)validator.error("Overhaul MSR block recipe has fuel vessel efficiency, but is not for a fuel vessel!").solve(() -> {
                                            br.fuelVesselEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.fuelVesselHeat!=0)validator.error("Overhaul MSR block recipe has fuel vessel heat, but is not for a fuel vessel!").solve(() -> {
                                            br.fuelVesselHeat = 0;
                                        }, "Set heat to 0");
                                        if(br.fuelVesselCriticality!=0)validator.error("Overhaul MSR block recipe has fuel vessel criticality, but is not for a fuel vessel!").solve(() -> {
                                            br.fuelVesselCriticality = 0;
                                        }, "Set criticality to 0");
                                        if(br.fuelVesselSelfPriming)validator.error("Overhaul MSR block recipe is fuel vessel self priming, but is not for a fuel vessel!").solve(() -> {
                                            br.fuelVesselSelfPriming = false;
                                        }, "Set self priming to false");
                                    }else{
                                        if(br.fuelVesselEfficiency<0)validator.error("Overhaul MSR block recipe fuel vessel efficiency must not be negative!").solve(() -> {
                                            br.fuelVesselEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.fuelVesselHeat<0)validator.error("Overhaul MSR block recipe fuel vessel heat must not be negative!").solve(() -> {
                                            br.fuelVesselHeat = 0;
                                        }, "Set heat to 0");
                                        if(br.fuelVesselCriticality<0)validator.error("Overhaul MSR block recipe fuel vessel criticality must not be negative!").solve(() -> {
                                            br.fuelVesselCriticality = 0;
                                        }, "Set criticality to 0");
                                    }
                                    if(!Float.isFinite(br.fuelVesselEfficiency))validator.error("Overhaul MSR block recipe fuel vessel efficiency is not finite!").solve(() -> {
                                        br.fuelVesselEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!b.irradiator){
                                        if(br.irradiatorEfficiency!=0)validator.error("Overhaul MSR block recipe has irradiator efficiency, but is not for an irradiator!").solve(() -> {
                                            br.irradiatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.irradiatorHeat!=0)validator.error("Overhaul MSR block recipe has irradiator heat, but is not for an irradiator!").solve(() -> {
                                            br.irradiatorHeat = 0;
                                        }, "Set heat to 0");
                                    }else{
                                        if(br.irradiatorEfficiency<0)validator.error("Overhaul MSR block recipe irradiator efficiency must not be negative!").solve(() -> {
                                            br.irradiatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.irradiatorHeat<0)validator.error("Overhaul MSR block recipe irradiator heat must not be negative!").solve(() -> {
                                            br.irradiatorHeat = 0;
                                        }, "Set heat to 0");
                                    }
                                    if(!Float.isFinite(br.irradiatorEfficiency))validator.error("Overhaul MSR block recipe irradiator efficiency is not finite!").solve(() -> {
                                        br.irradiatorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!Float.isFinite(br.irradiatorHeat))validator.error("Overhaul MSR block recipe irradiator heat is not finite!").solve(() -> {
                                        br.irradiatorHeat = 0;
                                    }, "Set heat to 0");
                                    if(!b.reflector){
                                        if(br.reflectorEfficiency!=0)validator.error("Overhaul MSR block recipe has reflector efficiency, but is not for a reflector!").solve(() -> {
                                            br.reflectorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.reflectorReflectivity!=0)validator.error("Overhaul MSR block recipe has reflector reflectivity, but is not for a reflector!").solve(() -> {
                                            br.reflectorReflectivity = 0;
                                        }, "Set reflectivity to 0");
                                    }else{
                                        if(br.reflectorEfficiency<0)validator.error("Overhaul MSR block recipe reflector efficiency must not be negative!").solve(() -> {
                                            br.reflectorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.reflectorReflectivity<0)validator.error("Overhaul MSR block recipe reflector reflectivity must not be negative!").solve(() -> {
                                            br.reflectorReflectivity = 0;
                                        }, "Set reflectivity to 0");
                                    }
                                    if(!Float.isFinite(br.reflectorEfficiency))validator.error("Overhaul MSR block recipe reflector efficiency is not finite!").solve(() -> {
                                        br.reflectorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!Float.isFinite(br.reflectorReflectivity))validator.error("Overhaul MSR block recipe reflector reflectivity is not finite!").solve(() -> {
                                        br.reflectorReflectivity = 0;
                                    }, "Set reflectivity to 0");
                                    if(!b.moderator){
                                        if(br.moderatorEfficiency!=0)validator.error("Overhaul MSR block recipe has moderator efficiency, but is not for a moderator!").solve(() -> {
                                            br.moderatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.moderatorFlux!=0)validator.error("Overhaul MSR block recipe has moderator flux, but is not for a moderator!").solve(() -> {
                                            br.moderatorFlux = 0;
                                        }, "Set flux to 0");
                                        if(br.moderatorActive)validator.error("Overhaul MSR block recipe is moderator active, but is not for a moderator!").solve(() -> {
                                            br.moderatorActive = false;
                                        }, "Set active to false");
                                    }else{
                                        if(br.moderatorEfficiency<0)validator.error("Overhaul MSR block recipe moderator efficiency must not be negative!").solve(() -> {
                                            br.moderatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.moderatorFlux<0)validator.error("Overhaul MSR block recipe moderator flux must not be negative!").solve(() -> {
                                            br.moderatorFlux = 0;
                                        }, "Set flux to 0");
                                    }
                                    if(!Float.isFinite(br.moderatorEfficiency))validator.error("Overhaul MSR block recipe moderator efficiency is not finite!").solve(() -> {
                                        br.moderatorEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!b.shield){
                                        if(br.shieldEfficiency!=0)validator.error("Overhaul MSR block recipe has shield efficiency, but is not for a shield!").solve(() -> {
                                            br.shieldEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.shieldHeat!=0)validator.error("Overhaul MSR block recipe has shield heat, but is not for a shield!").solve(() -> {
                                            br.shieldHeat = 0;
                                        }, "Set heat to 0");
                                    }else{
                                        if(br.shieldEfficiency<0)validator.error("Overhaul MSR block recipe shield efficiency must not be negative!").solve(() -> {
                                            br.shieldEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(br.shieldHeat<0)validator.error("Overhaul MSR block recipe shield heat must not be negative!").solve(() -> {
                                            br.shieldHeat = 0;
                                        }, "Set heat to 0");
                                    }
                                    if(!Float.isFinite(br.shieldEfficiency))validator.error("Overhaul MSR block recipe shield efficiency is not finite!").solve(() -> {
                                        br.shieldEfficiency = 0;
                                    }, "Set efficiency to 0");
                                    if(!b.heater){
                                        if(br.heaterCooling!=0)validator.error("Overhaul MSR block recipe has heater cooling, but is not for a heater!").solve(() -> {
                                            br.heaterCooling = 0;
                                        }, "Set cooling to 0");
                                    }
                                }
                                //</editor-fold>
                            }
                        }
                        //</editor-fold>
                        if(configuration.addon){
                            //<editor-fold defaultstate="collapsed" desc="Addon Blocks">
                            validator.stage("Checking Addon blocks...");
                            for(int i = 0; i<msr.allBlocks.size(); i++){
                                validator.stage("Checking Addon blocks... ("+(i+1)+"/"+msr.allBlocks.size()+")");
                                net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b = msr.allBlocks.get(i);
                                Runnable blockConfig = () -> {
                                    gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr.MenuBlockConfiguration(gui, this, configuration, b));
                                };
                                //<editor-fold defaultstate="collapsed" desc="Name">
                                if(b.name==null){
                                    validator.error("Overhaul MSR addon block name is null!").solve(blockConfig, "Go to Block configuration");
                                }else{
                                    net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block match = null;
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : Core.configuration.overhaul.fissionMSR.allBlocks){
                                        if(b.name.equals(b2.name))match = b2;
                                    }
                                    if(match==null)validator.error("Overhaul MSR addon block "+b.name+" does not match any blocks in the parent configuration!").solve(blockConfig, "Go to Block configuration");
                                    else{
                                        if(match.fuelVessel != b.fuelVessel)validator.error("Overhaul MSR addon block "+b.name+" setting Fuel Vessel does not match that of the corresponding block in the parent configuration!").solve(blockConfig, "Go to Block Configuration");
                                        if(match.moderator != b.moderator)validator.error("Overhaul MSR addon block "+b.name+" setting Moderator does not match that of the corresponding block in the parent configuration!").solve(blockConfig, "Go to Block Configuration");
                                        if(match.reflector != b.reflector)validator.error("Overhaul MSR addon block "+b.name+" setting Reflector does not match that of the corresponding block in the parent configuration!").solve(blockConfig, "Go to Block Configuration");
                                        if(match.irradiator != b.irradiator)validator.error("Overhaul MSR addon block "+b.name+" setting Irradiator does not match that of the corresponding block in the parent configuration!").solve(blockConfig, "Go to Block Configuration");
                                        if(match.heater != b.heater)validator.error("Overhaul MSR addon block "+b.name+" setting Heater does not match that of the corresponding block in the parent configuration!").solve(blockConfig, "Go to Block Configuration");
                                        if(match.shield != b.shield)validator.error("Overhaul MSR addon block "+b.name+" setting Shield does not match that of the corresponding block in the parent configuration!").solve(blockConfig, "Go to Block Configuration");
                                    }
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : msr.allBlocks){
                                        if(b==b2)continue;
                                        if(b.name.equals(b2.name))validator.error("Overhaul MSR addon block "+b.name+" is duplicated!").hint("This configuration contains multiple of the same addon blocks.").solve(blockConfig, "Go to Block configuration");
                                    }
                                }
                                //</editor-fold>
                                if(b.irradiator){
                                    //<editor-fold defaultstate="collapsed" desc="Block Recipes">
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe br : b.recipes){
                                        Runnable blockRecipeConfig = () -> {
                                            gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr.MenuBlockRecipeConfiguration(gui, this, configuration, b, br));
                                        };
                                        //<editor-fold defaultstate="collapsed" desc="Input Name">
                                        if(br.inputName==null){
                                            validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" input name is null!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        }else{
                                            String[] split = br.inputName.split("\\:");
                                            if(split.length<2||split.length>3){
                                                validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" input name is invalid! ("+br.inputName+")").hint("Item input name must be a namespaced ID! (namespace:inputname or namespace:inputname:metadata").solve(blockConfig, "Go to Block Recipe configuration");
                                            }else{
                                                for(char c : split[0].toCharArray()){
                                                    if(namespaceChars.indexOf(c)==-1){
                                                        validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" namespace is invalid! ("+split[0]+")").hint("Namespaces can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                                    }
                                                }
                                                for(char c : split[1].toCharArray()){
                                                    if(namespacePathChars.indexOf(c)==-1){
                                                        validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" input name is invalid! ("+split[1]+")").hint("block recipe input names can only contain characters 0-9, a-z, _, -, ., and /! This should be the input name of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                                    }
                                                }
                                                if(split.length==3){
                                                    try{
                                                        Integer.parseInt(split[2]);
                                                    }catch(Exception ex){
                                                        validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" metadata is invalid! ("+split[2]+")").hint("Metadata must be an integer! If the ingame block recipe does not have metadata, use only namespace:input name.").solve(blockConfig, "Go to Block Recipe configuration");
                                                    }
                                                }
                                            }
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe c2 : b.recipes){
                                                if(br==c2)continue;
                                                if(br.inputName.equals(c2.inputName))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" input name is not unique! ("+br.inputName+")").hint("This block contains multiple block recipes with the same name.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                            }
                                        }
                                        //</editor-fold>
                                        //<editor-fold defaultstate="collapsed" desc="Input Display name">
                                        if(br.inputDisplayName==null||br.inputDisplayName.isEmpty()){
                                            validator.warn("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has no display name!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        }
                                        //</editor-fold>
                                        //<editor-fold defaultstate="collapsed" desc="Output Name">
                                        if(br.outputName==null){
                                            validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" output name is null!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        }else{
                                            String[] split = br.outputName.split("\\:");
                                            if(split.length<2||split.length>3){
                                                validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" output name is invalid! ("+br.outputName+")").hint("Item output name must be a namespaced ID! (namespace:outputname or namespace:outputname:metadata").solve(blockConfig, "Go to Block Recipe configuration");
                                            }else{
                                                for(char c : split[0].toCharArray()){
                                                    if(namespaceChars.indexOf(c)==-1){
                                                        validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" namespace is invalid! ("+split[0]+")").hint("Namespaces can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                                    }
                                                }
                                                for(char c : split[1].toCharArray()){
                                                    if(namespacePathChars.indexOf(c)==-1){
                                                        validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" output name is invalid! ("+split[1]+")").hint("block recipe output names can only contain characters 0-9, a-z, _, -, ., and /! This should be the output name of the ingame block or item.").solve(blockConfig, "Go to Block Recipe configuration");
                                                    }
                                                }
                                                if(split.length==3){
                                                    try{
                                                        Integer.parseInt(split[2]);
                                                    }catch(Exception ex){
                                                        validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" metadata is invalid! ("+split[2]+")").hint("Metadata must be an integer! If the ingame block recipe does not have metadata, use only namespace:output name.").solve(blockConfig, "Go to Block Recipe configuration");
                                                    }
                                                }
                                            }
                                        }
                                        //</editor-fold>
                                        //<editor-fold defaultstate="collapsed" desc="Output Display name">
                                        if(br.outputDisplayName==null||br.outputDisplayName.isEmpty()){
                                            validator.warn("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has no display name!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        }
                                        //</editor-fold>
                                        //<editor-fold defaultstate="collapsed" desc="Legacy names">
                                        for(Configuration cf : Configuration.configurations){
                                            if(cf.overhaul!=null&&cf.overhaul.fissionMSR!=null){
                                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : cf.overhaul.fissionMSR.blocks){
                                                    if(!b.name.equals(b2.name))continue;
                                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r2 : b2.recipes){
                                                        if(br.inputName.equals(r2.inputName)){
                                                            if(!br.inputLegacyNames.equals(r2.inputLegacyNames))validator.warn("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" legacy names do not match! ("+br.inputName+")").hint("Another block recipe was found in block "+b2.name+" of Configuration "+cf.name+" with different legacy names.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                        }else{
                                                            for(String s : br.inputLegacyNames)if(r2.inputLegacyNames.contains(s))validator.warn("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" legacy names are not unique! ("+s+")").hint("Another block recipe was found in block "+b2.name+" of configuration "+cf.name+" with the same legacy name. These blocks could be confused when loading old files").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        for(AddonConfiguration cf : Configuration.internalAddonCache.values()){
                                            if(cf.self.overhaul!=null&&cf.self.overhaul.fissionMSR!=null){
                                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : cf.self.overhaul.fissionMSR.blocks){
                                                    if(!b.name.equals(b2.name))continue;
                                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r2 : b2.recipes){
                                                        if(br.inputName.equals(r2.inputName)&&!br.inputLegacyNames.equals(r2.inputLegacyNames)){
                                                            validator.warn("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" legacy names do not match! ("+br.inputName+")").hint("Another block recipe was found in block "+b2.name+" of Configuration "+cf.name+" with different legacy names.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        //</editor-fold>
                                        if(br.inputTexture==null)validator.warn("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has no input texture!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        if(br.outputTexture==null)validator.warn("Overhaul MSR addon block recipe "+br.outputName+" of block "+b.name+" has no output texture!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        if(!b.fuelVessel){
                                            if(br.fuelVesselEfficiency!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has fuel vessel efficiency, but is not for a fuel vessel!").solve(() -> {
                                                br.fuelVesselEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.fuelVesselHeat!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has fuel vessel heat, but is not for a fuel vessel!").solve(() -> {
                                                br.fuelVesselHeat = 0;
                                            }, "Set heat to 0");
                                            if(br.fuelVesselCriticality!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has fuel vessel criticality, but is not for a fuel vessel!").solve(() -> {
                                                br.fuelVesselCriticality = 0;
                                            }, "Set criticality to 0");
                                            if(br.fuelVesselSelfPriming)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" is fuel vessel self priming, but is not for a fuel vessel!").solve(() -> {
                                                br.fuelVesselSelfPriming = false;
                                            }, "Set self priming to false");
                                        }else{
                                            if(br.fuelVesselEfficiency<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" fuel vessel efficiency must not be negative!").solve(() -> {
                                                br.fuelVesselEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.fuelVesselHeat<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" fuel vessel heat must not be negative!").solve(() -> {
                                                br.fuelVesselHeat = 0;
                                            }, "Set heat to 0");
                                            if(br.fuelVesselCriticality<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" fuel vessel criticality must not be negative!").solve(() -> {
                                                br.fuelVesselCriticality = 0;
                                            }, "Set criticality to 0");
                                        }
                                        if(!Float.isFinite(br.fuelVesselEfficiency))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" fuel vessel efficiency is not finite!").solve(() -> {
                                            br.fuelVesselEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(!b.irradiator){
                                            if(br.irradiatorEfficiency!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has irradiator efficiency, but is not for an irradiator!").solve(() -> {
                                                br.irradiatorEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.irradiatorHeat!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has irradiator heat, but is not for an irradiator!").solve(() -> {
                                                br.irradiatorHeat = 0;
                                            }, "Set heat to 0");
                                        }else{
                                            if(br.irradiatorEfficiency<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" irradiator efficiency must not be negative!").solve(() -> {
                                                br.irradiatorEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.irradiatorHeat<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" irradiator heat must not be negative!").solve(() -> {
                                                br.irradiatorHeat = 0;
                                            }, "Set heat to 0");
                                        }
                                        if(!Float.isFinite(br.irradiatorEfficiency))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" irradiator efficiency is not finite!").solve(() -> {
                                            br.irradiatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(!Float.isFinite(br.irradiatorHeat))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" irradiator heat is not finite!").solve(() -> {
                                            br.irradiatorHeat = 0;
                                        }, "Set heat to 0");
                                        if(!b.reflector){
                                            if(br.reflectorEfficiency!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has reflector efficiency, but is not for a reflector!").solve(() -> {
                                                br.reflectorEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.reflectorReflectivity!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has reflector reflectivity, but is not for a reflector!").solve(() -> {
                                                br.reflectorReflectivity = 0;
                                            }, "Set reflectivity to 0");
                                        }else{
                                            if(br.reflectorEfficiency<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" reflector efficiency must not be negative!").solve(() -> {
                                                br.reflectorEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.reflectorReflectivity<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" reflector reflectivity must not be negative!").solve(() -> {
                                                br.reflectorReflectivity = 0;
                                            }, "Set reflectivity to 0");
                                        }
                                        if(!Float.isFinite(br.reflectorEfficiency))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" reflector efficiency is not finite!").solve(() -> {
                                            br.reflectorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(!Float.isFinite(br.reflectorReflectivity))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" reflector reflectivity is not finite!").solve(() -> {
                                            br.reflectorReflectivity = 0;
                                        }, "Set reflectivity to 0");
                                        if(!b.moderator){
                                            if(br.moderatorEfficiency!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has moderator efficiency, but is not for a moderator!").solve(() -> {
                                                br.moderatorEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.moderatorFlux!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has moderator flux, but is not for a moderator!").solve(() -> {
                                                br.moderatorFlux = 0;
                                            }, "Set flux to 0");
                                            if(br.moderatorActive)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" is moderator active, but is not for a moderator!").solve(() -> {
                                                br.moderatorActive = false;
                                            }, "Set active to false");
                                        }else{
                                            if(br.moderatorEfficiency<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" moderator efficiency must not be negative!").solve(() -> {
                                                br.moderatorEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.moderatorFlux<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" moderator flux must not be negative!").solve(() -> {
                                                br.moderatorFlux = 0;
                                            }, "Set flux to 0");
                                        }
                                        if(!Float.isFinite(br.moderatorEfficiency))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" moderator efficiency is not finite!").solve(() -> {
                                            br.moderatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(!b.shield){
                                            if(br.shieldEfficiency!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has shield efficiency, but is not for a shield!").solve(() -> {
                                                br.shieldEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.shieldHeat!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has shield heat, but is not for a shield!").solve(() -> {
                                                br.shieldHeat = 0;
                                            }, "Set heat to 0");
                                        }else{
                                            if(br.shieldEfficiency<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" shield efficiency must not be negative!").solve(() -> {
                                                br.shieldEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.shieldHeat<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" shield heat must not be negative!").solve(() -> {
                                                br.shieldHeat = 0;
                                            }, "Set heat to 0");
                                        }
                                        if(!Float.isFinite(br.shieldEfficiency))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" shield efficiency is not finite!").solve(() -> {
                                            br.shieldEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(!b.heater){
                                            if(br.heaterCooling!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has heater cooling, but is not for a heater!").solve(() -> {
                                                br.heaterCooling = 0;
                                            }, "Set cooling to 0");
                                        }
                                    }
                                    //</editor-fold>
                                }else{
                                    //<editor-fold defaultstate="collapsed" desc="Block Recipes">
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe br : b.recipes){
                                        Runnable blockRecipeConfig = () -> {
                                            gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr.MenuBlockRecipeConfiguration(gui, this, configuration, b, br));
                                        };
                                        //<editor-fold defaultstate="collapsed" desc="Input Name">
                                        if(br.inputName==null){
                                            validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" input name is null!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        }else{
                                            if(br.inputName.contains(":"))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" input name is invalid! ("+br.inputName+")").hint("Fluid name must be the name of the fluid! (ex. water, not fluid:water or minecraft:water)").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                            for(char ch : br.inputName.toCharArray()){
                                                if(namespaceChars.indexOf(ch)==-1){
                                                    validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" input name is invalid! ("+br.inputName+")").hint("Fluid names can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame fluid.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe c2 : b.recipes){
                                                if(br==c2)continue;
                                                if(br.inputName.equals(c2.inputName))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" input name is not unique! ("+br.inputName+")").hint("This configuration contains multiple block recipes with the same name.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                            }
                                        }
                                        //</editor-fold>
                                        //<editor-fold defaultstate="collapsed" desc="Input Display name">
                                        if(br.inputDisplayName==null||br.inputDisplayName.isEmpty()){
                                            validator.warn("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has no display name!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        }
                                        //</editor-fold>
                                        //<editor-fold defaultstate="collapsed" desc="Output Name">
                                        if(br.outputName==null){
                                            validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" output name is null!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        }else{
                                            if(br.outputName.contains(":"))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" output name is invalid! ("+br.outputName+")").hint("Fluid name must be the name of the fluid! (ex. water, not fluid:water or minecraft:water)").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                            for(char ch : br.outputName.toCharArray()){
                                                if(namespaceChars.indexOf(ch)==-1){
                                                    validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" output name is invalid! ("+br.outputName+")").hint("Fluid names can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame fluid.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                }
                                            }
                                        }
                                        //</editor-fold>
                                        //<editor-fold defaultstate="collapsed" desc="Output Display name">
                                        if(br.outputDisplayName==null||br.outputDisplayName.isEmpty()){
                                            validator.warn("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has no display name!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        }
                                        //</editor-fold>
                                        //<editor-fold defaultstate="collapsed" desc="Legacy names">
                                        for(Configuration cf : Configuration.configurations){
                                            if(cf.overhaul!=null&&cf.overhaul.fissionMSR!=null){
                                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : cf.overhaul.fissionMSR.blocks){
                                                    if(!b.name.equals(b2.name))continue;
                                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r2 : b2.recipes){
                                                        if(br.inputName.equals(r2.inputName)){
                                                            if(!br.inputLegacyNames.equals(r2.inputLegacyNames))validator.warn("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" legacy names do not match!").hint("Another block was found in Configuration "+cf.name+" with different legacy names.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                        }else{
                                                            for(String s : br.inputLegacyNames)if(r2.inputLegacyNames.contains(s))validator.warn("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" legacy names are not unique! ("+s+")").hint("Another block was found in configuration "+cf.name+" with the same legacy name. These blocks could be confused when loading old files").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        for(AddonConfiguration cf : Configuration.internalAddonCache.values()){
                                            if(cf.self.overhaul!=null&&cf.self.overhaul.fissionMSR!=null){
                                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : cf.self.overhaul.fissionMSR.blocks){
                                                    if(!b.name.equals(b2.name))continue;
                                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r2 : b2.recipes){
                                                        if(br.inputName.equals(r2.inputName)&&!br.inputLegacyNames.equals(r2.inputLegacyNames)){
                                                            validator.warn("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" legacy names do not match! ("+br.inputName+")").hint("Another block was found in Addon "+cf.name+" with different legacy names.").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        //</editor-fold>
                                        if(br.inputTexture==null)validator.warn("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has no input texture!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        if(br.outputTexture==null)validator.warn("Overhaul MSR addon block recipe "+br.outputName+" of block "+b.name+" has no output texture!").solve(blockRecipeConfig, "Go to Block Recipe configuration");
                                        if(!b.fuelVessel){
                                            if(br.fuelVesselEfficiency!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has fuel vessel efficiency, but is not for a fuel vessel!").solve(() -> {
                                                br.fuelVesselEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.fuelVesselHeat!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has fuel vessel heat, but is not for a fuel vessel!").solve(() -> {
                                                br.fuelVesselHeat = 0;
                                            }, "Set heat to 0");
                                            if(br.fuelVesselCriticality!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has fuel vessel criticality, but is not for a fuel vessel!").solve(() -> {
                                                br.fuelVesselCriticality = 0;
                                            }, "Set criticality to 0");
                                            if(br.fuelVesselSelfPriming)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" is fuel vessel self priming, but is not for a fuel vessel!").solve(() -> {
                                                br.fuelVesselSelfPriming = false;
                                            }, "Set self priming to false");
                                        }else{
                                            if(br.fuelVesselEfficiency<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" fuel vessel efficiency must not be negative!").solve(() -> {
                                                br.fuelVesselEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.fuelVesselHeat<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" fuel vessel heat must not be negative!").solve(() -> {
                                                br.fuelVesselHeat = 0;
                                            }, "Set heat to 0");
                                            if(br.fuelVesselCriticality<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" fuel vessel criticality must not be negative!").solve(() -> {
                                                br.fuelVesselCriticality = 0;
                                            }, "Set criticality to 0");
                                        }
                                        if(!Float.isFinite(br.fuelVesselEfficiency))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" fuel vessel efficiency is not finite!").solve(() -> {
                                            br.fuelVesselEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(!b.irradiator){
                                            if(br.irradiatorEfficiency!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has irradiator efficiency, but is not for an irradiator!").solve(() -> {
                                                br.irradiatorEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.irradiatorHeat!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has irradiator heat, but is not for an irradiator!").solve(() -> {
                                                br.irradiatorHeat = 0;
                                            }, "Set heat to 0");
                                        }else{
                                            if(br.irradiatorEfficiency<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" irradiator efficiency must not be negative!").solve(() -> {
                                                br.irradiatorEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.irradiatorHeat<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" irradiator heat must not be negative!").solve(() -> {
                                                br.irradiatorHeat = 0;
                                            }, "Set heat to 0");
                                        }
                                        if(!Float.isFinite(br.irradiatorEfficiency))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" irradiator efficiency is not finite!").solve(() -> {
                                            br.irradiatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(!Float.isFinite(br.irradiatorHeat))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" irradiator heat is not finite!").solve(() -> {
                                            br.irradiatorHeat = 0;
                                        }, "Set heat to 0");
                                        if(!b.reflector){
                                            if(br.reflectorEfficiency!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has reflector efficiency, but is not for a reflector!").solve(() -> {
                                                br.reflectorEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.reflectorReflectivity!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has reflector reflectivity, but is not for a reflector!").solve(() -> {
                                                br.reflectorReflectivity = 0;
                                            }, "Set reflectivity to 0");
                                        }else{
                                            if(br.reflectorEfficiency<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" reflector efficiency must not be negative!").solve(() -> {
                                                br.reflectorEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.reflectorReflectivity<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" reflector reflectivity must not be negative!").solve(() -> {
                                                br.reflectorReflectivity = 0;
                                            }, "Set reflectivity to 0");
                                        }
                                        if(!Float.isFinite(br.reflectorEfficiency))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" reflector efficiency is not finite!").solve(() -> {
                                            br.reflectorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(!Float.isFinite(br.reflectorReflectivity))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" reflector reflectivity is not finite!").solve(() -> {
                                            br.reflectorReflectivity = 0;
                                        }, "Set reflectivity to 0");
                                        if(!b.moderator){
                                            if(br.moderatorEfficiency!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has moderator efficiency, but is not for a moderator!").solve(() -> {
                                                br.moderatorEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.moderatorFlux!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has moderator flux, but is not for a moderator!").solve(() -> {
                                                br.moderatorFlux = 0;
                                            }, "Set flux to 0");
                                            if(br.moderatorActive)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" is moderator active, but is not for a moderator!").solve(() -> {
                                                br.moderatorActive = false;
                                            }, "Set active to false");
                                        }else{
                                            if(br.moderatorEfficiency<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" moderator efficiency must not be negative!").solve(() -> {
                                                br.moderatorEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.moderatorFlux<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" moderator flux must not be negative!").solve(() -> {
                                                br.moderatorFlux = 0;
                                            }, "Set flux to 0");
                                        }
                                        if(!Float.isFinite(br.moderatorEfficiency))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" moderator efficiency is not finite!").solve(() -> {
                                            br.moderatorEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(!b.shield){
                                            if(br.shieldEfficiency!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has shield efficiency, but is not for a shield!").solve(() -> {
                                                br.shieldEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.shieldHeat!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has shield heat, but is not for a shield!").solve(() -> {
                                                br.shieldHeat = 0;
                                            }, "Set heat to 0");
                                        }else{
                                            if(br.shieldEfficiency<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" shield efficiency must not be negative!").solve(() -> {
                                                br.shieldEfficiency = 0;
                                            }, "Set efficiency to 0");
                                            if(br.shieldHeat<0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" shield heat must not be negative!").solve(() -> {
                                                br.shieldHeat = 0;
                                            }, "Set heat to 0");
                                        }
                                        if(!Float.isFinite(br.shieldEfficiency))validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" shield efficiency is not finite!").solve(() -> {
                                            br.shieldEfficiency = 0;
                                        }, "Set efficiency to 0");
                                        if(!b.heater){
                                            if(br.heaterCooling!=0)validator.error("Overhaul MSR addon block recipe "+br.inputName+" of block "+b.name+" has heater cooling, but is not for a heater!").solve(() -> {
                                                br.heaterCooling = 0;
                                            }, "Set cooling to 0");
                                        }
                                    }
                                    //</editor-fold>
                                }
                            }
                            //</editor-fold>
                        }
                    }
                    if(overhaul.turbine!=null){
                        validator.stage("Checking overhaul Turbine configuration");
                        net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.TurbineConfiguration turbine = overhaul.turbine;
                        if(configuration.addon){
                            //<editor-fold defaultstate="collapsed" desc="Size">
                            if(turbine.minWidth!=0)validator.error("Overhaul Turbine minimum width must be zero! ("+turbine.minWidth+")").solve(() -> {
                                turbine.minWidth = 0;
                            }, "Set minimum width to 0");
                            if(turbine.minLength!=0)validator.error("Overhaul Turbine minimum length must be zero! ("+turbine.minLength+")").solve(() -> {
                                turbine.minLength = 0;
                            }, "Set minimum length to 0");
                            if(turbine.maxSize!=0)validator.error("Overhaul Turbine maximum size must be zero! ("+turbine.maxSize+")").solve(() -> {
                                turbine.maxSize = 0;
                            }, "Set maximum size to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Throughput Efficiency Leniency">
                            if(turbine.throughputEfficiencyLeniencyMult!=0)validator.error("Overhaul Turbine throughput efficiency leniency mult must be zero! ("+turbine.throughputEfficiencyLeniencyMult+")").solve(() -> {
                                turbine.throughputEfficiencyLeniencyMult = 0;
                            }, "Set throughput efficiency leniency mult to 0");
                            if(turbine.throughputEfficiencyLeniencyThreshold!=0)validator.error("Overhaul Turbine throughput efficiency leniency threshold must be zero! ("+turbine.throughputEfficiencyLeniencyThreshold+")").solve(() -> {
                                turbine.throughputEfficiencyLeniencyThreshold = 0;
                            }, "Set throughput efficiency leniency threshold to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Fluid Per Blade">
                            if(turbine.fluidPerBlade!=0)validator.error("Overhaul Turbine fluid per blade must be zero! ("+turbine.fluidPerBlade+")").solve(() -> {
                                turbine.fluidPerBlade = 0;
                            }, "Set fluid per blade to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Throughput Factor">
                            if(turbine.throughputFactor!=0)validator.error("Overhaul Turbine throughput factor must be zero! ("+turbine.throughputFactor+")").solve(() -> {
                                turbine.throughputFactor = 0;
                            }, "Set throughput factor to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Power Bonus">
                            if(turbine.powerBonus!=0)validator.error("Overhaul Turbine power bonus must be zero! ("+turbine.powerBonus+")").solve(() -> {
                                turbine.powerBonus = 0;
                            }, "Set power bonus to 0");
                            //</editor-fold>
                        }else{
                            //<editor-fold defaultstate="collapsed" desc="Size">
                            if(turbine.minWidth<3)validator.error("Overhaul Turbine minimum width must be at least 3! ("+turbine.minWidth+")").solve(() -> {
                                turbine.minWidth = 3;
                            }, "Set minimum width to 3");
                            if(turbine.minLength<1)validator.error("Overhaul Turbine minimum length must be positive! ("+turbine.minLength+")").solve(() -> {
                                turbine.minLength = 1;
                            }, "Set minimum length to 1");
                            if(turbine.maxSize<3)validator.error("Overhaul Turbine maximum size must be at least 3! ("+turbine.maxSize+")").solve(() -> {
                                turbine.maxSize = 3;
                            }, "Set maximum size to 1");
                            if(turbine.minWidth>64)validator.warn("Overhaul Turbine minimum width is too big! ("+turbine.minWidth+")").hint("Widths above 64x64x64 can cause very large file widths and significant performance issues!").solve(() -> {
                                turbine.minWidth = 64;
                            }, "Set minimum width to 64");
                            if(turbine.minLength>64)validator.warn("Overhaul Turbine minimum length is too big! ("+turbine.minLength+")").hint("Lengths above 64x64x64 can cause very large file lengths and significant performance issues!").solve(() -> {
                                turbine.minLength = 64;
                            }, "Set minimum length to 64");
                            if(turbine.maxSize>64)validator.warn("Overhaul Turbine maximum size is too big! ("+turbine.maxSize+")").hint("Sizes above 64x64x64 can cause very large file sizes and significant performance issues!").solve(() -> {
                                turbine.maxSize = 64;
                            }, "Set maximum size to 64");
                            if(turbine.maxSize<turbine.minWidth||turbine.maxSize<turbine.minLength)validator.error("Overhaul Turbine maximum size is less than minimum size! ("+turbine.maxSize+"<"+turbine.minWidth+"|"+turbine.minLength+")").solve(() -> {
                                if(turbine.maxSize<turbine.minWidth)turbine.minWidth = turbine.maxSize;
                                if(turbine.maxSize<turbine.minLength)turbine.minLength = turbine.maxSize;
                            }, "Set minimum size to equal maximum size");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Sparsity Penalty">
                            if(!Float.isFinite(turbine.throughputEfficiencyLeniencyMult))validator.error("Overhaul Turbine throughput efficiency leniency mult is not finite! ("+turbine.throughputEfficiencyLeniencyMult+")").solve(() -> {
                                turbine.throughputEfficiencyLeniencyMult = 0;
                            }, "Set throughput efficiency leniency mult to 0");
                            if(turbine.throughputEfficiencyLeniencyMult<0)validator.error("Overhaul Turbine throughput efficiency leniency mult is negative! ("+turbine.throughputEfficiencyLeniencyMult+")").solve(() -> {
                                turbine.throughputEfficiencyLeniencyMult = 0;
                            }, "Set throughput efficiency leniency mult to 0");
                            if(!Float.isFinite(turbine.throughputEfficiencyLeniencyThreshold))validator.error("Overhaul Turbine throughput efficiency leniency threshold is not finite! ("+turbine.throughputEfficiencyLeniencyThreshold+")").solve(() -> {
                                turbine.throughputEfficiencyLeniencyThreshold = 0;
                            }, "Set throughput efficiency leniency threshold to 0");
                            if(turbine.throughputEfficiencyLeniencyThreshold<0)validator.error("Overhaul Turbine throughput efficiency leniency threshold is negative! ("+turbine.throughputEfficiencyLeniencyThreshold+")").solve(() -> {
                                turbine.throughputEfficiencyLeniencyThreshold = 0;
                            }, "Set throughput efficiency leniency threshold to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Fluid Per Blade">
                            if(turbine.fluidPerBlade<=0)validator.error("Overhaul Turbine fluid per blade must be positive! ("+turbine.fluidPerBlade+")").solve(() -> {
                                turbine.fluidPerBlade = 1;
                            }, "Set fluid per blade to 1");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Throughput Factor">
                            if(!Float.isFinite(turbine.throughputFactor))validator.error("Overhaul Turbine throughput factor is not finite! ("+turbine.throughputFactor+")").solve(() -> {
                                turbine.throughputFactor = 0;
                            }, "Set throughput factor to 0");
                            if(turbine.throughputFactor<0)validator.error("Overhaul Turbine throughput factor is negative! ("+turbine.throughputFactor+")").solve(() -> {
                                turbine.throughputFactor = 0;
                            }, "Set throughput factor to 0");
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Power Bonus">
                            if(!Float.isFinite(turbine.powerBonus))validator.error("Overhaul Turbine power bonus is not finite! ("+turbine.powerBonus+")").solve(() -> {
                                turbine.powerBonus = 0;
                            }, "Set power bonus to 0");
                            if(turbine.powerBonus<0)validator.error("Overhaul Turbine power bonus is negative! ("+turbine.powerBonus+")").solve(() -> {
                                turbine.powerBonus = 0;
                            }, "Set power bonus to 0");
                            //</editor-fold>
                        }
                        //<editor-fold defaultstate="collapsed" desc="Blocks">
                        validator.stage("Checking blocks...");
                        for(int i = 0; i<turbine.blocks.size(); i++){
                            validator.stage("Checking blocks... ("+(i+1)+"/"+turbine.blocks.size()+")");
                            net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b = turbine.blocks.get(i);
                            Runnable blockConfig = () -> {
                                gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.turbine.MenuBlockConfiguration(gui, this, configuration, b));
                            };
                            //<editor-fold defaultstate="collapsed" desc="Name">
                            if(b.name==null){
                                validator.error("Overhaul Turbine block name is null!").solve(blockConfig, "Go to Block configuration");
                            }else{
                                String[] split = b.name.split("\\:");
                                if(split.length<2||split.length>3){
                                    validator.error("Overhaul Turbine block name is invalid! ("+b.name+")").hint("Block name must be a namespaced ID! (namespace:name or namespace:name:metadata").solve(blockConfig, "Go to Block configuration");
                                }else{
                                    for(char c : split[0].toCharArray()){
                                        if(namespaceChars.indexOf(c)==-1){
                                            validator.error("Overhaul Turbine block namespace is invalid! ("+split[0]+")").hint("Namespaces can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame block.").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                    for(char c : split[1].toCharArray()){
                                        if(namespacePathChars.indexOf(c)==-1){
                                            validator.error("Overhaul Turbine block name is invalid! ("+split[1]+")").hint("block names can only contain characters 0-9, a-z, _, -, ., and /! This should be the name of the ingame block.").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                    if(split.length==3){
                                        try{
                                            Integer.parseInt(split[2]);
                                        }catch(Exception ex){
                                            validator.error("Overhaul Turbine block metadata is invalid! ("+split[2]+")").hint("Metadata must be an integer! If the ingame block does not have metadata, use only namespace:name.").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                }
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b2 : turbine.blocks){
                                    if(b==b2)continue;
                                    if(b.name.equals(b2.name))validator.error("Overhaul Turbine block name is not unique! ("+b.name+")").hint("This configuration contains multiple blocks with the same name.").solve(blockConfig, "Go to Block configuration");
                                }
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Display name">
                            if(b.displayName==null||b.displayName.isEmpty()){
                                validator.warn("Overhaul Turbine block has no display name!").solve(blockConfig, "Go to Block configuration");
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Legacy names">
                            for(Configuration c : Configuration.configurations){
                                if(c.overhaul!=null&&c.overhaul.turbine!=null){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b2 : c.overhaul.turbine.blocks){
                                        if(b.name.equals(b2.name)){
                                            if(!b.legacyNames.equals(b2.legacyNames))validator.warn("Overhaul Turbine block legacy names do not match! ("+b.name+")").hint("Another block was found in Configuration "+c.name+" with different legacy names.").solve(blockConfig, "Go to Block configuration");
                                        }else{
                                            for(String s : b.legacyNames)if(b2.legacyNames.contains(s))validator.warn("Overhaul Turbine block legacy names are not unique! ("+s+")").hint("Another block was found in configuration "+c.name+" with the same legacy name. These blocks could be confused when loading old files").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                }
                            }
                            for(AddonConfiguration c : Configuration.internalAddonCache.values()){
                                if(c.self.overhaul!=null&&c.self.overhaul.turbine!=null){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b2 : c.self.overhaul.turbine.blocks){
                                        if(b.name.equals(b2.name)&&!b.legacyNames.equals(b2.legacyNames)){
                                            validator.warn("Overhaul Turbine block legacy names do not match! ("+b.name+")").hint("Another block was found in Addon "+c.name+" with different legacy names.").solve(blockConfig, "Go to Block configuration");
                                        }
                                    }
                                }
                            }
                            //</editor-fold>
                            if(b.texture==null)validator.warn("Overhaul Turbine block "+b.name+" has no texture!").solve(blockConfig, "Go to Block configuration");
                            //<editor-fold defaultstate="collapsed" desc="Settings">
                            if(b.casing&&(b.shaft||b.coil||b.blade||b.bearing||b.connector||b.inlet||b.outlet))validator.error("Overhaul Turbine block "+b.name+" is a casing block, but has functionality!").hint("Turbine coils, bearings, etc. should not be marked as a casing").solve(() -> {
                                b.casing = false;
                            }, "Set casing to false");
                            if(b.casingEdge&&(b.shaft||b.coil||b.blade||b.bearing||b.connector||b.inlet||b.outlet))validator.error("Overhaul Turbine block "+b.name+" is a casing edge block, but has functionality!").hint("Turbine coils, bearings, etc. should not be marked as a casing edge").solve(() -> {
                                b.casingEdge = false;
                            }, "Set casing edge to false");
                            if(b.controller&&!b.casing)validator.error("Overhaul Turbine controller "+b.name+" is not part of the casing!").solve(() -> {
                                b.casing = true;
                            }, "Set casing to true");
                            if(b.controller&&b.casingEdge)validator.error("Overhaul Turbine controller "+b.name+" is part of the casing edge!").solve(() -> {
                                b.casingEdge = false;
                            }, "Set casing edge to false");
                            if(!b.blade){
                                if(b.bladeEfficiency!=0)validator.error("Overhaul Turbine block has blade efficiency, but is not a blade!").solve(() -> {
                                    b.bladeEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.bladeExpansion!=0)validator.error("Overhaul Turbine block has blade expansion, but is not a blade!").solve(() -> {
                                    b.bladeExpansion = 0;
                                }, "Set expansion to 0");
                                if(b.bladeStator)validator.error("Overhaul Turbine block is blade stator, but is not a blade!").solve(() -> {
                                    b.bladeStator = false;
                                }, "Set stator to false");
                            }else{
                                if(b.bladeEfficiency<0)validator.error("Overhaul Turbine block blade efficiency must not be negative!").solve(() -> {
                                    b.bladeEfficiency = 0;
                                }, "Set efficiency to 0");
                                if(b.bladeExpansion<0)validator.error("Overhaul Turbine block blade expansion must not be negative!").solve(() -> {
                                    b.bladeExpansion = 0;
                                }, "Set expansion to 0");
                            }
                            if(!Float.isFinite(b.bladeEfficiency))validator.error("Overhaul Turbine block blade efficiency is not finite!").solve(() -> {
                                b.bladeEfficiency = 0;
                            }, "Set efficiency to 0");
                            if(!Float.isFinite(b.bladeExpansion))validator.error("Overhaul Turbine block blade expansion is not finite!").solve(() -> {
                                b.bladeExpansion = 0;
                            }, "Set expansion to 0");
                            if(!b.coil){
                                if(b.coilEfficiency!=0)validator.error("Overhaul Turbine block has coil efficiency, but is not a coil!").solve(() -> {
                                    b.coilEfficiency = 0;
                                }, "Set efficiency to 0");
                            }else{
                                if(b.coilEfficiency<0)validator.error("Overhaul Turbine block coil efficiency must not be negative!").solve(() -> {
                                    b.coilEfficiency = 0;
                                }, "Set efficiency to 0");
                            }
                            if(!Float.isFinite(b.coilEfficiency))validator.error("Overhaul Turbine block coil efficiency is not finite!").solve(() -> {
                                b.coilEfficiency = 0;
                            }, "Set efficiency to 0");
                            //</editor-fold>
                            validate(b.rules, "Overhaul Turbine block "+b.name, blockConfig);
                        }
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Recipes">
                        validator.stage("Checking recipes...");
                        for(int i = 0; i<turbine.recipes.size(); i++){
                            validator.stage("Checking recipes... ("+(i+1)+"/"+turbine.recipes.size()+")");
                            net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe c = turbine.recipes.get(i);
                            Runnable recipeConfig = () -> {
                                gui.open(new net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.turbine.MenuRecipeConfiguration(gui, this, configuration, c));
                            };
                            //<editor-fold defaultstate="collapsed" desc="Input Name">
                            if(c.inputName==null){
                                validator.error("Overhaul Turbine recipe input name is null!").solve(recipeConfig, "Go to Recipe configuration");
                            }else{
                                if(c.inputName.contains(":"))validator.error("Overhaul Turbine recipe input name is invalid! ("+c.inputName+")").hint("Fluid name must be the name of the fluid! (ex. water, not fluid:water or minecraft:water)").solve(recipeConfig, "Go to Recipe configuration");
                                for(char ch : c.inputName.toCharArray()){
                                    if(namespaceChars.indexOf(ch)==-1){
                                        validator.error("Overhaul Turbine recipe input name is invalid! ("+c.inputName+")").hint("Fluid names can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame fluid.").solve(recipeConfig, "Go to Recipe configuration");
                                    }
                                }
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe c2 : turbine.recipes){
                                    if(c==c2)continue;
                                    if(c.inputName.equals(c2.inputName))validator.error("Overhaul Turbine recipe input name is not unique! ("+c.inputName+")").hint("This configuration contains multiple recipes with the same name.").solve(recipeConfig, "Go to Recipe configuration");
                                }
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Input Display name">
                            if(c.inputDisplayName==null||c.inputDisplayName.isEmpty()){
                                validator.warn("Overhaul Turbine recipe has no display name!").solve(recipeConfig, "Go to Recipe configuration");
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Output Name">
                            if(c.outputName==null){
                                validator.error("Overhaul Turbine recipe output name is null!").solve(recipeConfig, "Go to Recipe configuration");
                            }else{
                                if(c.outputName.contains(":"))validator.error("Overhaul Turbine recipe output name is invalid! ("+c.outputName+")").hint("Fluid name must be the name of the fluid! (ex. water, not fluid:water or minecraft:water)").solve(recipeConfig, "Go to Recipe configuration");
                                for(char ch : c.outputName.toCharArray()){
                                    if(namespaceChars.indexOf(ch)==-1){
                                        validator.error("Overhaul Turbine recipe output name is invalid! ("+c.outputName+")").hint("Fluid names can only contain characters 0-9, a-z, _, -, and .! This should be the namespace of the ingame fluid.").solve(recipeConfig, "Go to Recipe configuration");
                                    }
                                }
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Output Display name">
                            if(c.outputDisplayName==null||c.outputDisplayName.isEmpty()){
                                validator.warn("Overhaul Turbine recipe has no display name!").solve(recipeConfig, "Go to Recipe configuration");
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Legacy names">
                            for(Configuration cf : Configuration.configurations){
                                if(cf.overhaul!=null&&cf.overhaul.turbine!=null){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe c2 : cf.overhaul.turbine.recipes){
                                        if(c.inputName.equals(c2.inputName)){
                                            if(!c.inputLegacyNames.equals(c2.inputLegacyNames))validator.warn("Overhaul Turbine block legacy names do not match! ("+c.inputName+")").hint("Another block was found in Configuration "+cf.name+" with different legacy names.").solve(recipeConfig, "Go to Recipe configuration");
                                        }else{
                                            for(String s : c.inputLegacyNames)if(c2.inputLegacyNames.contains(s))validator.warn("Overhaul Turbine block legacy names are not unique! ("+s+")").hint("Another block was found in configuration "+cf.name+" with the same legacy name. These blocks could be confused when loading old files").solve(recipeConfig, "Go to Recipe configuration");
                                        }
                                    }
                                }
                            }
                            for(AddonConfiguration cf : Configuration.internalAddonCache.values()){
                                if(cf.self.overhaul!=null&&cf.self.overhaul.turbine!=null){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe c2 : cf.self.overhaul.turbine.recipes){
                                        if(c.inputName.equals(c2.inputName)&&!c.inputLegacyNames.equals(c2.inputLegacyNames)){
                                            validator.warn("Overhaul Turbine block legacy names do not match! ("+c.inputName+")").hint("Another block was found in Addon "+cf.name+" with different legacy names.").solve(recipeConfig, "Go to Recipe configuration");
                                        }
                                    }
                                }
                            }
                            //</editor-fold>
                            if(c.inputTexture==null)validator.warn("Overhaul Turbine recipe "+c.inputName+" has no input texture!").solve(recipeConfig, "Go to Recipe configuration");
                            if(c.outputTexture==null)validator.warn("Overhaul Turbine recipe "+c.outputName+" has no output texture!").solve(recipeConfig, "Go to Recipe configuration");
                            if(!Double.isFinite(c.power))validator.error("Overhaul Turbine recipe power is not finite! ("+c.power+")").solve(() -> {
                                c.power = 0;
                            }, "Set power to 0");
                            if(c.power<0)validator.error("Overhaul Turbine recipe power is negative! ("+c.power+")").solve(() -> {
                                c.power = 0;
                            }, "Set power to 0");
                            if(!Double.isFinite(c.coefficient))validator.error("Overhaul Turbine recipe expansion coefficient is not finite! ("+c.coefficient+")").solve(() -> {
                                c.coefficient = 0;
                            }, "Set coefficient to 0");
                            if(c.coefficient<0)validator.error("Overhaul Turbine recipe expansion coefficient is negative! ("+c.coefficient+")").solve(() -> {
                                c.coefficient = 0;
                            }, "Set coefficient to 0");
                        }
                        //</editor-fold>
                    }
                }
            }
            validator.finish();
        }, "Configuration Validator: "+configuration.name);
        t.setDaemon(true);
        t.start();
    }
    @Override
    public void render2d(double deltaTime){
        label.width = list.width = gui.getWidth()-sidebar.width;
        list.height = gui.getHeight()-list.y;
        super.render2d(deltaTime);
    }
    private void validate(ArrayList<? extends AbstractPlacementRule> rules, String sectionName, Runnable blockCfg){
        for(AbstractPlacementRule rule : rules){
            if(rule.block!=null){
                if(rule.block instanceof net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block){
                    net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block)rule.block;
                    if(b.cooling==0)validator.warn(sectionName+" placement rule "+rule.ruleType.name+" contains a specific non-cooler block!").solve(blockCfg, "Go to Block configuration");
                }
                if(rule.block instanceof net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block){
                    net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block)rule.block;
                    if(b.parent!=null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" contains a port!").solve(blockCfg, "Go to Block configuration");
                    else if(!b.heatsink)validator.warn(sectionName+" placement rule "+rule.ruleType.name+" contains a specific non-heatsink block!").solve(blockCfg, "Go to Block configuration");
                }
                if(rule.block instanceof net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block){
                    net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b = (net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block)rule.block;
                    if(b.parent!=null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" contains a port!").solve(blockCfg, "Go to Block configuration");
                    else if(!b.heater)validator.warn(sectionName+" placement rule "+rule.ruleType.name+" contains a specific non-heater block!").solve(blockCfg, "Go to Block configuration");
                }
                if(rule.block instanceof net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block){
                    net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b = (net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block)rule.block;
                    if(!b.coil)validator.warn(sectionName+" placement rule "+rule.ruleType.name+" contains a specific non-coil block!").solve(blockCfg, "Go to Block configuration");
                }
            }
            if(rule.blockType!=null){
                if(rule.blockType instanceof net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType){
                    net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType b = (net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType)rule.blockType;
                    if(b==net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR)validator.warn(sectionName+" placement rule "+rule.ruleType.name+" contains air!").solve(blockCfg, "Go to Block configuration");
                }
                if(rule.blockType instanceof net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType){
                    net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType b = (net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType)rule.blockType;
                    if(b==net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR)validator.warn(sectionName+" placement rule "+rule.ruleType.name+" contains air!").solve(blockCfg, "Go to Block configuration");
                }
                if(rule.blockType instanceof net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType){
                    net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType b = (net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType)rule.blockType;
                    if(b==net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR)validator.warn(sectionName+" placement rule "+rule.ruleType.name+" contains air!").solve(blockCfg, "Go to Block configuration");
                }
                if(rule.blockType instanceof net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.PlacementRule.BlockType){
                    net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.PlacementRule.BlockType b = (net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.PlacementRule.BlockType)rule.blockType;
                    if(b==net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.PlacementRule.BlockType.CASING)validator.warn(sectionName+" placement rule "+rule.ruleType.name+" contains casing!").solve(blockCfg, "Go to Block configuration");
                }
            }
            switch(rule.ruleType){
                case AND:
                case OR:
                    if(rule.block!=null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" contains a block!").solve(() -> {
                        rule.block = null;
                    }, "Set rule block to null");
                    if(rule.blockType!=null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" contains a block type!").solve(() -> {
                        rule.blockType = null;
                    }, "Set rule block to null");
                    if(rule.min!=0)validator.error(sectionName+" placement rule "+rule.ruleType.name+" minimum is not 0!").solve(() -> {
                        rule.min = 0;
                    }, "Set rule min to 0");
                    if(rule.max!=0)validator.error(sectionName+" placement rule "+rule.ruleType.name+" maximum is not 0!").solve(() -> {
                        rule.max = 0;
                    }, "Set rule max to 0");
                    if(rule.max!=0)validator.error(sectionName+" placement rule "+rule.ruleType.name+" isSpecificBlock is set!").solve(() -> {
                        rule.isSpecificBlock = false;
                    }, "Set rule isSpecificBlock to false");
                    validate(rule.rules, sectionName, blockCfg);
                    break;
                case AXIAL:
                    if(rule.isSpecificBlock&&rule.block==null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" contains null block!").solve(blockCfg, "Go to Block configuration");
                    if(!rule.isSpecificBlock&&rule.blockType==null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" contains null block type!").solve(blockCfg, "Go to Block configuration");
                    if(!rule.isSpecificBlock&&rule.block!=null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" contains block!").solve(blockCfg, "Go to Block configuration");
                    if(rule.isSpecificBlock&&rule.blockType!=null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" containsblock type!").solve(blockCfg, "Go to Block configuration");
                    if(rule.min<0)validator.error(sectionName+" placement rule "+rule.ruleType.name+" minimum must not be negative!").solve(() -> {
                        rule.min = 0;
                    }, "Set rule min to 0");
                    if(rule.max<0)validator.error(sectionName+" placement rule "+rule.ruleType.name+" maximum must not be negative!").solve(() -> {
                        rule.max = 0;
                    }, "Set rule max to 0");
                    if(rule.min>3)validator.error(sectionName+" placement rule "+rule.ruleType.name+" minimum is too big!").solve(() -> {
                        rule.min = 0;
                    }, "Set rule min to 3");
                    if(rule.max>3)validator.error(sectionName+" placement rule "+rule.ruleType.name+" maximum is too big!").solve(() -> {
                        rule.max = 0;
                    }, "Set rule max to 3");
                    if(rule.max<rule.min)validator.error(sectionName+" placement rule "+rule.ruleType.name+" maximum is less than its minimum!").solve(() -> {
                        rule.min = rule.max;
                    }, "Set minimum to equal maximum");
                    if(!rule.rules.isEmpty())validator.error(sectionName+" placement rule "+rule.ruleType.name+" has sub-rules!").solve(() -> {
                        rule.rules.clear();
                    }, "Clear sub-rules");
                    break;
                case BETWEEN:
                    if(rule.isSpecificBlock&&rule.block==null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" contains null block!").solve(blockCfg, "Go to Block configuration");
                    if(!rule.isSpecificBlock&&rule.blockType==null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" contains null block type!").solve(blockCfg, "Go to Block configuration");
                    if(!rule.isSpecificBlock&&rule.block!=null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" contains block!").solve(blockCfg, "Go to Block configuration");
                    if(rule.isSpecificBlock&&rule.blockType!=null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" containsblock type!").solve(blockCfg, "Go to Block configuration");
                    if(rule.min<0)validator.error(sectionName+" placement rule "+rule.ruleType.name+" minimum must not be negative!").solve(() -> {
                        rule.min = 0;
                    }, "Set rule min to 0");
                    if(rule.max<0)validator.error(sectionName+" placement rule "+rule.ruleType.name+" maximum must not be negative!").solve(() -> {
                        rule.max = 0;
                    }, "Set rule max to 0");
                    if(rule.min>6)validator.error(sectionName+" placement rule "+rule.ruleType.name+" minimum is too big!").solve(() -> {
                        rule.min = 0;
                    }, "Set rule min to 3");
                    if(rule.max>6)validator.error(sectionName+" placement rule "+rule.ruleType.name+" maximum is too big!").solve(() -> {
                        rule.max = 0;
                    }, "Set rule max to 3");
                    if(rule.max<rule.min)validator.error(sectionName+" placement rule "+rule.ruleType.name+" maximum is less than its minimum!").solve(() -> {
                        rule.min = rule.max;
                    }, "Set minimum to equal maximum");
                    if(!rule.rules.isEmpty())validator.error(sectionName+" placement rule "+rule.ruleType.name+" has sub-rules!").solve(() -> {
                        rule.rules.clear();
                    }, "Clear sub-rules");
                    break;
                case EDGE:
                case VERTEX:
                    if(rule.isSpecificBlock&&rule.block==null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" contains null block!").solve(blockCfg, "Go to Block configuration");
                    if(!rule.isSpecificBlock&&rule.blockType==null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" contains null block type!").solve(blockCfg, "Go to Block configuration");
                    if(!rule.isSpecificBlock&&rule.block!=null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" contains block!").solve(blockCfg, "Go to Block configuration");
                    if(rule.isSpecificBlock&&rule.blockType!=null)validator.error(sectionName+" placement rule "+rule.ruleType.name+" containsblock type!").solve(blockCfg, "Go to Block configuration");
                    if(rule.min!=0)validator.error(sectionName+" placement rule "+rule.ruleType.name+" minimum is not 0!").solve(() -> {
                        rule.min = 0;
                    }, "Set rule min to 0");
                    if(rule.max!=0)validator.error(sectionName+" placement rule "+rule.ruleType.name+" maximum is not 0!").solve(() -> {
                        rule.max = 0;
                    }, "Set rule max to 0");
                    if(!rule.rules.isEmpty())validator.error(sectionName+" placement rule "+rule.ruleType.name+" has sub-rules!").solve(() -> {
                        rule.rules.clear();
                    }, "Clear sub-rules");
                    break;
                default:
                    validator.error(sectionName+" placement rule "+rule.ruleType.name+" has unrecognized rule type!");
            }
        }
    }
}