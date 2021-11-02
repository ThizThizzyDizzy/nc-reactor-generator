package net.ncplanner.plannerator.multiblock.configuration;

public interface ITemplateAccess<Template extends IBlockTemplate> {
    Template getTemplate();
}
