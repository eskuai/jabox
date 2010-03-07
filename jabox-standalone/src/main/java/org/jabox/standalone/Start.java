/*
 * The MIT License
 *
 * Copyright (c) 2009 Dimitris Kapanidis
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jabox.standalone;

import java.util.List;

import org.jabox.apis.embedded.EmbeddedServer;
import org.jabox.utils.WebappManager;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.Resource;

public class Start {
	private static final String PACKAGE = "/jabox-webapp/";

	public Start() {
		startEmbeddedJetty(false);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println(getJaboxWebapp());
		startEmbeddedJetty(true);
	}

	private static String getJaboxWebapp() {
		Resource res = Resource.newClassPathResource(PACKAGE);
		// String res =
		// "D:\\Documents\\My Developments\\Jabox\\workspace-jabox\\jabox\\jabox-webapp\\target\\jabox-webapp-0.0.4-SNAPSHOT\\";
		return res.toString();
	}

	/**
	 * 
	 * @param startJabox
	 *            If set to true the Jetty application is starting Jabox
	 *            Application together with the embeddedServers.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static void startEmbeddedJetty(final boolean startJabox) {
		Server server = new Server();
		SocketConnector connector = new SocketConnector();
		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(9090);
		server.setConnectors(new Connector[] { connector });
		try {
			List<String> webapps = WebappManager.getWebapps();
			for (String webapp : webapps) {
				addEmbeddedServer(server, webapp);
			}

			if (startJabox) {
				// Adding ROOT handler.
				// NOTE: This should be added last on server.
				WebAppContext bb = new WebAppContext();
				bb.setServer(server);
				bb.setContextPath("/");
				bb.setWar(getJaboxWebapp());
				server.addHandler(bb);
			}

			System.out
					.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
			server.start();
			if (startJabox) {
				while (System.in.available() == 0) {
					Thread.sleep(5000);
				}
				server.stop();
				server.join();

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
	}

	/**
	 * Helper function to add an embedded Server using the className to the
	 * running Jetty Server.
	 * 
	 * @param server
	 *            The Jetty server.
	 * @param className
	 *            The className of the EmbeddedServer.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private static void addEmbeddedServer(final Server server,
			final String className) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		EmbeddedServer es = (EmbeddedServer) Class.forName(className)
				.newInstance();
		es.addWebAppContext(server);
	}
}
