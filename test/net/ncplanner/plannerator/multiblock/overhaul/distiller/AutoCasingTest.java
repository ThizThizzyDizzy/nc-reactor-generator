package net.ncplanner.plannerator.multiblock.overhaul.distiller;

import java.io.File;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.file.reader.NCPFReader;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.module.UnderhaulModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulDistillerConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.BlockElement;

/** Exercises glass replacement and rebuilding with the actual bundled configuration. */
public final class AutoCasingTest{
    public static void main(String[] args){
        System.setProperty("plannerator.skipTextures", "true");
        new CoreModule().registerNCPF();
        new UnderhaulModule().registerNCPF();
        new OverhaulModule().registerNCPF();
        FileReader.formats.add(new NCPFReader());
        Core.project = FileReader.read(new File("src/configurations/nuclearcraft.ncpf.json"));
        OverhaulDistillerConfiguration config = Core.project.getConfiguration(OverhaulDistillerConfiguration::new);
        OverhaulDistiller distiller = new OverhaulDistiller(Core.project.conglomeration, 3, 3, 3, null);
        distiller.buildDefaultCasing();
        checkShell(distiller);

        BlockElement glass = config.blocks.stream().filter(b -> b.definition.getName().equals("nuclearcraft:machine_glass")).findFirst().get();
        // The cache still includes the old controller while every face is replaced.
        distiller.getBlocks(true);
        distiller.forEachCasingFacePosition(pos -> distiller.setBlock(pos, new Block(Core.project.conglomeration, null, glass)));
        distiller.buildDefaultCasing();
        checkShell(distiller);

        BlockPos hole = new BlockPos(0, 2, 2);
        distiller.setBlock(hole, null);
        distiller.buildDefaultCasing();
        check(distiller.getBlock(hole).template==glass, "An empty wall position must fill with glass");
        checkShell(distiller);
        String before = shell(distiller);
        distiller.buildDefaultCasing();
        check(before.equals(shell(distiller)), "Repeated rebuilding must leave the shell unchanged");

        for(BlockElement template : config.blocks){
            if(template.port!=null||template.powerPort!=null||template.processPort!=null||template.reservoirPort!=null){
                distiller.setBlock(hole, new Block(Core.project.conglomeration, null, template));
                distiller.buildDefaultCasing();
                check(distiller.getBlock(hole).template==template, "Preserve a manually placed port");
            }
        }
        System.out.println("PASS: bundled distiller fills with glass, restores its controller, preserves ports, and rebuilds consistently.");
    }
    private static void checkShell(OverhaulDistiller distiller){
        int[] controllers = {0};
        distiller.forEachCasingPosition(pos -> {
            Block block = distiller.getBlock(pos);
            check(block!=null, "No missing casing blocks");
            String name = block.template.definition.getName();
            if(block.isController())controllers[0]++;
            else check(name.equals("nuclearcraft:machine_frame")||name.equals("nuclearcraft:machine_glass"), "Shell must use casing or glass, not ports");
        });
        check(controllers[0]==1, "Exactly one controller after rebuilding");
    }
    private static String shell(OverhaulDistiller distiller){
        StringBuilder result = new StringBuilder();
        distiller.forEachCasingPosition(pos -> result.append(distiller.getBlock(pos).template.definition).append(';'));
        return result.toString();
    }
    private static void check(boolean condition, String message){
        if(!condition)throw new AssertionError(message);
    }
}
