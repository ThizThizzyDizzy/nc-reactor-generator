package multiblock.configuration;

public interface ITemplateAccess<Template extends IBlockTemplate> {
    Template getTemplate();
}
