package net.bzzt.ical;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.fortuna.ical4j.util.validation.ValidationResult;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class ValidationResultPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationResultPanel(String id, List<ValidationResult> results) {
		super(id);
		
		final MultiValueMap<String, ValidationResult> resultMap = new MultiValueMap<String, ValidationResult>();

		for (ValidationResult result : results)
		{
			resultMap.put(result.component.toString(), new ValidationResult(null, result.message, result.args));
		}
		
		add(new Label("summary", results.size() + " results in " + resultMap.keySet().size() + " components"));
		
		List<ValidationResult> generalFailures = (List<ValidationResult>) resultMap.get(null);
		if (generalFailures.isEmpty())
		{
			add(new Label("message", "None."));
			WebMarkupContainer webMarkupContainer = new WebMarkupContainer("messages");
//			webMarkupContainer.add(new WebMarkupContainer("message"));
			add(webMarkupContainer.setVisible(false));
		}
		else
		{
			add(new WebMarkupContainer("message").setVisible(false));
			add(new ValidationResultListView("messages", generalFailures));
		}
		
		add(new ListView<String>("component", new ArrayList<String>((Set<String>) resultMap.keySet()))
				{

					/**
					 * 
					 */
			private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(
							ListItem<String> item) {
						String key = item.getModelObject();
						if (key != null)
						{
							item.add(new Label("code", key.toString()));
						}
						else
						{
							item.add(new Label("code", ""));
							item.setVisible(false);
						}
						item.add(new ValidationResultListView("messages", (List<? extends ValidationResult>) resultMap.get(key)));
					}
			
				}
		);
//		for (Entry<Component, List<ValidationResult>> entry : )
//		{
//			info(entry.getKey().toString());
//			for (ValidationResult result : entry.getValue())
//			{
//				info(result.getText());
//			}
//		}
		// TODO Auto-generated constructor stub
	}

}
