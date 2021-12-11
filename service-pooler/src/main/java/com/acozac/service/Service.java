package com.acozac.service;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;

@Entity
public class Service extends PanacheEntity
{
    @NotNull
    @Size(min = 3, max = 50)
    public String name;
    @NotNull
    public String url;
    public String status;
    public String creationTime;

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
