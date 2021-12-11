import { Component } from 'react';
import { connect } from 'react-redux';
import './App.css';
import { ServicesTable } from "./ServicesTable";
import { FilterContainer } from "./filter";

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      date: new Date().toString()
    }
  }

  componentDidMount() {
    setInterval(() => {
      this.setState({
        date: new Date().toString()
      })
    }, 60000);

    this.props.onLoad();
  }

  add(service) {
    document.getElementById('main-form').reset();
    this.props.onAdd(service);
  }
  
  render() {
    const f = this.props.filter;
    const services = this.props.services.filter(service => service.name.toLowerCase().indexOf(f) > -1);
    return (
      <div>
        <h1>Monitoring Dashboard</h1>
        <div>
          <FilterContainer />
        </div>
        <ServicesTable 
          services={services}
          border={1}
          onSubmit={service => {
            this.add(service);
          }}
          onDelete={id => {
            this.props.onDelete(id);
          }}
        />
        <div>{this.state.date}</div>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  services: state.services,
  filter: state.filter
});
const mapDispatchToProps = dispatch => ({
  onLoad: () => dispatch({ type: 'SERVICES_LOAD' }),
  onAdd: service => dispatch({ type: 'SERVICES_ADD', service }),
  onDelete: id => dispatch({type: 'SERVICES_REMOVE', id})
});

const AppContainer = connect(mapStateToProps, mapDispatchToProps)(App);

export default AppContainer;