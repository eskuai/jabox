/*
 * Jabox Open Source Version
 * Copyright (C) 2009-2010 Dimitris Kapanidis                                                                                                                          
 * 
 * This file is part of Jabox
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package org.jabox.its.jtrac;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.jabox.apis.its.ITSConnector;
import org.jabox.apis.scm.SCMConnectorConfig;
import org.jabox.model.DeployerConfig;
import org.jabox.model.Project;
import org.jabox.model.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class JtracRepository implements ITSConnector<JtracRepositoryConfig>,
		Serializable {
	private static final Logger LOGGER = LoggerFactory
	.getLogger(JtracRepository.class);

	private static final long serialVersionUID = 8131183843391948936L;
	public static final String ID = "plugin.its.jtrac";

	private String _url;
	private final WebConversation _wc;

	public String getName() {
		return "Jtrac";
	}

	public String getId() {
		return ID;
	}

	@Override
	public String toString() {
		return getName();
	}

	public JtracRepository() {
		_wc = new WebConversation();
	}

	public void setUrl(final String url) {
		_url = url;
	}

	public boolean login(final JtracRepositoryConfig config)
			throws MalformedURLException, IOException, SAXException {
		LOGGER.info("jtrac Login: " + config.getUsername());

		WebRequest req = new GetMethodWebRequest(config.getServer().getUrl());
		WebResponse resp = _wc.getResponse(req);
		WebForm form = resp.getForms()[0]; // select the first form in the page
		form.setParameter("j_username", config.getUsername());
		form.setParameter("j_password", config.getPassword());
		resp = form.submit();

		// TODO check if not logged in.
		return true;
	}

	public boolean addProject(final Project project,
			final JtracRepositoryConfig config) throws IOException,
			SAXException {
		LOGGER.info("jtrac add Project: " + project.getName());

		WebRequest req = new GetMethodWebRequest(_url
				+ "/editproducts.cgi?action=add");
		WebResponse resp = _wc.getResponse(req);
		WebForm form = resp.getForms()[0];

		form.setParameter("product", project.getName());
		form.setParameter("description", project.getDescription());
		form.submit();
		return true;
	}

	public boolean addModule(final Project project,
			final JtracRepositoryConfig config, final String module,
			final String description, final String initialOwner)
			throws SAXException, IOException {
		LOGGER.info("jtrac add Module: " + project.getName());

		String url = config.getServer().getUrl();
		WebRequest req = new GetMethodWebRequest(url
				+ "/editcomponents.cgi?action=add&product=" + project.getName());
		WebResponse resp = _wc.getResponse(req);
		WebForm form = resp.getForms()[0];

		form.setParameter("component", module);
		form.setParameter("description", description);
		form.setParameter("initialowner", initialOwner);
		form.submit();
		return true;
	}

	public boolean addVersion(final Project project,
			final JtracRepositoryConfig config, final String version)
			throws IOException, SAXException {
		LOGGER.info("jtrac add Version: " + version);

		String url = config.getServer().getUrl();
		WebRequest req = new GetMethodWebRequest(url
				+ "/editversions.cgi?action=add&product=" + project.getName());
		WebResponse resp = _wc.getResponse(req);
		WebForm form = resp.getForms()[0];

		form.setParameter("version", version);
		form.submit();
		return true;
	}

	public DeployerConfig newConfig() {
		return new JtracRepositoryConfig();
	}

	public Component newEditor(final String id, final IModel<Server> model) {
		return new JtracRepositoryEditor(id, model);
	}

	public void addRepository(Project project, JtracRepositoryConfig config,
			SCMConnectorConfig scmConfig, String username, String password)
			throws MalformedURLException, IOException, SAXException {
	}

}
