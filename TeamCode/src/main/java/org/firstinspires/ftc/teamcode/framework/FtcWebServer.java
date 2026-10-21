package org.firstinspires.ftc.teamcode.framework;

import com.youfuns.webserver.WebServer;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class FtcWebServer {
    public static WebServer<?, ?, ?> initServer(int initialPort, Telemetry telemetry) {
        WebServer<?, ?, ?> server = null;
        int port = initialPort;
        while (server == null) {
            try {
                server = WebServer.create(port);
            } catch (Exception e) {
                port++;
            }
        }
        telemetry.addLine("Port: " + port);
        telemetry.update();
        return server;
    }
}
