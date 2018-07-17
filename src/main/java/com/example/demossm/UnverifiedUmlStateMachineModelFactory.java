package com.example.demossm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.uml2.uml.Model;

import org.springframework.core.io.Resource;
import org.springframework.statemachine.config.model.AbstractStateMachineModelFactory;
import org.springframework.statemachine.config.model.ConfigurationData;
import org.springframework.statemachine.config.model.DefaultStateMachineModel;
import org.springframework.statemachine.config.model.StateMachineModel;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.uml.support.UmlModelParser;
import org.springframework.statemachine.uml.support.UmlModelParser.DataHolder;
import org.springframework.statemachine.uml.support.UmlUtils;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

/**
 * UML model factory that produces StateMachineModel with non-null
 * ConfigurationData. That is to be used with ObjectStateMachineFactory
 */
public class UnverifiedUmlStateMachineModelFactory extends AbstractStateMachineModelFactory<String, String>
        implements StateMachineModelFactory<String, String> {

    private Resource resource;
    private String location;

    /**
     * Instantiates a new uml state machine model factory.
     *
     * @param resource the resource
     */
    public UnverifiedUmlStateMachineModelFactory(Resource resource) {
        Assert.notNull(resource, "Resource must be set");
        this.resource = resource;
    }

    /**
     * Instantiates a new uml state machine model factory.
     *
     * @param location the resource location
     */
    public UnverifiedUmlStateMachineModelFactory(String location) {
        Assert.notNull(location, "Location must be set");
        this.location = location;
    }

    @Override
    public StateMachineModel<String, String> build() {
        Model model = null;
        try {
            model = UmlUtils.getModel(getResourceUri(resolveResource()).getPath());
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot build build model from resource " + resource + " or location " + location, e);
        }
        UmlModelParser parser = new UmlModelParser(model, this);
        DataHolder dataHolder = parser.parseModel();
        return new DefaultStateMachineModel<String, String>(new ConfigurationData<String, String>(), dataHolder.getStatesData(), dataHolder.getTransitionsData());
    }

    private Resource resolveResource() {
        if (resource != null) {
            return resource;
        } else {
            return getResourceLoader().getResource(location);
        }
    }

    private URI getResourceUri(Resource resource) throws IOException {
        // try to see if resource is an actual File and eclipse
        // libs cannot use input stream. thus creating a tmp file with
        // needed .uml prefix and getting URI from there.
        try {
            return resource.getFile().toURI();
        } catch (Exception e) {
        }
        Path tempFile = Files.createTempFile(null, ".uml");
        FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(tempFile.toFile()));
        return tempFile.toUri();
    }
}
