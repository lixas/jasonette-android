package com.jasonette.seed.Service.websocket;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.jasonette.seed.Core.JasonViewActivity;
import com.jasonette.seed.Launcher.Launcher;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.ByteString;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by e on 11/3/17.
 */

public class JasonWebsocketService {
    private final class JasonWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            final JasonViewActivity context = ((JasonViewActivity) launcher.getCurrentContext());
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JasonViewActivity context = ((JasonViewActivity) launcher.getCurrentContext());
                    context.simple_trigger("$websocket.onopen", new JSONObject(), context);
                }
            });
        }
        @Override
        public void onMessage(WebSocket webSocket, final String text) {
            final JasonViewActivity context = ((JasonViewActivity) launcher.getCurrentContext());
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject response = new JSONObject();
                        JSONObject message = new JSONObject();
                        message.put("message", text);
                        message.put("type", "string");
                        response.put("$jason", message);
                        context.simple_trigger("$websocket.onmessage", response, context);
                    } catch (Exception e) {

                    }
                }
            });
        }
        @Override
        public void onMessage(WebSocket webSocket, final ByteString bytes) {
            final JasonViewActivity context = ((JasonViewActivity) launcher.getCurrentContext());
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject response = new JSONObject();
                        JSONObject message = new JSONObject();
                        message.put("message", bytes.hex());
                        message.put("type", "bytes");
                        response.put("$jason", message);
                        context.simple_trigger("$websocket.onmessage", response, context);
                    } catch (Exception e) {

                    }
                }
            });
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            final JasonViewActivity context = ((JasonViewActivity) launcher.getCurrentContext());
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JasonViewActivity context = ((JasonViewActivity) launcher.getCurrentContext());
                    context.simple_trigger("$websocket.onclose", new JSONObject(), context);
                }
            });
        }
        @Override
        public void onFailure(WebSocket webSocket, final Throwable t, Response response) {
            final JasonViewActivity context = ((JasonViewActivity) launcher.getCurrentContext());
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JasonViewActivity context = ((JasonViewActivity) launcher.getCurrentContext());
                        JSONObject res = new JSONObject();
                        JSONObject message = new JSONObject();
                        message.put("error", t.getMessage());
                        res.put("$jason", message);
                        context.simple_trigger("$websocket.onerror", res, context);
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    private JasonWebSocketListener listener;
    private Launcher launcher;
    private WebSocket ws;
    private Thread thread;

    public JasonWebsocketService(Launcher launcherParam) {
        launcher = launcherParam;
    }

    public void open(final JSONObject action) {
        try {
            JSONObject options = action.getJSONObject("options");
            String url = options.getString("url");
            OkHttpClient client = launcher.getHttpClient();
            Request request = new Request.Builder().url(url).build();
            listener = new JasonWebSocketListener();
            ws = client.newWebSocket(request, listener);
            client.dispatcher().executorService().shutdown();
        } catch (Exception e) {
            Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
        }
    }
    public void close() {
        ws.close(1000, "Goodbye!");
    }
    public void send(JSONObject action) {
        try {
            JSONObject options = action.getJSONObject("options");
            String text = options.getString("text");
            ws.send(text);
        } catch (Exception e) {
            Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
        }
    }


}