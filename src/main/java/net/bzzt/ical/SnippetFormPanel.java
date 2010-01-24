package net.bzzt.ical;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

public class SnippetFormPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class SnippetForm extends Form<SnippetForm> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String snippet;
		
		public SnippetForm(String id, String snippet, String charset) {
			super(id);
			this.snippet = snippet;
//			this.charset = charset;
			setDefaultModel(new CompoundPropertyModel<SnippetForm>(this));

			add(new TextArea<String>("snippet"));
		}

		/* (non-Javadoc)
		 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		protected void onSubmit() {
			setResponsePage(new SnippetValidationPage(snippet));
		}
	}
	
	public SnippetFormPanel(String id, String snippet, String charset)
	{
		super(id);
		add(new SnippetForm("snippetForm", snippet, charset));
	}
}
