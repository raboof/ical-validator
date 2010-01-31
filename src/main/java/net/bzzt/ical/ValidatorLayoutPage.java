package net.bzzt.ical;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;

public class ValidatorLayoutPage extends WebPage implements IHeaderContributor{

	@Override
	public void renderHead(IHeaderResponse response) {
		response.renderCSSReference(new ResourceReference(ValidatorLayoutPage.class, "style.css"));
	}

}
