package net.ncplanner.plannerator.multiblock.configuration;
import java.util.ArrayList;
import java.util.Objects;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.planner.Searchable;
public abstract class AbstractPlacementRule<BlockType extends IBlockType, Template extends IBlockTemplate> extends RuleContainer<BlockType, Template> implements Searchable {
    protected abstract AbstractBlockContainer<Template> getContainerFromParent(Configuration parent);

    public abstract AbstractPlacementRule<BlockType, Template> newRule();
    protected abstract byte saveBlockType(BlockType type);
    public abstract BlockType loadBlockType(byte type);

    private void configSaveBlock(Config config, Configuration parent, AbstractBlockContainer<Template> configuration) {
        config.set("isSpecificBlock", isSpecificBlock);
        if (isSpecificBlock) {
            int blockIndex = configuration.blocks.indexOf(block) + 1;
            if (parent != null) {
                blockIndex = getContainerFromParent(parent).allBlocks.indexOf(block) + 1;
            }
            config.set("block", blockIndex);
        } else {
            config.set("blockType", saveBlockType(blockType));
        }
    }
    public Config save(Configuration parent, AbstractBlockContainer<Template> configuration) {
        Config config = Config.newConfig();

        config.set("type", (byte) ruleType.ordinal());
        switch (ruleType) {
            case BETWEEN:
            case AXIAL:
                configSaveBlock(config, parent, configuration);
                config.set("min", min);
                config.set("max", max);
                break;
            case VERTEX:
            case EDGE:
                configSaveBlock(config, parent, configuration);
                break;
            case OR:
            case AND:
                ConfigList ruls = new ConfigList();
                for (AbstractPlacementRule<BlockType, Template> rule : rules) {
                    ruls.add(rule.save(parent, configuration));
                }
                config.set("rules", ruls);
                break;
        }
        return config;
    }

    @Override
    public ArrayList<String> getSearchableNames() {
        ArrayList<String> nams = new ArrayList<>();
        switch (ruleType) {
            case BETWEEN:
            case VERTEX:
            case AXIAL:
                if (isSpecificBlock) {
                    nams.addAll(block.getLegacyNames());
                    nams.add(block.getDisplayName());
                } else {
                    nams.add(blockType.getDisplayName());
                }
                break;
            case AND:
            case OR:
                for (AbstractPlacementRule<BlockType, Template> r : rules) nams.addAll(r.getSearchableNames());
                break;
        }
        return nams;
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames() {
        return getSearchableNames();
    }

    @Override
    public boolean stillEquals(RuleContainer<BlockType, Template> rc) {
        AbstractPlacementRule<BlockType, Template> pr = (AbstractPlacementRule<BlockType, Template>) rc;
        return pr.ruleType == ruleType && (pr.block==null?block==null:(block!=null&&Objects.equals(pr.block.getName(), block.getName()))) && pr.min == min && pr.max == max;
    }
}