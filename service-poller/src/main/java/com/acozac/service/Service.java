package com.acozac.service;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.URL;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Service extends PanacheEntity
{
    @NotNull
    @Size(min = 3, max = 50)
    public String name;
    @NotNull
    @URL(protocol = "https")
    public String url;
    public String status;
    @CreationTimestamp
    @Column(name = "creationtime", updatable = false)
    public Timestamp creationTime;

    @Override
    public String toString()
    {
        return "Service{" +
            "id=" + id +
            "name='" + name + '\'' +
            "creationTime='" + creationTime + '\'' +
            ", url='" + url + '\'' +
            ", status=" + status +
            '}';
    }
}
