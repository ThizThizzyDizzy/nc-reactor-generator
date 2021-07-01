package multiblock.configuration;

import multiblock.*;
import planner.menu.component.Searchable;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;

import java.util.ArrayList;
import java.util.Objects;

public abstract class AbstractPlacementRule<BlockType extends IBlockType<Template>,
        Template extends IBlockTemplate> extends RuleContainer<BlockType, Template> implements Searchable {
    public RuleType ruleType = RuleType.BETWEEN;
    public BlockType blockType;
    public Template block;

    public byte min;
    public byte max;

    protected abstract AbstractBlockContainer<Template> getContainerFromParent(Configuration parent);

    protected abstract byte saveBlockType(BlockType type);

    public abstract BlockType loadBlockType(byte type);

    public Config save(Configuration parent, AbstractBlockContainer<Template> configuration) {
        Config config = Config.newConfig();
        int blockIndex = configuration.blocks.indexOf(block) + 1;
        if (parent != null) {
            blockIndex = getContainerFromParent(parent).allBlocks.indexOf(block) + 1;
        }

        config.set("type", (byte) ruleType.ordinal());
        switch (ruleType) {
            case BETWEEN:
            case AXIAL:
                config.set("block", blockIndex);
                config.set("min", min);
                config.set("max", max);
                break;
            case VERTEX:
            case EDGE:
                config.set("block", blockIndex);
                break;
            case BETWEEN_GROUP:
            case AXIAL_GROUP:
                config.set("block", saveBlockType(blockType));
                config.set("min", min);
                config.set("max", max);
                break;
            case VERTEX_GROUP:
            case EDGE_GROUP:
                config.set("block", saveBlockType(blockType));
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
    public String toString() {
        switch (ruleType) {
            case BETWEEN:
                if (max == 6) return "At least " + min + " " + block.getDisplayName();
                if (min == max) return "Exactly " + min + " " + block.getDisplayName();
                return "Between " + min + " and " + max + " " + block.getDisplayName();
            case BETWEEN_GROUP:
                if (max == 6) return "At least " + min + " " + blockType.getDisplayName();
                if (min == max) return "Exactly " + min + " " + blockType.getDisplayName();
                return "Between " + min + " and " + max + " " + blockType.getDisplayName();
            case AXIAL:
                if (max == 3) return "At least " + min + " Axial pairs of " + block.getDisplayName();
                if (min == max) return "Exactly " + min + " Axial pairs of " + block.getDisplayName();
                return "Between " + min + " and " + max + " Axial pairs of " + block.getDisplayName();
            case AXIAL_GROUP:
                if (max == 3) return "At least " + min + " Axial pairs of " + blockType.getDisplayName();
                if (min == max) return "Exactly " + min + " Axial pairs of " + blockType.getDisplayName();
                return "Between " + min + " and " + max + " Axial pairs of " + blockType.getDisplayName();
            case VERTEX:
                return "Three " + block.getDisplayName() + " at the same vertex";
            case VERTEX_GROUP:
                return "Three " + blockType.getDisplayName() + " at the same vertex";
            case AND:
                StringBuilder s = new StringBuilder();
                for (AbstractPlacementRule<BlockType, Template> rule : rules) {
                    s.append(" AND ").append(rule.toString());
                }
                return (s.length() == 0) ? s.toString() : s.substring(5);
            case OR:
                s = new StringBuilder();
                for (AbstractPlacementRule<BlockType, Template> rule : rules) {
                    s.append(" OR ").append(rule.toString());
                }
                return (s.length() == 0) ? s.toString() : s.substring(4);
            case EDGE:
                return "Two " + block.getDisplayName() + " at the same edge";
            case EDGE_GROUP:
                return "Two " + blockType.getDisplayName() + " at the same edge";
        }
        return "Unknown Rule";
    }

    public <T extends Block & ITemplateAccess<Template>> boolean isValid(T block, Multiblock<T> reactor) {
        int num = 0;
        switch (ruleType) {
            case BETWEEN:
                for (T b : block.getActiveAdjacent(reactor)) {
                    if (b.getTemplate() == this.block) num++;
                }
                return num >= min && num <= max;
            case BETWEEN_GROUP:
                if (blockType.isAir()) {
                    num = 6 - block.getAdjacent(reactor).size();
                } else {
                    for (T b : block.getActiveAdjacent(reactor)) {
                        if (blockType.blockMatches(b)) num++;
                    }
                }
                return num >= min && num <= max;
            case AXIAL:
                for (Axis axis : axes) {
                    T b1 = reactor.getBlock(block.x - axis.x, block.y - axis.y, block.z - axis.z);
                    T b2 = reactor.getBlock(block.x + axis.x, block.y + axis.y, block.z + axis.z);
                    if (b1 != null && b1.getTemplate() == this.block && b1.isActive() &&
                            b2 != null && b2.getTemplate() == this.block && b2.isActive())
                        num++;
                }
                return num >= min && num <= max;
            case AXIAL_GROUP:
                if (blockType.isAir()) {
                    for (Axis axis : axes) {
                        T b1 = reactor.getBlock(block.x - axis.x, block.y - axis.y, block.z - axis.z);
                        T b2 = reactor.getBlock(block.x + axis.x, block.y + axis.y, block.z + axis.z);
                        if (b1 == null && b2 == null) num++;
                    }
                } else {
                    for (Axis axis : axes) {
                        T b1 = reactor.getBlock(block.x - axis.x, block.y - axis.y, block.z - axis.z);
                        T b2 = reactor.getBlock(block.x + axis.x, block.y + axis.y, block.z + axis.z);
                        if (b1 == null || b2 == null) continue;
                        if (!b1.isActive() || !b2.isActive()) continue;
                        if (blockType.blockMatches(b1) && blockType.blockMatches(b2)) num++;
                    }
                }
                return num >= min && num <= max;
            case VERTEX:
                ArrayList<Direction> dirs = new ArrayList<>();
                for (Direction d : Direction.values()) {
                    T b = reactor.getBlock(block.x + d.x, block.y + d.y, block.z + d.z);
                    if (b.getTemplate() == this.block) dirs.add(d);
                }
                for (Vertex e : Vertex.values()) {
                    boolean missingOne = false;
                    for (Direction d : e.directions) {
                        if (!dirs.contains(d)) {
                            missingOne = true;
                            break;
                        }
                    }
                    if (!missingOne) return true;
                }
                return false;
            case VERTEX_GROUP:
                dirs = new ArrayList<>();
                for (Direction d : Direction.values()) {
                    T b = reactor.getBlock(block.x + d.x, block.y + d.y, block.z + d.z);
                    if (blockType.isAir()) {
                        if (b == null) dirs.add(d);
                    } else {
                        if (blockType.blockMatches(b)) dirs.add(d);
                    }
                }
                for (Vertex e : Vertex.values()) {
                    boolean missingOne = false;
                    for (Direction d : e.directions) {
                        if (!dirs.contains(d)) {
                            missingOne = true;
                            break;
                        }
                    }
                    if (!missingOne) return true;
                }
                return false;
            case AND:
                for (AbstractPlacementRule<BlockType, Template> rule : rules) {
                    if (!rule.isValid(block, reactor)) return false;
                }
                return true;
            case OR:
                for (AbstractPlacementRule<BlockType, Template> rule : rules) {
                    if (rule.isValid(block, reactor)) return true;
                }
                return false;
            case EDGE:
                dirs = new ArrayList<>();
                for (Direction d : Direction.values()) {
                    T b = reactor.getBlock(block.x + d.x, block.y + d.y, block.z + d.z);
                    if (b.getTemplate() == this.block) dirs.add(d);
                }
                for (Edge3 e : Edge3.values()) {
                    boolean missingOne = false;
                    for (Direction d : e.directions) {
                        if (!dirs.contains(d)) {
                            missingOne = true;
                            break;
                        }
                    }
                    if (!missingOne) return true;
                }
                return false;
            case EDGE_GROUP:
                dirs = new ArrayList<>();
                for (Direction d : Direction.values()) {
                    T b = reactor.getBlock(block.x + d.x, block.y + d.y, block.z + d.z);
                    if (blockType.isAir()) {
                        if (b == null) dirs.add(d);
                    } else {
                        if (blockType.blockMatches(b)) dirs.add(d);
                    }
                }
                for (Edge3 e : Edge3.values()) {
                    boolean missingOne = false;
                    for (Direction d : e.directions) {
                        if (!dirs.contains(d)) missingOne = true;
                    }
                    if (!missingOne) return true;
                }
                return false;
        }
        throw new IllegalArgumentException("Unknown rule type: " + ruleType);
    }

    @Override
    public ArrayList<String> getSearchableNames() {
        ArrayList<String> nams = new ArrayList<>();
        switch (ruleType) {
            case BETWEEN:
            case VERTEX:
            case AXIAL:
                nams.addAll(block.getLegacyNames());
                nams.add(block.getDisplayName());
                break;
            case BETWEEN_GROUP:
            case VERTEX_GROUP:
            case AXIAL_GROUP:
                nams.add(blockType.getDisplayName());
                break;
            case AND:
            case OR:
                for (AbstractPlacementRule<BlockType, Template> r : rules) nams.addAll(r.getSearchableNames());
                break;
        }
        return nams;
    }

    /**
     * Warning: The order of enums in here is *significant*.
     * Add new emum variants only to the end of the list.
     */
    public enum RuleType {
        BETWEEN("Between"), AXIAL("Axial"), VERTEX("Vertex"), BETWEEN_GROUP("Between (Group)"),
        AXIAL_GROUP("Axial (Group)"), VERTEX_GROUP("Vertex (Group"), OR("Or"), AND("And"), EDGE("Edge"),
        EDGE_GROUP("Edge (Group)");
        public final String name;

        RuleType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public boolean stillEquals(RuleContainer<BlockType, Template> rc) {
        AbstractPlacementRule<BlockType, Template> pr = (AbstractPlacementRule<BlockType, Template>) rc;
        return pr.ruleType == ruleType && Objects.equals(pr.block, block) &&
                pr.min == min && pr.max == max;
    }
}