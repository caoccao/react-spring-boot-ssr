/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
            // Resolve the actual resource path.
            Path resourcePath = relativeRootPath.resolve(resourceName).normalize();
            if (resourcePath.toFile().exists()) {
                try {
                    // Read the module source code.
                    String codeString = Files.readString(resourcePath, StandardCharsets.UTF_8);
                    // Compile the module.
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
