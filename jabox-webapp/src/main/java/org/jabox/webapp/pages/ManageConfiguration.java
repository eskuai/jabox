package org.jabox.webapp.pages;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.persistence.provider.GeneralDao;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jabox.apis.Connector;
import org.jabox.apis.Manager;
import org.jabox.apis.bts.BTSConnector;
import org.jabox.apis.cis.CISConnector;
import org.jabox.apis.rms.RMSConnector;
import org.jabox.apis.scm.SCMConnector;
import org.jabox.model.Configuration;
import org.jabox.webapp.borders.NavomaticBorder;

/**
 * Homepage
 */
public class ManageConfiguration extends WebPage {

	private static final long serialVersionUID = 1L;

	@SpringBean
	protected GeneralDao _generalDao;

	@SpringBean
	protected Manager<Connector> _manager;

	public ManageConfiguration() {
		final Configuration configuration = _generalDao.getConfiguration();
		NavomaticBorder navomaticBorder = new NavomaticBorder("navomaticBorder");
		add(navomaticBorder);
		FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
		navomaticBorder.add(feedbackPanel);
		Form form = new Form("form") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				info("Saving Configuration: " + configuration);
				_generalDao.persist(configuration);
			}
		};
		CompoundPropertyModel model = new CompoundPropertyModel(configuration);
		form.setModel(model);
		form.add(new RequiredTextField("issueManagementUrl"));
		// DeployerPluginSelector child = new DeployerPluginSelector(
		// "configuration", model);
		// form.add(child);

		// DeployerPlugin plugin = registry.getEntry((String) pluginId);
		// configuration.setDeployerConfig(plugin
		// .newConfig());
		//
		// child.replace(plugin.newEditor("editor",
		// new PropertyModel(configuration, "deployerConfig")));
		SCMConnector.class.getSimpleName();
		add(configuration, form, SCMConnector.class);
		add(configuration, form, BTSConnector.class);
		add(configuration, form, CISConnector.class);
		add(configuration, form, RMSConnector.class);
		navomaticBorder.add(form);
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
	}

	private void add(final Configuration configuration, final Form form,
			Class<? extends Connector> connector) {
		List<Connector> connectors = _manager.getConnectors(connector);
		System.out.println("connectors: " + connector.getName() + ":"
				+ connectors);

		DropDownChoice ddc = new DropDownChoice(connector.getSimpleName(),
				new PropertyModel(configuration, connector.getSimpleName()), connectors,
				new ChoiceRenderer("toString", "toString"));
		form.add(ddc);
	}
}
