package net.bzzt.ical;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.validation.CollectingValidationResultHandler;
import net.fortuna.ical4j.util.validation.ValidationResult;
import net.fortuna.ical4j.util.validation.ValidationResultHandler;

import org.apache.commons.httpclient.URI;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.upload.FileUpload;
import org.wicketstuff.annotation.mount.MountPath;
import org.wicketstuff.annotation.strategy.MountMixedParam;

/**
 * Homepage
 */
@MountPath(path = "validate")
@MountMixedParam(parameterNames = { "url" })
public class HomePage extends ValidatorLayoutPage {

	public class SnippetForm extends Form<SnippetForm> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String snippet;
		
		public SnippetForm(String id) {
			super(id);
			setDefaultModel(new CompoundPropertyModel<SnippetForm>(this));

			add(new TextArea<String>("snippet"));
		}

		/* (non-Javadoc)
		 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		protected void onSubmit() {
//			URI uri = SnippetForm.this.getModelObject().url;
//
//			PageParameters parameters = new PageParameters();
//			parameters.add("url", uri.toString());
//			setResponsePage(UrlValidationPage.class, parameters);
		}
	}
	
	public class FileForm extends Form<FileForm> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private FileUpload file;
		
		public FileForm(String id) {
			super(id);
			setDefaultModel(new CompoundPropertyModel<FileForm>(this));

			add(new FileUploadField("file"));
		}

		/* (non-Javadoc)
		 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		protected void onSubmit() {
//			URI uri = this.getModelObject().url;
//
//			PageParameters parameters = new PageParameters();
//			parameters.add("url", uri.toString());
//			setResponsePage(UrlValidationPage.class, parameters);
		}
	}

	private static final long serialVersionUID = 1L;

	public static WebMarkupContainer getValidationResult(String id, InputStream responseBody,
			String responseCharSet) throws IOException {

		// We parse in 'relaxed' mode, so we can report as many errors as
		// possible when validating
		CompatibilityHints.setHintEnabled(
				CompatibilityHints.KEY_RELAXED_PARSING, true);

		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar;
		try {
			calendar = builder.build(responseBody);
		} catch (ParserException e1) {
			Session.get().error("Error on line " + e1.getLineNo() + ": " + e1.getError());
			return new WebMarkupContainer(id);
		}

		CompatibilityHints.setHintEnabled(
				CompatibilityHints.KEY_RELAXED_PARSING, false);

		CollectingValidationResultHandler validationResultHandler = new CollectingValidationResultHandler();
		ValidationResultHandler.set(validationResultHandler);

		try {
			calendar.validate(true);
		} catch (ValidationException e) {
			throw new IllegalStateException("Iets mis met de handler?"
					+ e.getMessage(), e);
		}

		List<ValidationResult> results = (List<ValidationResult>) validationResultHandler
				.getResults();

		return(new ValidationResultOverviewPanel(id, results));

	}

	// TODO Add any page properties or variables here

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
	public HomePage(final PageParameters parameters) {
		add(new UrlFormPanel("urlForm"));
		add(new FileForm("fileForm"));
		add(new SnippetForm("snippetForm"));
		add(new WebMarkupContainer("results"));
	}

}
