package org.lins.mmmjjkx.fakeplayermaker.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import lombok.SneakyThrows;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Map;

public class FPMLoader implements PluginLoader {
    private static final Map<String, String> PACKETLIB_VERSIONS;

    static {
        PACKETLIB_VERSIONS = Map.of(
                "1.19.4", "1.19.4-1",
                "1.20", "1.20-2",
                "1.20.1", "1.20-2",
                "1.20.2", "1.20.2-1",
                "1.20.3", "1.20.4-1",
                "1.20.4", "1.20.4-1",
                "1.20.5", "1.20.6-1",
                "1.20.6", "1.20.6-1"
        );
    }

    @SneakyThrows
    @Override
    public void classloader(@NotNull PluginClasspathBuilder pluginClasspathBuilder) {
        Class<?> SharedConstants = Class.forName("net.minecraft.SharedConstants");
        Field gameVersionField = SharedConstants.getDeclaredField("d");
        String version = (String) gameVersionField.get(null);
        String packetLibVersion = PACKETLIB_VERSIONS.get(version);
        if (packetLibVersion == null) {
            throw new IllegalArgumentException("Unsupported Minecraft version: " + version);
        }
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder("papermc", "default", "https://papermc.io/repo/repository/maven-public/").build());
        resolver.addRepository(new RemoteRepository.Builder("opencollab", "default", "https://repo.opencollab.dev/maven-releases/").build());
        resolver.addRepository(new RemoteRepository.Builder("opencollab-snapshot", "default", "https://repo.opencollab.dev/maven-snapshots/").build());

        resolver.addDependency(new Dependency(new DefaultArtifact("com.github.steveice10", "mcprotocollib", "jar", packetLibVersion), null));

        pluginClasspathBuilder.addLibrary(resolver);
    }
}
