package net.bzzt.ical;

import net.fortuna.ical4j.data.ParserException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class ParserErrorPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Log LOG = LogFactory.getLog(ParserErrorPanel.class);

	public ParserErrorPanel(String id, ParserException e1) {
		super(id);
		add(new Label("lineno", new Model<Integer>(e1.getLineNo())));
		add(new Label("error", new Model<String>(e1.getError())));
		if (e1.getCause() != null)
		{
			LOG.info("Errorparserpanel: " + e1.getMessage(), e1);
		}
	}

}
