package com.ccz.appinall.library.module.fcm;

import com.ccz.appinall.library.util.KeyGen;
import com.ccz.appinall.services.controller.auth.AuthCommandAction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;

@Slf4j
public class FCMConnection {
	private static Logger logger = Logger.getLogger(FCMConnection.class.getName());
	
	private static final String GCM_ELEMENT_NAME = "gcm";
    private static final String GCM_NAMESPACE = "google:mobile:data";
    
	private String senderId, senderKey;
	
	private XMPPConnection xmppConnection;
	private boolean connectionDraining = false;
	
	private LoggingConnectionListener loggingConnectionListener;
	private PacketListener packetListener;
	
	private FCMResultListener onFCMResultListener;
	
	private int failCount = 0;
	
	private final int TIME_TO_LIVE = 345600;// 4 WEEKS
	private final String PUSH_SOUND = "none.mp4";
	
	ObjectMapper objectMapper = new ObjectMapper();	//[note] not thread-safe for deserialization
	
	public FCMConnection(String senderId, String senderKey, FCMResultListener resultListener) {
		this.senderId = senderId;
		this.senderKey = senderKey;
		onFCMResultListener = resultListener;
	}	
	
	public boolean connectServer(String fcmUrl, int fcmPort) throws SmackException, IOException {
		try {			
			ConnectionConfiguration config = new ConnectionConfiguration(fcmUrl, fcmPort);
	        config.setSecurityMode(SecurityMode.enabled);
	        config.setReconnectionAllowed(true);
	        config.setRosterLoadedAtLogin(false);
	        config.setSendPresence(false);
	        config.setSocketFactory(SSLSocketFactory.getDefault());
	        config.setDebuggerEnabled(false);
	        
	        loggingConnectionListener = new LoggingConnectionListener();
	        packetListener = new FCMPacketListener();
	        
	        xmppConnection = new XMPPTCPConnection(config);
	        xmppConnection.connect();
	        xmppConnection.addConnectionListener(loggingConnectionListener);	        
	        xmppConnection.addPacketListener(packetListener, new PacketTypeFilter(Message.class));
	        xmppConnection.login(senderId + "@gcm.googleapis.com", senderKey);

	        return xmppConnection.isConnected();
		} catch (XMPPException e) {
			return false;
		}
	}
	
	public void closeServer() throws NotConnectedException {
		if(xmppConnection==null || xmppConnection.isConnected()==false)
			return;
		if(loggingConnectionListener!=null)
			xmppConnection.removeConnectionListener(loggingConnectionListener);
		if(packetListener!=null)
			xmppConnection.removePacketListener(packetListener);
		xmppConnection.disconnect();
		
		loggingConnectionListener = null;
		packetListener = null;
	}
	
	public boolean isConnected() {
		if(xmppConnection==null)
			return false;
		return xmppConnection.isConnected();
	}
	
	public boolean send(String to, String msgid, String json, int badgeCount) throws IOException {
		if(xmppConnection == null || xmppConnection.isConnected() == false)
			return false;
		JsonNode jnode = objectMapper.readTree(json);
		String dataPayload = jnode.get("data").asText();
		
		String notiTitle = "";
		String notiBody  = "";
		if(jnode.has("notification")) {
			JsonNode notiNode = jnode.get("notification");
			notiTitle = notiNode.get("alarmTitle").asText();
			notiBody = notiNode.get("alarmBody").asText();
		}
		
		try	{
			String jsonMsg = this.createJsonMessage(to, msgid, dataPayload, notiTitle, notiBody, badgeCount, PUSH_SOUND);
			this.sendDownstreamMessage(jsonMsg);
			failCount = 0;
		} catch (NotConnectedException e) {		
			log.error("***** FCM Connection Failed *****");
			e.printStackTrace();
			if(++failCount < 5)
				onFCMResultListener.OnCloseConnectionOnSending(to, msgid, json);
			return false;
		}
		return true;
	}
	
	public boolean send(String[] tos, String msgId, String jsonPush, int badgeCount) throws IOException {
		for(String to: tos)
			send(to, msgId, jsonPush, badgeCount);		
		return true;
	}
	
