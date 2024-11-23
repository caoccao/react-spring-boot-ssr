package com.caoccao.javet.demo.react.ssr;

import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.utils.JavetOSUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValuePromise;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
public class SsrController {
    @GetMapping("/render")
    public String render() {
        try (NodeRuntime nodeRuntime = V8Host.getNodeI18nInstance().createV8Runtime()) {
            Path rootPath = Path.of(JavetOSUtils.WORKING_DIRECTORY, "src-react", "dist", "assets");
            nodeRuntime.setV8ModuleResolver(new SsrModuleResolver(rootPath));
            nodeRuntime.getExecutor("process.on('unhandledRejection', (reason, promise) => {\n" +
                    "    console.log('Unhandled Rejection at:', promise, 'reason:', reason);\n" +
                    "});").executeVoid();
            String codeString = "import { render } from './render.js';\n" +
                    "render('App');";
            try (V8ValuePromise v8ValuePromise = nodeRuntime.getExecutor(codeString)
                    .setResourceName(rootPath.resolve("index.js").toString())
                    .setModule(true)
                    .execute()) {
                nodeRuntime.await();
                try (V8Value v8Value = v8ValuePromise.getResult()) {
                    System.out.println(v8Value);
                }
            }
            return codeString;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return "Error rendering component: " + e.getMessage();
        }
    }
}
