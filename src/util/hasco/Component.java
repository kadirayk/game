
package util.hasco;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "requiredInterface",
    "providedInterface",
    "parameter",
    "dependencies"
})
public class Component {

    @JsonProperty("name")
    private String name;
    @JsonProperty("requiredInterface")
    private List<RequiredInterface> requiredInterface = null;
    @JsonProperty("providedInterface")
    private List<String> providedInterface = null;
    @JsonProperty("parameter")
    private List<Parameter> parameter = null;
    @JsonProperty("dependencies")
    private List<Dependency> dependencies = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("requiredInterface")
    public List<RequiredInterface> getRequiredInterface() {
        return requiredInterface;
    }

    @JsonProperty("requiredInterface")
    public void setRequiredInterface(List<RequiredInterface> requiredInterface) {
        this.requiredInterface = requiredInterface;
    }

    @JsonProperty("providedInterface")
    public List<String> getProvidedInterface() {
        return providedInterface;
    }

    @JsonProperty("providedInterface")
    public void setProvidedInterface(List<String> providedInterface) {
        this.providedInterface = providedInterface;
    }

    @JsonProperty("parameter")
    public List<Parameter> getParameter() {
        return parameter;
    }

    @JsonProperty("parameter")
    public void setParameter(List<Parameter> parameter) {
        this.parameter = parameter;
    }

    @JsonProperty("dependencies")
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    @JsonProperty("dependencies")
    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
