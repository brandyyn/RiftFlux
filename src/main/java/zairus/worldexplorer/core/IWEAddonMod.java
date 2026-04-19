/*
 * Decompiled with CFR 0.152.
 */
package zairus.worldexplorer.core;

import zairus.worldexplorer.core.IWEAddonEntityManager;
import zairus.worldexplorer.core.IWEAddonMonsterManager;
import zairus.worldexplorer.core.IWEAddonRenderManager;

public interface IWEAddonMod {
    public IWEAddonEntityManager getEntityManager();

    public IWEAddonMonsterManager getMonsterManager();

    public IWEAddonRenderManager getRenderManager();
}

