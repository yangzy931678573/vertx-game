import groovy.json.JsonBuilder
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler

class ServerScript extends AbstractVerticle {
    @Override
    void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx)

        router.route().handler(BodyHandler.create())
        router.route().handler(CorsHandler.create("*"))

        def defaultHandler = {
            HttpServerRequest request = it.request()
            HttpServerResponse response = it.response()
            def respResult = new JsonBuilder()
            respResult.state{
                type 'SUCCESS'
                result  {
                    'execution-results' {
                        results '',''
                    }
                }
            }
            response.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            response.end(respResult.toString())

        }
        router.post('kie-server/services/rest/server/containers/instances/score')
                .handler(defaultHandler)

        vertx.createHttpServer()
                .requestHandler({ router.accept(it) })
                .listen(8080, { ar ->
            if (ar.succeeded()) {
                println "Score server started on 127.0.0.1:${ar.result().actualPort()}"
                startFuture.complete()
            } else {
                startFuture.fail(ar.cause())
            }
        })
    }
}
