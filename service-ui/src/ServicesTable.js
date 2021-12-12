function getValues() {
    const name = document.querySelector("input[name=name]").value;
    const url = document.querySelector("input[name=url]").value;
   // const status = document.querySelector("input[name=status]").value;
   // const creationTime = document.querySelector("input[name=creationTime]").value;
    
    const services = {
      name,
      url,
 //     status,
    //  creationTime
    };
    return services;
  }
  
  export const ServicesTable = ({ services, border, onSubmit, onDelete }) => (
    <form id="main-form" onSubmit={e => {
      e.preventDefault();
      const values = getValues();
      onSubmit(values);
    }}>
      <table border={border}>
        <thead>
          <tr>
            <th>Name</th>
            <th>URL</th>
            <th>Status</th>
            <th>Creation Time</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {services.map((service, index) => (
            <tr key={index}>
              <td>{service.name}</td>
              <td><a target="_blank" href={service.url}>{service.url}</a></td>
              <td>{service.status}</td>
              <td>{service.creationTime}</td>
              <td>
                <a href="#" className="delete-row" onClick={e => {
                  onDelete(service.id);
                }}>&#10006;</a>
              </td>
            </tr>
          ))}
        </tbody>
        <tfoot>
          <tr>
            <td><input type="text" required name="name" placeholder="enter Service name" /></td>
            <td><input type="text" required name="url" placeholder="enter Service URL" /></td>
            <td><button type="submit">Save</button></td>
          </tr>
        </tfoot>
      </table>
    </form>
  );