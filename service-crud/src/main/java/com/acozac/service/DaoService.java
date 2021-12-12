package com.acozac.service;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;

@ApplicationScoped
@Transactional(REQUIRED)
public class DaoService
{
    @Transactional(SUPPORTS)
    public List<Service> getAllServices()
    {
        return Service.listAll();
    }

    @Transactional(SUPPORTS)
    public Service getById(Long id)
    {
        return Service.findById(id);
    }

    public Service persist(@Valid Service service)
    {
        service.persist();
        return service;
    }

    public Service update(@Valid Service service)
    {
        Service entity = Service.findById(service.id);
        entity.name = service.name;
        entity.url = service.url;
        entity.status = service.status;
        return entity;
    }

    public void delete(Long id)
    {
        Service.deleteById(id);
    }
}
