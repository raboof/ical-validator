package net.bzzt.ical;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.wicketstuff.annotation.mount.MountPath;
import org.wicketstuff.annotation.strategy.MountMixedParam;

/**
 * Homepage
 */
@MountPath(path = "validate")
@MountMixedParam(parameterNames = { "url" })
public class HomePage extends ValidatorLayoutPage {

	private static final long serialVersionUID = 1L;
	
	private static final Log LOG = LogFactory.getLog(HomePage.class);

	
	// TODO Add any page properties or variables here

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
	public HomePage(final PageParameters parameters) {
		add(new UrlFormPanel("urlForm"));
		add(new FileFormPanel("fileForm"));
		add(new SnippetFormPanel("snippetForm", null, null));
		add(new WebMarkupContainer("results"));
	}

}
