package com.inso.framework.mail;

public class MailConfig {
		private String host;
		private int port;
		private String username;
		private String password;
		private boolean auth;
		private String fromAddress; //email address
		private String fromName;
		private String bounceAddress; 
		private String defaultCharset;
		public boolean isStartTlsEnabled() {
			return startTlsEnabled;
		}
		public void setStartTlsEnabled(boolean startTlsEnabled) {
			this.startTlsEnabled = startTlsEnabled;
		}
		public boolean isStartTlsRequired() {
			return startTlsRequired;
		}
		public void setStartTlsRequired(boolean startTlsRequired) {
			this.startTlsRequired = startTlsRequired;
		}
		public boolean isSslOnConnect() {
			return sslOnConnect;
		}
		public void setSslOnConnect(boolean sslOnConnect) {
			this.sslOnConnect = sslOnConnect;
		}
		public boolean isSslCheckServerIdentity() {
			return sslCheckServerIdentity;
		}
		public void setSslCheckServerIdentity(boolean sslCheckServerIdentity) {
			this.sslCheckServerIdentity = sslCheckServerIdentity;
		}
		private int timeout;
		
	    private boolean startTlsEnabled;  //false
	    private boolean startTlsRequired; //false

	    /** does the current transport use SSL/TLS encryption upon connection? */
	    private boolean sslOnConnect; //false;

	    /**
	     * If set to true, check the server identity as specified by RFC 2595. These
	     * additional checks based on the content of the server's certificate are
	     * intended to prevent man-in-the-middle attacks.
	     * Defaults to false.
	     */
	    private boolean sslCheckServerIdentity; //false;

		
		public String getDefaultCharset() {
			return defaultCharset;
		}
		public void setDefaultCharset(String defaultCharset) {
			this.defaultCharset = defaultCharset;
		}
		
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public boolean isAuth() {
			return auth;
		}
		public void setAuth(boolean auth) {
			this.auth = auth;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
		public int getTimeout() {
			return timeout;
		}
		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}
		public String getFromAddress() {
			return fromAddress;
		}
		public void setFromAddress(String fromAddress) {
			this.fromAddress = fromAddress;
		}
		public String getFromName() {
			return fromName;
		}
		public void setFromName(String fromName) {
			this.fromName = fromName;
		}
		public String getBounceAddress() {
			return bounceAddress;
		}
		public void setBounceAddress(String bounceAddress) {
			this.bounceAddress = bounceAddress;
		}
}
