package org.chaynik.wheely.service;

import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class SocketListener extends WebSocketAdapter {
    public static final String TYPE_MESSAGE = "message";

    public void onTextMessage(WebSocket websocket, String message) {
        Log.d(TYPE_MESSAGE,message);

    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) {

    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
        Log.d(TYPE_MESSAGE, "onDisconnected!");
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        Log.d(TYPE_MESSAGE, "Connected!");
        JSONObject json = new JSONObject();
        try {
            json.put("lon", 55.755826);
            json.put("lat", 37.6173);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        websocket.sendText(json.toString());
    }

}
