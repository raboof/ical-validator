package net.bzzt.ical;

import java.util.List;

import net.fortuna.ical4j.util.validation.ValidationResult;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

public class ValidationResultListView extends ListView<ValidationResult> {

	public ValidationResultListView(String id,
			List<? extends ValidationResult> list) {
		super(id, list);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void populateItem(ListItem<ValidationResult> item) {
		item.add(new Label("message", item.getModelObject().getText()));
	}

}
