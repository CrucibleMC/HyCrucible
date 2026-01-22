package io.github.crucible.hycrucible.bootstrap.mixin;

import org.spongepowered.asm.service.IMixinServiceBootstrap;

public class ServiceBootstrap implements IMixinServiceBootstrap {

    @Override
    public String getName() {
        return "HyCrucible";
    }

    @Override
    public String getServiceClassName() {
        return "io.github.crucible.hycrucible.bootstrap.mixin.Service";
    }

    @Override
    public void bootstrap() {

    }

}
