package io.github.crucible.hycrucible.bootstrap.mixin;

import io.github.crucible.hycrucible.bootstrap.util.ClassLoaderHolder;
import lombok.SneakyThrows;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.*;
import org.spongepowered.asm.util.IConsumer;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

public class Service extends MixinServiceAbstract {

    public static IMixinTransformer transformer;
    public static IConsumer<MixinEnvironment.Phase> phaseConsumer;


    @Override
    public String getName() {
        return "HyCrucible";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public IClassProvider getClassProvider() {
        return new ClassProvider();
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return new BytecodeProvider();
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return null;
    }

    @Override
    public IClassTracker getClassTracker() {
        return null;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return List.of("io.github.crucible.hycrucible.bootstrap.mixin.HyCruciblePlatform");
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        try {
            return new ContainerHandleURI(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException ignored) {
        }
        return new ContainerHandleVirtual(this.getName());
    }

    public void offer(IMixinInternal internal) {
        if (internal instanceof IMixinTransformerFactory) {
            transformer = ((IMixinTransformerFactory) internal).createTransformer();
        }
    }

    @Override
    @SneakyThrows
    public InputStream getResourceAsStream(String name) {
        return ClassLoaderHolder.findResource(name);
    }

    @Deprecated
    public void wire(MixinEnvironment.Phase phase, IConsumer<MixinEnvironment.Phase> phaseConsumer) {
        super.wire(phase, phaseConsumer);
        Service.phaseConsumer = phaseConsumer;
    }

}