	private void send(String jsonRequest) throws NotConnectedException {
        Packet request = new GcmPacketExtension(jsonRequest).toPacket();
        xmppConnection.sendPacket(request);
    }

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
	
	private String createJsonMessage(String to, String messageId,String data, String notiTitle, String notiContent, int badgeCount, String sound) throws JsonProcessingException {
		Map<String, String> notiPayload = null;
		Map<String, Object> dataPayload = makeBasicPushPayload(messageId, null);
		dataPayload.put("to", to);
		dataPayload.put("data", data);
		if( (notiTitle != null && notiTitle.length() > 0) || (notiContent != null && notiContent.length() > 0) ) {
			notiPayload = makeNotificationPayload(notiTitle, notiContent, badgeCount, sound);
			dataPayload.put("notification", notiPayload);
		}
		String jsonPushMessage = objectMapper.writeValueAsString(dataPayload);
		dataPayload.clear();
		if(notiPayload != null)
			notiPayload.clear();
		return jsonPushMessage;
	}
	
	static int autoIncNumber = 0;
	private Map<String, Object> makeBasicPushPayload(String messageId, String collapseKey) {
		Map<String, Object> message = new HashMap<String, Object>();		
		message.put("priority", "high");
		message.put("content_available", true);		
		message.put("delay_while_idle", true);
		message.put("time_to_live", TIME_TO_LIVE);	//time to live is second values		
		message.put("message_id", String.format("%s%04d", messageId, ++autoIncNumber%10000));
		if (collapseKey != null)
			message.put("collapse_key", collapseKey);
		return message;
	}

	private Map<String, String> makeNotificationPayload(String notiTitle, String notiContent, int badgeCount, String sound) {
		Map<String, String> notiPayload = new HashMap<String, String>();
		notiPayload.put("title", notiTitle);
		notiPayload.put("body",  notiContent);
		if(badgeCount>0)
			notiPayload.put("badge", badgeCount+"");
		if(sound!=null && sound.length()>0)
			notiPayload.put("sound", sound);
		return notiPayload;
	}   
	
    private boolean sendDownstreamMessage(String jsonRequest) throws	NotConnectedException {
	    	if (!connectionDraining) {
	    		send(jsonRequest);
	    		return true;
	    	}
	    	//logger.info("Dropping downstream message since the connection is draining");
	    	return false;
    }

