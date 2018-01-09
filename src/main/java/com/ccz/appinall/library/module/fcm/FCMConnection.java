package com.ccz.appinall.library.module.fcm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;

public class FCMConnection {
	
	public interface OnFcmResultListener {
		public void onAck(String mid, String from);
		public void onNAck(String mid, String regid);
		public void onControlMessage(String controlMsg);
		public void onConnectionDraining();
		public void onStanzaError(String error);
	}
	
	XMPPConnection xmppConnection;
	OnFcmResultListener onFcmResultListener;
	
	protected volatile boolean connectionDraining = false;
	
	LoggingConnectionListener loginConnectionListener;
	FCMPacketListener fcmPacketListener;
	
    public boolean connect(String senderId, String apiKey, OnFcmResultListener onFcmResultListener) throws XMPPException, IOException {
    	this.onFcmResultListener = onFcmResultListener;
		try {			
			ConnectionConfiguration config = new ConnectionConfiguration(FCMConfig.getInst().getFcmUrl(), FCMConfig.getInst().getFcmPort());
			config.setSocketFactory(SSLSocketFactory.getDefault());
	        config.setSecurityMode(SecurityMode.enabled);
	        config.setCompressionEnabled(true);
	        config.setReconnectionAllowed(true);
	        config.setRosterLoadedAtLogin(false);
	        config.setSendPresence(false);
	        config.setDebuggerEnabled(false);
	        
	        xmppConnection = new XMPPConnection(config);
	        xmppConnection.addConnectionListener(loginConnectionListener = new LoggingConnectionListener());	        
	        xmppConnection.addPacketListener(fcmPacketListener = new FCMPacketListener(), new PacketTypeFilter(Message.class));
	        xmppConnection.connect();
	        xmppConnection.login(senderId + "@gcm.googleapis.com", apiKey);
	        return xmppConnection.isConnected();
		} catch (XMPPException e) {
			return false;
		}
    }
	private void send(String jsonRequest) {
        Packet request = new GcmPacketExtension(jsonRequest).toPacket();
        xmppConnection.sendPacket(request);
    }
	
	public void close() {
		if(xmppConnection==null||xmppConnection.isConnected()==false)
			return;
		xmppConnection.removeConnectionListener(loginConnectionListener);
		xmppConnection.removePacketListener(fcmPacketListener);
		xmppConnection.disconnect();
		
		loginConnectionListener = null;
		fcmPacketListener = null;
	}
	
	public boolean isConnected() {
		if(xmppConnection==null)
			return false;
		return xmppConnection.isConnected();
	}
	
	public boolean Send(String to, String msgId, Map<String, String> data, Map<String, String> noti) {
		try{
			if(xmppConnection == null || xmppConnection.isConnected() == false)
				return false;
			String jsonRequest = this.createJsonMessage(to, msgId, "", data, noti);
			this.send(jsonRequest);
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			data.clear();
			noti.clear();
		}
	}

	public boolean Send(String to, String msgId, String jsonData, Map<String, String> noti) {
		HashMap<String,String> data = new Gson().fromJson(jsonData, new TypeToken<HashMap<String, String>>(){}.getType());	
		try {
			if(xmppConnection == null || xmppConnection.isConnected() == false)
				return false;
			@SuppressWarnings("serial")
			String jsonRequest = this.createJsonMessage(to, msgId, "", data, noti);	
			this.send(jsonRequest);
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			data.clear();
			noti.clear();
		}
	}

	//below is referenced by https://github.com/aerofs/smack 
	/**
     * Creates a JSON encoded GCM message.
     *
     * @param to RegistrationId of the target device (Required).
     * @param messageId Unique messageId for which CCS will send an "ack/nack" (Required).
     * @param payload Message content intended for the application. (Optional).
     * @param collapseKey GCM collapse_key parameter (Optional).
     * @param timeToLive GCM time_to_live parameter (Optional).
     * @param delayWhileIdle GCM delay_while_idle parameter (Optional).
     * @return JSON encoded GCM message.
	 * @throws JsonProcessingException 
     */
	
	private String createJsonMessage(String to, String msgid, String collapseKey, Map<String, String> data, Map<String, String> noti) throws JsonProcessingException {
		Map<String, Object> message = new HashMap<>();
		message.put("to", to);
		message.put("data", data);
		message.put("priority", "high");
		message.put("content_available", true);		
		message.put("delay_while_idle", true);
		message.put("time_to_live", 86400);		// 1day available		
		message.put("message_id", String.format("%s%04d", msgid));
		if (collapseKey != null)
			message.put("collapse_key", collapseKey);
		if(noti!=null)
			message.put("notification", noti);
		return new ObjectMapper().writeValueAsString(message);
	}
	
	static final class LoggingConnectionListener implements ConnectionListener {
        @Override
        public void reconnectionSuccessful() {
            System.out.println("Reconnecting..");
        }

