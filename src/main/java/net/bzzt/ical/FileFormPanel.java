package net.bzzt.ical;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

public class FileFormPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
			setResponsePage(new FileValidationPage(file));
		}
	}
	
	public FileFormPanel(String id)
	{
		super(id);
		add(new FileForm("fileForm"));
	}
}
