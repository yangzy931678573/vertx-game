package com.redhat.middleware.keynote;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;

public class MyLauncher extends Launcher {
    public MyLauncher() {
        System.setProperty("ACHIEVEMENTS_SERVER", "localhost");
        System.setProperty("ACHIEVEMENTS_SERVER_PORT", "9090");
        System.setProperty("SCORE_SERVER", "localhost");
        System.setProperty("SCORE_SERVER_PORT", "8080");
        System.setProperty("SCORE_PASSWORD", "kiewb");
        System.setProperty("SCORE_USER", "kiewb");
    }

    public static void main(String[] args) {
        System.out.println(">>> >>> >>>  The vert.x  application start ! ");
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
        Vertx vertx = Vertx.vertx();
        FileSystem fileSystem = vertx.fileSystem();
        Buffer defaultConfig = fileSystem.readFileBlocking("config.json");
        if (defaultConfig != null && defaultConfig.length() > 0)
            deploymentOptions.getConfig().mergeIn(defaultConfig.toJsonObject());
        Buffer droolsScore = fileSystem.readFileBlocking("drools-score.json");
        if (droolsScore != null && droolsScore.length() > 0)
            deploymentOptions.getConfig().mergeIn(droolsScore.toJsonObject());
        Buffer droolsScoreSummary = fileSystem.readFileBlocking("drools-score-summary.json");
        if (droolsScoreSummary != null && droolsScoreSummary.length() > 0)
            deploymentOptions.getConfig().mergeIn(droolsScoreSummary.toJsonObject());
        System.out.println(defaultConfig.toJsonObject().encodePrettily());
        System.out.println(droolsScore.toJsonObject().encodePrettily());
        System.out.println(droolsScoreSummary.toJsonObject().encodePrettily());
    }

}
