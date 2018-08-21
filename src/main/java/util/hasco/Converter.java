package util.hasco;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableCell;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;

import com.fasterxml.jackson.databind.ObjectMapper;

import util.FileUtil;

public class Converter {

	// columns of the md file
	private static final int COMPONENT_NAME = 0;
	private static final int PARAMETER_NAME = 1;
	private static final int PARAMETER_DESC = 2;
	private static final int PARAMETER_TYPE = 3;
	private static final int PARAMETER_DEFAULT = 4;
	private static final int PARAMETER_VALUES = 5;
	private static final int PARAMETER_EFFECT = 6;
	private static final int PARAMETER_FILE = 7;
	private static final int IMPORTANCE_RANK = 8;

	private static Map<String, Component> parsedComponents = new HashMap<>();

	private static Map<Parameter, Integer> parameterRanks = new HashMap<>();

	public static HascoDocument parseConf() {
		HascoDocument document = new HascoDocument();
		document.setRepository("GamingAnywhere");
		List<Component> components = new ArrayList<>();
		document.setComponents(components);

		Component component = parseComponent();

		return document;

	}

	private static Component parseComponent() {
		Component component = new Component();
		List<Parameter> parameters = new ArrayList<>();
		component.setParameter(parameters);

		Parameter parameter = parseParameter();

		return component;
	}

	private static Parameter parseParameter() {
		Parameter parameter = new Parameter();

		return parameter;
	}

	public static void parser() {
		List<Extension> extensions = Arrays.asList(TablesExtension.create());
		Parser parser = Parser.builder().extensions(extensions).build();
		Node node = parser.parse(FileUtil.readFile("resources/ga-conf-definitions.md"));

		TableRow row = (TableRow) node.getFirstChild().getLastChild().getFirstChild();
		HascoDocument doc = new HascoDocument();
		List<Component> components = new ArrayList<>();
		doc.setComponents(components);
		while (row != null) {
			TableCell cell = (TableCell) row.getFirstChild();
			String componentName = ((Text) cell.getFirstChild()).getLiteral();
			Component component = null;
			List<Parameter> parameters = null;
			if (!parsedComponents.containsKey(componentName)) {
				component = new Component();
				component.setName(componentName);
				parameters = new ArrayList<>();
				component.setParameter(parameters);
				components.add(component);
				parsedComponents.put(componentName, component);
			} else {
				component = parsedComponents.get(componentName);
				parameters = component.getParameter();
			}
			int cellCounter = 0;
			Parameter param = new Parameter();
			param.setComponentName(componentName);
			while (cell != null) {
				String val = null;
				switch (cellCounter) {
				case PARAMETER_NAME:
					val = ((Text) cell.getFirstChild()).getLiteral();
					param.setName(val);
					break;
				case PARAMETER_TYPE:
					val = ((Text) cell.getFirstChild()).getLiteral();
					param.setType(val);
					break;
				case PARAMETER_DEFAULT:
					val = ((Text) cell.getFirstChild()).getLiteral();
					if (!val.contains("*")) {
						param.setDefault(val);
					}
					break;
				case PARAMETER_VALUES:
					val = ((Text) cell.getFirstChild()).getLiteral();
					parseValues(param, val);
					break;
				case IMPORTANCE_RANK:
					val = ((Text) cell.getFirstChild()).getLiteral();
					param.setImportanceRank(Integer.valueOf(val));
					parameterRanks.put(param, Integer.valueOf(val));
				default:
					break;
				}
				cellCounter++;
				cell = (TableCell) cell.getNext();
			}
			row = (TableRow) row.getNext();
			parameters.add(param);
		}

		HascoDocument newDoc = selectMostImportantParameters();

		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(new File("resources/hascoDocument.json"), newDoc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static HascoDocument selectMostImportantParameters() {
		List<Component> components = new ArrayList<>();
		HascoDocument newDoc = new HascoDocument();
		newDoc.setComponents(components);

		Map<String, Component> addedComponents = new HashMap<>();

		Map<Parameter, Integer> sortedMap = parameterRanks.entrySet().stream().sorted(Collections.reverseOrder(Entry.comparingByValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		int i = 0;
		for (Parameter p : sortedMap.keySet()) {
			if (i == 10) { // get top 10
				break;
			}
			String componentName = p.getComponentName();
			Component component = null;
			if (!addedComponents.containsKey(componentName)) {
				component = new Component();
				component.setName(componentName);
				components.add(component);
				List<Parameter> parameters = new ArrayList<>();
				component.setParameter(parameters);
				addedComponents.put(componentName, component);
			} else {
				component = addedComponents.get(componentName);
			}
			component.getParameter().add(p);
			i++;
		}
		return newDoc;
	}

	private static void parseValues(Parameter param, String text) {
		List<String> values = new ArrayList<>();
		String[] rawValues = text.split(",");

		for (String s : rawValues) {
			if (s.contains("min")) {
				String min = s.split("=")[1];
				param.setMin(Double.parseDouble(min));
			} else if (s.contains("max")) {
				String max = s.split("=")[1];
				param.setMax(Double.parseDouble(max));
			} else if (s.contains("*")) {
				// don't add
			} else {
				values.add(s);
			}
		}
		param.setValues(values);
	}

}
