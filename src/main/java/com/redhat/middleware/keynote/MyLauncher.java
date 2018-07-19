package com.redhat.middleware.keynote;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MyLauncher extends Launcher {

    private static final Logger LOGGER = LogManager.getLogger(MyLauncher.class);

    private FileSystem fileSystem = null;

    public MyLauncher() {
        Vertx vertx = Vertx.vertx();
        fileSystem = vertx.fileSystem();
        Buffer sysPros = fileSystem.readFileBlocking("sys.json");
        JsonObject pros = sysPros.toJsonObject();
        pros.forEach(pro -> {
            String proName = pro.getKey();
            String proValue = (String) pro.getValue();
            System.setProperty(pro.getKey(), (String) pro.getValue());
            LOGGER.info("[{} = {}] => System.property ", proName, proValue);
        });
       /* System.setProperty("ACHIEVEMENTS_SERVER", "localhost");
        System.setProperty("ACHIEVEMENTS_SERVER_PORT", "9090");
        System.setProperty("SCORE_SERVER", "localhost");
        System.setProperty("SCORE_SERVER_PORT", "8080");
        System.setProperty("SCORE_PASSWORD", "kiewb");
        System.setProperty("SCORE_USER", "kiewb");
        System.setProperty("vertx.LOGGER-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
       */
    }

    public static void main(String[] args) {
        LOGGER.info(">>> >>> >>>  my game-server application start ! ");
        (new MyLauncher()).dispatch(args);
    }

    @Override
    public void afterConfigParsed(JsonObject config) {

    }

    @Override
    public void beforeStartingVertx(VertxOptions options) {
        options.setClustered(true)
                .setClusterHost("127.0.0.1");
    }

    @Override
    public void afterStartingVertx(Vertx vertx) {
    }

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        super.beforeDeployingVerticle(deploymentOptions);
        if (deploymentOptions.getConfig() == null)
            deploymentOptions.setConfig(new JsonObject());

        Buffer defaultConfig = fileSystem.readFileBlocking("config.json");
        if (defaultConfig != null && defaultConfig.length() > 0)
            deploymentOptions.getConfig().mergeIn(defaultConfig.toJsonObject());
        LOGGER.info("==>>默认配置： {} ", defaultConfig.toJsonObject().encodePrettily());
        Buffer droolsScore = fileSystem.readFileBlocking("drools-score.json");
        if (droolsScore != null && droolsScore.length() > 0)
            deploymentOptions.getConfig().mergeIn(droolsScore.toJsonObject());
        LOGGER.info("==>>流水线分数： {} ", droolsScore.toJsonObject().encodePrettily());
        Buffer droolsScoreSummary = fileSystem.readFileBlocking("drools-score-summary.json");
        if (droolsScoreSummary != null && droolsScoreSummary.length() > 0)
            deploymentOptions.getConfig().mergeIn(droolsScoreSummary.toJsonObject());
        LOGGER.info("==>>流水线分数汇总： {} ", droolsScoreSummary.toJsonObject().encodePrettily());

    }

}
