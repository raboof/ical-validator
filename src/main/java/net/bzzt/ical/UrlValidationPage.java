package net.bzzt.ical;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.wicketstuff.annotation.mount.MountPath;
import org.wicketstuff.annotation.strategy.MountMixedParam;

@MountPath(path = "urlvalidate")
@MountMixedParam(parameterNames = { "url" })
public class UrlValidationPage extends ValidatorLayoutPage {
	public UrlValidationPage(PageParameters parameters)
	{
		try {
			String url = (String) parameters.get("url");
			init(new URI(url, true));
		} catch (URIException e) {
			e.printStackTrace();
		}
	}

	private void init(URI uri) {
		GetMethod method = new GetMethod();
		try {
			method.setURI(uri);
		} catch (Exception e) {
			error(e);
		}

		HttpClient client = new HttpClient();
		try {
			client.executeMethod(method);
		} catch (Exception e) {
			error(e);
		}
		
		add(new UrlFormPanel("urlForm", uri));

		try {
			add(HomePage.getValidationResult("result", method.getResponseBodyAsStream(), method.getResponseCharSet()));
		} catch (Exception e) {
			add(new WebMarkupContainer("result"));
			error(e);
		}
	}
}
