package io.github.crucible.hycrucible.bootstrap.mixin;

import org.spongepowered.asm.launch.platform.IMixinPlatformServiceAgent;
import org.spongepowered.asm.launch.platform.MixinPlatformAgentAbstract;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.util.Constants;

import java.util.Collection;
import java.util.List;

public class HyCruciblePlatform extends MixinPlatformAgentAbstract implements IMixinPlatformServiceAgent {

    @Override
    public void init() {

    }

    @Override
    public String getSideName() {
        return Constants.SIDE_SERVER;
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        //TODO: Mixin container discover
        return List.of();
    }

}
