package net.bzzt.ical;

import net.fortuna.ical4j.util.validation.ValidationResult;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class SingleValidationResult extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SingleValidationResult(String id, ValidationResult model) {
		super(id);
		add(new Label("message", model.getText()));
		WebMarkupContainer link = new WebMarkupContainer("link");
		if (model.info != null && model.info.rfcSection != null) {
			link.add(new AttributeModifier("href", true, new Model<String>(
					"http://www.apps.ietf.org/rfc/rfc2445.html#sec-"
							+ model.info.rfcSection)));
			link.add(new Label("rfcSection", model.info.rfcSection));
		} else {
			link.setVisible(false);
		}
		add(link);
	}

}
