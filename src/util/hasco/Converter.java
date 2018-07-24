package util.hasco;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private static Map<String, Component> parsedComponents = new HashMap<>();

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
					if(!val.contains("*")){
						param.setDefault(val);
					}
					break;
				case PARAMETER_VALUES:
					val = ((Text) cell.getFirstChild()).getLiteral();
					parseValues(param, val);
					break;
				default:
					break;
				}
				cellCounter++;
				cell = (TableCell) cell.getNext();
			}
			row = (TableRow) row.getNext();
			parameters.add(param);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(new File("resources/hascoDocument.json"), doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
			} else if (s.contains("*")){
				// don't add
			} else {
				values.add(s);
			}
		}
		param.setValues(values);
	}

}
