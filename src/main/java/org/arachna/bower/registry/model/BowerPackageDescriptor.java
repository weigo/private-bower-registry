package org.arachna.bower.registry.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.arachna.bower.registry.BowerPackage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A bower package descriptor.
 *
 * @author Dirk Weigenand
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class BowerPackageDescriptor implements BowerPackage {
    private String name;
    private String url;

    public BowerPackageDescriptor() {
    }
    
    public BowerPackageDescriptor(final String name, final String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
