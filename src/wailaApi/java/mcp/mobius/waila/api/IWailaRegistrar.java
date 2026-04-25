package mcp.mobius.waila.api;

public interface IWailaRegistrar {
    void registerBodyProvider(IWailaDataProvider dataProvider, Class block);

    void registerNBTProvider(IWailaDataProvider dataProvider, Class block);
}
