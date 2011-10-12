package org.jabox.modules;

import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.panel.Panel;
import org.jabox.webapp.borders.MySiteBorder;
import org.jabox.webapp.panels.HeaderLinksPanel;

import com.google.inject.Binder;
import com.google.inject.Module;

public class JaboxWebappModule implements Module {

	public void configure(Binder binder) {
		binder.bind(Panel.class).to(HeaderLinksPanel.class);
		binder.bind(Border.class).to(MySiteBorder.class);
	}
}
