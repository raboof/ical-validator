package net.bzzt.ical;

import net.fortuna.ical4j.data.ParserException;

import org.apache.commons.lang.StringUtils;
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

	public ParserErrorPanel(String id, String calendarString, ParserException e1) {
		super(id);
		add(new Label("lineno", new Model<Integer>(e1.getLineNo())));
		add(new Label("error", new Model<String>(e1.getError())));
		
		String[] lines = StringUtils.split(calendarString, "\r\n");
		int start = Math.max(e1.getLineNo() - 5, 0);
		int end = Math.min(lines.length, e1.getLineNo() + 5);
		
		for (int i = start; i < end; i++)
		{
			info("" + i + ": " + lines[i]);
		}
		
		if (e1.getCause() != null)
		{
			LOG.info("Errorparserpanel: " + e1.getMessage(), e1);
		}
	}

}
