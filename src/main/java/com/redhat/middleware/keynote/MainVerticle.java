package com.redhat.middleware.keynote;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) throws Exception {
        System.out.println("Starting Keynote Demo Vert.x verticles, default event loop pool size is " + VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE);
        JsonObject config = config();
        Integer gameVerticleInstances = config.getInteger("game-verticle-instances", 6);
        Integer trafficVerticleInstances = config.getInteger("traffic-verticle-instances", 1);
        Integer serverVerticleInstances = config.getInteger("server-verticle-instances", 6);
        Integer internalServerVerticleInstances = config.getInteger("internal-server-verticle-instances", 1);
        Integer scoreTimerVerticleInstances = config.getInteger("score-timer-verticle-instances", 1);
        Integer boardVerticleInstances = config.getInteger("board-verticle-instances", 1);

        DeploymentOptions serverDeploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setInstances(serverVerticleInstances);

        DeploymentOptions gameDeploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setInstances(gameVerticleInstances);

        DeploymentOptions internalDeploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setInstances(internalServerVerticleInstances);

        DeploymentOptions trafficDeploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setInstances(trafficVerticleInstances);

        DeploymentOptions scoreTimerDeploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setInstances(scoreTimerVerticleInstances);

        DeploymentOptions boardDeploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setInstances(boardVerticleInstances);
        ArrayList<Future> list = new ArrayList<>();
        list.add(deployVerticleIfNeeded("GameVerticle.groovy", gameDeploymentOptions));
        list.add(deployVerticleIfNeeded(ServerVerticle.class.getName(), serverDeploymentOptions));
        list.add(deployVerticleIfNeeded(InternalServiceVerticle.class.getName(), internalDeploymentOptions));
        list.add(deployVerticleIfNeeded(TrafficServerVerticle.class.getName(), trafficDeploymentOptions));
        list.add(deployVerticleIfNeeded(ScoreTimerVerticle.class.getName(), scoreTimerDeploymentOptions));
        list.add(deployVerticleIfNeeded(BurrSummary.class.getName(), scoreTimerDeploymentOptions));
        list.add(deployVerticleIfNeeded(BoardVerticle.class.getName(), boardDeploymentOptions));
        CompositeFuture
                .all(list)
                .setHandler(reporter(future));
    }

    private Future<Void> deployVerticleIfNeeded(String verticle, DeploymentOptions options) {
        Future<Void> future = Future.future();
        if (options.getInstances() == 0) {
            future.complete();
        } else {
            vertx.deployVerticle(verticle, options, reporter(future));
        }
        return future;
    }

    private <T> Handler<AsyncResult<T>> reporter(Future<Void> future) {
        return ar -> {
            if (ar.succeeded()) {
                future.complete();
            } else {
                ar.cause().printStackTrace();
                future.fail(ar.cause());
            }
        };
    }
}