    class FCMPacketListener implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
            //logger.log(Level.INFO, "Received: " + packet.toXML());
            Message incomingMessage = (Message) packet;
            GcmPacketExtension gcmPacket = (GcmPacketExtension) incomingMessage.getExtension(GCM_NAMESPACE);
            String json = gcmPacket.getJson();
            try {
                @SuppressWarnings("unchecked")
                ObjectMapper mapper = new ObjectMapper();

                Map<String, Object> jmap = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});//(Map<String, Object>) JSONValue.parseWithException(json);

                // present for "ack"/"nack", null otherwise
                Object messageType = jmap.get("message_type");

                if (messageType == null) {
                    // Normal upstream data message
                    if(handleUpstreamMessage(jmap) == false) {
                    		onFCMResultListener.OnStanzaError(packet.toXML().toString());
                    		return;
                    }

                    // Send ACK to CCS
                    String messageId = (String) jmap.get("message_id");
                    String from = (String) jmap.get("from");
                    String ack = createJsonAck(from, messageId);
                    send(ack);
                } else if ("ack".equals(messageType.toString())) {
                    handleAckReceipt(jmap);
                } else if ("nack".equals(messageType.toString())) {
                    handleNackReceipt(jmap);
                } else if ("control".equals(messageType.toString())) {
                    handleControlMessage(jmap);
                } else {
                    //logger.log(Level.WARNING, "Unrecognized message type (%s)", messageType.toString());
                }
            } catch (Exception e) {
            }		
		}
	    /** 
	     * Handles an upstream data message from a device application.
	     *
	     * <p>This sample echo server sends an echo message back to the device.
	     * Subclasses should override this method to properly process upstream messages.
	     */    
	    private boolean handleUpstreamMessage(Map<String, Object> jsonObject) {
	        // PackageName of the application that sent this message.
	        String category = (String) jsonObject.get("category");
	        String from = (String) jsonObject.get("from");
	        if(category == null)
	        		return false;
	        @SuppressWarnings("unchecked")
	        Map<String, String> payload = (Map<String, String>) jsonObject.get("data");
	        payload.put("ECHO", "Application: " + category);
	 
	        //private String createJsonMessage(String to, String messageId,String data, String notiTitle, String notiContent, int badgeCount, String sound) throws JsonProcessingException {
	        // Send an ECHO response back
	        String echo = createUpstreamJsonMessage(from, KeyGen.makeKey("msg-"), "echo:CollapseKey", payload);
	 
	        try {
	            sendDownstreamMessage(echo); 
	        } catch (NotConnectedException e) {
	            //logger.log(Level.WARNING, "Not connected anymore, echo message is not sent", e);
	        }
	        return true;
	    }
		
		private String createUpstreamJsonMessage(String to, String messageId, String collapseKey, Map<String, String> data) {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> message = makeBasicPushPayload(messageId, collapseKey);
			message.put("to", to);
			message.put("data", data);
			try {
				return mapper.writeValueAsString(message);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return "";
			}
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
	    		ObjectMapper mapper = new ObjectMapper();
	        Map<String, Object> message = new HashMap<String, Object>();
	        message.put("message_type", "ack");
	        message.put("to", to);
	        message.put("message_id", messageId);
	        try {
				return mapper.writeValueAsString(message);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return "";
			}
	    }	    
	    /**
	     * Handles an ACK.
	     *
	     * <p>Logs a INFO message, but subclasses could override it to
	     * properly handle ACKs.
	     */
	    private void handleAckReceipt(Map<String, Object> jsonObject) {
	        String msgeId = (String) jsonObject.get("message_id");
	        String from = (String) jsonObject.get("from");
	        onFCMResultListener.OnAck(msgeId, from);
	    }
	    
	    /**
	     * Handles a NACK.
	     *
	     * <p>Logs a INFO message, but subclasses could override it to
	     * properly handle NACKs.
	     */
	    private void handleNackReceipt(Map<String, Object> jsonObject) {
	        String msgId = (String) jsonObject.get("message_id");
	        String from = (String) jsonObject.get("from");
	        onFCMResultListener.OnNAck(msgId, from);
	    }
	 
	    private void handleControlMessage(Map<String, Object> jsonObject) {
	        //logger.log(Level.INFO, "handleControlMessage(): " + jsonObject);
	        String controlType = (String) jsonObject.get("control_type");
	        if ("CONNECTION_DRAINING".equals(controlType)) {
	            connectionDraining = true;
	            onFCMResultListener.OnConnectionDraining(FCMConnection.this);
	        } else
	        	onFCMResultListener.OnControlMessage(controlType);
	    }

    }
    
    private class GcmPacketExtension extends DefaultPacketExtension {

        private final String json;
 
        public GcmPacketExtension(String json) {
            super(GCM_ELEMENT_NAME, GCM_NAMESPACE);
            this.json = json;
        }
 
        public String getJson() {
            return json;
        }
 
        @Override
        public String toXML() {
            return String.format("<%s xmlns=\"%s\">%s</%s>", GCM_ELEMENT_NAME, GCM_NAMESPACE, StringUtils.escapeForXML(json), GCM_ELEMENT_NAME);
        }
 
        public Packet toPacket() {
            Message message = new Message();
            message.addExtension(this);
            return message;
        }
    }
 
    private class LoggingConnectionListener implements ConnectionListener {
 
        @Override
        public void reconnectionSuccessful() {
        		logger.info("reconnectionSuccessful()");
        }
 
        @Override
        public void reconnectionFailed(Exception e) {
        		logger.info("reconnectionFailed()");
        }
 
        @Override
        public void reconnectingIn(int seconds) {
        		logger.info("reconnectingIn()");
        }
 
        @Override
        public void connectionClosedOnError(Exception e) {
        		logger.info("connectionClosedOnError()");
        }
 
        @Override
        public void connectionClosed() {
        		logger.info("connectionClosed()");
        }

		@Override
		public void connected(XMPPConnection arg0) {
			logger.info("connected()");
		}

		@Override
		public void authenticated(XMPPConnection connection) {
			logger.info("authenticated()");
		}
    }
}
