package com.caoccao.javet.demo.react.ssr;

import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.node.modules.NodeModuleAny;
import com.caoccao.javet.node.modules.NodeModuleModule;
import com.caoccao.javet.node.modules.NodeModuleProcess;
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
            Path.of(System.getProperty("user.dir"), "src-react", "dist", "build-ssr", "assets").normalize();

    @GetMapping("/render-by-cjs")
    public String renderByCjs() {
        // Create a Node.js runtime.
        try (NodeRuntime nodeRuntime =
                     V8Host.getNodeI18nInstance().createV8Runtime()) {
            // Tell Node.js where the root path of require() is.
            nodeRuntime.getNodeModule(NodeModuleModule.class)
                    .setRequireRootDirectory(ROOT_PATH.toFile());
            // Set the working directory for Node.js.
            nodeRuntime.getNodeModule(NodeModuleProcess.class)
                    .setWorkingDirectory(ROOT_PATH.toString());
            // Call require('./render.js') to load the SSR render module.
            NodeModuleAny module =
                    nodeRuntime.getNodeModule("./render.js", NodeModuleAny.class);
            try (V8ValueObject v8ValueObject = module.getModuleObject()) {
                // Call render('App') and return the result as a String.
                return v8ValueObject.invokeString("render", "App");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to render by CJS.", e);
            return "Error rendering component: " + e.getMessage();
        }
    }

    @GetMapping("/render-by-esm")
    public String renderByEsm() {
        // Create a Node.js runtime.
        try (NodeRuntime nodeRuntime =
                     V8Host.getNodeI18nInstance().createV8Runtime()) {
            // Set the SSR module resolver.
            nodeRuntime.setV8ModuleResolver(new SsrModuleResolver(ROOT_PATH));
            // Handle the event unhandledRejection, otherwise the Node.js process will exit immediately.
            nodeRuntime.getExecutor(
                    "process.on('unhandledRejection', (reason, promise) => {\n" +
                            "    console.error('Unhandled Rejection at:', promise, 'reason:', reason);\n" +
                            "});").executeVoid();
            // Prepare the code to call the render function.
            String codeString =
                    "import { render } from './render.js';\n" +
                            "globalThis.html = render('App');";
            // Execute the code as a module.
            try (V8ValuePromise v8ValuePromise = nodeRuntime.getExecutor(codeString)
                    .setResourceName(ROOT_PATH.resolve("index.js").toString())
                    .setModule(true)
                    .execute()) {
                // Pump the Node.js event loop.
                nodeRuntime.await();
                // Return the result as a String.
                return nodeRuntime.getGlobalObject().getString("html");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to render by ESM.", e);
            return "Error rendering component: " + e.getMessage();
        }
    }
}
