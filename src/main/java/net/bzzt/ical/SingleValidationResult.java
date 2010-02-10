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
		WebMarkupContainer rfcLink = new WebMarkupContainer("rfcLink");
		
		// http://tools.ietf.org/html/rfc5545
		
		if (model.info != null && model.info.getRfcSection() != null) {
			String url = "http://tools.ietf.org/html/rfc" + model.info.getRfcNumber() + ".html";
			link.add(new AttributeModifier("href", true, new Model<String>(
					url + "#section-"
					+ model.info.getRfcSection())));
			rfcLink.add(new AttributeModifier("href", true, new Model<String>(
					url)));
			link.add(new Label("rfcSection", model.info.getRfcSection()));
			rfcLink.add(new Label("rfcNumber", model.info.getRfcNumber()));
		} else {
			link.setVisible(false);
			rfcLink.setVisible(false);
		}
		add(link);
		add(rfcLink);
	}

}
