package com.caoccao.javet.demo.react.ssr;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetBuiltInModuleResolver;
import com.caoccao.javet.values.reference.IV8Module;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

public final class SsrModuleResolver extends JavetBuiltInModuleResolver {
    private static final Set<String> BUILT_IN_MODULES = Set.of("stream", "util");
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SsrModuleResolver.class);

    private final Path rootPath;

    public SsrModuleResolver(Path rootPath) {
        this.rootPath = Objects.requireNonNull(rootPath);
    }

    public Path getRootPath() {
        return rootPath;
    }

    @Override
    public IV8Module resolve(V8Runtime v8Runtime, String resourceName, IV8Module v8ModuleReferrer) throws JavetException {
        // Normalize built-in modules.
        if (BUILT_IN_MODULES.contains(resourceName)) {
            resourceName = "node:" + resourceName;
        }
        LOGGER.debug("Resolving {}.", resourceName);
        IV8Module iV8Module = super.resolve(v8Runtime, resourceName, v8ModuleReferrer);
        if (iV8Module == null) {
            Path relativeRootPath = rootPath;
            if (v8ModuleReferrer != null) {
                relativeRootPath = Path.of(v8ModuleReferrer.getResourceName()).getParent();
            }
            Path resourcePath = relativeRootPath.resolve(resourceName).normalize();
            if (resourcePath.toFile().exists()) {
                try {
                    String codeString = Files.readString(resourcePath, StandardCharsets.UTF_8);
                    iV8Module = v8Runtime
                            .getExecutor(codeString)
                            .setResourceName(resourcePath.toString())
                            .compileV8Module();
                } catch (IOException e) {
                    LOGGER.error("Failed to read file.", e);
                }
            }
        }
        return iV8Module;
    }
}
