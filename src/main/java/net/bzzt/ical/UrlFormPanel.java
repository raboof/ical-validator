package net.bzzt.ical;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

public class UrlFormPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class UrlForm extends Form<UrlForm> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private static final Log LOG = LogFactory.getLog(UrlForm.class);
		
		private URI url;
		
		public UrlForm(String id, URI url) {
			super(id);
			this.url = url;
			setDefaultModel(new CompoundPropertyModel<UrlForm>(this));

			add(new TextField<URI>("url"));
			add(new FeedbackPanel("feedback"));
		}

		/* (non-Javadoc)
		 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		protected void onSubmit() {
			URI uri = UrlForm.this.getModelObject().url;

			if ("webcal".equals(uri.getScheme()))
			{
				try {
					uri = new URI(uri.toString().replace("webcal", "http"), false);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
			GetMethod method = new GetMethod();
			try {
				method.setURI(uri);
				
				PageParameters parameters = new PageParameters();
				parameters.add("url", uri.toString());
				setResponsePage(UrlValidationPage.class, parameters);
			} catch (Exception e) {
				error(e.getMessage());
				LOG.info(e.getMessage(), e);
			}
		}
	}
	
	public UrlFormPanel(String id)
	{
		super(id);
		add(new UrlForm("urlForm", null));
	}
	
	public UrlFormPanel(String id, URI url)
	{
		super(id);
		add(new UrlForm("urlForm", url));
	}
}
