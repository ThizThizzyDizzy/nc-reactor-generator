package multiblock.configuration;
import java.util.ArrayList;
import java.util.Objects;
import multiblock.Axis;
import multiblock.Block;
import multiblock.Direction;
import multiblock.Edge3;
import multiblock.Multiblock;
import multiblock.Vertex;
import planner.menu.component.Searchable;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public abstract class AbstractPlacementRule<BlockType extends IBlockType, Template extends IBlockTemplate> extends RuleContainer<BlockType, Template> implements Searchable {
    public RuleType ruleType = RuleType.BETWEEN;
    public boolean isSpecificBlock = false;
    public BlockType blockType;
    public Template block;

    public byte min;
    public byte max;

    protected abstract AbstractBlockContainer<Template> getContainerFromParent(Configuration parent);

    public abstract AbstractPlacementRule<BlockType, Template> newRule();
    protected abstract byte saveBlockType(BlockType type);
    public abstract BlockType loadBlockType(byte type);

    private void configSaveBlock(Config config, Configuration parent, AbstractBlockContainer<Template> configuration) {
        if (isSpecificBlock) {
            int blockIndex = configuration.blocks.indexOf(block) + 1;
            if (parent != null) {
                blockIndex = getContainerFromParent(parent).allBlocks.indexOf(block) + 1;
            }
            config.set("blockIdx", blockIndex);
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

    private String getTargetName() {
        if (isSpecificBlock) return block.getDisplayName();
        else return blockType.getDisplayName();
    }
    @Override
    public String toString() {
        switch (ruleType) {
            case BETWEEN:
                if (max == 6) return "At least " + min + " " + getTargetName();
                if (min == max) return "Exactly " + min + " " + getTargetName();
                return "Between " + min + " and " + max + " " + getTargetName();
            case AXIAL:
                if (max == 3) return "At least " + min + " Axial pairs of " + getTargetName();
                if (min == max) return "Exactly " + min + " Axial pairs of " + getTargetName();
                return "Between " + min + " and " + max + " Axial pairs of " + getTargetName();
            case VERTEX:
                return "Three " + getTargetName() + " at the same vertex";
            case EDGE:
                return "Two " + getTargetName() + " at the same edge";
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
        }
        return "Unknown Rule";
    }

    private boolean isAirMatch() {
        return isSpecificBlock && blockType.isAir();
    }
    private <T extends Block & ITemplateAccess<Template>> boolean blockMatches(T block, Multiblock<T> reactor) {
        if (isSpecificBlock) return block != null && block.getTemplate() == this.block;
        else if (blockType.isAir()) return block == null;
        else return block != null && blockType.blockMatches(reactor, block);
    }
    public <T extends Block & ITemplateAccess<Template>> boolean isValid(T block, Multiblock<T> reactor) {
        int num = 0;
        boolean isAirMatch = isAirMatch();
        switch (ruleType) {
            case BETWEEN:
                if (isAirMatch) {
                    num = 6 - block.getAdjacent(reactor).size();
                } else {
                    for (T b : block.getActiveAdjacent(reactor)) {
                        if (blockMatches(b, reactor)) num++;
                    }
                }
                return num >= min && num <= max;
            case AXIAL:
                for (Axis axis : axes) {
                    T b1 = reactor.getBlock(block.x - axis.x, block.y - axis.y, block.z - axis.z);
                    T b2 = reactor.getBlock(block.x + axis.x, block.y + axis.y, block.z + axis.z);
                    if (isAirMatch) {
                        if (b1 == null && b2 == null) num++;
                    } else {
                        if (b1 == null || b2 == null) continue;
                        if (!b1.isActive() || !b2.isActive()) continue;
                        if (blockMatches(b1, reactor) && blockMatches(b2, reactor)) num++;
                    }
                }
                return num >= min && num <= max;
            case VERTEX:
            case EDGE:
                boolean[] dirs = new boolean[Direction.values().length];
                for (Direction d : Direction.values()) {
                    T b = reactor.getBlock(block.x + d.x, block.y + d.y, block.z + d.z);
                    if (isAirMatch) {
                        if (b == null) dirs[d.ordinal()] = true;
                    } else {
                        if (blockMatches(b, reactor)) dirs[d.ordinal()] = true;
                    }
                }
                if (ruleType == RuleType.VERTEX) {
                    outer: for (Vertex e : Vertex.values()) {
                        for (Direction d : e.directions) {
                            if (!dirs[d.ordinal()]) continue outer;
                        }
                        return true;
                    }
                } else if (ruleType == RuleType.EDGE) {
                    outer: for (Edge3 e : Edge3.values()) {
                        for (Direction d : e.directions) {
                            if (!dirs[d.ordinal()]) continue outer;
                        }
                        return true;
                    }
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

    /**
     * Warning: The order of enums in here is *significant*.
     * Add new emum variants only to the end of the list.
     */
    public enum RuleType {
        BETWEEN("Between"),
        AXIAL("Axial"),
        VERTEX("Vertex"),
        EDGE("Edge"),
        OR("Or"),
        AND("And");

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
        return pr.ruleType == ruleType && Objects.equals(pr.block, block) && pr.min == min && pr.max == max;
    }

    protected BlockType parseBlockType(AbstractBlockContainer<Template> configuration, String str) {
        throw new RuntimeException("parseNC not supported");
    }
    protected Template parseTemplate(AbstractBlockContainer<Template> configuration, String str) {
        throw new RuntimeException("parseNC not supported");
    }

    protected void parseNcInto(AbstractBlockContainer<Template> configuration, String str) {
        if (str.contains("||")) {
            this.ruleType = RuleType.OR;
            for (String sub : str.split("\\|\\|")) {
                AbstractPlacementRule<BlockType, Template> rul = newRule();
                rul.parseNcInto(configuration, sub.trim());
                this.rules.add(rul);
            }
        } else if (str.contains("&&")) {
            this.ruleType = RuleType.AND;
            for (String sub : str.split("&&")) {
                AbstractPlacementRule<BlockType, Template> rul = newRule();
                rul.parseNcInto(configuration, sub.trim());
                this.rules.add(rul);
            }
        } else {
            if (str.startsWith("at least ")) str = str.substring("at least ".length());
            boolean exactly = str.startsWith("exactly");
            if (exactly) str = str.substring(7).trim();

            int amount = 0;
            if (str.startsWith("zero")) {
                amount = 0;
                str = str.substring(4).trim();
            } else if (str.startsWith("one")) {
                amount = 1;
                str = str.substring(3).trim();
            } else if (str.startsWith("two")) {
                amount = 2;
                str = str.substring(3).trim();
            } else if (str.startsWith("three")) {
                amount = 3;
                str = str.substring(5).trim();
            } else if (str.startsWith("four")) {
                amount = 4;
                str = str.substring(4).trim();
            } else if (str.startsWith("five")) {
                amount = 5;
                str = str.substring(4).trim();
            } else if (str.startsWith("six")) {
                amount = 6;
                str = str.substring(3).trim();
            }

            boolean axial = str.startsWith("axial");
            if (axial) str = str.substring(5).trim();

            if(str.startsWith("of any "))str = str.substring("of any ".length());
            BlockType type = parseBlockType(configuration, str);
            Template block = type == null ? parseTemplate(configuration, str) : null;

            if (type == null && block == null)
                throw new IllegalArgumentException("Failed to parse rule " + str + ": block is null!");
            if (exactly && axial) {
                this.ruleType = RuleType.AND;
                AbstractPlacementRule<BlockType, Template> rul1 = newRule();
                AbstractPlacementRule<BlockType, Template> rul2 = newRule();
                if (type != null) {
                    rul1.ruleType = RuleType.BETWEEN;
                    rul2.ruleType = RuleType.AXIAL;
                    rul1.isSpecificBlock = rul2.isSpecificBlock = false;
                    rul1.blockType = rul2.blockType = type;
                } else {
                    rul1.ruleType = RuleType.BETWEEN;
                    rul2.ruleType = RuleType.AXIAL;
                    rul1.isSpecificBlock = rul2.isSpecificBlock = true;
                    rul1.block = rul2.block = block;
                }
                rul1.min = rul1.max = (byte) amount;
                rul2.min = rul2.max = (byte) (amount / 2);
                this.rules.add(rul1);
                this.rules.add(rul2);
            } else {
                int min = amount;
                int max = 6;
                if (exactly) max = min;

                if (type != null) {
                    this.ruleType = axial ? RuleType.AXIAL : RuleType.BETWEEN;
                    this.isSpecificBlock = false;
                    this.blockType = type;
                } else {
                    this.ruleType = axial ? RuleType.AXIAL : RuleType.BETWEEN;
                    this.isSpecificBlock = true;
                    this.block = block;
                }
                if (axial) {
                    min /= 2;
                    max /= 2;
                }
                this.min = (byte) min;
                this.max = (byte) max;
            }
        }
    }
}