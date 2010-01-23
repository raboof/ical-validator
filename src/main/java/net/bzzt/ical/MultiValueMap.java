package net.bzzt.ical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.MarkupContainer;

import net.fortuna.ical4j.util.validation.ValidationResult;

public class MultiValueMap<T, U> implements Serializable {
	private Map<T, List<U>> values = new HashMap<T, List<U>>();

	public void put(T key, U value) {
		getList(key).add(value);
	}

	public Set<T> keySet() {
		return values.keySet();
	}

	public List<U> get(T key) {
		return getList(key);
	}

	private List<U> getList(T key) {
		List<U> list = values.get(key);
		if (list == null)
		{
			list = new ArrayList<U>();
			values.put(key, list);
		}
		return list;
	}
}