        @Override
        public void reconnectionFailed(Exception e) {
        	System.out.printf("Reconnection failed.. ", e);
        }

        @Override
        public void reconnectingIn(int seconds) {
        	System.out.printf("Reconnecting in %d secs", seconds);
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            System.out.println("Connection closed on error.");
        }

        @Override
        public void connectionClosed() {
            System.out.println("Connection closed.");
        }
    }
	
	class FCMPacketListener implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
            Message incomingMessage = (Message) packet;
            GcmPacketExtension gcmPacket = (GcmPacketExtension) incomingMessage.getExtension(FCMConfig.getInst().getNamespace());
            String json = gcmPacket.getJson();
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> jsonObject = (Map<String, Object>) JSONValue.parseWithException(json);

                // present for "ack"/"nack", null otherwise
                Object messageType = jsonObject.get("message_type");
                if (messageType == null) {
                    // Normal upstream data message
                    if(handleUpstreamMessage(jsonObject) == false) {
                    	onFcmResultListener.onStanzaError(packet.toXML());
                    	return;
                    }
                    // Send ACK to CCS
                    String messageId = (String) jsonObject.get("message_id");
                    String from = (String) jsonObject.get("from");
                    String ack = createJsonAck(from, messageId);
                    send(ack);
                } else if ("ack".equals(messageType.toString())) {
                    Object msgId = jsonObject.get("message_id");
                    Object from = jsonObject.get("from");
                    if(msgId!=null && from!=null)
                    	onFcmResultListener.onAck(msgId.toString(), from.toString());
                } else if ("nack".equals(messageType.toString())) {
                	Object msgId = jsonObject.get("message_id");
                    Object from = jsonObject.get("from");
                    if(msgId!=null && from!=null)
                    	onFcmResultListener.onNAck(msgId.toString(), from.toString());
                } else if ("control".equals(messageType.toString())) {
                	String controlType = (String) jsonObject.get("control_type");
                    if ("CONNECTION_DRAINING".equals(controlType)) {
                        connectionDraining = true;
                        onFcmResultListener.onConnectionDraining();
                    } else
                    	onFcmResultListener.onControlMessage(controlType);
                } else {
                    //logger.log(Level.WARNING, "Unrecognized message type (%s)", messageType.toString());
                }
            } catch (ParseException e) {
            } catch (Exception e) {
            }		
		}    	
		
	    /** 
	     * Handles an upstream data message from a device application.
	     *
	     * <p>This sample echo server sends an echo message back to the device.
	     * Subclasses should override this method to properly process upstream messages.
	     */    
		int addNo = 0;
	    private boolean handleUpstreamMessage(Map<String, Object> jsonObject) throws JsonProcessingException {
	        String category = (String) jsonObject.get("category");
	        String from = (String) jsonObject.get("from");
	        if(category == null)
	        	return false;
	        @SuppressWarnings("unchecked")
	        Map<String, String> payload = (Map<String, String>) jsonObject.get("data");
	        payload.put("ECHO", "Application: " + category);
	 
	        // Send an ECHO response back
	        String msgid = ("Msg" + System.currentTimeMillis()) + (++addNo % 1000);
	        String echo = createJsonMessage(from, msgid, "echo:collapseKey", payload, null);
	 
            sendDownstreamMessage(echo); 
	        return true;
	    }
	    
	    private boolean sendDownstreamMessage(String jsonRequest) {
	    	if (!connectionDraining) {
	    		send(jsonRequest);
	    		return true;
	    	}
	    	//logger.info("Dropping downstream message since the connection is draining");
	    	return false;
	    }
	    
		/**
	     * Creates a JSON encoded ACK message for an upstream message received
	     * from an application.
	     *
	     * @param to RegistrationId of the device who sent the upstream message.
	     * @param messageId messageId of the upstream message to be acknowledged to CCS.
	     * @return JSON encoded ack.
	     */
	    private String createJsonAck(String to, String messageId) {
	        Map<String, Object> message = new HashMap<String, Object>();
	        message.put("message_type", "ack");
	        message.put("to", to);
	        message.put("message_id", messageId);
	        return JSONValue.toJSONString(message);
	    }
    }
	
    class GcmPacketExtension extends DefaultPacketExtension {

        private final String json;
 
        public GcmPacketExtension(String json) {
            super(FCMConfig.getInst().getElementName(), FCMConfig.getInst().getNamespace());
            this.json = json;
        }
 
        public String getJson() {
            return json;
        }
 
        @Override
        public String toXML() {
            return String.format("<%s xmlns=\"%s\">%s</%s>", FCMConfig.getInst().getElementName(), 
            			FCMConfig.getInst().getNamespace(), StringUtils.escapeForXML(json), FCMConfig.getInst().getElementName());
        }
 
        public Packet toPacket() {
            Message message = new Message();
            message.addExtension(this);
            return message;
        }
    }
}
