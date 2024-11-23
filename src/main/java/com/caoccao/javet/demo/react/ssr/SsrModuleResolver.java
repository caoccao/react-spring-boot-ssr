package com.caoccao.javet.demo.react.ssr;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetBuiltInModuleResolver;
import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.SimpleSet;
import com.caoccao.javet.values.reference.IV8Module;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SsrModuleResolver extends JavetBuiltInModuleResolver {
    protected static final Set<String> BUILT_IN_MODULES = SimpleSet.immutableOf(
            "stream", "util");

    protected static final List<MediaTypeAndExt> MEDIA_TYPE_AND_EXTS = SimpleList.immutableOf(
            new MediaTypeAndExt(Swc4jMediaType.JavaScript, ""),
            new MediaTypeAndExt(Swc4jMediaType.JavaScript, ".js"),
            new MediaTypeAndExt(Swc4jMediaType.Jsx, ".jsx"),
            new MediaTypeAndExt(Swc4jMediaType.TypeScript, ".ts"),
            new MediaTypeAndExt(Swc4jMediaType.Tsx, ".tsx"));
    protected Path rootPath;

    public SsrModuleResolver(Path rootPath) {
        this.rootPath = Objects.requireNonNull(rootPath);
    }

    public Path getRootPath() {
        return rootPath;
    }

    protected IV8Module resolve(
            V8Runtime v8Runtime,
            Swc4jMediaType mediaType,
            Path path) throws JavetException {
        File file = path.toFile();
        if (file.exists()) {
            System.out.println("  " + path);
            String codeString;
            try {
                codeString = Files.readString(path, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace(System.err);
                return null;
            }
            if (mediaType != Swc4jMediaType.JavaScript) {
                Swc4j swc4j = new Swc4j();
                Swc4jTranspileOptions options = new Swc4jTranspileOptions()
                        .setMediaType(mediaType)
                        .setSourceMap(Swc4jSourceMapOption.None)
                        .setParseMode(Swc4jParseMode.Module);
                try {
                    Swc4jTranspileOutput output = swc4j.transpile(codeString, options);
                    codeString = output.getCode();
                } catch (Swc4jCoreException e) {
                    e.printStackTrace(System.err);
                    return null;
                }
            }
            return v8Runtime
                    .getExecutor(codeString)
                    .setResourceName(path.toString())
                    .compileV8Module();
        }
        return null;
    }

    @Override
    public IV8Module resolve(V8Runtime v8Runtime, String resourceName, IV8Module v8ModuleReferrer) throws JavetException {
        if (BUILT_IN_MODULES.contains(resourceName)) {
            resourceName = "node:" + resourceName;
        }
        System.out.println("Resolving " + resourceName);
        IV8Module iV8Module = super.resolve(v8Runtime, resourceName, v8ModuleReferrer);
        if (iV8Module == null) {
            Path relativeRootPath = rootPath;
            if (v8ModuleReferrer != null) {
                relativeRootPath = Path.of(v8ModuleReferrer.getResourceName()).getParent();
            }
            if (resourceName.startsWith("@/")) {
                resourceName = resourceName.substring(2);
            }
            Path resourcePath = relativeRootPath.resolve(resourceName);
            File resourceFile = resourcePath.toFile();
            if (resourceFile.exists() && resourceFile.isDirectory()) {
                for (MediaTypeAndExt mediaTypeAndExt : MEDIA_TYPE_AND_EXTS) {
                    Path scriptPath = resourcePath
                            .resolve("index" + mediaTypeAndExt.ext)
                            .normalize();
                    iV8Module = resolve(v8Runtime, mediaTypeAndExt.mediaType, scriptPath);
                    if (iV8Module != null) {
                        break;
                    }
                }
            } else {
                for (MediaTypeAndExt mediaTypeAndExt : MEDIA_TYPE_AND_EXTS) {
                    Path scriptPath = resourcePath.getParent()
                            .resolve(resourcePath.getFileName() + mediaTypeAndExt.ext)
                            .normalize();
                    iV8Module = resolve(v8Runtime, mediaTypeAndExt.mediaType, scriptPath);
                    if (iV8Module != null) {
                        break;
                    }
                }
            }
        }
        return iV8Module;
    }

    protected static class MediaTypeAndExt {
        public String ext;
        public Swc4jMediaType mediaType;

        public MediaTypeAndExt(Swc4jMediaType mediaType, String ext) {
            this.mediaType = mediaType;
            this.ext = ext;
        }
    }
}
