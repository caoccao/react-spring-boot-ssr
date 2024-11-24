package com.caoccao.javet.demo.react.ssr;

import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.options.NodeRuntimeOptions;
import com.caoccao.javet.node.modules.NodeModuleAny;
import com.caoccao.javet.node.modules.NodeModuleModule;
import com.caoccao.javet.node.modules.NodeModuleProcess;
import com.caoccao.javet.utils.JavetOSUtils;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.V8ValuePromise;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
public class SsrController {
    protected static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SsrController.class);
    protected static final Path ROOT_PATH =
            Path.of(JavetOSUtils.WORKING_DIRECTORY, "src-react", "dist", "build-ssr", "assets").normalize();

    static {
        NodeRuntimeOptions.NODE_FLAGS.setExperimentalRequireModule(true);
    }

    @GetMapping("/render-by-cjs")
    public String renderByCjs() {
        try (NodeRuntime nodeRuntime = V8Host.getNodeI18nInstance().createV8Runtime()) {
            nodeRuntime.getNodeModule(NodeModuleModule.class).setRequireRootDirectory(ROOT_PATH.toFile());
            nodeRuntime.getNodeModule(NodeModuleProcess.class).setWorkingDirectory(ROOT_PATH.toFile().getAbsolutePath());
            NodeModuleAny module = nodeRuntime.getNodeModule("./render.js", NodeModuleAny.class);
            try (V8ValueObject v8ValueObject = module.getModuleObject()) {
                return v8ValueObject.invokeString("render", "App");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to render by CJS.", e);
            return "Error rendering component: " + e.getMessage();
        }
    }

    @GetMapping("/render-by-esm")
    public String renderByEsm() {
        try (NodeRuntime nodeRuntime = V8Host.getNodeI18nInstance().createV8Runtime()) {
            nodeRuntime.setV8ModuleResolver(new SsrModuleResolver(ROOT_PATH));
            nodeRuntime.getExecutor("process.on('unhandledRejection', (reason, promise) => {\n" +
                    "    console.error('Unhandled Rejection at:', promise, 'reason:', reason);\n" +
                    "});").executeVoid();
            String codeString = "import { render } from './render.js';\n" +
                    "globalThis.html = render('App');";
            try (V8ValuePromise v8ValuePromise = nodeRuntime.getExecutor(codeString)
                    .setResourceName(ROOT_PATH.resolve("index.js").toString())
                    .setModule(true)
                    .execute()) {
                nodeRuntime.await();
                return nodeRuntime.getGlobalObject().getString("html");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to render by ESM.", e);
            return "Error rendering component: " + e.getMessage();
        }
    }
}
