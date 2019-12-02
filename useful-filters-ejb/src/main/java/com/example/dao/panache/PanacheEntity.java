package com.example.dao.panache;

import com.example.dao.panache.transients.Link;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.example.serializer.GsonModel;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Represents an entity with a generated ID field {@link #id} of type {@link Long}. If your
 * Hibernate entities extend this class they gain the ID field and auto-generated accessors
 * to all their public fields (unless annotated with {@link Transient}), as well as all
 * the useful methods from {@link PanacheEntityBase}.
 * </p>
 * <p>
 * If you want a custom ID type or strategy, you can directly extend {@link PanacheEntityBase}
 * instead, and write your own ID field. You will still get auto-generated accessors and
 * all the useful methods.
 * </p>
 *
 * @author Stéphane Épardaud
 * @author mahdi Sharifi
 * @see PanacheEntityBase
 */
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Data
public abstract class PanacheEntity<IDType> extends GsonModel {

    public abstract IDType getId();

    @Column(name = "CREATE_AT")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createAt;

    @Transient
    @XmlElement(name = "links")
    private List<Link> links;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "<" + getId() + ">";
    }

    @PrePersist
    private void prepareToPersist() {
        setCreateAt(new Date());
    }

    public void addLink(String url, String rel) {
        if (links == null)
            links = new ArrayList<>();
        Link link = new Link();
        link.setHref(url);
        link.setRel(rel);
        links.add(link);
    }
    public void addLink(String url, String rel,String method) {
        if (links == null)
            links = new ArrayList<>();
        Link link = new Link();
        link.setHref(url);
        link.setRel(rel);
        link.setMethod(method);
        links.add(link);
    }
}
